package foryou.core.mvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import foryou.core.annotation.ClassMapingUrl;
import foryou.core.annotation.IgnoreField;
import foryou.core.annotation.InterceptorByClass;
import foryou.core.annotation.SynchronizedLock;
import foryou.core.base.BaseController;
import foryou.core.entity.ControllerMethod;
import foryou.core.entity.ControllerPrototype;

/**
 * fouryou框架核心类
 * 
 * @author luolin
 *
 */
public class MvcCore {

	public static String SCAN_PACKAGE_KEY = "controller-scan-package";
	public static String CONTROLLER_PATTERN_KEY = "controller-url-pattern";
	public static String CONTROLLER_INTERCEPTOR_KEY = "controller-default-interceptor";
	public static String CONTROLLER_PATTERN_VALUE = getMvcProperties(CONTROLLER_PATTERN_KEY);
	public static String CONTROLLER_INTERCEPTOR_VALUE = getMvcProperties(CONTROLLER_INTERCEPTOR_KEY);
	public static String GSON_SERIALIZE_NULL_KEY = "gson-serializeNulls";
	public static String GSON_SERIALIZE_NULL_KEY_VALUE = getMvcProperties(GSON_SERIALIZE_NULL_KEY);

	public static Map<String, ControllerPrototype> controllerPrototypeMap = new ConcurrentHashMap<String, ControllerPrototype>();
	public static Map<String, Object> ControllerFieldRefMap = new ConcurrentHashMap<String, Object>();

	public static void initMvc(File folder, String packageName, String[] scanPackages) {
		File[] files = folder.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				initMvc(file, packageName + fileName + ".", scanPackages);
			}
			for (String scanPackage : scanPackages) {
				boolean isAsterisk = scanPackage.length() > 0 && ("*".equals(scanPackage.substring(scanPackage.length() - 1, scanPackage.length())) || "*".equals(scanPackage.substring(0, 1)));
				if (isAsterisk) {
					scanPackage = scanPackage.replace("*", "");
				}
				if (scanPackage != null && fileName.lastIndexOf(".class") != -1 && packageName.indexOf(scanPackage) != -1) {
					String controllerName = fileName.replace(".class", "");
					String classPath = packageName + controllerName;
					System.out.println("Init [" + classPath + "]");
					Class<?> targetClass = null;
					try {
						targetClass = Class.forName(classPath);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						System.err.println("The ClassPath[" + classPath + "] Not Found...");
						continue;
					}
					try {
						putControllerPrototypeMap(controllerName, classPath, targetClass);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
			}
		}
	}

	/**
	 * 获取Controller的原型信息，缓存到Map中
	 * 
	 * @param controllerName 控制器名称
	 * @param classPath 类路径
	 * @param targetClass targetClass
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private static void putControllerPrototypeMap(String controllerName, String classPath, Class<?> targetClass) throws InstantiationException, IllegalAccessException {
		if (!targetClass.isAnnotationPresent(ClassMapingUrl.class)) {
			return;
		}
		if (controllerPrototypeMap.containsKey(controllerName)) {
			System.err.println("Create Controller Is Error:[the class name:" + controllerName + " already...]");
			return;
		}
		ControllerPrototype controllerPrototype = new ControllerPrototype();
		BaseController controller = (BaseController)targetClass.newInstance();
		controllerPrototype.setController(controller);
		System.out.println("Create [" + classPath + "].Controller ...");
		StringBuilder fieldNames = new StringBuilder("[");
		controllerPrototype.setFieldMap(getControllerFieldMap(new ConcurrentHashMap<String, Field>(), targetClass, new ArrayList<String>(), fieldNames));
		System.out.println("Create Controller Field Import" + fieldNames.toString() + "]...");
		Method[] methods = targetClass.getMethods();
		Map<String, ControllerMethod> methodMap = new ConcurrentHashMap<String, ControllerMethod>();
		for (Method method : methods) {
			ControllerMethod controllerMethod = new ControllerMethod();
			controllerMethod.setMethod(method);
			controllerMethod.setMethodSynchronized(method.isAnnotationPresent(SynchronizedLock.class));
			controllerMethod.setParameterTypeMap(getMethodParamterTypeMap(method));
			methodMap.put(method.getName(), controllerMethod);
		}
		controllerPrototype.setMethodMap(methodMap);
		if (!targetClass.isAnnotationPresent(InterceptorByClass.class)) {
			controllerPrototypeMap.put(controllerName, controllerPrototype);
			return;
		}
		InterceptorByClass interceptorByClass = (InterceptorByClass) targetClass.getAnnotation(InterceptorByClass.class);
		String interceptorClassPaths = interceptorByClass.classPaths();
		if ("config-file:mvc.properties".equals(interceptorClassPaths)) {
			System.out.println("Create Controller Interceptor ClassPaths[" + CONTROLLER_INTERCEPTOR_VALUE + "]...");
			controllerPrototype.setInterceptorClassPath(CONTROLLER_INTERCEPTOR_VALUE);
		} else if ("*".equals(interceptorClassPaths)) {
			// TODO 所有拦截器
		} else {
			System.out.println("Create Controller Interceptor ClassPaths[" + interceptorClassPaths + "]...");
			controllerPrototype.setInterceptorClassPath(interceptorClassPaths);
		}
		controllerPrototypeMap.put(controllerName, controllerPrototype);
	}

	/**
	 * 获取控制器中的所有属性
	 * 
	 * @param fieldMap 属性集合
	 * @param targetClass targetClass
	 * @param rootFieldNameList 根属性list
	 * @param fieldNames 属性名称
	 * @return 集合
	 */
	private static Map<String, Field> getControllerFieldMap(Map<String, Field> fieldMap, Class<?> targetClass, List<String> rootFieldNameList, StringBuilder fieldNames) {
		List<Field> fields = concat(targetClass.getFields(), targetClass.getDeclaredFields());
		String ignoreFieldNames = "";
		if (targetClass.isAnnotationPresent(IgnoreField.class)) {
			IgnoreField ignoreFields = targetClass.getAnnotation(IgnoreField.class);
			ignoreFieldNames = ignoreFields.fieldNames();
		}
		for (Field field : fields) {
			if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(IgnoreField.class) || ignoreFieldNames.contains(field.getName())) {
				continue;
			}
			if (field.getType().getClassLoader() != null) {
				rootFieldNameList.add(field.getName());
				getControllerFieldMap(fieldMap, field.getType(), rootFieldNameList, fieldNames);
			}
			if (rootFieldNameList.size() < 1) {
				fieldMap.put(field.getName(), field);
				fieldNames.append(field.getName() + ",");
			} else {
				String rootFieldName = "";
				for (String fieldName : rootFieldNameList) {
					rootFieldName += fieldName + ".";
				}
				fieldMap.put(rootFieldName + field.getName(), field);
				fieldNames.append(rootFieldName + field.getName() + ",");
			}
		}
		rootFieldNameList.clear();
		return fieldMap;
	}
	
	public static Map<String, Class<?>> getMethodParamterTypeMap(Method method) {
		Map<String, Class<?>> paramterTypeMap = new LinkedHashMap<String, Class<?>>();
		LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] paramterNames = localVariableTableParameterNameDiscoverer.getParameterNames(method);
		Class<?>[] paramterTypes = method.getParameterTypes();
		if(paramterNames == null || paramterTypes == null || paramterNames.length < 1 || paramterTypes.length < 1) {
			return paramterTypeMap;
		}
		for(int i = 0;i<paramterNames.length;i++){
			paramterTypeMap.put(paramterNames[i], paramterTypes[i]);
		}
		return paramterTypeMap;
	}

	/**
	 * 数组链接并去重操作
	 * 
	 * @param first 第一个数组
	 * @param second 第二个数组
	 * @return 数组
 	 */
	private static <T> List<T> concat(T[] first, T[] second) {
		List<T> list = new ArrayList<T>(first.length + second.length);
		list.addAll(Arrays.asList(first));
		list.addAll(Arrays.asList(second));
		Set<T> h = new HashSet<T>(list);
		list.clear();
		list.addAll(h);
		return list;
	}

	/**
	 * 获取mvc.properties的配置并存入MvcPropertiesMap中
	 * @param key 键
	 * @return String
	 */
	public static String getMvcProperties(String key) {
		String value = null;
		InputStreamReader inputStreamReader = null;
		String str = null;
		try {
			InputStream is = MvcCore.class.getResourceAsStream("/mvc.properties");
			// 获取并返回默认配置
			if (is == null) {
				return getMvcDefaultProperties(key);
			}
			inputStreamReader = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			while ((str = reader.readLine()) != null) {
				String[] splitParam = str.split(":");
				if (key.equals(splitParam[0])) {
					value = splitParam[1];
					break;
				}
			}
			reader.close();
			inputStreamReader.close();
		} catch (Exception e) {
			System.err.println("Error: There was an error loading the configuration file:[mvc.properties],please check the file");
			e.printStackTrace();
		}
		value = value == null ? getMvcDefaultProperties(key) : value;
		if (value == null) {
			System.err.println("Error: There configuration file[mvc.properties,mvc.default.properties] not found key:" + key);
		}
		return value;
	}

	/**
	 * 获取mvc.default.properties的默认配置并存入MvcPropertiesMap中
	 * @param key 键
	 * @return 配置信息
	 */
	public static String getMvcDefaultProperties(String key) {
		String value = null;
		InputStreamReader inputStreamReader = null;
		String str = null;
		try {
			InputStream is = MvcCore.class.getResourceAsStream("/mvc.default.properties");
			inputStreamReader = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			while ((str = reader.readLine()) != null) {
				String[] splitParam = str.split(":");
				if (key.equals(splitParam[0])) {
					value = splitParam[1];
					break;
				}
			}
			reader.close();
			inputStreamReader.close();
		} catch (Exception e) {
			System.err.println("Error: There was an error loading the configuration file:[mvc.properties],please check the file");
			e.printStackTrace();
		}
		if (value == null) {
			System.err.println("Error: There configuration file[mvc.properties] not found key:" + key);
		}
		return value;
	}

	/**
	 * 
	 * @param uri uri
	 * @param controllerPatternKey controllerPatternKey
	 * @return 字符串数组
	 * @throws Exception 异常
	 */
	public static String[] paraseUrl(String uri, String controllerPatternKey) throws Exception {
		CONTROLLER_PATTERN_VALUE = CONTROLLER_PATTERN_VALUE == null ? MvcCore.getMvcProperties(controllerPatternKey) : CONTROLLER_PATTERN_VALUE;
		if (uri.indexOf(CONTROLLER_PATTERN_VALUE) == -1) {
			System.err.println("the Request " + uri + " Is Not Such As " + CONTROLLER_PATTERN_VALUE + "Controller/Method...");
			throw new Exception("the Request " + uri + " Is Not Such As " + CONTROLLER_PATTERN_VALUE + "Controller/Method...");
		}
		uri = uri.split(CONTROLLER_PATTERN_VALUE)[1].replace("!", "/").replace(".php", "");
		String[] ret = { "", "" };
		for (int i = 0; i < uri.split("/").length; i++) {
			ret[i] = uri.split("/")[i];
		}
		return ret;
	}

	/**
	 * Controller控制器的Java反射方法调用
	 * @param obAction obAction
	 * @param controllerMethod controllerMethod
	 * @param httpRequestMap httpRequestMap
	 * @return 结果
	 * @throws IllegalAccessException IllegalAccessException
	 * @throws NoSuchMethodException NoSuchMethodException
	 * @throws SecurityException SecurityException
	 * @throws IllegalArgumentException IllegalArgumentException
	 */
	public static String invokeMethod(Object obAction, ControllerMethod controllerMethod, Map<String, String[]> httpRequestMap) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException {
		BaseController controller = (BaseController) obAction;
		try {
			if (controllerMethod.getMethodSynchronized()) {
				synchronized (BaseController.class) {
					return controllerMethod.getParameterTypeMap().size() < 1 ? (String) controllerMethod.getMethod().invoke(controller):(String) controllerMethod.getMethod().invoke(controller, getMethodInvokeParameters(httpRequestMap, controllerMethod.getParameterTypeMap()));
				}
			}
			return controllerMethod.getParameterTypeMap().size() < 1 ? (String) controllerMethod.getMethod().invoke(controller):(String) controllerMethod.getMethod().invoke(controller, getMethodInvokeParameters(httpRequestMap, controllerMethod.getParameterTypeMap()));
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
			return getExceptionAllinformation(e);
		} catch (Exception e) {
			e.printStackTrace();
			return getExceptionAllinformation(e);
		}
	}

	/**
	 * 获取反射调用方法需要传递的参数
	 * 
	 * @param paramterMap paramterMap
	 * @param parameterTypeMap parameterTypeMap
	 * @return 数组
	 * @throws Exception Exception
	 */
	public static Object[] getMethodInvokeParameters(Map<String, String[]> paramterMap, Map<String, Class<?>> parameterTypeMap) throws Exception {
		if (parameterTypeMap.size() < 1) {
			return null;
		}
		Object[] objects = new Object[parameterTypeMap.size()];
		Integer i = 0;
		Iterator<Map.Entry<String, Class<?>>> entryIterator = parameterTypeMap.entrySet().iterator();
		while (entryIterator.hasNext()) {
			Map.Entry<String, Class<?>> parameterType = entryIterator.next();
			System.out.println("The Method Paramter Key:" + parameterType.getKey() + " Type:" + parameterType.getValue());
			if (isBaseDataType(parameterType.getValue())) {
				// 基础数据类
				objects[i++] = paramterMap.get(parameterType.getKey()) == null ? getDefaultValue(parameterType.getValue()) : convertParameter(paramterMap.get(parameterType.getKey()), parameterType.getValue());
				continue;
			}
			Object obj = parameterType.getValue().newInstance();
			List<Field> fields = concat(parameterType.getValue().getFields(), parameterType.getValue().getDeclaredFields());
			for (Field field : fields) {
				if (paramterMap.get(parameterType.getKey() + "." + field.getName()) == null) {
					continue;
				}
				field.setAccessible(true);
				System.out.println(parameterType.getKey() + "." + field.getName() + "...");
				System.out.println(paramterMap.get(parameterType.getKey() + "." + field.getName()));
				field.set(obj, convertParameter(paramterMap.get(parameterType.getKey() + "." + field.getName()), field.getType()));
			}
			objects[i++] = obj;
		}
		return objects;
	}

	/**
	 * 注入方法参数值得类型转换
	 * 
	 * @param parameterType parameterType
	 * @param parameterValue parameterValue
	 * @return return
	 * @throws ParseException ParseException
	 */
	private static Object convertParameter(String[] requestParamters, Class<?> fieldClazz) throws ParseException {
		if (requestParamters == null || requestParamters[0] == null || "".equals(requestParamters[0])) {
			return null;
		}
		if (fieldClazz.toString().equals("class [Ljava.lang.String")) {
			return requestParamters;
		} else if (fieldClazz.toString().equals("class java.lang.String")) {
			return requestParamters[0];
		} else if (fieldClazz.toString().equals("class java.lang.Integer") || fieldClazz.toString().equals("int")) {
			return Integer.parseInt(requestParamters[0]);
		} else if (fieldClazz.toString().equals("class java.lang.Float") || fieldClazz.toString().equals("float")) {
			return Float.parseFloat(requestParamters[0]);
		} else if (fieldClazz.toString().equals("class java.lang.Double") || fieldClazz.toString().equals("double")) {
			return Double.parseDouble(requestParamters[0]);
		} else if (fieldClazz.toString().equals("class java.lang.Boolean") || fieldClazz.toString().equals("boolean")) {
			return Boolean.parseBoolean(requestParamters[0]);
		} else if (fieldClazz.toString().equals("class java.lang.Long") || fieldClazz.toString().equals("long")) {
			return Long.parseLong(requestParamters[0]);
		} else if (fieldClazz.toString().equals("class java.lang.Character") || fieldClazz.toString().equals("char")) {
			return requestParamters[0].charAt(0);
		} else if (fieldClazz.toString().equals("class java.util.Date") && requestParamters[0].length() == 10) {
			return new SimpleDateFormat("yyyy-MM-dd").parse(requestParamters[0]);
		} else if (fieldClazz.toString().equals("class java.util.Date") && requestParamters[0].length() == 16) {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(requestParamters[0]);
		} else if (fieldClazz.toString().equals("class java.util.Date") && requestParamters[0].length() == 19) {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(requestParamters[0]);
		}
		return null;
	}

	/**
	 * 判断是否为基础类型数据
	 * 
	 * @param clazz clazz
	 * @return return
	 * @throws Exception Exception
	 */
	private static boolean isBaseDataType(Class<?> clazz) throws Exception {
		return (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class) || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(BigDecimal.class) || clazz.equals(BigInteger.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class) || clazz.isPrimitive());
	}

	/**
	 * 获取基础数据类型默认值
	 * 
	 * @param clazz clazz
	 * @return return
	 */
	private static Object getDefaultValue(Class<?> clazz) {
		switch (clazz.toString()) {
		case "int":
			return 0;
		case "float":
			return 0.0f;
		case "double":
			return 0.0d;
		case "boolean":
			return false;
		case "long":
			return 0L;
		case "char":
			return 0;
		case "byte":
			return (byte) 0;
		case "short":
			return (short) 0;
		default:
			return null;
		}
	}

	public static String getExceptionAllinformation(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	/**
	 * 控制器的属性注入，不依赖get/set,但需要使用公共属性public
	 * @param controller controller
	 * @param fieldMap fieldMap
	 * @param fieldValueMap fieldValueMap
	 * @throws Exception 异常
	 */
	public static void controllerFieldInject(Object controller, Map<String, Field> fieldMap, Map<String, String[]> fieldValueMap) throws Exception {
		String fieldNotFind = "";
		for (Map.Entry<String, String[]> entry : fieldValueMap.entrySet()) {
			if (!fieldMap.containsKey(entry.getKey())) {
				fieldNotFind += entry.getKey() + "|";
				continue;
			}
			fieldInject(controller, entry.getKey().indexOf(".") == -1 ? null : fieldMap.get(entry.getKey().split("\\.")[0]), fieldMap.get(entry.getKey()), entry.getValue());
		}
		if (!"".equals(fieldNotFind)) {
			System.out.println("The Controller Field[" + fieldNotFind + "] Not Find");
		}
		MvcCore.ControllerFieldRefMap.clear();
	}

	/**
	 * 属性注入 getField是可以获取到父类的共有字段的， getDeclaredField只能获取本类所有字段
	 * @param controller controller
	 * @param rootField rootField
	 * @param subField subField
	 * @param fieldValues fieldValues
	 * @throws InstantiationException InstantiationException
	 * @throws IllegalAccessException IllegalAccessException
	 * @throws IllegalArgumentException IllegalArgumentException
	 * @throws ParseException ParseException
	 */
	public static void fieldInject(Object controller, Field rootField, Field subField, String[] fieldValues) throws InstantiationException, IllegalAccessException, IllegalArgumentException, ParseException {
		subField.setAccessible(true);
		if (fieldValues == null) {
			return;
		} else if (rootField != null) {
			Object rootFieldObject = null;
			if (MvcCore.ControllerFieldRefMap.containsKey(rootField.getName())) {
				rootFieldObject = MvcCore.ControllerFieldRefMap.get(rootField.getName());
			} else {
				rootFieldObject = rootField.getType().newInstance();
				MvcCore.ControllerFieldRefMap.put(rootField.getName(), rootFieldObject);
			}
			fieldInject(rootFieldObject, null, subField, fieldValues);
			rootField.setAccessible(true);
			rootField.set(controller, rootFieldObject);
		} else if (subField.getGenericType().toString().indexOf("[L") != -1 && (subField.getGenericType().toString().indexOf("java.lang.String") != -1 || subField.getGenericType().toString().indexOf("java.lang.Integer") != -1)) {
			subField.set(controller, fieldValues);
		} else if (subField.getGenericType().toString().equals("class java.lang.String")) {
			subField.set(controller, fieldValues[0]);
		} else if (subField.getGenericType().toString().equals("class java.lang.Integer") || subField.getGenericType().toString().equals("int")) {
			subField.set(controller, Integer.parseInt(fieldValues[0]));
		} else if (subField.getGenericType().toString().equals("class java.lang.Float") || subField.getGenericType().toString().equals("float")) {
			subField.set(controller, Float.parseFloat(fieldValues[0]));
		} else if (subField.getGenericType().toString().equals("class java.lang.Double") || subField.getGenericType().toString().equals("double")) {
			subField.set(controller, Double.parseDouble(fieldValues[0]));
		} else if (subField.getGenericType().toString().equals("class java.lang.Boolean") || subField.getGenericType().toString().equals("boolean")) {
			subField.set(controller, Boolean.parseBoolean(fieldValues[0]));
		} else if (subField.getGenericType().toString().equals("class java.lang.Long") || subField.getGenericType().toString().equals("long")) {
			subField.set(controller, Long.parseLong(fieldValues[0]));
		} else if (subField.getGenericType().toString().equals("class java.lang.Character") || subField.getGenericType().toString().equals("char")) {
			subField.set(controller, fieldValues[0].charAt(0));
		} else if (subField.getGenericType().toString().equals("class java.util.Date") && fieldValues[0].length() == 10) {
			subField.set(controller, new SimpleDateFormat("yyyy-MM-dd").parse(fieldValues[0]));
		} else if (subField.getGenericType().toString().equals("class java.util.Date") && fieldValues[0].length() == 16) {
			subField.set(controller, new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(fieldValues[0]));
		} else if (subField.getGenericType().toString().equals("class java.util.Date") && fieldValues[0].length() == 19) {
			subField.set(controller, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(fieldValues[0]));
		}
	}

	/**
	 * 前端控制器(RequestDispatcherFilter)对控制器进行初始化操作
	 * @param controllerPrototype controllerPrototype
	 * @param request request
	 * @param response response
	 * @return controller
	 * @throws Exception Exception
	 */
	@SuppressWarnings("unchecked")
	public static BaseController initController(ControllerPrototype controllerPrototype, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String ip = request.getHeader("X-Real-IP");
		ip = (ip == null || ip.equals("")) ? request.getRemoteAddr() : ip;
		BaseController controller = controllerPrototype.getController();
		controller.requestThreadLocal.set(request);
		controller.responseThreadLocal.set(response);
		controller.sessionThreadLocal.set(request.getSession());
		controller.servletContextThreadLocal.set(request.getSession().getServletContext());
		controller.ipThreadLocal.set(ip);
		MvcCore.controllerFieldInject(controller, controllerPrototype.getFieldMap(), request.getParameterMap());
		return controller;
	}

	/**
	 * response请求后返回结果处理
	 * @param result result
	 * @param baseController baseController
	 * @param request request
	 * @param response response
	 * @throws IOException IOException
	 * @throws ServletException ServletException
	 */
	public static void resultProcess(String result, BaseController baseController, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		GSON_SERIALIZE_NULL_KEY_VALUE = GSON_SERIALIZE_NULL_KEY_VALUE == null ? MvcCore.getMvcProperties(GSON_SERIALIZE_NULL_KEY) : GSON_SERIALIZE_NULL_KEY_VALUE;
		Gson gson = null;
		if (GSON_SERIALIZE_NULL_KEY_VALUE.equals("null")) {
			gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		} else {
			gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		}
		Boolean isAsyncRequest = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
		if (BaseController.RESULT_AJAX_DATA.equals(result)) {
			response.getWriter().write(gson.toJson(baseController.ajaxDataThreadLocal.get()));
		} else if (BaseController.RESULT_AJAX_BOOLEAN.equals(result)) {
			response.getWriter().write(gson.toJson(baseController.ajaxBooleanThreadLocal.get()));
		} else if (BaseController.RESULT_AJAX_STREAM.equals(result)) {
			response.getOutputStream().write(baseController.ajaxDataThreadLocal.get().getByteData());
		} else if (BaseController.RESULT_REDIRECT.equals(result) && (!isAsyncRequest)) {
			response.sendRedirect(baseController.redirectDataThreadLocal.get().getRedirectUrl());
		} else if (BaseController.RESULT_REDIRECT.equals(result) && isAsyncRequest) {
			response.getWriter().write(gson.toJson(baseController.redirectDataThreadLocal.get()));
		} else {
			response.getWriter().write(result);
		}
	}
}