package com.actolap.wse.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.actolap.wse.commons.Utils;
import com.actolap.wse.model.promotion.Coupon;
import com.actolap.wse.model.promotion.CouponRedemption;
import com.actolap.wse.model.promotion.CouponStatus;
import com.actolap.wse.model.promotion.CouponType;
import com.actolap.wse.mongo.ConnectionFactory;

public class CouponDao {

	public static void persist(Coupon coupon) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(coupon);
	}

	public static Coupon getById(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("id").equal(id);
		return query.get();
	}
	
	public static Coupon getByCodeNumber(String codeNumber) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("codeNumber").equal(codeNumber);
		return query.get();
	}
	
	public static List<Coupon> getByAllCoupon() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class);
		return query.asList();
	}
	
	public static void updateCouponVisibility(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("id").equal(id);
		UpdateOperations<Coupon> ops = datastore.createUpdateOperations(Coupon.class);
		ops.set("visibility", true);
		datastore.update(query, ops);
	}

	public static Coupon getLiveCouponByCode(String codeNumber, CouponType couponType) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("status").equal(CouponStatus.LIVE)
				.field("codeNumber").equal(codeNumber);
		if (couponType != null) {
			query.field("couponType").equal(couponType);
		}
		return query.get();
	}
	
	public static Coupon getByCode(String codeNumber) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("codeNumber").equal(codeNumber);
		return query.get();
	}
	
	public static Coupon getSignUpCoupon(CouponType couponType) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("status").equal(CouponStatus.LIVE).field("couponType").equal(couponType);
		return query.get();
	}
	
	public static CouponRedemption getLiveRedemption(int codeNumber) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<CouponRedemption> query = datastore.createQuery(CouponRedemption.class).field("uniqueNumber").equal(codeNumber).field("archive").equal(false);
		return query.get();
	}
	
	public static void updatecouponredemption(int code, boolean archive) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<CouponRedemption> query = datastore.createQuery(CouponRedemption.class).field("uniqueNumber").equal(code);
		UpdateOperations<CouponRedemption> ops = datastore.createUpdateOperations(CouponRedemption.class);
		if(archive)
			ops.set("archive", true);
		else if(!archive)
			ops.set("archive", false);
		datastore.update(query, ops);
	}
	
	
	
	public static void updatredemptionByCode(int couponCode, boolean archive) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<CouponRedemption> query = datastore.createQuery(CouponRedemption.class).field("uniqueNumber").equal(couponCode);
		UpdateOperations<CouponRedemption> ops = datastore.createUpdateOperations(CouponRedemption.class);
		if(archive)
			ops.set("archive", true);
		datastore.update(query, ops);
	}
	
	public static void couponupdateArchive(String couponCode, boolean archive) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("codeNumber").equal(couponCode);
		UpdateOperations<Coupon> ops = datastore.createUpdateOperations(Coupon.class);
		if(archive)
			ops.set("archive", true);
		datastore.update(query, ops);
	}
	
	public static Coupon getLiveCouponByCodeNumber(String codeNumber) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("status").equal(CouponStatus.LIVE)
				.field("codeNumber").equal(codeNumber);
		
		return query.get();
	}

	public static List<Coupon> list(String status, String query1) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class);
		if (Utils.isNotEmpty(query1)) {
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE);
			query.filter("title", pattern);
		}
		if (Utils.isNotEmpty(status)) {
			query.field("status").equal(status);
		}
		return query.asList();
	}

	public static List<Coupon> promotionList(Integer limit, String status) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("visibility").equal(true);
		query.order("expireDate");
		if (limit != null)
			query.limit(limit);
		if (status != null)
			query.field("status").equal(status);
		return query.asList();
	}

	public static void delete(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("id").equal(id);
		datastore.delete(query);

	}

	public static void update(String id, Map<String, Object> updateMap) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class);
		if (Utils.isNotEmpty(id))
			query.field("id").equal(id);
		if (updateMap.size() > 0) {
			UpdateOperations<Coupon> ops = datastore.createUpdateOperations(Coupon.class);
			for (String key : updateMap.keySet()) {
				ops.set(key, updateMap.get(key));
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops, false);
		}
	}

	public static void updateStatus() {
		Calendar calendar = Calendar.getInstance();
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class);
		query.field("status").equal(CouponStatus.PENDING_LIVE);
		query.field("startDate").lessThanOrEq(calendar.getTime());
		UpdateOperations<Coupon> ops = datastore.createUpdateOperations(Coupon.class);
		ops.set("status", CouponStatus.LIVE);
		datastore.update(query, ops);

	}

	public static void updateLiveToExpired() {
		Calendar calendar = Calendar.getInstance();
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class);
		query.field("status").equal(CouponStatus.LIVE);
		query.field("expireDate").lessThanOrEq(calendar.getTime());
		UpdateOperations<Coupon> ops = datastore.createUpdateOperations(Coupon.class);
		ops.set("status", CouponStatus.EXPIRED);
		datastore.update(query, ops);

	}
	
	public static List<Coupon> couponExpired() {
		Calendar calendar = Calendar.getInstance();
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("archive").equal(false);
		query.field("completionDate").lessThanOrEq(calendar.getTime());
		return query.asList();

	}

	public static void increaseRedemptionCount(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("id").equal(id);
		UpdateOperations<Coupon> ops = datastore.createUpdateOperations(Coupon.class);
		ops.inc("redemptionCount");
		datastore.update(query, ops);
	}
	public static void decreaseRedemptionCount(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class).field("id").equal(id);
		UpdateOperations<Coupon> ops = datastore.createUpdateOperations(Coupon.class);
		ops.dec("redemptionCount");
		datastore.update(query, ops);
	}

	public static long getLiveCoupon() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class);
		query.field("status").equal(CouponStatus.LIVE);
		return query.countAll();

	}

	public static List<Coupon> getCouponByStatus(String status) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Coupon> query = datastore.createQuery(Coupon.class);
		if (Utils.isNotEmpty(status))
			query.field("status").equal(CouponStatus.valueOf(status));
		return query.asList();

	}
	
	public static List<CouponRedemption> getCouponRedemptionList(String code, String PlayerId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<CouponRedemption> query = datastore.createQuery(CouponRedemption.class).field("playerId").equal(PlayerId).field("archive").equal(true).field("couponCode").equal(code);
		
		return query.asList();

	}
	
	public static List<CouponRedemption> getUsedCouponList(String code) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<CouponRedemption> query = datastore.createQuery(CouponRedemption.class).field("couponCode").equal(code).field("archive").equal(true);
		
		return query.asList();

	}
	
	public static List<CouponRedemption> getAppliedCouponsByPlayerID(String PlayerId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<CouponRedemption> query = datastore.createQuery(CouponRedemption.class).field("playerId").equal(PlayerId).field("archive").equal(true);
		return query.asList();

	}
	
	public static CouponRedemption getAppliedCouponsTransactionByPlayerID(String PlayerId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<CouponRedemption> query = datastore.createQuery(CouponRedemption.class).field("playerId").equal(PlayerId).field("archive").equal(true);
		return query.get();
	}
	
	public static List<CouponRedemption> getExpiredCouponRedemptionList(String code) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<CouponRedemption> query = datastore.createQuery(CouponRedemption.class).field("archive").equal(false).field("couponCode").equal(code);
		
		return query.asList();

	}
	
	public static List<CouponRedemption> getCouponRedemptionListBuyIn(String code, String PlayerId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<CouponRedemption> query = datastore.createQuery(CouponRedemption.class).field("playerId").equal(PlayerId).field("couponCode").equal(code);
		
		return query.asList();

	}
	
	public static void persistRedemption(CouponRedemption couponRedemption) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(couponRedemption);
	}


}

