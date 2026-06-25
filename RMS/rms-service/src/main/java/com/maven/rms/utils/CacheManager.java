package com.maven.rms.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

@Service
public class CacheManager {

    public final long TIME_TO_LIVE = TimeUnit.MINUTES.toMillis(15); // Made public
    private final ConcurrentMap<String, CacheObject> cache = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        cache.put(key, new CacheObject(value));
    }

    public Object get(String key) {
        CacheObject cacheObject = cache.get(key);
        if (cacheObject == null) {
            return null;
        }
        if (System.currentTimeMillis() - cacheObject.creationTime > TIME_TO_LIVE) {
            cache.remove(key);
            return null;
        }
        return cacheObject.value;
    }

    public static class CacheObject {
        public final long creationTime = System.currentTimeMillis(); // Made public
        private final Object value;

        CacheObject(Object value) {
            this.value = value;
        }

        // Getter for creationTime (optional since creationTime is public now)
        public long getCreationTime() {
            return creationTime;
        }
    }

    public ConcurrentMap<String, CacheObject> getCache() {
        return cache;
    }
}
