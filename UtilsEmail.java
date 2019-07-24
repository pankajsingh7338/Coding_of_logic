package com.actolap.wse;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.codec.binary.Base64;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.restexpress.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.actolap.config.Config;
import com.actolap.wse.dao.DepartmentDao;
import com.actolap.wse.dao.PermissionDao;
import com.actolap.wse.dao.RolePermissionDao;
import com.actolap.wse.dao.UserDao;
import com.actolap.wse.dto.EmailSenderDto;
import com.actolap.wse.model.ContactUs;
import com.actolap.wse.model.Department;
import com.actolap.wse.model.User;
import com.actolap.wse.model.WithdrawRequest;
import com.actolap.wse.model.backoffice.Permission;
import com.actolap.wse.model.backoffice.RolePermission;
import com.actolap.wse.model.game.poker.Tournament;
import com.actolap.wse.model.player.Category;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.model.player.PlayerClass;
import com.actolap.wse.model.player.PlayerTransaction;
import com.actolap.wse.model.report.DateCondition;
import com.actolap.wse.model.report.Report.DATERANGE;
import com.actolap.wse.model.report.ReportRequest;
import com.actolap.wse.model.type.DateType;
import com.actolap.wse.model.type.SubscribeUs;
import com.actolap.wse.request.CouponCreateRequest;
import com.actolap.wse.request.PromotionCreateRequest;
import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.SendGrid;

public class Utils {
	private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
	private static final String PASSWORD_PATTERN = "((?=.*[A-Z])(?=.*[!@#$%^&*]).{6,})";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static DecimalFormat df = new DecimalFormat("0.00");
	private static final SimpleDateFormat sdformat = new SimpleDateFormat("h.mm a");
	static {
		sdformat.setTimeZone(java.util.TimeZone.getTimeZone("IST"));
	}

	public enum EmailContextType {
		contactUs, customerContactUs, forgetPassword, emailVerify, securePin, customerSecurePin, subscribeUs, redemption, deployment, buyIn, class_upgrade, change_password, reset_passsword, buyIn_failed, success_redemption, failed_redemption, customer_support, tournament_register, customer_digital, customer_care_digital,
	}

	public static String getDateRange(DATERANGE date) {
		String type = "";
		switch (date) {
		case today:
			type = "TODAY";
			break;
		case yesterday:
			type = "YESTERDAY";
			break;
		case last7days:
			type = "LAST 7 DAY";
			break;
		case last14days:
			type = "LAST 14 DAY";
			break;
		case month2date:
			type = "MONTH TO DATE";
			break;
		case lastmonth:
			type = "LAST MONTH";
			break;
		case custom:
			type = "CUSTOM";
			break;
		default:
			break;
		}
		return type;
	}

	public static boolean validateIp(Department department, Request request) {
		boolean secure = true;
		if (department.getIpRestriction().isOn()) {
			if (department.getIpRestriction().getIpAddresses().contains(request.getIpSource())) {
				secure = true;
			} else {
				secure = false;
			}
		}
		return secure;
	}

	public static String getDatedifference(Date date) {
		String difference = "Never";
		if (date != null) {
			try {
				Date currentDate = new Date();
				long diffinMinutes = (currentDate.getTime() - date.getTime()) / (1000 * 60);
				if (diffinMinutes / (24 * 60 * 30 * 12) > 0) {
					long temp = diffinMinutes / (24 * 60 * 30 * 12);
					if (temp < 2) {
						difference = temp + " Year ago";
					} else {
						difference = temp + " Years ago";
					}
				} else if (diffinMinutes / (24 * 60 * 30) > 0) {
					long temp = diffinMinutes / (24 * 60 * 30);
					if (temp < 2) {
						difference = temp + " Month ago";
					} else {
						difference = temp + " Months ago";
					}
				} else if (diffinMinutes / (24 * 60) > 0) {
					long temp = diffinMinutes / (24 * 60);
					if (temp < 2) {
						difference = temp + " Day ago";
					} else {
						difference = temp + " Days ago";
					}
				} else if (diffinMinutes / (60) > 0) {
					long temp = diffinMinutes / (60);
					if (temp < 2) {
						difference = temp + " Hour ago";
					} else {
						difference = temp + " Hours ago";
					}
				} else if (diffinMinutes > 0) {
					if (diffinMinutes < 2) {
						difference = diffinMinutes + " Minute ago";
					} else {
						difference = diffinMinutes + " Minutes ago";
					}
				} else {
					long diffinSeconds = (currentDate.getTime() - date.getTime()) / (1000);
					if (diffinSeconds < 2) {
						difference = diffinSeconds + " Second ago";
					} else {
						difference = diffinSeconds + " Seconds ago";
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return difference; 

	}

	public static String getAfterDateDifference(Date date) {
		String difference = "Never";
		if (date != null) {
			try {
				Date currentDate = new Date();
				long diffinMinutes = (date.getTime() - currentDate.getTime()) / (1000 * 60);
				if (diffinMinutes / (24 * 60 * 30 * 12) > 0) {
					long temp = diffinMinutes / (24 * 60 * 30 * 12);
					if (temp < 2) {
						difference = temp + " Year";
					} else {
						difference = temp + " Years";
					}
				} else if (diffinMinutes / (24 * 60 * 30) > 0) {
					long temp = diffinMinutes / (24 * 60 * 30);
					if (temp < 2) {
						difference = temp + " Month";
					} else {
						difference = temp + " Months";
					}
				} else if (diffinMinutes / (24 * 60) > 0) {
					long temp = diffinMinutes / (24 * 60);
					if (temp < 2) {
						difference = temp + " Day";
					} else {
						difference = temp + " Days";
					}
				} else if (diffinMinutes / (60) > 0) {
					long temp = diffinMinutes / (60);
					if (temp < 2) {
						difference = temp + " Hour";
					} else {
						difference = temp + " Hours";
					}
				} else if (diffinMinutes > 0) {
					if (diffinMinutes < 2) {
						difference = diffinMinutes + " Minute";
					} else {
						difference = diffinMinutes + " Minutes";
					}
				} else {
					long diffinSeconds = (date.getTime() - currentDate.getTime()) / (1000);
					if (diffinSeconds < 2) {
						difference = diffinSeconds + " Second";
					} else {
						difference = diffinSeconds + " Seconds";
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return difference;
	} 
      
	public static int getDifferenceDays(Date d1) {
		long diff = new Date().getTime() - d1.getTime();
		// logger.info("Difference of days" +
		// String.valueOf(TimeUnit.DAYS.convert(diff,
		// TimeUnit.MILLISECONDS)));
		return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public static void getDateRange(ReportRequest reportRequest, String dateType) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String endDate = dateFormat.format(cal.getTime());
		if (dateType.equals(DateType.yesterday.toString())) {
			cal.add(Calendar.DATE, -1);
			endDate = dateFormat.format(cal.getTime());
		} else if (dateType.equals(DateType.last7days.toString())) {
			cal.add(Calendar.DATE, -6);
		} else if (dateType.equals(DateType.last14days.toString())) {
			cal.add(Calendar.DATE, -13);
		} else if (dateType.equals(DateType.month2date.toString())) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
		} else if (dateType.equals(DateType.lastmonth.toString())) {
			cal.add(Calendar.MONTH, -1);
		}
		String startDate = dateFormat.format(cal.getTime());
		if (dateType.equals(DateType.lastmonth.toString())) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
			startDate = dateFormat.format(cal.getTime());
			Calendar calender = Calendar.getInstance();
			calender.add(Calendar.MONTH, -1);
			calender.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endDate = dateFormat.format(calender.getTime());
		} 
		// LOG.info(startDate + " to" + endDate);
		DateCondition dateFilter = new DateCondition(startDate, endDate);
		reportRequest.setDateFilter(dateFilter);
	}

	/*
	 * public static String setOtp(String mobile, String otp) { String difference =
	 * "failed"; try { String message =
	 * " Your OTP for password reset request at LYVE Poker is " + otp +
	 * ". Call us at 09051222225 for any assistance."; String data = ""; data +=
	 * "method=sendMessage"; data += "&userid=" + Config.OTPLoginId; // your loginId
	 * data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); //
	 * your // password data += "&msg=" + URLEncoder.encode("Dear customer," +
	 * message, "UTF-8"); data += "&send_to=" + URLEncoder.encode("91" + mobile,
	 * "UTF-8"); // a // valid // 10 // digit // phone // no. data += "&v=1.1"; data
	 * += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or // “BINARY” data
	 * += "&auth_scheme=PLAIN"; URL url = new
	 * URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String setOtpForChipConversion(String mobile, String otp,
	 * String amount) { String difference = "failed"; try { String message =
	 * " Your OTP for chip conversion of " + amount + "VIP points to real chips is "
	 * + otp + ". Call us at 09051222225 for any assistance."; String data = "";
	 * data += "method=sendMessage"; data += "&userid=" + Config.OTPLoginId; // your
	 * loginId data += "&password=" + URLEncoder.encode(Config.OTPPassword,
	 * "UTF-8"); // your // password data += "&msg=" +
	 * URLEncoder.encode("Dear customer," + message, "UTF-8"); data += "&send_to=" +
	 * URLEncoder.encode("91" + mobile, "UTF-8"); // a // valid // 10 // digit //
	 * phone // no. data += "&v=1.1"; data += "&msg_type=TEXT"; // Can by "FLASH" or
	 * "UNICODE_TEXT" or // “BINARY” data += "&auth_scheme=PLAIN"; URL url = new
	 * URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String setOtpForWithdraw(String mobile, String otp) { String
	 * difference = "failed"; try { String message =
	 * " Your OTP for withdrawal request at LYVE Poker is " + otp +
	 * ". Call us at 09051222225 for any assistance."; String data = ""; data +=
	 * "method=sendMessage"; data += "&userid=" + Config.OTPLoginId; // your loginId
	 * data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); //
	 * your // password data += "&msg=" + URLEncoder.encode("Dear customer," +
	 * message, "UTF-8"); data += "&send_to=" + URLEncoder.encode("91" + mobile,
	 * "UTF-8"); // a // valid // 10 // digit // phone // no. data += "&v=1.1"; data
	 * += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or // “BINARY” data
	 * += "&auth_scheme=PLAIN"; URL url = new
	 * URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String setOtpForSignUp(String mobile, String otp) { String
	 * difference = "failed"; try { String message =
	 * " Your OTP for Sign Up at LYVE Poker is " + otp +
	 * ". Call us at 09051222225 for any assistance."; String data = ""; data +=
	 * "method=sendMessage"; data += "&userid=" + Config.OTPLoginId; // your loginId
	 * data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); //
	 * your // password data += "&msg=" + URLEncoder.encode("Dear customer," +
	 * message, "UTF-8"); data += "&send_to=" + URLEncoder.encode("91" + mobile,
	 * "UTF-8"); // a // valid // 10 // digit // phone // no. data += "&v=1.1"; data
	 * += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or // “BINARY” data
	 * += "&auth_scheme=PLAIN"; URL url = new
	 * URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String sendSignUpSms(String mobile) { String difference =
	 * "failed"; try { String message =
	 * " Thank you for signing up at LYVE Games. Please verify your account by completing the email verification link"
	 * ; String data = ""; data += "method=sendMessage"; data += "&userid=" +
	 * Config.OTPLoginId; // your loginId data += "&password=" +
	 * URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your // password data +=
	 * "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8"); data +=
	 * "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a // valid // 10
	 * // digit // phone // no. data += "&v=1.1"; data += "&msg_type=TEXT"; // Can
	 * by "FLASH" or "UNICODE_TEXT" or // “BINARY” data += "&auth_scheme=PLAIN"; URL
	 * url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	// sms for uploading documents and linking bank details
	/*
	 * public static String sendSmsForFinecialUpdate(String mobile, String userName,
	 * String email) { String difference = "failed"; try { String message = userName
	 * + " has been uploaded the Documents and Updated the Bank Details with this ("
	 * + email + ")."; String data = ""; data += "method=sendMessage"; data +=
	 * "&userid=" + Config.OTPLoginId; // your loginId data += "&password=" +
	 * URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your // password data +=
	 * "&msg=" + URLEncoder.encode("Dear Team, " + message, "UTF-8"); data +=
	 * "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a // valid // 10
	 * // digit // phone // no. data += "&v=1.1"; data += "&msg_type=TEXT"; // Can
	 * by "FLASH" or "UNICODE_TEXT" or // “BINARY” data += "&auth_scheme=PLAIN"; URL
	 * url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String sendSmsSuccessBuyIn(String mobile, String amount, String
	 * name) { String difference = "failed"; try { String message =
	 * " Thank you for purchasing real chips worth Rs." + amount +
	 * " at LYVE Poker. Details has been mailed to your email id. Team LYVE Poker.";
	 * String data = ""; data += "method=sendMessage"; data += "&userid=" +
	 * Config.OTPLoginId; // your loginId data += "&password=" +
	 * URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your // password data +=
	 * "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8"); data +=
	 * "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a // valid // 10
	 * // digit // phone // no. data += "&v=1.1"; data += "&msg_type=TEXT"; // Can
	 * by "FLASH" or "UNICODE_TEXT" or // “BINARY” data += "&auth_scheme=PLAIN"; URL
	 * url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String sendSmsTournamentRegister(String mobile, String
	 * tournamentName, String name) { String difference = "failed"; try { String
	 * message = " Thank you for signing up for " + tournamentName +
	 * ". Details has been mailed. Team LYVE Poker."; String data = ""; data +=
	 * "method=sendMessage"; data += "&userid=" + Config.OTPLoginId; // your loginId
	 * data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); //
	 * your // password data += "&msg=" + URLEncoder.encode("Dear customer," +
	 * message, "UTF-8"); data += "&send_to=" + URLEncoder.encode("91" + mobile,
	 * "UTF-8"); // a // valid // 10 // digit // phone // no. data += "&v=1.1"; data
	 * += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or // “BINARY” data
	 * += "&auth_scheme=PLAIN"; URL url = new
	 * URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String sendSmsRedemptionRequest(String mobile, long amount,
	 * String name) { String difference = "failed"; try { String message =
	 * " Your redemption request of Rs " + amount +
	 * " has been received. Further details have been mailed. Team LYVE Poker.";
	 * String data = ""; data += "method=sendMessage"; data += "&userid=" +
	 * Config.OTPLoginId; // your loginId data += "&password=" +
	 * URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your // password data +=
	 * "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8"); data +=
	 * "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a // valid // 10
	 * // digit // phone // no. data += "&v=1.1"; data += "&msg_type=TEXT"; // Can
	 * by "FLASH" or "UNICODE_TEXT" or // “BINARY” data += "&auth_scheme=PLAIN"; URL
	 * url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String sendSmsRedemptionExecute(String mobile, long amount,
	 * Date date, String name) { String difference = "failed"; try { String
	 * requestDate = sdf.format(date); String message =
	 * " Your online redemption request dated " + requestDate +
	 * " has been processed successfully from our end, for amount Rs " + amount +
	 * ". Team LYVE Poker."; String data = ""; data += "method=sendMessage"; data +=
	 * "&userid=" + Config.OTPLoginId; // your loginId data += "&password=" +
	 * URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your // password data +=
	 * "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8"); data +=
	 * "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a // valid // 10
	 * // digit // phone // no. data += "&v=1.1"; data += "&msg_type=TEXT"; // Can
	 * by "FLASH" or "UNICODE_TEXT" or // “BINARY” data += "&auth_scheme=PLAIN"; URL
	 * url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String sendSmsRedemptionReject(String mobile, long amount, Date
	 * date, String name) { String difference = "failed"; try { String message =
	 * " We have received your withdrawal revert request and the amount processing has been cancelled. Team LYVE Poker."
	 * ; String data = ""; data += "method=sendMessage"; data += "&userid=" +
	 * Config.OTPLoginId; // your loginId data += "&password=" +
	 * URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your // password data +=
	 * "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8"); data +=
	 * "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a // valid // 10
	 * // digit // phone // no. data += "&v=1.1"; data += "&msg_type=TEXT"; // Can
	 * by "FLASH" or "UNICODE_TEXT" or // “BINARY” data += "&auth_scheme=PLAIN"; URL
	 * url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String sendSmsFailedBuyIn(String mobile, String amount, String
	 * name) { String difference = "failed"; try { String message =
	 * " Oops! The payment transaction was incomplete. Please re-try or try another mode of payment. Team LYVE Poker."
	 * ; String data = ""; data += "method=sendMessage"; data += "&userid=" +
	 * Config.OTPLoginId; // your loginId data += "&password=" +
	 * URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your // password data +=
	 * "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8"); data +=
	 * "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a // valid // 10
	 * // digit // phone // no. data += "&v=1.1"; data += "&msg_type=TEXT"; // Can
	 * by "FLASH" or "UNICODE_TEXT" or // “BINARY” data += "&auth_scheme=PLAIN"; URL
	 * url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String sendSmsForgotPassword(String mobile, String name) {
	 * String difference = "failed"; try { String message =
	 * " Your request to reset the password has been mailed to your email id. Please use the OTP received to complete the reset. Team LYVE Poker."
	 * ; String data = ""; data += "method=sendMessage"; data += "&userid=" +
	 * Config.OTPLoginId; // your loginId data += "&password=" +
	 * URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your // password data +=
	 * "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8"); data +=
	 * "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a // valid // 10
	 * // digit // phone // no. data += "&v=1.1"; data += "&msg_type=TEXT"; // Can
	 * by "FLASH" or "UNICODE_TEXT" or // “BINARY” data += "&auth_scheme=PLAIN"; URL
	 * url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String sendSmsSuccessPasswordChange(String mobile, String name)
	 * { String difference = "failed"; try { String message =
	 * " Your password for LYVE Poker account has been set successfully. In case you are unable to still login, please call us at 09051222225."
	 * ; String data = ""; data += "method=sendMessage"; data += "&userid=" +
	 * Config.OTPLoginId; // your loginId data += "&password=" +
	 * URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your // password data +=
	 * "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8"); data +=
	 * "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a // valid // 10
	 * // digit // phone // no. data += "&v=1.1"; data += "&msg_type=TEXT"; // Can
	 * by "FLASH" or "UNICODE_TEXT" or // “BINARY” data += "&auth_scheme=PLAIN"; URL
	 * url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	/*
	 * public static String sendSmsUpgradeClass(String mobile, String newClass,
	 * String name) { String difference = "failed"; try { String message =
	 * " Wow! That's an awesome move. You have now been upgraded to " + newClass +
	 * " class. Enjoy Playing @LYVE Poker."; String data = ""; data +=
	 * "method=sendMessage"; data += "&userid=" + Config.OTPLoginId; // your loginId
	 * data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); //
	 * your // password data += "&msg=" + URLEncoder.encode("Dear customer," +
	 * message, "UTF-8"); data += "&send_to=" + URLEncoder.encode("91" + mobile,
	 * "UTF-8"); // a // valid // 10 // digit // phone // no. data += "&v=1.1"; data
	 * += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or // “BINARY” data
	 * += "&auth_scheme=PLAIN"; URL url = new
	 * URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setRequestMethod("GET"); conn.setDoOutput(true); conn.setDoInput(true);
	 * conn.setUseCaches(false); conn.connect(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line;
	 * StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) !=
	 * null) { buffer.append(line).append("\n"); } difference = buffer.toString();
	 * rd.close(); conn.disconnect(); } catch (Exception e) { e.printStackTrace(); }
	 * return difference; }
	 */

	public static boolean checkShowDates(CouponCreateRequest request) {
		boolean valid = false;
		if (request.isShowDates()) {
			if (request.getShowStartDate() != null && request.getShowEndDate() != null
					&& (request.getShowStartDate() <= request.getStartDate()
							&& request.getShowStartDate() <= request.getExpireDate())
					&& (request.getShowEndDate() > request.getShowStartDate()
							&& request.getShowEndDate() > request.getStartDate()
							&& request.getShowEndDate() <= request.getExpireDate())) {
				valid = true;
			}
		} else {
			valid = true;
		}
		return valid;
	}

	public static boolean checkPromotionShowDates(PromotionCreateRequest request) {
		boolean valid = false;
		if (request.isShowDates()) {
			if (request.getShowStartDate() != null && request.getShowEndDate() != null
					&& (request.getShowStartDate() <= request.getStartDate()
							&& request.getShowStartDate() <= request.getExpireDate())
					&& (request.getShowEndDate() > request.getShowStartDate()
							&& request.getShowEndDate() > request.getStartDate()
							&& request.getShowEndDate() <= request.getExpireDate())) {
				valid = true;
			}
		} else {
			valid = true;
		}
		return valid;
	}

	public static synchronized String generate() throws InterruptedException {
		Thread.sleep(1000);
		return new Hashids("WSEgames", 4).encrypt(System.currentTimeMillis() / 1000);
	} 
	public static String generateOTP() {
		Random randomGenerator = new Random();
		for (int idx = 1; idx <= 10; ++idx) {
			Integer randomInt = randomGenerator.nextInt(2000001);
			if (randomInt.toString().length() == 6)
				return randomInt.toString();
		}
		return "615234";
	}

	public static boolean sendEmailCsv(String fromEmail, List<String> toEmails, String subject, String messageText,
			String csvName, byte[] bytesArray) {
		// com.sendgrid.Response response;
		Email from = new Email(Config.FROM_EMAIL);
		Email to = new Email(toEmails.get(0));
		Content content = new Content("text/html", messageText);
		Mail mail = new Mail(from, subject, to, content);
		Personalization p = new Personalization();
		toEmails.remove(0);
		toEmails.forEach(email -> {
			p.addTo(new Email(email));
		});
		mail.addPersonalization(p);
		Attachments attachments = new Attachments();
		Base64 x = new Base64();
		String fileDataString = x.encodeAsString(bytesArray);
		attachments.setContent(fileDataString);
		attachments.setType("text/csv");
		attachments.setFilename(csvName + ".csv");
		attachments.setDisposition("attachment");
		attachments.setContentId("Report ");
		mail.addAttachments(attachments);
		SendGrid sg = new SendGrid(Config.EMAIL_API_KEY);
		com.sendgrid.Request request = new com.sendgrid.Request();
		try {
			request.method = Method.POST;
			request.endpoint = "mail/send";
			request.body = mail.build();
			sg.api(request);
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return true;
	}

	public static boolean emailSender(String fromEmail, String toEmail, String subject, String content1) {
		Email from = new Email(fromEmail);
		Email to = new Email(toEmail);
		Content content = new Content("text/html", content1);
		Mail mail = new Mail(from, subject, to, content);
		SendGrid sg = new SendGrid(Config.EMAIL_API_KEY);
		com.sendgrid.Request request = new com.sendgrid.Request();
		try {
			request.method = Method.POST;
			request.endpoint = "mail/send";
			request.body = mail.build();
			sg.api(request);
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean sentEmail(String email, String token, String domain, String userName, boolean sendPinLink)
			throws Exception {
		StringBuilder htmlBuilder = new StringBuilder();
		String content = null;
		htmlBuilder.append("<html><body><p>verification_link : <a href=" + Config.PROTOCOL + "://" + domain
				+ "/verify_email?token=" + token + ">" + Config.PROTOCOL + "://" + domain + "/verify_email?token="
				+ token + "</a></p></body></html>");
		content = getEmailContent("template/email_varification.html", EmailContextType.emailVerify,
				new EmailSenderDto(userName, null, null, null, null, domain, token, null, null));
		if (sendPinLink)
			sentSecurePinLink(email, userName, domain, token, "Generate Secure Pin");
		boolean status = emailSender(Config.FROM_EMAIL, email, "Verify your Email.", content);
		return status;
	}

	public static boolean sentForgotPasswordLink(String email, String token, String domain, String userName)
			throws Exception {
		String content = getEmailContent("template/password_reset.html", EmailContextType.forgetPassword,
				new EmailSenderDto(userName, null, null, null, null, domain, token, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, email, "Reset your password.", content);
		return status;
	}

	public static boolean sendMailToSender(ContactUs contactUs) throws Exception {
		String content = getEmailContent("template/confirm_contactus.html", EmailContextType.contactUs,
				new EmailSenderDto(contactUs.getName(), null, null, null, null, null, null, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, contactUs.getEmail(), contactUs.getSubject(), content);
		return status;
	}

	public static boolean sendMailForWithdraw(WithdrawRequest withdrawRequest, Player player, String domain,
			String subject) throws Exception {
		String content = getEmailContent("template/withdraw_request.html", EmailContextType.redemption,
				new EmailSenderDto(player.getUserName(), null, null, null, null, null, null,
						withdrawRequest.getDisplayWithdrawId(), Long.toString(withdrawRequest.getAmount())));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	// for digital signup
	public static boolean sendMailToCustomerForDigitalSignUp(Player player, String domain, String subject)
			throws Exception {
		String content = getEmailContent("template/customer_support_signup.html", EmailContextType.customer_digital,
				new EmailSenderDto(player.getGameName(), null, null, null, null, null, null, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailToCustomerCareForDigitalSignUp(Player player, String domain, String subject,
			String customerCare) throws Exception {
		String content = getEmailContent("template/customer_support_digital_signup.html",
				EmailContextType.customer_care_digital, new EmailSenderDto(player.getGameName(), null,
						player.getMobile().toString(), null, player.getEmail(), domain, null, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, customerCare, subject, content);
		return status;
	}

	public static boolean sendMailForDeployment(Player player, String domain, String subject, Date date, Date startTime,
			Date endTime) throws Exception {
		String content = getEmailContent("template/deployment.html", EmailContextType.deployment,
				new EmailSenderDto(player.getUserName(), null, null, null, null, null, null, date, startTime, endTime));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailForSuccessWithdraw(String value, WithdrawRequest withdrawRequest, Player player,
			String domain, String subject) throws Exception {
		String content = getEmailContent("template/withdraw_success.html", EmailContextType.success_redemption,
				new EmailSenderDto(player.getUserName(), null, null, null, null, null, null,
						withdrawRequest.getDisplayWithdrawId(), value));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailForFailedWithdraw(WithdrawRequest withdrawRequest, Player player, String domain,
			String subject) throws Exception {
		String content = getEmailContent("template/withdraw_failed.html", EmailContextType.failed_redemption,
				new EmailSenderDto(player.getUserName(), null, null, null, null, null, null,
						withdrawRequest.getDisplayWithdrawId(), Long.toString(withdrawRequest.getAmount())));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailForRegisterTournament(Tournament tournament, Player player, String domain,
			String subject) throws Exception {
		String content = getEmailContent("template/tournament_register.html", EmailContextType.tournament_register,
				new EmailSenderDto(player.getUserName(), null, null, null, null, null, tournament.getTitle(),
						tournament.getStartDate()));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailForUnRegisterTournament(Tournament tournament, Player player, String domain,
			String subject) throws Exception {
		String content = getEmailContent("template/tournament_unregister.html", EmailContextType.tournament_register,
				new EmailSenderDto(player.getUserName(), null, null, null, null, null, tournament.getTitle(),
						tournament.getStartDate()));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailForChangePassword(Player player, String domain, String subject) throws Exception {
		String content = getEmailContent("template/change_password.html", EmailContextType.change_password,
				new EmailSenderDto(player.getUserName(), null, null, null, player.getCurrentCountry(), player.getIp(),
						null, null));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailForResetPassword(Player player, String domain, String subject) throws Exception {
		String content = getEmailContent("template/reset_password.html", EmailContextType.reset_passsword,
				new EmailSenderDto(player.getUserName(), player.getLegal().getIp(), null, null,
						player.getCurrentCountry(), null, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailForBuyIn(Long chipAmount, PlayerTransaction playerTransaction, Player player,
			String domain, String subject) throws Exception {
		String content = getEmailContent("template/buyIn.html", EmailContextType.buyIn,
				new EmailSenderDto(player.getUserName(), null, null, null, null, null, null,
						playerTransaction.getDisplayTransaxId(), Long.toString(chipAmount)));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailForFailedBuyIn(String transactionId, Player player, String domain, String subject)
			throws Exception {
		String content = getEmailContent("template/buyIn_failed.html", EmailContextType.buyIn_failed,
				new EmailSenderDto(player.getUserName(), null, null, null, null, null, null, transactionId, null));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailForUpgradeClass(PlayerClass playerclass, Player player, String domain, String subject)
			throws Exception {
		String content = getEmailContent("template/class_upgrade.html", EmailContextType.class_upgrade,
				new EmailSenderDto(player.getUserName(), playerclass.toString(), domain.toString(),
						player.getForgotPasswordToken(), null, null, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, player.getEmail(), subject, content);
		return status;
	}

	public static boolean sendMailToCustomerSupport(ContactUs contactUs) throws Exception {
		// VelocityEngine velocityEngine = new VelocityEngine();
		// Properties p = new Properties();
		// p.setProperty("resource.loader", "class");
		// p.setProperty("class.resource.loader.class",
		// "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		// velocityEngine.init(p);
		// Template template =
		// velocityEngine.getTemplate("template/customer_service.html");
		// VelocityContext context = new VelocityContext();
		// context.put("senderName", contactUs.getName());
		// context.put("senderMobile", contactUs.getMobile());
		// context.put("senderMsg", contactUs.getMessage());
		// StringWriter writer = new StringWriter();
		// template.merge(context, writer);
		// StringBuffer sb = writer.getBuffer();
		// String content = sb.toString();
		String content = getEmailContent("template/customer_support.html", EmailContextType.customer_support,
				new EmailSenderDto(contactUs.getName(), contactUs.getMessage(), contactUs.getMobile(), null,
						contactUs.getEmail(), null, null, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, Config.CUSTOMER_SERVICE_EMAIL, contactUs.getSubject(), content);
		return status;
	}

	public static boolean sentMailForUploadDocuments(String gameName, String email) throws Exception {
		String content = getEmailContent("template/user_upload_documents.html", EmailContextType.customerContactUs,
				new EmailSenderDto(gameName, null, null, null, email, null, null, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, Config.CUSTOMER_SERVICE_EMAIL, "User Document Uploaded",
				content);
		return status;
	}

	public static boolean sendMailToSender(SubscribeUs subscribeUs) throws Exception {
		// VelocityEngine velocityEngine = new VelocityEngine();
		// Properties p = new Properties();
		// p.setProperty("resource.loader", "class");
		// p.setProperty("class.resource.loader.class",
		// "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		// velocityEngine.init(p);
		// Template template =
		// velocityEngine.getTemplate("template/confirm_contactus.html");
		// VelocityContext context = new VelocityContext();
		// context.put("userName", contactUs.getName());
		// context.put("lyveLogo", Config.WSE_LYVE_LOGO);
		// StringWriter writer = new StringWriter();
		// template.merge(context, writer);
		// StringBuffer sb = writer.getBuffer();
		// String content = sb.toString();

		String content = getEmailContent("template/customer_subscribe.html", EmailContextType.subscribeUs,
				new EmailSenderDto(subscribeUs.getEmail(), null, null, null, null, null, null, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, subscribeUs.getEmail(), "Lyve-Game Subscription", content);
		return status;
	}

	public static boolean sendMailToCustomerSupport(SubscribeUs subscribeUs) throws Exception {
		// VelocityEngine velocityEngine = new VelocityEngine();
		// Properties p = new Properties();
		// p.setProperty("resource.loader", "class");
		// p.setProperty("class.resource.loader.class",
		// "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		// velocityEngine.init(p);
		// Template template =
		// velocityEngine.getTemplate("template/customer_service.html");
		// VelocityContext context = new VelocityContext();
		// context.put("senderName", contactUs.getName());
		// context.put("senderMobile", contactUs.getMobile());
		// context.put("senderMsg", contactUs.getMessage());
		// StringWriter writer = new StringWriter();
		// template.merge(context, writer);
		// StringBuffer sb = writer.getBuffer();
		// String content = sb.toString();
		String content = getEmailContent("template/customer_subscribe.html", EmailContextType.customerContactUs,
				new EmailSenderDto(subscribeUs.getEmail(), "Lyve-Game Subscription", null, null, null, null, null, null,
						null));
		boolean status = emailSender(Config.FROM_EMAIL, Config.CUSTOMER_SERVICE_EMAIL, "Lyve-Game Subscription",
				content);
		return status;
	}

	public static boolean sentSecurePinLink(String email, String userName, String domain, String token, String subject)
			throws Exception {
		String template = "template/reset_secure_pin.html";
		if (subject.startsWith("Generate"))
			template = "template/generate_secure_pin.html";
		String content = getEmailContent(template, EmailContextType.securePin,
				new EmailSenderDto(userName, null, null, null, email, domain, token, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, email, subject, content);
		return status;
	}

	public static boolean sentSecurePinToCutomerSupport(String mobilePin, String email) throws Exception {
		String content = getEmailContent("template/customer_service_secure_pin.html",
				EmailContextType.customerSecurePin,
				new EmailSenderDto(null, null, null, mobilePin, email, null, null, null, null));
		boolean status = emailSender(Config.FROM_EMAIL, Config.CUSTOMER_SERVICE_EMAIL, "Mobile Reset Pin", content);
		return status;
	}

	public static String getEmailContent(String templete, EmailContextType emailContextType, EmailSenderDto emailSender)
			throws Exception {
		VelocityEngine velocityEngine = new VelocityEngine();
		// Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		Properties p = new Properties();
		p.setProperty("mail.smtps.host", "smtp.gmail.com");
		p.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		p.setProperty("mail.smtp.socketFactory.fallback", "false");
		p.setProperty("mail.smtp.port", "465");
		p.setProperty("mail.smtp.socketFactory.port", "465");
		p.setProperty("mail.smtps.auth", "true");
		p.put("mail.smtps.quitwait", "false");
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.init(p);
		Template template = velocityEngine.getTemplate(templete);
		VelocityContext context = new VelocityContext();
		buildContext(context, emailContextType, emailSender);
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		StringBuffer sb = writer.getBuffer();
		String content = sb.toString();
		return content;
	}

	public static VelocityContext buildContext(VelocityContext context, EmailContextType emailContextType,
			EmailSenderDto emailSender) {
		String updatedProtocol = Config.PROTOCOL;
		if (emailSender != null && emailSender.getDomain() != null
				&& emailSender.getDomain().equalsIgnoreCase("affiliate.lyvegames.com")) {
			updatedProtocol = "http";
		}
		switch (emailContextType) {
		case emailVerify:
			context.put("postURL", updatedProtocol + "://" + emailSender.getDomain() + "/verify_email?token="
					+ emailSender.getToken());
			context.put("userName", emailSender.getName());
			context.put("lyveLogo", Config.WSE_LYVE_LOGO);
			break;
		case forgetPassword:
			context.put("postURL", updatedProtocol + "://" + emailSender.getDomain() + "/reset_password?token="
					+ emailSender.getToken());
			context.put("userName", emailSender.getName());
			context.put("lyveLogo", Config.WSE_LYVE_LOGO);
			break;
		case contactUs:
			context.put("userName", emailSender.getName());
			context.put("lyveLogo", Config.WSE_LYVE_LOGO);
			break;
		case customerContactUs:
			context.put("senderName", emailSender.getName());
			context.put("senderMobile", emailSender.getMobile());
			context.put("senderMsg", emailSender.getMessage());
			context.put("email", emailSender.getPlayerEmail());
			break;
		case customer_support:
			context.put("userName", emailSender.getName());
			context.put("mobile", emailSender.getMobile());
			context.put("message", emailSender.getMessage());
			context.put("email", emailSender.getPlayerEmail());
			break;
		case securePin:
			context.put("userName", emailSender.getName());
			context.put("playerEmail", emailSender.getPlayerEmail());
			context.put("postURL", updatedProtocol + "://" + emailSender.getDomain() + "/generate-secure-pin");
			context.put("lyveLogo", Config.WSE_LYVE_LOGO);
			break;
		case redemption:
			context.put("userName", emailSender.getName());
			context.put("transactionId", emailSender.getTransactionId());
			context.put("amount", emailSender.getAmount());
			break;
		case customer_digital:
			context.put("userName", emailSender.getName());
			break;
		case customer_care_digital:
			context.put("userName", emailSender.getName());
			context.put("mobile", emailSender.getMobile());
			context.put("domain", emailSender.getDomain());
			context.put("email", emailSender.getPlayerEmail());
			break; 
		case success_redemption:
			context.put("userName", emailSender.getName());
			context.put("transactionId", emailSender.getTransactionId());
			context.put("amount", emailSender.getAmount());
			context.put("date", new Date());
			break;
		case failed_redemption:
			context.put("userName", emailSender.getName());
			context.put("transactionId", emailSender.getTransactionId());
			context.put("amount", emailSender.getAmount());
			context.put("date", new Date());
			break;
		case tournament_register:
			context.put("userName", emailSender.getName());
			Date date = emailSender.getDate();
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			String strTime = sdformat.format(date);
			String strDate = formatter.format(date);
			context.put("date", strDate);
			context.put("time", strTime);
			context.put("tournamentName", emailSender.getTournamentName());
			break;
		case deployment:
			context.put("userName", emailSender.getName());
			Date deploymentDate = emailSender.getDate();
			String strDateFormat = "dd-MMM-yyyy"; // Date format is Specified
			SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
			String sDate = objSDF.format(deploymentDate);
			String sTime = sdformat.format(emailSender.getStartTime());
			String eTime = sdformat.format(emailSender.getEndTime());
			// String sDate =
			// formater.format(com.actolap.wse.commons.Utils.utcToIst(deploymentDate));
			context.put("date", sDate);
			context.put("stime", sTime);
			context.put("etime", eTime);
			break;
		case buyIn:
			context.put("userName", emailSender.getName());
			context.put("transactionId", emailSender.getTransactionId());
			context.put("amount", emailSender.getAmount());
			break;
		case buyIn_failed:
			context.put("userName", emailSender.getName());
			context.put("transactionId", emailSender.getTransactionId());
			break;
		case class_upgrade:
			context.put("postURL", updatedProtocol + "://" + emailSender.getDomain());
			context.put("userName", emailSender.getName());
			context.put("playerclass", emailSender.getPlayerClass());
			break;
		case change_password:
			context.put("userName", emailSender.getName());
			context.put("country", emailSender.getCountry());
			context.put("ip", emailSender.getIp());
			break;
		case reset_passsword:
			context.put("userName", emailSender.getName());
			context.put("country", emailSender.getCountry());
			context.put("ip", emailSender.getPlayerClass());
			break;
		case customerSecurePin:
			context.put("playerEmail", emailSender.getPlayerEmail());
			context.put("mobilePin", emailSender.getSecurePin());
			context.put("lyveLogo", Config.WSE_LYVE_LOGO);
			break;
		default:
			break;
		}
		return context;
	}

	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}

	public static boolean isValidGameName(String gameName) {
		boolean result = true;
		try {
			if (gameName.matches("[0-9]+")) {
				result = false;
			}
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean validatePassword(final String password) {
		Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();

	}

	public static void userPermissions(String userId, List<String> permissionList) {
		User user = UserDao.getById(userId);
		if (user != null) {
			Department account = DepartmentDao.getById(user.getDepartmentId());
			List<RolePermission> rolePermissions = null;
			if (account != null)
				rolePermissions = RolePermissionDao.getByRoleIds(account.getRoleIds());
			List<String> permissionIds = new ArrayList<String>();
			if (rolePermissions != null) {
				for (RolePermission rolePermition : rolePermissions) {
					permissionIds.add(rolePermition.getPermissionId());
				}
			}
			List<Permission> permissions = null;
			if (!permissionIds.isEmpty())
				permissions = PermissionDao.getByIds(permissionIds, null, null);
			if (permissions != null) {
				for (Permission permissionObj : permissions) {
					permissionList.add(permissionObj.getTitle());
				}
			}
		}
	}

	public static String getClassLabel(PlayerClass playerClass) {
		String label = null;
		switch (playerClass) {
		case CRYSTAL:
			label = "Crystal";
			break;
		case PEARL:
			label = "Pearl";
			break;
		case TOPAZ: 
			label = "Topaz"; 
			break; 
		case SAPPHIRE: 
			label = "Sapphire"; 
			break; 
		case DIAMOND: 
			label = "Diamond"; 
			break;
		case RUBY:
			label = "Ruby";
			break;
		case OPAL:
			label = "Opal";
			break;
		}
		return label;
	}

	public static Category getPlayerCurrentCategory(PlayerClass playerClass, List<Category> categoryList) {
		Category currentCategory = null;
		for (Category category : categoryList) {
			if (category.getTitle().equals(playerClass)) {
				currentCategory = category;
				break;
			}
		} 
		return currentCategory;
	}

	public static int getAccountFreezedTime(Player player) {
		return (int) (TimeUnit.MILLISECONDS.toMinutes(new Date().getTime())
				- TimeUnit.MILLISECONDS.toMinutes(player.getLoginOtpValidation().getFreezeTime().getTime()));
	}

	// public static String generatePin() {
	// int randomPIN = (int) (Math.random() * 9000) + 1000;
	// return String.valueOf(randomPIN);
	// }

	public static String getEncryptedBySha256(String str) {
		String encryptedstr = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashInBytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
			// bytes to hex
			StringBuilder sb = new StringBuilder();
			for (byte b : hashInBytes) {
				sb.append(String.format("%02x", b));
			}
			encryptedstr = sb.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return encryptedstr;
	}

}

