package com.actolap.wse.backoffice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actolap.wse.UserPermission;
import com.actolap.wse.common.dashboard.reporting.ReportService;
import com.actolap.wse.config.InMemory;
import com.actolap.wse.dao.CouponDao;
import com.actolap.wse.dao.TableDao;
import com.actolap.wse.dao.TournamentDao;
import com.actolap.wse.dto.DashboardTournamentDto;
import com.actolap.wse.enums.ResponseCode;
import com.actolap.wse.model.game.poker.Tournament;
import com.actolap.wse.model.game.poker.TournamentStatus;
import com.actolap.wse.model.report.ReportRequest;
import com.actolap.wse.model.report.ReportingResponse;
import com.actolap.wse.response.DashboardTournamentsResponse;
import com.actolap.wse.response.DashboardWidgetResponse;
import com.actolap.wse.rest.secuirty.SecureAnnotation.UserSecure;
import com.actolap.wse.rest.secuirty.SecureAnnotation.WSEPermission;

@Path("/dashboard")
@Api(value = "Rest API")
@UserSecure
public class DashboardController {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

	@WSEPermission(pl = { UserPermission.dashboard_summary })
	public ReportingResponse summary(Request request, Response response) {
		return summaryGenerateDoc(request.getBodyAs(ReportRequest.class), request, response);
	}

	@POST
	@Path("/summary")
	@ApiOperation(value = "Create", notes = "Dashboard Summary", response = ReportingResponse.class, httpMethod = "post")
	public ReportingResponse summaryGenerateDoc(@ApiParam(required = true) ReportRequest reportRequest, @ApiParam(hidden = true) Request request,
			@ApiParam(hidden = true) Response response) {
		ReportingResponse dashboardResponse = new ReportingResponse();
		if (reportRequest != null && reportRequest.getDateFilter() != null) {
			try {
				dashboardResponse = ReportService.generateReport(reportRequest);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				dashboardResponse.setEd(e.getMessage());
			}
		} else {
			dashboardResponse.setRc(ResponseCode.FAILED);
			dashboardResponse.setEd("Post data is not coming as per documentation");
		}
		return dashboardResponse;
	}

	@WSEPermission(pl = { UserPermission.dashboard_widget })
	@GET
	@Path("/widgets")
	@ApiOperation(value = "Get", notes = "Get Dasboard Widgets Detail", response = DashboardWidgetResponse.class, httpMethod = "get")
	public DashboardWidgetResponse widgets(Request request, Response responseO) {
		DashboardWidgetResponse response = null;
		try {
			long liveTournament = TournamentDao.getTotalByStatus(TournamentStatus.LIVE);
			long liveCoupon = CouponDao.getLiveCoupon();
			long activeTables = TableDao.getActiveTables();
			long onlinePlayers = InMemory.globalStatsDto.getPlayers();
			response = new DashboardWidgetResponse(liveTournament, liveCoupon, activeTables, onlinePlayers);
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.dashboard_tournament })
	@GET
	@Path("/tournament/widgets")
	@ApiOperation(value = "List", notes = "Live Tournament List", response = DashboardTournamentsResponse.class, httpMethod = "get")
	public DashboardTournamentsResponse liveTournaments(Request request, Response responseO) {
		DashboardTournamentsResponse response = new DashboardTournamentsResponse();
		try {
			List<Tournament> tournamentList = TournamentDao.list(TournamentStatus.LIVE, null, null, null);
			if (tournamentList != null && !tournamentList.isEmpty()) {
				for (Tournament tournament : tournamentList) {
					DashboardTournamentDto tournamentObj = new DashboardTournamentDto(tournament);
					response.getLiveTournaments().add(tournamentObj);
				}
			} else {
				response.setMsg("No tournaments found");
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}
}

