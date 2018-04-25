
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author whb108
 */
public class EncryptDecrypt {

    public String encryptString(String plainText, SecretKey secretKey)
            throws GeneralSecurityException, IOException {
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] textBytes = plainText.getBytes("UTF-8");
        byte[] encryptedBytes = cipher.doFinal(textBytes);

        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedBytes);
        return encryptedText;
    }

    public String decryptString(String encryptedText, SecretKey secretKey)
            throws GeneralSecurityException, IOException {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedBytes = decoder.decode(encryptedText);

        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        Charset utf8 = StandardCharsets.UTF_8;
        CharBuffer decryptedChars = utf8.decode(ByteBuffer.wrap(decryptedBytes));
        String decrypt = new String(decryptedChars.array());
        return decrypt;
    }

    public SecretKey buildKey(long keySeed) throws GeneralSecurityException {
        SecretKey secretKey = null;
        long secretSeed = keySeed;
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(secretSeed);
        keyGenerator.init(128, random);
        secretKey = keyGenerator.generateKey();

        return secretKey;
    }


} // EncryptDecrypt
