
'use strict';
app.controller('cricketBattingCtrl', function($scope, $http, $stateParams, MCashService, CricketConfig) {
	$scope.page = { 
		title : 'Cricket Batting', 
	}; 
	$scope.isLoading = false; 
	$scope.dataLoading = true; 
	$scope.rowCollection = []; 
	$scope.response = {}; 
	$scope.response.error = ''; 
	$scope.displayedCollection = []; 
	$scope.isError = false; 
	$scope.pageError = false; 
	$scope.paginationShow = false;
	var totalData = 0;
	$scope.paginationOption = [ 10, 25, 50, 100 ];
	$scope.isPagination = true;
	$scope.showrecords = 10;
	$scope.start = 0;
	$scope.to = $scope.showrecords;
	$scope.itemsByPage = 50;
	$scope.cricForm = {}; 
	$scope.cricketForm = {}; 
	var handleSuccess = function(data, status) {
		if (data.status == 'SUCCESS') { 
			var cricStatuses = []; 
			for ( var key in data.response.cricketScorePointStatusMap) {
				cricStatuses.push({
					name : key,
					value : data.response.cricketScorePointStatusMap[key]
				});
			} 
			$scope.cricForm.statuses = [ {
				name : "ALL STATUS",
				value : null
			} ].concat(cricStatuses);
		} else {
			$scope.pageError = true;
			$scope.page = {
				title : 'Error Page',
			};
			$scope.response.error = data.errorDetails;
		}
	};
	
	CricketConfig.getCricketConfig().success(handleSuccess); 

	$scope.isError = false;
	if (MCashService.get("cricketForm.status") != undefined) {
		if ($scope.cricketForm.status != null && $scope.cricketForm.status != '')
			$scope.cricketForm.status = $scope.cricketForm.status;
		else {
			$scope.cricketForm.status = MCashService.get("cricketForm.status");
		}
	} else {
		if ($scope.cricketForm.status != null)
			$scope.cricketForm.status = $scope.cricketForm.status;
		else if ($scope.cricketForm.status == null) {
			var statuses = [ {
				name : "ALL STATUS",
				value : null
			} ];
			$scope.cricketForm.status = statuses[0];
		} else
			$scope.cricketForm.statuses = $scope.cricketForm.status; 
	} 

	$scope.filterCallServer = function() { 
		MCashService.setMycache('cricketForm.status', $scope.cricketForm.status);
		$scope.callServer();
	}
	  
	$scope.callServer = function() {
		$scope.isLoading = true;
		var responsePromise = $http.get(contextPath + '/ajx/cricket/get/pointScore/list?status=' + $scope.cricketForm.status.value); 
		responsePromise.success(function(data) { 
			if (data.status == 'SUCCESS') { 
				$scope.rowCollection = data.response;
				$scope.rowCollection.reverse();
				if ($scope.rowCollection.length != 0) { 
					$scope.isError = false;
					$scope.displayedCollection = [].concat($scope.rowCollection);
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
	$scope.pageChanged = function(idx) {
		$scope.to = idx * $scope.showrecords;
		$scope.start = (idx * $scope.showrecords) - $scope.showrecords;
		if ($scope.to >= totalData)
			$scope.to = totalData;
	}

	$scope.showData = function(selecteditem) {
		$scope.showrecords = selecteditem;
		if ($scope.showrecords >= totalData)
			$scope.isLoading = true;
		$scope.to = $scope.showrecords;
		$scope.isLoading = false;
	}
})

