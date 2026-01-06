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

    @SerializedName("isPublic")
    private boolean isPublic;

    @SerializedName("user")
    private User user;

    @SerializedName("songs")
    private List<Song> songs;

    // Constructor
    public Playlist() {}

    public Playlist(String name, Long userId) {
        this.name = name;
        User user = new User();
        user.setId(userId);
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    // ========== Helper Methods ==========

    /**
     * Lấy tên người tạo playlist
     */
    public String getOwnerName() {
        if (user != null) {
            if (user.getFullName() != null && !user.getFullName().isEmpty()) {
                return user.getFullName();
            }
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                return user.getUsername();
            }
        }
        return "Unknown";
    }

    /**
     * Đếm số bài hát
     */
    public int getSongCount() {
        return songs != null ? songs.size() : 0;
    }

    /**
     * Lấy ảnh cover
     */
    public String getCoverImage() {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        }
        if (songs != null && !songs.isEmpty() && songs.get(0) != null) {
            return songs.get(0).getAlbumArt();
        }
        return null;
    }

    /**
     * Format: "By Creator • X songs"
     */
    public String getCreatorAndSongCount() {
        return "By " + getOwnerName() + " • " + getSongCount() + " songs";
    }
}