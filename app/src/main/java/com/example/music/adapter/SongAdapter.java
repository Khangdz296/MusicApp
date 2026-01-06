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

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> mList;
    private int type;
    private OnSongClickListener listener; // 👇 Biến lắng nghe sự kiện click

    // Định nghĩa các hằng số kiểu hiển thị
    public static final int TYPE_BANNER = 1;
    public static final int TYPE_STANDARD = 2;
    public static final int TYPE_RECENT = 3;

    // 👇 1. Tạo Interface để bắn tín hiệu ra ngoài khi bấm vào bài hát
    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    // 👇 2. Cập nhật Constructor thêm tham số 'listener'
    public SongAdapter(List<Song> list, int type, OnSongClickListener listener) {
        this.mList = list;
        this.type = type;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;
        // Chọn layout dựa trên biến type
        if (type == TYPE_BANNER) {
            layoutId = R.layout.item_banner_hoang;
        } else if (type == TYPE_RECENT) {
            layoutId = R.layout.item_recent_hoang;
        } else {
            layoutId = R.layout.item_square_hoang;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = mList.get(position);
        if (song == null) return;

        // Gán dữ liệu lên giao diện
        if (holder.tvTitle != null) {
            holder.tvTitle.setText(song.getTitle());
        }

        if (holder.tvArtist != null) {
            if (song.getArtist() != null) {
                // Lấy tên từ Object Artist
                holder.tvArtist.setText(song.getArtist().getName());
            } else {
                holder.tvArtist.setText("Unknown Artist");
            }
        }

        // Load ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(song.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgThumb);

        // 👇 3. Bắt sự kiện Click vào bài hát
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song); // Truyền bài hát được chọn ra ngoài
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle, tvArtist;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);

            // Tìm view phụ (Artist hoặc Subtitle tùy layout)
            tvArtist = itemView.findViewById(R.id.tvArtist);
            if (tvArtist == null) {
                tvArtist = itemView.findViewById(R.id.tvSubtitle);
            }
        }
    }
}