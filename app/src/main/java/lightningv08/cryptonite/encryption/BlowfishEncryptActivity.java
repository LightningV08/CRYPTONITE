package lightningv08.cryptonite.encryption;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

import lightningv08.cryptonite.FileUtils;
import lightningv08.cryptonite.cloud.LoginActivity;
import lightningv08.cryptonite.passwordcheck.PasswordSafetyCheck;
import lightningv08.cryptonite.R;
import lightningv08.cryptonite.databinding.ActivityEncryptBinding;

public class BlowfishEncryptActivity extends AppCompatActivity {

    private ActivityEncryptBinding binding;
    private final int FILE_SELECT_CODE = 1;
    private Uri uri;
    private String password;
    private boolean buttonClickedInRockyou = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEncryptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.selectFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, FILE_SELECT_CODE);
        });
        binding.uploadButton.setOnClickListener(v -> {
            if (uri == null) {
                Toast.makeText(this, R.string.choose_file, Toast.LENGTH_SHORT).show();
                return;
            }
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            try {
                String path = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser(), "user not logged in").getUid() + "/"
                        + Uri.fromFile(new File(new FileUtils(getBaseContext()).getPath(uri)))
                        .getLastPathSegment();
                UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child(path).putFile(uri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this, R.string.file_uploaded_successfully, Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, R.string.file_upload_failed, Toast.LENGTH_SHORT).show();
                });
            } catch (RuntimeException e) {
                Toast.makeText(this, R.string.file_upload_failed, Toast.LENGTH_SHORT).show();
            }
        });
        binding.encryptButton.setOnClickListener(v -> {
            if (uri == null) {
                Toast.makeText(this, R.string.choose_file, Toast.LENGTH_SHORT).show();
                return;
            }
            password = binding.password.getText().toString();
            if (!password.isEmpty() && !password.equals(binding.confirmPassword.getText().toString())) {
                Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
                return;
            }
            String checkPasswordResult = PasswordSafetyCheck.checkPassword(password, this);
            if (checkPasswordResult.equals(getString(R.string.your_password_in_rockyou_passwords))) {
                if (!buttonClickedInRockyou) {
                    buttonClickedInRockyou = true;
                    Toast.makeText(this, R.string.your_password_in_rockyou_toast, Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                buttonClickedInRockyou = false;
            }
            if (checkPasswordResult.isEmpty() || buttonClickedInRockyou) {
                Blowfish blowfish = new Blowfish(password);
                try {
                    blowfish.encryptFileIv(getApplicationContext(), uri);
                    Toast.makeText(this, R.string.file_encrypted, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, getIntent());
                } catch (Exception e) {
                    Log.e("LightningV08", e.getMessage());
                    Toast.makeText(this, R.string.encryption_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, checkPasswordResult, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    uri = data.getData();
                    break;
            }
        }
    }
}