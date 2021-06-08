package com.cubegames.engine.auth;

import com.cubegames.engine.utils.Utils;

import org.springframework.security.crypto.codec.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Cypher {

  /*
  token -> un-Base64 -> decode -> userId + expired data
  userId + expired data -> encode -> Base64 -> token
  */

  private final static String ALGORITHM = "AES/ECB/PKCS5Padding";
  private final static SecretKeySpec key = generateKey();
  public static final String CODE_PAGE = "UTF-8";


  public static String encode(String sourceValue) {
    Utils.checkNotNullOrEmpty(sourceValue);
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] bytes = cipher.doFinal(sourceValue.getBytes("UTF-8"));
      final String encryptedString = new String(Base64.encode(bytes));
      return encryptedString;
    } catch (Exception e) {
      //log.error("Unable to encrypt the given string", e);
    }
    return null;
  }


  public static String decode(String encodedValue) {
    Utils.checkNotNullOrEmpty(encodedValue);
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] bytes = cipher.doFinal(Base64.decode(encodedValue.getBytes()));
      final String decryptedString = new String(bytes, CODE_PAGE);
      return decryptedString;
    } catch (Exception e) {
      //log.error("Unable to decrypt the given string", e);
    }
    return null;
  }


  private static SecretKeySpec generateKey() {
    String passPhrase = "open source components for encoding";
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Cannot load SHA algorithm!");
    }
    digest.update(passPhrase.getBytes());
    return new SecretKeySpec(digest.digest(), 0, 16, "AES");
  }



}
