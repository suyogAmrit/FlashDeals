package com.suyogindia.model;

/**
 * Created by suyogcomputech on 26/10/16.
 */

public class ReviewItem {
    String dealId, description, quantity_available, review_message, category_id, delivery_mode,image_url;
    int review_status;
    String   item_price, offer_price;

    public String getDealId() {
        return dealId;
    }

    public String getDescription() {
        return description;
    }

    public String getQuantity_available() {
        return quantity_available;
    }

    public String getReview_message() {
        return review_message;
    }

    public String getCategory_id() {
        return category_id;
    }

    public int getReview_status() {
        return review_status;
    }


    public String getItem_price() {
        return item_price;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public String getDelivery_mode() {
        return delivery_mode;
    }

    public String getImage_url() {
        return image_url;
    }
}
