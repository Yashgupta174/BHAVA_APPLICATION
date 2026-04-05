package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("success") public boolean success;
    @SerializedName("message") public String message;
}
