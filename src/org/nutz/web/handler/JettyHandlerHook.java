package org.nutz.web.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerWrapper;

public class JettyHandlerHook extends HandlerWrapper {

    protected static JettyHandlerHook me;
    
    JettyHandlerCallback callback;
    
    public void setCallback(JettyHandlerCallback callback) {
        this.callback = callback;
    }
    
    public static JettyHandlerHook me() {
        return me;
    }
    
    
    public JettyHandlerHook(Server server, Handler handler) {
        setServer(server);
        setHandler(handler);
        me = this;
    }
    
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {
        if (callback == null)
            super.handle(target, baseRequest, request, response);
        else
            callback.handle(_handler, target, baseRequest, request, response);
    }
}
