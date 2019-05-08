'use strict';
app.controller('cricketBowlingCtrl', function($scope, $http, $stateParams, MCashService, BlogConfig) {
	$scope.page = { 
		title : 'List of Categories', 
	}; 
	$scope.isLoading = false; 
	$scope.dataLoading = true; 
	$scope.rowCollection = []; 
	$scope.response = {}; 
	$scope.response.error = ''; 
	$scope.displayedCollection = []; 
	$scope.isError = false; 
	$scope.pageError = false; 
	$scope.tournamentForm = {};
	$scope.paginationShow = false;
	var totalData = 0;
	$scope.paginationOption = [ 10, 25, 50, 100 ];
	$scope.filteredItems = [];
	$scope.isPagination = true;
	$scope.showrecords = 10;
	$scope.start = 0;
	$scope.to = $scope.showrecords;
	$scope.itemsByPage = 50;
	
	$scope.callServer = function() { 
		$scope.isLoading = true;
		var responsePromise = $http.get(contextPath + '/ajx/cricket/category/get/list');
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

	$scope.$watch(function() {
		$scope.isPagination = true;
		if ($scope.rowCollection.length >= $scope.filteredItems.length) {
			$scope.total = $scope.filteredItems.length;
			$scope.to = $scope.displayedCollection.length + $scope.start;
		}
	})

})

