package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by suyogcomputech on 20/10/16.
 */

public class PlaceOrderSeller implements Parcelable{
    String sellerEmail, shippingCarge, deliveryMode, addressId;
    List<PlaceOrderItem> deals;

    public PlaceOrderSeller(String sellerEmail, String shippingCarge, String deliveryMode, String addressId) {
        this.sellerEmail = sellerEmail;
        this.shippingCarge = shippingCarge;
        this.deliveryMode = deliveryMode;
        this.deliveryMode = deliveryMode;
        this.addressId = addressId;
    }

    protected PlaceOrderSeller(Parcel in) {
        sellerEmail = in.readString();
        shippingCarge = in.readString();
        deliveryMode = in.readString();
        addressId = in.readString();
        deals = in.createTypedArrayList(PlaceOrderItem.CREATOR);
    }

    public static final Creator<PlaceOrderSeller> CREATOR = new Creator<PlaceOrderSeller>() {
        @Override
        public PlaceOrderSeller createFromParcel(Parcel in) {
            return new PlaceOrderSeller(in);
        }

        @Override
        public PlaceOrderSeller[] newArray(int size) {
            return new PlaceOrderSeller[size];
        }
    };

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public void setDeals(List<PlaceOrderItem> deals) {
        this.deals = deals;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sellerEmail);
        dest.writeString(shippingCarge);
        dest.writeString(deliveryMode);
        dest.writeString(addressId);
        dest.writeTypedList(deals);
    }
}
