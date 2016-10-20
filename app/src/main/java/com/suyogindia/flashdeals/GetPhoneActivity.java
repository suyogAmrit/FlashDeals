package com.suyogindia.flashdeals;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.suyogindia.helpers.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetPhoneActivity extends AppCompatActivity {
    private static final String INDIACODE = "+91";
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.btn_next)
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phone);

        ButterKnife.bind(GetPhoneActivity.this);
        etCode.setText(INDIACODE);
        etCode.setEnabled(false);
        etPhone.requestFocus();

    }
    @OnClick(R.id.btn_next)
    void onNext(){
        String code = etCode.getText().toString();
        String phone = etPhone.getText().toString();
        if (phone.equals("") && phone.length() < 10) {
            etPhone.setError(AppConstants.PHOENERROR);
        } else {
            etPhone.setError(null);
            showAlertDialog(code, phone);
        }
    }
    private void showAlertDialog(final String code, final String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GetPhoneActivity.this);
        builder.setMessage(AppConstants.CONFIRMDIALOG + "'" + code + phone + "'?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                fireIntent(code,phone);
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
        Intent i = new Intent(GetPhoneActivity.this,VerifyOTPActivity.class);
        i.putExtra(AppConstants.MOBILENUMBER,code+phone);
        startActivity(i);
    }


}
