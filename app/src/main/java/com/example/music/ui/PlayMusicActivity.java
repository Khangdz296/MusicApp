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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlayMusicActivity extends AppCompatActivity {

    private static final String TAG = "PlayMusicActivity";

    // Chế độ Repeat
    private static final int REPEAT_OFF = 0;      // Không lặp
    private static final int REPEAT_ALL = 1;      // Lặp toàn bộ danh sách
    private static final int REPEAT_ONE = 2;      // Lặp 1 bài

    ImageButton btnBack, btnMore, btnLike, btnShuffle, btnPrevious, btnPlay, btnNext, btnRepeat;
    ImageView imgAlbum;
    TextView txtTitle, txtSong, txtArtist, txtCurrent, txtDuration;
    SeekBar seekBar;

    Song currentSong;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    boolean isPlaying = false;
    boolean isLiked = false;

    // Quản lý danh sách
    List<Song> songList = new ArrayList<>();
    List<Song> originalSongList = new ArrayList<>(); // Lưu danh sách gốc
    int currentPosition = 0;

    // Trạng thái Shuffle và Repeat
    boolean isShuffleOn = false;
    int repeatMode = REPEAT_OFF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_screen_huy);

        initViews();
        btnPlay.setImageResource(R.drawable.ic_play);
        loadSongData();
        setupListeners();
        updateShuffleButton();
        updateRepeatButton();
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

            if (intent.hasExtra("song_list")) {
                songList = (ArrayList<Song>) intent.getSerializableExtra("song_list");
                originalSongList = new ArrayList<>(songList); // Sao lưu danh sách gốc
            }
            if (intent.hasExtra("current_position")) {
                currentPosition = intent.getIntExtra("current_position", 0);
            }

            if (currentSong != null) {
                Log.d(TAG, "Nhận bài hát: " + currentSong.getTitle());
                displaySongInfo();
                prepareMediaPlayer();

                // 👇 Kiểm tra và vô hiệu hóa nút nếu không có danh sách
                if (songList == null || songList.isEmpty()) {
                    btnShuffle.setEnabled(false);
                    btnShuffle.setAlpha(0.3f);
                    btnNext.setEnabled(false);
                    btnNext.setAlpha(0.3f);
                    btnPrevious.setEnabled(false);
                    btnPrevious.setAlpha(0.3f);
                    btnRepeat.setEnabled(false);
                    btnRepeat.setAlpha(0.3f);
                } else {
                    // Có danh sách thì bật lại các nút
                    btnShuffle.setEnabled(true);
                    btnShuffle.setAlpha(1.0f);
                    btnNext.setEnabled(true);
                    btnNext.setAlpha(1.0f);
                    btnPrevious.setEnabled(true);
                    btnPrevious.setAlpha(1.0f);
                    btnRepeat.setEnabled(true);
                    btnRepeat.setAlpha(1.0f);
                }
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

    private void displaySongInfo() {
        txtTitle.setText(currentSong.getTitle());
        txtSong.setText(currentSong.getTitle());
        txtArtist.setText(currentSong.getArtist());

        Glide.with(this)
                .load(currentSong.getImageUrl())
                .placeholder(R.drawable.ic_music_note)
                .error(R.drawable.ic_music_note)
                .into(imgAlbum);
    }

    private void prepareMediaPlayer() {
        if (currentSong == null || currentSong.getFileUrl() == null) {
            Toast.makeText(this, "Không có link nhạc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
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

                playMusic();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                btnPlay.setImageResource(R.drawable.ic_play);
                seekBar.setProgress(0);
                txtCurrent.setText("0:00");

                // Xử lý theo chế độ Repeat
                handleSongCompletion();
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
            playPreviousSong();
        });

        btnNext.setOnClickListener(v -> {
            playNextSong();
        });

        // 👇 SHUFFLE: Bật/tắt phát ngẫu nhiên
        btnShuffle.setOnClickListener(v -> {
            // Kiểm tra danh sách có rỗng không
            if (songList == null || songList.isEmpty()) {
                Toast.makeText(this, "Không có danh sách bài hát", Toast.LENGTH_SHORT).show();
                return;
            }

            isShuffleOn = !isShuffleOn;

            if (isShuffleOn) {
                // Lưu bài đang phát
                Song currentPlaying = songList.get(currentPosition);

                // Xáo trộn danh sách
                Collections.shuffle(songList);

                // Đưa bài đang phát lên đầu
                songList.remove(currentPlaying);
                songList.add(0, currentPlaying);
                currentPosition = 0;

                Toast.makeText(this, "Bật Shuffle", Toast.LENGTH_SHORT).show();
            } else {
                // Khôi phục danh sách gốc
                Song currentPlaying = songList.get(currentPosition);
                songList = new ArrayList<>(originalSongList);
                currentPosition = songList.indexOf(currentPlaying);

                Toast.makeText(this, "Tắt Shuffle", Toast.LENGTH_SHORT).show();
            }

            updateShuffleButton();
        });

        // 👇 REPEAT: Chuyển đổi giữa 3 chế độ
        btnRepeat.setOnClickListener(v -> {
            repeatMode = (repeatMode + 1) % 3; // Chuyển 0 → 1 → 2 → 0

            String message = "";
            switch (repeatMode) {
                case REPEAT_OFF:
                    message = "Tắt Repeat";
                    break;
                case REPEAT_ALL:
                    message = "Lặp tất cả";
                    break;
                case REPEAT_ONE:
                    message = "Lặp 1 bài";
                    break;
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            updateRepeatButton();
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

    // 👇 Xử lý khi bài hát kết thúc
    private void handleSongCompletion() {
        switch (repeatMode) {
            case REPEAT_ONE:
                // Phát lại bài hiện tại
                prepareMediaPlayer();
                break;

            case REPEAT_ALL:
                // Chuyển bài tiếp theo
                playNextSong();
                break;

            case REPEAT_OFF:
                // Chuyển bài tiếp theo (nếu chưa hết danh sách)
                if (currentPosition < songList.size() - 1) {
                    playNextSong();
                } else {
                    // Hết danh sách thì dừng
                    Toast.makeText(this, "Đã phát hết danh sách", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void playNextSong() {
        if (songList == null || songList.isEmpty()) {
            Toast.makeText(this, "Không có bài hát tiếp theo", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPosition++;
        if (currentPosition >= songList.size()) {
            currentPosition = 0; // Quay lại bài đầu
        }

        currentSong = songList.get(currentPosition);
        displaySongInfo();
        prepareMediaPlayer();

        Toast.makeText(this, "Đang phát: " + currentSong.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void playPreviousSong() {
        if (songList == null || songList.isEmpty()) {
            Toast.makeText(this, "Không có bài hát trước đó", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu đang phát > 3 giây, restart bài hiện tại
        if (mediaPlayer != null && mediaPlayer.getCurrentPosition() > 3000) {
            mediaPlayer.seekTo(0);
            txtCurrent.setText("0:00");
            seekBar.setProgress(0);
            return;
        }

        currentPosition--;
        if (currentPosition < 0) {
            currentPosition = songList.size() - 1; // Quay về bài cuối
        }

        currentSong = songList.get(currentPosition);
        displaySongInfo();
        prepareMediaPlayer();

        Toast.makeText(this, "Đang phát: " + currentSong.getTitle(), Toast.LENGTH_SHORT).show();
    }

    // 👇 Cập nhật màu nút Shuffle
    private void updateShuffleButton() {
        if (isShuffleOn) {
            btnShuffle.setColorFilter(getResources().getColor(android.R.color.holo_green_light));
        } else {
            btnShuffle.setColorFilter(getResources().getColor(android.R.color.white));
        }
    }

    // 👇 Cập nhật màu và icon nút Repeat
    private void updateRepeatButton() {
        switch (repeatMode) {
            case REPEAT_OFF:
                btnRepeat.setColorFilter(getResources().getColor(android.R.color.white));
                btnRepeat.setImageResource(R.drawable.repeat); // Icon repeat thường
                break;

            case REPEAT_ALL:
                btnRepeat.setColorFilter(getResources().getColor(android.R.color.holo_green_light));
                btnRepeat.setImageResource(R.drawable.repeat); // Icon repeat thường
                break;

            case REPEAT_ONE:
                btnRepeat.setColorFilter(getResources().getColor(android.R.color.holo_green_light));
                // Nếu có icon repeat_one riêng thì dùng, không thì giữ nguyên
                // btnRepeat.setImageResource(R.drawable.repeat_one);
                break;
        }
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