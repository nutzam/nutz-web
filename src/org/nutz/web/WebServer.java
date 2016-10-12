package org.nutz.web;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.socket.SocketAction;
import org.nutz.lang.socket.SocketContext;
import org.nutz.lang.socket.Sockets;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 这个类将调用 Jetty 的类启动一个 HTTP 服务，并提供关闭这个服务的 Socket 端口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebServer {

    private static final Log log = Logs.get();

    protected WebConfig dc;

    protected Server server;

    public WebServer(WebConfig config) {
        this.dc = config;

        // 保存到静态变量中
        Webs.setProp(config);
    }

    protected void prepare() throws IOException {
        if (dc.getAppPort() <= 0) {
            dc.set(WebConfig.APP_PORT, "80");
        }
        server = new Server(InetSocketAddress.createUnresolved("0.0.0.0", dc.getAppPort()));
        // 设置应用上下文
        String warUrlString = null;
        String rootPath = dc.getAppRoot();
        File root = Files.findFile(rootPath);
        if (root == null || !root.exists()) {
            log.warnf("root: '%s' not exist!", dc.get(WebConfig.APP_ROOT));
            warUrlString = Lang.runRootPath();
        } else {
            warUrlString = root.toURI().toURL().toExternalForm();
        }
        log.debugf("war path : %s", warUrlString);
        WebAppContext wac = new WebAppContext(warUrlString, dc.getAppContextPath());
        if (dc.hasAppDefaultsDescriptor()) {
            wac.setDefaultsDescriptor(dc.getAppDefaultsDescriptor());
        }
        wac.setExtraClasspath(dc.getAppClasspath());
        wac.setClassLoader(getClass().getClassLoader());
        wac.setConfigurationDiscovered(true);
        
        // wac.setResourceBase(warUrlString);
        // wac.addServlet(DefaultServlet.class, "/rs/*");
        server.setHandler(wac);
        List<String> websockets = dc.getList("websockets");
        try {
            Class<?> klass = Class.forName("org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer");
            Object tmp = klass.getMethod("configureContext", ServletContextHandler.class).invoke(null, wac);
            Method method = tmp.getClass().getMethod("addEndpoint", Class.class);
            if (websockets != null) {
                for (String name : websockets) {
                    method.invoke(tmp, Class.forName(name));
                }
            }
        }
        catch (Exception e) {
            log.warn("enable websocket fail");
        }
        
    }

    public void run() {
        try {
            // 准备 ..
            prepare();

            // 启动
            server.start();

            // 添加更多的 JSP 寻找路径
            if (dc.has("app-jsp-extpath")) {
                // 基础的 app-root 作为寻找列表的第一项
                WebAppContext wac = (WebAppContext) server.getHandler();
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

}
