package com.example.music.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Album implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Constructor, Getter, Setter
    public Album(Long id, String name, String artistName, String imageUrl) {
        this.id = id;
        this.name = name;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getArtistName() { return artistName; }
    public String getImageUrl() { return imageUrl; }
}