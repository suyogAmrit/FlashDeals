package com.suyogindia.model;

import java.util.ArrayList;

/**
 * Created by Tanmay on 11/14/2016.
 */

public class MyOrderResponse {
    private String status;
    private String message;
    private ArrayList<Order>order;

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

    public ArrayList<Order> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<Order> order) {
        this.order = order;
    }
}
