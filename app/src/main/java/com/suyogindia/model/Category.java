package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by suyogcomputech on 18/10/16.
 */

public class Category implements Parcelable {

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    String name, id;
    List<Deals> dealsList;

    protected Category(Parcel in) {
        name = in.readString();
        id = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Deals> getDealsList() {
        return dealsList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeTypedList(dealsList);
    }
}
