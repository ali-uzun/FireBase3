package com.example.firebase3;

public class Post {
    private String userEmail;
    private String imageUrl;
    private String dateTime;

    public Post(String userEmail, String imageUrl, String dateTime) {
        this.userEmail = userEmail;
        this.imageUrl = imageUrl;
        this.dateTime = dateTime;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDateTime() {
        return dateTime;
    }
}
