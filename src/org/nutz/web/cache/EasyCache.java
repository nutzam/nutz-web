package org.nutz.web.cache;

import java.util.HashMap;
import java.util.Map;

public class EasyCache {

    private static Map<String, CacheManager<?>> cm = new HashMap<String, CacheManager<?>>();

    public static void reg(String name, CacheManager<?> cManager) {
        cm.put(name, cManager);
    }

    public static void unreg(String name) {
        cm.remove(name);
    }

    public static CacheManager<?> M(String name) {
        return cm.get(name);
    }
}
