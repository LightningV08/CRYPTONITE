package lightningv08.cryptonite.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import lightningv08.cryptonite.R;
import lightningv08.cryptonite.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    private boolean languageListOpened = false;

    private Fragment selectLanguageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.changeLanguageButton.setOnClickListener(v -> {
            if (!languageListOpened) {
                selectLanguageFragment = new SelectLanguageFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.placeholder, selectLanguageFragment).commit();
                languageListOpened = true;
            } else {
                getSupportFragmentManager().beginTransaction().remove(selectLanguageFragment).commit();
                languageListOpened = false;
            }
        });

        binding.backButton.setOnClickListener(v -> onBackPressed());
    }
}