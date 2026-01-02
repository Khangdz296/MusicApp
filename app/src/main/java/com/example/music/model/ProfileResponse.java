package com.example.music.model;

public class ProfileResponse {
    private String status;
    private UserData user;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public static class UserData {
        private Long user_id;
        private String username;
        private String email;
        private String full_name;
        private String join_date;
        private Boolean is_active;

        // Getters
        public Long getUser_id() {
            return user_id;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getFull_name() {
            return full_name;
        }

        public String getJoin_date() {
            return join_date;
        }

        public Boolean getIs_active() {
            return is_active;
        }

        // Setters
        public void setUser_id(Long user_id) {
            this.user_id = user_id;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public void setJoin_date(String join_date) {
            this.join_date = join_date;
        }

        public void setIs_active(Boolean is_active) {
            this.is_active = is_active;
        }
    }
}