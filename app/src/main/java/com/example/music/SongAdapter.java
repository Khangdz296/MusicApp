package com.example.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Import thư viện Glide
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> mList;
    private int type;

    // Định nghĩa các hằng số kiểu hiển thị
    public static final int TYPE_BANNER = 1;   // Hình to (Banner)
    public static final int TYPE_STANDARD = 2; // Hình vuông (Nhạc mới, BXH)
    public static final int TYPE_RECENT = 3;   // Hình nhỏ (Nghe gần đây)

    // Constructor
    public SongAdapter(List<Song> list, int type) {
        this.mList = list;
        this.type = type;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;
        // 👇 Chọn layout dựa trên biến type (Khớp với tên file của bạn)
        if (type == TYPE_BANNER) {
            layoutId = R.layout.item_banner_hoang;
        } else if (type == TYPE_RECENT) {
            layoutId = R.layout.item_recent_hoang;
        } else {
            layoutId = R.layout.item_square_hoang; // Mặc định
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = mList.get(position);
        if (song == null) return;

        // 1. Gán chữ (Dùng Getter trong Song.java)
        if (holder.tvTitle != null) {
            holder.tvTitle.setText(song.getTitle());
        }

        // Gán tên ca sĩ (nếu View đó có hiển thị dòng phụ)
        if (holder.tvArtist != null) {
            holder.tvArtist.setText(song.getArtist());
        }

        // 2. LOAD ẢNH TỪ URL BẰNG GLIDE ✅
        Glide.with(holder.itemView.getContext())
                .load(song.getImageUrl()) // Lấy link ảnh từ Song.java
                .placeholder(R.drawable.ic_launcher_background) // Ảnh chờ
                .error(R.drawable.ic_launcher_background) // Ảnh lỗi
                .into(holder.imgThumb); // Đổ vào ImageView
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

            // 1. Ánh xạ các thành phần chung (Bắt buộc phải giống ID trong XML)
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);

            // 2. Xử lý logic tìm ID cho dòng chữ phụ (Ca sĩ / Subtitle)
            // Ưu tiên tìm ID 'tvArtist' trước (dùng cho item_square, item_recent)
            tvArtist = itemView.findViewById(R.id.tvArtist);

            // Nếu không tìm thấy tvArtist (nghĩa là đang ở layout Banner dùng ID tvSubtitle)
            if (tvArtist == null) {
                tvArtist = itemView.findViewById(R.id.tvSubtitle);
            }
        }
    }
}