package com.example.music;
import java.io.Serializable;

public class Song implements Serializable {
    private String id;          // ID duy nhất (VD: "song01")
    private String title;       // Tên bài hát (VD: "Chúng ta của hiện tại")
    private String artist;      // Tên ca sĩ (VD: "Sơn Tùng M-TP")
    private String imageUrl;    // Link ảnh bìa (VD: "https://example.com/anh.jpg")
    private String fileUrl;     // Link file nhạc mp3 (VD: "https://example.com/nhac.mp3")
    private int duration;       // Thời lượng (tính bằng giây)
    private boolean isFavorite; // Trạng thái yêu thích (true/false)

    // 1. Constructor rỗng (Cần thiết nếu sau này dùng Firebase)
    public Song() {
    }

    // 2. Constructor đầy đủ (Dùng để tạo dữ liệu giả)
    public Song(String id, String title, String artist, String imageUrl, String fileUrl, int duration, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.duration = duration;
        this.isFavorite = isFavorite;
    }

    // 3. Getter và Setter (Để lấy và sửa dữ liệu)

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}