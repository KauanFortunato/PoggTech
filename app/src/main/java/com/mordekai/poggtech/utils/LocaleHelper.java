package com.mordekai.poggtech.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {

    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        context.getApplicationContext().createConfigurationContext(config);

        // Salva no SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        prefs.edit().putString("language", languageCode).apply();
    }

    public static void applySavedLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String language = prefs.getString("language", "system");

        if (language.equals("system")) {
            language = Locale.getDefault().getLanguage(); // pega do sistema
        }

        setLocale(context, language);
    }
}
