package com.actolap.lyve.fe.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actolap.lyve.fe.api.ApiManager;
import com.actolap.lyve.fe.api.ApiMeta;
import com.actolap.lyve.fe.backend.response.ObjectResponse;
import com.actolap.lyve.fe.backend.response.PlayerLoginResponse;
import com.actolap.lyve.fe.cache.FECache;
import com.actolap.lyve.fe.config.Config;
import com.actolap.lyve.fe.frontend.response.PlayerLoginFeResponse;
import com.actolap.lyve.fe.interceptor.SessionWrapper;
import com.actolap.lyve.fe.model.Player;
import com.actolap.lyve.fe.model.RestrictedIp;
import com.actolap.lyve.fe.request.PlayerLoginRequest;

public class FeUtils {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FeUtils.class);

	public static boolean isNotEmpty(String input) {
		boolean flag = false;
		if (input != null && !input.trim().equals("")) {
			flag = true;
		}
		return flag;
	}

	public static String createEmailSecret(String str) {
		String email = "";
		int atIndex = str.indexOf('@');
		email = str.substring(0, 2);
		String temp = "";
		for (int i = 2; i < atIndex; i++)
			temp = temp + "x";
		email = email + temp + "@";
		temp = "";
		int dotIndex = str.indexOf(".", atIndex);
		for (int i = atIndex + 1; i < dotIndex; i++)
			temp = temp + "x";
		email = email + temp + str.substring(dotIndex);
		return email;
	}

	public static String createMobileSecret(String str) {
		String mobile = "";
		mobile = str.substring(0, 4);
		String temp = "";
		for (int i = 5; i < str.length(); i++)
			temp = temp + "x";
		mobile = mobile + temp;
		return mobile;
	}

	public static void readProperties(@SuppressWarnings("rawtypes") Class classLoad, String path, Logger logger)
			throws IOException {
		InputStream inputStream = classLoad.getClassLoader().getResourceAsStream(path);
		Properties p = new Properties();
		p.load(inputStream);
		Config.dev = Boolean.parseBoolean(p.getProperty("dev", "true"));
		Config.domain = p.getProperty("domain", Config.domain);
		ApiManager.apiBase = p.getProperty("apiBase", ApiManager.apiBase);
		Config.AWS_KEY = p.getProperty("aws_key", Config.AWS_KEY);
		Config.AWS_SECRET = p.getProperty("aws_secret", Config.AWS_SECRET);
		Config.AWS_BUCKET = p.getProperty("aws_bucket", Config.AWS_BUCKET);
		Config.FB_APP_ID = p.getProperty("fb_app_id", Config.FB_APP_ID);
		Config.FB_APP_SECRET = p.getProperty("fb_app_secret", Config.FB_APP_SECRET);
		Config.FB_API = p.getProperty("fb_api", Config.FB_API);
		Config.GOOGLE_APP_ID = p.getProperty("google_app_id", Config.GOOGLE_APP_ID);
		Config.GOOGLE_APP_SECRET = p.getProperty("google_app_secret", Config.GOOGLE_APP_SECRET);
		Config.GOOGLE_API = p.getProperty("google_api", Config.GOOGLE_API);
		Config.socketUrl = p.getProperty("socketurl", Config.socketUrl);
		Config.PAYMENT_TYPE = p.getProperty("payment_type", Config.PAYMENT_TYPE);
		Config.PROTOCOL = p.getProperty("protocol", Config.PROTOCOL);
		Config.GOOGLE_CAPTCH_SITE_KEY = p.getProperty("google_captcha_site_key", Config.GOOGLE_CAPTCH_SITE_KEY);
		Config.GOOGLE_CAPTCHA_API_URL = p.getProperty("google_captcha_api", Config.GOOGLE_CAPTCHA_API_URL);
		Config.GOOGLE_CAPTCHA_SECRET = p.getProperty("google_captcha_secret", Config.GOOGLE_CAPTCHA_SECRET);

	}

	public static void readPropertiesGit(@SuppressWarnings("rawtypes") Class classLoad, String path, Logger logger)
			throws IOException {
		InputStream inputStream = classLoad.getClassLoader().getResourceAsStream(path);
		Properties p = new Properties();
		p.load(inputStream);
		Config.version = p.getProperty("git.commit.id.abbrev", null);
	}
	
	public static void readPropertiesCssAndJSVersion() throws IOException {
	
		ObjectResponse objResponse = new ObjectResponse();
		try {
			String cssAndJsVersion = FECache.getCssAndJSVersion();
					if (cssAndJsVersion != null && !cssAndJsVersion.isEmpty()) {
						Config.cssAndJsVersion = cssAndJsVersion;
					} else {
						objResponse.setStatus(Constants.SOME_THING_WENT_WRONG);
					}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			objResponse.setStatus(Constants.FAILED);
			objResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		
	}
	
	public static void readRestrictedIP() {
		
		ObjectResponse objResponse = new ObjectResponse();
		try {
			List<RestrictedIp> beResponse = FECache.getrestrictedIps();
			if (beResponse != null && !beResponse.isEmpty()) {
				objResponse.setResponse(beResponse);
				objResponse.setStatus(Constants.SUCCESS);
			}else {
						objResponse.setStatus(Constants.SOME_THING_WENT_WRONG);
					}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			objResponse.setStatus(Constants.FAILED);
			objResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		
	}
	//Config.version = p.getProperty("git.commit.id.abbrev", null);

	
	public static HttpServletRequest sessionCreate(Player player, HttpServletRequest request)
			throws InterruptedException, ExecutionException, IOException {
		SessionWrapper session = new SessionWrapper();
		if (player != null)
			session.setPlayer(player);
		request.setAttribute("session", session);
		return request;
	}

	public static SessionWrapper buildSession(HttpServletRequest request, HttpServletResponse response) {
		String ip = getIP(request);
		SessionWrapper session = null;
		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			String token = null;
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(Constants.LYVE_TOK)) {
					token = cookie.getValue();
					break;
				}
			}
			if (token != null) {
				Player player = FECache.getPlayer(token, ip);
				if (player != null) {
					session = new SessionWrapper();
					session.setPlayerToken(token);
					session.setPlayer(player);
				} else {
					try {
						FeUtils.removeCookie(request.getContextPath(), response, false);
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			}
			if (session != null)
				request.setAttribute("session", session);
			else
				request.removeAttribute("session");
		}
		return session;
	}

	public static String getIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null && Config.dev) {
			ip = "106.205.46.226";
		} else {
			String[] ipArray = ip.split(",");
			ip = ipArray[0];
		}
		logger.info(ip);
		return ip;
	}

	public static void setCookie(String token, HttpServletResponse response) {
		Cookie cookie = new Cookie(Constants.LYVE_TOK, token);
		cookie.setMaxAge(60 * 60 * 24);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static void setFirstLoginCookie(String name, String token, HttpServletResponse response) {
		Cookie cookie = new Cookie(name, token);
		cookie.setMaxAge(60 * 60);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static void removeCookie(String contextPath, HttpServletResponse response, boolean redirect)
			throws IOException {
		Cookie cookie = new Cookie(Constants.LYVE_TOK, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		if (redirect)
			response.sendRedirect(contextPath + "/");
	}

	public static void removeCookie(String name, String contextPath, HttpServletResponse response) throws IOException {
		Cookie cookie = new Cookie(name, "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static void setSocialData(Map<String, String> profile, JSONObject json, String type) throws JSONException {
		profile.put("id", json.getString("id"));
		if (type.equals("facebook")) {
			profile.put("first_name", json.getString("first_name"));
			profile.put("last_name", json.getString("last_name"));
		} else if (type.equals("google")) {
			if(json.has("given_name"))
				profile.put("first_name", json.getString("given_name"));
			if(json.has("family_name"))
				profile.put("last_name", json.getString("family_name"));
		}
		if (json.has("email"))
			profile.put("email", json.getString("email"));
		if (json.has("gender")) {
			String gender = null;
			if (json.getString("gender").equals("male"))
				gender = "MALE";
			else if (json.getString("gender").equals("female"))
				gender = "FEMALE";
			profile.put("gender", gender);
		} else
			profile.put("gender", "UNKNOWN");
		if (json.has("picture")) {
			if (type.equals("facebook"))
				profile.put("avatar", json.getJSONObject("picture").getJSONObject("data").getString("url"));
			else
				profile.put("avatar", json.getString("picture"));
		}
	}

	public static String getClientIp(HttpServletRequest request) {
		String remoteAddr = null;
		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}
		return remoteAddr;
	}

	public static PlayerLoginFeResponse playerSocialSignUp(HttpServletRequest request, HttpServletResponse response,
			PlayerLoginRequest playerSignUpRequest)
			throws JSONException, InterruptedException, ExecutionException, IOException {
		PlayerLoginFeResponse feResponse = new PlayerLoginFeResponse();
		String feError;
		String redirectLocation = request.getContextPath() + "/social";
		String redirectLocationGoogle = request.getContextPath() + "/loginErrorBySocialSignup/google";
		String redirectLocationFacebook = request.getContextPath() + "/loginErrorBySocialSignup/signup";
		try {
			PlayerLoginResponse beResponse = ApiManager.playerLogin(playerSignUpRequest,
					FeUtils.createAPIMeta(null, request));
			if (beResponse != null) {
				if (beResponse.isS()) {
					feError = beResponse.getMsg();
					if (beResponse.isTermAccepted()) {
						FeUtils.setCookie(beResponse.getToken(), response);
						feResponse.setStatus(Constants.SUCCESS);
						feResponse.setSuccessMsg(beResponse.getMsg());
						logger.info(redirectLocation);
						response.sendRedirect(request.getContextPath() + "/social");
					} else {
						String state = "";
						if (request.getParameter("state") != null) {
							String[] explodeState = request.getParameter("state").split("--");
							if (explodeState[0] != null)
								state = explodeState[0];
						}
						FeUtils.setFirstLoginCookie(state, beResponse.getPlayer().getPlayerId(),
								response);
						logger.info(redirectLocation);
						response.sendRedirect(redirectLocation);
					}
				} else {
					// New Add
					if ("GOOGLE".equalsIgnoreCase(playerSignUpRequest.getLoginType()) 
							&& "UserAlreadyLoggedIn".equalsIgnoreCase(beResponse.getEd())) {
						feError = null;
						feResponse.setErrorDetails(beResponse.getEd());
						//response.sendRedirect(redirectLocationGoogle);
					} else {
						feError = beResponse.getEd();
						response.sendRedirect(redirectLocation);
					}
				}
				request.getSession().setAttribute("feError", feError);
			} else {
				feError = Constants.SERVER_IS_NOT_RESPONDING;
				response.sendRedirect(redirectLocation);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.sendRedirect(redirectLocation);
		}
		return feResponse;
	}

	public static ApiMeta createAPIMeta(SessionWrapper session, HttpServletRequest request) {
		String ip = getIP(request);
		// String ip = FeUtils.getClientIp(request);
		String playerToken = null;
		String playerId = null;
		if (ip == null && Config.dev) {
			ip = "106.205.46.226";
		}
		logger.info(ip);
		if (session != null) {
			playerId = session.getPlayer().getPlayerId();
			playerToken = session.getPlayerToken();
		}
		return new ApiMeta(playerToken, playerId, ip);
	}

	public static String getEmailAsUserName(String email) {
		String userName = email.substring(0, email.indexOf("@"));
		return userName;
	}
	
	public static String getEncryptedBySha256(String str) {
		String encryptedstr = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
	        byte[] hashInBytes = md.digest(str.getBytes(StandardCharsets.UTF_8));

			// bytes to hex
	        StringBuilder sb = new StringBuilder();
	        for (byte b : hashInBytes) {
	            sb.append(String.format("%02x", b));
	        }
	        encryptedstr = sb.toString();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return encryptedstr;
	}
}
