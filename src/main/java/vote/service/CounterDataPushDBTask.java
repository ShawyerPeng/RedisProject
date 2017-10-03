package vote.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import util.JedisPoolManager;

import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

public class CounterDataPushDBTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CounterDataPushDBTask.class);

    /**
     * jedis 客户端
     */
    private JedisPoolManager jedisPoolManager;
    private RedisTemplate redisTemplate;

    /**
     * 计数器列表
     */
    private static CopyOnWriteArrayList<ICounter> counters = new CopyOnWriteArrayList();

    /**
     * 加入计数器
     */
    public static void addCounter(ICounter counter) {
        counters.add(counter);
    }

    /**
     * 手动保存计数器
     */
    public static void saveCounter() {
        for (ICounter counter : counters) {
            counter.updates();
        }
    }

    /**
     * 同步数据，每 5 分钟执行一次
     */
    @Scheduled(cron = "0 0/5 *  * * ? ")
    public void syncData() {
        for (ICounter counter : counters) {
            counter.updates();
        }
    }

    /**
     * 删除空 key
     */
    public void delNullKey() {
        for (ICounter counter : counters) {
            Enumeration<String> keys = counter.counterKeys();
            while (keys.hasMoreElements()){
                String val = jedisPoolManager.getStringValue(keys.nextElement());
                if(val != null && val.equals("0")){
                    jedisPoolManager.del(keys.nextElement());
                }
            }
        }
    }
}
