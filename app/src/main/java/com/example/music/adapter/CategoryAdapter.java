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
import com.example.music.model.Category;
import com.example.music.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CatViewHolder> {

    private Context context;
    private List<Category> mList;
    private OnCategoryClickListener listener; // Listener bắt sự kiện click

    // 1. Interface click
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    // 2. Constructor đầy đủ (Context + List + Listener)
    public CategoryAdapter(Context context, List<Category> list, OnCategoryClickListener listener) {
        this.context = context;
        this.mList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gọi file layout item_category_hoang.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_hoang, parent, false);
        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        Category category = mList.get(position);
        if (category == null) return;

        // Set tên thể loại
        holder.tvName.setText(category.getName());

        // 3. Dùng Glide tải ảnh từ URL
        Glide.with(context)
                .load(category.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background) // Ảnh chờ
                .error(R.drawable.ic_launcher_background)       // Ảnh lỗi
                .into(holder.imgBackground);

        // 4. Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public class CatViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgBackground; // Thêm biến ảnh

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ ID từ file item_category_hoang.xml
            tvName = itemView.findViewById(R.id.tvTitle);

            // ⚠️ LƯU Ý: Bạn cần đảm bảo trong file xml có ImageView với id này
            // Nếu id khác (ví dụ imgThumb), hãy sửa lại dòng dưới cho khớp
            imgBackground = itemView.findViewById(R.id.imgBackground);
        }
    }
}