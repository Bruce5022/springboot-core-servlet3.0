package com.sky.servlet3;

import com.sky.servlet3.component.HelloFilter;
import com.sky.servlet3.component.HelloListener;
import com.sky.servlet3.component.HelloServlet;
import com.sky.servlet3.service.DemoService;

import javax.servlet.*;
import javax.servlet.annotation.HandlesTypes;
import java.util.EnumSet;
import java.util.Set;

// 容器启动的时候,会将@HandlesTypes指定的这个类型下面的子类(实现类子接口等)传递过来
// 传入感兴趣的类,容器启动后,会把指定类的子类,子接口等,都放入set
@HandlesTypes(value = {DemoService.class})
public class DemoServletContainerInitializer implements ServletContainerInitializer {

    /**
     * @param set            感兴趣的类所有的子类型
     * @param servletContext 当前web应用的ServletContext,一个web应用一个ServletContext
     * @throws ServletException
     */
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        System.out.println("--->All aware classes as follow :");
        set.stream().forEach(System.out::println);
        System.out.println("--->All aware classes end !!!");

        // 可以使用servletContext注册web组件(servlet,filter,listener)
        // 如果引入第三方包,需要往里面增加监听器,过滤器,可以用此方法
        // 注册servlet
        ServletRegistration.Dynamic helloServlet = servletContext.addServlet("helloServlet", new HelloServlet());
        helloServlet.addMapping("/hello");
        // 注册监听器
        servletContext.addListener(new HelloListener());
        // 注册过滤器
        FilterRegistration.Dynamic helloFilter = servletContext.addFilter("helloFilter", HelloFilter.class);
        helloFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }
}
