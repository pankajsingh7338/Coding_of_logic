package com.actolap.wse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.actolap.config.Config;
import com.actolap.wse.model.player.PlayerClass;

public class LyveSendSms {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
	public static DecimalFormat df = new DecimalFormat("0.00");
	private static final SimpleDateFormat sdformat = new SimpleDateFormat("h.mm a");
	static {
		sdformat.setTimeZone(java.util.TimeZone.getTimeZone("IST"));
	}

	public static String setOtp(String mobile, String otp) {
		String difference = "failed";
		try {

			String message = " The OTP for your password reset request is " + otp + ". Team LYVE Games";
			/*
			 * String message = " Your OTP for password reset request at LYVE Poker is " +
			 * otp + ". Call us at 09051222225 for any assistance.";
			 */
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String setOtpCSV(String mobile, String otp, String title) {
		String difference = "failed";
		try {

			String message = " Your OTP to download " + title + " is " + otp
					+ ". OTP is valid for next 3 min. Team LYVE Games";

			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear user," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String setOtpAffiliateCSV(String mobile, String otp, String title) {
		String difference = "failed";
		try {

			String message = " Your OTP to download Affiliate Players of " + title + " is " + otp
					+ ". OTP is valid for next 3 min. Team LYVE Games";

			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear user," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String setOtpChipConversion(String mobile, String otp) {
		String difference = "failed";
		try {

			String message = " The OTP for your chip conversion request is " + otp + ". Team LYVE Games";
			/*
			 * String message = " Your OTP for password reset request at LYVE Poker is " +
			 * otp + ". Call us at 09051222225 for any assistance.";
			 */
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendbankDetailsApprovalConfirmation(String mobile, String accountNumber) {
		String difference = "failed";
		try {
			String message = " Your Account number " + accountNumber
					+ " has been Approved. Thank you for updating your Bank Account details.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendbankDetailsRejectionConfirmation(String mobile, String accountNumber) {
		String difference = "failed";
		try {
			String message = " Your Account Number " + accountNumber
					+ " has been Rejected due to discrepancies. Kindly resubmit with correct details.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendKYCApprovalConfirmation(String mobile, String kyc) {
		String difference = "failed";
		try {
			String message = " Your " + kyc + " has been Approved. Thank you for updating your KYC details.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendKYCRejectionConfirmation(String mobile, String kyc) {
		String difference = "failed";
		try {
			String message = " Your " + kyc
					+ " has been Rejected due to discrepancies. Kindly re-upload with correct KYC details.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String setOtpForChipConversion(String mobile, String otp, String amount) {
		String difference = "failed";
		try {
			String message = " Your OTP for chip conversion of " + amount + "VIP points to real chips is " + otp
					+ ". Call us at 09051222225 for any assistance.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String playerAchievement(String mobile, String type, Long point, String name, String achievementType,
			int value) {
		String difference = "failed";
		try {
			String message = " Congrats! " + point + " " + type + " chips awarded to you on " + achievementType + " "
					+ value + " " + name + ". Call us at 09051222225 for any assistance.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSmsRedemptionReject(long mobile, long amount, Date date, String name) {
		String difference = "failed";
		try {
			String message = " We have received your request to revert your withdrawal. Hence we have cancelled the process from our end. Team LYVE Games ";
			// String message = " We have received your withdrawal revert request and the
			// amount processing has been cancelled. Team LYVE Poker.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a
																				// valid
																				// 10
																				// digit
																				// phone
																				// no.
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSmsRedemptionExecute(String mobile, long amount, Date date, String name) {
		String difference = "failed";
		try {
			String requestDate = sdf.format(date);
			String message = " Your online redemption request dated " + requestDate + " for the Amount Rs " + amount
					+ " have been processed successfully. Call us at +919051222225 for further assistance. Team LYVE Games.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSmsRedemptionRequest(String mobile, long amount, String name) {
		String difference = "failed";
		try {
			String message = " Your redemption request of Rs " + amount
					+ " have been received and the further details have been mailed to your email id. Team LYVE Games.";
			/*
			 * String message = " Your redemption request of Rs " + amount +
			 * " has been received. Further details have been mailed. Team LYVE Poker.";
			 */
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSignUpSms(String playerClass, String mobile) {
		String difference = "failed";
		try {
			String message = " Thank you for signing up for " + playerClass
					+ " class. Further details have been mailed to your email id. Team LYVE Games.";
			// String message = " Thank you for signing up at LYVE Games. Please verify your
			// account by completing the email verification link";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String deployment(Date date, Date startTime, Date endTime, String mobile, String username) {
		// String finalDate = sdf1.format(date);
		String strDateFormat = "dd-MMM-yyyy"; // Date format is Specified
		SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);

		String finalDate = objSDF.format(date);
		String sTime = sdformat.format(startTime);
		String eTime = sdformat.format(endTime);
		String difference = "failed";

		try {
			String message = "Dear " + username
					+ ", We are upgrading for a better gaming experience. Hence our Server will be down for merely from "
					+ sTime + " to " + eTime + " on " + finalDate + ". Inconvenience Regretted !! Team LYVE Games.";
			// String message = " Thank you for signing up at LYVE Games. Please verify your
			// account by completing the email verification link";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode(message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSmsUpgradeClass(String mobile, PlayerClass newClass, String name) {
		String difference = "failed";
		try {
			String message = " Congratulations. That was an awesome move. You have now been upgraded to " + newClass
					+ " class. Enjoy Playing  at LYVE Games.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSmsSuccessPasswordChange(String mobile, String name) {
		String difference = "failed";
		try {
			String message = " Your password for your account at LYVE Games  have been set successfully. In case you are still unable to login, please call us at 09051222225.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSmsFailedBuyIn(String mobile, String amount, String name) {
		String difference = "failed";
		try {
			String message = " Looks like your transaction was incomplete. Please try again or choose any other mode of payment. Team LYVE Games.";
			// String message = " Oops! The payment transaction was incomplete. Please
			// re-try or try another mode of payment. Team LYVE Poker.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSmsSuccessBuyIn(String mobile, String amount, String name) {
		String difference = "failed";
		try {
			String message = " Thank you for purchasing real chips worth Rs " + amount
					+ " at LYVE Games. The details of your purchase have been mailed to your email id. Team LYVE Games.";
			// String message = " Thank you for purchasing real chips worth Rs." + amount
			// + " at LYVE Poker. Details has been mailed to your email id. Team LYVE
			// Poker.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSmsForgotPassword(String mobile, String name) {
		String difference = "failed";
		try {
			String message = " Your request to reset the password has been mailed to your email id. Please use the OTP received to complete the reset. Team LYVE Games.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a
																				// valid
																				// 10
																				// digit
																				// phone
																				// no.
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String setOtpForWithdraw(String mobile, String otp) {
		String difference = "failed";
		try {

			// String message = " Your OTP for withdrawal request at LYVE Games is "+otp+".
			// Call us at 09051222225 for any assistance. Team LYVE Games";
			String message = " Your OTP for withdrawal request at LYVE Poker is " + otp
					+ ". Call us at 09051222225 for any assistance.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a
																				// valid
																				// 10
																				// digit
																				// phone
																				// no.
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String setOtpForSignUp(String mobile, String otp) {
		String difference = "failed";
		try {
			// String message = " Your OTP for Signing Up at LYVE Games is "+otp+". Call us
			// at 09051222225 for any assistance. Team LYVE Games";
			String message = " Your OTP for Sign Up at LYVE Poker is " + otp
					+ ". Call us at 09051222225 for any assistance.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8"); // a
																				// valid
																				// 10
																				// digit
																				// phone
																				// no.
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSmsForFinecialUpdate(String mobile, String userName, String email) {
		String difference = "failed";
		try {
			String message = userName + " have been uploaded the Documents and Updated the Bank Details with this ("
					+ email + ").";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear Team, " + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	public static String sendSmsTournamentRegister(String mobile, String tournamentName, String name) {
		String difference = "failed";
		try {
			String message = " Thank you for registering up for " + tournamentName
					+ ". Details have been mailed. Team LYVE Games.";
			String data = "";
			data += "method=sendMessage";
			data += "&userid=" + Config.OTPLoginId; // your loginId
			data += "&password=" + URLEncoder.encode(Config.OTPPassword, "UTF-8"); // your
																					// password
			data += "&msg=" + URLEncoder.encode("Dear customer," + message, "UTF-8");
			data += "&send_to=" + URLEncoder.encode("91" + mobile, "UTF-8");
			data += "&v=1.1";
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or
										// “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			difference = buffer.toString();
			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

}

