package com.example.myapplication;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class RecViewContent {
    private String name;
    private String image;

    public RecViewContent(String name, String image){
        this.name = name;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }
}
