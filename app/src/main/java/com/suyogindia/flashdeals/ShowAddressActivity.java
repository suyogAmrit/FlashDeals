package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.suyogindia.helpers.MenuListener;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.Address;
import com.suyogindia.model.AddressResponse;
import com.suyogindia.model.CartItem;
import com.suyogindia.model.PlaceOrderResponse;
import com.suyogindia.model.PlaceOrderSeller;
import com.suyogindia.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAddressActivity extends AppCompatActivity implements AddressAdapter.OnItemClickListner, MenuListener {
    WebApi addrApi;
    Call<AddressResponse> addressResponseCall;
    ProgressDialog dialog;
    Address address;
    Toolbar toolbar;
    ArrayList<CartItem> lisOrders;
    ArrayList<PlaceOrderSeller> lisSellers;
    String userId;
    private RecyclerView rcvShowAddr;
    private ArrayList<Address> addresListData;
    private AddressAdapter addressAdapter;
    private Call<Result> deleteAdddressCall;
    private Call<PlaceOrderResponse> responseCall;
    boolean isManagedAddr;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_address);
        isManagedAddr = getIntent().getBooleanExtra(AppConstants.EXTRA_MANAGE_ADDR, false);
        toolbar = (Toolbar) findViewById(R.id.toolbar_address_lis);
        toolbar.setTitle("Please Select Address");
        setSupportActionBar(toolbar);

        rcvShowAddr = (RecyclerView) findViewById(R.id.rcvShowAddr);

        rcvShowAddr.setLayoutManager(new LinearLayoutManager(ShowAddressActivity.this));
        addressAdapter = new AddressAdapter(ShowAddressActivity.this);
        rcvShowAddr.setAdapter(addressAdapter);
        addressAdapter.setOnItemclikListner(ShowAddressActivity.this);
        addrApi = AppHelpers.setupRetrofit();
        if (isManagedAddr == false) {
            lisSellers = getIntent().getParcelableArrayListExtra(AppConstants.SELLERDEITALS);
            lisOrders = getIntent().getParcelableArrayListExtra(AppConstants.ORDERDETAILS);
        }
        if (lisSellers != null) {
            Log.i("tag", lisSellers.get(0).getSellerEmail());
        }
        // showAllAddress();
    }

    private void showAllAddress() {
        if (addresListData!=null) {
            addresListData.clear();
            addressAdapter.clear();
        }
        if (AppHelpers.isConnectingToInternet(this)) {
            dialog = new ProgressDialog(ShowAddressActivity.this);
            dialog.setTitle("Please wait");
            dialog.setMessage("Showing Address...");
            dialog.show();
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
            userId = sharedPreferences.getString(AppConstants.USERID, AppConstants.NA);
            Map<String, String> addrMap = new HashMap<>(1);
            addrMap.put(AppConstants.EXTRA_USER_ID, userId);
            addressResponseCall = addrApi.getAllAddress(addrMap);
            addressResponseCall.enqueue(new Callback<AddressResponse>() {
                @Override
                public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                    dialog.dismiss();
                    Log.v("Response", response.body().getStatus());
                    if (response.body().getStatus().equals("1")) {
                        addresListData = response.body().getAddress();
                        addressAdapter.add(addresListData);
                        addressAdapter.setMenuListener(ShowAddressActivity.this);
//                        if (addresListData.size() >= 4) {
//                            menuItem.setVisible(false);
//                            invalidateOptionsMenu();
//                        } else {
//                            menuItem.setVisible(true);
//                            invalidateOptionsMenu();
//                        }
                    }
                }

                @Override
                public void onFailure(Call<AddressResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.v("Error", t.getMessage());
                }
            });
        } else {
            Snackbar snackbar = Snackbar.make(rcvShowAddr, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAllAddress();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_address, menu);
        menuItem = menu.findItem(R.id.action_add_address);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_add_address) {
            Intent intent = new Intent(ShowAddressActivity.this, AddAddressActivity.class);
            Bundle b = new Bundle();
            if (isManagedAddr == false) {
                b.putParcelableArrayList(AppConstants.ORDERDETAILS, lisOrders);
                b.putParcelableArrayList(AppConstants.SELLERDEITALS, lisSellers);
            }
            intent.putExtras(b);
            intent.putExtra(AppConstants.EXTRA_MANAGE_ADDR, isManagedAddr);
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
            if (dialog.isShowing()) {
                dialog.cancel();
            }
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
        responseCall = AppHelpers.placeOrder(this, lisOrders, lisSellers, addreId, 3);
        responseCall.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(Call<PlaceOrderResponse> call, Response<PlaceOrderResponse> response) {
                dialog.dismiss();
                Log.i("status", response.body().getStatus());
                //TODO
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
        if (isManagedAddr == false) {
            Intent i = new Intent(this, CartActivity.class);
            startActivity(i);
        }
    }

    public void deleteAddress(int adapterPosition) {
        Address myAddress = addresListData.get(adapterPosition);
        if (myAddress != null) {
            dialog = AppHelpers.showProgressDialog(this, AppConstants.DELETEMSG);
            dialog.show();
            Map<String, String> deleteMap = new HashMap<String, String>(1);
            deleteMap.put(AppConstants.EXTRA_ADDRESS_ID, myAddress.getId());
            WebApi api = AppHelpers.setupRetrofit();
            deleteAdddressCall = api.deleteAddress(deleteMap);
            deleteAdddressCall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    dialog.dismiss();
                    Log.v("Response", response.body().getMessage());
                    if (response.body().getStatus().equals("1")) {
                        addresListData.clear();
                        addressAdapter.clear();
                        showAllAddress();
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


    public void editAddress(int adapterPosition) {
        Intent intent = new Intent(this, AddAddressActivity.class);
        Bundle b = new Bundle();
        b.putParcelableArrayList(AppConstants.ORDERDETAILS, lisOrders);
        b.putParcelableArrayList(AppConstants.SELLERDEITALS, lisSellers);
        intent.putExtras(b);
        intent.putExtra(AppConstants.EXTRA_ADDRESS, addresListData.get(adapterPosition));
        startActivity(intent);
    }

    @Override
    public void setMenuItemVisible(boolean state) {
        menuItem.setVisible(state);
        invalidateOptionsMenu();
    }
}
