package com.example.music.model;

import java.io.Serializable;
import java.util.List;

public class Playlist implements Serializable {
    private String id;
    private String name;
    private String ownerName;   // Tên người tạo (cho Public Playlist)
    private String imageUrl;    // Ảnh đại diện playlist (thường là ảnh bài đầu tiên)
    private List<Song> songs;   // Danh sách bài hát bên trong
    private boolean isPublic;   // Công khai hay riêng tư

    public Playlist(String id, String name, String ownerName, String imageUrl) {
        this.id = id;
        this.name = name;
        this.ownerName = ownerName;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { this.songs = songs; }
}