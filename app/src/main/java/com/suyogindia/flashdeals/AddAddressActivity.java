package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.AddAddressResponse;
import com.suyogindia.model.Address;
import com.suyogindia.model.CartItem;
import com.suyogindia.model.PlaceOrderResponse;
import com.suyogindia.model.PlaceOrderSeller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddAddressActivity extends AppCompatActivity {
    Retrofit retrofit;
    WebApi userApi;
    Call<AddAddressResponse> userDeatilsInsertCall;
    Call<AddAddressResponse> userUpdateCall;
    ProgressDialog dialog;
    Address address;
    ArrayList<CartItem> lisOrders;
    ArrayList<PlaceOrderSeller> lisSellers;
    Call<PlaceOrderResponse> placeOrderResponseCall;
    boolean isManageOrder;
    Toolbar toolbar;
    private EditText etLocality, etCity, etState, etcountry, etZip, etPhone, etEmail;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        isManageOrder = getIntent().getBooleanExtra(AppConstants.EXTRA_MANAGE_ADDR, false);
        if (!isManageOrder) {
            lisSellers = getIntent().getParcelableArrayListExtra(AppConstants.SELLERDEITALS);
            lisOrders = getIntent().getParcelableArrayListExtra(AppConstants.ORDERDETAILS);
        }
        address = getIntent().getParcelableExtra(AppConstants.EXTRA_ADDRESS);
        showDilog("Please wait", "Saving your Address...");
        etLocality = (EditText) findViewById(R.id.etLocality);
        etCity = (EditText) findViewById(R.id.etCity);
        etState = (EditText) findViewById(R.id.etState);
        etcountry = (EditText) findViewById(R.id.etcountry);
        etZip = (EditText) findViewById(R.id.etZip);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        Button btnSaveAddr = (Button) findViewById(R.id.btnSaveAddr);
        toolbar = (Toolbar) findViewById(R.id.toolbar_add_address);
        toolbar.setTitle("Enter Details");
        setSupportActionBar(toolbar);
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        userID = shr.getString(AppConstants.USERID, AppConstants.NA);
        //  btnDeleteAddr = (Button) findViewById(R.id.btnDeleteAddr);

        userApi = AppHelpers.setupRetrofit();
        if (address != null) {
            etLocality.setText(address.getAddress());
            etCity.setText(address.getCity());
            etState.setText(address.getState());
            etcountry.setText(address.getCountry());
            etZip.setText(address.getZip());
            etPhone.setText(address.getPhone());
            etEmail.setText(address.getEmail());
        }
        btnSaveAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etLocality.getText().toString()) || TextUtils.isEmpty(etCity.getText().toString())
                        || TextUtils.isEmpty(etState.getText().toString()) || TextUtils.isEmpty(etcountry.getText().toString())
                        || TextUtils.isEmpty(etZip.getText().toString()) || TextUtils.isEmpty(etPhone.getText().toString())
                        || TextUtils.isEmpty(etEmail.getText().toString())) {
                    Toast.makeText(AddAddressActivity.this, "Please fillup address details", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!AppHelpers.isValidEmail(etEmail.getText().toString())) {
                    etEmail.setError("Email not valid");
                    return;
                } else if (etPhone.getText().toString().equals("")) {
                    etPhone.setError("Number Not Valid");
                    return;
                } else {
                    performOperation();
                }
            }//
        });
//
    }

    private void performOperation() {
        if (AppHelpers.isConnectingToInternet(AddAddressActivity.this)) {
            dialog.show();

            insertAddress(userID);

//            }
        } else {
            Snackbar snackbar = Snackbar.make(etZip, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            performOperation();
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

    private void insertAddress(String userID) {
        Map<String, String> userMap = new HashMap<String, String>(8);
        userMap.put(AppConstants.EXTRA_USER_ID, userID);
        userMap.put(AppConstants.EXTRA_USER_LOCALITY, etLocality.getText().toString());
        userMap.put(AppConstants.EXTRA_USER_CITY, etCity.getText().toString());
        userMap.put(AppConstants.EXTRA_USER_STATE, etState.getText().toString());
        userMap.put(AppConstants.EXTRA_USER_COUNTRY, etcountry.getText().toString());
        userMap.put(AppConstants.EXTRA_USER_ZIP, etZip.getText().toString());
        userMap.put(AppConstants.EXTRA_USER_PHONE, etPhone.getText().toString());
        userMap.put(AppConstants.EXTRA_USER_EMAIL, etEmail.getText().toString());
        userDeatilsInsertCall = userApi.insertUserDetails(userMap);
        userDeatilsInsertCall.enqueue(new Callback<AddAddressResponse>() {
            @Override
            public void onResponse(Call<AddAddressResponse> call, Response<AddAddressResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.v("Respone", response.body().getStatus());
                    if (response.body().getStatus().equals("1")) {
                        if (isManageOrder) {
//                        Intent intent = new Intent();
//                        setResult(RESULT_OK, intent);
                            finish();
//                        Intent intent = new Intent(AddAddressActivity.this,ShowAddressActivity.class);
//                        startActivity(intent);
//                        finish();
                        } else {
//                        postOrders(response.body().getAddressId());
                            String addreId = response.body().getAddressId();
                            Intent i = new Intent(AddAddressActivity.this, OrderReviewActivity.class);
                            i.putExtra(AppConstants.ADDRESSID, addreId);
                            i.putParcelableArrayListExtra(AppConstants.ORDERDETAILS, lisOrders);
                            i.putParcelableArrayListExtra(AppConstants.SELLERDEITALS, lisSellers);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Toast.makeText(AddAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddAddressResponse> call, Throwable t) {
                dialog.dismiss();
                Log.v("Error", t.getMessage());
            }
        });
    }


    private void postOrders(final String addressId) {
        if (AppHelpers.isConnectingToInternet(AddAddressActivity.this)) {
            if (!isManageOrder) {
                callPlaceOrderWebService(addressId);
            }
        } else {
            Snackbar snackbar = Snackbar.make(etZip, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            postOrders(addressId);
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

    private void callPlaceOrderWebService(String addressId) {
        dialog = AppHelpers.showProgressDialog(this, AppConstants.CREATEODER);
        dialog.show();

        Call<PlaceOrderResponse> placeOrderResponseCall = AppHelpers.placeOrder(this, lisOrders, lisSellers, addressId, 3);
        placeOrderResponseCall.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(Call<PlaceOrderResponse> call, Response<PlaceOrderResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.i(AppConstants.STATUS, response.body().getStatus());
                    //TODO CLEAR CART
                    if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                        long row = AppHelpers.clearCart(AddAddressActivity.this);
                        Log.i("DeletedRow", String.valueOf(row));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceOrderResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e(AppConstants.ERROR, t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (placeOrderResponseCall != null && !placeOrderResponseCall.isExecuted()) {
            placeOrderResponseCall.cancel();
        }
        if (userDeatilsInsertCall != null && !userDeatilsInsertCall.isExecuted()) {
            userDeatilsInsertCall.cancel();
        }
        if (userUpdateCall != null && !userUpdateCall.isExecuted()) {
            userUpdateCall.cancel();
        }
    }

    private void showDilog(String title, String message) {
        dialog = new ProgressDialog(AddAddressActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(message);
    }
}
