package org.nutz.web;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
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
        
        FilterHolder fh = new FilterHolder(NutFilter.class);
        fh.setInitParameter("modules", dc.get("mainModuleClassName"));
        ctx.addFilter(fh, "/*", null);
        
        server.setHandler(ctx);
    }
    
    public void run() {
        super.run();
    }
    
}
