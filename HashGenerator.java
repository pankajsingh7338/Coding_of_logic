package com.actolap.wse.payment.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

public class HashGenerator {

	private final static String SHA_512 = "SHA-512";
	private static final String HASH_SEQUENCE = "key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|udf6|udf7|udf8|udf9|udf10";
	private static final String VERIFY_HASH_SEQUENCE = "status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|amount|txnid|key";
	private static final String VERIFY_HASH_SEQUENCE_AD_CHRG = "additionalCharges|salt|status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|amount|txnid|key";
	private final static org.slf4j.Logger logger = LoggerFactory
			.getLogger(HashGenerator.class);

	private static boolean empty(String str) {
		return str == null || str.trim().equals("");
	}

	private static String hashCal(String type, String str) {
		byte[] hashseq = str.getBytes();
		StringBuffer hexString = new StringBuffer();
		try {
			MessageDigest algorithm = MessageDigest.getInstance(type);
			algorithm.reset();
			algorithm.update(hashseq);
			byte messageDigest[] = algorithm.digest();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1) {
					hexString.append("0");
				}
				hexString.append(hex);
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
		return hexString.toString();
	}

	public static void render(MerchantData merchantData, String salt)
			throws IOException, NoSuchFieldException, SecurityException {
		String hashString = "";
		String hash = "";

		for (String part : HASH_SEQUENCE.split("\\|")) {
			String o = "";
			Field field = null;
			try {
				field = merchantData.getClass().getDeclaredField(part);
			} catch (Exception e) {
				// logger.error(e.getMessage(), e);
			}
			if (field != null) {
				o = runGetter(field, merchantData);
			}
			hashString = empty(o) ? hashString.concat("") : hashString
					.concat(o);
			hashString = hashString.concat("|");
		}
		hashString = hashString.concat(salt);
		hash = hashCal(SHA_512, hashString);
		merchantData.setHash_string(hashString);
		merchantData.setHash(hash);
	}

	private static String runGetter(Field field, MerchantData merchantData) {
		// Find the correct method
		Method[] methods = merchantData.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if ((method.getName().startsWith("get"))
					&& (method.getName().length() == (field.getName().length() + 3))) {
				if (method.getName().toLowerCase()
						.endsWith(field.getName().toLowerCase())) {
					// Method found, run it
					try {
						return (String) method.invoke(merchantData);
					} catch (IllegalAccessException e) {
						logger.error("Could not determine method: "
								+ method.getName());
					} catch (InvocationTargetException e) {
						logger.error("Could not determine method: "
								+ method.getName());
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
		return null;
	}

	public static Map<String, String> convertClassToMap(MerchantData data) {
		// TODO Auto-generated method stub
		Map<String, String> result = new HashMap<String, String>();
		Field[] fields = data.getClass().getDeclaredFields();
		for (Field field : fields) {
			String o = runGetter(field, data);
			result.put(field.getName(), empty(o) ? "" : o);
		}
		return result;
	}

	private static String get(Map<String, String> data, String salt,
			String sequence) {
		String hashString = "";
		hashString = hashString.concat(salt);
		for (String part : sequence.split("\\|")) {
			String o = null;
			if (part != null && !"".equals(part))
				o = data.get(part);
			hashString = hashString.concat("|");
			hashString = empty(o) ? hashString.concat("") : hashString
					.concat(o);
		}
		return hashCal(SHA_512, hashString);
	}

	public static String getHashWithAdditionalCharge(Map<String, String> data,
			String salt) {
		return get(data, salt, VERIFY_HASH_SEQUENCE_AD_CHRG);
	}

	public static String getHash(Map<String, String> data, String salt) {
		return get(data, salt, VERIFY_HASH_SEQUENCE);
	}

}

