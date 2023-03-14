package lightningv08.cryptonite;

import android.content.Context;
import android.net.Uri;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
            this.iv = generateIV();
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

    public static byte[] generateIV() throws NoSuchAlgorithmException {
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

    private byte[] readInputStreamBytes(InputStream inputStream) throws IOException {
        return ByteStreams.toByteArray(inputStream);
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
}