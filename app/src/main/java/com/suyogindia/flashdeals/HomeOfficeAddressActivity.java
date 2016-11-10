package com.suyogindia.flashdeals;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.model.Address;


public class HomeOfficeAddressActivity extends AppCompatActivity {
    private EditText etplotno, etLocality, etCity, etState, etZip, etPhone;
    private Button btnSaveAddr;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        toolbar = (Toolbar) findViewById(R.id.toolbar_add_address);
        toolbar.setTitle("Please Add Address");
        setSupportActionBar(toolbar);

        etplotno = (EditText) findViewById(R.id.etplotno);
        etLocality = (EditText) findViewById(R.id.etLocality);
        etCity = (EditText) findViewById(R.id.etCity);
        etState = (EditText) findViewById(R.id.etState);
        etZip = (EditText) findViewById(R.id.etZip);
        etPhone = (EditText) findViewById(R.id.etPhone);
        btnSaveAddr = (Button) findViewById(R.id.btnSaveAddr);
        btnSaveAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etLocality.getText().toString()) || TextUtils.isEmpty(etCity.getText().toString())
                        || TextUtils.isEmpty(etState.getText().toString()) || TextUtils.isEmpty(etplotno.getText().toString())
                        || TextUtils.isEmpty(etZip.getText().toString()) || TextUtils.isEmpty(etPhone.getText().toString())) {
                    Toast.makeText(HomeOfficeAddressActivity.this, "Please fillup address details", Toast.LENGTH_SHORT).show();
                    return;
                } else if (etPhone.getText().toString().equals("") && AppHelpers.isValidMobile(etPhone.getText().toString().trim())) {
                    etPhone.setError("Number Not Valid");
                    return;
                } else {
                    Address address = new Address();
                    address.setAddress(etLocality.getText().toString());
                    address.setCity(etCity.getText().toString());
                    address.setPhone(etPhone.getText().toString());
                    address.setPlotno(etplotno.getText().toString());
                    address.setState(etState.getText().toString());
                    address.setZip(etZip.getText().toString());
                    Intent i = new Intent();
                    Bundle b = new Bundle();
                    b.putParcelable(AppConstants.EXTRA_ADDRESS, address);
                    i.putExtras(b);
                    setResult(RESULT_OK, i);
                    finish();
                }
            }//
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }
}
