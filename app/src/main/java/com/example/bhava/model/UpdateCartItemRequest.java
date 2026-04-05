package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class UpdateCartItemRequest {
    @SerializedName("quantity") public int quantity;

    public UpdateCartItemRequest(int quantity) {
        this.quantity = quantity;
    }
}
