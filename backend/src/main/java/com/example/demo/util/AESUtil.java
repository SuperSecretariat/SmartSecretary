package com.example.demo.util;

import com.example.demo.exceptions.DecryptionException;
import com.example.demo.exceptions.EncryptionException;
import com.example.demo.exceptions.KeyDerivationException;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16;
    private static final int AES_KEY_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static String secret;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static void initialize(String secretKey) {
        secret = secretKey;
    }

    private AESUtil(){}

    private static byte[] deriveKey() throws KeyDerivationException {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("Encryption key is not initialized");
        }
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            byte[] key = new byte[AES_KEY_LENGTH];
            System.arraycopy(hash, 0, key, 0, AES_KEY_LENGTH);
            return key;
        }catch (NoSuchAlgorithmException e) {
            throw new KeyDerivationException(e.getMessage());
        }

    }

    public static String encrypt(String data) throws EncryptionException {
        try {
            byte[] iv = new byte[IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            SecretKey key = new SecretKeySpec(deriveKey(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new EncryptionException(e.getMessage());
        }
    }

    public static String decrypt(String encryptedData) throws DecryptionException {
        try {
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);

            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedData);
            byte[] iv = new byte[IV_LENGTH];
            byteBuffer.get(iv);

            byte[] encryptedBytes = new byte[decodedData.length - IV_LENGTH];
            byteBuffer.get(encryptedBytes);

            SecretKey key = new SecretKeySpec(deriveKey(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            byte[] decrypted = cipher.doFinal(encryptedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new DecryptionException(e.getMessage());
        }
    }
}