package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class SessionItem {
    @SerializedName(value = "_id", alternate = {"id"})
    private String id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("subtitle")
    private String subtitle;
    
    @SerializedName(value = "audioUrl", alternate = {"audio_url", "audio"})
    private String audioUrl;
    
    @SerializedName("duration")
    private String duration;
    
    @SerializedName("day")
    private Integer day;
    
    @SerializedName("description")
    private String description;

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getAudioUrl() {
        if (audioUrl != null && audioUrl.startsWith("/uploads")) {
            // Prepend BASE_URL. Since BASE_URL has a trailing slash, we remove the leading slash from 'audioUrl'.
            return com.example.bhava.network.ApiClient.BASE_URL + audioUrl.substring(1);
        }
        return audioUrl;
    }
    public String getDuration() { return duration; }
    public Integer getDay() { return day; }
    public String getDescription() { return description; }
}
