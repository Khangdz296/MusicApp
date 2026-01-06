package com.example.music.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List; // Nhớ import

public class Album implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("artist")
    private Artist artist;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("songs")
    private List<Song> songs;

    public Album(Long id, String name, Artist artist, String imageUrl, List<Song> songs) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.songs = songs;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }

    // 👇 Getter mới
    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { this.songs = songs; }
    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }
}