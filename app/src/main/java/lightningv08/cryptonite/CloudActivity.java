package lightningv08.cryptonite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import lightningv08.cryptonite.databinding.ActivityCloudBinding;

public class CloudActivity extends AppCompatActivity {

    private ActivityCloudBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCloudBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        binding.authButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        binding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            binding.userEmail.setText(R.string.not_logged_in);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.userEmail.setText(auth.getCurrentUser() == null ? getResources().getString(R.string.not_logged_in) : "Logged in as " + auth.getCurrentUser().getEmail());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}