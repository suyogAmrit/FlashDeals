package com.suyogindia.model;

/**
 * Created by suyogcomputech on 26/10/16.
 */

public class ReviewOrderItem {
    int type;
    String seller_name, seller_email, seller_category;

    String dealId, description, quantity_available, review_message, category_id, shipping_price, total_item_price, item_price, offer_price, deliver_mode;
    int review_status;

    String footerItem;

    public ReviewOrderItem(int type, String seller_name, String seller_email, String seller_category) {
        this.type = type;
        this.seller_name = seller_name;
        this.seller_email = seller_email;
        this.seller_category = seller_category;
    }

    public ReviewOrderItem(int type, String dealId, String description, String quantity_available,
                           String review_message, String category_id, String shipping_price, String total_item_price,
                           String item_price, String offer_price,String deliver_mode, int review_status) {
        this.type = type;
        this.dealId = dealId;
        this.description = description;
        this.quantity_available = quantity_available;
        this.review_message = review_message;
        this.category_id = category_id;
        this.shipping_price = shipping_price;
        this.total_item_price = total_item_price;
        this.item_price = item_price;
        this.offer_price = offer_price;
        this.deliver_mode = deliver_mode;
        this.review_status = review_status;
    }

    public ReviewOrderItem(int type, String footerItem) {
        this.type = type;
        this.footerItem = footerItem;
    }

    public String getSeller_category() {
        return seller_category;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public int getType() {
        return type;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public String getSeller_email() {
        return seller_email;
    }

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

    public String getShipping_price() {
        return shipping_price;
    }

    public String getTotal_item_price() {
        return total_item_price;
    }

    public String getItem_price() {
        return item_price;
    }

    public int getReview_status() {
        return review_status;
    }

    public String getFooterItem() {
        return footerItem;
    }

    public String getDeliver_mode() {
        return deliver_mode;
    }
}
