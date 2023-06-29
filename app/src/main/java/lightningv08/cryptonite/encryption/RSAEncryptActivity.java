package lightningv08.cryptonite.encryption;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import lightningv08.cryptonite.databinding.ActivityRsaEncryptBinding;

public class RSAEncryptActivity extends AppCompatActivity {

    private ActivityRsaEncryptBinding binding;

    private final int FILE_SELECT_CODE = 1;

    private final int KEY_FILE_SELECT_CODE = 2;

    private Uri fileUri;

    private Uri keyUri;

    private boolean keySizeFragmentOpened = false;

    private Fragment chooseKeySizeFragment;

    private final AsyncExecutor executor = new AsyncExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRsaEncryptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.selectFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, FILE_SELECT_CODE);
        });
        binding.selectPubkeyFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, KEY_FILE_SELECT_CODE);
        });
        binding.generateKeyPair.setOnClickListener(v -> {
            if (!keySizeFragmentOpened) {
                if (fileUri == null) {
                    Toast.makeText(this, R.string.file_not_selected, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    startActivity(new Intent(this, LoginActivity.class));
                    return;
                }
                chooseKeySizeFragment = new RSAGenerateKeysFragment(getApplicationContext(), fileUri);
                getSupportFragmentManager().beginTransaction().replace(R.id.placeholder, chooseKeySizeFragment).commit();
                keySizeFragmentOpened = true;
            } else {
                getSupportFragmentManager().beginTransaction().remove(chooseKeySizeFragment).commit();
                keySizeFragmentOpened = false;
            }
        });
        binding.uploadButton.setOnClickListener(v -> {
            if (fileUri == null) {
                Toast.makeText(this, R.string.choose_file, Toast.LENGTH_SHORT).show();
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
        binding.encryptButton.setOnClickListener(v -> {
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
                        RSA.encryptFileIv(getApplicationContext(), fileUri, RSA.getPublicKeyFromKeyFile(getApplicationContext(), keyUri));
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
                            Toast.makeText(RSAEncryptActivity.this, R.string.file_encrypted, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, getIntent());
                        } else
                            Toast.makeText(RSAEncryptActivity.this, R.string.encryption_error, Toast.LENGTH_SHORT).show();
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
                    if (fileUtils.getPath(keyUri).endsWith(".privkey")) {
                        keyUri = null;
                        Toast.makeText(this, R.string.choose_pubkey, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }
}