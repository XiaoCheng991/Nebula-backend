package com.nebula.common.util;

import com.nebula.common.constant.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * RedisUtil 使用示例
 * 演示如何使用 RedisUtil 进行各种 Redis 操作
 *
 * 注意：这是一个示例类，实际使用时应该根据业务需求调整
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUsageExample {

    private final RedisUtil redisUtil;

    /**
     * String 操作示例
     */
    public void stringOperations() {
        // 设置值（无过期时间）
        redisUtil.set("key1", "value1");

        // 设置值（带过期时间，单位：秒）
        redisUtil.set("key2", "value2", 300);  // 5分钟过期

        // 获取值
        String value = redisUtil.get("key1");

        // 获取并设置新值（返回旧值）
        String oldValue = redisUtil.getAndSet("key1", "newValue");

        // 原子递增（常用于计数器）
        Long count = redisUtil.increment("counter", 1);

        // 原子递减
        Long decremented = redisUtil.decrement("counter", 1);

        // 条件设置（仅当 key 不存在时才设置）
        boolean setIfAbsent = redisUtil.setIfAbsent("lock:key", "locked", 60);

        // 条件设置（仅当 key 存在时才设置）
        boolean setIfPresent = redisUtil.setIfPresent("key1", "updated", 300);
    }

    /**
     * Hash 操作示例
     */
    public void hashOperations() {
        String key = "user:1";

        // 设置单个字段
        redisUtil.hSet(key, "name", "张三");
        redisUtil.hSet(key, "age", 25);
        redisUtil.hSet(key, "email", "zhangsan@example.com");

        // 批量设置字段
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("city", "北京");
        userInfo.put("country", "中国");
        redisUtil.hSetAll(key, userInfo);

        // 获取单个字段
        String name = redisUtil.hGet(key, "name");
        Integer age = redisUtil.hGet(key, "age");

        // 获取所有字段
        Map<String, Object> allFields = redisUtil.hGetAll(key);

        // 删除字段
        Long deletedCount = redisUtil.hDelete(key, "tempField", "oldField");

        // 判断字段是否存在
        boolean exists = redisUtil.hExists(key, "name");

        // 字段原子递增
        Long newAge = redisUtil.hIncrement(key, "visitCount", 1);
    }

    /**
     * List 操作示例
     */
    public void listOperations() {
        String key = "queue:tasks";

        // 左入队（列表头部）
        redisUtil.lPush(key, "task1", "task2");

        // 右入队（列表尾部）
        redisUtil.rPush(key, "task3", "task4");

        // 左出队（从头部移除并返回）
        Object firstTask = redisUtil.lPop(key);

        // 右出队（从尾部移除并返回）
        Object lastTask = redisUtil.rPop(key);

        // 阻塞左出队（带超时时间，单位：秒）
        Object task = redisUtil.bLPop(5, key);

        // 获取列表指定范围元素（0 到 -1 表示获取全部）
        List<Object> allTasks = redisUtil.lRange(key, 0, -1);

        // 获取列表指定索引元素
        Object thirdTask = redisUtil.lIndex(key, 2);

        // 获取列表长度
        Long queueSize = redisUtil.lSize(key);

        // 移除列表指定元素（count > 0 从头部开始，count < 0 从尾部开始，count = 0 移除所有）
        Long removed = redisUtil.lRemove(key, 1, "doneTask");

        // 修剪列表，保留指定范围
        redisUtil.lTrim(key, 0, 99);  // 只保留前 100 个元素
    }

    /**
     * Set 操作示例
     */
    public void setOperations() {
        String key = "set:tags";

        // 添加成员
        redisUtil.sAdd(key, "java", "spring", "redis");

        // 移除成员
        redisUtil.sRemove(key, "oldTag");

        // 获取所有成员
        Set<Object> tags = redisUtil.sMembers(key);

        // 判断成员是否存在
        boolean isMember = redisUtil.sIsMember(key, "java");

        // 获取 Set 大小
        Long tagCount = redisUtil.sSize(key);

        // 随机获取成员（不移除）
        List<Object> randomTags = redisUtil.sRandomMembers(key, 3);

        // 随机获取并移除成员
        Object randomTag = redisUtil.sPop(key);

        // Set 运算：交集
        Set<Object> intersection = redisUtil.sIntersect("set:java", "set:spring");

        // Set 运算：并集
        Set<Object> union = redisUtil.sUnion("set:a", "set:b", "set:c");

        // Set 运算：差集
        Set<Object> diff = redisUtil.sDiff("set:all", "set:processed");
    }

    /**
     * ZSet (Sorted Set) 操作示例
     */
    public void zSetOperations() {
        String key = "zset:ranking";

        // 添加成员（带分数）
        redisUtil.zAdd(key, "player1", 100.0);
        redisUtil.zAdd(key, "player2", 150.0);
        redisUtil.zAdd(key, "player3", 80.0);

        // 批量添加
        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
        tuples.add(new DefaultTypedTuple<>("player4", 200.0));
        tuples.add(new DefaultTypedTuple<>("player5", 120.0));
        redisUtil.zAddAll(key, tuples);

        // 获取指定范围成员（按分数升序，从0开始）
        Set<Object> top3 = redisUtil.zRange(key, 0, 2);

        // 获取指定分数范围成员
        Set<Object> midRange = redisUtil.zRangeByScore(key, 100.0, 150.0);

        // 获取成员排名（按分数升序，从0开始）
        Long rank = redisUtil.zRank(key, "player1");

        // 获取成员分数
        Double score = redisUtil.zScore(key, "player1");

        // 移除成员
        redisUtil.zRemove(key, "player1", "player2");

        // 按分数范围移除
        redisUtil.zRemoveByScore(key, 0.0, 50.0);

        // 获取 ZSet 大小
        Long totalPlayers = redisUtil.zSize(key);

        // 获取指定分数范围成员数量
        Long qualifiedCount = redisUtil.zCount(key, 100.0, 999999.0);
    }

    /**
     * 分布式锁示例
     */
    public void distributedLockExample() {
        String lockKey = "lock:order:123";
        String lockValue = UUID.randomUUID().toString();
        long lockTtl = 30;  // 锁的过期时间（秒）

        try {
            // 尝试获取锁
            boolean locked = redisUtil.tryLock(lockKey, lockValue, lockTtl);

            if (locked) {
                try {
                    // 执行业务逻辑
                    log.info("获取锁成功，执行业务逻辑...");

                    // 模拟业务处理
                    Thread.sleep(1000);

                } finally {
                    // 释放锁
                    boolean unlocked = redisUtil.unlock(lockKey, lockValue);
                    log.info("释放锁结果: {}", unlocked);
                }
            } else {
                log.warn("获取锁失败，其他线程正在处理...");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("业务处理被中断", e);
        }
    }

    /**
     * 批量操作示例
     */
    public void batchOperations() {
        // 批量获取
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        List<Object> values = redisUtil.multiGet(keys);

        // 批量设置
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        redisUtil.multiSet(map);

        // 批量设置（仅当 key 不存在时）
        redisUtil.multiSetIfAbsent(map);

        // 按模式删除
        Long deletedCount = redisUtil.deleteByPattern("temp:*");
        log.info("删除了 {} 个临时key", deletedCount);
    }

    /**
     * 通用操作示例
     */
    public void generalOperations() {
        String key = "test:key";

        // 检查 key 是否存在
        boolean exists = redisUtil.hasKey(key);

        // 设置过期时间（秒）
        redisUtil.expire(key, 300);

        // 获取剩余过期时间（秒）
        Long ttl = redisUtil.getExpire(key);

        // 移除过期时间（持久化）
        redisUtil.persist(key);

        // 获取 key 的数据类型
        var type = redisUtil.type(key);

        // 扫描匹配指定模式的 key
        Set<String> keys = redisUtil.scan("user:*", 100);
    }

    // ==================== 实际应用场景示例 ====================

    /**
     * 用户登录次数限制（防暴力破解）
     */
    public boolean checkLoginAttempts(String email) {
        String key = RedisKey.RateLimit.login(email);

        // 获取当前尝试次数
        Long attempts = redisUtil.get(key);

        if (attempts == null) {
            // 第一次尝试，设置初始值为 1，过期时间为 15 分钟
            redisUtil.set(key, 1, 15 * 60);
            return true;
        }

        if (attempts >= 5) {
            // 超过最大尝试次数
            return false;
        }

        // 增加尝试次数
        redisUtil.increment(key, 1);
        return true;
    }

    /**
     * 缓存用户信息（带过期时间）
     */
    public void cacheUserInfo(Long userId, Map<String, Object> userInfo) {
        String key = RedisKey.User.userInfo(userId);

        // 使用 Hash 存储用户信息
        redisUtil.hSetAll(key, userInfo);

        // 设置过期时间为 1 小时
        redisUtil.expire(key, RedisKey.User.INFO_TTL);
    }

    /**
     * 获取缓存的用户信息
     */
    public Map<String, Object> getCachedUserInfo(Long userId) {
        String key = RedisKey.User.userInfo(userId);
        return redisUtil.hGetAll(key);
    }

    /**
     * 记录用户在线状态（使用 Set）
     */
    public void markUserOnline(Long userId) {
        String key = "online:users";
        redisUtil.sAdd(key, userId.toString());
    }

    /**
     * 获取在线用户数量
     */
    public Long getOnlineUserCount() {
        String key = "online:users";
        return redisUtil.sSize(key);
    }

    /**
     * 文章阅读量排行榜（使用 ZSet）
     */
    public void incrementArticleView(Long articleId) {
        String key = "ranking:article:views";
        redisUtil.zIncrement(key, articleId.toString(), 1);
    }

    /**
     * 获取阅读量 Top 10 的文章
     */
    public Set<Object> getTop10Articles() {
        String key = "ranking:article:views";
        return redisUtil.zReverseRange(key, 0, 9);
    }

    // 内部辅助类，用于 ZSet 批量添加
    private static class DefaultTypedTuple<V> implements ZSetOperations.TypedTuple<V> {
        private final V value;
        private final Double score;

        public DefaultTypedTuple(V value, Double score) {
            this.value = value;
            this.score = score;
        }

        @Override
        public int compareTo(ZSetOperations.TypedTuple<V> o) {
            return Double.compare(this.score, o.getScore());
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public Double getScore() {
            return score;
        }
    }
}
