package com.example.music.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Playlist implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("is_public")
    private boolean isPublic;

    // 👇 QUAN TRỌNG: Backend trả về object "user", nên ở đây phải là User type
    @SerializedName("user")
    private User user;

    @SerializedName("songs")
    private List<Song> songs;

    // Constructor
    public Playlist(Long id, String name, User user, String imageUrl) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.imageUrl = imageUrl;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }

    public User getUser() { return user; } // Lấy User ra để hiển thị tên
    public void setUser(User user) { this.user = user; }

    public List<Song> getSongs() { return songs; }
}