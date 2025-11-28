package com.example.doan.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private int price;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("category")
    private Category category;

    @SerializedName("createdAt")
    private Date createdAt;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getQuantity() {
        return quantity;
    }

    public Category getCategory() {
        return category;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
