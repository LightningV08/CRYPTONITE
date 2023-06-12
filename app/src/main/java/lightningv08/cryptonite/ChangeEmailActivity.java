package lightningv08.cryptonite;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

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
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                user1.updateEmail(email).addOnCompleteListener(task1 -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, R.string.successfully_changed_email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.changing_email_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }
}