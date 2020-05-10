package org.nutz.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.CmdParams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 一个 Web 服务的启动器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @pw
 */
public class WebLauncher {

    private static final Log log = Logs.get();

    public static void main(String[] args) {
        start(args);
    }

    /**
     * 执行启动的主函数，接受一个参数，为 web 服务器的配置文件路径。如果没有这个参数，默认在 classpath 下寻找
     * "web.properties" 文件。
     * <p>
     * 这个文件遵循 Nutz 多行属性文件规范，同时必须具备如下的键:
     * <ul>
     * <li>"app-root" - 应用的根路径，比如 "~/workspace/git/danoo/strato/domain/ROOT"
     * <li>"app-port" - 应用监听的端口，比如 8080
     * <li>"app-rs" - 应用静态资源的地址前缀，比如 "http://localhost/strato"，或者 "/rs" 等
     * <li>"app-classpath" - 应用的类路径，可多行
     * <li>"admin-port" - 应用的管理端口，比如 8081
     * </ul>
     * 这个文件的例子，请参看源码 conf 目录下的 web.properties
     * 
     * @param args
     *            接受一个参数作为 web 服务器的配置文件路径
     */
    public static void start(String... args) {
        if (args != null && args.length > 0 && args[0].startsWith("-")) {
            exec(args);
            return;
        }
        WebConfig conf = null;
        String self = selfPath();
        if (self != null && checkWebXml(self)) {
            if (new File("web.properties").exists()) {
                log.info("web.properties found at " + new File("web.properties").getAbsolutePath());
                conf = new WebConfig("web.properties");
            }
            else {
                InputStream ins = WebLauncher.class.getClassLoader().getResourceAsStream("web.properties");
                if (ins == null) {
                    log.info("web.properties not found , using cmdline args");
                    conf = new WebConfig(new StringReader(""));
                } else {
                    log.info("web.properties found");
                    try {
                        conf = new WebConfig(new InputStreamReader(ins, Encoding.UTF8));
                    }
                    catch (UnsupportedEncodingException e) {
                        throw Lang.impossible();
                    }
                }
            }
            
            conf.put("war", self);
            conf.put("web-xml", self + "/WEB-INF/web.xml");
            CmdParams params = CmdParams.parse(args, null);
            conf.put(WebConfig.APP_PORT, params.get("port", conf.get(WebConfig.APP_PORT, "8080")));
            conf.put(WebConfig.BIND_ADDRESS, params.get("bind", conf.get(WebConfig.BIND_ADDRESS, "0.0.0.0")));
            conf.put(WebConfig.APP_CLASSPATH, params.get("cp", conf.get(WebConfig.APP_CLASSPATH, "./conf/")));
        }
        if (conf == null) {
            String path = Strings.sBlank(Lang.first(args), Webs.CONF_PATH);
            File f = Files.findFile(path);
            if (f == null) {
                throw new RuntimeException(new FileNotFoundException(path));
            } else {
                log.infof("launch by '%s'", f);
                Reader r = Streams.fileInr(f);
                conf = new WebConfig(r);
            }
        }
        // 系统属性
        conf.putAll(System.getProperties());
        // 环境变量
        conf.putAll(System.getenv());
        
        final WebServer server = new WebServer(conf);
        server.run();

        log.info("Server is down!");
    }

    private static boolean checkWebXml(String self) {
        File f = new File(self);
        ZipFile zip = null;
        try {
            zip = new ZipFile(f);
            return null != zip.getEntry("WEB-INF/web.xml");
        } catch (Exception e) {
            return false;
        } finally {
            Streams.safeClose(zip);
        }
    }

    public static void startNutOnlyWebapp(String... args) {
        String path = Strings.sBlank(Lang.first(args), Webs.CONF_PATH);

        log.infof("launch by '%s'", path);

        final WebServer server = new NutOnlyWebServer(new WebConfig(path));

        server.run();

        log.info("Server is down!");
    }
    
    public static void printHelp() {
        log.warn("web.properties not found");
    }
    
    public static void exec(String...args) {
        log.debug(Json.toJson(args));
        CmdParams params = CmdParams.parse(args, "debug");
        if (params.has("inject")) {
            String src = params.get("inject");
            String self = null;
            if (params.has("self"))
                self = params.get("self");
            else
                self = WebLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            String dst = params.get("output");
            if (dst == null) {
                dst = src.substring(0, src.lastIndexOf('.')) + "-nutzweb.war";
            }
            merge(params, src, self, dst);
            return;
        }
        if (params.has("war")) {
            WebConfig conf = new WebConfig(new StringReader(""));
            conf.set("war", params.check("war"));
            final WebServer server = new WebServer(conf);
            server.run();
        }
    }
    
    public static void merge(CmdParams params, String srcA, String srcB, String target) {
        try {
            Files.createFileIfNoExists(target);
            ZipInputStream zin_a = new ZipInputStream(new FileInputStream(srcA), Encoding.CHARSET_UTF8);
            ZipInputStream zin_b = new ZipInputStream(new FileInputStream(srcB), Encoding.CHARSET_UTF8);
            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(target), Encoding.CHARSET_UTF8);
            zout.setLevel(0);
            while (true) {
                ZipEntry en = zin_a.getNextEntry();
                if (en == null)
                    break;
                if (en.getName().equals("META-INF/MANIFEST.MF"))
                    continue;
                if (params.is("debug"))
                    log.debug("add " + en.getName());
                zout.putNextEntry(new ZipEntry(en.getName()));
                Streams.write(zout, zin_a);
                zout.closeEntry();
            }
            while (true) {
                ZipEntry en = zin_b.getNextEntry();
                if (en == null)
                    break;
                String name = en.getName();
                if (!name.contains("/"))
                    continue;
                if (name.startsWith("org/nutz/dao") || name.startsWith("org/nutz/aop"))
                    continue;
                if (params.is("debug"))
                    log.debug("add " + en.getName());
                try {
                    zout.putNextEntry(new ZipEntry(en.getName()));
                    Streams.write(zout, zin_b);
                    zout.closeEntry();
                }
                catch (Exception e) {
                    if (!en.getName().endsWith("/"))
                        log.info("dup ? " + en.getName());
                }
            }
            zout.flush();
            zout.finish();
            zout.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String selfPath() {
        try {
            ProtectionDomain domain = WebLauncher.class.getProtectionDomain();
            URL location = domain.getCodeSource().getLocation();
            return location.getFile();
        }
        catch (Exception e) {
            return "";
        }
    }
}
