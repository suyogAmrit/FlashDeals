package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tanmay on 10/21/2016.
 */

public class Orders implements Parcelable {
    private String order_id;
    private String mode_of_payment;
    private String payment_status;
    private String total_price;
    private String order_status;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getMode_of_payment() {
        return mode_of_payment;
    }

    public void setMode_of_payment(String mode_of_payment) {
        this.mode_of_payment = mode_of_payment;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }



    protected Orders(Parcel in) {
        order_id = in.readString();
        mode_of_payment = in.readString();
        payment_status = in.readString();
        total_price = in.readString();
        order_status = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(order_id);
        dest.writeString(mode_of_payment);
        dest.writeString(payment_status);
        dest.writeString(total_price);
        dest.writeString(order_status);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Orders> CREATOR = new Parcelable.Creator<Orders>() {
        @Override
        public Orders createFromParcel(Parcel in) {
            return new Orders(in);
        }

        @Override
        public Orders[] newArray(int size) {
            return new Orders[size];
        }
    };
}