package com.git.mingo.dashboard.util;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Created by dmgcodevil on 10.05.2014.
 */
public class SpringContextHelper {

    private ApplicationContext context;
    public SpringContextHelper(ServletContext servletContext) {
        /*ServletContext servletContext =
                ((WebApplicationContext) application.getContext())
                .getHttpSession().getServletContext();*/
        context = WebApplicationContextUtils.
                getRequiredWebApplicationContext(servletContext);
    }

    public Object getBean(final String beanRef) {
        return context.getBean(beanRef);
    }
}
