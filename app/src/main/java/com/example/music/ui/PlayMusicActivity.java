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
import com.example.music.model.Song;
import com.example.music.utils.MiniPlayerManager;

import java.util.ArrayList;
import java.util.List;

public class PlayMusicActivity extends AppCompatActivity {

    private static final String TAG = "PlayMusicActivity";

    ImageButton btnBack, btnMore, btnLike, btnShuffle, btnPrevious, btnPlay, btnNext, btnRepeat;
    ImageView imgAlbum;
    TextView txtTitle, txtSong, txtArtist, txtCurrent, txtDuration;
    SeekBar seekBar;

    private MiniPlayerManager miniPlayerManager;
    private Handler handler = new Handler();
    private boolean isUpdatingSeekBar = false;
    private boolean isDurationSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_screen_huy);

        miniPlayerManager = MiniPlayerManager.getInstance();

        initViews();

        // Kiểm tra xem có phải là request phát bài MỚI không
        Intent intent = getIntent();
        boolean isNewSongRequest = intent.getBooleanExtra("play_new_song", false);

        if (isNewSongRequest && intent.hasExtra("song_data")) {
            // CHỈ phát bài mới khi có flag "play_new_song" = true
            Song song = (Song) intent.getSerializableExtra("song_data");
            List<Song> songList = (ArrayList<Song>) intent.getSerializableExtra("song_list");
            int position = intent.getIntExtra("current_position", 0);

            miniPlayerManager.playSong(song, songList, position);
            Log.d(TAG, "Playing new song from Intent");
        } else {
            // Chỉ đồng bộ UI với trạng thái hiện tại, KHÔNG phát lại
            Log.d(TAG, "Opening full player - syncing UI only");
        }

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

        // Reset flag khi sync UI mới
        isDurationSet = false;

        // Thử set duration ngay lập tức
        trySetDuration();
    }

    private void trySetDuration() {
        MediaPlayer mp = miniPlayerManager.getMediaPlayer();
        if (mp != null) {
            try {
                int duration = mp.getDuration();
                // Kiểm tra duration hợp lệ (> 0)
                if (duration > 0) {
                    seekBar.setMax(duration);
                    txtDuration.setText(formatTime(duration));
                    isDurationSet = true;
                    Log.d(TAG, "Duration set successfully: " + duration);
                } else {
                    Log.d(TAG, "Invalid duration: " + duration);
                    // Set giá trị mặc định tạm thời
                    seekBar.setMax(100);
                    txtDuration.setText("--:--");
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "MediaPlayer not ready: " + e.getMessage());
                seekBar.setMax(100);
                txtDuration.setText("--:--");
            }
        } else {
            seekBar.setMax(100);
            txtDuration.setText("--:--");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlay.setOnClickListener(v -> {
            if (miniPlayerManager.isPreparing()) {
                return;
            }

            if (miniPlayerManager.isPlaying()) {
                miniPlayerManager.pauseMusic();
            } else {
                miniPlayerManager.playMusic();
            }
            syncUIWithMiniPlayer();
        });

        btnNext.setOnClickListener(v -> {
            if (miniPlayerManager.isPreparing()) {
                return;
            }

            miniPlayerManager.playNext();
            isDurationSet = false;
            syncUIWithMiniPlayer();
        });

        btnPrevious.setOnClickListener(v -> {
            if (miniPlayerManager.isPreparing()) {
                return;
            }

            miniPlayerManager.playPrevious();
            isDurationSet = false;
            syncUIWithMiniPlayer();
        });

        btnShuffle.setOnClickListener(v -> {
            miniPlayerManager.toggleShuffle();
            updateShuffleButton();
        });

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
                        try {
                            mp.seekTo(progress);
                            txtCurrent.setText(formatTime(progress));
                        } catch (IllegalStateException e) {
                            Log.e(TAG, "Error seeking: " + e.getMessage());
                        }
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
            btnShuffle.setColorFilter(getResources().getColor(R.color.spotify_green));
        } else {
            btnShuffle.setColorFilter(getResources().getColor(android.R.color.white));
        }
    }

    private void updateRepeatButton() {
        int repeatMode = miniPlayerManager.getRepeatMode();

        switch (repeatMode) {
            case MiniPlayerManager.REPEAT_OFF:
                btnRepeat.setImageResource(R.drawable.repeat);
                btnRepeat.setColorFilter(getResources().getColor(android.R.color.white));
                break;
            case MiniPlayerManager.REPEAT_ALL:
                btnRepeat.setImageResource(R.drawable.repeat);
                btnRepeat.setColorFilter(getResources().getColor(R.color.spotify_green));
                break;
            case MiniPlayerManager.REPEAT_ONE:
                btnRepeat.setImageResource(R.drawable.repeat);
                btnRepeat.setColorFilter(getResources().getColor(R.color.spotify_green));
                break;
        }
    }

    private void updateSeekBar() {
        MediaPlayer mp = miniPlayerManager.getMediaPlayer();

        // Nếu chưa set duration và MediaPlayer đã sẵn sàng, thử set lại
        if (!isDurationSet && mp != null) {
            trySetDuration();
        }

        if (!isUpdatingSeekBar && mp != null) {
            try {
                // Kiểm tra MediaPlayer đang trong trạng thái hợp lệ
                if (miniPlayerManager.isPlaying() || mp.getCurrentPosition() > 0) {
                    int currentPosition = mp.getCurrentPosition();
                    int duration = mp.getDuration();

                    // Cập nhật SeekBar
                    if (duration > 0) {
                        seekBar.setProgress(currentPosition);
                        txtCurrent.setText(formatTime(currentPosition));

                        // Set duration nếu chưa set hoặc bị sai
                        if (!isDurationSet || seekBar.getMax() != duration) {
                            seekBar.setMax(duration);
                            txtDuration.setText(formatTime(duration));
                            isDurationSet = true;
                        }
                    }
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error updating seekbar: " + e.getMessage());
            }
        }

        // Cập nhật nút play/pause
        btnPlay.setImageResource(miniPlayerManager.isPlaying() ?
                R.drawable.ic_pause : R.drawable.ic_play);

        handler.postDelayed(this::updateSeekBar, 100);
    }

    private String formatTime(int millis) {
        if (millis < 0) return "0:00";

        int seconds = (millis / 1000) % 60;
        int minutes = (millis / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDurationSet = false;
        syncUIWithMiniPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }
}