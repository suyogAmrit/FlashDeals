package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/21/2016.
 */

public class SellerOrders implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SellerOrders> CREATOR = new Parcelable.Creator<SellerOrders>() {
        @Override
        public SellerOrders createFromParcel(Parcel in) {
            return new SellerOrders(in);
        }

        @Override
        public SellerOrders[] newArray(int size) {
            return new SellerOrders[size];
        }
    };
    private String seller_name;
    private String seller_email;
    private String delevery_mode;
    private String shipping_charge;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String phone;
    private String email;
    private ArrayList<ItemOrders> items;
    private String seller_order_id;
    private String delevery_status;
    private String seller_address;
    private String rating;
    private String user_delevery_status;
    private int TYPE;

    public SellerOrders() {
        super();
    }

    protected SellerOrders(Parcel in) {
        seller_name = in.readString();
        delevery_mode = in.readString();
        shipping_charge = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        zip = in.readString();
        phone = in.readString();
        email = in.readString();
        items = in.readArrayList(null);
        TYPE = in.readInt();
        seller_order_id = in.readString();
        delevery_status = in.readString();
        user_delevery_status = in.readString();
        seller_email = in.readString();
        rating = in.readString();
        seller_address = in.readString();
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_email() {
        return seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
    }

    public String getSeller_address() {
        return seller_address;
    }

    public void setSeller_address(String seller_address) {
        this.seller_address = seller_address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUser_delevery_status() {
        return user_delevery_status;
    }

    public void setUser_delevery_status(String user_delevery_status) {
        this.user_delevery_status = user_delevery_status;
    }

    public String getDelevery_status() {
        return delevery_status;
    }

    public void setDelevery_status(String delevery_status) {
        this.delevery_status = delevery_status;
    }

    public String getSeller_order_id() {
        return seller_order_id;
    }

    public void setSeller_order_id(String seller_order_id) {
        this.seller_order_id = seller_order_id;
    }

    public int getTYPE() {
        return TYPE;
    }

    public void setTYPE(int TYPE) {
        this.TYPE = TYPE;
    }

    public String getDelevery_mode() {
        return delevery_mode;
    }

    public void setDelevery_mode(String delevery_mode) {
        this.delevery_mode = delevery_mode;
    }

    public String getShipping_charge() {
        return shipping_charge;
    }

    public void setShipping_charge(String shipping_charge) {
        this.shipping_charge = shipping_charge;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<ItemOrders> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemOrders> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(seller_name);
        dest.writeString(delevery_mode);
        dest.writeString(shipping_charge);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(zip);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeSerializable(items);
        dest.writeInt(TYPE);
        dest.writeString(seller_order_id);
        dest.writeString(delevery_status);
        dest.writeString(user_delevery_status);
        dest.writeString(seller_email);
        dest.writeString(rating);
        dest.writeString(seller_address);
    }
}
