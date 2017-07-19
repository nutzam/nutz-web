package org.nutz.web;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.nutz.mvc.NutFilter;

public class NutOnlyWebServer extends WebServer {

    public NutOnlyWebServer(WebConfig config) {
        super(config);
    }

    protected void prepare() throws IOException {
        server = new Server(dc.getAppPort());
        ServletContextHandler ctx = new ServletContextHandler();
        ctx.setContextPath("/");
        
        ctx.addServlet(DefaultServlet.class, "/*");
        if (dc.containsKey("app-root")) {
            ctx.setResourceBase(dc.get("app-root"));
        }
        //ctx.setSessionHandler(new SessionHandler(new HashSessionManager()));
        
        FilterHolder fh = new FilterHolder(NutFilter.class);
        fh.setInitParameter("modules", dc.get("mainModuleClassName"));
        ctx.addFilter(fh, "/*", null);
        
        server.setHandler(ctx);
    }
    
}
