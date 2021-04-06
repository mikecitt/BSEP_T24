package com.hospital.hospitalapi.service.email;

public class EmailConstants {

	private final static String FILE_PREFIX = "mail/";

	public final static String SUBJECT_ACTIVATE_ACCOUNT = "Activate account for therappy";
	public final static String SUBJECT_FORGOT_PASSWORD = "Forgot password for therappy";

	public final static String FILE_FORGOT_PASSWORD = FILE_PREFIX + "forgot-password.html";
	public final static String FILE_ACTIVATE_ACCOUNT = FILE_PREFIX + "activate-account.html";
}
