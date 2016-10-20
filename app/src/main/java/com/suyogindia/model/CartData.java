package com.suyogindia.model;

import java.util.List;

/**
 * Created by suyogcomputech on 19/10/16.
 */

public class CartData {
    List<CartItem> cartItemList;
    Seller mySeller;

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public Seller getMySeller() {
        return mySeller;
    }

    public void setMySeller(Seller mySeller) {
        this.mySeller = mySeller;
    }
}
