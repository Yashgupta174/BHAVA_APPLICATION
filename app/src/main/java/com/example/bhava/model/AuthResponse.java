package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("success") public boolean success;
    @SerializedName("message") public String message;
    @SerializedName("token")   public String token;
    @SerializedName("user")    public UserModel user;
}
