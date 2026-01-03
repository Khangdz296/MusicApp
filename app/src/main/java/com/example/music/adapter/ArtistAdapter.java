package com.example.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Nhớ import Glide
import com.example.music.model.Artist;
import com.example.music.R;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private Context context;
    private List<Artist> mList;
    private OnArtistClickListener listener; // Thêm listener để bắt sự kiện click

    // 1. Interface bắt sự kiện click
    public interface OnArtistClickListener {
        void onArtistClick(Artist artist);
    }

    // 2. Constructor cập nhật (thêm Context và Listener)
    public ArtistAdapter(Context context, List<Artist> list, OnArtistClickListener listener) {
        this.context = context;
        this.mList = list;
        this.listener = listener;
    }

    @NonNull @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_hoang, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = mList.get(position);
        if (artist == null) return;

        holder.tvName.setText(artist.getName());

        // 3. SỬA: Dùng Glide để load ảnh từ URL (thay vì setImageResource)
        Glide.with(context)
                .load(artist.getImageUrl()) // Đã đổi sang getImageUrl()
                .placeholder(R.drawable.ic_launcher_background) // Ảnh chờ
                .error(R.drawable.ic_launcher_background)       // Ảnh lỗi
                .into(holder.imgAvt);

        // 4. Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onArtistClick(artist);
        });
    }

    @Override
    public int getItemCount() { return mList != null ? mList.size() : 0; }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvt;
        TextView tvName;
        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đảm bảo file item_artist_hoang.xml có đúng 2 ID này
            imgAvt = itemView.findViewById(R.id.imgArtist);
            tvName = itemView.findViewById(R.id.tvArtistName);
        }
    }
}