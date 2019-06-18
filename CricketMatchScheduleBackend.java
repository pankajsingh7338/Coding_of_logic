package com.actolap.wse.backoffice.controller;

import java.util.ArrayList;
import java.util.Date;
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
import com.actolap.wse.dao.CricketMatchScheduleDao;
import com.actolap.wse.dto.CommonDto;
import com.actolap.wse.dto.CricketImageDto;
import com.actolap.wse.dto.CricketMatchScheduleDto;
import com.actolap.wse.model.CricketContest;
import com.actolap.wse.model.CricketImage;
import com.actolap.wse.model.MatchSchedule;
import com.actolap.wse.model.MatchSchedule.CricketMatchScheduleStatus;
import com.actolap.wse.request.CricketMatchScheduleRequest;
import com.actolap.wse.request.SearchResponse;
import com.actolap.wse.request.UpdateRequest;
import com.actolap.wse.response.CricketMatchScheduleConfigResponse;
import com.actolap.wse.response.CricketMatchScheduleResponse;
import com.actolap.wse.response.MatchScheduleListResponse;
import com.actolap.wse.rest.secuirty.SecureAnnotation.UserSecure;
import com.actolap.wse.rest.secuirty.SecureAnnotation.WSEPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/cricketMatchSchedule")
@Api(value = "Rest API")
@UserSecure
public class CricketMatchScheduleController {

	private static final Logger LOG = LoggerFactory.getLogger(CricketMatchScheduleController.class);

	@WSEPermission(pl = { UserPermission.cricket_matchSchedule_create })
	public GenericResponse createMatchSchedule(Request request, Response response) {
		return cricketMatchScheduleCreateDoc(request.getBodyAs(CricketMatchScheduleRequest.class), request, response);
	}
	
	@WSEPermission(pl = { UserPermission.cricket_matchSchedule_update })
	public GenericResponse updateMatchSchedule(Request request, Response response) {
		return cricketMatchScheduleUpdateDoc(request.getBodyAs(UpdateRequest.class), request, response);
	} 
	  
	@WSEPermission(pl = { UserPermission.cricket_match_schedule_config_tab })
	@GET
	@Path("/config")
	@ApiOperation(value = "Config", notes = "Cricket Match Schedule Config", response = CricketMatchScheduleConfigResponse.class, httpMethod = "get")
	public CricketMatchScheduleConfigResponse getMatchScheduleConfig(Request request, Response response0) {
		CricketMatchScheduleConfigResponse response = new CricketMatchScheduleConfigResponse();
		try {
			for (Entry<String, CricketMatchScheduleStatus> entry : InMemory.cricketMatchScheduleStatus.entrySet()) {
				response.getCricketMatchScheduleStatusMap().put(entry.getKey(), entry.getValue().toString());
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.cricket_match_filter_search })
	@GET
	@Path("/getContestList")
	@ApiOperation(value = "List", notes = "Match Schedule Contest Filter Search List", response = SearchResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "query", value = "Filter Query", dataType = "string", paramType = "query", required = true) })
	public SearchResponse getContestList(Request request, Response responseO) {
		SearchResponse response = new SearchResponse();
		try {
			String query = request.getHeader(Urlparams.query);
			if (!query.isEmpty()) {
				List<CricketContest> cricketContestList = CricketMatchScheduleDao.searchFilter(query);
				for (CricketContest cricketContest : cricketContestList) {
					response.getSearchList()
							.add(new CommonDto(cricketContest.getId(), cricketContest.getContestName()));
				}
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@Path("/create")
	@ApiOperation(value = "Create", notes = "Cricket Match Schedule Create", response = GenericResponse.class, httpMethod = "post")
	public GenericResponse cricketMatchScheduleCreateDoc(
			@ApiParam(required = true) CricketMatchScheduleRequest cricketMatchScheduleRequest,
			@ApiParam(hidden = true) Request request0, @ApiParam(hidden = true) Response response) {
		GenericResponse beResponse = new GenericResponse();
		if (cricketMatchScheduleRequest != null) {
			try {
				MatchSchedule matchSchedule = new MatchSchedule();
				matchSchedule.setScheduledMatchTitle(cricketMatchScheduleRequest.getScheduledMatchTitle());
				matchSchedule.setScheduledMatchInfo(cricketMatchScheduleRequest.getScheduledMatchInfo());
				matchSchedule.setCricketMatchKey(cricketMatchScheduleRequest.getCricketMatchKey());
				matchSchedule.setScheduledTime(new Date(cricketMatchScheduleRequest.getScheduledTime()));
				if (cricketMatchScheduleRequest.getScheduledStatus().equals("completed"))
					matchSchedule.setScheduledStatus(CricketMatchScheduleStatus.COMPLETED);
				else if (cricketMatchScheduleRequest.getScheduledStatus().equals("notstarted"))
					matchSchedule.setScheduledStatus(CricketMatchScheduleStatus.NOTSTARTED);
				else if (cricketMatchScheduleRequest.getScheduledStatus().equals("started"))
					matchSchedule.setScheduledStatus(CricketMatchScheduleStatus.STARTED);
				matchSchedule.setContestId(cricketMatchScheduleRequest.getContestId());
				matchSchedule.setAccessToken(cricketMatchScheduleRequest.getAccessToken()); 
				matchSchedule.setExpired(cricketMatchScheduleRequest.getExpired()); 
				CricketImage cricketImage = null;
				for (CricketImageDto cricketImageDto : cricketMatchScheduleRequest.getCricketImageList()) {
					cricketImage = new CricketImage();
					cricketImage.setTeamName(cricketImageDto.getTeamName()); 
					cricketImage.setTeamUrl(cricketImageDto.getTeamUrl()); 
					matchSchedule.getCricketImageList().add(cricketImage); 
				}
				CricketMatchScheduleDao.persist(matchSchedule);
				beResponse.setS(true);
				beResponse.setMsg("Cricket Match Schedule is Created");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				beResponse.setEd(e.getMessage());
			}
		} else {
			beResponse.setEd("Required feild are coming invalid");
		}
		return beResponse;
	}

	@WSEPermission(pl = { UserPermission.cricket_match_schedule_list_tab })
	@GET
	@Path("/get/matchScheduleList")
	@ApiOperation(value = "List", notes = "Match Schedule List", response = MatchScheduleListResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "status", value = "Cricket Match Status", dataType = "string", paramType = "query", required = true) })
	public MatchScheduleListResponse getMatchSchedule(Request request, Response responseO) {
		MatchScheduleListResponse response = new MatchScheduleListResponse();
		String status = request.getHeader(Urlparams.status);
		try {
			List<MatchSchedule> matchScheduleList = CricketMatchScheduleDao.list(status);
			if (matchScheduleList != null && !matchScheduleList.isEmpty()) {
				for (MatchSchedule matchSchedule : matchScheduleList) {
					CricketMatchScheduleDto cricketMatchScheduleDto = new CricketMatchScheduleDto(matchSchedule);
					response.getCricketMatchScheduleList().add(cricketMatchScheduleDto);
				}
			} else {
				response.setMsg("No match schedule found");
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
	@Path("/delete/matchSchedule")
	@ApiOperation(value = "Delete", notes = "Delete Match Schedule", response = GenericResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "category Id", dataType = "string", paramType = "query", required = true) })
	public GenericResponse deleteMatchScheduleById(Request request, Response responseO) {
		GenericResponse response = new GenericResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				response.setS(true);
				CricketMatchScheduleDao.delete(id);
				response.setMsg("Match Schedule has been successfully deleted");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id should not be empty");
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.cricket_match_schedule_get_tab })
	@GET
	@Path("/getMatchSchedule/details")
	@ApiOperation(value = "Get Details", notes = "Get Match Details", response = CricketMatchScheduleResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Match Schedule Id", dataType = "string", paramType = "query", required = true) })
	public CricketMatchScheduleResponse getMatchScheduleById(Request request, Response responseO) {
		CricketMatchScheduleResponse response = new CricketMatchScheduleResponse();
		String id = request.getHeader(Urlparams.id); 
		if (Utils.isNotEmpty(id)) { 
			try { 
				MatchSchedule matchSchedule = CricketMatchScheduleDao.getById(id);
				if (matchSchedule != null) {
					response.setId(matchSchedule.getId());
					response.setScheduledMatchInfo(matchSchedule.getScheduledMatchInfo());
					response.setScheduledMatchTitle(matchSchedule.getScheduledMatchTitle());
					response.setScheduledTime(matchSchedule.getScheduledTime().getTime());
					response.setScheduledStatus(matchSchedule.getScheduledStatus());
					response.setCricketMatchKey(matchSchedule.getCricketMatchKey());
					response.setAccessToken(matchSchedule.getAccessToken()); 
					response.setExpired(matchSchedule.getExpired()); 
					if (!matchSchedule.getCricketImageList().isEmpty()) {
						for (CricketImage cricketImage : matchSchedule.getCricketImageList()) {
							CricketImage cricketImageDto = new CricketImage();
							cricketImageDto.setTeamName(cricketImage.getTeamName()); 
							cricketImageDto.setTeamUrl(cricketImage.getTeamUrl()); 
							response.getCricketImageList().add(cricketImageDto);
						}
					}
					List<String> contestDiamentionList = (ArrayList<String>) matchSchedule.getContestId();
					if (!contestDiamentionList.isEmpty()) {
						List<CricketContest> CricketContestList = CricketMatchScheduleDao
								.getContestListByIds(contestDiamentionList);
						for (CricketContest cricketContest : CricketContestList) {
							response.getContestDiamention()
									.add(new CommonDto(cricketContest.getId(), cricketContest.getContestName()));
						}
					}
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
	@ApiOperation(value = "Update", notes = "Update Match Details By Id", response = GenericResponse.class, httpMethod = "post")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Match Id", dataType = "string", paramType = "query", required = true) })
	public GenericResponse cricketMatchScheduleUpdateDoc(@ApiParam(required = true) UpdateRequest matchRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response responseO) { 
		GenericResponse response = new GenericResponse(); 
		String id = request.getHeader(Urlparams.id); 
		if (Utils.isNotEmpty(id)) { 
			try { 
				if (matchRequest != null && matchRequest.getRequestData() != null) { 
					MatchSchedule matchSchedule = CricketMatchScheduleDao.getById(id);
					if (matchSchedule != null) { 
						String userId = request.getHeader(Urlparams.wseusr);
						List<String> permissionList = new ArrayList<String>();
						com.actolap.wse.Utils.userPermissions(userId, permissionList);
						Map<String, Object> mongoUpdate = new HashMap<String, Object>();
						mongoUpdate.putAll(matchRequest.getRequestData()); 
						if (!mongoUpdate.isEmpty()) {
							CricketMatchScheduleDao.update(id, mongoUpdate);
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

