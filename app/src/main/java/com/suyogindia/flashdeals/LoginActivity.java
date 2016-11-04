package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.LoginPostData;
import com.suyogindia.model.RegisterUserResponse;
import com.suyogindia.model.SocialLoginPostData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suyogindia.helpers.AppHelpers.isValidEmail;

/**
 * Created by suyogcomputech on 11/10/16.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "LoginActivity";

    @BindView(R.id.toolbar_login)
    Toolbar toolbar;
    @BindView(R.id.btn_facebook)
    LinearLayout btnFb;
    @BindView(R.id.btn_google)
    LinearLayout btnGoogle;


    @BindView(R.id.et_login_email)
    EditText etEmail;
    @BindView(R.id.et_login_password)
    EditText etPassword;

    //Facebook Login
    LoginButton fbLoginButton;
    CallbackManager callbackManager;
    String userId;
    Call<RegisterUserResponse> callLogin;
    Call<RegisterUserResponse> soicialLoginCall;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
        setupFbsdk();
        setupGoogleSdk();
        getUserID();
    }

    private void getUserID() {
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        userId = shr.getString(AppConstants.USERID, AppConstants.NA);
    }

    private void setupGoogleSdk() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile()
                .build();

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void setupFbsdk() {
        FacebookSdk.sdkInitialize(this);
        fbLoginButton = new LoginButton(this);
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
//        fbLoginButton.setPublishPermissions("public_profile,email");
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Log.i("fbResult", object.toString());
                        try {
                            if (object.has(AppConstants.EMAIL)) {
                                String name = object.getString(AppConstants.NAME);
                                String email = object.getString(AppConstants.EMAIL);
                                postCredentials(userId, name, email);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", AppConstants.FBGHRAPPERMISSION);
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(AppConstants.ERROR, error.getLocalizedMessage());
            }
        });
    }

    @OnClick(R.id.btn_facebook)
    void facebookLogin() {
        fbLoginButton.performClick();

    }

    @OnClick(R.id.btn_google)
    void googleLogin() {
        // Signin button clicked
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.btn_login)
    void login() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (!isValidEmail(email)) {
            etEmail.setError(getString(R.string.hint_enter_email));
        } else if (password.equals("") || password.length() < 5) {
            etEmail.setError(null);
            etPassword.setError(getString(R.string.hint_password_error));
        } else {
            etEmail.setError(null);
            etPassword.setError(null);
            if (AppHelpers.isConnectingToInternet(LoginActivity.this))
                performLogin(email, password);
            else {
                Snackbar snackbar = Snackbar.make(etEmail, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                        .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                login();
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

    private void performLogin(String email, String password) {
        setupUI();
        WebApi api = AppHelpers.setupRetrofit();
        LoginPostData data = new LoginPostData(userId, email, password);
        callLogin = api.getLoginResponse(data);
        callLogin.enqueue(new Callback<RegisterUserResponse>() {
            @Override
            public void onResponse(Call<RegisterUserResponse> call, Response<RegisterUserResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.i("responsecode", String.valueOf(response.code()));

                    if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                        saveEmailAndMove(response.body().getEmail());
                    } else {
                        Snackbar snackbar = Snackbar.make(etPassword, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        tvMessage.setTextColor(Color.YELLOW);
                        snackbar.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterUserResponse> call, Throwable t) {
                Log.e(AppConstants.ERROR, t.getLocalizedMessage());

            }
        });

    }

    private void saveEmailAndMove(String email) {
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = shr.edit();
        editor.putString(AppConstants.EMAIL, email);
        editor.apply();

        Intent i = new Intent(LoginActivity.this, SelectDealsActivity.class);
        i.putExtra(AppConstants.FROMPROFILE, false);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void setupUI() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        dialog = AppHelpers.showProgressDialog(LoginActivity.this, AppConstants.LOGINMESSAGE);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null)
            dialog.dismiss();
    }

    @OnClick(R.id.btn_signup)
    void signUp() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.tv_forgot_password)
    void forgotPassword() {
        Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Log.i(acct.getDisplayName(), acct.getEmail());
                postCredentials(userId, acct.getDisplayName(), acct.getEmail());
            } else {
                Toast.makeText(LoginActivity.this, "Sorry. Could not get account details", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Signed out, show unauthenticated UI.

        }
    }

    private void postCredentials(String userId, String displayName, String email) {
        if (AppHelpers.isConnectingToInternet(LoginActivity.this)) {
            setupUI();
            WebApi api = AppHelpers.setupRetrofit();
            SocialLoginPostData data = new SocialLoginPostData(userId, displayName, email);
            soicialLoginCall = api.getSocialLogin(data);
            soicialLoginCall.enqueue(new Callback<RegisterUserResponse>() {
                @Override
                public void onResponse(Call<RegisterUserResponse> call, Response<RegisterUserResponse> response) {
                    if (response.isSuccessful()) {
                        Log.i("response", response.body().getMessage());

                        if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                            saveEmailAndMove(response.body().getEmail());
                        } else {
                            Snackbar snackbar = Snackbar.make(etPassword, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                            // Changing action button text color
                            View sbView = snackbar.getView();
                            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            tvMessage.setTextColor(Color.YELLOW);
                            snackbar.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RegisterUserResponse> call, Throwable t) {
                    if (dialog != null)
                        dialog.dismiss();
                    Log.e(AppConstants.ERROR, t.getLocalizedMessage());
                    Toast.makeText(LoginActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Snackbar snackbar = Snackbar.make(etEmail, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            login();
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

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (callLogin != null && !callLogin.isExecuted()) {
            callLogin.cancel();
        }
        if (soicialLoginCall != null && !soicialLoginCall.isExecuted()) {
            soicialLoginCall.cancel();
        }
    }
}
