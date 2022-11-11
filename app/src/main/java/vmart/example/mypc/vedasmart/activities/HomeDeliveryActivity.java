package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.AddressController;
import vmart.example.mypc.vedasmart.controllers.CartController;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.model.AddressList;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import static vmart.example.mypc.vedasmart.activities.MyAccountActivity.isfroAddress;

public class HomeDeliveryActivity extends AppCompatActivity {
    ImageView toolbarImage;
    TextView textHeading, spinnerText, spinnerText1;
    android.support.v7.widget.Toolbar toolbar;
    RelativeLayout relativeSummary;
    LinearLayout relativeCart;
    ImageView image;
    TextView pickupAddress;
    AlertDialog.Builder dialog;
    int position = 0;
    ArrayList<AddressList> addressLists;
    Button viewOrderSummary;
    int selectePos;
    int cartCount = 0, cartAmount = 0;
    String totalMrp, totalVmart, totalSavings = "0";
    TextView cartValue, deleverycharges, amountpayable, yoursavings, taxamount;
    String homeDelivery = "HOME DELIVERY";
    TextView homedelievry;
    TextView editChange;
    RelativeLayout relativeBack;
    float gettinglong;
    float gettinglatt;
    TextView TextDelivery;
    ///passing data to server variables
    String address;
    String pincode, landmark, city, state, country, name, phonenumber;
    String estimate = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_delivery);
        Intent intent = getIntent();
        selectePos = intent.getIntExtra("Address", -1);
        addressLists = new ArrayList<>();
        init();
    }

    public void init() {
        relativeSummary = findViewById(R.id.relativeSummary);
        relativeCart = findViewById(R.id.relativeCartValue);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        relativeBack = findViewById(R.id.relative_back);
        textHeading = findViewById(R.id.Name);
        textHeading.setVisibility(View.VISIBLE);
        textHeading.setText("Select delivery Address");
        image = findViewById(R.id.textMoreLessimg);
        spinnerText = findViewById(R.id.spinnerText);
        spinnerText1 = findViewById(R.id.spinnerText1);
        homedelievry = findViewById(R.id.textHomeDelivery);
        editChange = findViewById(R.id.edit_Change);
        pickupAddress = findViewById(R.id.pickUpLocationBox);
        viewOrderSummary = findViewById(R.id.btn_ViewOrder);
        TextDelivery = findViewById(R.id.TextDelivery);
        pickupAddress.setSelected(true);
        boolean test = true;

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
        relativeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(HomeDeliveryActivity.this, DeliveryTypeActivity.class));
            }
        });
        if (isConn()) {
            if (AddressController.getInstance().addressLists.size() > 0) {
                for (int i = 0; i < AddressController.getInstance().addressLists.size() && test; i++) {
                    if (AddressController.getInstance().addressLists.get(i).getFavourite()) {
                        pincode = AddressController.getInstance().addressLists.get(i).getPinCode();
                        name = AddressController.getInstance().addressLists.get(i).getName();
                        city = AddressController.getInstance().addressLists.get(i).getCity();
                        phonenumber = AddressController.getInstance().addressLists.get(i).getMobile();
                        landmark = AddressController.getInstance().addressLists.get(i).getLandmark();
                        country = "India";
                        state = AddressController.getInstance().addressLists.get(i).getState();
                        double dlat = AddressController.getInstance().addressLists.get(i).getLatitude();
                        double dlang = AddressController.getInstance().addressLists.get(i).getLongitude();
                        gettinglatt = (float) dlat;
                        gettinglong = (float) dlang;
                        address = AddressController.getInstance().addressLists.get(i).getName() + " " + AddressController.getInstance().addressLists.get(i).getAddress() + " " + AddressController.getInstance().addressLists.get(i).getCity() + " " + AddressController.getInstance().addressLists.get(i).getState() + " " + AddressController.getInstance().addressLists.get(i).getPinCode();
                        pickupAddress.setText(address);
                        if (i == AddressController.getInstance().addressLists.get(i).getFavourite().compareTo(test)) {
                            test = false;
                        }
                    }
                }
                if (selectePos != -1) {
                    pincode = AddressController.getInstance().addressLists.get(selectePos).getPinCode();
                    double dlat = AddressController.getInstance().addressLists.get(selectePos).getLatitude();
                    double dlang = AddressController.getInstance().addressLists.get(selectePos).getLongitude();
                    gettinglong = (float) dlang;
                    gettinglatt = (float) dlat;
                    String address = AddressController.getInstance().addressLists.get(selectePos).getName() + " " +
                            AddressController.getInstance().addressLists.get(selectePos).getAddress() + " " +
                            AddressController.getInstance().addressLists.get(selectePos).getArea()+ " " +
                            AddressController.getInstance().addressLists.get(selectePos).getLandmark() + " " +
                            AddressController.getInstance().addressLists.get(selectePos).getCity() + " " +
                            AddressController.getInstance().addressLists.get(selectePos).getState() + " " +
                            AddressController.getInstance().addressLists.get(selectePos).getPinCode();
                    pickupAddress.setText(address);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        pickupAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConn()) {
                    /*if (AddressController.getInstance().addressLists.size() == 0) {
                        startActivity(new Intent(getApplicationContext(), AddNewAddressActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                    } else {*/
                        isfroAddress = false;
                        startActivity(new Intent(getApplicationContext(), AddressActivity.class));
                   /* }*/
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewOrderSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConn()) {
                    if (pickupAddress.getText().length() == 0) {
                        Toast.makeText(HomeDeliveryActivity.this, "Select delivery Address", Toast.LENGTH_SHORT).show();
                    } else if (estimate == null) {
                        Toast.makeText(getApplicationContext(), "Location is not serviceable", Toast.LENGTH_LONG).show();
                    } else {
                        oneTimePasswordAlert(estimate);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
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
        cartValue = findViewById(R.id.cartValue);
        deleverycharges = findViewById(R.id.deliveryCharges);
        amountpayable = findViewById(R.id.amountPay);
        yoursavings = findViewById(R.id.Savings);
        taxamount = findViewById(R.id.TaxAmount);
        cartValue.setText(getResources().getString(R.string.rupee) + cartAmount + ".00");
        ////EstimateCostforApis////
        if (isConn()) {
            if (pickupAddress.getText().length() > 0) {
                orderquotationcost(13.107568f, 77.571198f, gettinglatt, gettinglong, "pickup_drop");
            } else {
                deleverycharges.setText("*");
                TextDelivery.setText("Delivery Charges*");
                amountpayable.setText(getResources().getString(R.string.rupee) + (cartAmount)+".00*");
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        yoursavings.setText(getResources().getString(R.string.rupee) + totalSavings + ".00");
        double tax = Integer.valueOf(cartAmount) * 0.03;
        double i2 = tax;
    }

    public void orderquotationcost(final float pickuplat, float pickuplang, float deliverlat, float deliverlang, String id) {
        Progress.show(HomeDeliveryActivity.this);

        Log.e("statusCode", "Call");
        Log.e("messagelat", "" + pickuplat);
        Log.e("messagelang", "" + pickuplang);
        Log.e("messagedelat", "" + deliverlat);
        Log.e("messagedellang", "" + deliverlang);
        Log.e("messageid", "" + id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApiInterface.Dunzo_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApiInterface api = retrofit.create(ServerApiInterface.class);
        Call<ResponseBody> callable = api.getWeatherReport(pickuplat, pickuplang, deliverlat, deliverlang, id);
        Log.e("weather", " " + ServerApiInterface.Dunzo_URL + "api/v1/quote?pickup_lat=" + pickuplat + "&pickup_lng="
                + pickuplang + "&drop_lat=" + deliverlat + "&drop_lng=" + deliverlang + "&category_id=" + id);

        callable.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("weather", " " + call);
                String bodyString = null;
                if (response.code() == 200) {
                    Progress.dismiss(HomeDeliveryActivity.this);
                    // Do awesome stuff
                    try {
                        bodyString = new String(response.body().bytes());
                        JSONObject reponse = new JSONObject(bodyString);
                        Log.e("estimate body",""+reponse);
                        JSONObject etaresponse = new JSONObject(String.valueOf(reponse.getJSONObject("eta")));
                        String pickup = etaresponse.getString("pickup");
                        String dropoff = etaresponse.getString("dropoff");
                        if (pickup.equals(String.valueOf(-1))){
                            estimate = null;
                            deleverycharges.setText("*");
                            TextDelivery.setText("Delivery Charges*");
                            amountpayable.setText(getResources().getString(R.string.rupee) + (cartAmount)+".00*");
                            Toast.makeText(getApplicationContext(), "Delivery partner unavaliable currently", Toast.LENGTH_LONG).show();
                        }else {
                            estimate = String.valueOf(reponse.get("estimated_price"));
                            deleverycharges.setText(getResources().getString(R.string.rupee) + estimate);
                            TextDelivery.setText("Delivery Charges  INR :" + " " + estimate);
                            int deliverychargrs = Integer.parseInt(estimate);
                            amountpayable.setText(getResources().getString(R.string.rupee) + (cartAmount + deliverychargrs));
                       }
                        Log.e("etaresponse",""+pickup+" "+dropoff);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle other response codes
                    Progress.dismiss(HomeDeliveryActivity.this);
                    deleverycharges.setText("*");
                    TextDelivery.setText("Delivery Charges*");
                    amountpayable.setText(getResources().getString(R.string.rupee) + (cartAmount)+".00*");
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        String userMessage = jsonObject.getString("message");
                        String errorCode = jsonObject.getString("code");
                        Toast.makeText(getApplicationContext(), userMessage, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Progress.dismiss(HomeDeliveryActivity.this);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void oneTimePasswordAlert(final String estimateprice) {
        final Dialog otpAlert = new Dialog(this);
        otpAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpAlert.setContentView(R.layout.activity_alertforcall);
        otpAlert.setCanceledOnTouchOutside(false);
        otpAlert.show();

        Button button = otpAlert.findViewById(R.id.btn_no);
        button.setText("Stop");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DeliveryTypeActivity.class));
            }
        });
        TextView text_info = otpAlert.findViewById(R.id.text_info);
        text_info.setText("Alert");

        TextView textView1 = otpAlert.findViewById(R.id.textView1);
        textView1.setText("To door deliver this order, additional delivery charges will be added of" + " " + estimateprice);

        final Button done = otpAlert.findViewById(R.id.btn_yes);
        done.setText("Proceed");
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpAlert.dismiss();
                Intent intent = new Intent(HomeDeliveryActivity.this, DeliveryOrderSummary.class);
                intent.putExtra("selectedDeliveryType", homeDelivery);
                intent.putExtra("pickuplocation", pickupAddress.getText().toString());
                intent.putExtra("estimatedprice", estimate);
                intent.putExtra("addressPos", selectePos);
                intent.putExtra("lat", gettinglatt);
                intent.putExtra("lon", gettinglong);
                startActivity(intent);
            }
        });
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
    protected void onResume() {
        if (AddressController.getInstance().addressLists.size() == 0) {
            pickupAddress.setText("");
        }
        super.onResume();
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), DeliveryTypeActivity.class));
    }
}

