package com.example.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.music.model.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecentSongManager {
    // Tên file lưu trữ (Giống tên cuốn sổ)
    private static final String PREF_NAME = "MusicAppPref";
    // Tên dòng lưu dữ liệu (Giống nhãn dán)
    private static final String KEY_RECENT = "recent_songs";
    // Giới hạn số lượng bài lưu lại (Ví dụ: chỉ nhớ 10 bài gần nhất)
    private static final int MAX_SIZE = 10;

    // 1. HÀM LƯU BÀI HÁT (Gọi khi bấm vào bài hát)
    public static void saveSong(Context context, Song song) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // Bước 1: Lấy danh sách cũ ra trước
        List<Song> list = getRecentSongs(context);

        // Bước 2: Kiểm tra xem bài này đã có trong danh sách chưa?
        // Nếu có rồi thì XÓA đi (để tí nữa thêm vào đầu danh sách cho nó mới nhất)
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(song.getId())) {
                list.remove(i);
                break;
            }
        }

        // Bước 3: Thêm bài mới vào ĐẦU danh sách (vị trí 0)
        list.add(0, song);

        // Bước 4: Nếu danh sách dài quá (hơn 10 bài) thì cắt bớt đuôi
        if (list.size() > MAX_SIZE) {
            list = list.subList(0, MAX_SIZE);
        }

        // Bước 5: Biến danh sách thành chuỗi JSON và lưu lại vào máy
        String json = gson.toJson(list);
        pref.edit().putString(KEY_RECENT, json).apply();
    }

    public static List<Song> getRecentSongs(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String json = pref.getString(KEY_RECENT, null);

        if (json == null) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Song>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void clearHistory(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().remove(KEY_RECENT).apply();
    }
}