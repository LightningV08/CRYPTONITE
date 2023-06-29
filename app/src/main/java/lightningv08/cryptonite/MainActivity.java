package lightningv08.cryptonite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.security.Security;
import java.util.Locale;

import lightningv08.cryptonite.cloud.CloudActivity;
import lightningv08.cryptonite.databinding.ActivityMainBinding;
import lightningv08.cryptonite.encryption.ChooseCryptTypeActivity;
import lightningv08.cryptonite.hash.ChooseHashInputFragment;
import lightningv08.cryptonite.passwordcheck.PasswordSafetyCheckActivity;
import lightningv08.cryptonite.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private ActivityMainBinding binding;

    private SharedPreferences prefs;

    private boolean hashButtonOpened = false;

    private Fragment chooseHashInputFragment;

    private static boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getBooleanExtra("stop", false)) {
            firstRun = false;
        }

        prefs = getSharedPreferences("com.lightningv08.cryptonite", MODE_PRIVATE);

        if (firstRun) {
            String language = prefs.getString("language", "en");
            updateResources(this, language);
            if (!language.equals("en")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("stop", true);
                startActivity(intent);
                finish();
                firstRun = false;
            }
        }

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
        binding.cloudButton.setOnClickListener(v -> startActivity(new Intent(this, CloudActivity.class)));
        binding.passwordSafetyCheckButton.setOnClickListener(v -> startActivity(new Intent(this, PasswordSafetyCheckActivity.class)));
        binding.settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("firstrun", true) || !prefs.getBoolean("granted", false)) {
            Intent intent = new Intent(this, PermissionActivity.class);
            startActivity(intent);
            prefs.edit().putBoolean("firstrun", false).apply();
        }
        if (prefs.getBoolean("language_changed", false)) {
            recreate();
            prefs.edit().putBoolean("language_changed", false).apply();
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}