package com.actolap.lyve.fe.controller;

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
import com.actolap.lyve.fe.api.ApiManager;
import com.actolap.lyve.fe.backend.response.GenericBackendResponse;
import com.actolap.lyve.fe.backend.response.ObjectResponse;
import com.actolap.lyve.fe.backend.response.VoteListResponse;
import com.actolap.lyve.fe.common.Constants;
import com.actolap.lyve.fe.common.FeUtils;
import com.actolap.lyve.fe.frontend.response.GenericResponse;
import com.actolap.lyve.fe.interceptor.SessionWrapper; 
import com.google.gson.Gson; 

@Controller
public class VoteController extends CommonController { 
	
	Gson gson = new Gson();
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(VoteController.class); 
	
	@RequestMapping(value = "get/vote/question", method = RequestMethod.GET) 
	@ResponseBody 
	public ObjectResponse voteQuestionGate(HttpServletResponse response, HttpServletRequest request, @ModelAttribute("session") SessionWrapper session) {
		ObjectResponse feResponse = new ObjectResponse(); 
		try { 
				 VoteListResponse beResponse = ApiManager.getVoteQuestion(FeUtils.createAPIMeta(session, request)); 
					if (beResponse != null) { 
						if (beResponse.isS()) { 
							feResponse.setResponse(beResponse); 
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
	
	@RequestMapping(value = "set/vote", method = RequestMethod.GET) 
	@ResponseBody 
	public GenericResponse getVote(@RequestParam("id") String id, @RequestParam("voteAns") String voteAns, @RequestParam("totalVotes") String totalVotes, HttpServletResponse response, HttpServletRequest request, @ModelAttribute("session") SessionWrapper session) {    
		GenericResponse objResponse = new GenericResponse(); 
		try { 
			  GenericBackendResponse beResponse = ApiManager.setVote(id, voteAns, totalVotes, FeUtils.createAPIMeta(session, request));
				if (beResponse != null) { 
					if (beResponse.isS()) { 
						objResponse.setSuccessMsg(beResponse.getMsg()); 
						objResponse.setStatus(Constants.SUCCESS); 
					} else { 
						objResponse.setErrorDetails(beResponse.getEd()); 
						objResponse.setStatus(Constants.FAILED); 
					} 
				} else 
					objResponse.setStatus(Constants.FAILED); 
			} catch (Exception e) { 
				logger.error(e.getMessage(), e); 
			} 
			return objResponse; 
	}
	
	@RequestMapping(value = "get/question/description", method = RequestMethod.GET) 
	@ResponseBody 
	public ObjectResponse getHistoryQuestion(@RequestParam("questionId") String questionId, HttpServletResponse response, HttpServletRequest request, SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		ObjectResponse feResponse = new ObjectResponse(); 
		try { 
			 if (session != null) { 
				 VoteListResponse beResponse = ApiManager.getQuestionHistory(questionId, FeUtils.createAPIMeta(session, request)); 
					if (beResponse != null) { 
						if (beResponse.isS()) { 
							feResponse.setResponse(beResponse); 
							//feResponse.setSuccessMsg(beResponse.getMsg()); 
							feResponse.setStatus(Constants.SUCCESS); 
						} else { 
							feResponse.setErrorDetails(beResponse.getEd()); 
							feResponse.setStatus(Constants.FAILED); 
						} 
					} else { 
						feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING); 
						feResponse.setStatus(Constants.NOT_RESPONEDING); 
					} 
				} else 
					response.sendRedirect(request.getContextPath() + "/"); 
			} catch (Exception e) { 
				logger.info(e.getMessage(), e); 
				feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG); 
				feResponse.setStatus(Constants.FAILED); 
			} 
			return feResponse; 
   } 
}


