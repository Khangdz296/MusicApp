package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music.R;
import com.example.music.adapter.PublicPlaylistAdapter;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Playlist;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicPlaylistActivity extends AppCompatActivity {

    private RecyclerView rvPublicPlaylists;
    private PublicPlaylistAdapter adapter;
    private ApiService apiService;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_playlist_dhuy);

        rvPublicPlaylists = findViewById(R.id.rvPublicPlaylists);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnBack = findViewById(R.id.btnBack);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        setupRecyclerView();
        loadPublicPlaylists();

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new PublicPlaylistAdapter(playlist -> {
            openPlaylistDetail(playlist);
        });

        // Grid 2 cột
        rvPublicPlaylists.setLayoutManager(new GridLayoutManager(this, 2));
        rvPublicPlaylists.setAdapter(adapter);
    }

    private void loadPublicPlaylists() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);

        apiService.getPublicPlaylists().enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Playlist> playlists = response.body();
                    if (playlists.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setPlaylists(playlists);
                    }
                } else {
                    Toast.makeText(PublicPlaylistActivity.this,
                            "Không thể tải playlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PublicPlaylistActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPlaylistDetail(Playlist playlist) {
        Intent intent = new Intent(this, PublicPlaylistDetailActivity.class);
        intent.putExtra("playlistId", playlist.getId());
        intent.putExtra("playlistName", playlist.getName());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}