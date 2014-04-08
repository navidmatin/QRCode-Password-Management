package com.facelock.facelocker;

import java.io.File;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.content.SharedPreferences;
import android.util.Base64;

import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.BASE64DecoderStream;

public class PasswordCrypto {

	private static final String ENCRYPTION_KEY = "encryptionKey";
	private static SecretKey secretKey;
	private static Cipher eCipher;
	private static Cipher dCipher;
	
	public PasswordCrypto(String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		if(key==null)
		{
			try {
				secretKey = KeyGenerator.getInstance("DES").generateKey();
				eCipher = Cipher.getInstance("DES");
				dCipher = Cipher.getInstance("DES");
				
				eCipher.init(Cipher.ENCRYPT_MODE, secretKey);
				dCipher.init(Cipher.DECRYPT_MODE, secretKey);
			} catch (NoSuchAlgorithmException e) {
				throw new NoSuchAlgorithmException();
			} catch (NoSuchPaddingException e) {
				 throw new NoSuchPaddingException();
			} catch (InvalidKeyException e) {
				throw new InvalidKeyException();
			}
		}
		else
		{
			try {
				setKey(key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String encrypt (String str) throws Exception {
		try {
			byte[] utf8 = str.getBytes("UTF8");
			byte[] enc = eCipher.doFinal(utf8);
			enc = BASE64EncoderStream.encode(enc);
			return new String(enc);
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	public String decrypt (String str) throws Exception {
		try {
			byte[] dec = BASE64DecoderStream.decode(str.getBytes());
			byte[] utf8 = dCipher.doFinal(dec);
			return new String(utf8, "UTF8");
		} catch (Exception e) {
			throw e;
		}
	}
	public static String getKey() throws Exception{
		String s = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
		return s;
	}
	public static void setKey(String key) throws Exception {
		byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
		secretKey= new SecretKeySpec(encodedKey, 0, encodedKey.length, "DES");
	}
}
