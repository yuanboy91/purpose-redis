package com.purpose.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import com.purpose.shiro.ShiroCacheManager;
/**
 * 自定义缓存处理类
 * @author: Yuanbo
 * @date 2016年3月28日 下午2:26:10
 * @version V1.0
 */
public class CustomShiroCacheManager implements CacheManager, Destroyable {

	private ShiroCacheManager shrioCacheManager;

	public ShiroCacheManager getShrioCacheManager() {
		return shrioCacheManager;
	}

	public void setShrioCacheManager(ShiroCacheManager shrioCacheManager) {
		this.shrioCacheManager = shrioCacheManager;
	}

	@Override
	public void destroy() throws Exception {
		getShrioCacheManager().destroy();
	}

	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		return getShrioCacheManager().getCache(name);
	}

}
