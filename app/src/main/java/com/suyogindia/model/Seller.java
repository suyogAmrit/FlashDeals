package com.suyogindia.model;

/**
 * Created by suyogcomputech on 19/10/16.
 */

public class Seller {
    String email, name, maxPrice, shippingCharge, totalPrice;
    int deleveryMode;
    int shippingAdded;

    public Seller(String email, String name, String maxPrice, String shippingCharge) {
        this.email = email;
        this.name = name;
        this.maxPrice = maxPrice;
        this.shippingCharge = shippingCharge;
        deleveryMode = 0;
        this.shippingAdded = 0;
    }

    public int getShippingAdded() {
        return shippingAdded;
    }

    public void setShippingAdded(int shippingAdded) {
        this.shippingAdded = shippingAdded;
    }

    public int getDeleveryMode() {
        return deleveryMode;
    }

    public void setDeleveryMode(int deleveryMode) {
        this.deleveryMode = deleveryMode;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public String getShippingCharge() {
        return shippingCharge;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
