package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tanmay on 10/21/2016.
 */

public class ItemOrders implements Parcelable{
    private String description;
    private String mrp;
    private String offer_price;
    private String discount;
    private String offer_start_time;
    private String offer_end_time;

    public ItemOrders() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(String offer_price) {
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
    private ItemOrders(Parcel in){
        description  =in.readString();
        mrp = in.readString();
        offer_price = in.readString();
        discount = in.readString();
        offer_start_time = in.readString();
        offer_end_time = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(mrp);
        dest.writeString(offer_price);
        dest.writeString(discount);
        dest.writeString(offer_start_time);
        dest.writeString(offer_end_time);
    }
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ItemOrders> CREATOR = new Parcelable.Creator<ItemOrders>() {
        @Override
        public ItemOrders createFromParcel(Parcel in) {
            return new ItemOrders(in);
        }

        @Override
        public ItemOrders[] newArray(int size) {
            return new ItemOrders[size];
        }
    };
}
