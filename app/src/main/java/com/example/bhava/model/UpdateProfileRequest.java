package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("name")   public String name;
    @SerializedName("avatar")     public String avatar;
    @SerializedName("bio")        public String bio;
    @SerializedName("phoneNumber") public String phoneNumber;
    @SerializedName("location")   public String location;

    public UpdateProfileRequest(String name, String avatar, String bio, String phoneNumber, String location) {
        this.name        = name;
        this.avatar      = avatar;
        this.bio         = bio;
        this.phoneNumber = phoneNumber;
        this.location    = location;
    }
}
