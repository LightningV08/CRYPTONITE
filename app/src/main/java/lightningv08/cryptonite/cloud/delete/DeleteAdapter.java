package lightningv08.cryptonite.cloud.delete;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import lightningv08.cryptonite.R;
import lightningv08.cryptonite.cloud.download.DownloadModel;

public class DeleteAdapter extends RecyclerView.Adapter<DeleteViewHolder> {

    private final ArrayList<DownloadModel> deleteModels;


    public DeleteAdapter(ArrayList<DownloadModel> deleteModels) {
        this.deleteModels = deleteModels;
    }

    @NonNull
    @Override
    public DeleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeleteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteViewHolder holder, int position) {
        holder.name.setText(deleteModels.get(position).name);
        holder.deleteButton.setOnClickListener(v -> {
            deleteFile(holder.name.getContext(), deleteModels.get(position).url);
            deleteModels.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, deleteModels.size());
        });
    }

    public void deleteFile(Context context, String url) {
        StorageReference deleteReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        deleteReference.delete().addOnSuccessListener(unused -> {
            Toast.makeText(context, "Successfully deleted file", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Error deleting file", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return deleteModels.size();
    }
}
