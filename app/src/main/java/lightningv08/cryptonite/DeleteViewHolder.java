package lightningv08.cryptonite;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeleteViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    Button deleteButton;

    public DeleteViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        deleteButton = itemView.findViewById(R.id.delete_button);
    }
}
