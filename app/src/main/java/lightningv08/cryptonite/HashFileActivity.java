package lightningv08.cryptonite;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.bouncycastle.jcajce.provider.digest.Blake2b;
import org.bouncycastle.jcajce.provider.digest.Blake2s;
import org.bouncycastle.jcajce.provider.digest.GOST3411;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.jcajce.provider.digest.Whirlpool;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import lightningv08.cryptonite.databinding.ActivityHashFileBinding;

public class HashFileActivity extends AppCompatActivity {
    private ActivityHashFileBinding binding;
    private final int FILE_SELECT_CODE = 1;
    private Uri uri;
    private String hash_algorithm;

    private String hash = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHashFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hash_algorithm = getIntent().getStringExtra("hash_algorithm");
        binding.hashAlgorithmName.setText(getString(R.string.result_hash_algorithm, hash_algorithm));
        binding.selectFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, FILE_SELECT_CODE);
        });
        binding.copyButton.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (!Objects.equals(hash, "")) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("hash", binding.result.getText()));
                Toast.makeText(this, R.string.hash_copied, Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, R.string.calculate_hash_first, Toast.LENGTH_SHORT).show();
        });
        binding.hashButton.setOnClickListener(v -> doHashing());
    }

    @NonNull
    private String readInputStream(@NonNull InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    private void doHashing() {
        if (uri == null) return;
        String input = "";
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            input = readInputStream(inputStream);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), R.string.hashing_error, Toast.LENGTH_SHORT).show();
            Log.e("LightningV08", e.toString());
        }

        switch (hash_algorithm) {
            case "SHA-512":
            case "SHA-384":
            case "SHA-256":
            case "SHA-224":
            case "SHA-1":
            case "MD5":
                try {
                    MessageDigest md = MessageDigest.getInstance(hash_algorithm);
                    md.update(input.getBytes(StandardCharsets.UTF_8));
                    hash = Hex.toHexString(md.digest());
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(getApplicationContext(), R.string.hashing_error, Toast.LENGTH_SHORT).show();
                    Log.e("LightningV08", e.toString());
                }
                break;
            case "Whirlpool":
                Whirlpool.Digest mdWhirlpool = new Whirlpool.Digest();
                hash = Hex.toHexString(mdWhirlpool.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "GOST3411":
                GOST3411.Digest mdGost = new GOST3411.Digest();
                hash = Hex.toHexString(mdGost.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "RIPEMD160":
                RIPEMD160.Digest mdRipemd = new RIPEMD160.Digest();
                hash = Hex.toHexString(mdRipemd.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "Keccak512":
                Keccak.Digest512 mdKeccak512 = new Keccak.Digest512();
                hash = Hex.toHexString(mdKeccak512.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "Keccak384":
                Keccak.Digest384 mdKeccak384 = new Keccak.Digest384();
                hash = Hex.toHexString(mdKeccak384.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "Keccak256":
                Keccak.Digest256 mdKeccak256 = new Keccak.Digest256();
                hash = Hex.toHexString(mdKeccak256.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "Keccak224":
                Keccak.Digest224 mdKeccak224 = new Keccak.Digest224();
                hash = Hex.toHexString(mdKeccak224.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "Blake512":
                Blake2b.Blake2b512 mdBlake2b512 = new Blake2b.Blake2b512();
                hash = Hex.toHexString(mdBlake2b512.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "Blake384":
                Blake2b.Blake2b384 mdBlake2b384 = new Blake2b.Blake2b384();
                hash = Hex.toHexString(mdBlake2b384.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "Blake256":
                Blake2b.Blake2b256 mdBlake2b256 = new Blake2b.Blake2b256();
                hash = Hex.toHexString(mdBlake2b256.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "Blake224":
                Blake2s.Blake2s224 mdBlake2s224 = new Blake2s.Blake2s224();
                hash = Hex.toHexString(mdBlake2s224.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
            case "Blake128":
                Blake2s.Blake2s128 mdBlake2s128 = new Blake2s.Blake2s128();
                hash = Hex.toHexString(mdBlake2s128.digest(input.getBytes(StandardCharsets.UTF_8)));
                break;
        }
        binding.result.setText(hash);
        binding.copyButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    uri = data.getData();
                    doHashing();
                    break;
            }
        }
    }
}