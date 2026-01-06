package com.example.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.music.model.Song;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecentSongManager {
    private static final String PREF_NAME = "MusicAppPref";
    private static final String KEY_RECENT = "recent_songs";
    private static final int MAX_SIZE = 10;

    // 1. HÀM LƯU BÀI HÁT
    public static void saveSong(Context context, Song song) {
        if (song == null) return;

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        List<Song> list = getRecentSongs(context);

        // Kiểm tra trùng lặp dựa trên ID
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() != null && list.get(i).getId().equals(song.getId())) {
                list.remove(i);
                break;
            }
        }

        list.add(0, song);

        if (list.size() > MAX_SIZE) {
            list = new ArrayList<>(list.subList(0, MAX_SIZE));
        }

        String json = gson.toJson(list);
        pref.edit().putString(KEY_RECENT, json).apply();
    }

    // 2. HÀM LẤY DANH SÁCH (Đã thêm bẫy lỗi try-catch)
    public static List<Song> getRecentSongs(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = pref.getString(KEY_RECENT, null);

        if (json == null) {
            return new ArrayList<>();
        }

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Song>>() {}.getType();
            List<Song> songs = gson.fromJson(json, type);
            return (songs != null) ? songs : new ArrayList<>();

        } catch (JsonSyntaxException e) {
            // 👇 ĐÂY LÀ PHẦN QUAN TRỌNG NHẤT:
            // Nếu dữ liệu cũ (String) không khớp cấu trúc mới (Object),
            // ta xóa dữ liệu lỗi đó đi và trả về danh sách rỗng để tránh văng App.
            clearHistory(context);
            return new ArrayList<>();
        }
    }

    public static void clearHistory(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().remove(KEY_RECENT).apply();
    }
}