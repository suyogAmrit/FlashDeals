package com.suyogindia.model;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/21/2016.
 */

public class OrderResponse {
    private ArrayList<Orders> order;
    private String status;
    private String message;

    public ArrayList<Orders> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<Orders> order) {
        this.order = order;
    }

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
