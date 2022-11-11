package vmart.example.mypc.vedasmart.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.helper.Utils;
import vmart.example.mypc.vedasmart.pushnotification.FcmTokenPreference;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;
import vmart.example.mypc.vedasmart.smsreceiver.SmsReceiver;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText inputPhoneNumber, inputotp;
    Button signUp_SignIn;
    TextView getVerificationCode, skip;
    String phno;
    String otp;
    ServerApiInterface serverApiInterface;
    // Shared Preferences
    SharedPreferences pref;
    private static FirebaseAnalytics firebaseAnalytics;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    //key to store the cartID
    public static String cartid = "CARTID";
    String readOTP;
    CountDownTimer countDownTimer;
    TextView textViewShowTime;
    TextView countrycode;
    String merchant;
    SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Log.e("SignUp", "please do signIn");
        // playstore cocnern
        requestPemissionsToreadSMS();
        sessionManager = new SessionManager(LoginActivity.this);
        pref = getSharedPreferences("vmart", MODE_PRIVATE);
        //merchant = pref.getString("merchantId",""); //TODO this is the changes with different merchantid's
        merchant = "ID_29102019520"; //TODO this is the changes with different merchantid's
        editor = pref.edit();
        init();
    }

    @SuppressLint("CutPasteId")
    public void init() {
        inputPhoneNumber = findViewById(R.id.input_phoneNumer);
        inputotp = findViewById(R.id.input_verificationcode);
        textViewShowTime = findViewById(R.id.timerText);
        signUp_SignIn = findViewById(R.id.SignBtn);
        countrycode = findViewById(R.id.text_hint);
        skip = findViewById(R.id.skipText);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("merchantId", merchant);
                editor.commit();
                finish();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });

        Log.e("countrycodeLenght", " " + countrycode.getText().toString().length());
        signUp_SignIn.setOnClickListener(SignUpBtnClick);
        getVerificationCode = findViewById(R.id.text_verification);
        getVerificationCode.setOnClickListener(GetVerificationClick);
    }

    View.OnClickListener GetVerificationClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            phno = "+91" + inputPhoneNumber.getText().toString();
            if (Utils.haveNetworkConnection(getApplicationContext())) {
                if (validate()) {
                    Log.e("signup", " " + "signUp/signIn");
                    signUpSignIn(phno);
                } else {
                    //do nothing
                }
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
            }
        }
    };


    View.OnClickListener SignUpBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            phno = "+91" + inputPhoneNumber.getText().toString();
            otp = inputotp.getText().toString();
            closeKeyboard();
            if (Utils.haveNetworkConnection(getApplicationContext())) {
                if (validatephoneAndOtp()) {
                    Log.e("signup", " " + phno + " " + otp);
                    verifyOtp(phno, otp);

                } else {
                    //do nothing
                }
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
            }

        }
    };


    ///Validating the phone number
    public Boolean validate() {
        closeKeyboard();
        Boolean value = true;
        if (phno.length() == 0 || phno.length() < 13 || phno.length() > 13) {
            inputPhoneNumber.setError("Enter Valid PhoneNumber");
            value = false;
        }
        return value;
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    ////validating phonenumber and otp
    public Boolean validatephoneAndOtp() {
        Boolean value = true;
        if (phno.length() == 0 || phno.length() < 13 || phno.length() > 13) {
            inputPhoneNumber.setError("Enter Valid PhoneNumber");
            value = false;
        } else if (inputotp.length() == 0) {
            inputotp.setError("Enter OTP");
            value = false;
        }
        return value;
    }

    ////////sending the server singup/signin details to server
    public void signUpSignIn(final String phno) {
        Progress.show(LoginActivity.this);
        Log.e("phno", " " + phno);
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> signupcall = serverApiInterface.SignUpSingnIn(phno, merchant);
        signupcall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("responseBody", " " + response.body() + response.isSuccessful());
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
                        Progress.dismiss(LoginActivity.this);
                        if (statuscode.equals("3")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            // playstore concern
                            SmsReceiver.bindListener(new SmsReceiver.SmsListener() {
                                @Override
                                public void messageReceived(String messageText) {
                                    Log.e("receiveSMS", "receiveOtp");
                                    Log.e("message", messageText);
                                    readOTP = extractDigits(messageText);
                                    Log.e("readotp", " " + readOTP);
                                }
                            });
                            displayCountDownTimer(phno);
                        } else if (statuscode.equals("1")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                Log.e("error", " " + t.getMessage() + t.getCause());
                Progress.dismiss(LoginActivity.this);
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

///////////////Verifying the otp

    public void verifyOtp(final String phoneNumber, String otp) {
        Progress.show(LoginActivity.this);
        Log.e("otpDetails", " " + phoneNumber + " " + otp);
        String fcmToken = FcmTokenPreference.getInstance(getApplicationContext()).getFcmToken();
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        JsonObject otpObj = new JsonObject();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobileNumber", phoneNumber);
            jsonObject.put("otpNumber", otp);
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("tokenId", fcmToken);
            jsonObject.put("merchantId", merchant);

            JsonParser jsonParser = new JsonParser();
            otpObj = (JsonObject) jsonParser.parse(jsonObject.toString());

            //print parameter
            Log.e("otp gson.JSON:", " " + otpObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> verifytotpCall = serverApiInterface.verifyOtp(otpObj);
        verifytotpCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String statuscode, message, cartID;
                JSONArray optJsonArray;
                JSONObject otpJsonObj;
                if (response.body() != null) {
                    String bodyString = null;
                    try {
                        bodyString = new String(response.body().bytes());
                        Log.e("otpResponse", " " + bodyString);
                        optJsonArray = new JSONArray(bodyString);
                        otpJsonObj = optJsonArray.getJSONObject(0);
                        statuscode = otpJsonObj.getString("response");
                        message = otpJsonObj.getString("message");
                        cartID = otpJsonObj.getString("cartId");
                        Log.e("otpStatuscode", " " + statuscode);
                        Progress.dismiss(LoginActivity.this);
                        if (statuscode.equals("3")) {
                            textViewShowTime.setVisibility(View.GONE);
                            if (countDownTimer != null) {
                                Log.e("cancel", "sdjgfdsjfgdsj");
                                countDownTimer.cancel();
                                countDownTimer.onFinish();
                            }
                            Log.e("call", "otpResponse");
                            sessionManager.createLoginSession(phoneNumber);
                            editor.putString("merchantId", merchant);
                            editor.putString(cartid, cartID);
                            editor.commit();
                            finish();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else if (statuscode.equals("2")) {
                            textViewShowTime.setVisibility(View.GONE);
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } else if (statuscode.equals("1")) {
                            textViewShowTime.setVisibility(View.GONE);
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } else if (statuscode.equals("0")) {
                            textViewShowTime.setVisibility(View.GONE);
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Progress.dismiss(LoginActivity.this);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                textViewShowTime.setVisibility(View.GONE);
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                failurealert();
                Progress.dismiss(LoginActivity.this);
            }
        });

    }

    //////Runtime permissions to read the OTP SMS
    public void requestPemissionsToreadSMS() {
        Permissions.check(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, "Reading SMS permissions are required to auto verifying the OTP", new Permissions.Options()
                .setRationaleDialogTitle("info"), new PermissionHandler() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(LoginActivity.this, "Enter the OTP Manually", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ///extracting the otp number from otp string
    public String extractDigits(String src) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
                Log.e("builder", " " + builder);
            }
        }
        return builder.toString();
    }


    public void displayCountDownTimer(final String phonenumber) {

        countDownTimer = new CountDownTimer(30 * 1000, 500) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {

                //  playstore concern
                SmsReceiver.bindListener(new SmsReceiver.SmsListener() {
                    @Override
                    public void messageReceived(String messageText) {
                        Log.e("receiveSMS", "receiveOtp");
                        Log.e("message", messageText);
                        readOTP = extractDigits(messageText);
                        Log.e("readotp", " " + readOTP);
                    }
                });

                long seconds = leftTimeInMilliseconds / 1000;
                textViewShowTime.setVisibility(View.VISIBLE);
                textViewShowTime.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
                Log.e("timer", " " + textViewShowTime.getText().toString() + " " + readOTP);
                if (readOTP != null) {
                    countDownTimer.cancel();
                    inputotp.setText(readOTP);
                    if (Utils.haveNetworkConnection(LoginActivity.this)) {
                        verifyOtp(phonenumber, readOTP);
                    } else {
                        Toast.makeText(LoginActivity.this, "Check the network connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFinish() {
                // finish();
                textViewShowTime.setVisibility(View.GONE);
            }
        }.start();

    }


    @Override
    protected void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();

    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    public void failurealert() {
        final Dialog failurealert = new Dialog(this);
        failurealert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        failurealert.setCancelable(false);
        failurealert.setCanceledOnTouchOutside(false);
        failurealert.setCancelable(true);
        failurealert.setContentView(R.layout.failure_alert);
        failurealert.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        failurealert.show();

        TextView text = failurealert.findViewById(R.id.text_error);
        text.setText("Error");
        TextView text1 = failurealert.findViewById(R.id.requestfail);
        text1.setText("RequestFailed");
        Button cancel = failurealert.findViewById(R.id.btn_failurecancel);
        cancel.setText("Cancel");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failurealert.dismiss();

            }


        });
        Button retry = failurealert.findViewById(R.id.btn_failureretry);
        retry.setText("Retry");
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConn()) {
                    failurealert.dismiss();
                    signUpSignIn(phno);
                } else {
                    failurealert.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {    //when click on phone backbutton
        finishAffinity();
    }
}
