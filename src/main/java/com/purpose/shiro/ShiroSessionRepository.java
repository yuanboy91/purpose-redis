package com.purpose.shiro;

import java.io.Serializable;
import java.util.Collection;
import org.apache.shiro.session.Session;
/**
 * Session操作接口
 * @author: Yuanbo
 * @date 2016年3月28日 下午2:26:10
 * @version V1.0
 */
public interface ShiroSessionRepository {

	void saveSession(Session session);

	void deleteSession(Serializable sessionId);

	Session getSession(Serializable sessionId);

	Collection<Session> getAllSessions();
	
}
