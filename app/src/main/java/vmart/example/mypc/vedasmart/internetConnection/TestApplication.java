package vmart.example.mypc.vedasmart.internetConnection;

import android.support.multidex.MultiDexApplication;
import io.branch.referral.Branch;

public class TestApplication extends MultiDexApplication {
    private static TestApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        // Branch logging for debugging
        Branch.enableLogging();

        // Branch object initialization
        Branch.getAutoInstance(this);

        mInstance = this;
    }

    public static synchronized TestApplication getInstance() {
        return mInstance;
    }

    public void setConnectionListener(ConnectionReceiver.ConnectionReceiverListener listener) {
        ConnectionReceiver.connectionReceiverListener = listener;
    }
}
