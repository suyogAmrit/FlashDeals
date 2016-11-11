package com.suyogindia.flashdeals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.helpers.WebApi;
import com.suyogindia.model.Address;
import com.suyogindia.model.QuestionRequest;
import com.suyogindia.model.Result;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionaryActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.spinFoodType)
    Spinner spinFoodType;
    @BindView(R.id.spinTypeMoview)
    Spinner spinTypeMoview;
    @BindView(R.id.spinTypeCity)
    Spinner spinTypeCity;
    @BindView(R.id.etFamily)
    EditText etFamily;
    @BindView(R.id.txtHomeAddr)
    TextView txtHomeAddr;
    @BindView(R.id.txtOfficeAddr)
    TextView txtOfficeAddr;
    @BindView(R.id.etAlternaetPhone)
    EditText etAlternaetPhone;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_questionary)
    LinearLayout linearLayout;

    String[] spinFoodvalue = {"Veg", "Non-Veg"};
    String[] spinMovieValue = {"Hindi", "English"};
    String[] spincityvalue = {"N/A", "Cuttack", "Puri", "Khurda", "Pippli", "Jatni", "Dhenkanal"};
    ArrayAdapter<String> spinFoodAdapter, spinMoviewAdapter, spinCityAdapter;

    String foodString, moviewStriing, cityString;
    Address officeaddress, homeAddress = null;
    ProgressDialog dialog;
    InputMethodManager iim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionary);
        ButterKnife.bind(this);
        toolbar.setTitle("Tell us More!");
        setSupportActionBar(toolbar);
        iim = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        spinFoodAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, R.id.txtSpinnerValue, spinFoodvalue);
        spinFoodType.setAdapter(spinFoodAdapter);
        spinMoviewAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, R.id.txtSpinnerValue, spinMovieValue);
        spinTypeMoview.setAdapter(spinMoviewAdapter);
        spinCityAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, R.id.txtSpinnerValue, spincityvalue);
        spinTypeCity.setAdapter(spinCityAdapter);
        foodString = spinFoodvalue[0];
        moviewStriing = spinMovieValue[0];
        cityString = spincityvalue[0];
        spinFoodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                foodString = spinFoodType.getSelectedItem().toString();
                //foodString = parent.getItemAtPosition(position).toString();
                Log.v("FOOD", foodString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinTypeMoview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                moviewStriing = spinTypeMoview.getSelectedItem().toString();
                Log.v("Movie", moviewStriing);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinTypeCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityString = spinTypeCity.getSelectedItem().toString();
                Log.v("CITY", cityString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        txtHomeAddr.setOnClickListener(this);
        txtOfficeAddr.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_next) {
            if (validate()) {

                sedAnswers();
            }
        }
        return false;
    }

    private void sedAnswers() {
        if (AppHelpers.isConnectingToInternet(this)) {
            callWebServices();
        } else {
            Snackbar snackbar = Snackbar.make(etAlternaetPhone, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sedAnswers();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void callWebServices() {
        dialog = AppHelpers.showProgressDialog(this, AppConstants.QUESTIONDIALOG);
        dialog.show();
        String userId = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE).getString(AppConstants.USERID, AppConstants.NA);

        QuestionRequest request = new QuestionRequest(userId);
        ArrayList<Address> list = new ArrayList<>();
        if (officeaddress != null) {
            list.add(officeaddress);
        }
        if (homeAddress != null)
            list.add(homeAddress);
        ArrayList<String> listAnswrs = new ArrayList<>();
        listAnswrs.add(foodString);
        listAnswrs.add(moviewStriing);
        listAnswrs.add(cityString);
        listAnswrs.add(etFamily.getText().toString());
        listAnswrs.add("+91" + etAlternaetPhone.getText().toString());
        //TODO add all strings serially
        request.setAddressList(list);
        request.setAnswerList(listAnswrs);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASEURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(AppHelpers.getSocketTime())
                .build();
        WebApi api = retrofit.create(WebApi.class);
        //Call<Result> resultCall = AppHelpers.setupRetrofit().sendAnswers(request);
        Call<Result> resultCall = api.sendAnswers(request);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(AppConstants.SUCESS)) {
                        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = shr.edit();
                        editor.putBoolean(AppConstants.ANSWERS, true);
                        editor.apply();
                        Intent i = new Intent(QuestionaryActivity.this, SelectDealsActivity.class);
                        i.putExtra(AppConstants.FROMPROFILE, false);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(QuestionaryActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                dialog.dismiss();
                Log.e(AppConstants.ERROR, t.getLocalizedMessage());
                //Toast.makeText(QuestionaryActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validate() {

        iim.hideSoftInputFromInputMethod(etAlternaetPhone.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        if (foodString.equals("") && spinFoodType == null && spinFoodType.getSelectedItem() == null) {
            showSnackBar("Please provide Type of Food");
            return false;
        } else if (moviewStriing.equals("") && spinTypeMoview != null && spinTypeMoview.getSelectedItem() == null) {
            showSnackBar("Please provide Movie");
            return false;
        } else if (cityString.equals("") && spinTypeCity != null && spinTypeMoview.getSelectedItem() == null) {
            showSnackBar("Please provide City");
            return false;
        } else if (homeAddress == null && officeaddress == null) {
            showSnackBar("Please provide One Address");
            return false;
        } else if (!AppHelpers.isValidMobile(etAlternaetPhone.getText().toString().trim())) {
            etAlternaetPhone.setError("Please provide a valid Phone Number");
            return false;
        } else {
            etAlternaetPhone.setError(null);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_questions, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtHomeAddr) {
            Intent intent = new Intent(QuestionaryActivity.this, HomeOfficeAddressActivity.class);
            startActivityForResult(intent, AppConstants.REQUEST_CODE_HOME);
        }
        if (v.getId() == R.id.txtOfficeAddr) {
            Intent intent = new Intent(QuestionaryActivity.this, HomeOfficeAddressActivity.class);
            startActivityForResult(intent, AppConstants.REQUEST_CODE_OFFICE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_HOME) {
            if (resultCode == RESULT_OK) {
                homeAddress = data.getParcelableExtra(AppConstants.EXTRA_ADDRESS);
                if (homeAddress != null) {
                    txtHomeAddr.setVisibility(View.GONE);
                } else {
                    txtHomeAddr.setVisibility(View.VISIBLE);
                }
            }
        }
        if (requestCode == AppConstants.REQUEST_CODE_OFFICE) {
            if (resultCode == RESULT_OK) {
                officeaddress = data.getParcelableExtra(AppConstants.EXTRA_ADDRESS);
                if (officeaddress != null) {
                    txtOfficeAddr.setVisibility(View.GONE);
                } else {
                    txtOfficeAddr.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        tvMessage.setTextColor(Color.YELLOW);
        snackbar.show();
    }

}
