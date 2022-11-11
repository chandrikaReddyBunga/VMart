package vmart.example.mypc.vedasmart.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

public class SigninActivity extends AppCompatActivity {

    private TextInputEditText inputPhoneNumber;
    String phno, Numbers;
    private Button Signin;
    ServerApiInterface serverApiInterface;
    // Shared Preferences
    SharedPreferences pref;
    private static FirebaseAnalytics firebaseAnalytics;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    //key to store the cartID
    public static String cartid = "CARTID";
    private Dialog failurealert;

    ArrayList<String> result = new ArrayList<>();
    String TAG = "PhoneActivityTAG";
    Activity activity = SigninActivity.this;
    String wantPermission = Manifest.permission.READ_PHONE_STATE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private AlertDialog dialog;
    private FocusControl phoneFocus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Log.e("SignUp", "please do signIn");
        // playstore cocnern
        pref = getSharedPreferences("vmart", MODE_PRIVATE);
        editor = pref.edit();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (!checkPermission(wantPermission)) {
            requestPermission(wantPermission);
        } else {
            Log.d(TAG, "Phone number: ");
        }
        request();
        inputPhoneNumber = findViewById(R.id.input_phoneNumer);
        Signin = findViewById(R.id.phone_number_submit);
        phoneFocus = new FocusControl(inputPhoneNumber);
        inputPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(inputPhoneNumber.getText().toString().trim())) {
                    result.clear();
                    Numbers = "";
                    phoneFocus.hideKeyboard();
                    request();
                } else {
                    setSubmitEnabled(true);
                }
            }
        });
        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phno = "+91" + inputPhoneNumber.getText().toString().trim();
                if (validate()) {
                    Log.e("signup", " " + "signUp/signIn" + phno);
                    signUpSignIn(phno);
                    Toast.makeText(getApplicationContext(), "You clicked" + phno, Toast.LENGTH_SHORT).show();
                } else {
                    //do nothing
                }
            }
        });
    }

    void setSubmitEnabled(boolean enabled) {
        Signin.setEnabled(enabled);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void request() {
        List<SubscriptionInfo> subscription = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
        for (int i = 0; i < subscription.size(); i++) {
            SubscriptionInfo info = subscription.get(i);
            Log.d(TAG, "number " + info.getNumber());
            Numbers = info.getNumber();
            if (Numbers != null) {
                if (Numbers.contains("91")) {
                    result.add(Numbers.substring(Numbers.length() - 10));
                } else {
                    result.add(Numbers);
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Continue with");
        // add a list
        builder.setItems(result.toArray(new String[result.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.haveNetworkConnection(getApplicationContext())) {
                    inputPhoneNumber.setText(result.get(which));
                    phno = "+91" + result.get(which);
                    if (validate()) {
                        Log.e("signup", " " + "signUp/signIn" + phno);
                        signUpSignIn(phno);
                        Toast.makeText(getApplicationContext(), "You clicked" + phno, Toast.LENGTH_SHORT).show();
                    } else {
                        //do nothing
                    }
                } else {
                    Toast.makeText(SigninActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNeutralButton("Sign in with another number", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User pressed Cancel button. Write Logic Here
                dialog.dismiss();
                result.clear();
                Numbers = "";
                phoneFocus.showKeyboard();
            }
        });
        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            Toast.makeText(activity, "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    request();
                } else {
                    phoneFocus.showKeyboard();
                    Toast.makeText(activity, "Permission Denied. We can't get phone number.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    ///Validating the phone number
    public Boolean validate() {
        Boolean value = true;
        if (phno.length() == 0 || phno.length() < 13 || phno.length() > 13) {
            inputPhoneNumber.setError("Enter Valid PhoneNumber");
            value = false;
        }
        return value;
    }

    class FocusControl {
        static final int POST_DELAY = 250;
        private Handler handler;
        private InputMethodManager manager;
        private View focus;

        /**
         * Keyboard focus controller
         * <p>
         * Shows and hides the keyboard. Uses a runnable to do the showing as there are race
         * conditions with hiding the keyboard that this solves.
         *
         * @param focus The view element to focus and hide the keyboard from
         */
        public FocusControl(View focus) {
            handler = new Handler();
            manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            this.focus = focus;
        }

        /**
         * Focus the view and show the keyboard.
         */
        public void showKeyboard() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    focus.requestFocus();
                    manager.showSoftInput(focus, InputMethodManager.SHOW_IMPLICIT);
                }
            }, POST_DELAY);
        }

        /**
         * Hide the keyboard.
         */
        public void hideKeyboard() {
            View currentView = getCurrentFocus();
            if (currentView.equals(focus)) {
                manager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            }
        }
    }

    ////////sending the server singup/signin details to server
    public void signUpSignIn(final String phoneNumber) {
        final SessionManager sessionManager = new SessionManager(SigninActivity.this);
        Progress.show(SigninActivity.this);
        Log.e("otpDetails", " " + phoneNumber);
        String fcmToken = FcmTokenPreference.getInstance(getApplicationContext()).getFcmToken();
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String merchantId = "ID_171020192841";
        JsonObject signinObj = new JsonObject();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobileNumber", phoneNumber);
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("tokenId", fcmToken);
            jsonObject.put("merchantId", merchantId);
            JsonParser jsonParser = new JsonParser();
            signinObj = (JsonObject) jsonParser.parse(jsonObject.toString());

            //print parameter
            Log.e("otp gson.JSON:", " " + signinObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> verifytotpCall = serverApiInterface.SingnIn(signinObj);
        verifytotpCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Progress.dismiss(SigninActivity.this);

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
                        Progress.dismiss(SigninActivity.this);
                        if (statuscode.equals("3")) {
                            Log.e("call", "otpResponse");
                            sessionManager.createLoginSession(phoneNumber);
                            editor.putString(cartid, cartID);
                            editor.commit();
                            finish();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
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
                failurealert();
                Progress.dismiss(SigninActivity.this);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //failurealert.dismiss();
        dialog.dismiss();
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    public void failurealert() {
        failurealert = new Dialog(this);
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
    public void onBackPressed() {//when click on phone backbutton
        Numbers = "";
        result.clear();
        finishAffinity();
    }
}

