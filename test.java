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
