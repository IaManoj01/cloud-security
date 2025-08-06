package com.project.cloud.utils;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@Component
public class AESUtil {

    public static final int IV_LENGTH = 12;          // 12 bytes for GCM IV
    public static final int TAG_LENGTH_BIT = 128;    // 16 bytes auth tag

    public static class EncryptionResult {
        private final byte[] iv;
        private final byte[] cipherText;

        public EncryptionResult(byte[] iv, byte[] cipherText) {
            this.iv = iv;
            this.cipherText = cipherText;
        }

        public byte[] getIv() {
            return iv;
        }

        public byte[] getCipherText() {
            return cipherText;
        }
    }

    public EncryptionResult encrypt(byte[] plaintext, SecretKey key) throws Exception {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(plaintext);

        return new EncryptionResult(iv, encrypted);
    }

    public byte[] decrypt(byte[] cipherText, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        return cipher.doFinal(cipherText);
    }
}





//@Component
//public class AESUtil {
//
//    private static final int AES_KEY_SIZE = 128; // bits
//    private static final int GCM_TAG_LENGTH = 128; // bits
//    private static final int GCM_IV_LENGTH = 12; // bytes
//
//    public byte[] encrypt(byte[] data, byte[] keyBytes) throws Exception {
//        SecretKey key = new SecretKeySpec(keyBytes, 0, AES_KEY_SIZE / 8, "AES");
//
//        byte[] iv = new byte[GCM_IV_LENGTH];
//        new SecureRandom().nextBytes(iv);
//
//        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
//        GCMParameterSpec paramSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
//
//        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
//        byte[] encrypted = cipher.doFinal(data);
//
//        byte[] result = new byte[iv.length + encrypted.length];
//        System.arraycopy(iv, 0, result, 0, iv.length);
//        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
//
//        return result;
//    }
//
//    public byte[] decrypt(byte[] encryptedData, byte[] keyBytes) throws Exception {
//        SecretKey key = new SecretKeySpec(keyBytes, 0, AES_KEY_SIZE / 8, "AES");
//
//        byte[] iv = Arrays.copyOfRange(encryptedData, 0, GCM_IV_LENGTH);
//        byte[] actualEncrypted = Arrays.copyOfRange(encryptedData, GCM_IV_LENGTH, encryptedData.length);
//
//        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
//        GCMParameterSpec paramSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
//
//        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
//        return cipher.doFinal(actualEncrypted);
//    }
//}
