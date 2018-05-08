# foryou_core 1.8
foryou_core 1.0 通过自定义Java Annotation实现Request请求到Controller的调用，以及使用Java Reflect和递归算法实现Request参数自动注入到Controller属性中，且支持多级引用数据类型

[博客介绍](http://www.alinkeji.com/2017/03/19/RollinMvcCore/)

# 流程
1、  用户发送请求至前端拦截器RequestDispatcherFilter。

2、  RequestDispatcherFilter收到请求调用MvcCore.paraseUrl解析Url。

3、 RequestDispatcherFilter对解析后的Url查找ControllerUrlMap（初始化时扫描自定义注解@ClassMapingUrl存入的Map）是否存在对应的Controller（后端控制器）。

4、  RequestDispatcherFilter对Controller进行newInstance并初始化ControllerPropertyInject（对Controller进行请求参数的自动注入，可支持多级引用数据类型）。

5、  Controller执行Method并将结果（建议Json）返回给RequestDispatcherFilter。

6、 RequestDispatcherFilter响应用户。

## 引入
在你的 项目工程中lib目录加入foryou_core-1.8的jar包：


## 项目配置：

1.在你的项目Src根目录创建mvc.properties文件，并在文件中添加内容：scan-package:待扫描的packeage包名

说明：scan-package为键不允许改变，值配置为你项目控制器（Controller）的包路径

Example:scan-package:com.alinkeji.controller
	
2.将你的Controller类（上一步配置的packeage下的java）继承BaseController此父类并添加@ClassMapingUrl注解
	
	
	@ClassMapingUrl
	public class TestController extends BaseController {

		public String property1;
	
		public String property2;
	
		public String testMethod(){
			System.out.println("property1="+property1+",property2="+property2);
			return "Request This testMethod";
		}
	}
3.在web.xml配置启动监听器和前端请求拦截器，具体配置如下：

	<!-- 系统启动监听器 -->
	<listener>
   		<listener-class>com.alinkeji.listener.InitMvc</listener-class>
  	</listener>
  	
  	<!-- 前端Requset拦截器 -->
  	<filter>
    	<filter-name>requst</filter-name>
    	<filter-class>com.alinkeji.filter.RequestDispatcherFilter</filter-class>
  	</filter>
  	<filter-mapping>
   		<filter-name>requst</filter-name>
    	<url-pattern>/*</url-pattern>
  	</filter-mapping>

## 调用
经过上面的配置，tomcat在启动时会对mvc.properties下的包进行扫描并存入一个Map中
前端Requsrt请求的时候RequestDispatcherFilter前端控制器通过对Url进行分析调用相应的Controller中对应的method方法
调用的时候自动将所传递的参数进行Java的反射注入到Controller的属性中

Example:http://www.alinkeji.com/TestController/testMethod?property1=123&property2=456

Controller：TestController

Method：testMethod
将Controller和Method改为你项目对应的即可实现前端请求到Controller的调用。并自动实例化和注入多级引用数据类型的值

## License
Copyright 2017 luolin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
