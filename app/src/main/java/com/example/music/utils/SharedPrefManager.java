package com.example.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.music.model.User;
import com.google.gson.Gson;

public class SharedPrefManager {

    private static final String PREF_NAME = "MusicAppPrefs";
    private static final String KEY_USER = "user";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static SharedPrefManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Lưu User sau khi login thành công
     */
    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Lấy User object
     */
    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    /**
     * Lấy userId (dùng nhiều nhất)
     */
    public Long getUserId() {
        User user = getUser();
        return user != null ? user.getId() : -1L;
    }

    /**
     * Kiểm tra đã login chưa
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Logout
     */
    public void logout() {
        sharedPreferences.edit().clear().apply();
    }
}
