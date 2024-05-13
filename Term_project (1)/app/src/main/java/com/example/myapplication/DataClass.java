package com.example.myapplication;


public class DataClass {
    private String imageURL, caption;
    private String username;
    private String profilePhotoUrl;
    String postId;
    String userId;


    public DataClass(){

    }


    public DataClass(String imageURL, String caption, String username, String profilePhotoUrl, String postId, String userId) {
        this.imageURL = imageURL;
        this.caption = caption;
        this.username = username;
        this.profilePhotoUrl = profilePhotoUrl;
        this.postId = postId;
        this.userId = userId;
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public DataClass(String imageURL, String caption) {
        this.imageURL = imageURL;
        this.caption = caption;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImageURL() {
        return profilePhotoUrl;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profilePhotoUrl = profileImageURL;
    }

}