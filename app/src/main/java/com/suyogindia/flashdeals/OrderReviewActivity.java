package com.suyogindia.flashdeals;

import android.app.Dialog;
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
import android.text.Selection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;
import com.suyogindia.adapters.OrderReviewAdapter;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.MakePaymentHelper;
import com.suyogindia.model.CartItem;
import com.suyogindia.model.PlaceOrderSeller;
import com.suyogindia.model.ReviewItem;
import com.suyogindia.model.ReviewOrderItem;
import com.suyogindia.model.ReviewSeller;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suyogcomputech on 26/10/16.
 */

public class OrderReviewActivity extends AppCompatActivity {
    private static final String TAG = OrderReviewActivity.class.getName();
    @BindView(R.id.toolbar_cart)
    Toolbar toolbar;
    @BindView(R.id.rv_cart)
    RecyclerView rvOrderReview;
    ArrayList<CartItem> lisOrders;
    ArrayList<PlaceOrderSeller> lisSellers;
    ProgressDialog dialog;
    Call<OrderReviewResponse> response;
    ArrayList<ReviewOrderItem> listItems;
    double grandToal = 0;
    OrderReviewAdapter adapter;
    EditText etQty;
    Button btnOkay;
    Dialog d;
    private String addressId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        setupUi();

        collectDataFromIntent();
        sendDataForReview();
    }

    private void setupUi() {
        toolbar.setTitle("Order Review");
        setSupportActionBar(toolbar);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvOrderReview.setLayoutManager(llm);
        adapter = new OrderReviewAdapter(this);
        rvOrderReview.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_place_order) {
//            if (checkAllData()) {
            //make Payment
            MakePaymentHelper myMakePayment = new MakePaymentHelper(OrderReviewActivity.this);
            myMakePayment.initiatePayment(10.00);
//            } else {
//                Toast.makeText(this, "Some Items can't be delivered. Please remove the items to Proceed. ", Toast.LENGTH_LONG).show();
//            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==
                PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Success - Payment ID : " +
                        data.getStringExtra(SdkConstants.PAYMENT_ID));
                String paymentId =
                        data.getStringExtra(SdkConstants.PAYMENT_ID);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "cancelled");
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                Log.i(TAG, "failure");
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
                Log.i(TAG, "User returned without login");
            }
        }
    }

    private boolean checkAllData() {
        boolean canOrder = true;
        for (ReviewOrderItem item : listItems) {
            if (item.getType() == 2 && item.getReview_status() == 0) {
                canOrder = false;
            }
        }
        return canOrder;
    }

    private void sendDataForReview() {
        if (AppHelpers.isConnectingToInternet(this)) {
            callWebServiceForReview();
        } else {
            Snackbar snackbar = Snackbar.make(rvOrderReview, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendDataForReview();
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

    private void callWebServiceForReview() {
        dialog = AppHelpers.showProgressDialog(this, AppConstants.REVIEWMSG);
        dialog.show();
        grandToal = 0;
        response = AppHelpers.postForOrderReview(this, lisOrders, lisSellers, addressId);
        response.enqueue(new Callback<OrderReviewResponse>() {
            @Override
            public void onResponse(Call<OrderReviewResponse> call, Response<OrderReviewResponse> response) {
                dialog.dismiss();
                Log.i(AppConstants.STATUS + "orderreview", response.body().getStatus());
                if (response.body().getStatus().equals(AppConstants.SUCESS)) {
//                    OrderReviewResponse orderReviewResponse = response.body();
                    getReviewOrderItemsFrom(response.body());
                    if (listItems != null && listItems.size() > 0)
                        adapter.add(listItems);
                }
            }

            @Override
            public void onFailure(Call<OrderReviewResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });

    }

    private void getReviewOrderItemsFrom(OrderReviewResponse orderReviewResponse) {
        listItems = new ArrayList<>();
        List<ReviewSeller> sellerList = orderReviewResponse.getSellerList();
        for (ReviewSeller s : sellerList) {
            ReviewOrderItem item1 = new ReviewOrderItem(1, s.getSeller_name(), s.getSeller_email(), s.getCategory());
            listItems.add(item1);
            List<ReviewItem> itemList = s.getItems();
            for (ReviewItem item : itemList) {
                ReviewOrderItem item2 = new ReviewOrderItem(2, item.getDealId(), item.getDescription(), item.getQuantity_available(),
                        item.getReview_message(), item.getCategory_id(), item.getShipping_price(), item.getTotal_item_price(), item.getItem_price(), item.getOffer_price(), item.getReview_status());
                listItems.add(item2);
            }

            ReviewOrderItem item3 = new ReviewOrderItem(3, s.getSellerwise_total_price());
            listItems.add(item3);

        }
        ReviewOrderItem item5 = new ReviewOrderItem(4, orderReviewResponse.getGrand_total());
        listItems.add(item5);
        ReviewOrderItem item4 = new ReviewOrderItem(5, orderReviewResponse.getAddress());
        listItems.add(item4);
        grandToal = Double.parseDouble(orderReviewResponse.getGrand_total());
    }

    private void collectDataFromIntent() {
        addressId = getIntent().getExtras().getString(AppConstants.ADDRESSID);
        lisSellers = getIntent().getParcelableArrayListExtra(AppConstants.SELLERDEITALS);
        lisOrders = getIntent().getParcelableArrayListExtra(AppConstants.ORDERDETAILS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(OrderReviewActivity.this, CartActivity.class);
        startActivity(i);
    }

    public void pullNewDataAndDelete(String dealId) {

        for (int i = 0; i < lisOrders.size(); i++) {
            if (lisOrders.get(i).getType() == 1) {
                if (lisOrders.get(i).getDelaId().equals(dealId)) {
                    lisOrders.remove(i);
                }
            }
        }
        boolean noProduct = true;
        for (int i = 0; i < lisOrders.size(); i++) {
            if (lisOrders.get(i).getType() == 1) {
                noProduct = false;
            }
        }
        if (noProduct) {
            Toast.makeText(this, "Please Add Some Deals to cart", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(OrderReviewActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else
            sendDataForReview();
    }

    public void editQunatity(final String dealId, String quantity_available, String price) {
        d = new Dialog(OrderReviewActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_edit_qty);
        etQty = (EditText) d.findViewById(R.id.et_qty);
        btnOkay = (Button) d.findViewById(R.id.btn_okay);

        etQty.setText(quantity_available);
        Selection.setSelection(etQty.getText(), etQty.length());
        final Double priceD = Double.valueOf(price);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                String qty = etQty.getText().toString();

                int intQty = Integer.parseInt(qty);
                if (intQty > 0) {
                    etQty.setError(null);
                    Double totalPrice = intQty * priceD;
                    if (AppHelpers.updateCart(OrderReviewActivity.this, qty, String.valueOf(totalPrice), dealId) > 0) {
                        editOrderQty(dealId, qty);
                    }
                } else {
                    etQty.setError(AppConstants.QTYERROR);
                }
            }
        });
        d.show();
    }

    private void editOrderQty(String dealId, String qty) {
        for (int i = 0; i < lisOrders.size(); i++) {
            if (lisOrders.get(i).getType() == 1) {
                if (lisOrders.get(i).getDelaId().equals(dealId)) {
                    lisOrders.get(i).setQty(qty);
                }
            }
        }
        sendDataForReview();
    }
}