<!doctype html>
<html ng-app="wsegameApp" ng-controller="MainCtrl"
	class="no-js {{containerClass}}">
<head>
<meta charset="utf-8">
<title>LYVE-GAME</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width">
<!-- Place favicon.ico and apple-touch-icon.png in the root directory -->
<script type="text/javascript">
	var contextPath = "${pageContext.request.contextPath}";
</script>
</head>
<link rel="shortcut icon"
	href="${pageContext.request.contextPath}/resources/images/fav.png" />
<!-- build:css(.) styles/vendor.css?v=${sessionScope.version} -->
<!-- bower:css -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/bower_components/bootstrap/dist/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/bower_components/font-awesome/css/font-awesome.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/bower_components/rickshaw/rickshaw.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/bower_components/angular-loading-bar/build/loading-bar.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/bower_components/chosen/chosen.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/bower_components/animate.css/animate.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/styles/daterange/angular-date-range-picker.css?v=${sessionScope.version}">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/styles/datepicker/datetimepicker.css?v=${sessionScope.version}">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/styles/colorpicker/colorpicker.css?v=${sessionScope.version}">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/bower_components/ng-tags-input/ng-tags-input.css" />
<link
	href="${pageContext.request.contextPath}/resources/bower_components/edit-text/summernote.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/styles/main.css?v=${sessionScope.version}">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/styles/wsegame.css?v=${sessionScope.version}">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/styles/custom.css?v=${sessionScope.version}">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/bower_components/angular-ui-select/dist/select.css" />

<!-- endbuild -->
</head>
<body id="minovate"
	class="{{main.settings.navbarHeaderColor}} {{main.settings.activeColor}} {{containerClass}} header-fixed aside-fixed rightbar-hidden appWrapper"
	ng-class="{'header-fixed': main.settings.headerFixed, 'header-static': !main.settings.headerFixed, 'aside-fixed': main.settings.asideFixed, 'aside-static': !main.settings.asideFixed, 'rightbar-show': main.settings.rightbarShow, 'rightbar-hidden': !main.settings.rightbarShow}">


	<div id="wrap" ui-view autoscroll="false"></div>
	<!-- Page Loader -->
	<!-- <div id="pageloader" page-loader>
	</div> -->

	<div id="loading-overlay" class="ajax-page-loader">
		<span class="loading-page-indicator col-md-6"> </span>
	</div>

	<script
		src="${pageContext.request.contextPath}/resources/bower_components/jquery/jquery.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/chosen/chosen.jquery.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular/angular.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular/Chart.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/jquery/jquery.autoresize.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/edit-text/summernote.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/edit-text/angular-summernote.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular/angular-chart.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular/angular-cache.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-animate/angular-animate.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-chosen-localytics/chosen.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-fontawesome/dist/angular-fontawesome.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-fullscreen/src/angular-fullscreen.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-loading-bar/build/loading-bar.min.js"></script>
	<script type="text/javascript"
		src="https://rawgit.com/chinmaymk/angular-charts/bower/dist/angular-charts.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/d3/d3.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-resource/angular-resource.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-sanitize/angular-sanitize.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-smart-table/dist/smart-table.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-ui-router/release/angular-ui-router.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-ui-select/dist/select.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-ui-utils/ui-utils.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/oclazyload/dist/ocLazyLoad.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/ng-tags-input/ng-tags-input.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/countup/countUp.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/countup/angular-countUp.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/app.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/services.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/main.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/navcollapse.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/wsegame-navbar-permission.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/wsegame-no-space.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/wsegame-space-dash.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/wsegame-no-spacial-char.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/wsegame-model-dateformate.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/track-filter.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/numeric-only.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/hide-zero.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/percent-only.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/allow-decimal-numbers.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/unique-collection-byid.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/wsegame-smart-search.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/wsegame-smart-search-total.js?v=${sessionScope.version}"></script>

	<!-- Angular Controllers -->

	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/dashboard/dashboardCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/dashboard/dashboardHomeCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/dashboard/dashboardTournamentCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/dashboard/dashboardLiveCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/notAuthorized/notAuthorizedCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/report/reportCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/report/reportDetailCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/report/reportCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/report/reportUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/membership/membershipCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/membership/membershipUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/category/categoryCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/category/categoryUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/category/categoryCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerDetailsCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerBonusChipsCtrl.js?v=${sessionScope.version}"></script>
		<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerTransactionDetailsCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerBonusExpiredChipsCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerTdsCertificateListCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerDetailsCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerElearningCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerReportCtrl.js?v=${sessionScope.version}"></script>
		<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/playerReport/playerRecordReportCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/withdraw/withdrawCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/withdraw/withdrawUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/pokerCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tableCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tableCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tableUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tableConfigurationCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tableConfigurationCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tableConfigurationUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tournamentConfigurationCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tournamentConfigurationCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tournamentConfigurationUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tournamentCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tournamentCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tournamentUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/tournamentPlayerCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/poker/pokerMasterConfigurationUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/promotions/couponCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/promotions/couponCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/promotions/couponUpdateCtrl.js?v=${sessionScope.version}"></script>
		<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/promotions/CouponRedemptionPlayers.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/promotions/otherPromotionCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/promotions/otherPromotionCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/promotions/otherPromotionUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/chipconversion/chipConversionCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/loginValidate/loginValidateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/loginValidate/loginValidateUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/chipconversion/chipConversionCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/chipconversion/chipConversionUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/utilities/utilitiesCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/utilities/utilitiesDetailCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/leaderBoard/leaderBoardCreateCtrl.js?v=${sessionScope.version}"></script>
		<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/leaderBoard/leaderBoardCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/leaderBoard/leaderBoardUpdatectrl.js?v=${sessionScope.version}"></script>
		<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/leaderBoard/PlayerLeaderBoardPlayersCtrl.js?v=${sessionScope.version}"></script>
	<script 
		src="${pageContext.request.contextPath}/resources/scripts/controllers/affiliates/affiliateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/affiliates/affiliateCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/affiliates/affiliateUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/affiliates/referralCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/affiliates/referralCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/affiliates/affiliatePlayerCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/admin/adminCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/admin/adminUserCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/admin/adminUserUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/admin/departmentListCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/admin/departmentCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/admin/departmentUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/setting/userUpdatePasswordCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/setting/configurationUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/permission/permissionsCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/permission/permissionsCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/permission/permissionsUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/permission/roleCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/permission/roleCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/permission/roleUpdateCtrl.js?v=${sessionScope.version}"></script>
	
	
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/images/imageCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/images/imageCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/images/imageUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/images/typeCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/images/typeCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/images/typeUpdateCtrl.js?v=${sessionScope.version}"></script>
	
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/eLearningCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/questionCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/priceCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/voteCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/voteUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/priceCreateCtrl.js?v=${sessionScope.version}"></script>
		<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/priceUpdateCtrl.js?v=${sessionScope.version}"></script>
		<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/pollCtrl.js?v=${sessionScope.version}"></script>

	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/questionCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/voteCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/pollCreateCtrl.js?v=${sessionScope.version}"></script>	
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/pollUpdateCtrl.js?v=${sessionScope.version}"></script>	
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/questionUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/quizCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/quizUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/eLearning/elearningDashboardHomeCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/homeBlogCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/blogCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/ForumListCtrl.js?v=${sessionScope.version}"></script>
		<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/ForumCreateTitleCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/ForumUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerAchievement/PlayerAchievementCtrl.js?v=${sessionScope.version}"></script>
		<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerAchievement/PlayerAchievementCreateCtrl.js?v=${sessionScope.version}"></script>
		<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerAchievement/PlayerAchievementUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/player/playerAchievement/PlayerAchievementPlayers.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/blogCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/blogUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/topicCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/topicCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/topicUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/testimonialListCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/testimonialCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/blog/testimonialUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/payment/paymentListCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/IPRestriction/ipRestrictionListCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/marketings/MarketingCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/marketings/MarketingCtrl.js?v=${sessionScope.version}"></script>
	<script 
		src="${pageContext.request.contextPath}/resources/scripts/controllers/marketings/MarketingReportDetailsCtrl.js?v=${sessionScope.version}"></script>
	<script 
		src="${pageContext.request.contextPath}/resources/scripts/controllers/marketings/MarketingReportUpdateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/cricket/cricketContest/cricketContestCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/cricket/cricketContest/cricketContestCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/cricket/cricketBatting/cricketBattingCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/cricket/cricketBatting/cricketBattingCreateCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/model/modelCtrl.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/collapsesidebar.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/multiple-emails.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/popUp.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/controllers/nav.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/daterange/customDateRangePicker.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/datepicker/datetimepicker.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/daterange/bindonce.min.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/daterange/moment.min.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/daterange/moment-range.min.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/colorpicker/bootstrap-colorpicker-module.js?v=${sessionScope.version}"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/rickshaw/rickshaw.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/bower_components/angular-rickshaw/rickshaw.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/pageloader.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/scripts/directives/lazymodel.js"></script>

	<script type="text/javascript">
		$(function() {
			$("#loading-overlay").hide();
		});
	</script>

</body>
</html>

