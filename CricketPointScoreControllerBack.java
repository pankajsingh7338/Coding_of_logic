package com.actolap.wsegame.angularController;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import com.actolao.wsegmaes.api.ApiManager;
import com.actolap.wsegame.common.Constants;
import com.actolap.wsegame.common.FeUtils;
import com.actolap.wsegame.interceptor.SessionWrapper;
import com.actolap.wsegame.model.CricketCategory;
import com.actolap.wsegame.model.CricketCategoryListModel;
import com.actolap.wsegame.model.CricketPointScore;
import com.actolap.wsegame.model.CricketPointScoreListModel;
import com.actolap.wsegames.request.UpdateRequest;
import com.actolap.wsegames.response.CricketCategoryListResponse;
import com.actolap.wsegames.response.CricketPointScoreConfigResponse;
import com.actolap.wsegames.response.CricketPointScoreListResponse;
import com.actolap.wsegames.response.GenericBackendResponse;
import com.actolap.wsegames.response.GenericResponse;
import com.actolap.wsegames.response.ObjectListResponse;
import com.actolap.wsegames.response.ObjectResponse;
import com.google.gson.Gson;

@SessionAttributes({ "session" })
@Controller
public class CricketPointScoringController {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CricketContestController.class);
	Gson gson = new Gson();
	
	
	@RequestMapping(value = "ajx/cricket/PointScore/config", method = RequestMethod.GET)
	@ResponseBody
	public ObjectResponse getCricketPointConfig(HttpServletResponse response, HttpServletRequest request, SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		ObjectResponse feResponse = new ObjectResponse();
		try { 
			CricketPointScoreConfigResponse beResponse = ApiManager.getCricketScorePointConfigResponse(FeUtils.createAPIMeta(session, request));
			if (beResponse != null) {
				if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
					if (beResponse.isS()) {
						feResponse.setResponse(beResponse);
						feResponse.setStatus(Constants.SUCCESS); 
						feResponse.setSuccessMsg(beResponse.getMsg());
					} else { 
						feResponse.setStatus(Constants.FAILED); 
						feResponse.setErrorDetails(beResponse.getEd());
					} 
				}
			} else {
				feResponse.setStatus(Constants.NOT_RESPONEDING);
				feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
			}
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			feResponse.setStatus(Constants.FAILED);
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		return feResponse;
	}

	@RequestMapping(value = "ajx/cricket/category/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public GenericResponse createCricketCategory(@RequestBody String data, HttpServletResponse response,
			HttpServletRequest request, SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		GenericResponse feResponse = new GenericResponse(); 
		if (FeUtils.isNotEmpty(data)) { 
			try { 
				CricketCategory cricketCategory = gson.fromJson(data, CricketCategory.class);
				GenericBackendResponse beResponse = ApiManager.cricketCategoryCreate(cricketCategory,
						FeUtils.createAPIMeta(session, request)); 
				if (beResponse != null) { 
					if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
						if (beResponse.isS()) {
							feResponse.setD(beResponse.getD());
							feResponse.setStatus(Constants.SUCCESS);
							feResponse.setSuccessMsg(beResponse.getMsg());
						} else {
							feResponse.setStatus(Constants.FAILED);
							feResponse.setErrorDetails(beResponse.getEd()); 
						} 
					} 
				} else { 
					feResponse.setStatus(Constants.NOT_RESPONEDING); 
					feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING); 
				} 
			} catch (Exception e) { 
				logger.error(e.getMessage(), e); 
				feResponse.setStatus(Constants.FAILED); 
				feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG); 
			} 
		} else { 
			feResponse.setStatus(Constants.FAILED); 
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG); 
		} 
		return feResponse; 
	} 
	  
	@RequestMapping(value = "ajx/cricket/category/get/list", method = RequestMethod.GET)
	@ResponseBody
	public ObjectListResponse getCricketCategoryList(HttpServletResponse response, HttpServletRequest request,
			SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		ObjectListResponse feResponse = new ObjectListResponse();
		try {
			CricketCategoryListResponse beResponse = ApiManager
					.getCricketCategoryList(FeUtils.createAPIMeta(session, request));
			if (beResponse != null) {
				if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
					if (beResponse.isS()) {
						for (CricketCategoryListModel cricketCategory : beResponse.getCricketCategoryDto())
							feResponse.getResponse().add(cricketCategory);
						feResponse.setSuccessMsg(beResponse.getMsg());
						feResponse.setStatus(Constants.SUCCESS);
					} else {
						feResponse.setErrorDetails(beResponse.getEd());
						feResponse.setStatus(Constants.FAILED);
					}
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
	
	@RequestMapping(value = "ajx/category/get/details", method = RequestMethod.GET)
	@ResponseBody 
	public ObjectResponse getCategoryDetailsById(@RequestParam("id") String id, HttpServletResponse response, HttpServletRequest request,
			SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		ObjectResponse feResponse = new ObjectResponse();
		try { 
			CricketCategory beResponse = ApiManager.getCategoryDetailsById(id , FeUtils.createAPIMeta(session, request));
				if (beResponse != null) {
					if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
						if (beResponse.isS()) {
							feResponse.setResponse(beResponse);
							feResponse.setStatus(Constants.SUCCESS);
						} else { 
							feResponse.setErrorDetails(beResponse.getEd());
							feResponse.setStatus(Constants.FAILED);
						}
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
		
	@RequestMapping(value = "ajx/cricket/category/delete", method = RequestMethod.GET)
	@ResponseBody
	public GenericResponse deleteCricketCategory(@RequestParam("id") String id, HttpServletResponse response,
			HttpServletRequest request, SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		GenericResponse feResponse = new GenericResponse();
		try {
			GenericBackendResponse beResponse = ApiManager.deleteCricketCategoryById(id,
					FeUtils.createAPIMeta(session, request));
			if (beResponse != null) {
				if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
					if (beResponse.isS()) {
						feResponse.setSuccessMsg(beResponse.getMsg());
						feResponse.setStatus(Constants.SUCCESS);
					} else {
						feResponse.setErrorDetails(beResponse.getEd());
						feResponse.setStatus(Constants.FAILED);
					}
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

	@RequestMapping(value = "ajx/cricket/pointScoring/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public GenericResponse createCricketPointScoring(@RequestBody String data, HttpServletResponse response,
			HttpServletRequest request, SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		GenericResponse feResponse = new GenericResponse();
		if (FeUtils.isNotEmpty(data)) {
			try {
				CricketPointScore cricketCategory = gson.fromJson(data, CricketPointScore.class);
				GenericBackendResponse beResponse = ApiManager.cricketPointScoreCreate(cricketCategory,
						FeUtils.createAPIMeta(session, request)); 
				if (beResponse != null) { 
					if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
						if (beResponse.isS()) {
							feResponse.setD(beResponse.getD());
							feResponse.setStatus(Constants.SUCCESS);
							feResponse.setSuccessMsg(beResponse.getMsg());
						} else {
							feResponse.setStatus(Constants.FAILED);
							feResponse.setErrorDetails(beResponse.getEd());
						}
					}
				} else {
					feResponse.setStatus(Constants.NOT_RESPONEDING);
					feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				feResponse.setStatus(Constants.FAILED);
				feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
			}
		} else {
			feResponse.setStatus(Constants.FAILED);
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
		}
		return feResponse;
	}
	
	@RequestMapping(value = "ajx/cricket/get/pointScore/list", method = RequestMethod.GET)
	@ResponseBody
	public ObjectListResponse getCricketPointScoreList(@RequestParam("status") String cricketStatus, HttpServletResponse response, HttpServletRequest request,
			SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		ObjectListResponse feResponse = new ObjectListResponse();
		try {
			CricketPointScoreListResponse beResponse = ApiManager
					.getCricketPointScoreList(cricketStatus, FeUtils.createAPIMeta(session, request));
			if (beResponse != null) { 
				if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
					if (beResponse.isS()) {
						for (CricketPointScoreListModel cricketPointScoreListModel : beResponse.getCricketPointScoreList())
							feResponse.getResponse().add(cricketPointScoreListModel);
						feResponse.setSuccessMsg(beResponse.getMsg());
						feResponse.setStatus(Constants.SUCCESS);
					} else { 
						feResponse.setErrorDetails(beResponse.getEd()); 
						feResponse.setStatus(Constants.FAILED); 
					} 
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
	
	@RequestMapping(value = "ajx/cricket/scorePoint/delete", method = RequestMethod.GET)
	@ResponseBody
	public GenericResponse deleteCricketPointScoreById(@RequestParam("id") String id, HttpServletResponse response,
			HttpServletRequest request, SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		GenericResponse feResponse = new GenericResponse();
		try {
			GenericBackendResponse beResponse = ApiManager.deleteCricketPointScoreById(id,
					FeUtils.createAPIMeta(session, request));
			if (beResponse != null) {
				if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
					if (beResponse.isS()) {
						feResponse.setSuccessMsg(beResponse.getMsg());
						feResponse.setStatus(Constants.SUCCESS);
					} else {
						feResponse.setErrorDetails(beResponse.getEd());
						feResponse.setStatus(Constants.FAILED);
					}
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
	
	@RequestMapping(value = "ajx/cricket/get/pointScore", method = RequestMethod.GET)
	@ResponseBody 
	public ObjectResponse getCricketPoint(@RequestParam("id") String id, HttpServletResponse response, HttpServletRequest request,
			SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		ObjectResponse feResponse = new ObjectResponse();
		try {
			CricketPointScore beResponse = ApiManager.getCricketPointScoreById(id , FeUtils.createAPIMeta(session, request));
				if (beResponse != null) {
					if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
						if (beResponse.isS()) {
							feResponse.setResponse(beResponse);
							feResponse.setStatus(Constants.SUCCESS);
						} else {
							feResponse.setErrorDetails(beResponse.getEd());
							feResponse.setStatus(Constants.FAILED);
						}
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajx/cricket/pointScore/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public GenericResponse updateCricketPoints(@RequestParam(value = "cricket", required = false) String updateData, @RequestParam(value = "id", required = false) String id,
			HttpServletResponse response, HttpServletRequest request, SessionStatus status,	@ModelAttribute("session") SessionWrapper session) {
		GenericResponse feResponse = new GenericResponse();
		if (FeUtils.isNotEmpty(id)) {
			if (!updateData.isEmpty()) {
				try {
					UpdateRequest updateRequest = new UpdateRequest();
					updateRequest.setRequestData(gson.fromJson(updateData, HashMap.class));
					GenericBackendResponse beResponse = ApiManager.updateCricketById(id, updateRequest, FeUtils.createAPIMeta(session, request));
					if (beResponse != null) {
						if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
							if (beResponse.isS()) { 
								feResponse.setSuccessMsg(beResponse.getMsg());
								feResponse.setStatus(Constants.SUCCESS);
							} else { 
								feResponse.setErrorDetails(beResponse.getEd());
								feResponse.setStatus(Constants.FAILED);
							}
						}
					} else {
						feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
						feResponse.setStatus(Constants.NOT_RESPONEDING);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
					feResponse.setStatus(Constants.FAILED); 
				}
			} else {
				feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
				feResponse.setStatus(Constants.FAILED);
			}
		} else {
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
			feResponse.setStatus(Constants.FAILED);
		}
		return feResponse;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajx/cricket/category/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public GenericResponse updateCategoryDetails(@RequestParam(value = "category", required = false) String updateData, @RequestParam(value = "id", required = false) String id,
			HttpServletResponse response, HttpServletRequest request, SessionStatus status,	@ModelAttribute("session") SessionWrapper session) {
		GenericResponse feResponse = new GenericResponse();
		if (FeUtils.isNotEmpty(id)) { 
			if (!updateData.isEmpty()) {
				try {
					UpdateRequest updateRequest = new UpdateRequest();
					updateRequest.setRequestData(gson.fromJson(updateData, HashMap.class));
					GenericBackendResponse beResponse = ApiManager.updateCategoryById(id, updateRequest, FeUtils.createAPIMeta(session, request));
					if (beResponse != null) { 
						if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
							if (beResponse.isS()) { 
								feResponse.setSuccessMsg(beResponse.getMsg());
								feResponse.setStatus(Constants.SUCCESS);
							} else { 
								feResponse.setErrorDetails(beResponse.getEd());
								feResponse.setStatus(Constants.FAILED);
							}
						}
					} else {
						feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
						feResponse.setStatus(Constants.NOT_RESPONEDING);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
					feResponse.setStatus(Constants.FAILED); 
				}
			} else {
				feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
				feResponse.setStatus(Constants.FAILED);
			}
		} else {
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
			feResponse.setStatus(Constants.FAILED);
		}
		return feResponse;
	}
	
}
  

