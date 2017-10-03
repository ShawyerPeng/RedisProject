package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

public class JedisPoolManager {
    private static final Logger log = LoggerFactory.getLogger(JedisPoolManager.class);

    // Redis服务器IP，这里可使用diamond获取
    private static String HOST = "127.0.0.1";
    // Redis的端口号
    private static int PORT = 6379;
    // 可用连接实例的最大数目，默认值为8；
    // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_TOTAL = 50;
    // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 10;
    // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 5000;
    private static int TIMEOUT = 1000;

    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool;
    private ShardedJedisPool shardedJedisPool;

    public JedisPoolManager() {
        createJedisPool();
    }

    public JedisPoolManager(ShardedJedisPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }

    /**
     * 建立并初始化Jedis连接池
     */
    private static void createJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_TOTAL);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(TEST_ON_BORROW);
        jedisPool = new JedisPool(config, HOST, PORT, TIMEOUT);
    }

    /**
     * 获取Jedis实例
     */
    public synchronized static Jedis getResource() {
        try {
            if (jedisPool != null) {
                return jedisPool.getResource();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返还到连接池
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null && jedisPool != null) {
            jedis.close();
        }
    }

    /**
     * 释放 jedis 资源
     */
    public static void returnBrokenResource(final Jedis jedis) {
        if (jedis != null && jedisPool != null) {
            jedis.close();
        }
    }

    /**
     * Jedis 方法执行模板
     */
    public <T> T execute(JedisCallback.DoInJedis<T> doInJedis) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return doInJedis.doIn(jedis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }


    /**
     * 设置过期时间
     */
    public void expire(String key, int seconds) {
        if (seconds <= 0) {
            return;
        }
        Jedis jedis = getResource();
        if (jedis != null) {
            jedis.expire(key, seconds);
        }
        returnResource(jedis);
    }

    /**
     * set 设置值
     */
    public static String set(final String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.set(key, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * set 设置值和过期时间
     */
    public static String set(final String key, String value, int expireSeconds) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.setex(key, expireSeconds, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * get
     */
    public static String get(final String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.get(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * getbit
     */
    public static Boolean getbit(String key, long offset) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.getbit(key, offset);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * del
     */
    public static Long del(final String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.del(keys);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * incr
     *
     * @param key
     * @return 执行 INCR 命令之后 key 的值
     */
    public static Long incr(final String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.incr(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * decr
     *
     * @param key
     * @return 执行 DECR 命令之后 key 的值
     */
    public static Long decr(final String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.decr(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * incrBy
     */
    public static Long incrBy(final String key, long increment) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.incrBy(key, increment);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * decrBy
     */
    public static Long decrBy(final String key, long decrement) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.decrBy(key, decrement);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * incrByFloat
     */
    public static Double incrByFloat(final String key, Double increment) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.incrByFloat(key, increment);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * append
     *
     * @param key
     * @param value
     * @return 追加 value 之后，key 中字符串的长度
     */
    public static Long append(final String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.append(key, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * @param key   key
     * @param start 左截取范围
     * @param end   右截取范围
     * @return 截取得出的子字符串
     */
    public static String getrange(String key, Long start, Long end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.getrange(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * getSet
     *
     * @param key
     * @param value
     * @return 给定 key 的旧值
     */
    public static String getSet(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.getSet(key, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * strlen
     *
     * @param key
     * @return 字符串值的长度
     */
    public static Long strlen(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.strlen(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * setrange
     *
     * @param key
     * @param offset
     * @param value
     * @return 被 SETRANGE 修改之后，字符串的长度
     */
    public static Long setrange(String key, long offset, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.setrange(key, offset, value);
        } finally {
            returnResource(jedis);
        }
    }


    // SharedJedisPool!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    /**
     * del
     */
    public boolean del(String key) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            return shardedJedis.del(key) == 1;
        } catch (Exception e) {
            shardedJedis.close();
            return false;
        }
    }


    /**
     * 单存 redis
     */
    public String setStringValue(String key, String obj, int expireTime) {
        String result = "";
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        if (shardedJedis == null) {
            return result;
        }
        try {
            shardedJedis.setex(key, expireTime, obj);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
        return result;
    }


    /**
     * 单存 redis
     */
    public String setStringValue(String key, String obj) {
        String result = "";
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        if (shardedJedis == null) {
            return result;
        }
        try {
            shardedJedis.set(key, obj);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
        return result;
    }


    /**
     * 计数器
     */
    public long incr(String key, long increment, int expireTime) {
        long result = 0l;
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        if (shardedJedis == null) {
            return result;
        }
        try {
            result = shardedJedis.incrBy(key, increment);
            shardedJedis.expire(key, expireTime);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
        return result;
    }

    /**
     * 计数器
     */
    public long incr(String key, long increment) {
        long result = 0l;
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        if (shardedJedis == null) {
            return result;
        }
        try {
            result = shardedJedis.incrBy(key, increment);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
        return result;
    }

    /**
     * 原子操作，设置新值返回旧值
     */
    public long getAndSet(String key, Integer newValue) {
        long result = 0l;
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        if (shardedJedis == null) {
            return result;
        }
        try {
            String get = shardedJedis.getSet(key, String.valueOf(newValue));
            result = Long.valueOf(get);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            shardedJedis.close();
        }
        return result;
    }

    /**
     * 单个数据
     */
    public String getStringValue(String cacheKey) {
        String b = null;
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        if (shardedJedis == null) {
            return null;
        }
        try {
            b = shardedJedis.get(cacheKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            shardedJedisPool.close();
        }
        return b;
    }

    public static void main(String[] args) {
        Object value = JedisPoolManagerFactory.getJedisPoolManager().execute(new JedisCallback.DoInJedis<Object>() {
            @Override
            public Object doIn(Jedis jedis) {
                jedis.set("username", "admin");
                return jedis.get("username");
            }
        });
        System.out.println(value);

        Object value2 = JedisPoolManagerFactory.getShardedJedisPoolManager().execute(new JedisCallback.DoInJedis<Object>() {
            @Override
            public Object doIn(Jedis jedis) {
                jedis.set("username", "admin");
                return jedis.get("username");
            }
        });
        System.out.println(value2);
    }
}
