package com.suyogindia.helpers;

import android.app.ProgressDialog;
import android.content.Context;

import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.suyogindia.flashdeals.OrderReviewActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by suyogcomputech on 06/01/17.
 */

public class PayYouMoneyHelper {

    private static final String TAG = MakePaymentHelper.class.getName();
    private static final String MARCHANTID = "5640384";
    Context myContext;
    String email;
    ProgressDialog dialog;
    HashMap<String, String> params = new HashMap<>();
    String sUrl = "https://test.payumoney.com/mobileapp/payumoney/success.php";
    String fUrl = "https://test.payumoney.com/mobileapp/payumoney/failure.php";
    boolean isDebug = false;
    private String KEY = "DISVeLKQ";
    private String TXNID;
    private String PRODUCTINFO = "FlASH_DEAL";
    private String FIRSTNAME = "suyog";
    private String udf1 = "";
    private String udf2 = "";
    private String udf3 = "";
    private String udf4 = "";
    private String udf5 = "";
    private String PHONENUMBER = "8093324558";

    public PayYouMoneyHelper(Context myContext) {
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

    public void makePayment(double amount) {
        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder();

        String hashSequence = KEY + "|" + TXNID + "|" + String.valueOf(amount) + "|" + PRODUCTINFO + "|" + FIRSTNAME + "|"
                + email + "|" + "" + "|" + "" + "|" + "" + "|" + "" + "|" + "" + "|" + "omcS1V6wUR";

        builder.setAmount(amount)
                .setTnxId(TXNID)
                .setPhone(PHONENUMBER)
                .setProductName(PRODUCTINFO)
                .setFirstName(FIRSTNAME)
                .setEmail(email)
                .setsUrl(sUrl)
                .setfUrl(fUrl)
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setIsDebug(isDebug)
                .setKey(KEY)
                .setMerchantId(MARCHANTID);

        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();
        paymentParam.setMerchantHash(hashCal(hashSequence));
        PayUmoneySdkInitilizer.startPaymentActivityForResult((OrderReviewActivity) myContext, paymentParam);

    }
}
