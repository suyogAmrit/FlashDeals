package com.suyogindia.flashdeals;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suyogindia.helpers.AppConstants;

public class SellerMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    double lattitude,longitude;
    String sellerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_map);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Flash Deal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sellerName = getIntent().getStringExtra(AppConstants.EXTRA_SELLER_NAME);
        lattitude = getIntent().getDoubleExtra(AppConstants.EXTRA_LATTITUDE,0);
        longitude = getIntent().getDoubleExtra(AppConstants.EXTRA_LONGITUDE,0);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        SellerMapActivity.this.map = googleMap;
        LatLng latlang = new LatLng(lattitude, longitude);
        map.addMarker(new MarkerOptions().position(latlang).title(sellerName)).showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLng(latlang));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlang, 10);
        map.animateCamera(cameraUpdate);
    }
}
