package com.suyogindia.model;

import com.suyogindia.model.PlaceOrderSeller;

import java.util.List;

/**
 * Created by suyogcomputech on 26/10/16.
 */

public class ReviewOrderData {
    String userId;
    List<PlaceOrderSeller> sellerList;

    public ReviewOrderData(String userId, List<PlaceOrderSeller> sellerList) {
        this.userId = userId;
        this.sellerList = sellerList;
    }
}
