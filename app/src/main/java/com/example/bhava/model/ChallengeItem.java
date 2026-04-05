package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ChallengeItem {
    @SerializedName(value = "_id", alternate = {"id"})
    private String id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName(value = "badgeText", alternate = {"badge_text"})
    private String badgeText;
    
    @SerializedName(value = "joinedCount", alternate = {"joined_count", "listeningCount"})
    private String joinedCount;
    
    @SerializedName(value = "durationText", alternate = {"duration_text"})
    private String durationText;
    
    @SerializedName(value = "fullSubtitle", alternate = {"full_subtitle", "subtitle"})
    private String fullSubtitle;
    
    @SerializedName(value = "detailsLongDescription", alternate = {"details_long_description", "longDescription"})
    private String detailsLongDescription;
    
    @SerializedName("isHero")
    private boolean isHero;

    @SerializedName("hosts")
    private List<HostItem> hosts = new ArrayList<>();
    
    @SerializedName("sessions")
    private List<SessionItem> sessions;

    public static class HostItem {
        @SerializedName("name")
        private String name;
        @SerializedName("title")
        private String title;
        @SerializedName("initials")
        private String initials;
        @SerializedName("avatarColor")
        private String avatarColor;

        // Getters
        public String getName() { return name; }
        public String getTitle() { return title; }
        public String getInitials() { return initials; }
        public String getAvatarColor() { return avatarColor; }
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImage() {
        if (image != null && image.startsWith("/uploads")) {
            // Prepend BASE_URL. Since BASE_URL has a trailing slash, we remove the leading slash from 'image'.
            return com.example.bhava.network.ApiClient.BASE_URL + image.substring(1);
        }
        return image;
    }
    public String getCategory() { return category; }
    public String getBadgeText() { return badgeText; }
    public String getJoinedCount() { return joinedCount; }
    public String getDurationText() { return durationText; }
    public String getFullSubtitle() { return fullSubtitle; }
    public String getDetailsLongDescription() { return detailsLongDescription; }
    public boolean isHero() { return isHero; }
    public List<HostItem> getHosts() { return hosts != null ? hosts : new java.util.ArrayList<>(); }
    public List<SessionItem> getSessions() { return sessions != null ? sessions : new java.util.ArrayList<>(); }
}
