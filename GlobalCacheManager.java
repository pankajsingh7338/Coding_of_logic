package com.actolap.wse.inmemory.memcache;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;

public class GlobalCachedManager {

	public static RedisService redisMarginService = null; 

	public static void init(Properties p) { 
		final String redisMiscClusterAddress = p.getProperty("redis.address", "redis://127.0.0.1:6379/0");
		RedisClient redisClient = null;
		redisClient = RedisClient.create(RedisURI.create(redisMiscClusterAddress));
		redisClient.setDefaultTimeout(1, TimeUnit.SECONDS);
		redisMarginService = new RedisService(redisClient);
	}

	public static void set(String key, String value, Long ttlSeconds) {
		redisMarginService.set(key, value, ttlSeconds);
	}

	public static String get(String key) {
		return redisMarginService.get(key);
	}

	public static void del(String key) {
		redisMarginService.del(key);
	}
}

