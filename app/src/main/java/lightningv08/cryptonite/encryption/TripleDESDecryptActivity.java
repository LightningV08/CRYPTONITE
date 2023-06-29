package lightningv08.cryptonite.encryption;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lightningv08.cryptonite.AsyncExecutor;
import lightningv08.cryptonite.FileUtils;
import lightningv08.cryptonite.R;
import lightningv08.cryptonite.cloud.LoginActivity;
import lightningv08.cryptonite.databinding.ActivityDecryptBinding;

public class TripleDESDecryptActivity extends AppCompatActivity {

    private ActivityDecryptBinding binding;
    private final int FILE_SELECT_CODE = 1;
    private Uri uri;
    private String password;

    private final AsyncExecutor executor = new AsyncExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDecryptBinding.inflate(getLayoutInflater());
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
        binding.decryptButton.setOnClickListener(v -> {
            if (uri == null) {
                Toast.makeText(this, R.string.choose_file, Toast.LENGTH_SHORT).show();
                return;
            }
            password = binding.password.getText().toString();
            if (!password.isEmpty() && !password.equals(binding.confirmPassword.getText().toString())) {
                Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
                return;
            }
            executor.execute(new AsyncExecutor.AsyncExecutable() {
                private boolean finished = false;
                private boolean success = true;

                @Override
                public void doInBackground() {
                    TripleDES tripleDES = new TripleDES(password);
                    try {
                        tripleDES.decryptFileIv(getApplicationContext(), uri);
                    } catch (IOException | InvalidAlgorithmParameterException |
                             NoSuchPaddingException | IllegalBlockSizeException |
                             NoSuchAlgorithmException | BadPaddingException | InvalidKeyException |
                             NoSuchProviderException e) {
                        success = false;
                    }
                    finished = true;
                }

                @Override
                public void doInUIThread() {
                    if (finished) {
                        if (success) {
                            Toast.makeText(TripleDESDecryptActivity.this, R.string.file_decrypted, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, getIntent());
                        } else
                            Toast.makeText(TripleDESDecryptActivity.this, R.string.decryption_error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
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