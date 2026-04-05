package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {
    @SerializedName("token")
    private String token;

    @SerializedName("password")
    private String password;

    public ResetPasswordRequest(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
