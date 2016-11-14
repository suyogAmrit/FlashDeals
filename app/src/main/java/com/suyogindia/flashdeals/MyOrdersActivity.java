package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.suyogindia.adapters.OrdersDetailAdapter;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.FlasDealApi;
import com.suyogindia.model.ItemOrders;
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

public class MyOrdersActivity extends AppCompatActivity {
    public static String responseString, userDemoid;
    Retrofit retrofit;
    FlasDealApi flasDealApi;
    Call<OrderDetailResponse> orderResponseCall;
    ProgressDialog dialog;
    String userId;
    Call<Result> radioResponseCall, ratingResponsecall;
    private RecyclerView rcvMyOrders;
    //private MyOrderAdapter adapter;
    private OrdersDetailAdapter adapter;
    private ArrayList<SellerOrders> orderseList;

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
        adapter = new OrdersDetailAdapter(MyOrdersActivity.this);
        rcvMyOrders.setAdapter(adapter);

        retrofit = new Retrofit.Builder().baseUrl(AppConstants.BASEURL).addConverterFactory(GsonConverterFactory.create()).build();
        flasDealApi = retrofit.create(FlasDealApi.class);
        orderseList = new ArrayList<>();

        getMyOrders();
    }

    private void getMyOrders() {
        if (AppHelpers.isConnectingToInternet(MyOrdersActivity.this)) {
            showMyOrders();
        } else {
            Snackbar snackbar = Snackbar.make(rcvMyOrders, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getMyOrders();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    private void showMyOrders() {
        dialog = AppHelpers.showProgressDialog(MyOrdersActivity.this, "Showing Orders...");
        dialog.show();
        Map<String, String> orderMap = new HashMap<>(1);
        orderMap.put(AppConstants.USERID, userId);
        orderResponseCall = flasDealApi.getMyOrders(orderMap);
        orderResponseCall.enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(Call<OrderDetailResponse> call, Response<OrderDetailResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.v(AppConstants.RESPONSE, response.body().getStatus());
                    if (response.body().getStatus().equals("1")) {
                        orderseList = response.body().getOrder();
                        //if (orderseList != null && orderseList.size() > 0)
                        //adapter.add(getItemOrdersFrom(orderseList));

                    }
                }
            }

            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e(AppConstants.ERROR, t.getMessage());
            }
        });
    }

    private ArrayList<OrderItem> getItemOrdersFrom(ArrayList<SellerOrders> orderseList) {
        ArrayList<OrderItem> itemOrdersArrayList = new ArrayList<>();
        for (SellerOrders order : orderseList) {
            OrderItem itemSeller = new OrderItem(0, order.getSeller_name(), order.getSeller_order_id(), order.getSeller_address(), order.getPhone());
            itemOrdersArrayList.add(itemSeller);
            ArrayList<ItemOrders> arrayList = order.getItems();
            for (ItemOrders i : arrayList) {
                OrderItem item = new OrderItem(1, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
                item.setOrders(i);
                itemOrdersArrayList.add(item);
            }
            OrderItem orderItem = new OrderItem(2, order.getSeller_name(), order.getDelevery_mode(), order.getShipping_charge(),
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
        if (AppHelpers.isConnectingToInternet(MyOrdersActivity.this)) {

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
            Toast.makeText(MyOrdersActivity.this, "Please Connect to internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void rateSeller(String seller_email, String userDemoid, String seller_order_id, float rating) {
        if (AppHelpers.isConnectingToInternet(MyOrdersActivity.this)) {
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
            Toast.makeText(MyOrdersActivity.this, "Please Connect to internet", Toast.LENGTH_SHORT).show();
        }
    }
}
