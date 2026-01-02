package com.example.music.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.model.Category;
import com.example.music.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CatViewHolder> {

    private List<Category> mList;

    public CategoryAdapter(List<Category> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gọi đúng file layout của bạn: item_category_hoang.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_hoang, parent, false);
        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        Category category = mList.get(position);
        if (category == null) return;

        // Set tên thể loại
        holder.tvName.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public class CatViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvTitle);
        }
    }
}