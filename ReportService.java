package com.actolap.wse.common.dashboard.reporting;

import static java.util.Arrays.asList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ReportService {

	private final static Logger logger = LoggerFactory.getLogger(ReportService.class);

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public static ReportingResponse generateReport(ReportRequest reportRequest) {
		ReportingResponse response = new ReportingResponse();
		Stopwatch stopwatch = Stopwatch.createStarted();
		try {
			MongoDatabase db = ConnectionFactory.getInstance().getMongoDatabase();
			String collectionName = getEligibleCollection(reportRequest);
			logger.debug("Selected Collection {}", collectionName);
			Set<String> dates = new HashSet<String>();
			response = buildReport(reportRequest, db, collectionName, dates);
			handleMissingAggregates(response, dates, reportRequest);
			response.setRc(ResponseCode.SUCCESS);
			response.setS(true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.setRc(ResponseCode.FAILED);
			response.setEd(e.getMessage());
		}
		stopwatch.stop();
		response.setCi(stopwatch.elapsed(TimeUnit.MILLISECONDS));
		return response;

	}

	private static void handleMissingAggregates(ReportingResponse response, Set<String> existingdates,
			ReportRequest request) throws ParseException {
		if (request.getDimensions().size() == 1 && request.getDimensions().contains(Dimensions.date)) {
			Date startDate, endDate = null;
			Calendar start, end = null;
			List<String> dates = new ArrayList<String>();
			if (request.isHourly()) {
				startDate = sdf1.parse(request.getDateFilter().getStartDate());
				endDate = sdf1.parse(request.getDateFilter().getEndDate());
				start = Calendar.getInstance();
				start.setTime(startDate);
				end = Calendar.getInstance();
				end.setTime(endDate);
				for (Date date = start.getTime(); start.before(end) || start.equals(end); start
						.add(Calendar.HOUR_OF_DAY, 1), date = start.getTime()) {
					String day = sdf1.format(date);
					dates.add(day);
				}
			} else {
				startDate = sdf.parse(request.getDateFilter().getStartDate());
				endDate = sdf.parse(request.getDateFilter().getEndDate());
				start = Calendar.getInstance();
				start.setTime(startDate);
				end = Calendar.getInstance();
				end.setTime(endDate);
				for (Date date = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.DATE,
						1), date = start.getTime()) {
					String day = sdf.format(date);
					dates.add(day);
				}

			}
			if (dates.size() != response.getResponseItems().size()) {
				fillMiss(request, response, dates, existingdates);
			}

			sortResultByDate(request.isHourly(), response);
		}
	}

	private static void fillMiss(ReportRequest request, ReportingResponse reportingResponse, List<String> dates,
			Set<String> existing) {
		int index = 0;
		for (String date : dates) {
			if (!existing.contains(date)) {
				ResponseItem responseItem = new ResponseItem();
				responseItem.getColumns().put(Dimensions.date, date);
				for (Metric measure : request.getMeasures()) {
					responseItem.getMeasures().put(measure, 0);
					if (request.isPrevious())
						responseItem.getMeasurePrev().put(measure, 0);
				}
				reportingResponse.getResponseItems().add(index, responseItem);

			}
			index++;
		}

	}

	private static void sortResultByDate(boolean isHourly, ReportingResponse reportingResponse) {
		if (reportingResponse.getResponseItems().size() > 0) {
			Collections.sort(reportingResponse.getResponseItems(), new Comparator<ResponseItem>() {
				@Override
				public int compare(final ResponseItem object1, final ResponseItem object2) {
					int value = 0;
					try {
						if (!isHourly) {
							value = sdf.parse((String) object1.getColumns().get(Dimensions.date))
									.compareTo(sdf.parse((String) object2.getColumns().get(Dimensions.date)));
						} else {
							value = sdf1.parse((String) object1.getColumns().get(Dimensions.date))
									.compareTo(sdf1.parse((String) object2.getColumns().get(Dimensions.date)));
						}
					} catch (ParseException e) {
						logger.error(e.getMessage());
					}
					return value;
				}
			});
		}
	}

	private static ReportingResponse buildReport(ReportRequest reportRequest, MongoDatabase db, String collectionName,
			Set<String> exisitingDates) throws ParseException {
		final ReportingResponse reportingResponse = new ReportingResponse();
		final List<AggregateItem> prevAgg = new ArrayList<AggregateItem>();
		AggregateItem totalOfAgg = new AggregateItem();
		AggregateItem totalOfPreviousAgg = new AggregateItem();
		MongoCollection<AggregateItem> collection = db.getCollection(collectionName, AggregateItem.class);
		List<Document> lst = buildQuery(reportRequest, null);
		AggregateIterable<AggregateItem> iterable = collection.aggregate(lst);
		if (reportRequest.isPrevious()) {
			DateCondition prevDate = buildPrevDate(reportRequest);
			lst = buildQuery(reportRequest, prevDate);
			AggregateIterable<AggregateItem> prevIterable = collection.aggregate(lst);
			prevIterable.forEach(new Block<AggregateItem>() {
				@Override
				public void apply(final AggregateItem document) {
					prevAgg.add(document);
					totalOfPreviousAgg.buildTotal(document);
				}
			});
		}
		try {
			iterable.forEach(new Block<AggregateItem>() {

				@Override
				public void apply(final AggregateItem document) {
					ResponseItem responseItem = new ResponseItem();
					responseItem.setColumns(document.getColumns());
					for (AggregateItem aggregateItem : prevAgg) {
						if (aggregateItem.getColumns().equals(document.getColumns())) {
							responseItem.setMeasurePrev(buildResponse(reportRequest, aggregateItem));
							break;
						}
					}
					if (reportRequest.isPrevious() && responseItem.getMeasurePrev().isEmpty()) {
						responseItem.setMeasurePrev(buildResponse(reportRequest, new AggregateItem()));
					}

					if (reportRequest.getDimensions().contains(Dimensions.date)
							&& reportRequest.getDimensions().size() == 1)
						exisitingDates.add((String) document.getColumns().get(Dimensions.date));
					responseItem.setMeasures(buildResponse(reportRequest, document));
					totalOfAgg.buildTotal(document);
					reportingResponse.getResponseItems().add(responseItem);
				}
			});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			reportingResponse.setEd(e.getMessage());
		}
		if (reportRequest.isTotals()) {
			buildResponseTotal(reportingResponse, reportRequest, totalOfAgg, totalOfPreviousAgg);
		}
		return reportingResponse;
	}

	private static void buildResponseTotal(ReportingResponse response, ReportRequest request, AggregateItem totalOfAgg,
			AggregateItem totalOfPreviousAgg) {
		ResponseTotal responseTotal = new ResponseTotal();
		responseTotal.setMeasures(buildResponse(request, totalOfAgg));
		responseTotal.setMeasuresPrev(buildResponse(request, totalOfPreviousAgg));
		response.setResponseTotals(responseTotal);
	}

	private static DateCondition buildPrevDate(ReportRequest requestData) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		DateCondition buildPrevDate = new DateCondition();
		Date sDate = null;
		Date eDate = null;
		if (requestData.isHourly()) {
			try {
				sDate = sdf1.parse(requestData.getDateFilter().getStartDate());
				eDate = sdf1.parse(requestData.getDateFilter().getEndDate());
				// set the start date
				calendar.setTime(sDate);
				// minus 24 hours
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				buildPrevDate.setStartDate(sdf1.format(calendar.getTime()));
				// set the end date
				calendar.setTime(eDate);
				// minus 24 hours
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				buildPrevDate.setEndDate(sdf1.format(calendar.getTime()));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			try {
				sDate = sdf.parse(requestData.getDateFilter().getStartDate());
				eDate = sdf.parse(requestData.getDateFilter().getEndDate());

				// get different in days
				Long diffindays = (eDate.getTime() - sDate.getTime()) / 1000 / 60 / 60 / 24;

				calendar.setTime(sDate);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				buildPrevDate.setEndDate(sdf.format(calendar.getTime()));

				calendar.add(Calendar.DAY_OF_MONTH, -(diffindays.intValue()));
				buildPrevDate.setStartDate(sdf.format(calendar.getTime()));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return buildPrevDate;
	}

	private static Map<Metric, Object> buildResponse(final ReportRequest reportRequest, final AggregateItem document) {
		Map<Metric, Object> measures = new HashMap<Metric, Object>();
		for (Metric measure : reportRequest.getMeasures()) {
			switch (measure) {
			case moneyDeposit:
				measures.put(measure, document.getMoneyDeposit());
				break;
			case moneyDrawn:
				measures.put(measure, document.getMoneyDrawn());
				break;
			case withdrawCharge:
				measures.put(measure, document.getWithdrawCharge());
				break;
			case vipPointsDeducted:
				measures.put(measure, document.getVipPointsDeducted());
				break;
			case rakeGenerated:
				measures.put(measure, document.getRakeGenerated());
				break;
			case tdsGenerated:
				measures.put(measure, document.getTdsDeducted());
				break;
			case bonusIssued:
				measures.put(measure, document.getBonusIssued());
				break;
			case vipPointsIssued:
				measures.put(measure, document.getVipPointsIssued());
				break;
			case bonusReleased:
				measures.put(measure, document.getBonusReleased());
				break;
			case signUp:
				measures.put(measure, document.getSignUp());
				break;
			case gamePlayed:
				measures.put(measure, document.getGamePlayed());
				break;
			case gamesLost:
				measures.put(measure, document.getGamePlayed() - document.getGamesWon());
				break;
			case totalQuestion:
				measures.put(measure, document.getTotalQuestion());
				break;
			case totalAttemptQuestion:
				measures.put(measure, document.getTotalAttemptQuestion());
				break;
			case quizTaken:
				measures.put(measure, document.getQuizTaken());
				break;
			case correctAnswer:
				measures.put(measure, document.getCorrectAnswer());
				break;
			case incorrectAnswer:
				measures.put(measure, document.getIncorrectAnswer());
				break;
			case completedQuiz:
				measures.put(measure, document.getCompletedQuiz());
				break;
			case tournamentColl:
				measures.put(measure, document.getTournamentColl());
				break;
			case totalSpent:
				measures.put(measure, document.getTournamentColl() + document.getWagered());
				break;
			case affiliateUser:
				measures.put(measure, document.getAffiliateUser());
				break;
			case tournamentsParticipated:
				measures.put(measure, document.getTournamentsParticipated());
				break;
			case tournamentLost:
				measures.put(measure, document.getTournamentLost());
				break;
			case tournamentWon:
				measures.put(measure, document.getTournamentWon());
				break;
			case rakeRefunded:
				measures.put(measure, document.getRakeRefunded());
				break;
			case tdsRefunded:
				measures.put(measure, document.getTdsRefunded());
				break;
			case discountReceived:
				measures.put(measure, document.getDiscountReceived());
				break;
			case wagered:
				measures.put(measure, document.getWagered());
				break;
			case net:
				measures.put(measure, document.getNet());
				break;
			case won:
				measures.put(measure, document.getWon());
				break;
			case bonusChipsEncashed:
				measures.put(measure, document.getBonusChipsEncashed());
				break;
			case vipPointsEncashed:
				measures.put(measure, document.getVipPointsEncashed());
				break;
			case gamesWon:
				measures.put(measure, document.getGamesWon());
				break;
			case playedTime:
				measures.put(measure, document.getPlayedTime());
				break;
			case commission:
				measures.put(measure, document.getCommission());
				break;
			case tdsDeducted:
				measures.put(measure, document.getTdsDeducted());
				break;
			default:
				break;
			}
		}
		return measures;
	}

	private static List<Document> buildQuery(ReportRequest reportRequest, DateCondition prevDateFilter) {
		Document mrCondition = new Document();
		if (prevDateFilter == null) {
			prevDateFilter = reportRequest.getDateFilter();
		}
		buildConditions(reportRequest.getConditions(), prevDateFilter, mrCondition, reportRequest.getDimensions());
		Document match = new Document("$match", mrCondition);
		Document projection = new Document("$project", buildProjection(reportRequest.getDimensions(), reportRequest));
		Document group = buildGroup(reportRequest.getDimensions(), reportRequest.getMeasures());
		Document sort = null;
		if (Utils.isNotEmptyNA(reportRequest.getSort())) {
			if (Metric.valueOf(reportRequest.getSort()) != null) {
				sort = new Document("$sort", reportRequest.getSort());
			}
		}
		logger.debug("Match:" + match.toString());
		logger.debug("Project:" + projection.toString());
		logger.debug("Group:" + group.toString());
		List<Document> lst = null;
		lst = asList(match, projection, group);
		if (reportRequest.getLimit() != null)
			lst.add(new Document("$limit", reportRequest.getLimit()));
		if (sort != null)
			lst.add(sort);
		return lst;
	}

	private static Document buildProjection(Set<Dimensions> dimensions, ReportRequest requst) {
		Document fields = new Document();
		for (Dimensions dimension : dimensions) {
			fields.append(dimension.toString(), 1);
		}
		for (Metric measure : requst.getMeasures()) {
			if (measure == Metric.tdsGenerated) {
				fields.append(Metric.tdsDeducted.toString(), 1);
			} else if (measure == Metric.gamesLost) {
				fields.append(Metric.gamePlayed.toString(), 1);
				fields.append(Metric.gamesWon.toString(), 1);
			} else {
				fields.append(measure.toString(), 1);
			}

		}
		return fields;
	}

	private static Document buildGroup(Set<Dimensions> dimensions, Set<Metric> measures) {
		Document dbObjIdMap = new Document();
		for (Dimensions dimension : dimensions) {
			dbObjIdMap.append(dimension.toString(), "$" + dimension.toString());
		}

		Document groupFields = new Document("_id", dbObjIdMap);
		for (Metric measure : measures) {
			if (measure == Metric.tdsGenerated) {
				groupFields.append(Metric.tdsDeducted.toString(),
						new Document("$sum", "$" + Metric.tdsDeducted.toString()));
			} else if (measure == Metric.gamesLost) {
				groupFields.append(Metric.gamePlayed.toString(),
						new Document("$sum", "$" + Metric.gamePlayed.toString()));
				groupFields.append(Metric.gamesWon.toString(), new Document("$sum", "$" + Metric.gamesWon.toString()));
			} else {
				groupFields.append(measure.toString(), new Document("$sum", "$" + measure.toString()));
			}
		}
		Document group = new Document("$group", groupFields);

		return group;
	}

	private static void buildConditions(Set<Condition> conditions, DateCondition dateCondition, Document mrCondition,
			Set<Dimensions> dimensions) {
		for (Condition condition : conditions) {
			switch (condition.getOperation()) {
			case in:
				mrCondition.append(condition.getDimension().toString(), new Document("$in", condition.getValue()));
				break;
			case equal:
				mrCondition.append(condition.getDimension().toString(), condition.getValue());
				break;
			case notin:
				mrCondition.append(condition.getDimension().toString(), new Document("$nin", condition.getValue()));
				break;
			case notequal:
				mrCondition.append(condition.getDimension().toString(), new Document("$ne", condition.getValue()));
				break;
			default:
				break;
			}
		}
		if (dimensions.contains(Dimensions.gameType)) {
			mrCondition.append(Dimensions.gameType.toString(), new Document("$ne", null));
		}
		if (dateCondition != null) {
			BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
			if (dateCondition.getStartDate() != null) {
				builder = BasicDBObjectBuilder.start("$gte", dateCondition.getStartDate());
			}
			if (dateCondition.getEndDate() != null) {
				builder.add("$lte", dateCondition.getEndDate());
			}
			mrCondition.put("date", builder.get());
		}
	}

	private static String getEligibleCollection(ReportRequest reportRequest) {
		String collName = "AggregateDay";
		if (reportRequest.getReportType() != null && reportRequest.getReportType().equals(ReportType.SUMMARY)
				&& reportRequest.getDimensions().contains(Dimensions.gameType)) {
			collName = "AggregateGameTypeDay";
		}
		if (reportRequest.getReportType() != null && reportRequest.getReportType().equals(ReportType.SUMMARY)
				&& reportRequest.getDimensions().contains(Dimensions.gameType) && reportRequest.isHourly()) {
			collName = "AggregateGameTypeHour";
		}
		if (reportRequest.getReportType() != null && reportRequest.getReportType().equals(ReportType.SUMMARY)
				&& reportRequest.getDimensions().contains(Dimensions.gameName)) {
			collName = "AggregatePlayer";
		} else if (reportRequest.getReportType() != null && reportRequest.getReportType().equals(ReportType.PLAYER)) {
			collName = "AggregatePlayer";
		} else if (reportRequest.getReportType() == null && reportRequest.isHourly()) {
			collName = "AggregateHour";
		} else if (reportRequest.getReportType() != null && reportRequest.getReportType().equals(ReportType.QUIZ)
				&& !reportRequest.isHourly()) {
			collName = "AggregateQuiz";
		} else if (reportRequest.getReportType() != null && reportRequest.getReportType().equals(ReportType.QUIZ)
				&& reportRequest.isHourly()) {
			collName = "AggregateQuizHour";
		} else if (reportRequest.getReportType() != null && reportRequest.getReportType().equals(ReportType.AFFILIATE)
				&& !reportRequest.isHourly() && !reportRequest.getDimensions().contains(Dimensions.playerId)) {
			collName = "AggregateAffiliate";
		} else if (reportRequest.getReportType() != null && reportRequest.getReportType().equals(ReportType.AFFILIATE)
				&& reportRequest.isHourly() && !reportRequest.getDimensions().contains(Dimensions.playerId)) {
			collName = "AggregateAffiliateHour";
		} else if (reportRequest.getReportType() != null && reportRequest.getReportType().equals(ReportType.AFFILIATE)
				&& reportRequest.getDimensions().contains(Dimensions.playerId)) {
			collName = "AggregateAffiliatePlayer";
		}
		return collName;
	}

	public static void main(String[] args) {
		ReportRequest request = new ReportRequest();
		request.setMeasures(new HashSet<Metric>(Arrays.asList(Metric.moneyDrawn, Metric.gamePlayed)));
		request.setDimensions(new HashSet<Dimensions>(Arrays.asList(Dimensions.date)));
		DateCondition dateCondition = new DateCondition();
		dateCondition.setStartDate("2017-03-20");
		dateCondition.setEndDate("2017-04-10");
		request.setDateFilter(dateCondition);
		request.setTotals(true);
		request.setPrevious(true);
		// request.setHourly(true);
		Config.MONGOHOST.add("localhost");
		// ReportingResponse response = generateReport(request);
	}
}

