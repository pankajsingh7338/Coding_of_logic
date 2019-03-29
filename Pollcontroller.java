package com.actolap.wse.fe.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.constants.Urlparams;
import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.dao.VoteDao;
import com.actolap.wse.dto.VoteDescriptionDto;
import com.actolap.wse.dto.VoteDetailDto;
import com.actolap.wse.model.elearning.Vote;
import com.actolap.wse.model.elearning.VotersDetail;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.player.PlayerDocument;
import com.actolap.wse.model.player.PlayerDocument.DocumentStatus;
import com.actolap.wse.model.player.PlayerDocument.DocumentType;
import com.actolap.wse.response.VoteGetResponse;
import com.actolap.wse.response.VotingListResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Path("/vote")
@Api(value = "Rest API")

public class VoteController {

	private static final Logger LOG = LoggerFactory.getLogger(VoteController.class);

	@Path("/question")
	@ApiOperation(value = "List", notes = "Vote List", response = VotingListResponse.class, httpMethod = "get")
	public VotingListResponse questionList(Request request, Response responseO) {
		VotingListResponse response = new VotingListResponse();
		try {
			
			List<Vote> voteList = VoteDao.getQuestionByActive(true);
			List<Vote> questionList = VoteDao.getDescriptionByActive(true); 
			int totalVote = 0; 
			if (voteList != null && !voteList.isEmpty()) {  
				for(Vote voteQuestion : questionList) {  
					VoteDescriptionDto voteDescriptionDto = new VoteDescriptionDto(voteQuestion);
					response.getQuestionList().add(voteDescriptionDto);
				} 
				for (Vote vote : voteList) { 
					VoteDetailDto voteDto = new VoteDetailDto(vote); 
					List<Integer> list = new ArrayList<Integer>(vote.getVoteAnswers().values()); 
				    response.getVoteList().add(voteDto); 
					response.setAnswerList(list); 
					totalVote = vote.getVoteEmail().size();
				}  
				// delete after code deploy on production, date-> March 5th 2019 \\
				for (Vote vote : questionList) { 
					 vote.getVoteEmail().forEach((email) -> {
						 java.util.Optional<VotersDetail> exist = vote.getVotersDetail().stream().filter(p -> p.getEmail().equals(email)).findFirst();
						           if(!exist.isPresent()) {
								 VotersDetail votersDetails = new VotersDetail();
								 votersDetails.setEmail(email);
								 VoteDao.updateVotersEmail(votersDetails, vote.getId());
							 }
						 });
					  }
				List<Double> percent = new ArrayList<Double>(); 
				if(response.getAnswerList().size() != 0) { 
				double per1 = (float) (response.getAnswerList().get(0) * 100) / totalVote; 
			    double percentage1 = Double.parseDouble(new DecimalFormat("##.#").format(per1)); 
				double per2 = (float) (response.getAnswerList().get(1) * 100) / totalVote; 
				double percentage2 = Double.parseDouble(new DecimalFormat("##.#").format(per2)); 
				double per3 = (float) (response.getAnswerList().get(2) * 100) / totalVote; 
				double percentage3 = Double.parseDouble(new DecimalFormat("##.#").format(per3));  
				double per4 = (float) (response.getAnswerList().get(3) * 100) / totalVote; 
				double percentage4 = Double.parseDouble(new DecimalFormat("##.#").format(per4)); 
				percent.add(percentage1); 
				percent.add(percentage2); 
				percent.add(percentage3); 
				percent.add(percentage4); 
				response.setTotalPercent(percent); 
				response.setTotVot(totalVote); 
				String playerId = request.getHeader(Urlparams.playerId); 
				if (playerId != null && !"".equalsIgnoreCase(playerId)) {
					Player player = PlayerDao.getById(playerId);
					if (player != null && player.getEmail() != null) 
						response.setUserEmail(player.getEmail()); 
				} 								
			} 
			} else { 
				response.setMsg("No question found"); 
			} 
			response.setS(true); 
		} catch (Exception e) { 
			LOG.error(e.getMessage(), e); 
			response.setEd(e.getMessage()); 
		} 
		return response; 
	} 
	
	@GET 
	@Path("/setVote") 
	@ApiOperation(value = "set vote", notes = "Vote Id", response = VoteGetResponse.class, httpMethod = "get") 
	@ApiImplicitParams({ 
			@ApiImplicitParam(name = "id", value = "Vote Id", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "limit", value = "Limit", dataType = "string", paramType = "query", required = true) }) 
	public VoteGetResponse setAnswerForVote(Request request, Response responseO) { 
		VoteGetResponse response = new VoteGetResponse(); 
		String voteId = request.getHeader(Urlparams.id); 
		String playerId = request.getHeader(Urlparams.playerId); 
		String index = request.getHeader(Urlparams.voteAns);
		String totalVotes = request.getHeader(Urlparams.totalVotes);
		if (Utils.isNotEmpty(playerId)) { 
			try { 
				Vote votes = VoteDao.getById(voteId); 
				Player players = PlayerDao.getById(playerId); 
				if (!votes.getVoteEmail().contains(players.getEmail())) {  
					votes.getVoteEmail().add(players.getEmail()); 
					votes.setVoteEmail(votes.getVoteEmail()); 
					VotersDetail votersDetail = new VotersDetail();
					votersDetail.setEmail(players.getEmail());
					votersDetail.setDate(new Date());
					votersDetail.setElectedAnswer(votes.getOptions().get(Integer.parseInt(index)));
					votes.getVotersDetail().add(votersDetail);
					votes.setVotersDetail(votes.getVotersDetail());
					if (votes.getVoteAnswers().containsKey(index)) { 
						int x = votes.getVoteAnswers().get(index); 
						  x = x + 1; 
						  votes.getVoteAnswers().put(index, x); 
					} else {  
						if(index.equals("0")) { 
							votes.getVoteAnswers().put("1", 0); 
							votes.getVoteAnswers().put("2", 0); 
							votes.getVoteAnswers().put("3", 0); 
							votes.getVoteAnswers().put(index, 1);
						} 
						else if(index.equals("1")) { 
							votes.getVoteAnswers().put("0", 0);  
							votes.getVoteAnswers().put("2", 0); 
							votes.getVoteAnswers().put("3", 0); 
							votes.getVoteAnswers().put(index, 1);  
						} 
						else if(index.equals("2")) { 
							votes.getVoteAnswers().put("0", 0); 
							votes.getVoteAnswers().put("1", 0); 
							votes.getVoteAnswers().put("3", 0); 
							votes.getVoteAnswers().put(index, 1); 
						} 
						else if(index.equals("3")) { 
							votes.getVoteAnswers().put("0", 0); 
							votes.getVoteAnswers().put("1", 0);  
							votes.getVoteAnswers().put("2", 0); 
							votes.getVoteAnswers().put(index, 1); 
						} 
					}  
					votes.setVoteAnswers(votes.getVoteAnswers());  
					votes.setTotalVote(totalVotes);
				} 
				VoteDao.persist(votes);  
				response.setS(true); 
			} catch (Exception e) { 
				System.out.println(e.getMessage());  
			} 
		} 
		return response; 
	} 
	
	@Path("/getQuestionDescription") 
	@ApiOperation(value = "id", notes = "Vote id", response = VotingListResponse.class, httpMethod = "get") 
	@ApiImplicitParams({@ApiImplicitParam(name = "id", value = "Vote id", dataType = "string", paramType = "query", required = true)}) 
	public VotingListResponse getQuestionHistory(Request request, Response responseO) { 
		VotingListResponse response = new VotingListResponse(); 
		try { 
			String playerId = request.getHeader(Urlparams.playerId); 
			Player player = PlayerDao.getById(playerId); 
			String id = request.getHeader(Urlparams.id); 
			Vote votes = VoteDao.getById(id); 
			int totalVote =0; 
			if(votes != null ) { 
				VoteDetailDto voteOption = new VoteDetailDto(votes);  
				List<Integer> list = new ArrayList<Integer>(votes.getVoteAnswers().values()); 
				response.getVoteList().add(voteOption); 
				response.setAnswerList(list);
				totalVote = voteOption.getVoteEmail().size();
			} 
			List<Double> percent = new ArrayList<Double>(); 
			if(response.getAnswerList().size() != 0) { 
			double per1 = (float) (response.getAnswerList().get(0) * 100) / totalVote; 
		    double percentage1 = Double.parseDouble(new DecimalFormat("##.#").format(per1)); 
			double per2 = (float) (response.getAnswerList().get(1) * 100) / totalVote; 
			double percentage2 = Double.parseDouble(new DecimalFormat("##.#").format(per2)); 
			double per3 = (float) (response.getAnswerList().get(2) * 100) / totalVote; 
			double percentage3 = Double.parseDouble(new DecimalFormat("##.#").format(per3));  
			double per4 = (float) (response.getAnswerList().get(3) * 100) / totalVote; 
			double percentage4 = Double.parseDouble(new DecimalFormat("##.#").format(per4)); 
			percent.add(percentage1); 
			percent.add(percentage2); 
			percent.add(percentage3); 
			percent.add(percentage4); 
			response.setTotalPercent(percent); 
			response.setTotVot(totalVote); 
			if (player.getEmail() != null) 
				response.setUserEmail(player.getEmail()); 
			} 
		   response.setS(true); 
		} 
		catch (Exception e) { 
			LOG.error(e.getMessage(), e); 
			response.setEd(e.getMessage()); 
		} 
		return response; 
	} 
} 


