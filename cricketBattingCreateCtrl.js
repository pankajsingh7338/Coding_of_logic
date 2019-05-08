
'use strict'; 
app.controller('cricketBattingCreateCtrl', function($scope, $http, $state, 
		$stateParams, $timeout, $location) { 
	$scope.page = { 
		title : 'Points Scoring', 
	}; 
	  
	$scope.cricketPointsForm = {}; 
	$scope.battingCricketForm = {}; 
	$scope.errorMessage = ''; 
	$scope.isError = false; 
	$scope.rowCollection = []; 
	$scope.categoryList = []; 
	$scope.entityList = []; 
	
	$scope.emptyField = function() { 
		if ($scope.cricketPointsForm.title == undefined
				|| $scope.cricketPointsForm.title == '') { 
			$scope.isBlank = true; 
			$scope.blankMsg = 'Please Enter title.'; 
		} else if ($scope.cricketPointsForm.category == undefined
				|| $scope.cricketPointsForm.category == '') { 
			$scope.isBlank = true; 
			$scope.blankMsg = 'Please Select category.'; 
		} else if ($scope.cricketPointsForm.entity == undefined
				|| $scope.cricketPointsForm.entity == '') { 
			$scope.isBlank = false; 
			$scope.blankMsg = 'Please Select Entity'; 
		} else { 
			$scope.isBlank = false; 
		} 
	} 
	  
	$scope.callServer = function() { 
		$scope.isLoading = true; 
		var responsePromise = $http.get(contextPath
				+ '/ajx/cricket/category/get/list'); 
		responsePromise.success(function(data) { 
			if (data.status == 'SUCCESS') { 
				$scope.rowCollection = data.response; 
				if ($scope.rowCollection.length != 0) { 
					for (var i in $scope.rowCollection) { 
						$scope.categoryList.push($scope.rowCollection[i].title); 
						for(var j in $scope.rowCollection[i].entities) { 
							$scope.entityList.push( 
								$scope.rowCollection[i].entities[j]); 
						} 
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
	   
	$scope.scoringPointsCreate = function() { 
		$scope.isError = false; 
		if ($scope.cricketPointsForm.title != null 
				&& $scope.cricketPointsForm.title != '') { 
			if ($scope.cricketPointsForm.category != null
					&& $scope.cricketPointsForm.category != '') { 
				if ($scope.cricketPointsForm.entity.entity != null
						&& $scope.cricketPointsForm.entity.entity != '') { 
					$scope.myForm.$pristine = true; 
					var data = {}; 
					data = { 
						title : $scope.cricketPointsForm.title, 
						category : $scope.cricketPointsForm.category,
						entity : $scope.cricketPointsForm.entity.entity, 
						apiValue : $scope.cricketPointsForm.apiValue,
						manualValue : $scope.cricketPointsForm.manualValue, 
						playerPoints : $scope.cricketPointsForm.playerPoints,
						userPoints : $scope.cricketPointsForm.userPoints,
						manual : $scope.battingCricketForm.manual, 
						player : $scope.battingCricketForm.player,
						user : $scope.battingCricketForm.user
					} 
					var responsePromise = $http.post(contextPath
							+ '/ajx/cricket/pointScoring/create', data);
					responsePromise.success(function(data) { 
						if (data.status == 'SUCCESS') { 
							$scope.isError = false; 
							$scope.isSuccess = true; 
							$scope.successMessage = data.successMsg; 
							$timeout(function() { 
								$state.go("app.cricketBatting.list"); 
							}, 500); 
						} else { 
							$scope.isError = true; 
							$scope.myForm.$pristine = false; 
							$scope.errorMessage = data.errorDetails; 
						} 
					}) 
				} else { 
					$scope.isError = true;
					$scope.myForm.$pristine = falsew;
					$scope.errorMessage = 'Please select entity.';
				}
			} else { 
				$scope.isError = true; 
				$scope.myForm.$pristine = false; 
				$scope.errorMessage = 'Please select category.';
			} 
		} else {
			$scope.isError = true;
			$scope.myForm.$pristine = false;
			$scope.errorMessage = 'Title is required.';
		}
	}
	  
})


