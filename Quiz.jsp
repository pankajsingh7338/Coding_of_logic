<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
 <div class="container">
  <div class="row">
    <div class="col-md-7 col-sm-12 col-xs-12">
      <div class="quiz-left-part">
        <div class="level-bar-left">
        <div class="row">
          <div class="levelAll">
            <div class="col-md-4 col-sm-4 col-xs-12">
              <div class="bottom-pol text-center">
              <div class="bottom-pol-b"><a class="beginner"> 
                <img class="img-responsive"
										src="${pageContext.request.contextPath}/resources/lyve/images/elearning/coin2.png"
										alt="LYVE Games, Online Poker, Play Beginner Level" title="Play Beginner Level">
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
                <a class="intermediate"><a class="intermediate"> 
                 <img class="img-responsive"
										src="${pageContext.request.contextPath}/resources/lyve/images/elearning/coin1.png"
										alt="Play Intermediate Level, LYVE Games, Play Poker Online" title="Play Intermediate Level">
                </div>
                <button class="btn btn-gradient no-margin">
                  <span>PLAY</span>
                </button></a>
              </div>

          </div>
          <div class="col-md-4 col-sm-4 col-xs-12">

              <div class="bottom-pol text-center marginB-0">
                <div class="bottom-pol-b"><a class="advanced"> 
                <img class="img-responsive"
										src="${pageContext.request.contextPath}/resources/lyve/images/elearning/coin3.png"
										alt="LYVE Games, Online Poker, Play Beginner level" title="Play Advanced Level">
                </div>
                <button class="btn btn-gradient no-margin">
                  <span>PLAY</span>
                </button></a>

              </div>

          </div>
        </div>
        </div>
        </div>
        <!-- LEADERBOARD  -->
      <div class="col-md-12 col-sm-12 col-xs-12 quiz-home-table marginT-30">
        <div class="leaderboard">
        <section id="eLearning" ng-controller="elearningCtrl" ng-init="getleader()">
          <h4 class="text-center">Leaderboard
            <span class="shearBttn pull-right">

              <a href="#"><b><i class="fa fa-filter custom-filter"></i></b></a>
       <span class="socialHover leaderboardFilter">
                <ul>
                <li><a href="#" ng-click="myFilter('ALL')">All Levels</a></li>
                  <li><a href="#" ng-click="myFilter('BASIC')">Beginner</a></li>
                  <li><a href="#" ng-click="myFilter('INTERMEDIATE')">Intermediate</a></li>
                  <li><a href="#" ng-click="myFilter('ADVANCE')">Advanced</a></li>
                </ul>
              </span>
            </span>
          </h4>
          <table class="table table-hover table-responsive">
            <thead class="custom-tb-bt-border">
              <tr>
                <th>Name</th>
                <th>Score</th>
                <th>Level</th>
              
              </tr>
            </thead>
            <tbody class="custom-tb-top-border tbody-hight" ng-show="!isLoading">
              <tr ng-repeat="row in tempDisplayedCollection | filter: {'level' : myFilter }| orderBy : 'score':true ">

                <td>{{row.playerTitle}}</td>
                <td>{{row.score	| number: 2}} %</td> 
                <td>{{row.level}}</td>
				
              </tr>
            
            </tbody>
          </table></section>
          </div>
        </div>
        <!-- <div class="col-md-12 col-sm-12 col-xs-12 marginT-30">
        <section id="demos" class="quizSlideAll">
              <div class="row">
                <div class="large-12 columns">
                  <div class="owl-carousel quizSlide owl-theme">
                    <div class="item">
                      <a href="#"><img src="images/promotion1.png" alt="Lyve Games, Learn Poker Online, Poker Mantra"></a>
                    </div>
                    <div class="item">
                      <a href="#"><img src="images/promotion1.png" alt="LYVE Games, Online Gaming Companies in India, Poker Websites in India,  Poker Sites with Freerolls India"></a>
                    </div>
                    <div class="item">
                      <a href="#"><img src="images/promotion1.png" alt="LYVE Games, Poker Mantra, Poker Websites"></a>
                    </div>
                    <div class="item">
                      <a href="#"><img src="images/promotion1.png" alt="LYVE Games,Poker Websites in India, most trusted poker online sites in India"></a>
                    </div>
                    <div class="item">
                      <a href="#"><img src="images/promotion1.png" alt="LYVE Games, Learn Poker Online, Poker Mantra, Poker Tricks, Online Games in India"></a>
                    </div>
                    <div class="item">
                      <a href="#"><img src="images/promotion1.png" alt="Lyve Games, Learn Poker Online, Poker Mantra, Learn Poker, most trusted poker online sites in India"></a>
                    </div>
                    <div class="item">
                      <a href="#"><img src="images/promotion1.png" alt="yve Games, Learn Poker Online, Poker Mantra, Poker Websites, Poker Sites with Freerolls India"></a>
                    </div>
                    <div class="item">
                      <a href="#"><img src="images/promotion1.png" alt="Lyve Games, Learn Poker Online, Poker Mantra, Learn Poker,Online Gaming Companies in India"></a>
                    </div>

                  </div>
                </div>
              </div>
            </section>
</div> -->
      </div>
    </div>

    <div class="col-md-5 col-sm-12 col-xs-12">
      <div class="quiz-right-part">
        <div class="level-bar-right">
   <p>
								Poker players are certainly to be inquisitive thinkers and actively
								try to seek answers to questions for the players sitting round the
								table. As we all know, Poker is a game of variables, so players
								good in mathematics have an edge over another who merely play on
								courage and experiences. If conceptualised, each time one sit to
								play Poker &#8211; in a way &#8211; willingly subjecting to a
								certain type of Poker Strategy Quiz. To comprehend Poker, we have
								devised strategic quiz play for our users. These questions are
								challenging, interesting &#8211; and fun too. <br> The Quiz
								segment is segregated into three divisions:
							</p>
							<ul class="quiz_level_list">
								<li>Beginner</li>
								<li>Intermediate</li>
								<li>Advanced</li>
							</ul>
							<div class="clearfix"></div>
							<h4 class="heading1">Poker Quiz - Features</h4>
							<ul>
								<li>As mentioned, there are three categories &#8211;
									Beginners, Intermediate & Advanced; where there are certain
									questions for which answers are not absolute. It means our experts
									have formulated the best options that players might choose to
									triumph.</li>
								<li>Each round has 10 questions.</li>
								<li>Participant has to score at least 70% to earn the
									certification and other bonus.</li>
								<li>Re-Attempts are allowed.</li>
							</ul>
						</div>
  </div>

  <div class="forum-card marginT-30">
    <div class="col-md-4 col-sm-4 gap-forum-5">
      <div class="panel panel-default artical-panel ">
<a href="${pageContext.request.contextPath}/pokermantra/video">
        <div class="panel-body padding-none">
            <iframe width="100%" height="125px" src="https://www.youtube.com/embed/Nw7fCNlrZAg" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
          </div>
        <div class="forum-footer">
          <h3 class="text-center">Video</h3>
        </div>
          </a>
      </div>
    </div>
    <div class="col-md-4 col-sm-4 gap-forum-5">
      <div class="panel panel-default artical-panel">
        <a href="${pageContext.request.contextPath}/pokermantra/illustrativeStudy">
          <div class="panel-body">
          <p> Our learning comic strips present an educative as well as entertaining experience.  </p>
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
          <p> Discussions are always healthy, cultivate conversations over our forum.
 </p>
        </div>
        <div class="forum-footer">
          <h3 class="text-center">FORUM</h3>
        </div>
      </a>
      </div>
    </div>
    <div id="forumApp" ng-controller="forumCtrl" ng-init="getBlog()">
    <div class="col-md-4 col-sm-4 gap-forum-5">
      <div class="panel panel-default artical-panel">
     <a href="${pageContext.request.contextPath}/pokermantra/PokerMantraArticle/{{articleslug}}">
          <div class="panel-body">
          <p>Navigate a large collection of Poker education resource to cultivate your Poker skills. </p>
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
								alt="Learn Poker">
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
									class="text-rupee">&#8377;<strike> {{row.basePrice}} </span> 
								</strike> &nbsp;<span class="text-rupee">&#8377;{{row.salePrice}} </span> 
							</p>  
						</div>  
					</div> 
					<p class="text-center"> 
						<a href="" class="btn btn-gradient no-margin b0" 
							ng-click="processToPayForPdfDownload()"> <span>PROCEED TO PAY</span> 
						</a> 
					</p> 
				</div> 
			</div> 
		</div>  
	</div> 
  </div>
  </div>
      </div>
    </div>

  </div>


  </div> 
