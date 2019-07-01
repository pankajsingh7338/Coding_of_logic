package com.actolap.wse.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actolap.config.Config;
import com.actolap.wse.Constants;
import com.actolap.wse.CouponConstant;
import com.actolap.wse.PaymentConstant;
import com.actolap.wse.UserPermission;
import com.actolap.wse.WithdrawConstant;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.dao.CategoryDao;
import com.actolap.wse.dao.CategoryMasterDao;
import com.actolap.wse.dao.GlobalStatsDao;
import com.actolap.wse.dao.MembershipDao;
import com.actolap.wse.dao.PaymentGatewayDao;
import com.actolap.wse.dao.PermissionDao;
import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.dao.PokerConfigurationDao;
import com.actolap.wse.fe.controller.ConfigController;
import com.actolap.wse.inmemory.memcache.GlobalCachedManager;
import com.actolap.wse.model.CricketPoints.CricketPointScoreStatus;
import com.actolap.wse.model.EntityType;
import com.actolap.wse.model.GlobalStats;
import com.actolap.wse.model.Image.ImageStatus;
import com.actolap.wse.model.WithdrawStatus;
import com.actolap.wse.model.affiliate.Affiliate.AffiliateCommissionType;
import com.actolap.wse.model.affiliate.Affiliate.AffiliateType;
import com.actolap.wse.model.backoffice.Permission;
import com.actolap.wse.model.elearning.Level;
import com.actolap.wse.model.elearning.PlayerAchievement.PlayerAchievementStatus;
import com.actolap.wse.model.elearning.Question.QuestionStatus;
import com.actolap.wse.model.game.poker.AffiliatePlayer.PlayerReferralStatus;
import com.actolap.wse.model.game.poker.AffiliateStatus;
import com.actolap.wse.model.game.poker.GameMode;
import com.actolap.wse.model.game.poker.PokerConfiguration;
import com.actolap.wse.model.game.poker.TableStatus;
import com.actolap.wse.model.game.poker.TournamentStatus;
import com.actolap.wse.model.gameplay.GameType;
import com.actolap.wse.model.leaderBoard.LeaderBoard.LeaderBoardEntity;
import com.actolap.wse.model.leaderBoard.LeaderBoard.LeaderBoardStatus;
import com.actolap.wse.model.payment.PaymentGateway;
import com.actolap.wse.model.payment.Payment.PaymentStatus;
import com.actolap.wse.model.player.Category;
import com.actolap.wse.model.player.CategoryMaster;
import com.actolap.wse.model.player.Membership;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.player.PlayerClass;
import com.actolap.wse.model.playerAchievement.PlayerAchievementEntity.Offered;
import com.actolap.wse.model.playerAchievement.PlayerAchievementEntity.PlayerAchievementGiven;
import com.actolap.wse.model.playerAchievement.PlayerAchievementEntity.levels;
import com.actolap.wse.model.promotion.Coupon.PaymentType;
import com.actolap.wse.model.promotion.CouponStatus;
import com.actolap.wse.model.promotion.CouponType;
import com.actolap.wse.model.promotion.OfferedAt;
import com.actolap.wse.model.promotion.PromotionOtherStatus;
import com.actolap.wse.model.promotion.RedemptionType;
import com.actolap.wse.model.report.AffiliateMetric;
import com.actolap.wse.model.report.AffiliatePlayerExportCsv;
import com.actolap.wse.model.report.AffiliatePlayerMatric;
import com.actolap.wse.model.report.AffiliateReport;
import com.actolap.wse.model.report.Dimensions;
import com.actolap.wse.model.report.MarketingReportConfig;
import com.actolap.wse.model.report.MarketingReportMatric;
import com.actolap.wse.model.report.Metric;
import com.actolap.wse.model.report.Operation;
import com.actolap.wse.model.report.Report.DATERANGE;
import com.actolap.wse.model.report.ReportConfig;
import com.actolap.wse.mongo.ConnectionFactory;
import com.actolap.wse.scheduler.CouponRedemScheduler;
import com.actolap.wse.scheduler.CouponScheduler;
import com.actolap.wse.scheduler.LeaderBoardScheduler;
import com.actolap.wse.scheduler.PaymentReconcilScheduler;
import com.actolap.wse.scheduler.PlayerAchievementDistributionScheduler;
import com.actolap.wse.scheduler.PlayerAchievementScheduler;
import com.actolap.wse.scheduler.PlayerBonusScheduler;
import com.actolap.wse.scheduler.PlayerClassUpdateScheduler;
import com.actolap.wse.scheduler.PlayerGlobalStatsScheduler;
import com.actolap.wse.scheduler.PlayerTournamentChipsScheduler;
import com.actolap.wse.scheduler.PokerTableScheduler;
import com.actolap.wse.scheduler.PromotionOtherScheduler;
import com.actolap.wse.scheduler.QuizScheduler;
import com.actolap.wse.scheduler.ReportScheduler;
import com.actolap.wse.scheduler.TournamentScheduler;
import com.actolap.wse.scheduler.WinnerPriceDistributionScheduler;
import com.actolap.wse.schedulers.CategoryScheduler;
import com.actolap.wse.schedulers.GameQueueScheduler;
import com.actolap.wse.schedulers.QueueScheduler;


public class StartUp {

	private static final Logger LOG = LoggerFactory.getLogger(StartUp.class);

	public static void initialize(String path, Properties properties) {
		ConfigController.SWAGGER = loadFile("swagger.json");
		Constants.CROSS_DOMAIN = loadCross("cross.xml");
		Config.cph = Integer.parseInt(properties.getProperty("mongo.cph", "500")); 
		try {
			Config.SYSTEM_HOST = InetAddress.getLocalHost().getHostName();
		} catch (Exception ign) { 
		} 
		Utils.loadMongo(LOG, properties);
		Config.PROCESS_NAME = properties.getProperty("process", "NA");
		Config.FROM_EMAIL = properties.getProperty("from.email", Config.FROM_EMAIL);
		Config.PROTOCOL = properties.getProperty("protocol", Config.PROTOCOL);
		Config.WSE_LYVE_LOGO = properties.getProperty("wse.lyve.logo", Config.WSE_LYVE_LOGO);
		Config.AWS_BUCKET = properties.getProperty("aws.bucket", Config.AWS_BUCKET);
		Config.DOMAIN = properties.getProperty("domain", Config.DOMAIN);
		Config.dev = Boolean.parseBoolean(properties.getProperty("isDev", "true"));
		ConnectionFactory.getInstance();
		reportInit();
		affiliateReportInit();
		affiliatePlayerConfigInit(); 
		marketingReportInit();
		membershipInit();
		categoryInit();
		pokerInit();
		affiliateInit();
		permissionCreate();
		playerInit();
		playerAchievementInit();
		playerRecortdInit();
		quizInit();
		cricketInit();
		leaderBoardInit();
		paymentTransactionInit();
		withdrawRequestInit();
		entityTypeInit();
		playerAchievementEntityInit();
		couponInit();
		paymentGatewayInit();
		PokerTableScheduler.init();
		CouponRedemScheduler.init();
		TournamentScheduler.init();
		CouponScheduler.init();
		PromotionOtherScheduler.init();
		QueueScheduler.init();
		PlayerBonusScheduler.init();
		PlayerTournamentChipsScheduler.init();
		QuizScheduler.init();
		GameQueueScheduler.init();
		CategoryScheduler.init();
		ReportScheduler.init();
		PlayerClassUpdateScheduler.init();
		WinnerPriceDistributionScheduler.init();
		PlayerGlobalStatsScheduler.init();
		PaymentReconcilScheduler.init();
		PlayerAchievementScheduler.init();
		LeaderBoardScheduler.init();
		//PlayerAchievementDistributionScheduler.init();
		checkandCreateDirs();
		GlobalCachedManager.init(properties);
	}

	private static String loadCross(String path) {
		InputStream in = StartUp.class.getClassLoader().getResourceAsStream(path);
		StringBuilder result = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			char[] buf = new char[1024];
			int r = 0;

			while ((r = reader.read(buf)) != -1) {
				result.append(buf, 0, r);

			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return result.toString();
	}

	private static String loadFile(String path) {
		String resultS = null;
		InputStream in = StartUp.class.getClassLoader().getResourceAsStream(path);
		StringBuilder result = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			char[] buf = new char[1024];
			int r = 0;

			while ((r = reader.read(buf)) != -1) {
				result.append(buf, 0, r);

			}
			resultS = result.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}
		return resultS;
	}
	

	private static void playerInit() {

		Map<String, PlayerClass> playerClassMap = new HashMap<String, PlayerClass>();
		playerClassMap.put("Crystal", PlayerClass.CRYSTAL);
		playerClassMap.put("Pearl", PlayerClass.PEARL);
		playerClassMap.put("Topaz", PlayerClass.TOPAZ);
		playerClassMap.put("Sapphire", PlayerClass.SAPPHIRE);
		playerClassMap.put("Diamond", PlayerClass.DIAMOND);
		playerClassMap.put("Ruby", PlayerClass.RUBY);
		playerClassMap.put("Opal", PlayerClass.OPAL);

		InMemory.playerClassMap.putAll(playerClassMap);

		Map<String, Dimensions> playerDimMap = new LinkedHashMap<String, Dimensions>();
		playerDimMap.put("First Name", Dimensions.firstName);
		playerDimMap.put("Last Name", Dimensions.lastName);
		playerDimMap.put("Gender", Dimensions.gender);
		playerDimMap.put("State", Dimensions.state);
		playerDimMap.put("City", Dimensions.city);
		playerDimMap.put("Email", Dimensions.email);
		playerDimMap.put("Game Name", Dimensions.gameName);
		playerDimMap.put("Date", Dimensions.date);
		playerDimMap.put("Mobile", Dimensions.mobile);
		playerDimMap.put("Status", Dimensions.playerStatus);
		playerDimMap.put("Last Login", Dimensions.lastLogin);
		playerDimMap.put("Net Financial Status", Dimensions.netFinancialStatus);
		playerDimMap.put("Cash Chips In Hand", Dimensions.cashChipsInHand);
		playerDimMap.put("VIP Points In Hand", Dimensions.vipPointsInHand);
		InMemory.playerDimMap.putAll(playerDimMap);

		List<Player> players = PlayerDao.avatarDoesNotExist();
		players.forEach(player -> {
			Map<String, Object> mongoMap = null;
			if (player.getProfile() != null) {
				mongoMap = new HashMap<String, Object>();
				mongoMap.put("profile.avatar", Constants.defaultAvatar);
				PlayerDao.update(player.getId(), mongoMap);
			}

		});
	}

	private static void reportInit() {
		Map<String, Dimensions> filterMap = new HashMap<String, Dimensions>();
		filterMap.put("Player", Dimensions.playerId);
		filterMap.put("Tournament", Dimensions.tournament);
		filterMap.put("Affiliate", Dimensions.affiliateId);

		Map<String, Metric> measureMap = new HashMap<String, Metric>();
		measureMap.put("Game Played", Metric.gamePlayed);
		measureMap.put("Money Deposit", Metric.moneyDeposit);
		measureMap.put("Money Drawn", Metric.moneyDrawn);
		measureMap.put("Money Drawn Charge", Metric.withdrawCharge);
		measureMap.put("Vip Point Deducted", Metric.vipPointsDeducted);
		measureMap.put("Rake Generated", Metric.rakeGenerated);
		measureMap.put("Tds Generated", Metric.tdsGenerated);
		measureMap.put("Bonus Issued", Metric.bonusIssued);
		measureMap.put("Bonus Released", Metric.bonusReleased);
		measureMap.put("Sign Up", Metric.signUp);
		measureMap.put("Vip Points Issued", Metric.vipPointsIssued);
		// for Tournament
		measureMap.put("Price Money", Metric.priceMoney);
		measureMap.put("Enrolled", Metric.enrolled);
		measureMap.put("Players", Metric.players);
		measureMap.put("Collections", Metric.collections);
		// New
		measureMap.put("Tournaments Participated", Metric.tournamentsParticipated);
		measureMap.put("Tournament Lost", Metric.tournamentLost);
		measureMap.put("Tournament Won", Metric.tournamentWon);
		measureMap.put("Rake Refunded", Metric.rakeRefunded);
		measureMap.put("TDS Refunded", Metric.tdsRefunded);
		measureMap.put("Tournament Spending", Metric.tournamentColl);
		measureMap.put("Discount Received", Metric.discountReceived);
		measureMap.put("Chip Wagered", Metric.wagered);
		measureMap.put("Net Profit", Metric.net);
		measureMap.put("Bonus Chips Encashed", Metric.bonusChipsEncashed);
		measureMap.put("Vip Points Encashed", Metric.vipPointsEncashed);
		measureMap.put("Games Won", Metric.gamesWon);
		measureMap.put("Games Lost", Metric.gamesLost);
		measureMap.put("Chips Won", Metric.won);
		measureMap.put("Played Time", Metric.playedTime);
		measureMap.put("Tds Deducted", Metric.tdsDeducted);
		// Affiliate
		measureMap.put("Commission", Metric.commission);

		Map<String, Dimensions> dimMap = new HashMap<String, Dimensions>();
		dimMap.put("Player", Dimensions.playerId);
		dimMap.put("Game", Dimensions.gameplayId);
		dimMap.put("Table", Dimensions.tableId);
		dimMap.put("Tournament", Dimensions.tournament);
		dimMap.put("Date", Dimensions.date);
		dimMap.put("Game Type", Dimensions.gameType);
		dimMap.put("First Name", Dimensions.firstName);
		dimMap.put("Last Name", Dimensions.lastName);
		dimMap.put("Game Name", Dimensions.gameName);
		dimMap.put("Affiliate Name", Dimensions.affiliateName);
		dimMap.put("Game Mode", Dimensions.mode);
		dimMap.put("Gender", Dimensions.gender);
		dimMap.put("State", Dimensions.state);
		dimMap.put("City", Dimensions.city);
		dimMap.put("Email", Dimensions.email);
		dimMap.put("Mobile", Dimensions.mobile);
		dimMap.put("Status", Dimensions.playerStatus);
		dimMap.put("Last Login", Dimensions.lastLogin);
		dimMap.put("Ip Address", Dimensions.ipAddress);
		dimMap.put("Net Financial Status", Dimensions.netFinancialStatus);
		dimMap.put("Cash Chips In Hand", Dimensions.cashChipsInHand);
		dimMap.put("VIP Points In Hand", Dimensions.vipPointsInHand);

		// Affiliate
		dimMap.put("Affiliate", Dimensions.affiliateId);
		dimMap.put("Affiliate Email", Dimensions.affiliateEmail);
		dimMap.put("Affiliate First Name", Dimensions.affiliateFirstName);
		dimMap.put("Affiliate Last Name", Dimensions.affiliateLastName);

		Map<String, Operation> operation = new HashMap<String, Operation>();
		operation.put("IN", Operation.in);
		operation.put("NOT IN", Operation.notin);
		operation.put("EQUAL", Operation.equal);
		operation.put("NOT EQUAL", Operation.notequal);

		Map<String, DATERANGE> dateOption = new HashMap<String, DATERANGE>();
		dateOption.put("TODAY", DATERANGE.today);
		dateOption.put("YESTERDAY", DATERANGE.yesterday);
		dateOption.put("LAST 7 DAY", DATERANGE.last7days);
		dateOption.put("LAST 14 DAY", DATERANGE.last14days);
		dateOption.put("MONTH TO DATE", DATERANGE.month2date);
		dateOption.put("LAST MONTH", DATERANGE.lastmonth);
		dateOption.put("CUSTOM", DATERANGE.custom);

		ReportConfig reportConfig = new ReportConfig(measureMap, dimMap, operation, dateOption, filterMap);
		InMemory.reportConfig = reportConfig;

		for (Entry<String, Dimensions> dimension : InMemory.reportConfig.getDimentions().entrySet()) {
			InMemory.dimMap.put(dimension.getValue(), dimension.getKey());
		}

		for (Entry<String, Metric> measure : InMemory.reportConfig.getMeasures().entrySet()) {
			InMemory.measureMap.put(measure.getValue(), measure.getKey());
		}

		for (Entry<String, DATERANGE> dateRange : InMemory.reportConfig.getDateOption().entrySet()) {
			InMemory.dateRangeMap.put(dateRange.getValue(), dateRange.getKey());
		} 
	} 
	
	private static void marketingReportInit() {
		Map<String, MarketingReportMatric> marketingMeasureMap = new HashMap<String, MarketingReportMatric>();
		marketingMeasureMap.put("Game Name", MarketingReportMatric.gameName); 
		marketingMeasureMap.put("Mobile", MarketingReportMatric.mobile); 
		marketingMeasureMap.put("First Name", MarketingReportMatric.firstName); 
		marketingMeasureMap.put("Last Name", MarketingReportMatric.lastName); 
		marketingMeasureMap.put("Email", MarketingReportMatric.email); 
		marketingMeasureMap.put("Rake Generated", MarketingReportMatric.rakeGenerated);
		marketingMeasureMap.put("Vip Points", MarketingReportMatric.vipPoints);
		marketingMeasureMap.put("Wagered", MarketingReportMatric.wagered);
		marketingMeasureMap.put("Chips", MarketingReportMatric.chips); 
		marketingMeasureMap.put("Affiliate Name", MarketingReportMatric.affiliateUserName); 
		marketingMeasureMap.put("Game Type", MarketingReportMatric.gameType); 
		marketingMeasureMap.put("Game Played", MarketingReportMatric.gamePlayed);   
		marketingMeasureMap.put("Game Mode", MarketingReportMatric.gameMode); 
		InMemory.marketingMeasureMap.putAll(marketingMeasureMap); 
		MarketingReportConfig marketingReportConfig = new MarketingReportConfig(marketingMeasureMap);
		InMemory.marketingReportConfig = marketingReportConfig;   
	}
	private static void affiliateReportInit() { 
		Map<String, AffiliateMetric> affiliateMeasureMap = new HashMap<String, AffiliateMetric>();
		affiliateMeasureMap.put("Game Name", AffiliateMetric.gameName); 
		affiliateMeasureMap.put("Player Email", AffiliateMetric.playerEmail); 
		affiliateMeasureMap.put("Player FirstName", AffiliateMetric.playerFirstName); 
		affiliateMeasureMap.put("Player LastName", AffiliateMetric.playerLastName); 
		affiliateMeasureMap.put("Affiliate UserName", AffiliateMetric.affiliateUserName); 
		affiliateMeasureMap.put("Registered Date", AffiliateMetric.date); 
		affiliateMeasureMap.put("Left Date", AffiliateMetric.leftDate); 
		affiliateMeasureMap.put("Mobile", AffiliateMetric.mobile); 
		affiliateMeasureMap.put("Game Played", AffiliateMetric.gamePlayed);
		affiliateMeasureMap.put("Money Deposit", AffiliateMetric.moneyDeposit);
		affiliateMeasureMap.put("Rake Generated", AffiliateMetric.rakeGenerated);
		affiliateMeasureMap.put("Vip Points", AffiliateMetric.vipPoints); 
		affiliateMeasureMap.put("Wagered", AffiliateMetric.wagered); 
		affiliateMeasureMap.put("Player Class", AffiliateMetric.playerClass); 
		affiliateMeasureMap.put("Real Chips", AffiliateMetric.realChips); 
		affiliateMeasureMap.put("Tournament Spent", AffiliateMetric.tournamentSpent); 
		affiliateMeasureMap.put("Commission", AffiliateMetric.commission); 
		affiliateMeasureMap.put("Bonus Chips", AffiliateMetric.bonusChips); 
		InMemory.affiliateMeasureMap.putAll(affiliateMeasureMap); 
		Map<String, DATERANGE> dateOption = new HashMap<String, DATERANGE>();
		dateOption.put("TODAY", DATERANGE.today); 
		dateOption.put("YESTERDAY", DATERANGE.yesterday); 
		dateOption.put("LAST 7 DAY", DATERANGE.last7days); 
		dateOption.put("LAST 14 DAY", DATERANGE.last14days); 
		dateOption.put("MONTH TO DATE", DATERANGE.month2date); 
		dateOption.put("LAST MONTH", DATERANGE.lastmonth); 
		dateOption.put("CUSTOM", DATERANGE.custom); 

		AffiliateReport affiliateReport = new AffiliateReport(affiliateMeasureMap, dateOption);
		InMemory.affiliateReport = affiliateReport; 
		
		for (Entry<String, DATERANGE> dateRange : InMemory.affiliateReport.getAffiliateDateOption().entrySet()) {
			InMemory.affiliateDateRangeMap.put(dateRange.getValue(), dateRange.getKey());
		}
	} 
	
	private static void affiliatePlayerConfigInit() { 
		Map<String, AffiliatePlayerMatric> affiliatePlayerMeasureMap = new HashMap<String, AffiliatePlayerMatric>();
		affiliatePlayerMeasureMap.put("Player Class", AffiliatePlayerMatric.playerClass);
		affiliatePlayerMeasureMap.put("Registered Date", AffiliatePlayerMatric.registered);
		affiliatePlayerMeasureMap.put("Chips", AffiliatePlayerMatric.chips); 
		affiliatePlayerMeasureMap.put("Player Status", AffiliatePlayerMatric.status);
		affiliatePlayerMeasureMap.put("Player Email", AffiliatePlayerMatric.email); 
		affiliatePlayerMeasureMap.put("Wagered", AffiliatePlayerMatric.wagered);
		affiliatePlayerMeasureMap.put("Leave Date", AffiliatePlayerMatric.leaveDate);
		affiliatePlayerMeasureMap.put("Mobile", AffiliatePlayerMatric.mobile);
		affiliatePlayerMeasureMap.put("Game Played", AffiliatePlayerMatric.gamePlayed); 
		affiliatePlayerMeasureMap.put("Rake Generated", AffiliatePlayerMatric.rake);
		affiliatePlayerMeasureMap.put("Game Name", AffiliatePlayerMatric.gameName); 
		affiliatePlayerMeasureMap.put("Vip Points", AffiliatePlayerMatric.vipPoints);
		AffiliatePlayerExportCsv affiliatePlayerExportCsv = new AffiliatePlayerExportCsv(affiliatePlayerMeasureMap); 
		InMemory.affiliatePlayerExportCsv = affiliatePlayerExportCsv; 
	}

	private static void membershipInit() {

		List<Membership> membershipList = MembershipDao.list();
		if (membershipList.size() == 0) {
			for (int i = 0; i < 6; i++) {
				Membership membership = new Membership();
				membership.setTitle("Member" + i);
				membership.setColor("#cdebca");
				membership.setPlayerClass(PlayerClass.CRYSTAL);
				membership.setLevel(1);
				membership.setTenureDays(1);
				membership.setCashChipOnRenewalFive(2);
				membership.setOffLineTournamentDiscount(i + .2);
				membership.setTournamentDiscount(i);
				membership.setRakeRefundActivation(i + .0);
				membership.setTdsRefundPert(i + .1);
				membership.setSubsriptionFees(30);
				membership.setRenealOfferDiscount(i + .0);
				membership.setTdsRefundPert(i + .0);
				MembershipDao.persist(membership);
			}

		}
	}

	private static void categoryInit() {

		Map<String, PlayerClass> playerClasses = new HashMap<String, PlayerClass>();
		playerClasses.put("CRYSTAL", PlayerClass.CRYSTAL);
		playerClasses.put("DIAMOND", PlayerClass.DIAMOND);
		playerClasses.put("OPAL", PlayerClass.OPAL);
		playerClasses.put("PEARL", PlayerClass.PEARL);
		playerClasses.put("RUBY", PlayerClass.RUBY);
		playerClasses.put("SAPPHIRE", PlayerClass.SAPPHIRE);
		playerClasses.put("TOPAZ", PlayerClass.TOPAZ);
		// CategoryDao.delete();
		// TableConfigurationDao.delete();
		List<Category> categoryList = CategoryDao.list(null);
		if (categoryList.size() == 0) {
			int i = 0;
			for (PlayerClass playerClass : playerClasses.values()) {
				Category category = new Category();
				category.setTitle(playerClass);
				category.setBonusChipVipPoints(i + .2);
				category.setBonusChipsPert(i + .3);
				category.setCashChipsPert(100);
				category.setEveryMonthPoints(0);
				category.setFirstDepositMax(100);
				category.setFirstDepositMin(80);
				category.setNextLevelPoints(i);
				category.setRakeDiscountVip(i + .3);
				category.setRakeRefundPert(i + .6);
				category.setTenureDays(i);
				category.setTablePlayerLimit(6);
				category.setTdsRefundPert(i + .23);
				category.setTdsRefundVip(i + .44);
				/*category.setTournamentTicketDiscount(i + .56);
				category.setTtDiscountVip(i + 2.1);*/
				category.setVipPointsPert(i + 2.5);
				CategoryDao.persist(category);
				i++;

			}

		}

	}
	
	private static void withdrawRequestInit() {
		Map<String, WithdrawStatus> withdrawStatus = new HashMap<String, WithdrawStatus>();
		withdrawStatus.put("RECEIVED", WithdrawStatus.RECEIVED);
		withdrawStatus.put("EXECUTED", WithdrawStatus.EXECUTED);
		withdrawStatus.put("DELAYED", WithdrawStatus.DELAYED);
		withdrawStatus.put("REJECTED", WithdrawStatus.REJECTED);
		InMemory.withdrawStatusMap.putAll(withdrawStatus);
		
		 Map<String, Dimensions> withdrawDimMap = new LinkedHashMap<String, Dimensions>();
		 withdrawDimMap.put(WithdrawConstant.GAMENAME, Dimensions.gameName);
		 withdrawDimMap.put(WithdrawConstant.AMOUNT, Dimensions.amount);
		 withdrawDimMap.put(WithdrawConstant.PAYMENT_DATE, Dimensions.date);
		 withdrawDimMap.put(WithdrawConstant.TRANSACTION_ID, Dimensions.withdrawTransactionId);
		 withdrawDimMap.put(WithdrawConstant.RECEIVED_ON, Dimensions.ReceivedOn);
		 withdrawDimMap.put(WithdrawConstant.LAST_UPDATED, Dimensions.lastUpdate);
		 withdrawDimMap.put(WithdrawConstant.PLAYERUSERNAME, Dimensions.userName);
		 withdrawDimMap.put(WithdrawConstant.STATUS, Dimensions.withdrawStatus);
		    InMemory.withdrawDimMap.putAll(withdrawDimMap); 
	}

	private static void entityTypeInit() {
		Map<String, EntityType> entityTypes = new HashMap<String, EntityType>();
		entityTypes.put("COUPONS", EntityType.COUPONS);
		entityTypes.put("TOURNAMENTS", EntityType.TOURNAMENTS);
		InMemory.entityTypeMap.putAll(entityTypes);
	}
	private static void playerAchievementEntityInit() {
		List<Offered> playerAchievementEntity = new ArrayList<Offered>();
		playerAchievementEntity.add(Offered.BONUS_POINTS);
		playerAchievementEntity.add(Offered.VIP_POINTS);
		playerAchievementEntity.add(Offered.REAL_CHIPS);
		playerAchievementEntity.add(Offered.TOURNAMENT_POINTS);
		InMemory.playerAchievementEntity.addAll(playerAchievementEntity);
	}

	private static void pokerInit() {
		// PlayerDao.delete();
		Map<String, TableStatus> tableStatus = new HashMap<String, TableStatus>();
		tableStatus.put("DRAFT", TableStatus.DRAFT);
		tableStatus.put("PENDING APPROVAL", TableStatus.PENDING_APPROVAL);
		tableStatus.put("REJECTED", TableStatus.REJECTED);
		tableStatus.put("PENDING LIVE", TableStatus.PENDING_LIVE);
		tableStatus.put("LIVE", TableStatus.LIVE);
		tableStatus.put("STOPPED", TableStatus.STOPPED);
		InMemory.tableStatusMap.putAll(tableStatus);

		Map<String, TournamentStatus> tournamentStatus = new HashMap<String, TournamentStatus>();
		tournamentStatus.put("DRAFT", TournamentStatus.DRAFT);
		tournamentStatus.put("PENDING APPROVAL", TournamentStatus.PENDING_APPROVAL);
		tournamentStatus.put("REJECTED", TournamentStatus.REJECTED);
		tournamentStatus.put("PENDING LIVE", TournamentStatus.PENDING_LIVE);
		tournamentStatus.put("LIVE", TournamentStatus.LIVE);
		tournamentStatus.put("COMPLETED", TournamentStatus.COMPLETED);
		tournamentStatus.put("CANCELLED", TournamentStatus.CANCELLED);
		InMemory.tournamentStatusMap.putAll(tournamentStatus);

		Map<String, CouponStatus> couponStatus = new HashMap<String, CouponStatus>();
		couponStatus.put("DRAFT", CouponStatus.DRAFT);
		couponStatus.put("PENDING APPROVAL", CouponStatus.PENDING_APPROVAL);
		couponStatus.put("REJECTED", CouponStatus.REJECTED);
		couponStatus.put("PENDING LIVE", CouponStatus.PENDING_LIVE);
		couponStatus.put("LIVE", CouponStatus.LIVE);
		couponStatus.put("EXPIRED", CouponStatus.EXPIRED);
		couponStatus.put("STOPPED", CouponStatus.STOPPED);
		InMemory.couponStatusMap.putAll(couponStatus);
		
		Map<String, PromotionOtherStatus> otherPromotionStatus = new HashMap<String, PromotionOtherStatus>();
		otherPromotionStatus.put("DRAFT", PromotionOtherStatus.DRAFT);
		otherPromotionStatus.put("PENDING APPROVAL", PromotionOtherStatus.PENDING_APPROVAL);
		otherPromotionStatus.put("REJECTED", PromotionOtherStatus.REJECTED);
		otherPromotionStatus.put("PENDING LIVE", PromotionOtherStatus.PENDING_LIVE);
		otherPromotionStatus.put("LIVE", PromotionOtherStatus.LIVE);
		otherPromotionStatus.put("EXPIRED", PromotionOtherStatus.EXPIRED);
		otherPromotionStatus.put("STOPPED", PromotionOtherStatus.STOPPED);
		InMemory.promotionOtherStatusMap.putAll(otherPromotionStatus);

		Map<String, ImageStatus> imageStatus = new HashMap<String, ImageStatus>();
		imageStatus.put("DRAFT", ImageStatus.DRAFT);
		imageStatus.put("PENDING APPROVAL", ImageStatus.PENDING_APPROVAL);
		imageStatus.put("REJECTED", ImageStatus.REJECTED);
		imageStatus.put("ENABLE", ImageStatus.ENABLE);
		imageStatus.put("DISABLE", ImageStatus.DISABLE);
		InMemory.imageStatusMap.putAll(imageStatus);

		List<Integer> tableSizes = new ArrayList<Integer>();
		tableSizes.add(5);
		tableSizes.add(7);
		tableSizes.add(9);
		tableSizes.add(6);
		InMemory.tableSizes.addAll(tableSizes);
		List<String> days = new ArrayList<String>();
		days.add("Monday");
		days.add("Tuesday");
		days.add("Wednesday");
		days.add("Thursday");
		days.add("Friday");
		days.add("Saturday");
		days.add("Sunday");
		InMemory.day.addAll(days);
		List<Integer> timers = new ArrayList<Integer>();
		timers.add(5);
		timers.add(10);
		timers.add(15);
		timers.add(20);
		timers.add(25);
		timers.add(30);
		timers.add(60);
		InMemory.timers.addAll(timers);

		Map<String, GameMode> gameModes = new HashMap<String, GameMode>();
		gameModes.put("HOLDEM", GameMode.HOLDEM);
		gameModes.put("OMAHA", GameMode.OMAHA);
		gameModes.put("RING", GameMode.RING);
		gameModes.put("OMAHA_5CARD", GameMode.OMAHA_5CARD);
		

		for (Entry<String, GameMode> gameMode : gameModes.entrySet()) {
			InMemory.gameModeMap.put(gameMode.getValue(), gameMode.getKey());
		}
		Map<String, GameType> gameTypes = new HashMap<String, GameType>();
		gameTypes.put("DEMO", GameType.DEMO);
		gameTypes.put("FREE", GameType.FREE);
		gameTypes.put("CASH", GameType.CASH);
		// gameTypes.put("TOURNAMENT", GameType.TOURNAMENT);

		for (Entry<String, GameType> gameType : gameTypes.entrySet()) {
			InMemory.gameTypeMap.put(gameType.getValue(), gameType.getKey());
		}

		Map<String, String> categoryConfig = new LinkedHashMap<String, String>();
		categoryConfig.put("A", "Bonus Chips %");
		categoryConfig.put("B", "Bonus Chip Ratio");
		categoryConfig.put("C", "Cash Chip Ratio");
		categoryConfig.put("D", "Bonus Chips VIP Points");
		categoryConfig.put("E", "Tournament Ticket Discount");
		categoryConfig.put("H", "Bonus chips Validity tenure in days");
		categoryConfig.put("X", "VIP Points %");
		categoryConfig.put("Y", "Next Level Points");
		categoryConfig.put("AA", "Crystal Factor");
		categoryConfig.put("J", "Pearl Factor");
		categoryConfig.put("L", "Topaz Factor");
		categoryConfig.put("N", "Sapphire Factor");
		categoryConfig.put("P", "Diamond Factor");
		categoryConfig.put("R", "Ruby Factor");
		categoryConfig.put("T", "Opal Factor");
		categoryConfig.put("private_table", "Private Table");
		categoryConfig.put("rake_refund", "Rake Refund");
		categoryConfig.put("F", "Rake Refund %");
		categoryConfig.put("tds_refund", "TDS Refund");
		categoryConfig.put("G", "TDS Refund %");

		for (Entry<String, String> config : categoryConfig.entrySet()) {
			InMemory.categoryConfigMap.put(config.getKey(), config.getValue());
		}
		CategoryMaster categoryMaster = CategoryMasterDao.get();
		if (categoryMaster == null) {
			CategoryMaster master = new CategoryMaster();
			master.setA(10);
			master.setB(15);
			master.setC(20);
			master.setD(25);
			master.setE(30);
			master.setF(35);
			master.setG(40);
			CategoryMasterDao.persist(master);
		}

		PokerConfiguration pokerConfig = PokerConfigurationDao.get();
		if (pokerConfig == null) {
			PokerConfiguration pokerConfigObj = new PokerConfiguration();
			pokerConfigObj.setRakeMargin(2.2);
			PokerConfigurationDao.persist(pokerConfigObj);
		}

		GlobalStats globalStats = GlobalStatsDao.get();
		if (globalStats == null) {
			globalStats = new GlobalStats();
			GlobalStatsDao.persist(globalStats);
		}

	}

	private static void checkandCreateDirs() {
		File theDir = new File("/opt/tmp/");
		if (!theDir.exists()) {
			LOG.info("creating directory: " + "/opt/tmp/");
			try {
				theDir.mkdirs();
			} catch (SecurityException se) {
			}
		}

	}



	
	
	private static void affiliateInit() {

		Map<String, AffiliateStatus> affiliateStatus = new HashMap<String, AffiliateStatus>();
		affiliateStatus.put("PENDING APPROVAL", AffiliateStatus.PENDING_APPROVAL);
		affiliateStatus.put("ACTIVE", AffiliateStatus.ACTIVE);
		affiliateStatus.put("REJECTED", AffiliateStatus.REJECTED);
		affiliateStatus.put("BANNED", AffiliateStatus.BANNED);
		affiliateStatus.put("DELETED", AffiliateStatus.DELETED);
		InMemory.affiliateStatusMap.putAll(affiliateStatus);

		Map<String, PlayerReferralStatus> affiliatePlayerStatus = new HashMap<String, PlayerReferralStatus>();
		affiliatePlayerStatus.put("PENDING", PlayerReferralStatus.PENDING);
		affiliatePlayerStatus.put("APPROVED", PlayerReferralStatus.APPROVED);
		affiliatePlayerStatus.put("REJECTED", PlayerReferralStatus.REJECTED);
		affiliatePlayerStatus.put("BANNEDPLAYER", PlayerReferralStatus.BANNEDPLAYER); 
		InMemory.affiliatePlayerStatusMap.putAll(affiliatePlayerStatus); 
 
		Map<String, AffiliateType> affiliateTypes = new HashMap<String, AffiliateType>();
		affiliateTypes.put("GENERAL", AffiliateType.GENERAL);
		affiliateTypes.put("IN HOUSE", AffiliateType.IN_HOUSE);
		affiliateTypes.put("MARKETING", AffiliateType.MARKETING);
		InMemory.affiliateTypesMap.putAll(affiliateTypes);

		Map<String, AffiliateCommissionType> affiliateCommissionTypes = new HashMap<String, AffiliateCommissionType>();
		affiliateCommissionTypes.put("RAKE", AffiliateCommissionType.RAKE);
		affiliateCommissionTypes.put("WAGERED", AffiliateCommissionType.WAGERED);
		InMemory.affiliateCommissionTypes.putAll(affiliateCommissionTypes);

	}
	
	private static void permissionCreate() {
		try {
			List<String> permissions = new ArrayList<String>();
			List<Permission> permissionList = PermissionDao.getAll(null, null);
			for (Permission permission : permissionList) {
				permissions.add(permission.getTitle());

			}
			Field[] fields = UserPermission.class.getDeclaredFields();
			for (Field f : fields) {
				if (!permissions.contains(f.get(f.getName().toString()))) {
					String[] group = f.get(f.getName()).toString().split("_");
					Permission permissionObj = new Permission();
					permissionObj.setTitle(f.get(f.getName()).toString());
					permissionObj.setGroup(group[0]);
					PermissionDao.persist(permissionObj);
				}
			}
		} catch (Exception exp) {
			LOG.error(exp.getMessage(), exp);
		} 

	}

	private static void paymentGatewayInit() {
		try {
			PaymentGateway paymentGatewayObj = PaymentGatewayDao.getById("594125f655b4f46cf9bdc07e");
			if (paymentGatewayObj == null) {
				PaymentGateway paymentGateway = new PaymentGateway();
				paymentGateway.setId("594125f655b4f46cf9bdc07e");
				paymentGateway.setName("payu_paisa");
				paymentGateway.setSuccessURL("payment/redirect");
				paymentGateway.setFailedURL("payment/redirect");
				// paymentGateway.setCreateTime("2017-06-14T07:00:05.933Z");
				Map<String, Map<String, String>> meta = new HashMap<String, Map<String, String>>();
				Map<String, String> configurationProd = new HashMap<String, String>();
				configurationProd.put("salt", "v90DTBrH5b");
				configurationProd.put("key", "rEHx6wJ0");
				configurationProd.put("paymentURL", "https://secure.payu.in/_payment");
				configurationProd.put("paymentResponse", "https://www.payumoney.com/payment/op/getPaymentResponse");
				configurationProd.put("header_authorization", "sFe/FLWDgy6L9AozYG7ihJihuGtiN+3FXLjDgbuIHU8=");
				meta.put("prod", configurationProd);

				Map<String, String> configurationTest = new HashMap<String, String>();
				configurationTest.put("salt", "fE0aTrjr");
				configurationTest.put("key", "UFu3ed");
				configurationTest.put("paymentURL", "https://test.payu.in/_payment");
				configurationTest.put("paymentResponse", "https://test.payumoney.com/payment/op/getPaymentResponse");
				configurationTest.put("header_authorization", "KpNTiy57L6OFjS2D3TqPod8+6nfGmRVwVMi5t9jR4NU=");
				meta.put("test", configurationProd);

				paymentGateway.setMeta(meta);
				PaymentGatewayDao.persist(paymentGateway);
			}
		} catch (Exception exp) {
			LOG.error(exp.getMessage(), exp);
		}

	}

	private static void quizInit() {
		Map<String, Level> quizLevel = new LinkedHashMap<String, Level>();
		quizLevel.put("BASIC", Level.BASIC);
		quizLevel.put("INTERMEDIATE", Level.INTERMEDIATE);
		quizLevel.put("ADVANCE", Level.ADVANCE);
		InMemory.quizLevelMap.putAll(quizLevel);

		Map<String, Level> questionLevel = new LinkedHashMap<String, Level>();
		questionLevel.put("BASIC", Level.BASIC);
		questionLevel.put("INTERMEDIATE", Level.INTERMEDIATE);
		questionLevel.put("ADVANCE", Level.ADVANCE);
		InMemory.questionLevelMap.putAll(questionLevel);

		Map<String, QuestionStatus> questionStatus = new LinkedHashMap<String, QuestionStatus>();
		questionStatus.put("DRAFT", QuestionStatus.DRAFT);
		questionStatus.put("PENDING APPROVAL", QuestionStatus.PENDING_APPROVAL);
		questionStatus.put("ENABLE", QuestionStatus.ENABLE);
		questionStatus.put("DISABLE", QuestionStatus.DISABLE);
		questionStatus.put("REJECTED", QuestionStatus.REJECTED);
		InMemory.questionStatusMap.putAll(questionStatus);

	}
	
	private static void cricketInit() {
		Map<String, CricketPointScoreStatus> cricketPointScoreStatus = new LinkedHashMap<String, CricketPointScoreStatus>();
		cricketPointScoreStatus.put("DRAFT", CricketPointScoreStatus.DRAFT);
		cricketPointScoreStatus.put("PENDING APPROVAL", CricketPointScoreStatus.PENDING_APPROVAL);
		cricketPointScoreStatus.put("LIVE", CricketPointScoreStatus.LIVE);
		cricketPointScoreStatus.put("STOPPED", CricketPointScoreStatus.STOPPED);
		cricketPointScoreStatus.put("REJECTED", CricketPointScoreStatus.REJECTED);
		InMemory.CricketPointScoreStatusMap.putAll(cricketPointScoreStatus); 
	}
	
	private static void playerAchievementInit() {
		Map<String, PlayerAchievementGiven> playerAchievement = new LinkedHashMap<String, PlayerAchievementGiven>();
		playerAchievement.put("HANDS PLAYED", PlayerAchievementGiven.HANDSPLAYED);
		playerAchievement.put("VIP", PlayerAchievementGiven.VIP);
		playerAchievement.put("RAKE", PlayerAchievementGiven.RAKE);
		InMemory.playerAchievementMap.putAll(playerAchievement);

		

		Map<String, PlayerAchievementStatus> playerAchievementStatus = new LinkedHashMap<String, PlayerAchievementStatus>();
		playerAchievementStatus.put("DRAFT", PlayerAchievementStatus.DRAFT);
		playerAchievementStatus.put("PENDING APPROVAL", PlayerAchievementStatus.PENDING_APPROVAL);
		playerAchievementStatus.put("ENABLE", PlayerAchievementStatus.ENABLE);
		playerAchievementStatus.put("DISABLE", PlayerAchievementStatus.DISABLE);
		playerAchievementStatus.put("REJECTED", PlayerAchievementStatus.REJECTED);
		playerAchievementStatus.put("EXPIRED", PlayerAchievementStatus.EXPIRED);
		InMemory.playerAchievementStatusMap.putAll(playerAchievementStatus);

		Map<String, levels> achievementPlayers = new LinkedHashMap<String, levels>();
		achievementPlayers.put("LEVEL-1", levels.LEVEL1);
		achievementPlayers.put("LEVEL-2", levels.LEVEL2);
		achievementPlayers.put("LEVEL-3", levels.LEVEL3);
		achievementPlayers.put("LEVEL-4", levels.LEVEL4);
		achievementPlayers.put("LEVEL-5", levels.LEVEL5);
		InMemory.achievementPlayersMap.putAll(achievementPlayers);
		
		Map<String, Dimensions> playerAchievementDimMap = new LinkedHashMap<String, Dimensions>();
		playerAchievementDimMap.put("Game Name", Dimensions.gameName);
		playerAchievementDimMap.put("Player Class", Dimensions.playerClass);
		playerAchievementDimMap.put("Mobile", Dimensions.mobile);
		playerAchievementDimMap.put("Entity Type", Dimensions.entityType);
		playerAchievementDimMap.put("Chips/points", Dimensions.points);
		playerAchievementDimMap.put("Level", Dimensions.level);
		InMemory.playerAchievementDimMap.putAll(playerAchievementDimMap);
	}
	
	
	private static void playerRecortdInit() {
		
		Map<String, Dimensions> playerRecordDimMap = new LinkedHashMap<String, Dimensions>();
		playerRecordDimMap.put("Game Name", Dimensions.gameName);
		playerRecordDimMap.put("Mobile", Dimensions.mobile);
		playerRecordDimMap.put("First Name", Dimensions.firstName);
		playerRecordDimMap.put("Email", Dimensions.email);
		playerRecordDimMap.put("Rake", Dimensions.rake);
		playerRecordDimMap.put("Hands", Dimensions.hands);
		playerRecordDimMap.put("VIP", Dimensions.vip);
		playerRecordDimMap.put("Wagered", Dimensions.wagered);
		InMemory.playerRecordDimMap.putAll(playerRecordDimMap);
	}
	
	private static void leaderBoardInit() {
		Map<String, LeaderBoardEntity> leaderBoard = new LinkedHashMap<String, LeaderBoardEntity>();
		leaderBoard.put("HANDS PLAYED", LeaderBoardEntity.HANDSPLAYED);
		leaderBoard.put("VIP", LeaderBoardEntity.VIP);
		leaderBoard.put("RAKE", LeaderBoardEntity.RAKE);
		leaderBoard.put("WAGERED", LeaderBoardEntity.WAGERED);
		InMemory.leaderBoardEntityMap.putAll(leaderBoard);

		

		Map<String, LeaderBoardStatus> leaderBoardStatus = new LinkedHashMap<String, LeaderBoardStatus>();
		leaderBoardStatus.put("DRAFT", LeaderBoardStatus.DRAFT);
		leaderBoardStatus.put("PENDING APPROVAL", LeaderBoardStatus.PENDING_APPROVAL);
		leaderBoardStatus.put("ENABLE", LeaderBoardStatus.ENABLE);
		leaderBoardStatus.put("DISABLE", LeaderBoardStatus.DISABLE);
		leaderBoardStatus.put("REJECTED", LeaderBoardStatus.REJECTED);
		leaderBoardStatus.put("EXPIRED", LeaderBoardStatus.EXPIRED);
		leaderBoardStatus.put("PENDING LIVE", LeaderBoardStatus.PENDING_LIVE);
		InMemory.leaderBoardStatusMap.putAll(leaderBoardStatus);

	}
	
	private static void paymentTransactionInit() {
	    Map<String, PaymentStatus> paymentStatus = new LinkedHashMap<String, PaymentStatus>();
	    paymentStatus.put(PaymentConstant.BOUNCED, PaymentStatus.bounced);
	    paymentStatus.put(PaymentConstant.COMPLETED, PaymentStatus.completed);
	    paymentStatus.put(PaymentConstant.FAILED, PaymentStatus.failed);
	    paymentStatus.put(PaymentConstant.HASH_NOT_MATCH, PaymentStatus.hash_not_match);
	    paymentStatus.put(PaymentConstant.INITIATED, PaymentStatus.initiated);
	    paymentStatus.put(PaymentConstant.MONEY_WITH_PROVIDER, PaymentStatus.money_with_provider);
	    paymentStatus.put(PaymentConstant.NOT_STARTED, PaymentStatus.not_started);
	    paymentStatus.put(PaymentConstant.PARTIALLY_REFUNDED, PaymentStatus.partially_refunded);
	    paymentStatus.put(PaymentConstant.REFUNDED, PaymentStatus.refunded);
	    paymentStatus.put(PaymentConstant.SETTLEMENT_IN_REFUNDED, PaymentStatus.settlement_in_process);
	    paymentStatus.put(PaymentConstant.UNDER_DISPUTE, PaymentStatus.under_dispute);
	    paymentStatus.put(PaymentConstant.VERIFICATION_REFUNDING, PaymentStatus.verification_pending);	
	    InMemory.paymentStatusMap.putAll(paymentStatus);	
	    
	    Map<String, Dimensions> paymentDimMap = new LinkedHashMap<String, Dimensions>();
	    paymentDimMap.put(PaymentConstant.GAMENAME, Dimensions.gameName);
	    paymentDimMap.put(PaymentConstant.AMOUNT, Dimensions.amount);
	    paymentDimMap.put(PaymentConstant.PAYMNET_DATE, Dimensions.date);
	    paymentDimMap.put(PaymentConstant.LYVE_TRANSACTION_ID, Dimensions.displayTransaxId);
	    paymentDimMap.put(PaymentConstant.GATEWAY_TRANSACTION_ID, Dimensions.gateway_transaction_id);
	    paymentDimMap.put(PaymentConstant.EMAIL, Dimensions.email);
	    paymentDimMap.put(PaymentConstant.PHONE, Dimensions.mobile);
	    paymentDimMap.put(PaymentConstant.STATUS, Dimensions.paymentStatus);
	    InMemory.paymentDimMap.putAll(paymentDimMap);    
	}

	private static void couponInit() {

		Map<String, RedemptionType> redemptionType = new HashMap<String, RedemptionType>();
		//redemptionType.put("BONUS", RedemptionType.BONUS);
		redemptionType.put("CASH", RedemptionType.CASH);
		//redemptionType.put("VIP", RedemptionType.VIP);
		redemptionType.put("TOURNAMENT_POINTS", RedemptionType.TOURNAMENT_POINTS);
		redemptionType.put("VIP_POINTS", RedemptionType.VIP_POINTS);
		redemptionType.put("BONUS_CHIPS", RedemptionType.BONUS_CHIPS);
		redemptionType.put("DISCOUNT", RedemptionType.DISCOUNT);

		for (Entry<String, RedemptionType> rType : redemptionType.entrySet()) {
			InMemory.redemptionTypeMap.put(rType.getValue(), rType.getKey());
		}

		Map<PaymentType, List<CouponType>> couponTypes = new LinkedHashMap<PaymentType, List<CouponType>>();
		List<CouponType> freeType = new ArrayList<CouponType>();
		freeType.add(CouponType.SIGNUP);
		freeType.add(CouponType.TOURNAMENT);
		freeType.add(CouponType.BUYIN);
		couponTypes.put(PaymentType.FREE, freeType);
		List<CouponType> paidType = new ArrayList<CouponType>();
		paidType.add(CouponType.BONUS);
		paidType.add(CouponType.VIP);
		paidType.add(CouponType.TOURNAMENT);
		couponTypes.put(PaymentType.PAID, paidType);
		InMemory.couponTypeMap.putAll(couponTypes);

		Map<CouponType, List<OfferedAt>> offeredAt = new HashMap<CouponType, List<OfferedAt>>();
		List<OfferedAt> buyInType = new ArrayList<OfferedAt>();
		buyInType.add(OfferedAt.REGISTERING);
		List<OfferedAt> tournamentType = new ArrayList<OfferedAt>();
		tournamentType.add(OfferedAt.WINNING);
		tournamentType.add(OfferedAt.REGISTERING);
		offeredAt.put(CouponType.BUYIN, buyInType);
		offeredAt.put(CouponType.TOURNAMENT, tournamentType);
		InMemory.offeredAtMap.putAll(offeredAt);

		Map<String, List<String>> offeredFor = new HashMap<String, List<String>>();
		List<String> forSignup = new ArrayList<String>();
		forSignup.add("BONUS_CHIPS");
		forSignup.add("TOURNAMENT_POINTS");
		forSignup.add("VIP_POINTS");
		forSignup.add("CASH");
		List<String> forTournament = new ArrayList<String>();
		forTournament.add("TOURNAMENT_POINTS");
		forTournament.add("VIP_POINTS");
		forTournament.add("BONUS_CHIPS");
		forTournament.add("DISCOUNT");
		List<String> forBUYIN = new ArrayList<String>();
		// forBUYIN.add("POINTS");
		forBUYIN.add("BONUS_CHIPS");
		forBUYIN.add("TOURNAMENT_POINTS");
		forBUYIN.add("VIP_POINTS");
		forBUYIN.add("CASH");
		offeredFor.put("SIGNUP", forSignup);
		offeredFor.put("TOURNAMENT", forTournament);
		offeredFor.put("BUYIN", forBUYIN);
		InMemory.offered.putAll(offeredFor);
		
		Map<String, Dimensions> couponDimMap = new LinkedHashMap<String, Dimensions>();
		couponDimMap.put(CouponConstant.GAMENAME, Dimensions.gameName);
		couponDimMap.put(CouponConstant.REDEMPTION_TYPE, Dimensions.entityType);
		couponDimMap.put(CouponConstant.MOBILE, Dimensions.mobile);
		couponDimMap.put(CouponConstant.PLAYERCLASS, Dimensions.playerClass);
		couponDimMap.put(CouponConstant.CHIPSPOINTS, Dimensions.ChipsPoints);
	    couponDimMap.put(CouponConstant.DATE, Dimensions.date);
	    InMemory.couponDimMap.putAll(couponDimMap); 
	}

	// private static void memCachedInit() {
	//
	// String[] servers = { Config.MEMCACHED_HOST };
	// SockIOPool pool = SockIOPool.getInstance("WSEGAMES");
	// pool.setServers(servers);
	// pool.setFailover(true);
	// pool.setInitConn(10);
	// pool.setMinConn(5);
	// pool.setMaxConn(250);
	// pool.setMaintSleep(30);
	// pool.setNagle(false);
	// pool.setSocketTO(3000);
	// pool.setAliveCheck(true);
	// pool.initialize();
	//
	// }
	
	
}

