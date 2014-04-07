package com.facelock.facelocker;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Class that provides a static method to generate secure passwords of varying sizes.
 * @author Hector Medina-Fetterman
 * @version 1.0
 */
public class PasswordGenerator {
	private static final Character[] upperCase = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private static final Character[] lowerCase = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	private static final Character[] digits = {'0','1','2','3','4','5','6','7','8','9'};
	private static final Character[] symbols = {'`','!','"','?','$','%','^','&','*','(',')','_','-','+','=','{','[','}',']',':',';','@','\'','~','#','|','\\','<',',','>','.','/'};
	
	/**
	 * Generates a secure password using randomly selected characters.
	 * The length of the password is randomly chosen between [minLength, maxLength]
	 * and contains upper case letters, lower case letters, digits, and symbols.
	 * 
	 * @param minLength The minimum length the generated password should be
	 * @param maxLength The maximum length the generated password should be
	 * @return The randomly generated secure password.
	 */
	public static String Generate(int minLength, int maxLength){
		//Calculate a random integer for password length [minLength, maxLength]
		int length = minLength + (int)(Math.random() * ((maxLength - minLength) + 1));
		
		/*
		 * Calculate the number of each character type in the password. 
		 * Number of Lower Case characters : [1, Length - 3]
		 * Number of Upper Case characters : [1, Length - numLowerCase - 2]
		 * Number of Digit characters : [1, Length - numLowerCase - numUpperCase - 1]
		 * Left over character spaces are filled with symbols. Number is between [1, Length - 3],
		 * depending on previous selections.
		 * 
		 */
		Random random = new Random();
		
		int numLowerCase = random.nextInt(length - 3) + 1;
		int numUpperCase = random.nextInt(length - numLowerCase - 2) + 1;
		int numDigits = random.nextInt(length - numUpperCase - numLowerCase - 1) + 1;
		int numSymbols = length - numLowerCase - numUpperCase - numDigits;
		
		ArrayList<Character> passwordSet = new ArrayList<Character>();
		
		//add the characters in the correct amounts ot the password set
		for(int i = 0; i < numLowerCase; i++)
			passwordSet.add(lowerCase[random.nextInt(lowerCase.length)]);
		for(int i = 0; i < numUpperCase; i++)
			passwordSet.add(upperCase[random.nextInt(upperCase.length)]);
		for(int i = 0; i < numDigits; i++)
			passwordSet.add(digits[random.nextInt(digits.length)]);
		for(int i = 0; i < numSymbols; i++)
			passwordSet.add(symbols[random.nextInt(symbols.length)]);
		
		//shuffle the password using a Fisher-Yates Shuffle
		Collections.shuffle(passwordSet);
		
		//iterate and concatenate the string
		String password = "";
		for(Character c : passwordSet){
			password += c.toString();
		}
		
		return password;
	}
}
