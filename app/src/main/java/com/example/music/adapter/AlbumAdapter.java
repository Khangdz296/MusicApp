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
import com.example.music.model.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Context context;
    private List<Album> mList;
    private OnAlbumClickListener listener; // Biến listener

    // Interface bắt sự kiện click
    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

    // 👇 CONSTRUCTOR NÀY QUAN TRỌNG: PHẢI CÓ 3 THAM SỐ
    public AlbumAdapter(Context context, List<Album> mList, OnAlbumClickListener listener) {
        this.context = context;
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_row_hoang, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = mList.get(position);
        if (album == null) return;

        holder.tvTitle.setText(album.getName());
        holder.tvSubtitle.setText("Album • " + album.getArtistName());

        Glide.with(context)
                .load(album.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgThumb);

        // Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAlbumClick(album);
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle, tvSubtitle;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}