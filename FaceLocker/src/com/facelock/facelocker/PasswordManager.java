package com.facelock.facelocker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;

/**
 * Singleton class that manages login information. 
 * Login information is mapped to an application for sorting and searching by the user.
 * Multiple passwords can be stored for a single application.
 * 
 * @Version 1.0
 * @author Hector Medina-Fetterman
 */
public class PasswordManager {
	private static String _key;
	private static PasswordManager instance = new PasswordManager(_key);
	private HashMap<String, HashMap<String, LoginInformation>> passwordMap;
	
	
	private PasswordManager(String key){
		passwordMap = new HashMap<String, HashMap<String, LoginInformation>>();
	}
	
	/**
	 * @return The singleton instance of the PasswordManager class
	 */
	public static PasswordManager getInstance(String key){
		return instance;
	}
	
	private static String getApplicationUsernameKey (String application, String username) {
		return application + "#" + username;
	}
	
	/**
	 * Stores login information in the password manager for later retrieval and storage.
	 * Information is encrypted on storage.
	 * 
	 * @param application The application associated with the login information
	 * @param username The username used to login to the application
	 * @param password The unencrypted password used to login to the application
	 */

	public Boolean storeLogin(String application, String username, String password, SharedPreferences logins) {
		String key = getApplicationUsernameKey(application, username);
		SharedPreferences.Editor editor = logins.edit();
		String encryptedPassword;
		try {
			encryptedPassword = PasswordCrypto.encrypt(password, "000102030405060708090A0B0C0D0E0F");

		} catch (Exception e) {
			return false;
		}
		
		editor.putString(key, encryptedPassword);
    	return editor.commit();
	}
	
	
	public void getLogins(SharedPreferences logins) {
		Map<String, ?> pairs = logins.getAll();
    	Set<String> keys = pairs.keySet();
    	
    	for (String key : keys) {
    		String[] appUserCombo = key.split("#", 2);
    		HashMap<String, LoginInformation> applicationMap = passwordMap.get(appUserCombo[0]);
    		if(applicationMap == null)
    			applicationMap = new HashMap<String, LoginInformation>();
    		applicationMap.put(appUserCombo[1], new LoginInformation(appUserCombo[0], appUserCombo[1], pairs.get(key).toString()));
    		passwordMap.put(appUserCombo[0], applicationMap);
    	}
	}
	
	public String getPassword(String application, String username, SharedPreferences logins) {
		String applicationUsernameKey = getApplicationUsernameKey(application, username);
		String encryptedPassword = logins.getString(applicationUsernameKey, "");
		String decryptedPassword;
		try {
			decryptedPassword = PasswordCrypto.decrypt(encryptedPassword, "000102030405060708090A0B0C0D0E0F");

		} catch (Exception e) {
			decryptedPassword = e.toString();
		}
		return decryptedPassword;
	}
	
	public static Boolean deleteAll (SharedPreferences logins) {
		SharedPreferences.Editor editor = logins.edit();
		editor.clear();
		instance = new PasswordManager(null);
    	return editor.commit();
	}
	
	
	/**
	 * Retrieves the login information for a certain application and username.
	 * 
	 * @param application The application name
	 * @param username The username for the given login information
	 * @return The login information associated with the application and username, or null if no information exists
	 *//*
	public LoginInformation getLogin(String application, String username){
		HashMap<String, LoginInformation> applicationMap = passwordMap.get(application);
		if(applicationMap == null)
			return null;
		LoginInformation val = applicationMap.get(username);
		return val;
	}
	
	*//**
	 * Retrieves a HashMap of usernames to login information for the given application name.
	 * 
	 * @param application The application name
	 * @return A mapping of usernames to login information for a given application
	 *//*
	public HashMap<String, LoginInformation> getLogins(String application){
		return new HashMap<String,LoginInformation>(passwordMap.get(application));
	}*/
	
	public HashMap<String, List<String>> getMap(){
		HashMap<String, List<String>> val = new HashMap<String, List<String>>();
		for(String key : passwordMap.keySet()){
			Set<String> temp = passwordMap.get(key).keySet();
			val.put(key, Arrays.asList(temp.toArray(new String[temp.size()])));
		}
		return val;
	}
	
	public int size(){
		return passwordMap.size();
	}
	
	public String[] getApplications(){
		return passwordMap.keySet().toArray(new String[passwordMap.size()]);
	}
}
