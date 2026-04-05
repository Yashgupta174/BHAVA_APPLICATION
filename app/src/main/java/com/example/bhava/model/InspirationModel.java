package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class InspirationModel {
    @SerializedName("_id")
    private String id;

    @SerializedName("source")
    private String source;

    @SerializedName("content")
    private String content;

    @SerializedName("author")
    private String author;

    @SerializedName("date")
    private String date;

    @SerializedName("isActive")
    private boolean isActive;

    public String getId() { return id; }
    public String getSource() { return source; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public boolean isActive() { return isActive; }
}
