package lightningv08.cryptonite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import lightningv08.cryptonite.databinding.ActivityPermissionBinding;
import lightningv08.cryptonite.utils.PermissionUtils;

public class PermissionActivity extends AppCompatActivity {
    private ActivityPermissionBinding binding;
    private static final int PERMISSION_STORAGE = 101;
    private SharedPreferences prefs;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("com.lightningv08.cryptonite", MODE_PRIVATE);

        binding.permissionButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
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