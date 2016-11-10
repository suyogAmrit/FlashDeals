package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.GenOtpPostData;
import com.suyogindia.model.GenOtpResponse;
import com.suyogindia.model.VerifyOtpPostData;
import com.suyogindia.model.VerifyOtpResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suyogcomputech on 10/10/16.
 */
public class VerifyOTPActivity extends AppCompatActivity {

    @BindView(R.id.tv_otp_instruction)
    TextView tvInstruction;
    @BindView(R.id.et_otp)
    EditText etOTP;
    @BindView(R.id.btn_resend_otp)
    Button btnResend;
    @BindView(R.id.tv_otp_counter)
    TextView tvCounter;
    @BindView(R.id.toolbar_otp)
    Toolbar toolbar;
    ProgressDialog dialog;

    String phoneNumber;
    OTPReceiver otpReceiver;
    int counterTime = 2 * 60 * 1000;
    boolean disabled = false;
    IntentFilter filter;
    Call<GenOtpResponse> otpResponseCall;
    Call<VerifyOtpResponse> verifyOtpResponseCall;
    MenuItem menuItem;
    boolean showMenu = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        phoneNumber = getIntent().getExtras().getString(AppConstants.MOBILENUMBER);
        Log.i("phn", phoneNumber);
        tvInstruction.setText(AppConstants.OTPINSTRUCTION + phoneNumber);
      //  etOTP.addTextChangedListener(new MyTextWatcher());
        updateUiForResend(true);
        generateOTP();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(VerifyOTPActivity.this, GetPhoneActivity.class);
        startActivity(i);
    }

    private void generateOTP() {

        otpReceiver = new OTPReceiver();
        filter = new IntentFilter(OTPReceiver.ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(otpReceiver, filter);
        //TODO
        requestOTP();
    }

    @OnClick(R.id.btn_resend_otp)
    void resendOtp() {
        requestOTP();
    }

    private void requestOTP() {
        if (AppHelpers.isConnectingToInternet(VerifyOTPActivity.this)) {
            //show dialog
            dialog = AppHelpers.showProgressDialog(VerifyOTPActivity.this, AppConstants.GENOTPMESSAGE);
            dialog.show();
            //setup retrofit
            WebApi api = AppHelpers.setupRetrofit();

            GenOtpPostData data = new GenOtpPostData(phoneNumber);
            otpResponseCall = api.getGenOtpResponse(data);
            otpResponseCall.enqueue(new Callback<GenOtpResponse>() {
                @Override
                public void onResponse(Call<GenOtpResponse> call, Response<GenOtpResponse> response) {
                    if (response.isSuccessful()) {
                        String status = response.body().getStatus();
                        Log.i(AppConstants.RESPONSE, response.body().getStatus());
                        dialog.dismiss();
                        if (status.equals(AppConstants.SUCESS)) {
                            updateUiForResend(false);
                        } else {
                            Snackbar snackbar = Snackbar.make(etOTP, response.body().getMessage(), Snackbar.LENGTH_INDEFINITE)
                                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            requestOTP();
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
                }

                @Override
                public void onFailure(Call<GenOtpResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.e("Error", t.getLocalizedMessage());
                }
            });
        } else {
            Snackbar snackbar = Snackbar.make(etOTP, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestOTP();
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

    private void updateUiForResend(boolean b) {
        if (b) {
            tvCounter.setVisibility(View.GONE);
            btnResend.setEnabled(true);
        } else {
            tvCounter.setVisibility(View.VISIBLE);
            btnResend.setEnabled(false);
            CountDownTimer timer = new CountDownTimer(2 * 60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long totalSecs = millisUntilFinished / 1000;
                    int mins = (int) (totalSecs / 60);
                    int sec = (int) (totalSecs % 60);
                    tvCounter.setText("Resend OTP in: " + String.valueOf(mins) + ":" + String.valueOf(sec));
                    if (sec < 10)
                        tvCounter.setText("Resend OTP in: " + String.valueOf(mins) + ":0" + String.valueOf(sec));
                }

                @Override
                public void onFinish() {
                    updateUiForResend(true);
                }
            }.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(otpReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_otp, menu);
        menuItem = menu.findItem(R.id.action_verify);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_verify) {
            String otp = etOTP.getText().toString();
            if (validateOtp(otp))
                verifyOTP(otp);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem.setVisible(showMenu);
        return true;
    }

    private void verifyOTP(String otp) {
        if (AppHelpers.isConnectingToInternet(VerifyOTPActivity.this)) {
            dialog = AppHelpers.showProgressDialog(VerifyOTPActivity.this, AppConstants.VERFYOTPMSG);
            dialog.show();
            showMenu = false;
            invalidateOptionsMenu();
            WebApi api = AppHelpers.setupRetrofit();
            VerifyOtpPostData data = new VerifyOtpPostData(otp, phoneNumber);
            verifyOtpResponseCall = api.getVeryOtpResponse(data);
            verifyOtpResponseCall.enqueue(new Callback<VerifyOtpResponse>() {
                @Override
                public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {
                    dialog.dismiss();
                    showMenu = true;
                    invalidateOptionsMenu();

                    if (response.isSuccessful()) {
                        Log.i("status", response.body().getStatus());
                        if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                            Log.i("response", response.body().getStatus());
                            String userId = response.body().getUserId();
                            //saveuserID
                            saveUserId(userId);

                        } else {
                            Snackbar snackbar = Snackbar.make(etOTP, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                            // Changing action button text color
                            View sbView = snackbar.getView();
                            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            tvMessage.setTextColor(Color.YELLOW);
                            snackbar.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.e("Error", t.getLocalizedMessage());
                    showMenu = true;
                    invalidateOptionsMenu();

                }
            });
        } else {
            Snackbar snackbar = Snackbar.make(etOTP, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (validateOtp(etOTP.getText().toString()))
                                verifyOTP(etOTP.getText().toString());
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

    private boolean validateOtp(String s) {
        return s.trim().length() == 4;
    }


    private void saveUserId(String userId) {
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shr.edit();
        editor.putString(AppConstants.USERID, userId);
        editor.apply();

        //fire Intent
        Intent i = new Intent(VerifyOTPActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (otpResponseCall != null && !otpResponseCall.isExecuted()) {
            otpResponseCall.cancel();
        }
        if (verifyOtpResponseCall != null && !verifyOtpResponseCall.isExecuted()) {
            verifyOtpResponseCall.cancel();
        }
    }

    public class OTPReceiver extends BroadcastReceiver {
        public final static String ACTION = "com.notifica.OTPRESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            String otp = intent.getExtras().getString(AppConstants.OTP);
            Log.i("OTP", otp);
            verifyOTP(otp);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() == 4) {
                verifyOTP(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
