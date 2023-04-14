package lightningv08.cryptonite.encryption;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import lightningv08.cryptonite.R;
import lightningv08.cryptonite.databinding.ActivityEncryptBinding;

public class TripleDESEncryptActivity extends AppCompatActivity {

    private ActivityEncryptBinding binding;
    private final int FILE_SELECT_CODE = 1;
    private Uri uri;
    private String password;

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
            TripleDES tripleDES = new TripleDES(password);
            try {
                tripleDES.encryptFileIv(getApplicationContext(), uri);
                Toast.makeText(this, R.string.file_encrypted, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, getIntent());
            } catch (Exception e) {
                Log.e("LightningV08", e.getMessage());
                Toast.makeText(this, R.string.encryption_error, Toast.LENGTH_SHORT).show();
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