package com.actolap.wse.dao;

import java.util.List;
import java.util.regex.Pattern;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import com.actolap.wse.analytics.aggregate.model.AggregateAffiliatePlayer;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.model.game.poker.AffiliatePlayer.AffiliateReferralStatus;
import com.actolap.wse.mongo.ConnectionFactory;

public class AggregateAffiliatePlayerDao {

	public static List<AggregateAffiliatePlayer> getReportList(String affiliateId, String startDate, String endDate) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AggregateAffiliatePlayer> query = datastore.createQuery(AggregateAffiliatePlayer.class)
				.field("affiliateId").equal(affiliateId);
		if (startDate != null)
			query.field("date").greaterThanOrEq(startDate);
		if (endDate != null)
			query.field("date").lessThanOrEq(endDate);
		return query.asList();
	}

	public static List<AggregateAffiliatePlayer> list(String affiliateId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AggregateAffiliatePlayer> query = datastore.createQuery(AggregateAffiliatePlayer.class)
				.field("affiliateId").equal(affiliateId);
		return query.asList();
	}

	public static List<AggregateAffiliatePlayer> listByPlayerId(String affiliateId, String playerId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AggregateAffiliatePlayer> query = datastore.createQuery(AggregateAffiliatePlayer.class);
		if (playerId != null && affiliateId != null) {
			query.field("affiliateId").equal(affiliateId).field("playerId").equal(playerId);
		}
		return query.asList();
	}

	public static List<AggregateAffiliatePlayer> getReportListByDate(String gameMode, String startDate, String endDate,
			List<String> value) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AggregateAffiliatePlayer> query = datastore.createQuery(AggregateAffiliatePlayer.class);
		if (!value.isEmpty())
			query.field("affiliateId").in(value);
		if (Utils.isNotEmpty(gameMode)) { 
			if (gameMode.equals("HOLDEM")) {
				if (startDate != null)
					query.field("date").greaterThanOrEq(startDate);
				if (endDate != null)
					query.field("date").lessThanOrEq(endDate); 
				query.or(query.criteria("mode").equal(gameMode),
						query.criteria("mode").equal(null));  
			} else { 
				if (startDate != null)
					query.field("date").greaterThanOrEq(startDate);
				if (endDate != null)
					query.field("date").lessThanOrEq(endDate);
				Pattern pattern = Pattern.compile(gameMode, Pattern.CASE_INSENSITIVE);
				query.field("mode").equal(pattern); 
			}
		}
		return query.asList();
	}
}

