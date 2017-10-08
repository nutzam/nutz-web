package org.nutz.web.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;

public interface JettyHandlerCallback {

    void handle(Handler handler,
                String target,
                Request baseRequest,
                HttpServletRequest request,
                HttpServletResponse response)
            throws IOException, ServletException;
}
