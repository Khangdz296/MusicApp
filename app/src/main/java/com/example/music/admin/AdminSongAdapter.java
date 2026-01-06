package com.example.music.admin; // Hoặc package adapter tùy bạn

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
import com.example.music.model.Song;

import java.util.List;

public class AdminSongAdapter extends RecyclerView.Adapter<AdminSongAdapter.SongViewHolder> {

    private Context context;
    private List<Song> list;
    private OnAdminActionListener listener; // Đổi tên listener cho rõ nghĩa

    // 1. Interface: Xử lý sự kiện XÓA
    public interface OnAdminActionListener {
        void onDeleteClick(Song song);
    }

    // 2. Constructor
    public AdminSongAdapter(Context context, List<Song> list, OnAdminActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Song> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // QUAN TRỌNG: Dùng layout item_admin_song (có nút thùng rác)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = list.get(position);
        if (song == null) return;

        // Gán thông tin
        holder.tvSongName.setText(song.getTitle());
        if (song.getArtist() != null) {
            holder.tvArtist.setText(song.getArtist().getName());
        } else {
            holder.tvArtist.setText("Unknown Artist");
        }

        // Load ảnh
        Glide.with(context)
                .load(song.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgAlbum);

        // --- XỬ LÝ SỰ KIỆN NÚT XÓA ---
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                // Truyền bài hát cần xóa ra ngoài Activity
                listener.onDeleteClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName, tvArtist;
        ImageView imgAlbum, btnDelete; // Thay btnFavorite bằng btnDelete

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ ID theo file item_admin_song.xml
            imgAlbum = itemView.findViewById(R.id.imgAlbum);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvArtist = itemView.findViewById(R.id.tvArtist);

            // Đây là nút thùng rác
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}