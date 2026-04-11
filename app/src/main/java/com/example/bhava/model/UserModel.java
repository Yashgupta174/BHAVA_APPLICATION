package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("id")        public String id;
    @SerializedName("name")      public String name;
    @SerializedName("email")     public String email;
    @SerializedName("provider")  public String provider;
    @SerializedName("avatar")    public String avatar;
    @SerializedName("bio")       public String bio;
    @SerializedName("phoneNumber") public String phoneNumber;
    @SerializedName("location")  public String location;
    @SerializedName("createdAt") public String createdAt;
    @SerializedName("joinedChallenges") public java.util.List<String> joinedChallenges;
    @SerializedName("streakCount") public int streakCount;
}
