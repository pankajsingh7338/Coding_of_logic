'use strict';
var app = angular
		.module(
				'wsegameApp',
				[ 'angular-cache', 'ngAnimate', 'ngResource', 'ngSanitize',
						'ui.bootstrap', 'ui.router', 'ui.utils',
						'angular-loading-bar', 'lazyModel', 'oc.lazyLoad',
						'ui.select', 'smart-table', 'ngTagsInput',
						'countUpModule', 'angular-rickshaw', 'dateRangePicker',
						'ui.bootstrap.datetimepicker', 'colorpicker.module',
						'chart.js', 'summernote' ])
		.run(
				[
						'$rootScope',
						'$state',
						'$stateParams',
						function($rootScope, $state, $stateParams) {
							$rootScope.$state = $state;
							$rootScope.$stateParams = $stateParams;
							$rootScope
									.$on(
											'$stateChangeSuccess',
											function(event, toState) {
												event.targetScope
														.$watch(
																'$viewContentLoaded',
																function() {
																	angular
																			.element(
																					'html, body, #content')
																			.animate(
																					{
																						scrollTop : 0
																					},
																					200);
																	setTimeout(
																			function() {
																				angular
																						.element(
																								'#wrap')
																						.css(
																								'visibility',
																								'visible');
																				if (!angular
																						.element(
																								'.dropdown')
																						.hasClass(
																								'open')) {
																					angular
																							.element(
																									'.dropdown')
																							.find(
																									'>ul')
																							.slideUp();
																				}
																			},
																			200);
																});
												$rootScope.containerClass = toState.containerClass;
											});
						} ])

		.config([ 'uiSelectConfig', function(uiSelectConfig) {
			uiSelectConfig.theme = 'bootstrap';
		} ])

		.config(function(CacheFactoryProvider) {
			angular.extend(CacheFactoryProvider.defaults, {
				maxAge : 989 * 60 * 1000,
				deleteOnExpire : 'passive',
				storageMode : 'localStorage'
			});app.forum

		})

		.config(
				[
						'$stateProvider',
						'$urlRouterProvider',
						function($stateProvider, $urlRouterProvider) {

							$urlRouterProvider.otherwise('/dashboard/home');
							$stateProvider

									.state('app', {
										abstract : true,
										// url : '/ajx',
										templateUrl : 'views/tmpl/app.html'
									})
									// dashboard

									.state(
											'app.dashboard',
											{
												url : '/dashboard',
												controller : 'DashboardCtrl',
												templateUrl : 'views/tmpl/dashboard/dashboard_template.html',
											})
									.state(
											'app.dashboard.home',
											{
												url : '/home',
												controller : 'DashboardHomeCtrl',
												templateUrl : 'views/tmpl/dashboard/dashboard_home_template.html',
											})

									.state(
											'app.errorPage',
											{
												url : '/not/authorized',
												controller : 'NotAuthorizedCtrl',
												templateUrl : 'views/tmpl/notAuthorized/not_authorized_template.html',
											})
									// /////////////Table
									.state(
											'app.table',
											{
												url : '/poker/table',
												controller : 'TableCtrl',
												templateUrl : 'views/tmpl/poker/poker_template.html',
											})
									.state(
											'app.table.list',
											{
												url : '/list',
												controller : 'TableCtrl',
												templateUrl : 'views/tmpl/poker/table_list_template.html',
												params : {
													status : null
												}

											})
									.state(
											'app.table.create',
											{
												url : '/create',
												controller : 'TableCreateCtrl',
												templateUrl : 'views/tmpl/poker/table_create_template.html',

											})
									.state(
											'app.table.update',
											{
												url : '/update/:id',
												controller : 'TableUpdateCtrl',
												templateUrl : 'views/tmpl/poker/table_update_template.html',

											})
									// ///Table configuration
									.state(
											'app.tableConfiguration',
											{
												url : '/poker/table-configuration',
												controller : 'TableConfigurationCtrl',
												templateUrl : 'views/tmpl/poker/poker_template.html',
											})
									.state(
											'app.tableConfiguration.list',
											{
												url : '/list',
												controller : 'TableConfigurationCtrl',
												templateUrl : 'views/tmpl/poker/table_config_list_template.html',

											})
									.state(
											'app.tableConfiguration.create',
											{
												url : '/create',
												controller : 'TableConfigurationCreateCtrl',
												templateUrl : 'views/tmpl/poker/table_config_create_template.html',

											})
									.state(
											'app.tableConfiguration.update',
											{
												url : '/update/:id',
												controller : 'TableConfigurationUpdateCtrl',
												templateUrl : 'views/tmpl/poker/table_config_update_template.html',

											})
									// ///Tournament
									.state(
											'app.tournament',
											{
												url : '/poker/tournament',
												controller : 'TournamentCtrl',
												templateUrl : 'views/tmpl/poker/poker_template.html',
											})
									.state(
											'app.tournament.list',
											{
												url : '/list',
												controller : 'TournamentCtrl',
												templateUrl : 'views/tmpl/poker/tournament_list_template.html',
												params : {
													status : null
												}

											})
									.state(
											'app.tournament.create',
											{
												url : '/create',
												controller : 'TournamentCreateCtrl',
												templateUrl : 'views/tmpl/poker/tournament_create_template.html',

											})
									.state(
											'app.tournament.update',
											{
												reloadOnSearch : false,
												url : '/update/:id?tab',
												controller : 'TournamentUpdateCtrl',
												templateUrl : 'views/tmpl/poker/tournament_update_template.html',

											})
									// ///Tournament configuration
									.state(
											'app.tournamentConfiguration',
											{
												url : '/poker/tournament-configuration',
												controller : 'TournamentConfigurationCtrl',
												templateUrl : 'views/tmpl/poker/poker_template.html',
											})
									.state(
											'app.tournamentConfiguration.list',
											{
												url : '/list',
												controller : 'TournamentConfigurationCtrl',
												templateUrl : 'views/tmpl/poker/tournament_config_list_template.html',

											})
									.state(
											'app.tournamentConfiguration.create',
											{
												url : '/create',
												controller : 'TournamentConfigurationCreateCtrl',
												templateUrl : 'views/tmpl/poker/tournament_config_create_template.html',

											})
									.state(
											'app.tournamentConfiguration.update',
											{
												url : '/update/:id',
												controller : 'TournamentConfigurationUpdateCtrl',
												templateUrl : 'views/tmpl/poker/tournament_config_update_template.html',

											})
									// ///// Poker Master Configuration
									.state(
											'app.poker',
											{
												url : '/poker',
												controller : 'PokerCtrl',
												templateUrl : 'views/tmpl/poker/poker_template.html',

											})
									.state(
											'app.poker.poker_master_config',
											{
												url : '/master-configuration/update/',
												controller : 'PokerMasterConfigurationUpdateCtrl',
												templateUrl : 'views/tmpl/poker/master_config_update_template.html',
											})
									// // ///Promotions
									// .state(
									// 'app.promotions',
									// {
									// url : '/promotion',
									// templateUrl :
									// 'views/tmpl/promotions/promotions_template.html',
									//
									// })
									// ///// Coupons
									.state(
											'app.coupons',
											{
												url : '/promotion/coupon',
												templateUrl : 'views/tmpl/promotions/promotions_template.html',

											})
									.state(
											'app.coupons.list',
											{
												url : '/list',
												controller : 'CouponCtrl',
												templateUrl : 'views/tmpl/promotions/coupon_list_template.html',
												params : {
													status : null
												}
											})
									.state(
											'app.coupons.create',
											{
												url : '/create',
												controller : 'CouponCreateCtrl',
												templateUrl : 'views/tmpl/promotions/coupon_create_template.html',

											})
									.state(
											'app.coupons.update',
											{
												url : '/update/:id',
												controller : 'CouponUpdateCtrl',
												templateUrl : 'views/tmpl/promotions/coupon_update_template.html',
											})
											
											// others_promotions
											.state(
											'app.othersPromotions',
											{
												url : '/promotion/others',
												templateUrl : 'views/tmpl/promotions/others_promotions_template.html',

											})
									.state(
											'app.othersPromotions.list',
											{
												url : '/list',
												controller : 'otherPromotionCtrl',
												templateUrl : 'views/tmpl/promotions/other_promotion_list.html',
												params : {
													status : null
												}
											})
									.state(
											'app.othersPromotions.create',
											{
												url : '/create',
												controller : 'otherPromotionCreateCtrl',
												templateUrl : 'views/tmpl/promotions/other_promotion_create_template.html',

											})
									.state(
											'app.othersPromotions.update',
											{
												url : '/update/:id',
												controller : 'otherPromotionUpdateCtrl',
												templateUrl : 'views/tmpl/promotions/other_promotion_update_template.html',
											})
									// ///// Affilate
									.state(
											'app.affiliates',
											{
												url : '/affiliate',
												templateUrl : 'views/tmpl/affiliates/affiliate_template.html',

											})
									.state(
											'app.affiliates.affiliate',
											{
												url : '/affilate',
												templateUrl : 'views/tmpl/affiliates/affiliate_template.html',

											})
									.state(
											'app.affiliates.affiliate_list',
											{
												url : '/affiliates/list',
												controller : 'AffiliateCtrl',
												templateUrl : 'views/tmpl/affiliates/affiliate_list_template.html',

											})
									.state(
											'app.affiliates.affiliate_create',
											{
												reloadOnSearch : false,
												url : '/affilate/create?tab',
												controller : 'AffiliateCreateCtrl',
												templateUrl : 'views/tmpl/affiliates/affiliate_create_template.html',

											})
									.state(
											'app.affiliates.affiliate_update',
											{
												reloadOnSearch : false,
												url : '/affilate/update/:id?tab',
												controller : 'AffiliateUpdateCtrl',
												templateUrl : 'views/tmpl/affiliates/affiliate_update_template.html',
											})
									.state(
											'app.affiliates.referral_list',
											{
												url : '/referral-code/list',
												controller : 'ReferralCtrl',
												templateUrl : 'views/tmpl/affiliates/referral_list_template.html',

											})
									.state(
											'app.affiliates.referral_create',
											{
												url : '/referral-code/create',
												controller : 'ReferralCreateCtrl',
												templateUrl : 'views/tmpl/affiliates/referral_create_template.html',

											})
									.state(
											'app.affiliates.referral_update',
											{
												reloadOnSearch : false,
												url : '/referral-code/update/:id?tab',
												controller : 'ReferralUpdateCtrl',
												templateUrl : 'views/tmpl/affiliates/referral_update_template.html',
											})
									// ////User//////////
									.state(
											'app.admin',
											{
												url : '/systemadmin/user',
												controller : 'AdminCtrl',
												templateUrl : 'views/tmpl/admin/user_template.html',
											})
									.state(
											'app.admin.list',
											{
												url : '/list',
												controller : 'AdminCtrl',
												templateUrl : 'views/tmpl/admin/user_list_template.html',
											})
									.state(
											'app.admin.user',
											{
												url : '/create',
												controller : 'AdminUserCreateCtrl',
												templateUrl : 'views/tmpl/admin/create_user_template.html',

											})

									.state(
											'app.admin.update',
											{
												url : '/update/:id',
												params : {
													departmentId : null
												},
												controller : 'AdminUserUpdateCtrl',
												templateUrl : 'views/tmpl/admin/update_user_template.html',
											})
									// ///Department////////
									.state(
											'app.department',
											{
												url : '/systemadmin/department',
												templateUrl : 'views/tmpl/admin/department_template.html',

											})
									.state(
											'app.department.list',
											{
												url : '/list',
												controller : 'DepartmentListCtrl',
												templateUrl : 'views/tmpl/admin/department_list_template.html',

											})
									.state(
											'app.department.create',
											{
												url : '/create',
												controller : 'DepartmentCreateCtrl',
												templateUrl : 'views/tmpl/admin/department_create_template.html',

											})

									.state(
											'app.department.update',
											{
												url : '/update/:id',
												controller : 'DepartmentUpdateCtrl',
												templateUrl : 'views/tmpl/admin/department_update_template.html',
											})

									// ////Permission
									.state(
											'app.permissions',
											{
												url : '/permission/permissions',
												controller : 'PermissionsCtrl',
												templateUrl : 'views/tmpl/permission/permission_template.html',
											})
									.state(
											'app.permissions.list',
											{
												url : '/list',
												controller : 'PermissionsCtrl',
												templateUrl : 'views/tmpl/permission/permissions_list_template.html',
											})
									.state(
											'app.permissions.create',
											{
												url : '/create',
												controller : 'PermissionsCreateCtrl',
												templateUrl : 'views/tmpl/permission/permissions_create_template.html',
											})

									.state(
											'app.permissions.update',
											{
												reloadOnSearch : false,
												url : '/update/:id?tab',
												controller : 'PermissionsUpdateCtrl',
												templateUrl : 'views/tmpl/permission/permissions_update_template.html',
											})

									// /// Role
									.state(
											'app.role',
											{
												url : '/permission',
												controller : 'RoleCtrl',
												templateUrl : 'views/tmpl/permission/permission_template.html',
											})
									.state(
											'app.role.list',
											{
												url : '/role/list',
												controller : 'RoleCtrl',
												templateUrl : 'views/tmpl/permission/role_list_template.html',
											})
									.state(
											'app.role.create',
											{
												url : '/role/create',
												controller : 'RoleCreateCtrl',
												templateUrl : 'views/tmpl/permission/role_create_template.html',
											})

									.state(
											'app.role.update',
											{
												reloadOnSearch : false,
												url : '/role/update/:id?tab',
												controller : 'RoleUpdateCtrl',
												templateUrl : 'views/tmpl/permission/role_update_template.html',
											})
											
									// ////image
									.state(
											'app.images',
											{
												url : '/image/images',
												controller : 'ImageCtrl',
												templateUrl : 'views/tmpl/images/image_template.html',
											})
									.state(
											'app.images.list',
											{
												url : '/list',
												controller : 'ImageCtrl',
												templateUrl : 'views/tmpl/images/image_list_template.html',
											})
									.state(
											'app.images.create',
											{
												url : '/create',
												controller : 'ImageCreateCtrl',
												templateUrl : 'views/tmpl/images/image_create_template.html',
											})

									.state(
											'app.images.update',
											{
												reloadOnSearch : false,
												url : '/update/:id?tab',
												controller : 'ImageUpdateCtrl',
												templateUrl : 'views/tmpl/images/image_update_template.html',
											})
											
											
											
									// Payment
										
										.state(
											'app.payment',
											{
												url : '/payment',
												templateUrl : 'views/tmpl/payment/payment_template.html',

											})	
										.state(
											'app.payment.list',
											{
												url : '/list',
												controller : 'paymentListCtrl',
												templateUrl : 'views/tmpl/payment/payment_list.html',
											})
											
									// IP Restriction
											
											.state(
											'app.ipRestriction',
											{
												url : '/ipRestriction',
												templateUrl : 'views/tmpl/IPRestriction/IPRestriction_template.html',

											})	
										.state(
											'app.ipRestriction.list',
											{
												url : '/ipRestriction/list',
												controller : 'ipRestrictionListCtrl',
												templateUrl : 'views/tmpl/IPRestriction/ipRestriction_list.html',
											})


									// /// Type
									.state(
											'app.type',
											{
												url : '/type',
												controller : 'TypeCtrl',
												templateUrl : 'views/tmpl/images/image_template.html',
											})
									.state(
											'app.type.list',
											{
												url : '/type/list',
												controller : 'TypeCtrl',
												templateUrl : 'views/tmpl/images/type_list_template.html',
											})
									.state(
											'app.type.create',
											{
												url : '/type/create',
												controller : 'TypeCreateCtrl',
												templateUrl : 'views/tmpl/images/type_create_template.html',
											})

									.state(
											'app.type.update',
											{
												reloadOnSearch : false,
												url : '/type/update/:id',
												controller : 'TypeUpdateCtrl',
												templateUrl : 'views/tmpl/images/type_update_template.html',
											})
		

									.state(
											'app.settings',
											{
												url : '/setting',
												templateUrl : 'views/tmpl/setting/setting_template.html',

											})
									.state(
											'app.settings.user',
											{
												url : '/change-password',
												controller : 'UserUpdatePasswordCtrl',
												templateUrl : 'views/tmpl/setting/user_change_password_template.html',

											})
											
										.state(
											'app.settings.config',
											{
												url : '/configuration',
												controller : 'ConfigurationUpdateCtrl',
												templateUrl : 'views/tmpl/setting/configuration_update_template.html',

											})
											
										// marketings
										.state(
											'app.marketings',
											{
												url : '/marketings',
												templateUrl : 'views/tmpl/marketings/marketing_template.html',

											}) 
										.state( 
											'app.marketings.list', 
											{ 
												url : '/marketings/list', 
												controller : 'MarketingCtrl', 
												templateUrl : 'views/tmpl/marketings/marketing_report_list_template.html', 

											}) 
										.state( 
											'app.marketings.message', 
											{ 
												url : '/marketings/create', 
												controller : 'MarketingCreateCtrl', 
												templateUrl : 'views/tmpl/marketings/marketing_create_template.html', 

											}) 
										.state(
											'app.marketings.detail',
											{
												url : "/marketings/detail/:id",
												controller : 'MarketingReportDetailsCtrl', 
												templateUrl : "views/tmpl/marketings/marketing_report_details_template.html",
												data : { 
													pageTitle : 'Marketings Report Detail'
												}
											}) 
											.state(
											'app.marketings.update',  
											{
												url : 'marketings/update/:id',
												controller : 'MarketingReportUpdateCtrl',
												templateUrl : 'views/tmpl/marketings/marketing_report_update_template.html',
											})
											
											
											
								// player Record report
								.state(
											'app.record.report',
											{
												url : '/report/record',
												templateUrl : 'views/tmpl/playerRecordReport/record_report_template.html',

											})
											
								.state(
										'app.recordList',
											{
											url : '/record/list',
											controller : 'PlayerRecordReportCtrl',
											templateUrl : 'views/tmpl/playerRecordReport/record_report_list_template.html',

											})			
											
									// //////////Report///////////

									.state(
											'app.report',
											{
												url : '/report',
												controller : 'ReportCtrl',
												templateUrl : 'views/tmpl/report/report_template.html',
											})
									.state(
											'app.report.list',
											{
												url : '/list',
												controller : 'ReportCtrl',
												templateUrl : 'views/tmpl/report/report_list_template.html',
											})

									.state(
											'app.report.create',
											{
												url : '/create',
												controller : 'ReportCreateCtrl',
												templateUrl : 'views/tmpl/report/report_create_template.html',
											})
									.state(
											'app.report.detail',
											{
												url : "/detail/:id",
												templateUrl : "views/tmpl/report/report_detail_list_template.html",
												data : {
													pageTitle : 'Report Detail' 
												}
											}) 
									.state(
											'app.report.update',  
											{
												url : '/update/:id',
												controller : 'ReportUpdateCtrl',
												templateUrl : 'views/tmpl/report/report_update_template.html',
											})
											
											
											
											
									// ///// Membership Tab
									.state(
											'app.membership',
											{
												url : '/membership',
												controller : 'MembershipCtrl',
												templateUrl : 'views/tmpl/membership/membership_template.html',
											})
									.state(
											'app.membership.list',
											{
												url : '/list',
												controller : 'MembershipCtrl',
												templateUrl : 'views/tmpl/membership/membership_list_template.html',
											})
									.state(
											'app.membership.update',
											{
												url : '/update/:id',
												controller : 'MembershipUpdateCtrl',
												templateUrl : 'views/tmpl/membership/membership_update_template.html',
											})

									// ///// Category Tab
									.state(
											'app.category',
											{
												url : '/players',
												templateUrl : 'views/tmpl/player/player_template.html',
											})
									.state(
											'app.category.list',
											{
												url : '/categories/list',
												controller : 'CategoryCtrl',
												templateUrl : 'views/tmpl/player/category/category_list.html',
											})
									.state(
											'app.category.update',
											{
												url : '/category/update/:id',
												controller : 'CategoryUpdateCtrl',
												templateUrl : 'views/tmpl/player/category/category_update.html',
											})
									.state(
											'app.category.create',
											{
												url : '/categories/create',
												controller : 'categoryCreateCtrl',
												templateUrl : 'views/tmpl/player/category/category_create.html',
											})
											
											// chip conversion
											.state(
											'app.chipconversion',
											{
												url : '/chipconversion',
												templateUrl : 'views/tmpl/player/chipconversion/chip_conversion_template.html',

											})
									.state(
											'app.chipconversion.list',
											{ 
												url : '/chipconversion/list',
												controller : 'ChipConversionCtrl',
												templateUrl : 'views/tmpl/player/chipconversion/chip_conversion_list_template.html',
												params : {
												status : null
												}
											}) 
									.state(
											'app.chipconversion.create',
											{ 
												url : '/chipconversion/create',
												controller : 'ChipConversionCreateCtrl',
												templateUrl : 'views/tmpl/player/chipconversion/chip_conversion_create_template.html',

											}) 
									.state(
											'app.chipconversion.update',
											{ 
												url : '/chipconversion/update/:id',
												controller : 'ChipConversionUpdateCtrl',
												templateUrl : 'views/tmpl/player/chipconversion/chip_conversion_update_template.html',
											}) 
												
											// utilities//
											
											.state(
											'app.utilities',
											{
												url : '/gameHistory',
												templateUrl : 'views/tmpl/player/utilities/utilities_template.html',

											})
									.state(
											'app.utilities.list',
											{ 
												url : '/list',
												controller : 'utilitiesCtrl',
												templateUrl : 'views/tmpl/player/utilities/utilities_list_template.html',
											}) 
									.state(
											'app.utilities.update',
											{
												url : '/details/:id',
												controller : 'utilitiesDetailCtrl',
												templateUrl : 'views/tmpl/player/utilities/utilities_list_details.html',

											})
											
									// ///// Login validate //////
									.state(
											'app.loginValidate', 
											{ 
												url : '/chipconversion', 
												templateUrl : 'views/tmpl/player/chipconversion/chip_conversion_template.html',

											}) 
									.state( 
											'app.loginValidate.create', 
											{ 
												url : '/loginValidate/create', 
												controller : 'loginValidateCtrl', 
												templateUrl : 'views/tmpl/player/loginValidate/login_validate.html', 
											}) 
											
									.state(
											'app.loginValidate.list',
											{
												url : '/loginValidate/update',
												controller : 'loginValidateUpdateCtrl',
												templateUrl : 'views/tmpl/player/loginValidate/login_validate_update.html',

											})
									 .state(
											'app.loginValidate.update',
											{
												url : '/loginValidate/list',
												controller : 'loginValidateUpdateCtrl',
												templateUrl : 'views/tmpl/player/loginValidate/login_validate_list.html',
											})
											
									  // leader board configuration
											
									  .state(
											'app.leaderboard', 
											{ 
												url : '/player/leaderBoard', 
												templateUrl : 'views/tmpl/player/leaderBoard/leaderBoard_template.html',

											}) 
									.state( 
											'app.leaderboard.update', 
											
											{ 
												url : '/update/:id', 
												controller : 'leaderBoardUpdatectrl', 
												templateUrl : 'views/tmpl/player/leaderBoard/leaderBoard_update_template.html', 
											}) 
									.state( 
											'app.leaderboard.list', 
											{ 
												url : '/leaderboard/list', 
												controller : 'leaderBoardCtrl', 
												templateUrl : 'views/tmpl/player/leaderBoard/leaderBoard_list_template.html', 
											}) 
									.state( 
											'app.leaderboard.create', 
											{ 
												url : '/leaderboard/create', 
												controller : 'leaderBoardCreateCtrl', 
												templateUrl : 'views/tmpl/player/leaderBoard/leaderBoard_create_template.html', 
											}) 
									// ////////////Cricket/////////////
									.state(
											'app.cricket',
											{
												url : '/cricket',
												templateUrl : 'views/tmpl/cricket/cricket_template.html',
											}) 
									.state(
											'app.cricketContest',
											{
												url : '/contest',
												templateUrl : 'views/tmpl/cricket/cricketContest/cricketContest_template.html',
											})
									.state(
											'app.cricketContest.list',
											{
												url : '/list',
												controller : 'cricketContestCtrl',
												templateUrl : 'views/tmpl/cricket/cricketContest/cricketContest_list_template.html',
											})		
									.state(
											'app.cricketContest.create',
											{
												url : '/create',
												controller : 'cricketContestCreateCtrl',
												templateUrl : 'views/tmpl/cricket/cricketContest/cricketContest_create_template.html',

											}) 
									.state(
											'app.cricketBatting', 
											{
												url : '/contest',
												templateUrl : 'views/tmpl/cricket/cricketBatting/cricketBatting_template.html',
											})
									 .state(
											'app.cricketBatting.list', 
											{ 
												url : '/batting/list', 
												controller : 'cricketBattingCtrl', 
												templateUrl : 'views/tmpl/cricket/cricketBatting/cricketBatting_list_template.html', 
											})
									 .state(
											'app.cricketBatting.create',
											{ 
												url : 'batting/create',
												controller : 'cricketBattingCreateCtrl', 
												templateUrl : 'views/tmpl/cricket/cricketBatting/cricketBatting_create_template.html',
											}) 
											   
									// ////////////Player///////////// 
									.state(
											'app.players',
											{
												url : '/players',
												templateUrl : 'views/tmpl/player/player_template.html',
											})
									.state(
											'app.players.list',
											{
												url : '/player/list',
												controller : 'PlayerCtrl',
												templateUrl : 'views/tmpl/player/player_list.html',
											})
									.state(
											'app.players.details',
											{
												reloadOnSearch : false,
												url : '/player/details/:id?tab',
												controller : 'PlayerDetailsCtrl',
												templateUrl : 'views/tmpl/player/player_details_template.html',
											})	
											
											// player offer and Bonanza
											.state(
											'app.playerAchievement',
											{
												url : '/player/playerAchievement',
												templateUrl : 'views/tmpl/player/playerAchievement/playerAchievement_template.html',
											})
									.state(
											'app.playerAchievement.list',
											{
												url : '/list',
												controller : 'PlayerAchievementCtrl',
												templateUrl : 'views/tmpl/player/playerAchievement/playerAchievement_list_template.html',
												params : {
													status : null
												}

											})
									.state(
											'app.playerAchievement.create',
											{
												url : '/create',
												controller : 'PlayerAchievementCreateCtrl',
												templateUrl : 'views/tmpl/player/playerAchievement/playerAchievement_create_template.html',

											})
									.state(
											'app.playerAchievement.update',
											{
												url : '/update/:id',
												controller : 'PlayerAchievementUpdateCtrl',
												templateUrl : 'views/tmpl/player/playerAchievement/playerAchievement_update_template.html',

											})
											
								// ///Withdraw///////
							
									.state(
											'app.withdraw',
											{
												url : '/withdraw',
												controller : 'WithdrawCtrl',
												templateUrl : 'views/tmpl/withdraw/withdraw_template.html',
											})
									.state(
											'app.withdraw.list',
											{
												url : '/list',
												controller : 'WithdrawCtrl',
												templateUrl : 'views/tmpl/withdraw/withdraw_list_template.html',
											})
									.state(
											'app.withdraw.update',
											{
												url : '/update/:id',
												controller : 'WithdrawUpdateCtrl',
												templateUrl : 'views/tmpl/withdraw/withdraw_update_template.html',
											})		
										// polls
											.state(
											'app.poll',
											{
												url : '/elearning/poll',
												controller : 'ELearningCtrl',
												templateUrl : 'views/tmpl/eLearning/poll_template.html',
											})
									.state(
											'app.poll.list',
											{
												url : '/list',
												controller : 'PollCtrl',
												templateUrl : 'views/tmpl/eLearning/poll_list_template.html',
												params : {
													status : null
												}

											})
									.state(
											'app.poll.create',
											{
												url : '/create',
												controller : 'PollCreateCtrl',
												templateUrl : 'views/tmpl/eLearning/poll_create_template.html',

											})
									.state(
											'app.poll.update',
											{
												url : '/update/:id',
												controller : 'PollUpdateCtrl',
												templateUrl : 'views/tmpl/eLearning/poll_update_template.html',

											})

									// ///E Learning/////
									.state(
											'app.question',
											{
												url : '/elearning/question',
												controller : 'ELearningCtrl',
												templateUrl : 'views/tmpl/eLearning/elearning_template.html',
											})
									.state(
											'app.question.list',
											{
												url : '/list',
												controller : 'QuestionCtrl',
												templateUrl : 'views/tmpl/eLearning/question_list_template.html',
												params : {
													status : null
												}

											})
									.state(
											'app.question.create',
											{
												url : '/create',
												controller : 'QuestionCreateCtrl',
												templateUrl : 'views/tmpl/eLearning/question_create_template.html',

											})
									.state(
											'app.question.update',
											{
												url : '/update/:id',
												controller : 'QuestionUpdateCtrl',
												templateUrl : 'views/tmpl/eLearning/question_update_template.html',

											})
											
									// vote
											
									.state(
											'app.vote',
											{
												url : '/elearning/vote',
												controller : 'ELearningCtrl',
												templateUrl : 'views/tmpl/eLearning/elearning_template.html',
											})
									.state(
											'app.vote.list',
											{
												url : '/list',
												controller : 'VoteCtrl',
												templateUrl : 'views/tmpl/eLearning/vote_list_template.html',
												params : {
													status : null
												}

											})
									.state(
											'app.vote.create',
											{
												url : '/create',
												controller : 'VoteCreateCtrl',
												templateUrl : 'views/tmpl/eLearning/vote_create_template.html',

											})
									.state(
											'app.vote.update',
											{
												url : '/update/:id',
												controller : 'VoteUpdateCtrl',
												templateUrl : 'views/tmpl/eLearning/vote_update_template.html',

											})

											
									.state(
											'app.quiz',
											{
												url : '/elearning/quiz',
												controller : 'ELearningCtrl',
												templateUrl : 'views/tmpl/eLearning/elearning_template.html',
											})
									.state(
											'app.quiz.list',
											{
												url : '/list',
												controller : 'QuizCtrl',
												templateUrl : 'views/tmpl/eLearning/quiz_list_template.html',
												params : {
													status : null
												}

											})
									.state(
											'app.elearning',
											{
												url : '/elearning/',
												controller : 'ELearningCtrl',
												templateUrl : 'views/tmpl/eLearning/elearning_template.html',
											})
									.state(
											'app.elearning.dashboard',
											{
												url : 'dashboard',
												controller : 'ElearingDashboardHomeCtrl',
												templateUrl : 'views/tmpl/eLearning/elearning_dashboard_home_template.html',
											})
									.state(
											'app.price',
											{
												url : '/elearning/price',
												controller : 'ELearningCtrl',
												templateUrl : 'views/tmpl/eLearning/elearning_template.html',
											})
									.state(
									  	   'app.price.list',
											{
												url : '/list',
												controller : 'PriceCtrl', 
												templateUrl : 'views/tmpl/eLearning/price_list_template.html',
												params : {
													status : null
											}

											}) 
									.state(
										'app.price.create',
										{
											url : '/create',
											controller : 'PriceCreateCtrl',
											templateUrl : 'views/tmpl/eLearning/price_create_template.html',
										})
										
									.state(
											'app.price.update',
											{
												url : '/update/:id',
												controller : 'PriceUpdateCtrl',
												templateUrl : 'views/tmpl/eLearning/price_update_template.html',

											})

										// //Blogs
									.state(
											'app.blog',
											{
												url : '/blog',
												controller : 'BlogCtrl',
												templateUrl : 'views/tmpl/blog/blog_template.html',
											})
									.state(
											'app.blog.list',
											{
												url : '/list',
												controller : 'BlogCtrl',
												templateUrl : 'views/tmpl/blog/blog_list_template.html',
											})
											
											// for forum list display controller
											.state(
											'app.forum',
											{
												url : '/forum',
												controller : 'BlogCtrl',
												templateUrl : 'views/tmpl/blog/blog_template.html',
											})
											.state(
											'app.forum.list',
											{
												url : '/list',
												controller : 'ForumListCtrl',
												templateUrl : 'views/tmpl/blog/ForumList.html',
											})
											.state(
											'app.forum.create',
											{
												url : '/create',
												controller : 'ForumCreateTitleCtrl',
												templateUrl : 'views/tmpl/blog/ForumList_create_title.html',
											})
											.state(
											'app.forum.update',
											{
												url : '/update/:id',
												controller : 'ForumUpdateCtrl',
												templateUrl : 'views/tmpl/blog/Forum_update_template.html',

											})
											 // testimonial
											.state(
											'app.testimonial',
											{
												url : '/testimonial',
												controller : 'BlogCtrl',
												templateUrl : 'views/tmpl/blog/blog_template.html',
											})
											.state(
											'app.testimonial.list',
											{
												url : '/list',
												controller : 'testimonialListCtrl',
												templateUrl : 'views/tmpl/blog/testimonial_list_template.html',
											})
											.state(
											'app.testimonial.create',
											{
												url : '/create',
												controller : 'testimonialCreateCtrl',
												templateUrl : 'views/tmpl/blog/testimonial_create_template.html',
											})
											.state(
											'app.testimonial.update',
											{
												url : '/update/:id',
												controller : 'testimonialUpdateCtrl',
												templateUrl : 'views/tmpl/blog/testimonial_update_template.html',
											})
									.state(
											'app.blog.create',
											{
												url : '/create',
												controller : 'BlogCreateCtrl',
												templateUrl : 'views/tmpl/blog/blog_create_template.html',

											})
									.state(
											'app.blog.update',
											{
												url : '/update/:id',
												controller : 'BlogUpdateCtrl',
												templateUrl : 'views/tmpl/blog/blog_update_template.html',

											})
									.state(
											'app.topic',
											{
												url : '/blog/topic',
												controller : 'BlogCtrl',
												templateUrl : 'views/tmpl/blog/blog_template.html',
											})
									.state(
											'app.topic.list',
											{
												url : '/list',
												controller : 'TopicCtrl',
												templateUrl : 'views/tmpl/blog/topic_list_template.html',
												params : {
													status : null
												}

											})
									.state(
											'app.topic.create',
											{
												url : '/create',
												controller : 'TopicCreateCtrl',
												templateUrl : 'views/tmpl/blog/topic_create_template.html',

											})
									.state(
											'app.topic.update',
											{
												url : '/update/:id',
												controller : 'TopicUpdateCtrl',
												templateUrl : 'views/tmpl/blog/topic_update_template.html',

											})
									// //Home Blogs
									.state(
											'app.homeblog',
											{
												url : '/blog/home-links',
												controller : 'BlogCtrl',
												templateUrl : 'views/tmpl/blog/blog_template.html',
											})
									.state(
											'app.homeblog.list',
											{
												url : '/list',
												controller : 'HomeBlogCtrl',
												templateUrl : 'views/tmpl/blog/home_blog_list_template.html',
											})

						} ])

		.service('MCashService', function(CacheFactory) {
			return {
				setMycache : function(key, value) {
					var cache = '';
					if (CacheFactory.get('mobCache') == undefined) {
						cache = CacheFactory('mobCache', {
							maxAge : 915 * 60 * 1000, // Items added to this
							// cache
							deleteOnExpire : 'passive', // Items will be deleted
							storageMode : 'localStorage', // This cache will
						// use
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
							maxAge : 915 * 60 * 1000, // Items added to this
							// cache
							deleteOnExpire : 'passive', // Items will be deleted
							storageMode : 'localStorage', // This cache will
						// use
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
							maxAge : 915 * 60 * 1000, // Items added to this
														// cache
							deleteOnExpire : 'passive', // Items will be deleted
							storageMode : 'localStorage' // This cache will
															// use
						});
					} else {
						cache = CacheFactory.get('mobCache');
					}
					cache.remove(key);
				}
			}
		});
app.factory('httpErrorResponseInterceptor', [
		'$q',
		'$location',
		'$window',
		'$timeout',
		function($q, $location, $window, $timeout) {
			return {
				response : function(responseData) {
					if (typeof responseData.data == 'string'
							&& responseData.data.indexOf("|LOGIN") > -1) {
						// $timeout(function() {
						// $window.alert("You have been logged out");
						// });
						$window.location.reload();
					} else {
						return responseData;
					}
				}

			};
		} ]);

app.config(function(tagsInputConfigProvider) {
	tagsInputConfigProvider.setDefaults('tagsInput', {
		placeholder : ''
	});
	tagsInputConfigProvider.setActiveInterpolation('tagsInput', {
		placeholder : true
	});
});

app.filter('underscoreless', function() {
	return function(input) {
		return input.replace(/_/g, ' ');
	};
});

// Http Intercpetor to check auth failures for xhr requests
app.config([ '$httpProvider', function($httpProvider) {
	$httpProvider.interceptors.push('httpErrorResponseInterceptor');
} ]);

