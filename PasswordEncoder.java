package com.actolap.wsegame.common;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.actolap.wsegame.callManager.Base64;

public class PasswordEncoder {

	private final static int ITERATION_COUNT = 2;

	/**
	 * 
	 * @param password
	 * @param saltKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static String encode(String password, String saltKey)
			throws NoSuchAlgorithmException, IOException {
		String encodedPassword = null;
		byte[] salt = base64ToByte(saltKey);
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(salt);
		byte[] btPass = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < ITERATION_COUNT; i++) {
			digest.reset(); 
			btPass = digest.digest(btPass); 
		} 
		encodedPassword = byteToBase64(btPass);
		return encodedPassword;
	}

	/**
	 * @param str
	 * @return byte[]
	 * @throws IOException
	 */
	private static byte[] base64ToByte(String str) throws IOException {
		byte[] returnbyteArray = Base64.decode(str, Base64.DEFAULT);
		return returnbyteArray;
	}

	/**
	 * @param bt
	 * @return String
	 * @throws IOException
	 */
	private static String byteToBase64(byte[] bt) {
		String returnString = Base64.encodeToString(bt, Base64.DEFAULT);
		return returnString;
	}

}

