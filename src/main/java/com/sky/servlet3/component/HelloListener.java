package com.sky.servlet3.component;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class HelloListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        // 也可以在这里注册组件,如果
        System.out.println("==================contextInitialized===================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("==================contextDestroyed===================");
    }
}
