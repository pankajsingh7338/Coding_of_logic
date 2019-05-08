'use strict';
app.controller('cricketBowlingUpdateCtrl', function($scope, $http, $state,
		$stateParams, $timeout, $location) {
	$scope.page = {
		title : 'Update Category',
	};

	$scope.id = $stateParams.id;
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

	$scope.getCategory = function() {
		$scope.isLoading = true;
		var responsePromise = $http.get(contextPath
				+ '/ajx/category/get/details?id=' + $scope.id);
		responsePromise.success(function(data) {
			if (data.status == 'SUCCESS') {
				$scope.categoryCricketForm.id = data.response.id;
				$scope.categoryCricketForm.title = data.response.title;
				if (data.response.entities != null) { 
					for ( var i in data.response.entities) {
						$scope.entity.push(data.response.entities[i]);
						entityList.push({
							entity : data.response.entities[i].entity
						}); 
					} 
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

	$scope.updateCategory = function() {
		if ($scope.categoryCricketForm.title != undefined
				&& $scope.categoryCricketForm.title != '') {
			var map = {};

			map["title"] = $scope.categoryCricketForm.title;
			map["entities"] = entityList;
			
			var responsePromise = $http({
				method : 'POST',
				url : contextPath + '/ajx/cricket/category/update',
				headers : { 
					'Content-Type' : undefined
				},
				data : { 
					id : $scope.id
				},
				transformRequest : function(data) {
					var formData = new FormData();
					formData.append("id", $scope.id);
					formData.append("category", angular.toJson(map));
					return formData;
				}
			}).success(function(data, status, headers, config) {
				if (data.status == 'SUCCESS') {
					$scope.isError = false;
					$scope.isSuccess = true;
					$scope.successMessage = data.successMsg;
					$timeout(function() {
						$state.go("app.cricketBowling.list"); 
					}, 100);
				} else {
					$scope.isError = true;
					$scope.myForm.$pristine = false;
					$scope.errorMessage = data.errorDetails;
				}
			})
		} else {
			$scope.isError = true;
			$scope.myForm.$pristine = false;
			$scope.errorMessage = "Please Enter title";
		}
	}

})

