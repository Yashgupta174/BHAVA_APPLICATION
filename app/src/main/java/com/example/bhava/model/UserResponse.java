package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("success") public boolean success;
    @SerializedName("message") public String message;
    @SerializedName("user")    public UserModel user;
}
