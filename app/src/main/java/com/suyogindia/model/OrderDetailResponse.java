package com.suyogindia.model;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/21/2016.
 */

public class OrderDetailResponse {
    private String status;
    private String message;
    private ArrayList<SellerOrders>seller;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<SellerOrders> getSeller() {
        return seller;
    }

    public void setSeller(ArrayList<SellerOrders> seller) {
        this.seller = seller;
    }
}
