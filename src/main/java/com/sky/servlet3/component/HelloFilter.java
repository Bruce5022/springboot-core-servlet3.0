package com.sky.servlet3.component;

import javax.servlet.*;
import java.io.IOException;

public class HelloFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("==================init===================");
    }

    @Override
    public void destroy() {
        System.out.println("==================destroy===================");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        System.out.println("==================doFilter1===================");
        filterChain.doFilter(servletRequest,servletResponse);
        System.out.println("==================doFilter2===================");
    }
}
