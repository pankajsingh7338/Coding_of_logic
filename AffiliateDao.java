package com.actolap.wse.dao;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.actolap.wse.commons.PasswordEncoder;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.model.affiliate.Affiliate;
import com.actolap.wse.model.affiliate.Affiliate.AffiliateType;
import com.actolap.wse.model.affiliate.AffiliateReferralCode;
import com.actolap.wse.model.game.poker.AffiliateStatus;
import com.actolap.wse.mongo.ConnectionFactory;

public class AffiliateDao {

	private static final String PASS_KEY = "#####wse-game-game-26-january####";

	public static void persist(Affiliate affiliate) throws NoSuchAlgorithmException, IOException {
		affiliate.setEmail(affiliate.getEmail().toLowerCase()); 
		affiliate.setPassword(PasswordEncoder.encode(affiliate.getPassword(), PASS_KEY));
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(affiliate);

	} 
	  
	public static Affiliate affiliateLogIn(String email, String password) throws NoSuchAlgorithmException, IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("email").equal(email.toLowerCase())
				.field("password").equal(PasswordEncoder.encode(password, PASS_KEY));
		return query.get();
	}

	public static Affiliate loginWithMobile(String mobile, String password) throws NoSuchAlgorithmException,
			IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("mobile").equal(mobile).field("password")
				.equal(PasswordEncoder.encode(password, PASS_KEY));
		return query.get();
	}

	public static List<Affiliate> list(String query1, AffiliateType type, AffiliateStatus status) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class);
		if (Utils.isNotEmpty(query1)) {
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE);
			query.filter("name", pattern);
		}
		if (type != null) {
			query.field("type").equal(type);
		}
		if (status != null) {
			query.field("status").equal(status);
		}
		return query.asList(); 
	}

	public static Affiliate getById(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("id").equal(id);
		return query.get(); 
	} 

	public static Affiliate getByReferralCode(String playerCode) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("playerCode").equal(playerCode);
		return query.get();
	}

	public static Affiliate getByEmail(String email) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("email").equal(email);
		return query.get();
	}

	public static Affiliate getByMobile(String mobile) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("mobile").equal(mobile);
		return query.get();
	}
	
	public static AffiliateReferralCode getByReferral(String referralCode) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<AffiliateReferralCode> query = datastore.createQuery(AffiliateReferralCode.class);
		if (Utils.isNotEmpty(referralCode)) { 
			Pattern pattern = Pattern.compile(referralCode, Pattern.CASE_INSENSITIVE); 
			query.filter("referralCode", pattern); 
		}
		return query.get();
	}

	public static List<Affiliate> search(String query1, Integer limit) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("status").equal(AffiliateStatus.ACTIVE);
		if (Utils.isNotEmpty(query1)) { 
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE);
			query.filter("name", pattern); 
		} 
		if (limit != null)
			query.limit(limit);
		return query.asList();
	}
	
	public static List<Affiliate> searchFilter(String query1) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class);
		if (Utils.isNotEmpty(query1)) { 
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE);
			query.filter("name", pattern); 
		} 
		return query.asList();
	}

/*	public static void update(String id, Map<String, Object> mongoUpdate) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class);
		query.field("id").equal(id);
		if (mongoUpdate.size() > 0) {
			UpdateOperations<Affiliate> ops = datastore.createUpdateOperations(Affiliate.class);
			for (String key : mongoUpdate.keySet()) {
				ops.set(key, mongoUpdate.get(key));
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops);
		}
	}*/
	
	public static void update(String id, Map<String, Object> mongoUpdate) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class);
		query.field("id").equal(id);
		if (mongoUpdate.size() > 0) {
			UpdateOperations<Affiliate> ops = datastore
					.createUpdateOperations(Affiliate.class);
			for (String key : mongoUpdate.keySet()) {
				ops.set(key, mongoUpdate.get(key));
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops);
		}
	}

	public static List<Affiliate> getByIds(List<String> ids) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class);
		query.criteria("id").in(ids);
		return query.asList();
	}

	public static void emailVerified(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("id").equal(id);
		UpdateOperations<Affiliate> ops = datastore.createUpdateOperations(Affiliate.class);
		ops.set("emailVerified", true);
		datastore.update(query, ops);
	}

	public static void updateEmailStatus(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("id").equal(id);
		UpdateOperations<Affiliate> ops = datastore.createUpdateOperations(Affiliate.class);
		ops.set("emailVerified", true);
		datastore.update(query, ops);
	}

	public static void updateLastLogin(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class);
		query.field("id").equal(id);
		UpdateOperations<Affiliate> ops = datastore.createUpdateOperations(Affiliate.class);
		ops.set("lastLogin.time", new Date());
		datastore.update(query, ops, false);
	}

	public static void updateEmailToken(String id, String emailVerifiedToken) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("id").equal(id);
		UpdateOperations<Affiliate> ops = datastore.createUpdateOperations(Affiliate.class);
		ops.set("emailVerifiedToken", emailVerifiedToken);
		datastore.update(query, ops);
	}

	public static void updatePasswordToken(String id, String passwordToken, Boolean forgotTokenExpired) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("id").equal(id);
		UpdateOperations<Affiliate> ops = datastore.createUpdateOperations(Affiliate.class);
		if (Utils.isNotEmpty(passwordToken)) {
			ops.set("forgotPasswordToken", passwordToken);
		}
		if (forgotTokenExpired != null) {
			ops.set("forgotTokenExpired", forgotTokenExpired);
		}
		datastore.update(query, ops);
	}

	public static void resetPassword(String affiliateId, String newPassword) throws NoSuchAlgorithmException,
			IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("id").equal(affiliateId);
		UpdateOperations<Affiliate> ops = datastore.createUpdateOperations(Affiliate.class);
		ops.set("password", PasswordEncoder.encode(newPassword, PASS_KEY));
		datastore.update(query, ops);
	}

	public static void delete(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("id").equal(id);
		datastore.delete(query);

	}

	public static boolean updatePassword(String affiliateId, String newPassword, String oldPassword)
			throws NoSuchAlgorithmException, IOException {
		boolean success = false;
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("id").equal(affiliateId);
		if (Utils.isNotEmpty(oldPassword)) {
			query.field("password").equal(PasswordEncoder.encode(oldPassword, PASS_KEY));
		}
		UpdateOperations<Affiliate> ops = datastore.createUpdateOperations(Affiliate.class);
		ops.set("password", PasswordEncoder.encode(newPassword, PASS_KEY));
		UpdateResults result = datastore.update(query, ops);
		if (result.getUpdatedCount() > 0) {
			success = true;
		}
		return success;
	}

	public static void updateMobileNumber(String id, String mobile) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("id").equal(id);
		UpdateOperations<Affiliate> ops = datastore.createUpdateOperations(Affiliate.class);
		if (mobile != null) {
			ops.set("mobile", mobile);
		}
		datastore.update(query, ops);
	}

	public static void increasePlayerCount(String id, int count) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class).field("id").equal(id);
		UpdateOperations<Affiliate> ops = datastore.createUpdateOperations(Affiliate.class);
		ops.set("players", count); 
		datastore.update(query, ops); 
	}
	
	public static List<Affiliate> getListByIds(List<String> ids) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Affiliate> query = datastore.createQuery(Affiliate.class);
		query.criteria("id").in(ids);
		return query.asList();
	} 

}

