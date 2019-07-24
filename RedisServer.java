package com.actolap.wse.inmemory.memcache;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lambdaworks.redis.RedisAsyncConnection;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.protocol.SetArgs;

public class RedisService {

	public static Logger logger = LoggerFactory.getLogger(RedisService.class);
	
	RedisAsyncConnection<String, String> asycn;
	RedisConnection<String, String> sycn = null;
	
	RedisClient redisClient;

	public RedisService(RedisClient redisClient) {
		this.redisClient = redisClient;
		sycn = redisClient.connect();
		sycn.setTimeout(1, TimeUnit.SECONDS);
		asycn = redisClient.connectAsync();
	}

	void set(String key, String value, Long seconds) {
		if (seconds != null) {
			sycn.set(key, value, SetArgs.Builder.ex(seconds));
		} else {
			sycn.set(key, value); 
		}

	}

	String get(String key) {
		return sycn.get(key);
	}

	void del(String key) {
		asycn.del(key);
	} 
} 


