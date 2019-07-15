package com.actolap.wse.cache;

import java.util.concurrent.TimeUnit;

import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.model.player.Player;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class PlayerCache {

	private static LoadingCache<String, Player> playerCache = CacheBuilder
			.newBuilder().maximumSize(1000)
			.expireAfterWrite(60, TimeUnit.SECONDS)
			.build(new CacheLoader<String, Player>() {
				@Override
				public Player load(String key) throws Exception {
					return PlayerDao.getById(key);
				}
			});

	public static String getPlayerTitle(String id) {
		String title = null;
		try {
			title = playerCache.get(id).getUserName();
		} catch (Exception e) {
		}
		return title;
	}

	public static Player getPlayer(String id) {
		Player player = null;
		try {
			player = playerCache.get(id);
		} catch (Exception e) {
		}
		return player;
	}
	
	public static String getPlayerGameName(String id) {
		String gameName = null;
		try {
			gameName = playerCache.get(id).getGameName();
		} catch (Exception e) {
		}
		return gameName;
	}

}

