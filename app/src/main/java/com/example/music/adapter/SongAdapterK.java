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
import com.example.music.model.Song;

import java.util.List;

public class SongAdapterK extends RecyclerView.Adapter<SongAdapterK.SongViewHolder> {

    private Context context;
    private List<Song> list;
    private OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public SongAdapterK(Context context, List<Song> list, OnSongClickListener listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_dhuy, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = list.get(position);
        if (song == null) return;

        holder.tvSongName.setText(song.getTitle());

        // 👇 SỬA LẠI: Lấy tên từ Object Artist
        if (song.getArtist() != null) {
            holder.tvArtist.setText(song.getArtist().getName());
        } else {
            holder.tvArtist.setText("Unknown Artist");
        }

        Glide.with(context)
                .load(song.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgAlbum);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName, tvArtist;
        ImageView imgAlbum, btnFavorite, btnAddToPlaylist;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbum = itemView.findViewById(R.id.imgAlbum);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnAddToPlaylist = itemView.findViewById(R.id.btnAddToPlaylist);
        }
    }
}