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
import com.example.music.model.Category; // Nhớ import Category
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;

public class FavoriteSongsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Tạo RecyclerView bằng code (không cần tạo file XML riêng)
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setBackgroundColor(0xFF121212); // Màu nền tối

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- TẠO DỮ LIỆU GIẢ ---
        List<Song> list = new ArrayList<>();

        // 1. Tạo một Category giả để nhét vào Song (cho code đỡ báo lỗi null)
        Category catPop = new Category(1L, "V-Pop", "");

        // 2. Tạo Song với Constructor MỚI (Long id, ..., Category)
        list.add(new Song(1L, "Muộn Rồi Mà Sao Còn", "Sơn Tùng M-TP", "https://picsum.photos/200/200?random=7", "", 300, true, catPop));
        list.add(new Song(2L, "Nàng Thơ", "Hoàng Dũng", "https://picsum.photos/200/200?random=8", "", 300, true, catPop));
        list.add(new Song(3L, "Waiting For You", "MONO", "https://picsum.photos/200/200?random=9", "", 280, true, catPop));

        // 3. Khởi tạo Adapter
        // Lưu ý: Đảm bảo FavoriteSongAdapter của bạn đã có constructor nhận Listener nhé!
        FavoriteSongAdapter adapter = new FavoriteSongAdapter(getContext(), list, song -> {
            Toast.makeText(getContext(), "Phát bài: " + song.getTitle(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);

        return recyclerView;
    }
}