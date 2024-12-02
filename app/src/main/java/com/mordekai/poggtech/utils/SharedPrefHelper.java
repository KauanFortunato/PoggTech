package com.mordekai.poggtech.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.mordekai.poggtech.model.User;


public class SharedPrefHelper {
    private static final String PREF_NAME = "UserPrefs";

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public SharedPrefHelper(Context context) {
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        gson = new Gson();
    }

    // TODO: 02/12/2024 Sempre que for criar um novo usuário, é importante apagar o ultimo
    public void saveUser(User user) {
        clearUser();
        String userJson = gson.toJson(user);
        editor.putString("user", userJson);
        editor.apply();
    }

    public User getUser() {
        String userJson = sharedPref.getString("user", null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void clearUser() {
        editor.remove("user");
        editor.apply();
    }
}
