package com.example.doan.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Drink {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("basePrice")
    private double basePrice;
    @SerializedName("isActive")
    private boolean isActive;
    @SerializedName("sizes")
    private List<Size> sizes;
    @SerializedName("toppings")
    private List<Topping> toppings;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public double getBasePrice() { return basePrice; }
    public boolean isActive() { return isActive; }
    public List<Size> getSizes() { return sizes; }
    public List<Topping> getToppings() { return toppings; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public void setActive(boolean active) { isActive = active; }
    public void setSizes(List<Size> sizes) { this.sizes = sizes; }
    public void setToppings(List<Topping> toppings) { this.toppings = toppings; }
}
