package com.suyogindia.helpers;

import android.content.Context;
import android.util.Log;

import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.suyogindia.flashdeals.OrderReviewActivity;
import com.suyogindia.model.HashRequest;
import com.suyogindia.model.HashResponse;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suyogcomputech on 01/11/16.
 */

public class MakePaymentHelper {

    private static final String TAG = MakePaymentHelper.class.getName();
    private static final String MARCHANTID = "5640384";
    Context myContext;
    String email;
    private String KEY = "DISVeLKQ";
    private String TXNID;
    private String PRODUCTINFO = "Flash_DEAL";
    private String FIRSTNAME = "suyog";
    private String SALT = "omcS1V6wUR";
    private String udf1 = "";
    private String udf2 = "";
    private String udf3 = "";
    private String udf4 = "";
    private String udf5 = "";
    private String PHONENUMBER = "8093324558";

    public MakePaymentHelper(Context myContext) {
        this.myContext = myContext;
        email = "sasikant.singh@rediffmail.com";
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
        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder();
        builder.setMerchantId(MARCHANTID)
                .setIsDebug(false)
                .setKey(KEY)
                .setAmount(amount)
                .setTnxId(TXNID)
                .setPhone(PHONENUMBER)
                .setProductName(PRODUCTINFO)
                .setEmail(email)
                .setFirstName(FIRSTNAME)
                .setsUrl("https://www.PayUmoney.com/mobileapp/PayUmoney/success.php")
                .setfUrl("https://www.PayUmoney.com/mobileapp/PayUmoney/failure.php")
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5);
        final PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();
        String hashSequence = KEY + "|" + TXNID + "|" + amount + "|" + PRODUCTINFO + "|" + FIRSTNAME + "|" + email + "|" + udf1 + "|" + udf2 + "|" +
                udf3 + "|" + udf4 + "|" + udf5 + "|" + SALT;
        Log.i("hassequence", hashSequence);
        String serverCalculatedHash = hashCal(hashSequence);
        Log.i("hash", serverCalculatedHash);
//        paymentParam.setMerchantHash(serverCalculatedHash);
//        PayUmoneySdkInitilizer.startPaymentActivityForResult((OrderReviewActivity) myContext, paymentParam);
        WebApi api = AppHelpers.setupRetrofit();
        HashRequest request = new HashRequest(KEY, TXNID, amount, PRODUCTINFO, FIRSTNAME, email, udf1, udf2, udf3, udf4, udf5);
        Call<HashResponse> responseCall = api.getHash(request);
        responseCall.enqueue(new Callback<HashResponse>() {
            @Override
            public void onResponse(Call<HashResponse> call, Response<HashResponse> response) {
                Log.i(AppConstants.STATUS, response.body().getResult());
                String hash = response.body().getResult();
                paymentParam.setMerchantHash(hash);
                PayUmoneySdkInitilizer.startPaymentActivityForResult((OrderReviewActivity) myContext, paymentParam);

            }

            @Override
            public void onFailure(Call<HashResponse> call, Throwable t) {

            }
        });
    }

}
