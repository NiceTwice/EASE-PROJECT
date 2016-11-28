package com.Ease.dashboard;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class OnStart implements ServletContextListener{
	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		System.out.println("ServletContextListener destroyed");
	}

	// Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent evt) {
		
	}
}
