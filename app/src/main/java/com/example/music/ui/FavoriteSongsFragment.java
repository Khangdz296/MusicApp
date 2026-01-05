package com.example.music.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.adapter.FavoriteSongAdapter;
import com.example.music.model.Category;
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;

public class FavoriteSongsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Tạo RecyclerView bằng code (Background tối màu)
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setBackgroundColor(0xFF121212); // Màu nền đen

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- 2. TẠO DỮ LIỆU GIẢ (MOCK DATA) ---
        List<Song> list = new ArrayList<>();

        // Tạo Category giả (ID là Long 1L)
        Category catPop = new Category(1L, "V-Pop", "https://example.com/image.png");

        // 👇 CẬP NHẬT CONSTRUCTOR SONG (Thêm số view vào cuối cùng)
        // new Song(id, title, artist, img, file, duration, isFavorite, category, VIEWS)

        list.add(new Song(1L, "Muộn Rồi Mà Sao Còn", "Sơn Tùng M-TP",
                "https://i.scdn.co/image/ab6761610000e5ebc53f7c462377b7f1e7373f52", "", 300, true, catPop, 1500000));

        list.add(new Song(2L, "Nàng Thơ", "Hoàng Dũng",
                "https://i.scdn.co/image/ab6761610000e5ebc6b73df78cb0ce400d43dfc6", "", 300, true, catPop, 850000));

        list.add(new Song(3L, "Waiting For You", "MONO",
                "https://i.scdn.co/image/ab6761610000e5eb54e7d44869c43d2cc95e54c8", "", 280, true, catPop, 2000000));

        // 3. KHỞI TẠO ADAPTER
        // Lưu ý: Đảm bảo class FavoriteSongAdapter của bạn đã nhận đúng Model Song mới
        FavoriteSongAdapter adapter = new FavoriteSongAdapter(getContext(), list, song -> {
            Toast.makeText(getContext(), "Phát bài: " + song.getTitle(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);

        return recyclerView;
    }
}