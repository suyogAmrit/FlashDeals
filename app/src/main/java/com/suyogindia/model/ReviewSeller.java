package com.suyogindia.model;

import java.util.List;

/**
 * Created by suyogcomputech on 26/10/16.
 */

public class ReviewSeller {
    String  sellerwise_total_price,seller_name, seller_email,category;
    List<ReviewItem> items;

    public String getSellerwise_total_price() {
        return sellerwise_total_price;
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
