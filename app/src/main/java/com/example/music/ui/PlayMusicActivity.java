package com.example.music.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Song;
import com.example.music.utils.MiniPlayerManager;

import java.util.ArrayList;
import java.util.List;
import com.example.music.ui.FavoriteHelper;
import android.graphics.Color;
import com.example.music.ui.AddToPlaylistHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayMusicActivity extends AppCompatActivity {
    private AddToPlaylistHelper addToPlaylistHelper;
    private FavoriteHelper favoriteHelper;
    private List<Long> likedSongIds = new ArrayList<>();
    private static final String TAG = "PlayMusicActivity";
    private Long currentUserId = 1L;


    ImageButton btnBack, btnMore, btnLike, btnShuffle, btnPrevious, btnPlay, btnNext, btnRepeat;
    ImageView imgAlbum;
    TextView txtTitle, txtSong, txtArtist, txtCurrent, txtDuration;
    SeekBar seekBar;

    private MiniPlayerManager miniPlayerManager;
    private Handler handler = new Handler();
    private boolean isUpdatingSeekBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_screen_huy);

        // Lấy instance của Mini Player Manager
        miniPlayerManager = MiniPlayerManager.getInstance();
        // 3. Khởi tạo Helper
        addToPlaylistHelper = new AddToPlaylistHelper(this);
        favoriteHelper = new FavoriteHelper(this);
        initViews();

        // Kiểm tra xem có dữ liệu từ Intent không
        Intent intent = getIntent();
        if (intent.hasExtra("song_data")) {
            // Có dữ liệu mới -> Phát bài mới
            Song song = (Song) intent.getSerializableExtra("song_data");
            List<Song> songList = (ArrayList<Song>) intent.getSerializableExtra("song_list");
            int position = intent.getIntExtra("current_position", 0);

            miniPlayerManager.playSong(song, songList, position);
        }

        // Đồng bộ UI với trạng thái hiện tại
        syncUIWithMiniPlayer();
        setupListeners();
        updateSeekBar();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnMore = findViewById(R.id.btnMore);
        btnLike = findViewById(R.id.btnLike);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnRepeat = findViewById(R.id.btnRepeat);

        imgAlbum = findViewById(R.id.imgAlbum);
        txtTitle = findViewById(R.id.txtTitle);
        txtSong = findViewById(R.id.txtSong);
        txtArtist = findViewById(R.id.txtArtist);
        txtCurrent = findViewById(R.id.txtCurrent);
        txtDuration = findViewById(R.id.txtDuration);
        seekBar = findViewById(R.id.seekBar);
    }
    // 👇 2. HÀM GỌI API LẤY DANH SÁCH YÊU THÍCH
    private void fetchUserFavorites() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getFavoriteSongs(currentUserId).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    likedSongIds.clear();
                    // Chỉ lấy ID đưa vào list
                    for (Song s : response.body()) {
                        likedSongIds.add(s.getId());
                    }
                    // Sau khi có dữ liệu thì cập nhật lại giao diện ngay
                    syncUIWithMiniPlayer();
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e(TAG, "Lỗi lấy favorites: " + t.getMessage());
            }
        });
    }

    private void syncUIWithMiniPlayer() {
        Song currentSong = miniPlayerManager.getCurrentSong();
        if (currentSong == null) {
            finish();
            return;
        }

        // Hiển thị thông tin bài hát
        txtTitle.setText(currentSong.getTitle());
        txtSong.setText(currentSong.getTitle());
        if (currentSong.getArtist() != null) {
            txtArtist.setText(currentSong.getArtist().getName());
        } else {
            txtArtist.setText("Unknown Artist");
        }

        Glide.with(this)
                .load(currentSong.getImageUrl())
                .placeholder(R.drawable.ic_music_note)
                .error(R.drawable.ic_music_note)
                .into(imgAlbum);

        // Cập nhật nút Play/Pause
        btnPlay.setImageResource(miniPlayerManager.isPlaying() ?
                R.drawable.ic_pause : R.drawable.ic_play);

        // Cập nhật nút Shuffle
        updateShuffleButton();

        // Cập nhật nút Repeat
        updateRepeatButton();

        // Cập nhật SeekBar
        MediaPlayer mp = miniPlayerManager.getMediaPlayer();
        if (mp != null) {
            try {
                seekBar.setMax(mp.getDuration());
                txtDuration.setText(formatTime(mp.getDuration()));
            } catch (Exception e) {
                seekBar.setMax(100);
                txtDuration.setText("0:00");
            }
        }
        if (likedSongIds.contains(currentSong.getId())) {
            btnLike.setImageResource(R.drawable.ic_heart_filled);
            btnLike.setColorFilter(Color.RED);
        } else {
            btnLike.setImageResource(R.drawable.ic_heart_outline);
            btnLike.setColorFilter(Color.WHITE);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // --- ĐOẠN CODE MỚI CHO btnMore ---
        btnMore.setOnClickListener(v -> {
            // Lấy bài hát đang phát hiện tại
            Song currentSong = miniPlayerManager.getCurrentSong();

            if (currentSong != null) {
                // Gọi BottomSheet "Thêm vào Playlist" lên
                addToPlaylistHelper.showAddToPlaylistDialog(currentSong);
            }
        });
        btnLike.setOnClickListener(v -> {
            Song currentSong = miniPlayerManager.getCurrentSong();
            if (currentSong != null) {
                // Gọi Helper để xử lý Thích/Bỏ thích
                favoriteHelper.toggleFavorite(currentSong, btnLike, likedSongIds);
            }
        });
        btnPlay.setOnClickListener(v -> {
            if (miniPlayerManager.isPlaying()) {
                miniPlayerManager.pauseMusic();
            } else {
                miniPlayerManager.playMusic();
            }
            syncUIWithMiniPlayer();
        });

        btnNext.setOnClickListener(v -> {
            miniPlayerManager.playNext();
            syncUIWithMiniPlayer();
        });

        btnPrevious.setOnClickListener(v -> {
            miniPlayerManager.playPrevious();
            syncUIWithMiniPlayer();
        });

        // Shuffle button
        btnShuffle.setOnClickListener(v -> {
            miniPlayerManager.toggleShuffle();
            updateShuffleButton();
        });

        // Repeat button
        btnRepeat.setOnClickListener(v -> {
            miniPlayerManager.toggleRepeat();
            updateRepeatButton();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MediaPlayer mp = miniPlayerManager.getMediaPlayer();
                    if (mp != null) {
                        mp.seekTo(progress);
                        txtCurrent.setText(formatTime(progress));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUpdatingSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUpdatingSeekBar = false;
            }
        });
    }

    private void updateShuffleButton() {
        if (miniPlayerManager.isShuffleEnabled()) {
            // Shuffle ON - màu xanh lá
            btnShuffle.setColorFilter(getResources().getColor(R.color.spotify_green));
        } else {
            // Shuffle OFF - màu trắng
            btnShuffle.setColorFilter(getResources().getColor(android.R.color.white));
        }
    }

    private void updateRepeatButton() {
        int repeatMode = miniPlayerManager.getRepeatMode();

        switch (repeatMode) {
            case MiniPlayerManager.REPEAT_OFF:
                // OFF - màu trắng
                btnRepeat.setImageResource(R.drawable.repeat);
                btnRepeat.setColorFilter(getResources().getColor(android.R.color.white));
                break;
            case MiniPlayerManager.REPEAT_ALL:
                // ALL - màu xanh lá
                btnRepeat.setImageResource(R.drawable.repeat);
                btnRepeat.setColorFilter(getResources().getColor(R.color.spotify_green));
                break;
            case MiniPlayerManager.REPEAT_ONE:
                // ONE - màu xanh lá, icon khác (nếu có)
                // Nếu bạn có icon repeat_one, thay đổi ở đây
                btnRepeat.setImageResource(R.drawable.repeat);
                btnRepeat.setColorFilter(getResources().getColor(R.color.spotify_green));
                break;
        }
    }

    private void updateSeekBar() {
        if (!isUpdatingSeekBar) {
            MediaPlayer mp = miniPlayerManager.getMediaPlayer();
            if (mp != null && miniPlayerManager.isPlaying()) {
                try {
                    int currentPosition = mp.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    txtCurrent.setText(formatTime(currentPosition));
                } catch (Exception e) {
                    // MediaPlayer chưa sẵn sàng
                }
            }
        }

        // Cập nhật nút play/pause
        btnPlay.setImageResource(miniPlayerManager.isPlaying() ?
                R.drawable.ic_pause : R.drawable.ic_play);

        handler.postDelayed(() -> updateSeekBar(), 100);
    }

    private String formatTime(int millis) {
        int seconds = (millis / 1000) % 60;
        int minutes = (millis / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        // KHÔNG release MediaPlayer vì mini player vẫn cần nó
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncUIWithMiniPlayer();
    }
}