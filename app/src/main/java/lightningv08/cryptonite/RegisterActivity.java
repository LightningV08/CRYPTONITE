package lightningv08.cryptonite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import lightningv08.cryptonite.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, R.string.already_logged_in, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), CloudActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.loginNow.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        binding.registerButton.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            String email = String.valueOf(binding.email.getText()),
                    password = String.valueOf(binding.password.getText()),
                    password_confirm = String.valueOf(binding.passwordConfirm.getText());
            if (email.isEmpty()) {
                Toast.makeText(RegisterActivity.this, R.string.enter_email, Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, R.string.enter_pwd, Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                return;
            }
            if (password_confirm.isEmpty()) {
                Toast.makeText(this, R.string.confirm_pwd, Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                return;
            }
            if (!password.equals(password_confirm)) {
                Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        binding.progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.account_created, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), CloudActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}