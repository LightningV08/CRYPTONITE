package lightningv08.cryptonite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import lightningv08.cryptonite.databinding.ActivityPermissionBinding;

public class PermissionActivity extends AppCompatActivity {
    private ActivityPermissionBinding binding;
    private static final int PERMISSION_STORAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (PermissionUtils.hasPermissions(this)) {
            binding.permissionInfo.setText("Разрешение получено");
        } else {
            binding.permissionInfo.setText("Разрешение не предоставлено");
        }

        binding.permissionButton.setOnClickListener(v -> {
            if (PermissionUtils.hasPermissions(this)) return;
            PermissionUtils.requestPermissions(this, PERMISSION_STORAGE);
        });
    }
}