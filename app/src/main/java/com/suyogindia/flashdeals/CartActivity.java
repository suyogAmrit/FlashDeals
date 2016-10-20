package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.suyogindia.adapters.CartAdapter;
import com.suyogindia.database.DataBaseHelper;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.model.CartItem;
import com.suyogindia.model.PlaceOrderResponse;
import com.suyogindia.model.Seller;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suyogcomputech on 19/10/16.
 */
public class CartActivity extends AppCompatActivity {
    @BindView(R.id.rv_cart)
    RecyclerView rvCart;
    @BindView(R.id.toolbar_cart)
    Toolbar toolbar;

    ArrayList<CartItem> list;
    CartAdapter adapter;
    double grandTotal, totalQunatity;
    Call<PlaceOrderResponse> responseCall = null;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        setupUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        grandTotal = 0;
        totalQunatity = 0;
        getCartData();
    }

    private void setupUI() {
        toolbar.setTitle("Cart");
        setSupportActionBar(toolbar);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvCart.setLayoutManager(llm);
        adapter = new CartAdapter(this);
        rvCart.setAdapter(adapter);
    }

    private void getCartData() {
        DataBaseHelper helper = new DataBaseHelper(CartActivity.this);
        helper.open();
        list = helper.getCartData();
        helper.close();
        if (list != null && list.size() > 0)
            getGrandTotal();
    }

    private void getGrandTotal() {

        for (CartItem c : list) {
            if (c.getType() == 2) {
                grandTotal = grandTotal + Double.parseDouble(c.getSeller().getTotalPrice());
            }
            if (c.getType() == 1) {
                totalQunatity = totalQunatity + Integer.parseInt(c.getQty());
            }
        }
        CartItem c = new CartItem(null, null, null, null, null, null, null, null, null);
        c.setType(3);
        c.setTotalQuantity(String.valueOf(totalQunatity));
        c.setGrandTotal(String.valueOf(grandTotal));
        list.add(c);
        adapter.add(list);
    }

    public void removeItem(String delaId) {
        DataBaseHelper helper = new DataBaseHelper(CartActivity.this);
        helper.open();
        long row = helper.removeFromCart(delaId);
        if (row > 0) {
            getCartData();
        }
    }

    public void updateDeliveryInfoAndTotalPrice(int adapterPosition, int a) {
        CartItem item = list.get(adapterPosition);
        Seller mySeller = item.getSeller();
        mySeller.setDeleveryMode(a);

        if (a == 1 && Double.parseDouble(mySeller.getMaxPrice()) > Double.parseDouble(mySeller.getTotalPrice())) {
            grandTotal = totalQunatity + Double.parseDouble(mySeller.getShippingCharge());
            mySeller.setShippingAdded(2);
        } else if (a == 2 && mySeller.getShippingAdded() == 2) {
            grandTotal = totalQunatity - Double.parseDouble(mySeller.getShippingCharge());
            mySeller.setShippingAdded(1);
        }

        CartItem grandItem = list.get(list.size() - 1);
        grandItem.setGrandTotal(String.valueOf(grandTotal));
        adapter.add(list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_place_order) {
            boolean modeDelivery = true;
            for (CartItem cartItem : list) {
                if (cartItem.getType() == 2) {
                    if (cartItem.getSeller().getDeleveryMode() == 0) {
                        modeDelivery = false;
                    }
                }
            }
            if (modeDelivery) {
                boolean homeDelivery = false;
                for (CartItem cartItem : list) {
                    if (cartItem.getType() == 2) {
                        if (cartItem.getSeller().getDeleveryMode() == 1) {
                            homeDelivery = true;
                        }
                    }
                }
                if (homeDelivery) {
                    //move to select address
                    Intent i = new Intent(CartActivity.this, ShowAddressActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelableArrayList(AppConstants.ORDERDETAILS, list);
                    i.putExtras(b);
                    startActivity(i);
                    finish();
                } else {
                    placeOrder();
                }
            } else {
                Snackbar snackbar = Snackbar.make(rvCart, AppConstants.SELECTDELMODE, Snackbar.LENGTH_LONG);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                tvMessage.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
        return true;
    }


    private void placeOrder() {
        if (AppHelpers.isConnectingToInternet(this)) {
            callPlaceOrderWebService();
        } else {
            Snackbar snackbar = Snackbar.make(rvCart, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            placeOrder();
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

    private void callPlaceOrderWebService() {
        dialog = AppHelpers.showProgressDialog(this, AppConstants.CREATEODER);
        dialog.show();
        responseCall = AppHelpers.placeOrder(this, list, "", 3);
        responseCall.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(Call<PlaceOrderResponse> call, Response<PlaceOrderResponse> response) {
                dialog.dismiss();
                Log.i("status", response.body().getStatus());
            }

            @Override
            public void onFailure(Call<PlaceOrderResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(CartActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e(AppConstants.ERROR, t.getLocalizedMessage());
            }
        });
    }
}
