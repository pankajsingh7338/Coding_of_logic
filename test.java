   // For exporting csv of data


@RequestMapping(value = "ajx/marketing/report/export/csv", method = RequestMethod.GET)
	@ResponseBody
	public void reportCSVById(HttpServletResponse response, HttpServletRequest request, SessionStatus status,
			@RequestParam("id") String id, @ModelAttribute("session") SessionWrapper session) {
		ObjectListResponse feResponse = new ObjectListResponse();
		try {
			MarketingReportResponse beResponse = ApiManager.getMarketingReportExportCsv(id,
					FeUtils.createAPIMeta(session, request));
			if (beResponse != null) {
				if (FeUtils.handleRepsonse(beResponse, request, status, response)) {
					if (beResponse.isS()) {
						response.setContentType("text/csv");
						String reportName = "marketingPlayerDetails("
								+ new SimpleDateFormat("dd-MMMM-yy").format(new Date()) + ").csv";
						response.setHeader("Content-disposition", "attachment;filename=" + reportName);
						ArrayList<String> rows = new ArrayList<String>();
						rows.add(Joiner.on(",").join(beResponse.getColumns()));
						rows.add("\n");
						for (List<Object> data : beResponse.getRows()) {
							List<Object> dataList = new ArrayList<Object>();
							for (Object datas : data) {
								dataList.add(datas.toString().replaceAll(",", " "));
							}
							rows.add(Joiner.on(",").join(dataList));
							rows.add("\n");
						}
						Iterator<String> iter = rows.iterator();
						while (iter.hasNext()) {
							String outputString = (String) iter.next();
							response.getOutputStream().print(outputString);
						}
						response.getOutputStream().flush();
					} else {
						feResponse.setErrorDetails(beResponse.getEd());
						feResponse.setStatus(Constants.FAILED);
					}
				}
			} else {
				feResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING);
				feResponse.setStatus(Constants.NOT_RESPONEDING);
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			feResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG);
			feResponse.setStatus(Constants.FAILED);
		}
	}


@WSEPermission(pl = { UserPermission.marketing_export_csv_file })
	@GET
	@Path("/export/csvFile")
	@ApiOperation(value = "Get", notes = "Get Report Details By Id", response = MarketingReportResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Marketing Report Id", dataType = "string", paramType = "query", required = true) })
	public MarketingReportResponse exportCsvFile(Request request, Response responseO) {
		MarketingReportResponse reportResponse = new MarketingReportResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				MarketingReport marketingReport = MarketingReportDao.getById(id);
				if (marketingReport != null) {
					if (marketingReport.getType().equals("PLAYER"))
						buildReport(reportResponse, marketingReport.getDimentions(), marketingReport);
					else
						buildAffiliateReport(reportResponse, marketingReport.getDimentions(), marketingReport);
					if (reportResponse.getRows().isEmpty()) {
						reportResponse.setMsg("No marketing report details found"); 
					}
					reportResponse.setS(true);
				} else {
					reportResponse.setEd("Id is not valid");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				reportResponse.setEd(e.getMessage());
			}
		} else {
			reportResponse.setEd("Id should not be empty");
		}
		return reportResponse;
	}



@WSEPermission(pl = { UserPermission.marketing_get_report_details })
	@GET
	@Path("/get/details")
	@ApiOperation(value = "Get", notes = "Get Report Details By Id", response = MarketingReportResponse.class, httpMethod = "get")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Marketing Report Id", dataType = "string", paramType = "query", required = true) })
	public MarketingReportResponse getMarketingReportDetails(Request request, Response responseO) {
		MarketingReportResponse reportResponse = new MarketingReportResponse();
		String id = request.getHeader(Urlparams.id);
		if (Utils.isNotEmpty(id)) {
			try {
				MarketingReport marketingReport = MarketingReportDao.getById(id);
				if (marketingReport != null) { 
					if (marketingReport.getType().equals("PLAYER")) 
						buildReport(reportResponse, marketingReport.getDimentions(), marketingReport);
					else
						buildAffiliateReport(reportResponse, marketingReport.getDimentions(), marketingReport);
					if (reportResponse.getRows().isEmpty()) {
						reportResponse.setMsg("No report details found");
					} 
					reportResponse.setS(true);
				} else {
					reportResponse.setEd("Id is not valid");
				}

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				reportResponse.setEd(e.getMessage());
			}
		} else {
			reportResponse.setEd("Id should not be empty");
		}
		return reportResponse;
	}


@Path("/report/create")
	@ApiOperation(value = "Create", notes = "Report Create", response = GenericResponse.class, httpMethod = "post")
	public GenericResponse createMarketingDoc(@ApiParam(required = true) MarketingReportRequest marketingReportRequest,
			@ApiParam(hidden = true) Request request0, @ApiParam(hidden = true) Response response) {
		GenericResponse reportResponse = new GenericResponse();
		if (marketingReportRequest != null) {
			try {
				MarketingReport marketingReport = new MarketingReport();
				marketingReport.setTitle(marketingReportRequest.getTitle());
				marketingReport.setType(marketingReportRequest.getType());
				marketingReport.setFilters(marketingReportRequest.getFilters());
				marketingReport.setMinValues(Long.parseLong(marketingReportRequest.getMinValues()));
				marketingReport.setMaxValues(Long.parseLong(marketingReportRequest.getMaxValues()));
				marketingReport.setDimentions(marketingReportRequest.getDimentions());
				marketingReport.setValue(marketingReportRequest.getValue()); 
				marketingReport.setStartDate(marketingReportRequest.getStartDate());
				marketingReport.setEndDate(marketingReportRequest.getEndDate());
				MarketingReportDao.persist(marketingReport);
				reportResponse.setD(marketingReport.getId());
				reportResponse.setS(true);
				reportResponse.setMsg("Marketing Report is Created");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				reportResponse.setEd(e.getMessage()); 
			}
		} else {
			reportResponse.setEd("Required feild are coming invalid");
		}
		return reportResponse;
	}


public static void persist(MarketingReport marketingReport) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(marketingReport); 
	}
	
	public static MarketingReport getById(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<MarketingReport> query = datastore.createQuery(MarketingReport.class).field("id").equal(id);
		return query.get(); 
	}
	
	public static List<MarketingReport> list(String title) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<MarketingReport> query = datastore.createQuery(MarketingReport.class); 
		if (Utils.isNotEmpty(title)) {
			Pattern pattern = Pattern.compile(title, Pattern.CASE_INSENSITIVE);
			query.filter("title", pattern); 
		} 
		return query.asList(); 
	} 
	public static void delete(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<MarketingReport> query = datastore.createQuery(MarketingReport.class).field("id").equal(id);
		datastore.delete(query);

	}
	
	public static void updateMarketingReport(String id, Map<String, Object> updatedReport) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<MarketingReport> query = datastore.createQuery(MarketingReport.class);
		query.field("id").equal(id);
		if (updatedReport.size() > 0) {
			UpdateOperations<MarketingReport> ops = datastore.createUpdateOperations(MarketingReport.class);
			for (String key : updatedReport.keySet()) {
				ops.set(key, updatedReport.get(key));
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops, false);
		}
	}




