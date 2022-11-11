package vmart.example.mypc.vedasmart.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.CartController;
import vmart.example.mypc.vedasmart.sessions.SessionManager;

public class DeliveryTypeActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    ImageView toolbarImage;
    TextView textHeading, spinnerText, spinnerText1;
    ImageView image;
    SharedPreferences sharedPreferences;
    String mobile,merchant;
    RelativeLayout relativeSummary;
    LinearLayout relativeCart;
    LinearLayout selfpickLayout, homeDeliveryLayout;
    TextView view;
    TextView deliveychargesDes;
    TextView textsavings, savingsAmount;
    TextView cartValue;
    TextView taxtAmount;
    int cartCount = 0, cartAmount = 0;
    String totalMrp, totalVmart, totalSavings = "0";
    String pos;
    public static boolean isFromSelfpick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_type);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        mobile = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId","");
        Intent intent = new Intent();
        pos = intent.getStringExtra("locationselectedPos");
        init();
    }

    public void init() {
        relativeSummary = findViewById(R.id.relativeSummary);
        relativeCart = findViewById(R.id.relativeCartValue);
        relativeSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (relativeCart.getVisibility() == View.VISIBLE) {
                    relativeCart.setVisibility(View.GONE);
                    spinnerText.setVisibility(View.VISIBLE);
                    spinnerText1.setVisibility(View.INVISIBLE);
                    image.setImageResource(R.drawable.ic_down_red);
                } else {
                    relativeCart.setVisibility(View.VISIBLE);
                    image.setImageResource(R.drawable.ic_up_red);
                    spinnerText1.setVisibility(View.VISIBLE);
                    spinnerText.setVisibility(View.INVISIBLE);
                }
            }
        });
        toolbar = findViewById(R.id.toolbar);
        toolbarImage = findViewById(R.id.backIcon);
        setSupportActionBar(toolbar);
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            }
        });
        RelativeLayout relative_back = findViewById(R.id.relative_back);
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            }
        });
        textHeading = findViewById(R.id.Name);
        textHeading.setText("Select Delivery Type");
        textHeading.setVisibility(View.VISIBLE);
        image = findViewById(R.id.textMoreLessimg);
        spinnerText = findViewById(R.id.spinnerText);
        spinnerText1 = findViewById(R.id.spinnerText1);
        selfpickLayout = findViewById(R.id.linearSelfPickUp);
        selfpickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromSelfpick = true;
                SelfPickUpActivity.ti = null;
                Log.e("isFromSelfpick", "" + isFromSelfpick);
                Intent intent = new Intent(getApplicationContext(), SelfPickUpActivity.class);
                startActivity(intent);
            }
        });
        homeDeliveryLayout = findViewById(R.id.linearHomeDelivery);
        homeDeliveryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromSelfpick = false;
                Log.e("isfromHome", "" + isFromSelfpick);
               /* Intent intent = new Intent(getApplicationContext(), HomeDeliveryActivity.class);
                startActivity(intent);*/ //TODO LOCAL TESTING U NEED CHANGE BY OUR CONVIENCE

                // TODO...... FOR PLAYSTORE MUST AND SHOULD
                if (cartAmount >= 1500) {
                    Intent intent = new Intent(getApplicationContext(), HomeDeliveryActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeliveryTypeActivity.this);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Order value should be greater than or equal to ₹ 1500.00 for door delivery")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    dialog.cancel();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
        // TODO ON HERE MUST AND SHOULD DO IN PLAYSTORE or LOCAL ALSO USE IT.
            }
        });

        deliveychargesDes = findViewById(R.id.deliverycharges_des);
        textsavings = findViewById(R.id.textSavings);
        savingsAmount = findViewById(R.id.Savings);
        taxtAmount = findViewById(R.id.textTax);
        cartValue = findViewById(R.id.cartValue);
        view = findViewById(R.id.deliveryCharges);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deliveychargesDes.setVisibility(View.VISIBLE);
                textsavings.setVisibility(View.GONE);
                savingsAmount.setVisibility(View.GONE);
                taxtAmount.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("call", "hide");
                        deliveychargesDes.setVisibility(View.GONE);
                        textsavings.setVisibility(View.VISIBLE);
                        savingsAmount.setVisibility(View.VISIBLE);
                        taxtAmount.setVisibility(View.VISIBLE);
                    }
                }, 5000);
            }
        });
        if (CartController.getInstace().cartListArray != null && CartController.getInstace().cartListArray.size() > 0) {
            for (int i = 0; i < CartController.getInstace().cartListArray.size(); i++) {
                cartCount = cartCount + Integer.valueOf(CartController.getInstace().cartListArray.get(i).getQuantity());
                cartAmount = cartAmount + Integer.valueOf(CartController.getInstace().cartListArray.get(i).getQuantity()) * Integer.valueOf(CartController.getInstace().cartListArray.get(i).getVmartPrice());
                totalMrp = getTotalMrp(CartController.getInstace().cartListArray.get(i).getQuantity(), CartController.getInstace().cartListArray.get(i).getMrpPrice());
                totalVmart = getTotalVmart(CartController.getInstace().cartListArray.get(i).getQuantity(), CartController.getInstace().cartListArray.get(i).getVmartPrice());
                totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));
            }
        }
        cartValue.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
        savingsAmount.setText(getResources().getString(R.string.rupee) + " " + totalSavings + ".00");
        double tax = Integer.valueOf(cartAmount) * 0.03;
        double i2 = tax;
    }
    ///getting the total Mrp for adding
    public String getTotalMrp(String count, String mrp) {
        int totalmrp = Integer.valueOf(count) * Integer.valueOf(mrp);
        return String.valueOf(totalmrp);
    }
    ///getting the totalVmart for adding
    public String getTotalVmart(String count, String vmart) {
        int totalVmart = Integer.valueOf(count) * Integer.valueOf(vmart);
        return String.valueOf(totalVmart);
    }

    public String getTotalSavings(String totalMrp, String totalVmart) {
        String totalSavings = String.valueOf(Integer.valueOf(totalMrp) - Integer.valueOf(totalVmart));
        return totalSavings;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), CartActivity.class));
    }
}