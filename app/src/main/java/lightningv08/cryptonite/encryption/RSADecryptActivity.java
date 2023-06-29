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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lightningv08.cryptonite.utils.AsyncExecutor;
import lightningv08.cryptonite.utils.FileUtils;
import lightningv08.cryptonite.R;
import lightningv08.cryptonite.cloud.LoginActivity;
import lightningv08.cryptonite.databinding.ActivityRsaDecryptBinding;

public class RSADecryptActivity extends AppCompatActivity {

    private ActivityRsaDecryptBinding binding;

    private final int FILE_SELECT_CODE = 1;

    private final int KEY_FILE_SELECT_CODE = 2;

    private Uri fileUri;

    private Uri keyUri;

    private final AsyncExecutor executor = new AsyncExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRsaDecryptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.selectFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, FILE_SELECT_CODE);
        });
        binding.selectPrivkeyFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, KEY_FILE_SELECT_CODE);
        });
        binding.uploadButton.setOnClickListener(v -> {
            if (fileUri == null) {
                Toast.makeText(this, R.string.choose_file, Toast.LENGTH_SHORT).show();
                return;
            }
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            try {
                String path = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser(), "user not logged in").getUid() + "/"
                        + Uri.fromFile(new File(new FileUtils(getBaseContext()).getPath(fileUri)))
                        .getLastPathSegment();
                UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child(path).putFile(fileUri);

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
            if (fileUri == null) {
                Toast.makeText(this, R.string.choose_file, Toast.LENGTH_SHORT).show();
                return;
            }
            if (keyUri == null) {
                Toast.makeText(this, R.string.choose_key_file, Toast.LENGTH_SHORT).show();
                return;
            }
            executor.execute(new AsyncExecutor.AsyncExecutable() {
                private boolean finished = false;
                private boolean success = true;

                @Override
                public void doInBackground() {
                    try {
                        RSA.decryptFileIv(getApplicationContext(), fileUri, RSA.getPrivateKeyFromKeyFile(getApplicationContext(), keyUri));
                    } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException |
                             NoSuchAlgorithmException | BadPaddingException | InvalidKeyException |
                             InvalidKeySpecException e) {
                        success = false;
                    }
                    finished = true;
                }

                @Override
                public void doInUIThread() {
                    if (finished) {
                        if (success) {
                            Toast.makeText(RSADecryptActivity.this, R.string.file_decrypted, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, getIntent());
                        } else
                            Toast.makeText(RSADecryptActivity.this, R.string.decryption_error, Toast.LENGTH_SHORT).show();
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
                    fileUri = data.getData();
                    break;
                case KEY_FILE_SELECT_CODE:
                    keyUri = data.getData();
                    FileUtils fileUtils = new FileUtils(getApplicationContext());
                    if (fileUtils.getPath(keyUri).endsWith(".pubkey")) {
                        keyUri = null;
                        Toast.makeText(this, R.string.choose_privkey, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }
}