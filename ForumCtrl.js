function forumCtrl($scope, $http, $location, $timeout, $interval, $rootScope, $window,
    PlayerData, SharedDataService) {
    $rootScope.SharedDataService = SharedDataService;
    $scope.article = {};
    $scope.forumListSearch = [];
    $scope.blogForum = {};
    $scope.blogForum.title="Select Topic";
    $scope.listAll = true;
    $scope.tempForumList = [];
    $scope.filterTopicList = [];
    $scope.isSearch = false;
    $scope.article.search = null;
    $scope.isCommentError = false;
    $scope.loading = false;
    $scope.forumListtwo=[]; 
    $scope.commentList=[];
    
    // // Get UserName Methods ////
	
	$scope.getPlayerUserName = function() {
		$scope.playerUserName = PlayerData.getUserName();
	}
	
	// / Get Checkin Method ////
	$scope.getcheckin = function() {   
		 $scope.openLoginDialog();
	}
	// // Fetching Forum from Blog ////
	
    $scope.getBlog = function(slug) {
        $scope.isLoading = true;
        $scope.loading = true;
        if ($scope.article.search == '') {
            $scope.isSearch = false;
            $scope.article.search = null;
        }
        var responsePromise = $http.get(contextPath + '/blog/list?query=' +
            $scope.article.search + '&slug=' + slug);
        responsePromise
            .success(function(data) {
                if (data.status == "SUCCESS") {
                    $scope.domain = data.domain;
                     for (i = 0; i < data.response.topics.length; i++)
                    	 {
                    	  if(data.response.topics[i].title == "ARTICLE")
                    		 $scope.articleslug = data.response.topics[i].slug;
                    	  if(data.response.topics[i].title == "ILLUSTRATION")
                     		 $scope.illustrationslug = data.response.topics[i].slug;
                    	 }
                    if (data.response.blog) {
                        $scope.blog = data.response.blog;
                    }
                    $scope.loading = false;
                    if ($scope.article.search != null)
                        $scope.isSearch = true;
                    $scope.forumList = [];
                    if (PlayerData.isPlayerLoggedIn()) {
                    	$scope.playerID = PlayerData.getPlayerLoginData().playerId;
                    }
                    for (i = 0; i < data.response.blogs.length; i++) {
                        if (data.response.blogs[i].topic == "FORUM" &&  data.response.blogs[i].pokerMantra == true){
                        	if(data.response.blogs[i].likes.includes($scope.playerID))
                        		data.response.blogs[i].iscolor = true;
                        	for (j = 0; j < data.response.blogs[i].comments.length; j++){
                                if(!/^(f|ht)tps?:\/\//i.test(data.response.blogs[i].comments[j].image)) 
                            		data.response.blogs[i].comments[j].image = contextPath + '/resources/lyve' + data.response.blogs[i].comments[j].image;
                        	}
                        	data.response.blogs[i].image = data.response.blogs[i].imageUrl;
                        	data.response.blogs[i].userName = data.response.blogs[i].title;
                            $scope.forumList.push(data.response.blogs[i]);
                        }
                        $scope.forumListSearch.push(data.response.blogs[i])
                    }
                    $scope.forumList = $scope.forumList.concat($scope.forumListtwo);
                    $scope.forumList.reverse();
                    $scope.tempForumList = $scope.forumList;
                    $scope.tempForumList.reverse();
                    $scope.topics = data.response.topics;
                    if (slug != null && $scope.topics.length != 0) {
                        for (var key in $scope.topics) {
                            if ($scope.topics[key].slug == slug)
                                $scope.topics[key].isTopic = true;
                        }
                    }
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
                $scope.isLoading = false;
            });
    }
    

    // Fetching Forum //
    $scope.getForum = function(slug) {
        $scope.forumList = [];
        $scope.isLoading = true;
        $scope.loading = true;
        if ($scope.article.search == '') {
            $scope.isSearch = false;
            $scope.article.search = null;
        }
        var responsePromise = $http.get(contextPath + '/forum/list?query=' +
            $scope.article.search + '&slug=' + slug);
        responsePromise
            .success(function(data) {
                if (data.status == "SUCCESS") {
                    if (data.response.blog) {
                        $scope.blog = data.response.blog;
                    }
                    $scope.loading = false;
                    if ($scope.article.search != null)
                        $scope.isSearch = true;
                    $scope.forumListone = [];
                    $scope.forumListtopic = [];
                    $scope.topicListToDisplay = [];
                    for (i = 0; i < data.response.forums.length; i++) {
                        if (data.response.forums[i].title != null &&
                            data.response.forums[i].status == "ENABLE" &&
                            data.response.forums[i].metaDescription == null)
                            $scope.forumListone.push(data.response.forums[i]);
                            $scope.forumListtopic.push(data.response.forums[i]);  
                        $scope.forumListSearch.push(data.response.forums[i])
                        if (!$scope.topicListToDisplay.includes(data.response.forums[i].title)) {
                            $scope.topicListToDisplay.push(data.response.forums[i].title)
                        }
                    }
                    $scope.forumListtwo = [];
                    if (PlayerData.isPlayerLoggedIn()) {
                    	$scope.playerID = PlayerData.getPlayerLoginData().playerId;
                    }
                    for (i = 0; i < data.response.forums.length; i++) {
                        if (data.response.forums[i].title != null &&
                            data.response.forums[i].status == "ENABLE" &&
                            data.response.forums[i].metaDescription != null) {
                        	if(data.response.forums[i].likes.includes($scope.playerID))
                        		data.response.forums[i].iscolor = true;
                        	if(!/^(f|ht)tps?:\/\//i.test(data.response.forums[i].image)) 
                        		data.response.forums[i].image = contextPath + '/resources/lyve' + data.response.forums[i].image;
                            $scope.forumListtwo.push(data.response.forums[i]);
                        }
                        $scope.forumListSearch.push(data.response.forums[i]);
                    }
                    $scope.forumList = $scope.forumList.concat($scope.forumListtwo);                    
                    $scope.tempForumList = $scope.forumList;
                    $scope.tempForumList.reverse();
                    $scope.forumListtwo.reverse();
                  
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
                $scope.isLoading = false;
            });
    }
    $scope.changeTitle = function(title) {
        $scope.blogForum.title = title;
    }
    $scope.searcharticle = function(value) {
        if ($scope.article.search == '')
            $scope.getarticle(value);
    }
    $scope.showDescription = function(article) {
        $scope.currentTitle = article.shortContent;
        $scope.currentContent = article.content;
    }
    
       // //Ask Question Begin ////

    $scope.emptyField = function() {
        $scope.isError = false;
        contents = $scope.blogForum.content.replace(/<\/?[^>]+(>|$)/g, "");
        if ($scope.blogForum.forumshortContent == '' ||
            $scope.blogForum.forumshortContent == undefined) {
            $scope.isBlank = true;
            $scope.blankMsg = 'Short content is required.';
        } else if ($scope.blogForum.forumshortContent != '' &&
            $scope.blogForum.forumshortContent != undefined &&
            $scope.blogForum.forumshortContent.length > 150) {
            $scope.isBlank = true;
            $scope.blankMsg = 'Short content length less than or equal to 150.';
        } else if (contents == '' || contents == undefined) {
            $scope.isBlank = true;
            $scope.blankMsg = 'Contents is required.';
        } else {
            $scope.isBlank = false;
        }
    }

    
    $scope.blogQuestionAnswer = function() {
        if (PlayerData.isPlayerLoggedIn()) {
            $scope.isError = false;
            if ($scope.blogForum.title != null && $scope.blogForum.title != '') {
                if ($scope.blogForum.forumshortContent != null &&
                    $scope.blogForum.forumshortContent != '') {
                    if ($scope.blogForum.forumcontent != null &&
                        $scope.blogForum.forumcontent != '') {
                        $scope.myForm.$pristine = true;
                        var data = {};
                        data = {

                            title: $scope.blogForum.title,
                            forumshortContent: $scope.blogForum.forumshortContent,
                            forumcontent: $scope.blogForum.forumcontent
                        }
                        var responsePromise = $http({
                            method: 'POST',
                            url: contextPath + '/forum/create',
                            headers: {
                                'Content-Type': undefined
                            },
                            data: {
                                blog: data
                            },
                            transformRequest: function(data) {
                                var formData = new FormData();
                                formData.append("file", data.file);
                                formData.append("blog", angular.toJson(data.blog));
                                return formData;
                            }
                        }).success(function(data) {
                            if (data.status == 'SUCCESS') {
                                $scope.isError = false;
                                $scope.isSuccess = true;
                                // $scope.successMessage = data.successMsg;
                                $scope.successMessage = "Your question has been posted successfully";
                                $timeout(function() {
                                    $('#askQuestions').modal('hide');
                                    $state.go("app.blog.list");
                                }, 1000);
                            } else {
                                $scope.isError = true;
                                $scope.myForm.$pristine = false;
                                $scope.errorMessage = data.errorDetails;
                            }
                        })
                    } else {
                        $scope.isError = true;
                        $scope.myForm.$pristine = false;
                        $scope.errorMessage = 'Comment is required.';
                    }
                } else {
                    $scope.isError = true;
                    $scope.myForm.$pristine = false;
                    $scope.errorMessage = 'Main Question  is required.';
                }
            } else {
                $scope.isError = true;
                $scope.myForm.$pristine = false;
                $scope.errorMessage = 'Topic is required.';
            }
        } else{
        	$scope.isError = true;
            $scope.myForm.$pristine = false;
            $scope.errorMessage = 'Please login first.';
        }
        	
    }
           // // Ask Question End ////
    
    $scope.commentindex = function(forumList) {
        $scope.currentId = forumList.id;
    }
    $scope.sendIdComment = function(forumList) {
        $scope.currentId = forumList.id;
    }
    $scope.sendIdDetailsComment = function(forumlistdetails) {
        $scope.currentId = forumlistdetails.id;
    }
    
    // // Pay and Download Begin ////
    
    $scope.getPrice = function() {
        if (PlayerData.isPlayerLoggedIn()) {
            // $('.pdfPopup').modal('show');
            $('#pdfPopup').modal('show');
            $scope.displayedCollection = [];
            var status = "ENABLE";
            $scope.isLoading = true;
            $scope.isError = false;
            var responsePromise = $http.get(contextPath +
                '/pokermantra/price/get?status=' + status);
            responsePromise.success(function(data) {
                if (data.status == "SUCCESS") {
                    $scope.rowCollection = data.response;
                    if ($scope.rowCollection.length != 0) {
                        $scope.isError = false;
                        $scope.displayedCollection = []
                            .concat($scope.rowCollection);
                        $scope.payPrice = $scope.rowCollection[0].salePrice;
                    } 
                    else {
                        $scope.isError = true;
                    }
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
                $scope.isLoading = false;
            })
        } else
            $scope.openLoginDialog();
    }
    $scope.processToPayForPdfDownload = function() {
        $scope.isError = false;
        $scope.amountTest = $scope.amount;
        $scope.amount = $scope.payPrice;
        if ($scope.amount > 9 && $scope.amount <= 1000000) {
        	makePaymentRequestForPdfDownload($scope.amount)
        } else {
            $scope.isError = true;
        }
    }
    var paymentwindow = null;

    function makePaymentRequestForPdfDownload(amount) {
        if (paymentwindow)
            paymentwindow.close();
        var w = screen.width;
        var h = screen.height;
        var width = 982;
        var height = 705;
        var left = (w / 2) - (width / 2);
        var top = (h / 2) - (height / 2);
        paymentwindow = window.open(contextPath + "/payment/pdfDownload/popup", 'Payment', 'height=' + height + ',width=' + width +
            ',left=' + left + ',top=' + top);
        paymentwindow.amount = amount;
        paymentwindow.code = $scope.code;
    }
    
    if (paymentStatus == "true") {
        $scope.buyInDialog = false;
        $scope.paymentSuccessDialog = true;
        var responsePromise = $http.get(contextPath +
                '/pokermantra/price/get?status=' + status);
            responsePromise.success(function(data) {
                if (data.status == "SUCCESS") {
                 $window.location.href = data.response[0].pdfUrl;
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
        })
    }
    
         // // Pay and Download End ////

    $scope.filterTopic = function(topic, status) {
        $scope.tempForumList = [];
        if (status == true) {
            $scope.filterTopicList.push(topic);
            for (var i = 0; i < $scope.forumList.length; i++) {
                if ($scope.filterTopicList.includes($scope.forumList[i].title))
                    $scope.tempForumList.push($scope.forumList[i]);
            }
        } else {
            $scope.filterTopicList.splice($scope.filterTopicList.indexOf(topic), 1);
            if ($scope.filterTopicList.length == 0)
                $scope.tempForumList = $scope.forumList;
            else {
                for (var i = 0; i < $scope.forumList.length; i++) {
                    if ($scope.filterTopicList.includes($scope.forumList[i].title))
                        $scope.tempForumList.push($scope.forumList[i]);
                }
            }
        }

    }
    
    // // Polls Begin ////
    
    $scope.getVoteQuestions = function() {
        $scope.isLegalLoading = true;
        $scope.data = [];
        var responsePromise = $http.get(contextPath + '/get/vote/question');
        responsePromise.success(function(data) {
            if (data.status == 'SUCCESS') {
            	if(data.response.voteList.length > 0) {
                $scope.voteQuestion = data.response.voteList[0].description;
                $scope.pollOption = data.response.voteList[0].options;
                $scope.id = data.response.voteList[0].id;
                $scope.voteAnswer = data.response.answerList;
                $scope.totalVotes = data.response.totVot;
                $scope.totalVot = data.response.voteList[0].totalVote;
                $scope.totalPerc = data.response.totalPercent;
                $scope.totalQuestion = data.response.questionList;
                $scope.totalId = data.response.questionList[0].id;
                if (data.response.voteList[0].voteEmail.includes(data.response.userEmail))
                    $scope.isVoted = true;
            	}

            } else {
                $scope.pageError = true;
                $scope.page = {
                    title: 'Error Page'
                };
                $scope.responseError = data.errorDetails;
            }
            $scope.dataLoading = false;
        })
    }
    $scope.selectAnsw = function(ans) {
        if (ans != null) {
            $scope.voteAns = ans;
        }
    }
    $scope.checkSession = function(){
    	if (PlayerData.isPlayerLoggedIn()) 
    		$('.pdfHistory').modal('show');
    	else
    		$scope.openLoginDialog();
    }
    $scope.sendVote = function(voteAns) {
    	if (voteAns == undefined)
    		voteAns = 0;
    	if (PlayerData.isPlayerLoggedIn()) {
    	$scope.totalVotes = $scope.totalVotes + 1;
        var responsePromise = $http.get(contextPath + '/set/vote/?id=' + $scope.id + '&voteAns=' + voteAns
        		+ '&totalVotes=' + $scope.totalVotes);
        responsePromise.success(function(data) {
            if (data.status == 'SUCCESS') {
                $scope.getVoteQuestions();
                if (voteAns == null) {
                    $scope.notSubmit = true;
                } else
                    $scope.notSubmit = false;
                $scope.nextVisibility = false;
            }
        })
    	} else 
    		$scope.openLoginDialog();
    }
    
    $scope.getQuestionHistory = function(questionId) {
        var responsePromise = $http.get(contextPath + '/get/question/description/?questionId=' + questionId);
        responsePromise.success(function(data) {
            if (data.status == 'SUCCESS') {
                $scope.voteQuestion = data.response.voteList[0].description;
                $scope.pollOption = data.response.voteList[0].options;
                $scope.totalVot = data.response.voteList[0].totalVote; 
                $scope.voteAnswer = data.response.answerList;
                // $scope.totalVotes = data.response.totalVote;
                $scope.totalPerc = data.response.totalPercent;
            } else {
                $scope.pageError = true;
                $scope.page = {
                    title: 'Error Page'
                };
                $scope.responseError = data.errorDetails;
            }
            $scope.dataLoading = false;
        })
    }
            // // Polls End ////
    
    
         // // Sending Data On Forum_details Begin ////
    
    $scope.setDetails = function(blogId, index) {
        $scope.ind = index;
        $scope.currentId = blogId;
        var responsePromise = $http.get(contextPath +
            '/blog/setDefault?blogId=' + blogId);
        responsePromise
            .success(function(data) {
                if (data.status == "SUCCESS") {
                    var slug1 = "";
                    $scope.getForum(slug1);
                    $scope.getBlog(slug1);
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
                $scope.isLoading = false;
            });
    }

    $scope.getLatestBlog = function() {
        $scope.likeArray = [];
        var responsePromise = $http.get(contextPath +
            '/blog/getDefault');
        responsePromise
            .success(function(data) {
                if (data.status == "SUCCESS") {
                    if (data.response.forumshortContent != null)
                        $scope.title = data.response.forumshortContent;
                    else
                        $scope.title = data.response.title;
                    if (data.response.forumcontent != null)
                        $scope.content = data.response.forumcontent;
                    else
                        $scope.content = data.response.content;
                    $scope.length = data.response.likes.length;
                    $scope.likeTemp = 0;   
                    if (PlayerData.isPlayerLoggedIn()) {
                    	$scope.playerID = PlayerData.getPlayerLoginData().playerId;
                    }
                    $scope.likeArray = data.response.likes;
                    for (var i = 0; i < $scope.length; i++) {
                        if ($scope.playerID == $scope.likeArray[i]) {
                            $scope.likeTemp = 1;
                        }
                    }
                    $scope.likes = data.response.likesCount;
                    $scope.comments = data.response.commentsCount;
                    $scope.currentId = data.response.id;
                    for (var j = 0; j < data.response.comments.length; j++)
                    if(!/^(f|ht)tps?:\/\//i.test(data.response.comments[j].image)) {
                		data.response.comments[j].image = contextPath + '/resources/lyve' + data.response.comments[j].image;
                    }
                    $scope.commentList = data.response.comments;
                    $scope.commentList.reverse();
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
                $scope.isLoading = false;
            });
    }
                 // //Sending Data On Forum_details End ////
    
    
          // // Likes and Comments Begin ////
    
    $scope.sendForLike = function(forumlist) {
        if (PlayerData.isPlayerLoggedIn()) {
            var forum;
            forum = forumlist.userId;
            if (forum == '' || forum == undefined || forum == null)
                forum = forumlist.id;
            var responsePromise = $http.get(contextPath + '/forum/like?forum=' + forum);
            responsePromise
                .success(function(data) {
                    if (data.status == "SUCCESS") {
                        $scope.getLatestLike();
                    } else {
                        $scope.responseMsg = data.errorDetails;
                    }
                    $scope.isLoading = false;
                });
        } else
            $scope.openLoginDialog();
    }
    
    $scope.doLikeForum = function() {
        if (PlayerData.isPlayerLoggedIn()) {
            var article;
            article = $scope.currentId
            if (article == '' || article == undefined || article == null)
                article = forumlist.id;
            var responsePromise = $http.get(contextPath +
                '/article/like?article=' + article);
            responsePromise.success(function(data) {
                if (data.status == "SUCCESS") {
                    $scope.getLatestLike();
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
                $scope.isLoading = false;
            });
        } else
            $scope.openLoginDialog();
    }
    
    $scope.openLoginDialog = function() {
		SharedDataService.runCallBack('openLoginDialog');
	};
    /*
	 * $scope.openLoginModal = function() { if (!PlayerData.isPlayerLoggedIn()) {
	 * $('.login').modal('show'); } }
	 */
    
    $scope.getLatestLike = function() {
    	$scope.playerID = PlayerData.getPlayerLoginData().playerId;
        var responsePromise = $http.get(contextPath +
            '/forum/getLike');
        responsePromise
            .success(function(data) {
                if (data.status == "SUCCESS") {
                    $scope.recentLikeId = data.response.id;
                    for (var i = 0; i < $scope.tempForumList.length; i++) {
                        if ($scope.recentLikeId == $scope.tempForumList[i].id) {
                            $scope.tempForumList[i].likesCount = data.response.likesCount;
                            $scope.tempForumList[i].likes = data.response.likes;
                            break;
                        }
                    }
                    for (var i = 0; i < $scope.tempForumList.length; i++) {
                    if ($scope.recentLikeId == $scope.tempForumList[i].id) { 
                        if ($scope.tempForumList[i].likes.includes($scope.playerID)) {
                            $scope.tempForumList[i].iscolor=true;
                            break;
                    }   }}
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
            });
    }


    $scope.postCommentHome = function() {
        if (PlayerData.isPlayerLoggedIn()) {
            $scope.isCommentError = false;
            if ($scope.commentBox != null || $scope.commentBox != '' ||
                $scope.commentBox != undefined) {
                        var data = {
                            comment: $scope.commentBox,
                            isCommentsEmail: $scope.isCommentsEmail,
                            isNewPostEmail: $scope.isNewPostEmail,
                            currentId: $scope.currentId
                        }
                        var responsePromise = $http.post(contextPath +
                            '/article/comments/', data)
                        responsePromise.success(function(data, status, headers,
                            config) {
                            if (data.status == 'SUCCESS') {                               
                                $scope.commentBox = null;
                                $scope.loading = false;
                                $scope.isCommentSuccess = true;
                                $scope.responseMsg = data.successMsg;
                                $scope.getLatestComment();
                                $('#commentForum').modal('hide');

                                $timeout(function() {
                                    $scope.nav.changePassword.isResponse = false;
                                }, 2000);

                            } else {
                                $scope.isCommentError = true;
                                $scope.loading = false;
                                $scope.errorMsg = data.errorDetails;
                            }
                        });
            } else {
                $scope.loading = true;
                $scope.isCommentError = true;
                $scope.isError = true;
                $scope.errorMsg = "Comment should not be empty"
            }
        } else{
        $scope.loading = true;
        $scope.isCommentError = true;
        $scope.isError = true;
        $scope.errorMsg = "please login first!"
    }
        }


    $scope.postComment = function() {
        if (PlayerData.isPlayerLoggedIn()) {
            $scope.isCommentError = false;
            if ($scope.commentBox != null || $scope.commentBox != '' ||
                $scope.commentBox != undefined) {
                        var data = {
                            comment: $scope.commentBox,
                            isCommentsEmail: $scope.isCommentsEmail,
                            isNewPostEmail: $scope.isNewPostEmail,
                            currentId: $scope.currentId
                        }
                        var responsePromise = $http.post(contextPath +
                            '/article/comments/', data)
                        responsePromise.success(function(data, status, headers,
                            config) {
                            if (data.status == 'SUCCESS') {                               
                                $scope.commentBox = null;
                                $scope.loading = false;
                                $scope.isCommentSuccess = true;
                                $scope.responseMsg = data.successMsg;
                                $scope.getLatestBlog();
                                $('#commentForumDetails').modal('hide');

                                $timeout(function() {
                                    $scope.nav.changePassword.isResponse = false;
                                }, 2000);

                            } else {
                                $scope.isCommentError = true;
                                $scope.loading = false;
                                $scope.errorMsg = data.errorDetails;
                            }
                        });
            } else {
                $scope.loading = true;
                $scope.isCommentError = true;
                $scope.isError = true;
                $scope.errorMsg = "Comment should not be empty"
            }
        } else
        	{
	        $scope.loading = true;
	        $scope.isCommentError = true;
	        $scope.isError = true;
	        $scope.errorMsg = "please login first!"
	        }
    }


    $scope.postCommentPokermantra = function() {
        if (PlayerData.isPlayerLoggedIn()) {
            $scope.isCommentError = false;
            if ($scope.commentBox != null || $scope.commentBox != '' ||
                $scope.commentBox != undefined) {
                        var data = {
                            comment: $scope.commentBox,
                            isCommentsEmail: $scope.isCommentsEmail,
                            isNewPostEmail: $scope.isNewPostEmail,
                            currentId: $scope.currentId
                        }
                        var responsePromise = $http.post(contextPath +
                            '/article/comments/', data)
                        responsePromise.success(function(data, status, headers,
                            config) {
                            if (data.status == 'SUCCESS') {                               
                                $scope.commentBox = null;
                                $scope.loading = false;
                                $scope.isCommentSuccess = true;
                                $scope.responseMsg = data.successMsg;
                                $scope.getLatestComment();
                                $('#commentPokerMantra').modal('hide');

                                $timeout(function() {
                                    $scope.nav.changePassword.isResponse = false;
                                }, 2000);

                            } else {
                                $scope.isCommentError = true;
                                $scope.loading = false;
                                $scope.errorMsg = data.errorDetails;
                            }
                        });

            } else {
                $scope.loading = true;
                $scope.isCommentError = true;
                $scope.isError = true;
                $scope.errorMsg = "Comment should not be empty"
            }
        } else{
        $scope.loading = true;
        $scope.isCommentError = true;
        $scope.isError = true;
        $scope.errorMsg = "Please login first!"
    }
        }

    $scope.getLatestComment = function() {   	
        var responsePromise = $http.get(contextPath +
            '/forum/getComment');
        responsePromise
            .success(function(data) {
                if (data.status == "SUCCESS") {
                    $scope.recentCommentId = data.response.id;
                    for (var i = 0; i < $scope.tempForumList.length; i++) {
                        if ($scope.recentCommentId == $scope.tempForumList[i].id) {
                            $scope.tempForumList[i].commentsCount = data.response.commentsCount;
                            $scope.commentList = data.response.comments;
                            $scope.comments = data.response.commentsCount;
                            break;
                        }
                    }
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
            });
    }

    $scope.likechange = function() {
        $scope.getLatestBlog();
    }

   
    // // Likes and Comments End ////
    
    
    
    // // Filters Begin ////
    
    $scope.getRecentQuestions = function() {
        $scope.tempForumList = $scope.forumList;
    }

    $scope.getRecentAnswers = function() {
        $scope.tempForumList = [];
        $scope.isLoading = true;
        var responsePromise = $http.get(contextPath +
            '/forum/getRecentAnswer');
        responsePromise.success(function(data) {
            if (data.status == "SUCCESS") {
                $scope.isLoading = false;
                $scope.recentAnswers = data.response.recentAnswers;
                for (var i = 0; i < $scope.forumList.length; i++) {
                    if ($scope.recentAnswers.includes($scope.forumList[i].id)) {
                        $scope.tempForumList.push($scope.forumList[i])
                    }
                }
            } else {
                $scope.responseMsg = data.errorDetails;
            }
            $scope.isLoading = false;
        });
    }

    $scope.getMostPopular = function() {
        var slug = "";
        $scope.tempForumList = [];       
        $scope.isLoading = true;
        var responsePromise = $http.get(contextPath + '/forum/getMostPopular?query=' +
            '&slug=' + slug);
        responsePromise
            .success(function(data) {
                if (data.status == "SUCCESS") {
                    $scope.tempForumList = data.response.forums;
                    $scope.tempForumList.sort(sortByProperty('likesCount'));
                    $scope.tempForumList.reverse();
                    for (var i = 0; i < $scope.tempForumList.length; i++) {
                            if ($scope.tempForumList[i].likes.includes($scope.playerID)) {
                                $scope.tempForumList[i].iscolor=true;
                           }
                            if(!/^(f|ht)tps?:\/\//i.test(data.response.forums[i].image)) {
                        		data.response.forums[i].image = contextPath + '/resources/lyve' + data.response.forums[i].image;
                            }}
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
                $scope.isLoading = false;
            });
    }

    var sortByProperty = function(likes) {
        return function(x, y) {
            return ((x[likes] === y[likes]) ? 0 : ((x[likes] > y[likes]) ? 1 : -1));

        };

    };
            
            // // Filters End ////
    
}
