package com.zcloud.alone.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Title : Redis管理
 * Description : 直接操作redis
 *
 * @Author dzm
 */
public class RedisUtils {

	private static RedisTemplate<Serializable, Object> redisTemplate = SpringUtil.getBean("redisTemplate");

	/**
	 * 默认过期时长 1天，单位：秒
	 */
	public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

	/**
	 * 不设置过期时长
	 */
	public static final long NOT_EXPIRE = -1;

	public static Set<Serializable> keys(final String key) {
		return redisTemplate.keys(key);
	}

	/**
	 * 判断缓存中是否有对应的key
	 *
	 * @param key
	 * @return
	 */
	public static boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
	 *
	 * @param oldKey
	 * @param newKey
	 */
	public static void renameKey(String oldKey, String newKey) {
		redisTemplate.rename(oldKey, newKey);
	}

	/**
	 * newKey不存在时才重命名
	 *
	 * @param oldKey
	 * @param newKey
	 * @return 修改成功返回true
	 */
	public static boolean renameKeyNotExist(String oldKey, String newKey) {
		return redisTemplate.renameIfAbsent(oldKey, newKey);
	}

	/**
	 * 删除对应的key
	 *
	 * @param key
	 */
	public static void remove(final String key) {
		if (exists(key)) {
			redisTemplate.delete(key);
		}
	}

	/**
	 * 批量删除对应的key
	 *
	 * @param keys
	 */
	public static void remove(final String... keys) {
		Set<Serializable> kSet = Stream.of(keys).collect(Collectors.toSet());
		redisTemplate.delete(kSet);
	}

	/**
	 * @param pattern 正则表达式
	 * @return java.lang.Integer
	 * @author Monster
	 * @Date 2020-10-13 11:07
	 * @Description 根据正则表达式查询组内的数量
	 **/
	public static Integer getKeysSizeCountInGroup(final String pattern) {
		Set<Serializable> keys = redisTemplate.keys(pattern);
		return keys.size();
	}

	/**
	 * 根据正则表达式查询组内的数据
	 *
	 * @param pattern
	 * @return java.util.Set<java.io.Serializable>
	 * @author Monster
	 * @Date 2020-10-27 19:26
	 */
	public static Set<Serializable> getKeysCountInGroup(final String pattern) {
		Set<Serializable> keys = redisTemplate.keys(pattern);
		return keys;
	}

	/**
	 * 根据正则表达式批量删除key
	 *
	 * @param pattern
	 */
	public static void removePattern(final String pattern) {
		Set<Serializable> keys = redisTemplate.keys(pattern);
		if (keys.size() > 0) {
			redisTemplate.delete(keys);
		}
	}

	/**
	 * 设置key的生命周期
	 *
	 * @param key
	 * @param time
	 * @param timeUnit
	 */
	public static void expireKey(String key, long time, TimeUnit timeUnit) {
		redisTemplate.expire(key, time, timeUnit);
	}

	/**
	 * 指定key在指定的日期过期
	 *
	 * @param key
	 * @param date
	 */
	public static void expireKeyAt(String key, Date date) {
		redisTemplate.expireAt(key, date);
	}

	/**
	 * 查询key的生命周期
	 *
	 * @param key
	 * @param timeUnit
	 * @return
	 */
	public static long getKeyExpire(String key, TimeUnit timeUnit) {
		return redisTemplate.getExpire(key, timeUnit);
	}

	/**
	 * 将key设置为永久有效
	 *
	 * @param key
	 */
	public static void persistKey(String key) {
		redisTemplate.persist(key);
	}

	/**
	 * 写入缓存(字符串)
	 * @param key
	 * @param value
	 * @return
	 */
	public static void setString(final String key, Object value) {
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		operations.set(key, value);
	}

	/**
	 * 写入缓存（字符串）并设置过期时间
	 * @param key
	 * @param value
	 * @return
	 */
	public static void setString(final String key, Object value, Long expireTime) {
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		operations.set(key, value);
		expireKey(key, expireTime, TimeUnit.SECONDS);
	}

	/**
	 * 读取缓存(字符串)
	 * @param key
	 * @return
	 */
	public static Object getString(final String key) {
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		return operations.get(key);
	}

	/**
	 * 写入缓存(list)
	 * @param key
	 * @param value
	 * @return
	 */
	public static void setList(final String key, Object value) {
		ListOperations<Serializable, Object> operations = redisTemplate.opsForList();
		operations.leftPush(key, value);
	}

	/**
	 * 读取缓存(list)
	 * @param key
	 * @return
	 */
	public static Object getList(final String key, long index) {
		ListOperations<Serializable, Object> operations = redisTemplate.opsForList();
		return operations.index(key, index);
	}

	/**
	 * 写入缓存(hash)
	 * @param key
	 * @param hKey
	 * @param hValue
	 * @return
	 */
	public static void setHash(final String key, String hKey, Object hValue) {
		HashOperations<Serializable, String, Object> operations = redisTemplate.opsForHash();
		operations.put(key, hKey, hValue);
	}

	/**
	 * 读取缓存(hash)
	 * @param key
	 * @param hKey
	 * @return
	 */
	public static Object getHash(final String key, String hKey) {
		HashOperations<Serializable, String, Object> operations = redisTemplate.opsForHash();
		return operations.get(key, hKey);
	}

	/**
	 * 读取缓存(hash)的所有的key
	 * @param key
	 * @return
	 */
	public static Set<String> getKeys(final String key) {
		HashOperations<Serializable, String, Object> operations = redisTemplate.opsForHash();
		return operations.keys(key);
	}

	/**
	 * 删除缓存(hash)
	 * @param key
	 * @param hKey
	 * @return
	 */
	public static void removeHash(final String key, String hKey) {
		HashOperations<Serializable, String, Object> operations = redisTemplate.opsForHash();
		operations.delete(key, hKey);
	}

	/**
	 * 写入缓存(set)
	 * @param key
	 * @param value
	 * @return
	 */
	public static void setSet(final String key, Object... value) {
		SetOperations<Serializable, Object> operations = redisTemplate.opsForSet();
		operations.add(key, value);
	}

	/**
	 * 读取缓存(set)
	 * @param key
	 * @return
	 */
	public static Object getSet(final String key) {
		SetOperations<Serializable, Object> operations = redisTemplate.opsForSet();
		return operations.pop(key);
	}

	/**
	 * 自增
	 * @param key
	 * @param liveTime
	 * @return
	 */
	public static Long incr(String key, long liveTime) {
		RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
		Long increment = entityIdCounter.getAndIncrement();
		//初始设置过期时间
		boolean incrementIsNull = null == increment || increment.longValue() == 0;
		if (incrementIsNull && liveTime > 0) {
			entityIdCounter.expire(liveTime, TimeUnit.DAYS);
		}
		return increment;
	}
}
