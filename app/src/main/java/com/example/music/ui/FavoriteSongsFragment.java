package com.example.music.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.adapter.FavoriteSongAdapter;
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

        // Mock Data Song (Dùng Constructor của Song.java bạn đang có)
        List<Song> list = new ArrayList<>();
        // Lưu ý: Kiểm tra lại Constructor của class Song để điền tham số cho đúng
        // Ví dụ: new Song(id, title, artist, imageUrl, url, duration, isFavorite)
        list.add(new Song("1", "Muộn Rồi Mà Sao Còn", "Sơn Tùng", "https://picsum.photos/200/200?random=7", "", 300, true));
        list.add(new Song("2", "Nàng Thơ", "Hoàng Dũng", "https://picsum.photos/200/200?random=8", "", 300, true));

        FavoriteSongAdapter adapter = new FavoriteSongAdapter(getContext(), list, song -> {
            // Click listener
        });
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }
}