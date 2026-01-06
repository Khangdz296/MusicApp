package com.example.music.model;

import com.google.gson.annotations.SerializedName; // Import thư viện GSON
import java.io.Serializable;

public class Song implements Serializable {

    //Dùng @SerializedName để đảm bảo tên biến khớp 100% với JSON Backend
    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("artist")
    private String artist;

    @SerializedName("imageUrl") // Backend có thể trả về "imageUrl" hoặc "image_url" tùy config
    private String imageUrl;

    @SerializedName("albumArt")
    private String albumArt;
    @SerializedName("fileUrl")
    private String fileUrl;

    @SerializedName("duration")
    private int duration;

    @SerializedName("favorite")
    private boolean isFavorite;

    @SerializedName("category")
    private Category category;

    //MỚI THÊM: Biến này để hứng số lượt nghe làm BXH
    @SerializedName("views")
    private int views;

    public Song() {
    }

    // Constructor cập nhật đầy đủ tham số
    public Song(Long id, String title, String artist, String imageUrl,String albumArt, String fileUrl, int duration, boolean isFavorite, Category category, int views) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.duration = duration;
        this.isFavorite = isFavorite;
        this.category = category;
        this.views = views;
        this.albumArt = albumArt;
    }

    // --- Getters và Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    //  Getter & Setter cho Views
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }
}