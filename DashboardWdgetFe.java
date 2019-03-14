package com.actolap.wsegame.angularController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.actolao.wsegmaes.api.ApiMeta;
import com.actolap.wsegame.analytics.request.model.DashboardUtils;
import com.actolap.wsegame.analytics.request.model.DataModel;
import com.actolap.wsegame.chart.ChartModel;
import com.actolap.wsegame.common.Constants;
import com.actolap.wsegame.common.FeUtils;
import com.actolap.wsegame.interceptor.SessionWrapper;
import com.actolap.wsegame.model.DashboardTournamentWidgetsModel;
import com.actolap.wsegame.model.DifferentEntityModel;
import com.actolap.wsegame.reporting.response.DashboardResponse;
import com.actolap.wsegame.reporting.response.DifferentEntity;
import com.actolap.wsegames.response.DashBoardSummaryResponse;
import com.actolap.wsegames.response.DashboardLabel;
import com.actolap.wsegames.response.DashboardLiveWidgetsResponse;
import com.actolap.wsegames.response.DashboardTournamentWidgetsResponse;
import com.actolap.wsegames.response.ObjectListResponse;
import com.actolap.wsegames.response.ObjectResponse;
import com.google.gson.Gson;

@SessionAttributes({ "session" })
@Controller
public class DashboardController {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(DashboardController.class);
	Gson gson = new Gson();

	@RequestMapping(value = "ajx/dashboard/widgets/summary", method = RequestMethod.POST)
	@ResponseBody
	public DashBoardSummaryResponse summary(@RequestBody DataModel data, HttpServletResponse response, HttpServletRequest request, SessionStatus status,
			@ModelAttribute("session") SessionWrapper session) {
		DashBoardSummaryResponse feResponse = new DashBoardSummaryResponse();
		if (data != null) {
			try {
				ApiMeta apiMeta = FeUtils.createAPIMeta(session, request);
				// DashboardConfigResponse dashboardConfig = ApiManager
				// .getDashbardConfig(apiMeta);
				// if (dashboardConfig != null) {
				// if (dashboardConfig.isS()) {
				DashboardResponse dashboardSummaryResponse = DashboardUtils.buildDashboardSummary(data, apiMeta);
				if (dashboardSummaryResponse != null) {
					if (FeUtils.handleRepsonse(dashboardSummaryResponse, request, status, response)) {
						if (dashboardSummaryResponse.isS()) {
							DashboardLabel level = null;
							List<DashboardLabel> levels = new ArrayList<DashboardLabel>();
							Map<String, ChartModel> chartData = new HashMap<String, ChartModel>();
							for (Entry<String, List<DifferentEntity>> entry : dashboardSummaryResponse.getData().entrySet()) {
								ChartModel highChart = DashboardUtils.getChartData(entry.getKey(), dashboardSummaryResponse.getReportingResponse(), feResponse);
								for (DifferentEntity de : entry.getValue()) {
									DifferentEntityModel differntEntityFe = new DifferentEntityModel(de);
									level = new DashboardLabel(differntEntityFe.getLabelBottom(), true, entry.getKey(), differntEntityFe.getValue(), differntEntityFe.isInc(),
											differntEntityFe.getPercentValue(), differntEntityFe.getIcon());
									chartData.put(entry.getKey(), highChart);
								}
								feResponse.setChartData(chartData);
								levels.add(level);
							}
							List<DashboardLabel> arrengedLevels = new ArrayList<DashboardLabel>();
							getArrengedDashOBJ(arrengedLevels, levels);
							feResponse.setLevels(arrengedLevels);
							feResponse.setSuccessMsg(dashboardSummaryResponse.getMsg());
							feResponse.setStatus(Constants.SUCCESS);
						} else {
							feResponse.setErrorDetails(dashboardSummaryResponse.getEd());
							feResponse.setStatus(Constants.FAILED);
						}
					}
				} else {
					feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
					feResponse.setStatus(Constants.NOT_RESPONEDING);
				}
				// } else {
				// feResponse.setErrorDetails(dashboardConfig.getEd());
				// feResponse.setStatus(Constants.FAILED);
				// }
				// } else {
				// feResponse
				// .setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
				// feResponse.setStatus(Constants.NOT_RESPONEDING);
				// }
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
				feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
				feResponse.setStatus(Constants.FAILED);
			}
		} else {
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
			feResponse.setStatus(Constants.FAILED);
		}
		return feResponse;
	}

	private void getArrengedDashOBJ(List<DashboardLabel> arrengedLevels, List<DashboardLabel> levels) {
		int counter = 0;
		while (counter <= 7) {
			for (DashboardLabel obj : levels) {
				if (counter == 0 && obj.getType().equals("Game Played")) {
					arrengedLevels.add(0, obj);
				} else if (counter == 1 && obj.getType().equals("Sign Up")) {
					arrengedLevels.add(1, obj);
				} else if (counter == 2 && obj.getType().equals("Money Deposited")) {
					arrengedLevels.add(2, obj);
				} else if (counter == 3 && obj.getType().equals("Money Withdrawn")) {
					arrengedLevels.add(3, obj);
				} else if (counter == 4 && obj.getType().equals("Rake Generated")) {
					arrengedLevels.add(4, obj);
				} else if (counter == 5 && obj.getType().equals("Chips Wagered")) {
					arrengedLevels.add(5, obj);
				} else if (counter == 6 && obj.getType().equals("Bonus Chips Issued")) {
					arrengedLevels.add(6, obj);
				} else if (counter == 7 && obj.getType().equals("Bonus Chips Released")) {
					arrengedLevels.add(7, obj);
				}
			}
			counter++;
		}
	}

	@RequestMapping(value = "ajx/dashboard/widgets/live", method = RequestMethod.GET)
	@ResponseBody
	public ObjectResponse liveWidgets(HttpServletResponse response, HttpServletRequest request, SessionStatus status, @ModelAttribute("session") SessionWrapper session) {
		ObjectResponse feResponse = new ObjectResponse();
		try {
			DashboardLiveWidgetsResponse beResponse = ApiManager.getDashboardLiveWidgets(FeUtils.createAPIMeta(session, request));
			if (beResponse != null) {
				if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
					if (beResponse.isS()) {
						feResponse.setResponse(beResponse);
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

	@RequestMapping(value = "ajx/dashboard/widgets/tournament", method = RequestMethod.GET)
	@ResponseBody
	public ObjectListResponse tournamentWidgets(HttpServletResponse response, HttpServletRequest request, SessionStatus status, @ModelAttribute("session") SessionWrapper session,
			@RequestParam("query") String query) {
		ObjectListResponse feResponse = new ObjectListResponse();
		try {
			DashboardTournamentWidgetsResponse beResponse = ApiManager.getDashboardTounramentWidgets(FeUtils.createAPIMeta(session, request));
			if (beResponse != null) {
				if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
					if (beResponse.isS()) {
						for (DashboardTournamentWidgetsModel dashTournamentWidget : beResponse.getLiveTournaments())
							feResponse.getResponse().add(dashTournamentWidget);
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

}

