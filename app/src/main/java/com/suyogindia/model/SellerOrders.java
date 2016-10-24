package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/21/2016.
 */

public class SellerOrders implements Parcelable{
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
    private ArrayList<ItemOrders>items;

    public String getSeller_email() {
        return seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
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
    protected SellerOrders(Parcel in) {
        seller_email = in.readString();
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
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(seller_email);
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
    }
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
}
