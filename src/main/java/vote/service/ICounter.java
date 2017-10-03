package vote.service;

import java.util.Enumeration;

/**
 * 通用计数器接口
 */
public interface ICounter {
    /**
     * 计数器中 field 计数 + 1 并返回
     */
    long incr(String field);

    /**
     * 批量更新 pv
     */
    void updates();

    /**
     * 单个更新 pv
     */
    void update(String field);

    /**
     * 获取 keys
     */
    Enumeration<String> counterKeys();
}
