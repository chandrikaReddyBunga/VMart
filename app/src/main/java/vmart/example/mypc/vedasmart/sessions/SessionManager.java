package vmart.example.mypc.vedasmart.sessions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import vmart.example.mypc.vedasmart.activities.LoginActivity;
import vmart.example.mypc.vedasmart.activities.MerchantListActivity;
import vmart.example.mypc.vedasmart.activities.SigninActivity;
import vmart.example.mypc.vedasmart.controllers.HomeProductsDataController;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "vmart";
    //vmartApp
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_MOBILE = "mobile";
    public static final String IS_ONLINE = "false";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String mobile) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        ///storing the mobile value
        editor.putString(KEY_MOBILE, mobile);
        // commit changes
        editor.commit();
    }

    public void logoutUser() {
        editor.putBoolean(IS_LOGIN, false);
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _context.startActivity(i);
    }

    /**
     * Quick checkky for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        String mobileNumber = pref.getString(SessionManager.KEY_MOBILE, "");
        Log.e("sessionMobile", " " + mobileNumber);
        return pref.getBoolean(IS_LOGIN, false);
    }
}