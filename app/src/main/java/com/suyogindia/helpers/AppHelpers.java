package com.suyogindia.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.suyogindia.database.DataBaseHelper;
import com.suyogindia.flashdeals.ClearCartService;
import com.suyogindia.flashdeals.OrderReviewResponse;
import com.suyogindia.model.CartItem;
import com.suyogindia.model.CreateOrderRequest;
import com.suyogindia.model.CreateOrderResponse;
import com.suyogindia.model.Deals;
import com.suyogindia.model.PlaceOrderItem;
import com.suyogindia.model.PlaceOrderPostData;
import com.suyogindia.model.PlaceOrderResponse;
import com.suyogindia.model.PlaceOrderSeller;
import com.suyogindia.model.ReviewOrderData;
import com.suyogindia.model.Seller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by suyogcomputech on 10/10/16.
 */

public class AppHelpers {
    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivity.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivity != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network",
                                    "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static WebApi setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WebApi.class);
    }

    public static ProgressDialog showProgressDialog(Context context, String genotpmessage) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(AppConstants.DALOGTITLE);
        dialog.setMessage(genotpmessage);
        return dialog;
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static long addToCart(Context myContext, String qty, String totalPrice, Deals myDeals) {
        DataBaseHelper helper = new DataBaseHelper(myContext);
        helper.open();
        long row = helper.addDeal(qty, totalPrice, myDeals);
        helper.close();
        setAlarmToClearCart(myContext);
        return row;
    }

    private static void setAlarmToClearCart(Context myContext) {
        SharedPreferences shr = myContext.getSharedPreferences(AppConstants.USERPREFS, Context.MODE_PRIVATE);
        long time = shr.getLong(AppConstants.CARTCLEARTIME, System.currentTimeMillis());
        Intent i = new Intent(myContext, ClearCartService.class);
        AlarmManager manager = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(myContext, 100, i, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, time + (2 * 60 * 60 * 1000), pi);
    }

    public static long updateCart(Context context, String qty, String totalPrice, String dealId) {
        DataBaseHelper helper = new DataBaseHelper(context);
        helper.open();
        long row = helper.updateDeal(qty, totalPrice, dealId);
        helper.close();
        return row;
    }

    public static Call<PlaceOrderResponse> placeOrder(Context context, List<CartItem> list, ArrayList<PlaceOrderSeller> sellerList, String addressId, int modeOfPayment) {
        SharedPreferences shr = context.getSharedPreferences(AppConstants.USERPREFS, Context.MODE_PRIVATE);
        String userId = shr.getString(AppConstants.USERID, AppConstants.NA);

        for (PlaceOrderSeller seller : sellerList) {
            String email = seller.getSellerEmail();
            List<PlaceOrderItem> itemList = new ArrayList<>();
            for (CartItem deal :
                    list) {
                if (deal.getType() == 1 && deal.getSellerEmail().equals(email)) {
                    PlaceOrderItem item = new PlaceOrderItem(deal.getDelaId(), deal.getQty());
                    itemList.add(item);
                }
            }
            seller.setDeals(itemList);
            seller.setAddressId(addressId);
        }
        PlaceOrderPostData data = new PlaceOrderPostData(userId, sellerList, modeOfPayment);
        WebApi api = setupRetrofit();
        return api.senOrders(data);
    }

    public static ArrayList<PlaceOrderSeller> getSellerList(ArrayList<CartItem> list) {
        ArrayList<PlaceOrderSeller> sellerList = new ArrayList<>();
        for (CartItem cartItem :
                list) {
            if (cartItem.getType() == 2) {
                Seller mySeller = cartItem.getSeller();
                PlaceOrderSeller seller = new PlaceOrderSeller(mySeller.getEmail(), mySeller.getShippingCharge(),
                        String.valueOf(mySeller.getDeleveryMode()));
                sellerList.add(seller);
            }
        }
        return sellerList;
    }

    public static long clearCart(Context context) {
        DataBaseHelper helper = new DataBaseHelper(context);
        helper.open();
        return helper.clearCart();
    }

    public static Call<OrderReviewResponse> postForOrderReview(Context context, ArrayList<CartItem> list, ArrayList<PlaceOrderSeller> sellerList, String addressId) {
        SharedPreferences shr = context.getSharedPreferences(AppConstants.USERPREFS, Context.MODE_PRIVATE);
        String userId = shr.getString(AppConstants.USERID, AppConstants.NA);
        ArrayList<Integer> listRemovePos = new ArrayList<>();
        for (PlaceOrderSeller seller : sellerList) {
            String email = seller.getSellerEmail();
            boolean found = false;
            List<PlaceOrderItem> itemList = new ArrayList<>();
            for (CartItem deal :
                    list) {
                if (deal.getType() == 1 && deal.getSellerEmail().equals(email)) {
                    PlaceOrderItem item = new PlaceOrderItem(deal.getDelaId(), deal.getQty());
                    itemList.add(item);
                    found = true;
                }

            }
            if (!found) {
                listRemovePos.add(sellerList.indexOf(seller));
            } else {
                seller.setDeals(itemList);
                seller.setAddressId(addressId);
            }
        }
        for (int i : listRemovePos) {
            sellerList.remove(i);
        }
        ReviewOrderData data = new ReviewOrderData(userId, sellerList);
        WebApi api = setupRetrofit();
        return api.reviewOrders(data);
    }

    public static long deleteFromCart(Context mContext, String dealId) {
        DataBaseHelper helper = new DataBaseHelper(mContext);
        helper.open();
        long row = helper.deleteFromCart(dealId);
        helper.close();
        return row;
    }

    public static Call<CreateOrderResponse> createOrder(Context context, ArrayList<CartItem> list, ArrayList<PlaceOrderSeller> sellerList, String addressId) {
        SharedPreferences shr = context.getSharedPreferences(AppConstants.USERPREFS, Context.MODE_PRIVATE);
        String userId = shr.getString(AppConstants.USERID, AppConstants.NA);
        ArrayList<Integer> listRemovePos = new ArrayList<>();
        for (PlaceOrderSeller seller : sellerList) {
            String email = seller.getSellerEmail();
            boolean found = false;
            List<PlaceOrderItem> itemList = new ArrayList<>();
            for (CartItem deal :
                    list) {
                if (deal.getType() == 1 && deal.getSellerEmail().equals(email)) {
                    PlaceOrderItem item = new PlaceOrderItem(deal.getDelaId(), deal.getQty());
                    itemList.add(item);
                    found = true;
                }

            }
            if (!found) {
                listRemovePos.add(sellerList.indexOf(seller));
            } else {
                seller.setDeals(itemList);
                seller.setAddressId(addressId);
            }
        }
        for (int i : listRemovePos) {
            sellerList.remove(i);
        }
        CreateOrderRequest data = new CreateOrderRequest(userId, addressId);
        data.setSellerList(sellerList);
        WebApi api = setupRetrofit();
        return api.createOrder(data);

    }
}
