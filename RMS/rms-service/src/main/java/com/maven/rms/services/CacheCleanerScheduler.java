package com.maven.rms.services;

import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.maven.rms.utils.CacheManager;

@Component
public class CacheCleanerScheduler {
    @Autowired
    private CacheManager cacheManager;

    @Scheduled(fixedRate = 10 * 60 * 1000) // Run cleanup every 10 minutes
    public void scheduleCleanup() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, CacheManager.CacheObject>> iterator = cacheManager.getCache().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, CacheManager.CacheObject> entry = iterator.next();
            CacheManager.CacheObject cacheObject = entry.getValue();

            if (currentTime - cacheObject.creationTime > cacheManager.TIME_TO_LIVE) {
                iterator.remove();
            }
        }
    }
}
