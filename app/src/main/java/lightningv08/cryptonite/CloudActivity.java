package lightningv08.cryptonite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

import lightningv08.cryptonite.databinding.ActivityCloudBinding;

public class CloudActivity extends AppCompatActivity {

    private ActivityCloudBinding binding;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private final static int UPLOAD_FILE_SELECT_CODE = 1;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCloudBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.authButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        binding.logoutButton.setOnClickListener(v -> logout());

        binding.uploadButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, R.string.not_authorized, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, UPLOAD_FILE_SELECT_CODE);
        });

        binding.downloadButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, R.string.not_authorized, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, DownloadActivity.class));
        });

        binding.deleteButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, R.string.not_authorized, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, DeleteActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.userEmail.setText(auth.getCurrentUser() == null
                ? getResources().getString(R.string.not_logged_in)
                : getResources().getString(R.string.logged_in_as) + " " + auth.getCurrentUser().getEmail());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPLOAD_FILE_SELECT_CODE:
                    uri = data.getData();
                    try {
                        String path = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser(), "user not logged in").getUid() + "/"
                                + Uri.fromFile(new File(new FileUtils(getApplicationContext()).getPath(uri)))
                                .getLastPathSegment();
                        UploadTask uploadTask = storageReference.child(path).putFile(uri);

                        uploadTask.addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        }).addOnFailureListener(exception -> {
                            Toast.makeText(CloudActivity.this, R.string.file_upload_failed, Toast.LENGTH_SHORT).show();
                        }).addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(CloudActivity.this, R.string.file_uploaded_successfully, Toast.LENGTH_SHORT).show();
                        });
                    } catch (RuntimeException e) {
                        Toast.makeText(this, R.string.file_upload_failed, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        binding.userEmail.setText(R.string.not_logged_in);
    }
}