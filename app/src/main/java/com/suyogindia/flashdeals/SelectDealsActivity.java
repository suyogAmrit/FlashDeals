package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.suyogindia.adapters.SelectDealsAdapter;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.ListCategoryResponse;
import com.suyogindia.model.SendCategoryResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suyogcomputech on 14/10/16.
 */
public class SelectDealsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_select_deals)
    Toolbar toolbar;
    @BindView(R.id.rv_deals)
    RecyclerView rvDeals;

    ProgressDialog dialog;
    Call<ListCategoryResponse> responseCall;
    SelectDealsAdapter adapter;
    Call<SendCategoryResponse> categoryResponseCall;
    boolean fromProfile = false;
    private List<String> listSelectedIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_deals);
        ButterKnife.bind(this);
        fromProfile = getIntent().getExtras().getBoolean(AppConstants.FROMPROFILE);
        setupUi();
        getCategoryList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromProfile) {
            Intent i = new Intent(this, MyProfileActivity.class);
            startActivity(i);
        }
    }

    private void setupUi() {
        toolbar.setTitle("Select The Deals");
        setSupportActionBar(toolbar);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvDeals.setLayoutManager(llm);
        listSelectedIds = new ArrayList<>();
        adapter = new SelectDealsAdapter(this, new SelectDealsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListCategoryResponse.Category item) {
                boolean notPresent = true;
                for (int i = 0; i < listSelectedIds.size(); i++) {
                    String s = listSelectedIds.get(i);
                    if (s.equals(item.getId())) {
                        listSelectedIds.remove(i);
                        notPresent = false;
                        break;
                    }
                }
                if (notPresent) {
                    listSelectedIds.add(item.getId());
                }
            }
        });

        rvDeals.setAdapter(adapter);
    }

    private void getCategoryList() {
        if (AppHelpers.isConnectingToInternet(this)) {
            callWebService();
        } else {
            Snackbar snackbar = Snackbar.make(rvDeals, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getCategoryList();
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

    private void callWebService() {
        dialog = AppHelpers.showProgressDialog(this, AppConstants.CATEGORYMSG);
        dialog.show();
        WebApi api = AppHelpers.setupRetrofit();
        responseCall = api.getCategory();
        responseCall.enqueue(new Callback<ListCategoryResponse>() {
            @Override
            public void onResponse(Call<ListCategoryResponse> call, Response<ListCategoryResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.i(AppConstants.RESPONSE, response.body().getStatus());
                    if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                        adapter.addCategoryList(response.body().getCategoryList());
                    } else {
                        Toast.makeText(SelectDealsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListCategoryResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e(AppConstants.ERROR, t.getLocalizedMessage());
                Toast.makeText(SelectDealsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (responseCall != null && !responseCall.isExecuted()) {
            responseCall.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_deals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_okay) {
            Log.i("size", String.valueOf(listSelectedIds.size()));
            if (listSelectedIds.size() > 0) {
                sendSelectedCategories();
            } else {
                Snackbar snackbar = Snackbar.make(rvDeals, AppConstants.SELCTCATEGORY, Snackbar.LENGTH_SHORT);

                snackbar.setActionTextColor(Color.RED);
                // Changing action button text color
                View sbView = snackbar.getView();
                TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                tvMessage.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
        return true;
    }

    public void sendSelectedCategories() {
        if (AppHelpers.isConnectingToInternet(this)) {
            callWebServiceToSend();
        } else {
            Snackbar snackbar = Snackbar.make(rvDeals, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendSelectedCategories();
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

    private void callWebServiceToSend() {
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        String userId = shr.getString(AppConstants.USERID, AppConstants.NA);
        dialog = AppHelpers.showProgressDialog(this, AppConstants.SENDCATMSG);
        dialog.show();
        WebApi api = AppHelpers.setupRetrofit();
        SendCategoryPostData data = new SendCategoryPostData(userId, listSelectedIds);
        categoryResponseCall = api.setCategoryList(data);
        categoryResponseCall.enqueue(new Callback<SendCategoryResponse>() {
            @Override
            public void onResponse(Call<SendCategoryResponse> call, Response<SendCategoryResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.i(AppConstants.RESPONSE, response.body().getStatus());
                    if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                        Intent i = new Intent(SelectDealsActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(SelectDealsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SendCategoryResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e(AppConstants.ERROR, t.getLocalizedMessage());
                Toast.makeText(SelectDealsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
