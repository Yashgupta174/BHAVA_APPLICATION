package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class AddToCartRequest {
    @SerializedName("productId") public int productId;
    @SerializedName("title")     public String title;
    @SerializedName("price")     public String price;
    @SerializedName("image")     public String image;
    @SerializedName("category")  public String category;
    @SerializedName("quantity")  public int quantity;

    public AddToCartRequest(int productId, String title, String price,
                            String image, String category, int quantity) {
        this.productId = productId;
        this.title     = title;
        this.price     = price;
        this.image     = image;
        this.category  = category;
        this.quantity  = quantity;
    }
}
