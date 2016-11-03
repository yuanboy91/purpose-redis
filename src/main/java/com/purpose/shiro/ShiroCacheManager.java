package com.purpose.shiro;

import org.apache.shiro.cache.Cache;  
/**
 * 缓存处理接口
 * @author: Yuanbo
 * @date 2016年3月28日 下午2:26:10
 * @version V1.0
 */
public interface ShiroCacheManager {  
   
	<K, V> Cache<K, V> getCache(String name);    
        
    void destroy();
    
}