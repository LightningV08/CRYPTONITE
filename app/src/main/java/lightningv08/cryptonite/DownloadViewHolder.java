package lightningv08.cryptonite;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    Button downloadButton;

    public DownloadViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        downloadButton = itemView.findViewById(R.id.download_button);
    }
}
