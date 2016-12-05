package com.suyogindia.flashdeals;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.suyogindia.helpers.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetPhoneActivity extends AppCompatActivity {
    private static final String INDIACODE = "+91";
    private static final int MSGREQ = 214;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.toolbar_get_phone)
    Toolbar toolbar;
    @BindView(R.id.cb_terms)
    CheckBox cbTerms;
    @BindView(R.id.tv_terms)
    TextView tvTerms;


    private boolean checked = false;
    private String code;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phone);

        ButterKnife.bind(GetPhoneActivity.this);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        etCode.setText(INDIACODE);
        etCode.setEnabled(false);
        etPhone.requestFocus();

        tvTerms.setClickable(true);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvTerms.setText(Html.fromHtml(AppConstants.TERMS));

        cbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
            }
        });
    }


    @OnClick(R.id.btn_next)
    void onNext() {
        if (checked) {
            code = etCode.getText().toString();
            phone = etPhone.getText().toString();
            if (phone.equals("") && phone.length() < 10) {
                etPhone.setError(AppConstants.PHOENERROR);
            } else {
                etPhone.setError(null);
                requestMessagePermission(code, phone);
            }
        }else{
            Snackbar snackbar = Snackbar.make(etPhone,AppConstants.ACCECPTTERMS,Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void showAlertDialog(final String code, final String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GetPhoneActivity.this);
        builder.setMessage(AppConstants.CONFIRMDIALOG + "'" + code + phone + "'?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                fireIntent(code, phone);
            }

        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fireIntent(String code, String phone) {
        Intent i = new Intent(GetPhoneActivity.this, VerifyOTPActivity.class);
        i.putExtra(AppConstants.MOBILENUMBER, code + phone);
        startActivity(i);
        finish();
    }

    private void requestMessagePermission(String code, String mobile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS}, MSGREQ);
            } else {
                showAlertDialog(code, mobile);
            }
        } else {
            showAlertDialog(code, mobile);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MSGREQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showAlertDialog(code, phone);
            } else {
                Toast.makeText(GetPhoneActivity.this, "Please Grant messaging permission from App Settings", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
