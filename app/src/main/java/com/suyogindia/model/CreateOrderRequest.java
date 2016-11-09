package com.suyogindia.model;

import java.util.List;

/**
 * Created by suyogcomputech on 08/11/16.
 */

public class CreateOrderRequest {
    String userId, addressId;
    List<PlaceOrderSeller> sellerList;

    public CreateOrderRequest(String userId, String addressId) {
        this.userId = userId;
        this.addressId = addressId;
    }

    public void setSellerList(List<PlaceOrderSeller> sellerList) {
        this.sellerList = sellerList;
    }

}
