package com.suyogindia.model;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/20/2016.
 */

public class AddressResponse {
    private ArrayList<Address> address;
    private String status;
    private String message;

    public ArrayList<Address> getAddress() {
        return address;
    }

    public void setAddress(ArrayList<Address> address) {
        this.address = address;
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
