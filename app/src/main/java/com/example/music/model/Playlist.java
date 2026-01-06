package com.example.music.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("user")
    private User user;

    @SerializedName("songs")
    private List<Song> songs;

    // Constructor rỗng (Bắt buộc để Gson hoạt động ổn định)
    public Playlist() {
    }

    // Constructor để tạo mới nhanh
    public Playlist(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Xử lý list song để tránh NullPointerException
    public List<Song> getSongs() {
        if (songs == null) songs = new ArrayList<>();
        return songs;
    }
    public void setSongs(List<Song> songs) { this.songs = songs; }
}