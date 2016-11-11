package com.suyogindia.model;

import java.util.List;

/**
 * Created by suyogcomputech on 26/10/16.
 */

public class ReviewSeller {
    String  seller_name, seller_email,category,seller_item_price,seller_shipping_charge,seller_total_price;
    List<ReviewItem> items;

    public String getSeller_item_price() {
        return seller_item_price;
    }

    public String getSeller_shipping_charge() {
        return seller_shipping_charge;
    }

    public String getSeller_total_price() {
        return seller_total_price;
    }


    public String getSeller_name() {
        return seller_name;
    }

    public String getSeller_email() {
        return seller_email;
    }

    public List<ReviewItem> getItems() {
        return items;
    }

    public String getCategory() {
        return category;
    }
}
