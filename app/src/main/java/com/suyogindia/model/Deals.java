package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by suyogcomputech on 18/10/16.
 */

public class Deals implements Parcelable {

    public static final Creator<Deals> CREATOR = new Creator<Deals>() {
        @Override
        public Deals createFromParcel(Parcel in) {
            return new Deals(in);
        }

        @Override
        public Deals[] newArray(int size) {
            return new Deals[size];
        }
    };
    String deal_id, seller_email, desciption, mrp, offer_price, discount, quantity, max_price, max_distance, shipping_charge, seller_name;

    protected Deals(Parcel in) {
        deal_id = in.readString();
        seller_email = in.readString();
        desciption = in.readString();
        mrp = in.readString();
        offer_price = in.readString();
        discount = in.readString();
        quantity = in.readString();
        max_price = in.readString();
        max_distance = in.readString();
        shipping_charge = in.readString();
        seller_name = in.readString();
    }

    public String getSeller_name() {
        return seller_name;
    }

    public String getDeal_id() {
        return deal_id;
    }

    public String getSeller_email() {
        return seller_email;
    }

    public String getDesciption() {
        return desciption;
    }

    public String getMrp() {
        return mrp;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public String getDiscount() {
        return discount;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getMax_price() {
        return max_price;
    }

    public String getMax_distance() {
        return max_distance;
    }

    public String getShipping_charge() {
        return shipping_charge;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deal_id);
        dest.writeString(seller_email);
        dest.writeString(desciption);
        dest.writeString(mrp);
        dest.writeString(offer_price);
        dest.writeString(discount);
        dest.writeString(quantity);
        dest.writeString(max_price);
        dest.writeString(max_distance);
        dest.writeString(shipping_charge);
        dest.writeString(seller_name);
    }
}
