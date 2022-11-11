package vmart.example.mypc.vedasmart.pushnotification;

import android.content.Context;
import android.content.SharedPreferences;

public class FcmTokenPreference {
    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String TAG_TOKEN = "tagtoken";

    private static FcmTokenPreference mInstance;
    private static Context mContext;

    public FcmTokenPreference(Context context) {
        mContext = context;
    }

    public static synchronized FcmTokenPreference getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FcmTokenPreference(context);
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean saveFcmToken(String token) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getFcmToken() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_TOKEN, null);
    }
}
