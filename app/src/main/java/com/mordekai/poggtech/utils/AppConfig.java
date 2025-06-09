package com.mordekai.poggtech.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_IP = "server_ip";
    private static String baseUrl = null;

    public static void initialize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String ip = prefs.getString(KEY_IP, "poggers.ddns.net"); // IP padrão
        baseUrl = "http://" + ip + "/PoggTech-APIs/public/";
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(Context context, String ip) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_IP, ip);
        editor.apply();

        // Atualiza a baseUrl globalmente
        baseUrl = "http://" + ip + "/PoggTech-APIs/public/";
    }
}
