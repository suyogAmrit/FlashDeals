package com.suyogindia.flashdeals;

import com.suyogindia.model.ReviewSeller;

import java.util.List;

/**
 * Created by suyogcomputech on 26/10/16.
 */

public class OrderReviewResponse {
    String address, grand_total, status, message;
    List<ReviewSeller> sellerList;

    public String getAddress() {
        return address;
    }

    public String getGrand_total() {
        return grand_total;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<ReviewSeller> getSellerList() {
        return sellerList;
    }
}
