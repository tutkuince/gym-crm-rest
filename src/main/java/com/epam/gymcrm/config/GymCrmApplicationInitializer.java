package com.epam.gymcrm.config;

import com.epam.gymcrm.api.logging.TransactionIdFilter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class GymCrmApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(
                HibernateConfig.class,
                WebConfig.class
        );

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");

        FilterRegistration.Dynamic transactionIdFilter = servletContext.addFilter(
                "transactionIdFilter", TransactionIdFilter.class);
        transactionIdFilter.addMappingForUrlPatterns(null, false, "/*");
    }
}
