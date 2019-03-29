package com.actolap.wse.backoffice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actolap.wse.Constants;
import com.actolap.wse.LyveSendSms;
import com.actolap.wse.UserPermission;
import com.actolap.wse.cache.PlayerCache;
import com.actolap.wse.common.dashboard.reporting.ReportService;
import com.actolap.wse.commons.GenericResponse;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.config.InMemory;
import com.actolap.wse.constants.Urlparams;
import com.actolap.wse.dao.AffiliateDao;
import com.actolap.wse.dao.OneTimePasswordDao;
import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.dao.ReportDao;
import com.actolap.wse.dao.TableDao;
import com.actolap.wse.dao.TournamentDao;
import com.actolap.wse.dao.UserDao;
import com.actolap.wse.dto.CommonDto;
import com.actolap.wse.dto.ReportDto;
import com.actolap.wse.dto.UserDepartment;
import com.actolap.wse.enums.ResponseCode;
import com.actolap.wse.inmemory.memcache.AffiliateCache;
import com.actolap.wse.model.OneTimePassword;
import com.actolap.wse.model.User;
import com.actolap.wse.model.affiliate.Affiliate;
import com.actolap.wse.model.game.poker.ListResponse;
import com.actolap.wse.model.game.poker.PokerTable;
import com.actolap.wse.model.game.poker.Tournament;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.report.Condition;
import com.actolap.wse.model.report.DateCondition;
import com.actolap.wse.model.report.Dimensions;
import com.actolap.wse.model.report.Metric;
import com.actolap.wse.model.report.Operation;
import com.actolap.wse.model.report.Report;
import com.actolap.wse.model.report.Report.DATERANGE;
import com.actolap.wse.model.report.Report.FREQUENCY;
import com.actolap.wse.model.report.ReportConfig;
import com.actolap.wse.model.report.ReportRequest;
import com.actolap.wse.model.report.ReportType;
import com.actolap.wse.model.report.ReportingResponse;
import com.actolap.wse.model.report.ResponseItem;
import com.actolap.wse.request.OTPRequest;
import com.actolap.wse.request.ReportConditionDto;
import com.actolap.wse.request.ReportCreateRequest;
import com.actolap.wse.request.UpdateRequest;
import com.actolap.wse.response.ReportConfigResponse;
import com.actolap.wse.response.ReportDetailResponse;
import com.actolap.wse.response.ReportGetResponse;
import com.actolap.wse.response.ReportListResponse;
import com.actolap.wse.response.ReportOtpResponse;
import com.actolap.wse.response.SearchResponse;
import com.actolap.wse.rest.secuirty.SecureAnnotation.UserSecure;
import com.actolap.wse.rest.secuirty.SecureAnnotation.WSEPermission;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mongodb.util.JSON;

@Path("/report")
@Api(value = "Rest API")
@UserSecure 
public class ReportController {
	private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);
	Gson gson = new Gson();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdf_trip = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

	@WSEPermission(pl = { UserPermission.reports_create })
	public GenericResponse create(Request request, Response response) {
		return reportCreatePost(request.getBodyAs(ReportCreateRequest.class), request, response);
	}

	@WSEPermission(pl = { UserPermission.reports_update })
	public GenericResponse update(Request request, Response response) {
		return reportUpdateDoc(request.getBodyAs(UpdateRequest.class), request, response);
	}

	public GenericResponse validate(Request request, Response response) {
		return validateOtp(request.getBodyAs(OTPRequest.class), request, response);
	}

	@WSEPermission(pl = { UserPermission.reports_config })
	@GET
	@Path("/config")
	@ApiOperation(value = "Config", notes = "Report Config", response = ReportConfigResponse.class, httpMethod = "get")
	public ReportConfigResponse config(Request request, Response responseO) {
		ReportConfigResponse response = new ReportConfigResponse();
		try {
			@SuppressWarnings("unchecked")
			Optional<UserDepartment> optUserDepartment = (Optional<UserDepartment>) request.getExt().get(
					Constants.USER_KEY); 
			UserDepartment userDepartment = optUserDepartment.get(); 
			User user = userDepartment.getUser(); 
			List<String> permissionList = new ArrayList<String>(); 
			com.actolap.wse.Utils.userPermissions(user.getId(), permissionList); 
			ReportConfig reportConfig = InMemory.reportConfig; 
			for (String key : reportConfig.getDimentions().keySet()) { 
				if (key.equals("First Name") && permissionList.contains(UserPermission.reports_dimensions_firstName)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Last Name")
						&& permissionList.contains(UserPermission.reports_dimensions_lastName)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Game Name")
						&& permissionList.contains(UserPermission.reports_dimensions_gameName)) {
					response.getSummary().getDimensions().add(key);
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Gender") && permissionList.contains(UserPermission.reports_dimensions_gender)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("State") && permissionList.contains(UserPermission.reports_dimensions_state)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("City") && permissionList.contains(UserPermission.reports_dimensions_city)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Email") && permissionList.contains(UserPermission.reports_dimensions_email)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
					response.getSummary().getDimensions().add(key);
				} else if (key.equals("Mobile") && permissionList.contains(UserPermission.reports_dimensions_mobile)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
					response.getSummary().getDimensions().add(key);
				} else if (key.equals("Status") && permissionList.contains(UserPermission.reports_dimensions_status)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Player") && permissionList.contains(UserPermission.reports_dimensions_playerId)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Last Login")
						&& permissionList.contains(UserPermission.reports_dimensions_lastLogin)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Net Financial Status")
						&& permissionList.contains(UserPermission.reports_dimensions_netFinancialStatus)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Cash Chips In Hand")
						&& permissionList.contains(UserPermission.reports_dimensions_cashChipsInHand)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("VIP Points In Hand")
						&& permissionList.contains(UserPermission.reports_dimensions_vipPointsInHand)) {
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Game Type")
						&& permissionList.contains(UserPermission.reports_dimensions_gameType)) {
					response.getPlayer().getDimensions().add(key);
					response.getSummary().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Date") && permissionList.contains(UserPermission.reports_dimensions_date)) {
					response.getSummary().getDimensions().add(key);
					response.getTournament().getDimensions().add(key);
					response.getPlayer().getDimensions().add(key);
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Tournament")
						&& permissionList.contains(UserPermission.reports_dimensions_tournament)) {
					response.getTournament().getDimensions().add(key);
				} else if (key.equals("Affiliate Email")
						&& permissionList.contains(UserPermission.reports_dimensions_affiliateEmail)) {
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Affiliate First Name")
						&& permissionList.contains(UserPermission.reports_dimensions_affiliateFirstName)) {
					response.getAffiliate().getDimensions().add(key);
				} else if (key.equals("Affiliate Last Name")
						&& permissionList.contains(UserPermission.reports_dimensions_affiliateLastName)) {
					response.getAffiliate().getDimensions().add(key);
				}

			}
			for (String key : reportConfig.getMeasures().keySet()) {
				if (key.equals("Game Played") && permissionList.contains(UserPermission.reports_metrics_game_played)) {
					response.getSummary().getMeasures().add(key);
					response.getPlayer().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Bonus Issued")
						&& permissionList.contains(UserPermission.reports_metrics_bonus_issued)) {
					response.getPlayer().getMeasures().add(key);
					response.getSummary().getMeasures().add(key);
				} else if (key.equals("Bonus Released")
						&& permissionList.contains(UserPermission.reports_metrics_bonus_released)) {
					response.getPlayer().getMeasures().add(key);
					response.getSummary().getMeasures().add(key);
				} else if (key.equals("Vip Points Issued")
						&& permissionList.contains(UserPermission.reports_metrics_vip_points_issued)) {
					response.getPlayer().getMeasures().add(key);
					response.getSummary().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Money Deposit")
						&& permissionList.contains(UserPermission.reports_metrics_money_deposit)) {
					response.getPlayer().getMeasures().add(key);
					response.getSummary().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Money Drawn")
						&& permissionList.contains(UserPermission.reports_metrics_moneyDrawn)) {
					response.getPlayer().getMeasures().add(key);
					response.getSummary().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Money Drawn Charge")
						&& permissionList.contains(UserPermission.reports_metrics_moneyDrawn_charge)) {
					response.getPlayer().getMeasures().add(key);
					response.getSummary().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Vip Point Deducted")
						&& permissionList.contains(UserPermission.reports_metrics_vip_points_deducted)) {
					response.getPlayer().getMeasures().add(key);
					response.getSummary().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Price Money")
						&& permissionList.contains(UserPermission.reports_metrics_priceMoney)) {
					response.getTournament().getMeasures().add(key);
				} else if (key.equals("Enrolled") && permissionList.contains(UserPermission.reports_metrics_Enrolled)) {
					response.getTournament().getMeasures().add(key);
				} else if (key.equals("Players") && permissionList.contains(UserPermission.reports_metrics_players)) {
					response.getTournament().getMeasures().add(key);
				} else if (key.equals("Collections")
						&& permissionList.contains(UserPermission.reports_metrics_collections)) {
					response.getTournament().getMeasures().add(key);
				} else if (key.equals("Tournaments Participated")
						&& permissionList.contains(UserPermission.reports_metrics_tournamentsParticipated)) {
					response.getPlayer().getMeasures().add(key);
				} else if (key.equals("Tournament Lost")
						&& permissionList.contains(UserPermission.reports_metrics_tournamentLost)) {
					response.getPlayer().getMeasures().add(key);
				} else if (key.equals("Tournament Won")
						&& permissionList.contains(UserPermission.reports_metrics_tournamentWon)) {
					response.getPlayer().getMeasures().add(key);
				} else if (key.equals("Tournament Spending")
						&& permissionList.contains(UserPermission.reports_metrics_tournamentSpending)) {
					response.getPlayer().getMeasures().add(key);
				} else if (key.equals("TDS Refunded")
						&& permissionList.contains(UserPermission.reports_metrics_tdsRefunded)) {
					response.getPlayer().getMeasures().add(key);
				} else if (key.equals("Discount Received")
						&& permissionList.contains(UserPermission.reports_metrics_discountReceived)) {
					response.getPlayer().getMeasures().add(key);
				} else if (key.equals("Chip Wagered")
						&& permissionList.contains(UserPermission.reports_metrics_chipWagered)) {
					response.getPlayer().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Bonus Chips Encashed")
						&& permissionList.contains(UserPermission.reports_metrics_bonusChipsEncashed)) {
					response.getPlayer().getMeasures().add(key);
				} else if (key.equals("Vip Points Encashed")
						&& permissionList.contains(UserPermission.reports_metrics_vip_points_issued)) {
					response.getPlayer().getMeasures().add(key);
				} else if (key.equals("Games Won") && permissionList.contains(UserPermission.reports_metrics_gamesWon)) {
					response.getPlayer().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Games Lost")
						&& permissionList.contains(UserPermission.reports_metrics_gamesLost)) {
					response.getPlayer().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Net Profit")
						&& permissionList.contains(UserPermission.reports_metrics_netProfit)) {
					response.getPlayer().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Rake Generated")
						&& permissionList.contains(UserPermission.reports_metrics_rake_generated)) {
					response.getPlayer().getMeasures().add(key);
					response.getSummary().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Played Time")
						&& permissionList.contains(UserPermission.reports_metrics_played_time)) {
					response.getPlayer().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Chips Won") && permissionList.contains(UserPermission.reports_metrics_chipsWon)) {
					response.getPlayer().getMeasures().add(key);
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Commission")
						&& permissionList.contains(UserPermission.reports_metrics_commission)) {
					response.getAffiliate().getMeasures().add(key);
				} else if (key.equals("Tds Deducted")
						&& permissionList.contains(UserPermission.reports_metrics_tds_deducted)) {
					response.getPlayer().getMeasures().add(key);
					response.getSummary().getMeasures().add(key);
				}

			}
			for (String key : reportConfig.getFilters().keySet()) {
				if (key.equals("Player") && permissionList.contains(UserPermission.reports_filters_playerId)) {
					response.getPlayer().getFilters().add(key);
					response.getAffiliate().getFilters().add(key);
				} else if (key.equals("Game") && permissionList.contains(UserPermission.reports_filters_gamePlayedId)) {
					response.getSummary().getFilters().add(key);
				} else if (key.equals("Table") && permissionList.contains(UserPermission.reports_filters_tableId)) {
					response.getSummary().getFilters().add(key);
				} else if (key.equals("Tournament")
						&& permissionList.contains(UserPermission.reports_filters_tournamentId)) {
					response.getTournament().getFilters().add(key);
				} else if (key.equals("Affiliate") && permissionList.contains(UserPermission.reports_filters_affiliate)) {
					response.getAffiliate().getFilters().add(key);
				}
			}
			for (String key : reportConfig.getDateOption().keySet()) {
				response.getSummary().getDateRanges().add(key);
				response.getTournament().getDateRanges().add(key);
				response.getPlayer().getDateRanges().add(key);
				response.getAffiliate().getDateRanges().add(key);
			}
			for (String key : reportConfig.getOperation().keySet()) {
				response.getPlayer().getDateRanges().add(key);
				response.getTournament().getOperations().add(key);
				response.getAffiliate().getOperations().add(key);
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@POST
	@Path("/create")
	@ApiOperation(value = "Create", notes = "Report Create", response = GenericResponse.class, httpMethod = "post")
	public GenericResponse reportCreatePost(@ApiParam(required = true) ReportCreateRequest request,
			@ApiParam(hidden = true) Request request0, @ApiParam(hidden = true) Response response) {
		GenericResponse reportResponse = new GenericResponse();
		try {
			if (request != null
					&& Utils.isNotEmpty(request.getTitle())
					&& Utils.isNotEmpty(request.getDateType())
					&& Utils.isNotEmpty(request.getType())
					&& !request.getDimensions().isEmpty()
					&& ((request.isScheduled() && Utils.isNotEmpty(request.getFrequency())
							&& Utils.isNotEmpty(request.getEmails()) && request.getHourOfDay() != null) || !request
								.isScheduled())) {
				@SuppressWarnings("unchecked")
				Optional<UserDepartment> optUserDepartment = (Optional<UserDepartment>) request0.getExt().get(
						Constants.USER_KEY);
				UserDepartment userDepartment = optUserDepartment.get();
				ReportConfig reportConfig = InMemory.reportConfig;
				Report reportRequest = changeToReport(request, reportConfig);
				reportRequest.setAccountId(userDepartment.getUser().getDepartmentId());
				reportRequest.setUserId(userDepartment.getUser().getId());
				ReportDao.persist(reportRequest);
				if (request.isCreatNRun()) {
					reportResponse.setD(reportRequest.getId());
				}
				reportResponse.setS(true);
				reportResponse.setMsg("Report has been successfully created");
			} else {
				reportResponse.setEd("Required fields are coming invalid");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			reportResponse.setEd(e.getMessage());
		}
		return reportResponse;
	}

	@WSEPermission(pl = { UserPermission.reports_view })
	@GET
	@Path("/list")
	@ApiOperation(value = "List", notes = "Report List", response = ReportListResponse.class, httpMethod = "get")
	@ApiImplicitParams({ @ApiImplicitParam(name = "query", value = "Title", dataType = "string", paramType = "query", required = true) })
	public ReportListResponse list(Request request, Response responseO) {
		ReportListResponse response = new ReportListResponse();
		try {
			String query = request.getHeader(Urlparams.query);
			@SuppressWarnings("unchecked")
			Optional<UserDepartment> optUserDepartment = (Optional<UserDepartment>) request.getExt().get(
					Constants.USER_KEY);
			UserDepartment userDepartment = optUserDepartment.get();
			List<Report> reportList = ReportDao.list(userDepartment.getUser().getDepartmentId(), userDepartment
					.getUser().getId(), query);
			if (reportList != null && !reportList.isEmpty()) {
				changeToListResponse(reportList, response);
			} else {
				response.setMsg("No reports found");
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.reports_get })
	@GET
	@Path("/get")
	@ApiOperation(value = "Get", notes = "Get Report Details By Id", response = ReportGetResponse.class, httpMethod = "get")
	public ReportGetResponse get(Request request, Response responseO) {
		ReportGetResponse response = new ReportGetResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmptyNA(id)) {
			try {
				Report report = ReportDao.getById(id);
				if (report != null) {
					changeToReportResponse(report, response);
					response.setS(true);
				} else {
					response.setEd("Id is not valid");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id should not be empty");
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.reports_filter_search })
	@GET
	@Path("/filter/search/list")
	@ApiOperation(value = "List", notes = "Report Filter Search List", response = SearchResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "url", value = "Filter Url", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "query", value = "Filter Query", dataType = "string", paramType = "query", required = true) })
	public SearchResponse filterList(Request request, Response responseO) {
		SearchResponse response = new SearchResponse();
		try {
			String url = request.getHeader(Urlparams.url);
			String query = request.getHeader(Urlparams.query);
			if (url.equals("Player")) {
				List<Player> players = PlayerDao.search(query, null);
				if(players.isEmpty()) {
				 List<Player> playerGameNames = PlayerDao.searchGameName(query, null);
				 for (Player player : playerGameNames) {
						response.getSearchList().add(new CommonDto(player.getId(), player.getGameName()));
					}
				 }
				for (Player player : players) {
					response.getSearchList().add(new CommonDto(player.getId(), player.getUserName()));
					 if(player.getGameName()!=null) {
						 response.getSearchList().add(new CommonDto(player.getId(), player.getGameName()));}
				}
				
			} else if (url.equals("Table")) {
				List<PokerTable> tables = TableDao.search(query, null);
				for (PokerTable table : tables) {
					response.getSearchList().add(new CommonDto(table.getId(), table.getTitle()));
				}
			} else if (url.equals("Tournament")) {
				List<Tournament> tournaments = TournamentDao.search(query, null);
				for (Tournament tournament : tournaments) {
					response.getSearchList().add(new CommonDto(tournament.getId(), tournament.getTitle()));
				}
			} else if (url.equals("Affiliate")) {
				List<Affiliate> affiliates = AffiliateDao.search(query, null);
				for (Affiliate affiliate : affiliates) {
					response.getSearchList().add(new CommonDto(affiliate.getId(), affiliate.getName()));
				}
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.reports_detail_view })
	@GET
	@Path("/detail")
	@ApiOperation(value = "Get", notes = "Get Report Details By Id", response = ReportDetailResponse.class, httpMethod = "get")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "Report Id", dataType = "string", paramType = "query", required = true) })
	public ReportDetailResponse detail(Request request, Response responseO) {
		ReportDetailResponse reportResponse = new ReportDetailResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				Report report = ReportDao.getById(id);
				if (report != null) {
					buildReport(reportResponse, report);
					if (reportResponse.getRows().isEmpty()) {
						reportResponse.setMsg("No report details found");
					}
					reportResponse.setS(true);
				} else {
					reportResponse.setEd("Id is not valid");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				reportResponse.setEd(e.getMessage());
			}
		} else {
			reportResponse.setEd("Id should not be empty");
		}
		return reportResponse;
	}

	@WSEPermission(pl = { UserPermission.reports_delete })
	@GET
	@Path("/delete")
	@ApiOperation(value = "Delete", notes = "Delete Report", response = GenericResponse.class, httpMethod = "get")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "Report Id", dataType = "string", paramType = "query", required = true) })
	public GenericResponse delete(Request request, Response responseO) {
		GenericResponse response = new GenericResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				ReportDao.delete(id);
				response.setS(true);
				response.setMsg("Report has been successfully deleted");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id should not be empty");
		}
		return response;
	}

	// //Only for Developer Use
	@WSEPermission(pl = { UserPermission.reports_delete })
	@GET
	@Path("/delete/by/user")
	public GenericResponse deleteByUser(Request request, Response responseO) {
		GenericResponse response = new GenericResponse();
		String userId = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(userId)) {
			try {
				ReportDao.deleteByUserId(userId);
				response.setS(true);
				response.setMsg("Report has been successfully deleted");
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
	@ApiOperation(value = "Update", notes = "Report Update", response = GenericResponse.class, httpMethod = "post")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "Report Id", dataType = "string", paramType = "query", required = true) })
	public GenericResponse reportUpdateDoc(@ApiParam(required = true) UpdateRequest reportRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response0) {
		GenericResponse response = new GenericResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				if (reportRequest != null && reportRequest.getRequestData() != null) {
					Map<String, Object> updateData = reportRequest.getRequestData();
					Map<String, Object> updatedMap = new HashMap<String, Object>();
					boolean scheduleChanged = updateReportMap(updateData, updatedMap);
					ReportDao.updateReport(id, updatedMap);
					if (scheduleChanged) {
						Report report = ReportDao.getById(id);
						report.buildNextRunDate();
						if (report.getNextScheduleRun() != null) {
							ReportDao.updateNextRun(report);
						}
					}
					response.setS(true);
					response.setMsg("Report has been successfully updated");
				} else {
					response.setEd("Required fields are coming invalid");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id can not be empty");
		}
		return response;
	}

	@GET
	@Path("/send/user/otp")
	@ApiOperation(value = "Send User OTP", notes = "Send OTP", response = ReportOtpResponse.class, httpMethod = "get")
	public ReportOtpResponse sendOtp(@ApiParam(hidden = true) Request request,
			@ApiParam(hidden = true) Response response0) {
		ReportOtpResponse response = new ReportOtpResponse();
		if (request != null) {
			try {
				@SuppressWarnings("unchecked")
				Optional<UserDepartment> optUserDepartment = (Optional<UserDepartment>) request.getExt().get(
						Constants.USER_KEY);
				UserDepartment userDepartment = optUserDepartment.get();
				User user = UserDao.getById(userDepartment.getUser().getId());
				if (user != null) {
					String otp = com.actolap.wse.Utils.generateOTP();
					String otpResponse = LyveSendSms.setOtp(user.getMobile(), otp);
					String[] responseParts = otpResponse.split("\\|");
					String responseCode = responseParts[0];
					if (responseCode.trim().equals("success")) {
						String transactionId = responseParts[2].trim();
						OneTimePassword oneTimePassword = new OneTimePassword();
						oneTimePassword.setOtp(otp);
						oneTimePassword.setMobile(user.getMobile());
						oneTimePassword.setTransactionId(transactionId);
						oneTimePassword.setExpireDate(new Date());
						OneTimePasswordDao.persist(oneTimePassword);
						response.setD(transactionId);
						response.setMobile(user.getMobile());
						response.setMsg("We have send an OTP to your mobile " + user.getMobile());
						response.setS(true);
					}
				} else {
					response.setEd("Looks like you are not registered with us");
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
	@Path("/otp/validate")
	@ApiOperation(value = "Validate", notes = "Otp Validate", response = GenericResponse.class, httpMethod = "post")
	public GenericResponse validateOtp(@ApiParam(required = true) OTPRequest otpRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response) {
		GenericResponse otpResponse = new GenericResponse();
		try {
			if (otpRequest != null && Utils.isNotEmpty(otpRequest.getOtp())
					&& Utils.isNotEmpty(otpRequest.getTransactionId())) {
				OneTimePassword otp = OneTimePasswordDao.getByTransactionId(otpRequest.getTransactionId(),
						otpRequest.getOtp());
				if (otp != null) {
					otpResponse.setS(true);
				} else {
					otpResponse.setEd("Invalid OTP, please enter a valid OTP.");
				}
			} else {
				otpResponse.setEd("Required fields are coming invalid");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			otpResponse.setEd(e.getMessage());
		}
		return otpResponse;
	}

	@SuppressWarnings("unchecked")
	private boolean updateReportMap(Map<String, Object> updateData, Map<String, Object> reportMap) {
		boolean scheduleChanged = false;
		ReportConfig reportConfig = InMemory.reportConfig;
		for (String key : updateData.keySet()) {
			if (key.equals("dimensions")) {
				List<String> dimentionList = (List<String>) updateData.get(key);
				List<Dimensions> sortDimensions = new ArrayList<Dimensions>();
				Set<Dimensions> dimensions = new HashSet<Dimensions>();
				if (!dimentionList.isEmpty()) {
					for (String dimension : dimentionList) {
						Dimensions dimension2 = reportConfig.getDimentions().get(dimension);
						dimensions.add(dimension2);
						sortDimensions.add(dimension2);
					}
					reportMap.put(key, dimensions);
					reportMap.put("sortedDimensions", sortDimensions);
				}
			} else if (key.equals("measures")) {
				List<String> measureList = (List<String>) updateData.get(key);
				List<Metric> sortMeasures = new ArrayList<Metric>();
				Set<Metric> measures = new HashSet<Metric>();
					for (String measure : measureList) {
						Metric measureObj = Metric.valueOf(reportConfig.getMeasures().get(measure).name());
						measures.add(measureObj);
						sortMeasures.add(measureObj);
					}
					reportMap.put(key, measures);
					reportMap.put("sortedMeasures", sortMeasures);
			} else if (key.equals("dateType")) {
				DATERANGE dateRange = reportConfig.getDateOption().get(updateData.get(key));
				if (updateData.get("dateType").equals("CUSTOM")) {
					reportMap.put("dateFilter", updateData.get("dateFilter"));
				}
				reportMap.put(key, dateRange);
			} else if (key.equals("conditions")) {
				Object object = updateData.get(key);
				String jsonString = JSON.serialize(object);
				@SuppressWarnings("serial")
				ArrayList<ReportConditionDto> reportConditionList = gson.fromJson(jsonString,
						new TypeToken<ArrayList<ReportConditionDto>>() {
						}.getType());
				if (reportConditionList != null) {
					reportMap.put(key, reportConditionToCondition(reportConditionList, reportConfig));
				}

			} else if (key.equals("schedule")) {
				reportMap.put("scheduled", updateData.get("schedule"));
				scheduleChanged = true;
			} else if (key.equals("recipientEmail")) {
				String emails = (String) updateData.get("recipientEmail");
				reportMap.put("emails", parseEmailString(emails));
			} else if (key.equals("frequency")) {
				String freq = (String) updateData.get("frequency");
				reportMap.put("frequency", FREQUENCY.valueOf(freq));
				scheduleChanged = true;
			} else {
				reportMap.put(key, updateData.get(key));
			}
		}
		return scheduleChanged;
	}

	private List<Condition> reportConditionToCondition(List<ReportConditionDto> reportConditionList,
			ReportConfig reportConfig) {
		List<Condition> conditions = new ArrayList<Condition>();
		for (ReportConditionDto reportCondition : reportConditionList) {
			Condition condition = new Condition();
			condition.setDimension(Dimensions.valueOf(reportConfig.getDimentions().get(reportCondition.getDimension())
					+ ""));
			condition.setValue(reportCondition.getValue());
			condition.setOperation(Operation.valueOf(reportConfig.getOperation().get(reportCondition.getOperation())
					+ ""));
			conditions.add(condition);
		}
		return conditions;
	}

	private Report changeToReport(ReportCreateRequest reportRequest, ReportConfig reportConig) {
		Set<ReportConditionDto> conditionsList = reportRequest.getConditions();
		Report report = new Report();

		Set<String> measureList = new HashSet<String>();
		Set<String> dimensionList = new HashSet<String>();
		reportRequest.getMeasures().forEach((measure) -> {
			measureList.add(measure);
			report.getSortedMeasures().add(InMemory.reportConfig.getMeasures().get(measure));
		});
		reportRequest.getDimensions().forEach((dimension) -> {
			dimensionList.add(dimension);
			report.getSortedDimensions().add(InMemory.reportConfig.getDimentions().get(dimension));
		});
		Set<Metric> measures = new HashSet<Metric>();
		Set<Dimensions> dimensions = new HashSet<Dimensions>();
		for (String measure : measureList) {
			measures.add(reportConig.getMeasures().get(measure));
		}
		for (String dimention : dimensionList) {
			dimensions.add(reportConig.getDimentions().get(dimention));
		}
		Set<Condition> conditions = new HashSet<Condition>();
		for (ReportConditionDto reportCondition : conditionsList) {
			Condition condition = new Condition();
			condition.setDimension(reportConig.getDimentions().get(reportCondition.getDimension()));
			condition.setValue(reportCondition.getValue());
			condition.setOperation(reportConig.getOperation().get(reportCondition.getOperation()));
			conditions.add(condition);
		}
		report.setDateType(reportConig.getDateOption().get(reportRequest.getDateType()));
		if (reportConig.getDateOption().get(reportRequest.getDateType()) == DATERANGE.custom) {
			report.setDateFilter(reportRequest.getDateFilter());
		}
		if (reportRequest.isScheduled()) {
			report.setScheduled(reportRequest.isScheduled());
			report.setFrequency(FREQUENCY.valueOf(reportRequest.getFrequency()));
			String[] emails = parseEmailString(reportRequest.getEmails());
			if (emails.length > 0)
				report.setEmails(Arrays.asList(emails));

		}
		report.setTitle(reportRequest.getTitle());
		ReportType type = ReportType.valueOf(reportRequest.getType());
		if (type != null)
			report.setType(type);
		report.setMeasures(measures);
		report.setDimensions(dimensions);
		report.setConditions(conditions);
		report.setHourOfDay(reportRequest.getHourOfDay());
		report.buildNextRunDate();
		return report;
	}

	private String[] parseEmailString(String emails) {
		return emails.split(",");
	}

	private void changeToListResponse(List<Report> reportList, ReportListResponse listResponse) {
		for (Report report : reportList) {
			ReportDto reportDto = new ReportDto();
			if (report.getDateType() == DATERANGE.custom) {
				String type = report.getDateFilter().getStartDate() + " to " + report.getDateFilter().getEndDate();
				reportDto.setDateType(type);
			} else {
				reportDto.setDateType(InMemory.dateRangeMap.get(report.getDateType()));
			}
			reportDto.setId(report.getId());
			reportDto.setTitle(report.getTitle());
			if (report.getLastCompiled() != null)
				reportDto.setLastCompiled(report.getLastCompiled().getTime());
			listResponse.getReportList().add(reportDto);
		}
	}

	private void changeToReportResponse(Report report, ReportGetResponse reportResponse) {
		reportResponse.setTitle(report.getTitle());
		if (report.getType() != null)
			reportResponse.setType(report.getType().toString());
		reportResponse.setDateType(com.actolap.wse.Utils.getDateRange(report.getDateType()));
		for (Condition condition : report.getConditions()) {
			ReportConditionDto reportConditionDto = new ReportConditionDto();
			reportConditionDto.setDimension(InMemory.dimMap.get(condition.getDimension()));
			reportConditionDto.setOperation(condition.getOperation().name().toUpperCase());
			@SuppressWarnings("unchecked")
			List<String> valueList = (ArrayList<String>) condition.getValue();
			if (condition.getDimension() != null) {
				if (condition.getDimension().equals(Dimensions.playerId)) {
					List<Player> playerList = PlayerDao.getByIds(valueList);
					if (playerList != null) {
						for (Player player : playerList) {
							reportConditionDto.getSelectedFilters().add(
									new CommonDto(player.getId(), player.getUserName()));
						}
					}
				} else if (condition.getDimension().equals(Dimensions.tableId)) {
					List<PokerTable> tableList = TableDao.getByIds(valueList);
					if (tableList != null) {
						for (PokerTable table : tableList) {
							reportConditionDto.getSelectedFilters().add(new CommonDto(table.getId(), table.getTitle()));
						}
					}

				} else if (condition.getDimension().equals(Dimensions.tournament)) {
					List<Tournament> tournamentList = TournamentDao.getByIds(valueList);
					if (tournamentList != null) {
						for (Tournament tournament : tournamentList) {
							reportConditionDto.getSelectedFilters().add(
									new CommonDto(tournament.getId(), tournament.getTitle()));
						}
					}

				} else if (condition.getDimension().equals(Dimensions.affiliateId)) {
					List<Affiliate> affiliateList = AffiliateDao.getByIds(valueList);
					if (affiliateList != null) {
						for (Affiliate affiliate : affiliateList) {
							reportConditionDto.getSelectedFilters().add(
									new CommonDto(affiliate.getId(), affiliate.getName()));
						}
					}

				}
			}
			reportConditionDto.setValue(valueList);
			reportResponse.getConditions().add(reportConditionDto);
		}
		List<String> dimensionSet = new ArrayList<String>();
		for (Dimensions dimension : report.getSortedDimensions()) {
			dimensionSet.add(InMemory.dimMap.get(dimension));
		}
		List<String> measureSet = new ArrayList<String>();
		for (Metric measure : report.getSortedMeasures()) {
			measureSet.add(InMemory.measureMap.get(measure));
		}
		reportResponse.setHourOfDay(report.getHourOfDay());
		reportResponse.setDimensions(dimensionSet);
		reportResponse.setMeasures(measureSet);
		reportResponse.setScheduled(report.isScheduled());
		if (report.isScheduled()) {
			reportResponse.setFrequency(report.getFrequency().name());
			reportResponse.setEmails(parseEmailList(report.getEmails()));
		}
		if (report.getDateFilter() != null) {
			reportResponse.setDateFilter(report.getDateFilter());
		}
	}

	private String parseEmailList(List<String> emails) {
		Joiner joiner = Joiner.on(", ").skipNulls();
		String emailString = joiner.join(emails);
		return emailString;
	} 

	@SuppressWarnings({ "unchecked"})
	public static void buildReport(ReportDetailResponse reportResponse, Report report) throws ParseException {
		if (report != null) {
			List<String> tournamentIds = new ArrayList<String>();
			reportResponse.setTitle(report.getTitle());
			List<String> col = new ArrayList<String>();
			if (!(report.getDateType() == DATERANGE.custom)) {
				getDateRange(report);
			}
			ListResponse response = new ListResponse();
			ReportRequest reportRequest = new ReportRequest();
			if (report.getType() != null) {
				if (report.getType().equals(ReportType.SUMMARY)) {
					buildRequest(report, reportRequest);
					reportRequest.getDimensions().add(Dimensions.playerId);
					ReportingResponse reportResponce = ReportService.generateReport(reportRequest);
					int i = 0;
					for (ResponseItem responseItem : reportResponce.getResponseItems()) {
						List<Object> values = new ArrayList<Object>();
						Player player = null;
						for (Dimensions key : report.getSortedDimensions()) {
							if (i == 0) {
								col.add(ReportController.getDimensionLabel(key));
							}
							if (key.equals(Dimensions.playerId))
								values.add(PlayerCache.getPlayerTitle(responseItem.getColumns().get(key).toString()));
							else {
								Object obj = responseItem.getColumns().get(key);
								if (obj != null) {
									values.add(obj);
								} else {
									if (report.getSortedDimensions().contains(Dimensions.gameName)) {
											if (isVirtualPlayerDimension(key)) {
												if (player == null)
													player = PlayerCache.getPlayer(responseItem.getColumns()
															.get(Dimensions.playerId).toString());
												if (player != null) {
													values.add(getDimensionLabel(key, player));
												} else {
													values.add("Deleted");
												}
										}
									}
								}
							}
						}
						for (Metric key : report.getSortedMeasures()) {
							if (i == 0) {
								col.add(ReportController.getMeasureLabel(key));
							}
							if (key.equals(Metric.rakeGenerated)) {
								values.add(com.actolap.wse.Utils.df.format(responseItem.getMeasures().get(key)));
							} else {
								values.add(responseItem.getMeasures().get(key));
							}
						}
						reportResponse.getRows().add(values);
						i++;
					}
					reportResponse.setColumns(col);
				} else if (report.getType().equals(ReportType.PLAYER)) {
					buildRequest(report, reportRequest);
					reportRequest.getDimensions().add(Dimensions.playerId);
					ReportingResponse reportResponce = ReportService.generateReport(reportRequest);
					int i = 0;
					for (ResponseItem responseItem : reportResponce.getResponseItems()) {
						List<Object> values = new ArrayList<Object>();
						Player player = null;
						for (Dimensions dimension : report.getSortedDimensions()) {
							if (isVirtualPlayerDimension(dimension)) {
								if (player == null)
									player = PlayerCache.getPlayer(responseItem.getColumns().get(Dimensions.playerId)
											.toString());
								if (player != null) {
									values.add(getDimensionLabel(dimension, player));
								} else {
									values.add("Deleted");
								}
							} else {
								if (!dimension.equals(Dimensions.playerId)) {
									Object object = responseItem.getColumns().get(dimension);
									if (object != null)
										values.add(object);
									else
										values.add("NA");
								}
							}
							if (i == 0 && !dimension.equals(Dimensions.playerId))
								col.add(ReportController.getDimensionLabel(dimension));
							if (i == 0 && dimension.equals(Dimensions.playerId))
								col.add(ReportController.getDimensionLabel(dimension));
						}
						for (Metric key : report.getSortedMeasures()) {
							if (i == 0) {
								col.add(ReportController.getMeasureLabel(key));
							}
							if (key.equals(Metric.playedTime)) {
								long playTimeInMs = (long) responseItem.getMeasures().get(key);
								values.add(Utils.convertSecondsToHMmSs(playTimeInMs));
							} else if (key.equals(Metric.rakeGenerated)) {
								values.add(com.actolap.wse.Utils.df.format(responseItem.getMeasures().get(key)));
							} else {
								values.add(responseItem.getMeasures().get(key));
							}
						}
						reportResponse.getRows().add(values);
						i++;
					}
					reportResponse.setColumns(col);
				} else if (report.getType().equals(ReportType.AFFILIATE)) { 
					buildRequest(report, reportRequest);
					reportRequest.getDimensions().add(Dimensions.affiliateId);
					for (Dimensions dimension : reportRequest.getDimensions()) {
						if (isVirtualPlayerDimension(dimension)) {
							reportRequest.getDimensions().add(Dimensions.playerId);
							break;
						}
					}
					ReportingResponse reportResponce = ReportService.generateReport(reportRequest);
					int i = 0;
					for (ResponseItem responseItem : reportResponce.getResponseItems()) {
						List<Object> values = new ArrayList<Object>();
						Player player = null;
						Affiliate affiliate = null;
						for (Dimensions dimension : report.getSortedDimensions()) {
							if (isVirtualPlayerDimension(dimension)) {
								if (player == null && reportRequest.getDimensions().contains(Dimensions.playerId))
									player = PlayerCache.getPlayer(responseItem.getColumns().get(Dimensions.playerId)
											.toString());
								if (player != null) {
									values.add(getDimensionLabel(dimension, player));
								} else {
									values.add("Deleted");
								}
							} else if (isVirtualAffiliateDimension(dimension)) {
								if (affiliate == null)
									affiliate = AffiliateCache.getAffiliate(responseItem.getColumns()
											.get(Dimensions.affiliateId).toString());
								if (affiliate != null) {
									values.add(getAffiliateDimensionLabel(dimension, affiliate));
								} else {
									values.add("Deleted");
								}
							} else {
								if (!dimension.equals(Dimensions.affiliateId)) {
									Object object = responseItem.getColumns().get(dimension);
									if (object != null)
										values.add(object);
									else
										values.add("NA");
								}
							}
							if (i == 0 && !dimension.equals(Dimensions.playerId)
									&& !dimension.equals(Dimensions.affiliateId))
								col.add(ReportController.getDimensionLabel(dimension));
						}
						for (Metric key : report.getSortedMeasures()) {
							if (i == 0) {
								col.add(ReportController.getMeasureLabel(key));
							} 
							if (key.equals(Metric.playedTime)) {
								long playTimeInMs = (long) responseItem.getMeasures().get(key);
								values.add(Utils.convertSecondsToHMmSs(playTimeInMs));
							} else if (key.equals(Metric.rakeGenerated)) {
								values.add(com.actolap.wse.Utils.df.format(responseItem.getMeasures().get(key)));
							} else if (key.equals(Metric.commission)) {
								values.add(com.actolap.wse.Utils.df.format(responseItem.getMeasures().get(key)));
							} else {
								values.add(responseItem.getMeasures().get(key));
							}
						}
						reportResponse.getRows().add(values);
						i++;
					}
					reportResponse.setColumns(col);
				} else if (report.getType().equals(ReportType.TOURNAMENT)) {

					if (report.getDateType().equals(DATERANGE.custom)) {
						DateCondition dateFilter = new DateCondition(report.getDateFilter().getStartDate(), report
								.getDateFilter().getEndDate());
						reportRequest.setDateFilter(dateFilter);
					} else {
						com.actolap.wse.Utils.getDateRange(reportRequest, report.getDateType().toString());
					}

					for (Condition condition : report.getConditions()) {
						tournamentIds = (List<String>) condition.getValue();
					}
					try {
						response = TournamentDao.getTournamentReport(tournamentIds,
								sdf.parse(report.getDateFilter().getStartDate()),
								sdf.parse(report.getDateFilter().getEndDate()));
						reportResponse.setColumns(col);
						int i = 0;
						for (Object obj : response.getData()) {
							List<Object> values = new ArrayList<Object>();
							Tournament tournament = (Tournament) obj;
							for (Dimensions dimension : report.getSortedDimensions()) {
								buildDimesionResponse(dimension, col, i, values, tournament);
							}
							for (Metric metric : report.getSortedMeasures()) {
								buildMetricResponse(metric, col, i, values, tournament);
							}
							reportResponse.getRows().add(values);
							i++;
						}
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}
			}
			if (col.isEmpty()) {
				ResponseItem respItem = new ResponseItem();
				Map<Dimensions, Object> columnsResponse = respItem.getColumns();
				report.getDimensions().remove(Dimensions.playerId);
				for (Dimensions dimension : report.getSortedDimensions()) {
					columnsResponse.put(dimension, InMemory.dimMap.get(dimension));
				}
				Map<Metric, Object> measures = respItem.getMeasures();

				for (Metric measure : report.getSortedMeasures()) {
					measures.put(measure, InMemory.measureMap.get(measure));
				}

				if (col != null) {
					for (Dimensions colKey : columnsResponse.keySet()) {
						Dimensions dimension = colKey;
						col.add(InMemory.dimMap.get(dimension));
					}
					for (Metric measure : measures.keySet()) {
						col.add(InMemory.measureMap.get(measure));
					}
				}
				reportResponse.setColumns(col);
			}
			ReportDao.updateForLastRun(report.getId());
			reportResponse.setRc(ResponseCode.SUCCESS);

		} else {
			reportResponse.setRc(ResponseCode.FAILED);
			reportResponse.setEd("data specified by you is not correct");
		}
	}

	private static void buildRequest(Report report, ReportRequest reportRequest) {
		reportRequest.setConditions(report.getConditions());
		reportRequest.setReportType(report.getType());
		reportRequest.setMeasures(report.getMeasures());
		reportRequest.setDimensions(report.getDimensions());
		if (report.getDateType().equals(DATERANGE.custom)) {
			DateCondition dateFilter = new DateCondition(report.getDateFilter().getStartDate(), report.getDateFilter()
					.getEndDate());
			reportRequest.setDateFilter(dateFilter);
		} else {
			com.actolap.wse.Utils.getDateRange(reportRequest, report.getDateType().toString());
		}
	}

	private static void buildDimesionResponse(Dimensions dimesion, List<String> col, int i, List<Object> values,
			Tournament tournament) {

		switch (dimesion) {
		case date:
			if (i == 0)
				col.add("Date");
			values.add(sdf_trip.format(tournament.getStartDate()));
			break;
		case tournament:
			if (i == 0)
				col.add("Tournament");
			values.add(tournament.getTitle());
			break;
		default:
			break;

		}
	}

	private static void buildMetricResponse(Metric metric, List<String> col, int i, List<Object> values,
			Tournament tournament) {

		switch (metric) {
		case collections:
			if (i == 0)
				col.add("Collections");
			values.add(tournament.getCollection());
			break;
		case players:
			if (i == 0)
				col.add("Players");
			values.add(tournament.getPlayers());
			break;
		case priceMoney:
			if (i == 0)
				col.add("Price Money");
			values.add(tournament.getPriceMoney());
			break;
		case enrolled:
			if (i == 0)
				col.add("Enrolled");
			values.add(tournament.getPlayers());
			break;
		default:
			break; 

		}
	}

	private static boolean isVirtualPlayerDimension(Dimensions dimension) {
		if (dimension.equals(Dimensions.firstName) || dimension.equals(Dimensions.lastName) || dimension.equals(Dimensions.gameName)
				|| dimension.equals(Dimensions.gender) || dimension.equals(Dimensions.state)
				|| dimension.equals(Dimensions.city) || dimension.equals(Dimensions.email)
				|| dimension.equals(Dimensions.mobile) || dimension.equals(Dimensions.playerStatus)
				|| dimension.equals(Dimensions.lastLogin) || dimension.equals(Dimensions.netFinancialStatus)
				|| dimension.equals(Dimensions.cashChipsInHand) || dimension.equals(Dimensions.vipPointsInHand) || dimension.equals(Dimensions.playerId)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isVirtualAffiliateDimension(Dimensions dimension) {
		if (dimension.equals(Dimensions.affiliateFirstName) || dimension.equals(Dimensions.affiliateLastName)
				|| dimension.equals(Dimensions.affiliateEmail)) {
			return true;
		} else {
			return false;
		}
	}

	private static void getDateRange(Report report) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String endDate = dateFormat.format(cal.getTime());
		switch (report.getDateType()) {
		case today:
			break;
		case yesterday:
			cal.add(Calendar.DATE, -1);
			break;
		case last7days:
			cal.add(Calendar.DATE, -7);
			break;
		case last14days:
			cal.add(Calendar.DATE, -14);
			break;
		case month2date:
			cal.set(Calendar.DAY_OF_MONTH, 1);
			break;
		case lastmonth:
			cal.add(Calendar.MONTH, -1);
			break;
		default:
			break;
		}
		String startDate = dateFormat.format(cal.getTime());
		if (report.getDateType() == DATERANGE.lastmonth) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
			startDate = dateFormat.format(cal.getTime());
			Calendar calender = Calendar.getInstance();
			calender.add(Calendar.MONTH, -1);
			calender.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endDate = dateFormat.format(calender.getTime());

		}
		LOG.info(startDate + "  to" + endDate);
		DateCondition dateCondition = new DateCondition(startDate, endDate);
		report.setDateFilter(dateCondition);
	}

	@SuppressWarnings("incomplete-switch")
	public static String getDimensionLabel(Dimensions dimension) {
		String label = null;
		switch (dimension) {
		case date:
			label = "Date";
			break;
		case gameName:
			label = "Game Name";
			break;
		case playerId:
		     label = "Player Id";
		     break;
		case tableId:
			label = "Table";
			break;
		case gameplayId:
			label = "Game";
			break;
		case firstName:
			label = "First Name";
			break;
		case lastName:
			label = "Last Name";
			break;
		case gender:
			label = "Gender";
			break;
		case state:
			label = "State";
			break;
		case city:
			label = "City";
			break;
		case email:
			label = "Email";
			break;
		case mobile:
			label = "Mobile";
			break;
		case playerStatus:
			label = "Status";
			break;
		case lastLogin:
			label = "Last Login";
			break;
		case netFinancialStatus:
			label = "Net Financial Status";
			break;
		case cashChipsInHand:
			label = "Cash Chips In Hand";
			break;
		case vipPointsInHand:
			label = "VIP Points In Hand";
			break;
		case gameType:
			label = "Game Type";
			break;
		case affiliateFirstName:
			label = "Affiliate First Name";
			break;
		case affiliateLastName:
			label = "Affiliate Last Name";
			break;
		case affiliateEmail:
			label = "Affiliate Email";
			break;
		}

		return label;
	}

	public static String getMeasureLabel(Metric measure) {
		String label = null;
		switch (measure) {
		case gamePlayed:
			label = "Game Played";
			break;
		case priceMoney:
			label = "Price Money";
			break;
		case enrolled:
			label = "Enrolled";
			break;
		case players:
			label = "Players";
			break;
		case collections:
			label = "Collections";
			break;
		case bonusIssued:
			label = "Bonus Issued";
			break;
		case bonusReleased:
			label = "Bonus Released";
			break;
		case vipPointsIssued:
			label = "Vip Points Issued";
			break;
		case moneyDeposit:
			label = "Money Deposit";
			break;
		case signUp:
			label = "Sign Up";
			break;
		case tournamentsParticipated:
			label = "Tournaments Participated";
			break;
		case tournamentLost:
			label = "Tournament Lost";
			break;
		case tournamentWon:
			label = "Tournament Won";
			break;
		case rakeRefunded:
			label = "Rake Refunded";
			break;
		case tdsRefunded:
			label = "TDS Refunded";
			break;
		case tournamentColl:
			label = "Tournament Spending";
			break;
		case discountReceived:
			label = "Discount Received";
			break;
		case wagered:
			label = "Chip Wagered";
			break;
		case net:
			label = "Net Profit";
			break;
		case won:
			label = "Chips Won";
			break;
		case bonusChipsEncashed:
			label = "Bonus Chips Encashed";
			break;
		case vipPointsEncashed:
			label = "Vip Points Encashed";
			break;
		case gamesWon:
			label = "Games Won";
			break;
		case gamesLost:
			label = "Games Lost";
			break;
		case moneyDrawn:
			label = "Money Drawn";
			break;
		case withdrawCharge:
			label = "Money Drawn Charge";
			break;
		case rakeGenerated:
			label = "Rake Generated";
			break;
		case playedTime:
			label = "Played Time";
			break;
		case commission:
			label = "Commission";
			break;
		case vipPointsDeducted:
			label = "Vip Point Deducted";
			break;
		case tdsDeducted:
			label = "Tds Deducted";
			break;

		default:
			break;

		}
		return label;
	}

	@SuppressWarnings("incomplete-switch")
	public static String getDimensionLabel(Dimensions dimension, Player player) {
		String label = "NA";
		switch (dimension) {
		case firstName:
			if (player.getProfile() != null && Utils.isNotEmpty(player.getProfile().getFirstName()))
				label = player.getProfile().getFirstName();
			break;
		case lastName:
			if (player.getProfile() != null && Utils.isNotEmpty(player.getProfile().getLastName()))
				label = player.getProfile().getLastName();
			break;
		case gameName:
			if (player.getProfile() != null && Utils.isNotEmpty(player.getGameName()))
				label = player.getGameName();
			break;
		case playerId:
			if (player.getProfile() != null && Utils.isNotEmpty(player.getId()))
				label = player.getId();
			break;
		case gender:
			if (player.getProfile() != null && player.getProfile().getGender() != null)
				label = player.getProfile().getGender().toString();
			break;
		case state:
			if (player.getProfile() != null && player.getProfile().getAddress() != null
					&& player.getProfile().getAddress().getState() != null)
				label = player.getProfile().getAddress().getState();
			break;
		case city:
			if (player.getProfile() != null && player.getProfile().getAddress() != null
					&& player.getProfile().getAddress().getCity() != null)
				label = player.getProfile().getAddress().getCity();
			break;
		case email:
			label = player.getEmail();
			break;
		case mobile:
			if (player.getMobile() != null)
				label = player.getMobile().toString();
			break;
		case playerStatus:
			if (player.getStatus() != null)
				label = player.getStatus().toString();
			break;
		case lastLogin:
			if (player.getLastLogin() != null && player.getLastLogin().getTime() != null)
				label = com.actolap.wse.Utils.getDatedifference(player.getLastLogin().getTime());
			break;
		case netFinancialStatus:
			label = "NA";
			break;
		case cashChipsInHand:
			if (player.getWallet() != null)
				label = Long.valueOf(player.getWallet().getCash()).toString();
			break;
		case vipPointsInHand:
			if (player.getWallet() != null)
				label = Long.valueOf(player.getWallet().getVip()).toString();
			break;
		case gameType:
			label = "NA";
			break;
		}
		return label;
	}

	@SuppressWarnings("incomplete-switch")
	public static String getAffiliateDimensionLabel(Dimensions dimension, Affiliate affiliate) {
		String label = "NA";
		switch (dimension) {
		case affiliateFirstName:
			if (affiliate.getProfile() != null && Utils.isNotEmpty(affiliate.getProfile().getFirstName()))
				label = affiliate.getProfile().getFirstName();
			break;
		case affiliateLastName:
			if (affiliate.getProfile() != null && Utils.isNotEmpty(affiliate.getProfile().getLastName()))
				label = affiliate.getProfile().getLastName();

			break;
		case affiliateEmail:
			label = affiliate.getEmail();
			break;
		}
		return label;
	}

}

