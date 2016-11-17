package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tanmay on 11/14/2016.
 */

public class Item implements Parcelable {
    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
    private String description;
    private double mrp;
    private double offer_price;
    private String discount;
    private String offer_start_time;
    private String offer_end_time;
    private double total_price;
    private int quantity;
    private String order_date;
    private String image_url;
    private String new_order_id;

    public Item() {
        super();
    }

    private Item(Parcel in) {
        description = in.readString();
        mrp = in.readDouble();
        offer_price = in.readDouble();
        discount = in.readString();
        offer_start_time = in.readString();
        offer_end_time = in.readString();
        total_price = in.readDouble();
        quantity = in.readInt();
        order_date = in.readString();
        image_url = in.readString();
        new_order_id = in.readString();

    }

    public String getNew_order_id() {
        return new_order_id;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(double offer_price) {
        this.offer_price = offer_price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOffer_start_time() {
        return offer_start_time;
    }

    public void setOffer_start_time(String offer_start_time) {
        this.offer_start_time = offer_start_time;
    }

    public String getOffer_end_time() {
        return offer_end_time;
    }

    public void setOffer_end_time(String offer_end_time) {
        this.offer_end_time = offer_end_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeDouble(mrp);
        dest.writeDouble(offer_price);
        dest.writeString(discount);
        dest.writeString(offer_start_time);
        dest.writeString(offer_end_time);
        dest.writeDouble(total_price);
        dest.writeInt(quantity);
        dest.writeString(order_date);
        dest.writeString(image_url);
        dest.writeString(new_order_id);
    }

    public String getImage_url() {
        return image_url;
    }
}
