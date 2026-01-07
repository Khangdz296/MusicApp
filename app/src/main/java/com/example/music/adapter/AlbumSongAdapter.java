package com.example.music.adapter;

import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.ViewHolder> {

    private List<Song> list;
    private OnItemClickListener listener;
    private List<Long> likedSongIds = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(Song song);
        void onAddToPlaylistClick(Song song);
        void onFavoriteClick(Song song, ImageView btnFavorite, List<Long> likedIds);
    }

    public AlbumSongAdapter(List<Song> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }
    public void setLikedSongIds(List<Long> likedSongIds) {
        this.likedSongIds = likedSongIds;
        notifyDataSetChanged();
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
        // 👇 3. LOGIC CHECK TIM ĐỎ/TRẮNG
        if (likedSongIds.contains(song.getId())) {
            holder.btnFavorite.setImageResource(R.drawable.ic_heart_filled);
            holder.btnFavorite.setColorFilter(Color.RED);
        } else {
            holder.btnFavorite.setImageResource(R.drawable.ic_heart_outline);
            holder.btnFavorite.setColorFilter(Color.GRAY);
        }

        // Load ảnh
        Glide.with(holder.itemView.getContext())
                .load(song.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgCover);
        holder.btnAddToPlaylist.setOnClickListener(v -> {
            listener.onAddToPlaylistClick(song);
        });
        // Sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(song);
        });
        holder.btnFavorite.setOnClickListener(v -> {
            listener.onFavoriteClick(song, holder.btnFavorite, likedSongIds);
        });

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Khai báo biến
        ImageView imgCover, btnFavorite, btnAdd;
        TextView tvName, tvArtist;
        ImageView btnAddToPlaylist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // 👇 ÁNH XẠ ĐÚNG ID TRONG FILE XML CỦA DHUY
            // (Tuyệt đối không sửa tên R.id... ở đây)

            imgCover = itemView.findViewById(R.id.imgAlbum);       // XML đặt là imgAlbum
            tvName   = itemView.findViewById(R.id.tvSongName);     // XML đặt là tvSongName
            tvArtist = itemView.findViewById(R.id.tvArtist);       // XML đặt là tvArtist
            btnFavorite = itemView.findViewById(R.id.btnFavorite);    // XML đặt là btnFavorite
            btnAddToPlaylist   = itemView.findViewById(R.id.btnAddToPlaylist); // XML đặt là btnAddToPlaylist
        }
    }
}