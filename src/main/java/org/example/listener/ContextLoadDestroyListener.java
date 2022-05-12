package org.example.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.example.attribute.ContextAttributes;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ContextLoadDestroyListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();
        final String basePackage = "org.example";
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(basePackage);
        servletContext.setAttribute(ContextAttributes.CONTEXT_ATTR, context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();
        final Object attribute = servletContext.getAttribute(ContextAttributes.CONTEXT_ATTR);
        if (attribute instanceof AutoCloseable autoCloseable) {
            try {
                autoCloseable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
