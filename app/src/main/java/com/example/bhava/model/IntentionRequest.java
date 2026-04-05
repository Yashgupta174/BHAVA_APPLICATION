package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class IntentionRequest {
    @SerializedName("content")
    private String content;

    public IntentionRequest(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
}
