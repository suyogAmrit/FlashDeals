package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tanmay on 11/14/2016.
 */

public class Order implements Parcelable{
    private String seller_email;
    private float rating;
    private String seller_name;
    private String seller_address;
    private String seller_order_id;
    private String delevery_mode;
    private double shipping_charge;
    private String delevery_status;
    private String user_delevery_status;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String phone;
    private String email;
    private double seller_total_price;
    private String order_date;
    private String contact_number;
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public DeleiveryInfo getDelevery_info() {
        return delevery_info;
    }

    public void setDelevery_info(DeleiveryInfo delevery_info) {
        this.delevery_info = delevery_info;
    }

    private DeleiveryInfo delevery_info;



    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public double getSeller_total_price() {
        return seller_total_price;
    }

    public void setSeller_total_price(double seller_total_price) {
        this.seller_total_price = seller_total_price;
    }

    public Order(){
        super();
    }
    private Order(Parcel in) {
        seller_email = in.readString();
        rating = in.readFloat();
        seller_name = in.readString();
        seller_address = in.readString();
        seller_order_id = in.readString();
        delevery_mode = in.readString();
        shipping_charge = in.readDouble();
        delevery_status = in.readString();
        user_delevery_status = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        zip = in.readString();
        phone = in.readString();
        email = in.readString();
        items = in.createTypedArrayList(Item.CREATOR);
        seller_total_price =in.readDouble();
        order_date = in.readString();
        contact_number = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getSeller_email() {
        return seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_address() {
        return seller_address;
    }

    public void setSeller_address(String seller_address) {
        this.seller_address = seller_address;
    }

    public String getSeller_order_id() {
        return seller_order_id;
    }

    public void setSeller_order_id(String seller_order_id) {
        this.seller_order_id = seller_order_id;
    }

    public String getDelevery_mode() {
        return delevery_mode;
    }

    public void setDelevery_mode(String delevery_mode) {
        this.delevery_mode = delevery_mode;
    }

    public double getShipping_charge() {
        return shipping_charge;
    }

    public void setShipping_charge(double shipping_charge) {
        this.shipping_charge = shipping_charge;
    }

    public String getDelevery_status() {
        return delevery_status;
    }

    public void setDelevery_status(String delevery_status) {
        this.delevery_status = delevery_status;
    }

    public String getUser_delevery_status() {
        return user_delevery_status;
    }

    public void setUser_delevery_status(String user_delevery_status) {
        this.user_delevery_status = user_delevery_status;
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

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    private ArrayList<Item>items;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(seller_email);
        dest.writeFloat(rating);
        dest.writeString(seller_name);
        dest.writeString(seller_address);
        dest.writeString(seller_order_id);
        dest.writeString(delevery_mode);
        dest.writeDouble(shipping_charge);
        dest.writeString(delevery_status);
        dest.writeString(user_delevery_status);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(zip);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeTypedList(items);
        dest.writeDouble(seller_total_price);
        dest.writeString(order_date);
        dest.writeString(contact_number);
    }
}
