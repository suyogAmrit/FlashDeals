package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.ForgotPasswordPostData;
import com.suyogindia.model.ForgotPasswordResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suyogcomputech on 14/10/16.
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    @BindView(R.id.et_dialog_password)
    EditText etEmail;
    @BindView(R.id.tv_dialog_instruction)
    TextView tvInstruction;
    @BindView(R.id.toolbar_forgot)
    Toolbar toolbar;

    ProgressDialog dialog;
    Call<ForgotPasswordResponse> call;
    SendForgotPassword taskForgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_password);
        ButterKnife.bind(this);
        toolbar.setTitle("Forgot Password");
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.btn_dialog_okay)
    void requestPassword() {
        String email = etEmail.getText().toString();
        if (AppHelpers.isValidEmail(email)) {
            etEmail.setError(null);
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            if (AppHelpers.isConnectingToInternet(ForgotPasswordActivity.this)) {
//                callWeservice(email);
                taskForgotPassword = new SendForgotPassword();
                taskForgotPassword.execute(email);
            } else {
                Snackbar snackbar = Snackbar.make(etEmail, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                        .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPassword();
                            }
                        });
                snackbar.setActionTextColor(Color.RED);
                // Changing action button text color
                View sbView = snackbar.getView();
                TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                tvMessage.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        } else {
            etEmail.setError(getString(R.string.hint_enter_email));
        }
    }

    private void callWeservice(String email) {
        dialog = AppHelpers.showProgressDialog(ForgotPasswordActivity.this, AppConstants.FORGOTPASSMSG);
        dialog.show();
        WebApi api = AppHelpers.setupRetrofit();
        call = api.getForgotPassword(new ForgotPasswordPostData(email));
        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                dialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e(AppConstants.ERROR, t.getLocalizedMessage());
                Toast.makeText(ForgotPasswordActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (taskForgotPassword != null && taskForgotPassword.getStatus() != AsyncTask.Status.FINISHED) {
            taskForgotPassword.cancel(true);
        }
    }

    private class SendForgotPassword extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = AppHelpers.showProgressDialog(ForgotPasswordActivity.this, AppConstants.FORGOTPASSMSG);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                Log.i("response", s);
                JSONObject response = new JSONObject(s);
                String status = response.getString(AppConstants.STATUS);
                Toast.makeText(ForgotPasswordActivity.this, response.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();
                if (status.equals(AppConstants.SUCESS)) {
                    finish();
                }
            } catch (NullPointerException e) {
                Toast.makeText(ForgotPasswordActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL myUrl = new URL(AppConstants.BASEURL + "forgot_password_user.php");
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                JSONObject reqJsonObject = new JSONObject();
                reqJsonObject.put(AppConstants.EMAIL, params[0]);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.getOutputStream().write(reqJsonObject.toString().getBytes());
                String response = AppHelpers.convertStreamToString(connection.getInputStream());
                return response;
            } catch (MalformedURLException e) {
                Log.e(AppConstants.ERROR, e.getLocalizedMessage());
                return null;
            } catch (SocketTimeoutException e) {
                Log.e(AppConstants.ERROR, e.getLocalizedMessage());
                return null;
            } catch (IOException e) {
                Log.e(AppConstants.ERROR, e.getLocalizedMessage());
                return null;
            } catch (JSONException e) {
                Log.e(AppConstants.ERROR, e.getLocalizedMessage());
                return null;
            }
        }
    }
}
