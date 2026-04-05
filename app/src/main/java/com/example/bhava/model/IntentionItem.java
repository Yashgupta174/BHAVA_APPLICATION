package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class IntentionItem {
    @SerializedName("_id")
    private String id;

    @SerializedName("content")
    private String content;

    @SerializedName("createdAt")
    private String createdAt;

    public String getId() { return id; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }

    public void setContent(String content) { this.content = content; }
}
