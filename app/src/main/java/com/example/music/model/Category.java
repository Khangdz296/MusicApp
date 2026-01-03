package com.example.music.model;

import java.io.Serializable;

public class Category implements Serializable {
    // 👇 SỬA QUAN TRỌNG: Đổi String thành Long để khớp với Backend
    private Long id;
    private String name;
    private String imageUrl;

    // 1. Constructor rỗng (Bắt buộc phải có để Gson/Jackson đọc JSON không bị lỗi)
    public Category() {
    }

    // 2. Constructor đầy đủ
    public Category(Long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // --- Getters và Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}