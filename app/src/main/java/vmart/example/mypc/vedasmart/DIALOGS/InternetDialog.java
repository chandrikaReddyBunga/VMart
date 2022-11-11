package vmart.example.mypc.vedasmart.DIALOGS;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import vmart.example.mypc.vedasmart.R;

public class InternetDialog {
    public static AlertDialog alertDialog;

    public static Dialog interNet(Context context) {
        Dialog dialog = new Dialog(context);
        // Dismiss any old dialog.
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            final FrameLayout frameLayout = new FrameLayout(context);
            alertBuilder.setView(frameLayout);
            alertDialog = alertBuilder.create();
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_goals);
          WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alertDialog.getWindow().getAttributes());
            lp.width = 150;
            lp.height = 500;
            alertDialog.getWindow().setAttributes(lp);
            LayoutInflater inflater = alertDialog.getLayoutInflater();
            final View dialogLayout = inflater.inflate(R.layout.dialog_internet, frameLayout);
            alertBuilder.setView(dialogLayout);
            Button btnOk = dialogLayout.findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            if(!((Activity) context).isFinishing()) {
                alertDialog.show();
            }
        }
        return dialog;
    }
}