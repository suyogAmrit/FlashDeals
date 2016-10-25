package com.suyogindia.model;

/**
 * Created by Tanmay on 10/25/2016.
 */

public class OrderItem {
    int type;


    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    private String seller_name;
    private String seller_delevery_mode;
    private String seller_shipping_charge;
    private String seller_address;
    private String seller_city;
    private String seller_state;
    private String seller_country;
    private String seller_zip;
    private String seller_phone;
    private String email;

    ItemOrders orders;

    public String getSeller_order_id() {
        return seller_order_id;
    }

    public void setSeller_order_id(String seller_order_id) {
        this.seller_order_id = seller_order_id;
    }

    private String seller_order_id;

    public String getDelevery_status() {
        return delevery_status;
    }

    public void setDelevery_status(String delevery_status) {
        this.delevery_status = delevery_status;
    }

    private String delevery_status;
    public OrderItem(int type, String seller_name, String seller_delevery_mode, String seller_shipping_charge, String seller_address, String seller_city, String seller_state, String seller_country, String seller_zip, String seller_phone, String email,String delevery_status) {
        this.type = type;
        this.seller_name = seller_name;
        this.seller_delevery_mode = seller_delevery_mode;
        this.seller_shipping_charge = seller_shipping_charge;
        this.seller_address = seller_address;
        this.seller_city = seller_city;
        this.seller_state = seller_state;
        this.seller_country = seller_country;
        this.seller_zip = seller_zip;
        this.seller_phone = seller_phone;
        this.email = email;
        this.delevery_status = delevery_status;
    }

    public OrderItem(int type, String seller_name, String seller_order_id) {
        this.type = type;
        this.seller_name = seller_name;
        this.seller_order_id = seller_order_id;
    }

    public ItemOrders getOrders() {
        return orders;
    }

    public void setOrders(ItemOrders orders) {
        this.orders = orders;
    }

    public int getType() {
        return type;
    }
}
