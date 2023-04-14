package lightningv08.cryptonite.encryption;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import lightningv08.cryptonite.R;
import lightningv08.cryptonite.databinding.FragmentRsaGenerateKeysBinding;

public class RSAGenerateKeysFragment extends Fragment {

    private FragmentRsaGenerateKeysBinding binding;

    private Context context;

    private Uri fileUri;

    public RSAGenerateKeysFragment(@NonNull Context context, @NonNull Uri fileUri) {
        this.context = context;
        this.fileUri = fileUri;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRsaGenerateKeysBinding.inflate(inflater, container, false);
        binding.generateKeyPair.setOnClickListener(v -> {
            try {
                int keySize = 2048;
                if (!binding.keySize.getText().toString().equals(""))
                    keySize = Integer.parseInt(binding.keySize.getText().toString());
                if (RSA.areKeysStored(context, fileUri))
                    showSimpleDialog(v.getContext(), keySize);
                else storeKeys(keySize);
            } catch (Exception e) {
                Log.e("LightningV08", e.getMessage());
                Toast.makeText(context, R.string.key_generation_error, Toast.LENGTH_SHORT).show();
            }
        });
        return binding.getRoot();
    }

    private void showSimpleDialog(Context context, int keySize) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);

        builder.setMessage(R.string.ask_generate_new_keys)
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    try {
                        storeKeys(keySize);
                    } catch (Exception e) {
                        Log.e("LightningV08", e.getMessage());
                        Toast.makeText(context, R.string.key_generation_error, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        alert.setTitle(R.string.keys_already_generated);
        alert.show();
    }

    private void storeKeys(int keySize) throws NoSuchAlgorithmException, IOException {
        RSA.storeKeys(RSA.generateKeys(keySize), context, fileUri);
    }
}