package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.RegisterUserPostData;
import com.suyogindia.model.RegisterUserResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suyogcomputech on 13/10/16.
 */
public class RegistrationActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_reg)
    Toolbar toolbar;
    @BindView(R.id.et_reg_name)
    EditText etName;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_cnfrm_pass)
    EditText etCPass;

    ProgressDialog dialog;

    String name, email, password, cPass;
    String userId;
    Call<RegisterUserResponse> call;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        toolbar.setTitle("Registration");
        setSupportActionBar(toolbar);
        getUserId();
    }

    private void getUserId() {
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        userId = shr.getString(AppConstants.USERID, AppConstants.NA);
    }

    @OnClick(R.id.btn_complete_registraion)
    void completeRegistration() {
        name = etName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        cPass = etCPass.getText().toString();
        if (name.equals("")) {
            etName.setError(getString(R.string.hint_name));
        } else if (!AppHelpers.isValidEmail(email)) {
            etName.setError(null);
            etEmail.setError(getString(R.string.hint_enter_email));
        } else if (password.equals("") || password.length() < 5) {
            etName.setError(null);
            etEmail.setError(null);
            etPassword.setError(getString(R.string.hint_password_error));
            etCPass.setError(getString(R.string.hint_password_error));
        } else if (!password.equals(cPass)) {
            etName.setError(null);
            etEmail.setError(null);
            etPassword.setError(getString(R.string.password_match_error));
            etCPass.setError(getString(R.string.password_match_error));
        } else {
            registerUser();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(RegistrationActivity.this,LoginActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (call != null && !call.isExecuted()) {
            call.cancel();
        }
    }

    private void registerUser() {
        if (AppHelpers.isConnectingToInternet(RegistrationActivity.this)) {
            setupUi();
            WebApi webApi = AppHelpers.setupRetrofit();
            RegisterUserPostData data = new RegisterUserPostData(userId, name, email, password);
            call = webApi.getRegisterResponse(data);
            Log.i("req", data.toString());
            call.enqueue(new Callback<RegisterUserResponse>() {

                @Override
                public void onResponse(Call<RegisterUserResponse> call, Response<RegisterUserResponse> response) {
                    dialog.dismiss();
                    Log.i("responsecode", String.valueOf(response.code()));

                    if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                        saveEmailAndMove(response.body().getEmail());
                    } else {
                        Snackbar snackbar = Snackbar.make(etCPass, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        tvMessage.setTextColor(Color.YELLOW);
                        snackbar.show();
                    }
//                    Toast.makeText(RegistrationActivity.this, response.code() + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<RegisterUserResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.e(AppConstants.ERROR, t.getLocalizedMessage());
                }
            });
        } else {
            Snackbar snackbar = Snackbar.make(etName, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            registerUser();
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

    private void setupUi() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        dialog = AppHelpers.showProgressDialog(RegistrationActivity.this, AppConstants.REGISTERMSG);
        dialog.show();
    }

    private void saveEmailAndMove(String email) {
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shr.edit();
        editor.putString(AppConstants.EMAIL, email);
        editor.apply();

        Intent i = new Intent(RegistrationActivity.this, SelectDealsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

}
