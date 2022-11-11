package vmart.example.mypc.vedasmart.RECEIVER;

import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.RefWatcher;

public class MyApplication extends Application {
    public static MyApplication myApplication;
    // for memory leaks
    RefWatcher refWatcher;
    public static RefWatcher getRefWatcher(Context context)
    {

        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.refWatcher;
    }
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }
    // for internet connection
    public static synchronized MyApplication getMyApplication() { return myApplication; }
}
