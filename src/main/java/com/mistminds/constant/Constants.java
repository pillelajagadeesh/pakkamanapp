/*package com.mistminds.constant;

import java.security.SecureRandom;
import java.util.Random;

public class Constants {
	public static final String SUCCESS_RESULT = "SUCCESS";
	public static final String FAILURE_RESULT = "FAILURE";
	public static final String USER_STATUS = "NOT ACTIVE";
	public static final String CLOUDINARY = "cloudinary://672384348283461:gBHxxzfwg2erB8Bj7WfbRjbI9NU@mist-minds";
	
	public static String generatePassword() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
		final int PW_LENGTH = 6;
		Random rnd = new SecureRandom();
		StringBuilder pass = new StringBuilder();
		for (int i = 0; i < PW_LENGTH; i++) {
			pass.append(chars.charAt(rnd.nextInt(chars.length())));
		}
		return pass.toString();
	}
}
*/