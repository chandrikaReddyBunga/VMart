
package vmart.example.mypc.vedasmart.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import vmart.example.mypc.vedasmart.R;


public class Progress {

    private Progress() {
    }

    private static ProgressDialog progress;

    public static void show(final Activity activity) {
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress != null) {
                    progress.dismiss();
                    progress = null;
                }
                progress = new ProgressDialog(activity, R.style.MyAlertDialogStyle);
                progress.setMessage("Loading....");
                progress.setCancelable(false);
                progress.setCanceledOnTouchOutside(false);
                progress.show();
            }
        });
    }

    public static void dismiss(final Activity activity) {
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress != null) {
                    Log.e("dismiss", "progress");
                    progress.dismiss();
                    progress = null;
                }
            }
        });
    }

}
