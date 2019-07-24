package com.actolap.wse.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actolap.wse.dao.QuizDao;
import com.actolap.wse.manager.AnalyticsManager;
import com.actolap.wse.model.elearning.Quiz;
import com.actolap.wse.model.elearning.Quiz.AnswerStatus;

public class QuizScheduler {

	public static Logger logger = LoggerFactory.getLogger(QuizScheduler.class);

	private static ScheduledExecutorService quizScheduler;

	public static void init() {
		if (quizScheduler == null) {
			quizScheduler = Executors.newScheduledThreadPool(1);
			quizScheduler.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					try {
						Quiz quiz = QuizDao.getPendingQuiz();
						if (quiz != null) {
							Boolean completed = null;
							if (quiz.getCurrentQuestionIndex() == (quiz.getQuestions().size() - 1)) {
								completed = true;
							}
							float score = 0;
							if (completed != null && completed) {
								if (!quiz.getQuestions().isEmpty()) {
									score = (((float) quiz.getCorrectAnswerCount() / (float) quiz.getQuestions().size())
											* 100);
								}
								QuizDao.updateQuestion(quiz.getId(), null, null, null, null, completed,
										AnswerStatus.ATTEMPTED, score);
								AnalyticsManager.quizCompleted(quiz.getLevel(), quiz.getPlayerId());
							}

						}
					} catch (Exception exp) {
						logger.error(exp.getMessage(), exp);
					}

				}
			}, 0, 10, TimeUnit.SECONDS);
			logger.info("Quiz Scheduler started to run for every " + 10 + " Seconds");
		}

	}

	public static void destroy() {
		try {
			quizScheduler.shutdownNow();
		} catch (Exception e) {

		}

	}

}

