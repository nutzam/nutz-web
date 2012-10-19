package org.nutz.web;

import org.nutz.ioc.impl.PropertiesProxy;

/**
 * 一些常量和帮助函数的集合
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Webs {

    /**
     * 在会话中，表示当前用户的键
     */
    public static final String ME = "me";

    /**
     * 用户密码加密方式在 attributes 里的名称，值可能为 "MD5|SHA1" 等
     */
    public static final String PWD_ENCRYPT = "PWD_ENCRYPT";

    /**
     * 默认配置文件路径
     */
    public static final String CONF_PATH = "web.properties";

    /**
     * 默认的 RS 属性名
     */
    public static final String RS = "rs";

    /**
     * 所有配置信息
     */
    private static PropertiesProxy WEB_PROP = null;

    protected static void setProp(PropertiesProxy wp) {
        WEB_PROP = wp;
    }

    /**
     * Ｗeb服务启动时加载的配置文件(包含了include)
     * 
     * @return 配置文件
     */
    public static PropertiesProxy getProp() {
        return WEB_PROP;
    }

    /**
     * 封装所有错误
     */
    public static abstract class Err {

        public static WebException create(String key) {
            return create(null, key, null);
        }

        public static WebException create(String key, Object reason) {
            return create(null, key, reason);
        }

        public static WebException create(Throwable e, String key, Object reason) {
            return new WebException(e).key(key).reason(reason);
        }

        public static WebException wrap(Throwable e) {
            if (e instanceof WebException)
                return (WebException) e;
            return new WebException(e).key(e.getClass().getName()).reason(e.toString());
        }

    }

}
