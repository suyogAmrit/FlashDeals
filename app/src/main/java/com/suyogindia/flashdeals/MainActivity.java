package com.suyogindia.flashdeals;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;
import com.suyogindia.adapters.DealsFragmentPagerAdapter;
import com.suyogindia.database.DataBaseHelper;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.Category;
import com.suyogindia.model.GetDealsPostData;
import com.suyogindia.model.GetDealsResponse;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suyogcomputech on 13/10/16.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int LOCREQCODE = 111;


    @BindView(R.id.drawer_layout)
    DrawerLayout myDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView myNavigationView;
    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.tab_main)
    TabLayout tabs;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.fab_cart)
    FloatingActionButton fabCart;


    String userId, email, pinCode = "";
    ProgressDialog dialog;
    Call<GetDealsResponse> dealsResponseCall;
    DealsFragmentPagerAdapter adapter;
    int totalItem = 0;
    boolean doubleBackToExitPressedOnce = false;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LinearLayout imgEditProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setTitle(getString(R.string.title_deals));
        setSupportActionBar(toolbar);
        getDataFromSharedPrefs();

        initNavigationDrawer();
        // First we need to check availability of play services
        checkFireBaseSeriver();
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        getDeals();
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalItem > 0) {
                    Intent i = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    private void checkFireBaseSeriver() {
        try {

            final String token = FirebaseInstanceId.getInstance().getToken();
            final String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            if (AppHelpers.isConnectingToInternet(MainActivity.this)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MyFirebaseInstanceIDService.sendRegistrationToServer(token, android_id, userId);
                    }
                }).start();
            }
        } catch (NullPointerException e) {
            Intent i = new Intent(MainActivity.this, MyFirebaseInstanceIDService.class);
            startService(i);
        }
    }

    private void getCartSize() {
        DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
        helper.open();
        totalItem = helper.getNumberOfDeals();
        helper.close();
        if (totalItem > 0) {
            fabCart.setVisibility(View.VISIBLE);
        } else {
            fabCart.setVisibility(View.GONE);
        }
    }

    private void getDeals() {
        if (AppHelpers.isConnectingToInternet(MainActivity.this)) {
            callWebServicesForDeals();
        } else {
            Snackbar snackbar = Snackbar.make(tvLocation, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDeals();
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

    private void callWebServicesForDeals() {
        dialog = AppHelpers.showProgressDialog(MainActivity.this, AppConstants.GETDEALSMSG);
        dialog.show();
        WebApi api = AppHelpers.setupRetrofit();
        dealsResponseCall = api.getDealsFrom(new GetDealsPostData(userId, pinCode));
        dealsResponseCall.enqueue(new Callback<GetDealsResponse>() {
            @Override
            public void onResponse(Call<GetDealsResponse> call, Response<GetDealsResponse> response) {
                dialog.dismiss();
                Log.i(AppConstants.RESPONSE, response.body().getStatus());
                if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                    createUi(response.body().getCategoryList());
                } else {
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetDealsResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e(AppConstants.ERROR, t.getLocalizedMessage());
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUi(List<Category> categoryList) {
        adapter = new DealsFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addItems(categoryList);
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }

    private void getDataFromSharedPrefs() {
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        userId = shr.getString(AppConstants.USERID, AppConstants.NA);
        email = shr.getString(AppConstants.EMAIL, AppConstants.NA);
        checkCredentialAndRedirect();
    }

    private void checkCredentialAndRedirect() {
        if (userId.equals(AppConstants.NA)) {
            Intent iOTP = new Intent(MainActivity.this, GetPhoneActivity.class);
            startActivity(iOTP);
            finish();
        } else if (email.equals(AppConstants.NA)) {
            Intent iReg = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(iReg);
            finish();
        }
    }

    private void initNavigationDrawer() {
        myNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_deals:
                        myDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_my_orders:
                        Intent intent = new Intent(MainActivity.this, MyOrdersActivity.class);
                        startActivity(intent);
                        break;

                }
                return true;
            }
        });
        View header = myNavigationView.getHeaderView(0);
        TextView tvEmail = (TextView) header.findViewById(R.id.tv_email);
        tvEmail.setText(email);
        imgEditProfile = (LinearLayout) header.findViewById(R.id.imgEditProfile);
        imgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, myDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        myDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
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

    /**
     * Method to verify google play services on the device
     */
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

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        getCartSize();
    }


    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    private void displayLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCREQCODE);
            } else {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
//            mLastLocation.setAccuracy(100000);
                if (mLastLocation != null) {
                    double latitude = mLastLocation.getLatitude();
                    double longitude = mLastLocation.getLongitude();

                    tvLocation.setText(latitude + ", " + longitude);
                    getAddressFromLocation();
                } else {

                    tvLocation
                            .setText("Can not Locate You");
                }
            }
        } else {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
//            mLastLocation.setAccuracy(100000);
            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                tvLocation.setText(latitude + ", " + longitude);
                getAddressFromLocation();
            } else {

                tvLocation
                        .setText("Can not Locate You");
            }
        }
    }

    private void getAddressFromLocation() {
        Geocoder myGeocoder = new Geocoder(this);
        try {
            List<Address> addressList = myGeocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            Address myAddress = addressList.get(0);
            pinCode = myAddress.getPostalCode();
            SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
            String savedPincode = shr.getString(AppConstants.PINCODE, "na");
            if (savedPincode.equals("na")) {
                SharedPreferences.Editor editor = shr.edit();
                editor.putString(AppConstants.PINCODE, pinCode);
                editor.apply();
            }


            tvLocation.setText(myAddress.getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCREQCODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @OnClick(R.id.btn_edit_location)
    void ediLocation() {

    }

    public void updateCart() {
        Toast.makeText(this, "Cart Updated", Toast.LENGTH_SHORT).show();
        totalItem = totalItem + 1;
        if (totalItem > 0) {
            fabCart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
