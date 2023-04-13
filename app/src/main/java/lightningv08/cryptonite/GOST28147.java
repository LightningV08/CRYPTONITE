package lightningv08.cryptonite;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class GOST28147 extends FileEncrypter {

    private final SecretKey key;
    private final byte[] iv;
    private static final String SALT = "!CRYPTONITE_GOST";

    public GOST28147(String key) {
        try {
            this.key = getKeyFromPassword(key, SALT, 256);
            this.iv = generateIV();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encrypt(SecretKey key, byte[] iv, byte[] msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "GOST28147");
        Cipher cipher = Cipher.getInstance("GOST28147/CBC/PKCS5Padding", "SC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        return cipher.doFinal(msg);
    }

    @Override
    public byte[] decrypt(SecretKey key, byte[] iv, byte[] encrypted) throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "GOST28147");
        Cipher cipher = Cipher.getInstance("GOST28147/CBC/PKCS5Padding", "SC");
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
