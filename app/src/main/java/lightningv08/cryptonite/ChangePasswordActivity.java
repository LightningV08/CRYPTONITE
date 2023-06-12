package lightningv08.cryptonite;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import lightningv08.cryptonite.databinding.ActivityChangePasswordBinding;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.changePasswordButton.setOnClickListener(v -> {
            String currentPassword = String.valueOf(binding.currentPassword.getText());
            String newPassword = String.valueOf(binding.newPassword.getText());
            String confirmNewPassword = String.valueOf(binding.confirmPassword.getText());
            if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            user.reauthenticate(EmailAuthProvider.getCredential(
                    Objects.requireNonNull(user.getEmail(), "user not logged in"), currentPassword)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                    user1.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, R.string.successfully_changed_password, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, R.string.password_changing_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ChangePasswordActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}