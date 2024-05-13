package com.example.myapplication;

import com.google.firebase.auth.FirebaseUser;

public class User {
     long id;
     String name;
     String email;
     String password;
     String displayName;

    private String profileImageUrl;

    public User() {}

    public User(String email,String name, String password,long id) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.id = id;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public String getProfilePhotoUrl() {

        return profileImageUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {

        this.profileImageUrl = profilePhotoUrl;
    }
    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static User fromFirebaseUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setDisplayName(firebaseUser.getDisplayName());
        user.setEmail(firebaseUser.getEmail());
        // Set other fields if needed
        return user;
    }
}
