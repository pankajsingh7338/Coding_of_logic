function homeCtrl($scope, $http, $location, $timeout, $interval, $rootScope, PlayerData, SharedDataService,
		MCashService) {
		
	$scope.authData = [];
	$scope.response = {};
	$scope.displayedCollection = []; 
	
	
	/*function loginValueChanged(value) {
		$timeout(function() {
			$scope.$apply(function($scope) { 
				if (!value) { 
				} else { 
					$scope.wallet = PlayerData.getPlayerWallet();
					if($scope.wallet) {
					$scope.classImage = $scope.wallet.playerClass.toLowerCase();
					var inx = $scope.classWheel.indexOf($scope.classImage + '_home.jpg')
					$scope.homeWheelSlider(inx, $scope.classImage);
					$scope.getPlayerClassOffers($scope.classImage);
					$scope.changeClassDetails($scope.classImage);
					$scope.cash = parseInt($scope.wallet.cash.replace(/,/g, ''));
					$scope.leaderBoardSize = $scope.wallet.leaderBoardList;
					$scope.playerAchievement = $scope.wallet.playerAchievement;
					}
					$scope.walletInterval = $interval(function() {
						$scope.wallet = PlayerData.getPlayerWallet();
						if ($scope.wallet) { 
							$scope.classImage = $scope.wallet.playerClass.toLowerCase();
							$scope.cash = parseInt($scope.wallet.cash.replace(/,/g, ''));
							if (MCashService.get('player.OldClass') != $scope.classImage) {
								MCashService.setMycache('player.OldClass', $scope.wallet.playerClass.toLowerCase());
								$scope.changeClassDetails($scope.classImage);
							} else {
								MCashService.setMycache('player.OldClass', $scope.wallet.playerClass.toLowerCase());
							}
							$scope.leaderBoardSize = $scope.wallet.leaderBoardList;
							$scope.playerAchievement = $scope.wallet.playerAchievement;
						} 
					}, 2000)
				}
			});
		});
	}*/
	
	/*PlayerData.onLoginValueChange(loginValueChanged);*/

	/*$scope.getAccessToken = function() {
		AuthAcessToken.getAuthAcessToken().success(function(data) {
			if (data.status) {
				$scope.authData = data.auth;
				$scope.getcricketschedule();
			}
		});
	}*/
	
	function AddZero(num) {
		return (num >= 0 && num < 10) ? "0" + num : num + "";
	}
	  
	/*$scope.getcricketschedule = function() {
		if ($scope.authData != null && $scope.authData.access_token != null) {
			var responsePromise = $http.get(contextPath
					+ '/cricket/match/schedule?accessToken='
					+ $scope.authData.access_token);
			responsePromise.success(function(data) {
				if (data.status) {

				} else {
					$scope.errorMsg = data.message;
				}
			})
		}
	} */

	$scope.callServer = function() { 
		$scope.isLoading = true; 
		var responsePromise = $http.get(contextPath
				+ '/player/get/match/schedule/list'); 
		responsePromise.success(function(data) { 
			if (data.status == 'SUCCESS') { 
				$scope.rowCollection = data.response; 
				$scope.rowCollection.reverse(); 
				if ($scope.rowCollection.length != 0) {
					var filData; 
					$scope.isError = false; 
						for (var i =0 ; i < $scope.rowCollection.length; i++) { 
							$scope.contestCount = $scope.rowCollection[i].contestDiamention.length; 
							filData = { 
									scheduledMatchInfo : $scope.rowCollection[i].scheduledMatchInfo, 
									scheduledTime : $scope.rowCollection[i].scheduledTime, 
									scheduledStatus : $scope.rowCollection[i].scheduledStatus, 
									scheduledMatchTitle : $scope.rowCollection[i].scheduledMatchTitle, 
									teamOneName :  $scope.rowCollection[i].cricketImageList[0].teamName, 
									teamOneUrl : $scope.rowCollection[i].cricketImageList[0].teamUrl, 
									teamTwoName : $scope.rowCollection[i].cricketImageList[1].teamName, 
									teamTwoUrl : $scope.rowCollection[i].cricketImageList[1].teamUrl,  
									contestCount : $scope.contestCount 
							} 
						 $scope.displayedCollection.push(filData); 
						} 
					$scope.total = $scope.rowCollection.length; 
					totalData = $scope.rowCollection.length; 
					if (totalData <= 10) { 
						$scope.paginationShow = false; 
					} else { 
						$scope.paginationShow = true; 
					} 
				} else { 
					$scope.isError = true; 
					$scope.paginationShow = false; 
					$scope.response.error = data.successMsg; 
				} 
			} else { 
				$scope.pageError = true; 
				$scope.page = { 
					title : 'Error Page', 
				}; 
				$scope.response.error = data.errorDetails; 
			} 
			$scope.dataLoading = false; 
			$scope.isLoading = false; 
		}) 
	}; 
	  
} 
  
  
