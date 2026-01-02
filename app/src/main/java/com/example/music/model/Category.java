package com.example.music.model;

public class Category {
    private String name;
    private int imageResId; // Màu nền hoặc ảnh nền

    public Category(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }
    public String getName() { return name; }
    public int getImageResId() { return imageResId; }
}