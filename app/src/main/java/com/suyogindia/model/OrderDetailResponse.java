package com.suyogindia.model;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/21/2016.
 */

public class OrderDetailResponse {
    private String status;
    private String message;

    public ArrayList<SellerOrders> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<SellerOrders> order) {
        this.order = order;
    }

    private ArrayList<SellerOrders>order;

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

}
