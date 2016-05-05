package com.stevenckwong.AlmRestfulClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

// import sun.misc.BASE64Decoder;
// import sun.misc.BASE64Encoder;

import java.util.Base64;

/************************************************************************************************************************
 * 
 * This tool is to provide the encryption and decryption facility so that passwords to access ALM using the
 * AlmRestfulClient.App utility can be obfuscated. 
 * This class should by right be deployed in its own JAR and obfuscated to prevent anyone who have access to it
 * to see the master password used for the encryption.
 * 
 * Credit goes to Johannes Brodwall - 
 * Post here http://stackoverflow.com/questions/1132567/encrypt-password-in-configuration-files
 * 
 * I just copied and pasted the code here :)
 * 
 * @author Administrator
 *
 ************************************************************************************************************************/

public class PasswordTool {

	    private static final char[] PASSWORD = "AlmRestfulClientPassword".toCharArray();
	    private static final byte[] SALT = {
	        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
	        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
	    };

	    public static void main(String[] args) throws Exception {
	        // String originalPassword = "secret";
	    	String originalPassword = args[0];
	    	
	    	if (originalPassword == null) {
	    		System.out.println("Please provide the password to encrypt as parameter. Eg.\n" + 
	    								"java com.stevenckwong.AlmRestfulClient.PasswordTool cleartextpassword");
	    	}
	        System.out.println("Original password: " + originalPassword);
	        String encryptedPassword = encrypt(originalPassword);
	        System.out.println("Encrypted password: " + encryptedPassword);
	        // String decryptedPassword = decrypt(encryptedPassword);
	        // System.out.println("Decrypted password: " + decryptedPassword);
	    }

	    public static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
	        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
	        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
	        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
	        return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
	    }

	    private static String base64Encode(byte[] bytes) {
	        // NB: This class is internal, and you probably should use another impl
	    	return Base64.getEncoder().encodeToString(bytes);
	        // return new BASE64Encoder.encode(bytes);
	    }

	    private static String decrypt(String property) throws GeneralSecurityException, IOException {
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
	        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
	        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
	        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
	        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	    }

	    private static byte[] base64Decode(String property) throws IOException {
	        // NB: This class is internal, and you probably should use another impl
	    	return Base64.getDecoder().decode(property);
	        // return new BASE64Decoder().decodeBuffer(property);
	    }

	}
	
