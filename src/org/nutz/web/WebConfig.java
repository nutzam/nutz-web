package org.nutz.web;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segments;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.lang.util.MultiLineProperties;
import org.nutz.web.jsp.RsScaner;

/**
 * 封装 web.properies 的读取，你的应用可以继承这个类，实现自己更专有的配置文件读取类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebConfig {

    /**
     * 一个用来区分测试数据库和生产数据库的后缀。在测试用例运行前，修改这个变量即可
     */
    public static String JUNIT_DB_SUFFIX = "";

    /**
     * 配置文件的键名: 应用的根路径，比如 "~/workspace/git/danoo/strato/domain/ROOT"
     */
    private static final String APP_ROOT = "app-root";

    /**
     * 配置文件的键名: 应用监听的端口，比如 8080
     */
    private static final String APP_PORT = "app-port";

    /**
     * 配置文件的键名: 应用静态资源的地址前缀，比如 "http://localhost/strato"，或者 "/rs" 等
     */
    private static final String APP_RS = "app-rs";

    /**
     * 配置文件的键名: 应用的类路径，可多行
     */
    private static final String APP_CLASSPATH = "app-classpath";

    /**
     * 配置文件的键名: 应用的管理端口，比如 8081
     */
    private static final String ADMIN_PORT = "admin-port";

    /**
     * 配置文件的键名: 引入更多的配置文件
     */
    private static final String MACRO_INCLUDE = "$include";

    /**
     * 存放所有的属性
     */
    protected PropertiesProxy pp;

    public String getAppRoot() {
        return pp.get(APP_ROOT);
    }

    public int getAppPort() {
        return pp.getInt(APP_PORT);
    }

    public String getAppRs() {
        return pp.get(APP_RS);
    }

    public String getAppClasspath() {
        return pp.get(APP_CLASSPATH);
    }

    public int getAdminPort() {
        return pp.getInt(ADMIN_PORT, pp.getInt(APP_PORT)+1);
    }

    // ================================================= 一些通用方法

    public void set(String key, String val) {
        pp.put(key, val);
    }

    public String get(String key) {
        return pp.get(key);
    }

    public String check(String key) {
        String val = get(key);
        if (null == val)
            throw Lang.makeThrow("Ioc.$conf expect property '%s'", key);
        return val;
    }

    public String get(String key, String defaultValue) {
        return pp.get(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean dfval) {
        String val = pp.get(key);
        if (Strings.isBlank(val))
            return dfval;
        return Castors.me().castTo(val, Boolean.class);
    }

    public int getInt(String key) {
        return pp.getInt(key);
    }

    public int getInt(String key, int dfval) {
        return pp.getInt(key, dfval);
    }

    public long getLong(String key) {
        return pp.getLong(key);
    }

    public long getLong(String key, long dfval) {
        return pp.getLong(key, dfval);
    }

    public String getTrim(String key) {
        return pp.getTrim(key);
    }

    public String getTrim(String key, String defaultValue) {
        return pp.getTrim(key, defaultValue);
    }

    public List<String> getKeys() {
        return pp.getKeys();
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
        jrs.setForce("force".equalsIgnoreCase(get("app-rs-scan","force")));
        return jrs;
    }

    /**
     * 在构造函数中解析配置文件
     * 
     * @param path
     *            配置文件路径
     */
    public WebConfig(String path) {
        // 开始解析
        this.pp = new PropertiesProxy();
        this.pp.setPaths(path);
        // 预处理键 : 引入其他的配置文件
        String str = this.pp.get(MACRO_INCLUDE);
        if (!Strings.isBlank(str)) {
            String[] ss = Strings.splitIgnoreBlank(str, "\n");
            for (String s : ss) {
                File f = Files.findFile(s);
                if (null == f) {
                    throw Lang.makeThrow("Fail to found path '%s' in CLASSPATH or File System!", s);
                }
                // 如果是一个包，引用全部 Files
                if (f.isDirectory()) {
                    Disks.visitFile(f, new FileVisitor() {
                        public void visit(File f) {
                            _join_propertiesFile(f);
                        }
                    }, new FileFilter() {
                        public boolean accept(File f) {
                            if (f.isDirectory())
                                return !f.isHidden() && !f.getName().startsWith(".");
                            return f.getName().endsWith(".properties");
                        }
                    });
                }
                // 否则引用单个文件
                else {
                    _join_propertiesFile(f);
                }
            }
        }
    }

    private void _join_propertiesFile(File f) {
        MultiLineProperties mp = new MultiLineProperties();
        try {
            mp.load(Streams.fileInr(f));
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        for (String key : mp.keys())
            pp.put(key, mp.get(key));
    }

}
