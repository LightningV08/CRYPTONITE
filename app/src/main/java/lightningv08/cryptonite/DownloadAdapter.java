package lightningv08.cryptonite;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadViewHolder> {

    private final ArrayList<DownloadModel> downloadModels;

    public DownloadAdapter(ArrayList<DownloadModel> downloadModels) {
        this.downloadModels = downloadModels;
    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
        holder.name.setText(downloadModels.get(position).name);
        holder.downloadButton.setOnClickListener(v -> {
            downloadFile(holder.name.getContext(), downloadModels.get(position).name,
                    downloadModels.get(position).url);
        });
    }

    public void downloadFile(Context context, String filename, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        downloadmanager.enqueue(request);
    }


    @Override
    public int getItemCount() {
        return downloadModels.size();
    }
}
