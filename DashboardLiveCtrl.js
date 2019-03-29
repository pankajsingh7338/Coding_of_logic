'use strict';
app.controller('DashboardLiveCtrl', function($scope, $http, $state, $stateParams, $timeout, MCashService, TableConfig,
		$interval) {

	$scope.oldTotalActTbl = 0;
	$scope.newTotalActTbl = 0;
	$scope.oldTotalLiveCoupon = 0;
	$scope.newTotalLiveCoupon = 0;
	$scope.oldTotalLiveTournament = 0;
	$scope.newTotalLiveTournament = 0;
	$scope.oldTotalPlayers = 0;
	$scope.newTotalPlayers = 0;
	$scope.auto = true;
	var promise;
	$scope.refresh = function() {
		$scope.autoUpdate($scope.auto);
	};
	$scope.autoUpdate = function(auto) {
		$scope.isLiveLoading = true;
		if (auto == true) {
			promise = $interval(function() {
				if ($state.current.name == 'app.dashboard.home') {
					var responsePromise = $http.get(contextPath + '/ajx/dashboard/widgets/live');
					responsePromise.success(function(data) {
						if (data.status == 'SUCCESS') {
							$scope.newTotalActTbl = data.response.activeTables;
							$scope.newTotalLiveCoupon = data.response.liveCoupons;
							$scope.newTotalLiveTournament = data.response.liveTournamets;
							$scope.newTotalPlayers = data.response.onlinePlayers;
							if ($scope.oldTotalPlayers != $scope.newTotalPlayers)
								$scope.oldTotalPlayers = $scope.newTotalPlayers;
							if ($scope.oldTotalActTbl != $scope.newTotalActTbl)
								$scope.oldTotalActTbl = $scope.newTotalActTbl;
							if ($scope.oldTotalLiveCoupon != $scope.newTotalLiveCoupon)
								$scope.oldTotalLiveCoupon = $scope.newTotalLiveCoupon;
							if ($scope.oldTotalLiveTournament != $scope.newTotalLiveTournament)
								$scope.oldTotalLiveTournament = $scope.newTotalLiveTournament;

						} else {
							$scope.pageError = true;
							$scope.page = {
								title : 'Error Page',
							};
							$scope.response.error = data.errorDetails;
						}
						$scope.isLiveLoading = false;
					})
				} else {
					$interval.cancel(promise);
					$scope.isLiveLoading = false;
				}
			}, 5000);
		} else {
			$interval.cancel(promise);
			$scope.dataLoading = false;
			$scope.isLoading = false;
		}
	}
})

