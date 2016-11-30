package com.suyogindia.flashdeals;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.suyogindia.adapters.OrdersDetailAdapter;
import com.suyogindia.helpers.AndroidUtils;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.FlasDealApi;
import com.suyogindia.model.Item;
import com.suyogindia.model.ItemOrder;
import com.suyogindia.model.ItemOrders;
import com.suyogindia.model.MyOrderResponse;
import com.suyogindia.model.Order;
import com.suyogindia.model.OrderDetailResponse;
import com.suyogindia.model.OrderItem;
import com.suyogindia.model.Result;
import com.suyogindia.model.SellerOrders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrdersActivity extends AppCompatActivity {
    private static final int REQUEST_CALL = 201;
    private static final int REQIEST_LOCATION = 202;
    public static String responseString, userDemoid;
    private RecyclerView recyclerOrders;
    Retrofit retrofit;
    FlasDealApi flasDealApi;
    Call<MyOrderResponse>orderResponseCall;
    ProgressDialog dialog;
    String userId;
    Call<Result> radioResponseCall, ratingResponsecall;
    private OrdersDetailAdapter adapter;
    private ArrayList<Order> orderseList;
    String phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        recyclerOrders = (RecyclerView)findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(OrdersActivity.this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        userId = shr.getString(AppConstants.USERID, AppConstants.NA);
        userDemoid = userId;
        adapter = new OrdersDetailAdapter(this);
        retrofit = new Retrofit.Builder().baseUrl(AppConstants.BASEURL).addConverterFactory(GsonConverterFactory.create()).build();
        flasDealApi = retrofit.create(FlasDealApi.class);
        orderseList = new ArrayList<>();
        dialog = AppHelpers.showProgressDialog(OrdersActivity.this, "Showing Orders...");
        dialog.show();

        if (AppHelpers.isConnectingToInternet(OrdersActivity.this)) {
            showMyOrders();
        } else {
            Toast.makeText(OrdersActivity.this, AppConstants.PLEASE_CONNCT, Toast.LENGTH_SHORT).show();
        }
    }
    private void showMyOrders() {
        Map<String, String> orderMap = new HashMap<>(1);
        orderMap.put(AppConstants.USERID, userId);
        orderResponseCall = flasDealApi.getMyOrder(orderMap);
        orderResponseCall.enqueue(new Callback<MyOrderResponse>() {
            @Override
            public void onResponse(Call<MyOrderResponse> call, Response<MyOrderResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.v(AppConstants.RESPONSE, response.body().getStatus());
                    if (response.body().getStatus().equals("1")) {
                        orderseList.clear();
                        orderseList.addAll(response.body().getOrder());
                        recyclerOrders.setAdapter(adapter);
                        if (orderseList != null && orderseList.size() > 0)
                            adapter.add(getItemOrdersFrom(orderseList));

                    }
                }
            }

            @Override
            public void onFailure(Call<MyOrderResponse> call, Throwable t) {
                dialog.dismiss();
                Log.v(AppConstants.ERROR, t.getMessage());
            }
        });
    }
    private ArrayList<ItemOrder> getItemOrdersFrom(ArrayList<Order> orderseList) {
        ArrayList<ItemOrder> itemOrdersArrayList = new ArrayList<>();
        for (Order order : orderseList) {
            ItemOrder itemSeller = new ItemOrder(0, order.getSeller_name(), order.getSeller_order_id(), order.getSeller_address(), order.getPhone(),order.getShipping_charge(),order.getSeller_total_price(),order.getOrder_date(),order.getContact_number(),order.getLatitude(),order.getLongitude());
            itemOrdersArrayList.add(itemSeller);
            ArrayList<Item> arrayList = order.getItems();
            for (Item i : arrayList) {
                ItemOrder item = new ItemOrder(1, null, null, 0, null, null, null, null, null, null, null, null, null, null, null, 0);
                item.setItem(i);
                itemOrdersArrayList.add(item);
            }
            ItemOrder itemdelivery = new ItemOrder(2,order.getDelevery_info());
            itemOrdersArrayList.add(itemdelivery);
            ItemOrder orderItem = new ItemOrder(3, order.getSeller_name(), order.getDelevery_mode(), order.getShipping_charge(),
                    order.getAddress(), order.getCity(), order.getState(), order.getCountry(), order.getZip(), order.getPhone(), order.getEmail(), order.getDelevery_status(), order.getUser_delevery_status(), order.getSeller_order_id(), order.getSeller_email(), order.getRating());
            itemOrdersArrayList.add(orderItem);
        }
        return itemOrdersArrayList;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
    public void sendRadioRespond(String seller_order_id, String ids) {
        if (AppHelpers.isConnectingToInternet(OrdersActivity.this)) {

            Map<String, String> map = new HashMap<>(2);
            map.put("seller_order_id", seller_order_id);
            map.put("user_delevery_status", ids);
            radioResponseCall = flasDealApi.sendInfoOFRadio(map);
            radioResponseCall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful()) {
                        Log.v(AppConstants.RESPONSE, response.body().getMessage());
                        responseString = "" + response.body().getStatus();
                        Log.v("", "");
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.v(AppConstants.ERROR, t.getMessage());
                }
            });
        } else {
            Toast.makeText(OrdersActivity.this, "Please Connect to internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void rateSeller(String seller_email, String userDemoid, String seller_order_id, float rating) {
        if (AppHelpers.isConnectingToInternet(OrdersActivity.this)) {
            Map<String, String> ratemap = new HashMap<>(4);
            ratemap.put("seller_id", seller_email);
            ratemap.put("user_id", userDemoid);
            ratemap.put("order_id", seller_order_id);
            ratemap.put("rating", "" + rating);
            ratingResponsecall = flasDealApi.sendratingInfo(ratemap);
            ratingResponsecall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful()) {
                        Log.v(AppConstants.RESPONSE, response.body().getStatus());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.v(AppConstants.ERROR, t.getMessage());
                }
            });
        } else {
            Toast.makeText(OrdersActivity.this, "Please Connect to internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestCallSeller(String phone) {
        phonenumber = phone;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AndroidUtils.checkPermission(android.Manifest.permission.CALL_PHONE, this)) {
                callSeller();
            } else {
                AndroidUtils.requestPermission(this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            }
        } else {
            callSeller();
        }
    }

    private void callSeller() {
        if (!TextUtils.isEmpty(phonenumber)) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phonenumber));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Can not make call", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           callSeller();
        }
    }
}
