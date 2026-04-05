package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class AiResponse {
    @SerializedName("success")
    public boolean success;

    @SerializedName("data")
    public AiData data;

    public static class AiData {
        @SerializedName("reply")
        public String reply;

        @SerializedName("role")
        public String role;

        @SerializedName("timestamp")
        public String timestamp;
    }
}
