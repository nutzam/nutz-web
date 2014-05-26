package org.nutz.web.maker;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Strings;

public class ProjectConf {

    String jetty_ver;
    String path;
    String pkg;
    String pnm;
    int app_port = 8080;
    int app_admin_port = 8081;

    Map<String, String> modules = new HashMap<String, String>();

    /**
     * @return 一个新的配置项
     */
    public static ProjectConf NEW() {
        return new ProjectConf();
    }

    /**
     * 设定工程目录
     * 
     * @param path
     * @return
     */
    public ProjectConf path(String path) {
        this.path = path;
        return this;
    }

    /**
     * 设定最外层包名
     * 
     * @param pkg
     * @return
     */
    public ProjectConf pkg(String pkg) {
        this.pkg = pkg.toLowerCase();
        if (-1 != pkg.lastIndexOf(".")) {
            this.pnm = Strings.upperFirst(pkg.substring(pkg.lastIndexOf(".") + 1));
        }
        return this;
    }

    /**
     * 设定端口
     * 
     * @param port
     * @return
     */
    public ProjectConf appPort(int port) {
        this.app_port = port;
        return this;
    }

    /**
     * 设定管理端口
     * 
     * @param port
     * @return
     */
    public ProjectConf adminPort(int port) {
        this.app_admin_port = port;
        return this;
    }

    /**
     * 给出各个http模块名称与对应的路径
     * 
     * @param nm
     * @param at
     * @return
     */
    public ProjectConf addModule(String nm, String at) {
        modules.put(nm, at);
        return this;
    }

    /**
     * 给出各个http模块名称与对应的路径
     * 
     * @param nm
     * @param at
     * @return
     */
    public ProjectConf addModule(String nm) {
        modules.put(nm, nm);
        return this;
    }

}
