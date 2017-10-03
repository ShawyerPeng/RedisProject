package util;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

public class JedisPoolManagerFactory {
    private static volatile JedisPoolManager jedisPoolManager;

    public static JedisPoolManager getJedisPoolManager() {
        if (jedisPoolManager == null) {
            synchronized (JedisPoolManagerFactory.class) {
                if (jedisPoolManager == null) {
                    jedisPoolManager = createJedisPoolManager();
                }
            }
        }
        return jedisPoolManager;
    }
    public static JedisPoolManager getShardedJedisPoolManager() {
        if (jedisPoolManager == null) {
            synchronized (JedisPoolManagerFactory.class) {
                if (jedisPoolManager == null) {
                    jedisPoolManager = createShardedJedisPoolManager();
                }
            }
        }
        return jedisPoolManager;
    }

    /**
     * 设置
     */
    public static void setJedisPoolManager(JedisPoolManager jpm) {
        if (jedisPoolManager == null) {
            synchronized (JedisPoolManagerFactory.class) {
                if (jedisPoolManager == null) {
                    jedisPoolManager = jpm;
                }
            }
        }
    }

    /**
     * 根据配置创建JedisPoolManager
     */
    private static JedisPoolManager createJedisPoolManager() {
        // 池基本配置
        JedisPoolConfig config = new JedisPoolConfig();
        List<JedisShardInfo> shards = new ArrayList<>();
        // 链接
        shards.add(new JedisShardInfo("127.0.0.1", 3988, 22222));
        // 构造池
        ShardedJedisPool shardedJedisPool = new ShardedJedisPool(config, shards);

        //return new JedisPoolManager(shardedJedisPool);
        return new JedisPoolManager();
    }
    /**
     * 根据配置创建ShardedJedisPoolManager
     */
    private static JedisPoolManager createShardedJedisPoolManager() {
        // 池基本配置
        JedisPoolConfig config = new JedisPoolConfig();
        List<JedisShardInfo> shards = new ArrayList<>();
        // 链接
        shards.add(new JedisShardInfo("127.0.0.1", 6379, 2000));
        // 构造池
        ShardedJedisPool shardedJedisPool = new ShardedJedisPool(config, shards);

        return new JedisPoolManager(shardedJedisPool);
    }
}
