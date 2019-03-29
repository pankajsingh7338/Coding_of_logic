package com.actolap.wse.dao;
import java.util.Date;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.actolap.wse.commons.Utils;
import com.actolap.wse.model.game.poker.PokerTable;
import com.actolap.wse.model.gameplay.GameType;
import com.actolap.wse.model.gameplay.snapshot.GameHistory;
import com.actolap.wse.model.report.DateCondition;
import com.actolap.wse.mongo.ConnectionFactory;

public class GameHistoryDao { 
	
	
	
	public static List<GameHistory> getGameHistory(Date startDate, Date endDate, Date tStartDate, Date eStartDate) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();  
		Query<GameHistory> query = datastore.createQuery(GameHistory.class);
		if (startDate != null) 
			query.field("start").greaterThanOrEq(startDate); 
		if (endDate != null) 
			query.field("start").lessThanOrEq(endDate);
		if(tStartDate != null)
			query.field("start").greaterThanOrEq(tStartDate);
		if(eStartDate != null)
			query.field("start").lessThanOrEq(eStartDate);
		//query.order("-start"); 
		return query.asList(); 
	} 
	  
	public static PokerTable getTitleById(String tableId) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<PokerTable> query = datastore.createQuery(PokerTable.class).field("_id").equal(tableId); 
		return query.get(); 
	} 
	  
	public static List<GameHistory> getByType(DateCondition dateFilter) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();  
		Query<GameHistory> query = datastore.createQuery(GameHistory.class).field("stats.gameType").equal(GameType.CASH);
		if (Utils.isNotEmptyNA(dateFilter.getStartDate())) {
			query.field("start").greaterThanOrEq(dateFilter.getStartDate());
		}
		if (Utils.isNotEmptyNA(dateFilter.getEndDate())) {
			query.field("start").lessThanOrEq(dateFilter.getEndDate());
		}
		return query.asList();
	}
	
	public static GameHistory getLastId() { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<GameHistory> query = datastore.createQuery(GameHistory.class); 
		 query.order("-start").limit(1);
		return query.get();
	} 
	
} 


