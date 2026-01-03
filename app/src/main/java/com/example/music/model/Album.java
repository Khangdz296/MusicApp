package com.example.music.model;

import java.io.Serializable;

public class Album implements Serializable {
    private String id;
    private String name;        // Tên Album
    private String artistName;  // Tên Ca sĩ
    private String imageUrl;    // Link ảnh bìa album

    public Album(String id, String name, String artistName, String imageUrl) {
        this.id = id;
        this.name = name;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}