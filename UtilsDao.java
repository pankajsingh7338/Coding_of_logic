package com.actolap.wse.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import com.actolap.wse.model.TableRoom;
import com.actolap.wse.model.gameplay.snapshot.GameHistory;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.mongo.ConnectionFactory;

public class UtilitiesDao {


	public static List<GameHistory> getGameIdList(long uniqueId, Date start, Date end, String playerid) throws ParseException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<GameHistory> query = datastore.createQuery(GameHistory.class);
		if (start != null)
			query.field("start").greaterThanOrEq(start);
		if (end != null)
			query.field("start").lessThanOrEq(end);
		if (uniqueId != 0)
			query.field("uniqueId").equal(uniqueId);
		if (start == null && end == null && uniqueId == 0 && playerid == null) {
		 long lastSevenDaysMiliSeconds = (new Date().getTime() - (7 * 24 * 60 * 60 * 1000));
		 Date lastSevenDaysDate = new Date(lastSevenDaysMiliSeconds);
		 query.field("start").greaterThanOrEq(lastSevenDaysDate);}
		 query.order("-start");
		 if(playerid != null)
			 query.field("stats.playerGameStats.id").equal(playerid);
		 return query.asList();
	}

	public static List<TableRoom> getTableNameList(String tid) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<TableRoom> query = datastore.createQuery(TableRoom.class).field("tid").equal(tid);
		return query.asList();
	}

	public static GameHistory getById(long uniqueId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<GameHistory> query = datastore.createQuery(GameHistory.class).field("uniqueId").equal(uniqueId);
		return query.get();
	}

	public static Player getById(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		return query.get();
	}
	
	public static Player getByGameName(String gameName) {
		Pattern pattern = Pattern.compile("^" + gameName + "$", Pattern.CASE_INSENSITIVE);//This line will create a pattern to match words starts with "b", ends with "b" and its case insensitive too.
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("gameName").equal(pattern).retrievedFields(true, "gameName");
		return query.get(); 
	}
	

}

