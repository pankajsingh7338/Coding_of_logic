package com.actolap.wse.fe.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actolap.config.Config;
import com.actolap.wse.LyveSendSms;
import com.actolap.wse.commons.GenericResponse;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.constants.Urlparams;
import com.actolap.wse.dao.CouponDao;
import com.actolap.wse.dao.PaymentDao;
import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.dao.ResponsibleGamingDao;
import com.actolap.wse.game.dao.PlayerBonusDao;
import com.actolap.wse.game.dao.PlayerTransactionDao;
import com.actolap.wse.inmemory.memcache.InMemory;
import com.actolap.wse.manager.AnalyticsManager;
import com.actolap.wse.model.payment.Payment;
import com.actolap.wse.model.payment.Payment.PaymentStatus;
import com.actolap.wse.model.player.Category;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.player.PlayerBonus;
import com.actolap.wse.model.player.PlayerTransaction;
import com.actolap.wse.model.player.PlayerTransaction.TransactionType;
import com.actolap.wse.model.player.ResponsibleGaming;
import com.actolap.wse.model.promotion.Coupon;
import com.actolap.wse.model.promotion.CouponRedemption;
import com.actolap.wse.model.promotion.RedemptionType;
import com.actolap.wse.payment.service.PaymentService;
import com.actolap.wse.payment.service.PaymentServices;
import com.actolap.wse.request.PaymentRequest;
import com.actolap.wse.request.PaymentVerifyRequest;
import com.actolap.wse.response.PaymentResponse;
import com.actolap.wse.response.PaymentVerifyResponse;
import com.actolap.wse.response.PlayerDetailsGetResponse;
import com.actolap.wse.response.ResponsibleGamingResponse;
import com.actolap.wse.rest.secuirty.PermissionHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/payment")
@Api(value = "Rest API")
public class PaymentController {

	private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); 

	public PaymentResponse request(Request request, Response response) {
		return paymentRequestDoc(request.getBodyAs(PaymentRequest.class), request, response);
	}

	public GenericResponse verify(Request request, Response response) {
		return paymentVerifyDoc(request.getBodyAs(PaymentVerifyRequest.class), request, response);
	}

	/*public GenericResponse paymentMobileDoc(Request request, Response response) {
		return paymentMobileDetailsDoc(request.getBodyAs(PaymentMobileSet.class), request, response);
	}*/

	@POST
	@Path("/request")
	@ApiOperation(value = "Request", notes = "Payment Request", response = PaymentResponse.class, httpMethod = "post")
	public PaymentResponse paymentRequestDoc(@ApiParam(required = true) PaymentRequest paymentRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response0) {
		PaymentResponse response = new PaymentResponse();
		Map<String, String> formData = null;
		Map<String, String> paymentFormData = null;
		try {
			String userIp = request.getHeader(PermissionHandler.USER_HEADER_IP);
			if (paymentRequest != null && Utils.isNotEmpty(paymentRequest.getAmount())
					&& Utils.isNotEmpty(paymentRequest.getEmail()) && Utils.isNotEmpty(paymentRequest.getProductinfo())
					&& Utils.isNotEmpty(paymentRequest.getFirstname()) && Utils.isNotEmpty(paymentRequest.getUserId())
					&& Utils.isNotEmpty(paymentRequest.getPaymentType())
					&& Utils.isNotEmpty(paymentRequest.getGatewayId()) && Utils.isNotEmpty(paymentRequest.getConsumer())
					&& Utils.isNotEmpty(userIp)) {
				paymentRequest.setIp(userIp);
				PaymentService paymentService = new PaymentService(PaymentServices.make_payment);
				try {
					paymentService.render(paymentRequest);
					formData = paymentService.getPaymentFormData(paymentRequest.getContextPath());
					paymentFormData = paymentService.getPaymentFormData(formData);
					response.setFormData(paymentFormData);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				response.setS(true);
			} else {
				response.setEd("Required fields are coming invalid");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@POST
	@Path("/verify")
	@ApiOperation(value = "Request", notes = "Payment Verify", response = PaymentVerifyResponse.class, httpMethod = "post")
	public PaymentVerifyResponse paymentVerifyDoc(@ApiParam(required = true) PaymentVerifyRequest paymentRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response0) {
		PaymentVerifyResponse response = new PaymentVerifyResponse();
		PaymentService paymentService = new PaymentService(PaymentServices.verify_payment);
		if (paymentRequest != null && paymentRequest.getVerifyRequest() != null) {
			try {
				Payment payment = paymentService.render(paymentRequest.getVerifyRequest(),
						request.getHeader("User-Agent"));
				Player player = PlayerDao.getById(payment.getUserId());
				if (payment != null && payment.getStatus() == PaymentStatus.completed) {
					response.setTxnid(payment.getId());
					response.setTxnid(payment.getDisplayTransaxId());
					response.setAmount(payment.getAmount());
					response.setStatus(payment.getStatus().toString());
					if(payment.getUniqueId() != null)
						response.setUniqueId(payment.getUniqueId());
					if(payment.getProductinfo().equals("Chips")) {
						chipBuyIn(payment);
					}
					else if(payment.getProductinfo().equals("Upgrade")) {
						upgradeNow(payment);
					}
					else if(payment.getProductinfo().equals("CouponPayment")) {
						CouponPayment(payment,player);
					} else if(payment.getProductinfo().equals("PdfDownload")) {
						pdfDownload(payment, player);
					}
					response.setS(true);
					response.setMsg("Payment has been verified successfully");
				} else {
					response.setTxnid(payment.getId());
					response.setTxnid(payment.getDisplayTransaxId());
					response.setAmount(payment.getAmount());
					response.setStatus(payment.getStatus().toString());
					response.setMsg("Payment has not been verified");
					com.actolap.wse.Utils.sendMailForFailedBuyIn(payment.getDisplayTransaxId(), player, Config.DOMAIN,
							"Buy-in Failed ");
					LyveSendSms.sendSmsFailedBuyIn(player.getMobile().toString(),
							payment.getAmount().toString(), player.getUserName());
				}

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		} else {
			response.setEd("Required fields are coming invalid");
		}
		return response;
	}

	public static void chipBuyIn(Payment payment) {
		try {
			Long chipAmount = Long.valueOf(payment.getAmount());
			Player player = PlayerDao.getById(payment.getUserId());
			if (player != null) {
				Long bonusChips = null;
				Category toUpdateCategory = null;
				Category currentCategory = InMemory.categoryMap.get(player.getPlayerClass());
				
				bonusChips = (long) (chipAmount * (currentCategory.getBonusChipsPert() / 100));
				List<PlayerTransaction> playerTransectionList = new ArrayList<PlayerTransaction>();
				PlayerTransaction playerRealTransection = new PlayerTransaction();
				playerRealTransection.setChips(chipAmount);
				playerRealTransection.setType(TransactionType.BUYIN);
				playerRealTransection.setPid(player.getId());
				playerRealTransection.setRefId(payment.getId());
				playerRealTransection.setDisplayTransaxId(payment.getDisplayTransaxId());
				playerTransectionList.add(playerRealTransection);
				PlayerTransactionDao.persist(playerRealTransection);
				
				com.actolap.wse.Utils.sendMailForBuyIn(chipAmount, playerRealTransection, player, Config.DOMAIN,
						"Buy-In for transaction ID " + payment.getDisplayTransaxId());
				if (player.getMobile() != null)
					LyveSendSms.sendSmsSuccessBuyIn(player.getMobile().toString(), chipAmount.toString(),
							player.getUserName());
				PlayerDao.updateBuyInDetail(player.getId(), chipAmount, null, toUpdateCategory);
				
				AnalyticsManager.moneyDeposited(player.getId(), chipAmount);
				
				
				PaymentDao.processed(payment.getId());
			
			
			if(payment.getUniqueCode() != null && !payment.getUniqueCode().equals("undefined") && !payment.getUniqueCode().equals("") ) {
				handleCoupon(payment,player);
				CouponRedemption couponRedemption = CouponDao.getLiveRedemption(Integer.parseInt(payment.getUniqueCode()));
				if(couponRedemption != null) {
					if(!couponRedemption.getRedemptionType().equals(RedemptionType.BONUS_POINTS)) {
						addBonusChips(bonusChips, playerRealTransection, player, payment);
						PlayerBonus playerBonus = new PlayerBonus();
						playerBonus.setPid(player.getId());
						if (bonusChips != null) {
							playerBonus.setValue(bonusChips);
							playerBonus.setD(new Date());
							Calendar calendar = Calendar.getInstance();
							calendar.add(Calendar.DAY_OF_MONTH, 30);
							playerBonus.setEd(calendar.getTime());
							PlayerBonusDao.persist(playerBonus);
							PlayerDao.updateWallet(payment.getUserId(), null, bonusChips, null, null, null);
							if (bonusChips != null && bonusChips > 0)
								AnalyticsManager.bonusChipsIssued(player.getId(), null, bonusChips.longValue());
						}
					}
				}
			} else {
				addBonusChips(bonusChips, playerRealTransection, player, payment);
				PlayerBonus playerBonus = new PlayerBonus();
				playerBonus.setPid(player.getId());
				if (bonusChips != null) {
					playerBonus.setValue(bonusChips);
					playerBonus.setD(new Date());
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_MONTH, 30);
					playerBonus.setEd(calendar.getTime());
					PlayerBonusDao.persist(playerBonus);
					PlayerDao.updateWallet(payment.getUserId(), null, bonusChips, null, null, null);
					if (bonusChips != null && bonusChips > 0)
						AnalyticsManager.bonusChipsIssued(player.getId(), null, bonusChips.longValue());
				}
			}
				player.getPlayerBankBalanceSheet().add(Long.toString(chipAmount)); //Adding chipAmount to playerBankBalancesheet 
				PlayerDao.updatePlayerBankBalanceSheet(player.getId(), player.getPlayerBankBalanceSheet());
			     
			}

			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	//for upgrade
	public static void upgradeNow(Payment payment) {
		try {
			Long chipAmount = Long.valueOf(payment.getAmount());
			Long updatedChipAmount= null;
			Player player = PlayerDao.getById(payment.getUserId());
			if (player != null) {
				Long upgradationFee = null;
				Long bonusChips = null;
				Category toUpdateCategory = null;
				Category currentCategory = InMemory.categoryMap.get(player.getPlayerClass());
				if (chipAmount > currentCategory.getFirstDepositMax() && currentCategory.getFirstDepositMax() != 0) {
					Optional<Category> toUpdate = InMemory.categories.stream()
							.filter(c -> (chipAmount >= c.getFirstDepositMin()
									&& (c.getFirstDepositMin() == 0 || chipAmount <= c.getFirstDepositMax())))
							.findFirst();
					if (toUpdate.isPresent()) {
						currentCategory = toUpdate.get();
						toUpdateCategory = currentCategory;
					}
				}
				if(toUpdateCategory != null)
					upgradationFee= new Long(toUpdateCategory.getUpgradationFee());
				bonusChips = (long) (chipAmount * (currentCategory.getBonusChipsPert() / 100));
				List<PlayerTransaction> playerTransectionList = new ArrayList<PlayerTransaction>();
				PlayerTransaction playerRealTransection = new PlayerTransaction(); 
				playerRealTransection.setChips(chipAmount); 
				playerRealTransection.setType(TransactionType.CLASS_UPGRADE); 
				playerRealTransection.setPid(player.getId()); 
				playerRealTransection.setRefId(payment.getId()); 
				playerRealTransection.setOldClass(player.getPlayerClass());
				playerRealTransection.setUpdateClass(toUpdateCategory.getTitle());   
				playerRealTransection.setDisplayTransaxId(payment.getDisplayTransaxId());
				playerTransectionList.add(playerRealTransection); 
				PlayerTransactionDao.persist(playerRealTransection);
				com.actolap.wse.Utils.sendMailForBuyIn(chipAmount, playerRealTransection, player, Config.DOMAIN,
						"Buy-In for transaction ID " + payment.getDisplayTransaxId());
				if (player.getMobile() != null)
					LyveSendSms.sendSmsSuccessBuyIn(player.getMobile().toString(), chipAmount.toString(),
							player.getUserName());
				if(chipAmount >= upgradationFee) {
					updatedChipAmount = chipAmount - upgradationFee;
				} else {
					updatedChipAmount = chipAmount;
				}
				
				PlayerDao.updateBuyInDetail(player.getId(), updatedChipAmount, null, toUpdateCategory);
				Player playersheet = PlayerDao.getById(player.getId());
				if(playersheet!=null) {
					playersheet.getPlayerBankBalanceSheet().add(Long.toString(updatedChipAmount));
					PlayerDao.updatePlayerBankBalanceSheet(player.getId(), playersheet.getPlayerBankBalanceSheet());
				}
				AnalyticsManager.moneyDeposited(player.getId(), chipAmount);
				
				if (player.getMobile() != null && toUpdateCategory != null && !player.getPlayerClass().equals(toUpdateCategory.getTitle())) {
					LOG.info("entered to change playerclass");
					LyveSendSms.sendSmsUpgradeClass(player.getMobile().toString(), toUpdateCategory.getTitle(), player.getUserName());
				} else { 
					LOG.info("not entered to change playerclass");
				}
				PaymentDao.processed(payment.getId());
			
				
			
			if(payment.getUniqueCode() != null && !payment.getUniqueCode().equals("undefined") && !payment.getUniqueCode().equals("")) {
				handleCoupon(payment, player);
				CouponRedemption couponRedemption = CouponDao.getLiveRedemption(Integer.parseInt(payment.getUniqueCode()));
				if(couponRedemption != null) {
					if(!couponRedemption.getRedemptionType().equals(RedemptionType.BONUS_POINTS)) {
						addBonusChips(bonusChips, playerRealTransection, player, payment);
						PlayerBonus playerBonus = new PlayerBonus();
						playerBonus.setPid(player.getId());
						if (bonusChips != null) {
							playerBonus.setValue(bonusChips);
							playerBonus.setD(new Date());
							Calendar calendar = Calendar.getInstance();
							calendar.add(Calendar.DAY_OF_MONTH, 30);
							playerBonus.setEd(calendar.getTime());
							PlayerBonusDao.persist(playerBonus);
							PlayerDao.updateWallet(payment.getUserId(), null, bonusChips, null, null, null);
							if (bonusChips != null && bonusChips > 0)
								AnalyticsManager.bonusChipsIssued(player.getId(), null, bonusChips.longValue());
						}
					}
				}
			} else {
				addBonusChips(bonusChips, playerRealTransection, player, payment);
				PlayerBonus playerBonus = new PlayerBonus();
				playerBonus.setPid(player.getId());
				if (bonusChips != null) {
					playerBonus.setValue(bonusChips);
					playerBonus.setD(new Date());
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_MONTH, 30);
					playerBonus.setEd(calendar.getTime());
					PlayerBonusDao.persist(playerBonus);
					PlayerDao.updateWallet(payment.getUserId(), null, bonusChips, null, null, null);
					if (bonusChips != null && bonusChips > 0)
						AnalyticsManager.bonusChipsIssued(player.getId(), null, bonusChips.longValue());
				}
			}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@GET
	@Path("/paymentDetailsPayu")
	@ApiOperation(value = "Get Player Details", notes = "Get PlayerDetails Details By Id", response = PlayerDetailsGetResponse.class, httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "amount", value = "amount", dataType = "long", paramType = "query", required = false) })
	public PlayerDetailsGetResponse paymentDetailsPayu(Request request, Response responseO) {
		PlayerDetailsGetResponse response = new PlayerDetailsGetResponse();
		String playerId = request.getHeader(Urlparams.playerId);
		if (Utils.isNotEmpty(playerId)) {
			try {
				Player player = PlayerDao.getById(playerId);
				response.setPlayerId(playerId);
				response.setFirstName(player.getProfile().getFirstName());
				response.setLastName(player.getProfile().getLastName());
				response.setEmail(player.getEmail());
				response.setMobile(player.getMobile());
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id should not be empty");
		}
		return response;
	}

	public static void addBonusChips(Long bonusChips, PlayerTransaction playerTransection, Player player,
			Payment payment) {
		PlayerTransaction playerTransaction = new PlayerTransaction();
		playerTransaction.setBonus(bonusChips);
		playerTransaction.setType(TransactionType.EARNED); 
		playerTransaction.setPid(player.getId());
		playerTransaction.setRefId(payment.getId());
		playerTransaction.setDisplayTransaxId(payment.getDisplayTransaxId());
		PlayerTransactionDao.persist(playerTransaction);
	}

	public static void handleCoupon (Payment payment, Player player) {
		
		if(payment.getUniqueCode() != null && !payment.getUniqueCode().equals("undefined")) {
			Coupon coupon = null;
			CouponRedemption couponRedemption = CouponDao.getLiveRedemption(Integer.parseInt(payment.getUniqueCode()));
			if(couponRedemption != null && couponRedemption.getCouponCode() != null) {
				coupon = CouponDao.getLiveCouponByCodeNumber(couponRedemption.getCouponCode());
				if(coupon != null && coupon.getRedemptionCount() < coupon.getMaxRedemption()) {
			if(couponRedemption.getMaxDeposit() > 0 && couponRedemption.getMinDeposit() > 0) {
				if(Integer.parseInt(payment.getAmount()) >= couponRedemption.getMinDeposit() && Integer.parseInt(payment.getAmount()) <= couponRedemption.getMaxDeposit()) {
					PlayerTransaction playerTransaction = new PlayerTransaction();
				if(couponRedemption.getRedemptionType().equals(RedemptionType.BONUS_POINTS)) {
					PlayerDao.updateWallet(payment.getUserId(), null, (long)couponRedemption.getPoints(), null, null, null);
					playerTransaction.setBonus((long)couponRedemption.getPoints());
				}
				else if(couponRedemption.getRedemptionType().equals(RedemptionType.TOURNAMENT_POINTS)) {
					PlayerDao.updateWallet(payment.getUserId(), null, null, null, null, (long)couponRedemption.getPoints());
					playerTransaction.setTournamentChips((long)couponRedemption.getPoints());
				}
				else if(couponRedemption.getRedemptionType().equals(RedemptionType.VIP_POINTS)) {
					PlayerDao.updateWallet(payment.getUserId(), null, null, null, (long)couponRedemption.getPoints(), null);
					playerTransaction.setVip((long)couponRedemption.getPoints());
				}
				else if(couponRedemption.getRedemptionType().equals(RedemptionType.CASH)) {
					PlayerDao.updateWallet(payment.getUserId(), null, null, (long)couponRedemption.getPoints(), null, null);
					playerTransaction.setRealChips((long)couponRedemption.getPoints());
				}
				playerTransaction.setType(TransactionType.COUPON_REIMBURSEMENT);
				playerTransaction.setPid(player.getId());
				playerTransaction.setCouponCode(couponRedemption.getCouponCode());
				PlayerTransactionDao.persist(playerTransaction);
				CouponDao.updatecouponredemption(Integer.parseInt(payment.getUniqueCode()), true);
					CouponDao.increaseRedemptionCount(coupon.getId());
				
				
			}
				}
				}
		}
		}
		
	}
	
	public static void CouponPayment(Payment payment, Player player) {
		try {
			if(payment.getUniqueCode() != null) {
				Coupon coupon = CouponDao.getByCodeNumber(payment.getUniqueCode());
				if(coupon != null) {
					PlayerTransaction playerTransaction = new PlayerTransaction();
					if(coupon.getRedemptionType().equals(RedemptionType.BONUS_POINTS)) {
						PlayerDao.updateWallet(payment.getUserId(), null, (long)coupon.getPoints(), null, null, null);
						playerTransaction.setBonus((long)coupon.getPoints());
					}
					else if(coupon.getRedemptionType().equals(RedemptionType.TOURNAMENT_POINTS)) {
						PlayerDao.updateWallet(payment.getUserId(), null, null, null, null, (long)coupon.getPoints());
						playerTransaction.setTournamentChips((long)coupon.getPoints());
					}
					else if(coupon.getRedemptionType().equals(RedemptionType.VIP_POINTS)) {
						PlayerDao.updateWallet(payment.getUserId(), null, null, null, (long)coupon.getPoints(), null);
						playerTransaction.setVip((long)coupon.getPoints());
					}
					playerTransaction.setType(TransactionType.COUPON_REIMBURSEMENT);
					playerTransaction.setPid(player.getId());
					PlayerTransactionDao.persist(playerTransaction);
					
					CouponRedemption couponRedemption = new CouponRedemption();
					couponRedemption.setCouponCode(coupon.getCodeNumber());
					couponRedemption.setPlayerId(player.getId());
					couponRedemption.setArchive(true);
					couponRedemption.setCouponType(coupon.getCouponType().toString());
					CouponDao.persistRedemption(couponRedemption);
					if(coupon.getCodeNumber() != null)
						coupon = CouponDao.getLiveCouponByCodeNumber(coupon.getCodeNumber());
					if(coupon != null)
						CouponDao.increaseRedemptionCount(coupon.getId());
					
				}
			}
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	public static void pdfDownload(Payment payment,Player player) {
		Long chipAmount = Long.valueOf(payment.getAmount());
		PlayerTransaction playerTransaction = new PlayerTransaction();
		playerTransaction.setPid(player.getId());
		playerTransaction.setType(TransactionType.PDF_DOWNLOAD);
		playerTransaction.setRealChips(chipAmount);
		PlayerTransactionDao.persist(playerTransaction);
		
	}

	@GET
	@Path("/validateBuying/gaming")
	@ApiOperation(value = "Validate Player Buyin", notes = "Get Player Detail for Validate Buyin By Id", response = ResponsibleGamingResponse.class, httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "buyinAmount", value = "buyinAmount", dataType = "String", paramType = "query", required = true),
			@ApiImplicitParam(name = "Player Id", value = "playerId", dataType = "String", paramType = "query", required = true) })
	public ResponsibleGamingResponse validateBuyinGaming(Request request, Response responseO) {
		ResponsibleGamingResponse response = new ResponsibleGamingResponse();
		String playerId = request.getHeader(Urlparams.playerId);
		String amount = request.getHeader(Urlparams.buyinAmount);
		long totalAmount = Long.parseLong(amount);
		if (Utils.isNotEmpty(playerId)) {
			try {
				boolean isDailyBuyin = true;
				boolean isWeekBuyin = true;
				boolean isMonthBuyin = true;
				ResponsibleGaming responsibleGaming = ResponsibleGamingDao.getById(playerId);
				if (responsibleGaming != null) {
					if (responsibleGaming.isMonthlyLimit() && responsibleGaming.getMonthly().getDepLimit() > 0) {
						long chipsAmount = 0, totalChipsAmount = 0;
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.MONTH, -1); 
						Date sDate = cal.getTime(); 
						Date startDate = sdf.parse(Utils.utcToIst(sDate));
						Date endDate = new Date();
						List<PlayerTransaction> playerTransactionList = PlayerTransactionDao
								.getListByPlayerIdandType(playerId, startDate, endDate);
						if (playerTransactionList != null) {
							for (PlayerTransaction playerTransaction : playerTransactionList) {
								chipsAmount = chipsAmount + playerTransaction.getChips();
							}
							totalChipsAmount = responsibleGaming.getMonthly().getDepLimit();
							if (totalAmount <= totalChipsAmount && chipsAmount <= totalChipsAmount) {
								isMonthBuyin = true;
								response.setBuyInTrue(isMonthBuyin);
							} else {
								isMonthBuyin = false;
								response.setBuyInTrue(isMonthBuyin);
							}
						} else {
							isMonthBuyin = true;
							response.setBuyInTrue(isMonthBuyin);
						}
					}
					if (isMonthBuyin && responsibleGaming.isWeeklyLimit() && responsibleGaming.getWeekly().getDepLimit() > 0) {
						long chipsAmount = 0, totalChipsAmount = 0;
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DATE, -7);
						Date sDate = cal.getTime();
						Date startDate = sdf.parse(Utils.utcToIst(sDate));
						Date endDate = new Date();
						List<PlayerTransaction> playerTransactionList = PlayerTransactionDao
								.getListByPlayerIdandType(playerId, startDate, endDate);
						if (playerTransactionList != null) {
							for (PlayerTransaction playerTransaction : playerTransactionList) {
								chipsAmount = chipsAmount + playerTransaction.getChips();
							}
							totalChipsAmount = responsibleGaming.getWeekly().getDepLimit();
							if (totalAmount <= totalChipsAmount && chipsAmount <= totalChipsAmount) {
								isWeekBuyin = true;
								response.setBuyInTrue(isWeekBuyin);
							} else {
								isWeekBuyin = false;
								response.setBuyInTrue(isWeekBuyin);
							}
						} else {
							isWeekBuyin = true;
							response.setBuyInTrue(isWeekBuyin);
						}
					}
					if (isWeekBuyin && responsibleGaming.isDailyLimit() && responsibleGaming.getDaily().getDepLimit() > 0) {
						Date startDate = new Date();
						Date sDate = sdf.parse(Utils.utcToIst(startDate));
						long chipsAmount = 0;
						List<PlayerTransaction> playerTransactionList = PlayerTransactionDao
								.getListByPlayerIdandType(playerId, sDate, null);
						if (playerTransactionList != null) {
							for (PlayerTransaction playerTransaction : playerTransactionList) {
								chipsAmount = chipsAmount + playerTransaction.getChips();
							}
							if (responsibleGaming.getDaily().getDepLimit() >= 0) {
								if (totalAmount <= responsibleGaming.getDaily().getDepLimit() && chipsAmount <= responsibleGaming.getDaily().getDepLimit()) {
									isDailyBuyin = true;
									response.setBuyInTrue(isDailyBuyin);
								} else {
									isDailyBuyin = false;
									response.setBuyInTrue(isDailyBuyin);
								}
							} else {
								if (totalAmount <= responsibleGaming.getDaily().getDepLimit()) {
									isDailyBuyin = true;
									response.setBuyInTrue(isDailyBuyin);
								} else {
									isDailyBuyin = false;
									response.setBuyInTrue(isDailyBuyin);
								}
							}
						} else {
							isDailyBuyin = true;
						}
					}
				} else {
					isDailyBuyin = true;
				}
				if (!isMonthBuyin) {
					response.setEd("You can not buyin more than your Monthly Limit");
				} else if (!isWeekBuyin) {
					response.setEd("You can not buyin more than your Weekly Limit");
				} else if (!isDailyBuyin) {
					response.setEd("You can not buyin more than your Daily Limit");
				} else {
					response.setBuyInTrue(true);
				}
				response.setS(true);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id should not be empty");
		}
		return response;
	}

}

