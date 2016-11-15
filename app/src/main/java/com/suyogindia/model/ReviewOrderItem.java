package com.suyogindia.model;

/**
 * Created by suyogcomputech on 26/10/16.
 */

public class ReviewOrderItem {
    int type;
    String seller_name, seller_email, seller_category;

    String dealId, description, quantity_available, review_message, category_id, item_price, offer_price, deliver_mode,image_url;
    int review_status;
    String seller_item_price, seller_shipping_charge, seller_total_price;
    String footerItem;

    public ReviewOrderItem(int type, String seller_name, String seller_email, String seller_category) {
        this.type = type;
        if (type == 1) {
            this.seller_name = seller_name;
            this.seller_email = seller_email;
            this.seller_category = seller_category;
        } else if (type == 3) {
            this.seller_item_price = seller_name;
            this.seller_shipping_charge = seller_email;
            this.seller_total_price = seller_category;
        }
    }

    public ReviewOrderItem(int type, String dealId, String description, String quantity_available,
                           String review_message, String category_id,
                           String item_price, String offer_price, String deliver_mode, String image_url, int review_status) {
        this.type = type;
        this.dealId = dealId;
        this.description = description;
        this.quantity_available = quantity_available;
        this.review_message = review_message;
        this.category_id = category_id;
        this.item_price = item_price;
        this.offer_price = offer_price;
        this.deliver_mode = deliver_mode;
        this.review_status = review_status;
        this.image_url = image_url;
    }

    public ReviewOrderItem(int type, String footerItem) {
        this.type = type;
        this.footerItem = footerItem;
    }

    public String getImage_url() {
        return image_url;
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

    public String getSeller_item_price() {
        return seller_item_price;
    }

    public String getSeller_shipping_charge() {
        return seller_shipping_charge;
    }

    public String getSeller_total_price() {
        return seller_total_price;
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
