package com.example.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.model.Song; // Import đúng model Song của bạn

import java.util.List;

public class FavoriteSongAdapter extends RecyclerView.Adapter<FavoriteSongAdapter.SongViewHolder> {

    private Context context;
    private List<Song> mList;
    private OnSongClickListener listener; // Biến listener để bắt sự kiện click

    // 1. Tạo Interface để Fragment có thể xử lý khi bấm vào bài hát
    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    // 2. Constructor chuẩn (3 tham số) để khớp với Fragment
    public FavoriteSongAdapter(Context context, List<Song> mList, OnSongClickListener listener) {
        this.context = context;
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Dùng chung layout dòng (item_library_row_hoang) cho đồng bộ
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_row_hoang, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = mList.get(position);
        if (song == null) return;

        // Gán dữ liệu
        holder.tvTitle.setText(song.getTitle());
        holder.tvSubtitle.setText("Song • " + song.getArtist()); // VD: Song • Sơn Tùng

        // Tải ảnh bằng Glide
        Glide.with(context)
                .load(song.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgThumb);

        // Bắt sự kiện click vào dòng bài hát
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle, tvSubtitle;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ ID từ file item_library_row_hoang.xml
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}