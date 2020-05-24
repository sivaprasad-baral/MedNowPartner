package com.example.mednowpartner.model;

public class Partner {

    private String email,name,phone,userId,profileImg;
    private String licenseNo,pharmacyName,pharmacyImg;
    private Double latitude,longitude,rating;
    private boolean available;

    public Partner() {
    }

    public Partner(String email, String name, String userId) {
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

    public Partner(String email, String name, String phone, String userId) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.userId = userId;
    }

    public Partner(String email, String name, String phone, String userId, String profileImg, String licenseNo, String pharmacyName, String pharmacyImg, Double latitude, Double longitude, Double rating, boolean available) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.userId = userId;
        this.profileImg = profileImg;
        this.licenseNo = licenseNo;
        this.pharmacyName = pharmacyName;
        this.pharmacyImg = pharmacyImg;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.available = available;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getPharmacyImg() {
        return pharmacyImg;
    }

    public void setPharmacyImg(String pharmacyImg) {
        this.pharmacyImg = pharmacyImg;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
