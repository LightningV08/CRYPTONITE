package lightningv08.cryptonite.cloud.delete;

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

import lightningv08.cryptonite.cloud.download.DownloadModel;
import lightningv08.cryptonite.databinding.ActivityDeleteBinding;

public class DeleteActivity extends AppCompatActivity {

    private ActivityDeleteBinding binding;
    private final ArrayList<DownloadModel> deleteModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser(),
                        "user not logged in").getUid());
        binding.progressBar.setVisibility(View.VISIBLE);

        storageReference.listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> list = listResult.getItems();
            if (list.isEmpty()) {
                binding.progressBar.setVisibility(View.GONE);
                binding.noFilesText.setVisibility(View.VISIBLE);
            }
            for (StorageReference fileRef : list) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    deleteModels.add(new DownloadModel(uri.getLastPathSegment().split("/")[1], uri.toString()));
                }).addOnSuccessListener(uri -> {
                    deleteModels.sort((o1, o2) -> o1.name.compareToIgnoreCase(o2.name));
                    binding.recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    binding.recycler.setAdapter(new DeleteAdapter(deleteModels));
                    binding.progressBar.setVisibility(View.GONE);
                });
            }
        });

        binding.backButton.setOnClickListener(v -> onBackPressed());
    }
}