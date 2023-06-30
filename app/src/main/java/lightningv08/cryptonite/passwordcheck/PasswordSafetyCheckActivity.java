package lightningv08.cryptonite.passwordcheck;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import lightningv08.cryptonite.R;
import lightningv08.cryptonite.databinding.ActivityPasswordSafetyCheckBinding;

public class PasswordSafetyCheckActivity extends AppCompatActivity {
    private ActivityPasswordSafetyCheckBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordSafetyCheckBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.checkPasswordButton.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.result.setText("");
            String checkResult = PasswordSafetyCheck.checkPassword(String.valueOf(binding.password.getText()), this);
            if (checkResult.equals("")) {
                binding.result.setText(R.string.your_password_is_secure);
            } else {
                binding.result.setText(checkResult);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }
}