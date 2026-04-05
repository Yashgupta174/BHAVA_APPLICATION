package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class PlaceOrderRequest {
    @SerializedName("address") public AddressModel address;
    @SerializedName("notes")   public String notes;

    public static class AddressModel {
        @SerializedName("line1")   public String line1;
        @SerializedName("city")    public String city;
        @SerializedName("state")   public String state;
        @SerializedName("pincode") public String pincode;

        public AddressModel(String line1, String city, String state, String pincode) {
            this.line1   = line1;
            this.city    = city;
            this.state   = state;
            this.pincode = pincode;
        }
    }

    public PlaceOrderRequest(AddressModel address, String notes) {
        this.address = address;
        this.notes   = notes;
    }
}
