package com.actolap.wse.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.actolap.wse.model.backoffice.LeaderBoardConfig;
import com.actolap.wse.model.elearning.Quiz;
import com.actolap.wse.model.elearning.PlayerAchievement.PlayerAchievementStatus;
import com.actolap.wse.model.leaderBoard.LeaderBoard;
import com.actolap.wse.model.leaderBoard.LeaderBoard.LeaderBoardEntity;
import com.actolap.wse.model.leaderBoard.LeaderBoard.LeaderBoardStatus;
import com.actolap.wse.model.player.LeaderBoardCalculate;
import com.actolap.wse.mongo.ConnectionFactory;

public class LeaderBoardDao {
	public static List<Quiz> getLeaderBoard(
			Boolean completed) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Quiz> query = datastore.createQuery(Quiz.class);
		query.order("-correctAnswerCount");
		query.field("score").greaterThan(0);
		query.limit(50);
		if (completed != null) {
			query.field("completed").equal(completed);
		}
		return query.asList();

	} 
	public static void persist(LeaderBoardConfig leaderBoardConfig) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(leaderBoardConfig);
	}
	
	
	public static void persist(LeaderBoard leaderBoard) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(leaderBoard);
	}
	public static LeaderBoardConfig getById() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoardConfig> query = datastore.createQuery(LeaderBoardConfig.class);
		return query.get();
	} 
	
	public static LeaderBoard getByLeaderBoardId( String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoard> query = datastore.createQuery(LeaderBoard.class).field("id").equal(id);
		return query.get();
	} 
	
	public static void delete(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoard> query = datastore.createQuery(LeaderBoard.class)
				.field("id").equal(id);
		datastore.delete(query);

	}
	
	public static LeaderBoardCalculate getByplayerId(String pid) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoardCalculate> query = datastore.createQuery(LeaderBoardCalculate.class).field("pid").equal(pid);	
		return query.get();
	} 
	
	public static void persist(LeaderBoardCalculate leaderBoardCalculate) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(leaderBoardCalculate);
	}
	
	public static void updateValue(String id, String startDate,Double vipPoints) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoardCalculate> query = datastore.createQuery(LeaderBoardCalculate.class);
		query.field("pid").equal(id);
		UpdateOperations<LeaderBoardCalculate> ops = datastore.createUpdateOperations(LeaderBoardCalculate.class);
		ops.inc("vipPointsList."+startDate, vipPoints);
		datastore.update(query, ops);
		
	} 
	
	public static void updateNewValue(String id, String sdate, Double vipPoints) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoardCalculate> query = datastore.createQuery(LeaderBoardCalculate.class);
		query.field("pid").equal(id);;
		UpdateOperations<LeaderBoardCalculate> ops = datastore
				.createUpdateOperations(LeaderBoardCalculate.class);
		ops.set("vipPointsList."+sdate, vipPoints);
		datastore.update(query, ops);
		
		
	} 
	
	public static void deleteLeaderBoardCalculate() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoardCalculate> query = datastore.createQuery(LeaderBoardCalculate.class);
		query.getCollection().drop();
		
	}
	
	public static LeaderBoardConfig getUpdateById(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoardConfig> query = datastore.createQuery(LeaderBoardConfig.class).field("id").equal(id);
		return query.get(); 
	} 
	
	public static LeaderBoard getByTitle(String title) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoard> query = datastore.createQuery(LeaderBoard.class).field("title").equal(title);
		return query.get(); 
	} 
	
	public static LeaderBoard getByEntity(LeaderBoardEntity leaderBoardEntity) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoard> query = datastore.createQuery(LeaderBoard.class).field("leaderBoardEntity").equal(leaderBoardEntity);
		query.field("status").equal(PlayerAchievementStatus.ENABLE);
		return query.get();
	}
	
	public static List<LeaderBoardCalculate> getVipCalculation() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoardCalculate> query = datastore.createQuery(LeaderBoardCalculate.class);
		return query.asList();
	} 
	 
	public static void update(String id, Map<String, Object> mongoUpdate) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoardConfig> query = datastore.createQuery(LeaderBoardConfig.class);
		query.field("id").equal(id);
		if (mongoUpdate.size() > 0) {
			UpdateOperations<LeaderBoardConfig> ops = datastore
					.createUpdateOperations(LeaderBoardConfig.class);
			for (String key : mongoUpdate.keySet()) {
				ops.set(key, mongoUpdate.get(key));
			}
			datastore.update(query, ops);
		} 
	} 
	
	public static List<LeaderBoard> list(LeaderBoardEntity entity,LeaderBoardStatus status) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoard> query = datastore.createQuery(LeaderBoard.class);
		
		if (entity != null) {
			query.field("leaderBoardEntity").equal(entity);
		}
		if (status != null) {
			query.field("status").equal(status);
		}
		return query.asList();
	}
	public static List<LeaderBoard> activeList() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoard> query = datastore.createQuery(LeaderBoard.class).field("status").equal(LeaderBoardStatus.ENABLE);
		return query.asList();
	}
	
	public static void updateValue(String id, Map<String, Object> mongoUpdate) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<LeaderBoard> query = datastore.createQuery(LeaderBoard.class);
		query.field("id").equal(id);
		if (mongoUpdate.size() > 0) {
			UpdateOperations<LeaderBoard> ops = datastore
					.createUpdateOperations(LeaderBoard.class);
			for (String key : mongoUpdate.keySet()) {
				ops.set(key, mongoUpdate.get(key));
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops);
		}
	}

}

