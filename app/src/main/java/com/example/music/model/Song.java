package com.example.music.model;

import java.io.Serializable;

public class Song implements Serializable {
    // 👇 SỬA 1: Đổi String thành Long để khớp với Backend
    private Long id;

    private String title;
    private String artist;
    private String imageUrl;
    private String fileUrl;
    private int duration;
    private boolean isFavorite;

    // 👇 SỬA 2: Thêm đối tượng Category để hứng dữ liệu thể loại từ API
    private Category category;

    public Song() {
    }

    public Song(Long id, String title, String artist, String imageUrl, String fileUrl, int duration, boolean isFavorite, Category category) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.duration = duration;
        this.isFavorite = isFavorite;
        this.category = category;
    }

    // --- Getters và Setters ---

    // Nhớ sửa kiểu trả về là Long
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

    // Getter Setter cho Category
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}