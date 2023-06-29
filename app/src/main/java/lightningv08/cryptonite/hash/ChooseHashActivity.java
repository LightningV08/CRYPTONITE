package lightningv08.cryptonite.hash;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import lightningv08.cryptonite.databinding.ChooseHashTypeBinding;

public class ChooseHashActivity extends AppCompatActivity {
    private ChooseHashTypeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChooseHashTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String hashType = getIntent().getStringExtra("hash_type");
        binding.sha512Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "SHA-512");
            startActivity(intent);
        });
        binding.sha384Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "SHA-384");
            startActivity(intent);
        });
        binding.sha256Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "SHA-256");
            startActivity(intent);
        });
        binding.sha224Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "SHA-224");
            startActivity(intent);
        });
        binding.sha1Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "SHA-1");
            startActivity(intent);
        });
        binding.md5Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "MD5");
            startActivity(intent);
        });
        binding.whirlpoolButton.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Whirlpool");
            startActivity(intent);
        });
        binding.gost3411Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "GOST3411");
            startActivity(intent);
        });
        binding.ripemd160Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "RIPEMD160");
            startActivity(intent);
        });
        binding.keccak512Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Keccak512");
            startActivity(intent);
        });
        binding.keccak384Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Keccak384");
            startActivity(intent);
        });
        binding.keccak256Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Keccak256");
            startActivity(intent);
        });
        binding.keccak224Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Keccak224");
            startActivity(intent);
        });
        binding.blake512Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Blake512");
            startActivity(intent);
        });
        binding.blake384Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Blake384");
            startActivity(intent);
        });
        binding.blake256Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Blake256");
            startActivity(intent);
        });
        binding.blake224Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Blake224");
            startActivity(intent);
        });
        binding.blake160Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Blake160");
            startActivity(intent);
        });
        binding.blake128Button.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(hashType, "text")) intent = new Intent(this, HashTextActivity.class);
            else intent = new Intent(this, HashFileActivity.class);
            intent.putExtra("hash_algorithm", "Blake128");
            startActivity(intent);
        });
    }
}
