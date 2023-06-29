package lightningv08.cryptonite.cloud.delete;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lightningv08.cryptonite.R;

public class DeleteViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    Button deleteButton;

    public DeleteViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        deleteButton = itemView.findViewById(R.id.delete_button);
    }
}
