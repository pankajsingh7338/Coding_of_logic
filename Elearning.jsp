<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="forumApp" ng-controller="forumCtrl">

	<!-- comment popup -->

	<div class="modal fade" id="commentPokerMantra" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog " role="document">
			<div class="modal-content">
				<div class="modal-header e_modal_1">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close"> 
						<img
							src="${pageContext.request.contextPath}/resources/lyve/images/elearning/cancel.png"
							alt="Lyve Games, Learn Poker Online, Poker Mantra, Poker Tips, Indian Gaming Websites">
					</button>
					<h4 class="modal-title forum-modal-title" id="myModalLabel">
						Comments</h4>
				</div>
				<div class="modal-body">

					<textarea class="form-control forum-modal-input" rows="3"
						ng-model="commentBox" placeholder="Type your comment here"></textarea>
					<div class="form-control margin-top-20 forum-modal-input">{{playerUserName}}</div>
				</div>
				<div class="col-md-12 col-sm-12">
					<div class="text-center ">

						<div class="alert alert-danger alert-dismissible" role="alert"
							ng-show="isCommentError">
							<strong>Warning!</strong> {{errorMsg}}
						</div>
					</div>
				</div>
				<div class="modal-footer e_modal_1">
					<p class="text-center">
						<a href="#" ng-click="postCommentPokermantra()"
							class="btn btn-gradient no-margin"> <span>POST</span>
						</a>
					</p>
				</div>
			</div>
		</div>
	</div>
	<!-- Banner BG -->
	<div class="container"> 
		<div class="row">
			<div class="col-md-7 col-sm-12 col-xs-12"
				ng-init="getBlog('${slug}')">
				<div class="banner-default marginT">
					<div class="item">
						<img
							src="${pageContext.request.contextPath}/resources/lyve/images/elearning/PokerMantraBannerImage.png"
							alt="LYVE Games, Learn Poker Online, Poker Mantra, Poker Tricks, Poker Websites in India">
						<!-- <div class="banner-text">
                            <h3>Ready to practice your Poker skill <br> with </h3>
                            <h1>POKER MANTRA</h1>
                            <h3>Explore cutting edge Poker strategies, from beginner to
                                professional, to ensure that you are always at the top of the
                                game.</h3>
                        </div> -->
					</div>
				</div>
				<div class="mediaPart">
						<a href="${pageContext.request.contextPath}/pokermantra/video">
							<div class="item-list">
								<div class="marginT">
									<ul class="media-list">
										<li class="media">
											<div class="media-row">
												<div class="mediaImg">
													<img
														src="${pageContext.request.contextPath}/resources/lyve/images/elearning/VIDEO_Icon.png"
														alt="Lyve Games, Learn Poker Online, Poker Mantra, Learn Poker, most trusted poker online sites in India">
												</div>
												<div class="item-text">
													<h4 class="media-heading">
														<b>VIDEO</b>
													</h4>
													<p class="list-info">A wide variety of videos covering
														strategies of Poker - a priceless resource for beginners
														and professionals alike.</p>
												</div>
											</div>
										</li>
									</ul>
								</div>
							</div>
					</a>  
							<a
							href="${pageContext.request.contextPath}/pokermantra/illustrativeStudy">
								<div class="item-list">
									<div class="marginT">
										<ul class="media-list">
											<li class="media">
												<div class="media-row">
													<div class="mediaImg">
														<img
															src="${pageContext.request.contextPath}/resources/lyve/images/elearning/Illustration_Icon.png"
															alt="Lyve Games, Learn Poker Online, Poker Mantra, Learn Poker, Poker Sites with Freerolls India">
													</div>
													<div class="item-text">
														<h4 class="media-heading">
															<b>ILLUSTRATION</b>
														</h4>
														<p class="list-info">Having fun and playing Poker go
															hand-in-hand. Our learning comic strips present an
															educative as well as entertaining experience.</p>
													</div>
												</div>
											</li>
										</ul>
									</div>
								</div>
						</a> 
							<a
								href="${pageContext.request.contextPath}/pokermantra/quiz/levels">
									<div class="item-list">
										<div class="marginT">
											<ul class="media-list">
												<li class="media">
													<div class="media-row">
														<div class="mediaImg">
															<img
																src="${pageContext.request.contextPath}/resources/lyve/images/elearning/Quiz_Icon.png"
																alt="Lyve Games, Learn Poker Online, Poker Mantra, Poker Tips, Poker Websites in India">
														</div>
														<div class="item-text">
															<h4 class="media-heading">
																<b>QUIZ</b>
															</h4>
															<p class="list-info">Ready to test your Poker skills?
																The objective of Poker Quiz to give players a chance to
																put their Poker knowledge in use.</p>
														</div>
													</div>
												</li>
											</ul>
										</div>
									</div>
							</a> 									
								<a	href="${pageContext.request.contextPath}/pokermantra/PokerMantraArticle/{{articleslug}}">
										<div class="item-list">
											<div class="marginT">
												<ul class="media-list">
													<li class="media">
														<div class="media-row">
															<div class="mediaImg">
																<img
																	src="${pageContext.request.contextPath}/resources/lyve/images/elearning/ArticIe_Icon.png"
																	alt="LYVE Games, Learn Poker Online, Poker Mantra, Poker Tricks, Online Games in India, most trusted poker online sites in India">
															</div>
															<div class="item-text">
																<h4 class="media-heading">
																	<b>WHITE PAPERS </b>
																</h4>
																<p class="list-info">Navigate a large collection of
																	Poker education resource to cultivate your Poker
																	skills.</p>
															</div>
														</div>
													</li>
												</ul>
											</div>
										</div>
								</a>
				</div>
				<div class="col-md-7 col-sm-7 col-xs-12">
					<p class="bottom-text">Poker Mantra e-book is a must for anyone
						who has rudimentary or no knowledge of the game of Texas Hold'em
						and wishes to become a winner in a short time. The book not only
						caters beginners but also enumerates advanced level strategies in
						order to be a winning holdem player. The book showcases various
						Poker theories and strategies in order to learn the intricacies of
						the game.</p>
					<div class="readIndex">
						<a
							href="${pageContext.request.contextPath}/pokermantra/readMoreIndex"
							class="btn btn-gradient no-margin"><span>READ MORE</span> </a>
					</div>
				</div>
				<div class="col-md-5 col-sm-5 col-xs-12 text-center ">
					<h5 class="text-center bottom-text-1">E-Learning by Poker
						Mantra</h5>
					<div>
						<i class="fa fa-file-pdf-o text-center f-font"></i>
					</div>
					<div>
						<a href="#" ng-click="getPrice()"
							class="btn btn-gradient no-margin" data-toggle="modal"> <span>PAY
								& DOWNLOAD</span>
						</a>
					</div>
				</div>
			</div>
			<div class="col-md-5 col-sm-12 col-xs-12 marginT gap-r-0">
				<div class="panel panel-default topic rightPanelCollapse">
					<h4 class="panel-heading text-center topic-heading">POPULAR
						FORUM TOPICS</h4>
					<div class="panel-group"
						ng-init="getBlog('${slug}');getForum('${slug}')">
						<div class="panel panel-default"
							ng-repeat="forumlist in tempForumList">
							<div class="panel-heading">
								<h4 class="panel-title">
									<div class="panel-body slide-text">
										<a ng-click="getcheckin()" style="cursor: pointer;"> 
											 <a
											ng-href="${pageContext.request.contextPath}/pokermantra/forum_details/{{forumlist.slug}}"
											ng-click="setDetails(forumlist.id)"> 
												<p>{{forumlist.shortContent}}
													{{forumlist.forumshortContent}}</p>
										</a> <span class="dateTime homepageIcon">{{forumlist.friendlyDate}}
												&nbsp; <span class="icon-right" ng-if="!forumlist.iscolor">
													<a href="#" ng-click="sendForLike(forumlist)"> <i
														class="fa fa-thumbs-up"></i> {{forumlist.likesCount}}
												</a> <a href="#"
													ng-click="sendIdComment(forumlist);getPlayerUserName()"
													data-toggle="modal" data-target="#commentPokerMantra"><i
														class="fa fa-commenting"></i> {{forumlist.commentsCount}}
												</a> <span class="shearBttn"> <i class="fa fa-share-alt"
														aria-hidden="true"></i> <span class="socialHover">
															<ul>
																<li><a
																	href="http://api.addthis.com/oexchange/0.8/forward/facebook/offer?pco=tbx32nj-1.0&amp;url={{domain}}/pokermantra/forum_details/{{forumlist.slug}}"
																	target="_blank"> <img
																		src="${pageContext.request.contextPath}/resources/lyve/images/elearning/facebook.png"
																		alt="facebook" border="0" alt="Facebook" />
																</a></li>
																<li><script async
																		src="https://platform.twitter.com/widgets.js"
																		charset="utf-8"></script> <a
																	href="https://twitter.com/share?url={{domain}}/pokermantra/forum_details/{{forumlist.slug}}"
																	onclick="javascript:window.open(this.href,
																 '', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=300,width=600');return false;"><img
																		src="${pageContext.request.contextPath}/resources/lyve/images/elearning/twiitter.png"
																		alt="Share on Twitter" /></a></li>
																<li>
																	<!-- <script async
																		src="//platform.linkedin.com/in.js" charset="utf-8"></script> -->
																	<a
																	href=https://www.linkedin.com/shareArticle?mini=true&url={{domain}}/pokermantra/forum_details/{{forumlist.slug}}
																	"onclick="javascript:window.open(this.href,
                                                            	'', 'left=0,top=0,width=650,height=420,personalbar=0,toolbar=0,scrollbars=0,resizable=0'); return false;"><img
																		src="${pageContext.request.contextPath}/resources/lyve/images/elearning/linkedin.png"
																		alt="Share on Linkedin" /></a>
																</li>

																<li><script
																		src="https://apis.google.com/js/platform.js" async
																		defer></script> <a
																	href="https://plus.google.com/share?url={{domain}}/pokermantra/forum_details/{{forumlist.slug}}"
																	onclick="javascript:window.open(this.href,
													            '', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=600');return false;"><img
																		src="https://www.gstatic.com/images/icons/gplus-64.png"
																		alt="Share on Google+" /></a></li>
															</ul>
													</span>
												</span>
											</span> <span class="icon-right" ng-if="forumlist.iscolor"> <i
													class="fa fa-thumbs-up like-active-blue"></i>
													{{forumlist.likesCount}} <a href="#"
													ng-click="sendIdComment(forumlist);getPlayerUserName()"
													data-toggle="modal" data-target="#commentPokerMantra"><i
														class="fa fa-commenting"></i> {{forumlist.commentsCount}}
												</a> <span class="shearBttn"> <i class="fa fa-share-alt"
														aria-hidden="true"></i> <span class="socialHover">
															<ul>
																<li><a
																	href="http://api.addthis.com/oexchange/0.8/forward/facebook/offer?pco=tbx32nj-1.0&amp;url={{domain}}/pokermantra/forum_details/{{forumlist.slug}}"
																	target="_blank"><img
																		src="${pageContext.request.contextPath}/resources/lyve/images/elearning/facebook.png"
																		alt="facebook" " border="0" alt="Facebook" /> </a></li>
																<li><script
																		src="https://apis.google.com/js/platform.js" async
																		defer></script> <a
																	href="https://plus.google.com/share?url={{domain}}/pokermantra/forum_details/{{forumlist.slug}}"
																	onclick="javascript:window.open(this.href,
                                  '', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=600');return false;"><img
																		src="https://www.gstatic.com/images/icons/gplus-64.png"
																		alt="Share on Google+" /></a></li>
																<li><a
																	href="https://twitter.com/share?url={{domain}}/pokermantra/forum_details/{{forumlist.slug}}"
																	onclick="javascript:window.open(this.href,
					          '', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=300,width=600');return false;"><img
																		src="${pageContext.request.contextPath}/resources/lyve/images/elearning/twiitter.png"
																		alt="Share on Twitter" /></a></li>

																<li>
																	<!-- <script async
																		src="//platform.linkedin.com/in.js" charset="utf-8"></script> -->
																	<a
																	href=https://www.linkedin.com/shareArticle?mini=true&url={{domain}}/pokermantra/forum_details/{{forumlist.slug}}
																	"onclick="javascript:window.open(this.href,
                              '', 'left=0,top=0,width=650,height=420,personalbar=0,toolbar=0,scrollbars=0,resizable=0'); return false;"><img
																		src="${pageContext.request.contextPath}/resources/lyve/images/elearning/linkedin.png"
																		alt="Share on Linkedin" /></a>
																</li>

															</ul>
													</span>
												</span>
											</span>
										</span>
									</div>
								</h4>
							</div>
						</div>
					</div>
					<p class="text-center">
						<a href="${pageContext.request.contextPath}/pokermantra/forum"
							class="btn btn-gradient no-margin b-gap-10"> <span>View
								All</span> 
   				    	</a>
					</p>
				</div>
				<div class="panel panel-default border-in">
					<div class="panel-heading pool-heading">
						<h3 class="panel-title text-center pool-title">POLL</h3>
					</div>
					<div class="panel-body pool-content" ng-init="getVoteQuestions()">
						<p>{{voteQuestion}}</p>
						<form class="pad-t-20 poll-frm click-to-hide">
							<ul class="q-options" ng-hide="isVoted">
								<li class="" id="radio-container" ng-click="selectAnsw($index)"
									ng-repeat="option in pollOption track by $index">
									<div class="radio-container">
										<label class=""> <input type="radio" name="gender"
										    ng-checked="$index==0"
											class="pad-t-20">{{option}} <span class="checkmark"
											ng-model="answerOption" ></span>
										</label>
									</div>
								</li>
							</ul>
						</form>
						<div class="click-to-show" id="showProgress">
							<form class="poll-frm">
								<ul class="q-options">
									<li class="" id="radio-container" ng-hide="!isVoted"
										ng-repeat="option in pollOption track by $index">{{option}}
										<div class="radio-container">
											<label class=""> <input type="radio" name="gender"
												required="isCheck" ng-model="answerOption" class="pad-t-20"
												ng-click="selectAnsw($index)">
											</label>
											<div class="progress-barsss">
												<div class="col-md-10 col-xs-9 col-sm-10">
													<div class="progress bar-pro align-for">
														<div class="progress-bar progress-bar-success"
															role="progressbar" aria-valuenow="100px;"
															aria-valuemin="0" aria-valuemax=" {{totalPerc[$index]}}%"
															style="width: {{totalPerc[$index]}}%"></div>
													</div> 
												</div> 
												<div class="col-md-2 col-xs-3 col-sm-2">
													<p class="progress-text hide-show-p">{{totalPerc[$index]}}%</p>
												</div>
											</div>
										</div>
									</li>
								</ul>
							</form>
							<div class="bottom-poll" ng-hide="!isVoted">
								<p class="text-center poll-btn">
									<a href="#" class="text-left historyPokermantra"
										data-toggle="modal" ng-click="checkSession()"><i
										class="fa fa-history t5"></i> </a> <span class="totalVote">Total
										Votes : {{totalVotes}}</span>
								</p>
							</div>
						</div>
						<div class="bottom-poll" ng-hide="isVoted">
							<p class="text-center poll-btn">
								<a href="#" class="text-left historyPokermantra"
									data-toggle="modal"  ng-click="checkSession()"><i
									class="fa fa-history t5"></i></a> <a href="#"
									class="btn btn-gradient no-margin b0" data-toggle="modal"
									ng-click="sendVote(voteAns)" data-target="#showProgress"> <span>POLL</span>
								</a>
							</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="sideSocial hidden-sm hidden-xs">
		<div class="socialiconInner">
			<a href="tel:+91 8039659110" class="customercare"
				title="Customer Care">Customer Care</a>
			<div class="homesocialIcon">
				<div class="innersocialicon">
					<div class="socialIcon">
						<a href="https://www.facebook.com/pg/LYVE-Games-293870091349163/"
							title="Facebook"><i class="fa fa-facebook"></i></a>
					</div>
					<div class="socialIcon2">
						<a href="https://twitter.com/LYVEGames" title="Twitter"><i
							class="fa fa-twitter"></i></a>
					</div>
					<div class="socialIcon3">
						<a href="https://www.instagram.com/lyve_games/" title="Instagram"><i
							class="fa fa-instagram"></i></a>
					</div>
				</div>
			</div>
			<a href="https://www.youtube.com/channel/UC5cS38VBqVLTyPZHnjzm--g"
				class="homeyoutubeIcon" rel="nofollow" title="Youtube">youtube</a>
		</div>
	</div>
	<div id="pdfHistory" class="pdfHistory modal fade chip-buy"
		tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg " role="document">
			<div class="modal-content white-bg">
				<div class="modal-heder">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<img
							src="${pageContext.request.contextPath}/resources/lyve/images/elearning/cancel.png"
							alt="Lyve Games, Learn Poker Online, Poker Mantra, Poker Websites,Online Gaming Companies in India">
					</button>
				</div>
				<div class="modal-body pdfmodalHistroy">
					<div class="newPoll">
						<div class="row">
							<div class="col-md-8 col-sm-12 col-xs-12">
								<div class="panel panel-default border-in">
									<div class="panel-heading pool-heading">
										<h3 class="panel-title text-center pool-title">POLL</h3>
									</div>
									<div class="panel-body pool-content">
										<p>{{voteQuestion}}</p>
										<form class="pad-t-20">
											<ul class="q-options">
												<li class="" id="radio-container"
													ng-repeat="option in pollOption track by $index">
													<p class="vote_name">{{option}}</p>
													<div class="radio-container">
														<label class="modal-lable-aligne"> <input
															type="radio" name="gender"
															required"
															ng-model="answerOption"
															class="pad-t-20" ng-click="selectAnsw($index)">
														</label>
														<div class="progress-barsss">
															<div class="col-md-10 col-xs-9 col-sm-10">
																<div class="progress bar-pro modal-progrr">
																	<div class="progress-bar progress-bar-success"
																		role="progressbar" aria-valuemin=" {{totalPerc[$index]}}%"
																		aria-valuemax="100"
																		style="width: {{totalPerc[$index]}}%"></div>
																</div>
															</div> 
															<div class="col-md-2 col-xs-3 col-sm-2">
																<p class="progress-text modal-progrr-txt">{{totalPerc[$index]}}%</p>
															</div>
														</div>
													</div>
												</li>
											</ul>
										</form>
										<div class="bottom-poll">
											<p class="text-center poll-btn">
												<span class="totalVote">Total Votes : {{totalVot}}</span>
											</p>
										</div>
									</div>
								</div>
							</div>
							<div class="col-md-4 col-sm-12 col-xs-12">
								<div class="poll-history">
									<h3 class="text-center poll-history-text">POLL HISTORY</h3>
									<div class="poll-history-details">
										<ul class="q-options">
											<li class="" ng-repeat="question in totalQuestion"><a
												ng-click="getQuestionHistory(question.id)" class="pollQsn">
													<span>{{question.description}}</span> <span
													class="totalPoll">Votes : {{question.totalVote}}</span>
											</a></li>
										</ul>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- Modal -->
	<div id="pdfPopup" class="pdfPopup modal fade chip-buy" tabindex="-1"
		role="dialog">
		<div class="modal-dialog modal-sm " role="document">
			<div class="modal-content white-bg">
				<div class="modal-body pdfmodal">
					<div class="modal-heder">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<img
								src="${pageContext.request.contextPath}/resources/lyve/images/elearning/cancel.png"
								alt="Lyve Games, Learn Poker Online, Poker Mantra, Poker Tips, Indian Gaming Websites, Poker Sites with Freerolls India">
						</button> 
					</div> 
					<div class="pdfPopupTop"> 
						<div class="pdficon"> 
							<h4 class="modal-title"> 
								<i class="far fa-file-pdf text-center e-f-font"></i> 
							</h4>  
						</div> 
						<div class="pdfTxt"> 
							<p class="modal-text">Download this and be a pro poker player 
							</p> 
						</div> 
					</div> 
					<div class="pdfInput"> 
						<div ng-repeat="row in displayedCollection"> 
							<input type="text" class="form-control modal-input" 
								ng-model="amount1" class="text-rupee" 
								placeholder="&#8377; {{row.salePrice}}" ng-disabled="true"> 
							<p class="text-center gap-5"> 
								<span class="modal-text">Confirm Buying</span>&nbsp;&nbsp;<span 
									class="text-rupee">&#8377;<strike> 
										{{row.basePrice}} </span> </strike> &nbsp;<span class="text-rupee">&#8377;{{row.salePrice}}
								</span> 
							</p> 
						</div> 
					</div> 
					<p class="text-center">
						<a href="" class="btn btn-gradient no-margin b0"
							ng-click="processToPayForPdfDownload()"> <span>PROCEED
								TO PAY</span>
						</a>
					</p>
				</div>
			</div>
		</div>
	</div>
	<div ng-if="paymentSuccessDialog"></div>
</div>


