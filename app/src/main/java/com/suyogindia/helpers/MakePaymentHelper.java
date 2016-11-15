package com.suyogindia.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.payUMoney.sdk.SdkSession;
import com.suyogindia.flashdeals.OrderReviewActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by suyogcomputech on 01/11/16.
 */

public class MakePaymentHelper {

    private static final String TAG = MakePaymentHelper.class.getName();
    private static final String MARCHANTID = "5640384";
    Context myContext;
    String email;
    ProgressDialog dialog;
    HashMap<String, String> params = new HashMap<>();
    private String KEY = "DISVeLKQ";
    private String TXNID;
    private String PRODUCTINFO = "Flash_DEAL";
    private String FIRSTNAME = "suyog";
    private String udf1 = "";
    private String udf2 = "";
    private String udf3 = "";
    private String udf4 = "";
    private String udf5 = "";
    private String PHONENUMBER = "8093324558";

    public MakePaymentHelper(Context myContext) {
        this.myContext = myContext;

        email = myContext.getSharedPreferences(AppConstants.USERPREFS, Context.MODE_PRIVATE).getString(AppConstants.EMAIL, AppConstants.NA);
        TXNID = "0nf7" + System.currentTimeMillis();
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    public void initiatePayment(double amount) {
        if (SdkSession.getInstance(myContext) == null) {
            SdkSession.startPaymentProcess((OrderReviewActivity) myContext, params);
        } else {
            SdkSession.createNewInstance(myContext);
        }

        String hashSequence = KEY + "|" + TXNID + "|" + String.valueOf(amount) + "|" + PRODUCTINFO + "|" + FIRSTNAME + "|"
                + email + "|" + "" + "|" + "" + "|" + "" + "|" + "" + "|" + "" + "|" + "omcS1V6wUR";
        params.put("key", KEY);
        params.put("MerchantId", MARCHANTID);
        String hash = hashCal(hashSequence);
        Log.i("hash", hash);
        params.put("TxnId", TXNID);
        params.put("SURL", "https://mobiletest.payumoney.com/mobileapp/payumoney/success.php");
        params.put("FURL", "https://mobiletest.payumoney.com/mobileapp/payumoney/failure.php");
        params.put("ProductInfo", PRODUCTINFO);
        params.put("firstName", FIRSTNAME);
        params.put("Email", email);
        params.put("Phone", PHONENUMBER);
        params.put("Amount", String.valueOf(amount));
        params.put("hash", hash);
        params.put("udf1", "");
        params.put("udf2", "");
        params.put("udf3", "");
        params.put("udf4", "");
        params.put("udf5", "");
        SdkSession.startPaymentProcess((OrderReviewActivity) myContext, params);
        //System.out.print(s);
        Log.i("mess1", params + "");
    }
}


