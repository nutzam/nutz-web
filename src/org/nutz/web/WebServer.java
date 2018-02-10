package org.nutz.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnectionStatistics;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.lang.Each;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.socket.SocketAction;
import org.nutz.lang.socket.SocketContext;
import org.nutz.lang.socket.Sockets;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.web.handler.JettyHandlerHook;

/**
 * 这个类将调用 Jetty 的类启动一个 HTTP 服务，并提供关闭这个服务的 Socket 端口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebServer {

    private static final Log log = Logs.get();

    protected WebConfig dc;

    protected Server server;
    
    boolean websocketEnable = false;
    
    WebAppContext wac;

    public WebServer(WebConfig config) {
        this.dc = config;

        // 保存到静态变量中
        Webs.setProp(config);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void prepare() throws IOException {
        // 获取监听地址和端口
        if (dc.getAppPort() <= 0) {
            dc.set(WebConfig.APP_PORT, "80");
        }
        if (!dc.has(WebConfig.BIND_ADDRESS))
            dc.set(WebConfig.BIND_ADDRESS, "0.0.0.0");
        // 创建基础服务器
        server = new Server(new QueuedThreadPool(Lang.isAndroid ? 50 : 500));
        ServerConnector connector= new ServerConnector(server);
        connector.setHost(dc.get(WebConfig.BIND_ADDRESS));
        connector.setPort(dc.getAppPort());
        server.setConnectors(new Connector[]{connector});
        
        // 设置应用上下文
        String warUrlString = null;
        if (dc.has("war")) {
            warUrlString = dc.get("war");
        } else {
            String rootPath = dc.getAppRoot();
            File root = Files.findFile(rootPath);
            if (root == null || !root.exists()) {
                log.warnf("root: '%s' not exist!", dc.get(WebConfig.APP_ROOT));
                warUrlString = Lang.runRootPath();
            } else {
                warUrlString = root.getAbsoluteFile().toURI().toURL().toExternalForm();
            }
        }
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 1024*1024);
        log.debugf("++war path : %s", warUrlString);
        wac = new WebAppContext(warUrlString, dc.getAppContextPath());
        if (warUrlString.endsWith(".war") || dc.has("war")) {
            wac.setExtractWAR(false);
            wac.setCopyWebInf(true);
            wac.setProtectedTargets(new String[]{"/java", "/javax", "/org", "/net", "/WEB-INF", "/META-INF"});
            wac.setTempDirectory(new File("./tmp").getAbsoluteFile());
            wac.setServerClasses(new String[] { "org.objectweb.asm.", // hide asm used by jetty
                                                    "org.eclipse.jdt.", // hide jdt used by jetty
                                                    "org.nutz" // hide nutz classes
                                            });
            InputStream ins = getClass().getClassLoader().getResourceAsStream("web.allows");
            if (ins != null) {
                log.info("found web.allows");
                final Set<String> allowPaths = new HashSet<String>();
                Streams.eachLine(new InputStreamReader(ins), new Each<String>() {
                    public void invoke(int index, String ele, int length) {
                        allowPaths.add(ele);
                    }
                });
                wac.addFilter(new FilterHolder(new WebAllowFilter(allowPaths)), "/*", EnumSet.of(DispatcherType.REQUEST));
            }
        } else {
            if (dc.hasAppDefaultsDescriptor()) {
                wac.setDefaultsDescriptor(dc.getAppDefaultsDescriptor());
            }
            wac.setClassLoader(getClass().getClassLoader());
        }
        wac.setExtraClasspath(dc.getAppClasspath());
        wac.setConfigurationDiscovered(true);
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            wac.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        }
        server.setHandler(new JettyHandlerHook(server, wac));
        try {
            Class _klass = Class.forName("org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer", false, getClass().getClassLoader());
            Class.forName("javax.annotation.security.RunAs", false, getClass().getClassLoader());
            List<String> list = Configuration.ClassList.serverDefault(server);
            list.add("org.eclipse.jetty.annotations.AnnotationConfiguration");
            wac.setConfigurationClasses(list);
            if (dc.has("war"))
                _klass.getMethod("configureContext", ServletContextHandler.class).invoke(null, wac);
            log.info("init websocket context success");
            websocketEnable = true;
        } catch (Exception e) {
            log.info("miss some websocket class, skip websocket init", e);
        }
        
        // Extra options
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);
        
        if (!Lang.isAndroid) {
            // === jetty-stats.xml ===
            StatisticsHandler stats = new StatisticsHandler();
            stats.setHandler(server.getHandler());
            server.setHandler(stats);
            ServerConnectionStatistics.addToAllConnectors(server);
            
            LowResourceMonitor lowResourcesMonitor=new LowResourceMonitor(server);
            lowResourcesMonitor.setPeriod(1000);
            lowResourcesMonitor.setLowResourcesIdleTimeout(200);
            lowResourcesMonitor.setMonitorThreads(true);
            lowResourcesMonitor.setMaxConnections(0);
            lowResourcesMonitor.setMaxMemory(0);
            lowResourcesMonitor.setMaxLowResourcesTime(5000);
            server.addBean(lowResourcesMonitor);
        }
    }

    public void run() {
        try {
            // 准备 ..
            prepare();

            // 启动
            server.start();
            

            if (websocketEnable) {
                List<String> websockets = dc.getList("websockets");
                try {
                    ServerContainer sc = (ServerContainer) wac.getAttribute(ServerContainer.class.getName());
                    if (websockets != null) {
                        for (String className : websockets) {
                            sc.addEndpoint(Class.forName(className));
                        }
                    }
                }
                catch (Exception e) {
                    log.warn("enable websocket fail", e);
                }
            }

            // 添加更多的 JSP 寻找路径
            if (dc.has("app-jsp-extpath")) {
                // 基础的 app-root 作为寻找列表的第一项
                List<Resource> rs = new ArrayList<Resource>();
                rs.add(wac.getBaseResource());
                String[] ss = Strings.splitIgnoreBlank(dc.trim("app-jsp-extpath"), "[,\n]");
                for (String s : ss) {
                    File d = Files.findFile(s);
                    if (null != d) {
                        Resource r = Resource.newResource(d.getCanonicalFile().toURI());
                        if (r.exists()) {
                        	log.debug("app-jsp-extpath OK >> " + s);
                            rs.add(r);
                            continue;
                        }
                    }
                    log.debug("app-jsp-extpath FAIL >> " + s);
                }
                // 设置进上下文
                wac.setBaseResource(new ResourceCollection(rs.toArray(new Resource[rs.size()])));
            }

            // 自省一下,判断自己是否能否正常访问
            Response resp = Http.get("http://127.0.0.1:" + dc.getAppPort());
            if (resp == null || resp.getStatus() >= 500) {
                log.error("Self-Testing fail !!Server start fail?!!");
                server.stop();
                return;
            }

            if (log.isInfoEnabled())
                log.info("Server is up!");

            // 管理
            if (log.isInfoEnabled())
                log.infof("Create admin port at %d", dc.getAdminPort());
            Sockets.localListenOne(dc.getAdminPort(), "stop", new SocketAction() {
                public void run(SocketContext context) {
                    if (null != server)
                        try {
                            server.stop();
                        }
                        catch (Exception e4stop) {
                            if (log.isErrorEnabled())
                                log.error("Fail to stop!", e4stop);
                        }
                    Sockets.close();
                }
            });

        }
        catch (Throwable e) {
            if (log.isWarnEnabled())
                log.warn("Unknow error", e);
        }

    }

    @Override
    protected void finalize() throws Throwable {
        if (null != server)
            try {
                server.stop();
            }
            catch (Throwable e) {
                if (log.isErrorEnabled())
                    log.error("Fail to stop!", e);
                throw e;
            }
        super.finalize();
    }

    public static class WebAllowFilter implements Filter {
        
        protected Set<String> allowPaths;
        
        public WebAllowFilter(Set<String> allowPaths) {
            this.allowPaths = allowPaths;
        }

        public void init(FilterConfig filterConfig){
        }
        
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest)request;
            String uri = req.getRequestURI();
            if (uri != null && uri.length() > 1) {
                uri = uri.substring(1);
                URL u = getClass().getClassLoader().getResource(uri);
                if (u != null && !allowPaths.contains(uri)) {
                    ((HttpServletResponse)response).setStatus(404);
                    return;
                }
                //log.debug("Pass : " + uri);
            }
            chain.doFilter(request, response);
        }
        
        public void destroy() {
        }
    }
}
