package com.hcltech.digitalbankingservice.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; // Use CBC mode with PKCS5 padding
    private static final String FIXED_KEY = "1234567890123456"; // 16 bytes for AES-128
    private static final byte[] FIXED_IV = new byte[16]; // 16 bytes for AES block size (IV)

    private static final SecretKeySpec SECRET_KEY = new SecretKeySpec(FIXED_KEY.getBytes(), "AES");

    public static String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, new IvParameterSpec(FIXED_IV));
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting", e);
        }
    }

    public static String decrypt(String encryptedValue) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, new IvParameterSpec(FIXED_IV));
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedValue);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting", e);
        }
    }
}
