package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class SignupRequest {
    @SerializedName("name")     public String name;
    @SerializedName("email")    public String email;
    @SerializedName("password") public String password;

    public SignupRequest(String name, String email, String password) {
        this.name     = name;
        this.email    = email;
        this.password = password;
    }
}
