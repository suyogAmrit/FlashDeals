package com.suyogindia.model;

/**
 * Created by suyogcomputech on 08/11/16.
 */

public class CreateOrderItem {
    String dealId, quantity, delivery_mode;

    public CreateOrderItem(String dealId, String quantity, String delivery_mode) {
        this.dealId = dealId;
        this.quantity = quantity;
        this.delivery_mode = delivery_mode;
    }
}
