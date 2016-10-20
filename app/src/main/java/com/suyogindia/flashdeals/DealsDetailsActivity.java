package com.suyogindia.flashdeals;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.model.CartItem;
import com.suyogindia.model.Deals;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suyogcomputech on 18/10/16.
 */
public class DealsDetailsActivity extends AppCompatActivity {
    Deals myDeals;

    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.tv_seller)
    TextView tvSeller;
    @BindView(R.id.tv_mrp)
    TextView tvMrp;
    @BindView(R.id.tv_discount)
    TextView tvDiscount;
    @BindView(R.id.tv_cart_offer_price)
    TextView tvOfferPrice;
    @BindView(R.id.et_qty)
    EditText etQty;
    @BindView(R.id.btn_add)
    ImageButton btnAdd;
    @BindView(R.id.btn_minus)
    ImageButton btnMinus;
    @BindView(R.id.tv_total_price)
    TextView tvTotal;
    @BindView(R.id.toolbar_details)
    Toolbar toolbar;
    String qty, totalPrice;
    int detailsType;

    CartItem cartItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals_details);
        ButterKnife.bind(this);
        detailsType = getIntent().getExtras().getInt(AppConstants.DETAILSTYPE);
        if (detailsType == 2) {
            myDeals = getIntent().getExtras().getParcelable(AppConstants.DEAL);
            setupUI();
        } else {
            cartItem = getIntent().getExtras().getParcelable(AppConstants.CARTITEM);
            Log.i(qty, cartItem.getMaxqty());
            setupUIWithCartItem();
        }

        etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();

                if (detailsType == 2) {
                    if (Integer.parseInt(value) > 0) {
                        btnMinus.setVisibility(View.VISIBLE);
                    } else {
                        btnMinus.setVisibility(View.INVISIBLE);
                    }
                    if (Integer.parseInt(value) == Integer.valueOf(myDeals.getQuantity())) {
                        btnAdd.setVisibility(View.INVISIBLE);
                    } else {
                        btnAdd.setVisibility(View.VISIBLE);
                    }
                }
                if (detailsType == 1) {
                    if (Integer.parseInt(value) > 1) {
                        btnMinus.setVisibility(View.VISIBLE);
                    } else {
                        btnMinus.setVisibility(View.INVISIBLE);
                    }
                    if (Integer.parseInt(value) == Integer.valueOf(cartItem.getMaxqty())) {
                        btnAdd.setVisibility(View.INVISIBLE);
                    } else {
                        btnAdd.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void setupUIWithCartItem() {
        toolbar.setTitle("Add Quantity");
        setSupportActionBar(toolbar);
        qty = cartItem.getQty();
        totalPrice = cartItem.getTotalPrice();
        tvDesc.setText(cartItem.getDesc());
        tvDiscount.setText(cartItem.getDiscount());
        tvMrp.setText(AppConstants.RUPEE + cartItem.getMrp());
        tvOfferPrice.setText(AppConstants.RUPEE + cartItem.getOfferPrice());
        tvSeller.setText(cartItem.getSellerName());
        etQty.setText(qty);
        tvTotal.setText(AppConstants.RUPEE + totalPrice);
    }

    private void setupUI() {
        toolbar.setTitle("Add Quantity");
        setSupportActionBar(toolbar);
        qty = totalPrice = "0";
        tvDesc.setText(myDeals.getDesciption());
        tvDiscount.setText(myDeals.getDiscount());
        tvMrp.setText(AppConstants.RUPEE + myDeals.getMrp());
        tvOfferPrice.setText(AppConstants.RUPEE + myDeals.getOffer_price());
        tvSeller.setText(myDeals.getSeller_name());
        etQty.setText(qty);
        btnMinus.setVisibility(View.INVISIBLE);
        tvTotal.setText(AppConstants.RUPEE + totalPrice);


    }

    @OnClick(R.id.btn_add)
    void addQty() {
        String value = etQty.getText().toString();
        int intValue = Integer.valueOf(value);
        if (detailsType == 2) {
            if (intValue < Integer.valueOf(myDeals.getQuantity()))
                etQty.setText(String.valueOf(intValue + 1));
        }
        if (detailsType == 1) {
            if (intValue < Integer.valueOf(cartItem.getMaxqty()))
                etQty.setText(String.valueOf(intValue + 1));
        }
        setTotalPrice(etQty.getText().toString());
    }

    private void setTotalPrice(String s) {
        qty = s;
        int value = Integer.parseInt(s);
        if (value != 0) {
            if (detailsType == 2) {
                double totalPriceValue = Double.parseDouble(myDeals.getOffer_price()) * value;
                totalPrice = String.valueOf(totalPriceValue);
            } else if (detailsType == 1) {
                double totalPriceValue = Double.parseDouble(cartItem.getOfferPrice()) * value;
                totalPrice = String.valueOf(totalPriceValue);
            }
        } else {
            totalPrice = "0";
        }
        tvTotal.setText(AppConstants.RUPEE + totalPrice);
    }

    @OnClick(R.id.btn_minus)
    void minusQty() {
        String value = etQty.getText().toString();
        int intValue = Integer.valueOf(value);
        etQty.setText(String.valueOf(intValue - 1));
        setTotalPrice(etQty.getText().toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_to_cart) {
            if (!qty.equals("0") && !totalPrice.equals("0")) {
                if (detailsType == 2) {
                    long row = AppHelpers.addToCart(this, qty, totalPrice, myDeals);
                    if (row > 0) {
                        Toast.makeText(this, "Item Added To Cart", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Snackbar snackbar = Snackbar.make(etQty, AppConstants.WENTWRONG, Snackbar.LENGTH_SHORT);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        tvMessage.setTextColor(Color.YELLOW);
                        snackbar.show();
                    }
                } else if (detailsType == 1) {
                    long row = AppHelpers.updateCart(this, qty, totalPrice, cartItem.getDelaId());
                    if (row > 0) {
                        Toast.makeText(this, "Item Added To Cart", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Snackbar snackbar = Snackbar.make(etQty, AppConstants.WENTWRONG, Snackbar.LENGTH_SHORT);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        tvMessage.setTextColor(Color.YELLOW);
                        snackbar.show();
                    }
                }

            } else {
                Snackbar snackbar = Snackbar.make(etQty, AppConstants.ENTERQTY, Snackbar.LENGTH_SHORT);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                tvMessage.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
        return true;
    }


}
