package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tanmay on 10/25/2016.
 */

public class OrderItem implements Parcelable{
    int type;
    private String seller_name;
    private String user_delevery_status;
    private String rating;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

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
    private String seller_order_id;
    private String delevery_status;

    public String getSeller_email() {
        return seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
    }

    private String seller_email;

    protected  OrderItem(Parcel in){
        type = in.readInt();
        seller_name = in.readString();
        user_delevery_status = in.readString();
        seller_order_id = in.readString();
        delevery_status = in.readString();
        seller_email = in.readString();
        rating = in.readString();
    }
    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }



    public String getUser_delevery_status() {
        return user_delevery_status;
    }

    public void setUser_delevery_status(String user_delevery_status) {
        this.user_delevery_status = user_delevery_status;
    }



    public String getSeller_order_id() {
        return seller_order_id;
    }

    public void setSeller_order_id(String seller_order_id) {
        this.seller_order_id = seller_order_id;
    }



    public String getDelevery_status() {
        return delevery_status;
    }

    public void setDelevery_status(String delevery_status) {
        this.delevery_status = delevery_status;
    }


    public OrderItem(int type, String seller_name, String seller_delevery_mode, String seller_shipping_charge, String seller_address, String seller_city, String seller_state, String seller_country, String seller_zip, String seller_phone, String email,String delevery_status,String user_delevery_status,String seller_order_id,String seller_email,String rating) {
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
        this.user_delevery_status = user_delevery_status;
        this.seller_order_id = seller_order_id;
        this.seller_email = seller_email;
        this.rating = rating;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(seller_name);
        dest.writeString(user_delevery_status);
        dest.writeString(seller_order_id);
        dest.writeString(delevery_status);
        dest.writeString(seller_email);
        dest.writeString(rating);
    }
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OrderItem> CREATOR = new Parcelable.Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };
}
