package foryou.core.listener;


import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import foryou.core.mvc.MvcCore;

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
		System.out.println("System Initialize");
		File root = new File(this.getClass().getClassLoader().getResource("/").getPath());
		System.out.println("Init Foryou Core Mvc....");
		try {
			MvcCore.initMvc(root, "", MvcCore.getProperties("controller-scan-package").split(","));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
