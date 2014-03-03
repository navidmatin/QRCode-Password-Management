package com.facelock.facelocker;

public class LoginInformation {
	private String application;
	
	private String username;
	private String password;
	
	private String encryptionKey;
	
	public LoginInformation(String application, String username, String password){
		this.application = application;
		this.username = username;
		this.password = password;
	}

	
}
