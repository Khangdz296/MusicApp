package com.example.music.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.adapter.SongAdapterK;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.ApiResponse;
import com.example.music.model.Playlist;
import com.example.music.model.Song;
import com.example.music.utils.SharedPrefManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicPlaylistDetailActivity extends AppCompatActivity {

    private RecyclerView rvSongs;
    private SongAdapterK  adapter;
    private ApiService apiService;
    private TextView tvPlaylistName, tvCreatorInfo, tvDescription;
    private ImageView imgCover;
    private MaterialButton btnFollow;
    private FloatingActionButton btnPlayBig;
    private ImageView btnMore;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private Long playlistId;
    private List<Song> songs;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_playlist_detail_dhuy);

        playlistId = getIntent().getLongExtra("playlistId", -1);
        String playlistName = getIntent().getStringExtra("playlistName");

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadPlaylistDetail();

        btnFollow.setOnClickListener(v -> followPlaylist());

        btnPlayBig.setOnClickListener(v -> {
            Toast.makeText(this, "Phát toàn bộ playlist", Toast.LENGTH_SHORT).show();
        });

        btnMore.setOnClickListener(v -> {
            Toast.makeText(this, "More options", Toast.LENGTH_SHORT).show();
        });
    }

    private void initViews() {
        imgCover = findViewById(R.id.imgCover);
        tvPlaylistName = findViewById(R.id.tvPlaylistName);
//        tvCreatorInfo = findViewById(R.id.tvCreatorInfo);
//        tvDescription = findViewById(R.id.tvDescription);
//        btnFollow = findViewById(R.id.btnFollow);
//        btnPlayBig = findViewById(R.id.btnPlayBig);
        btnMore = findViewById(R.id.btnMore);
        rvSongs = findViewById(R.id.rvSongs);
//        collapsingToolbar = findViewById(R.id.collapsingToolbar);
//        toolbar = findViewById(R.id.toolbar);

        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new SongAdapterK(context, songs,new SongAdapterK.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                playSong(song);
            }
            @Override
            public void onAddToPlaylistClick(Song song) {
                showAddToPlaylistDialog(song);
            }
            @Override
            public void onFavoriteClick(Song song) {
                toggleFavorite(song);
            }
        }) ;

        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.setAdapter(adapter);
        rvSongs.setNestedScrollingEnabled(false);
    }

    private void loadPlaylistDetail() {
        apiService.getPlaylistDetail(playlistId).enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayPlaylistInfo(response.body());
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Toast.makeText(PublicPlaylistDetailActivity.this,
                        "Lỗi tải chi tiết", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPlaylistInfo(Playlist playlist) {
        tvPlaylistName.setText(playlist.getName());
        tvCreatorInfo.setText(playlist.getCreatorAndSongCount());

        // Set description (nếu có)
        if (playlist.getImageUrl() != null && !playlist.getImageUrl().isEmpty()) {
            tvDescription.setText("Playlist công khai");
        }

        // Load cover image
        Glide.with(this)
                .load(playlist.getCoverImage())
                .placeholder(R.drawable.ic_playlist)
                .into(imgCover);

        // Set title cho Collapsing Toolbar
        collapsingToolbar.setTitle(playlist.getName());

        // Hiển thị danh sách bài hát
        if (playlist.getSongs() != null && !playlist.getSongs().isEmpty()) {
            adapter.setSongs(playlist.getSongs());
        }
    }

    private void followPlaylist() {
        // TODO: Implement follow playlist API
        Toast.makeText(this, "Đã follow playlist", Toast.LENGTH_SHORT).show();
        btnFollow.setText("Đã follow");
        btnFollow.setEnabled(false);
    }

    private void playSong(Song song) {
        Toast.makeText(this, "Playing: " + song.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void showAddToPlaylistDialog(Song song) {
        AddToPlaylistDialog dialog = AddToPlaylistDialog.newInstance(song.getId());
        dialog.show(getSupportFragmentManager(), "AddToPlaylistDialog");
    }

    private void toggleFavorite(Song song) {
        Long userId = SharedPrefManager.getInstance(this).getUserId();

        apiService.toggleFavorite(song.getId(), userId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    song.setFavorite(!song.isFavorite());
                    adapter.notifyDataSetChanged();
                    String message = song.isFavorite() ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích";
                    Toast.makeText(PublicPlaylistDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(PublicPlaylistDetailActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
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