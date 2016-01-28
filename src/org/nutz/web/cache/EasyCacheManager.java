package org.nutz.web.cache;

import java.util.HashMap;
import java.util.Map;

public abstract class EasyCacheManager<T> implements CacheManager<T> {

    private Map<String, T> cache = new HashMap<String, T>();

    private T _new(String key) {
        T value = null;
        value = create(key);
        if (value != null) {
            cache.put(key, value);
        }
        return value;
    }

    public synchronized T get(String key) {
        T value = null;
        if (cache.containsKey(key)) {
            value = cache.get(key);
            if (isExpired(value)) {
                value = _new(key);
            }
        } else {
            value = _new(key);
        }
        return value;
    }

}
