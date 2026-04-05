package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class PreorderRequest {
    @SerializedName("name")   public String name;
    @SerializedName("email")  public String email;
    @SerializedName("mobile") public String mobile;
    @SerializedName("notes")  public String notes;

    public PreorderRequest(String name, String email, String mobile, String notes) {
        this.name   = name;
        this.email  = email;
        this.mobile = mobile;
        this.notes  = notes;
    }
}
