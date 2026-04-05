package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class ContactRequest {
    @SerializedName("name")        public String name;
    @SerializedName("mobile")      public String mobile;
    @SerializedName("email")       public String email;
    @SerializedName("description") public String description;

    public ContactRequest(String name, String mobile, String email, String description) {
        this.name        = name;
        this.mobile      = mobile;
        this.email       = email;
        this.description = description;
    }
}
