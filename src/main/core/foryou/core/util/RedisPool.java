package foryou.core.util;

import foryou.core.mvc.MvcCore;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/** 
* @author 罗林 E-mail:1071893649@qq.com 
* @version 创建时间：2018年6月12日 上午9:26:09 
* 类说明 
*/
public class RedisPool {
	
	/**
	 * jedis连接池
	 */
	private static JedisPool pool;
	
	/**
	 * 最大连接数
	 */
    private static int maxTotal = 20;
  
    /**
     * 最大空闲连接数
     */
    private static int maxIdle = 10;
  
    /**
     * 最小空闲连接数
     */
    private static int minIdle = 5;

    /**
     * 在取连接时测试连接的可用性
     */
    private static boolean testOnBorrow = true;
  
    /**
     * 再还连接时不测试连接的可用性
     */
    private static boolean testOnReturn = false;
  
    static {
    	/**
    	 * 初始化连接池
    	 */
        initPool();
    }  
  
    public static Jedis getJedis(){  
        return pool.getResource();  
    }  
  
    public static void close(Jedis jedis){  
        jedis.close();  
    }  
  
    private static void initPool() {
    	String host = "127.0.0.1";
    	int port = 6379;
    	String jedisPool = MvcCore.getProperties("jedis-pool");
    	if (jedisPool != null) {
    		host = jedisPool.split(",")[0];
    		port = Integer.parseInt(jedisPool.split(",")[1]);
    	}
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);
        pool = new JedisPool(config, host, port); 
    }
}
