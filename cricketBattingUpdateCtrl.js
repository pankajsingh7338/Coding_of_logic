'use strict'; 
app 
		.controller( 
				'cricketBattingUpdateCtrl',
				function($scope, $http, $state, $stateParams, $timeout,
						$location) { 
					$scope.page = { 
						title : 'Cricket Batting',
					}; 

					$scope.id = $stateParams.id;
					$scope.isLoading = false; 
					$scope.dataLoading = true; 
					$scope.rowCollection = []; 
					$scope.displayedCollection = []; 
					$scope.response = {}; 
					$scope.response.error = ''; 
					$scope.isError = false; 
					$scope.pageError = false; 
					$scope.paginationShow = false; 
					$scope.cricketPointsForm = {}; 
					$scope.battingCricketForm = {}; 
					$scope.cricketBattingForm = {}; 
					$scope.categoryList = []; 
					$scope.entityList = []; 
					
					$scope.getPointScore = function() { 
						$scope.isLoading = true; 
						var responsePromise = $http
								.get(contextPath
										+ '/ajx/cricket/get/pointScore?id='
										+ $scope.id); 
						responsePromise
								.success(function(data) { 
									if (data.status == 'SUCCESS') { 
										$scope.cricketPointsForm.id = data.response.id;
										$scope.cricketPointsForm.title = data.response.title;
										$scope.cricketPointsForm.category = data.response.category;
										$scope.cricketPointsForm.entity = data.response.entity;
										$scope.cricketPointsForm.manualValue = data.response.manualValue;
										$scope.cricketPointsForm.apiValue = data.response.apiValue;
										$scope.cricketPointsForm.playerPoints = data.response.playerPoints;
										$scope.cricketPointsForm.userPoints = data.response.userPoints;
										$scope.battingCricketForm.manual = data.response.manual;
										$scope.battingCricketForm.player = data.response.player;
										$scope.battingCricketForm.user = data.response.user;
										$scope.cricketBattingForm.status = data.response.status;
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

					$scope.getMeasure = function() { 
						$scope.isLoading = true; 
						var responsePromise = $http.get(contextPath
								+ '/ajx/cricket/category/get/list'); 
						responsePromise
								.success(function(data) { 
									if (data.status == 'SUCCESS') { 
										$scope.rowCollection = data.response; 
										if ($scope.rowCollection.length != 0) { 
											for ( var i in $scope.rowCollection) { 
												$scope.categoryList 
														.push($scope.rowCollection[i].title);
												for ( var j in $scope.rowCollection[i].entities) {
													if (!$scope.entityList
															.includes($scope.cricketPointsForm.entity)) {
														$scope.entityList
																.push($scope.rowCollection[i].entities[j].entity);
													}
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

					$scope.scoringPointsUpdate = function() { 
						if ($scope.cricketPointsForm.title != undefined
								&& $scope.cricketPointsForm.title != '') {
							if ($scope.cricketPointsForm.category != undefined
									&& $scope.cricketPointsForm.category != '') {
								if ($scope.cricketPointsForm.entity.entity != ''
										&& $scope.cricketPointsForm.entity.entity != undefined) {
									var map = {}; 
									map["title"] = $scope.cricketPointsForm.title; 
									map["category"] = $scope.cricketPointsForm.category; 
									map["entity"] = $scope.cricketPointsForm.entity.entity; 
									map["manualValue"] = $scope.cricketPointsForm.manualValue; 
									map["apiValue"] = $scope.cricketPointsForm.apiValue; 
									map["playerPoints"] = $scope.cricketPointsForm.playerPoints; 
									map["userPoints"] = $scope.cricketPointsForm.userPoints; 
									map["manual"] = $scope.battingCricketForm.manual; 
									map["player"] = $scope.battingCricketForm.player; 
									map["user"] = $scope.battingCricketForm.user; 
									
									var responsePromise = $http(
											{
												method : 'POST',
												url : contextPath
														+ '/ajx/cricket/pointScore/update',
												headers : {
													'Content-Type' : undefined
												},
												data : {
													id : $scope.id
												},
												transformRequest : function(
														data) {
													var formData = new FormData();
													formData.append("id",
															$scope.id);
													formData
															.append(
																	"cricket",
																	angular
																			.toJson(map));
													return formData;
												}
											})
											.success(
													function(data, status,
															headers, config) {
														if (data.status == 'SUCCESS') {
															$scope.isError = false;
															$scope.isSuccess = true;
															$scope.successMessage = data.successMsg;
															var newId = $scope.id;
															var idObj = {
																'id' : newId
															};
															$timeout(
																	function() {
																		$state
																				.go(
																						"app.cricketBatting.list",
																						(idObj));
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
									$scope.errorMessage = "Entity is required";
								} 
							} else {
								$scope.isError = true;
								$scope.myForm.$pristine = false;
								$scope.errorMessage = "Category is required";
							}
						} else { 
							$scope.isError = true;
							$scope.myForm.$pristine = false; 
							$scope.errorMessage = "Title is required";
						}
					}

				})

