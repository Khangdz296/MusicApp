package com.example.music.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.adapter.SongAdapterK;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Song;
import com.example.music.ui.AddToPlaylistHelper;
import com.example.music.utils.MiniPlayerManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteSongsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SongAdapterK adapter;
    private AddToPlaylistHelper addToPlaylistHelper;
    private FavoriteHelper favoriteHelper; // 1. Khai báo
    private List<Song> listSong = new ArrayList<>();
    private List<Long> likedSongIds = new ArrayList<>();
    private TextView tvEmptyNotify; // Thêm text thông báo nếu rỗng

    // Giả lập User ID (Sau này lấy từ SharedPreferences khi login xong)
    private Long currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Bạn có thể cần thêm TextView id tvEmptyNotify vào layout fragment nếu muốn hiển thị "Chưa có bài hát nào"
        // Ở đây tui tạo Recycler view bằng code như bạn làm, nhưng tốt nhất nên có file XML layout.

        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setBackgroundColor(0xFF121212);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addToPlaylistHelper = new AddToPlaylistHelper(getContext());
        favoriteHelper = new FavoriteHelper(getContext()); // 2. Khởi tạo

        // Setup Adapter rỗng trước
        adapter = new SongAdapterK(getContext(), listSong, new SongAdapterK.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                int position = listSong.indexOf(song);
                MiniPlayerManager.getInstance().playSong(song, listSong, position);

                Intent intent = new Intent(getContext(), PlayMusicActivity.class);
                intent.putExtra("song_data", song);
                intent.putExtra("song_list", new ArrayList<>(listSong));
                intent.putExtra("current_position", position);
                startActivity(intent);
            }

            @Override
            public void onAddToPlaylistClick(Song song) {
                addToPlaylistHelper.showAddToPlaylistDialog(song);
            }
            @Override
            public void onFavoriteClick(Song song, ImageView btnFavorite, List<Long> ids) {
                // 👇 GỌI HELPER VỚI DANH SÁCH ID
                favoriteHelper.toggleFavorite(song, btnFavorite, ids);
            }

        });
        recyclerView.setAdapter(adapter);

        // GỌI API LẤY DỮ LIỆU THẬT
        fetchFavoriteSongs();

        return recyclerView;
    }

    // Load lại danh sách khi quay lại màn hình (đề phòng user bỏ like ở màn hình player)
    @Override
    public void onResume() {
        super.onResume();
        fetchFavoriteSongs();
    }

    private void fetchFavoriteSongs() {
        // 👇 1. LẤY ID THẬT TỪ SHAREDPREFERENCES
        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        long realUserId = prefs.getLong("user_id", -1L);

        // 👇 2. KIỂM TRA ĐĂNG NHẬP
        if (realUserId == -1L) {
            // Nếu chưa đăng nhập: Xóa sạch dữ liệu
            listSong.clear();
            likedSongIds.clear();
            if (adapter != null) {
                adapter.updateData(listSong);
                adapter.setLikedSongIds(likedSongIds);
            }
            // Toast.makeText(getContext(), "Vui lòng đăng nhập để xem bài hát yêu thích", Toast.LENGTH_SHORT).show();
            return;
        }

        // 👇 3. GỌI API VỚI ID THẬT
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getFavoriteSongs(realUserId).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listSong = response.body();

                    // 👇 CẬP NHẬT LIST ID ĐỂ ADAPTER BIẾT LÀ TIM ĐỎ
                    likedSongIds.clear();
                    for (Song s : listSong) {
                        likedSongIds.add(s.getId());
                    }

                    // Cập nhật Adapter
                    adapter.updateData(listSong);
                    adapter.setLikedSongIds(likedSongIds); // 👈 Quan trọng: Để hiện tim đỏ

                    Log.d("FAV_API", "Lấy được " + listSong.size() + " bài yêu thích.");
                } else {
                    Log.e("FAV_API", "Lỗi lấy dữ liệu: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("FAV_API", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}