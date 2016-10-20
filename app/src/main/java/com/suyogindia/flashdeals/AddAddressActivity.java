package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.Address;
import com.suyogindia.model.Result;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddAddressActivity extends AppCompatActivity {
    Retrofit retrofit;
    WebApi userApi;
    Call<Result> userDeatilsInsertCall, userUpdateCall, deleteAdddressCall;
    ProgressDialog dialog;
    Address address;
    private EditText etLocality, etCity, etState, etcountry, etZip, etPhone, etEmail;
    private Button btnSaveAddr, btnDeleteAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        address = getIntent().getParcelableExtra(AppConstants.EXTRA_ADDRESS);
        showDilog("Please wait", "Saving your Address...");
        etLocality = (EditText) findViewById(R.id.etLocality);
        etCity = (EditText) findViewById(R.id.etCity);
        etState = (EditText) findViewById(R.id.etState);
        etcountry = (EditText) findViewById(R.id.etcountry);
        etZip = (EditText) findViewById(R.id.etcountry);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnSaveAddr = (Button) findViewById(R.id.btnSaveAddr);
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
                } else if (!AppHelpers.isValidEmail(etPhone.getText().toString())) {
                    etPhone.setError("Number Not Valid");
                    return;
                } else {
                    dialog.show();
                    if (address == null) {
                        Map<String, String> userMap = new HashMap<String, String>(8);
                        userMap.put(AppConstants.EXTRA_USER_ID, "259b2c0934d4f32187cf712c1b307582");
                        userMap.put(AppConstants.EXTRA_USER_LOCALITY, etLocality.getText().toString());
                        userMap.put(AppConstants.EXTRA_USER_CITY, etCity.getText().toString());
                        userMap.put(AppConstants.EXTRA_USER_STATE, etState.getText().toString());
                        userMap.put(AppConstants.EXTRA_USER_COUNTRY, etcountry.getText().toString());
                        userMap.put(AppConstants.EXTRA_USER_ZIP, etZip.getText().toString());
                        userMap.put(AppConstants.EXTRA_USER_PHONE, etPhone.getText().toString());
                        userMap.put(AppConstants.EXTRA_USER_EMAIL, etEmail.getText().toString());
                        userDeatilsInsertCall = userApi.insertUserDetails(userMap);
                        userDeatilsInsertCall.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                dialog.dismiss();
                                Log.v("Respone", response.body().getStatus());
                                if (response.body().getStatus().equals("1")) {
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(AddAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                dialog.dismiss();
                                Log.v("Error", t.getMessage());
                            }
                        });
                    } else if (address != null) {
                        showDilog("Please wait", "Updating Addrress...");
                        Map<String, String> userUpdateMap = new HashMap<String, String>(8);
                        userUpdateMap.put(AppConstants.EXTRA_ADDRESS_ID, address.getId());
                        userUpdateMap.put(AppConstants.EXTRA_USER_LOCALITY, etLocality.getText().toString());
                        userUpdateMap.put(AppConstants.EXTRA_USER_CITY, etCity.getText().toString());
                        userUpdateMap.put(AppConstants.EXTRA_USER_STATE, etState.getText().toString());
                        userUpdateMap.put(AppConstants.EXTRA_USER_COUNTRY, etcountry.getText().toString());
                        userUpdateMap.put(AppConstants.EXTRA_USER_ZIP, etZip.getText().toString());
                        userUpdateMap.put(AppConstants.EXTRA_USER_PHONE, etPhone.getText().toString());
                        userUpdateMap.put(AppConstants.EXTRA_USER_EMAIL, etEmail.getText().toString());
                        userUpdateCall = userApi.updateUserDetails(userUpdateMap);
                        userUpdateCall.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                dialog.dismiss();
                                Log.v("Response", response.body().getStatus());
                                if (response.body().getStatus().equals("1")) {
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(AddAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                dialog.dismiss();
                                Log.v("error", t.getMessage());
                            }
                        });
                    }
                }
            }//
        });
//        btnDeleteAddr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (address != null) {
//                    showDilog("Pleasw wait", "Removing...");
//                    dialog.show();
//                    Map<String, String> deleteMap = new HashMap<String, String>(1);
//                    deleteMap.put(AppConstants.EXTRA_ADDRESS_ID, address.getId());
//                    deleteAdddressCall = userApi.deleteAddress(deleteMap);
//                    deleteAdddressCall.enqueue(new Callback<Result>() {
//                        @Override
//                        public void onResponse(Call<Result> call, Response<Result> response) {
//                            dialog.dismiss();
//                            Log.v("Response", response.body().getMessage());
//                            if (response.body().getStatus().equals("1")) {
//                                Intent intent = new Intent();
//                                setResult(RESULT_OK, intent);
//                                finish();
//                            } else {
//                                Toast.makeText(AddAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Result> call, Throwable t) {
//                            Log.v("Error", t.getMessage());
//                        }
//                    });
//                }
//            }
//        });
    }

    private void showDilog(String title, String message) {
        dialog = new ProgressDialog(AddAddressActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(message);
    }
}
