package com.actolap.lyve.fe.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;
import com.actolap.lyve.fe.api.ApiManager;
import com.actolap.lyve.fe.backend.response.GenericBackendResponse;
import com.actolap.lyve.fe.backend.response.ObjectResponse;
import com.actolap.lyve.fe.backend.response.TableListResponse;
import com.actolap.lyve.fe.cache.FECache;
import com.actolap.lyve.fe.common.Constants;
import com.actolap.lyve.fe.common.FeUtils;
import com.actolap.lyve.fe.config.Config; 
import com.actolap.lyve.fe.frontend.response.GenericResponse;
import com.actolap.lyve.fe.frontend.response.PlayerDashboardDataResponse;
import com.actolap.lyve.fe.interceptor.SessionWrapper;
import com.actolap.lyve.fe.model.SocketResponse;

@Controller
public class LobbyController extends CommonController {

	// private static Gson gson = new Gson();
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(LobbyController.class);
	
	@RequestMapping(value = "lobby/list", method = RequestMethod.GET)
	@ResponseBody
	public ObjectResponse lobbyFreerollList(@RequestParam(value = "type", required = true) String type, @RequestParam(value = "mode", required = false) String mode,
			@RequestParam(value = "tableSize", required = true) String tableSize, @RequestParam(value = "sb", required = false) String sb,
			@RequestParam(value = "bb", required = true) String bb, @RequestParam(value = "buyInMin", required = false) String buyInMin,
			@RequestParam(value = "buyInMax", required = true) String buyInMax, HttpServletRequest request) throws IOException {
		ObjectResponse response = new ObjectResponse();
		try {
			if (!("HOLDEM".equals(mode) || "OMAHA".equals(mode)))
				mode = ""; 
			TableListResponse beResponse = null; 
			
			if ("freeroll".equals(type)) 
				beResponse = ApiManager.lobbyFreerollList(mode, tableSize, sb, bb, buyInMin, buyInMax, FeUtils.createAPIMeta(null, request));
			else if ("realplay".equals(type))
				beResponse = ApiManager.lobbyrealplayList(mode, tableSize, sb, bb, buyInMin, buyInMax, FeUtils.createAPIMeta(null, request));
			else
				throw new Exception("Url issue");

			response.setResponse(beResponse);
			if (beResponse != null) {
				if (beResponse.isS()) {
					response.setStatus(Constants.SUCCESS);
					response.setSuccessMsg(beResponse.getMsg());
					response.setResponse(beResponse.getTableList());
				} else {
					response.setStatus(Constants.FAILED);
					response.setErrorDetails(beResponse.getEd());
				}
			} else {
				response.setStatus(Constants.NOT_RESPONEDING);
				response.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.setStatus(Constants.FAILED);
			response.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		return response;
	}

	@RequestMapping(value = "lobby/logout", method = RequestMethod.POST)
	@ResponseBody
	public GenericResponse lobbyLogout(SessionStatus status, @ModelAttribute("session") SessionWrapper session, WebRequest request, HttpServletResponse response, HttpServletRequest req) {
		try {
			if (session != null) {
				GenericBackendResponse beResponse = ApiManager.logout(FeUtils.createAPIMeta(session, req),session.getPlayer().getPlayerId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		
		
		GenericResponse feResponse = new GenericResponse();
		status.setComplete();
		try {
			request.removeAttribute("session", WebRequest.SCOPE_SESSION);
			FeUtils.removeCookie(request.getContextPath(), response, false);
			feResponse.setStatus(Constants.SUCCESS);
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			feResponse.setStatus(Constants.FAILED);
		} 
		return feResponse;
	} 
      
	@RequestMapping(value = "player/connection", method = RequestMethod.GET)
	@ResponseBody
	public ObjectResponse getReportConfig(WebRequest webRequest, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("session") SessionWrapper session) {
		ObjectResponse feResponse = new ObjectResponse();
		try {
			// if (firstCall)
			// request.getSession().setAttribute("firstLogin", false);
			SocketResponse socketResponse = new SocketResponse();
			if (session != null) {
				PlayerDashboardDataResponse beResponse = FECache.getDashboardData(FeUtils.createAPIMeta(session, request));
				if (beResponse != null) {
					if (beResponse.isS()) {
						if (!beResponse.isModified()) {
							if (beResponse.getTournamentImage() != null)
								beResponse.setTournamentImage("https://" + Config.AWS_BUCKET + ".s3.amazonaws.com/" + beResponse.getTournamentImage());
							if (!beResponse.getAvatar().startsWith("http"))
								beResponse.setAvatar(request.getContextPath() + Config.IMAGE_PATH + beResponse.getAvatar());
							beResponse.setModified(true);
						} 
						socketResponse.setDashboardResponse(beResponse);
					}
				}
				socketResponse.setPlayerId(session.getPlayer().getPlayerId());
				socketResponse.setToken(session.getPlayerToken());
				socketResponse.setSocketUrl(Config.socketUrl);
				socketResponse.setGameName(session.getPlayer().getGameName());
				String userName = session.getPlayer().getUserName();
				if (userName == null) {
					userName = FeUtils.getEmailAsUserName(session.getPlayer().getEmail());
				} else if (userName.contains("@"))
					userName = FeUtils.getEmailAsUserName(session.getPlayer().getUserName());
				socketResponse.setUserName(userName);
				feResponse.setStatus(Constants.SUCCESS);
				feResponse.setSuccessMsg("Contected");
			} else {
				// boolean firstLogin = false;
				// try {
				// firstLogin = (boolean)
				// request.getSession().getAttribute("firstLogin");
				// // FeUtils.removeCookie(webRequest, response);
				// } catch (Exception e) {
				// firstLogin = false;
				// }
				// socketResponse.setFirstLogin(firstLogin);
				GenericBackendResponse Objesponse = ApiManager.checkValidIP(FeUtils.createAPIMeta(null, request));
				if(Objesponse.getEd() != null && Objesponse.getEd().equals(Constants.RESTRICTED_IP)) {
					feResponse.setStatus(Constants.RESTRICTED_IP);
					if(Objesponse.getStateName() != null)
						feResponse.setStateName(Objesponse.getStateName());
				} else {
					feResponse.setStatus(Constants.SESSION_NOT_FOUND);
				}
				
			}
			feResponse.setResponse(socketResponse);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			feResponse.setStatus(Constants.FAILED);
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		return feResponse;
	}
	
	@RequestMapping(value = "instaPlay/send/sms", method = RequestMethod.GET)
	@ResponseBody 
	public GenericResponse getVote(HttpServletResponse response, HttpServletRequest request, @ModelAttribute("session") SessionWrapper session) {    
		GenericResponse feResponse = new GenericResponse(); 
		try {
			GenericBackendResponse beResponse = ApiManager.sendSmsToUser(FeUtils.createAPIMeta(session, request));
			if (beResponse != null) { 
				if (beResponse.isS()) { 
					feResponse.setSuccessMsg(beResponse.getMsg());
					feResponse.setStatus(Constants.SUCCESS);
			   	} else { 
					feResponse.setErrorDetails(beResponse.getEd());
					feResponse.setStatus(Constants.FAILED);
			   	} 
		    } else { 
		    	feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
		    	feResponse.setStatus(Constants.NOT_RESPONEDING);
		  }
	  } catch (Exception e) { 
		  logger.info(e.getMessage(), e);
		  feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		  feResponse.setStatus(Constants.FAILED);
	   } 
	  return feResponse;
   }
 }

 

