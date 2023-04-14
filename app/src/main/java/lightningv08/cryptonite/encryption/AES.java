package lightningv08.cryptonite.encryption;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES extends FileEncrypter {
    private final SecretKey key;
    private final byte[] iv;
    private static final String SALT = "!CRYPTONITE_AES!";

    public AES(@NonNull String key, byte[] iv) {
        this.key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        this.iv = iv;
    }

    public AES(SecretKey key, byte[] iv) {
        this.key = key;
        this.iv = iv;
    }

    public AES(String key) {
        try {
            this.key = getKeyFromPassword(key, SALT, 256);
            this.iv = generateIV();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey generateKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize);
        return keyGen.generateKey();
    }

    public byte[] encrypt(@NonNull SecretKey key, byte[] iv, byte[] msg)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        return cipher.doFinal(msg);
    }

    public byte[] decrypt(SecretKey key, byte[] iv, byte[] encrypted)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        return cipher.doFinal(encrypted);
    }

    @Override
    protected SecretKey getKey() {
        return key;
    }

    @Override
    protected byte[] getIv() {
        return iv;
    }

    @Override
    protected int getIvSize() {
        return 16;
    }
}