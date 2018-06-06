package foryou.core.listener;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import foryou.core.mvc.MvcCore;
import foryou.core.util.StringUtil;

/**
 * Mvc Listener
 * @author 罗林
 *
 */
public class InitMvc implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		System.out.println("Closing service...");
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		String loadType = servletContextEvent.getServletContext().getInitParameter("loadType");
		loadType = StringUtil.isEmpty(loadType) ? "classes":loadType;
		System.out.println("System initialize type " + loadType);
		if(loadType.equals("classes")){
			classesInit();
		}else if(loadType.equals("lib")){
			libInit(servletContextEvent.getServletContext().getInitParameter("containJarNames"));
		}else{
			System.err.println("Load type is only supported classes or lib,please check web.xml param-name loadType");
		}
		
	}
	
	/**
	 * 普通源码controller初始化
	 */
	private void classesInit(){
		File root = new File(this.getClass().getClassLoader().getResource("/").getPath());
		System.out.println("Init Foryou Mvc Classes Controller ....");
		try {
			MvcCore.initMvc(root, "", MvcCore.getProperties("controller-scan-package").split(","));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * lib包中的jar源码进行初始化
	 * @param containJarNames
	 */
	private void libInit(String containJarNames){
		System.out.println("Init Foryou Mvc Controller Contain JarNames["+containJarNames+"]....");
		try {
			List<String> jarPathList = getJarPaths(new File(Thread.currentThread().getContextClassLoader().getResource("/").getPath()).getParentFile(),containJarNames.split(","),new ArrayList<String>());
			MvcCore.initMvc(jarPathList, MvcCore.getProperties("controller-scan-package").split(","));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取目录中包含了indexJarNames的jar包的真实路径
	 * @param folder
	 * @param indexJarNames
	 * @param jarPathList
	 * @return
	 */
	private List<String> getJarPaths(File folder,String[] indexJarNames,List<String> jarPathList) {
		for(File file:folder.listFiles()){
			if (file.isDirectory()) {
				getJarPaths(file, indexJarNames, jarPathList);
			}
			if (!file.getPath().endsWith(".jar")) {
				continue;
			}
			for(String jarName:indexJarNames){
				if(file.getName().contains(jarName)){
					jarPathList.add(file.getPath());
					continue;
				}
			}
		}
		return jarPathList;
	}

}
