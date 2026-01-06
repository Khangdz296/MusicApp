package com.example.music.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Thêm cái này để truyền object giữa các Activity

public class User implements Serializable {

    // THÊM TRƯỜNG ID QUAN TRỌNG NÀY
    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("password")
    private String password;

    // --- Constructor rỗng (Cần thiết cho Gson/Retrofit) ---
    public User() {
    }

    // --- Constructor đầy đủ (Để hứng dữ liệu từ API) ---
    public User(Long id, String username, String email, String fullName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    // --- Constructor cũ (Giữ lại để không lỗi code đăng ký/đăng nhập cũ) ---
    public User(String username, String email, String fullName, String password) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // --- Getter & Setter (Nhớ thêm cho ID) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}