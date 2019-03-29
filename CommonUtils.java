package com.actolap.wse.commons;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;

import com.actolap.config.Config;
import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.inmemory.memcache.GlobalCachedManager;
import com.actolap.wse.inmemory.memcache.InMemory;
import com.actolap.wse.manager.AnalyticsManager;
import com.actolap.wse.model.game.poker.Tournament;
import com.actolap.wse.model.game.poker.TournamentStatus;
import com.actolap.wse.model.gameplay.GameType;
import com.actolap.wse.model.player.Category;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.player.PlayerClass;
import com.actolap.wse.model.player.PlayerTransaction;

public class Utils {

	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00");
	private static DecimalFormat df2 = new DecimalFormat(".##");

	static {
		sdf.setTimeZone(java.util.TimeZone.getTimeZone("IST"));
	}

	public static Properties readProperties(Class<?> classStart, String path) throws IOException {
		Properties properties = null;
		InputStream inputStream = classStart.getClassLoader().getResourceAsStream(path);
		properties = new Properties();
		properties.load(inputStream);
		return properties;
	}

	public static byte[] trim(byte[] bytes) {
		int i = bytes.length - 1;
		while (i >= 0 && bytes[i] == 0) {
			--i;
		}
		return Arrays.copyOf(bytes, i + 1);
	}

	public static boolean isNotEmpty(String input) {
		boolean flag = false;
		if (input != null && !input.trim().equals("") && !input.equals("null")) {
			flag = true;
		}
		return flag;
	}

	public static boolean isNotEmptyNA(String input) {
		boolean flag = false;
		if (input != null && !input.trim().equals("") && !input.trim().equals("NA")) {
			flag = true;
		}
		return flag;
	}

	public static Integer intValue(String input, int defaultValue) {
		Integer intValue = intValue(input);
		if (intValue == null) {
			intValue = defaultValue;
		}

		return intValue;
	}

	public static Integer intValue(String input) {
		Integer intValue = null;
		try {
			if (isNotEmpty(input))
				intValue = Integer.parseInt(input);
		} catch (Exception e) {

		}
		return intValue;
	}

	public static Set<String> stringToSet(String input) {
		Set<String> stringvalue = new HashSet<String>();
		try {
			String[] split = input.split(",");
			for (String temp : split) {
				stringvalue.add(temp.trim());
			}
		} catch (Exception e) {
		}
		return stringvalue;
	}

	public static List<String> stringToList(String input) {
		List<String> stringvalue = new ArrayList<String>();
		try {
			String[] split = input.split(",");
			for (String temp : split) {
				stringvalue.add(temp.trim());
			}
		} catch (Exception e) {
		}
		return stringvalue;
	}

	public static boolean checkforValidIPs(List<String> ipList) {
		boolean valid = true;
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		if (ipList != null) {
			for (String ip : ipList) {
				if (!pattern.matcher(ip).matches()) {
					valid = false;
					break;
				}
			}
		} else
			valid = false;
		return valid;
	}

	public static String convertSecondsToHMmSs(long ms) {
		/*ms = ms / 1000;
		long s = ms % 60;
		long m = (ms / 60) % 60;
		long h = (ms / (60 * 60)) % 24;
		return String.format("%d:%02d:%02d", h, m, s);*/
		
		long millis = ms;
		String hms = String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	  return hms;
	}

	public static String getDate(Date date) {
		String dateHour = null;
		try {
			if (date != null) {
				dateHour = sdf.format(date);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dateHour;
	}

	public static Date buildDate(String date) {
		Date parseDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			parseDate = sdf.parse(date);
		} catch (Exception ex) {

		}
		return parseDate;
	}

	public static void main(String[] args) {
		getDate(new Date());
	}

	public static Properties readPropertiesCommons(@SuppressWarnings("rawtypes") Class classLoad, String path, Logger logger) throws IOException {
		InputStream inputStream = classLoad.getClassLoader().getResourceAsStream(path);
		Properties properties = new Properties();
		properties.load(inputStream);
		loadMongo(logger, properties);
		GlobalCachedManager.init(properties);
		return properties;
	}

	public static void loadMongo(Logger logger, Properties properties) {
		try {
			Config.port = Integer.parseInt(properties.getProperty("mongo.port", Config.port + ""));
		} catch (NumberFormatException npe) {
			// ignore
		}
		int mongoHostReadCount = Integer.parseInt(properties.getProperty("mongo.count", null));
		logger.info("Mongo  Count" + mongoHostReadCount);
		for (int i = 1; i <= mongoHostReadCount; i++) {
			logger.info("Mongo host : " + properties.getProperty("mongo.count." + i));
			Config.MONGOHOST.add(properties.getProperty("mongo.count." + i, null));
		}
		Config.DB_NAME = properties.getProperty("mongo.db", Config.DB_NAME);
	}

	public static boolean userAuthentication(String id, String token, String ip, Logger logger) {
		String userToken = GlobalCachedManager.get(id);
		boolean secure = false;
		if (userToken != null) {
			String[] playerTokens = token.split("_");
			String[] tokens = userToken.split("\t");
			if (tokens[0].equals(playerTokens[0]) && tokens[1].equals(ip)) {
				GlobalCachedManager.set(id, userToken, 60L);
				secure = true;
			}
		} else {
			logger.info("Player userAuthentication failed {} token {} ip {}", id, token, ip);
		}
		return secure;
	}

	public static double vipPointIssued(PlayerClass playerClass, Double rake, GameType gameType) {
		double vipPoints = 0;
		if (playerClass != null && rake != null && rake > 0 && gameType == GameType.CASH) {
			Category category = InMemory.categoryMap.get(playerClass);
			if (category != null) {
				//vipPoints = Math.round(rake * 82 * category.getVipPointsPert()/10000);
				vipPoints = rake * category.getVipPointsPert()/100;
				vipPoints = Double.valueOf(df2.format(vipPoints));
			}
		}
		return vipPoints;
	}

	public static boolean tournamentReBuy(Tournament tournament, String playerId) {
		boolean success = false;
		if (tournament != null && playerId != null && tournament.getStatus().equals(TournamentStatus.LIVE) ||tournament.getStatus().equals(TournamentStatus.PENDING_LIVE) || tournament.getStatus().equals(TournamentStatus.PENDING_LIVE)) {
			Player player = PlayerDao.getById(playerId);
			if (player != null && tournament.getEntryTicket() <= player.getWallet().getCash()) {
				PlayerDao.updateWallet(player.getId(), null, null, -Long.valueOf(tournament.getEntryTicket()), null, null);
				PlayerTransaction.reBuyTournament(player, tournament);
				AnalyticsManager.tournamentRegistered(player, (long) tournament.getEntryTicket());
				success = true;
			}
		}
		return success;
	}

	public static void setUserName(Player player, boolean gamePlay) {
		if (Utils.isNotEmpty(player.getProfile().getGameName()) && gamePlay)
			player.setUserName(player.getProfile().getGameName());
		else if (Utils.isNotEmpty(player.getProfile().getFirstName()) && Utils.isNotEmpty(player.getProfile().getLastName()))
			player.setUserName(player.getProfile().getFirstName());
		else
			player.setUserName(getEmailAsUserName(player.getEmail()));
	}

	public static Player claimFreeChips(String playerId, long claimedFreeChips) {
		PlayerDao.updateWallet(playerId, claimedFreeChips, null, null, null, null);
		return PlayerDao.getById(playerId);
	}

	public static boolean eligibleToClaim(Player player, Boolean eligible) {
		if (player.getWallet() != null && player.getWallet().getFree() <= 200) {
			eligible = true;
		} else if (player.getWallet() != null && player.getWallet().getLastClaimDate() == null) {
			eligible = true;
		} else if (player.getWallet() != null && player.getWallet().getLastClaimDate() != null) {
			Calendar cal = Calendar.getInstance();
			// Start Date
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 1);
			Date start = cal.getTime();
			// End Date
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			Date end = cal.getTime();
			// Last Claim Date
			cal.setTime(player.getWallet().getLastClaimDate());
			cal.add(Calendar.HOUR_OF_DAY, 5);
			cal.add(Calendar.MINUTE, 30);
			Date lastClaimDate = cal.getTime();
			// Cureent Date
			cal.setTime(new Date());
			cal.add(Calendar.HOUR_OF_DAY, 5);
			cal.add(Calendar.MINUTE, 30);
			Date currentDate = cal.getTime();
			if (!(lastClaimDate.getTime() >= start.getTime() && lastClaimDate.getTime() <= end.getTime())
					&& (currentDate.getTime() >= start.getTime() && currentDate.getTime() <= end.getTime()))
				eligible = true;
			else
				eligible = false;
		}
		return eligible;
	}

	public static String getEmailAsUserName(String email) {
		String userName = email.substring(0, email.indexOf("@"));
		return userName;
	}

	public static boolean build(UpdateOperations<?> ops, String measure, long value, boolean success) {
		if (value != 0) {
			ops.inc(measure, value);
			success = true;
		}

		return success;

	}

	public static boolean build(UpdateOperations<?> ops, String measure, double value, boolean success) {
		if (value != 0) {
			ops.inc(measure, value);
			success = true;
		}
		return success;
	}
	public static String utcToIst(Date date) {
	DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
	return formatter.format(date);
	}
	public static String istToUtc(Date date) { 
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.format(date); 
	} 
	
	/*public static String IstToUtc(Date date) {
	DateFormat formatterUTC = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	return formatterUTC.format(date);
	}*/
}

