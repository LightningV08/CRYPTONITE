package lightningv08.cryptonite;

import android.content.Context;
import android.net.Uri;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class FileEncrypter {

    public static SecretKey getKeyFromPassword(String password, String salt, int keyLength, int iterationCount) throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterationCount, keyLength);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public static SecretKey getKeyFromPassword(String password, String salt, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, keyLength);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public byte[] generateIV() throws NoSuchAlgorithmException {
        byte[] b = new byte[getIvSize()];
        SecureRandom.getInstanceStrong().nextBytes(b);
        return b;
    }

    protected static byte[] joinByteArray(byte[] byte1, byte[] byte2) {
        return ByteBuffer.allocate(byte1.length + byte2.length)
                .put(byte1)
                .put(byte2)
                .array();
    }

    protected byte[] readInputStreamBytes(InputStream inputStream) throws IOException {
        return ByteStreams.toByteArray(inputStream);
    }

    public abstract byte[] encrypt(SecretKey key, byte[] iv, byte[] msg)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException;

    public abstract byte[] decrypt(SecretKey key, byte[] iv, byte[] encrypted)
            throws IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchPaddingException, NoSuchAlgorithmException;

    public void encryptFileIv(Context context, Uri uri) throws IOException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        InputStream inputStream = context.getContentResolver().openInputStream(uri);

        byte[] s = readInputStreamBytes(inputStream);
        byte[] encrypted = encrypt(getKey(), getIv(), s);
        inputStream.close();
        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
        outputStream.write(joinByteArray(getIv(), encrypted));
        outputStream.close();
    }

    public void decryptFileIv(Context context, Uri uri) throws IOException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        byte[] s = readInputStreamBytes(inputStream);
        byte[] iv_ = Arrays.copyOfRange(s, 0, getIvSize());
        byte[] content = Arrays.copyOfRange(s, getIvSize(), s.length);
        byte[] decrypted = decrypt(getKey(), iv_, content);
        inputStream.close();
        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
        outputStream.write(decrypted);
        outputStream.close();
    }

    protected abstract SecretKey getKey();

    protected abstract byte[] getIv();

    protected abstract int getIvSize();
}
