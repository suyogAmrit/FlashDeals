package com.suyogindia.flashdeals;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.suyogindia.adapters.SellerItemOrderAdapter;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.FlasDealApi;
import com.suyogindia.model.ItemOrders;
import com.suyogindia.model.OrderDetailResponse;
import com.suyogindia.model.Orders;
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

public class ShowOrderDetailsActivity extends AppCompatActivity {
    private ExpandableListView expListOrders;
    private SellerItemOrderAdapter adapter;
    private ArrayList<SellerOrders> sellerOrderses;
    private ArrayList<ItemOrders> itemOrderses;
    Retrofit retrofit;
    FlasDealApi flasDealApi;
    Call<OrderDetailResponse> orderDetailResponseCall;
    Orders orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order_details);
        orders = getIntent().getParcelableExtra(AppConstants.EXTRA_ORDERS);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Showing Deatails of Id: "+orders.getOrder_id());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        expListOrders = (ExpandableListView)findViewById(R.id.expListOrders);
        retrofit = new Retrofit.Builder().baseUrl(AppConstants.BASEURL).addConverterFactory(GsonConverterFactory.create()).build();
        flasDealApi = retrofit.create(FlasDealApi.class);
        sellerOrderses = new ArrayList<>();
        itemOrderses = new ArrayList<>();
        if (AppHelpers.isConnectingToInternet(ShowOrderDetailsActivity.this)){
            showOrderDeatails();
        }else {
            Toast.makeText(ShowOrderDetailsActivity.this, AppConstants.PLEASE_CONNCT,Toast.LENGTH_SHORT).show();
        }
    }

    private void showOrderDeatails() {
        Map<String,String> orderMap = new HashMap<>(1);
        orderMap.put("order_id",orders.getOrder_id());
        orderDetailResponseCall = flasDealApi.getAllOrderDetails(orderMap);
        orderDetailResponseCall.enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(Call<OrderDetailResponse> call, Response<OrderDetailResponse> response) {
                Log.v(AppConstants.RESPONSE,response.body().getMessage());
                sellerOrderses = response.body().getSeller();
                for (SellerOrders sellOrders : sellerOrderses){
                    itemOrderses.addAll(sellOrders.getItems());
                }

                adapter = new SellerItemOrderAdapter(ShowOrderDetailsActivity.this,sellerOrderses);
                expListOrders.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
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
}
