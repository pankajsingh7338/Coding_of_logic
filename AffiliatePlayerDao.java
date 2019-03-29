package com.actolap.wse.dao;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.actolap.wse.commons.Utils;
import com.actolap.wse.model.game.poker.AffiliatePlayer;
import com.actolap.wse.model.game.poker.AffiliatePlayer.AffiliateReferralStatus;
import com.actolap.wse.model.game.poker.AffiliatePlayer.PlayerReferralStatus; 
import com.actolap.wse.mongo.ConnectionFactory;

public class AffiliatePlayerDao {

	public static void persist(AffiliatePlayer player) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		datastore.save(player); 
	} 
	  
	public static List<AffiliatePlayer> affiliatePlayerGet(String affiliateId, String status) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("affiliateId").equal(affiliateId); 
		if (Utils.isNotEmpty(status)) { 
			query.field("status").equal(status); 
		} 
		return query.asList(); 
	} 
	  
	public static List<AffiliatePlayer> affiliateList(String affiliateId, String query1,
			AffiliateReferralStatus affiliateStatus, Date date) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("affiliateId") 
				.equal(affiliateId); 
		if (Utils.isNotEmpty(query1)) { 
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE); 
			query.filter("userName", pattern); 
		} 
		if (affiliateStatus != null) { 
			if(affiliateStatus.equals(AffiliateReferralStatus.APPROVED)) 
				query.or(query.criteria("affiliateStatus").equal(affiliateStatus),query.criteria("status").equal(PlayerReferralStatus.APPROVED),
						query.criteria("affiliateStatus").equal(AffiliateReferralStatus.BANNEDPLAYER),
						query.criteria("status").equal(PlayerReferralStatus.BANNEDPLAYER));  
			else if(affiliateStatus.equals(AffiliateReferralStatus.PENDING)) {  
				query.or(query.criteria("affiliateStatus").equal(affiliateStatus),
						query.criteria("affiliateStatus").equal(AffiliateReferralStatus.PENDING),query.criteria("status").equal(PlayerReferralStatus.PENDING));
			} 
			else if(affiliateStatus.equals(AffiliateReferralStatus.REJECTED))
				query.field("affiliateStatus").equal(affiliateStatus);
		} 
		return query.asList(); 
	} 
	  
	public static AffiliatePlayer getByPlayerAndAffiliateId(String affiliateId, String playerId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("affiliateId")
				.equal(affiliateId).field("playerId").equal(playerId);
		return query.get();
	}

	public static AffiliatePlayer getByPlayerId(String playerId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("playerId").equal(playerId);
		return query.get(); 
	} 
     
	public static AffiliatePlayer getByAffiliateId(String affiliateId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("affiliateId")
				.equal(affiliateId);
		return query.get();
	} 
	  
	public static AffiliatePlayer getRejectedPlayer(String playerId) {  
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("playerId").equal(playerId);  
		query.and(query.criteria("status").notEqual(PlayerReferralStatus.PENDING), 
				query.criteria("status").notEqual(PlayerReferralStatus.APPROVED));  
		return query.get(); 
	} 
	 
	public static AffiliatePlayer getRejectedAffiliatePlayer(String playerId, String affiliateId) {  
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("playerId").equal(playerId).field("affiliateId").equal(affiliateId);   
		query.and(query.criteria("status").equal(PlayerReferralStatus.REJECTED),
				query.criteria("affiliateStatus").equal(AffiliateReferralStatus.REJECTED));  
		return query.get(); 
	} 
	
	public static AffiliatePlayer getApprovedAffiliatePlayer(String playerId, String affiliateId) {  
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("playerId").equal(playerId).field("affiliateId").equal(affiliateId);   
		query.and(query.criteria("status").equal(PlayerReferralStatus.APPROVED),
				query.criteria("affiliateStatus").equal(AffiliateReferralStatus.APPROVED));  
		return query.get(); 
	} 
      
	public static void updatePlayerStatus(String playerId, PlayerReferralStatus status, String affiliateId, Date date) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("playerId").equal(playerId).field("affiliateId").equal(affiliateId); 
		UpdateOperations<AffiliatePlayer> ops = datastore.createUpdateOperations(AffiliatePlayer.class);
		ops.set("status", status); 
		ops.set("affiliateStatus", status); 
		ops.set("lastUpdate", date); 
		datastore.update(query, ops); 
	}

	public static void updatePlayerAffiliateStatus(String playerId, AffiliateReferralStatus affiliateStatus, Date date) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("playerId").equal(playerId);
		UpdateOperations<AffiliatePlayer> ops = datastore.createUpdateOperations(AffiliatePlayer.class);
		ops.set("affiliateStatus", affiliateStatus);
		if(affiliateStatus.equals(AffiliateReferralStatus.REJECTED)) {
			ops.set("status", affiliateStatus);
			ops.set("lastUpdate", date); 
		}
		datastore.update(query, ops); 
	} 

	public static void delete(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("id").equal(id);
		datastore.delete(query);

	}
	
	public static List<AffiliatePlayer> getReportList(String affiliateId) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("affiliateId").equal(affiliateId); 
		return query.asList(); 
	} 
	
	public static void updateBanPlayer(String playerId, PlayerReferralStatus status, String affiliateId, Date date) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AffiliatePlayer> query = datastore.createQuery(AffiliatePlayer.class).field("playerId").equal(playerId).field("affiliateId").equal(affiliateId); 
		UpdateOperations<AffiliatePlayer> ops = datastore.createUpdateOperations(AffiliatePlayer.class);
		if(affiliateId != null && playerId != null) { 
			ops.set("status", status); 
			ops.set("affiliateStatus", status); 
			ops.set("lastUpdate", date); 
			datastore.update(query, ops); 
		} 
	} 
      
} 



