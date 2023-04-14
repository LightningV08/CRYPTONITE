package lightningv08.cryptonite.encryption;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lightningv08.cryptonite.FileUtils;

public class RSA {

    public static KeyPair generateKeys(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keySize);
        return generator.generateKeyPair();
    }

    public static void storeKeys(@NonNull KeyPair pair, @NonNull Context context, @NonNull Uri uri) throws IOException {
        FileUtils fileUtils = new FileUtils(context);
        File file1 = new File(fileUtils.getPath(uri) + ".pubkey");
        File file2 = new File(fileUtils.getPath(uri) + ".privkey");
        file1.getParentFile().mkdirs();
        file1.createNewFile();
        file2.createNewFile();
        Uri uri1 = Uri.fromFile(file1);
        Uri uri2 = Uri.fromFile(file2);
        OutputStream inputStream1 = context.getContentResolver().openOutputStream(uri1);
        OutputStream inputStream2 = context.getContentResolver().openOutputStream(uri2);
        inputStream1.write(pair.getPublic().getEncoded());
        inputStream2.write(pair.getPrivate().getEncoded());
        inputStream1.close();
        inputStream2.close();
    }

    public static boolean areKeysStored(@NonNull Context context, @NonNull Uri uri) {
        FileUtils fileUtils = new FileUtils(context);
        boolean check1 = new File(fileUtils.getPath(uri) + ".pubkey").exists();
        boolean check2 = new File(fileUtils.getPath(uri) + ".privkey").exists();
        return check1 && check2;
    }

    public static void encryptFileIv(@NonNull Context context, Uri uri, PublicKey publicKey)
            throws IOException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        InputStream inputStream = context.getContentResolver().openInputStream(uri);

        byte[] s = FileEncrypter.readInputStreamBytes(inputStream);
        inputStream.close();
        byte[] encrypted = encrypt(publicKey, s);
        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
        outputStream.write(encrypted);
        outputStream.close();
    }

    public static void decryptFileIv(@NonNull Context context, Uri uri, PrivateKey privateKey)
            throws IOException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        InputStream inputStream = context.getContentResolver().openInputStream(uri);

        byte[] encrypted = FileEncrypter.readInputStreamBytes(inputStream);
        inputStream.close();
        byte[] decrypted = decrypt(privateKey, encrypted);
        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
        outputStream.write(decrypted);
        outputStream.close();
    }

    public static byte[] encrypt(PublicKey publicKey, byte[] msg)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(msg);
    }

    public static byte[] decrypt(PrivateKey privateKey, byte[] encrypted)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encrypted);
    }

    public static PublicKey getPublicKeyFromFile(Context context, Uri fileUri)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        FileUtils fileUtils = new FileUtils(context);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Files.readAllBytes(new File(fileUtils.getPath(fileUri) + ".pubkey").toPath()));
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static PublicKey getPublicKeyFromKeyFile(Context context, Uri keyUri)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        FileUtils fileUtils = new FileUtils(context);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Files.readAllBytes(new File(fileUtils.getPath(keyUri)).toPath()));
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static PrivateKey getPrivateKeyFromFile(Context context, Uri fileUri)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        FileUtils fileUtils = new FileUtils(context);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Files.readAllBytes(new File(fileUtils.getPath(fileUri) + ".privkey").toPath()));
        return keyFactory.generatePrivate(privateKeySpec);
    }

    public static PrivateKey getPrivateKeyFromKeyFile(Context context, Uri keyUri)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        FileUtils fileUtils = new FileUtils(context);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Files.readAllBytes(new File(fileUtils.getPath(keyUri)).toPath()));
        return keyFactory.generatePrivate(privateKeySpec);
    }
}
