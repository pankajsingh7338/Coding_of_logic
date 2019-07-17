function lobbyCtrl($scope, $http, $location, $timeout, $interval, $rootScope, MCashService, SharedDataService,
		PlayerData, WseWebsocket, ResponseReader, Queue, HeartBeatScheduler, Schedular) {
	
	

	$scope.PlayerData = PlayerData;
	// //Validate Login Check Methods
	$scope.playeLoggedIn = false;
	function loginValueChanged(value) {
		$timeout(function() {
			$scope.$apply(function($scope) {
				if (!value) {
				} else {
					$scope.wallet = PlayerData.getPlayerWallet();
					$scope.walletInterval = $interval(function() {
						$scope.wallet = PlayerData.getPlayerWallet();
					}, 0)
				}
			});
		});
	}
	PlayerData.onLoginValueChange(loginValueChanged);

	$rootScope.WseWebsocket = WseWebsocket; 
	$rootScope.ResponseReader = ResponseReader; 
	$rootScope.SharedDataService = SharedDataService; 
	$rootScope.Queue = Queue;
	$rootScope.HeartBeatScheduler = HeartBeatScheduler;
	$rootScope.Schedular = Schedular;
	$rootScope.PlayerData = PlayerData;
	$rootScope.Scope = $scope;
	$rootScope.onWebsocketConnect = [];
	$scope.isLogout = false;
	// Extend tournamentCtrl
	tournamentCtrl($scope, $http, $timeout, $interval, MCashService, SharedDataService, PlayerData);

	setGameplayStateLogics($scope, $timeout, $interval, $rootScope, $http, SharedDataService, PlayerData, MCashService);
	
	dashboardCtrl($scope, $http, $location, $timeout, $interval,$rootScope, MCashService, PlayerData, SharedDataService);
	
	
	

	$scope.listType = void 0;

	/* Use only in case of console hit */
	
	$('.lobby-buyin').click(function() {
		$('.chip-buy').modal('show');
		document.getElementById('loadData').click();
	});

	$scope.onlyLobby = function() {
		
		if (MCashService.get("lobby.nav") != undefined && MCashService.get("lobby.nav") != null)
			document.getElementById("open" + MCashService.get("lobby.nav")).click();
		else
			document.getElementById("openrealplay").click();
	}
	
	if (MCashService.get('login.isRemeberMe') == undefined)
		$scope.rememberMe = "";
	else 
		$scope.rememberMe = MCashService.get('login.isRemeberMe');
	
	if (MCashService.get('login.rememberUserName') == undefined)
		$scope.email = '';
	else {
		if (MCashService.get('login.isRemeberMe') == true)
			$scope.email = MCashService.get('login.rememberUserName');
		else {
			$scope.email = '';
			$scope.password = '';
		}
			
	
	}

	$scope.password = '';
	
	$scope.rememberMeStatus = function() {
		MCashService.setMycache('login.isRemeberMe', $scope.rememberMe);
	}

	$scope.lobby_freerolls = [];
	var allMode = {
		"omaha" : "OMAHA",
		"holdem" : "HOLDEM",
		"OMAHA_5CARD" : "OMAHA_5CARD"
	};
	$scope.filterSelected = {
		all : true,
		omaha : false,
		holdem : false,
		OMAHA_5CARD : false
	}
	$scope.filterDisabled = {
		all : true,
		omaha : false,
		holdem : false,
		OMAHA_5CARD : false
	}

	
	
	$scope.selectMode = function(mode) {
		if ($scope.filterDisabled[mode])
			return;
		$scope.filterSelected = {
			all : false,
			omaha : false,
			holdem : false,
			OMAHA_5CARD : false
		}
		$scope.filterDisabled = {
			all : false,
			omaha : false,
			holdem : false,
			OMAHA_5CARD : false
		}
		$scope.filterSelected[mode] = true;
		$scope.filterDisabled[mode] = true;
		if (mode == "all")
			$scope.mode = "";
		else
			$scope.mode = allMode[mode];
		$scope.lobby_freerollList($scope.url, $scope.listType);
	}
	$scope.getTablePlayerStats = function(tableId, tableTitle,canPlay) {
		$scope.canPlay = canPlay;
		$scope.tableTitle = tableTitle;
		$scope.change = true;
		$timeout(function() {
			if ($rootScope.webSocketConnected)
				sentToSocket({
					event : "12",
					data : tableId
				});
			else {
				$rootScope.onWebsocketConnect.push(function() {
					sentToSocket({
						event : "12",
						data : tableId
					});
				});
			}
		}, 1);
	}
	
	
	
	$scope.getAllDataForTable = function() {
			
				sentToSocket({
					event : "17",
					data : ""
				});
			
	
	}

	$scope.bets = [ {
		value : {
			from : '',
			to : ''
		},
		name : 'ALL'
	}, {
		value : {
			from : 1,
			to : 5
		},
		name : '1-5'
	}, {
		value : {
			from : 5,
			to : 20
		},
		name : '5-20'
	}, {
		value : {
			from : 20,
			to : 50
		},
		name : '20-50'
	}, {
		value : {
			from : 50,
			to : 200
		},
		name : '50-200'
	} ];
	$scope.buyIns = [ {
		value : {
			from : '',
			to : ''
		},
		name : 'ALL'
	}, {
		value : {
			from : 0,
			to : 99
		},
		name : '1-99'
	}, {
		value : {
			from : 100,
			to : 499
		},
		name : '100-499'
	}, {
		value : {
			from : 500,
			to : 999
		},
		name : '500-999'
	}, {
		value : {
			from : 1000,
			to : 4999
		},
		name : '1000-4999'
	}, {
		value : {
			from : '',
			to : 5000
		},
		name : 'above 5000'
	} ];
	$scope.tableSizes = [ {
		value : '',
		name : 'ALL'
	}, {
		value : 5,
		name : 5
	}, {
		value : 6,
		name : 6
	}, {
		value : 7,
		name : 7
	}, {
		value : 9,
		name : 9
	} ];
	
	$scope.filterGameTypeList = [ {
		value : '',
		name : 'ALL'
	}, {
		value : 'HOLDEM',
		name : 'NL Holdem'
	}, {
		value : 'OMAHA',
		name : 'PL Omaha'
	}, {
		value : 'OMAHA_5CARD',
		name : 'PL5 Omaha'
	}];
	
	if (MCashService.get('lobby.tableSize') == undefined)
		$scope.tableSize = $scope.tableSizes[0].value;
	else
		$scope.tableSize = MCashService.get('lobby.tableSize');
	
	if (MCashService.get('lobby.filterMode') == undefined)
		$scope.filterMode = $scope.filterGameTypeList[0].value;
	else
		$scope.filterMode = MCashService.get('lobby.filterMode');

	if (MCashService.get('lobby.blindRange') == undefined)
		$scope.blindRange = $scope.bets[0].value;
	else
		$scope.blindRange = MCashService.get('lobby.blindRange');

	if (MCashService.get('lobby.buyInRange') == undefined)
		$scope.buyInRange = $scope.buyIns[0].value;
	else
		$scope.buyInRange = MCashService.get('lobby.buyInRange');

	if (MCashService.get('lobby.hideEmpty') == undefined)
		$scope.hideEmpty = false;
	else
		$scope.hideEmpty = MCashService.get('lobby.hideEmpty');
	if (MCashService.get('lobby.hideFull') == undefined)
		$scope.hideFull = false;
	else
		$scope.hideFull = MCashService.get('lobby.hideFull');

	$scope.lobbyFilter = function(value) {
		MCashService.setMycache('lobby.buyInRange', $scope.buyInRange);
		MCashService.setMycache('lobby.blindRange', $scope.blindRange);
		MCashService.setMycache('lobby.tableSize', $scope.tableSize);
		MCashService.setMycache('lobby.filterMode', $scope.filterMode);
		$scope.lobby_freerollList(MCashService.get("lobby.nav"));
		$scope.filterList();
	}

	$scope.filterList = function() {
		MCashService.setMycache('lobby.hideEmpty', $scope.hideEmpty);
		MCashService.setMycache('lobby.hideFull', $scope.hideFull);
		if (!MCashService.get('lobby.hideEmpty'))
			$scope.lobby_freerollList(MCashService.get("lobby.nav"));
		$scope.lobbyList = $scope.lobby_freerolls;
		$scope.lobby_freerolls = [];
		for ( var i in $scope.lobbyList) {
			if ($scope.filterData($scope.lobbyList[i]))
				$scope.lobby_freerolls.push($scope.lobbyList[i]);
		}
		$scope.setTableMsg($scope.lobby_freerolls);
	}
	$scope.filterData = function(table) {
		if ((!($scope.hideEmpty && table.players == 0) || !$scope.hideEmpty)
				&& (!($scope.hideFull && table.players == table.maxPlayers) || !$scope.hideFull))
			return true;
		return false;

	}
	$scope.setTableMsg = function(lobbyList) {
		if (!lobbyList.length) {
			$scope.lobby_freerolls = [];
			$scope.successMsg = 'No table found';
		}
	}
	$scope.lobby_freerollList = function(type) {
		$scope.dataLoading = true;
		if($scope.filterMode == "ALL")
			$scope.mode = undefined;
		else
			$scope.mode = $scope.filterMode;
		var responsePromise = $http.get(contextPath + '/lobby/list?type=' + type + '&mode=' + $scope.mode
				+ '&tableSize=' + $scope.tableSize + '&sb=' + $scope.blindRange.from + '&bb=' + $scope.blindRange.to
				+ '&buyInMin=' + $scope.buyInRange.from + '&buyInMax=' + $scope.buyInRange.to);
		responsePromise.success(function(data) {
			$scope.dataLoading = false;
			if (data.status == "SUCCESS") {
				if (data.response) {
					if (data.response.length) {
						$scope.lobby_freerolls = data.response;
						$scope.displayData = data.response;
						$scope.getTablePlayerStats($scope.lobby_freerolls[0].id, $scope.lobby_freerolls[0].title,$scope.lobby_freerolls[0].canPlay);
						$scope.updateFreeRollLobbyData($rootScope.Scope.freeRollData);
						if ($scope.hideFull || $scope.hideEmpty) {
							$scope.lobbyList = $scope.lobby_freerolls;
							$scope.lobby_freerolls = [];
							for ( var i in $scope.lobbyList) {
								if ($scope.filterData($scope.lobbyList[i]))
									$scope.lobby_freerolls.push($scope.lobbyList[i]);
							}
							$scope.setTableMsg($scope.lobby_freerolls);
						}
					} else {
						$scope.lobby_freerolls = [];
						$scope.successMsg = data.successMsg;
					}
				} else
					$scope.lobby_freerolls = [];
				/*
				 * $timeout(function() { $(".today-match-list").niceScroll({
				 * cursorcolor : "#8799bc", speed : 2000 }); }, 1000)
				 */
			} else if (data.status == 'FAILED') {
				$scope.isError = true;
				$scope.errorMessage = data.errorDetails;
			} else {
				$scope.loading = false;
				$scope.pageError = true;
				$scope.page = {
					title : 'Error Page'
				};
				$scope.errorMessage = data.errorDetails;
			}
		})
	};
	
	
	
	

	$scope.isSuccess = false;
	$scope.loadingChips = false;
	$scope.chipsClaim = function() {
		$scope.loadingChips = true;
		var responsePromise = $http.get(contextPath + '/free/chips/claim');
		responsePromise.success(function(data) {
			if (data.status == "SUCCESS") {
				$scope.wallet.free = data.freeChips;
				$scope.wallet.freeDailyClaimed = data.freeDailyClaimed
				$scope.isSuccess = true;
				$scope.successChipsMsg = data.successMsg;
				$timeout(function() {
					$scope.isSuccess = false;
					$scope.loadingChips = false;
				}, 2000);
			} else if (data.status == 'FAILED') {
				$scope.isError = true;
				$scope.isSuccess = false;
				$scope.errorMessage = data.errorDetails;
				$timeout(function() {
					$scope.isError = false;
				}, 5000);
			}
			$scope.loadingChips = false;
		})
	};
	// $scope.firstCall = true;
	$scope.updateFreeRollLobbyData = function(data) {
		for ( var i in $scope.lobby_freerolls) {
			for ( var j in data) {
				if ($scope.lobby_freerolls[i].id == data[j].id) {
					$scope.lobby_freerolls[i].players = data[j].players;
				}
			}
		}
		// if ($scope.firstCall) {
		// $scope.getTablePlayerStats($scope.lobby_freerolls[0].id,
		// $scope.lobby_freerolls[0].title);
		// $scope.firstCall = false;
		// }
		// $scope.filterList();
	}
	
	
	function countProperties (obj) {
	    var count = 0;

	    for (var property in obj) {
	        if (Object.prototype.hasOwnProperty.call(obj, property)) {
	            count++;
	        }
	    }

	    return count;
	}
	$scope.tableRoll = {};
	$scope.updatePlayerStatData = function(data) {
		$timeout(function() {
			$scope.$apply(function() {
				
					$scope.roomStats = {};
					$scope.change = false;
				if(data.roomStats) {
					for(var l in data.roomStats) {
						if(data.roomStats[l].roomPlayers && data.roomStats[l].roomPlayers.length>0) {
							$scope.roomStats[l] = data.roomStats[l];
						}
					}
					if(data.title) {
						$scope.titleIfAbsent = data.title;
					}
				}
				$scope.roomStatsSize = countProperties($scope.roomStats);

				//$scope.roomStats = data.roomStats;
				for ( var i in $scope.lobby_freerolls) {
					if ($scope.lobby_freerolls[i].id == data.tid) {
						$scope.tableRoll = $scope.lobby_freerolls[i];
					}
				}
			
			});
		}, 0);
	}
	
	



	$scope.joinTableRoom = function(tableRoll, rid) {
		var data = {
			roll : tableRoll,
			rid : rid
		}
		SharedDataService.runCallBack("roomJoin", data);
	}
	
	$scope.joinTableRoomNow = function(tableRoll, rid) {
		var data = {
			roll : tableRoll,
			rid : rid
		}
		SharedDataService.runCallBack("roomJoinNow", data);
	}

	$scope.getRealplayList = function() {
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.notificationShow = false;
		$scope.transactionShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.leaderBoardShow = false;
		$scope.tableShow = true;
		$scope.selectMode('all');
		MCashService.setMycache('lobby.nav', 'realplay');
		$scope.lobby_freerollList($scope.listType = 'realplay');
		
	}
	
	$scope.getPersonalDetails = function() {
		$scope.is.ShowDetails = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.personalDetails = true;
		$scope.tableShow = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.notificationShow = false;
		$scope.transactionShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.leaderBoardShow = false;
		
		
	}
	
	$scope.getPlayerAchievement = function() {
		$scope.responsibleGameShow = false;
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = true;
		$scope.tableShow = false;
		$scope.notificationShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.transactionShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.leaderBoardShow = false;
		$scope.getDashboardPlayerAchievement();
		
		
	}
	
	$scope.getResponsibleGaming = function() {
		$scope.responsibleGameShow = true;
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.tableShow = false;
		$scope.notificationShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.transactionShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.leaderBoardShow = false;
		$scope.getResponsibleGame();
		
		
	}
	
	$scope.getLeaderBoard = function() {
		$scope.responsibleGameShow = false;
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.tableShow = false;
		$scope.notificationShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.transactionShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.leaderBoardShow = true;
		$scope.openLobbyLeaderBoard();
		
		
	}
	
	
	$scope.getNotificationHistory = function() {
		$scope.responsibleGameShow = false;
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.tableShow = false;
		$scope.referShow = false;
		$scope.leaderBoardShow = false;
		$scope.quizShow = false;
		$scope.notificationShow = true;
		$scope.transactionShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.showNotification();
		
		
	}
	
	$scope.getTransactionHistory = function() {
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.tableShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.transactionShow = true;
		$scope.notificationShow = false;
		$scope.leaderBoardShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.getDashboardPlayerAccount();
		
		
	}
	
	$scope.getKycDetails = function() {
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.tableShow = false;
		$scope.transactionShow = false;
		$scope.notificationShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.leaderBoardShow = false;
		$scope.kycShow = true;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.getDashboardFinancialDetails();
		
		
	}
	
	$scope.getFinancialDetails = function() {
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.tableShow = false;
		$scope.transactionShow = false;
		$scope.notificationShow = false;
		$scope.kycShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.financialShow = true;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.leaderBoardShow = false;
		$scope.getDashboardFinancialDetail();
		
		
	}
	
	$scope.getChipConversion = function() {
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.tableShow = false;
		$scope.transactionShow = false;
		$scope.notificationShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = true;
		$scope.changePasswordShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.leaderBoardShow = false;
		$scope.getChipConversionConfig();
		
		
	}
	
	$scope.getChangePassword = function() {
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.tableShow = false;
		$scope.transactionShow = false;
		$scope.notificationShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.changePasswordShow = true;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.leaderBoardShow = false;
		$scope.getDashboardPlayerAccount();
		
		
	}
	
	$scope.getAffiliate = function() {
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.tableShow = false;
		$scope.transactionShow = false;
		$scope.notificationShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.affiliateShow = true;
		$scope.avatarShow = false;
		$scope.leaderBoardShow = false;
		$scope.getAffiliateReferral();
		
		
	}
	
	$scope.getAvatar = function() {
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.tableShow = false;
		$scope.transactionShow = false;
		$scope.notificationShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.avatarShow = true;
		$scope.leaderBoardShow = false;
		$scope.getDashboardPlayerAccount();
		
		
	}
	
	$scope.getReferral = function() {
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.tableShow = false;
		$scope.transactionShow = false;
		$scope.notificationShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.referShow = true;
		$scope.quizShow = false;
		$scope.leaderBoardShow = false;
		$scope.getReferDetails();
		
		
	}
	
	$scope.getQuiz = function() {
		$scope.is.ShowDetails = false;
		$scope.personalDetails = false;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.tableShow = false;
		$scope.transactionShow = false;
		$scope.notificationShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.referShow = false;
		$scope.quizShow = true;
		$scope.leaderBoardShow = false;
		$scope.getDashboardPlayerElearning();
		
		
	}
	
	$scope.getLogout = function() {
		$scope.lobbyLogout(true);	
		window.close(); 
	} 
       
	$scope.getFreerollList = function() {
		$scope.personalDetails = false;
		$scope.tableShow = true;
		$scope.playerAchievement = false;
		$scope.responsibleGameShow = false;
		$scope.notificationShow = false;
		$scope.transactionShow = false;
		$scope.notificationShow = false;
		$scope.kycShow = false;
		$scope.financialShow = false;
		$scope.chipConversionShow = false;
		$scope.changePasswordShow = false;
		$scope.affiliateShow = false;
		$scope.avatarShow = false;
		$scope.referShow = false;
		$scope.quizShow = false;
		$scope.leaderBoardShow = false;
		$scope.selectMode('all');
		MCashService.setMycache('lobby.nav', 'freeroll');
		$scope.lobby_freerollList($scope.listType = 'freeroll');
		
	}
	
	$scope.openLobbyLeaderBoard = function() {
		$scope.loading = true;
		 var responsePromise = $http.get(contextPath + '/player/get/leaderBoard/config');
		    responsePromise.success(function(data) { 
		      if (data.status == 'SUCCESS') {
		    	  if(data.response.leaderBoardList) {
		    		  $scope.leaderBoardList = [];
		    		  $scope.leaderBoardList = data.response.leaderBoardList;
		    		  if($scope.leaderBoardList && $scope.leaderBoardList.length > 0) {
			    		  if($scope.leaderBoardList[0].type == 'HANDSPLAYED')
			    			  $scope.pointsValue = 'Rake Hands';
			    		  else
			    			  $scope.pointsValue = 'Points';
			    		  $scope.selectOption = $scope.leaderBoardList[0].title;
			    		  $scope.getLobbyLeaderBoard($scope.leaderBoardList[0].id);
			    	  }
		    	   }
		    	  
		    	  } else { 
		        $scope.pageError = true;
		        $scope.page = {
		          title : 'Error Page'
		        };
		        $scope.response.error = data.errorDetails;
		      }
		      $scope.dataLoading = false;
		      $scope.isLoading = false;
		    })
		
		
		
	}
	
	
	
	var option = null;
	
	
	
	

	$scope.getLobbyTournamentList = function(value) {
		$scope.is.ShowDetails = false;
		MCashService.setMycache('lobby.nav', 'tournament');
		// if (MCashService.get("tournament.id") != null && !value)
		// $scope.is.ShowDetails = true;
		// else if (value)
		// MCashService.remove("tournament.id");
		$scope.lobby_tournamentList('lobby');
		// MCashService.setMycache('lobby.nav', 'tournament');
	}

	// If player is logout
	$scope.logOutEvent = function() {
		$('.disconnected_user').modal('hide');
		$('.login').modal('show');
	}
	
	$scope.getTournamentDetailpopup = function () {
		$("#lobbyTournamentDetailPopup").modal('show');
	}

	$scope.lobbyLogout = function(logout) {
		if (logout) {
			$rootScope.Queue.Sender.run("3", {});
			var responsePromise = $http.post(contextPath + '/lobby/logout');
			responsePromise.success(function(data) {
				if (data.status == 'SUCCESS') {
					// SharedDataService.change('playerLoggedIn', false);
					$scope.isLogout = true;
				} else if (data.status == 'FAILED') {
					$scope.isError = true;
					$scope.errorMessage = data.errorDetails;
				}
			})
		}
		$('.log-out-alert').modal('hide');
	}

	LobbyNavService.setInits({
		freeroll : $scope.getFreerollList,
		realplay : $scope.getRealplayList,
		personalDetails : $scope.getPersonalDetails,
		playerAchievement : $scope.getPlayerAchievement,
		responsibleGame : $scope.getResponsibleGaming,
		//leaderBoard : $scope.getLeaderBoard,
		refer : $scope.getReferral,
		notificationHistory : $scope.getNotificationHistory,
		transactionHistory : $scope.getTransactionHistory,
		kyc : $scope.getKycDetails,
		financial : $scope.getFinancialDetails,
		chipConversion : $scope.getChipConversion,
		changePassword : $scope.getChangePassword,
		affiliate : $scope.getAffiliate,
		quiz : $scope.getQuiz,
		avatar : $scope.getAvatar,
		logout : $scope.getLogout
		
		//tournament : $scope.getLobbyTournamentList
	});

	SharedDataService.setCallBack('socketReconnect', function(event) {
		$timeout(function() {
			$rootScope.WseWebsocket.reconnect();
		});
	});

	if (MCashService.get("tournament.id") != undefined) {
		id = MCashService.get("tournament.id");
		$scope.getTournamentDetails(id);
	}
	
	

	$scope.loginLoader = false;
	// //Open Sign Up Methods
	$scope.openSignUpDialog = function() {
		$scope.isError = false;
		SharedDataService.runCallBack('openSignupDialog');
	};
	// //Login Social
	$scope.openSocialLoginDialog = function(type) {
		$scope.isError = false;
		SharedDataService.runCallBack('openSocialDialog', type);
	};

	// //Open Reset Dialog
	$scope.resetPassword = function() {
		$scope.isError = false;
		SharedDataService.runCallBack('openResetDialog');
	};

	// //Direct Login
	$scope.isError = false;
	$scope.login = function() {
		if ($scope.email != undefined
				&& $scope.email != null
				&& $scope.email != ''
				) {
			if ($scope.password != undefined && $scope.password != null && $scope.password != ''
					&& $scope.password.length >= 6) {
				$scope.loginLoader = true;
				$scope.isError = false;
				var data = {
					email : $scope.email,
					password : $scope.password
				}
				SharedDataService.runCallBack('directLogin', data);
			}
		}
	};

	// Login Error
	SharedDataService.setCallBack("errorRedirect", function(data) {
		$timeout(function() {
			if (data != undefined && data != null) {
				$scope.isError = true;
				$scope.errorMsg = data;
				$scope.loginLoader = false;
			} else {
				$scope.loginLoader = false;
				$scope.isError = false;
			}
		})
	});
	// //Get UserName Methods
	$scope.getPlayerUserName = function() {
		return PlayerData.getUserName();
	}
	  
	$(".toggle-pwd").click(function() {

		  $(this).toggleClass("fa-eye fa-eye-slash");
		  var input = $($(this).attr("toggle"));
		  if (input.attr("type") == "password") {
		    input.attr("type", "text");
		  } else {
		    input.attr("type", "password"); 
		  } 
		});
	$(function() {  
		$('#validateLogPassword') 
				.keydown( 
						function(e) { 
							
						}); 
	}); 
			
	$scope.sendSms = function() {
		var responsePromise = $http.get(contextPath + '/instaPlay/send/sms');
		responsePromise.success(function(data) {
			if (data.status == "SUCCESS") {
				$scope.isSuccess = true;
				$scope.successChipsMsg = data.successMsg;
				$timeout(function() {
					$scope.isSuccess = false;
					$scope.loadingChips = false; 
				}, 2000);
			} else if (data.status == 'FAILED') {
				$scope.isError = true; 
				$scope.isSuccess = false;
				$scope.errorMessage = data.errorDetails;
				$timeout(function() {
					$scope.isError = false;
				}, 5000);
			}
		})
	};
	
	$scope.tabKey = null;
	$scope.lobbyMobileNavChange = function(key) {
		LobbyNavService.runInit(key);
		$scope.tabKey = key;
	}
	
	SharedDataService.setCallBack('lobbyRedirect', function(key) {
		$timeout(function() {
			$scope.$apply(function($scope) {
				$scope.lobbyMobileNavChange(key);
				
			});
		});
	});
	
	$(document).ready(function() {
		$(".number-only").keypress(function() {
			return (/\d/.test(String.fromCharCode(event.which)))
		});
	});
	
	
}



