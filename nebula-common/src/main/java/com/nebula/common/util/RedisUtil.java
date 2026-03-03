package com.nebula.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * 封装常用的 Redis 操作，提供统一的异常处理和日志记录
 */
@Slf4j
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // ========================= String 操作 =========================

    /**
     * 设置值
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            LogUtil.Redis.set(log, key, -1);
            return true;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "set", key, e.getMessage());
            return false;
        }
    }

    /**
     * 设置值并指定过期时间（秒）
     */
    public boolean set(String key, Object value, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            LogUtil.Redis.set(log, key, ttlSeconds);
            return true;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "set", key, e.getMessage());
            return false;
        }
    }

    /**
     * 获取值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            LogUtil.Redis.get(log, key, value != null);
            return (T) value;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "get", key, e.getMessage());
            return null;
        }
    }

    /**
     * 获取并设置新值
     */
    @SuppressWarnings("unchecked")
    public <T> T getAndSet(String key, Object value) {
        try {
            Object oldValue = redisTemplate.opsForValue().getAndSet(key, value);
            log.debug("Redis getAndSet | key={}", key);
            return (T) oldValue;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "getAndSet", key, e.getMessage());
            return null;
        }
    }

    /**
     * 原子递增
     */
    public Long increment(String key, long delta) {
        try {
            Long value = redisTemplate.opsForValue().increment(key, delta);
            log.debug("Redis increment | key={}, delta={}", key, delta);
            return value;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "increment", key, e.getMessage());
            return null;
        }
    }

    /**
     * 原子递减
     */
    public Long decrement(String key, long delta) {
        try {
            Long value = redisTemplate.opsForValue().decrement(key, delta);
            log.debug("Redis decrement | key={}, delta={}", key, delta);
            return value;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "decrement", key, e.getMessage());
            return null;
        }
    }

    /**
     * 当 key 不存在时才设置值（SETNX）
     */
    public boolean setIfAbsent(String key, Object value, long ttlSeconds) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Redis setIfAbsent | key={}, ttl={}", key, ttlSeconds);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "setIfAbsent", key, e.getMessage());
            return false;
        }
    }

    /**
     * 当 key 存在时才设置值
     */
    public boolean setIfPresent(String key, Object value, long ttlSeconds) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfPresent(key, value, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Redis setIfPresent | key={}, ttl={}", key, ttlSeconds);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "setIfPresent", key, e.getMessage());
            return false;
        }
    }

    // ========================= Hash 操作 =========================

    /**
     * 设置 Hash 字段值
     */
    public boolean hSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            log.debug("Redis hSet | key={}, field={}", key, field);
            return true;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "hSet", key + ":" + field, e.getMessage());
            return false;
        }
    }

    /**
     * 批量设置 Hash 字段
     */
    public boolean hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            log.debug("Redis hSetAll | key={}, size={}", key, map.size());
            return true;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "hSetAll", key, e.getMessage());
            return false;
        }
    }

    /**
     * 获取 Hash 字段值
     */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String field) {
        try {
            Object value = redisTemplate.opsForHash().get(key, field);
            log.debug("Redis hGet | key={}, field={}", key, field);
            return (T) value;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "hGet", key + ":" + field, e.getMessage());
            return null;
        }
    }

    /**
     * 获取所有 Hash 字段
     */
    public Map<String, Object> hGetAll(String key) {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            Map<String, Object> result = new HashMap<>();
            entries.forEach((k, v) -> result.put(k.toString(), v));
            log.debug("Redis hGetAll | key={}, size={}", key, result.size());
            return result;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "hGetAll", key, e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * 删除 Hash 字段
     */
    public Long hDelete(String key, Object... fields) {
        try {
            Long count = redisTemplate.opsForHash().delete(key, fields);
            log.debug("Redis hDelete | key={}, fields={}", key, Arrays.toString(fields));
            return count;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "hDelete", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 判断 Hash 字段是否存在
     */
    public boolean hExists(String key, String field) {
        try {
            Boolean exists = redisTemplate.opsForHash().hasKey(key, field);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "hExists", key + ":" + field, e.getMessage());
            return false;
        }
    }

    /**
     * Hash 字段原子递增
     */
    public Long hIncrement(String key, String field, long delta) {
        try {
            Long value = redisTemplate.opsForHash().increment(key, field, delta);
            log.debug("Redis hIncrement | key={}, field={}, delta={}", key, field, delta);
            return value;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "hIncrement", key + ":" + field, e.getMessage());
            return null;
        }
    }

    // ========================= List 操作 =========================

    /**
     * 左入队（列表头部）
     */
    public Long lPush(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForList().leftPushAll(key, values);
            log.debug("Redis lPush | key={}, count={}", key, values.length);
            return count;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "lPush", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 右入队（列表尾部）
     */
    public Long rPush(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForList().rightPushAll(key, values);
            log.debug("Redis rPush | key={}, count={}", key, values.length);
            return count;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "rPush", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 左出队（从列表头部移除并返回）
     */
    @SuppressWarnings("unchecked")
    public <T> T lPop(String key) {
        try {
            Object value = redisTemplate.opsForList().leftPop(key);
            log.debug("Redis lPop | key={}", key);
            return (T) value;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "lPop", key, e.getMessage());
            return null;
        }
    }

    /**
     * 右出队（从列表尾部移除并返回）
     */
    @SuppressWarnings("unchecked")
    public <T> T rPop(String key) {
        try {
            Object value = redisTemplate.opsForList().rightPop(key);
            log.debug("Redis rPop | key={}", key);
            return (T) value;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "rPop", key, e.getMessage());
            return null;
        }
    }

    /**
     * 阻塞左出队（带超时时间）
     */
    @SuppressWarnings("unchecked")
    public <T> T bLPop(long timeoutSeconds, String... keys) {
        try {
            Object value = redisTemplate.opsForList().leftPop(keys[0], timeoutSeconds, TimeUnit.SECONDS);
            log.debug("Redis bLPop | keys={}, timeout={}", Arrays.toString(keys), timeoutSeconds);
            return (T) value;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "bLPop", Arrays.toString(keys), e.getMessage());
            return null;
        }
    }

    /**
     * 获取列表指定范围元素
     */
    public List<Object> lRange(String key, long start, long end) {
        try {
            List<Object> list = redisTemplate.opsForList().range(key, start, end);
            log.debug("Redis lRange | key={}, start={}, end={}", key, start, end);
            return list != null ? list : Collections.emptyList();
        } catch (Exception e) {
            LogUtil.Redis.error(log, "lRange", key, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 获取列表指定索引元素
     */
    @SuppressWarnings("unchecked")
    public <T> T lIndex(String key, long index) {
        try {
            Object value = redisTemplate.opsForList().index(key, index);
            log.debug("Redis lIndex | key={}, index={}", key, index);
            return (T) value;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "lIndex", key, e.getMessage());
            return null;
        }
    }

    /**
     * 获取列表长度
     */
    public Long lSize(String key) {
        try {
            Long size = redisTemplate.opsForList().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "lSize", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 移除列表指定元素
     */
    public Long lRemove(String key, long count, Object value) {
        try {
            Long removed = redisTemplate.opsForList().remove(key, count, value);
            log.debug("Redis lRemove | key={}, count={}", key, count);
            return removed != null ? removed : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "lRemove", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 修剪列表，保留指定范围
     */
    public boolean lTrim(String key, long start, long end) {
        try {
            redisTemplate.opsForList().trim(key, start, end);
            log.debug("Redis lTrim | key={}, start={}, end={}", key, start, end);
            return true;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "lTrim", key, e.getMessage());
            return false;
        }
    }

    // ========================= Set 操作 =========================

    /**
     * 添加成员到 Set
     */
    public Long sAdd(String key, Object... members) {
        try {
            Long count = redisTemplate.opsForSet().add(key, members);
            log.debug("Redis sAdd | key={}, count={}", key, members.length);
            return count != null ? count : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "sAdd", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 从 Set 移除成员
     */
    public Long sRemove(String key, Object... members) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, members);
            log.debug("Redis sRemove | key={}, count={}", key, members.length);
            return count != null ? count : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "sRemove", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 获取 Set 所有成员
     */
    public Set<Object> sMembers(String key) {
        try {
            Set<Object> members = redisTemplate.opsForSet().members(key);
            log.debug("Redis sMembers | key={}", key);
            return members != null ? members : Collections.emptySet();
        } catch (Exception e) {
            LogUtil.Redis.error(log, "sMembers", key, e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * 判断成员是否在 Set 中
     */
    public boolean sIsMember(String key, Object member) {
        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(key, member);
            return Boolean.TRUE.equals(isMember);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "sIsMember", key, e.getMessage());
            return false;
        }
    }

    /**
     * 获取 Set 大小
     */
    public Long sSize(String key) {
        try {
            Long size = redisTemplate.opsForSet().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "sSize", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 随机获取 Set 成员（不移除）
     */
    public List<Object> sRandomMembers(String key, long count) {
        try {
            List<Object> members = redisTemplate.opsForSet().randomMembers(key, count);
            return members != null ? members : Collections.emptyList();
        } catch (Exception e) {
            LogUtil.Redis.error(log, "sRandomMembers", key, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 随机获取并移除 Set 成员
     */
    @SuppressWarnings("unchecked")
    public <T> T sPop(String key) {
        try {
            Object member = redisTemplate.opsForSet().pop(key);
            return (T) member;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "sPop", key, e.getMessage());
            return null;
        }
    }

    /**
     * 获取两个 Set 的交集
     */
    public Set<Object> sIntersect(String key1, String key2) {
        try {
            Set<Object> intersect = redisTemplate.opsForSet().intersect(key1, key2);
            return intersect != null ? intersect : Collections.emptySet();
        } catch (Exception e) {
            LogUtil.Redis.error(log, "sIntersect", key1 + "&" + key2, e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * 获取多个 Set 的并集
     */
    public Set<Object> sUnion(String... keys) {
        try {
            if (keys.length < 2) return Collections.emptySet();
            List<String> keyList = Arrays.asList(keys);
            String firstKey = keyList.get(0);
            List<String> otherKeys = keyList.subList(1, keyList.size());
            Set<Object> union = redisTemplate.opsForSet().union(firstKey, otherKeys);
            return union != null ? union : Collections.emptySet();
        } catch (Exception e) {
            LogUtil.Redis.error(log, "sUnion", Arrays.toString(keys), e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * 获取两个 Set 的差集
     */
    public Set<Object> sDiff(String key1, String key2) {
        try {
            Set<Object> diff = redisTemplate.opsForSet().difference(key1, key2);
            return diff != null ? diff : Collections.emptySet();
        } catch (Exception e) {
            LogUtil.Redis.error(log, "sDiff", key1 + "-" + key2, e.getMessage());
            return Collections.emptySet();
        }
    }

    // ========================= ZSet 操作 =========================

    /**
     * 添加成员到 ZSet
     */
    public boolean zAdd(String key, Object member, double score) {
        try {
            Boolean result = redisTemplate.opsForZSet().add(key, member, score);
            log.debug("Redis zAdd | key={}, member={}, score={}", key, member, score);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zAdd", key, e.getMessage());
            return false;
        }
    }

    /**
     * 批量添加成员到 ZSet
     */
    public Long zAddAll(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        try {
            Long count = redisTemplate.opsForZSet().add(key, tuples);
            log.debug("Redis zAddAll | key={}, count={}", key, tuples.size());
            return count != null ? count : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zAddAll", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 获取 ZSet 指定范围成员（按分数升序）
     */
    public Set<Object> zRange(String key, long start, long end) {
        try {
            Set<Object> set = redisTemplate.opsForZSet().range(key, start, end);
            return set != null ? set : Collections.emptySet();
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zRange", key, e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * 获取 ZSet 指定分数范围成员（按分数升序）
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        try {
            Set<Object> set = redisTemplate.opsForZSet().rangeByScore(key, min, max);
            return set != null ? set : Collections.emptySet();
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zRangeByScore", key, e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * 获取 ZSet 成员排名（按分数升序，从0开始）
     */
    public Long zRank(String key, Object member) {
        try {
            Long rank = redisTemplate.opsForZSet().rank(key, member);
            return rank != null ? rank : -1L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zRank", key, e.getMessage());
            return -1L;
        }
    }

    /**
     * 获取 ZSet 成员分数
     */
    public Double zScore(String key, Object member) {
        try {
            Double score = redisTemplate.opsForZSet().score(key, member);
            return score != null ? score : 0.0;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zScore", key, e.getMessage());
            return 0.0;
        }
    }

    /**
     * 移除 ZSet 成员
     */
    public Long zRemove(String key, Object... members) {
        try {
            Long count = redisTemplate.opsForZSet().remove(key, members);
            log.debug("Redis zRemove | key={}, count={}", key, members.length);
            return count != null ? count : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zRemove", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 按分数范围移除 ZSet 成员
     */
    public Long zRemoveByScore(String key, double min, double max) {
        try {
            Long count = redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
            log.debug("Redis zRemoveByScore | key={}, min={}, max={}", key, min, max);
            return count != null ? count : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zRemoveByScore", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 获取 ZSet 大小
     */
    public Long zSize(String key) {
        try {
            Long size = redisTemplate.opsForZSet().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zSize", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 获取 ZSet 指定分数范围成员数量
     */
    public Long zCount(String key, double min, double max) {
        try {
            Long count = redisTemplate.opsForZSet().count(key, min, max);
            return count != null ? count : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zCount", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 获取 ZSet 指定范围成员（按分数降序）
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        try {
            Set<Object> set = redisTemplate.opsForZSet().reverseRange(key, start, end);
            return set != null ? set : Collections.emptySet();
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zReverseRange", key, e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * ZSet 成员分数原子递增
     */
    public Double zIncrement(String key, Object member, double delta) {
        try {
            Double score = redisTemplate.opsForZSet().incrementScore(key, member, delta);
            log.debug("Redis zIncrement | key={}, member={}, delta={}", key, member, delta);
            return score != null ? score : 0.0;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "zIncrement", key, e.getMessage());
            return 0.0;
        }
    }

    // ========================= 通用操作 =========================

    /**
     * 删除 Key
     */
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            LogUtil.Redis.delete(log, key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "delete", key, e.getMessage());
            return false;
        }
    }

    /**
     * 批量删除 Key
     */
    public Long delete(Collection<String> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            log.debug("Redis delete | keys={}, count={}", keys, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "deleteBatch", keys.toString(), e.getMessage());
            return 0L;
        }
    }

    /**
     * 判断 Key 是否存在
     */
    public boolean hasKey(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "hasKey", key, e.getMessage());
            return false;
        }
    }

    /**
     * 设置过期时间（秒）
     */
    public boolean expire(String key, long ttlSeconds) {
        try {
            Boolean result = redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Redis expire | key={}, ttl={}", key, ttlSeconds);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "expire", key, e.getMessage());
            return false;
        }
    }

    /**
     * 获取过期时间（秒）
     */
    public Long getExpire(String key) {
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl != null ? ttl : -2L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "getExpire", key, e.getMessage());
            return -2L;
        }
    }

    /**
     * 移除过期时间（持久化）
     */
    public boolean persist(String key) {
        try {
            Boolean result = redisTemplate.persist(key);
            log.debug("Redis persist | key={}", key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "persist", key, e.getMessage());
            return false;
        }
    }

    /**
     * 获取 Key 的数据类型
     */
    public DataType type(String key) {
        try {
            return redisTemplate.type(key);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "type", key, e.getMessage());
            return DataType.NONE;
        }
    }

    /**
     * 扫描匹配指定模式的 Key
     */
    public Set<String> scan(String pattern, long count) {
        try {
            Set<String> keys = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions()
                    .match(pattern)
                    .count(count)
                    .build();
            Cursor<String> cursor = redisTemplate.scan(options);
            cursor.forEachRemaining(keys::add);
            cursor.close();
            log.debug("Redis scan | pattern={}, count={}", pattern, keys.size());
            return keys;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "scan", pattern, e.getMessage());
            return Collections.emptySet();
        }
    }

    // ========================= 批量操作 =========================

    /**
     * 批量获取
     */
    public List<Object> multiGet(List<String> keys) {
        try {
            List<Object> values = redisTemplate.opsForValue().multiGet(keys);
            log.debug("Redis multiGet | keys={}, count={}", keys, values != null ? values.size() : 0);
            return values != null ? values : Collections.emptyList();
        } catch (Exception e) {
            LogUtil.Redis.error(log, "multiGet", keys.toString(), e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 批量设置
     */
    public boolean multiSet(Map<String, Object> map) {
        try {
            redisTemplate.opsForValue().multiSet(map);
            log.debug("Redis multiSet | count={}", map.size());
            return true;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "multiSet", "map size:" + map.size(), e.getMessage());
            return false;
        }
    }

    /**
     * 批量设置（仅当 key 不存在时）
     */
    public boolean multiSetIfAbsent(Map<String, Object> map) {
        try {
            Boolean result = redisTemplate.opsForValue().multiSetIfAbsent(map);
            log.debug("Redis multiSetIfAbsent | count={}", map.size());
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "multiSetIfAbsent", "map size:" + map.size(), e.getMessage());
            return false;
        }
    }

    /**
     * 按模式删除 Key
     */
    public Long deleteByPattern(String pattern) {
        try {
            Set<String> keys = scan(pattern, 1000);
            if (!keys.isEmpty()) {
                Long count = redisTemplate.delete(keys);
                log.debug("Redis deleteByPattern | pattern={}, count={}", pattern, count);
                return count != null ? count : 0L;
            }
            return 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "deleteByPattern", pattern, e.getMessage());
            return 0L;
        }
    }

    // ========================= 分布式锁 =========================

    /**
     * 尝试获取分布式锁
     *
     * @param key       锁的 key
     * @param value     锁的值（用于解锁时验证）
     * @param ttlSeconds 锁的过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String key, String value, long ttlSeconds) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Redis tryLock | key={}, ttl={}", key, ttlSeconds);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "tryLock", key, e.getMessage());
            return false;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param key   锁的 key
     * @param value 锁的值（用于验证）
     * @return 是否释放成功
     */
    public boolean unlock(String key, String value) {
        try {
            Object currentValue = redisTemplate.opsForValue().get(key);
            if (currentValue != null && currentValue.toString().equals(value)) {
                Boolean result = redisTemplate.delete(key);
                log.debug("Redis unlock | key={}", key);
                return Boolean.TRUE.equals(result);
            }
            return false;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "unlock", key, e.getMessage());
            return false;
        }
    }

    /**
     * 检查分布式锁是否被持有
     */
    public boolean isLocked(String key) {
        return hasKey(key);
    }

    /**
     * 获取锁的剩余过期时间（秒）
     */
    public Long getLockTtl(String key) {
        return getExpire(key);
    }

    // ========================= 数据库操作 =========================

    /**
     * 清空当前数据库
     */
    public boolean flushDb() {
        try {
            redisTemplate.getConnectionFactory()
                    .getConnection()
                    .flushDb();
            log.warn("Redis flushDb executed");
            return true;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "flushDb", "database", e.getMessage());
            return false;
        }
    }

    /**
     * 获取当前数据库 Key 数量
     */
    public Long dbSize() {
        try {
            Long size = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .dbSize();
            return size != null ? size : 0L;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "dbSize", "database", e.getMessage());
            return 0L;
        }
    }
}
