package lightningv08.cryptonite;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lightningv08.cryptonite.databinding.ActivityDownloadBinding;

public class DownloadActivity extends AppCompatActivity {

    private ActivityDownloadBinding binding;
    private final ArrayList<DownloadModel> downloadModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser(),
                        "user not logged in").getUid());
        binding.progressBar.setVisibility(View.VISIBLE);

        storageReference.listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> list = listResult.getItems();
            for (StorageReference fileRef : list) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadModels.add(new DownloadModel(uri.getLastPathSegment().split("/")[1], uri.toString()));
                }).addOnSuccessListener(uri -> {
                    binding.recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    binding.recycler.setAdapter(new DownloadAdapter(downloadModels));
                    binding.progressBar.setVisibility(View.GONE);
                });
            }
        });
    }
}