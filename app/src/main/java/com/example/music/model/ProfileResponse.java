package com.example.music.model;

import java.util.Date;

public class ProfileResponse {
    private String status;
    private String message;
    private UserData user;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        private Date join_date;
        private Boolean is_active;

        public Long getUser_id() {
            return user_id;
        }

        public void setUser_id(Long user_id) {
            this.user_id = user_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public Date getJoin_date() {
            return join_date;
        }

        public void setJoin_date(Date join_date) {
            this.join_date = join_date;
        }

        public Boolean getIs_active() {
            return is_active;
        }

        public void setIs_active(Boolean is_active) {
            this.is_active = is_active;
        }
    }
}