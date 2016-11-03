package com.purpose.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.purpose.utils.SerializeUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * 通过jedis管理redis内存数据库
 * @author: Yuanbo
 * @date 2016年3月28日 下午2:26:10
 * @version V1.0
 */
public class RedisManager {

	private static final Logger logger = LoggerFactory.getLogger(RedisManager.class);
	//下面属性都定义在了properties文件中，这里通过spring的注解方式来直接使用
	@Value("${redis.host}")
	private String host;
	@Value("${redis.port}")
	private int port;
	@Value("${redis.pass}")
	private String pass;
	@Value("${redis.maxActive}")
	private int maxActive;
	@Value("${redis.maxIdle}")
	private int maxIdle;
	@Value("${redis.maxWait}")
	private int maxWait;
	@Value("${redis.testOnBorrow}")
	private boolean testOnBorrow;
	@Value("${redis.testOnReturn}")
	private boolean testOnReturn;

	//设置为0的话就是永远都不会过期
	private int expire = 0;
	//setting session or cache's database
	private int index = 1;
	//setting store's database
	private int store = 0;
	//定义一个管理池，所有的redisManager共同使用。
	private JedisPool jedisPool = null;

	/**
	 * 初始化方法,在这个方法中通过host和port来初始化jedispool。
	 */
	public void init() {
		if (null == host || 0 == port) {
			logger.info("请初始化redis配置文件");
			throw new NullPointerException("找不到redis配置文件");
		}
		if (jedisPool == null) {
			JedisPoolConfig poolConfig = new JedisPoolConfig();
			poolConfig.setMaxActive(maxActive);
			poolConfig.setMaxIdle(maxIdle);
			poolConfig.setMaxWait(maxWait);
			poolConfig.setTestOnBorrow(testOnBorrow);
			poolConfig.setTestOnReturn(testOnReturn);
			jedisPool = new JedisPool(poolConfig, host, port);
		}
	}

	/**
	 * get value from redis
	 * @param key
	 * @return
	 */
	public byte[] get(byte[] key) {
		byte[] value = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			value = jedis.get(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * get value from redis
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String value = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			value = jedis.get(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * set value
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] set(byte[] key, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			jedis.set(key, value);
			if (this.expire != 0) {
				jedis.expire(key, this.expire);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * set value
	 * @param key
	 * @param value
	 * @return
	 */
	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			jedis.set(key, value);
			if (this.expire != 0) {
				jedis.expire(key, this.expire);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * set value
	 * @param key
	 * @param value
	 * @param expire
	 * @return
	 */
	public byte[] set(byte[] key, byte[] value, int expire) {
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			jedis.set(key, value);
			if (expire != 0) {
				jedis.expire(key, expire);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * set value
	 * @param key
	 * @param value
	 * @param expire
	 * @return
	 */
	public String set(String key, String value, int expire) {
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			jedis.set(key, value);
			if (expire != 0) {
				jedis.expire(key, expire);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * del value
	 * @param key
	 */
	public void del(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			jedis.del(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * del value
	 * @param key
	 */
	public void del(String key) {
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			jedis.del(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * flush value
	 */
	public void flushDB() {
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			jedis.flushDB();
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * size
	 */
	public Long dbSize() {
		Long dbSize = 0L;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			dbSize = jedis.dbSize();
		} finally {
			jedisPool.returnResource(jedis);
		}
		return dbSize;
	}

	/**
	 * keys
	 * @param regex
	 * @return
	 */
	public Set<byte[]> keys(String pattern) {
		Set<byte[]> keys = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			//logger.info("调用keys方法--------Shiro");
			keys = jedis.keys(pattern.getBytes());
			if (null != keys)
				System.out.println(keys.size());
		} finally {
			jedisPool.returnResource(jedis);
		}
		return keys;
	}

	public void dels(String pattern) {
		Set<byte[]> keys = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(index);
		try {
			//logger.info("调用dels方法--------Shiro");
			keys = jedis.keys(pattern.getBytes());
			Iterator<byte[]> ito = keys.iterator();
			while (ito.hasNext()) {
				jedis.del(ito.next());
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public RedisManager() {  
    }  
	
	/*********************add by 2016.10.22******************/
	
	/**
	 * 判断键是否存在
	 * @param key
	 * @return
	 */
	public Boolean xlExists(String key) {
		boolean result = false;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			result = jedis.exists(key.getBytes());
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 键在某段时间后失效
	 * @param key
	 * @param seconds
	 * @return
	 */
	public Long xlExpire(String key, int seconds) {
		Long result = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			result = jedis.expire(key.getBytes(), seconds);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 通过键删除值
	 * @param key
	 */
	public Long xlDel(String key) {
		Long result = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			result = jedis.del(key.getBytes());
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 设置单个值
	 * @param key
	 * @param value
	 * @return
	 */
	public String xlSet(String key, Object value) {
		String result = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			result = jedis.set(key.getBytes(), SerializeUtils.serialize(value));
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 获取单个值
	 * @param key
	 * @return
	 */
	public Object xlGet(String key) {
		Object result = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			result = SerializeUtils.deserialize((byte[])jedis.get(key.getBytes()));
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 设置Map类型的值
	 * @param key
	 * @param value
	 * @return
	 */
	public String xlSetMap(String key, Map<String, Object> value) {
		String result = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			Map<byte[], byte[]> byteMap = new HashMap<byte[], byte[]>();
			for (Map.Entry tempValue : value.entrySet()) {
				String tmepKey = (String) tempValue.getKey();
				byteMap.put(tmepKey.getBytes(), SerializeUtils.serialize(tempValue.getValue()));
			}
			result = jedis.hmset(key.getBytes(), byteMap);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	};
	
	/**
	 * 获取Map类型的值
	 * @param key
	 * @return
	 */
	public Map<String, Object> xlGetMap(String key) {
		Map<String, Object> values = new HashMap<String, Object>();
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			Map<byte[], byte[]> result = jedis.hgetAll(key.getBytes());
			for (Map.Entry tempValue : result.entrySet()) {
				String tmepKey = new String((byte[]) tempValue.getKey());
				Object tmepValue = SerializeUtils.deserialize((byte[]) tempValue.getValue());
				values.put(tmepKey, tmepValue);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return values;
	}
	
	/**
	 * 通过键追加Map类型的值
	 * @param key
	 * @param value
	 * @return
	 */
	public String xlMapAppend(String key, Map<String, Object> value) {
		String result = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			Map<byte[], byte[]> byteMap = new HashMap<byte[], byte[]>();
			for (Map.Entry tempValue : value.entrySet()) {
				String tmepKey = (String) tempValue.getKey();
				Object tmepValue = tempValue.getValue();
				byteMap.put(tmepKey.getBytes(), SerializeUtils.serialize(tmepValue));
			}
			result = jedis.hmset(key.getBytes(), byteMap);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 去除Map类型的值
	 * @param key
	 * @param mapkey
	 * @return
	 */
	public String xlMapRemove(String key, String[] mapkey) {
		String result = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			Map<byte[], byte[]> byteMap = jedis.hgetAll(key.getBytes());
			Map<byte[], byte[]> newMap = new HashMap<byte[], byte[]>();
			for (Map.Entry tempValue : byteMap.entrySet()) {
				boolean value = true;
				byte[] tmepKey = (byte[]) tempValue.getKey();
				byte[] tmepv = (byte[]) tempValue.getValue();
				for (String delKey : mapkey) {
					if (new String(tmepKey).equals(delKey)) {
						value = false;
					}
				}
				if (value == true) {
					newMap.put(tmepKey, tmepv);
				}
			}
			this.xlDel(key);
			result = jedis.hmset(key.getBytes(), newMap);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 设置List<Object>类型的值
	 * @param <T>
	 * @param key
	 * @param list
	 * @return
	 */
	public <T> Long xlSetListObject(String key, List<T> list) {
		Long result = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			byte[] keyTemp = key.getBytes();
			for (int i = 0; i < list.size(); i++) {
				T temp = list.get(i);
				byte[] valueTemp = SerializeUtils.serialize(temp);
				result = jedis.lpush(keyTemp, valueTemp);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 设置List<Entity>类型的值
	 * @param <T>
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> List<T> xlGetListObject(String key) {
		List<T> result = new ArrayList<T>();
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			List<byte[]> reponse = jedis.lrange(key.getBytes(), 0, -1);
			for (int i = 0; i < reponse.size(); i++) {
				byte[] temp = reponse.get(i);
				T value = (T) SerializeUtils.deserialize(temp);
				result.add(i, value);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 追加List<Entity>类型的值
	 * @param <T>
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> Long xlListObjectAppend(String key, List<T> value) {
		Long result = null;
		Jedis jedis = jedisPool.getResource();
		jedis.auth(pass);
		jedis.select(store);
		try {
			byte[] keyTemp = key.getBytes();
			for (int i = 0; i < value.size(); i++) {
				T temp = value.get(i);
				byte[] valueTemp = SerializeUtils.serialize(temp);
				result = jedis.lpush(keyTemp, valueTemp);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}
	
}