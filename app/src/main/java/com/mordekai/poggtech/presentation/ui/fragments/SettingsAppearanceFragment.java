package com.mordekai.poggtech.presentation.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;

public class SettingsAppearanceFragment extends Fragment {

    private SharedPreferences preferences;
    private ImageView iconThemeLight, iconThemeDark, iconThemeSystem;
    private View btnThemeLight, btnThemeDark, btnThemeSystem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_appearance, container, false);
        getActivity().findViewById(R.id.headerContainer).setVisibility(View.GONE);

        preferences = requireActivity().getSharedPreferences("app_prefs", MODE_PRIVATE);

        setupViews(view);
        setupListeners();
        loadSavedTheme();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().findViewById(R.id.headerContainer).setVisibility(View.GONE);
        ((BottomNavVisibilityController) requireActivity()).hideBottomNav();
    }

    private void setupViews(View view) {
        btnThemeLight = view.findViewById(R.id.button_theme_light);
        btnThemeDark = view.findViewById(R.id.button_theme_dark);
        btnThemeSystem = view.findViewById(R.id.button_theme_system);

        iconThemeLight = view.findViewById(R.id.icon_theme_light);
        iconThemeDark = view.findViewById(R.id.icon_theme_dark);
        iconThemeSystem = view.findViewById(R.id.icon_theme_system);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (v.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    private void setupListeners() {
        View.OnClickListener themeClickListener = v -> {
            int selectedTheme;
            if (v == btnThemeLight) {
                selectedTheme = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (v == btnThemeDark) {
                selectedTheme = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                selectedTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            saveThemeSelection(selectedTheme);
            updateSelectedIcon(selectedTheme);
        };

        btnThemeLight.setOnClickListener(themeClickListener);
        btnThemeDark.setOnClickListener(themeClickListener);
        btnThemeSystem.setOnClickListener(themeClickListener);
    }

    private void loadSavedTheme() {
        int savedTheme = preferences.getInt("selected_theme", -1);

        if (savedTheme == -1) {
            savedTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            preferences.edit().putInt("selected_theme", savedTheme).apply();
        }

        updateSelectedIcon(savedTheme);
    }

    private void saveThemeSelection(int themeMode) {
        int currentTheme = preferences.getInt("selected_theme", -1);

        if (currentTheme != themeMode) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("selected_theme", themeMode);
            editor.putBoolean("theme_changed", true);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(themeMode);
        }
    }

    private void updateSelectedIcon(int selectedTheme) {
        iconThemeLight.setImageResource(R.drawable.ic_radio_unselected);
        iconThemeDark.setImageResource(R.drawable.ic_radio_unselected);
        iconThemeSystem.setImageResource(R.drawable.ic_radio_unselected);

        if (selectedTheme == AppCompatDelegate.MODE_NIGHT_NO) {
            iconThemeLight.setImageResource(R.drawable.ic_radio_selected);
        } else if (selectedTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            iconThemeDark.setImageResource(R.drawable.ic_radio_selected);
        } else {
            iconThemeSystem.setImageResource(R.drawable.ic_radio_selected);
        }
    }
}