'use strict';
app.controller('cricketMatchScheduleCreateCtrl', function($scope, $http, $state, $stateParams, $timeout, $location) {
    
    $scope.button = {
        label: 'Choose File'
    };
    $scope.cricketMatchForm = {}; 
    $scope.errorMessage = ''; 
    $scope.isError = false; 
	$scope.isDisable = true; 
	$scope.filter = {};
	$scope.filter.dimValue1 = [];
	$scope.filters = [];
	
	$scope.callServer = function() {
		$scope.isLoading = true;
		var responsePromise = $http.post(contextPath + '/access/token');
		responsePromise.success(function(data) {
			if (data.status) {
				$scope.authData = data.auth;
				$scope.getCricketMathSchedule(); 
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

	$scope.getCricketMathSchedule = function() {
		if ($scope.authData != null && $scope.authData != ''
				&& $scope.authData.access_token != ''
				&& $scope.authData.access_token != null) {
			var responsePromise = $http.get(contextPath
					+ '/ajx/cricketMatch/schedule?accessToken='
					+ $scope.authData.access_token); 
			responsePromise.success(function(data) { 
				if (data.status) {
					
						
				} else {
					$scope.errorMsg = data.message;
				}
			})
		}
	}
	
	$scope.cricketMatchForm.team1Doc = '';  
	$scope.chooseImage = false;  
	$scope.validImage = false; 
	$scope.fileReaderSupported = window.FileReader != null; 
	$scope.photoChanged = function(files) {
		if (files != null) {
			var file = files[0];
			$timeout(function() {
				var fileReader = new FileReader();
				fileReader.readAsDataURL(file);
				fileReader.onload = function(e) {
					$timeout(function() {
						var img = new Image();
						img.src = e.target.result;
						if (file.type == 'image/jpeg'
								|| file.type == 'image/jpg'
								|| file.type == 'image/png') {
							$timeout(
									function() {
										$scope.button = {
											label : 'Update File'
										};
										$scope.isDocError = false;
										$scope.cricketMatchForm.team1Doc = e.target.result; 
										$scope.chooseImage = true; 
									}, 100); 
						} else { 
							$scope.isDocError = true; 
							$scope.docError = 'Document should be in png,jpg,jpeg format'; 
						} 
					}); 
				} 
			}); 
		} 
	}; 
	   
	$scope.cricketMatchForm.team2Doc = ''; 
	$scope.chooseImage = false; 
	$scope.validImage = false; 
	$scope.fileReaderSupported = window.FileReader != null; 
	$scope.imageChanged = function(files) { 
		if (files != null) { 
			var file = files[0]; 
			$timeout(function() { 
				var fileReader = new FileReader(); 
				fileReader.readAsDataURL(file); 
				fileReader.onload = function(e) { 
					$timeout(function() { 
						var img = new Image(); 
						img.src = e.target.result; 
						if (file.type == 'image/jpeg' 
								|| file.type == 'image/jpg' 
								|| file.type == 'image/png') { 
							$timeout( 
									function() { 
										$scope.button = { 
											label : 'Update File' 
										}; 
										$scope.isDocError = false;
										$scope.cricketMatchForm.team2Doc = e.target.result; 
										$scope.chooseImage = true;
									}, 100);
						} else {
							$scope.isDocError = true;
							$scope.docError = 'Document should be in png,jpg,jpeg format';
						}
					});
				}
			});
		}
	}; 
	
	$scope.getDimentionList = function(search) {
		$scope.selectedValuesAdd = [];
		if (search == undefined)
			search = null;
		var responsePromise = $http.get(contextPath
				+ "/ajx/cricketMatch/getContest/search/list?query="
				+ search); 
		responsePromise 
				.success(function(data) {
					if (data.status == 'SUCCESS') {
						$scope.selectedValuesAdd = data.response; 
						if ($scope.filter.dimValue1.length != 0) {
							var output = [];
							angular
									.forEach(
											$scope.selectedValuesAdd,
											function(item) {
												var isPush = true;
												if (item.id != undefined
														&& item.id != null) {
													for (var j = 0; j < $scope.filter.dimValue1.length; j++) {
														if ($scope.filter.dimValue1[j].id == item.id) {
															isPush = false;
															break;
														}
													}
												}
												if (isPush)
													output
															.push(item);
											});
							$scope.selectedValuesAdd = output;
						}
					} else if (data.status == 'FAILED') {
						$scope.isError = true; 
						$scope.errorMsg = data.ed;
					} else {
						$scope.pageError = true;
						$scope.page = {
							title : 'Error Page',
						};
						$scope.responseError = data.errorDetails;
					}
				});
	}

	$scope.dimentionListQuery = function(query) {
		var responsePromise = $http.get(contextPath
				+ "/ajx/cricketMatch/getContest/search/list?query="
				+ query);
		responsePromise.success(function(data, status, headers,
				config) { 
			$scope.filters[0].placeholder = data.response;
		});
	} 
	  
	$scope.addFilter = function() {
		$scope.isFilError = false;
		$scope.isError = false;
		var selDimValue = [];
		selDimValue = $scope.filter.dimValue1; 
		if (selDimValue.length > 0) {
			var filData = {
				value : selDimValue,
				placeholder : []
			}
			$scope.filters.push(filData); 
			$scope.filter.dimValue1 = []; 
			$scope.addItem = false;  
			if ($scope.filters.length == 1) {
				$scope.hideAddFilter = true;
			}
		} else {
			$scope.isFilError = true;
			$scope.filterError = 'All filter fields are mandatory';
		}
	}
	$scope.removeFilter = function(index) {
		$scope.filters.splice(index, 1);
		$scope.hideAddFilter = false; 
		$scope.isValidFilter = true;
		$scope.isError = false;
	}
	
	
    $scope.contestCreate = function() {
        $scope.isError = false;
         if ($scope.contestCricketForm.contestName != null && $scope.contestCricketForm.contestName != '') {
            if ($scope.contestCricketForm.contestCode != null && $scope.contestCricketForm.contestCode != '') {
                    if ($scope.contestCricketForm.shortContent != null && $scope.contestCricketForm.shortContent != '') {
                        if ($scope.contestCricketForm.description != null && $scope.contestCricketForm.description != '') {
                            if ($scope.contestCricketForm.contestType != null && $scope.contestCricketForm.contestType != '') {
                            	 if ($scope.contestCricketForm.contestMode != null && $scope.contestCricketForm.contestMode != '') {
                            		 if ($scope.contestCricketForm.minimumParticipants != null && $scope.contestCricketForm.minimumParticipants != '') {
                            			 if ($scope.contestCricketForm.maximumParticipants != null && $scope.contestCricketForm.maximumParticipants != '') {
                            				 if (answerOptions.length >= 1) { 
                                            $scope.myForm.$pristine = true; 
                                            var data = {}; 
                                            data = { 
                                            	contestName: $scope.contestCricketForm.contestName,
                                            	contestCode:$scope.contestCricketForm.contestCode,
                                            	shortContent: $scope.contestCricketForm.shortContent,
                                            	description: $scope.contestCricketForm.description,
                                            	contestType: $scope.contestCricketForm.contestType,
                                            	contestMode: $scope.contestCricketForm.contestMode,
                                            	freePoint: $scope.contestCricketForm.freePoint,
                                            	contestEntryFee:$scope.contestCricketForm.contestEntryFee,
                                            	entryFee:$scope.contestCricketForm.entryFee,
                                            	concession:$scope.contestCricketForm.concession,
                                            	concessionEntity:$scope.contestCricketForm.concessionEntity,
                                            	concessionEntityValue:$scope.contestCricketForm.concessionEntityValue,
                                            	addOn:$scope.contestCricketForm.addOn,
                                            	addOnEntity:$scope.contestCricketForm.addonEntity,
                                            	winningPrice:$scope.contestCricketForm.winningPrice,
                                            	playersNumber: $scope.contestCricketForm.playersNumber,
                                            	minimumParticipants: $scope.contestCricketForm.minimumParticipants,
                                            	maximumParticipants: $scope.contestCricketForm.maximumParticipants,
                                            	prizeDistribution : answerOptions
                                            }
                                            var responsePromise = $http({
                                                method: 'POST',
                                                url: contextPath + '/ajx/cricketContest/create',
                                                headers: {
                                                    'Content-Type': undefined
                                                },
                                                data: {
                                                    contest: data
                                                },
                                                transformRequest: function(data) {
                                                    var formData = new FormData();
                                                    formData.append("contest", angular.toJson(data.contest));
                                                    return formData;
                                                }
                                            }).success(function(data) {
                                                if (data.status == 'SUCCESS') {
                                                    $scope.isError = false;
                                                    $scope.isSuccess = true;
                                                    $scope.successMessage = data.successMsg;
                                                    $timeout(function() {
                                                        $state.go("app.cricketContest.list");
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
                                                 $scope.errorMessage = 'Prize distribution is required.';
                                         }
                            	    } else {
                                         $scope.isError = true;
                                         $scope.myForm.$pristine = false;
                                         $scope.errorMessage = 'Maximum participants is required.';
                                       }
                                    } else {
                                        $scope.isError = true;
                                        $scope.myForm.$pristine = false;
                                        $scope.errorMessage = 'Minimum participants is required.';
                                    }
                                } else {
                                    $scope.isError = true;
                                    $scope.myForm.$pristine = false;
                                    $scope.errorMessage = 'Contest mode is required.';
                                }
                            } else {
                                $scope.isError = true;
                                $scope.myForm.$pristine = false;
                                $scope.errorMessage = 'Contest Type is required.';
                            }
                        } else {
                            $scope.isError = true;
                            $scope.myForm.$pristine = false;
                            $scope.errorMessage = 'Description is required.';
                        }
                    } else {
                        $scope.isError = true;
                        $scope.myForm.$pristine = false;
                        $scope.errorMessage = 'ShortContent is required.';
                    } 
            } else { 
                $scope.isError = true; 
                $scope.myForm.$pristine = false; 
                $scope.errorMessage = 'Contest code is required.'; 
            } 
        } else { 
            $scope.isError = true; 
            $scope.myForm.$pristine = false; 
            $scope.errorMessage = 'Contest name is required.'; 
        } 
    } 

}) 
  
 
