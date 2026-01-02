package com.example.music.model;

import java.io.Serializable;

public class Artist implements Serializable {
    private String name;
    private int imageResId; // Ảnh avatar tròn

    public Artist(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
