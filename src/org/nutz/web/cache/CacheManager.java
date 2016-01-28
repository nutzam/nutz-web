package org.nutz.web.cache;

public interface CacheManager<T> {

    public boolean isExpired(T entity);

    public T create(String key);

    public T get(String key);

}
