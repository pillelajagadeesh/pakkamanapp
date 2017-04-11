package com.mistminds.domain;

public class Welcome {
    private String id;
	private String message;
	private String status;
	private String authToken;
	private String monthlycredit;

	public Welcome() {
	}

	public Welcome(final String message) {
		this.message = message;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Welcome(final String message, final String authToken) {
		this.message = message;
		this.authToken = authToken;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getMonthlycredit() {
		return monthlycredit;
	}

	public void setMonthlycredit(String monthlycredit) {
		this.monthlycredit = monthlycredit;
	}

}
