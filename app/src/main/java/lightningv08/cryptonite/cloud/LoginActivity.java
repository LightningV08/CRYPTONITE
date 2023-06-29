package lightningv08.cryptonite.cloud;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import lightningv08.cryptonite.R;
import lightningv08.cryptonite.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
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
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.registerNow.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        });

        binding.loginButton.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            String email = String.valueOf(binding.email.getText()), password = String.valueOf(binding.password.getText());

            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, R.string.enter_email, Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, R.string.enter_pwd, Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        binding.progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            checkIfEmailVerified();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (user.isEmailVerified() || TestEmailsContainer.testEmails.contains(user.getEmail())) {
            Toast.makeText(this, R.string.login_successful, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), CloudActivity.class));
            finish();
        } else {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, R.string.email_not_verified, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, CloudActivity.class));
        finish();
    }
}