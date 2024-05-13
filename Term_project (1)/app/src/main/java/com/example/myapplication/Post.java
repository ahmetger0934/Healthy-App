package com.example.myapplication;

public class Post {
    private String postid;
    private String postimage;
    private String description;
    private String publisher;
    private int likes;
    private int comments;
    private int shares;


    public Post(String postid, String postimage, String description, String publisher, int likes, int comments, int shares) {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
    }

    public Post() {
    }

    public Post(String recipe) {
        this.description = recipe;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postid='" + postid + '\'' +
                ", postimage='" + postimage + '\'' +
                ", description='" + description + '\'' +
                ", publisher='" + publisher + '\'' +
                ", likes=" + likes +
                ", comments=" + comments +
                ", shares=" + shares +
                '}';
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void incrementComments() {
        this.comments++;
    }

    public void incrementShares() {
        this.shares++;
    }

    public void decrementLikes() {
        this.likes--;
    }

    public void decrementComments() {
        this.comments--;
    }

    public void decrementShares() {
        this.shares--;
    }

    public void incrementLikes(int n) {
        this.likes += n;
    }

    public void incrementComments(int n) {
        this.comments += n;
    }

    public void incrementShares(int n) {
        this.shares += n;
    }

    public void decrementLikes(int n) {
        this.likes -= n;
    }

    public void decrementComments(int n) {
        this.comments -= n;
    }
}

