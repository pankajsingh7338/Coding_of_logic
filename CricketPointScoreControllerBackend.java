package com.actolap.wse.backoffice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import com.actolap.wse.UserPermission;
import com.actolap.wse.commons.GenericResponse;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.config.InMemory;
import com.actolap.wse.constants.Urlparams;
import com.actolap.wse.dao.CricketPointScoreDao;
import com.actolap.wse.dao.CricketScoringPointDao; 
import com.actolap.wse.dto.CricketCategoryDto;
import com.actolap.wse.dto.CricketPointScoreDto;
import com.actolap.wse.model.CricketCategory;
import com.actolap.wse.model.CricketPoints;
import com.actolap.wse.model.CricketPoints.CricketPointScoreStatus;
import com.actolap.wse.request.CricketCategoryRequest;
import com.actolap.wse.request.CricketPointScoreRequest;
import com.actolap.wse.request.UpdateRequest;
import com.actolap.wse.response.CricketCategoryListResponse;
import com.actolap.wse.response.CricketCategoryResponse;
import com.actolap.wse.response.CricketPointScoreConfigResponse;
import com.actolap.wse.response.CricketPointScoreListResponse;
import com.actolap.wse.response.CricketPointScoreResponse;
import com.actolap.wse.rest.secuirty.SecureAnnotation.UserSecure;
import com.actolap.wse.rest.secuirty.SecureAnnotation.WSEPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/cricketPointScoring") 
@Api(value = "Rest API") 
@UserSecure
public class CricketPointScoringController { 

	private static final Logger LOG = LoggerFactory.getLogger(CricketPointScoringController.class);

	@WSEPermission(pl = { UserPermission.cricket_bowling_create })
	public GenericResponse create(Request request, Response response) {
		return cricketCategoryCreateDoc(request.getBodyAs(CricketCategoryRequest.class), request, response);
	}

	@WSEPermission(pl = { UserPermission.cricket_batting_create })
	public GenericResponse createPointScore(Request request, Response response) {
		return cricketPointScoreCreateDoc(request.getBodyAs(CricketPointScoreRequest.class), request, response);
	}

	@WSEPermission(pl = { UserPermission.cricket_batting_update_tab })
	public GenericResponse cricketUpdate(Request request, Response response) {
		return cricketUpdateDoc(request.getBodyAs(UpdateRequest.class), request, response);
	}
	
	@WSEPermission(pl = { UserPermission.cricket_bowling_update_tab }) 
	public GenericResponse categoryUpdate(Request request, Response response) { 
		return categoryUpdateDoc(request.getBodyAs(UpdateRequest.class), request, response);
	}
	
	@Path("/create/category")
	@ApiOperation(value = "Create", notes = "Cricket Category Create", response = GenericResponse.class, httpMethod = "post")
	public GenericResponse cricketCategoryCreateDoc(
			@ApiParam(required = true) CricketCategoryRequest cricketPointScoringRequest,
			@ApiParam(hidden = true) Request request0, @ApiParam(hidden = true) Response response) {
		GenericResponse reportResponse = new GenericResponse();  
		if (cricketPointScoringRequest != null) { 
			try { 
				CricketCategory cricketCategory = new CricketCategory();
				cricketCategory.setTitle(cricketPointScoringRequest.getTitle());
				cricketCategory.setEntities(cricketPointScoringRequest.getEntities()); 
				CricketScoringPointDao.persist(cricketCategory);
				reportResponse.setS(true); 
				reportResponse.setMsg("Cricket category is Created");
			} catch (Exception e) { 
				LOG.error(e.getMessage(), e); 
				reportResponse.setEd(e.getMessage()); 
			} 
		} else { 
			reportResponse.setEd("Required feild are coming invalid"); 
		} 
		return reportResponse; 
	} 

	@WSEPermission(pl = { UserPermission.cricket_bowling_list_tab })
	@GET
	@Path("/get/list")
	@ApiOperation(value = "List", notes = "Category List", response = CricketCategoryListResponse.class, httpMethod = "get")
	public CricketCategoryListResponse categoryList(Request request, Response responseO) {
		CricketCategoryListResponse response = new CricketCategoryListResponse();
		try {
			List<CricketCategory> cricketCategoryList = CricketScoringPointDao.list();
			if (cricketCategoryList != null && !cricketCategoryList.isEmpty()) {
				for (CricketCategory cricketCategory : cricketCategoryList) {
					CricketCategoryDto cricketCategoryDto = new CricketCategoryDto(cricketCategory);
					response.getCricketCategoryDto().add(cricketCategoryDto);
				}
			} else {
				response.setMsg("No categories found");
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}
	
	@WSEPermission(pl = { UserPermission.cricket_bowling_get_tab })
	@GET
	@Path("/get/details")
	@ApiOperation(value = "Details", notes = "Get Category Details", response = CricketCategoryResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Category Id", dataType = "string", paramType = "query", required = true) })
	public CricketCategoryResponse getDetails(Request request, Response responseO) { 
		CricketCategoryResponse response = new CricketCategoryResponse(); 
		String id = request.getHeader(Urlparams.id); 
		if (Utils.isNotEmpty(id)) { 
			try { 
				CricketCategory cricketCategory = CricketScoringPointDao.getById(id); 
				if (cricketCategory != null) { 
					response = new CricketCategoryResponse(cricketCategory); 
					response.setS(true); 
				} else { 
					response.setEd("Id is not valid"); 
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

	@WSEPermission(pl = { UserPermission.cricket_bowling_delete_tab })
	@GET
	@Path("/delete/category")
	@ApiOperation(value = "Delete", notes = "Delete Cricket category", response = GenericResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "category Id", dataType = "string", paramType = "query", required = true) })
	public GenericResponse deleteCategoryById(Request request, Response responseO) {
		GenericResponse response = new GenericResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				CricketScoringPointDao.delete(id);
				response.setS(true);
				response.setMsg("Cricket Category has been successfully deleted");
			} catch (Exception e) { 
				LOG.error(e.getMessage(), e); 
				response.setEd(e.getMessage()); 
			} 
		} else { 
			response.setEd("Id should not be empty"); 
		} 
		return response; 
	} 
	
	@POST 
	@Path("/updateCategory")  
	@ApiOperation(value = "Update", notes = "Update Category Details By Id", response = GenericResponse.class, httpMethod = "post") 
	@ApiImplicitParams({ 
			@ApiImplicitParam(name = "id", value = "Category Id", dataType = "string", paramType = "query", required = true) }) 
	public GenericResponse categoryUpdateDoc(@ApiParam(required = true) UpdateRequest categoryRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response responseO) { 
		GenericResponse response = new GenericResponse(); 
		String id = request.getHeader(Urlparams.id); 
		if (Utils.isNotEmpty(id)) { 
			try { 
				if (categoryRequest != null && categoryRequest.getRequestData() != null) {
					CricketCategory cricketCategory = CricketScoringPointDao.getById(id);
					if (cricketCategory != null) {
						String userId = request.getHeader(Urlparams.wseusr);
						List<String> permissionList = new ArrayList<String>();
						com.actolap.wse.Utils.userPermissions(userId, permissionList);
						Map<String, Object> mongoUpdate = new HashMap<String, Object>();
						mongoUpdate.putAll(categoryRequest.getRequestData()); 
						if (!mongoUpdate.isEmpty()) {
							CricketScoringPointDao.update(id, mongoUpdate);
							response.setS(true);
							if (response.getMsg() == null) 
								response.setMsg("Category has been successfully updated.");
						} else { 
							response.setEd("data is coming wrong");
						}
					} else {
						response.setEd("Invalid Id.");
					}
				} else { 
					response.setEd("Required fields are invalid."); 
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id shouldn't be empty");
		}
		return response;
	}
	  
	@WSEPermission(pl = { UserPermission.cricket_batting_config_tab })
	@GET
	@Path("/config") 
	@ApiOperation(value = "Config", notes = "Cricket Point Config", response = CricketPointScoreConfigResponse.class, httpMethod = "get")
	public CricketPointScoreConfigResponse config(Request request, Response response0) {
		CricketPointScoreConfigResponse response = new CricketPointScoreConfigResponse();
		try { 
			for (Entry<String, CricketPointScoreStatus> entry : InMemory.CricketPointScoreStatusMap.entrySet()) {
				response.getCricketScorePointStatusMap().put(entry.getKey(), entry.getValue().toString());
			}
			response.setS(true); 
		} catch (Exception e) { 
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response; 
	} 
	  
	@Path("/pointScore/create") 
	@ApiOperation(value = "Create", notes = "Cricket Category Create", response = GenericResponse.class, httpMethod = "post") 
	public GenericResponse cricketPointScoreCreateDoc(
			@ApiParam(required = true) CricketPointScoreRequest cricketPointScoreRequest,
			@ApiParam(hidden = true) Request request0, @ApiParam(hidden = true) Response response) { 
		GenericResponse reportResponse = new GenericResponse();  
		if (cricketPointScoreRequest != null) { 
			try { 
				CricketPoints cricketPoints = new CricketPoints(); 
				cricketPoints.setTitle(cricketPointScoreRequest.getTitle()); 
				cricketPoints.setCategory(cricketPointScoreRequest.getCategory()); 
				cricketPoints.setEntity(cricketPointScoreRequest.getEntity()); 
				if (cricketPointScoreRequest.getManualValue() != null) 
					cricketPoints.setManualValue(cricketPointScoreRequest.getManualValue()); 
				cricketPoints.setManual(cricketPointScoreRequest.isManual()); 
				if (cricketPointScoreRequest.getPlayerPoints() != null) 
					cricketPoints.setPlayerPoints(cricketPointScoreRequest.getPlayerPoints()); 
				cricketPoints.setPlayer(cricketPointScoreRequest.isPlayer()); 
				if (cricketPointScoreRequest.getUserPoints() != null) 
					cricketPoints.setUserPoints(cricketPointScoreRequest.getUserPoints()); 
				cricketPoints.setUser(cricketPointScoreRequest.isUser()); 
				if (cricketPointScoreRequest.getApiValue() != null) 
					cricketPoints.setApiValue(cricketPointScoreRequest.getApiValue());
				cricketPoints.setStatus(CricketPointScoreStatus.DRAFT);
				CricketPointScoreDao.persist(cricketPoints);
				reportResponse.setS(true);
				reportResponse.setMsg("Cricket point is Created");
			} catch (Exception e) { 
				LOG.error(e.getMessage(), e);
				reportResponse.setEd(e.getMessage());
			}
		} else {
			reportResponse.setEd("Required feild are coming invalid");
		}
		return reportResponse;
	}

	@WSEPermission(pl = { UserPermission.cricket_batting_list_tab })
	@GET
	@Path("/get/pointScore")
	@ApiOperation(value = "List", notes = "Point Score List", response = CricketPointScoreListResponse.class, httpMethod = "get")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "status", value = "Cricket point Status", dataType = "string", paramType = "query", required = true)})
	public CricketPointScoreListResponse getPointScoreList(Request request, Response responseO) {
		CricketPointScoreListResponse response = new CricketPointScoreListResponse();
		String status = request.getHeader(Urlparams.status); 
		try {
			List<CricketPoints> CricketPointsList = CricketPointScoreDao.list(status);
			if (CricketPointsList != null && !CricketPointsList.isEmpty()) {
				for (CricketPoints cricketPoints : CricketPointsList) {
					CricketPointScoreDto cricketPointScoreDto = new CricketPointScoreDto(cricketPoints);
					response.getCricketPointScoreList().add(cricketPointScoreDto);
				}
			} else { 
				response.setMsg("No cricket point list found");
			} 
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.cricket_batting_delete_tab })
	@GET
	@Path("/delete/pointScore")
	@ApiOperation(value = "Delete", notes = "Delete Cricket category", response = GenericResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "category Id", dataType = "string", paramType = "query", required = true) })
	public GenericResponse deletePointScoreById(Request request, Response responseO) {
		GenericResponse response = new GenericResponse(); 
		String id = request.getHeader(Urlparams.id); 
		if (Utils.isNotEmpty(id)) { 
			try { 
				response.setS(true);
				CricketPointScoreDao.delete(id);
				response.setMsg("Cricket Point Score has been successfully deleted");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else { 
			response.setEd("Id should not be empty");
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.cricket_batting_get_tab })
	@GET
	@Path("/get")
	@ApiOperation(value = "List", notes = "Get Cricket Score", response = CricketPointScoreResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Score point Id", dataType = "string", paramType = "query", required = true) })
	public CricketPointScoreResponse getPointById(Request request, Response responseO) {
		CricketPointScoreResponse response = new CricketPointScoreResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				CricketPoints cricketPoints = CricketPointScoreDao.getById(id);
				if (cricketPoints != null) {
					response = new CricketPointScoreResponse(cricketPoints);
					response.setS(true);
				} else {
					response.setEd("Id is not valid");
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
	  
	@POST 
	@Path("/update") 
	@ApiOperation(value = "Update", notes = "Update cricket Details By Id", response = GenericResponse.class, httpMethod = "post") 
	@ApiImplicitParams({ 
			@ApiImplicitParam(name = "id", value = "Cricket Id", dataType = "string", paramType = "query", required = true) }) 
	public GenericResponse cricketUpdateDoc(@ApiParam(required = true) UpdateRequest cricketRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response responseO) { 
		GenericResponse response = new GenericResponse(); 
		String id = request.getHeader(Urlparams.id); 
		if (Utils.isNotEmpty(id)) { 
			CricketPointScoreStatus status; 
			try { 
				if (cricketRequest != null && cricketRequest.getRequestData() != null) {
					CricketPoints cricketPoints = CricketPointScoreDao.getById(id);
					if (cricketPoints != null) {
						String userId = request.getHeader(Urlparams.wseusr);
						List<String> permissionList = new ArrayList<String>();
						com.actolap.wse.Utils.userPermissions(userId, permissionList);
						Map<String, Object> mongoUpdate = new HashMap<String, Object>();
						mongoUpdate.putAll(cricketRequest.getRequestData());
						for (Entry<String, Object> entry : cricketRequest.getRequestData().entrySet()) {
							if (entry.getKey().equals("status")) {
								status = CricketPointScoreStatus
										.valueOf(cricketRequest.getRequestData().get("status").toString());
								if ((cricketPoints.getStatus().equals(CricketPointScoreStatus.DRAFT)
										|| cricketPoints.getStatus().equals(CricketPointScoreStatus.REJECTED))
										&& status.equals(CricketPointScoreStatus.PENDING_APPROVAL)) {
									if (permissionList
											.contains(UserPermission.cricket_batting_status_pending_approval)) {
										mongoUpdate.put("status", CricketPointScoreStatus.PENDING_APPROVAL);
									}
								} else if (cricketPoints.getStatus().equals(CricketPointScoreStatus.PENDING_APPROVAL)
										&& status.equals(CricketPointScoreStatus.REJECTED)) {
									if (permissionList
											.contains(UserPermission.cricket_batting_status_reject_approval)) {
										mongoUpdate.put("status", CricketPointScoreStatus.REJECTED);
									}
								} else if (cricketPoints.getStatus().equals(CricketPointScoreStatus.PENDING_APPROVAL)
										&& status.equals(CricketPointScoreStatus.LIVE)) {
									if (permissionList.contains(UserPermission.cricket_batting_status_pending_live)) {
										mongoUpdate.put("status", CricketPointScoreStatus.LIVE);
									} 
								} else if (cricketPoints.getStatus().equals(CricketPointScoreStatus.LIVE)
										&& status.equals(CricketPointScoreStatus.STOPPED)) {
									if (permissionList.contains(UserPermission.cricket_batting_status_live)) {
										mongoUpdate.put("status", CricketPointScoreStatus.STOPPED);
									}
								}
							}
						}
						if (!mongoUpdate.isEmpty()) {
							CricketPointScoreDao.update(id, mongoUpdate);
							response.setS(true);
							if (response.getMsg() == null) 
								response.setMsg("Cricket has been successfully updated.");
						} else { 
							response.setEd("data is coming wrong");
						}
					} else {
						response.setEd("Invalid Id.");
					}
				} else { 
					response.setEd("Required fields are invalid."); 
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id shouldn't be empty");
		}
		return response;
	}
}

