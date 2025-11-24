package com.air.quality.index.aqi.Cache;

import com.air.quality.index.aqi.DTO.AirQualityResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AirQualityCache {
    private final long ttlMillis;
    private final int maxEntries;

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public AirQualityCache(
            @Value("${cache.ttl-seconds:300}") long ttlSeconds,
            @Value("${cache.max-entries:100}") int maxEntries
    ){
        this.ttlMillis = ttlSeconds * 1000;
        this.maxEntries = maxEntries;
    }

    @Data
    @AllArgsConstructor
    private static class CacheEntry{
        private AirQualityResponse value;
        private long timestamp;
    }

    public AirQualityResponse get(String cityKey) {
        CacheEntry entry = cache.get(cityKey);
        if (entry == null) return null;

        if (isExpired(entry)) {
            cache.remove(cityKey);
            return null;
        }
        return entry.getValue();
    }

    public void put(String cityKey, AirQualityResponse value) {
        if (cache.size() >= maxEntries) {
            evictOldest();
        }
        cache.put(cityKey, new CacheEntry(value, System.currentTimeMillis()));
    }

    private boolean isExpired(CacheEntry entry) {
        return System.currentTimeMillis() - entry.getTimestamp() > ttlMillis;
    }

    private void evictOldest() {
        // Simple approximation: iterate and remove the oldest timestamp
        String oldestKey = null;
        long oldestTime = Long.MAX_VALUE;

        for (Map.Entry<String, CacheEntry> e : cache.entrySet()) {
            if (e.getValue().timestamp < oldestTime) {
                oldestTime = e.getValue().timestamp;
                oldestKey = e.getKey();
            }
        }
        if (oldestKey != null) {
            cache.remove(oldestKey);
        }
    }
}
