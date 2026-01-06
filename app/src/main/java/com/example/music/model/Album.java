package com.example.music.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List; // Nhớ import

public class Album implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("songs")
    private List<Song> songs;

    public Album(Long id, String name, String artistName, String imageUrl, List<Song> songs) {
        this.id = id;
        this.name = name;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.songs = songs;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getArtistName() { return artistName; }
    public String getImageUrl() { return imageUrl; }

    // 👇 Getter mới
    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { this.songs = songs; }
}