package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by suyogcomputech on 19/10/16.
 */

public class Seller implements Parcelable{
    String email, name, maxPrice, shippingCharge, totalPrice;
    int deleveryMode;
    int shippingAdded;

    public Seller(String email, String name, String maxPrice, String shippingCharge) {
        this.email = email;
        this.name = name;
        this.maxPrice = maxPrice;
        this.shippingCharge = shippingCharge;
        deleveryMode = 0;
        this.shippingAdded = 0;
    }

    protected Seller(Parcel in) {
        email = in.readString();
        name = in.readString();
        maxPrice = in.readString();
        shippingCharge = in.readString();
        totalPrice = in.readString();
        deleveryMode = in.readInt();
        shippingAdded = in.readInt();
    }

    public static final Creator<Seller> CREATOR = new Creator<Seller>() {
        @Override
        public Seller createFromParcel(Parcel in) {
            return new Seller(in);
        }

        @Override
        public Seller[] newArray(int size) {
            return new Seller[size];
        }
    };

    public int getShippingAdded() {
        return shippingAdded;
    }

    public void setShippingAdded(int shippingAdded) {
        this.shippingAdded = shippingAdded;
    }

    public int getDeleveryMode() {
        return deleveryMode;
    }

    public void setDeleveryMode(int deleveryMode) {
        this.deleveryMode = deleveryMode;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public String getShippingCharge() {
        return shippingCharge;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(maxPrice);
        dest.writeString(shippingCharge);
        dest.writeString(totalPrice);
        dest.writeInt(deleveryMode);
        dest.writeInt(shippingAdded);
    }
}
