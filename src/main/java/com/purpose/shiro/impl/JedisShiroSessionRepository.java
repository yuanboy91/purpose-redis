package com.purpose.shiro.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purpose.cache.RedisManager;
import com.purpose.shiro.ShiroSessionRepository;
import com.purpose.utils.SerializeUtils;
/**
 * Session操作实现类
 * @author: Yuanbo
 * @date 2016年3月28日 下午2:26:10
 * @version V1.0
 */
public class JedisShiroSessionRepository implements ShiroSessionRepository {

	private static final Logger logger = LoggerFactory.getLogger(JedisShiroSessionRepository.class);
	//redis session key 前缀
	private final String REDIS_SHIRO_SESSION = "shiro-session";

	private RedisManager redisManager;

	@Override
	public void saveSession(Session session) {
		redisManager.init();
		if (session == null || session.getId() == null) {
			logger.error("session或者session ID为空");
		}
		byte[] key = getRedisSessionKey(session.getId()).getBytes();
		byte[] value = SerializeUtils.serialize(session);
		Long timeOut = session.getTimeout() / 1000;
		redisManager.set(key, value, Integer.parseInt(timeOut.toString()));
	}

	@Override
	public void deleteSession(Serializable sessionId) {
		redisManager.init();
		if (sessionId == null) {
			logger.error("sessionId为空");
		}
		redisManager.del(getRedisSessionKey(sessionId).getBytes());
	}

	@Override
	public Session getSession(Serializable sessionId) {
		redisManager.init();
		if (null == sessionId) {
			logger.error("sessionId为空");
			return null;
		}
		Session session = null;
		byte[] value = redisManager.get(getRedisSessionKey(sessionId).getBytes());
		if (null == value)
			return null;
		session = (Session) SerializeUtils.deserialize(value);
		return session;
	}

	@Override
	public Collection<Session> getAllSessions() {
		redisManager.init();
		Set<Session> sessions = new HashSet<Session>();
		Set<byte[]> byteKeys = redisManager.keys(this.REDIS_SHIRO_SESSION + "*");
		if (byteKeys != null && byteKeys.size() > 0) {
			for (byte[] bs : byteKeys) {
				Session s = (Session) SerializeUtils.deserialize(redisManager.get(bs));
				sessions.add(s);
			}
		}
		return sessions;
	}

	/**
	 * 获取redis中的session key
	 * @param sessionId
	 * @return
	 */
	private String getRedisSessionKey(Serializable sessionId) {
		return this.REDIS_SHIRO_SESSION + sessionId;
	}

	public RedisManager getRedisManager() {
		return redisManager;
	}

	public void setRedisManager(RedisManager redisManager) {
		this.redisManager = redisManager;
	}

	public JedisShiroSessionRepository() {

	}

}