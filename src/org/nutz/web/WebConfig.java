package org.nutz.web;

import java.io.Reader;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segments;
import org.nutz.lang.util.Disks;
import org.nutz.web.jsp.RsScaner;

/**
 * 封装 web.properies 的读取，你的应用可以继承这个类，实现自己更专有的配置文件读取类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebConfig extends PropertiesProxy {

    /**
     * 一个用来区分测试数据库和生产数据库的后缀。在测试用例运行前，修改这个变量即可
     */
    public static String JUNIT_DB_SUFFIX = "";

    /**
     * 配置文件的键名: 应用的根路径，比如 "~/nutz/web/ROOT"
     */
    public static final String APP_ROOT = "app-root";

    /**
     * 配置WEB应用的上下文路径，默认是 "/"
     */
    public static final String APP_CONTEXT_PATH = "app-context-path";

    /**
     * 配置文件的键名: 应用监听的端口，比如 8080
     */
    public static final String APP_PORT = "app-port";
    
    public static final String BIND_ADDRESS = "bind-address";

    /**
     * 配置文件的键名: 应用静态资源的地址前缀，比如 "http://localhost/nutz"，或者 "/rs" 等
     */
    public static final String APP_RS = "app-rs";

    /**
     * 配置文件的键名: 应用的类路径，可多行
     */
    public static final String APP_CLASSPATH = "app-classpath";

    /**
     * 配置文件的键名: 应用的管理端口，比如 8081
     */
    public static final String ADMIN_PORT = "admin-port";

    /**
     * Jetty 默认参数集合，如果为空，则忽略
     */
    public static final String APP_DEFAULTS_DESCRIPTOR = "app-defaults-descriptor";

    /**
     * 配置文件的键名: 引入更多的配置文件
     */
    public static final String MACRO_INCLUDE = "$include";

    public String getAppRoot() {
        return Disks.absolute(get(APP_ROOT));
    }

    public String getAppContextPath() {
        return this.get(APP_CONTEXT_PATH, "/");
    }

    public int getAppPort() {
        return getInt(APP_PORT);
    }

    public String getAppRs() {
        return get(APP_RS);
    }

    public String getAppClasspath() {
        return get(APP_CLASSPATH);
    }

    public int getAdminPort() {
        return getInt(ADMIN_PORT, getInt(APP_PORT) + 1);
    }

    public String getAppDefaultsDescriptor() {
        return get(APP_DEFAULTS_DESCRIPTOR);
    }

    public boolean hasAppDefaultsDescriptor() {
        return has(APP_DEFAULTS_DESCRIPTOR);
    }

    public String getAppName() {
        return get("app-name");
    }

    public String getAppExtrs() {
        return get("app-extrs");
    }

    public String getAppAnnPaths() {
        return get("app-ann-paths");
    }

    public String getAppModules() {
        return get("app-modules");
    }

    // ================================================= 获取路径扫描器
    /**
     * 创建一个资源扫描器，它需要下列属性:
     * <ul>
     * <li><b>app-rs</b> : 资源的网络访问前缀
     * <li><b>app-rs-home</b> : 资源的硬盘所在目录
     * <li><b>app-rs-css</b> : CSS 的 HTML 代码模板
     * <li><b>app-rs-script</b> : JS 的 HTML 代码模板
     * <li><b>app-rs-scan-path</b> : 资源的扫描路径
     * </ul>
     * 
     * @return 资源扫描器
     */
    public RsScaner getScaner() {
        // 生成对象
        RsScaner jrs = new RsScaner();
        jrs.setRs(check("app-rs"));
        jrs.setRsHome(check("app-rs-home"));
        jrs.setSegCss(Segments.create(check("app-rs-css")));
        jrs.setSegJs(Segments.create(check("app-rs-script")));
        jrs.setScanPaths(Strings.splitIgnoreBlank(check("app-rs-scan-path"), "\n"));
        jrs.setForce("force".equalsIgnoreCase(get("app-rs-scan", "force")));
        return jrs;
    }

    /**
     * 在构造函数中解析配置文件
     * 
     * @param path
     *            配置文件路径
     */
    public WebConfig(String path) {
        super(path);
        // 预处理键 : 引入其他的配置文件
        joinByKey(MACRO_INCLUDE);
    }

    public WebConfig(Reader r) {
        super(r);
        joinByKey(MACRO_INCLUDE);
    }

    public String check(String key) {
        String val = get(key);
        if (null == val)
            throw Lang.makeThrow("Ioc.$conf expect property '%s'", key);
        return val;
    }

}
