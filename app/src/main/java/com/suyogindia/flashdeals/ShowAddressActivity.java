package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.suyogindia.adapters.AddressAdapter;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.Address;
import com.suyogindia.model.AddressResponse;
import com.suyogindia.model.CartItem;
import com.suyogindia.model.PlaceOrderResponse;
import com.suyogindia.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAddressActivity extends AppCompatActivity implements AddressAdapter.OnItemClickListner {
    WebApi addrApi;
    Call<AddressResponse> addressResponseCall;
    ProgressDialog dialog;
    Address address;
    Toolbar toolbar;
    ArrayList<CartItem> lisOrders;
    private RecyclerView rcvShowAddr;
    private ArrayList<Address> addresListData;
    private AddressAdapter addressAdapter;
    private Call<Result> deleteAdddressCall;
    private Call<PlaceOrderResponse> responseCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_address);

        toolbar = (Toolbar) findViewById(R.id.toolbar_address_lis);
        toolbar.setTitle("Please Select Address");
        setSupportActionBar(toolbar);
        dialog = new ProgressDialog(ShowAddressActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Showing Address...");
        rcvShowAddr = (RecyclerView) findViewById(R.id.rcvShowAddr);
        rcvShowAddr.setLayoutManager(new LinearLayoutManager(ShowAddressActivity.this));
        addresListData = new ArrayList<>();
        addrApi = AppHelpers.setupRetrofit();
        showAllAddress();
        lisOrders = getIntent().getExtras().getParcelableArrayList(AppConstants.ORDERDETAILS);
    }

    private void showAllAddress() {
        dialog.show();
        Map<String, String> addrMap = new HashMap<>(1);
        addrMap.put(AppConstants.EXTRA_USER_ID, "259b2c0934d4f32187cf712c1b307582");
        addressResponseCall = addrApi.getAllAddress(addrMap);
        addressResponseCall.enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                dialog.dismiss();
                Log.v("Response", response.body().getStatus());
                if (response.body().getStatus().equals("1")) {
                    addresListData = response.body().getAddress();
                    addressAdapter = new AddressAdapter(ShowAddressActivity.this, addresListData);
                    rcvShowAddr.setAdapter(addressAdapter);
                    addressAdapter.setOnItemclikListner(ShowAddressActivity.this);
                }
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                dialog.dismiss();
                Log.v("Error", t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_add_address) {
            Intent intent = new Intent(ShowAddressActivity.this, AddAddressActivity.class);
            Bundle b = new Bundle();
            b.putParcelableArrayList(AppConstants.ORDERDETAILS, lisOrders);
            intent.putExtras(b);
            startActivityForResult(intent, AppConstants.REQUEST_CODE_ADDRESS);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == AppConstants.REQUEST_CODE_ADDRESS) {
            showAllAddress();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Address address = addresListData.get(position);
        String addreId = address.getId();
        placeOrder(addreId);
    }

    private void placeOrder(final String addreId) {
        if (AppHelpers.isConnectingToInternet(this)) {
            callWebService(addreId);
        } else {
            Snackbar snackbar = Snackbar.make(rcvShowAddr, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            placeOrder(addreId);
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void callWebService(String addreId) {
        dialog = AppHelpers.showProgressDialog(this, AppConstants.CREATEODER);
        dialog.show();
        responseCall = AppHelpers.placeOrder(this, lisOrders, addreId, 3);
        responseCall.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(Call<PlaceOrderResponse> call, Response<PlaceOrderResponse> response) {
                dialog.dismiss();
                Log.i("status", response.body().getStatus());
            }

            @Override
            public void onFailure(Call<PlaceOrderResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(ShowAddressActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e(AppConstants.ERROR, t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showAllAddress();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, CartActivity.class);
        startActivity(i);
    }

    public void deleteAddress(int adapterPosition) {
        Address myAddress = addresListData.get(adapterPosition);
        if (myAddress != null) {
            dialog = AppHelpers.showProgressDialog(this, AppConstants.DELETEMSG);
            dialog.show();
            Map<String, String> deleteMap = new HashMap<String, String>(1);
            deleteMap.put(AppConstants.EXTRA_ADDRESS_ID, address.getId());
            WebApi api = AppHelpers.setupRetrofit();
            deleteAdddressCall = api.deleteAddress(deleteMap);
            deleteAdddressCall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    dialog.dismiss();
                    Log.v("Response", response.body().getMessage());
                    if (response.body().getStatus().equals("1")) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(ShowAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.v("Error", t.getMessage());
                }
            });
        }
    }


}
