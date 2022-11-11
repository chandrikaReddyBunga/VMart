package vmart.example.mypc.vedasmart.sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class PincodePreferenceManager {

    private static final String SharedPref_Name = "PincodePref's";
    private static final String PINCODE_KEY = "pincode";

    private static PincodePreferenceManager mInstance;
    private Context mContext;

    public PincodePreferenceManager(Context context) {
        mContext = context;
    }

    public static synchronized PincodePreferenceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PincodePreferenceManager(context);
        }
        return mInstance;
    }

    public boolean savePincodeToSharedPref(String pincode) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SharedPref_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PINCODE_KEY, pincode);
        editor.apply();
        return true;
    }

    public String getPincodeFromSharedPref() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SharedPref_Name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PINCODE_KEY, null);
    }
}
