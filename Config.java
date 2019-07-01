package com.actolap.config;

import java.util.HashSet;
import java.util.Set;

public class Config {

	public static String AWS_BUCKET = "lyve-assets-dev";
	public static String HOST = "localhost:8080";
	public static String DOMAIN = "localhost:8080/lyve-fe";
	public static String SYSTEM_HOST = "NA";
	public static String PROCESS_NAME = "NA";
	public static String DB_NAME = "wse-games";
	public static int cph = 500;
	public static boolean replication = true;
	public static int port = 27017;
	public static boolean dev = true;
	public static Set<String> MONGOHOST = new HashSet<String>();

	public static String OTPLoginId = "2000168066"; // "2000168066"
	public static String OTPPassword = "punam@sms18";// punam@sms18

	public static String EMAIL_API_KEY = "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";
	public static String FROM_EMAIL = "donotreply@lyvegames.com";
	public static String CUSTOMER_SERVICE_EMAIL = "customerservice@lyvegames.com ";
	public static String WSE_LYVE_LOGO = "http://lyve-assets-dev.s3.amazonaws.com/public_images/wsegames/logo.png";
	public static long referralChips = 100;
	public static String PROTOCOL = "https";
	//public static String restrictedState = "Telangana , Assam, Odisha, Gujarat, Nagaland, Sikkim, Karnataka, West Bengal";
	public static String[] restrictedState = {"Telangana" , "Assam", "Odisha", "Gujarat", "Nagaland", "Sikkim"};
	public static String image_path_unity = "/resources/lyve";
	
	
	public static String AWS_KEY = "AKIAJ5R4R2GBSQWI7IAA";
	public static String AWS_SECRET = "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";
	public static String AWS_BUCKET_LOG_BACKUP = "lyve-logs-backups";
	public static boolean logTransferScheduler = false;
	public static String environment = "dev";
}

