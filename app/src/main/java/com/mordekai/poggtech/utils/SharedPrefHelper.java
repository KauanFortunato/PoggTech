package com.mordekai.poggtech.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.mordekai.poggtech.data.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


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

    //User
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


    // ---------- Search History ----------

    public void addSearchHistory(String query) {
        List<String> historyList = getRawSearchHistory(); // <- usar raw

        // Remover o termo se já existir
        historyList.removeIf(item -> item.equals(query));

        // Adicionar ao fim (mais recente)
        historyList.add(query);

        saveSearchHistoryList(historyList);
    }

    public void removeItemFromHistory(String query) {
        List<String> historyList = getRawSearchHistory(); // <- usar raw

        historyList.removeIf(item -> item.equals(query));

        saveSearchHistoryList(historyList);
    }

    public void clearSearchHistory() {
        editor.remove("searchHistory");
        editor.apply();
    }

    public List<String> getSearchHistory() {
        List<String> historyList = getRawSearchHistory();
        Collections.reverse(historyList);
        return historyList;
    }

    private List<String> getRawSearchHistory() {
        String historyJson = sharedPref.getString("searchHistory", null);
        if (historyJson != null) {
            String[] historyArray = gson.fromJson(historyJson, String[].class);
            return new ArrayList<>(Arrays.asList(historyArray));
        }
        return new ArrayList<>();
    }

    private void saveSearchHistoryList(List<String> historyList) {
        String json = gson.toJson(historyList.toArray(new String[0]));
        editor.putString("searchHistory", json);
        editor.apply();
    }

}
