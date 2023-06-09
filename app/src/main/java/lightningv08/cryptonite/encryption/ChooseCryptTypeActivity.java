package lightningv08.cryptonite.encryption;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import lightningv08.cryptonite.databinding.ChooseCryptTypeBinding;

public class ChooseCryptTypeActivity extends AppCompatActivity {

    private ChooseCryptTypeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChooseCryptTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String crypt_operation = getIntent().getStringExtra("crypt_operation");
        binding.backButton.setOnClickListener(v -> onBackPressed());
        binding.aesButton.setOnClickListener(v -> {
            switch (crypt_operation) {
                case "encrypt":
                    startActivity(new Intent(this, AESEncryptActivity.class));
                    break;
                case "decrypt":
                    startActivity(new Intent(this, AESDecryptActivity.class));
                    break;
            }
        });
        binding.rsaButton.setOnClickListener(v -> {
            switch (crypt_operation) {
                case "encrypt":
                    startActivity(new Intent(this, RSAEncryptActivity.class));
                    break;
                case "decrypt":
                    startActivity(new Intent(this, RSADecryptActivity.class));
                    break;
            }
        });
        binding.blowfishButton.setOnClickListener(v -> {
            switch (crypt_operation) {
                case "encrypt":
                    startActivity(new Intent(this, BlowfishEncryptActivity.class));
                    break;
                case "decrypt":
                    startActivity(new Intent(this, BlowfishDecryptActivity.class));
                    break;
            }
        });
        binding.twofishButton.setOnClickListener(v -> {
            switch (crypt_operation) {
                case "encrypt":
                    startActivity(new Intent(this, TwofishEncryptActivity.class));
                    break;
                case "decrypt":
                    startActivity(new Intent(this, TwofishDecryptActivity.class));
                    break;
            }
        });
        binding.tripleDesButton.setOnClickListener(v -> {
            switch (crypt_operation) {
                case "encrypt":
                    startActivity(new Intent(this, TripleDESEncryptActivity.class));
                    break;
                case "decrypt":
                    startActivity(new Intent(this, TripleDESDecryptActivity.class));
                    break;
            }
        });
        binding.desButton.setOnClickListener(v -> {
            switch (crypt_operation) {
                case "encrypt":
                    startActivity(new Intent(this, DESEncryptActivity.class));
                    break;
                case "decrypt":
                    startActivity(new Intent(this, DESDecryptActivity.class));
                    break;
            }
        });
        binding.gostButton.setOnClickListener(v -> {
            switch (crypt_operation) {
                case "encrypt":
                    startActivity(new Intent(this, GOSTEncryptActivity.class));
                    break;
                case "decrypt":
                    startActivity(new Intent(this, GOSTDecryptActivity.class));
                    break;
            }
        });
        binding.rc4Button.setOnClickListener(v -> {
            switch (crypt_operation) {
                case "encrypt":
                    startActivity(new Intent(this, RC4EncryptActivity.class));
                    break;
                case "decrypt":
                    startActivity(new Intent(this, RC4DecryptActivity.class));
                    break;
            }
        });
    }
}
