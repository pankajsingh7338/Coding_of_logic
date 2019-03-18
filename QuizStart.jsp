<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<section id="eLearning" ng-controller="elearningCtrl">
	<div ng-hide=true>
		<canvas id="demo" width="1250" height="900" style="display: none;"></canvas>
		<img id="certificateImage" class="img-responsive"
			src="${pageContext.request.contextPath}/resources/lyve/images/elearning/certificate.jpg"
			alt="Certificate">
	</div>
	<div class="container" ng-init="getQuestions()">
		<div class="col-md-7 col-sm-12 col-xs-12">
			<div class="quiz-left-part">
				<div class="level-bar-left quiz-details-left">
					<div class="about_content">
						<div class="text-center" ng-show="loading">
							<img class="img-loader" style="margin-top: 138px;"
								src="${pageContext.request.contextPath}/resources/lyve/images/loader.gif"
								alt="Loader">
						</div>
						<div ng-show="!loading && !isError">
							<div ng-show="!finish">
								<div class="" ng-show="!completed">
									<div class="question-area">
										<div class="">
											<div class="custom-levelAll">
												<div class="quiz-portal continueAll">
													<p class="text-center">
														<b>{{completedMsg}}</b>
													</p>

													<div class="col-md-6 text-center">
														<a ng-click="completeQuiz('YES')"
															class="no-margin ">
															<button class="btn btn-gradient no-margin">
																<span>Continue Quiz</span>
															</button>
														</a>
													</div>
													<div class="col-md-6 text-center">
														<a ng-click="completeQuiz('NO')"
															class="no-margin ">
															<button class="btn btn-gradient no-margin">
																<span>New Quiz</span>
															</button>
														</a>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<!--Question area  -->
								<div class="" ng-show="completed && !loading && !isError">
									<div class="question-area">
										<div class="clearfix"></div>
										<form id="example-form">
											<div>
												<div class="">
													<div class="">
														<div class="">
															<div class="custom-levelAll">
																<div class="quiz-portal">
																	<h4 class="question">
																		<b>Q.{{questionIndex}} {{question.title}} </b> </b>
																	</h4>
																	<div class="quiz-portal-items">
																		<div class="qz">
																			<!-- Rdio Button Design -->
																			<ul class="q-options">
																				<li class="" id="radio-container"
																					ng-repeat="option in question.options track by $index">
																					<div class="radio-container">
																						<label class="" id="{{$index}}"> <input
																							type="radio" name="gender" value="$index"
																							required ng-disabled="isCheck" class="pad-t-20"
																							ng-click="selectAns($index)">{{option}} <span
																							class="checkmark"></span>
																						</label>
																					</div>
																				</li>
																			</ul>
																			<!-- End Radio -->
																			<span class="que-count">{{questionIndex}}/10 </span>
																		</div>
																	</div>
																</div>
															</div>
														</div>
													</div>
													<div class="col-md-6 col-sm-6 col-xs-6 text-center">
														<button class="btn btn-gradient no-margin"
															id="submitButton" ng-disabled="submitVisibility"
															ng-click="submitAnswer(answer)">
															<span>SUBMIT</span>
														</button>
													</div>
													<div class="col-md-6 col-sm-6 col-xs-6 text-center">
														<button class="btn btn-gradient no-margin" id="nextButton"
															ng-disabled="nextVisibility"
															ng-click="getNextQuestion(questionIndex);count()">
															<span>{{button.label}}</span>
														</button>
													</div>
												</div>
											</div>
										</form>
									</div>
								</div>
								<!--End of question area  -->
							</div>
							<div ng-show="finish">
								<div class="">
									<div class="custom-levelAll">
										<div class="quiz-portal">
											<div class="quiz-win">
												<div class="row">
													<div class="col-md-12 text-center complete-txt">
														<h2>
															<b>Quiz completed</b>
														</h2>
														<p>
															<strong>You can score better!!</strong>
														</p>
													</div>
												</div>
												<div class="row">
													<div class="col-md-4 text-center">
														<h3>
															<b>{{score | number: 2}} %</b>
														</h3>
														<p>Correct</p>
													</div>
													<div class="col-md-4 text-center">
														<h3>
															<b>{{totalCorrect}}</b>
														</h3>
														<p>Correct</p>
													</div>
													<div class="col-md-4 text-center">
														<h3>
															<b>{{totalQuestion}}</b>
														</h3>
														<p>Questions</p>
													</div>
												</div>
												<div class="row">
													<div class="col-md-12 text-center complete-txt">
														<p>Do you want to re-attempt?</p>
													</div>
													<div class="row">
														<div class="col-md-6 col-sm-6 col-xs-6 text-center">
															<a
																href="${pageContext.request.contextPath}/pokermantra/quiz/levels"
																class="btn btn-gradient no-margin"><span> No
															</span> </a>
														</div>
														<div class="col-md-6 col-sm-6 col-xs-6 text-center">
															<a href="#" class="btn btn-gradient no-margin "> <span
																ng-click="getQuestions()">Yes</span>
															</a>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div ng-show="isError" class="text-center">
							<h1>{{errorMessage}}</h1>
						</div>
					</div>
				</div>


				<!-- Start Responsive Timer -->
				<br> <br>


				<div class="quiz-detail-right marginT-20 display-none">
					<div class="panel panel-default topic">
						<div class="img-self text-center">
							<div class="c_down" ng-show="!showResult &amp;&amp; completed">
								<div ng-show="!showResult &amp;&amp; completed"
									id="countdownResponsive" class="">
									<canvas id="countdown360_countdown"> 
										<span id="countdown-text" role="status" aria-live="assertive"></span> 
										</canvas>
								</div>
							</div>
							<div class="thums_box" ng-show="showResult">
								<div class="thums_up" ng-show="result && !notSubmit && !finish">
									<img
										src="${pageContext.request.contextPath}/resources/lyve/images/elearning/thums_up.png"
										alt="Thums Up">
								</div>
								<div class="thum_down"
									ng-show="!result && !notSubmit && !finish">
									<img
										src="${pageContext.request.contextPath}/resources/lyve/images/elearning/thums_down.png"
										alt="Thums down">
								</div>
								<div class="thum_down" ng-show="notSubmit && !finish">
									<img
										src="${pageContext.request.contextPath}/resources/lyve/images/elearning/time_up.png"
										alt="Time Up">
								</div>
							</div>
							<div class="thum_up" ng-show="!completed">
								<img
									src="${pageContext.request.contextPath}/resources/lyve/images/elearning/info.png"
									alt="Info">
							</div>
							<div ng-show="finish">
								<%-- 	<div class="col-md-12 col-sm-12 col-xs-12 marginT-30">
											<div class="level-bar-left quiz-details-left">
												<canvas id="demo" width="1200" height="859"
													style="display: none;"></canvas>
												<img id="certificateImage" class="img-responsive"
													src="${pageContext.request.contextPath}/resources/lyve/images/elearning/certificate.jpg"
													alt="e-Learning certificate of completation">
												<div class="certificate_texts">
													<h4 class="certificate_heading">Certificate of
														Completion</h4>
													<p>
														This is to certify that <span class="cer-holder-name">{{title.toUpperCase()}}</span>
														<br>has successfully completed the {{level}} level in<br>
														Poker Quiz with <span class="cer-percentage">{{score
															| number: 0}}%</span> on <span class="cer-date">{{date}}</span>
													</p>
												</div>
												<button ng-click="checkFullNameAndDownload()"
													class="btn btn-success" href="#">
													<i aria-hidden="true" class="fa fa-download"></i> Download
													Certificate
												</button>
											</div>  
										</div> --%>
								<div class="quiz-detail-right marginT-20">
									<div class="">
										<div class="addFrm">
											<img
												src="${pageContext.request.contextPath}/resources/lyve/images/Poker-mantra.jpg"
												alt="LYVE Games, Learn Poker Online, Poker Mantra, Poker Tricks">
										</div>
										<div class="btn-bottom text-center claim_bonus_bttn">
											<a href="#" class="btn btn-gradient no-margin"> <span>Claim
													Bonus/Coupon Code</span>
											</a>
										</div>
										<div class="txt-bottom text-center"
											ng-click="checkFullNameAndDownload(score,level)">
											<a href="" class="btn yellow-mem-download"> <i
												class="fa fa-download"></i> DOWNLAOD CERTIFICATE
											</a> <span class="dwnlo-error-msg">{{errorMsg}}</span>
										</div>

									</div>
								</div>
							</div>
						</div>
						<div class="timerBtmTxt ans-place" ng-show='!finish'>
							<div class="">
								<div class="timer">
									<div class="clearfix" ng-show="!finish"></div>
									<div ng-show="notSubmit || showResult"></div>
									<div ng-show="completed && !showResult && !finish"></div>
									<div ng-show="ansDescription!=null && !finish">
										<p>{{ansDescription}}</p>
									</div>
								</div>
							</div>
						</div>
						<div class="timerBtmTxt wrong-ans-bttm-txt" ng-show="!finish">
							Intermediate and advanced categories have certain questions whose
							answers are not absolute. It means our experts have formulated
							the best options that players might take to make winning hands.</div>
					</div>
				</div>




				<!-- End Responsive Timer -->


				<div class="col-md-12 col-sm-12 col-xs-12 marginT-30">
					<div class="bottm-level-bar-left">
						<div class="row">
							<div class="bottm-levelAll">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<div class="bottom-pol text-center">
										<div class="bottom-pol-b">
											<a class="beginner"> <img class="img-responsive"
												src="${pageContext.request.contextPath}/resources/lyve/images/elearning/coin2.png"
												alt="Play Intermediate Level" title="Play Beginner Level">
										</div>
										<button class="btn btn-gradient no-margin">
											<span>PLAY</span>
										</button>
										</a>
									</div>
								</div>
								<div class="col-md-4 col-sm-4 col-xs-12">
									<div class="bottom-pol text-center">
										<div class="bottom-pol-i">
											<a class="intermediate"> <img class="img-responsive"
												src="${pageContext.request.contextPath}/resources/lyve/images/elearning/coin1.png"
												alt="Play Intermediate Level"
												title="Play Intermediate Level">
										</div>
										<button class="btn btn-gradient no-margin">
											<span>PLAY</span>
										</button>
										</a>
									</div>
								</div>
								<div class="col-md-4 col-sm-4 col-xs-12">
									<div class="bottom-pol text-center marginB-0">
										<div class="bottom-pol-b">
											<a class="advanced"> <img class="img-responsive"
												src="${pageContext.request.contextPath}/resources/lyve/images/elearning/coin3.png"
												alt="Play Advanced Level" title="Play Advanced Level">
										</div>
										<button class="btn btn-gradient no-margin">
											<span>PLAY</span>
										</button>
										</a>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="col-md-5 col-sm-12 col-xs-12">
			<!--    <div class="quiz-detail-right marginT-20 responsive-display-none">  -->
			<div class="quiz-detail-right marginT-20 responsive-display-none">
				<div class="panel panel-default topic">
					<div class="img-self text-center">
						<div class="c_down" ng-show="!showResult &amp;&amp; completed">
							<div ng-show="!showResult &amp;&amp; completed" id="countdown"
								class="">
								<canvas id="countdown360_countdown"> 
										<span id="countdown-text" role="status" aria-live="assertive"></span> 
										</canvas>
							</div>
						</div>
						<div class="thums_box" ng-show="showResult">
							<div class="thums_up" ng-show="result && !notSubmit && !finish">
								<img
									src="${pageContext.request.contextPath}/resources/lyve/images/elearning/thums_up.png"
									alt="Thums Up">
							</div>
							<div class="thum_down" ng-show="!result && !notSubmit && !finish">
								<img
									src="${pageContext.request.contextPath}/resources/lyve/images/elearning/thums_down.png"
									alt="Thums down">
							</div>
							<div class="thum_down" ng-show="notSubmit && !finish">
								<img
									src="${pageContext.request.contextPath}/resources/lyve/images/elearning/time_up.png"
									alt="Time Up">
							</div>
						</div>
						<div class="thum_up" ng-show="!completed">
							<img
								src="${pageContext.request.contextPath}/resources/lyve/images/elearning/info.png"
								alt="Info">
						</div>
						<div ng-show="finish">
							<%-- 	<div class="col-md-12 col-sm-12 col-xs-12 marginT-30">
											<div class="level-bar-left quiz-details-left">
												<canvas id="demo" width="1200" height="859"
													style="display: none;"></canvas>
												<img id="certificateImage" class="img-responsive"
													src="${pageContext.request.contextPath}/resources/lyve/images/elearning/certificate.jpg"
													alt="e-Learning certificate of completation">
												<div class="certificate_texts">
													<h4 class="certificate_heading">Certificate of
														Completion</h4>
													<p>
														This is to certify that <span class="cer-holder-name">{{title.toUpperCase()}}</span>
														<br>has successfully completed the {{level}} level in<br>
														Poker Quiz with <span class="cer-percentage">{{score
															| number: 0}}%</span> on <span class="cer-date">{{date}}</span>
													</p>
												</div>
												<button ng-click="checkFullNameAndDownload()"
													class="btn btn-success" href="#">
													<i aria-hidden="true" class="fa fa-download"></i> Download
													Certificate
												</button>
											</div>  
										</div> --%>
							<div class="quiz-detail-right marginT-20">
								<div class="">
									<div class="addFrm">
										<img
											src="${pageContext.request.contextPath}/resources/lyve/images/Poker-mantra.jpg"
											alt="LYVE Games, Learn Poker Online, Poker Mantra, Poker Tricks">
									</div>
									<div class="btn-bottom text-center claim_bonus_bttn">
										<a href="#" class="btn btn-gradient no-margin"> <span>Claim
												Bonus/Coupon Code</span>
										</a>
									</div>
									<div class="txt-bottom text-center"
										ng-click="checkFullNameAndDownload(score,level)">
										<a href="" class="btn yellow-mem-download"> <i
											class="fa fa-download"></i> DOWNLAOD CERTIFICATE
										</a> <span class="dwnlo-error-msg">{{errorMsg}}</span>
									</div>

								</div>
							</div>
						</div>
					</div>
					<div class="timerBtmTxt ans-place" ng-show='!finish'>
						<div class="">
							<div class="timer">
								<div class="clearfix" ng-show="!finish"></div>
								<div ng-show="notSubmit || showResult"></div>
								<div ng-show="completed && !showResult && !finish"></div>
								<div ng-show="ansDescription!=null && !finish">
									<p>{{ansDescription}}</p>
								</div>
							</div>
						</div>
					</div>
					<div class="timerBtmTxt wrong-ans-bttm-txt" ng-show="!finish">
						Intermediate and advanced categories have certain questions whose
						answers are not absolute. It means our experts have formulated the
						best options that players might take to make winning hands.</div>
				</div>
			</div>
			<div class="forum-card marginT-30">
				<div class="col-md-4 col-sm-4 gap-forum-5">
					<div class="panel panel-default artical-panel ">
						<a href="${pageContext.request.contextPath}/pokermantra/video">
							<div class="panel-body padding-none">
								<iframe width="100%" height="125px"
									src="https://www.youtube.com/embed/Nw7fCNlrZAg" frameborder="0"
									allow="autoplay; encrypted-media" allowfullscreen></iframe>
							</div>
							<div class="forum-footer">
								<h3 class="text-center">Video</h3>
							</div>
						</a>
					</div>
				</div>
				<div class="col-md-4 col-sm-4 gap-forum-5">
					<div class="panel panel-default artical-panel">
						<a
							href="${pageContext.request.contextPath}/pokermantra/illustrativeStudy">
							<div class="panel-body">
								<p>Having fun and playing Poker go hand-in-hand. Our
									learning comic strips...</p>
							</div>
							<div class="forum-footer">
								<h3 class="text-center">ILLUSTRATION</h3>
							</div>
						</a>
					</div>
				</div>
				<div class="col-md-4 col-sm-4 gap-forum-5">
					<div class="panel panel-default artical-panel">
						<a href="${pageContext.request.contextPath}/pokermantra/forum">
							<div class="panel-body">
								<p>Discussions are always healthy, cultivate conversations
									over our forum.</p>
							</div>
							<div class="forum-footer">
								<h3 class="text-center">FORUM</h3>
							</div>
						</a>
					</div>
				</div>
				<div class="col-md-4 col-sm-4 gap-forum-5"
					ng-init="getSlug('${slug}')">
					<div class="panel panel-default artical-panel">
						<a
							href="${pageContext.request.contextPath}/pokermantra/PokerMantraArticle/{{articleslug}}">
							<div class="panel-body">
								<p>Navigate a large collection of Poker education resource
									to cultivate your Poker skills.</p>
							</div>
							<div class="forum-footer">
								<h3 class="text-center">WHITE PAPERS</h3>
							</div>
						</a>
					</div>
				</div>
				<div class="col-md-8 col-sm-8 gap-forum-5">
							<div class="text-forum">
								<div class="row">
									<div class="col-md-6 col-sm-7 responsive-alllan1">

										<a
											href="${pageContext.request.contextPath}/pokermantra/readMoreIndex">
											<h3>E-Learning by Poker Mantra</h3>
											<p>Poker theories and strategies to learn the game.</p>
										</a> <a
											href="${pageContext.request.contextPath}/pokermantra/readMoreIndex"
											class="btn btn-gradient pad-btn smallBttn marginT-8"> <span>READ
												MORE</span>
										</a>
									</div>

									<div class="col-md-6 col-sm-5 responsive-alllan">
										
										<p class="text-center dwnloadPDF">
											<i class="fa fa-file-pdf-o text-center g-font"></i><br>
											<a href="#" ng-click="getPrice()" class="btn btn-gradient pad-btn smallBttn"
												> <span>PAY
													& DOWNLOAD</span>
											</a>
										</p>
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
								alt="">
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
							ng-click="processToPay()"> <span>PROCEED TO PAY</span>
						</a>
					</p>
				</div>
			</div>
		</div>
	</div>
</section>


