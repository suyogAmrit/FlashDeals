package com.suyogindia.flashdeals;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
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
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, LocationListener {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int LOCREQCODE = 111;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;
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
    Dialog exitDialog;
    boolean answers;
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
        if (userId.equals(AppConstants.NA)) {
            Intent iOTP = new Intent(MainActivity.this, GetPhoneActivity.class);
            startActivity(iOTP);
            finish();
        } else if (email.equals(AppConstants.NA)) {
            Intent iReg = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(iReg);
            finish();
        } else if (!answers) {
            Intent iAnswers = new Intent(MainActivity.this, QuestionaryActivity.class);
            startActivity(iAnswers);
            finish();
        } else {
            initNavigationDrawer();
            // First we need to check availability of play services
            checkFireBaseSeriver();
            if (checkPlayServices()) {

                // Building the GoogleApi client
                buildGoogleApiClient();
                createLocationRequest();
                buildLocationSettingsRequest();
                checkLocationSettings();
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
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCREQCODE);
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient,
                        mLocationRequest,
                        this
                ).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {

                }
            });
        }
    }

    private void checkFireBaseSeriver() {
        try {
            Log.i("userid", userId);
            final String token = FirebaseInstanceId.getInstance().getToken();
            Log.i("token", token);
            final String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            if (AppHelpers.isConnectingToInternet(MainActivity.this)) {

                MyFirebaseInstanceIDService.sendRegistrationToServer(token, android_id, userId);

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
                if (response.isSuccessful()) {
                    Log.i(AppConstants.RESPONSE, response.body().getStatus());
                    if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                        createUi(response.body().getCategoryList());
                    } else {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
        answers = shr.getBoolean(AppConstants.ANSWERS, false);
        // checkCredentialAndRedirect();
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
                        //Intent intent = new Intent(MainActivity.this, MyOrdersActivity.class);
                        Intent intent = new Intent(MainActivity.this, OrdersActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_profile:
                        Intent intent1 = new Intent(MainActivity.this, MyProfileActivity.class);
                        startActivity(intent1);
                        break;
                }
                return true;
            }
        });
//
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

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//
//        }
//    }

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
                    tvLocation.setText("Can not Locate you!");
                    // showEnableLocationDialog();
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
                tvLocation.setText("Can not Locate you!");
                // showEnableLocationDialog();
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
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            tvLocation.setText("Can not locate you");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCREQCODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
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
        showExitDialog();
    }

    private void showExitDialog() {
        exitDialog = new Dialog(MainActivity.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setContentView(R.layout.dialog_exit);
        Button btnYes = (Button) exitDialog.findViewById(R.id.btn_yes);
        Button btnNo = (Button) exitDialog.findViewById(R.id.btn_no);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                finish();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });
        exitDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        getAddressFromLocation();
    }
}
