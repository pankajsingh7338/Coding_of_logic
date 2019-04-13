package com.actolap.wse.affiliate.fe.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actolap.wse.common.dashboard.reporting.ReportService;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.constants.Urlparams;
import com.actolap.wse.dto.DashboardAffiliatePlayerDto;
import com.actolap.wse.enums.ResponseCode;
import com.actolap.wse.model.report.Condition;
import com.actolap.wse.model.report.DateCondition;
import com.actolap.wse.model.report.Dimensions;
import com.actolap.wse.model.report.Metric;
import com.actolap.wse.model.report.Operation;
import com.actolap.wse.model.report.ReportRequest;
import com.actolap.wse.model.report.ReportType;
import com.actolap.wse.model.report.ReportingResponse;
import com.actolap.wse.response.AffiliateUsersResponse;

@Path("/affiliate/public/dashboard")
@Api(value = "Rest API")
public class AffiliatePublicDashboardController {

	private static final Logger LOG = LoggerFactory.getLogger(AffiliatePublicDashboardController.class);

	public ReportingResponse summary(Request request, Response response) {
		return summaryGenerateDoc(request.getBodyAs(ReportRequest.class), request, response);
	}

	@POST
	@Path("/summary")
	@ApiOperation(value = "Create", notes = "Affiliate Dashboard Summary", response = ReportingResponse.class, httpMethod = "post")
	public ReportingResponse summaryGenerateDoc(@ApiParam(required = true) ReportRequest reportRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response) {
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

	@GET 
	@Path("/users") 
	@ApiOperation(value = "List", notes = "Affiliate User List", response = AffiliateUsersResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Affiliate Id", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "startDate", value = "Start Date", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "endDate", value = "End Date", dataType = "string", paramType = "query", required = true) })
	public AffiliateUsersResponse list(Request request, Response responseO) { 
		AffiliateUsersResponse response = new AffiliateUsersResponse(); 
		String AffiliateId = request.getHeader(Urlparams.id); 
		if (Utils.isNotEmpty(AffiliateId)) { 
			try { 
				String startDate = request.getHeader(Urlparams.startDate);
				String endDate = request.getHeader(Urlparams.endDate);
				ReportRequest reportRequest = new ReportRequest(); 
				Condition condition = new Condition(); 
				condition.setDimension(Dimensions.affiliateId); 
				condition.setOperation(Operation.equal); 
				condition.setValue(AffiliateId); 
				reportRequest.getConditions().add(condition); 
				reportRequest.setReportType(ReportType.AFFILIATE); 
				reportRequest.getMeasures().add(Metric.totalSpent); 
				reportRequest.getMeasures().add(Metric.wagered); 
				reportRequest.getMeasures().add(Metric.tournamentColl); 
				reportRequest.getMeasures().add(Metric.commission); 
				reportRequest.getMeasures().add(Metric.rakeGenerated); 
				reportRequest.getDimensions().add(Dimensions.playerId); 
				if (Utils.isNotEmpty(startDate) && Utils.isNotEmpty(endDate)) 
					reportRequest.setDateFilter(new DateCondition(startDate, endDate)); 
				ReportingResponse dashboardResponse = ReportService.generateReport(reportRequest); 
				if (!dashboardResponse.getResponseItems().isEmpty()) { 
					dashboardResponse.getResponseItems().forEach(responseItem -> { 
						DashboardAffiliatePlayerDto userDto = new DashboardAffiliatePlayerDto(responseItem);
						response.getAffiliatePlayers().add(userDto); 
					}); 
				} else { 
					response.setMsg("No user found"); 
				} 
				response.setS(true); 
			} catch (Exception e) { 
				LOG.error(e.getMessage(), e); 
				response.setEd(e.getMessage()); 
			}
		} else {
			response.setEd("Affiliate id can not blank");
		}
		return response;
	}

}

