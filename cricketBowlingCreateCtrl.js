'use strict';
app.controller('cricketBowlingCreateCtrl', function($scope, $http, $state,
		$stateParams, $timeout, $location) {
	$scope.page = {
		title : 'Create Category',
	};
	$scope.categoryCricketForm = {}; 
	$scope.errorMessage = ''; 
	$scope.isError = false; 
	var entityList = []; 
	$scope.entity = []; 

	$scope.emptyField = function() { 
		if ($scope.categoryCricketForm.title == undefined 
				|| $scope.categoryCricketForm.title == '') {  
			$scope.isBlank = true; 
			$scope.blankMsg = 'Category name is required.';  
		} else { 
			$scope.isBlank = false; 
		} 
	} 

	$scope.addEntity = function() { 
		$scope.isFilError = false;
		$scope.isError = false;
		if ($scope.categoryCricketForm.entity != undefined
				&& $scope.categoryCricketForm.entity != '') { 
			var filData = {
					entity : $scope.categoryCricketForm.entity
				} 
			$scope.entity.push(filData); 
			entityList.push({
				entity : $scope.categoryCricketForm.entity
			}); 
			var length = entityList.length; 
			if (length == 0) 
				$scope.hideAddEntity = true; 
			$scope.isEntError = false; 
			$scope.categoryCricketForm.entity = ''; 
		} else {
			$scope.isEntError = true;
			$scope.entError = 'Please Enter Entity'; 
		}
	}
	  
	$scope.removeEntity = function(index) {
		var dimRemove = $scope.entity[index].entity; 
		entityList.splice(index, 1);
		$scope.entity.splice(index, 1);
		$scope.hideAddEntity = false;
		$scope.isValidFilter = true;
		$scope.isError = false; 
	} 

	$scope.createCategory = function() {
		$scope.isError = false;
		if ($scope.categoryCricketForm.title != null
				&& $scope.categoryCricketForm.title != '') { 
			$scope.myForm.$pristine = true;
			var data = {};
			data = {
				title : $scope.categoryCricketForm.title,
				entities : entityList 
			}
			var responsePromise = $http.post(contextPath
					+ '/ajx/cricket/category/create', data);
			responsePromise.success(function(data) {
				if (data.status == 'SUCCESS') {
					$scope.isError = false; 
					$scope.isSuccess = true; 
					$scope.successMessage = data.successMsg;
					$timeout(function() {
						$state.go("app.cricketBowling.list");
					}, 500);
				} else {
					$scope.isError = true;
					$scope.myForm.$pristine = false;
					$scope.errorMessage = data.errorDetails;
				}
			})
		} else { 
			$scope.isError = true;
			$scope.myForm.$pristine = false;
			$scope.errorMessage = 'Title is required.';
		}
	}
})

