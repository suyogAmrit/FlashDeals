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
import android.text.TextUtils;
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
import com.suyogindia.model.PlaceOrderSeller;
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
        else {
            Toast.makeText(this, "Add Some Product to Cart", Toast.LENGTH_SHORT).show();
            finish();
        }
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
        list.add(0, c);
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
        if (!TextUtils.isEmpty(mySeller.getMaxPrice())) {
            if (a == 1 && Double.parseDouble(mySeller.getMaxPrice()) > Double.parseDouble(mySeller.getTotalPrice())) {
                grandTotal = totalQunatity + Double.parseDouble(mySeller.getShippingCharge());
                mySeller.setShippingAdded(2);
            } else if (a == 2 && mySeller.getShippingAdded() == 2) {
                grandTotal = totalQunatity - Double.parseDouble(mySeller.getShippingCharge());
                mySeller.setShippingAdded(1);
            }
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
            for (int i = 0;i<list.size();i++) {
                if (list.get(i).getType() == 2) {
                    if (list.get(i).getSeller().getDeleveryMode() == 0) {
                        modeDelivery = false;
                        rvCart.scrollToPosition(i);
                        break;
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
                ArrayList<PlaceOrderSeller> sellerArrayList = AppHelpers.getSellerList(list);

                if (homeDelivery) {
                    //move to select address
                    Intent i = new Intent(CartActivity.this, ShowAddressActivity.class);
                    i.putParcelableArrayListExtra(AppConstants.SELLERDEITALS, sellerArrayList);
                    i.putParcelableArrayListExtra(AppConstants.ORDERDETAILS, list);
                    i.putExtra(AppConstants.EXTRA_MANAGE_ADDR, false);
                    Log.i("seller", String.valueOf(list.get(2).getSeller()));
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(CartActivity.this, OrderReviewActivity.class);
                    i.putParcelableArrayListExtra(AppConstants.SELLERDEITALS, sellerArrayList);
                    i.putParcelableArrayListExtra(AppConstants.ORDERDETAILS, list);
                    i.putExtra(AppConstants.ADDRESSID, "");
                    i.putExtra(AppConstants.EXTRA_MANAGE_ADDR, false);
                    Log.i("seller", String.valueOf(list.get(2).getSeller()));
                    startActivity(i);
                    finish();
                }
            } else {
                Snackbar snackbar = Snackbar.make(rvCart, AppConstants.SELECTDELMODE, Snackbar.LENGTH_LONG);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                tvMessage.setTextColor(Color.RED);
                snackbar.show();
            }
        }
        return true;
    }


    private void placeOrder(final ArrayList<PlaceOrderSeller> sellerArrayList) {
        if (AppHelpers.isConnectingToInternet(this)) {
            callPlaceOrderWebService(sellerArrayList);
        } else {
            Snackbar snackbar = Snackbar.make(rvCart, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            placeOrder(sellerArrayList);
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

    private void callPlaceOrderWebService(ArrayList<PlaceOrderSeller> sellerArrayList) {
        dialog = AppHelpers.showProgressDialog(this, AppConstants.CREATEODER);
        dialog.show();
        responseCall = AppHelpers.placeOrder(this, list, sellerArrayList, "", 3);
        responseCall.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(Call<PlaceOrderResponse> call, Response<PlaceOrderResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.i("status", response.body().getStatus());
                    if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                        long row = AppHelpers.clearCart(CartActivity.this);
                        Log.i("DeletedRow", String.valueOf(row));
                        finish();
                    }
                }
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
