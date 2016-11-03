package com.suyogindia.model;

/**
 * Created by suyogcomputech on 02/11/16.
 */

public class HashRequest {
    String key, txnid, amount, productInfo, firstName, email, udf1, udf2, udf3, udf4, udf5;

    public HashRequest(String key, String txnid, String amount, String productInfo, String firstName, String email, String udf1, String udf2, String udf3, String udf4, String udf5) {
        this.key = key;
        this.txnid = txnid;
        this.amount = amount;
        this.productInfo = productInfo;
        this.firstName = firstName;
        this.email = email;
        this.udf1 = udf1;
        this.udf2 = udf2;
        this.udf3 = udf3;
        this.udf4 = udf4;
        this.udf5 = udf5;
    }
}
