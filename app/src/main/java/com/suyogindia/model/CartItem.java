package com.suyogindia.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by suyogcomputech on 19/10/16.
 */

public class CartItem implements Parcelable {
    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };
    String delaId, desc, mrp, offerPrice, qty, discount, totalPrice, maxqty, sellerEmail;
    int type;
    Seller seller;
    String sellerName;
    String category;

    String totalQuantity, grandTotal;

    public void setQty(String qty) {
        this.qty = qty;
    }

    public CartItem(String delaId, String desc, String mrp, String offerPrice, String qty, String discount, String totalPrice, String maxqty, String sellerEmail) {
        this.delaId = delaId;
        this.desc = desc;
        this.mrp = mrp;
        this.offerPrice = offerPrice;
        this.qty = qty;
        this.discount = discount;
        this.totalPrice = totalPrice;
        this.maxqty = maxqty;
        this.sellerEmail = sellerEmail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    protected CartItem(Parcel in) {
        delaId = in.readString();
        desc = in.readString();
        mrp = in.readString();
        offerPrice = in.readString();
        qty = in.readString();
        discount = in.readString();
        totalPrice = in.readString();
        type = in.readInt();
        sellerName = in.readString();
        maxqty = in.readString();
        sellerEmail = in.readString();

    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getMaxqty() {
        return maxqty;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDiscount() {
        return discount;
    }

    public String getDelaId() {
        return delaId;
    }

    public String getDesc() {
        return desc;
    }

    public String getMrp() {
        return mrp;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public String getQty() {
        return qty;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(delaId);
        dest.writeString(desc);
        dest.writeString(mrp);
        dest.writeString(offerPrice);
        dest.writeString(qty);
        dest.writeString(discount);
        dest.writeString(totalPrice);
        dest.writeInt(type);
        dest.writeString(sellerName);
        dest.writeString(maxqty);
        dest.writeString(sellerEmail);
    }
}
