package lightningv08.cryptonite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import lightningv08.cryptonite.databinding.ActivityPermissionBinding;

public class PermissionActivity extends AppCompatActivity {
    private ActivityPermissionBinding binding;
    private static final int PERMISSION_STORAGE = 101;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("com.lightningv08.cryptonite", MODE_PRIVATE);

        binding.permissionButton.setOnClickListener(v -> {
            if (PermissionUtils.hasPermissions(this)) return;
            PermissionUtils.requestPermissions(this, PERMISSION_STORAGE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtils.hasPermissions(this)) {
            binding.permissionInfo.setText(R.string.permission_granted);
            binding.permissionInfo.invalidate();
            prefs.edit().putBoolean("granted", true).apply();
            finish();
        } else {
            binding.permissionInfo.setText(R.string.permission_not_granted);
        }
    }
}