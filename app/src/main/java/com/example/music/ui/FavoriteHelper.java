package com.example.music.ui;


import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteHelper {

    private Context context;
    private ApiService apiService;
    private Long currentUserId = 1L;

    public FavoriteHelper(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    // Hàm xử lý Click tim
    public void toggleFavorite(Song song, ImageView btnFavorite, List<Long> likedIds) {
        // Kiểm tra xem bài hát có đang được like không
        boolean isLiked = likedIds.contains(song.getId());

        if (isLiked) {
            // --- TRƯỜNG HỢP BỎ THÍCH ---
            // 1. Cập nhật UI ngay
            btnFavorite.setImageResource(R.drawable.ic_heart_outline);
            btnFavorite.setColorFilter(Color.GRAY);
            likedIds.remove(song.getId()); // Xóa ID khỏi list

            // 2. Gọi API xóa
            apiService.removeFavorite(currentUserId, song.getId()).enqueue(new Callback<Void>() {
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
                    // Xử lý lỗi...
                }
            });

        } else {
            // --- TRƯỜNG HỢP THÍCH ---
            // 1. Cập nhật UI ngay
            btnFavorite.setImageResource(R.drawable.ic_heart_filled);
            btnFavorite.setColorFilter(Color.RED);
            likedIds.add(song.getId()); // Thêm ID vào list

            // 2. Gọi API thêm
            apiService.addFavorite(currentUserId, song.getId()).enqueue(new Callback<Void>() {
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
                    // Xử lý lỗi...
                }
            });
        }
    }
}