package com.example.music.adapter;

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

public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.ViewHolder> {

    private List<Song> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public AlbumSongAdapter(List<Song> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 👇 Gọi đúng file XML gốc của dhuy (không sửa gì cả)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_dhuy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = list.get(position);
        if (song == null) return;

        // Gán dữ liệu
        holder.tvName.setText(song.getTitle());
        if (song.getArtist() != null) {
            // Lấy tên từ Object Artist
            holder.tvArtist.setText(song.getArtist().getName());
        } else {
            holder.tvArtist.setText("Unknown Artist");
        }

        // Load ảnh
        Glide.with(holder.itemView.getContext())
                .load(song.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgCover);

        // Sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(song);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Khai báo biến
        ImageView imgCover, btnHeart, btnAdd;
        TextView tvName, tvArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // 👇 ÁNH XẠ ĐÚNG ID TRONG FILE XML CỦA DHUY
            // (Tuyệt đối không sửa tên R.id... ở đây)

            imgCover = itemView.findViewById(R.id.imgAlbum);       // XML đặt là imgAlbum
            tvName   = itemView.findViewById(R.id.tvSongName);     // XML đặt là tvSongName
            tvArtist = itemView.findViewById(R.id.tvArtist);       // XML đặt là tvArtist
            btnHeart = itemView.findViewById(R.id.btnFavorite);    // XML đặt là btnFavorite
            btnAdd   = itemView.findViewById(R.id.btnAddToPlaylist); // XML đặt là btnAddToPlaylist
        }
    }
}