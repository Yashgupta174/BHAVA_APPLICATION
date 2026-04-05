package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class FavoriteStatusResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("isFavorite")
    private boolean isFavorite;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getMessage() {
        return message;
    }
}
