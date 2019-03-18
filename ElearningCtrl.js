function elearningCtrl($scope, $http, $location, $window, $timeout, $interval, 
		$rootScope,  MCashService, PlayerData) {

	$scope.question = null;
	var quizId = null;
	var questionId = null;
	$scope.questionIndex = null;
	$scope.isCheck = false;
	$scope.button = {};
	$scope.quiz = {};
	$scope.response = {};
	$scope.quiz.answer = null;
	$scope.showResult = false;    
	$scope.loading = false;
	$scope.submitVisibility = true;
	$scope.isError = false;
	$scope.errorMessage = null;
	$scope.article = {};
	$scope.completed = true;
	$scope.notSubmit = false;
	$scope.finish = false;
	$scope.score = null;
	$scope.article.search = null;
	$scope.below = false;
	$scope.answer = null;
	$scope.nextVisibility = true;
	$scope.downloadVisible = true;
	$scope.nav = {
		elearning : {
			isError : false,
			isResponse : false,
			active : true
		}
	};
	$scope.elearning = {};

	var monthNames = [ "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" ];
	var countdown = $("#countdown").countdown360({
		radius : 60,
		/* strokeStyle : "rgb(243, 146, 30)", */
		strokeStyle : "transparent",
		strokeWidth : 8,
		fillStyle : "transparent",
		seconds : 0,
		label : [ '', '' ],
		fontSize : 40,
		fontColor : '#000000',
		autostart : false,
		clockwise : true,
		onComplete : function() {
			$('#submitButton').click()
		}
	});
	var countdownResponsive = $("#countdownResponsive").countdown360({
		radius : 60,
		 strokeStyle : "rgb(243, 146, 30)", 
		strokeStyle : "transparent",
		strokeWidth : 8,
		fillStyle : "transparent",
		seconds : 0,
		label : [ '', '' ],
		fontSize : 40,
		fontColor : '#000000',
		autostart : false,
		clockwise : true,
		onComplete : function() {
			$('#submitButton').click()
		}
	});
	$scope.getQuestions = function() {
		$scope.score = null; 
		$scope.loading = true;
		$scope.question = null;
		var level = null;
		if ($location.$$absUrl.includes("/basic")) 
			level = "BASIC";
		if ($location.$$absUrl.includes("/intermediate"))
			level = "INTERMEDIATE";
		if ($location.$$absUrl.includes("/advance"))
			level = "ADVANCE";
		var responsePromise = $http.get(contextPath + '/get/quiz?level='
				+ level);
		responsePromise
				.success(function(data) {
					if (data.status == 'SUCCESS') {
						$scope.button.label = 'Next';
						$scope.ansDescription = null; 
						if (data.response.completed) {
							$scope.showResult = false;
							$scope.isCheck = false;
							$scope.finish = false;
							$scope.completed = data.response.completed + 1;
							$scope.question = data.response.question;
							$scope.questionIndex = data.response.questionIndex + 1;
							quizId = data.response.id;
							questionId = data.response.question.id;
							countdown.settings.seconds = data.response.question.completeIn;
							countdown.start();
							countdownResponsive.settings.seconds = data.response.question.completeIn;
						    countdownResponsive.start();
							$timeout(function() {
								$scope.loading = false;
							}, 1000);
						} else {
							$scope.loading = false;
							$scope.completed = data.response.completed;
							$scope.completedMsg = data.response.msg;
							$scope.question = data.response.question;
							$scope.questionIndex = data.response.questionIndex + 1;
							quizId = data.response.id;
							questionId = data.response.question.id;
							countdown.settings.seconds = data.response.question.completeIn;
							countdownResponsive.settings.seconds = data.response.question.completeIn;
							if (MCashService.get("quiz.resume.active")) {
								$scope.completeQuiz('YES');
								MCashService.remove('quiz.resume.active');
							} 
						} 
					} else if (data.status == 'FAILED') {
						countdown.stop();
						countdownResponsive.stop();
						$scope.loading = false;
						$scope.isError = true;
						$scope.errorMessage = data.errorDetails;
					} else { 
						countdown.stop(); 
						countdownResponsive.stop();
						$scope.ressetMailErrorMessage = data.errorDetails; 
					} 
				}) 
				
	} 
	$scope.result = function(value) { 
		var responsePromise = $http.get(contextPath
				+ '/get/quiz/result?quizId=' + quizId);
		responsePromise.success(function(data) {
			if (data.status == 'SUCCESS') { 
				if (value == 'newQuiz')
					$scope.getQuestions();
				$scope.submitVisibility = true;
				$scope.isCheck = true;
				countdown.stop();
				countdownResponsive.stop();
			} else if (data.status == 'FAILED') {
				countdown.stop();
				countdownResponsive.stop();
				$scope.loading = false;
				$scope.isError = true;
				$scope.errorMessage = data.errorDetails;
			} else {
				countdown.stop();
				countdownResponsive.stop();
				$scope.loading = false;
				$scope.isError = true;
				$scope.errorMessage = data.errorDetails;
			}
		})
	}
	$scope.completeQuiz = function(value) {
		if (value == 'YES') {
			countdown.start();
			countdownResponsive.start();
			$scope.completed = true;
		} else {
			$scope.result('newQuiz');
		}
	}
	$scope.getNextQuestion = function(index) { 
		if (index < 10) { 
			$scope.answer = null; 
			$scope.loading = true; 
			$scope.question = null; 
			var responsePromise = $http 
					.get(contextPath + '/get/quiz/question?quizId=' + quizId 
							+ '&index=' + index);  
			responsePromise 
					.success(function(data) {  
						if (data.status == 'SUCCESS') {  
							$scope.submitVisibility = true;  
							$scope.nextVisibility = true; 
							countdown.settings.seconds = data.response.question.completeIn + 1; 
							countdown.start(); 
							countdownResponsive.settings.seconds = data.response.question.completeIn; 
							countdownResponsive.start();
							$scope.ansDescription = null; 
							$scope.showResult = false; 
							$scope.isCheck = false; 
							$scope.quiz.answer = null; 
							$scope.question = data.response.question; 
							$scope.questionIndex = data.response.questionIndex + 1; 
							quizId = data.response.id; 
							questionId = data.response.question.id; 
							$timeout(function() { 
								$scope.loading = false; 
							}, 1000);  
							if (index == 9)  
								$scope.button.label = 'Finish'; 
						} else if (data.status == 'FAILED') {  
							countdown.stop(); 
							countdownResponsive.stop();
							$scope.loading = false; 
							$scope.isError = true; 
							$scope.errorMessage = data.errorDetails; 
						} else { 
							countdown.stop(); 
							countdownResponsive.stop();
							$scope.loading = false; 
							$scope.isError = true; 
							$scope.errorMessage = data.errorDetails; 
						} 
					}) 
		} else { 
			$scope.submitVisibility = true; 
			$scope.isCheck = true; 
			var responsePromise = $http.get(contextPath
					+ '/get/quiz/result?quizId=' + quizId);
			responsePromise.success(function(data) {
				if (data.status == 'SUCCESS') {
					if (data.response.score >= 75) {
						$scope.below = false;
					} else {
						$scope.below = true;
					}
					$scope.finish = true;
					$scope.submitVisibility = true;
					$scope.isCheck = true;
					$scope.score = data.response.score;
					$scope.totalQuestion = data.response.totalQuestion;
					$scope.totalCorrect = data.response.totalCorrect;
					if (data.response.level == 'BASIC')
						$scope.level = 'Basic';
					else if (data.response.level == 'INTERMEDIATE')
						$scope.level = 'Intermediate';
					else if (data.response.level == 'ADVANCE')
						$scope.level = 'Advance';
					if (data.response.playerTitle != null
							&& data.response.playerTitle != "")
						$scope.title = data.response.playerTitle;
					else
						$scope.title = 'You have';
					var date = new Date(data.response.date);
					$scope.date = monthNames[date.getMonth()] + ' '
							+ dateOrdinal(date.getDate()) + ', '
							+ date.getFullYear();
					countdown.stop(); 
					countdownResponsive.stop();
				} else if (data.status == 'FAILED') { 
					countdown.stop(); 
					countdownResponsive.stop(); 
					$scope.loading = false; 
					$scope.isError = true;
					$scope.errorMessage = data.errorDetails;
				} else { 
					countdown.stop();
					countdownResponsive.stop();
					$scope.loading = false;
					$scope.isError = true;
					$scope.errorMessage = data.errorDetails;
				} 
			}) 
 
		} 
	} 
 
	function dateOrdinal(d) {
		return d
				+ (31 == d || 21 == d || 1 == d ? "st"
						: 22 == d || 2 == d ? "nd" : 23 == d || 3 == d ? "rd"
								: "th")
	};
	$scope.selectAns = function(ans) {
		if (ans != null) { 
			$scope.answer = ans;
			$scope.submitVisibility = false;
		}
	}
	
	$scope.submitAnswer = function(answer) { 
		$scope.submitVisibility = true; 
		var responsePromise = $http.get(contextPath 
				+ '/check/quiz/answer?quizId=' + quizId + '&questionId=' 
				+ questionId + '&answer=' + answer); 
		responsePromise.success(function(data) { 
			if (data.status == 'SUCCESS') { 
				if (answer == null) { 
					$scope.notSubmit = true; 
					$('#' + data.response.correctOption).attr('class', 
							'notAttemp_ans'); 
				} else 
					$scope.notSubmit = false; 
				$scope.nextVisibility = false; 
				countdown.stop();  
				countdownResponsive.stop();
				$scope.showResult = true; 
				$scope.isCheck = true; 
				$scope.ansDescription = data.response.answerDescription; 
				$scope.result = data.response.result; 
				if ($scope.result) 
					$('#' + answer).attr('class', 'right-Ans'); 
				else { 
					$('#' + answer).attr('class', 'wrong-Ans'); 
					$('#' + data.response.correctOption).attr('class', 
							'right-Ans');  
				} 
			} else if (data.status == 'FAILED') { 
				countdown.stop();
				countdownResponsive.stop();
				$scope.loading = false; 
				$scope.isError = false;  
				$scope.errorMessage = data.errorDetails; 
			} else { 
				countdown.stop(); 
				countdownResponsive.stop();
				$scope.loading = false; 
				$scope.isError = true; 
				$scope.errorMessage = data.errorDetails; 
			} 
		}) 
	} 
	$scope.updateName = function() { 
		var map = {}; 
		if ($scope.firstName != undefined && $scope.firstName != '') { 
			if ($scope.lastName != undefined && $scope.lastName != '') { 
				map['profile.firstName'] = $scope.firstName; 
				map['profile.lastName'] = $scope.lastName; 
				var responsePromise = $http.post(contextPath 
						+ "/player/personal/detail/update", { 
					requestData : map 
				}); 
				responsePromise 
						.success(function(data, status, headers, config) { 
							if (data.status == 'SUCCESS') { 
								$('.full-name-dialog').modal('hide'); 
								$scope.title = $scope.firstName + " " 
										+ $scope.lastName; 
								$scope.downloadCertificate(); 
								$scope.firstName = null; 
								$scope.lastName = null; 
							} else { 
 
							} 
						}) 
			} else { 
				// $scope.loading = false; 
			} 
		} else { 
			// $scope.loading = false; 
		} 
	} 

	/* For download Image start */ 

	$scope.checkFullNameAndDownload = function(score, level) { 
		 $http.get(contextPath + '/player/personal/detail/get') 
				.success( 
						function(data) { 
							if (data.status == "SUCCESS") { 
								$scope.personalDetail = data.response; 
								$scope.downloadVisible = true; 
								if (($scope.personalDetail.firstName != null && $scope.personalDetail.lastName != null)  
										&& ($scope.personalDetail.firstName != "" && $scope.personalDetail.lastName != "" && score >= 70)) {  
									     $scope.downloadCertificate($scope.personalDetail.firstName + " "+ $scope.personalDetail.lastName, level, score); 
									// $('.full-name-dialog').modal('show');  
								} else {  
									$('.full-name-dialog').modal('show'); 
									$scope.errorMsg = "To get certificate score should be more than 70.00 %"; 
								} 
							} else { 
								$scope.errorMsgDetails = "No data founds"; 
							}  
						})  
		} 
	$scope.downloadCertificate = function(title, level, score) { 
		if (title != null) { 
			var titleTemp = title.toUpperCase(); 
			title = titleTemp  
		} 
		var ctx = demo.getContext('2d'), w = demo.width, h = demo.height, img = new Image();
		img.onload = function() {
			ctx.drawImage(img, 0, 0, w, h);
			ctx.fillStyle = '#000';
			ctx.font = '57px Freehand521 BT';
			ctx.fillText(' Certificate of Completion ', 290, h / 2 - 200);
			ctx.font = '47px Freehand521 BT'; 
			ctx.fillText('This is to certify that ' + title, 270, h / 2 - 80);
			if (level == 'Intermediate')
				ctx.fillText('has successfully completed the ' + level
						+ ' level in', 120, h / 2 - 25);
			else
				ctx.fillText('has successfully completed the ' + level
						+ ' level in', 185, h / 2 - 25);
			ctx.fillText('Poker Quiz with ' + score.toFixed(0) + '% on '
				 + '.', 220, h / 2 + 30);
			if (level != null)
				download(demo, 'LyveGames-certificate-' + level
						+ '.png');
		}
		img.crossOrigin = 'anonymous';
		img.src = document.getElementById("certificateImage").src;
	}

	function download(canvas, filename) {
		if (typeof filename !== 'string' || filename.trim().length === 0)
			filename = 'Untitled';
		var lnk = document.createElement('a'), e;
		lnk.download = filename;
		lnk.href = canvas.toDataURL();
		if (document.createEvent) {
			e = document.createEvent("MouseEvents");
			e.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false,
					false, false, false, 0, null);
			lnk.dispatchEvent(e);
		} else if (lnk.fireEvent) {
			lnk.fireEvent("onclick");
		}
	}

	$scope.myFilter = function(level) {  
		var i = 0;
		$scope.tempDisplayedCollection = [];
		if (level == "BASIC") {
			for (i = 0; i < $scope.displayedCollection.length; i++) {
				if ($scope.displayedCollection[i].level == "BASIC")
					$scope.tempDisplayedCollection.push($scope.displayedCollection[i]);
			} 

		} else if (level == 'INTERMEDIATE') { 
			for (i = 0; i < $scope.displayedCollection.length; i++) {
				if ($scope.displayedCollection[i].level == "INTERMEDIATE")
					$scope.tempDisplayedCollection.push($scope.displayedCollection[i]);

			}

		}
		else if (level == 'ADVANCE')
			for (i = 0; i < $scope.displayedCollection.length; i++) {
				if ($scope.displayedCollection[i].level == "ADVANCE")
					$scope.tempDisplayedCollection.push($scope.displayedCollection[i]);

			}
		else
			for (i = 0; i < $scope.displayedCollection.length; i++) {
				
					$scope.tempDisplayedCollection.push($scope.displayedCollection[i]);

			}

	};
	
	/* For leaderboard  */
	
	$scope.getleader = function() {

		var completed = true;
		$scope.isLoading = true;

		var responsePromise = $http.get(contextPath
				+ '/pokermantra/leaderboard/get?completed=' + completed);
		responsePromise.success(function(data) {
			$scope.displayedCollection = [];
			$scope.tempDisplayedCollection = [];
			$scope.elearning = data.response;
			if (data.status == "SUCCESS") {
				$scope.rowCollection = data.response;
				if ($scope.rowCollection.length != 0) {
					$scope.nav.elearning.isError = false;
					$scope.displayedCollection = []
							.concat($scope.rowCollection);
					$scope.tempDisplayedCollection = []
							.concat($scope.rowCollection);
				} else {
					$scope.nav.elearning.isError = true;
					$scope.response.error = data.successMsg;
				}
			} else {
				$scope.responseMsg = data.errorDetails;
			}
			$scope.isLoading = false;
		})
	}
	
 ///// Pay and Download Begin ////    
    
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

                    } else {
                        $scope.isError = true;
                    }
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
                $scope.isLoading = false;
            })
        } else
            $scope.openLoginModal();
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
    
    //// Pay and Download End ////
    
    $scope.getSlug = function(slug) {
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
                     for (i = 0; i < data.response.topics.length; i++)
                    	 {
                    	  if(data.response.topics[i].title == "ARTICLE")
                    		 $scope.articleslug = data.response.topics[i].slug;
                    	  if(data.response.topics[i].title == "ILLUSTRATION")
                     		 $scope.illustrationslug = data.response.topics[i].slug;
                    	 }
                    
                } else {
                    $scope.responseMsg = data.errorDetails;
                }
                $scope.isLoading = false;
            });
    }
	
} 


