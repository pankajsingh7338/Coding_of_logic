package com.actolap.wse.fe.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.actolap.config.Config;
import com.actolap.wse.Constants;
import com.actolap.wse.LyveSendSms;
import com.actolap.wse.commons.GenericResponse;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.constants.Urlparams;
import com.actolap.wse.dao.AffiliateDao;
import com.actolap.wse.dao.AffiliatePlayerDao;
import com.actolap.wse.dao.CouponDao;
import com.actolap.wse.dao.LoginValidateDao;
import com.actolap.wse.dao.OneTimePasswordDao;
import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.dao.PokerConfigurationDao;
import com.actolap.wse.dto.PlayerPublicDto;
import com.actolap.wse.game.dao.PlayerBonusDao;
import com.actolap.wse.game.dao.PlayerTransactionDao;
import com.actolap.wse.inmemory.memcache.GlobalCachedManager;
import com.actolap.wse.manager.AnalyticsManager;
import com.actolap.wse.model.OneTimePassword;
import com.actolap.wse.model.TermAndCondition;
import com.actolap.wse.model.affiliate.Affiliate;
import com.actolap.wse.model.backoffice.ApiConfiguration;
import com.actolap.wse.model.game.poker.AffiliatePlayer;
import com.actolap.wse.model.game.poker.PokerConfiguration;
import com.actolap.wse.model.game.poker.AffiliatePlayer.AffiliateReferralStatus;
import com.actolap.wse.model.game.poker.AffiliatePlayer.PlayerReferralStatus;
import com.actolap.wse.model.player.ClassUpdateVipPoint;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.player.PlayerBonus;
import com.actolap.wse.model.player.Player.Gender;
import com.actolap.wse.model.player.Player.LoginSource;
import com.actolap.wse.model.player.Player.LoginType;
import com.actolap.wse.model.player.Player.PlayerSource;
import com.actolap.wse.model.player.PlayerTransaction.TransactionType;
import com.actolap.wse.model.promotion.Coupon;
import com.actolap.wse.model.promotion.CouponType;
import com.actolap.wse.model.promotion.RedemptionType;
import com.actolap.wse.model.player.PlayerClass;
import com.actolap.wse.model.player.PlayerProfile;
import com.actolap.wse.model.player.PlayerSecurityValidate;
import com.actolap.wse.model.player.PlayerStatus;
import com.actolap.wse.model.player.PlayerTransaction;
import com.actolap.wse.request.PlayerLoginRequest;
import com.actolap.wse.request.PlayerSignUpRequest;
import com.actolap.wse.request.PlayerSocialSignUpRequest;
import com.actolap.wse.request.ResetPasswordRequest;
import com.actolap.wse.response.EmailValidationResponse;
import com.actolap.wse.response.LoginValidateListResponse;
import com.actolap.wse.response.PlayerLoginResponse;
import com.actolap.wse.response.PlayerSignUpResponse;
import com.actolap.wse.response.PlayerSocialSignUpResponse;
import com.actolap.wse.response.TermAndConditionResponse;
import com.actolap.wse.response.TermsAcceptedResponse;
import com.actolap.wse.response.TokenResponse;
import com.actolap.wse.rest.secuirty.PermissionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.DuplicateKeyException;

@Path("/player")
@Api(value = "Rest API")
public class PlayerLoginController {

	private static final Logger LOG = LoggerFactory.getLogger(PlayerLoginController.class);

	public PlayerLoginResponse login(Request request, Response response) {
		return loginDoc(request.getBodyAs(PlayerLoginRequest.class), request, response);
	}

	public PlayerSignUpResponse signUp(Request request, Response response) {
		return signUpDoc(request.getBodyAs(PlayerSignUpRequest.class), request, response);
	}

	public PlayerSocialSignUpResponse SocialSignUp(Request request, Response response) {
		return SocialSignUpDoc(request.getBodyAs(PlayerSocialSignUpRequest.class), request, response);
	}

	public PlayerSignUpResponse validateGame(Request request, Response response) {
		return validategameName(request.getBodyAs(PlayerSignUpRequest.class), request, response);
	}

	public GenericResponse passwordUpdate(Request request, Response response) {
		return updatePasswordDoc(request.getBodyAs(ResetPasswordRequest.class), request, response);
	}

	@POST
	@Path("/login")
	@ApiOperation(value = "Player Login", notes = "Player Login By Email or Google or Facebook", response = PlayerLoginResponse.class, httpMethod = "post")
	public PlayerLoginResponse loginDoc(@ApiParam(required = true) PlayerLoginRequest loginRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response) {
		PlayerLoginResponse loginResponse = new PlayerLoginResponse();
		try {
			String email = null;
			LoginType loginType = null;
			if (loginRequest.getLoginType() != null)
				loginType = LoginType.valueOf(loginRequest.getLoginType());
			if (loginRequest != null && loginType != null && Utils.isNotEmpty(loginRequest.getUsername()) && ((loginType
					.equals(LoginType.EMAIL) && Utils.isNotEmpty(loginRequest.getPassword())
					&& Utils.isNotEmpty(loginRequest.getDomain()))
					|| (loginRequest.isEligbleAge() && loginRequest.getGender() != null
							&& ((loginType.equals(LoginType.FACEBOOK) && Utils.isNotEmpty(loginRequest.getFbId()))
									|| (loginType.equals(LoginType.GOOGLE)
											&& Utils.isNotEmpty(loginRequest.getGoogleId())))))) {
				LOG.info("enterd after all checking in player login");
				Player player = null;
				Player playerObj = new Player();
				boolean isError = false;
				boolean isEmail = com.actolap.wse.Utils.isValidEmailAddress(loginRequest.getUsername());
				boolean isGameName = com.actolap.wse.Utils.isValidGameName(loginRequest.getUsername());
				if (loginType.equals(LoginType.EMAIL)) {
					if (isEmail) {
						player = PlayerDao.playerLogIn(loginRequest.getUsername(), loginRequest.getPassword());
						}
					else if (isGameName) {
						player = PlayerDao.loginWithGameName(loginRequest.getUsername(), loginRequest.getPassword());
					} else {
						player = PlayerDao.loginWithMobile(Long.valueOf(loginRequest.getUsername()),
								loginRequest.getPassword());
						if (player == null)
							player = PlayerDao.loginWithUnverifiedMobile(Long.valueOf(loginRequest.getUsername()),
									loginRequest.getPassword());
					}
				} else if (loginType.equals(LoginType.FACEBOOK)) {
					player = PlayerDao.getByEmail(loginRequest.getUsername());
					if (player == null) {
						playerObj.setFbId(loginRequest.getFbId());
					}
				} else if (loginType.equals(LoginType.GOOGLE)) {
					LOG.info("enterd to the google section");
					player = PlayerDao.getByEmail(loginRequest.getUsername());
					if (player == null) {
						playerObj.setGoogleId(loginRequest.getGoogleId());
					}
				}
				PlayerPublicDto playerDto = new PlayerPublicDto();
				LOG.info("entered in DTO");
				
				if (player != null || loginType.equals(LoginType.GOOGLE) || loginType.equals(LoginType.FACEBOOK)) {
					if (player == null
							&& (loginType.equals(LoginType.GOOGLE) || loginType.equals(LoginType.FACEBOOK))) {
						if (Utils.isNotEmpty(loginRequest.getFirstName())
								&& Utils.isNotEmpty(loginRequest.getLastName())) {
							playerObj.setUserName(loginRequest.getFirstName() + " " + loginRequest.getLastName());
						} else {
							playerObj.setUserName(loginRequest.getUsername());
						}

						playerObj.setEmail(loginRequest.getUsername());
						PlayerProfile playerProfile = new PlayerProfile();
						playerProfile.setFirstName(loginRequest.getFirstName());
						playerProfile.setLastName(loginRequest.getLastName());
						playerProfile.setEligbleAge(loginRequest.isEligbleAge());
						if (loginRequest.getAvatar() != null)
							playerProfile.setAvatar(loginRequest.getAvatar());
						else
							playerProfile.setAvatar(Constants.defaultAvatar);
						try {
							Gender gender = Gender.valueOf(loginRequest.getGender());
							playerProfile.setGender(gender);
						} catch (IllegalArgumentException e) {
							LOG.error(e.getMessage(), e);
							loginResponse.setEd("Gender " + loginRequest.getGender() + " is not valid");
							isError = true;
						}
						playerObj.setProfile(playerProfile);
						playerObj.setPlayerClass(PlayerClass.CRYSTAL);
						playerObj.setClassUpdateVipPoint(new ClassUpdateVipPoint(new Date()));
						playerObj.setSource(PlayerSource.DIRECT);
						playerObj.setLoginSource(LoginSource.SOCIAL);
						playerObj.setStatus(PlayerStatus.ACTIVE);
						if (loginType.equals(LoginType.GOOGLE))
							playerObj.setLoginType(LoginType.GOOGLE);
						else
							playerObj.setLoginType(LoginType.FACEBOOK);
						playerObj.setEmailVerified(true);
						if (!isError) {
							PlayerDao.persist(playerObj);
							String token = UUID.randomUUID().toString();
							com.actolap.wse.Utils.sentSecurePinLink(playerObj.getEmail(),
									playerObj.getEmail().substring(0, playerObj.getEmail().indexOf("@")), Config.DOMAIN,
									token, "Generate Secure Pin");
						}
						Utils.setUserName(playerObj, false);
						playerDto.setUserName(playerObj.getUserName());
						playerDto.setPlayerId(playerObj.getId());
						playerDto.setEmail(playerObj.getEmail());
						email = playerObj.getEmail();
						AnalyticsManager.signUp();
					} else {
						if (isEmail) {
							loginResponse.setEmailVerified(player.isEmailVerified());
							loginResponse.setEmailLogin(true);
						} else if (isGameName) {
							loginResponse.setEmailVerified(true);
							loginResponse.setEmailLogin(true);
						} else {
							loginResponse.setEmailVerified(true);
							if (player.getMobile() == null)
								loginResponse.setMobileUnverified(true);
							if (player.getMobile() != null) {
								playerDto.setMobile(player.getMobile().toString());
							} else {
								playerDto.setMobile(player.getMobileUnverified().toString()); 
							}
						}
						if (player.getLegal() != null)
							loginResponse.setTermAccepted(player.getLegal().isTermsAccepted());
						Utils.setUserName(player, false);
						playerDto.setUserName(player.getUserName());
						playerDto.setPlayerId(player.getId());
						playerDto.setEmail(player.getEmail());
					}
					
					if (!isError) {
						if (player == null)
							player = PlayerDao.getByEmail(email);
						if (player.getStatus() != PlayerStatus.BLOCKED && player.isBanPlayer()!=true) {
							Integer freezeTime = null;
							if (player.getLoginOtpValidation() != null
									&& player.getLoginOtpValidation().getFreezeTime() != null)
								freezeTime = 15 - com.actolap.wse.Utils.getAccountFreezedTime(player);
							if (player.getLoginOtpValidation() == null || player.getLoginOtpValidation().getCount() < 4
									|| player.getLoginOtpValidation().getFreezeTime() == null
									|| (freezeTime != null && freezeTime <= 0)) {

								// Checking Multiple logged in with same User ID
								boolean passedToLogin = true;
								if (loginType.equals(LoginType.EMAIL) || loginType.equals(LoginType.GOOGLE)) {
									if ("normal".equalsIgnoreCase(loginRequest.getForcelogin()) && loginResponse.isTermAccepted()) {
										String userToken = (String) GlobalCachedManager.get(playerDto.getPlayerId());
										if (userToken != null) {
											loginResponse.setEd("UserAlreadyLoggedIn");
											passedToLogin = false;
										}
									}
								}
								if (passedToLogin) {
									String token = UUID.randomUUID().toString();

									if (loginType.equals(LoginType.GOOGLE) || loginType.equals(LoginType.FACEBOOK)) {
										PlayerDao.updateLastLogin(playerDto.getPlayerId(), token);
										GlobalCachedManager.set(playerDto.getPlayerId(),
												token + "\t" + request.getHeader(PermissionHandler.USER_HEADER_IP),
												600L);

									} else if (loginResponse.isEmailLogin() && loginResponse.isEmailVerified()
											&& loginResponse.isTermAccepted()) {
										PlayerDao.updateLastLogin(playerDto.getPlayerId(), token);
										GlobalCachedManager.set(playerDto.getPlayerId(),
												token + "\t" + request.getHeader(PermissionHandler.USER_HEADER_IP),
												600L);

									} else if (!loginResponse.isEmailLogin() && !loginResponse.isMobileUnverified()
											&& loginResponse.isTermAccepted()) {
										PlayerDao.updateLastLogin(playerDto.getPlayerId(), token);
										GlobalCachedManager.set(playerDto.getPlayerId(),
												token + "\t" + request.getHeader(PermissionHandler.USER_HEADER_IP),
												600L);
									}
									LOG.info("IPTESTING" + request.getHeader(PermissionHandler.USER_HEADER_IP));
									loginResponse.setToken(token + "_" + playerDto.getPlayerId());
									loginResponse.setPlayer(playerDto);
									loginResponse.setS(true);
								}
							} else {
								loginResponse
										.setEd("You have exceeded the maximum no of OTP limit, Please try login after "
												+ freezeTime + " min.");
							}
						} else {
							loginResponse.setEd(
									"Your account has been temporarily deactivated. Please contact customer service for further assistance.");
						}
					}
				} else {
					loginResponse.setEd("Looks like your email/mobile number/game name or password did not match");
				} 
			} else {
				loginResponse.setEd("Required fields are coming invalid");
			}
		} catch (NumberFormatException e) {
			LOG.error(e.getMessage(), e);
			loginResponse.setEd(e.getMessage());
			loginResponse.setMsg("Looks like your email/mobile number/game name or password did not match");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			loginResponse.setEd(e.getMessage());
		}
		return loginResponse;
	}

	@POST
	@Path("/signup")
	@ApiOperation(value = "Player SignUp", notes = "Player SignUp", response = PlayerSignUpResponse.class, httpMethod = "post")
	public PlayerSignUpResponse signUpDoc(@ApiParam(required = true) PlayerSignUpRequest signUpRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response) {
		PlayerSignUpResponse signUpResponse = new PlayerSignUpResponse();
		try {
			SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
			Date date = format.parse(new Date().toString());
			long forgetLinkTime = date.getTime();
			long generatePinTime = date.getTime();
			if (signUpRequest != null && Utils.isNotEmptyNA(signUpRequest.getFirstName())
					&& Utils.isNotEmptyNA(signUpRequest.getLastName()) && Utils.isNotEmpty(signUpRequest.getPassword())
					&& Utils.isNotEmpty(signUpRequest.getEmail()) && Utils.isNotEmpty(signUpRequest.getGender())
					&& signUpRequest.isEligbleAge() && Utils.isNotEmpty(signUpRequest.getDomain())
					&& signUpRequest.getMobile() != null && signUpRequest.getFirstName().length() <= 25
					&& signUpRequest.getLastName().length() <= 25 && signUpRequest.getGameName() != null
					&& signUpRequest.isTermAccept()) {
				if (com.actolap.wse.Utils.validatePassword(signUpRequest.getPassword())) {
					Player gameNameExist = PlayerDao.getByGameName(signUpRequest.getGameName());
					if(gameNameExist == null) {
					Player playerObj = PlayerDao.getByEmail(signUpRequest.getEmail());
					if (playerObj == null) {
						if (signUpRequest.getMobile() != null)
							playerObj = PlayerDao.getByMobile(Long.valueOf(signUpRequest.getMobile()));
						if (playerObj == null) {
							Player player = new Player();
							boolean isError = false;
							boolean isMobileError = false;
							boolean isReferralError = false;
							boolean isReferVerify = false;
							Player referralPlayer = new Player();
							Long bonusChips = null;
							Long tournamentPoints = null;
							Long vipPoints = null;
							String errorMsg = null;
							player.setEmail(signUpRequest.getEmail());
							player.setGameName(signUpRequest.getGameName());
							player.setUserName(signUpRequest.getFirstName() + " " + signUpRequest.getLastName());
							player.setPassword(signUpRequest.getPassword());
							player.setPlayerClass(PlayerClass.CRYSTAL);
							player.setClassUpdateVipPoint(new ClassUpdateVipPoint(new Date()));
							player.setSource(PlayerSource.DIRECT);
							player.setLoginType(LoginType.EMAIL);
							player.setLoginSource(LoginSource.EMAIL);
							player.setStatus(PlayerStatus.ACTIVE);
							player.setReferralCode(com.actolap.wse.Utils.generate());
							PlayerProfile profile = new PlayerProfile();
							profile.setFirstName(signUpRequest.getFirstName());
							profile.setLastName(signUpRequest.getLastName());
							profile.setEligbleAge(signUpRequest.isEligbleAge());
							profile.setAvatar(Constants.defaultAvatar);
							if (signUpRequest.isTermAccept()) {
								player.getLegal().setTermDate(new Date());
								player.getLegal().setTermsAccepted(signUpRequest.isTermAccept());
							}
							try {
								Gender gender = Gender.valueOf(signUpRequest.getGender());
								profile.setGender(gender);
								player.setProfile(profile);
							} catch (IllegalArgumentException e) {
								LOG.error(e.getMessage(), e);
								signUpResponse.setEd("Gender" + signUpRequest.getGender() + " is not valid");
								isError = true;
							}
							PlayerSecurityValidate playerSecurityValidate = new PlayerSecurityValidate();
							playerSecurityValidate.setForgetMaxCount(0);
							playerSecurityValidate.setPinMaxCount(0);
							playerSecurityValidate.setForgetLinkTime(forgetLinkTime);
							playerSecurityValidate.setGeneratePinTime(generatePinTime);
							player.setPlayerSecurityValidate(playerSecurityValidate);
							if (!isError) {
								if (signUpRequest.getReferral() != null) {
									Coupon coupon = CouponDao.getLiveCouponByCode(signUpRequest.getReferral(),
											CouponType.SIGNUP);
									referralPlayer = PlayerDao.getByReferralCode(signUpRequest.getReferral());
									LOG.info("Promo Code " + signUpRequest.getReferral());
									if (coupon != null || referralPlayer != null) {
										if (!player.isSignupCoupon()) {
											if (!player.getId().equals(referralPlayer.getId())) {
												isReferVerify = true;
											} else {
												errorMsg = "Not allow to use your own referral code";
												isReferralError = true;
											}

										}

									} else {
										signUpResponse.setEd("Invalid referral code");
										return signUpResponse;
									}
								}
							}
							if (!isError) {
								if (signUpRequest.getAffiliateReferral() != null
										&& !signUpRequest.getAffiliateReferral().equals("")) {
									Affiliate affiliate = AffiliateDao
											.getByReferralCode(signUpRequest.getAffiliateReferral());
									if (affiliate != null) {
										player.setTempAffiliateCode(signUpRequest.getAffiliateReferral());
									} else {
										signUpResponse.setEd("Invalid affiliate code");
										return signUpResponse;
									}
								}
							}
							if (!isError) {
								if (signUpRequest.getMobile() != null) {
									String otp = com.actolap.wse.Utils.generateOTP();
									String otpResponse = LyveSendSms
											.setOtpForSignUp(signUpRequest.getMobile().toString(), otp);
									String[] responseParts = otpResponse.split("\\|");
									String responseCode = responseParts[0];
									if (responseCode.trim().equals("success")) {
										String mobileNumber = responseParts[1].trim();
										String transactionId = responseParts[2].trim();
										OneTimePassword oneTimePassword = new OneTimePassword();
										oneTimePassword.setOtp(otp);
										oneTimePassword.setPlayerId(player.getId());
										oneTimePassword.setEmail(signUpRequest.getEmail());
										oneTimePassword.setMobile(signUpRequest.getMobile().toString());
										oneTimePassword.setTransactionId(transactionId);
										oneTimePassword.setExpireDate(new Date());
										OneTimePasswordDao.persist(oneTimePassword);
										signUpResponse.setTransactionId(transactionId);
										signUpResponse.setMobile(mobileNumber.substring(2, 12));
										player.setMobileUnverified(signUpRequest.getMobile());
									} else {
										isMobileError = true;
									}
								}
								if (!isError && !isMobileError && !isReferralError) {
									player.setEmailVerifiedToken(UUID.randomUUID().toString() + "-" + player.getId());
									PlayerDao.persist(player);
									com.actolap.wse.Utils.sentEmail(signUpRequest.getEmail(),
											player.getEmailVerifiedToken(), Config.DOMAIN,
											signUpRequest.getFirstName() + " " + signUpRequest.getLastName(), true);
									if (isReferVerify)
										bonusChips = handleReferral(player, referralPlayer);
									signUpResponse.setEmail(signUpRequest.getEmail());
									signUpResponse.setPlayerId(player.getId());
									signUpResponse.setS(true);
									AnalyticsManager.signUp();
									LyveSendSms.sendSignUpSms(PlayerClass.CRYSTAL.toString(),signUpRequest.getMobile().toString());
									signUpResponse.setMsg("You have been signUp successfully");
								} else {
									if (isMobileError)
										signUpResponse.setMsg("This mobile number is invalid");
								}
							}

						} else {
							signUpResponse.setEd("This mobile number has been already registered");
						}
					} else {
						signUpResponse.setEd("This email has been already registered");
					}
					} else {
						signUpResponse.setEd("This game name has already been taken");
					}
				} else {
					signUpResponse.setEd(
							"Password length at least 6 characters and must contains one lowercase, one uppercase characters and one special symbols");
				}
			} else {
				signUpResponse.setEd("Required fields are coming invalid");
			}
		} catch (NumberFormatException e) {
			LOG.error(e.getMessage(), e);
			signUpResponse.setEd("Mobile number is invalid");
		} catch (DuplicateKeyException e) {
			LOG.error(e.getMessage(), e);
			signUpResponse.setEd("This email has been already registered");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			signUpResponse.setEd(e.getMessage());
		}
		return signUpResponse;
	}

	@POST
	@Path("/social/signup")
	@ApiOperation(value = "Player Social SignUp", notes = "Player Social SignUp", response = PlayerSocialSignUpResponse.class, httpMethod = "post")
	public PlayerSocialSignUpResponse SocialSignUpDoc(
			@ApiParam(required = true) PlayerSocialSignUpRequest socialSignUpRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response) {
		PlayerSocialSignUpResponse signUpResponse = new PlayerSocialSignUpResponse();
		try {
			LOG.info("entered in sign up");
			SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
			Date date = format.parse(new Date().toString());
			long forgetLinkTime = date.getTime();
			long generatePinTime = date.getTime();
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(socialSignUpRequest);
			System.out.println(json);
			LOG.info("sign up request is" + json);
			if (socialSignUpRequest != null && Utils.isNotEmptyNA(socialSignUpRequest.getFirstName())
					&& Utils.isNotEmptyNA(socialSignUpRequest.getLastName())
					&& Utils.isNotEmptyNA(socialSignUpRequest.getGameName()) && socialSignUpRequest.isEligbleAge()
					&& socialSignUpRequest.isTermAccept()) {
				if (socialSignUpRequest.getPassword().equals(socialSignUpRequest.getRePassword())) {
					LOG.info("unity request verified successfully for email"+socialSignUpRequest.getFirstName());
					String playerId = socialSignUpRequest.getId(); 
					String version = socialSignUpRequest.getTermVersion(); 
					String ip = socialSignUpRequest.getIp(); 
					PlayerProfile profile = new PlayerProfile(); 
					String token = null; 
					Player gameNameExist = PlayerDao.getByGameName(socialSignUpRequest.getGameName());
					if(gameNameExist == null) {
					Player player = PlayerDao.getById(playerId);
					if (player != null) {
						LOG.info("entered after all validation");
						boolean isError = false;
						boolean isMobileError = false;
						boolean isReferralError = false;
						boolean isReferVerify = false;
						Player referralPlayer = new Player();
						Long bonusChips = null;
						Long tournamentPoints = null;
						Long vipPoints = null;
						String errorMsg = null;
						if (socialSignUpRequest.getFirstName() != null && socialSignUpRequest.getLastName() != null) {
							PlayerDao.updateProfile(player.getId(), socialSignUpRequest.getFirstName(),
									socialSignUpRequest.getLastName());
						}
						if (socialSignUpRequest.getGameName() != null)
							player.setGameName(socialSignUpRequest.getGameName());
						if (socialSignUpRequest.isEligbleAge()) {
							profile = player.getProfile();
							profile.setEligbleAge(socialSignUpRequest.isEligbleAge());
							player.setProfile(profile);
						}
						
						if (!isError) {
							if (Utils.isNotEmptyNA(socialSignUpRequest.getReferral()) && socialSignUpRequest.getReferral() != null) {
								Coupon coupon = CouponDao.getLiveCouponByCode(socialSignUpRequest.getReferral(),
										CouponType.SIGNUP);
								referralPlayer = PlayerDao.getByReferralCode(socialSignUpRequest.getReferral());
								LOG.info("Promo Code " + socialSignUpRequest.getReferral());
								if (coupon != null || referralPlayer != null) {
									if (!player.isSignupCoupon()) {
										if (!player.getId().equals(referralPlayer.getId())) {
											isReferVerify = true;
										} else {
											errorMsg = "Not allow to use your own referral code";
											isReferralError = true;
										}
									}

								} else {
									signUpResponse.setEd("Invalid referral code");
									return signUpResponse;
								}

							}
						}
						if (!isError) {
							if (Utils.isNotEmptyNA(socialSignUpRequest.getAffiliateReferral()) &&socialSignUpRequest.getAffiliateReferral() != null) {
								Affiliate affiliate = AffiliateDao.getByReferralCode(socialSignUpRequest.getAffiliateReferral());
								if(affiliate != null) {
								AffiliatePlayer existingPlayer = AffiliatePlayerDao.getByPlayerAndAffiliateId(affiliate.getId(), player.getId());
								if (existingPlayer == null) {
									AffiliatePlayer affiliatePlayer = AffiliatePlayerDao.getRejectedPlayer(player.getId());
									if (affiliatePlayer == null || (affiliatePlayer.getStatus().equals(PlayerReferralStatus.REJECTED))) {
										AffiliatePlayer affiliatePlayerobj = new AffiliatePlayer();
										Player playerObj = PlayerDao.getById(playerId);
										if(playerObj != null) {
										affiliatePlayerobj.setUserName(playerObj.getUserName());
										affiliatePlayerobj.setAffiliateId(affiliate.getId()); 
										affiliatePlayerobj.setPlayerId(player.getId());
										affiliatePlayerobj.setRegistered(new Date());
										affiliatePlayerobj.setStatus(PlayerReferralStatus.PENDING);
										affiliatePlayerobj.setAffiliateStatus(AffiliateReferralStatus.PENDING); 
										AffiliatePlayerDao.persist(affiliatePlayerobj);
										PlayerDao.updateAffiliateReferralCode(player.getId(), socialSignUpRequest.getAffiliateReferral());
									   }
									} 
								} 
							} else { 
									signUpResponse.setEd("Invalid affiliate code");
									return signUpResponse;
								}
							} 
						 }

						if (socialSignUpRequest.isTermAccept()) {
							LOG.info("enterd in term accepted");
							PokerConfiguration pokerconfig = PokerConfigurationDao.get();
							token = UUID.randomUUID().toString();
							PlayerDao.updateTermStatus(playerId, socialSignUpRequest.isTermAccept(), version, ip,
									pokerconfig.getFreeChips(), token);
							PlayerPublicDto playerObj = new PlayerPublicDto();
							playerObj.setEmail(player.getEmail());
							if (player.getMobile() != null)
								playerObj.setMobile(player.getMobile().toString());
							playerObj.setPlayerId(playerId);
							Utils.setUserName(player, false);
							playerObj.setUserName(player.getUserName());
							PlayerSecurityValidate playerSecurityValidate = new PlayerSecurityValidate();
							playerSecurityValidate.setForgetMaxCount(0);
							playerSecurityValidate.setPinMaxCount(0);
							playerSecurityValidate.setForgetLinkTime(forgetLinkTime);
							playerSecurityValidate.setGeneratePinTime(generatePinTime);
							player.setPlayerSecurityValidate(playerSecurityValidate);
							PlayerDao.updatePlayerSecurity(player.getId() , player.getPlayerSecurityValidate());
							// response.setPlayer(playerObj);
							GlobalCachedManager.set(playerId, token + "\t" + ip, 600L);
						}
						
						if (!isError && !isMobileError && !isReferralError) {
							if (isReferVerify)
								bonusChips = handleReferral(player, referralPlayer);
							if (socialSignUpRequest.getGameName() != null)
								PlayerDao.updateGameName(player.getId(), socialSignUpRequest.getGameName());
							signUpResponse.setToken(token + "_" + player.getId());
							signUpResponse.setPlayer(player);
							if (player.getLegal() != null)
								signUpResponse.setTermAccepted(player.getLegal().isTermsAccepted());
							PlayerTransaction playerTransaction = new PlayerTransaction();
							playerTransaction.setPid(player.getId());
							playerTransaction.setType(TransactionType.security_pin);
							playerTransaction.setSecurityPinSent(true);
							PlayerTransactionDao.persist(playerTransaction);
							if (socialSignUpRequest.getPassword() != null)
								PlayerDao.savePasswordSocial(player.getId(), socialSignUpRequest.getPassword());
							handleSignUpCoupon(player.getId());

							signUpResponse.setS(true);
							signUpResponse.setMsg("You have been signUp successfully");
						} else {
							if (isMobileError)
								signUpResponse.setMsg("This mobile number is invalid");
						}
					} 
					}else {
						signUpResponse.setEd("This game name has already been taken");
					}

				} else {
					signUpResponse.setEd("Password and confirm password do not match");
				}
			} else {
				signUpResponse.setEd("Required fields are coming invalid");
			}
		} catch (NumberFormatException e) {
			LOG.error(e.getMessage(), e);
			signUpResponse.setEd("Mobile number is invalid");
		} catch (DuplicateKeyException e) {
			LOG.error(e.getMessage(), e);
			signUpResponse.setEd("This email has been already registered");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			signUpResponse.setEd(e.getMessage());
		}
		return signUpResponse;
	}

	@GET
	@Path("/forget/link")
	@ApiOperation(value = "Get Forget Password Link Or OTP", notes = "Player Forget Password link sent on his/her email or mobile", response = EmailValidationResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "email", value = "Player Email Or Mobile", dataType = "string", paramType = "query", required = true) })
	public EmailValidationResponse sendForgetPasswordLink(@ApiParam(hidden = true) Request request,
			@ApiParam(hidden = true) Response response0) {
		EmailValidationResponse response = new EmailValidationResponse();
		String emailOrMobile = request.getHeader(Urlparams.email);
		String domain = Config.DOMAIN;
		if (Utils.isNotEmpty(emailOrMobile) && Utils.isNotEmpty(domain)) {
			try {
				SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
				boolean isEmail = com.actolap.wse.Utils.isValidEmailAddress(emailOrMobile);
				Player player = null;
				boolean isMobileError = false;
				Date date = format.parse(new Date().toString());
				long forgetLinkTime = date.getTime();
				int maxCount = 0;
				boolean sendPasswordLink = false;
				boolean sendOtp = false; 
				if (isEmail) {
					player = PlayerDao.getByEmail(emailOrMobile);
					if (player != null) {
						maxCount = player.getPlayerSecurityValidate().getForgetMaxCount(); 
					} else {
						response.setEd("Looks like this email is not registered with us");
						return response;
					}
				} else {
					player = PlayerDao.getByMobile(Long.valueOf(emailOrMobile));
					if (player != null) {
						maxCount = player.getPlayerSecurityValidate().getForgetMaxCount();
					} else {
						response.setEd("Looks like this mobile number is not registered with us");
						return response;
					}
				}
				if (player != null) {
					LoginValidateListResponse loginValidateListResponse = getValidateList();
					if (isEmail) {
						if (player.getPlayerSecurityValidate().getForgetMaxCount() < loginValidateListResponse.getMaxCount()) { 
							sendPasswordLink = true; 
							maxCount++; 
						} else { 
							if ((forgetLinkTime - player.getPlayerSecurityValidate().getForgetLinkTime()) > loginValidateListResponse.getForgetTime()) {
								maxCount = 1;
								sendPasswordLink = true;
							} else {
								response.setEd("LIMIT_EXCEED");
								return response;
							}
						}
					} else {
						if (player.getPlayerSecurityValidate().getForgetMaxCount() < loginValidateListResponse
								.getMaxCount()) {
							sendOtp = true;
							maxCount++;
						} else {
							if ((forgetLinkTime - player.getPlayerSecurityValidate()
									.getForgetLinkTime()) > loginValidateListResponse.getForgetTime()) {
								maxCount = 1;
								sendOtp = true;
							} else {
								response.setEd("LIMIT_EXCEED");
								return response;
							}
						}
					}
					if (sendPasswordLink) {
						player.setForgotPasswordToken(UUID.randomUUID().toString() + "-" + player.getId());
						PlayerDao.updatePasswordToken(player.getId(), player.getForgotPasswordToken(), false);
						com.actolap.wse.Utils.sentForgotPasswordLink(emailOrMobile, player.getForgotPasswordToken(),
								domain, player.getUserName());
						if (player.getMobile() != null) {
							LyveSendSms.sendSmsForgotPassword(player.getMobile().toString(),
									player.getUserName());
						}
						PlayerDao.updateMaxCount(player.getId(), maxCount);
						PlayerDao.updateTime(player.getId(), forgetLinkTime);

					} else if (sendOtp) {
						String otp = com.actolap.wse.Utils.generateOTP();
						//String otpResponse = com.actolap.wse.Utils.setOtp(emailOrMobile, otp);
						String otpResponse = LyveSendSms.setOtp(emailOrMobile, otp);
						String[] responseParts = otpResponse.split("\\|");
						String responseCode = responseParts[0];
						if (responseCode.trim().equals("success")) {
							String mobileNumber = responseParts[1].trim();
							String transactionId = responseParts[2].trim();
							OneTimePassword oneTimePassword = new OneTimePassword();
							oneTimePassword.setOtp(otp);
							oneTimePassword.setMobile(emailOrMobile);
							oneTimePassword.setTransactionId(transactionId);
							oneTimePassword.setPlayerId(player.getId());
							oneTimePassword.setEmail(player.getEmail());
							oneTimePassword.setExpireDate(new Date());
							OneTimePasswordDao.persist(oneTimePassword);
							response.setTransactionId(transactionId);
							response.setMobile(mobileNumber.substring(2, 12));
							PlayerDao.updateMaxCount(player.getId(), maxCount);
							PlayerDao.updateTime(player.getId(), forgetLinkTime);
						} else {
							isMobileError = true;
						}
					}
					if (!isMobileError) {
						if (player.getMobile() != null)
							response.setMobile(player.getMobile().toString());
						if (isEmail)
							response.setMsg("A reset password link has been sent to your email " + player.getEmail());
						response.setPlayerId(player.getId());
						response.setS(true);
					} else {
						if (isEmail)
							response.setEd(
									"A reset password link has been sent to your email below. Kindly follow the instructions on your email");
						else
							response.setEd("A reset password OTP has been sent to your mobile number");
					}
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Required fields are coming invalid");
		}
		return response;
	}

	@GET
	@Path("/email/varification/link")
	@ApiOperation(value = "Get Email Verification Link", notes = "Email Verification Link Sent To Player Email", response = GenericResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "email", value = "Player Email", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "url", value = "Domain link to email verification link on given domain", dataType = "string", paramType = "query", required = true) })
	public GenericResponse sendEmailVarificationLink(@ApiParam(hidden = true) Request request,
			@ApiParam(hidden = true) Response response0) {
		GenericResponse response = new GenericResponse();
		String email = request.getHeader(Urlparams.email);
		String domain = request.getHeader(Urlparams.url);
		if (Utils.isNotEmpty(email) && Utils.isNotEmpty(domain)) {
			try {
				Player player = PlayerDao.getByEmail(email);
				if (player != null) {
					player.setEmailVerifiedToken(UUID.randomUUID().toString() + "-" + player.getId());
					PlayerDao.updateEmailToken(player.getId(), player.getEmailVerifiedToken());
					com.actolap.wse.Utils.sentEmail(email, player.getEmailVerifiedToken(), domain, player.getUserName(),
							false);
					response.setMsg("We have send link on your email " + player.getEmail());
					response.setS(true);
				} else {
					response.setMsg("Looks like your email did not match");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Required fields are coming invalid");
		}
		return response;
	}

	@GET
	@Path("/validate/email/token")
	@ApiOperation(value = "Validate Email Token", notes = "Validate Email Token Which Has Send To Player Email", response = EmailValidationResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "tokenId", value = "Email Token", dataType = "string", paramType = "query", required = true) })
	public EmailValidationResponse validateEmailToken(Request request, Response responseO) {
		EmailValidationResponse response = new EmailValidationResponse();
		String token = request.getHeader(Urlparams.tokenId);
		if (Utils.isNotEmptyNA(token)) {
			try {
				String playerId = getPlayerId(token);
				Player player = PlayerDao.getById(playerId);
				if(player != null && !player.isEmailVerified()) {
				if (Utils.isNotEmpty(player.getEmailVerifiedToken())
						&& player.getEmailVerifiedToken().equals(token) && !player.isEmailVerified()) {
					PlayerDao.updateEmailStatus(player.getId());
					response.setEmail(player.getEmail());
					response.setPlayerId(player.getId());
					if (player.getLegal() != null)
						response.setTermAccepted(player.getLegal().isTermsAccepted());
					response.setMsg("Your email " + player.getEmail() + " was successfully verified");
					handleSignUpCoupon(playerId);
					response.setS(true);
				} else {
					response.setEd("This link has been expired. Please request again for email verified.");
				}
				} else {
					response.setEd("Email Already verified.");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Required fields are coming invalid");
		}
		return response;
	}

	@GET
	@Path("/validate/password/token")
	@ApiOperation(value = "Validate Forget Password Token", notes = "Validate forget password token which is expired after verified", response = EmailValidationResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "tokenId", value = "Forget Password Token", dataType = "string", paramType = "query", required = true) })
	public GenericResponse validatePasswordToken(Request request, Response responseO) {
		EmailValidationResponse response = new EmailValidationResponse();
		String token = request.getHeader(Urlparams.tokenId);
		if (Utils.isNotEmpty(token)) {
			try {
				String playerId = getPlayerId(token);
				Player player = PlayerDao.getById(playerId);
				if (player != null && Utils.isNotEmpty(player.getForgotPasswordToken())
						&& player.getForgotPasswordToken().equals(token) && !player.isForgotTokenExpired()) {
					PlayerDao.updateForgotToken(player.getId());
					response.setEmail(player.getEmail());
					response.setPlayerId(player.getId());
					response.setMsg("Your forgot password token is valid");
					response.setS(true);
				} else {
					response.setEd("This link has been expired. Please request again for forgot password.");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Required fields are coming invalid");
		}
		return response;
	}

	@POST
	@Path("/reset/password")
	@ApiOperation(value = "Player Reset Password", notes = "Player Reset Password", response = GenericResponse.class, httpMethod = "post")

	public GenericResponse updatePasswordDoc(@ApiParam(required = true) ResetPasswordRequest playerRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response0) {
		GenericResponse response = new GenericResponse();
		try {
			String currentCountry = "INDIA";
			if (playerRequest != null && Utils.isNotEmpty(playerRequest.getNewPassword())
					&& ((playerRequest.isByEmail() && Utils.isNotEmpty(playerRequest.getPasswordToken()))
							|| (!playerRequest.isByEmail() && Utils.isNotEmpty(playerRequest.getId())))) {
				if (com.actolap.wse.Utils.validatePassword(playerRequest.getNewPassword())) {
					String playerId = null;
					if (playerRequest.isByEmail())
						playerId = getPlayerId(playerRequest.getPasswordToken());
					else
						playerId = playerRequest.getId();
					Player player = PlayerDao.getById(playerId);
					if (player != null
							&& ((playerRequest.isByEmail() && Utils.isNotEmpty(player.getForgotPasswordToken())
									&& player.getForgotPasswordToken().equals(playerRequest.getPasswordToken()))
									|| !playerRequest.isByEmail())) {
						PlayerDao.resetPassword(player.getId(), playerRequest.getNewPassword(), currentCountry);
						response.setMsg("Password has been successfully updated");
						com.actolap.wse.Utils.sendMailForResetPassword(player, Config.DOMAIN,
								"password has been successfully changed ");
						if (player.getMobile() != null)
							LyveSendSms.sendSmsSuccessPasswordChange(player.getMobile().toString(),
									player.getUserName());
						response.setS(true);
					} else {

						response.setEd("This link has been expired. Please request again for forgot password.");
					}

				} else {
					response.setEd(
							"Password length at least 6 characters and must contains one lowercase, one uppercase character and one special symbol");
				}
			} else {
				response.setEd("Required fields are coming invalid");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@GET
	@Path("/email/validate")
	@ApiOperation(value = "Email Verification", notes = "Email Verification", response = EmailValidationResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Player Id", dataType = "string", paramType = "query", required = true) })
	public EmailValidationResponse validateEmail(@ApiParam(hidden = true) Request request,
			@ApiParam(hidden = true) Response response0) {
		EmailValidationResponse response = new EmailValidationResponse();
		String playerId = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(playerId)) {
			try {
				Player player = PlayerDao.getById(playerId);
				if (player != null) {
					if (!player.isEmailVerified()) {
						PlayerDao.updateEmailStatus(player.getId());
						response.setEmail(player.getEmail());
						if (player.getLegal() != null)
							response.setTermAccepted(player.getLegal().isTermsAccepted());
						response.setMsg("Your email " + player.getEmail() + " was successfully verified");
						response.setS(true);
					} else {
						response.setEd("The email verification link that you clicked has expired.");
					}
				} else {

					response.setEd("Player id is invalid");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Player id should not be empty");
		}
		return response;
	}

	@GET
	@Path("/terms/accept")
	@ApiOperation(value = "Terms Accept", notes = "Terms and Conditions Acceptance", response = TermsAcceptedResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Player Id", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "agree", value = "Accept terms and conditions", dataType = "boolean", paramType = "query", required = true),
			@ApiImplicitParam(name = "version", value = "Terms version", dataType = "boolean", paramType = "query", required = true),
			@ApiImplicitParam(name = "ip", value = "Ip Address", dataType = "boolean", paramType = "query", required = true) })
	public TermsAcceptedResponse agreementAcceptence(@ApiParam(hidden = true) Request request,
			@ApiParam(hidden = true) Response response0) {
		TermsAcceptedResponse response = new TermsAcceptedResponse();
		String playerId = request.getHeader(Urlparams.id);
		boolean isAgree = Boolean.valueOf(request.getHeader(Urlparams.agree));
		String version = request.getHeader(Urlparams.version);
		String ip = request.getHeader(Urlparams.ip);
		if (Utils.isNotEmpty(playerId) && Utils.isNotEmpty(version) && Utils.isNotEmpty(ip)) {
			try {
				Player player = PlayerDao.getById(playerId);
				if (player != null) {
					PokerConfiguration pokerconfig = PokerConfigurationDao.get();
					String token = UUID.randomUUID().toString();
					if (player.getWallet().getFree() == 0) {
						PlayerDao.updateTermStatus(playerId, isAgree, version, ip, pokerconfig.getFreeChips(), token);
					}

					PlayerPublicDto playerObj = new PlayerPublicDto();
					playerObj.setEmail(player.getEmail());
					if (player.getMobile() != null)
						playerObj.setMobile(player.getMobile().toString());
					playerObj.setPlayerId(playerId);
					Utils.setUserName(player, false);
					playerObj.setUserName(player.getUserName());
					response.setPlayer(playerObj);
					GlobalCachedManager.set(playerId, token + "\t" + ip, 600L);
					response.setToken(token + "_" + playerId);
					response.setS(true);
				} else {
					response.setEd("Player id is invalid");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Required fields are coming invalid");
		}
		return response;
	}

	@GET
	@Path("/terms")
	@ApiOperation(value = "Terms And Conditions", notes = "List Of Terms And Conditions", response = TermAndConditionResponse.class, httpMethod = "get")
	public TermAndConditionResponse termAndCondition(Request request, Response responseO) {
		TermAndConditionResponse response = new TermAndConditionResponse();
		try {
			Field[] fields = TermAndCondition.class.getDeclaredFields();
			for (Field f : fields) {
				response.getTermAndConditions().add(f.get(f.getName()).toString());
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@GET
	@Path("/logout")
	@ApiOperation(value = "Logout", notes = "Player Logout", response = GenericResponse.class, httpMethod = "post")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "playerId", value = "Player Id", dataType = "string", paramType = "query", required = true), })
	public GenericResponse logout(Request request, Response responseO) {
		GenericResponse response = new GenericResponse();
		String playerId = request.getHeader(Urlparams.playerId);
		try {
			if (GlobalCachedManager.get(playerId) != null) {
				GlobalCachedManager.del(playerId);
				response.setMsg("You have successfully logged out.");
				response.setS(true);
			} else {
				response.setEd("You have already logged out.");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@GET
	@Path("/validate/token")
	@ApiOperation(value = "Validate Player Token", notes = "Validate Player Token Which Is Player Get After Login", response = TokenResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "lyve-tok", value = "Token", dataType = "string", paramType = "query", required = true) })
	public TokenResponse validateToken(Request request, Response responseO) {
		TokenResponse response = new TokenResponse();
		String token = request.getHeader(PermissionHandler.PLAYER_HEADER_TOKEN);
		String ipAddress = request.getHeader(PermissionHandler.USER_HEADER_IP);
		if (Utils.isNotEmpty(token) && Utils.isNotEmpty(ipAddress)) {
			try {
				String[] playerTokens = token.split("_");
				String userToken = (String) GlobalCachedManager.get(playerTokens[1]);
				if (userToken != null) {
					String[] tokens = userToken.split("\t");
					Player player = PlayerDao.getById(playerTokens[1]);
					if (player != null && ipAddress.equals(tokens[1]) && playerTokens[0].equals(tokens[0])) {
						response.setPlayer(new PlayerPublicDto(playerTokens[1], player.getUserName(),
								player.getMobile(), player.getEmail(), player.getProfile().getFirstName(), player.getGameName()));
						GlobalCachedManager.set(playerTokens[1], userToken, 600L);
						response.setS(true);
					} else {
						response.setEd("User logged in from another browser");
					}
				} else {
					response.setEd("Your session has expired. Please log in again");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Required field are comming invalid");
		}
		return response;
	}

	private String getPlayerId(String token) {
		if (token.lastIndexOf("-") > 0) {
			return token.substring(token.lastIndexOf("-") + 1);
		}
		return null;
	}

	@POST
	@Path("/validateGame")
	@ApiOperation(value = "Validate Game Name", notes = "Validate Game Name", response = PlayerSignUpResponse.class, httpMethod = "post")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "tokenId", value = "Email Token", dataType = "string", paramType = "query", required = true) })
	public PlayerSignUpResponse validategameName(@ApiParam(required = true) PlayerSignUpRequest signUpRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response) {

		PlayerSignUpResponse signUpResponse = new PlayerSignUpResponse();

		try {
			Player playerObj = PlayerDao.getByGameName(signUpRequest.getGameName());
			if (playerObj != null) {
				signUpResponse.setEd("This game Name exists");

			} else
				signUpResponse.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			signUpResponse.setEd(e.getMessage());
		}

		return signUpResponse;
	}

	private long handleReferral(Player player, Player referralPlayer) {
		long bonusChips = Config.referralChips;
		PlayerDao.updateReferralDetail(player.getId(), referralPlayer.getId(), Config.referralChips, false);
		PlayerDao.updateReferralDetail(referralPlayer.getId(), null, Config.referralChips, true);
		PlayerBonus playerBonus = new PlayerBonus();
		playerBonus.setPid(player.getId());
		playerBonus.setValue(bonusChips);
		playerBonus.setD(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 30);
		playerBonus.setEd(calendar.getTime());
		PlayerBonusDao.persist(playerBonus);

		PlayerTransaction playerTransection = new PlayerTransaction();
		playerTransection.setBonus(bonusChips);
		playerTransection.setPid(player.getId());
		playerTransection.setType(TransactionType.EARNED);
		PlayerTransactionDao.persist(playerTransection);

		PlayerBonus referralPlayerBonus = new PlayerBonus();
		referralPlayerBonus.setPid(referralPlayer.getId());
		referralPlayerBonus.setValue(bonusChips);
		referralPlayerBonus.setD(new Date());
		Calendar calendarObj = Calendar.getInstance();
		calendarObj.add(Calendar.DAY_OF_MONTH, 30);
		referralPlayerBonus.setEd(calendarObj.getTime());
		PlayerBonusDao.persist(referralPlayerBonus);

		PlayerTransaction refPlayerTransection = new PlayerTransaction();
		refPlayerTransection.setBonus(bonusChips);
		refPlayerTransection.setPid(referralPlayer.getId());
		refPlayerTransection.setType(TransactionType.EARNED);
		PlayerTransactionDao.persist(refPlayerTransection);

		PlayerDao.updateWallet(player.getId(), null, bonusChips, null, null, null);
		PlayerDao.updateWallet(referralPlayer.getId(), null, bonusChips, null, null, null);
		AnalyticsManager.bonusChipsIssued(player.getId(), null, bonusChips);
		AnalyticsManager.bonusChipsIssued(referralPlayer.getId(), null, bonusChips);

		return bonusChips;

	}

	@GET
	@Path("/update/configuration")
	@ApiOperation(value = "update player configuration", notes = "update all player fields", response = GenericResponse.class, httpMethod = "get")
	public GenericResponse updateConfiguration(Request request, Response responseO) {
		GenericResponse response = new GenericResponse();
		try {
			SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
			Date date = format.parse(new Date().toString());
			long forgetLinkTime = date.getTime();
			long generatePinTime = date.getTime();
			int forgetMaxCount = 0;
			int pinMaxCount = 0;
			Map<String, Object> playerMap = new HashMap<String, Object>();
			List<Player> playerList = PlayerDao.getById();
			for (Player player : playerList) {
				if (player.getPlayerSecurityValidate() == null) {
					playerMap.put("playerSecurityValidate.forgetLinkTime", forgetLinkTime);
					playerMap.put("playerSecurityValidate.generatePinTime", generatePinTime);
					playerMap.put("playerSecurityValidate.forgetMaxCount", forgetMaxCount);
					playerMap.put("playerSecurityValidate.pinMaxCount", pinMaxCount);
					PlayerDao.updateConfiguration(playerMap);
				}
			}
			response.setMsg("player configuration has been updated successfully");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	public LoginValidateListResponse getValidateList() {
		LoginValidateListResponse response = new LoginValidateListResponse();
		try {
			ApiConfiguration apiConfiguration = LoginValidateDao.getById();
			if (apiConfiguration != null) {
				response = new LoginValidateListResponse(apiConfiguration);
				response.setS(true);
			} else {
				response.setEd("Id is not valid");
			}
		} catch (Exception e) {
			response.setEd(e.getMessage());
		}
		return response;
	}

	private static void handleSignUpCoupon(String playerId) {
		Coupon coupon = CouponDao.getSignUpCoupon(CouponType.SIGNUP);
		if (coupon != null) {
			if (coupon.getMaxRedemption() > coupon.getRedemptionCount())
				if (coupon.getRedemptionType() != null) {
					PlayerTransaction playerTransaction = new PlayerTransaction();
					playerTransaction.setType(TransactionType.COUPON_REIMBURSEMENT);
					playerTransaction.setPid(playerId);
					playerTransaction.setSignUpCoupon(true);
					if (coupon.getSignUp() != null && coupon.getSignUp().getAvail() > 0) {
						if (coupon.getRedemptionType().equals(RedemptionType.TOURNAMENT_POINTS)) {
							PlayerDao.updateWallet(playerId, null, null, null, null,
									(long) coupon.getSignUp().getAvail());
							playerTransaction.setTournamentChips((long)coupon.getSignUp().getAvail());
						}else if (coupon.getRedemptionType().equals(RedemptionType.CASH)) {
							PlayerDao.updateWallet(playerId, null, null, (long) coupon.getSignUp().getAvail(), null,
									null);
							playerTransaction.setRealChips((long)coupon.getSignUp().getAvail());
						}
						else if (coupon.getRedemptionType().equals(RedemptionType.BONUS_POINTS)
								|| coupon.getRedemptionType().equals(RedemptionType.BONUS)
								|| coupon.getRedemptionType().equals(RedemptionType.BONUS_CHIPS)) {
							PlayerDao.updateWallet(playerId, null, (long) coupon.getSignUp().getAvail(), null, null,
									null);
							playerTransaction.setBonus((long)coupon.getSignUp().getAvail());
						}
						else if (coupon.getRedemptionType().equals(RedemptionType.VIP)
								|| coupon.getRedemptionType().equals(RedemptionType.VIP_POINTS)) {
							PlayerDao.updateWallet(playerId, null, null, null, (long) coupon.getSignUp().getAvail(),
									null);
							playerTransaction.setVip((long)coupon.getSignUp().getAvail());
						}
						
						PlayerTransactionDao.persist(playerTransaction);
						CouponDao.increaseRedemptionCount(coupon.getId());
					}
				}

		}
	}

}

