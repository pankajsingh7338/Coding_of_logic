package com.actolap.wse.dao;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.actolap.wse.analytics.aggregate.TempMaster;
import com.actolap.wse.analytics.aggregate.request.PlayerGameTransaction;
import com.actolap.wse.analytics.aggregate.request.PlayerGameTransaction.RoomTransactionEvent;
import com.actolap.wse.commons.PasswordEncoder;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.model.BankAccount;
import com.actolap.wse.model.CouponApplied;
import com.actolap.wse.model.SecurityPin;
import com.actolap.wse.model.game.poker.AffiliatePlayer.AffiliateReferralStatus;
import com.actolap.wse.model.game.poker.AffiliatePlayer.PlayerReferralStatus;
import com.actolap.wse.model.gameplay.GameType;
import com.actolap.wse.model.player.Category;
import com.actolap.wse.model.player.ChipType;
import com.actolap.wse.model.player.GamePlaySetting;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.player.PlayerDocument;
import com.actolap.wse.model.player.PlayerSecurityValidate;
import com.actolap.wse.model.player.PlayerStatus;
import com.actolap.wse.model.player.PlayerTransactionResult;
import com.actolap.wse.model.player.PlayerWallet;
import com.actolap.wse.model.promotion.Coupon;
import com.actolap.wse.model.promotion.CouponType;
import com.actolap.wse.model.promotion.OfferedAt;
import com.actolap.wse.mongo.ConnectionFactory;

public class PlayerDao {

	private static final String PASS_KEY = "#####wse-game-game-26-january####";

	public static void persist(Player player) throws NoSuchAlgorithmException, IOException { 
		if (Utils.isNotEmpty(player.getEmail())) 
			player.setEmail(player.getEmail().toLowerCase()); 
		if (player.getPassword() != null) 
			player.setPassword(PasswordEncoder.encode(player.getPassword(), PASS_KEY)); 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		datastore.save(player); 
	} 

 
	public static Boolean validatePassword(String id, String password) throws NoSuchAlgorithmException, IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id).field("password").equal(PasswordEncoder.encode(password, PASS_KEY));
		if (query.get() != null)
			return true;
		return false;
	}

	public static Boolean validateSecurePin(String id, String securePin) throws NoSuchAlgorithmException, IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id).field("securePin").equal(securePin);
		if (query.get() != null)
			return true;
		return false;
	}

	public static Player playerLogIn(String email, String password) throws NoSuchAlgorithmException, IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("email").equal(email.toLowerCase()).field("password").equal(PasswordEncoder.encode(password, PASS_KEY));
		return query.get(); 
	} 
 
	public static Player loginWithFaceBook(String email, String fbId) throws NoSuchAlgorithmException, IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("email").equal(email.toLowerCase()).field("fbId").equal(fbId);
		return query.get();
	}

	public static Player loginWithGoogle(String email, String googleId) throws NoSuchAlgorithmException, IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("email").equal(email.toLowerCase()).field("googleId").equal(googleId);
		return query.get();
	}

	public static Player loginWithMobile(Long mobile, String password) throws NoSuchAlgorithmException, IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("mobile").equal(mobile.toString()).field("password").equal(PasswordEncoder.encode(password, PASS_KEY));
		return query.get();
	}
	
	public static Player loginWithGameName(String gameName, String password) throws NoSuchAlgorithmException, IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("gameName").equal(gameName).field("password").equal(PasswordEncoder.encode(password, PASS_KEY));
		return query.get(); 
	} 
 
	public static Player loginWithUnverifiedMobile(Long mobileUnverified, String password) throws NoSuchAlgorithmException, IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("mobileUnverified").equal(mobileUnverified).field("password")
				.equal(PasswordEncoder.encode(password, PASS_KEY));
		return query.get();
	} 
   
	public static void updatePasswordToken(String id, String passwordToken, Boolean forgotTokenExpired) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (Utils.isNotEmpty(passwordToken)) { 
			ops.set("forgotPasswordToken", passwordToken); 
		} 
		if (forgotTokenExpired != null) { 
			ops.set("forgotTokenExpired", forgotTokenExpired); 
		} 
		datastore.update(query, ops); 
	} 
  
	public static void updateEmailToken(String id, String emailVerifiedToken) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("emailVerifiedToken", emailVerifiedToken); 
		datastore.update(query, ops); 
	} 
	
	public static void updateTime(String id, long forgetLinkTime) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("playerSecurityValidate.forgetLinkTime", forgetLinkTime); 
		datastore.update(query, ops); 
	} 
	
	public static void updateMaxCount(String id, int maxCount) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("playerSecurityValidate.forgetMaxCount", maxCount); 
		datastore.update(query, ops); 
	} 
	
	public static void updateGeneratePinTime(String id, long generatePinTime) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("playerSecurityValidate.generatePinTime", generatePinTime); 
		datastore.update(query, ops); 
	} 
	
	public static void updateMaxTimeForPin(String id, int maxCountPin) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("playerSecurityValidate.pinMaxCount", maxCountPin); 
		datastore.update(query, ops); 
	} 


	public static List<Player> search(String query1, Integer limit) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		if (Utils.isNotEmpty(query1)) {
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE);
			query.filter("userName", pattern);
		}
		if (limit != null) {
			query.limit(limit);
		}
		return query.asList();
	}
	
	public static List<Player> searchGameName(String query1, Integer limit) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		if (Utils.isNotEmpty(query1)) {
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE);
			query.filter("gameName", pattern);
		}
		if (limit != null) {
			query.limit(limit);
		}
		return query.asList();
	}

	public static List<Player> list(String query1) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		if (Utils.isNotEmpty(query1)) {
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE);
			query.filter("gameName", pattern);
		} 
		return query.asList(); 
	} 
	
	public static List<Player> getList(String query1, Date startDate, Date endDate, String userName) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class); 
		if (Utils.isNotEmpty(query1)) { 
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE); 
			query.filter("gameName", pattern); 
		} 
		if (Utils.isNotEmpty(userName)) { 
			Pattern pattern = Pattern.compile(userName, Pattern.CASE_INSENSITIVE); 
			query.filter("userName", pattern); 
		} 
		if(startDate != null) 
			query.field("createTime").greaterThanOrEq(startDate); 
		if(endDate != null) 
			query.field("createTime").lessThanOrEq(endDate); 
		query.order("-createTime"); 
		return query.asList();  
	} 
	
	public static Player getPlayerList(String id, String email) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class); 
		if (id != null) { 
			query.field("id").equal(id);
		} 
		if (Utils.isNotEmpty(email) && email != null) { 
			Pattern pattern = Pattern.compile(email, Pattern.CASE_INSENSITIVE); 
			query.filter("email", pattern); 
		} 
		query.order("-createTime"); 
		return query.get();  
	} 
	
	public static List<Player> getList() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class); 
		query.order("-createTime"); 
		return query.asList();  
	} 
	
	
      
	public static List<Player> avatarDoesNotExist() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("profile.avatar").doesNotExist();
		return query.asList();
	}

	public static Player getById(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		return query.get();
	} 
	
	public static List<Player> getById() { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		return query.asList(); 
	} 
 
	public static Player getByReferralCode(String referralCode) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("referralCode").equal(referralCode); 
		return query.get(); 
	} 

	public static Player getActivePlayerById(String id) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id).field("status").equal(PlayerStatus.ACTIVE); 
		return query.get(); 
	} 
 
	public static List<Player> getByIds(List<String> ids) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.criteria("id").in(ids);
		return query.asList();
	} 

	public static Player getByEmail(String email) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("email").equal(email);
		return query.get();
	}

	public static Player getByGameName(String gameName) {
		Pattern pattern = Pattern.compile("^" + gameName + "$", Pattern.CASE_INSENSITIVE);//This line will create a pattern to match words starts with "b", ends with "b" and its case insensitive too.
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("gameName").equal(pattern).retrievedFields(true, "gameName");
		return query.get(); 
	} 
      
	public static Player getByMobile(Long mobile) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("mobile").equal(mobile);
		return query.get(); 
	}

	public static Player getByFacebookId(String fbId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("fbId").equal(fbId);
		return query.get(); 
	} 
    
	public static Player getByGoogleId(String googleId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("googleId").equal(googleId);
		return query.get();
	}

	public static Player getByUserName(String userName) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("userName").equal(userName);
		return query.get();
	}

	public static void updateDocumentStatus(String id, List<PlayerDocument> documents) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class); 
		query.field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("documents", documents); 
		ops.set("lastUpdate", new Date()); 
		datastore.update(query, ops); 

	} 
	
	public static void updateIsKyc(String id, boolean status) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("kyc", status);
		ops.set("lastUpdate", new Date());
		datastore.update(query, ops);

	} 
	
	public static void updateDecodedPassKey(String email, String passKey) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("email").equal(email);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("decodePassKey", passKey);
		ops.set("lastUpdate", new Date());
		datastore.update(query, ops);

	} 
	
	public static void updateDecodedPassKeyForgotPassword(String id, String passKey) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("decodePassKeyForgetPassword", passKey);
		ops.set("lastUpdate", new Date());
		datastore.update(query, ops);

	} 
     
	public static void updateBankDetails(String id, List<BankAccount> bankDetail) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("bankDetails", bankDetail);
		ops.set("lastUpdate", new Date());
		datastore.update(query, ops); 

	} 
	
	public static void savePasswordSocial(String id , String password) throws NoSuchAlgorithmException, IOException { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if(password != null)
			ops.set("password", PasswordEncoder.encode(password, PASS_KEY));
		ops.set("lastUpdate", new Date());
		datastore.update(query, ops);
		
	} 
     
	public static void updateLastLogin(String id, String token) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("token", token);
		ops.set("lastLogin.time", new Date());
		ops.set("lastLogin.loggedIn", true);
		datastore.update(query, ops, false);
	}

	public static void updateFirstLogin(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("lastLogin.loggedIn", true); 
		datastore.update(query, ops); 
	} 
      
	public static void updateKycDetail(String id, Player player) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (player.getProfile().getFirstName() != null)
			ops.set("profile.firstName", player.getProfile().getFirstName());
		if (player.getProfile().getLastName() != null)
			ops.set("profile.lastName", player.getProfile().getLastName());
		if (player.getProfile().getLastName() != null)
			ops.set("profile.maritalStatus", player.getProfile().getMaritalStatus());
		if (player.getProfile().getDob() != null)
			ops.set("profile.dob", player.getProfile().getDob());
		if (player.getProfile().getDob() != null)
			ops.set("profile.address", player.getProfile().getAddress());
		datastore.update(query, ops, false);
	}

	public static void updateBuyInDetail(String id, Long deposited, Long bonusChips, Category toUpdate) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (toUpdate != null) {
			ops.set("playerClass", toUpdate.getTitle());
			ops.set("classUpdateVipPoint.classChangeDate", new Date());
			ops.set("classUpdateVipPoint.everyMonthVipPoint", 0);
			ops.set("classUpdateVipPoint.vipPointToStayInClass", 0);
		}
		if (deposited != null) {
			ops.inc("wallet.cash", deposited);
		}
		if (bonusChips != null) {
			ops.inc("wallet.bonus", bonusChips);
		}
		ops.set("vipPointWB", 0);
		ops.set("lastBuyInOrWithdraw", new Date());
		ops.set("lastUpdate", new Date());
		datastore.update(query, ops);
	}

	public static void update(String id, Map<String, Object> playerMap) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id); 
		if (playerMap.size() > 0) {
			UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
			for (String key : playerMap.keySet()) {
			 if(!key.equals("nextLevelPoints")) 
				ops.set(key, playerMap.get(key));
			} 
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops, false);
		} 
	} 
	
	public static void updatePlayerSecurity(String id, PlayerSecurityValidate playerSecurityValidate) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (playerSecurityValidate != null) { 
			ops.set("playerSecurityValidate.forgetLinkTime", playerSecurityValidate.getForgetLinkTime()); 
			ops.set("playerSecurityValidate.generatePinTime", playerSecurityValidate.getGeneratePinTime()); 
			ops.set("playerSecurityValidate.forgetMaxCount", playerSecurityValidate.getForgetMaxCount());
			ops.set("playerSecurityValidate.pinMaxCount", playerSecurityValidate.getPinMaxCount());
		} 
		datastore.update(query, ops); 
	} 
	
	
	public static void updateConfiguration(Map<String, Object> playerMap) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		if (playerMap.size() > 0) {
			UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
			for (String key : playerMap.keySet()) {
				ops.set(key, playerMap.get(key));
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops, false);
		}
	}

	// public static void delete(String id) {
	// Datastore datastore = ConnectionFactory.getInstance().getDatastore();
	// Query<Player> query =
	// datastore.createQuery(Player.class).field("id").equal(id);
	// datastore.delete(query);
	//
	// }

	public static void updateEmailStatus(String id) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("emailVerified", true); 
		datastore.update(query, ops); 
	} 
	  
	public static void updateForgotToken(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("forgotTokenExpired", false); 
		datastore.update(query, ops); 
	} 
      
	public static void updateMobileNumber(String id, Long mobile) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (mobile != null) {
			ops.set("mobile", mobile);
		} 
		datastore.update(query, ops); 
	}  
	  
	public static void updateGameName(String id, String gameName) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (gameName != null) { 
			ops.set("gameName", gameName); 
		} 
		datastore.update(query, ops); 
	} 
	  
	public static void updateProfile(String id, String firtsName, String lastName) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (firtsName != null && lastName != null) { 
			ops.set("profile.firstName", firtsName); 
			ops.set("profile.lastName", lastName); 
			ops.set("userName", firtsName + " "+ lastName);
		} 
		datastore.update(query, ops); 
	} 
	  
	public static void updateSecurityPin(String id, List<SecurityPin> securityPin) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("securityPin", securityPin);
		ops.set("lastUpdate", new Date());
		datastore.update(query, ops);

	}

	public static void updateUnverifiedMobile(String id, Long unverifiedMobile) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (unverifiedMobile != null) {
			ops.set("mobileUnverified", unverifiedMobile);
		}
		datastore.update(query, ops);
	}

	public static void resetWithdrawalVIP(String id, long vipChips, long newBalance) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (vipChips != 0) {
			ops.inc("wallet.vip", vipChips);
			// ops.inc("classUpdateVipPoint.vipPointToStayInClass", vipChips);
			// ops.inc("classUpdateVipPoint.everyMonthVipPoint", vipChips);
		}
		ops.set("vipPointWB", 0);
		ops.set("lastBuyInOrWithdraw", new Date());
		ops.set("stats.balance", newBalance); 
		datastore.update(query, ops); 
	} 
 
	public static void updateWallet(String id, Long freeChips, Long bonusChips, Long cashChips, Long vipChips, Long tournamentPoints) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (freeChips != null) {
			ops.inc("wallet.free", freeChips);
			ops.set("wallet.lastClaimDate", new Date());
		}
		if (bonusChips != null) {
			ops.inc("wallet.bonus", bonusChips);
		}
		if (cashChips != null) {
			ops.inc("wallet.cash", cashChips);
		}
		if (tournamentPoints != null) {
			ops.inc("wallet.tournamentPoints", tournamentPoints);
		}
		if (vipChips != null) {
			ops.inc("wallet.vip", vipChips);
			ops.inc("vipPointWB", vipChips);
			// ops.inc("classUpdateVipPoint.vipPointToStayInClass", vipChips);
			// ops.inc("classUpdateVipPoint.everyMonthVipPoint", vipChips);
		}
		ops.set("signupCoupon", true);
		datastore.update(query, ops);
	} 
	  
	public static void updateChipConvertWallet(String id, Long bonusChips, Long cashChips, Long vipChips) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		
		if (bonusChips != null) {
			ops.set("wallet.bonus", bonusChips);
		}
		if (cashChips != null) {
			ops.set("wallet.cash", cashChips);
		}
		
		if (vipChips != null) {
			ops.set("wallet.vip", vipChips);
			ops.set("vipPointWB", vipChips);
			// ops.inc("classUpdateVipPoint.vipPointToStayInClass", vipChips);
			// ops.inc("classUpdateVipPoint.everyMonthVipPoint", vipChips);
		}
		ops.set("signupCoupon", true);
		datastore.update(query, ops);
	}
	
	public static void updateChipsWithdrawReverse(String id, Long cashChips) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		
		if (cashChips != null) {
			ops.inc("wallet.cash", cashChips);
		}
		datastore.update(query, ops);
	}

	public static void addCoupon(Coupon coupon, String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		CouponApplied couponApplied = new CouponApplied();
		Player player = PlayerDao.getById(id);

		if (coupon.getCouponType().equals(CouponType.BUYIN))
			couponApplied.setChips(coupon.getBuyIn().getOffered());
		else if (coupon.getCouponType().equals(CouponType.TOURNAMENT))
			couponApplied.setChips(coupon.getTournament().getPoints());
		else
			couponApplied.setChips(coupon.getSignUp().getAvail());
		couponApplied.setRedemptionType(coupon.getRedemptionType());
		couponApplied.setCouponType(coupon.getCouponType());
		couponApplied.setCouponCode(coupon.getCodeNumber());
		couponApplied.setDate(new Date());
		List<CouponApplied> couponCodeList1 = player.getCouponApplied();
		couponCodeList1.add(couponApplied);
		ops.set("couponApplied", couponCodeList1);
		datastore.update(query, ops);
	}

	public static void addTournamentCouponList(Coupon coupon, String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		List<String> winning = new ArrayList<>();
		List<String> registering = new ArrayList<>();
		Player player = PlayerDao.getById(id);
		Map<String, List<String>> tournamentCouponAppliedList = player.getTournamentCouponAppliedList();
		if (coupon.getOfferedAt() != null && coupon.getOfferedAt().equals(OfferedAt.WINNING)) {
			winning.add(coupon.getCodeNumber());
			if (!tournamentCouponAppliedList.containsKey("WINNING"))
				tournamentCouponAppliedList.put("WINNING", winning);
			else {
				tournamentCouponAppliedList.get("WINNING").add(coupon.getCodeNumber());
			}
		} else {
			registering.add(coupon.getCodeNumber());
			if (!tournamentCouponAppliedList.containsKey("REGISTERING"))
				tournamentCouponAppliedList.put("REGISTERING", registering);
			else {
				tournamentCouponAppliedList.get("REGISTERING").add(coupon.getCodeNumber());
			}
		}
		// List<CouponApplied> couponCodeList1=player.getCouponApplied();
		// couponCodeList1.add(couponApplied);
		ops.set("tournamentCouponAppliedList", tournamentCouponAppliedList);
		datastore.update(query, ops);
	}

	// for buyin coupon

	public static void addBuyInCoupon(Coupon coupon, String id, String status) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		
		if (!status.equals("empty")) { 
			ops.set("buyInCouponAvailable", coupon.getCodeNumber()); 
			datastore.update(query, ops); 
		} else { 
			ops.set("buyInCouponAvailable", ""); 
			datastore.update(query, ops); 
		} 
	} 
  
	public static void updateReferralDetail(String id, String referredBy, Long referralChips, boolean referredPlayer) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (referredBy != null) {
			ops.set("referredBy", referredBy);
		}
		if (referralChips != null) {
			ops.inc("referralEarnChips", referralChips);
		}
		if (referredPlayer) {
			ops.inc("referredPlayers");
		}

		datastore.update(query, ops);
	}

	public static void updateWalletGameServerIncrement(String id, Long freeChips, Long bonusChips, Long cashChips, Double vipChips) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (freeChips != null) {
			ops.inc("wallet.free", freeChips);
		}
		if (bonusChips != null) {
			ops.inc("wallet.bonus", bonusChips);
		}
		if (cashChips != null) {
			ops.inc("wallet.cash", cashChips);
		}
		if (vipChips != null) {
			ops.inc("wallet.vip", vipChips);
			ops.inc("vipPointWB", vipChips);
			ops.inc("classUpdateVipPoint.vipPointToStayInClass", vipChips);
			ops.inc("classUpdateVipPoint.everyMonthVipPoint", vipChips);
		}
		datastore.update(query, ops);
	}

	public static PlayerTransactionResult updateWalletGameServer(String id, Long freeChips, Long bonusChips, Long cashChips, Long vipChips) {
		PlayerTransactionResult result = new PlayerTransactionResult();
		boolean success;
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (freeChips != null) {
			ops.inc("wallet.free", freeChips);
		}
		if (bonusChips != null) {
			ops.inc("wallet.bonus", bonusChips);
		}
		if (cashChips != null) {
			ops.inc("wallet.cash", cashChips); 
		}
		if (vipChips != null) {
			ops.inc("wallet.vip", vipChips);
			ops.inc("vipPointWB", vipChips);
			ops.inc("classUpdateVipPoint.vipPointToStayInClass", vipChips);
			ops.inc("classUpdateVipPoint.everyMonthVipPoint", vipChips);
		}
		Player player = datastore.findAndModify(query, ops, false);
		success = validatePlayerWallet(player, freeChips, bonusChips, cashChips, vipChips);
		if (!success) {
			rollBackLastTransaction(id, freeChips, bonusChips, cashChips, vipChips, datastore);
		}
		result.setSuccess(success);
		result.setPlayer(player);
		return result;
	}

	public static void updateGamePlaySettingGameServer(String id, GamePlaySetting gamePlaySetting) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (gamePlaySetting.getHandStrength() != null)
			ops.set("gamePlaySetting.handStrength", gamePlaySetting.getHandStrength());
		if (gamePlaySetting.getSound() != null)
			ops.set("gamePlaySetting.sound", gamePlaySetting.getSound());
		datastore.update(query, ops);
	}

	private static void rollBackLastTransaction(String id, Long freeChips, Long bonusChips, Long cashChips, Long vipChips, Datastore datastore) {
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (freeChips != null) {
			ops.inc("wallet.free", -1 * freeChips);
		}
		if (bonusChips != null) {
			ops.inc("wallet.bonus", -1 * bonusChips);
		}
		if (cashChips != null) {
			ops.inc("wallet.cash", -1 * cashChips);
		}
		if (vipChips != null) {
			ops.inc("wallet.vip", -1 * vipChips);
			ops.inc("vipPointWB", -vipChips);
			ops.inc("classUpdateVipPoint.vipPointToStayInClass", -vipChips);
			ops.inc("classUpdateVipPoint.everyMonthVipPoint", -vipChips);

		}
		datastore.update(query, ops);
	}

	private static boolean validatePlayerWallet(Player player, Long freeChips, Long bonusChips, Long cashChips, Long vipChips) {
		boolean success = true;
		if (freeChips != null && player.getWallet().getFree() < 0) {
			success = false;
		} else if (bonusChips != null && player.getWallet().getBonus() < 0) {
			success = false;
		} else if (cashChips != null && player.getWallet().getCash() < 0) {
			success = false;
		} else if (vipChips != null && player.getWallet().getVip() < 0) {
			success = false;
		}
		return success;
	}

	public static void updateTermStatus(String id, boolean termsAccepted, String termVersion, String ip, int freeChips, String token) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("legal.termsAccepted", termsAccepted);
		ops.set("legal.termVersion", termVersion);
		ops.set("legal.ip", ip);
		ops.set("legal.termDate", new Date());
		ops.set("lastLogin.time", new Date());
		ops.inc("wallet.free", freeChips);
		ops.set("token", token);

		datastore.update(query, ops);
	}

	public static void resetPassword(String playerId, String newPassword, String country) throws NoSuchAlgorithmException, IOException {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(playerId);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("password", PasswordEncoder.encode(newPassword, PASS_KEY));
		if(country != null)
			ops.set("currentCountry", country);
		datastore.update(query, ops);
	}

	public static boolean updatePassword(String playerId, String newPassword, String oldPassword, String ip, String country) throws NoSuchAlgorithmException, IOException {
		boolean success = false; 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(playerId); 
		if (Utils.isNotEmpty(oldPassword)) { 
			query.field("password").equal(PasswordEncoder.encode(oldPassword, PASS_KEY)); 
		} 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("password", PasswordEncoder.encode(newPassword, PASS_KEY)); 
		if(ip != null) 
			ops.set("ip", ip); 
		if(country != null) 
			ops.set("currentCountry", country); 
		UpdateResults result = datastore.update(query, ops); 
		if (result.getUpdatedCount() > 0) { 
			success = true; 
		} 
		return success; 
	} 
	
	public static boolean validateOldPassword(String playerId, String oldPassword) throws NoSuchAlgorithmException, IOException {
		boolean success = false; 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(playerId); 
		if (Utils.isNotEmpty(oldPassword)) { 
			if(query.get().getPassword().equals(PasswordEncoder.encode(oldPassword, PASS_KEY))) 
				success = true;
		}
		return success; 
	 }
      
	public static void updateDocumentDetail(String id, List<PlayerDocument> documents) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (documents != null && !documents.isEmpty()) {
			ops.set("documents", documents);
		}
		datastore.update(query, ops);
	}

	public static void updateDocumentByType(String id, PlayerDocument document) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		if (document != null && document.getType() != null) {
			query.field("documents.type").equal(document.getType());
		}
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if (document != null && document.getType() != null && document.getUrl() != null && document.getUpload() != null && document.getStatus() != null) {
			ops.set("documents.$.upload", document.getUpload());
			if (document.getNumber() != null)
				ops.set("documents.$.number", document.getNumber());
			ops.set("documents.$.url", document.getUrl());
			ops.set("documents.$.status", document.getStatus());
		}
		datastore.update(query, ops);
	}
	
	public static void updateKycDocumentByType(String id, List<PlayerDocument> document) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("documents", document);
		datastore.update(query, ops);
	}

	public static void updateAffiliateReferralCode(String id, String affiliateReferralCode) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("affiliateReferralCode", affiliateReferralCode);
		datastore.update(query, ops);
	}

	public static void updateReferralCode(String id, String affiliateReferralCode) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		ops.set("referralCode", affiliateReferralCode);
		datastore.update(query, ops);
	}

	public static void updateAffiliateId(String id, String affiliateId, PlayerReferralStatus status) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if(status.equals(PlayerReferralStatus.REJECTED)) 
			ops.set("preAffiliateId", affiliateId);
		else
			ops.set("affiliateId", affiliateId);  
		datastore.update(query, ops); 
	} 
	  
	public static void updateRejectedAffiliateId(String id, String affiliateId, AffiliateReferralStatus affiliatestatus) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id);
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
		if(affiliatestatus.equals(AffiliateReferralStatus.REJECTED)) 
			ops.set("preAffiliateId", affiliateId);  
		else 
			ops.set("affiliateId", affiliateId);  
		datastore.update(query, ops); 
	}

	public static void elearingSummary(TempMaster tempMaster) {
		if (tempMaster.getPlayerId() != null) {
			Datastore datastore = ConnectionFactory.getInstance().getDatastore();
			Query<Player> query = datastore.createQuery(Player.class).field("id").equal(tempMaster.getPlayerId());
			UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
			boolean success = false;
			success = Utils.build(ops, "eSummary.quizTaken", tempMaster.getQuizTaken(), success);
			success = Utils.build(ops, "eSummary.totalQuestion", tempMaster.getTotalQuestion(), success);
			success = Utils.build(ops, "eSummary.completedQuiz", tempMaster.getCompletedQuiz(), success);
			success = Utils.build(ops, "eSummary.correctAnswer", tempMaster.getCorrectAnswer(), success);
			success = Utils.build(ops, "eSummary.incorrectAnswer", tempMaster.getIncorrectAnswer(), success);
			if (success)
				datastore.update(query, ops);
		}
	}

	public static void updateStats(TempMaster tempMaster) {
		if (tempMaster.getPlayerId() != null) {
			Datastore datastore = ConnectionFactory.getInstance().getDatastore();
			Query<Player> query = datastore.createQuery(Player.class).field("id").equal(tempMaster.getPlayerId());
			UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
			boolean success = false;
			success = Utils.build(ops, "stats.gamesWon", tempMaster.getGamesWon(), success);
			success = Utils.build(ops, "stats.deposited", tempMaster.getMoneyDeposit(), success);
			success = Utils.build(ops, "stats.withdrawn", tempMaster.getMoneyDrawn(), success);
			success = Utils.build(ops, "stats.duration", tempMaster.getPlayedTime(), success);
			success = Utils.build(ops, "stats.rake", tempMaster.getRakeGenerated(), success);
			if (tempMaster.getGameType() != null) {
				if (tempMaster.getGameType() == GameType.CASH) {
					success = Utils.build(ops, "stats.wagered", tempMaster.getWagered(), success);
				}
				success = Utils.build(ops, "stats.gameSummary." + tempMaster.getGameType().toString() + ".played", 1, success);
				success = Utils.build(ops, "stats.gameSummary." + tempMaster.getGameType().toString() + ".duration", tempMaster.getPlayedTime(), success);
			}

			if (success)
				datastore.update(query, ops);
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean buyIn(int chips, Player existingPlayer, GameType gameType, String tableId, String roomId, Lock lock) {
		boolean eligible = false;
		lock.lock();
		try {
			Datastore datastore = ConnectionFactory.getInstance().getDatastore();
			Query<Player> query = datastore.createQuery(Player.class).field("id").equal(existingPlayer.getId());
			query.queryNonPrimary();
			Player player = query.get();
			PlayerWallet playerWallet = player.getWallet();
			switch (gameType) {
			case CASH:
				if (playerWallet.getCash() >= chips) {
					PlayerTransactionResult result = updateWalletGameServer(player.getId(), null, null, (long) -chips, null);
					if (result.isSuccess()) {
						PlayerGameTransaction.build(RoomTransactionEvent.room_enter, gameType, existingPlayer.getId(), tableId, roomId, chips, result.getPlayer().getWallet()
								.getCash(), ChipType.REAL);
						eligible = true;
					}
				}
				break;
			case FREE:
				if (playerWallet.getFree() >= chips) {
					PlayerTransactionResult result = updateWalletGameServer(player.getId(), (long) -chips, null, null, null);
					if (result.isSuccess()) {
						PlayerGameTransaction.build(RoomTransactionEvent.room_enter, gameType, existingPlayer.getId(), tableId, roomId, chips, result.getPlayer().getWallet()
								.getFree(), ChipType.FREE);
						eligible = true;
					}
				}
				break;
			case DEMO:
				eligible = true;
				break;
			}
		} finally {
			lock.unlock();
		}
		return eligible;

	}

	public static void returnWalletMoney(int chips, Player existingPlayer, GameType gameType, String tableId, String roomId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(existingPlayer.getId());
		Player player = query.get();
		if (player != null) {
			switch (gameType) {
			case CASH: {
				PlayerTransactionResult result = updateWalletGameServer(player.getId(), null, null, (long) +chips, null);
				if (result.isSuccess()) {
					PlayerGameTransaction.build(RoomTransactionEvent.room_exit, gameType, existingPlayer.getId(), tableId, roomId, chips, result.getPlayer().getWallet().getCash(),
							ChipType.REAL);
				}
			}
				break;
			case FREE:
				PlayerTransactionResult result = updateWalletGameServer(player.getId(), (long) +chips, null, null, null);
				if (result.isSuccess()) {
					PlayerGameTransaction.build(RoomTransactionEvent.room_exit, gameType, existingPlayer.getId(), tableId, roomId, chips, result.getPlayer().getWallet().getFree(),
							ChipType.FREE);
				}
				break;
			case DEMO:
				break;
			}
		}
	}
	
	public static void updatePlayerBankBalanceSheet(String id, List<String> playerBankBalanceSheet) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("playerBankBalanceSheet", playerBankBalanceSheet); 
		datastore.update(query, ops); 
	} 
	
	public static void updatePlayerBan(String id, boolean isBan) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		ops.set("banPlayer", isBan); 
		datastore.update(query, ops); 
	} 
	
	public static void updateBankBalanceSheetAfterWithdraw(String id, Map<String, Object> requestMap) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Player> query = datastore.createQuery(Player.class);
		query.field("id").equal(id);
		if (requestMap.size() > 0) {
			UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class);
			for (Entry<String, Object> entry : requestMap.entrySet()) {
				ops.set(entry.getKey(), entry.getValue());
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops, false);
		}
	}
	
	public static void updatePlayerWalletCash(String id, Long cashChips ) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<Player> query = datastore.createQuery(Player.class).field("id").equal(id); 
		UpdateOperations<Player> ops = datastore.createUpdateOperations(Player.class); 
		if (cashChips != null) {
			ops.inc("wallet.cash", cashChips);
		}
		datastore.update(query, ops); 
	} 
	
	

}
