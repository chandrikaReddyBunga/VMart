package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.HomeProductsDataController;
import vmart.example.mypc.vedasmart.sessions.SessionManager;


public class MyAccountActivity extends AppCompatActivity {
    RelativeLayout rSignOut;
    SessionManager sessionManager;
    public static Dialog dialog;
    ImageView back;
    TextView toolTextName;
    RelativeLayout relativeAddress, relativeOrder, relativeSignIn, relativeDeliveryArea, relativeWishlist;
    Toolbar toolbar;
    String mobileNumber, cartID,merchant;
    public SharedPreferences sharedPreferences;
    ProgressDialog pd;
    public  static boolean isFrommyaccount = false;
    public  static boolean isfroAddress=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        sessionManager = new SessionManager(getApplicationContext());
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.myAccountToolbar);
        setSupportActionBar(toolbar);
        back = findViewById(R.id.backIcon);
        toolTextName = findViewById(R.id.toolProductName);
        toolTextName.setText("My Account");
        toolTextName.setVisibility(View.VISIBLE);

        RelativeLayout relative_back = findViewById(R.id.relative_back);
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });

        /////getting the cartID and mobilenumber that are store in sharedpreference
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        cartID = sharedPreferences.getString(LoginActivity.cartid, " ");
        mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId","");
        Log.e("acartId", " " + cartID + " " + mobileNumber);
        relativeOrder = findViewById(R.id.relativeMyOrders);
        relativeAddress = findViewById(R.id.relativeAddress);
        rSignOut = findViewById(R.id.relativeSignout);
        relativeSignIn = findViewById(R.id.relativeSignIn);
        relativeDeliveryArea = findViewById(R.id.relativeDeliveryAreas);
        relativeWishlist = findViewById(R.id.relativeWishlist);
        if (mobileNumber.equals("")) {
            relativeSignIn.setVisibility(View.VISIBLE);
            relativeAddress.setVisibility(View.GONE);
            relativeOrder.setVisibility(View.GONE);
            rSignOut.setVisibility(View.GONE);
            relativeWishlist.setVisibility(View.GONE);
            relativeDeliveryArea.setVisibility(View.GONE);
        } else {
            relativeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isFrommyaccount = true;
                    startActivity(new Intent(getApplicationContext(), OrderInformation.class));
                }
            });

            relativeAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isfroAddress=true;
                    Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
                    startActivity(intent);
                }
            });

            rSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialogue();
                }
            });
        }

        relativeSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
            }
        });

        relativeDeliveryArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, DeliveryNewActivity.class);
                intent.putExtra("yourBoolName", false);
                startActivity(intent);

            }
        });
        relativeWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), WishListActivity.class));
            }
        });
        pd = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
    }

    public void AlertDialogue() {
        Button Cancel, Ok;
        dialog = new Dialog(MyAccountActivity.this);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_signout);
        TextView signoutText = dialog.findViewById(R.id.signoutText);
        TextView alertText = dialog.findViewById(R.id.alertText);
        alertText.setText("Alert");
        signoutText.setText("SIGN OUT");

        Cancel = dialog.findViewById(R.id.cancel);
        Ok = dialog.findViewById(R.id.ok);
        Ok.setText("OK");
        Cancel.setText("Cancel");

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConn()) {
                    if (sessionManager.isLoggedIn()) {
                        sessionManager.logoutUser();
                        dialog.cancel();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //calling this method to check the internet connection
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {    //when click on phone backbutton
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        }else {
            finish();
        }
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}
