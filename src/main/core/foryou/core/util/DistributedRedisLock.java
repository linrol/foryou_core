package foryou.core.util;

import java.util.Collections;

import redis.clients.jedis.Jedis;

/**
 * @author 罗林 E-mail:1071893649@qq.com
 * @version 创建时间：2018年6月11日 下午4:13:52 类说明
 */
public class DistributedRedisLock {

	private static final String LOCK_SUCCESS = "OK";
	private static final String SET_IF_NOT_EXIST = "NX";
	private static final String SET_WITH_EXPIRE_TIME = "PX";

	private static final Long RELEASE_SUCCESS = 1L;

	/**
	 * time millisecond
	 */
	private static final int TIME = 1000;

	/**
	 * 获取默认10秒释放时间非阻塞式的分布式锁
	 * 
	 * @param key
	 * @param request
	 * @return
	 */
	public static boolean tryLock(String key, String request) {
		Jedis jedis = RedisPool.getJedis();
		String result = jedis.set(key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, 10 * TIME);
		RedisPool.close(jedis);
		if (LOCK_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取自定义失效时间的非阻塞式的分布式锁
	 * 
	 * @param key
	 * @param request
	 * @param expireTime
	 * @return
	 */
	public static boolean tryLock(String key, String request, int expireTime) {
		Jedis jedis = RedisPool.getJedis();
		String result = jedis.set(key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
		RedisPool.close(jedis);
		if (LOCK_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取阻塞分布式锁
	 * 
	 * @param key
	 * @param request
	 * @throws InterruptedException
	 */
	public static void lock(String key, String request) throws InterruptedException {
		for (;;) {
			Jedis jedis = RedisPool.getJedis();
			String result = jedis.set(key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, 10 * TIME);
			RedisPool.close(jedis);
			if (LOCK_SUCCESS.equals(result)) {
				break;
			}
			// 防止一直消耗 CPU
			Thread.sleep(100);
		}
		
	}

	/**
	 * 获取自定义阻塞时间的分布式锁
	 * 
	 * @param key
	 * @param request
	 * @param blockTime
	 * @return
	 * @throws InterruptedException
	 */
	public static boolean lock(String key, String request, int blockTime) throws InterruptedException {
		while (blockTime >= 0) {
			Jedis jedis = RedisPool.getJedis();
			String result = jedis.set(key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, 10 * TIME);
			RedisPool.close(jedis);
			if (LOCK_SUCCESS.equals(result)) {
				return true;
			}
			blockTime -= 100;
			Thread.sleep(100);
		}
		return false;
	}

	/**
	 * 释放分布式锁
	 * 
	 * @param jedis
	 *            Redis客户端
	 * @param lockKey
	 *            锁
	 * @param requestId
	 *            请求标识
	 * @return 是否释放成功
	 */
	public static boolean unlock(String key, String request) {
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		Jedis jedis = RedisPool.getJedis();
		Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(request));
		RedisPool.close(jedis);
		if (RELEASE_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}
	
}
