package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by suyogcomputech on 18/10/16.
 */

public class GetDealsResponse {
    private String status, message;
    private List<Category> categoryList;

    public String getMessage() {
        return message;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public String getStatus() {
        return status;
    }



}
