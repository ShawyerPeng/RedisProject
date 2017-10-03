package ranking.util;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StringRedisUtil {
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }
    public Object get(String key, long start, long end) {
        return key == null ? null : redisTemplate.opsForValue().get(key, start, end);
    }
    /**
     * 放入
     *
     * @param key   键
     * @param value 值
     * @return true 成功 false 失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 放入并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间 (秒) time 要大于 0 如果 time 小于等于 0 将设置无限期
     * @param timeUnit 时间单位
     * @return true 成功 false 失败
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     *
     * @param key
     * @param value
     * @return
     */
    public Object getAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    public <T> List<T> multiGet(List<String> keys, Class<T> clazz) {
        List<T> values = (List<T>) redisTemplate.opsForValue().multiGet(keys);
        return values;
    }




    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几 (大于 0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于 0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
        
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几 (小于 0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于 0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }


}
