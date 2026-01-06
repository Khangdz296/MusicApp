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

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.model.Song;
import com.example.music.ui.PlayMusicActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Singleton class để quản lý Mini Player
 * Đảm bảo chỉ có 1 instance duy nhất xuyên suốt app
 */
public class MiniPlayerManager {

    private static final String TAG = "MiniPlayerManager";
    private static MiniPlayerManager instance;

    // Repeat modes
    public static final int REPEAT_OFF = 0;
    public static final int REPEAT_ALL = 1;
    public static final int REPEAT_ONE = 2;

    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private List<Song> songList = new ArrayList<>();
    private List<Song> originalSongList = new ArrayList<>(); // Lưu thứ tự gốc
    private int currentPosition = 0;
    private boolean isPlaying = false;

    // Shuffle và Repeat
    private boolean isShuffleEnabled = false;
    private int repeatMode = REPEAT_OFF;

    private View miniPlayerView;
    private ImageView imgMiniAlbum;
    private TextView txtMiniTitle, txtMiniArtist;
    private ImageButton btnMiniPlay, btnMiniNext;
    private ProgressBar miniProgressBar;

    private Handler handler = new Handler();
    private Context context;

    // Private constructor để ngăn khởi tạo từ bên ngoài
    private MiniPlayerManager() {
    }

    // Lấy instance duy nhất
    public static synchronized MiniPlayerManager getInstance() {
        if (instance == null) {
            instance = new MiniPlayerManager();
        }
        return instance;
    }

    // Khởi tạo Mini Player với View từ MainActivity
    public void initialize(Context context, View miniPlayerView) {
        this.context = context;
        this.miniPlayerView = miniPlayerView;

        imgMiniAlbum = miniPlayerView.findViewById(R.id.imgMiniAlbum);
        txtMiniTitle = miniPlayerView.findViewById(R.id.txtMiniTitle);
        txtMiniArtist = miniPlayerView.findViewById(R.id.txtMiniArtist);
        btnMiniPlay = miniPlayerView.findViewById(R.id.btnMiniPlay);
        btnMiniNext = miniPlayerView.findViewById(R.id.btnMiniNext);
        miniProgressBar = miniPlayerView.findViewById(R.id.miniProgressBar);

        setupListeners();
        Log.d(TAG, "Mini Player initialized");
    }

    private void setupListeners() {
        // Click vào mini player -> Mở PlayMusicActivity
        View content = miniPlayerView.findViewById(R.id.miniPlayerContent);
        if (content != null) {
            content.setOnClickListener(v -> openFullPlayer());
        }

        // Nút Play/Pause
        btnMiniPlay.setOnClickListener(v -> {
            if (isPlaying) {
                pauseMusic();
            } else {
                playMusic();
            }
        });

        // Nút Next
        btnMiniNext.setOnClickListener(v -> playNext());
    }

    // Phát bài hát mới
    public void playSong(Song song, List<Song> list, int position) {
        this.currentSong = song;
        this.originalSongList = list != null ? new ArrayList<>(list) : new ArrayList<>();

        // Nếu shuffle đang bật, giữ nguyên danh sách đã shuffle
        if (!isShuffleEnabled) {
            this.songList = new ArrayList<>(originalSongList);
        } else {
            // Tạo lại danh sách shuffle với bài hiện tại ở đầu
            createShuffledList(song);
        }

        this.currentPosition = position;

        prepareMediaPlayer();
        showMiniPlayer();
        updateMiniPlayerUI();
    }

    private void prepareMediaPlayer() {
        if (currentSong == null) return;

        // Release MediaPlayer cũ
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
                playMusic();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                updatePlayButton();
                handleSongCompletion();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: " + what);
                return true;
            });

        } catch (IOException e) {
            Log.e(TAG, "Error preparing media: " + e.getMessage());
        }
    }

    private void handleSongCompletion() {
        switch (repeatMode) {
            case REPEAT_ONE:
                // Phát lại bài hiện tại
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(0);
                    playMusic();
                }
                break;
            case REPEAT_ALL:
                // Phát bài tiếp theo
                playNext();
                break;
            case REPEAT_OFF:
            default:
                // Phát bài tiếp, nếu hết danh sách thì dừng
                if (currentPosition < songList.size() - 1) {
                    playNext();
                } else {
                    // Hết danh sách, dừng phát
                    isPlaying = false;
                    updatePlayButton();
                }
                break;
        }
    }

    public void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            updatePlayButton();
            updateProgress();
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            updatePlayButton();
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
        prepareMediaPlayer();
        updateMiniPlayerUI();
    }

    public void playPrevious() {
        if (songList.isEmpty()) return;

        // Nếu đang phát > 3 giây, restart bài hiện tại
        if (mediaPlayer != null && mediaPlayer.getCurrentPosition() > 3000) {
            mediaPlayer.seekTo(0);
            return;
        }

        currentPosition--;
        if (currentPosition < 0) {
            currentPosition = songList.size() - 1;
        }

        currentSong = songList.get(currentPosition);
        prepareMediaPlayer();
        updateMiniPlayerUI();
    }

    // Shuffle
    public void toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled;

        if (isShuffleEnabled) {
            createShuffledList(currentSong);
        } else {
            // Tắt shuffle, trở về danh sách gốc
            songList = new ArrayList<>(originalSongList);
            // Tìm vị trí của bài hiện tại trong danh sách gốc
            currentPosition = findSongPosition(currentSong);
        }

        Log.d(TAG, "Shuffle: " + (isShuffleEnabled ? "ON" : "OFF"));
    }

    private void createShuffledList(Song currentSong) {
        songList = new ArrayList<>(originalSongList);

        // Xáo trộn danh sách
        Collections.shuffle(songList, new Random());

        // Đưa bài hiện tại lên đầu
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

    // Repeat
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

    private void updateMiniPlayerUI() {
        if (currentSong == null) return;

        txtMiniTitle.setText(currentSong.getTitle());
        txtMiniTitle.setSelected(true); // Bật marquee

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
            int currentPos = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();

            if (duration > 0) {
                int progress = (int) ((currentPos * 100.0) / duration);
                miniProgressBar.setProgress(progress);
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

    // Cleanup
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
        isPlaying = false;
    }
}