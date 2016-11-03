package com.purpose.shiro.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import com.purpose.cache.RedisManager;
import com.purpose.utils.SerializeUtils;
/**
 * 获取缓存类
 * @author: Yuanbo
 * @date 2016年3月28日 下午2:26:10
 * @version V1.0
 */
public class JedisShiroCache<K, V> implements Cache<K, V> {
	
	private final String REDIS_SHIRO_CACHE = "shiro-cache";

	private RedisManager redisManager;

	private String name;

	public JedisShiroCache(RedisManager redisManager, String name) {
		this.redisManager = redisManager;
		this.name = name;
	}

	public String getName() {
		if (name == null) {
			return "";
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void clear() throws CacheException {
		redisManager.init();
		String keysPattern = this.REDIS_SHIRO_CACHE + "*";
		redisManager.flushDB();
	}

	@Override
	public V get(K key) throws CacheException {
		redisManager.init();
		byte[] byteKey = getCacheKey(key).getBytes();
		byte[] byteValue = redisManager.get(byteKey);
		if (null == byteValue)
			return null;
		return (V) SerializeUtils.deserialize(byteValue);
	}

	@Override
	public Set<K> keys() {
		redisManager.init();
		Set<byte[]> byteSet = redisManager.keys(this.REDIS_SHIRO_CACHE + "*");
		Set<K> keys = new HashSet<K>();
		for (byte[] bs : byteSet) {
			keys.add((K) SerializeUtils.deserialize(bs));
		}
		return keys;
	}

	@Override
	public V put(K key, V value) throws CacheException {
		redisManager.init();
		V previos = get(key);
		redisManager.set(getCacheKey(key).getBytes(), SerializeUtils.serialize(value),1800);
		return previos;
	}

	@Override
	public V remove(K key) throws CacheException {
		redisManager.init();
		V previos = get(key);
		redisManager.del(getCacheKey(key).getBytes());
		return previos;
	}

	@Override
	public int size() {
		redisManager.init();
		if (keys() == null)
			return 0;
		return keys().size();
	}

	@Override
	public Collection<V> values() {
		Set<byte[]> byteSet = redisManager.keys(this.REDIS_SHIRO_CACHE + "*");
		List<V> result = new LinkedList<V>();
		for (byte[] bs : byteSet) {
			result.add((V) SerializeUtils.deserialize(redisManager.get(bs)));
		}
		return result;
	}

	private String getCacheKey(Object key) {
		return this.REDIS_SHIRO_CACHE + getName() + ":" + key;
	}
	
}