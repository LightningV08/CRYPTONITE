package lightningv08.cryptonite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.security.Security;

import lightningv08.cryptonite.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private ActivityMainBinding binding;

    private SharedPreferences prefs;

    private boolean hashButtonOpened = false;

    private Fragment chooseHashInputFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("com.lightningv08.cryptonite", MODE_PRIVATE);

        binding.encryptButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChooseCryptTypeActivity.class);
            intent.putExtra("crypt_operation", "encrypt");
            startActivity(intent);
        });
        binding.decryptButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChooseCryptTypeActivity.class);
            intent.putExtra("crypt_operation", "decrypt");
            startActivity(intent);
        });
        binding.hashButton.setOnClickListener(v -> {
            if (!hashButtonOpened) {
                chooseHashInputFragment = new ChooseHashInputFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.placeholder, chooseHashInputFragment).commit();
                hashButtonOpened = true;
            } else {
                getSupportFragmentManager().beginTransaction().remove(chooseHashInputFragment).commit();
                hashButtonOpened = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("firstrun", true) || !prefs.getBoolean("granted", false)) {
            Intent intent = new Intent(this, PermissionActivity.class);
            startActivity(intent);
            prefs.edit().putBoolean("firstrun", false).apply();
        }
    }
}