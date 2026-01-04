package com.example.music.model;

import java.io.Serializable;

public class Artist implements Serializable {
    // SỬA QUAN TRỌNG: Đổi String thành Long
    private Long id;

    private String name;
    private String imageUrl;

    // 1. Constructor rỗng (Bắt buộc để Retrofit/Gson hoạt động tốt)
    public Artist() {
    }

    // 2. Constructor đầy đủ (Cập nhật tham số id thành Long)
    public Artist(Long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // --- Getters và Setters ---

    // Nhớ sửa kiểu trả về và tham số là Long
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}