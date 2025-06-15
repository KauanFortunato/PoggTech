package com.mordekai.poggtech.presentation.ui.bottomsheets;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mordekai.poggtech.utils.LocaleHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mordekai.poggtech.R;

import java.util.Locale;

public class ChangeLanguageBottomSheet extends BottomSheetDialogFragment {

    private ImageView iconPorguese, iconEnglish, iconSystem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_change_language, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iconPorguese = view.findViewById(R.id.icon_portuguese);
        iconEnglish = view.findViewById(R.id.icon_english);
        iconSystem = view.findViewById(R.id.icon_system);

        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String lang = prefs.getString("language", "system"); // ⬅️ padrão agora é "system"

        updateSelectedLanguageIcon(lang);

        view.findViewById(R.id.icon_portuguese).setOnClickListener(v -> {
            changeLanguage("pt");
        });

        view.findViewById(R.id.icon_english).setOnClickListener(v -> {
            changeLanguage("en");
        });

        view.findViewById(R.id.icon_system).setOnClickListener(v -> {
            changeLanguage("system");
        });
    }



    private void changeLanguage(String langCode) {
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (langCode.equals("system")) {
            // Salva como "system", mas aplica o idioma do sistema atual
            String systemLang = Locale.getDefault().getLanguage();
            LocaleHelper.setLocale(requireContext(), systemLang);
            editor.putString("language", "system"); // ⬅️ salva "system"
        } else {
            LocaleHelper.setLocale(requireContext(), langCode);
            editor.putString("language", langCode);
        }

        editor.apply();
        requireActivity().recreate();
        dismiss();
    }


    private void updateSelectedLanguageIcon(String selectedLang) {
        iconPorguese.setImageResource(R.drawable.ic_radio_unselected);
        iconEnglish.setImageResource(R.drawable.ic_radio_unselected);
        iconSystem.setImageResource(R.drawable.ic_radio_unselected);

        switch (selectedLang) {
            case "pt":
                iconPorguese.setImageResource(R.drawable.ic_radio_selected);
                break;
            case "en":
                iconEnglish.setImageResource(R.drawable.ic_radio_selected);
                break;
            case "system":
            default:
                iconSystem.setImageResource(R.drawable.ic_radio_selected);
                break;
        }
    }
}
