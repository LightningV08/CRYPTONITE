package lightningv08.cryptonite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import lightningv08.cryptonite.databinding.ActivityAccountManageBinding;

public class AccountManageActivity extends AppCompatActivity {
    private ActivityAccountManageBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        binding.logoutButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, R.string.not_authorized, Toast.LENGTH_SHORT).show();
            } else {
                logout();
                finish();
            }
        });

        binding.changeEmailButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, R.string.not_authorized, Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, ChangeEmailActivity.class));
            }
        });

        binding.changePasswordButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, R.string.not_authorized, Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, ChangePasswordActivity.class));
            }
        });

        binding.deleteAccountButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, R.string.not_authorized, Toast.LENGTH_SHORT).show();
            } else {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.userEmail.setText(auth.getCurrentUser() == null
                ? getResources().getString(R.string.not_logged_in)
                : getResources().getString(R.string.logged_in_as) + " " + auth.getCurrentUser().getEmail());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        binding.userEmail.setText(R.string.not_logged_in);
    }
}