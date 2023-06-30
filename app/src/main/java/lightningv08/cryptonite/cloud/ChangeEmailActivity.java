package lightningv08.cryptonite.cloud;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import lightningv08.cryptonite.R;
import lightningv08.cryptonite.databinding.ActivityChangeEmailBinding;

public class ChangeEmailActivity extends AppCompatActivity {

    private ActivityChangeEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.changeEmailButton.setOnClickListener(v -> {
            String email = String.valueOf(binding.email.getText());
            String password = String.valueOf(binding.password.getText());
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            user.reauthenticate(EmailAuthProvider.getCredential(
                    Objects.requireNonNull(user.getEmail(), "user not logged in"), password)).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                    user1.updateEmail(email).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, R.string.successfully_changed_email, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, R.string.email_changing_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }
}