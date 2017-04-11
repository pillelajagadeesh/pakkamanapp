package com.mistminds.config;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Application constants.
 */
public final class Constants {

    // Spring profile for development, production and "fast", see http://jhipster.github.io/profiles.html
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String SPRING_PROFILE_FAST = "fast";
    // Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
    public static final String SPRING_PROFILE_CLOUD = "cloud";
    // Spring profile used when deploying to Heroku
    public static final String SPRING_PROFILE_HEROKU = "heroku";

    public static final String SYSTEM_ACCOUNT = "system";
	
	public static final String SUCCESS_RESULT = "SUCCESS";
	public static final String FAILURE_RESULT = "FAILURE";
	public static final String USER_STATUS = "NOT ACTIVE";
	public static final String CLOUDINARY = "cloudinary://672384348283461:gBHxxzfwg2erB8Bj7WfbRjbI9NU@mist-minds";
	public static String generatePassword() {
		/*String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";*/
		String chars =  "0123456789";
		final int PW_LENGTH = 4;
		Random rnd = new SecureRandom();
		StringBuilder pass = new StringBuilder();
		for (int i = 0; i < PW_LENGTH; i++) {
			pass.append(chars.charAt(rnd.nextInt(chars.length())));
		}
		return pass.toString();
	}
	
	public static String generateTrackId() {
		/*String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";*/
		String chars =  "0123456789";
		final int PW_LENGTH = 10;
		Random rnd = new SecureRandom();
		StringBuilder pass = new StringBuilder();
		for (int i = 0; i < PW_LENGTH; i++) {
			pass.append(chars.charAt(rnd.nextInt(chars.length())));
		}
		return pass.toString();
	}

	public static final String generateAdId(){
	    //generate random UUIDs
	    String idOne = UUID.randomUUID().toString();
	    String AdId = idOne.substring(30, 35)+idOne.substring(0, 5);
		return AdId;
	  }

    private Constants() {
    }
}
