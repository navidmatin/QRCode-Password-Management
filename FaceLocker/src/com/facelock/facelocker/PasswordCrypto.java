package com.facelock.facelocker;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.BASE64DecoderStream;

public class PasswordCrypto {

	private static SecretKey secretKey;
	private static Cipher eCipher;
	private static Cipher dCipher;
	
	public PasswordCrypto() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
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
}
