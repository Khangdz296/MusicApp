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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.model.Song;

import java.io.IOException;

public class PlayMusicActivity extends AppCompatActivity {

    private static final String TAG = "PlayMusicActivity";

    ImageButton btnBack, btnMore, btnLike, btnShuffle, btnPrevious, btnPlay, btnNext, btnRepeat;
    ImageView imgAlbum;
    TextView txtTitle, txtSong, txtArtist, txtCurrent, txtDuration;
    SeekBar seekBar;

    Song currentSong;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    boolean isPlaying = false;
    boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_screen_huy);

        initViews();

        btnPlay.setImageResource(R.drawable.ic_play);

        loadSongData();
        setupListeners();
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

    private void loadSongData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("song_data")) {
            currentSong = (Song) intent.getSerializableExtra("song_data");

            if (currentSong != null) {
                Log.d(TAG, "Nhận bài hát: " + currentSong.getTitle());

                txtTitle.setText(currentSong.getTitle());
                txtSong.setText(currentSong.getTitle());
                txtArtist.setText(currentSong.getArtist());

                Glide.with(this)
                        .load(currentSong.getImageUrl())
                        .placeholder(R.drawable.ic_music_note)
                        .error(R.drawable.ic_music_note)
                        .into(imgAlbum);

                prepareMediaPlayer();
            } else {
                Log.e(TAG, "Song data is null");
                Toast.makeText(this, "Lỗi: Không có dữ liệu bài hát", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Log.e(TAG, "No song data in intent");
            Toast.makeText(this, "Lỗi: Không nhận được bài hát", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void prepareMediaPlayer() {
        if (currentSong == null || currentSong.getFileUrl() == null) {
            Toast.makeText(this, "Không có link nhạc", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(currentSong.getFileUrl());
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "MediaPlayer prepared");
                int duration = mp.getDuration();
                seekBar.setMax(duration);
                txtDuration.setText(formatTime(duration));
                txtCurrent.setText("0:00");
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                btnPlay.setImageResource(R.drawable.ic_play);
                seekBar.setProgress(0);
                txtCurrent.setText("0:00");
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                Toast.makeText(this, "Lỗi phát nhạc", Toast.LENGTH_SHORT).show();
                return true;
            });

        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            Toast.makeText(this, "Không thể phát nhạc", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnMore.setOnClickListener(v -> {
            Toast.makeText(this, "More options", Toast.LENGTH_SHORT).show();
        });

        btnLike.setOnClickListener(v -> {
            isLiked = !isLiked;
            if (isLiked) {
                btnLike.setImageResource(R.drawable.heart);
                Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                btnLike.setImageResource(R.drawable.heart);
                Toast.makeText(this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
            }
        });

        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer == null) return;

            if (isPlaying) {
                pauseMusic();
            } else {
                playMusic();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            Toast.makeText(this, "Previous (Chưa có danh sách)", Toast.LENGTH_SHORT).show();
        });

        btnNext.setOnClickListener(v -> {
            Toast.makeText(this, "Next (Chưa có danh sách)", Toast.LENGTH_SHORT).show();
        });

        btnShuffle.setOnClickListener(v -> {
            Toast.makeText(this, "Shuffle đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnRepeat.setOnClickListener(v -> {
            Toast.makeText(this, "Repeat đang phát triển", Toast.LENGTH_SHORT).show();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    txtCurrent.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            btnPlay.setImageResource(R.drawable.ic_pause);
            updateSeekBar();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && isPlaying) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            txtCurrent.setText(formatTime(mediaPlayer.getCurrentPosition()));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isPlaying) {
                        updateSeekBar();
                    }
                }
            }, 100);
        }
    }

    private String formatTime(int millis) {
        int seconds = (millis / 1000) % 60;
        int minutes = (millis / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            pauseMusic();
        }
    }
}