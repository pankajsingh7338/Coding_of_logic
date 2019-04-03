package com.actolap.wse.fe.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.constants.Urlparams;
import com.actolap.wse.dao.TableDao;
import com.actolap.wse.dto.LobbyRunningTableDto;
import com.actolap.wse.model.game.poker.GameMode;
import com.actolap.wse.model.game.poker.PokerTable;
import com.actolap.wse.model.gameplay.GameType;
import com.actolap.wse.response.RunningTablesLobbyResponse;

@Path("/table/public")
@Api(value = "Rest API")
public class TablePublicController { 

	private static final Logger LOG = LoggerFactory.getLogger(TablePublicController.class); 
 
	@GET 
	@Path("/free/lobby/list")  
	@ApiOperation(value = "Free Table Lobby List", notes = "Free Lobby List", response = RunningTablesLobbyResponse.class, httpMethod = "get")
	@ApiImplicitParams({ @ApiImplicitParam(name = "mode", value = "Game Mode", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "tableSize", value = "Table Size", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "sb", value = "Small Blind", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "bb", value = "Big Blind", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "buyInMin", value = "Buy In Min", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "buyInMax", value = "Buy In Max", dataType = "string", paramType = "query", required = true) })
	public RunningTablesLobbyResponse getFreelobbyList(Request request, Response responseO) {
		RunningTablesLobbyResponse response = new RunningTablesLobbyResponse(); 
		try { 
			String mode = request.getHeader(Urlparams.mode); 
			String tableSize = request.getHeader(Urlparams.tableSize); 
			String sb = request.getHeader(Urlparams.sb);
			String bb = request.getHeader(Urlparams.bb);
			String buyInMin = request.getHeader(Urlparams.buyInMin); 
			String buyInMax = request.getHeader(Urlparams.buyInMax); 

			GameMode gameMode = null; 
			if (Utils.isNotEmptyNA(mode)) 
				gameMode = GameMode.valueOf(mode); 
			List<PokerTable> dbTableList = TableDao.runningList(null, GameType.FREE); 
			List<LobbyRunningTableDto> tableList = new ArrayList<LobbyRunningTableDto>();
			if (dbTableList != null && !dbTableList.isEmpty()) { 
				for (PokerTable table : dbTableList) { 
					LobbyRunningTableDto tableData = new LobbyRunningTableDto(table, gameMode);
					if (Utils.isNotEmpty(tableData.getTitle()))
						tableList.add(tableData);
				}
				for (LobbyRunningTableDto table : tableList) {
					if (filterData(table, tableSize, sb, bb, buyInMin, buyInMax, mode))
						response.getTableList().add(table);
				}
				if (response.getTableList().isEmpty()) 
					response.setMsg("No table found"); 
			} else { 
				response.setMsg("No table found");
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	public boolean filterData(LobbyRunningTableDto table, String tableSize, String sb, String bb, String buyInMin, String buyInMax, String mode) {
		Integer ts = null, smallBlind = null, bigBlind = null, bMin = null, bMax = null;
		String gameMode = null;
		if (Utils.isNotEmptyNA(tableSize)) 
			ts = Integer.parseInt(tableSize);
		if (Utils.isNotEmptyNA(sb))
			smallBlind = Integer.parseInt(sb);
		if (Utils.isNotEmptyNA(bb))
			bigBlind = Integer.parseInt(bb);
		if (Utils.isNotEmptyNA(buyInMin))
			bMin = Integer.parseInt(buyInMin); 
		if (Utils.isNotEmptyNA(buyInMax))
			bMax = Integer.parseInt(buyInMax);
		if (Utils.isNotEmptyNA(mode))
			gameMode = mode;
		if (((ts != null && table.getSize() == ts) || ts == null)
				&& ((smallBlind != null && table.getBlind() >= smallBlind && bigBlind != null && table.getBlindBig() <= bigBlind) || (smallBlind == null && bigBlind == null))
				&& ((bMin != null && table.getBuyInMin() >= bMin && bMax != null && table.getBuyInMax() <= bMax) || (bMin == null && bMax != null && table.getBuyInMax() > bMax) || (bMin == null && bMax == null))
				&& ((gameMode != null && table.getMode().equals(gameMode)) || gameMode == null))
			return true;
		return false;

	}
		 
	@GET
	@Path("/real/lobby/list") 
	@ApiOperation(value = "Real Table Lobby List", notes = "Real Table Lobby List", response = RunningTablesLobbyResponse.class, httpMethod = "get")
	@ApiImplicitParams({ @ApiImplicitParam(name = "mode", value = "Game Mode", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "tableSize", value = "Table Size", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "sb", value = "Small Blind", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "bb", value = "Big Blind", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "buyInMin", value = "Buy In Min", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "buyInMax", value = "Buy In Max", dataType = "string", paramType = "query", required = true) })
	public RunningTablesLobbyResponse getRealPlaylobbies(Request request, Response responseO) {
		RunningTablesLobbyResponse response = new RunningTablesLobbyResponse();
		try { 
			String mode = request.getHeader(Urlparams.mode); 
			String tableSize = request.getHeader(Urlparams.tableSize); 
			String sb = request.getHeader(Urlparams.sb); 
			String bb = request.getHeader(Urlparams.bb); 
			String buyInMin = request.getHeader(Urlparams.buyInMin); 
			String buyInMax = request.getHeader(Urlparams.buyInMax); 
			GameMode gameMode = null; 
			if (Utils.isNotEmptyNA(mode)) 
				gameMode = GameMode.valueOf(mode); 
			List<PokerTable> dbTableList = TableDao.runningList(null, GameType.CASH);
			List<LobbyRunningTableDto> tableList = new ArrayList<LobbyRunningTableDto>();
			if (dbTableList != null && !dbTableList.isEmpty()) {
				for (PokerTable table : dbTableList) {
					LobbyRunningTableDto tableData = new LobbyRunningTableDto(table, gameMode);
					if (Utils.isNotEmpty(tableData.getTitle()))
						tableList.add(tableData);
				} 
				for (LobbyRunningTableDto table : tableList) {
					if (filterData(table, tableSize, sb, bb, buyInMin, buyInMax, mode))
						response.getTableList().add(table);
				}
				if (response.getTableList().isEmpty())
					response.setMsg("No table found");
			} else {
				response.setMsg("No table found"); 
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}
	
 } 


