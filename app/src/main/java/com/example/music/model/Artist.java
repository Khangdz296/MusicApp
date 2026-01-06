package com.example.music.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Artist implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("imageUrl")
    private String imageUrl;

    // 👇 THÊM TRƯỜNG MÔ TẢ (Biography)
    @SerializedName("description")
    private String description;

    // 1. Constructor rỗng
    public Artist() {
    }

    // 2. Constructor đầy đủ
    public Artist(Long id, String name, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // --- Getters và Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // 👇 Getter và Setter cho Description
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}