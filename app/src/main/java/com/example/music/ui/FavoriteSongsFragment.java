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
import com.example.music.model.Artist; // 👇 THÊM IMPORT ARTIST
import com.example.music.model.Category;
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;

public class FavoriteSongsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setBackgroundColor(0xFF121212);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- 2. TẠO DỮ LIỆU GIẢ (MOCK DATA) ---
        List<Song> list = new ArrayList<>();

        // Tạo Category giả
        Category catPop = new Category(1L, "V-Pop", "");

        // 👇 TẠO OBJECT ARTIST GIẢ (Vì Constructor Song cần Artist object)
        Artist artistSonTung = new Artist(1L, "Sơn Tùng M-TP", "https://example.com/st.jpg", "Mô tả Sơn Tùng");
        Artist artistHoangDung = new Artist(2L, "Hoàng Dũng", "https://example.com/hd.jpg", "Mô tả Hoàng Dũng");
        Artist artistMono = new Artist(3L, "MONO", "https://example.com/mono.jpg", "Mô tả MONO");

        // 👇 CẬP NHẬT TRUYỀN ARTIST OBJECT VÀO CONSTRUCTOR SONG
        // new Song(id, title, artistObj, img, file, duration, isFavorite, category, views)

        list.add(new Song(1L, "Muộn Rồi Mà Sao Còn", artistSonTung,
                "https://i.scdn.co/image/ab6761610000e5ebc53f7c462377b7f1e7373f52", "", 300, true, catPop, 1500000));

        list.add(new Song(2L, "Nàng Thơ", artistHoangDung,
                "https://i.scdn.co/image/ab6761610000e5ebc6b73df78cb0ce400d43dfc6", "", 300, true, catPop, 850000));

        list.add(new Song(3L, "Waiting For You", artistMono,
                "https://i.scdn.co/image/ab6761610000e5eb54e7d44869c43d2cc95e54c8", "", 280, true, catPop, 2000000));

        // 3. KHỞI TẠO ADAPTER
        FavoriteSongAdapter adapter = new FavoriteSongAdapter(getContext(), list, song -> {
            Toast.makeText(getContext(), "Phát bài: " + song.getTitle(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);

        return recyclerView;
    }
}