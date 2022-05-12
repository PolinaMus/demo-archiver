package org.example.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.attribute.ContextAttributes;
import org.example.handler.Handler;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Map;

public class FrontServlet extends HttpServlet {
    private Map<String, Handler> routes;

    @Override
    public void init() {
        final ApplicationContext context = (ApplicationContext) getServletContext()
                .getAttribute(ContextAttributes.CONTEXT_ATTR);
        routes = (Map<String, Handler>) context.getBean("routes");
    }

    @Override
    protected void service(final HttpServletRequest req,final HttpServletResponse resp) throws IOException {
        final String uri = req.getRequestURI();
        final Handler handler = routes.get(uri);
        if (handler != null) {
            try {
                handler.handle(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
