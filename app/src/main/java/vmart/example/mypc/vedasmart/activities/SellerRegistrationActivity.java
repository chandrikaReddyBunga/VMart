package vmart.example.mypc.vedasmart.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.helper.Utils;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;

public class SellerRegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Toolbar toolbar;
    TextView toolbarText;
    RelativeLayout backBtnLayout;
    EditText editName, editEmail, editPhone, editAccountNumber, editIfscCode, editAccountname, editGst;
    String name, email, phone, bankname, accntNumber, ifsccode, accountname, gst;
    Button registerSellerBtn;
    SharedPreferences sharedPreferences;
    String userID;
    String[] bankNames = {"Select Bank", "Andra Bank", "Bank Of Baroda Corporate", "Bank of India", "Canara Bank", "Central Bank of India", "IDBI Bank", "IDFC Bank", "Indian Bank", "Karnataka Bank", "State Bank of Hyderabad", "State Bank of India"};
    Spinner bankSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        userID = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.sellerRegtoolbar);
        setSupportActionBar(toolbar);
        toolbarText = findViewById(R.id.Name);
        toolbarText.setVisibility(View.VISIBLE);
        toolbarText.setText("Seller Registration");
        backBtnLayout = findViewById(R.id.relative_back);
        backBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        editName = findViewById(R.id.edit_seller_name);
        editEmail = findViewById(R.id.edit_seller_email);
        editPhone = findViewById(R.id.edit_seller_phone);
        editAccountNumber = findViewById(R.id.edit_account_number);
        editAccountname = findViewById(R.id.edit_seller_account_name);
        editIfscCode = findViewById(R.id.edit_seller_ifsc);
        editGst = findViewById(R.id.edit_gst);
        bankSpinner = findViewById(R.id.spinnerBank);

        /////click on spinner
        bankSpinner.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bankNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        bankSpinner.setAdapter(dataAdapter);

        registerSellerBtn = findViewById(R.id.btn_seller_reg);
        registerSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validations();
            }
        });
    }


    public void validations() {
        name = editName.getText().toString();
        email = editEmail.getText().toString();
        phone = "+91" + editPhone.getText().toString();
        accntNumber = editAccountNumber.getText().toString();
        ifsccode = editIfscCode.getText().toString();
        accountname = editAccountname.getText().toString();
        gst = editGst.getText().toString();

        if (name.length() == 0) {
            editName.setError("Name cannot be empty");
        } else if (email.length() == 0 || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter a valid email");
        } else if (phone.length() == 0 || phone.length() < 13 || phone.length() > 13) {
            editPhone.setError("enter a valid PhoneNumber");
        } else if (bankname.length() == 0) {
            Toast.makeText(SellerRegistrationActivity.this, "Select the bankname", Toast.LENGTH_SHORT).show();
        } else if (accntNumber.length() == 0) {
            editAccountNumber.setError("Enter your account number");
        } else if (ifsccode.length() == 0) {
            editIfscCode.setError("IFSC field cannot be empty");
        } else if (accountname.length() == 0) {
            editAccountname.setError("Account name cannot be empty");
        } else if (gst.length() == 0) {
            editGst.setError("GST cannot be empty");
        } else if (Utils.haveNetworkConnection(SellerRegistrationActivity.this)) {
            sellerRegistration(name, email, phone, bankname, accntNumber, ifsccode, accountname, gst, userID);
        } else {
            Toast.makeText(SellerRegistrationActivity.this, "Check the network connection", Toast.LENGTH_SHORT).show();
        }
    }


    ///////////Seller Registration API call//////////////
    public void sellerRegistration(final String name, final String email, final String phone, final String bankname, final String accntNumber, final String ifsccode, final String accountname, final String gst, final String userID) {
        JsonObject sellerRegObj = new JsonObject();
        JSONObject sellerObj = new JSONObject();
        try {
            sellerObj.put("Phone", phone);
            JsonParser jsonParser = new JsonParser();
            sellerRegObj = (JsonObject) jsonParser.parse(sellerObj.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.2.18:8080/V-Mart/")
                .addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        ServerApiInterface serverApiInterface = retrofit.create(ServerApiInterface.class);
        Call<ResponseBody> sellerRegCall = serverApiInterface.sellerRegistration(sellerRegObj);
        sellerRegCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String bodyString = null, statuscode, message, cartID;
                JSONObject jsonObject;
                JSONArray jsonArray;

                if (response.body() != null) {
                    try {
                        bodyString = new String(response.body().bytes());
                        Log.e("bodyString", " " + bodyString);
                        jsonArray = new JSONArray(bodyString);
                        jsonObject = jsonArray.getJSONObject(0);
                        statuscode = jsonObject.getString("response");
                        message = jsonObject.getString("message");

                        if (statuscode.equals("3")) {
                            Log.e("sellerReg", " " + message);
                            verifySeller(name, gst, email, phone, bankname, accntNumber, ifsccode, accountname, userID);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
             //   new AlertShowingDialog(getApplicationContext(),"No Internet Connection", "Warning","OK");

             //   Toast.makeText(SellerRegistrationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    ////////////Calling Verify Seller REgistration API//////////
    public void verifySeller(final String Name, final String GSTIN, final String Email, String Phone, final String BankName, final String AccountNumber, final String IFSC, final String AccountName, final String userID) {

        //for OTP alert dialog
        final EditText enter_otp;
        TextView btncontinue, btncancel;
        final Dialog otpdialog = new Dialog(this);
        otpdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpdialog.setCanceledOnTouchOutside(false);
        otpdialog.setCancelable(false);
        otpdialog.setContentView(R.layout.activity_otp_dialog);
        btncontinue = otpdialog.findViewById(R.id.otp_continue);
        btncancel = otpdialog.findViewById(R.id.otp_cancel);
        enter_otp = otpdialog.findViewById(R.id.edit_otp);

        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = enter_otp.getText().toString();
                JsonObject sverifyObj = new JsonObject();
                JSONObject sellerregisterverifyobj = new JSONObject();
                try {
                    sellerregisterverifyobj.put("Name", Name);
                    sellerregisterverifyobj.put("GSTIN", GSTIN);
                    sellerregisterverifyobj.put("Email", Email);
                    sellerregisterverifyobj.put("Phone", phone);
                    sellerregisterverifyobj.put("BankName", BankName);
                    sellerregisterverifyobj.put("AccountNumber", AccountNumber);
                    sellerregisterverifyobj.put("IFSC", IFSC);
                    sellerregisterverifyobj.put("AccountName", AccountName);
                    sellerregisterverifyobj.put("RegisterTime", getCurrentTime());
                    sellerregisterverifyobj.put("userId", userID);
                    sellerregisterverifyobj.put("otp", otp);

                    JsonParser jsonParser = new JsonParser();
                    sverifyObj = (JsonObject) jsonParser.parse(sellerregisterverifyobj.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("regverify", " " + sellerregisterverifyobj.toString());
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.2.18:8080/V-Mart/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ServerApiInterface api = retrofit.create(ServerApiInterface.class);
                Call<ResponseBody> calling = api.sellerVerifyOtp(sverifyObj);
                calling.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String bodyString = null, statuscode = null, message = null;
                        JSONObject jsonObject;
                        JSONArray jsonArray;

                        if (response.body() != null) {
                            try {
                                bodyString = new String(response.body().bytes());
                                Log.e("bodyString", " " + bodyString);
                                jsonArray = new JSONArray(bodyString);
                                jsonObject = jsonArray.getJSONObject(0);
                                statuscode = jsonObject.getString("response");
                                message = jsonObject.getString("message");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (statuscode.equals("3")) {
                                Log.e("sellerRegOtp", " " + message);
                                startActivity(new Intent(SellerRegistrationActivity.this, AdminProductsList.class));

                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("regverifycode", "failed");
                    }
                });

            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpdialog.dismiss();
            }
        });
        otpdialog.show();

    }


    ////////for unix time stamp
    public String getCurrentTime() {

        long unixTime;
        unixTime = System.currentTimeMillis() / 1L;
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1000L);

        Log.e("attem", "" + attempt_time);
        return attempt_time;

    }


    ////////for seleting the spinner item
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (bankSpinner.getSelectedItem() == "Select Bank") {


        } else {
            Toast.makeText(SellerRegistrationActivity.this, bankSpinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
            bankname = bankSpinner.getSelectedItem().toString();
            Log.e("bankName", " " + bankname);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }@Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();

    }
}
