package lightningv08.cryptonite;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import lightningv08.cryptonite.databinding.FragmentSelectLanguageBinding;

public class SelectLanguageFragment extends Fragment {
    private FragmentSelectLanguageBinding binding;

    private SharedPreferences prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelectLanguageBinding.inflate(inflater, container, false);
        prefs = requireContext().getSharedPreferences("com.lightningv08.cryptonite", Context.MODE_PRIVATE);

        binding.english.setOnClickListener(v -> updateResources(requireContext(), "en"));

        binding.chinese.setOnClickListener(v -> updateResources(requireContext(), "zh"));

        binding.french.setOnClickListener(v -> updateResources(requireContext(), "fr"));

        binding.german.setOnClickListener(v -> updateResources(requireContext(), "de"));

        binding.hindi.setOnClickListener(v -> updateResources(requireContext(), "hi"));

        binding.indonesian.setOnClickListener(v -> updateResources(requireContext(), "in"));

        binding.korean.setOnClickListener(v -> updateResources(requireContext(), "ko"));

        binding.portuguese.setOnClickListener(v -> updateResources(requireContext(), "pt"));

        binding.russian.setOnClickListener(v -> updateResources(requireContext(), "ru"));

        binding.spanish.setOnClickListener(v -> updateResources(requireContext(), "es"));

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        prefs.edit().putString("language", language).apply();
        prefs.edit().putBoolean("language_changed", true).apply();
        prefs.edit().putBoolean("was_recreated", false).apply();
        requireActivity().recreate();
    }
}