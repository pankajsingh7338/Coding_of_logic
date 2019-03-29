function MCashService(CacheFactory) {
	return {
		setMycache : function(key, value) {
			var cache = '';
			if (CacheFactory.get('mobCache') == undefined) {
				cache = CacheFactory('mobCache', {
					maxAge : 915 * 60 * 1000, // Items added to this cache
					deleteOnExpire : 'passive', // Items will be deleted
					storageMode : 'localStorage' // This cache will use
				});
			} else {
				cache = CacheFactory.get('mobCache');
			}
			cache.put(key, value);
		},
		get : function(key) {
			var cache = '';
			if (CacheFactory.get('mobCache') == undefined) {
				cache = CacheFactory('mobCache', {
					maxAge : 915 * 60 * 1000, // Items added to this cache
					deleteOnExpire : 'passive', // Items will be deleted
					storageMode : 'localStorage' // This cache will use
				});
			} else {
				cache = CacheFactory.get('mobCache');
			}
			return cache.get(key);
		},
		remove : function(key) {
			var cache = '';
			if (CacheFactory.get('mobCache') == undefined) {
				cache = CacheFactory('mobCache', {
					maxAge : 915 * 60 * 1000, // Items added to this cache
					deleteOnExpire : 'passive', // Items will be deleted
					storageMode : 'localStorage' // This cache will use
				});
			} else {
				cache = CacheFactory.get('mobCache');
			}
			cache.remove(key);
		}
	}
}

var globle = {};

// //Shared Service for data
angular.module('shared-data-service', []).service('SharedDataService', function() {
	var obj = globle;
	return {
		setValue : function(key, value) {
			obj[key] = {
				value : value,
				onChange : []
			};
		},
		setCallBack : function(key, func) {
			obj[key] = {
				callBack : func
			};
		},
		runCallBack : function(key, arg) {
			if (obj[key])
				obj[key].callBack(arg)
		},
		removeCallBack : function(key) {
			if (obj[key])
				delete obj[key];
		},
		change : function(key, value) {
			obj[key].value = value;
			if (obj[key].onChange.length) {
				var changes = obj[key].onChange;
				for ( var i in changes) {
					changes[i](value);
				}
			}
		},
		onChange : function(key, func) {
			obj[key].onChange.push(func);
		},
		get : function(key) {
			return obj[key];
		}
	}
});

// Shared Service for data
angular.module('player-class-offer', []).service('PlayerClassOffer', function($http) {
	return {
		getPlayerClassOffer : function() {
			return $http.get(contextPath + '/player/class/offer');
		}
	}
});

// //Pagination Filter Function

function pagination() {
	return function(input, start) {
		start = +start;
		if (input && input.length > 0)
			return input.slice(start);
		else
			return 0;
	};
}

// //Data Encryption Service for data
angular.module('data-encryption-service', []).service('DataEncriptionService', function($http) {

	'use strict';

	var AesUtil = function(keySize, iterationCount) {
		this.keySize = keySize / 32;
		this.iterationCount = iterationCount;
	};

	AesUtil.prototype.generateKey = function(salt, passPhrase) {
		var key = CryptoJS.PBKDF2(passPhrase, CryptoJS.enc.Hex.parse(salt), {
			keySize : this.keySize,
			iterations : this.iterationCount
		});
		return key;
	}

	AesUtil.prototype.encrypt = function(salt, iv, passPhrase, plainText) {
		var key = this.generateKey(salt, passPhrase);
		var encrypted = CryptoJS.AES.encrypt(plainText, key, {
			iv : CryptoJS.enc.Hex.parse(iv)
		});
		return encrypted.ciphertext.toString(CryptoJS.enc.Base64);
	}

	AesUtil.prototype.decrypt = function(salt, iv, passPhrase, cipherText) {
		var key = this.generateKey(salt, passPhrase);
		var cipherParams = CryptoJS.lib.CipherParams.create({
			ciphertext : CryptoJS.enc.Base64.parse(cipherText)
		});
		var decrypted = CryptoJS.AES.decrypt(cipherParams, key, {
			iv : CryptoJS.enc.Hex.parse(iv)
		});
		return decrypted.toString(CryptoJS.enc.Utf8);
	}

	function Encryptor() {
		var token, iv, ks, ed, itc, salt = void 0;
		token = iv = ks = ed = itc = salt;
		var onEncrptionFunc = void 0;
		var self = this;

		this.checkToken = function() {
			var t = Tokenizer().get("token");
			if (t && t.length) {
				var ioh = t.lastIndexOf("-");
				if (ioh > 10) {
					var ksitc = t.substring(ioh + 1);
					if (ksitc.length > 2) {
						var ioc = ksitc.indexOf(":");
						ks = parseInt(ksitc.substring(0, ioc));
						itc = parseInt(ksitc.substring(ioc + 1));
						if (ks > 0 && itc > 0) {
							token = t.substring(0, ioh);
							setIVandSALT(Tokenizer().get("salt"));
						} else
							thorwTokenNotValid();
					} else {
						thorwTokenNotValid()
					}
				} else {
					thorwTokenNotValid()
				}
			} else
				thorwTokenNotValid();
			return self;
		}

		this.setEncryptionData = function(data) {
			ed = data;
		}

		this.encrypt = function() {
			if (salt != void 0 && token != void 0 && iv != void 0 && ks != void 0 && itc != void 0) {
				if (onEncrptionFunc != void 0) {
					if (ed != void 0)
						getEncrpted(ed);
					else
						throw Error("Encryption data not found!");
				} else
					throw Error("On Encryption call back function is not set!");
			} else {
				throw Error("token is not set");
			}
			return self;
		}

		this.encryptNow = function() {
			self.checkToken();
			self.encrypt();
		}

		this.onEncryption = function(func) {
			if (typeof func == "function") {
				onEncrptionFunc = func;
			} else
				throw Error("only function accepted! Caught \"" + typeof func + "\"");
			return self;
		}

		function getEncrpted(data) {

			function encryptString(d) {
				var aesUtil = new AesUtil(ks, itc);
				var ciphertext = void 0;
				if (d != void 0)
					data = d;
				ciphertext = aesUtil.encrypt(salt, iv, token, data);
				onEncrptionFunc(ciphertext);
			}

			function encryptObject() {
				return encryptString(JSON.stringify(data));
			}

			switch (typeof data) {
			case "object":
				if (data != null)
					encryptObject();
				else
					throw Error("Unable to encript data of \"Null\"");
				break;
			case "string":
				encryptString();
				break;
			default:
				throw Error("Unable to encript data of type \"" + typeof data + "\"");
			}
		}

		function thorwTokenNotValid() {
			throw Error("token not valid");
		}

		function setIVandSALT(text) {
			var ioa = text.indexOf("#");
			salt = text.substring(0, ioa);
			iv = text.substring(ioa + 1);
		}
	}

	function getNewModel(value) {
		return {
			value : value,
			lastUpdated : (value != void 0) ? (new Date()) : (void 0)
		}
	}
	var obj = {
		token : getNewModel(),
		salt : getNewModel()
	};

	function Tokenizer() {

		var TOKEN_MAX_LIFE = 30000;

		return {
			get : function(name) {
				if (name in obj) {
					var ins = obj[name];
					if (ins.lastUpdated != void 0) {
						if (name == "token") {
							if ((ins.lastUpdated.getTime() + TOKEN_MAX_LIFE) > new Date().getTime()) {
								return ins.value;
							} else
								throw Error("Player Login Token Expired");
						} else
							return ins.value;
					} else
						throw Error("Player login " + name + " in never assinged");
				} else {
					throw Error("Tokenizer function doesn't contain " + name);
				}

			},
			set : function(key, value) {
				obj[key] = getNewModel(value);
			}
		};
	}

	var EI = {
		getInstance : function() {
			// if (Tokenizer().get("token") != void 0) {
			return new Encryptor();
			// }
			// throw Error("Token not Available");
		},
		setToken : function() {
			var responsePromise = $http.get(contextPath + '/player/login/token');
			responsePromise.success(function(data) {
				if (data.status == "SUCCESS") {
					data = JSON.parse(data.successMsg);
					Tokenizer().set("salt", data.salt);
					Tokenizer().set("token", data.token);
					if (RETURN.instance != void 0)
						RETURN.instance.encryptNow();
					else
						throw Error("Error! unable to find encryptor object");
				} else {
					throw Error("Unable to get Login Token");
				}
			})
		}
	}

	var RETURN = {
		instance : void 0,
		getNewInstance : function() {
			EI.setToken();
			RETURN.instance = EI.getInstance();
			return RETURN.instance;
		}
	};
	return RETURN;
});

var socketData = {};

// //Socket-connect
angular.module('player-data', []).service('PlayerData', function($http, $rootScope, $interval) {
	var playerData = {
		lastPlayerLoginValue : void 0,
		isPlayerLoggedIn : false,
		playerData : void 0,
		playerLoginData : void 0,
		onLoginValueChange : [],
		onServerError : void 0,
		runOnceOnLoginValueChange : void 0
	};

	var obj = {
		firstLoginToken : new Date().getTime(),
		isPlayerLoggedIn : function() {
			return playerData.isPlayerLoggedIn;
		},
		resetFirstLoginToken : function() {
			this.fistLoginToken = new Date().getTime();
		},
		getPlayerData : function() {
			return playerData.playerData;
		},
		getPlayerLoginData : function() {
			return playerData.playerLoginData;
		},
		getUserName : function() {
			return (playerData.playerLoginData != null ? playerData.playerLoginData.userName : "");
		},
		getPlayerWallet : function() {
			if (playerData.playerData) {
				if (playerData.playerData.tournamentImage == null)
					playerData.playerData.tournamentImage = contextPath + "/resources/lyve/images/sapphire.jpg";
				return {
					cash : playerData.playerData.realChips,
					free : playerData.playerData.freerollChips,
					freeDailyClaimed : playerData.playerData.freeDailyClaimed,
					bonus : playerData.playerData.bonusChips,
					tournamentPoints : playerData.playerData.tournamentPoints,
					vipPoints : playerData.playerData.vipPoints,
					recentTransactions : playerData.playerData.recentTransactionsMessage,
					onlinePlayers : playerData.playerData.onlinePlayers,
					tournamentPlayers : playerData.playerData.tournamentPlayers,
					tournamentImage : playerData.playerData.tournamentImage,
					avatar : playerData.playerData.avatar,
					playerClass : playerData.playerData.playerClass,
					gamePlaySetting : playerData.playerData.gamePlaySetting
				}
			}
		},
		runAllLoginCallBack : function(value) {
			playerData.lastPlayerLoginValue = value;
			playerData.isPlayerLoggedIn = value;
			rullAllOnLoginChangeCallbacks();
		},
		onLoginValueChange : function(func) {
			if (typeof func === "function")
				playerData.onLoginValueChange.push(func);
			else
				throw Error("Only function is required for callBack");
		},
		runOnceAfterLogin : function(func) {
			if (typeof func === "function")
				playerData.runOnceOnLoginValueChange = func;
			else
				throw Error("Only function is required for callBack");
		},
		onServerError : function(func) {
			if (typeof func === "function")
				playerData.onServerError = func;
			else
				throw Error("Only function is required for callBack");
		}

	}

	if (!playerrData)
		playerrData = obj;
	else
		return playerrData;

	function rullAllOnLoginChangeCallbacks() {
		for ( var index in playerData.onLoginValueChange) {
			playerData.onLoginValueChange[index](playerData.isPlayerLoggedIn);
		}
	}

	function successResponse(data) {
		if (data.status == 'SUCCESS') {
			successResponseStatus(data);
			setTimeout( function(){ 
				$rootScope.SharedDataService.runCallBack("closeSocialWindow"); 
			}, 1000);
		} else if (data.status == 'SESSION_NOT_FOUND') {
			noSessionResponse(data);
		}
	}
	
	window.onunload = function() {
		$rootScope.SharedDataService.runCallBack("closeSocialWindow");
	};

	function successResponseStatus(data) {
		playerData.isPlayerLoggedIn = true;
		playerData.playerLoginData = data.response;
		if (data.response && data.response.dashboardResponse) {
			playerData.playerData = data.response.dashboardResponse;
		}
		if (playerData.lastPlayerLoginValue != playerData.isPlayerLoggedIn) {
			playerData.lastPlayerLoginValue = true;
			rullAllOnLoginChangeCallbacks();
			if (playerData.runOnceOnLoginValueChange)
				playerData.runOnceOnLoginValueChange();
			$rootScope.SharedDataService.runCallBack("closeSocialWindow");
			$rootScope.SharedDataService.runCallBack('closeLoginPopup');
		}
	}

	function noSessionResponse(data) {
		playerData.isPlayerLoggedIn = false;
		playerData.playerLoginData = void 0;
		playerData.playerData = void 0;
		if (playerData.lastPlayerLoginValue != playerData.isPlayerLoggedIn) {
			playerData.lastPlayerLoginValue = false;
			rullAllOnLoginChangeCallbacks();
		}
	}

	function firstLoginResponse(pid) {
		$rootScope.SharedDataService.runCallBack("closeSocialWindow");
		$rootScope.SharedDataService.runCallBack("showTermAccept", pid);
	}

	function getFirstPlayerData() {
		return $http.get(contextPath + '/player/connection');
	}

	function getPlayerData() {
		return $http.get(contextPath + '/player/connection');
	}
	
	
/*autologout starts here
	
	
	var idleTime = 0;
	$(document).ready(function () {
	    //Increment the idle time counter every minute.
	    var idleInterval = setInterval(timerIncrement, 60000); // 1 minute

	    //Zero the idle timer on mouse movement.
	    $(this).mousemove(function (e) {
	        idleTime = 0;
	        timerIncrement();
	    });
	    $(this).keypress(function (e) {
	        idleTime = 0;
	        timerIncrement();
	    });
	    $(window).load(function() { // better to use $(document).ready(function(){
	        $('.List li').on('click touchstart', function() {
	        	idleTime = 0;
		        timerIncrement();
	        });
	    });
	});

	function timerIncrement() {
	    idleTime = idleTime + 1;
	    if (idleTime > 9) { // 10 minutes
	    	if(playerData.isPlayerLoggedIn)
	    		window.location.href = contextPath + '/logout';
	    }
	}
	
	autologout ends here*/

	function run() {
		if (document.cookie && document.cookie.includes('LYVE_TOKEN'))
			getPlayerData().success(successResponse);
		else {
			if (document.cookie && document.cookie.includes(playerrData.firstLoginToken)) {
				var all = document.cookie.split(';').map(function(d) {
					var arr = d.split('=');
					return {
						key : arr[0] ? arr[0].trim() : '',
						value : arr[1] ? arr[1].trim() : ''
					}
				});
				var pid = '';
				for ( var i in all) {
					if (all[i].key == playerrData.firstLoginToken) {
						pid = all[i].value;
					}
				}
				firstLoginResponse(pid);
			} else {
				noSessionResponse({});
			}
		}
	}

	getFirstPlayerData().success(function(data) {
		if (data.status == 'SUCCESS') {
			successResponseStatus(data);
		} else if (data.status == 'SESSION_NOT_FOUND') {
			playerData.lastPlayerLoginValue = false;
			rullAllOnLoginChangeCallbacks();
		}
	});

	var interval = $interval(function() {
		run();
	}, 5000);

	return obj;
});

// socket heartbeat schedular
function HeartBeatScheduler($interval, $rootScope) {
	var heartBeatScheduler = {
		load : function(str) {
			var self = this;
			var initialDataReceived = false;
			var lastHeartBeatRecevied;
			var connected = false;
			var heartBeatTimeOut;
			var lifeTimeOut;
			var sendHeartBeatInterval;
			var Intervals = [];

			var keepAlive = function(h, t) {
				heartBeatTimeOut = h * 1000;
				lifeTimeOut = t * 1000;
				setHeartBeatToSendSignal();
				checkHeartBeatToKeepAlive();
			}

			var setHeartBeatToSendSignal = function(h) {
				connected = true;
				sendSignal();
				Intervals.sendHeartBeatInterval = $interval(sendSignal, heartBeatTimeOut);
			}

			var sendSignal = function() {
				if (!connected)
					self.restoreDefault();
				else {
					// console.log(" Sending Heart Beat")
					$rootScope.WseWebsocket.send('2:');
				}
			}

			var checkHeartBeatToKeepAlive = function() {
				if (connected) {
					Intervals.checkHeartBeatInterval = $interval(checkSignal, 1000);
				} else {
					// console.log("Websocket Disconnected");
				}
			}

			var checkSignal = function() {
				if (lastHeartBeatRecevied) {
					var time = (new Date().getTime() - lastHeartBeatRecevied.getTime());
					if (time > lifeTimeOut) {
						// console.log("Websocket Dead : HeartBeat TimeOut
						// expired");
						self.restoreDefault();
						$rootScope.WseWebsocket.reconnect();
						heartBeatScheduler.instance = void 0;
					}
				}
			}

			this.heartBeatReceived = function() {
				// console.log(' Heart Beat Recieved in : ' + (new
				// Date().getTime() - lastHeartBeatRecevied.getTime())
				// / 1000 + 's');
				lastHeartBeatRecevied = new Date();
			}

			this.restoreDefault = function() {
				connected = false;
				initialDataReceived = false;
				heartBeatTimeOut = void 0;
				lifeTimeOut = void 0;
				for ( var i in Intervals) {
					$interval.cancel(Intervals[i]);
				}
			}

			lastHeartBeatRecevied = new Date();
			var d = str.indexOf(':', 2);
			var heartBeat = Number(str.substring(2, d));
			var timeOut = Number(str.substring(d + 1));
			// console.log('Websocket HeartBeat Activated : up-' + heartBeat + '
			// down-' + timeOut);
			initialDataReceived = true;
			keepAlive(heartBeat, timeOut);

		},
		lastHeartBeatData : void 0,
		instance : void 0,
		startHeartBeat : function(str) {
			// if (heartBeatScheduler.instance == void 0)
			if (heartBeatScheduler.instance) {
				heartBeatScheduler.instance.restoreDefault();
				heartBeatScheduler.instance = void 0;
			}
			if (str) {
				heartBeatScheduler.instance = new heartBeatScheduler.load(str);
				heartBeatScheduler.lastHeartBeatData = str;
			} else if (heartBeatScheduler.lastHeartBeatData) {
				heartBeatScheduler.instance = new heartBeatScheduler.load(heartBeatScheduler.lastHeartBeatData);
			}
		},
		stopHeartBeat : function() {
			if (heartBeatScheduler.instance) {
				heartBeatScheduler.instance.restoreDefault();
				heartBeatScheduler.instance = void 0;
			} else {
				// console.warn("Some thing went wroung. Trying to stop
				// heartbeat which is never create");
			}
		},
		heartBeatReceived : function() {

			if (!heartBeatScheduler.instance) {
				if (heartBeatScheduler.lastHeartBeatData) {
					heartBeatScheduler.instance = new heartBeatScheduler.load(heartBeatScheduler.lastHeartBeatData);
					heartBeatScheduler.instance.heartBeatReceived();
				} else {
					// console.warn("Some thing went wroung. Trying to check
					// heartbeat which is never create");
				}
			} else
				heartBeatScheduler.instance.heartBeatReceived();
		}
	}
	return heartBeatScheduler;
};

// //Schedular service
angular.module('ngSchedular', []).service('Schedular', function() {
	function schedular(data) {
		var obj = {
			sch : data.func,
			arg : data.arguments,
			interval : data.time,
			intervalInstance : void 0,
			onStart : data.onStart,
			onStop : data.onStop
		}
		this.start = function() {
			if (obj.intervalInstance == void 0) {
				obj.intervalInstance = setInterval(obj.sch(obj.arg), obj.interval);
				if (typeof obj.onStart == "function")
					obj.onStart();
			} else {
				throw Error("Unable to start again! Schedular already running");
			}
		}

		this.stop = function() {
			if (obj.intervalInstance != void 0) {
				clearInterval(obj.intervalInstance);
				obj.intervalInstance = void 0;
				if (typeof obj.onStop == "function")
					obj.onStop();
			} else {
				throw Error("Unable to stop! Schedular not running");
			}
		}
	}
	return schedular;
});

