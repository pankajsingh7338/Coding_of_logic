'use strict';
app
		.controller(
				'DashboardHomeCtrl',
				function($scope, $http, $state, $stateParams, $timeout,
						MCashService, TableConfig, $interval) {
					$scope.page = {
						title : 'Dashboard',
					};
					$scope.errorMessage = '';
					$scope.isError = false;
					$scope.isSuccess = false;
					$scope.successMessage = '';
					$scope.dashboard = {};
					$scope.response = {};
					$scope.parent = {};
					$scope.parent.levels = [];
					$scope.parent.range = moment().range(
							moment().add('days', -7), moment());
					$scope.parent.refresh = function() {
						$scope.parent.getResponse();
					}
					$scope.rangeSelectOptions = [
							{
								label : "Today",
								range : moment().range(moment().toDate(),
										moment().toDate())
							},
							{
								label : "Yesterday",
								range : moment().range(
										moment().add('days', -1),
										moment().add('days', -1))
							},
							{
								label : "Last 7 days",
								range : moment().range(
										moment().add('days', -7), moment())
							},
							{
								label : "Last 14 days",
								range : moment().range(
										moment().add('days', -14), moment())
							},
							{
								label : "Month to Date",
								range : moment().range(
										moment().startOf("month")
												.startOf("day"), moment())
							},
							{
								label : "Last Month",
								range : moment().range(
										moment().startOf("month").add("month",
												-1).startOf("day"),
										moment().add("month", -1)
												.endOf("month").startOf("day"))
							} ]

					$scope.parent.levels = [];
					$scope.onClick = function(points, evt) {
						//console.log(points, evt);
					};

					// $scope.datasetOverride = [ {
					// label : "chart",
					// borderWidth : 3,
					// hoverBackgroundColor : "rgba(255,99,132,0.4)",
					// hoverBorderColor : "rgba(255,99,132,1)",
					// type : 'line'
					//
					// } ];

					// $scope.options = {
					// scales : {
					// yAxes : [ {
					// id : 'y-axis-1',
					// type : 'linear',
					// display : true,
					// position : 'left'
					//
					// }
					// // yAxes: [{
					// // scaleLabel: {
					// // display: true,
					// // labelString: 'probability'
					// // }
					// // }]
					// // , {
					// // id : 'y-axis-2',
					// // type : 'linear',
					// // display : true,
					// // position : 'right'
					// // }
					// ],
					// xAxes : [ {
					// barPercentage : 0.4
					// } ]
					// },
					// maintainAspectRatio : false
					// };
					// $scope.options = {
					// scales : {
					// yAxes : [ {
					// scaleLabel : {
					// display : true,
					// labelString : 'Probability'
					// }
					// } ],
					// xAxes : [ {
					// scaleLabel : {
					// display : true,
					// labelString : 'Day'
					// }
					// } ]
					// },
					// maintainAspectRatio : false
					// }

					var range = MCashService.get('dashboard.range');
					if (range != undefined) {
						$scope.parent.range = $scope.parent.range = moment()
								.range(range.start, range.end);
					}

					$scope.parent.chartData = [];
					$scope.parent.getResponse = function() {
						$scope.dataLoading = true;
						MCashService.setMycache('dashboard.range', {
							start : $scope.parent.range.start,
							end : $scope.parent.range.end
						});
						var startDate = $scope.parent.range.start
								.format('MM/DD/YYYY');
						var endDate = $scope.parent.range.end
								.format('MM/DD/YYYY');
						var search = $scope.search;
						var data = {
							startDate : startDate,
							endDate : endDate
						}

						var responsePromise = $http.post(contextPath
								+ '/ajx/dashboard/widgets/summary', data);
						responsePromise
								.success(function(data) {
									if (data.status == 'SUCCESS') {
										$scope.parent.chartData = data.chartData;
										$scope.parent.levels = data.levels;
										var label = MCashService
												.get('dashboard.label');
										if (label != undefined) {
											$scope.getChart(label.key,
													label.inx);
										} else {
											$scope.getChart('Game Played', 0);
										}
									} else {
										$scope.pageError = true;
										$scope.page = {
											title : 'Error Page',
										};
										$scope.response.error = data.errorDetails;
									}
									$scope.dataLoading = false;
								})
					}

					$scope.getChart = function(value, inx) {
						var label = {
							inx : inx,
							key : value
						};

						for ( var key in $scope.parent.chartData) {
							if (key == value) {
								$scope.inx = inx;
								$scope.chartLabel = value;
								$scope.labels = $scope.parent.chartData[key].labels;
								$scope.options = $scope.parent.chartData[key].options;
								$scope.data = [ $scope.parent.chartData[key].data ];
								$scope.datasetOverride = $scope.parent.chartData[key].datasetOverride;
							}
						}
						MCashService.setMycache('dashboard.label', label);
					}

				})

