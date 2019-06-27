package com.actolap.wse.backoffice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.restexpress.Request;
import org.restexpress.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.actolap.wse.Constants;
import com.actolap.wse.LyveSendSms;
import com.actolap.wse.UserPermission;
import com.actolap.wse.analytics.aggregate.model.AggregateAffiliatePlayer;
import com.actolap.wse.commons.GenericResponse;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.config.InMemory;
import com.actolap.wse.constants.Urlparams;
import com.actolap.wse.dao.AffiliateDao;
import com.actolap.wse.dao.AffiliatePlayerDao;
import com.actolap.wse.dao.AggregateAffiliatePlayerDao;
import com.actolap.wse.dao.OneTimePasswordDao;
import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.dao.UserDao;
import com.actolap.wse.dto.AffiliateDto;
import com.actolap.wse.dto.AffiliateIndPlayerDto;
import com.actolap.wse.dto.DocumentRequestDto;
import com.actolap.wse.dto.UserDepartment;
import com.actolap.wse.enums.ResponseCode;
import com.actolap.wse.game.dao.AffiliateReferralCodeDao;
import com.actolap.wse.manager.AnalyticsManager;
import com.actolap.wse.model.BankAccount;
import com.actolap.wse.model.OneTimePassword;
import com.actolap.wse.model.User;
import com.actolap.wse.model.affiliate.Affiliate;
import com.actolap.wse.model.affiliate.Affiliate.AffiliateCommissionType;
import com.actolap.wse.model.affiliate.Affiliate.AffiliateType;
import com.actolap.wse.model.affiliate.AffiliateDocument;
import com.actolap.wse.model.affiliate.AffiliateDocument.DocumentStatus;
import com.actolap.wse.model.affiliate.AffiliateDocument.DocumentType;
import com.actolap.wse.model.affiliate.AffiliateProfile;
import com.actolap.wse.model.affiliate.AffiliateReferralCode;
import com.actolap.wse.model.game.poker.AffiliatePlayer;
import com.actolap.wse.model.game.poker.AffiliatePlayer.AffiliateReferralStatus;
import com.actolap.wse.model.game.poker.AffiliatePlayer.PlayerReferralStatus;
import com.actolap.wse.model.game.poker.AffiliateStatus;
import com.actolap.wse.model.player.Address;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.player.Player.Gender;
import com.actolap.wse.model.player.Player.MaritalStatus;
import com.actolap.wse.model.report.AffiliatePlayerExportCsv;
import com.actolap.wse.model.report.AffiliatePlayerMatric;
import com.actolap.wse.request.AffiliateCreateRequest;
import com.actolap.wse.request.AffiliatePlayerRequest;
import com.actolap.wse.request.OTPRequest;
import com.actolap.wse.request.UpdateRequest;
import com.actolap.wse.response.AffiliateConfigResponse;
import com.actolap.wse.response.AffiliateGetResponse;
import com.actolap.wse.response.AffiliateIndPlayerResponse;
import com.actolap.wse.response.AffiliateListResponse;
import com.actolap.wse.response.AffiliateOtpResponse;
import com.actolap.wse.response.AffiliatePlayerConfigResponse;
import com.actolap.wse.response.AffiliatePlayerResponse;
import com.actolap.wse.rest.secuirty.SecureAnnotation.UserSecure;
import com.actolap.wse.rest.secuirty.SecureAnnotation.WSEPermission;
import com.google.common.base.Optional;
import com.mongodb.DuplicateKeyException;

@Path("/affiliate")
@Api(value = "Rest API")
@UserSecure
public class AffiliateController {
	private static final Logger LOG = LoggerFactory.getLogger(AffiliateController.class);

	@WSEPermission(pl = { UserPermission.affiliates_create })
	public GenericResponse create(Request request, Response response) {
		return affiliateCreateDoc(request.getBodyAs(AffiliateCreateRequest.class), request, response);
	}

	@WSEPermission(pl = { UserPermission.affiliate_export_csv })
	public GenericResponse affiliatePlayerValidateOtp(Request request, Response response) {
		return affiliatePlayerValidateOtpDoc(request.getBodyAs(OTPRequest.class), request, response);
	}

	@WSEPermission(pl = { UserPermission.affiliate_export_csv_file })
	public GenericResponse getAffiliatePlayerExportCsv(Request request, Response response) {
		return getAffiliatePlayerExportCsvDoc(request.getBodyAs(AffiliatePlayerRequest.class), request, response);
	}

	@WSEPermission(pl = { UserPermission.affiliates_update })
	public GenericResponse update(Request request, Response response) {
		return affiliateUpdateDoc(request.getBodyAs(UpdateRequest.class), request, response);
	}

	@WSEPermission(pl = { UserPermission.affiliates_config })
	@GET
	@Path("/config")
	@ApiOperation(value = "Config", notes = "Affiliate Config", response = AffiliateConfigResponse.class, httpMethod = "get")
	public AffiliateConfigResponse config(Request request, Response response0) {
		AffiliateConfigResponse response = new AffiliateConfigResponse();
		try {
			for (Entry<String, AffiliateType> entry : InMemory.affiliateTypesMap.entrySet()) {
				response.getTypes().put(entry.getKey(), entry.getValue().toString());
			}
			for (Entry<String, AffiliateCommissionType> entry : InMemory.affiliateCommissionTypes.entrySet()) {
				response.getCommissionTypes().put(entry.getKey(), entry.getValue().toString());
			}
			for (Entry<String, AffiliateStatus> entry : InMemory.affiliateStatusMap.entrySet()) {
				response.getStatusMap().put(entry.getKey(), entry.getValue().toString());
			}
			for (Entry<String, PlayerReferralStatus> entry : InMemory.affiliatePlayerStatusMap.entrySet()) {
				response.getPlayerStatusMap().put(entry.getKey(), entry.getValue().toString());
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@POST
	@Path("/create")
	@ApiOperation(value = "Create", notes = "Affiliate Create", response = GenericResponse.class, httpMethod = "post")
	public GenericResponse affiliateCreateDoc(@ApiParam(required = true) AffiliateCreateRequest affiliateRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response) {
		GenericResponse tableResponse = new GenericResponse();
		try {
			if (affiliateRequest != null && Utils.isNotEmpty(affiliateRequest.getEmail())
					&& Utils.isNotEmpty(affiliateRequest.getCommissionType())
					&& Utils.isNotEmpty(affiliateRequest.getPassword()) && Utils.isNotEmpty(affiliateRequest.getType())
					&& affiliateRequest.getMobile() != null
					&& (affiliateRequest.getProfile() != null && affiliateRequest.getProfile().getAddress() != null
							&& Utils.isNotEmpty(affiliateRequest.getProfile().getFirstName())
							&& Utils.isNotEmpty(affiliateRequest.getProfile().getLastName())
							&& Utils.isNotEmpty(affiliateRequest.getProfile().getGender())
							&& affiliateRequest.getProfile().getDob() != null
							&& Utils.isNotEmpty(affiliateRequest.getProfile().getAddress().getAddressLine1())
							&& Utils.isNotEmpty(affiliateRequest.getProfile().getAddress().getCity())
							&& Utils.isNotEmpty(affiliateRequest.getProfile().getAddress().getPinCode())
							&& Utils.isNotEmpty(affiliateRequest.getProfile().getAddress().getState()))
					&& (affiliateRequest.getBankDetail() != null
							&& Utils.isNotEmpty(affiliateRequest.getBankDetail().getBankName())
							&& Utils.isNotEmpty(affiliateRequest.getBankDetail().getBranch())
							&& Utils.isNotEmpty(affiliateRequest.getBankDetail().getAccountNumber())
							&& Utils.isNotEmpty(affiliateRequest.getBankDetail().getIfscCode())
							&& Utils.isNotEmpty(affiliateRequest.getBankDetail().getAccountName())
							&& Utils.isNotEmpty(affiliateRequest.getBankDetail().getAccountType()))
					&& affiliateRequest.getSettlementDate() > 0 && affiliateRequest.getCommissionMargin() != null
					&& (affiliateRequest.isTournament() && affiliateRequest.getTournamentMargin() != null
							|| !affiliateRequest.isTournament())
					&& !affiliateRequest.getDocuments().isEmpty() && affiliateRequest.getDocuments().size() >= 2
					&& ((affiliateRequest.isOdFacility() && affiliateRequest.getOdAmount() != null
							&& affiliateRequest.getOdAmount() > 0) || !affiliateRequest.isOdFacility())
					&& Utils.isNotEmpty(affiliateRequest.getDomain())) {
				Affiliate affiliateObj = AffiliateDao.getByMobile(affiliateRequest.getMobile());
				if (affiliateObj == null) {
					Affiliate affiliate = new Affiliate();
					affiliate.setName(affiliateRequest.getProfile().getFirstName() + " "
							+ affiliateRequest.getProfile().getLastName());
					affiliate.setEmail(affiliateRequest.getEmail());
					affiliate.setMobile(affiliateRequest.getMobile());
					affiliate.setPassword(affiliateRequest.getPassword());
					affiliate.setSettlementDate(affiliateRequest.getSettlementDate());
					affiliate.setStatus(AffiliateStatus.PENDING_APPROVAL);
					affiliate.setOdFacility(affiliateRequest.isOdFacility());
					affiliate.setSecurityDeposit(affiliateRequest.isSecurityDeposit());
					affiliate.setTournament(affiliateRequest.isTournament());
					affiliate.setAffiliateEmail(affiliateRequest.isAffiliateEmail());
					affiliate.setAffiliateCsvExport(affiliateRequest.isAffiliateCsvExport());
					if (affiliateRequest.isTournament())
						affiliate.setTournamentMargin(affiliateRequest.getTournamentMargin());
					affiliate.setCommissionMargin(affiliateRequest.getCommissionMargin());
					boolean isError = false;
					try {
						AffiliateType affiliateType = AffiliateType.valueOf(affiliateRequest.getType());
						affiliate.setType(affiliateType);
					} catch (IllegalArgumentException e) {
						LOG.error(e.getMessage(), e);
						tableResponse.setEd("Affiliate type " + affiliateRequest.getType() + " is not valid");
						isError = true;
					}
					try {
						AffiliateCommissionType commissionType = AffiliateCommissionType
								.valueOf(affiliateRequest.getCommissionType());
						affiliate.setCommissionType(commissionType);
					} catch (IllegalArgumentException e) {
						LOG.error(e.getMessage(), e);
						tableResponse.setEd(
								"Affiliate commission type " + affiliateRequest.getCommissionType() + " is not valid");
						isError = true;
					}
					AffiliateProfile profile = new AffiliateProfile();
					profile.setFirstName(affiliateRequest.getProfile().getFirstName());
					profile.setLastName(affiliateRequest.getProfile().getLastName());
					try {
						Gender gender = Gender.valueOf(affiliateRequest.getProfile().getGender());
						profile.setGender(gender);
					} catch (IllegalArgumentException e) {
						LOG.error(e.getMessage(), e);
						tableResponse.setEd(
								"Affiliate gender " + affiliateRequest.getProfile().getGender() + " is not valid");
						isError = true;
					}
					if (affiliateRequest.getProfile().getMaritalStatus() != null) {
						try {
							MaritalStatus maritalStatus = MaritalStatus
									.valueOf(affiliateRequest.getProfile().getMaritalStatus());
							profile.setMaritalStatus(maritalStatus);
						} catch (IllegalArgumentException e) {
							LOG.error(e.getMessage(), e);
							tableResponse.setEd("Affiliate marital Status "
									+ affiliateRequest.getProfile().getMaritalStatus() + " is not valid");
							isError = true;
						}
					}
					profile.setDob(new Date(affiliateRequest.getProfile().getDob()));
					Address address = new Address();
					address.setAddressLine1(affiliateRequest.getProfile().getAddress().getAddressLine1());
					address.setAddressLine2(affiliateRequest.getProfile().getAddress().getAddressLine2());
					address.setCity(affiliateRequest.getProfile().getAddress().getCity());
					address.setCountry(affiliateRequest.getProfile().getAddress().getCountry());
					address.setPinCode(affiliateRequest.getProfile().getAddress().getPinCode());
					address.setState(affiliateRequest.getProfile().getAddress().getState());
					profile.setAddress(address);
					affiliate.setProfile(profile);
					BankAccount bankAccount = new BankAccount();
					bankAccount.setBankName(affiliateRequest.getBankDetail().getBankName());
					bankAccount.setBranch(affiliateRequest.getBankDetail().getBranch());
					bankAccount.setAccountNumber(affiliateRequest.getBankDetail().getAccountNumber());
					bankAccount.setIfscCode(affiliateRequest.getBankDetail().getIfscCode());
					bankAccount.setAccountName(affiliateRequest.getBankDetail().getAccountName());
					bankAccount.setAccountType(affiliateRequest.getBankDetail().getAccountType());
					affiliate.setBankDetail(bankAccount);
					AffiliateReferralCode affiliateReferralCodeset = AffiliateReferralCodeDao
							.getByMobile(affiliateRequest.getMobile());
					if (affiliateReferralCodeset != null) {
						affiliate.setPlayerCode(affiliateReferralCodeset.getReferralCode());
						AffiliateReferralCodeDao.consumed(affiliateReferralCodeset.getId());
					}
					AffiliateDocument affiliateDocument = null;
					boolean addDocumnet = true;
					for (DocumentRequestDto document : affiliateRequest.getDocuments()) {
						affiliateDocument = new AffiliateDocument();
						affiliateDocument.setNumber(document.getNumber());
						affiliateDocument.setDocumentName(document.getDocumentName());
						affiliateDocument.setAadharNumber(document.getAadharNumber());
						try {
							DocumentType documentType = DocumentType.valueOf(document.getType());
							for (AffiliateDocument existDocument : affiliate.getDocuments()) {
								if (existDocument.getType().equals(documentType)) {
									addDocumnet = false;
									break;
								}

							}
							if (addDocumnet)
								affiliateDocument.setType(documentType);
							else
								tableResponse.setEd("Document type " + document.getType() + " can not duplicate");

						} catch (IllegalArgumentException e) {
							LOG.error(e.getMessage(), e);
							tableResponse.setEd("Document type " + document.getType() + " is not valid");
							isError = true;
						}
						affiliateDocument.setUrl(document.getUrl());
						affiliateDocument.setStatus(DocumentStatus.PENDING);
						affiliateDocument.setUpload(new Date());
						affiliate.getDocuments().add(affiliateDocument);
						affiliate.setOdFacility(affiliateRequest.isOdFacility());
						if (affiliateRequest.isOdFacility())
							affiliate.setOdAmount(affiliateRequest.getOdAmount());
						affiliate.setKycState(6);
					}
					if (!isError && addDocumnet) {
						affiliate.setEmailVerifiedToken(UUID.randomUUID().toString() + "-" + affiliate.getId());
						AffiliateDao.persist(affiliate);
						com.actolap.wse.Utils.sentEmail(affiliateRequest.getEmail(), affiliate.getEmailVerifiedToken(),
								affiliateRequest.getDomain(), affiliateRequest.getProfile().getFirstName() + " "
										+ affiliateRequest.getProfile().getLastName(),
								false);
						tableResponse.setS(true);
						tableResponse.setMsg("Affiliate has been successfully created");
					}
				} else {
					tableResponse.setEd("This mobile number has been already registered");
				}
			} else {
				tableResponse.setEd("Required fields are coming invalid");
			}
		} catch (DuplicateKeyException e) {
			LOG.error(e.getMessage(), e);
			tableResponse.setEd("This email is already exist");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			tableResponse.setEd(e.getMessage());
		}
		return tableResponse;
	}

	@WSEPermission(pl = { UserPermission.affiliates_view })
	@GET
	@Path("/list")
	@ApiOperation(value = "List", notes = "Affiliate List", response = AffiliateListResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "query", value = "Title", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "type", value = "Type", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "status", value = "Status", dataType = "string", paramType = "query", required = true) })
	public AffiliateListResponse list(Request request, Response responseO) {
		AffiliateListResponse response = new AffiliateListResponse();
		try {
			String query = request.getHeader(Urlparams.query);
			AffiliateType type = null;
			if (request.getHeader(Urlparams.type) != null && Utils.isNotEmpty(request.getHeader(Urlparams.type)))
				type = AffiliateType.valueOf(request.getHeader(Urlparams.type));
			AffiliateStatus status = null;
			if (request.getHeader(Urlparams.status) != null && Utils.isNotEmpty(request.getHeader(Urlparams.status)))
				status = AffiliateStatus.valueOf(request.getHeader(Urlparams.status));
			List<Affiliate> affiliateList = AffiliateDao.list(query, type, status);
			if (affiliateList != null && !affiliateList.isEmpty()) {
				for (Affiliate affiliate : affiliateList) {
					AffiliateDto affiliateData = new AffiliateDto(affiliate);
					response.getAffiliateList().add(affiliateData);
				}
			} else {
				response.setMsg("No affiliate found");
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.affiliates_get })
	@GET
	@Path("/get")
	@ApiOperation(value = "Get", notes = "Get Affiliate Details By Id", response = AffiliateGetResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Affiliate Id", dataType = "string", paramType = "query", required = true) })
	public AffiliateGetResponse get(Request request, Response responseO) {
		AffiliateGetResponse response = new AffiliateGetResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				Affiliate affiliate = AffiliateDao.getById(id);
				if (affiliate != null) {
					response = new AffiliateGetResponse(affiliate);
					response.setS(true);
				} else {
					response.setEd("Id is not valid");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id should not be empty");
		}
		return response;
	}

	@POST
	@Path("/update")
	@ApiOperation(value = "Update", notes = "Affiliate Update", response = GenericResponse.class, httpMethod = "post")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Affiliate Id", dataType = "string", paramType = "query", required = true) })
	public GenericResponse affiliateUpdateDoc(@ApiParam(required = true) UpdateRequest affiliateRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response0) {
		GenericResponse response = new GenericResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				AffiliateStatus status = null;
				if (affiliateRequest != null && affiliateRequest.getRequestData() != null) {
					Affiliate affiliate = AffiliateDao.getById(id);
					if (affiliate != null) {
						Map<String, Object> mongoUpdate = new HashMap<String, Object>();
						mongoUpdate.putAll(affiliateRequest.getRequestData());
						for (Entry<String, Object> entry : affiliateRequest.getRequestData().entrySet()) {
							if (entry.getKey().equals("commissionMargin")) {
								mongoUpdate.put(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
							} else if (entry.getKey().equals("affiliateEmail")) {
								mongoUpdate.put(entry.getKey(), Boolean.parseBoolean(entry.getValue().toString()));
							} else if (entry.getKey().equals("affiliateCsvExport")) {
								mongoUpdate.put(entry.getKey(), Boolean.parseBoolean(entry.getValue().toString()));
							} else if (entry.getKey().equals("profile.firstName")) {
								if (mongoUpdate.get("profile.lastName") != null)
									mongoUpdate.put("name",
											entry.getValue() + " " + mongoUpdate.get("profile.lastName"));
								else {
									if (affiliate.getProfile() != null)
										mongoUpdate.put("name",
												entry.getValue() + " " + affiliate.getProfile().getLastName());
								}
								if (affiliate.getKycState() == 0)
									mongoUpdate.put("kycState", 1);
							} else if (entry.getKey().equals("profile.lastName")) {
								if (mongoUpdate.get("profile.firstName") != null)
									mongoUpdate.put("name",
											mongoUpdate.get("profile.firstName") + " " + entry.getValue());
								else {
									if (affiliate.getProfile() != null)
										mongoUpdate.put("name",
												affiliate.getProfile().getFirstName() + " " + entry.getValue());
								}
								if (affiliate.getKycState() == 0)
									mongoUpdate.put("kycState", 1);
							} else if (entry.getKey().equals("profile.address.addressLine1")
									|| entry.getKey().equals("profile.address.state")
									|| entry.getKey().equals("profile.address.city")
									|| entry.getKey().equals("profile.address.country")
									|| entry.getKey().equals("profile.address.pinCode")) {
								if (affiliate.getKycState() != 6)
									mongoUpdate.put("kycState", 6);
							} else if (entry.getKey().equals("settlementDate")) {
								mongoUpdate.put(entry.getKey(), Integer.valueOf(entry.getValue().toString()));
							} else if (entry.getKey().equals("type")) {
								mongoUpdate.put(entry.getKey(), AffiliateType.valueOf(entry.getValue().toString()));
							} else if (entry.getKey().equals("commissionType")) {
								mongoUpdate.put(entry.getKey(),
										AffiliateCommissionType.valueOf(entry.getValue().toString()));
							} else if (entry.getKey().equals("securityDeposit")
									|| entry.getKey().equals("odFacility")) {
								mongoUpdate.put(entry.getKey(), Boolean.parseBoolean(entry.getValue().toString()));
							} else if (entry.getKey().equals("tournament")) {
								if (entry.getValue() != null && (entry.getValue().toString().equals("true"))) {
									if (mongoUpdate.get("tournamentMargin") != null) {
										mongoUpdate.put("tournamentMargin",
												Double.parseDouble(mongoUpdate.get("tournamentMargin").toString()));
										mongoUpdate.put(entry.getKey(),
												Boolean.parseBoolean(entry.getValue().toString()));
									} else {
										mongoUpdate.remove("tournament");
										mongoUpdate.remove("tournamentMargin");
									}
								} else if (entry.getValue() != null && (entry.getValue().toString().equals("false"))) {
									mongoUpdate.put(entry.getKey(), Boolean.parseBoolean(entry.getValue().toString()));
									mongoUpdate.remove("tournamentMargin");
								}
							} else if (entry.getKey().equals("status")) {
								status = AffiliateStatus.valueOf(entry.getValue().toString());
								if (entry.getValue() != null) {
									if (affiliate.getStatus().equals(AffiliateStatus.PENDING_APPROVAL)
											&& (status.equals(AffiliateStatus.ACTIVE)
													|| status.equals(AffiliateStatus.REJECTED))) {
										mongoUpdate.put(entry.getKey(), status);
									} else if (affiliate.getStatus().equals(AffiliateStatus.REJECTED)
											&& status.equals(AffiliateStatus.ACTIVE)) {
										mongoUpdate.put(entry.getKey(), status);
									} else if (affiliate.getStatus().equals(AffiliateStatus.ACTIVE)
											&& (status.equals(AffiliateStatus.BANNED)
													|| status.equals(AffiliateStatus.DELETED))) {
										mongoUpdate.put(entry.getKey(), status);
									} else if ((affiliate.getStatus().equals(AffiliateStatus.BANNED)
											|| affiliate.getStatus().equals(AffiliateStatus.DELETED))
											&& (status.equals(AffiliateStatus.ACTIVE)
													|| status.equals(AffiliateStatus.DELETED))) {
										mongoUpdate.put(entry.getKey(), status);
									} else {
										mongoUpdate.remove(entry.getKey());
									}
								}
							}
						}
						boolean kycError = false;
						if (mongoUpdate.get("status") != null
								&& mongoUpdate.get("status").equals(AffiliateStatus.ACTIVE)
								&& affiliate.getKycState() != 6)
							kycError = true;
						if (!kycError) {
							if (!mongoUpdate.isEmpty()) {
								AffiliateDao.update(id, mongoUpdate);
								response.setS(true);
								response.setMsg("Affiliate has been successfully updated");
							}
						} else {
							response.setEd("Please update KYC first after that you will change status");
						}
					} else {
						response.setEd("Id is not valid");
					}
				} else {
					response.setEd("Required fields are coming invalid");
				}
			} catch (DuplicateKeyException e) {
				LOG.error(e.getMessage(), e);
				response.setEd("This email is already exist");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id should not be empty");
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.affiliates_delete })
	@GET
	@Path("/delete")
	@ApiOperation(value = "Delete", notes = "Delete Affiliate", response = GenericResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Affiliate Id", dataType = "string", paramType = "query", required = true) })
	public GenericResponse delete(Request request, Response responseO) {
		GenericResponse response = new GenericResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				Affiliate affiliate = AffiliateDao.getById(id);
				if (affiliate != null) {
					AffiliateDao.delete(id);
					response.setS(true);
					response.setMsg("Affiliate has been successfully deleted");
				} else {
					response.setMsg("Id is not valid");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Id should not be empty");
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.affiliates_players_sub_tab })
	@GET
	@Path("/players")
	@ApiOperation(value = "List", notes = "Affiliate Player List", response = AffiliateIndPlayerResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Affiliate Id", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "status", value = "Affiliate Player Status", dataType = "string", paramType = "query", required = true) })
	public AffiliateIndPlayerResponse affiliatePlayers(Request request, Response responseO) {
		AffiliateIndPlayerResponse response = new AffiliateIndPlayerResponse();
		String id = request.getHeader(Urlparams.id);
		String status = request.getHeader(Urlparams.status);
		String searchName = request.getHeader(Urlparams.searchName);
		DecimalFormat df = new DecimalFormat("#.##");
		if (Utils.isNotEmpty(id)) {
			try {
				Player player = null;
				String playerId = null;
				Affiliate affiliate = AffiliateDao.getById(id);
				if (affiliate != null) {
					if (affiliate.getStatus() != null && affiliate.getStatus().equals(AffiliateStatus.ACTIVE)) {
						boolean isError = false;
						if (!isError) {
							List<AffiliatePlayer> affiliatePlayerList = AffiliatePlayerDao.affiliatePlayerGet(id,
									status);
							LOG.info("affiliatePlayerList is " + affiliatePlayerList);
							if (!affiliatePlayerList.isEmpty()) {
								for (AffiliatePlayer affiliatePlayer : affiliatePlayerList) {
									AffiliateIndPlayerDto affiliateIndPlayerDto = new AffiliateIndPlayerDto();
									player = PlayerDao.getById(affiliatePlayer.getPlayerId());
									if (player != null && player.getGameName() != null
											&& player.getGameName().contains(searchName)) {
										playerId = player.getId();
										if (player.getGameName() != null)
											affiliateIndPlayerDto.setGameName(player.getGameName());
										affiliateIndPlayerDto.setStatus(affiliatePlayer.getStatus());
										affiliateIndPlayerDto.setAffiliateStatus(affiliatePlayer.getAffiliateStatus());
										String registered = Utils.utcToIst(affiliatePlayer.getRegistered());
										affiliateIndPlayerDto.setRegistered(registered);
										if (affiliatePlayer.getStatus().equals(PlayerReferralStatus.REJECTED)
												&& affiliatePlayer.getAffiliateStatus()
														.equals(AffiliateReferralStatus.REJECTED)) {
											String leaveDate = Utils.utcToIst(affiliatePlayer.getLastUpdate());
											affiliateIndPlayerDto.setLeaveDate(leaveDate);
										} else
											affiliateIndPlayerDto.setLeaveDate("NA");
										if (player.getPlayerClass() != null)
											affiliateIndPlayerDto.setPlayerClass(player.getPlayerClass());
										affiliateIndPlayerDto.setId(player.getId());
										if (player.getMobile() != null)
											affiliateIndPlayerDto.setMobile(player.getMobile());
										affiliateIndPlayerDto.setAffiliateEmail(affiliate.isAffiliateEmail());
										affiliateIndPlayerDto.setRealChips(player.getWallet().getCash());
										affiliateIndPlayerDto.setEmail(player.getEmail());
										List<AggregateAffiliatePlayer> AggregateAffiliatePlayerList = AggregateAffiliatePlayerDao
												.listByPlayerId(id, playerId);
										if (!AggregateAffiliatePlayerList.isEmpty()) {
											for (AggregateAffiliatePlayer aggregateAffiliatePlayer : AggregateAffiliatePlayerList) {
												if (aggregateAffiliatePlayer.getRakeGenerated() != null)
													affiliateIndPlayerDto.setRake(Double.parseDouble(
															df.format(aggregateAffiliatePlayer.getRakeGenerated())));
												else
													affiliateIndPlayerDto.setRake(0.0);
												if (aggregateAffiliatePlayer.getVipPointsIssued() != null)
													affiliateIndPlayerDto
															.setVipPoints(affiliateIndPlayerDto.getVipPoints()
																	+ aggregateAffiliatePlayer.getVipPointsIssued());
												if (aggregateAffiliatePlayer.getWagered() != null)
													affiliateIndPlayerDto.setWagered(affiliateIndPlayerDto.getWagered()
															+ aggregateAffiliatePlayer.getWagered());
												if (aggregateAffiliatePlayer.getGamePlayed() != null)
													affiliateIndPlayerDto
															.setGamePlayed(affiliateIndPlayerDto.getGamePlayed()
																	+ aggregateAffiliatePlayer.getGamePlayed());
											}
										}
										response.getAffiliateIndPlayerDtoList().add(affiliateIndPlayerDto);
									}
								}
								response.setS(true);
							} else {
								response.setMsg("data not found");
							}
						}
					} else {
						response.setEd("Affiliate status should be ACTIVE");
						response.setAffiliateIndPlayerDtoList(null);
					}
				} else {
					response.setEd("Id is not valid");
					response.setAffiliateIndPlayerDtoList(null);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
				response.setAffiliateIndPlayerDtoList(null);
			}
		} else {
			response.setEd("Id should not be empty");
			response.setAffiliateIndPlayerDtoList(null);
		}
		return response;
	}

	@WSEPermission(pl = { UserPermission.affiliates_view })
	@GET
	@Path("/player/approve")
	@ApiOperation(value = "Approve Player", notes = "Affiliate Approve Player Status", response = GenericResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "playerId", value = "Player Id", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "affiliateId", value = "Affiliate Id", dataType = "string", paramType = "query", required = true),
			@ApiImplicitParam(name = "status", value = "Player Status", dataType = "string", paramType = "query", required = true) })
	public GenericResponse approvePlayer(Request request, Response responseO) {
		GenericResponse response = new GenericResponse();
		try {
			String playerId = request.getHeader(Urlparams.playerId);
			String status = request.getHeader(Urlparams.status);
			String affiliateId = request.getHeader(Urlparams.id);
			boolean isError = false;
			if (Utils.isNotEmpty(playerId) && status != null) {
				PlayerReferralStatus playerStatus = null;
				try {
					playerStatus = PlayerReferralStatus.valueOf(status);
				} catch (IllegalArgumentException e) {
					LOG.error(e.getMessage(), e);
					response.setEd("Status " + request.getHeader(Urlparams.status) + " is not valid");
					isError = true;
				}
				if (!isError) {
					Date date = new Date();
					AffiliatePlayerDao.updatePlayerStatus(playerId, playerStatus, affiliateId, date);
					Affiliate affiliate = AffiliateDao.getById(affiliateId);
					if (affiliate != null) {
						int count = affiliate.getPlayers();
						if (playerStatus.equals(PlayerReferralStatus.APPROVED)) {
							count++;
							PlayerDao.updateAffiliateId(playerId, affiliateId, playerStatus);
							AffiliateDao.increasePlayerCount(affiliateId, count);
							AnalyticsManager.affiliateUser(affiliateId);
						} else if (playerStatus.equals(PlayerReferralStatus.REJECTED) && count > 0) {
							count--;
							PlayerDao.updateAffiliateId(playerId, affiliateId, playerStatus);
							AffiliateDao.increasePlayerCount(affiliateId, count);
							AnalyticsManager.affiliateUserDecrement(affiliateId);
						}
					}
					response.setS(true);
					response.setMsg("Player status has been successfully updated");
				}
			} else {
				response.setEd("Required field are coming invalid");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	// affiliate player export csv

	@WSEPermission(pl = { UserPermission.affiliate_config_export_csv })
	@GET
	@Path("/affiliateConfig")
	@ApiOperation(value = "config", notes = "Affiliate Player Config", response = AffiliatePlayerConfigResponse.class, httpMethod = "get")
	public AffiliatePlayerConfigResponse affiliatePlayerConfig(Request request, Response responseO) {
		AffiliatePlayerConfigResponse response = new AffiliatePlayerConfigResponse();
		try {
			AffiliatePlayerExportCsv affiliatePlayerExportCsv = InMemory.affiliatePlayerExportCsv;
			for (String key : affiliatePlayerExportCsv.getAffiliatePlayerMeasures().keySet()) {
				if (key.equals("Game Name")) {
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				} else if (key.equals("Vip Points")) {
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				} else if (key.equals("Rake Generated")) {
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				} else if (key.equals("Game Played")) {
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				} else if (key.equals("Wagered")) {
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				} else if (key.equals("Registered Date")) {
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				} else if (key.equals("Leave Date")) { 
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				} else if (key.equals("Player Email")) {
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				} else if (key.equals("Mobile")) {
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				} else if (key.equals("Player Class")) {
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				} else if (key.equals("Real Chips")) {
					response.getAffiliatePlayerMeasure().getMeasures().add(key);
				}
			}
			response.setS(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setEd(e.getMessage());
		}
		return response;
	}

	@GET
	@Path("/get/otp")
	@ApiOperation(value = "Send User OTP", notes = "Send OTP", response = AffiliateOtpResponse.class, httpMethod = "get")
	public AffiliateOtpResponse getAffiliatePlayerOtp(@ApiParam(hidden = true) Request request,
			@ApiParam(hidden = true) Response response0) {
		AffiliateOtpResponse response = new AffiliateOtpResponse();
		if (request != null) {
			try {
				@SuppressWarnings("unchecked")
				Optional<UserDepartment> optUserDepartment = (Optional<UserDepartment>) request.getExt()
						.get(Constants.USER_KEY);
				UserDepartment userDepartment = optUserDepartment.get();
				User user = UserDao.getById(userDepartment.getUser().getId());
				if (user != null) {
					String otp = com.actolap.wse.Utils.generateOTP();
					String otpResponse = LyveSendSms.setOtp(user.getMobile(), otp);
					String[] responseParts = otpResponse.split("\\|");
					String responseCode = responseParts[0];
					if (responseCode.trim().equals("success")) {
						String transactionId = responseParts[2].trim();
						OneTimePassword oneTimePassword = new OneTimePassword();
						oneTimePassword.setOtp(otp);
						oneTimePassword.setMobile(user.getMobile());
						oneTimePassword.setTransactionId(transactionId);
						oneTimePassword.setExpireDate(new Date());
						OneTimePasswordDao.persist(oneTimePassword);
						response.setD(transactionId);
						response.setMobile(user.getMobile());
						response.setMsg("We have send an OTP to your mobile " + user.getMobile());
						response.setS(true);
					}
				} else {
					response.setEd("Looks like you are not registered with us");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setEd(e.getMessage());
			}
		} else {
			response.setEd("Required fields are coming invalid");
		}
		return response;
	}

	@POST
	@Path("/validate/otp")
	@ApiOperation(value = "Validate", notes = "Otp Validate", response = GenericResponse.class, httpMethod = "post")
	public GenericResponse affiliatePlayerValidateOtpDoc(@ApiParam(required = true) OTPRequest otpRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response) {
		GenericResponse otpResponse = new GenericResponse();
		try {
			if (otpRequest != null && Utils.isNotEmpty(otpRequest.getOtp())
					&& Utils.isNotEmpty(otpRequest.getTransactionId())) {
				OneTimePassword otp = OneTimePasswordDao.getByTransactionId(otpRequest.getTransactionId(),
						otpRequest.getOtp());
				if (otp != null) {
					otpResponse.setS(true);
				} else {
					otpResponse.setEd("Invalid OTP, please enter a valid OTP.");
				}
			} else {
				otpResponse.setEd("Required fields are coming invalid");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			otpResponse.setEd(e.getMessage());
		}
		return otpResponse;
	}

	@WSEPermission(pl = { UserPermission.affiliate_export_csv_file })
	@POST
	@Path("/export/csv")
	@ApiOperation(value = "Get", notes = "Get Affiliate player Details By Id", response = AffiliatePlayerResponse.class, httpMethod = "post")
	public AffiliatePlayerResponse getAffiliatePlayerExportCsvDoc(
			@ApiParam(required = true) AffiliatePlayerRequest affiliatePlayerRequest,
			@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response0) {
		AffiliatePlayerResponse affiliatePlayerResponse = new AffiliatePlayerResponse();
		if (affiliatePlayerRequest != null) {
			try {
				if (true) {
					buildReport(affiliatePlayerResponse, affiliatePlayerRequest.getDimensions(),
							affiliatePlayerRequest.getAffiliateId());
					if (affiliatePlayerResponse.getRows().isEmpty()) {
						affiliatePlayerResponse.setMsg("No affiliate player details found");
					}
					affiliatePlayerResponse.setS(true);
				} else {
					affiliatePlayerResponse.setEd("Id is not valid");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				affiliatePlayerResponse.setEd(e.getMessage());
			}
		} else {
			affiliatePlayerResponse.setEd("Id should not be empty");
		}
		return affiliatePlayerResponse;
	}

	public static void buildReport(AffiliatePlayerResponse affiliatePlayerResponse, List<String> dimensions,
			String affiliateId) throws ParseException {
		List<String> col = new ArrayList<String>();
		col.addAll(dimensions);
		List<AffiliateIndPlayerDto> affiliateIndPlayerDtoList = new ArrayList<AffiliateIndPlayerDto>();
		DecimalFormat df = new DecimalFormat("#.##");
		Player player = null;
		String playerId = null;
		Affiliate affiliate = AffiliateDao.getById(affiliateId);
		if (affiliate != null) {
			if (affiliate.getStatus() != null && affiliate.getStatus().equals(AffiliateStatus.ACTIVE)) {
				List<AffiliatePlayer> affiliatePlayerList = AffiliatePlayerDao.affiliatePlayerGetById(affiliateId);
				LOG.info("affiliatePlayerList is " + affiliatePlayerList);
				if (!affiliatePlayerList.isEmpty()) {
					for (AffiliatePlayer affiliatePlayer : affiliatePlayerList) {
						AffiliateIndPlayerDto affiliateIndPlayerDto = new AffiliateIndPlayerDto();
						player = PlayerDao.getById(affiliatePlayer.getPlayerId());
						if (player != null && player.getGameName() != null) {
							playerId = player.getId();
							if (player.getGameName() != null)
								affiliateIndPlayerDto.setGameName(player.getGameName());
							affiliateIndPlayerDto.setStatus(affiliatePlayer.getStatus());
							affiliateIndPlayerDto.setAffiliateStatus(affiliatePlayer.getAffiliateStatus());
							String registered = Utils.utcToIst(affiliatePlayer.getRegistered());
							affiliateIndPlayerDto.setRegistered(registered);
							if (affiliatePlayer.getStatus().equals(PlayerReferralStatus.REJECTED)
									&& affiliatePlayer.getAffiliateStatus().equals(AffiliateReferralStatus.REJECTED)) {
								String leaveDate = Utils.utcToIst(affiliatePlayer.getLastUpdate());
								affiliateIndPlayerDto.setLeaveDate(leaveDate);
							} else
								affiliateIndPlayerDto.setLeaveDate("NA");
							if (player.getPlayerClass() != null)
								affiliateIndPlayerDto.setPlayerClass(player.getPlayerClass());
							affiliateIndPlayerDto.setId(player.getId());
							if (player.getMobile() != null)
								affiliateIndPlayerDto.setMobile(player.getMobile());
							affiliateIndPlayerDto.setAffiliateEmail(affiliate.isAffiliateEmail());
							affiliateIndPlayerDto.setRealChips(player.getWallet().getCash());
							affiliateIndPlayerDto.setEmail(player.getEmail());
							List<AggregateAffiliatePlayer> AggregateAffiliatePlayerList = AggregateAffiliatePlayerDao
									.listByPlayerId(affiliateId, playerId);
							if (!AggregateAffiliatePlayerList.isEmpty()) {
								for (AggregateAffiliatePlayer aggregateAffiliatePlayer : AggregateAffiliatePlayerList) {
									if (aggregateAffiliatePlayer.getRakeGenerated() != null)
										affiliateIndPlayerDto.setRake(Double
												.parseDouble(df.format(aggregateAffiliatePlayer.getRakeGenerated())));
									else
										affiliateIndPlayerDto.setRake(0.0);
									if (aggregateAffiliatePlayer.getVipPointsIssued() != null)
										affiliateIndPlayerDto.setVipPoints(affiliateIndPlayerDto.getVipPoints()
												+ aggregateAffiliatePlayer.getVipPointsIssued());
									if (aggregateAffiliatePlayer.getWagered() != null)
										affiliateIndPlayerDto.setWagered(affiliateIndPlayerDto.getWagered()
												+ aggregateAffiliatePlayer.getWagered());
									if (aggregateAffiliatePlayer.getGamePlayed() != null)
										affiliateIndPlayerDto.setGamePlayed(affiliateIndPlayerDto.getGamePlayed()
												+ aggregateAffiliatePlayer.getGamePlayed());
								}
							}
							affiliateIndPlayerDtoList.add(affiliateIndPlayerDto);
						}
					}
				}
			}
		}
		List<String> dimension = new ArrayList<String>();
		dimension.addAll(dimensions);
		for (AffiliateIndPlayerDto affiliateIndPlayerDto1 : affiliateIndPlayerDtoList) {
			List<Object> values = new ArrayList<Object>();
			for (String key : dimension) {
				AffiliatePlayerMatric affiliatePlayerMatric = InMemory.affiliatePlayerExportCsv
						.getAffiliatePlayerMeasures().get(key);
				if (affiliatePlayerMatric != null)
					values.add(getDimensionLabel(affiliatePlayerMatric, affiliateIndPlayerDto1));
			}
			affiliatePlayerResponse.getRows().add(values);
		}
		affiliatePlayerResponse.setColumns(col);
		affiliatePlayerResponse.setRc(ResponseCode.SUCCESS);
	}

	@SuppressWarnings("incomplete-switch")
	public static String getDimensionLabel(AffiliatePlayerMatric affiliatePlayerMatric,
			AffiliateIndPlayerDto affiliateIndPlayerDto) {
		String label = "NA";
		switch (affiliatePlayerMatric) {
		case gameName:
			if (affiliateIndPlayerDto.getGameName() != null)
				label = affiliateIndPlayerDto.getGameName();
			break;
		case mobile:
			label = Long.toString(affiliateIndPlayerDto.getMobile());
			break;
		case registered:
			if (affiliateIndPlayerDto.getRegistered() != null)
				label = affiliateIndPlayerDto.getRegistered();
			break;
		case gamePlayed:
			label = Long.toString(affiliateIndPlayerDto.getGamePlayed());
			break;
		case vipPoints:
			label = Double.toString(affiliateIndPlayerDto.getVipPoints());
			break;
		case wagered:
			label = Long.toString(affiliateIndPlayerDto.getWagered());
			break;
		case rake:
			label = Double.toString(affiliateIndPlayerDto.getRake());
			break;
		case email:
			if (affiliateIndPlayerDto.getEmail() != null)
				label = affiliateIndPlayerDto.getEmail().toString();
			break;
		/*case firstName:
			if (affiliateIndPlayerDto.getFirstName() != null)
				label = affiliateIndPlayerDto.getFirstName();
			break;
		case lastName:
			if (affiliateIndPlayerDto.getLastName() != null)
				label = affiliateIndPlayerDto.getLastName();
			break;*/
		case leaveDate:
			if (affiliateIndPlayerDto.getLeaveDate() != null)
				label = affiliateIndPlayerDto.getLeaveDate();
			break;
		case playerClass:
			if (affiliateIndPlayerDto.getPlayerClass() != null)
				label = affiliateIndPlayerDto.getPlayerClass().toString();
			break;
		case realChips:
			label = Long.toString(affiliateIndPlayerDto.getRealChips());
			break;
		}
		return label;
	}
}

