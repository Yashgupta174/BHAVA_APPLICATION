package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class AiRequest {
    @SerializedName("message")
    public String message;

    public AiRequest(String message) {
        this.message = message;
    }
}
