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
import android.text.TextUtils;
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
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.CartItem;
import com.suyogindia.model.CreateOrderResponse;
import com.suyogindia.model.OrderReviewResponse;
import com.suyogindia.model.PlaceOrderSeller;
import com.suyogindia.model.Result;
import com.suyogindia.model.ReviewItem;
import com.suyogindia.model.ReviewOrderItem;
import com.suyogindia.model.ReviewSeller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;

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
    String orderId = "";
    String userId;
    Call<CreateOrderResponse> createOrderResponseCall;
    WebApi webApi;
    Call<Result> resultCall;
    Dialog confirmOderDialog;
    private String addressId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        setupUi();
        getUserId();
        collectDataFromIntent();
        sendDataForReview();
    }

    private void getUserId() {
        userId = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE).getString(AppConstants.USERID, AppConstants.NA);
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
            //TODO uncomment the above lines and comment the bellows
            sendOrderDetails();
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
                if (!TextUtils.isEmpty(orderId)) {
                    sendOrderDataToServer(paymentId, orderId, userId);
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "cancelled");
                Toast.makeText(this, "Payment Cancelled.", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                Log.i(TAG, "failure");
                Toast.makeText(this, "Transaction Failed.", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
                Log.i(TAG, "User returned without login");
                Toast.makeText(this, "Transaction Failed.", Toast.LENGTH_SHORT).show();
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

    private void sendOrderDetails() {
        if (AppHelpers.isConnectingToInternet(this)) {
            callWebServiceAndCreateOrder();
        } else {
            Snackbar snackbar = Snackbar.make(rvOrderReview, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendOrderDetails();
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

    private void callWebServiceAndCreateOrder() {
        dialog = AppHelpers.showProgressDialog(this, AppConstants.CREATEODER);
        dialog.show();
        createOrderResponseCall = AppHelpers.createOrder(OrderReviewActivity.this, lisOrders, lisSellers, addressId);
        createOrderResponseCall.enqueue(new Callback<CreateOrderResponse>() {
            @Override
            public void onResponse(Call<CreateOrderResponse> call, Response<CreateOrderResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.i(AppConstants.STATUS, response.body().getStatus());
                    if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                        orderId = response.body().getOrderId();
                        Log.i("orderid", response.body().getOrderId());
                        MakePaymentHelper myMakePayment = new MakePaymentHelper(OrderReviewActivity.this);
                        // TODO: 08/11/16 change to total amount
//                        myMakePayment.initiatePayment(10.00);
                        //Changes for send order data
                        if (!TextUtils.isEmpty(orderId)) {
                            sendOrderDataToServer("2016", orderId, userId);
                        }
                    } else {
                        Toast.makeText(OrderReviewActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateOrderResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e(AppConstants.ERROR, t.getLocalizedMessage());
            }
        });
    }

    private void sendOrderDataToServer(final String paymentId, final String orderId, final String userId) {
        if (AppHelpers.isConnectingToInternet(this)) {
            callWebServiceForPaymentConfirmation(paymentId, orderId, userId);
        } else {
            Snackbar snackbar = Snackbar.make(rvOrderReview, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendOrderDataToServer(paymentId, orderId, userId);
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

    private void callWebServiceForPaymentConfirmation(String paymentId, String orderId, String userId) {
        dialog=AppHelpers.showProgressDialog(OrderReviewActivity.this,AppConstants.ORDERCONFRMMSG);
        dialog.show();
        Map<String, String> mapOrder = new HashMap<>(3);
        mapOrder.put(AppConstants.PAYMENTID, paymentId);
        mapOrder.put("orderId", orderId);
        mapOrder.put("userId", userId);
        webApi = AppHelpers.setupRetrofit();
        resultCall = webApi.sendOrderData(mapOrder);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                dialog.dismiss();
                Log.v(AppConstants.RESPONSE, response.body().getStatus());
                if (response.body().getStatus().equals("1")) {
                    AppHelpers.clearCart(OrderReviewActivity.this);
                    showOrderSucessDialog();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                dialog.dismiss();
                Log.e(AppConstants.ERROR, t.getMessage());
                Toast.makeText(OrderReviewActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOrderSucessDialog() {
        confirmOderDialog = new Dialog(this);
        confirmOderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmOderDialog.setContentView(R.layout.dialog_order_sucess);
        TextView tvOrderID = (TextView) confirmOderDialog.findViewById(R.id.tv_dialog_order_id);
        tvOrderID.setText("Order Id: " + orderId);
        Button btnOkay = (Button) confirmOderDialog.findViewById(R.id.btn_confirm);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOderDialog.dismiss();
                Intent i = new Intent(OrderReviewActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        confirmOderDialog.setCancelable(false);
        confirmOderDialog.show();
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
                if (response.isSuccessful()) {
                    Log.i(AppConstants.STATUS + "orderreview", response.body().getStatus());
                    if (response.body().getStatus().equals(AppConstants.SUCESS)) {
//                    OrderReviewResponse orderReviewResponse = response.body();
                        getReviewOrderItemsFrom(response.body());
                        if (listItems != null && listItems.size() > 0)
                            adapter.add(listItems);
                    }
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
                        item.getReview_message(), item.getCategory_id(), item.getShipping_price(), item.getTotal_item_price(),
                        item.getItem_price(), item.getOffer_price(), item.getDelivery_mode(), item.getReview_status());
                listItems.add(item2);
            }

            ReviewOrderItem item3 = new ReviewOrderItem(3, s.getSellerwise_total_price());
            listItems.add(item3);

        }
        ReviewOrderItem item5 = new ReviewOrderItem(4, orderReviewResponse.getGrand_total());
        listItems.add(0, item5);
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
