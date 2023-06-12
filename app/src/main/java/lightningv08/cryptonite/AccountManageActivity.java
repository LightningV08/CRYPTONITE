package lightningv08.cryptonite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

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
                String password = String.valueOf(binding.password.getText());
                if (password.isEmpty()) {
                    Toast.makeText(this, R.string.enter_pwd, Toast.LENGTH_SHORT).show();
                    return;
                }
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.reauthenticate(EmailAuthProvider.getCredential(
                        Objects.requireNonNull(user.getEmail(), "user not logged in"), password)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deleteAllFiles(false);
                        user.delete().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                logout();
                                Toast.makeText(this, R.string.successfully_deleted_account, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, R.string.failed_deleting_account, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.deleteAllDataButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, R.string.not_authorized, Toast.LENGTH_SHORT).show();
            } else {
                String password = String.valueOf(binding.password.getText());
                if (password.isEmpty()) {
                    Toast.makeText(this, R.string.enter_pwd, Toast.LENGTH_SHORT).show();
                    return;
                }
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.reauthenticate(EmailAuthProvider.getCredential(
                        Objects.requireNonNull(user.getEmail(), "user not logged in"), password)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deleteAllFiles(true);
                    } else {
                        Toast.makeText(AccountManageActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void deleteAllFiles(boolean toasts) {
        FirebaseStorage.getInstance().getReference().child(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser(), "user not logged in")
                        .getUid()).listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> list = listResult.getItems();
            for (StorageReference fileRef : list) {
                fileRef.delete();
            }
            FirebaseStorage.getInstance().getReference().child(
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser(), "user not logged in")
                            .getUid()).listAll().addOnSuccessListener(listResult1 -> {
                List<StorageReference> list1 = listResult1.getItems();
                if (list1.isEmpty()) {
                    if (toasts)
                        Toast.makeText(this, R.string.successfully_deleted_all_files_from_cloud, Toast.LENGTH_SHORT).show();
                } else {
                    if (toasts)
                        Toast.makeText(this, R.string.failed_deleting_all_files_from_cloud, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}