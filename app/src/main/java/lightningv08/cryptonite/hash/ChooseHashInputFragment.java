package lightningv08.cryptonite.hash;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import lightningv08.cryptonite.databinding.FragmentChooseHashInputBinding;

public class ChooseHashInputFragment extends Fragment {

    private FragmentChooseHashInputBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChooseHashInputBinding.inflate(inflater, container, false);
        binding.fileHash.setOnClickListener(v -> {
            Intent intent = new Intent(this.getContext(), ChooseHashActivity.class);
            intent.putExtra("hash_type", "file");
            startActivity(intent);
        });
        binding.textHash.setOnClickListener(v -> {
            Intent intent = new Intent(this.getContext(), ChooseHashActivity.class);
            intent.putExtra("hash_type", "text");
            startActivity(intent);
        });
        return binding.getRoot();
    }
}