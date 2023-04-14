package lightningv08.cryptonite.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TripleDES extends FileEncrypter {

    private final SecretKey key;
    private final byte[] iv;
    private static final String SALT = "!CRYPTONITE_3DES";

    public TripleDES(String key) {
        try {
            this.key = getKeyFromPassword(key, SALT, 192);
            this.iv = generateIV();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encrypt(SecretKey key, byte[] iv, byte[] msg)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "DESede");
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        return cipher.doFinal(msg);
    }

    @Override
    public byte[] decrypt(SecretKey key, byte[] iv, byte[] encrypted)
            throws IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchPaddingException, NoSuchAlgorithmException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "DESede");
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

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
        return 8;
    }
}
