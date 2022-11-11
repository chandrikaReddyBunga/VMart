package vmart.example.mypc.vedasmart.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.sessions.SessionManager;

public class SplashScreenActivity extends AppCompatActivity {
    // Splash screen timer
    private int SPLASH_TIME_OUT = 3000;
    SharedPreferences sharedPreferences;
    String mobileNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalshscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirect();
            }
        }, SPLASH_TIME_OUT);
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    public void redirect() {
        SessionManager sessionManager = new SessionManager(SplashScreenActivity.this);
        Log.e("checkLogin", " " + sessionManager.isLoggedIn());
        if (sessionManager.isLoggedIn()) {
            sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
            String cartID = sharedPreferences.getString(LoginActivity.cartid, "");
            mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
            String merchant = sharedPreferences.getString("merchantId", "");
            Log.e("cartId", " " + cartID + " " + mobileNumber + " " + merchant);
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        finish();
    }
}