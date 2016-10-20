package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by suyogcomputech on 20/10/16.
 */

public class PlaceOrderItem implements Parcelable{
    String dealId, quantity;

    public PlaceOrderItem(String dealId, String quantity) {
        this.dealId = dealId;
        this.quantity = quantity;
    }

    protected PlaceOrderItem(Parcel in) {
        dealId = in.readString();
        quantity = in.readString();
    }

    public static final Creator<PlaceOrderItem> CREATOR = new Creator<PlaceOrderItem>() {
        @Override
        public PlaceOrderItem createFromParcel(Parcel in) {
            return new PlaceOrderItem(in);
        }

        @Override
        public PlaceOrderItem[] newArray(int size) {
            return new PlaceOrderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dealId);
        dest.writeString(quantity);
    }
}
