package com.actolao.wsegmaes.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.slf4j.LoggerFactory;
import com.actolap.common.http.NingClient;
import com.actolap.wsegame.config.Config; 
import com.actolap.wsegame.model.Affiliate;
import com.actolap.wsegame.model.Blog;
import com.actolap.wsegame.model.Category;
import com.actolap.wsegame.model.ChipConversion;
import com.actolap.wsegame.model.Coupon;
import com.actolap.wsegame.model.Department;
import com.actolap.wsegame.model.Forum;
import com.actolap.wsegame.model.Image;
import com.actolap.wsegame.model.ImageType;
import com.actolap.wsegame.model.LeaderBoard;
import com.actolap.wsegame.model.LoginValidate;
import com.actolap.wsegame.model.MarketingReport;
import com.actolap.wsegame.model.Membership;
import com.actolap.wsegame.model.Permissions;
import com.actolap.wsegame.model.Player;
import com.actolap.wsegame.model.PlayerAchievement;
import com.actolap.wsegame.model.PlayerKycUpdateModel;
import com.actolap.wsegame.model.PlayerTdsCertificate;
import com.actolap.wsegame.model.PokerMasterConfiguration;
import com.actolap.wsegame.model.Price;
import com.actolap.wsegame.model.PromotionOther;
import com.actolap.wsegame.model.Poll;
import com.actolap.wsegame.model.Question;
import com.actolap.wsegame.model.Report;
import com.actolap.wsegame.model.ReportUiBeResponse;
import com.actolap.wsegame.model.RestrictedStateConfigRequest;
import com.actolap.wsegame.model.Role;
import com.actolap.wsegame.model.Table;
import com.actolap.wsegame.model.TableConfiguration;
import com.actolap.wsegame.model.Testimonial;
import com.actolap.wsegame.model.Topic;
import com.actolap.wsegame.model.Tournament;
import com.actolap.wsegame.model.TournamentConfiguration;
import com.actolap.wsegame.model.User;
import com.actolap.wsegame.model.UserPassword;
import com.actolap.wsegame.model.UtilitiesGameHistoryListModel;
import com.actolap.wsegame.model.Vote;
import com.actolap.wsegame.model.Withdraw;
import com.actolap.wsegame.reporting.request.ReportRequest;
import com.actolap.wsegame.reporting.response.ReportingResponse;
import com.actolap.wsegames.request.BankAccountUpdateRequest;
import com.actolap.wsegames.request.ConfirmPasswordRequest;
import com.actolap.wsegames.request.LoginRequest;
import com.actolap.wsegames.request.OTPRequest;
import com.actolap.wsegames.request.PaymentReportRequest;
import com.actolap.wsegames.request.PlayerReportRequest;
import com.actolap.wsegames.request.UpdateRequest;
import com.actolap.wsegames.response.AchievementPlayersConfigResponse;
import com.actolap.wsegames.response.AchievementPlayersListResponse;
import com.actolap.wsegames.response.AffiliateConfigResponse;
import com.actolap.wsegames.response.AffiliateListResponse;
import com.actolap.wsegames.response.AffiliatePlayerListResponse;
import com.actolap.wsegames.response.AffiliateReferralJoinResponse;
import com.actolap.wsegames.response.BlogConfigResponse;
import com.actolap.wsegames.response.BlogListResponse;
import com.actolap.wsegames.response.CategoryListResponse;
import com.actolap.wsegames.response.CategoryMaterConfigListResponse;
import com.actolap.wsegames.response.ChipConversionListResponse;
import com.actolap.wsegames.response.CouponConfigResponse;
import com.actolap.wsegames.response.CouponListResponse;
import com.actolap.wsegames.response.DashboardLiveWidgetsResponse;
import com.actolap.wsegames.response.DashboardTournamentWidgetsResponse;
import com.actolap.wsegames.response.DepartmentListResponse;
import com.actolap.wsegames.response.ElearningConfigResponse;
import com.actolap.wsegames.response.ElearningQuizResponse;
import com.actolap.wsegames.response.ForgotPasswordResponse;
import com.actolap.wsegames.response.ForumListResponse;
import com.actolap.wsegames.response.GenericBackendResponse;
import com.actolap.wsegames.response.ImageConfigResponse;
import com.actolap.wsegames.response.ImageTypeListResponse;
import com.actolap.wsegames.response.ImagesListResponse;
import com.actolap.wsegames.response.LeaderBoardConfigResponse;
import com.actolap.wsegames.response.LeaderBoardListResponse;
import com.actolap.wsegames.response.LoginResponse;
import com.actolap.wsegames.response.LoginValidateResponse;
import com.actolap.wsegames.response.MarketingReportConfigResponse;
import com.actolap.wsegames.response.MarketingReportListResponse;
import com.actolap.wsegames.response.MarketingReportResponse;
import com.actolap.wsegames.response.MembershipListResponse;
import com.actolap.wsegames.response.OTPResponse;
import com.actolap.wsegames.response.PaymentTransactionConfig;
import com.actolap.wsegames.response.PaymentTransactionListResponse;
import com.actolap.wsegames.response.PermissionsListResponse;
import com.actolap.wsegames.response.PlayerAchievementConfigResponse;
import com.actolap.wsegames.response.PlayerAchievementListResponse;
import com.actolap.wsegames.response.PlayerBonusChipsListResponse;
import com.actolap.wsegames.response.PlayerBonusExpiredChipsListResponse;
import com.actolap.wsegames.response.PlayerElearningResponse;
import com.actolap.wsegames.response.PlayerListResponse;
import com.actolap.wsegames.response.PlayerTdsCertificateListResponse;
import com.actolap.wsegames.response.PlayerTransactionResponse;
import com.actolap.wsegames.response.PokerMasterConfigResponse;
import com.actolap.wsegames.response.PriceListResponse;
import com.actolap.wsegames.response.PromotionConfigResponse;
import com.actolap.wsegames.response.PromotionListResponse;
import com.actolap.wsegames.response.PollListResponse;
import com.actolap.wsegames.response.QuestionListResponse;
import com.actolap.wsegames.response.ReferralCodeListResponse;
import com.actolap.wsegames.response.ReportConfigResponse;
import com.actolap.wsegames.response.ReportListResponse;
import com.actolap.wsegames.response.RestrictedStateResponse;
import com.actolap.wsegames.response.RoleListResponse;
import com.actolap.wsegames.response.SearchResponse;
import com.actolap.wsegames.response.SearchTableConfigResponse;
import com.actolap.wsegames.response.TableConfigResponse;
import com.actolap.wsegames.response.TableConfigurationListResponse;
import com.actolap.wsegames.response.TableListResponse;
import com.actolap.wsegames.response.TestimonialListResponse;
import com.actolap.wsegames.response.TopicListResponse;
import com.actolap.wsegames.response.TournamentConfigResponse;
import com.actolap.wsegames.response.TournamentConfigurationListResponse;
import com.actolap.wsegames.response.TournamentListResponse;
import com.actolap.wsegames.response.TournamentPlayerListResponse;
import com.actolap.wsegames.response.UserResponse;
import com.actolap.wsegames.response.UtilitiesListResponse;
import com.actolap.wsegames.response.VoteListResponse;
import com.actolap.wsegames.response.WithdrawConfigResponse;
import com.actolap.wsegames.response.WithdrawListResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApiManager {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ApiManager.class);

	public static String apiBase = Config.apiBaseUrl; 
	private static String loginUrl = "/user/login";
	private static String forgotPasswordUrl = "/user/forgot_password";
	private static String resetPasswordUrl = "/user/reset_password";
	private static String confirmPasswordUrl = "/user/confirm_password";
	// Department Urls
	private static String departmentCreateUrl = "/department/create";
	private static String departmentListUrl = "/department/list";
	private static String getDepartmentUrl = "/department/get";
	private static String departmentUpdateUrl = "/department/update";
	private static String departmentSearchListUrl = "/department/search/list";
	// User Urls
	private static String userListUrl = "/user/list";
	private static String userCreateUrl = "/user/create";
	private static String getUserUrl = "/user/get";
	private static String userUpdateUrl = "/user/update";
	private static String userPasswordUpdateUrl = "/user/update/password";
	private static String roleSearchListUrl = "/department/roles";
	// Role Urls
	private static String roleListUrl = "/role/list";
	private static String roleCreateUrl = "/role/create";
	private static String roleGetUrl = "/role/get";
	private static String roleUpdateUrl = "/role/update";
	private static String roleDeleteUrl = "/role/delete";
	private static String roleAddPermissionsUrl = "/permission/add/role";
	private static String removeRolePermissionsUrl = "/permission/remove/rolePermission";
	// Permission Urls
	private static String permissionsListUrl = "/permission/list";
	private static String permissionsCreateUrl = "/permission/create";
	private static String permissionsGetUrl = "/permission/get";
	private static String permissionsUpdateUrl = "/permission/update";
	private static String permissionsDeleteUrl = "/permission/delete";
	private static String permissionsAddRoleUrl = "/role/add/permissions";
	private static String createLoginValidateUrl = "/permission/validateCreate";
	//private static String createLeaderBoardUrl = "/permission/leaderBoardCreate";
	//private static String getLeaderBoardUrl = "/permission/getLeaderBoard";
	//private static String leaderBoardUpdateUrl = "/permission/updateLeaderBoard";
	private static String getLoginValidateUrl = "/permission/getList";
	private static String loginValidateUpdateUrl = "/permission/updateLoginValidate";
	
	

	// Image Urls
	private static String imageConfigUrl = "/image/config";
	private static String imageEntityUrl = "/image/entity";
	private static String imageListUrl = "/image/list";
	private static String imageCreateUrl = "/image/create";
	private static String imageGetUrl = "/image/get";
	private static String imageUpdateUrl = "/image/update";
	private static String imageDeleteUrl = "/image/delete";

	// Image Type Urls
	private static String imageTypeListUrl = "/image/type/list";
	private static String imageTypeCreateUrl = "/image/type/create";
	private static String imageTypeGetUrl = "/image/type/get";
	// private static String imageTypeUpdateUrl = "/image/type/update";
	// private static String imageTypeDeleteUrl = "/image/type/delete";

	// Report Urls
	private static String reportListUrl = "/report/list";
	private static String reportConfigUrl = "/report/config";
	private static String reportCreateUrl = "/report/create";
	private static String reportSearchListUrl = "/report/filter/search/list";
	private static String reportUpdateUrl = "/report/update";
	private static String reportGetUrl = "/report/get";
	private static String reportOtpUrl = "/report/send/user/otp";
	private static String reportValidateOtpUrl = "/report/otp/validate";
	private static String reportDeleteUrl = "/report/delete";
	private static String reportDetailListUrl = "/report/detail";

	// Membership Urls
	private static String membershipListUrl = "/membership/list";
	private static String getMembershipUrl = "/membership/get";
	private static String membershipUpdateUrl = "/membership/update";

	// Category Urls
	private static String categoryListUrl = "/category/list";
	private static String getCategoryUrl = "/category/get";
	private static String categoryUpdateUrl = "/category/update";
	private static String categoryMasterConfigUpdateUrl = "/category/master/update";
	private static String getCategoryMasterConfigUrl = "/category/master/config";
	private static String categoryCreateUrl = "/category/create";
	
	//PaymentTransaction PayU
	private static String playerTransactionPayUListUrl = "/playerPaymentUrl/list";
	private static String paymentTransactionConfigUrl = "/playerPaymentUrl/config";
	private static String paymentTransactionReportConfigUrl = "/playerPaymentUrl/csv/report";
	private static String paymentExportDataFeildsUrl = "/playerPaymentUrl/report/detail";

	// Player Urls
	private static String palyerConfigUrl = "/player/csv/report/config";
	private static String playerListUrl = "/player/list";
	private static String getPlayerDetailsUrl = "/player/get";
	private static String playerUpdateDocumentUrl = "/player/update/document/status";
	private static String playerBankAccountUpdateUrl = "/player/update/bankAccount/status";
	private static String playerBonusChipsListUrl = "/player/bonus";
	private static String playerBonusExpiredChipsListUrl = "/player/expired/bonus";
	private static String playerTdsCertificateListUrl = "/player/tds/certificates";
	private static String uploadTdsCertificateUrl = "/player/tds/certificate/upload";
	private static String playerTdsCertificateDeleteUrl = "/player/tds/certificate/delete";
	private static String playerElearningQuizListUrl = "/player/quiz/detail";
	private static String playerExportDataFeildsUrl = "/player/report/detail";
	private static String playerSearchListUrl = "/player/search/list";
	private static String playerTransactionListUrl = "/player/transaction/detail";
	private static String playerBanUrl = "/player/ban";
	private static String playerKycUpdateUrl = "/player/kyc/update";
	private static String playerJoinRequest = "/player/join/affiliate";
	private static String playerAffiliateGet = "/player/affiliate/get";
	private static String playerUpdateDocument = "/player/updateDocument";
	
	//ip restriction
	private static String saveIPrestrictionUrl = "/ipRestriction/saveRestrictedIP";
	private static String deleteIPrestrictionUrl = "/ipRestriction/deleteRestrictedIP";
	private static String getRestrictedStateUrl = "/ipRestriction/getState";
	private static String updateRestrictedStateUrl = "/ipRestriction/updateState";
	
	// GameHistory utilities List Url
	
	private static String utilitiesGameIdListUrl = "/utilities/getGameDetails";
	private static String utilitiesGameHistoryIdListUrl = "/utilities/getGameHistoryById";

	// Poker Table Urls
	private static String tableListUrl = "/table/list";
	private static String tableCreateUrl = "/table/create";
	private static String getTableUrl = "/table/get";
	private static String tableUpdateUrl = "/table/update";
	private static String tableConfigUrl = "/table/config";

	// Poker Table Configuration Urls
	private static String tableConfigListUrl = "/table/config/list";
	private static String tableConfigCreateUrl = "/table/config/create";
	private static String getTableConfigUrl = "/table/config/get";
	private static String tableConfigUpdateUrl = "/table/config/update";
	private static String tableConfigSearchListUrl = "/table/config/search/list";
	private static String pokerMasterConfigUpdateUrl = "/poker/config/update";
	private static String getPokerMasterConfigUrl = "/poker/config/get";
	private static String pokerMasterConfigUrl = "/poker/config/config";

	// Poker Tournament Urls
	private static String tournamentListUrl = "/tournament/list";
	private static String tournamentCreateUrl = "/tournament/create";
	private static String getTournamentUrl = "/tournament/get";
	private static String tournamentUpdateUrl = "/tournament/update";
	private static String tournamentConfigUrl = "/tournament/config";
	private static String tournamentPlayerListUrl = "/tournament/players";

	// Poker Tournament Configuration Urls
	private static String tournamentConfigListUrl = "/tournament/config/list";
	private static String tournamentConfigCreateUrl = "/tournament/config/create";
	private static String getTournamentConfigUrl = "/tournament/config/get";
	private static String tournamentConfigUpdateUrl = "/tournament/config/update";
	private static String tournamentConfigSearchListUrl = "/tournament/config/search/list";
	// Coupon Urls
	private static String couponListUrl = "/coupon/list";
	private static String couponCreateUrl = "/coupon/create";
	private static String getCouponUrl = "/coupon/get";
	private static String couponUpdateUrl = "/coupon/update";
	private static String couponDeleteUrl = "/coupon/delete";
	private static String couponConfigUrl = "/coupon/config";
	
	
	// Promotion Urls
	private static String promotionListUrl = "/other/promotion/list";
	private static String promotionCreateUrl = "/other/promotion/create";
	private static String getPromotionUrl = "/other/promotion/get";
	private static String promotionUpdateUrl = "/other/promotion/update";
	private static String promotionDeleteUrl = "/other/promotion/delete";
	private static String promotionConfigUrl = "/other/promotion/config";
	
	//chipsConversion Urls
	private static String chipsConversionCreateUrl = "/chipsconversion/create";
	private static String chipConversionListUrl = "/chipsconversion/list";
	private static String chipConversionDeleteUrl = "/chipsconversion/delete";
	private static String getChipConversionUrl = "/chipsconversion/get";
	private static String chipConversionUpdateUrl = "/chipsconversion/update";

	// Affiliate Urls
	private static String affiliateListUrl = "/affiliate/list";
	private static String affiliateCreateUrl = "/affiliate/create";
	private static String getAffiliateUrl = "/affiliate/get";
	private static String affiliateUpdateUrl = "/affiliate/update";
	private static String affiliateDeleteUrl = "/affiliate/delete";
	private static String affiliateConfigUrl = "/affiliate/config";
	private static String affiliatePlayersListUrl = "/affiliate/players";
	private static String affiliatePlayersAction = "/affiliate/player/approve";

	// Affiliate Urls
	private static String referralListUrl = "/affiliate/referral/code/list";
	private static String referralCreateUrl = "/affiliate/referral/code/create";
	private static String referralDeleteUrl = "/affiliate/referral/code/delete";

	// Dashboard Urls
	private static String dashbaordSummaryUrl = "/dashboard/summary";
	private static String dashbaordLiveWidgetUrl = "/dashboard/widgets";
	private static String dashbaordTournamentWidgetUrl = "/dashboard/tournament/widgets";

	// private static String getBankByIfscUrl = "https://ifsc.razorpay.com/";

	// Question Urls
	private static String questionListUrl = "/question/list";
	private static String questionCreateUrl = "/question/create";
	private static String questionGetUrl = "/question/get";
	private static String questionUpdateUrl = "/question/update";
	private static String questionDeleteUrl = "/question/delete";
	
	//vote
	
	private static String voteListUrl = "/vote/list"; 
	private static String voteCreateUrl = "/vote/create";
	private static String voteGetUrl = "/vote/get";
	private static String voteUpdateUrl = "/vote/update";
	private static String voteDeleteUrl = "/vote/delete";
	private static String voteQuestionUrl = "/vote/question";
	
	//price URLs
	private static String priceListUrl = "/price/pricelist";
	private static String priceCreateUrl = "/price/createprice";
	private static String priceGetUrl = "/price/get";
	private static String priceUpdateUrl = "/price/update";
	private static String priceDeleteUrl = "/price/delete";
	
	//POLL
	private static String pollCreateUrl = "/poll/create";
	private static String pollListUrl = "/poll/list";
	private static String pollGetUrl = "/poll/get";
	private static String pollUpdateUrl = "/poll/update";
	private static String pollDeleteUrl = "/poll/delete";

	// Elearning Urls
	private static String elearningDashbaordSummaryUrl = "/e-learning/dashboard/summary";
	private static String elearningConfigUrl = "/quiz/config";
	private static String elearningQuizListUrl = "/quiz/list";
	
	//player Achievement
	private static String playerAchievementConfigUrl = "/playerAchievement/config";
	private static String achievementPlayersConfigUrl = "/playerAchievement/players/config";
	private static String playerAchievementCreateUrl = "/playerAchievement/create";
	private static String playerAchievementListUrl = "/playerAchievement/list";
	private static String playerAchievementGetUrl = "/playerAchievement/get";
	private static String playerAchievementUpdateUrl = "/playerAchievement/update";
	private static String achievementPlayersListUrl = "/playerAchievement/players";
	
	//leaderboard
	private static String leaderBoardListUrl = "/leaderBoard/list";
	private static String leaderBoardCreateUrl = "/leaderBoard/create";
	private static String leaderBoardGetUrl = "/leaderBoard/get";
	private static String leaderBoardUpdateUrl = "/leaderBoard/update";
	private static String leaderBoardDeleteUrl = "/leaderBoard/delete";
	private static String leaderBoardConfigUrl = "/leaderBoard/config";

	// Blog Urls 
	private static String blogListUrl = "/blog/list";
	private static String forumListUrl = "/blog/forumlist";
	private static String blogCreateUrl = "/blog/create";
	private static String forumCreateUrl = "/blog/forumlistcreate";
	private static String blogGetUrl = "/blog/get";
	private static String forumGetUrl = "/blog/getforum";
	private static String blogUpdateUrl = "/blog/update";
	private static String forumUpdateUrl = "/blog/forumupdate";
	private static String blogDeleteUrl = "/blog/delete";
	private static String forumDeleteUrl = "/blog/forumdelete";
	private static String blogConfigUrl = "/blog/config";
	private static String homeBlogSearchListUrl = "/home/blog/search/blogs";
	private static String homeBlogListUrl = "/home/blog/add";
	private static String homeBlogDeleteUrl = "/home/blog/delete";
	private static String changeHomeBlogMoveUpUrl = "/home/blog/move/up";
	private static String changeHomeBlogMoveDownUrl = "/home/blog/move/down";
	

	// Topic Urls
	private static String topicListUrl = "/topic/list";
	private static String topicCreateUrl = "/topic/create";
	private static String topicGetUrl = "/topic/get";
	private static String topicUpdateUrl = "/topic/update";
	private static String topicDeleteUrl = "/topic/delete";
	private static String topicSearchListUrl = "/topic/search/list";
	
	// Testimonial Url's
	private static String testimonialCreateUrl = "/testimonial/create";
	private static String testimonialListUrl = "/testimonial/list";
	private static String testimonialGetUrl = "/testimonial/get";
	private static String testimonialUpdateUrl = "/testimonial/update";

	// Withdraw Urls
	private static String withdrawListUrl = "/withdraw/request/list";
	private static String withdrawGetUrl = "/withdraw/request/get";
	private static String withdrawUpdateUrl = "/withdraw/request/update";
	private static String withdrawConfigUrl = "/withdraw/request/config";
	
	//marketing 
	private static String marketingReportCreateUrl = "/marketing/report/create"; 
	private static String marketingReportDetailListUrl = "/marketing/get/details"; 
	private static String marketingReportConfigUrl = "/marketing/get/marketingConfig";  
	private static String marketingReportDownloadCsvExport = "/marketing/export/csvFile"; 
	private static String marketingReportListUrl = "/marketing/list"; 
	private static String marketingReportDeleteUrl = "/marketing/report/delete"; 
	private static String marketingReportGetUrl = "/marketing/get/report"; 
	private static String marketingReportOtpUrl = "/marketing/get/otp"; 
	private static String marketingReportValidateOtpUrl = "/marketing/validate/otp";
	private static String marketingReportUpdateUrl = "/marketing/report/update"; 
	private static String marketingReportSearchListUrl = "/marketing/search/list";  
	//private static Gson gson = new Gson();
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
	

	public static LoginResponse login(String username, String password, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		LoginRequest loginRequest = new LoginRequest(username, password);
		String response = NingClient.getInstance().postData(apiBase + loginUrl, gson.toJson(loginRequest), apiMeta);
		return gson.fromJson(response, LoginResponse.class);
	}

	public static ForgotPasswordResponse forgotPassword(String email) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + forgotPasswordUrl + "?email=" + email, null);
		return gson.fromJson(response, ForgotPasswordResponse.class);
	}

	public static GenericBackendResponse resetPassword(String tokenId) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + resetPasswordUrl + "?tokenId=" + tokenId, null);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse confirmPassword(String password, String confirmPassword, String passwordToken) throws InterruptedException, ExecutionException,
			IOException {
		ConfirmPasswordRequest confirmPasswordRequest = new ConfirmPasswordRequest(password, confirmPassword, passwordToken);
		String response = NingClient.getInstance().postData(apiBase + confirmPasswordUrl, gson.toJson(confirmPasswordRequest), null);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	// Department Apis
	public static GenericBackendResponse departmentCreate(Department department, ApiMeta user) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + departmentCreateUrl, gson.toJson(department), user);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static DepartmentListResponse getDepartmentList(ApiMeta apiMeta, String query, String status) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(
				apiBase + departmentListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status=" + URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, DepartmentListResponse.class);
	}

	public static GenericBackendResponse departmentUpdate(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + departmentUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static Department getDepartmentById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getDepartmentUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Department.class);
	}

	public static SearchResponse getDepartmentSearchList(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + departmentSearchListUrl, apiMeta);
		return gson.fromJson(response, SearchResponse.class);
	}

	// User Apis
	public static UserResponse getUserList(String id, ApiMeta apiMeta, String query, String status) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + userListUrl + "?departmentId=" + id + "&query=" + URLEncoder.encode(query, "UTF-8") + "&status=" + status,
				apiMeta);
		return gson.fromJson(response, UserResponse.class);
	}

	public static GenericBackendResponse userCreate(User user, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + userCreateUrl, gson.toJson(user), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static User getUser(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getUserUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, User.class);
	}

	public static GenericBackendResponse userUpdate(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + userUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse userPasswordUpdate(UserPassword userPassword, String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + userPasswordUpdateUrl + "?id=" + id, gson.toJson(userPassword), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static SearchResponse getDepartmentRoleSearchList(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + roleSearchListUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, SearchResponse.class);
	}

	// Role Apis
	public static RoleListResponse getRoleList(ApiMeta apiMeta, String query) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + roleListUrl + "?query=" + URLEncoder.encode(query, "UTF-8"), apiMeta);
		return gson.fromJson(response, RoleListResponse.class);
	}

	public static GenericBackendResponse createRole(String title, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + roleCreateUrl + "?title=" + URLEncoder.encode(title, "UTF-8"), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static Role getRoleById(String id, ApiMeta apiMeta, String query, String group) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + roleGetUrl + "?id=" + id + "&query=" + URLEncoder.encode(query, "UTF-8") + "&group=" + URLEncoder.encode(group, "UTF-8"));
		String response = NingClient.getInstance().loadURl(
				apiBase + roleGetUrl + "?id=" + id + "&query=" + URLEncoder.encode(query, "UTF-8") + "&group=" + URLEncoder.encode(group, "UTF-8"), apiMeta);
		return gson.fromJson(response, Role.class);
	}

	public static GenericBackendResponse updateRoleById(String id, String title, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + roleUpdateUrl + "?title=" + URLEncoder.encode(title, "UTF-8") + "&id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse getRoleDelete(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + roleDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse addPermissions(String roleId, String permissionsIds, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + permissionsAddRoleUrl + "?roleId=" + roleId + "&permissionIds=" + permissionsIds);
		String response = NingClient.getInstance().loadURl(apiBase + permissionsAddRoleUrl + "?roleId=" + roleId + "&permissionIds=" + permissionsIds, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse removeRolePermissoin(String roleId, String permissionId, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + removeRolePermissionsUrl + "?roleId=" + roleId + "&permissionId=" + permissionId);
		String response = NingClient.getInstance().loadURl(apiBase + removeRolePermissionsUrl + "?roleId=" + roleId + "&permissionId=" + permissionId, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static GenericBackendResponse createLoginValidate(LoginValidate loginValidate, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + createLoginValidateUrl, gson.toJson(loginValidate), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	
	//leaderboard
	
	public static LeaderBoardListResponse getLeaderBoardList(ApiMeta apiMeta, String level, String status, String type, String query) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + leaderBoardListUrl + "?query=" + query +  "&type=" + URLEncoder.encode(type, "UTF-8") + "&status="
						+ URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, LeaderBoardListResponse.class);
	}
	public static GenericBackendResponse createLeaderBoard(LeaderBoard leaderBoard, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + leaderBoardCreateUrl + gson.toJson(leaderBoard));
		String response = NingClient.getInstance().postData(apiBase + leaderBoardCreateUrl, gson.toJson(leaderBoard), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	public static LeaderBoard getLeaderBoardById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + leaderBoardGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + leaderBoardGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, LeaderBoard.class);
	}
	
	public static GenericBackendResponse updateLeaderBoardById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + leaderBoardUpdateUrl + "?id=" + id);
		String response = NingClient.getInstance().postData(apiBase + leaderBoardUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static GenericBackendResponse leaderBoardDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + leaderBoardDeleteUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + leaderBoardDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static LeaderBoardConfigResponse getLeaderBoardConfigResponse(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + leaderBoardConfigUrl, apiMeta);
		return gson.fromJson(response, LeaderBoardConfigResponse.class);
	}
	
	public static LoginValidateResponse getValidateMobileList(String query, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + getLoginValidateUrl + "?query=" + query);
		String response = NingClient.getInstance().loadURl(apiBase + getLoginValidateUrl + "?query=" + query, apiMeta);
		return gson.fromJson(response, LoginValidateResponse.class);
	}
	
	public static GenericBackendResponse updateLoginValidateById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + loginValidateUpdateUrl + "?id=" + id);
		String response = NingClient.getInstance().postData(apiBase + loginValidateUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	// Images Apis 
	public static ImageConfigResponse getImageConfigResponse(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + imageConfigUrl);
		String response = NingClient.getInstance().loadURl(apiBase + imageConfigUrl, apiMeta);
		return gson.fromJson(response, ImageConfigResponse.class);
	}

	public static SearchResponse getImageEntityResponse(ApiMeta apiMeta, String type, String status) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + imageEntityUrl + "?type=" + type + "&status=" + status, apiMeta);
		return gson.fromJson(response, SearchResponse.class);
	}

	public static ImagesListResponse getImages(ApiMeta apiMeta, int limit, int skip, String sort, boolean sortOrder, String type, String entityType, String status)
			throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(
				apiBase + imageListUrl + "?limit=" + limit + "&skip=" + skip + "&sort=" + sort + "&sortOrder=" + sortOrder + "&type=" + type + "&entityType=" + entityType
						+ "&status=" + status, apiMeta);
		return gson.fromJson(response, ImagesListResponse.class);
	}

	public static GenericBackendResponse createImages(Image image, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + imageCreateUrl, gson.toJson(image), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static Image getImageById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + imageGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + imageGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Image.class);
	}

	public static GenericBackendResponse getImageDelete(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + imageDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static ImageType getImageTypeById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + imageTypeGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + imageTypeGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, ImageType.class);
	}

	public static GenericBackendResponse updateImageById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + blogUpdateUrl + "?id=" + id + gson.toJson(requestData));
		String response = NingClient.getInstance().postData(apiBase + imageUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse deleteImage(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + imageDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	// ImageType Apis
	public static ImageTypeListResponse getImageTypes(ApiMeta apiMeta, int limit, int skip, String sort, boolean sortOrder, String query) throws InterruptedException,
			ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(
				apiBase + imageTypeListUrl + "?limit=" + limit + "&skip=" + skip + "&sort=" + sort + "&sortOrder=" + sortOrder + "&query=" + URLEncoder.encode(query, "UTF-8"),
				apiMeta);
		return gson.fromJson(response, ImageTypeListResponse.class);
	}

	public static GenericBackendResponse createImageType(ImageType imageType, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + imageTypeCreateUrl, gson.toJson(imageType), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static ImageType getImageTypeById(String id, ApiMeta apiMeta, String query) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + permissionsGetUrl + "?id=" + id + "&query=" + query);
		String response = NingClient.getInstance().loadURl(apiBase + imageTypeGetUrl + "?id=" + id + "&query=" + query, apiMeta);
		return gson.fromJson(response, ImageType.class);
	}

	// public static GenericBackendResponse updateImageType(String id, ImageType
	// imageType, ApiMeta apiMeta)
	// throws InterruptedException, ExecutionException, IOException {
	// String response = NingClient.getInstance().postData(apiBase +
	// imageTypeUpdateUrl + "?id=" + id,
	// gson.toJson(imageType), apiMeta);
	// return gson.fromJson(response, GenericBackendResponse.class);
	// }

	// public static GenericBackendResponse deleteImageType(String id, ApiMeta
	// apiMeta)
	// throws InterruptedException, ExecutionException, IOException {
	// String response = NingClient.getInstance().loadURl(apiBase +
	// imageTypeDeleteUrl + "?id=" + id, apiMeta);
	// return gson.fromJson(response, GenericBackendResponse.class);
	// }

	// Permission Apis
	
	public static PermissionsListResponse getPermissionList(ApiMeta apiMeta, String query, String group) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(
				apiBase + permissionsListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&group=" + URLEncoder.encode(group, "UTF-8"), apiMeta);
		return gson.fromJson(response, PermissionsListResponse.class);
	}

	public static GenericBackendResponse createPermissions(Permissions permission, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + permissionsCreateUrl, gson.toJson(permission), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static Permissions getPermissionsById(String id, ApiMeta apiMeta, String query) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + permissionsGetUrl + "?id=" + id + "&query=" + query);
		String response = NingClient.getInstance().loadURl(apiBase + permissionsGetUrl + "?id=" + id + "&query=" + query, apiMeta);
		return gson.fromJson(response, Permissions.class);
	}

	public static GenericBackendResponse updatePermissionsById(Permissions permissions, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + permissionsUpdateUrl, gson.toJson(permissions), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse getPermissionDelete(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + permissionsDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse addRole(String id, String roleIds, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String tempUrl = apiBase + roleAddPermissionsUrl + "?id=" + id + "&roleIds=" + roleIds;
		logger.info(tempUrl);
		String response = NingClient.getInstance().loadURl(apiBase + roleAddPermissionsUrl + "?id=" + id + "&roleIds=" + roleIds, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	// Report Apis
	public static ReportConfigResponse getReportConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + reportConfigUrl, apiMeta);
		return gson.fromJson(response, ReportConfigResponse.class);
	}

	public static GenericBackendResponse reportCreate(Report report, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + reportCreateUrl, gson.toJson(report), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	//marketings 
	public static GenericBackendResponse marketingReportCreate(MarketingReport marketingReport, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + marketingReportCreateUrl, gson.toJson(marketingReport), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);  
	} 
	
	public static MarketingReportResponse getMarketingReportDetailList(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + marketingReportDetailListUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, MarketingReportResponse.class); 
	}
	public static MarketingReportConfigResponse getMarketingReportConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + marketingReportConfigUrl); 
		String response = NingClient.getInstance().loadURl(apiBase + marketingReportConfigUrl, apiMeta); 
		return gson.fromJson(response, MarketingReportConfigResponse.class);  
	} 
	
	public static MarketingReportResponse getMarketingReportExportCsv(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + marketingReportDownloadCsvExport + "?id=" + id, apiMeta);
		return gson.fromJson(response, MarketingReportResponse.class); 
	}
	
	public static MarketingReportListResponse getMarketingReportList(ApiMeta apiMeta, String query) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + marketingReportListUrl + "?query=" + URLEncoder.encode(query, "UTF-8"), apiMeta);
		return gson.fromJson(response, MarketingReportListResponse.class); 
	} 
	
	public static GenericBackendResponse marketingReportDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + marketingReportDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	} 
	
	public static MarketingReport getMarketingReport(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + marketingReportGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, MarketingReport.class);
	}
	
	public static OTPResponse sendMarketingReportdOtp(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + marketingReportOtpUrl, apiMeta);
		String response = NingClient.getInstance().loadURl(apiBase + marketingReportOtpUrl, apiMeta);
		return gson.fromJson(response, OTPResponse.class);
	}
	
	public static GenericBackendResponse marketingReportOtpValidate(OTPRequest otpRequest, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + marketingReportValidateOtpUrl, gson.toJson(otpRequest), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static GenericBackendResponse marketingReportUpdate(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + marketingReportUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	} 
	public static SearchResponse getMarketingReportSearchList(String query, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + marketingReportSearchListUrl + "?query=" + URLEncoder.encode(query, "UTF-8"));
		String response = NingClient.getInstance().loadURl(apiBase + marketingReportSearchListUrl + "?query=" + URLEncoder.encode(query, "UTF-8"), apiMeta);
		return gson.fromJson(response, SearchResponse.class);
	} 
	// end marketing report api //
	public static SearchResponse getReportSearchList(String url, String query, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + reportSearchListUrl + "?url=" + url + "&query=" + URLEncoder.encode(query, "UTF-8"));
		String response = NingClient.getInstance().loadURl(apiBase + reportSearchListUrl + "?url=" + url + "&query=" + URLEncoder.encode(query, "UTF-8"), apiMeta);
		return gson.fromJson(response, SearchResponse.class);
	} 
	  
	public static GenericBackendResponse reportUpdate(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + reportUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	 
	public static ReportListResponse getReportList(ApiMeta apiMeta, String query) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + reportListUrl + "?query=" + URLEncoder.encode(query, "UTF-8"), apiMeta);
		return gson.fromJson(response, ReportListResponse.class);
	}
    
	public static Report getReport(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + reportGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Report.class);
	}

	public static OTPResponse sendReportdOtp(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + reportOtpUrl, apiMeta);
		String response = NingClient.getInstance().loadURl(apiBase + reportOtpUrl, apiMeta);
		return gson.fromJson(response, OTPResponse.class);
	}

	public static GenericBackendResponse reportOtpValidate(OTPRequest otpRequest, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + reportValidateOtpUrl, gson.toJson(otpRequest), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static GenericBackendResponse updateBankStatus(BankAccountUpdateRequest bankAccountUpdateRequest, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + playerBankAccountUpdateUrl, gson.toJson(bankAccountUpdateRequest), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse reportDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + reportDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static ReportUiBeResponse getReportDetailList(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + reportDetailListUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, ReportUiBeResponse.class);
	}
	
	// Membership Apis
	public static MembershipListResponse getMembershipList(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + membershipListUrl, apiMeta);
		return gson.fromJson(response, MembershipListResponse.class);
	}

	public static Membership getMembershipById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getMembershipUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Membership.class);
	}

	public static GenericBackendResponse membershipUpdate(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + membershipUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	// Category Apis
	public static CategoryListResponse getCategoryList(ApiMeta apiMeta, String query) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + categoryListUrl + "?query=" + URLEncoder.encode(query, "UTF-8"), apiMeta);
		return gson.fromJson(response, CategoryListResponse.class);
	}
	
	public static GenericBackendResponse createCategory(Category category, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + categoryCreateUrl + gson.toJson(category));
		String response = NingClient.getInstance().postData(apiBase + categoryCreateUrl, gson.toJson(category), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static CategoryMaterConfigListResponse getCategoryMasterConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getCategoryMasterConfigUrl, apiMeta);
		return gson.fromJson(response, CategoryMaterConfigListResponse.class);
	}

	public static Category getCategoryById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getCategoryUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Category.class);
	}

	public static GenericBackendResponse categoryUpdate(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + categoryUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse categoryMasterConfigUpdate(UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + categoryMasterConfigUpdateUrl, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	//PlayerTransaction Apis
	public static ReportConfigResponse getPaymentTransactionConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + paymentTransactionReportConfigUrl, apiMeta);
		return gson.fromJson(response, ReportConfigResponse.class);
	}
	
	public static ReportUiBeResponse exportDataFeildsInCsvOfPayment(PaymentReportRequest paymentReportRequest, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + paymentExportDataFeildsUrl, gson.toJson(paymentReportRequest), apiMeta);
		String response = NingClient.getInstance().postData(apiBase + paymentExportDataFeildsUrl, gson.toJson(paymentReportRequest), apiMeta);
		return gson.fromJson(response, ReportUiBeResponse.class);
	}
	
	public static PaymentTransactionListResponse getPlayerTransactionPayUList(String query, String status, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + playerTransactionPayUListUrl + "?query=" + URLEncoder.encode(query, "UTF-8")  + "&status=" + URLEncoder.encode(status, "UTF-8"));
		String response = NingClient.getInstance().loadURl(apiBase + playerTransactionPayUListUrl + "?query=" + URLEncoder.encode(query, "UTF-8")  + "&status=" + URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, PaymentTransactionListResponse.class);
	}
	public static PaymentTransactionConfig getPaymentTransactionConfigResponse(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + paymentTransactionConfigUrl);
		String response = NingClient.getInstance().loadURl(apiBase + paymentTransactionConfigUrl, apiMeta);
		return gson.fromJson(response, PaymentTransactionConfig.class);
	}

	// Player Apis
	public static ReportConfigResponse getPlayerConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + palyerConfigUrl, apiMeta);
		return gson.fromJson(response, ReportConfigResponse.class);
	}
	
	public static PlayerListResponse getPlayerList(String query ,String startDate, String endDate, String userName, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + playerListUrl + "?query=" + query + "&startDate=" + startDate + "&endDate=" + endDate);
		String response = NingClient.getInstance().loadURl(apiBase + playerListUrl + "?query=" + query  + "&startDate=" + startDate + "&endDate=" + endDate + "&userName=" + userName, apiMeta); 
		return gson.fromJson(response, PlayerListResponse.class); 
	} 
	  
	public static Player getPlayerDetailsById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getPlayerDetailsUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Player.class);
	}
	
	public static Player updatedocumentwithuniqueKyc(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + playerUpdateDocument, apiMeta);
		return gson.fromJson(response, Player.class);
	}

	public static GenericBackendResponse playerUpdateDocument(String id, String status, String type,String bankDetail, String uniqueKycKey,ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + playerUpdateDocumentUrl + "?id=" + id + "&status=" + status + "&type=" + type + "&bankDetail=" + bankDetail + "&uniqueKycKey=" + uniqueKycKey, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static PlayerBonusChipsListResponse getPlayerBonusChipsList(ApiMeta apiMeta, String id) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + playerBonusChipsListUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, PlayerBonusChipsListResponse.class);
	}

	public static PlayerBonusExpiredChipsListResponse getPlayerBonusExpiredChipsList(ApiMeta apiMeta, String id) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + playerBonusExpiredChipsListUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, PlayerBonusExpiredChipsListResponse.class);
	}

	public static PlayerTdsCertificateListResponse getPlayerTdsCertificateList(ApiMeta apiMeta, String id) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + playerTdsCertificateListUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, PlayerTdsCertificateListResponse.class);
	}

	public static GenericBackendResponse uploadTdsCertificate(PlayerTdsCertificate tdsCertificate, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + uploadTdsCertificateUrl, gson.toJson(tdsCertificate), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse playerTdsCertificateDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + playerTdsCertificateDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static PlayerElearningResponse getPlayerElearningQuizList(ApiMeta apiMeta, String id, String startDate, String endDate, String level, String completed)
			throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(
				apiBase + playerElearningQuizListUrl + "?playerId=" + id + "&startDate=" + startDate + "&endDate=" + endDate + "&level=" + level + "&completed=" + completed,
				apiMeta);
		return gson.fromJson(response, PlayerElearningResponse.class);
	}

	public static ReportUiBeResponse exportDataFeildsInCsv(PlayerReportRequest playerReportRequest, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + playerExportDataFeildsUrl, gson.toJson(playerReportRequest), apiMeta);
		String response = NingClient.getInstance().postData(apiBase + playerExportDataFeildsUrl, gson.toJson(playerReportRequest), apiMeta);
		return gson.fromJson(response, ReportUiBeResponse.class);
	}

	public static SearchResponse getPlayerSearchList(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + playerSearchListUrl);
		String response = NingClient.getInstance().loadURl(apiBase + playerSearchListUrl, apiMeta);
		return gson.fromJson(response, SearchResponse.class);
	}
	
	
	public static GenericBackendResponse saveIPRestriction(RestrictedStateConfigRequest restrictedStateConfigRequest, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + saveIPrestrictionUrl, gson.toJson(restrictedStateConfigRequest), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static GenericBackendResponse deleteIPRestriction(RestrictedStateConfigRequest restrictedStateConfigRequest, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + deleteIPrestrictionUrl, gson.toJson(restrictedStateConfigRequest), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static RestrictedStateResponse getRestrictedState(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getRestrictedStateUrl , apiMeta);
		return gson.fromJson(response, RestrictedStateResponse.class);
	}
	
	public static GenericBackendResponse updateRestrictedState(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + updateRestrictedStateUrl , apiMeta);
		return gson.fromJson(response, RestrictedStateResponse.class);
	}
	
	//player transaction detail
	
	public static PlayerTransactionResponse getPlayerTransactionList(ApiMeta apiMeta, String id)
			throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(
				apiBase + playerTransactionListUrl + "?playerId=" + id ,apiMeta);
		return gson.fromJson(response, PlayerTransactionResponse.class);
	}
	
	//player Ban
	
	public static GenericBackendResponse banPlayer(ApiMeta apiMeta, boolean banPlayer, String id) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + playerBanUrl + "?banPlayer=" + banPlayer + "&id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	//upload KYC Documents
	
	 public static GenericBackendResponse uploadKycPlayerbyId(PlayerKycUpdateModel playerKycUpdateModel, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + playerKycUpdateUrl + gson.toJson(playerKycUpdateModel));
		String response = NingClient.getInstance().postData(apiBase + playerKycUpdateUrl, gson.toJson(playerKycUpdateModel), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	//player join affiliate
	
	public static AffiliateReferralJoinResponse playerRequestToAffiliate(String referralCode, String playerId, ApiMeta apiMeta)
			throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + playerJoinRequest + "&code=" + Objects.toString(referralCode) + "&playerId=" + playerId);
		String response = NingClient.getInstance().loadURl(apiBase + playerJoinRequest + "?code=" + Objects.toString(referralCode) + "&playerId=" + playerId, apiMeta);
		return gson.fromJson(response, AffiliateReferralJoinResponse.class); 
	} 
	
	public static AffiliateReferralJoinResponse getPlayerAffiliate(String playerId, ApiMeta apiMeta)
			throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + playerAffiliateGet + "&playerId" + playerId);
		String response = NingClient.getInstance().loadURl(apiBase + playerAffiliateGet + "?playerId=" + playerId, apiMeta);
		return gson.fromJson(response, AffiliateReferralJoinResponse.class);
	}

	// Poker Table Apis
	public static TableListResponse getTableList(ApiMeta apiMeta, String query, String status, String tableConfig) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(
				apiBase + tableListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status=" + status + "&configId=" + tableConfig, apiMeta);
		return gson.fromJson(response, TableListResponse.class);
	}

	public static GenericBackendResponse tableCreate(Table table, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + tableCreateUrl, gson.toJson(table), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static Table getTableById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getTableUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Table.class);
	}

	public static GenericBackendResponse tableUpdate(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + tableUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static TableConfigResponse getTableConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + tableConfigUrl, apiMeta);
		return gson.fromJson(response, TableConfigResponse.class);
	}

	// Apis Poker Table Configuration

	public static TableConfigurationListResponse getTableConfigurationList(ApiMeta apiMeta, String query, String status) throws InterruptedException, ExecutionException,
			IOException {
		String response = NingClient.getInstance().loadURl(apiBase + tableConfigListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status=" + status, apiMeta);
		return gson.fromJson(response, TableConfigurationListResponse.class);
	}

	public static GenericBackendResponse tableConfigurationCreate(TableConfiguration tableConfig, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + tableConfigCreateUrl, gson.toJson(tableConfig), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static TableConfiguration getTableConfigById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getTableConfigUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, TableConfiguration.class);
	}

	public static GenericBackendResponse tableConfigUpdate(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + tableConfigUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static SearchResponse getTableConfigSearchList(String status, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + tableConfigSearchListUrl + "?status=" + status, apiMeta);
		return gson.fromJson(response, SearchResponse.class);
	}

	// Apis Poker Tournament Configuration
	public static TournamentConfigurationListResponse getTournamentConfigurationList(ApiMeta apiMeta, String query, String status) throws InterruptedException, ExecutionException,
			IOException {
		String response = NingClient.getInstance().loadURl(apiBase + tournamentConfigListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status=" + status, apiMeta);
		return gson.fromJson(response, TournamentConfigurationListResponse.class);
	}

	public static GenericBackendResponse tournamentConfigurationCreate(TournamentConfiguration tournamentConfig, ApiMeta apiMeta) throws InterruptedException, ExecutionException,
			IOException {
		logger.info(apiBase + tournamentConfigCreateUrl + gson.toJson(tournamentConfig));
		String response = NingClient.getInstance().postData(apiBase + tournamentConfigCreateUrl, gson.toJson(tournamentConfig), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static TournamentConfiguration getTournamentConfigById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getTournamentConfigUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, TournamentConfiguration.class);
	}

	public static GenericBackendResponse tournamentConfigUpdate(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + tournamentConfigUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static SearchTableConfigResponse getTournamentConfigSearchList(ApiMeta apiMeta, String status) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + tournamentConfigSearchListUrl + "?status=" + status, apiMeta);
		return gson.fromJson(response, SearchTableConfigResponse.class);
	}

	// Apis Poker Tournament
	public static TournamentListResponse getTournamentList(ApiMeta apiMeta, String query, String status) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + tournamentListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status=" + status, apiMeta);
		return gson.fromJson(response, TournamentListResponse.class);
	}

	public static GenericBackendResponse tournamentCreate(Tournament tournament, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + tournamentCreateUrl + gson.toJson(tournament));
		String response = NingClient.getInstance().postData(apiBase + tournamentCreateUrl, gson.toJson(tournament), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static Tournament getTournamentById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getTournamentUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Tournament.class);
	}

	public static GenericBackendResponse tournamentUpdate(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + tournamentUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static TournamentConfigResponse getTournamentConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + tournamentConfigUrl, apiMeta);
		return gson.fromJson(response, TournamentConfigResponse.class);
	}

	public static TournamentPlayerListResponse getTournamentPlayerList(ApiMeta apiMeta, String query, String id) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + tournamentPlayerListUrl + "?id=" + id + "&query=" + URLEncoder.encode(query, "UTF-8"), apiMeta);
		return gson.fromJson(response, TournamentPlayerListResponse.class);
	}

	// Apis Poker Master configuration
	public static GenericBackendResponse pokerMasterConfigUpdate(UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().postData(apiBase + pokerMasterConfigUpdateUrl, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static PokerMasterConfiguration getPokerMasterConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + getPokerMasterConfigUrl, apiMeta);
		return gson.fromJson(response, PokerMasterConfiguration.class);
	}

	public static PokerMasterConfigResponse getPokerMasterConfigData(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + pokerMasterConfigUrl, apiMeta);
		return gson.fromJson(response, PokerMasterConfigResponse.class);
	}

	// Coupon Apis
	public static CouponListResponse getCouponList(ApiMeta apiMeta, String query, String status) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(
				apiBase + couponListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status=" + URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, CouponListResponse.class);
	}

	public static GenericBackendResponse createCoupon(Coupon coupon, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + couponCreateUrl + gson.toJson(coupon));
		String response = NingClient.getInstance().postData(apiBase + couponCreateUrl, gson.toJson(coupon), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static Coupon getCouponById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String tempUrl = apiBase + getCouponUrl + "?id=" + id;
		logger.info(tempUrl);
		String response = NingClient.getInstance().loadURl(apiBase + getCouponUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Coupon.class);
	}

	public static GenericBackendResponse updateCouponById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String tempUrl = apiBase + couponUpdateUrl + "?id=" + id + gson.toJson(requestData);
		logger.info(tempUrl);
		String response = NingClient.getInstance().postData(apiBase + couponUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);

		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse couponDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + couponDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static CouponConfigResponse getCouponConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + couponConfigUrl, apiMeta);
		return gson.fromJson(response, CouponConfigResponse.class);
	}
	
	
	// other promotion
	
	public static PromotionListResponse getPromotionList(ApiMeta apiMeta, String query, String status) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(
				apiBase + promotionListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status=" + URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, PromotionListResponse.class);
	}
	
	public static GenericBackendResponse createPromotion(PromotionOther promotionOther, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + promotionCreateUrl + gson.toJson(promotionOther));
		String response = NingClient.getInstance().postData(apiBase + promotionCreateUrl, gson.toJson(promotionOther), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static PromotionOther getPromotionById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String tempUrl = apiBase + getPromotionUrl + "?id=" + id;
		logger.info(tempUrl);
		String response = NingClient.getInstance().loadURl(apiBase + getPromotionUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, PromotionOther.class);
	}

	public static GenericBackendResponse updatePromotionById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String tempUrl = apiBase + promotionUpdateUrl + "?id=" + id + gson.toJson(requestData);
		logger.info(tempUrl);
		String response = NingClient.getInstance().postData(apiBase + promotionUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);

		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse promotionDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + promotionDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
		
	}
	
	public static PromotionConfigResponse getPromotionConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + promotionConfigUrl, apiMeta);
		return gson.fromJson(response, PromotionConfigResponse.class);
	}
	
	//chips conversion
	
	public static GenericBackendResponse createChipsConversion(ChipConversion chipConversion, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + chipsConversionCreateUrl + gson.toJson(chipConversion));
		String response = NingClient.getInstance().postData(apiBase + chipsConversionCreateUrl, gson.toJson(chipConversion), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
		
		public static ChipConversionListResponse getChipConversionList(ApiMeta apiMeta, String query) throws InterruptedException, ExecutionException, IOException {
			logger.info(apiBase + chipConversionListUrl);
			String response = NingClient.getInstance().loadURl(apiBase + chipConversionListUrl + "?query=" +URLEncoder.encode(query, "UTF-8") ,apiMeta);
			return gson.fromJson(response, ChipConversionListResponse.class);
		
	}
		
		public static GenericBackendResponse chipConversionDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
			String response = NingClient.getInstance().loadURl(apiBase + chipConversionDeleteUrl + "?id=" + id, apiMeta);
			return gson.fromJson(response, GenericBackendResponse.class);
		}
		
		public static ChipConversion getChipConversionById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
			String tempUrl = apiBase + getChipConversionUrl + "?id=" + id;
			logger.info(tempUrl);
			String response = NingClient.getInstance().loadURl(apiBase + getChipConversionUrl + "?id=" + id, apiMeta);
			return gson.fromJson(response, ChipConversion.class);
		}
		
		public static GenericBackendResponse updateChipConversionById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
			String tempUrl = apiBase + chipConversionUpdateUrl + "?id=" + id + gson.toJson(requestData);
			logger.info(tempUrl);
			String response = NingClient.getInstance().postData(apiBase + chipConversionUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);

			return gson.fromJson(response, GenericBackendResponse.class);
		}
		
	// utilities Apis
			
	 
	public static UtilitiesListResponse getUtilitiesGameIdList(String startDate, String endDate, String gameId, String gameName, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + utilitiesGameIdListUrl + "?startDate=" + startDate + "&endDate=" + endDate + "&gameId=" + gameId);
		String response = NingClient.getInstance().loadURl(apiBase + utilitiesGameIdListUrl + "?startDate=" + startDate + "&endDate=" + endDate + "&gameId=" + gameId + "&gameName=" + gameName, apiMeta); 
		return gson.fromJson(response, UtilitiesListResponse.class); 
	} 
	
	public static UtilitiesGameHistoryListModel getGameHistoryById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + utilitiesGameHistoryIdListUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + utilitiesGameHistoryIdListUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, UtilitiesGameHistoryListModel.class);
	}
      
	// Affiliate Apis
	public static AffiliateListResponse getAffiliateList(ApiMeta apiMeta, String query, String type, String status) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + affiliateListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&type=" + type + "&status=" + status,
				apiMeta);
		return gson.fromJson(response, AffiliateListResponse.class);
	}

	public static GenericBackendResponse createAffiliate(Affiliate affiliate, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + affiliateCreateUrl + gson.toJson(affiliate));
		String response = NingClient.getInstance().postData(apiBase + affiliateCreateUrl, gson.toJson(affiliate), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	} 

	public static Affiliate getAffiliateById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String tempUrl = apiBase + getAffiliateUrl + "?id=" + id;
		logger.info(tempUrl);
		String response = NingClient.getInstance().loadURl(apiBase + getAffiliateUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Affiliate.class);
	}

	// public static Affiliate getBankByIfsc(String ifsc) throws
	// InterruptedException, ExecutionException, IOException {
	// String tempUrl = getBankByIfscUrl + ifsc;
	// logger.info(tempUrl);
	// String response = NingClient.getInstance().loadURl(getBankByIfscUrl +
	// ifsc, null);
	// return gson.fromJson(response, Affiliate.class);
	// }

	public static GenericBackendResponse updateAffiliateById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String tempUrl = apiBase + affiliateUpdateUrl + "?id=" + id + gson.toJson(requestData);
		logger.info(tempUrl);
		String response = NingClient.getInstance().postData(apiBase + affiliateUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);

		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse affiliateDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + affiliateDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static AffiliateConfigResponse getAffiliateConfig(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + affiliateConfigUrl, apiMeta);
		return gson.fromJson(response, AffiliateConfigResponse.class);
	}

	public static AffiliatePlayerListResponse getAffiliatePlayerList(ApiMeta apiMeta, String id, String status) throws InterruptedException, ExecutionException,
			IOException {
		String response = NingClient.getInstance().loadURl(
				apiBase + affiliatePlayersListUrl + "?id=" + id  + "&status=" + URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, AffiliatePlayerListResponse.class);
	}

	public static GenericBackendResponse affiliatePlayerAction(ApiMeta apiMeta, String playerId, String affiliateId, String status) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + affiliatePlayersAction + "?playerId=" + playerId + "&id=" + affiliateId + "&status=" + URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	// Referral Apis
	public static ReferralCodeListResponse getReferralCodeList(ApiMeta apiMeta, String query, String status) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + referralListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&consume=" + status, apiMeta);
		return gson.fromJson(response, ReferralCodeListResponse.class);
	}

	public static GenericBackendResponse createReferralCode(ApiMeta apiMeta, String mobile, String referralCode) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + referralCreateUrl + "?mobile=" + mobile + "&referralCode=" + referralCode, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse deleteReferralCodeById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + referralDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	// Dashboard Apis
	public static ReportingResponse getDashboardSummary(ApiMeta apiMeta, ReportRequest reportRequest) throws InterruptedException, ExecutionException, IOException {
		String tempUrl = apiBase + dashbaordSummaryUrl;
		logger.info(tempUrl);
		logger.info(gson.toJson(reportRequest));
		String response = NingClient.getInstance().postData(apiBase + dashbaordSummaryUrl, gson.toJson(reportRequest), apiMeta);
		return gson.fromJson(response, ReportingResponse.class);
	}

	public static DashboardLiveWidgetsResponse getDashboardLiveWidgets(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String tempUrl = apiBase + dashbaordLiveWidgetUrl;
		logger.info(tempUrl);
		String response = NingClient.getInstance().loadURl(apiBase + dashbaordLiveWidgetUrl, apiMeta);
		return gson.fromJson(response, DashboardLiveWidgetsResponse.class);
	}

	public static DashboardTournamentWidgetsResponse getDashboardTounramentWidgets(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + dashbaordTournamentWidgetUrl, apiMeta);
		return gson.fromJson(response, DashboardTournamentWidgetsResponse.class);
	}

	// Question Apis
	public static QuestionListResponse getQuestionList(ApiMeta apiMeta, String level, String status, String type, String query) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + questionListUrl + "?query=" + query + "&level=" + URLEncoder.encode(level, "UTF-8") + "&type=" + URLEncoder.encode(type, "UTF-8") + "&status="
						+ URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, QuestionListResponse.class);
	}
	
	public static GenericBackendResponse createQuestion(Question question, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + questionCreateUrl + gson.toJson(question));
		String response = NingClient.getInstance().postData(apiBase + questionCreateUrl, gson.toJson(question), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static GenericBackendResponse questionDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + questionDeleteUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + questionDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static Question getQuestionById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + questionGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + questionGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Question.class);
	}

	public static GenericBackendResponse updateQuestionById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + questionUpdateUrl + "?id=" + id);
		String response = NingClient.getInstance().postData(apiBase + questionUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static ElearningConfigResponse getQuestionConfigResponse(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + elearningConfigUrl, apiMeta);
		return gson.fromJson(response, ElearningConfigResponse.class);
	}
	
	//player Achievement
	public static PlayerAchievementConfigResponse getPlayerAchievementConfigResponse(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + playerAchievementConfigUrl, apiMeta);
		return gson.fromJson(response, PlayerAchievementConfigResponse.class);
	}
	
	public static AchievementPlayersConfigResponse getAchievementPlayersConfigResponse(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + achievementPlayersConfigUrl, apiMeta);
		return gson.fromJson(response, AchievementPlayersConfigResponse.class);
	}
	
	public static AchievementPlayersListResponse getAchievementPlayerList(ApiMeta apiMeta, String id, String level) throws InterruptedException, ExecutionException,
	IOException {
		String response = NingClient.getInstance().loadURl(
		apiBase + achievementPlayersListUrl + "?id=" + id  + "&level=" + URLEncoder.encode(level, "UTF-8"), apiMeta);
		return gson.fromJson(response, AchievementPlayersListResponse.class);
}
	
	public static GenericBackendResponse createPlayerAchievement(PlayerAchievement playerAchievement, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + playerAchievementCreateUrl + gson.toJson(playerAchievement));
		String response = NingClient.getInstance().postData(apiBase + playerAchievementCreateUrl, gson.toJson(playerAchievement), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static PlayerAchievementListResponse getPlayerAchievementList(ApiMeta apiMeta, String level, String status, String type, String query) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + playerAchievementListUrl + "?query=" + query + "&level=" + URLEncoder.encode(level, "UTF-8") + "&type=" + URLEncoder.encode(type, "UTF-8") + "&status="
						+ URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, PlayerAchievementListResponse.class);
	}
	
	public static PlayerAchievement getPlayerAchievementById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + playerAchievementGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + playerAchievementGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, PlayerAchievement.class);
	}
	
	public static GenericBackendResponse updatePlayerAchievementById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + playerAchievementUpdateUrl + "?id=" + id);
		String response = NingClient.getInstance().postData(apiBase + playerAchievementUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	// vote
	public static VoteListResponse getVoteList(ApiMeta apiMeta, String status, String query) throws InterruptedException, ExecutionException,IOException {
		String response = NingClient.getInstance().loadURl(apiBase + voteListUrl + "?query=" + query + "&status=" + URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, VoteListResponse.class); 
	} 
	
	public static GenericBackendResponse createVote(Vote vote, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + voteCreateUrl + gson.toJson(vote));
		String response = NingClient.getInstance().postData(apiBase + voteCreateUrl, gson.toJson(vote), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static Vote getVoteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + voteGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + voteGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Vote.class);
	}
	public static GenericBackendResponse voteDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + voteDeleteUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + voteDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	public static GenericBackendResponse updateVoteById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + voteUpdateUrl + "?id=" + id);
		String response = NingClient.getInstance().postData(apiBase + voteUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	public static VoteListResponse getVoteQuestion(ApiMeta apiMeta) throws InterruptedException, ExecutionException,
	IOException {
		String response = NingClient.getInstance().loadURl(
		apiBase + voteQuestionUrl, apiMeta);
		return gson.fromJson(response, VoteListResponse.class);
	}
	
	// Price APIS
	public static GenericBackendResponse createPrice(Price price, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + priceCreateUrl + gson.toJson(price));
		String response = NingClient.getInstance().postData(apiBase + priceCreateUrl, gson.toJson(price), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static PriceListResponse getPriceList(ApiMeta apiMeta, String status, String query) throws InterruptedException, ExecutionException,IOException {
		String response = NingClient.getInstance().loadURl(apiBase + priceListUrl + "?query=" + query + "&status=" + URLEncoder.encode(status, "UTF-8"), apiMeta);
		return gson.fromJson(response, PriceListResponse.class);
	}
	
	public static GenericBackendResponse priceDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + priceDeleteUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + priceDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static Price getPriceById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + priceGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + priceGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Price.class);
	}

	public static GenericBackendResponse updatePriceById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + priceUpdateUrl + "?id=" + id);
		String response = NingClient.getInstance().postData(apiBase + priceUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static ElearningConfigResponse getPriceConfigResponse(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + elearningConfigUrl, apiMeta);
		return gson.fromJson(response, ElearningConfigResponse.class);
	}

	//poll
	public static GenericBackendResponse pollDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + pollDeleteUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + pollDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	public static GenericBackendResponse updatePollById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + pollUpdateUrl + "?id=" + id);
		String response = NingClient.getInstance().postData(apiBase + pollUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	public static Poll getPollById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + questionGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + pollGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Poll.class);
	}
		public static GenericBackendResponse createPoll(Poll poll, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
			logger.info(apiBase + pollCreateUrl + gson.toJson(poll));
			String response = NingClient.getInstance().postData(apiBase + pollCreateUrl, gson.toJson(poll), apiMeta);
			return gson.fromJson(response, GenericBackendResponse.class);
		}
		public static PollListResponse getPollList(ApiMeta apiMeta, String status, String query) throws InterruptedException, ExecutionException,
		IOException {
			String response = NingClient.getInstance().loadURl(
			apiBase + pollListUrl + "?query=" + query + "&level=" + "&status="+ URLEncoder.encode(status, "UTF-8"), apiMeta);
			return gson.fromJson(response, PollListResponse.class);
		}
		
	// E-learning Dashboard Apis
	public static ReportingResponse getElearningDashboardSummary(ApiMeta apiMeta, ReportRequest reportRequest) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + elearningDashbaordSummaryUrl + gson.toJson(reportRequest));
		String response = NingClient.getInstance().postData(apiBase + elearningDashbaordSummaryUrl, gson.toJson(reportRequest), apiMeta);
		return gson.fromJson(response, ReportingResponse.class);
	}

	// E-learning Quiz Apis
	public static ElearningQuizResponse getElearningQuizList(ApiMeta apiMeta, String startDate, String endDate, String level, String quizStatus) throws InterruptedException,
			ExecutionException, IOException {
		logger.info(apiBase + elearningQuizListUrl + "?level=" + level + "&startDate=" + startDate + "&endDate=" + endDate + "&completed=" + quizStatus);
		String response = NingClient.getInstance().loadURl(
				apiBase + elearningQuizListUrl + "?level=" + level + "&completed=" + quizStatus + "&startDate=" + startDate + "&endDate=" + endDate, apiMeta);
		return gson.fromJson(response, ElearningQuizResponse.class);
	}

	// Blog Apis
	public static BlogListResponse getBlogList(ApiMeta apiMeta, String query, String status, String topicId, String trivia) throws InterruptedException, ExecutionException,IOException {			
		logger.info(apiBase + blogCreateUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status=" + URLEncoder.encode(status, "UTF-8") + "&trivia=" + trivia);
		String response = NingClient.getInstance().loadURl(apiBase + blogListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status=" + URLEncoder.encode(status, "UTF-8") + "&topicId="
				+ URLEncoder.encode(topicId, "UTF-8") + "&trivia=" + trivia, apiMeta);				
		   return gson.fromJson(response, BlogListResponse.class);
	}
//Forum in Blog
	
	public static ForumListResponse getForumList(ApiMeta apiMeta, String query, String status, String topicId,
			String trivia) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + forumListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status="
				+ URLEncoder.encode(status, "UTF-8") + "&trivia=" + trivia);
		String response = NingClient.getInstance()
				.loadURl(apiBase + forumListUrl + "?query=" + URLEncoder.encode(query, "UTF-8") + "&status="
						+ URLEncoder.encode(status, "UTF-8") + "&topicId=" + URLEncoder.encode(topicId, "UTF-8")
						+ "&trivia=" + trivia, apiMeta);
		return gson.fromJson(response, ForumListResponse.class);
	}
	
	//forum in Blog
	
	public static GenericBackendResponse createForum(Forum forum, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + forumCreateUrl + gson.toJson(forum));
		String response = NingClient.getInstance().postData(apiBase + forumCreateUrl, gson.toJson(forum), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	public static Forum getForumById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + forumGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + forumGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Forum.class);
	}
	
	public static GenericBackendResponse createBlog(Blog blog, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + blogCreateUrl + gson.toJson(blog));
		String response = NingClient.getInstance().postData(apiBase + blogCreateUrl, gson.toJson(blog), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse blogDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + blogDeleteUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + blogDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	public static GenericBackendResponse forumDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + forumDeleteUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + forumDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	public static Blog getBlogById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + blogGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + blogGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Blog.class);
	}

	public static GenericBackendResponse updateBlogById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + blogUpdateUrl + "?id=" + id + gson.toJson(requestData));
		String response = NingClient.getInstance().postData(apiBase + blogUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	//forum
	public static GenericBackendResponse updateForumById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + forumUpdateUrl + "?id=" + id + gson.toJson(requestData));
		String response = NingClient.getInstance().postData(apiBase + forumUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static BlogConfigResponse getBlogConfigResponse(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + blogConfigUrl);
		String response = NingClient.getInstance().loadURl(apiBase + blogConfigUrl, apiMeta);
		return gson.fromJson(response, BlogConfigResponse.class);
	}

	public static SearchResponse getBlogSearchList(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + homeBlogSearchListUrl);
		String response = NingClient.getInstance().loadURl(apiBase + homeBlogSearchListUrl, apiMeta);
		return gson.fromJson(response, SearchResponse.class);
	}

	public static BlogListResponse getAddedHomeBlogList(ApiMeta apiMeta, String id, String query, String topicId) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + homeBlogListUrl + "?id=" + id + "&query=" + URLEncoder.encode(query, "UTF-8") + "&topicId=" + topicId);
		String response = NingClient.getInstance()
				.loadURl(apiBase + homeBlogListUrl + "?id=" + id + "&query=" + URLEncoder.encode(query, "UTF-8") + "&topicId=" + topicId, apiMeta);
		return gson.fromJson(response, BlogListResponse.class);
	}

	public static GenericBackendResponse homeBlogDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + homeBlogDeleteUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + homeBlogDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse changeHomeBlogMoveUp(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + changeHomeBlogMoveUpUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + changeHomeBlogMoveUpUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse changeHomeBlogMoveDown(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + changeHomeBlogMoveDownUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + changeHomeBlogMoveDownUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	//Testimonial
	
	public static GenericBackendResponse createTestimonial(Testimonial testimonial, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + testimonialCreateUrl + gson.toJson(testimonial));
		String response = NingClient.getInstance().postData(apiBase + testimonialCreateUrl, gson.toJson(testimonial), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}
	
	public static TestimonialListResponse getTestimonialList(ApiMeta apiMeta, String query) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + testimonialListUrl + "?query=" + URLEncoder.encode(query, "UTF-8"));
		String response = NingClient.getInstance().loadURl(apiBase + testimonialListUrl + "?query" + URLEncoder.encode(query, "UTF-8"), apiMeta); 
		return gson.fromJson(response, TestimonialListResponse.class);
	}
	
	public static Testimonial getTestimonialById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + testimonialGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + testimonialGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Testimonial.class);
	}
	
	public static GenericBackendResponse updateTestimonialById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + testimonialUpdateUrl + "?id=" + id + gson.toJson(requestData));
		String response = NingClient.getInstance().postData(apiBase + testimonialUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	// Topic Apis
	public static TopicListResponse getTopicList(ApiMeta apiMeta, String query) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + topicListUrl + "?query=" + URLEncoder.encode(query, "UTF-8"));
		String response = NingClient.getInstance().loadURl(apiBase + topicListUrl + "?query=" + URLEncoder.encode(query, "UTF-8"), apiMeta);
		return gson.fromJson(response, TopicListResponse.class);
	}

	public static GenericBackendResponse createTopic(Topic topic, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + topicCreateUrl + gson.toJson(topic));
		String response = NingClient.getInstance().postData(apiBase + topicCreateUrl, gson.toJson(topic), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static GenericBackendResponse topicDeleteById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + topicDeleteUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + topicDeleteUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static Topic getTopicById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + topicGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + topicGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Topic.class);
	}

	public static GenericBackendResponse updateTopicById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + topicUpdateUrl + "?id=" + id);
		String response = NingClient.getInstance().postData(apiBase + topicUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static SearchResponse getTopicSearchList(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + topicSearchListUrl);
		String response = NingClient.getInstance().loadURl(apiBase + topicSearchListUrl, apiMeta);
		return gson.fromJson(response, SearchResponse.class);
	}

	// //Withdraw
	public static WithdrawListResponse getWithdrawList(ApiMeta apiMeta, String status, String playerId) throws InterruptedException, ExecutionException, IOException {
		String response = NingClient.getInstance().loadURl(apiBase + withdrawListUrl + "?status=" + URLEncoder.encode(status, "UTF-8") + "&playerId=" + playerId, apiMeta);
		return gson.fromJson(response, WithdrawListResponse.class);
	}

	public static Withdraw getWithdrawById(String id, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + withdrawGetUrl + "?id=" + id);
		String response = NingClient.getInstance().loadURl(apiBase + withdrawGetUrl + "?id=" + id, apiMeta);
		return gson.fromJson(response, Withdraw.class);
	}

	public static GenericBackendResponse updateWithdrawById(String id, UpdateRequest requestData, ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + withdrawUpdateUrl + "?id=" + id);
		String response = NingClient.getInstance().postData(apiBase + withdrawUpdateUrl + "?id=" + id, gson.toJson(requestData), apiMeta);
		return gson.fromJson(response, GenericBackendResponse.class);
	}

	public static WithdrawConfigResponse getWithdrawConfigResponse(ApiMeta apiMeta) throws InterruptedException, ExecutionException, IOException {
		logger.info(apiBase + withdrawConfigUrl);
		String response = NingClient.getInstance().loadURl(apiBase + withdrawConfigUrl, apiMeta);
		return gson.fromJson(response, WithdrawConfigResponse.class);
	}
}


