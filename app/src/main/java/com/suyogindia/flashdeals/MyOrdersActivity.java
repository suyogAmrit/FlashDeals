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
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.FlasDealApi;
import com.suyogindia.helpers.OnMyOrderItemClickListener;
import com.suyogindia.model.OrderResponse;
import com.suyogindia.model.Orders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyOrdersActivity extends AppCompatActivity implements OnMyOrderItemClickListener {
    private RecyclerView rcvMyOrders;
    Retrofit retrofit;
    FlasDealApi flasDealApi;
    Call<OrderResponse> orderResponseCall;
    private MyOrderAdapter adapter;
    private ArrayList<Orders>orderseList;
    ProgressDialog dialog;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        userId = shr.getString(AppConstants.USERID, AppConstants.NA);
        rcvMyOrders = (RecyclerView)findViewById(R.id.rcvMyOrders);
        rcvMyOrders.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this));
        retrofit = new Retrofit.Builder().baseUrl(AppConstants.BASEURL).addConverterFactory(GsonConverterFactory.create()).build();
        flasDealApi = retrofit.create(FlasDealApi.class);
        orderseList = new ArrayList<>();
        dialog = AppHelpers.showProgressDialog(MyOrdersActivity.this,"Showing Orders...");
        dialog.show();
        if (AppHelpers.isConnectingToInternet(MyOrdersActivity.this)){
            showMyOrders();
        }else {
            Toast.makeText(MyOrdersActivity.this,AppConstants.PLEASE_CONNCT,Toast.LENGTH_SHORT).show();
        }

    }

    private void showMyOrders() {
        Map<String,String> orderMap = new HashMap<>(1);
        orderMap.put(AppConstants.USERID,userId);
        orderResponseCall = flasDealApi.getMyOrders(orderMap);
        orderResponseCall.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                dialog.dismiss();
                Log.v(AppConstants.RESPONSE,response.body().getStatus());
                if (response.body().getStatus().equals("1")){
                    orderseList = response.body().getOrder();
                    adapter = new MyOrderAdapter(MyOrdersActivity.this,orderseList);
                    rcvMyOrders.setAdapter(adapter);
                    adapter.setMyorderClickListener(MyOrdersActivity.this);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                dialog.dismiss();
                Log.v(AppConstants.ERROR,t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onMyOrderItemclick(View view, int position) {
        Orders orders = orderseList.get(position);
        Intent intent = new Intent(MyOrdersActivity.this,ShowOrderDetailsActivity.class);
        intent.putExtra(AppConstants.EXTRA_ORDERS,orders);
        startActivity(intent);
    }
}
