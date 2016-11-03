package com.purpose.shiro.impl;

import org.apache.shiro.cache.Cache;
import com.purpose.cache.RedisManager;
import com.purpose.shiro.ShiroCacheManager;
/**
 * 缓存处理实现类
 * @author: Yuanbo
 * @date 2016年3月28日 下午2:26:10
 * @version V1.0
 */
public class JedisShiroCacheManager implements ShiroCacheManager {

	private RedisManager redisManager;

	public RedisManager getRedisManager() {
		return redisManager;
	}

	public void setRedisManager(RedisManager redisManager) {
		this.redisManager = redisManager;
	}

	@Override
	public <K, V> Cache<K, V> getCache(String name) {
		return new JedisShiroCache<K, V>(redisManager, name);
	}

	@Override
	public void destroy() {
		redisManager.init();
		redisManager.flushDB();
	}

}