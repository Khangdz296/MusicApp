package com.example.music.ui;

import android.content.Context;
import android.content.SharedPreferences; // 👇 Nhớ import SharedPreferences
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteHelper {

    private Context context;
    private ApiService apiService;

    // 👇 1. XÓA dòng private Long currentUserId = 1L; đi nhé

    public FavoriteHelper(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    // 👇 2. THÊM HÀM LẤY DANH SÁCH ID (Để dùng cho onResume ở các Activity)
    public interface FavoriteCallback {
        void onLikedIdsLoaded(List<Long> likedIds);
    }

    public void getLikedSongIds(FavoriteCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        long realUserId = prefs.getLong("user_id", -1L);

        if (realUserId == -1L) {
            callback.onLikedIdsLoaded(new ArrayList<>());
            return;
        }

        apiService.getFavoriteSongs(realUserId).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                List<Long> ids = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    for (Song s : response.body()) {
                        ids.add(s.getId());
                    }
                }
                callback.onLikedIdsLoaded(ids);
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                callback.onLikedIdsLoaded(new ArrayList<>());
            }
        });
    }

    // 👇 3. SỬA HÀM TOGGLE ĐỂ CHECK LOGIN
    public void toggleFavorite(Song song, ImageView btnFavorite, List<Long> likedIds) {
        // A. LẤY ID THẬT
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        long realUserId = prefs.getLong("user_id", -1L);

        // B. CHECK ĐĂNG NHẬP
        if (realUserId == -1L) {
            Toast.makeText(context, "Vui lòng đăng nhập để thích bài hát!", Toast.LENGTH_SHORT).show();
            return; // 🛑 Dừng lại ngay
        }

        // C. LOGIC CŨ (Thay currentUserId bằng realUserId)
        boolean isLiked = likedIds.contains(song.getId());

        if (isLiked) {
            // --- BỎ THÍCH ---
            btnFavorite.setImageResource(R.drawable.ic_heart_outline);
            btnFavorite.setColorFilter(Color.GRAY);
            likedIds.remove(song.getId());

            apiService.removeFavorite(realUserId, song.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        // Lỗi -> Revert
                        btnFavorite.setImageResource(R.drawable.ic_heart_filled);
                        btnFavorite.setColorFilter(Color.RED);
                        likedIds.add(song.getId());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Xử lý lỗi
                }
            });

        } else {
            // --- THÍCH ---
            btnFavorite.setImageResource(R.drawable.ic_heart_filled);
            btnFavorite.setColorFilter(Color.RED);
            likedIds.add(song.getId());

            apiService.addFavorite(realUserId, song.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        // Lỗi -> Revert
                        btnFavorite.setImageResource(R.drawable.ic_heart_outline);
                        btnFavorite.setColorFilter(Color.GRAY);
                        likedIds.remove(song.getId());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Xử lý lỗi
                }
            });
        }
    }
}