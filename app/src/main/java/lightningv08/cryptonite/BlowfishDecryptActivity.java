package lightningv08.cryptonite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import lightningv08.cryptonite.databinding.ActivityDecryptBinding;

public class BlowfishDecryptActivity extends AppCompatActivity {

    private ActivityDecryptBinding binding;
    private final int FILE_SELECT_CODE = 1;
    private Uri uri;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDecryptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.chooseFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, FILE_SELECT_CODE);
        });
        binding.decryptButton.setOnClickListener(v -> {
            if (uri == null) {
                Toast.makeText(this, "Choose file", Toast.LENGTH_SHORT).show();
                return;
            }
            password = binding.password.getText().toString();
            if (!password.isEmpty() && !password.equals(binding.confirmPassword.getText().toString())) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                return;
            }
           Blowfish blowfish = new Blowfish(password);
            try {
                blowfish.decryptFileIv(getApplicationContext(), uri);
                Toast.makeText(this, "File decrypted", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, getIntent());
            } catch (Exception e) {
                Log.e("LightningV08", e.toString());
                Toast.makeText(this, "Decryption error", Toast.LENGTH_SHORT).show();
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