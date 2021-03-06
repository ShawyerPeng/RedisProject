package cache;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import util.ShardRedisPoolManager;

import java.util.concurrent.Callable;

public class ShardRedisCache implements Cache {
    private static final Logger log = LoggerFactory.getLogger(ShardRedisCache.class);

    private ShardRedisPoolManager shardRedisPoolManager;
    private String name;
    private RedisSerializer keySerializer;
    private RedisSerializer valueSerializer;
    private int defaultExpiration = 0;

    public ShardRedisCache(String name, ShardRedisPoolManager shardRedisPoolManager, RedisSerializer keySerializer, RedisSerializer valueSerializer) {
        this.name = name;
        this.shardRedisPoolManager = shardRedisPoolManager;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    public int getDefaultExpiration() {
        return defaultExpiration;
    }

    public void setDefaultExpiration(int defaultExpiration) {
        this.defaultExpiration = defaultExpiration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ShardRedisPoolManager getNativeCache() {
        return shardRedisPoolManager;
    }

    @Override
    public ValueWrapper get(Object key) {
        log.info("======get from cache '{}' by key : {} ======",name,key);
        try {
            byte[] computeKey = computeKey(key);//keySerializer.serialize(key);
            byte[] bs = shardRedisPoolManager.get(computeKey);
            Object value = valueSerializer.deserialize(bs);
            return (bs == null ? null : new SimpleValueWrapper(value));
        } catch (SerializationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper wrapper = this.get(key);
        return wrapper == null?null:(T)wrapper.get();
    }

    @Override
    public <T> T get(Object key, Callable<T> callable) {
        ValueWrapper wrapper = this.get(key);
        return wrapper == null?null:(T)wrapper.get();
    }

    @Override
    public void put(Object key, Object value) {
        putIfAbsent(key,value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        log.info("======put to cache '{}' with key : {} ======",name,key);
        try {
            byte[] k = computeKey(key);//keySerializer.serialize(key);
            byte[] v = valueSerializer.serialize(value);

            String s = shardRedisPoolManager.set(k,v);
            if (s!=null) this.expires(k);
            return (s == null ? null : new SimpleValueWrapper(s));
        } catch (SerializationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void evict(Object key) {
        log.info("======delete from cache '{}' by key : {} ======",name,key);
        try {
            byte[] k = computeKey(key);//keySerializer.serialize(key);
            shardRedisPoolManager.del(k);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {

    }

    private byte[] computeKey(Object key){
        byte[] bytes = keySerializer.serialize(key);
        return ArrayUtils.addAll(name.getBytes(),bytes);
    }

    /**
     * 设置 Key 失效时间
     */
    private void expires(byte[] key) {
        shardRedisPoolManager.expire(key,defaultExpiration);
    }
}
