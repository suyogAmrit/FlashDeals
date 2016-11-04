package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.FlasDealApi;
import com.suyogindia.model.Profile;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyProfileActivity extends AppCompatActivity {
    ProgressDialog dialog;
    Retrofit retrofit;
    FlasDealApi profileApi;
    String userId;
    private TextView txtProfileName, txtProfileEmail, txtProfilePhone;
    private Button btnManageAddress;
    private Button btnSelectDeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtProfileName = (TextView) findViewById(R.id.txtProfileName);
        txtProfileEmail = (TextView) findViewById(R.id.txtProfileEmail);
        txtProfilePhone = (TextView) findViewById(R.id.txtProfilePhone);
        btnManageAddress = (Button) findViewById(R.id.btnManageAddress);
        btnSelectDeals = (Button) findViewById(R.id.btnSelectDeals);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Showing profile...");
        dialog.show();
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        userId = shr.getString(AppConstants.USERID, AppConstants.NA);
        retrofit = new Retrofit.Builder().baseUrl(AppConstants.BASEURL).addConverterFactory(GsonConverterFactory.create()).build();
        profileApi = retrofit.create(FlasDealApi.class);
        if (AppHelpers.isConnectingToInternet(MyProfileActivity.this)) {
            showProfileData();
        } else {
            Toast.makeText(MyProfileActivity.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }
        btnManageAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, ShowAddressActivity.class);
                intent.putExtra(AppConstants.EXTRA_MANAGE_ADDR, true);
                startActivity(intent);
            }
        });
        btnSelectDeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyProfileActivity.this, SelectDealsActivity.class);
                i.putExtra(AppConstants.FROMPROFILE, true);
                startActivity(i);
                finish();
            }
        });
    }

    private void showProfileData() {
        Map<String, String> map = new HashMap<>(1);
        map.put(AppConstants.USERID, userId);
        Call<Profile> profileCall = profileApi.getProfileInfo(map);
        profileCall.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("1")) {
                        txtProfileName.setText(response.body().getName());
                        txtProfileEmail.setText(response.body().getEmail());
                        txtProfilePhone.setText(response.body().getMobile_no());
                        //Toast.makeText(MyProfileActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.v("ProfileError", t.getMessage());
            }
        });
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
}
