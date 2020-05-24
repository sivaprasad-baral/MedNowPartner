package com.example.mednowpartner.model;

public class User {

    private String email,phone,name,userId,profileImg;
    private Double latitude,longitude;

    public User() {
    }

    public User(String email, String phone, String name, String userId, String profileImg, Double latitude, Double longitude) {
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.userId = userId;
        this.profileImg = profileImg;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
