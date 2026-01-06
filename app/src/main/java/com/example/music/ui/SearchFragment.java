package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.api.ApiService;
import com.example.music.ui.CategoryDetailActivity;
import com.example.music.R;
import com.example.music.adapter.CategoryAdapterK;
import com.example.music.adapter.SongAdapterK;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Category;
import com.example.music.model.Song;
import com.example.music.utils.MiniPlayerManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView rcvCategories, rcvSearchResults;
    private TextView tvSubHeader;
    private EditText edtSearch;
    private CategoryAdapterK categoryAdapter;
    private SongAdapterK songAdapter;
    private MiniPlayerManager miniPlayerManager;

    // Lưu danh sách kết quả tìm kiếm
    private List<Song> searchResultList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_khang, container, false);

        // Khởi tạo MiniPlayerManager
        miniPlayerManager = MiniPlayerManager.getInstance();

        // 1. Ánh xạ View
        rcvCategories = view.findViewById(R.id.rcvCategories);
        rcvSearchResults = view.findViewById(R.id.rcvSearchResults);
        tvSubHeader = view.findViewById(R.id.tvSubHeader);
        edtSearch = view.findViewById(R.id.edtSearch);

        // 2. Cài đặt Category Adapter (Grid 2 cột)
        rcvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        categoryAdapter = new CategoryAdapterK(new ArrayList<>(), new CategoryAdapterK.IClickCategoryListener() {
            @Override
            public void onClick(Category category, int color) {
                // Chuyển sang màn hình Detail
                Intent intent = new Intent(getContext(), CategoryDetailActivity.class);
                intent.putExtra("CAT_ID", category.getId());
                intent.putExtra("CAT_NAME", category.getName());
                intent.putExtra("CAT_COLOR", color);
                startActivity(intent);
            }
        });
        rcvCategories.setAdapter(categoryAdapter);

        // 3. Cài đặt Search Result Adapter
        rcvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khi click vào bài hát từ kết quả tìm kiếm
        songAdapter = new SongAdapterK(getContext(), new ArrayList<>(), new SongAdapterK.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // Tìm vị trí của bài hát trong danh sách
                int position = findSongPosition(song);

                Log.d("SEARCH_CLICK", "Người dùng đã chọn bài: " + song.getTitle());
                Log.d("SEARCH_CLICK", "ID bài hát: " + song.getId());
                Log.d("SEARCH_CLICK", "Vị trí: " + position);

                // Phát nhạc qua MiniPlayerManager
                miniPlayerManager.playSong(song, searchResultList, position);

                // Chuyển sang màn hình PlayMusicActivity
                Intent intent = new Intent(getContext(), PlayMusicActivity.class);
                intent.putExtra("song_data", song);
                intent.putExtra("song_list", new ArrayList<>(searchResultList));
                intent.putExtra("current_position", position);
                startActivity(intent);
            }
        });
        rcvSearchResults.setAdapter(songAdapter);

        // 4. Load dữ liệu Category ban đầu
        loadCategories();

        // 5. Bắt sự kiện gõ chữ
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim();
                if (!keyword.isEmpty()) {
                    showSearchMode(true);
                    performSearch(keyword);
                } else {
                    showSearchMode(false);
                    // Xóa danh sách kết quả khi không còn tìm kiếm
                    searchResultList.clear();
                }
            }
        });

        return view;
    }

    private void loadCategories() {
        RetrofitClient.getClient().create(ApiService.class).getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryAdapter.updateData(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi gọi API: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String keyword) {
        RetrofitClient.getClient().create(ApiService.class).searchSongs(keyword).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lưu danh sách kết quả tìm kiếm
                    searchResultList = new ArrayList<>(response.body());

                    // Cập nhật adapter
                    songAdapter.updateData(searchResultList);

                    Log.d("SEARCH_RESULT", "Tìm thấy " + searchResultList.size() + " bài hát");
                }
            }
            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi tìm kiếm: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSearchMode(boolean isSearching) {
        if (isSearching) {
            rcvCategories.setVisibility(View.GONE);
            tvSubHeader.setVisibility(View.GONE);
            rcvSearchResults.setVisibility(View.VISIBLE);
        } else {
            rcvCategories.setVisibility(View.VISIBLE);
            tvSubHeader.setVisibility(View.VISIBLE);
            rcvSearchResults.setVisibility(View.GONE);
        }
    }

    /**
     * Tìm vị trí của bài hát trong danh sách kết quả tìm kiếm
     */
    private int findSongPosition(Song song) {
        if (song == null || searchResultList.isEmpty()) {
            return 0;
        }

        for (int i = 0; i < searchResultList.size(); i++) {
            if (searchResultList.get(i).getId() == song.getId()) {
                return i;
            }
        }
        return 0;
    }
}