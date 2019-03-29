package com.actolap.lyve.fe.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.actolap.lyve.fe.api.ApiManager;
import com.actolap.lyve.fe.backend.response.GenericBackendResponse;
import com.actolap.lyve.fe.backend.response.PlayerLoginResponse;
import com.actolap.lyve.fe.backend.response.PlayerResendOtpBeResponse;
import com.actolap.lyve.fe.cache.FECache;
import com.actolap.lyve.fe.common.Constants;
import com.actolap.lyve.fe.common.FeUtils;
import com.actolap.lyve.fe.config.Config;
import com.actolap.lyve.fe.frontend.response.GenericResponse;
import com.actolap.lyve.fe.frontend.response.PlayerLoginFeResponse;
import com.actolap.lyve.fe.frontend.response.PlayerResendOtpFeResponse;
import com.actolap.lyve.fe.frontend.response.PlayerSignUpFeResponse;
import com.actolap.lyve.fe.model.Player;
import com.actolap.lyve.fe.request.PlayerLoginRequest;
import com.actolap.lyve.fe.request.ResetPasswordRequest;
import com.actolap.lyve.fe.social.facebook.FBConnection;
import com.actolap.lyve.fe.social.google.GoogleConnection;
import com.actolap.lyve.fe.utils.Tokenizer;
import com.google.gson.Gson;

@Controller
public class LoginController {

	private static Gson gson = new Gson();
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping(value = "player/login", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public PlayerLoginFeResponse login(@RequestParam(value = "url", required = false) String url, @RequestBody String data, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		PlayerLoginFeResponse feResponse = new PlayerLoginFeResponse();
		try {
			String ip = FeUtils.getClientIp(request);
			if (FeUtils.isNotEmpty(data) && FeUtils.isNotEmpty(ip)) {
				PlayerLoginRequest playerLoginRequest = Tokenizer.decrypt(data, FECache.assignAndGetToken(ip), PlayerLoginRequest.class);

				PlayerLoginResponse beResponse = ApiManager.playerLogin(new PlayerLoginRequest(playerLoginRequest.getUsername(), playerLoginRequest.getPassword(), Config.domain,
						"EMAIL",playerLoginRequest.getForcelogin()), FeUtils.createAPIMeta(null, request));
				if (beResponse != null) {
					if (beResponse.isS()) {
						Player player = new Player();
						if (beResponse.isEmailLogin()) {
							if (beResponse.isEmailVerified()) {
								if (beResponse.isTermAccepted()) {
									feResponse.setStatus(Constants.SUCCESS);
									feResponse.setSuccessMsg(beResponse.getMsg());
									FeUtils.setCookie(beResponse.getToken(), response);
									if (url != null && FeUtils.isNotEmpty(url))
										feResponse.setUrl(url);
									else
										feResponse.setUrl(request.getContextPath() + "/");
								} else {
									feResponse.setStatus(Constants.TERMNOTACCEPTED);
									player.setPlayerId(beResponse.getPlayer().getPlayerId());
									feResponse.setPlayer(player);
									feResponse.setErrorDetails(beResponse.getMsg());
								}
							} else {
								feResponse.setStatus(Constants.EMAILNOTVERIFIED);
								feResponse.setTermAccepted(beResponse.isTermAccepted());
								feResponse.setErrorDetails(beResponse.getMsg());
							}
						} else {
							if (!beResponse.isMobileUnverified()) {
								if (beResponse.isTermAccepted()) {
									feResponse.setStatus(Constants.SUCCESS);
									feResponse.setSuccessMsg(beResponse.getMsg());
									FeUtils.setCookie(beResponse.getToken(), response);
									FeUtils.sessionCreate(beResponse.getPlayer(), request);
									if (url != null && FeUtils.isNotEmpty(url))
										feResponse.setUrl(url);
									else
										feResponse.setUrl(request.getContextPath() + "/");
								} else {
									feResponse.setStatus(Constants.TERMNOTACCEPTED);
									player.setPlayerId(beResponse.getPlayer().getPlayerId());
									feResponse.setPlayer(player);
									feResponse.setTransactionId(beResponse.getTransactionId());
									feResponse.setErrorDetails(beResponse.getMsg());
								}
							} else {
								feResponse.setStatus(Constants.MOBILENOTVERIFIED);
								feResponse.setTermAccepted(beResponse.isTermAccepted());
								player.setPlayerId(beResponse.getPlayer().getPlayerId());
								feResponse.setPlayer(player);
								feResponse.setTransactionId(beResponse.getTransactionId());
								feResponse.setErrorDetails(beResponse.getMsg());
							}
						}
					} else {
						feResponse.setStatus(Constants.FAILED);
						feResponse.setErrorDetails(beResponse.getEd());
					}
				} else {
					feResponse.setStatus(Constants.NOT_RESPONEDING);
					feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
				}
			} else {
				feResponse.setStatus(Constants.FAILED);
				feResponse.setErrorDetails(Constants.IP_NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			feResponse.setStatus(Constants.FAILED);
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		return feResponse;
	}

	@RequestMapping(value = "player/reset", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public GenericResponse resetPassword(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws IOException {
		GenericResponse feResponse = new PlayerSignUpFeResponse();
		try {
			if (FeUtils.isNotEmpty(data)) {
				ResetPasswordRequest resetPassword = gson.fromJson(data, ResetPasswordRequest.class);
				GenericBackendResponse beResponse = ApiManager.playerResetPassword(resetPassword, FeUtils.createAPIMeta(null, request));
				if (beResponse != null) {
					if (beResponse.isS()) {
						feResponse.setStatus(Constants.SUCCESS);
						feResponse.setSuccessMsg(beResponse.getMsg());
						feResponse.setUrl(request.getContextPath() + "/");
					} else {
						feResponse.setStatus(Constants.FAILED);
						feResponse.setErrorDetails(beResponse.getEd());
					}
				} else {
					feResponse.setStatus(Constants.NOT_RESPONEDING);
					feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
				}
			} else {
				feResponse.setStatus(Constants.FAILED);
				feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			feResponse.setStatus(Constants.FAILED);
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		return feResponse;
	}

	@RequestMapping(value = "player/forget", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public PlayerResendOtpFeResponse sendVarification(@RequestParam String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		PlayerResendOtpFeResponse feResponse = new PlayerResendOtpFeResponse();
		try {
			if (FeUtils.isNotEmpty(email)) {
				PlayerResendOtpBeResponse beResponse = ApiManager.playerForgetPassword(Config.domain, email, FeUtils.createAPIMeta(null, request));
				if (beResponse != null) {
					if (beResponse.isS()) {
						if (beResponse.getMobile() != null) {
							feResponse.setMobile(beResponse.getMobile());
							feResponse.setTransactionId(beResponse.getTransactionId());
							feResponse.setPlayerId(beResponse.getPlayerId());
							request.getSession().setAttribute("playerId", beResponse.getPlayerId());
						}
						feResponse.setStatus(Constants.SUCCESS);
						feResponse.setSuccessMsg(beResponse.getMsg());
						feResponse.setUrl(request.getContextPath() + "/");
						request.getSession().removeAttribute("expired");
					} else {
						feResponse.setStatus(Constants.FAILED);
						feResponse.setErrorDetails(beResponse.getEd());
						request.getSession().removeAttribute("expired");
					}
				} else {
					feResponse.setStatus(Constants.NOT_RESPONEDING);
					feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
					request.getSession().removeAttribute("expired");
				}
			} else {
				feResponse.setStatus(Constants.FAILED);
				feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
				request.getSession().removeAttribute("expired");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			feResponse.setStatus(Constants.FAILED);
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
			request.getSession().removeAttribute("expired");
		}
		return feResponse;
	}

	@RequestMapping(value = "player/resend/mail", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public GenericResponse resendVarificationMail(@RequestParam String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		GenericResponse feResponse = new GenericResponse();
		try {
			if (FeUtils.isNotEmpty(email)) {
				GenericBackendResponse beResponse = ApiManager.playerResendMail(Config.domain, email, FeUtils.createAPIMeta(null, request));
				if (beResponse != null) {
					if (beResponse.isS()) {
						feResponse.setStatus(Constants.SUCCESS);
						feResponse.setSuccessMsg(beResponse.getMsg());
					} else {
						feResponse.setStatus(Constants.FAILED);
						feResponse.setErrorDetails(beResponse.getEd());
					}
				} else {
					feResponse.setStatus(Constants.NOT_RESPONEDING);
					feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
				}
			} else {
				feResponse.setStatus(Constants.FAILED);
				feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			feResponse.setStatus(Constants.FAILED);
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		return feResponse;
	}

	@RequestMapping(value = "player/login/token", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public GenericResponse loginToken(HttpServletRequest request) {
		GenericResponse feResponse = new GenericResponse();
		try {
			String ip = FeUtils.getClientIp(request);
			if (ip != null) {
				feResponse.setStatus(Constants.SUCCESS);
				feResponse.setSuccessMsg("{\"token\": \"" + FECache.assignAndGetToken(ip) + "\",\"salt\":\"" + Tokenizer.SALT + "\"}");
			} else {
				feResponse.setStatus(Constants.FAILED);
				feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			feResponse.setStatus(Constants.FAILED);
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		return feResponse;
	}

	@RequestMapping(value = "player/social/login", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public GenericResponse createSocialUrl(@RequestParam(value = "type") String type, @RequestParam(value = "firstLoginParam") String firstLoginParam, HttpServletRequest request,
			HttpServletResponse response, @RequestParam(value = "forcelogin") String forcelogin) {
		GenericResponse feResponse = new GenericResponse();
		try {
			feResponse.setStatus(Constants.SUCCESS);
			if (type.equals(Config.GOOGLE) && "normal".equalsIgnoreCase(forcelogin))
				feResponse.setUrl(new GoogleConnection().getGoogleAuthUrlNormal(firstLoginParam));
			else if (type.equals(Config.GOOGLE))
				feResponse.setUrl(new GoogleConnection().getGoogleAuthUrl(firstLoginParam));
			else if (type.equals(Config.FACEBOOK))
				feResponse.setUrl(new FBConnection().getFBAuthUrl(firstLoginParam));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			feResponse.setStatus(Constants.FAILED);
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		return feResponse;
	}
}

