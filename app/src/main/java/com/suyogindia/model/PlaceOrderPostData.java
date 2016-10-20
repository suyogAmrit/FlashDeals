package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by suyogcomputech on 20/10/16.
 */

public class PlaceOrderPostData implements Parcelable{
    String userId;
    List<PlaceOrderSeller> sellerList;
    int modeOfPayment;

    public PlaceOrderPostData(String userId, List<PlaceOrderSeller> sellerList, int modeOfPayment) {
        this.userId = userId;
        this.sellerList = sellerList;
        this.modeOfPayment = modeOfPayment;
    }

    protected PlaceOrderPostData(Parcel in) {
        userId = in.readString();
        modeOfPayment = in.readInt();
    }

    public static final Creator<PlaceOrderPostData> CREATOR = new Creator<PlaceOrderPostData>() {
        @Override
        public PlaceOrderPostData createFromParcel(Parcel in) {
            return new PlaceOrderPostData(in);
        }

        @Override
        public PlaceOrderPostData[] newArray(int size) {
            return new PlaceOrderPostData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeInt(modeOfPayment);
    }
}
