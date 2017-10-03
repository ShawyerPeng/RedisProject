package cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import util.ShardRedisPoolManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ShardRedisCacheManager implements CacheManager {
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap(16);
    private volatile Set<String> cacheNames = Collections.emptySet();
    private int defaultExpiration;
    private Map<String, Integer> expires;
    private ShardRedisPoolManager shardRedisPoolManager;
    private RedisSerializer keySerializer;
    private RedisSerializer valueSerializer;

    public ShardRedisCacheManager() {
    }

    public ShardRedisCacheManager(ShardRedisPoolManager shardRedisPoolManager) {
        this(shardRedisPoolManager, Collections.emptySet());
    }

    public ShardRedisCacheManager(ShardRedisPoolManager shardRedisPoolManager, Set<String> cacheNames) {
        this.shardRedisPoolManager = shardRedisPoolManager;
        this.cacheNames = cacheNames;
        this.defaultExpiration = 0;
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = cacheMap.get(name);
        return cache == null ? createCache(name) : cache;
    }

    private synchronized Cache createCache(String cacheName) {
        if (keySerializer == null) {
            keySerializer = new StringRedisSerializer();
        }
        if (valueSerializer == null) {
            valueSerializer = new JdkSerializationRedisSerializer();
        }
        ShardRedisCache cache = new ShardRedisCache(cacheName, shardRedisPoolManager, keySerializer, valueSerializer);
        cache.setDefaultExpiration(this.computeExpiration(cacheName));

        if (cacheNames == null || cacheNames.size() == 0) {
            cacheNames = new LinkedHashSet<>();
        }
        cacheNames.add(cacheName);
        cacheMap.put(cacheName, cache);
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheNames;
    }

    public void setCacheNames(Set<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    public int getDefaultExpiration() {
        return defaultExpiration;
    }

    public void setDefaultExpiration(int defaultExpiration) {
        this.defaultExpiration = defaultExpiration;
    }

    public Map<String, Integer> getExpires() {
        return expires;
    }

    public void setExpires(Map<String, Integer> expires) {
        this.expires = expires;
    }

    public void setKeySerializer(RedisSerializer keySerializer) {
        this.keySerializer = keySerializer;
    }

    public void setValueSerializer(RedisSerializer valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    private int computeExpiration(String name) {
        Integer expiration = null;
        if (this.expires != null) {
            expiration = this.expires.get(name);
        }
        return expiration != null ? expiration.intValue() : this.defaultExpiration;
    }
}
