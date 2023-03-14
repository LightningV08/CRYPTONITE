package lightningv08.cryptonite;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.common.io.ByteStreams;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private final SecretKey key;
    private final byte[] iv;

    private static final String SALT = "!CRYPTONITE_AES!";

    public AES(String key, byte[] iv) {
        this.key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        this.iv = iv;
    }

    public AES(SecretKey key, byte[] iv) {
        this.key = key;
        this.iv = iv;
    }

    public AES(String key) {
        try {
            this.key = getKeyFromPassword(key, SALT);
            this.iv = generateIVBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey getKeyFromPassword(String password, String salt, int iterationCount) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterationCount, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public static SecretKey getKeyFromPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public static SecretKey generateKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize);
        return keyGen.generateKey();
    }

    public static String generateIV() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 16;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }

    public static byte[] generateIVBytes() throws NoSuchAlgorithmException {
        byte[] b = new byte[16];
        SecureRandom.getInstanceStrong().nextBytes(b);
        return b;
    }

    public void encryptFileIv(Context context, Uri uri) throws IOException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        InputStream inputStream = context.getContentResolver().openInputStream(uri);

        byte[] s = readInputStreamBytes(inputStream);
        byte[] encrypted = encrypt(this.key, this.iv, s);
        inputStream.close();
        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
        outputStream.write(joinByteArray(iv, encrypted));
        outputStream.close();
    }

    public void decryptFileIv(Context context, Uri uri) throws IOException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        byte[] s = readInputStreamBytes(inputStream);
        byte[] iv_ = Arrays.copyOfRange(s, 0, 16);
        byte[] content = Arrays.copyOfRange(s, 16, s.length);
        byte[] decrypted = decrypt(this.key, iv_, content);
        inputStream.close();
        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
        outputStream.write(decrypted);
        outputStream.close();
    }

    private static byte[] joinByteArray(byte[] byte1, byte[] byte2) {
        return ByteBuffer.allocate(byte1.length + byte2.length)
                .put(byte1)
                .put(byte2)
                .array();
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    private byte[] readInputStreamBytes(InputStream inputStream) throws IOException {
        return ByteStreams.toByteArray(inputStream);
    }

    private String getIvFromFile(String s) {
        //return s.substring(0, s.offsetByCodePoints(0, 16));
        return s.substring(0, 16);
    }

    private String getContentFromFile(String s) {
        return s.substring(16);
    }

    public static String encrypt(String key, String iv, String msg) throws Exception {
        byte[] bytesOfKey = key.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("SHA256");
        byte[] keyHash = md.digest(bytesOfKey);

        final byte[] ivBytes = iv.getBytes();

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyHash, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));

        final byte[] resultBytes = cipher.doFinal(msg.getBytes());
        return Base64.getMimeEncoder().encodeToString(resultBytes);
    }

    public static String encrypt(byte[] key, String iv, String msg) throws Exception {
        final byte[] ivBytes = iv.getBytes();

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));

        final byte[] resultBytes = cipher.doFinal(msg.getBytes());
        return Base64.getMimeEncoder().encodeToString(resultBytes);
    }

    public static String encrypt(SecretKey key, String iv, String msg)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        final byte[] ivBytes = iv.getBytes();

        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));

        final byte[] resultBytes = cipher.doFinal(msg.getBytes());
        return Base64.getEncoder().encodeToString(resultBytes);
        //return new String(resultBytes, StandardCharsets.UTF_8);
    }

    public static byte[] encrypt(SecretKey key, byte[] iv, byte[] msg)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        return cipher.doFinal(msg);
        //return new String(resultBytes, StandardCharsets.UTF_8);
    }

    public static byte[] decrypt(SecretKey key, byte[] iv, byte[] encrypted)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        return cipher.doFinal(encrypted);
    }

    public static String decrypt(SecretKey key, String iv, String encrypted)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        final byte[] ivBytes = iv.getBytes();

        final byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);
        //final byte[] encryptedBytes = encrypted.getBytes(StandardCharsets.UTF_8);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));

        final byte[] resultBytes = cipher.doFinal(encryptedBytes);
        //return new String(resultBytes, StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(resultBytes);
    }

    public static String decrypt(String key, String iv, String encrypted) throws Exception {
        byte[] bytesOfKey = key.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("SHA256");
        byte[] keyHash = md.digest(bytesOfKey);

        final byte[] ivBytes = iv.getBytes();

        final byte[] encryptedBytes = Base64.getMimeDecoder().decode(encrypted);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyHash, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));

        final byte[] resultBytes = cipher.doFinal(encryptedBytes);
        return new String(resultBytes);
    }

    public static String decrypt(byte[] key, String iv, String encrypted) throws Exception {
        final byte[] ivBytes = iv.getBytes();

        final byte[] encryptedBytes = Base64.getMimeDecoder().decode(encrypted);

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));

        final byte[] resultBytes = cipher.doFinal(encryptedBytes);
        return new String(resultBytes);
    }
}