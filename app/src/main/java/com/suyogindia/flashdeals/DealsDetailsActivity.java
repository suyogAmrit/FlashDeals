package com.suyogindia.flashdeals;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.CartItem;
import com.suyogindia.model.Deals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suyogcomputech on 18/10/16.
 */
public class DealsDetailsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int LOCREQCODE = 111;
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
    //    @BindView(R.id.et_qty)
//    EditText etQty;
//    @BindView(R.id.btn_add)
//    ImageButton btnAdd;
//    @BindView(R.id.btn_minus)
//    ImageButton btnMinus;
    @BindView(R.id.tv_total_price)
    TextView tvTotal;
    @BindView(R.id.toolbar_details)
    Toolbar toolbar;
    @BindView(R.id.tv_delivery_to_pincode)
    TextView tvDelivery;
    @BindView(R.id.ibtn_edit_location)
    ImageButton btnEdit;
    @BindView(R.id.iv_deal)
    ImageView ivDeal;
    String qty, totalPrice;
    int detailsType;
    CartItem cartItem;
    String pinCode;
    Dialog myDialog;
    EditText etPincode;
    LinearLayout btnUseMyLocation;
    Button btnSubmit;
    @BindView(R.id.tv_only_left)
    TextView tvOnlyLeft;
    @BindView(R.id.spn_qty)
    Spinner spnQty;
    @BindView(R.id.tv_text_cart)
    TextView tvAddToCart;
    @BindView(R.id.cl_add_to_cart)
    RelativeLayout btnAddToCart;

    ArrayList<Integer> qtyList;
    ArrayAdapter<Integer> adapter;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LinearLayout imgEditProfile;

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
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);

        pinCode = shr.getString(AppConstants.PINCODE, AppConstants.NA);
//        etQty.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String value = s.toString();
//
//                if (detailsType == 2) {
//                    if (Integer.parseInt(value) > 1) {
//                        btnMinus.setVisibility(View.VISIBLE);
//                    } else {
//                        btnMinus.setVisibility(View.INVISIBLE);
//                    }
//                    if (Integer.parseInt(value) == Integer.valueOf(myDeals.getQuantity())) {
//                        btnAdd.setVisibility(View.INVISIBLE);
//                    } else {
//                        btnAdd.setVisibility(View.VISIBLE);
//                    }
//                }
//                if (detailsType == 1) {
//                    if (Integer.parseInt(value) > 1) {
//                        btnMinus.setVisibility(View.VISIBLE);
//                    } else {
//                        btnMinus.setVisibility(View.INVISIBLE);
//                    }
//                    if (Integer.parseInt(value) == Integer.valueOf(cartItem.getMaxqty())) {
//                        btnAdd.setVisibility(View.INVISIBLE);
//                    } else {
//                        btnAdd.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        });
        Log.i("pincode", pinCode);
        if (pinCode.equals(AppConstants.NA)) {
            tvDelivery.setText(AppConstants.ENTERPINCODE);
        } else {
            checkDeliveryOptions();
        }
    }

    private void checkDeliveryOptions() {
        if (AppHelpers.isConnectingToInternet(DealsDetailsActivity.this)) {
            callCheckDeliverWebService();

        } else {
            Snackbar snackbar = Snackbar.make(btnEdit, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkDeliveryOptions();
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

    @OnClick(R.id.ibtn_edit_location)
    void editeLocation() {
        showDialog();

    }

    @Override
    public void onBackPressed() {
        if (detailsType == 2) {
            super.onBackPressed();
        } else {
            Intent i = new Intent(DealsDetailsActivity.this, CartActivity.class);
            startActivity(i);
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCREQCODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayLocation();
        }
    }

    private void showDialog() {
        myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialog_pincode);
        etPincode = (EditText) myDialog.findViewById(R.id.et_dialo_pincode);
        btnUseMyLocation = (LinearLayout) myDialog.findViewById(R.id.btn_use_my_loc);
        btnSubmit = (Button) myDialog.findViewById(R.id.btn_submit);
        btnSubmit.setEnabled(false);
        etPincode.addTextChangedListener(new PincodeTextWatcher());
        btnUseMyLocation.setOnClickListener(new UseMyLocListener());
        btnSubmit.setOnClickListener(new PincodeSubmitListener());
        myDialog.show();
    }

    private void callCheckDeliverWebService() {
        String dealId = "";
        if (detailsType == 2) {
            dealId = myDeals.getDeal_id();
        } else {
            dealId = cartItem.getDelaId();
        }
        CheckDeliverPostData data = new CheckDeliverPostData(dealId, pinCode);
        WebApi api = AppHelpers.setupRetrofit();
        Call<CheckDeliveryResponse> responseCall = api.getDeliveryOptions(data);
        responseCall.enqueue(new Callback<CheckDeliveryResponse>() {
            @Override
            public void onResponse(Call<CheckDeliveryResponse> call, Response<CheckDeliveryResponse> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    if (status.equals(AppConstants.SUCESS)) {
                        setupUI(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckDeliveryResponse> call, Throwable t) {

            }

        });
    }

    private void setupUI(CheckDeliveryResponse body) {
        tvDelivery.setText(body.getMessage());
        if (body.getDelevery_area_status().equals(AppConstants.SUCESS)) {
            tvDelivery.setTextColor(Color.parseColor("#004D40"));
        } else {
            tvDelivery.setTextColor(Color.parseColor("#d32f2f"));
        }
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shr.edit();
        editor.putString(AppConstants.PINCODE, pinCode);
        editor.apply();
    }

    private void setupUIWithCartItem() {
        toolbar.setTitle("Edit Quantity");
        setSupportActionBar(toolbar);
        qty = cartItem.getQty();
        totalPrice = cartItem.getTotalPrice();
        tvDesc.setText(cartItem.getDesc());
        tvDiscount.setText(cartItem.getDiscount() + "%");
        tvMrp.setText(AppConstants.RUPEE + cartItem.getMrp());
        tvOfferPrice.setText(AppConstants.RUPEE + cartItem.getOfferPrice());
        tvSeller.setText(cartItem.getSellerName());
//        etQty.setText(qty);
        final int qtyValue = Integer.valueOf(cartItem.getMaxqty());
        qtyList = new ArrayList<>();
        for (int i = 1; i <= qtyValue; i++) {
            qtyList.add(i);
        }
        adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, qtyList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        tvAddToCart.setText("Update Cart");
        spnQty.setAdapter(adapter);
        spnQty.setSelection(0);
        spnQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int value = (int) parent.getItemAtPosition(position);
                qty = String.valueOf(value);
                setTotalPrice(qty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tvTotal.setText(AppConstants.RUPEE + totalPrice);
        tvOnlyLeft.setText("Only " + cartItem.getMaxqty() + " left.");

        Glide.with(this).load(cartItem.getImage_url()).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).override(155, 155).placeholder(R.drawable.ic_picasa).into(ivDeal);

    }

    private void setupUI() {
        toolbar.setTitle("Add Quantity");
        setSupportActionBar(toolbar);
        qty = "1";
        totalPrice = myDeals.getOffer_price();
        tvDesc.setText(myDeals.getDesciption());
        tvDiscount.setText(myDeals.getDiscount() + "%");
        tvMrp.setText(AppConstants.RUPEE + myDeals.getMrp());
        tvOfferPrice.setText(AppConstants.RUPEE + myDeals.getOffer_price());
        tvSeller.setText(myDeals.getSeller_name());
        tvAddToCart.setText("ADD TO CART");
//        etQty.setText(qty);
//        btnMinus.setVisibility(View.INVISIBLE);
        int qtyValue = Integer.valueOf(myDeals.getQuantity());
        qtyList = new ArrayList<>();
        for (int i = 1; i <= qtyValue; i++) {
            qtyList.add(i);
        }
        adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, qtyList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

        spnQty.setAdapter(adapter);
        spnQty.setSelection(0);
        spnQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int value = (int) parent.getItemAtPosition(position);
                qty = String.valueOf(value);
                setTotalPrice(qty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tvTotal.setText(AppConstants.RUPEE + totalPrice);
        tvOnlyLeft.setText("Only " + myDeals.getQuantity() + " left.");
        Glide.with(this).load(myDeals.getImage_url()).override(155, 155).placeholder(R.drawable.ic_picasa).into(ivDeal);

    }

//    @OnClick(R.id.btn_add)
//    void addQty() {
//        String value = etQty.getText().toString();
//        int intValue = Integer.valueOf(value);
//        if (detailsType == 2) {
//            if (intValue < Integer.valueOf(myDeals.getQuantity()))
//                etQty.setText(String.valueOf(intValue + 1));
//        }
//        if (detailsType == 1) {
//            if (intValue < Integer.valueOf(cartItem.getMaxqty()))
//                etQty.setText(String.valueOf(intValue + 1));
//        }
//        setTotalPrice(etQty.getText().toString());
//    }

    private void setTotalPrice(String s) {
        qty = s;
        int value = Integer.parseInt(s);
        if (value != 0) {
            if (detailsType == 2) {
                double totalPriceValue = Double.parseDouble(myDeals.getOffer_price()) * value;
                totalPrice = Double.toString(totalPriceValue);
            } else if (detailsType == 1) {
                double totalPriceValue = Double.parseDouble(cartItem.getOfferPrice()) * value;
                totalPrice = Double.toString(totalPriceValue);
            }
        } else {
            totalPrice = "0";
        }
        tvTotal.setText(AppConstants.RUPEE + totalPrice);
    }

//    @OnClick(R.id.btn_minus)
//    void minusQty() {
//        String value = etQty.getText().toString();
//        int intValue = Integer.valueOf(value);
//        etQty.setText(String.valueOf(intValue - 1));
//        setTotalPrice(etQty.getText().toString());
//
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_details, menu);
//        return true;
//    }

    @OnClick(R.id.cl_add_to_cart)
    void addOrUpdateCart() {
        if (!qty.equals("0") && !totalPrice.equals("0")) {
            if (detailsType == 2) {
                long row = AppHelpers.addToCart(this, qty, totalPrice, myDeals);
                if (row > 0) {
                    Snackbar snackbar = Snackbar.make(tvDesc, AppConstants.CARTUPDATE, Snackbar.LENGTH_INDEFINITE)
                            .setAction(AppConstants.GOTOCART, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(DealsDetailsActivity.this, CartActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                    snackbar.setActionTextColor(Color.GREEN);
                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    tvMessage.setTextColor(Color.YELLOW);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(tvDesc, AppConstants.WENTWRONG, Snackbar.LENGTH_SHORT);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    tvMessage.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
            } else if (detailsType == 1) {
                long row = AppHelpers.updateCart(this, qty, totalPrice, cartItem.getDelaId());
                if (row > 0) {
                    Intent i = new Intent(DealsDetailsActivity.this, CartActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Snackbar snackbar = Snackbar.make(tvDesc, AppConstants.WENTWRONG, Snackbar.LENGTH_SHORT);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    tvMessage.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }

        } else {
            Snackbar snackbar = Snackbar.make(tvDesc, AppConstants.ENTERQTY, Snackbar.LENGTH_SHORT);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_add_to_cart) {
//
//        }
//        return true;
//    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    private void displayLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCREQCODE);
            } else {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    double latitude = mLastLocation.getLatitude();
                    double longitude = mLastLocation.getLongitude();

                    getAddressFromLocation();
                } else {

                    Toast.makeText(DealsDetailsActivity.this, "Can not Locate You", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
//            mLastLocation.setAccuracy(100000);
            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                getAddressFromLocation();
            } else {

                Toast.makeText(DealsDetailsActivity.this, "Can not Locate You", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getAddressFromLocation() {
        Geocoder myGeocoder = new Geocoder(this);
        try {
            List<Address> addressList = myGeocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            Address myAddress = addressList.get(0);
            pinCode = myAddress.getPostalCode();
            Log.i("pincode", pinCode);
            SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = shr.edit();
            editor.putString(AppConstants.PINCODE, pinCode);
            editor.apply();
            checkDeliveryOptions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PincodeSubmitListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (myDialog != null) {
                myDialog.dismiss();
            }
            pinCode = etPincode.getText().toString();
            checkDeliveryOptions();
        }
    }

    private class UseMyLocListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.i("onclick", "use my location");
            if (myDialog != null) {
                myDialog.dismiss();
            }
            if (checkPlayServices()) {

                // Building the GoogleApi client
                buildGoogleApiClient();
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }

        }
    }

    private class PincodeTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() == 6) {
                btnSubmit.setEnabled(true);
            } else {
                btnSubmit.setEnabled(false);

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


}
