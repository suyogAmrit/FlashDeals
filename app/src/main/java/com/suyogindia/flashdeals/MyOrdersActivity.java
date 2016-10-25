package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.suyogindia.adapters.MyOrderAdapter;
import com.suyogindia.adapters.OrdersDetailAdapter;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.FlasDealApi;
import com.suyogindia.helpers.OnMyOrderItemClickListener;
import com.suyogindia.model.ItemOrders;
import com.suyogindia.model.OrderDetailResponse;
import com.suyogindia.model.OrderItem;
import com.suyogindia.model.OrderResponse;
import com.suyogindia.model.Orders;
import com.suyogindia.model.Result;
import com.suyogindia.model.Seller;
import com.suyogindia.model.SellerOrders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyOrdersActivity extends AppCompatActivity {
    private RecyclerView rcvMyOrders;
    Retrofit retrofit;
    FlasDealApi flasDealApi;
    Call<OrderDetailResponse> orderResponseCall;
    //private MyOrderAdapter adapter;
    private OrdersDetailAdapter adapter;
    private ArrayList<SellerOrders> orderseList;
    ProgressDialog dialog;
    String userId;
    Call<Result> radioResponseCall,ratingResponsecall;
    public static  String responseString,userDemoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        userId = shr.getString(AppConstants.USERID, AppConstants.NA);
        userDemoid = userId;
        rcvMyOrders = (RecyclerView) findViewById(R.id.rcvMyOrders);
        rcvMyOrders.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this));
        retrofit = new Retrofit.Builder().baseUrl(AppConstants.BASEURL).addConverterFactory(GsonConverterFactory.create()).build();
        flasDealApi = retrofit.create(FlasDealApi.class);
        orderseList = new ArrayList<>();
        dialog = AppHelpers.showProgressDialog(MyOrdersActivity.this, "Showing Orders...");
        dialog.show();
        if (AppHelpers.isConnectingToInternet(MyOrdersActivity.this)) {
            showMyOrders();
        } else {
            Toast.makeText(MyOrdersActivity.this, AppConstants.PLEASE_CONNCT, Toast.LENGTH_SHORT).show();
        }

    }

    private void showMyOrders() {
        Map<String, String> orderMap = new HashMap<>(1);
        orderMap.put(AppConstants.USERID, userId);
        orderResponseCall = flasDealApi.getMyOrders(orderMap);
        orderResponseCall.enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(Call<OrderDetailResponse> call, Response<OrderDetailResponse> response) {
                dialog.dismiss();
                Log.v(AppConstants.RESPONSE, response.body().getStatus());
                if (response.body().getStatus().equals("1")) {
                    orderseList = response.body().getOrder();
                    ArrayList<OrderItem> itemOrdersArrayList = getItemOrdersFrom(orderseList);
                    adapter = new OrdersDetailAdapter(MyOrdersActivity.this, itemOrdersArrayList);
                    rcvMyOrders.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                dialog.dismiss();
                Log.v(AppConstants.ERROR, t.getMessage());
            }
        });
    }

    private ArrayList<OrderItem> getItemOrdersFrom(ArrayList<SellerOrders> orderseList) {
        ArrayList<OrderItem> itemOrdersArrayList = new ArrayList<>();
        for (SellerOrders order : orderseList) {
            OrderItem itemSeller = new OrderItem(0, order.getSeller_name(), order.getSeller_order_id());
            itemOrdersArrayList.add(itemSeller);
            ArrayList<ItemOrders> arrayList = order.getItems();
            for (ItemOrders i : arrayList) {
                OrderItem item = new OrderItem(1, null, null, null, null, null, null, null, null, null, null,null,null,null,null,null);
                item.setOrders(i);
                itemOrdersArrayList.add(item);
            }
            OrderItem orderItem = new OrderItem(2, order.getSeller_name(),order.getDelevery_mode(), order.getShipping_charge(),
                    order.getAddress(), order.getCity(), order.getState(), order.getCountry(), order.getZip(), order.getPhone(), order.getEmail(),order.getDelevery_status(),order.getUser_delevery_status(),order.getSeller_order_id(),order.getSeller_email(),order.getRating());
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
        Map<String,String> map = new HashMap<>(2);
        map.put("seller_order_id",seller_order_id);
        map.put("user_delevery_status",ids);
        radioResponseCall = flasDealApi.sendInfoOFRadio(map);
        radioResponseCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.v(AppConstants.RESPONSE,response.body().getMessage());
                responseString = ""+response.body().getStatus();
                Log.v("","");
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.v(AppConstants.ERROR,t.getMessage());
            }
        });
    }

    public void rateSeller(String seller_email, String userDemoid, String seller_order_id, float rating) {
        Map<String,String> ratemap = new HashMap<>(4);
        ratemap.put("seller_id",seller_email);
        ratemap.put("user_id",userDemoid);
        ratemap.put("order_id",seller_order_id);
        ratemap.put("rating",""+rating);
        ratingResponsecall = flasDealApi.sendratingInfo(ratemap);
        ratingResponsecall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.v(AppConstants.RESPONSE,response.body().getStatus());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.v(AppConstants.ERROR,t.getMessage());
            }
        });
    }
}
