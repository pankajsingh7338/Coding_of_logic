

package com.actolap.wse.fe.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actolap.wse.LyveSendSms;
import com.actolap.wse.commons.GenericResponse;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.constants.Urlparams;
import com.actolap.wse.dao.AffiliateDao;
import com.actolap.wse.dao.AffiliatePlayerDao;
import com.actolap.wse.dao.OneTimePasswordDao;
import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.model.OneTimePassword;
import com.actolap.wse.model.affiliate.Affiliate;
import com.actolap.wse.model.game.poker.AffiliatePlayer;
import com.actolap.wse.model.game.poker.AffiliateStatus;
import com.actolap.wse.model.game.poker.AffiliatePlayer.AffiliateReferralStatus;
import com.actolap.wse.model.game.poker.AffiliatePlayer.PlayerReferralStatus;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.player.PlayerStatus;
import com.actolap.wse.request.OTPRequest;
import com.actolap.wse.request.WithDrawOtpRequest;
import com.actolap.wse.response.OTPAuthenticationResponse;
import com.actolap.wse.response.OtpGetResponse;

@Path("/otp")
@Api(value = "Rest API")
public class OTPController {
	private static final Logger LOG = LoggerFactory.getLogger(OTPController.class);

	public GenericResponse validate(Request request, Response response) {
		return validateDoc(request.getBodyAs(OTPRequest.class), request, response);
	}

	public GenericResponse otpWithDrawl(Request request, Response response) {
		return otpForWithdrawDoc(request.getBodyAs(WithDrawOtpRequest.class), request, response);
	}

	@GET
	@Path("/get")
	@ApiOperation(value = "Get OTP", notes = "Player gets OPT to verify signup or new mobile number.And also use for update new mobile number", response = OtpGetResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "playerId", value = "Player Id", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "mobile", value = "Mobile Number", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "resend", value = "Resend (Only for resend OTP message)", dataType = "boolean", paramType = "query", required = true),
			@ApiImplicitParam(name = "validateOtpCount", value = "Reset login otp validate count (Only for first time )", dataType = "boolean", paramType = "query") })
	public OtpGetResponse getOtp(Request request, Response responseO) {
		OtpGetResponse response = new OtpGetResponse();
		String playerId = request.getHeader(Urlparams.playerId);
		String mobile = request.getHeader(Urlparams.mobile);
		boolean validateOtpCount = Boolean.valueOf(request.getHeader(Urlparams.validateOtpCount));
		boolean isResend = Boolean.valueOf(request.getHeader(Urlparams.resend));
		if (Utils.isNotEmpty(mobile)) {
			try {	   
				Player playerObj = PlayerDao.getById(playerId);
				if (playerObj != null) {
					boolean mobileError = false;
					if (!Long.valueOf(mobile).equals(playerObj.getMobileUnverified())) {
						Player existPlayer = PlayerDao.getByMobile(Long.valueOf(mobile));
						if (existPlayer == null) { 
							PlayerDao.updateUnverifiedMobile(playerId, Long.valueOf(mobile)); 
						} else { 
							mobileError = true; 
						} 
					} 
					if (!mobileError) {
						String otp = com.actolap.wse.Utils.generateOTP();
						String otpResponse = LyveSendSms.setOtp(mobile, otp);
						String[] responseParts = otpResponse.split("\\|");
						String responseCode = responseParts[0];
						if (responseCode.trim().equals("success")) {
							String transactionId = responseParts[2].trim();
							OneTimePassword oneTimePassword = new OneTimePassword();
							oneTimePassword.setPlayerId(playerId);
							oneTimePassword.setOtp(otp);
							oneTimePassword.setMobile(mobile);
							oneTimePassword.setTransactionId(transactionId);
							oneTimePassword.setExpireDate(new Date());
							Player player = PlayerDao.getById(playerId);
							if (player != null)
								oneTimePassword.setEmail(player.getEmail());
							OneTimePasswordDao.persist(oneTimePassword);
							response.setTransactionId(transactionId);
							if (validateOtpCount && player.getLoginOtpValidation() != null
									&& player.getLoginOtpValidation().getCount() < 4) {
								Map<String, Object> playerMap = new HashMap<String, Object>();
								playerMap.put("loginOtpValidation.count", 0);
								PlayerDao.update(player.getId(), playerMap);
							}
							if (isResend) {
								response.setMsg("For your request, we have resend the OTP to your mobile number");
							} else {
								response.setMsg("OTP sent to your mobile number");
							}
							response.setS(true);
						} else {
							response.setEd("This mobile number is invalid");
						}
					} else {
						response.setEd("This mobile number has been already registered");
					}
				} else {
					response.setEd("Invalid player id");
				}
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage(), e);
				response.setEd("Mobile number is invalid");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Mobile number can not be empty");
		}
		return response;
	}

	@GET
	@Path("/getSignUp")
	@ApiOperation(value = "Get Sign Up OTP", notes = "Player gets OPT for Sign Up request", response = OtpGetResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "playerId", value = "Player Id", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "mobile", value = "Mobile Number", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "resend", value = "Resend (Only for resend OTP message)", dataType = "boolean", paramType = "query", required = true),
			@ApiImplicitParam(name = "validateOtpCount", value = "Reset login otp validate count (Only for first time )", dataType = "boolean", paramType = "query") })
	public OtpGetResponse getOtpSignUp(Request request, Response responseO) {
		OtpGetResponse response = new OtpGetResponse();
		String playerId = request.getHeader(Urlparams.playerId);
		String mobile = request.getHeader(Urlparams.mobile);
		boolean validateOtpCount = Boolean.valueOf(request.getHeader(Urlparams.validateOtpCount));
		boolean isResend = Boolean.valueOf(request.getHeader(Urlparams.resend));
		if (Utils.isNotEmpty(mobile)) {
			try {
				Player playerObj = PlayerDao.getById(playerId);
				if (playerObj != null) {
					boolean mobileError = false;
					if (!Long.valueOf(mobile).equals(playerObj.getMobileUnverified())) {
						Player existPlayer = PlayerDao.getByMobile(Long.valueOf(mobile));
						if (existPlayer == null) {
							PlayerDao.updateUnverifiedMobile(playerId, Long.valueOf(mobile));
						} else {
							mobileError = true;
						}
					}
					if (!mobileError) {
						String otp = com.actolap.wse.Utils.generateOTP();
						String otpResponse = LyveSendSms.setOtpForSignUp(mobile, otp);
						String[] responseParts = otpResponse.split("\\|");
						String responseCode = responseParts[0];
						if (responseCode.trim().equals("success")) {
							String transactionId = responseParts[2].trim();
							OneTimePassword oneTimePassword = new OneTimePassword();
							oneTimePassword.setPlayerId(playerId);
							oneTimePassword.setOtp(otp);
							oneTimePassword.setMobile(mobile);
							oneTimePassword.setTransactionId(transactionId);
							oneTimePassword.setExpireDate(new Date());
							Player player = PlayerDao.getById(playerId);
							if (player != null)
								oneTimePassword.setEmail(player.getEmail());
							OneTimePasswordDao.persist(oneTimePassword);
							response.setTransactionId(transactionId);
							if (validateOtpCount && player.getLoginOtpValidation() != null
									&& player.getLoginOtpValidation().getCount() < 4) {
								Map<String, Object> playerMap = new HashMap<String, Object>();
								playerMap.put("loginOtpValidation.count", 0);
								PlayerDao.update(player.getId(), playerMap);
							}
							if (isResend) {
								response.setMsg("For your request, we have resend the OTP to your mobile number");
							} else {
								response.setMsg("OTP sent to your mobile number");
							}
							response.setS(true);
						} else {
							response.setEd("This mobile number is invalid");
						}
					} else {
						response.setEd("This mobile number has been already registered");
					}
				} else {
					response.setEd("Invalid player id");
				}
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage(), e);
				response.setEd("Mobile number is invalid");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Mobile number can not be empty");
		}
		return response;
	}

	@POST
	@Path("/getWithdraw")
	@ApiOperation(value = "Get OTP For Withdraw", notes = "Player gets OPT to verify Withdraw or new mobile number.And also use for update new mobile number", response = OtpGetResponse.class, httpMethod = "post")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "playerId", value = "Player Id", dataType = "string", paramType = "query", required = true) })
	public OtpGetResponse otpForWithdrawDoc(@ApiParam(required = true) WithDrawOtpRequest withDrawOtpRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response) {
		OtpGetResponse otpGetResponse = new OtpGetResponse();
		String playerId = request.getHeader(Urlparams.playerId);
		String mobile = null;
		if (withDrawOtpRequest.getMobile() != null)
			mobile = withDrawOtpRequest.getMobile();
		boolean validateOtpCount = withDrawOtpRequest.isValidateOtpCount();
		boolean isResend = withDrawOtpRequest.isResend();
		if (Utils.isNotEmpty(mobile)) {
			try {
				Player playerObj = PlayerDao.getById(playerId);
				if (playerObj != null) {
					boolean mobileError = false;
					if (!Long.valueOf(mobile).equals(playerObj.getMobileUnverified())) {
						Player existPlayer = PlayerDao.getByMobile(Long.valueOf(mobile));
						if (existPlayer == null) {
							PlayerDao.updateUnverifiedMobile(playerId, Long.valueOf(mobile));
						} 
					}
					if (!mobileError) {
						String otp = com.actolap.wse.Utils.generateOTP();
						String otpResponse = LyveSendSms.setOtpForWithdraw(mobile, otp);
						String[] responseParts = otpResponse.split("\\|");
						String responseCode = responseParts[0];
						if (responseCode.trim().equals("success")) {
							String transactionId = responseParts[2].trim();
							OneTimePassword oneTimePassword = new OneTimePassword();
							oneTimePassword.setPlayerId(playerId);
							oneTimePassword.setOtp(otp);
							oneTimePassword.setMobile(mobile);
							oneTimePassword.setTransactionId(transactionId);
							oneTimePassword.setExpireDate(new Date());
							Player player = PlayerDao.getById(playerId);
							if (player != null)
								oneTimePassword.setEmail(player.getEmail());
							OneTimePasswordDao.persist(oneTimePassword);
							otpGetResponse.setTransactionId(transactionId);
							if (validateOtpCount && player.getLoginOtpValidation() != null
									&& player.getLoginOtpValidation().getCount() < 4) {
								Map<String, Object> playerMap = new HashMap<String, Object>();
								playerMap.put("loginOtpValidation.count", 0);
								PlayerDao.update(player.getId(), playerMap);
							}
							if (isResend) {
								otpGetResponse.setMsg("For your request, we have resend the OTP to your mobile number");
							} else {
								otpGetResponse.setMsg("OTP sent to your mobile number");
							}
							otpGetResponse.setS(true);
						} else {
							otpGetResponse.setEd("This mobile number is invalid");
						}
					} else {
						otpGetResponse.setEd("This mobile number has been already registered");
					}
				} else {
					otpGetResponse.setEd("Invalid player id");
				}
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage(), e);
				otpGetResponse.setEd("Mobile number is invalid");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				otpGetResponse.setEd(e.getMessage());
			}
		} else {
			otpGetResponse.setEd("Mobile number can not be empty");
		}
		return otpGetResponse;
	}

	@POST
	@Path("/validate")
	@ApiOperation(value = "Validate OTP", notes = "OTP validate based on the OTP number send on player mobile", response = OTPAuthenticationResponse.class, httpMethod = "post")
	public OTPAuthenticationResponse validateDoc(@ApiParam(required = true) OTPRequest otpRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response) {
		OTPAuthenticationResponse otpResponse = new OTPAuthenticationResponse();
		try {
			SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
			if (otpRequest != null && Utils.isNotEmpty(otpRequest.getOtp())
					&& Utils.isNotEmpty(otpRequest.getTransactionId()) && otpRequest.getMobile() != null) {
				OneTimePassword otp = OneTimePasswordDao.getByTransactionId(otpRequest.getTransactionId());

				Date d1 = format.parse(otp.getCreateTime().toString());
				Date d2 = format.parse(new Date().toString());
				long diff = d2.getTime() - d1.getTime();
				long diffMinutes = diff / (60 * 1000) % 60;
				if (otp != null) {
					Player player = PlayerDao.getById(otp.getPlayerId());
					Integer freezeTime = null;
					if (player.getLoginOtpValidation() != null
							&& player.getLoginOtpValidation().getFreezeTime() != null)
						freezeTime = 15 - com.actolap.wse.Utils.getAccountFreezedTime(player);
					if (player.getStatus() != PlayerStatus.BLOCKED) {
						
							if (!otpRequest.isValidateCount()
									|| (otpRequest.isValidateCount() && (player.getLoginOtpValidation() == null
											|| player.getLoginOtpValidation().getCount() < 4
											|| player.getLoginOtpValidation().getFreezeTime() == null
											|| (freezeTime != null && freezeTime <= 0)))) {
								if (otp.getOtp().equals(otpRequest.getOtp())
										&& otp.getMobile().equals(otpRequest.getMobile())) {
									if (diffMinutes < 5) {
									PlayerDao.updateMobileNumber(otp.getPlayerId(),
											Long.valueOf(otpRequest.getMobile()));
									otpResponse.setEmail(otp.getEmail());
									otpResponse.setPlayerId(otp.getPlayerId());
									if (player != null && player.getLegal() != null)
										otpResponse.setTermAccepted(player.getLegal().isTermsAccepted());
									if(player.getTempAffiliateCode() != null) {
										Affiliate affiliate = AffiliateDao.getByReferralCode(player.getTempAffiliateCode());
										if (affiliate != null && affiliate.getStatus().equals(AffiliateStatus.ACTIVE)) {
											AffiliatePlayer existingPlayer = AffiliatePlayerDao.getByPlayerAndAffiliateId(affiliate.getId(), player.getId());
											if (existingPlayer == null) {
												AffiliatePlayer affiliatePlayer = AffiliatePlayerDao.getRejectedPlayer(player.getId());
												if (affiliatePlayer == null || (affiliatePlayer.getStatus().equals(PlayerReferralStatus.REJECTED))) {
													AffiliatePlayer affiliatePlayerobj = new AffiliatePlayer();
													affiliatePlayerobj.setUserName(player.getUserName());
													affiliatePlayerobj.setAffiliateId(affiliate.getId());
													affiliatePlayerobj.setPlayerId(player.getId());
													affiliatePlayerobj.setRegistered(new Date());
													affiliatePlayerobj.setStatus(PlayerReferralStatus.PENDING);
													affiliatePlayerobj.setAffiliateStatus(AffiliateReferralStatus.PENDING);
													AffiliatePlayerDao.persist(affiliatePlayerobj);
													PlayerDao.updateAffiliateReferralCode(player.getId(), player.getTempAffiliateCode());
												} 
											} 
										}
									}
									otpResponse.setS(true);
									otpResponse.setMobile(otpRequest.getMobile());
									otpResponse.setMsg("Your mobile has been successfully updated.");
									if (otpRequest.isValidateCount()) {
										Map<String, Object> playerMap = new HashMap<String, Object>();
										playerMap.put("loginOtpValidation.count", 0);
										playerMap.put("status", PlayerStatus.ACTIVE);
										PlayerDao.update(player.getId(), playerMap);
									}
									} else {
										otpResponse.setEd("Otp Expired.");
									}
								} else {
									if (otpRequest.isValidateCount()) {
										validateOtpLoginCount(otpRequest, player);
									}
									otpResponse.setEd("Invalid OTP, please enter a valid OTP.");
								}
							} else {
								otpResponse.setEd("Your account has been frozen for " + freezeTime
										+ " min, Please try login after " + freezeTime + " min.");
							}
						
					} else {
						otpResponse.setEd("Your account has been blocked.");
					}
				} else {
					otpResponse.setEd("No OTP found with this transaction id.");
				}
			} else {
				otpResponse.setEd("Required fields are coming invalid");
			}
		} catch (NumberFormatException e) {
			LOG.error(e.getMessage(), e);
			otpResponse.setEd("Mobile number is invalid");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			otpResponse.setEd(e.getMessage());
		}
		return otpResponse;
	}

	public static void validateOtpLoginCount(OTPRequest otpRequest, Player player) {
		Map<String, Object> playerMap = new HashMap<String, Object>();
		int count = 0;
		if (player.getLoginOtpValidation() != null)
			count = player.getLoginOtpValidation().getCount();
		if (player.getLoginOtpValidation() == null
				|| (player.getLoginOtpValidation() != null && player.getLoginOtpValidation().getCount() <= 4))
			playerMap.put("loginOtpValidation.count", count + 1);
		if (player.getLoginOtpValidation() != null && player.getLoginOtpValidation().getCount() == 3) {
			playerMap.put("loginOtpValidation.freezeTime", new Date());
			playerMap.put("status", PlayerStatus.FREEZED);
		} else if (player.getLoginOtpValidation() != null && player.getLoginOtpValidation().getCount() == 4
				&& com.actolap.wse.Utils.getAccountFreezedTime(player) >= 15) {
			playerMap.put("status", PlayerStatus.BLOCKED);
			playerMap.put("loginOtpValidation.count", count + 1);
		}
		if (!playerMap.isEmpty())
			PlayerDao.update(player.getId(), playerMap);
	}
	
	@GET
	@Path("/get/chipConversion")
	@ApiOperation(value = "Get Chip Conversion OTP", notes = "Player gets Otp at the time of chip conversion", response = OtpGetResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "playerId", value = "Player Id", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "mobile", value = "Mobile Number", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "amount", value = "Mobile Number", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "resend", value = "Resend (Only for resend OTP message)", dataType = "boolean", paramType = "query", required = true),
			@ApiImplicitParam(name = "validateOtpCount", value = "Reset login otp validate count (Only for first time )", dataType = "boolean", paramType = "query") })
	public OtpGetResponse getChipConversionOtp(Request request, Response responseO) {
		OtpGetResponse response = new OtpGetResponse();
		String playerId = request.getHeader(Urlparams.playerId);
		String mobile = request.getHeader(Urlparams.mobile);
		String vipPoints = request.getHeader(Urlparams.vipPoints);
		boolean validateOtpCount = Boolean.valueOf(request.getHeader(Urlparams.validateOtpCount));
		boolean isResend = Boolean.valueOf(request.getHeader(Urlparams.resend));
		if (Utils.isNotEmpty(mobile)) {
			try {
				Player playerObj = PlayerDao.getById(playerId);
				if (playerObj != null) {
					boolean mobileError = false;
					if (!Long.valueOf(mobile).equals(playerObj.getMobileUnverified())) {
						Player existPlayer = PlayerDao.getByMobile(Long.valueOf(mobile));
						if (existPlayer == null) {
							PlayerDao.updateUnverifiedMobile(playerId, Long.valueOf(mobile));
						} 
					}
					if (!mobileError) {
						String otp = com.actolap.wse.Utils.generateOTP();
						//String otpResponse = com.actolap.wse.Utils.setOtpForChipConversion(mobile, otp, vipPoints);
						String otpResponse = LyveSendSms.setOtp(mobile, otp);
						String[] responseParts = otpResponse.split("\\|");
						String responseCode = responseParts[0];
						if (responseCode.trim().equals("success")) {
							String transactionId = responseParts[2].trim();
							OneTimePassword oneTimePassword = new OneTimePassword();
							oneTimePassword.setPlayerId(playerId);
							oneTimePassword.setOtp(otp);
							oneTimePassword.setMobile(mobile);
							oneTimePassword.setTransactionId(transactionId);
							oneTimePassword.setExpireDate(new Date());
							Player player = PlayerDao.getById(playerId);
							if (player != null)
								oneTimePassword.setEmail(player.getEmail());
							OneTimePasswordDao.persist(oneTimePassword);
							response.setTransactionId(transactionId);
							if (validateOtpCount && player.getLoginOtpValidation() != null
									&& player.getLoginOtpValidation().getCount() < 4) {
								Map<String, Object> playerMap = new HashMap<String, Object>();
								playerMap.put("loginOtpValidation.count", 0);
								PlayerDao.update(player.getId(), playerMap);
							}
							if (isResend) {
								response.setMsg("For your request, we have resend the OTP to your mobile number");
							} else {
								response.setMsg("OTP sent to your mobile number");
							}
							response.setS(true);
						} else {
							response.setEd("This mobile number is invalid");
						}
					} else {
						response.setEd("This mobile number has been already registered");
					}
				} else {
					response.setEd("Invalid player id");
				}
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage(), e);
				response.setEd("Mobile number is invalid");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Mobile number can not be empty");
		}
		return response;
	}
	// @POST
	// @Path("/validate")
	// @ApiOperation(value = "Validate OTP", notes =
	// "OTP validate based on the OTP number send on player mobile", response =
	// OTPAuthenticationResponse.class, httpMethod = "post")
	// public OTPAuthenticationResponse validateDoc(@ApiParam(required = true)
	// OTPRequest otpRequest,
	// @ApiParam(hidden = true) Request request, @ApiParam(hidden = true)
	// Response response) {
	// OTPAuthenticationResponse otpResponse = new OTPAuthenticationResponse();
	// try {
	// if (otpRequest != null && Utils.isNotEmpty(otpRequest.getOtp())
	// && Utils.isNotEmpty(otpRequest.getTransactionId()) &&
	// otpRequest.getMobile() != null) {
	// OneTimePassword otp =
	// OneTimePasswordDao.getByTransactionId(otpRequest.getTransactionId(),
	// otpRequest.getOtp());
	// if (otp != null && otp.getMobile().equals(otpRequest.getMobile())) {
	// PlayerDao.updateMobileNumber(otp.getPlayerId(),
	// Long.valueOf(otpRequest.getMobile()));
	// otpResponse.setEmail(otp.getEmail());
	// otpResponse.setPlayerId(otp.getPlayerId());
	// Player player = PlayerDao.getById(otp.getPlayerId());
	// if (player != null && player.getLegal() != null)
	// otpResponse.setTermAccepted(player.getLegal().isTermsAccepted());
	// otpResponse.setS(true);
	// otpResponse.setMsg("Your mobile has been successfully updated.");
	// } else {
	// otpResponse.setEd("Invalid OTP, please enter a valid OTP.");
	// }
	// } else {
	// otpResponse.setEd("Required fields are coming invalid");
	// }
	// } catch (NumberFormatException e) {
	// LOG.error(e.getMessage(), e);
	// otpResponse.setEd("Mobile number is invalid");
	// } catch (Exception e) {
	// LOG.error(e.getMessage(), e);
	// otpResponse.setEd(e.getMessage());
	// }
	// return otpResponse;
	// }

}

