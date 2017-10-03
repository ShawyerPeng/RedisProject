package util;

import redis.clients.jedis.Jedis;

public class JedisCallback {
    /**
     * 定义 Jedis 实例回调接口
     */
    public interface DoInJedis<T> {
        T doIn(Jedis jedis);
    }
}
