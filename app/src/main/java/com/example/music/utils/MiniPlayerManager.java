package com.example.music.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.model.Song;
import com.example.music.ui.PlayMusicActivity;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MiniPlayerManager {

    private static final String TAG = "MiniPlayerManager";
    private static MiniPlayerManager instance;

    // Repeat modes
    public static final int REPEAT_OFF = 0;
    public static final int REPEAT_ALL = 1;
    public static final int REPEAT_ONE = 2;

    // Timeout và retry
    private static final long PREPARE_TIMEOUT = 15000; // 15 giây
    private static final int MAX_RETRY_COUNT = 3;
    private int currentRetryCount = 0;

    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private List<Song> songList = new ArrayList<>();
    private List<Song> originalSongList = new ArrayList<>();
    private int currentPosition = 0;
    private boolean isPlaying = false;
    private boolean isPreparing = false; // Đang chuẩn bị phát

    // Shuffle và Repeat
    private boolean isShuffleEnabled = false;
    private int repeatMode = REPEAT_OFF;

    private View miniPlayerView;
    private ImageView imgMiniAlbum;
    private TextView txtMiniTitle, txtMiniArtist;
    private ImageButton btnMiniPlay, btnMiniNext;
    private ProgressBar miniProgressBar;

    private Handler handler = new Handler();
    private Handler timeoutHandler = new Handler();
    private Runnable timeoutRunnable;
    private Context context;
    private ApiService apiService;

    // Callback listener
    public interface OnPlayerStateListener {
        void onPrepared();
        void onError();
        void onSongChanged();
    }

    private OnPlayerStateListener playerStateListener;

    private MiniPlayerManager() {
    }

    public static synchronized MiniPlayerManager getInstance() {
        if (instance == null) {
            instance = new MiniPlayerManager();
        }
        return instance;
    }

    public void initialize(Context context, View miniPlayerView) {
        this.context = context;
        this.miniPlayerView = miniPlayerView;

        imgMiniAlbum = miniPlayerView.findViewById(R.id.imgMiniAlbum);
        txtMiniTitle = miniPlayerView.findViewById(R.id.txtMiniTitle);
        txtMiniArtist = miniPlayerView.findViewById(R.id.txtMiniArtist);
        btnMiniPlay = miniPlayerView.findViewById(R.id.btnMiniPlay);
        btnMiniNext = miniPlayerView.findViewById(R.id.btnMiniNext);
        miniProgressBar = miniPlayerView.findViewById(R.id.miniProgressBar);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        setupListeners();
        Log.d(TAG, "Mini Player initialized");
    }

    private void setupListeners() {
        View content = miniPlayerView.findViewById(R.id.miniPlayerContent);
        if (content != null) {
            content.setOnClickListener(v -> openFullPlayer());
        }

        btnMiniPlay.setOnClickListener(v -> {
            if (isPreparing) {
                showToast("Đang tải bài hát, vui lòng chờ...");
                return;
            }

            if (isPlaying) {
                pauseMusic();
            } else {
                playMusic();
            }
        });

        btnMiniNext.setOnClickListener(v -> {
            if (isPreparing) {
                showToast("Đang tải bài hát, vui lòng chờ...");
                return;
            }
            playNext();
        });
    }

    public void playSong(Song song, List<Song> list, int position) {
        this.currentSong = song;
        this.originalSongList = list != null ? new ArrayList<>(list) : new ArrayList<>();

        if (!isShuffleEnabled) {
            this.songList = new ArrayList<>(originalSongList);
        } else {
            createShuffledList(song);
        }

        this.currentPosition = position;
        this.currentRetryCount = 0; // Reset retry count

        prepareMediaPlayer();
        showMiniPlayer();
        updateMiniPlayerUI();
    }

    private void prepareMediaPlayer() {
        if (currentSong == null) return;

        // Hủy timeout cũ nếu có
        cancelTimeout();

        // Release MediaPlayer cũ
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        isPreparing = true;
        showLoadingState();

        try {
            mediaPlayer = new MediaPlayer();

            // Cài đặt các thuộc tính để tối ưu cho streaming
            mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);

            mediaPlayer.setDataSource(currentSong.getFileUrl());
            mediaPlayer.prepareAsync();

            // Bắt đầu timeout
            startTimeout();

            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "MediaPlayer prepared successfully");
                cancelTimeout();
                isPreparing = false;
                currentRetryCount = 0; // Reset retry sau khi thành công
                hideLoadingState();
                playMusic();

                // Gọi API tăng view
                if (currentSong != null && currentSong.getId() != null) {
                    incrementViewCount(currentSong.getId());
                }

                // Thông báo đã chuẩn bị xong
                if (playerStateListener != null) {
                    playerStateListener.onPrepared();
                }
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                updatePlayButton();
                handleSongCompletion();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: what=" + what + ", extra=" + extra);
                cancelTimeout();
                isPreparing = false;
                hideLoadingState();
                handleMediaPlayerError(what, extra);
                return true;
            });

            // Xử lý lỗi buffering
            mediaPlayer.setOnInfoListener((mp, what, extra) -> {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    Log.d(TAG, "Buffering started");
                    showToast("Đang tải...");
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    Log.d(TAG, "Buffering ended");
                }
                return false;
            });

        } catch (IOException e) {
            Log.e(TAG, "Error preparing media: " + e.getMessage());
            isPreparing = false;
            hideLoadingState();
            handlePrepareError();
        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException: " + e.getMessage());
            isPreparing = false;
            hideLoadingState();
            handlePrepareError();
        }
    }

    private void incrementViewCount(Long songId) {
        if (apiService == null) return;

        apiService.incrementView(songId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "View count updated for song: " + songId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Failed to update view count: " + t.getMessage());
            }
        });
    }

    private void startTimeout() {
        timeoutRunnable = () -> {
            Log.e(TAG, "Prepare timeout!");
            isPreparing = false;
            hideLoadingState();

            if (mediaPlayer != null) {
                try {
                    mediaPlayer.reset();
                } catch (Exception e) {
                    Log.e(TAG, "Error resetting player: " + e.getMessage());
                }
            }

            handlePrepareError();
        };

        timeoutHandler.postDelayed(timeoutRunnable, PREPARE_TIMEOUT);
    }

    private void cancelTimeout() {
        if (timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
            timeoutRunnable = null;
        }
    }

    private void handleMediaPlayerError(int what, int extra) {
        String errorMsg = "Lỗi phát nhạc";

        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                errorMsg = "Lỗi server";
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                errorMsg = "Lỗi không xác định";
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                errorMsg = "Lỗi kết nối mạng";
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                errorMsg = "File nhạc lỗi";
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                errorMsg = "Định dạng không hỗ trợ";
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                errorMsg = "Quá thời gian chờ";
                break;
        }

        showToast(errorMsg);
        handlePrepareError();
    }

    private void handlePrepareError() {
        if (currentRetryCount < MAX_RETRY_COUNT) {
            currentRetryCount++;
            Log.d(TAG, "Retrying... Attempt " + currentRetryCount);
            showToast("Đang thử lại... (" + currentRetryCount + "/" + MAX_RETRY_COUNT + ")");

            // Retry sau 2 giây
            handler.postDelayed(() -> {
                prepareMediaPlayer();
            }, 2000);
        } else {
            Log.e(TAG, "Max retry reached. Giving up.");
            showToast("Không thể phát nhạc. Vui lòng kiểm tra kết nối mạng.");
            currentRetryCount = 0;
            isPlaying = false;
            updatePlayButton();
        }
    }

    private void showLoadingState() {
        if (btnMiniPlay != null) {
            btnMiniPlay.setEnabled(false);
            btnMiniPlay.setAlpha(0.5f);
        }
        if (btnMiniNext != null) {
            btnMiniNext.setEnabled(false);
            btnMiniNext.setAlpha(0.5f);
        }
    }

    private void hideLoadingState() {
        if (btnMiniPlay != null) {
            btnMiniPlay.setEnabled(true);
            btnMiniPlay.setAlpha(1.0f);
        }
        if (btnMiniNext != null) {
            btnMiniNext.setEnabled(true);
            btnMiniNext.setAlpha(1.0f);
        }
    }

    private void showToast(String message) {
        if (context != null) {
            handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
        }
    }

    private void handleSongCompletion() {
        switch (repeatMode) {
            case REPEAT_ONE:
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(0);
                    playMusic();
                }
                break;
            case REPEAT_ALL:
                playNext();
                break;
            case REPEAT_OFF:
            default:
                if (currentPosition < songList.size() - 1) {
                    playNext();
                } else {
                    isPlaying = false;
                    updatePlayButton();
                }
                break;
        }
    }

    public void playMusic() {
        if (mediaPlayer != null && !isPreparing) {
            try {
                mediaPlayer.start();
                isPlaying = true;
                updatePlayButton();
                updateProgress();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error starting playback: " + e.getMessage());
                showToast("Không thể phát nhạc");
            }
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
                isPlaying = false;
                updatePlayButton();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error pausing playback: " + e.getMessage());
            }
        }
    }

    public void playNext() {
        if (songList.isEmpty()) return;

        currentPosition++;
        if (currentPosition >= songList.size()) {
            if (repeatMode == REPEAT_ALL) {
                currentPosition = 0;
            } else {
                currentPosition = songList.size() - 1;
                return;
            }
        }

        currentSong = songList.get(currentPosition);
        currentRetryCount = 0; // Reset retry cho bài mới
        prepareMediaPlayer();
        updateMiniPlayerUI();
    }

    public void playPrevious() {
        if (songList.isEmpty()) return;

        if (mediaPlayer != null && mediaPlayer.getCurrentPosition() > 3000) {
            mediaPlayer.seekTo(0);
            return;
        }

        currentPosition--;
        if (currentPosition < 0) {
            currentPosition = songList.size() - 1;
        }

        currentSong = songList.get(currentPosition);
        currentRetryCount = 0; // Reset retry cho bài mới
        prepareMediaPlayer();
        updateMiniPlayerUI();
    }

    public void toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled;

        if (isShuffleEnabled) {
            createShuffledList(currentSong);
        } else {
            songList = new ArrayList<>(originalSongList);
            currentPosition = findSongPosition(currentSong);
        }

        Log.d(TAG, "Shuffle: " + (isShuffleEnabled ? "ON" : "OFF"));
    }

    private void createShuffledList(Song currentSong) {
        songList = new ArrayList<>(originalSongList);
        Collections.shuffle(songList, new Random());

        int currentIndex = findSongPosition(currentSong);
        if (currentIndex != -1 && currentIndex != 0) {
            songList.remove(currentIndex);
            songList.add(0, currentSong);
        }

        currentPosition = 0;
    }

    private int findSongPosition(Song song) {
        if (song == null) return -1;
        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).getId() == song.getId()) {
                return i;
            }
        }
        return -1;
    }

    public void toggleRepeat() {
        repeatMode = (repeatMode + 1) % 3;
        String mode = repeatMode == REPEAT_OFF ? "OFF" :
                repeatMode == REPEAT_ALL ? "ALL" : "ONE";
        Log.d(TAG, "Repeat mode: " + mode);
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public boolean isShuffleEnabled() {
        return isShuffleEnabled;
    }

    public boolean isPreparing() {
        return isPreparing;
    }

    private void updateMiniPlayerUI() {
        if (currentSong == null) return;

        txtMiniTitle.setText(currentSong.getTitle());
        txtMiniTitle.setSelected(true);

        if (currentSong.getArtist() != null) {
            txtMiniArtist.setText(currentSong.getArtist().getName());
        } else {
            txtMiniArtist.setText("Unknown Artist");
        }

        if (context != null) {
            Glide.with(context)
                    .load(currentSong.getImageUrl())
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .into(imgMiniAlbum);
        }
    }

    private void updatePlayButton() {
        if (btnMiniPlay != null) {
            btnMiniPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        }
    }

    private void updateProgress() {
        if (mediaPlayer != null && isPlaying && miniProgressBar != null) {
            try {
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                if (duration > 0) {
                    int progress = (int) ((currentPos * 100.0) / duration);
                    miniProgressBar.setProgress(progress);
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error updating progress: " + e.getMessage());
            }
        }

        handler.postDelayed(() -> updateProgress(), 100);
    }

    private void showMiniPlayer() {
        if (miniPlayerView != null) {
            miniPlayerView.setVisibility(View.VISIBLE);
        }
    }

    public void hideMiniPlayer() {
        if (miniPlayerView != null) {
            miniPlayerView.setVisibility(View.GONE);
        }
    }

    private void openFullPlayer() {
        if (context == null || currentSong == null) return;

        Intent intent = new Intent(context, PlayMusicActivity.class);
        // KHÔNG thêm "play_new_song" flag - chỉ mở UI
        intent.putExtra("song_data", currentSong);
        intent.putExtra("song_list", new ArrayList<>(songList));
        intent.putExtra("current_position", currentPosition);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Getters
    public Song getCurrentSong() {
        return currentSong;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public List<Song> getSongList() {
        return songList;
    }

    // Set listener
    public void setPlayerStateListener(OnPlayerStateListener listener) {
        this.playerStateListener = listener;
    }

    // Cleanup
    public void release() {
        cancelTimeout();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing player: " + e.getMessage());
            }
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
        timeoutHandler.removeCallbacksAndMessages(null);
        isPlaying = false;
        isPreparing = false;
    }
}