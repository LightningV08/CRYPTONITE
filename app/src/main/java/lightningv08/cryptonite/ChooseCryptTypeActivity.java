package lightningv08.cryptonite;

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
        });
        binding.blowfishButton.setOnClickListener(v -> {
        });
        binding.twofishButton.setOnClickListener(v -> {
        });
        binding.des3Button.setOnClickListener(v -> {
        });
        binding.desButton.setOnClickListener(v -> {
        });
        binding.gostButton.setOnClickListener(v -> {
        });
        binding.rc4Button.setOnClickListener(v -> {
        });
    }
}
