package com.example.demo.util;

import com.example.demo.exceptions.DecryptionException;
import com.example.demo.exceptions.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtil {

    private static final String ALGORITHM = "AES";

    private static final String SECRET = System.getenv("AES_SECRET") != null
            ? System.getenv("AES_SECRET")
            : "DefaultSecretKey";

    static {
        if (SECRET == null || SECRET.length() != 16) {
            throw new IllegalStateException(
                    "Variabila de mediu AES_SECRET trebuie să existe și să aibă EXACT 16 caractere!");
        }
    }

    private AESUtil(){}
    public static String encrypt(String data) throws EncryptionException {
        try{
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        }catch(Exception e){
            throw new EncryptionException(e.getMessage());
        }

    }

    public static String decrypt(String encryptedData) throws DecryptionException {
        try{
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decrypted, StandardCharsets.UTF_8);
        }catch (Exception e){
            throw new DecryptionException(e.getMessage());
        }

    }
}