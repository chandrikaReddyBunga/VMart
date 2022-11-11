package vmart.example.mypc.vedasmart.activities;

import android.app.Dialog;
import android.content.Intent;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.AddressController;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.helper.Utils;
import vmart.example.mypc.vedasmart.model.DeliveryInfo;
import vmart.example.mypc.vedasmart.model.OrderInfo;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;

public class PaymentModeActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView toolbarImage;
    TextView toolbarText,payonlinetext,codtext,text_info_text,text_subInfo1_text;
    RelativeLayout cod;
    RelativeLayout payonline;
    ServerApiInterface serverApiInterface;
    String userId, cartTotal, totalCost, deliveryCharges, deliverytype, paymentType, timeStamp;
    String totalSavings, name, mobile, address, pin, date, time,email;
    String totalMrp, totalVmart, totalsavings = "0";
    private OrderInfo orderInfo;
    String address1;
    String pincode, landmark, city, state, name1, phonenumber;
    float lati, longi;
    private int selectedpos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);
        Intent intent = getIntent();
        selectedpos = intent.getIntExtra("addresspos11",-1);
        userId = intent.getStringExtra("userId");
        cartTotal = intent.getStringExtra("cartTotal");
        totalCost = intent.getStringExtra("totalCost");
        deliveryCharges = intent.getStringExtra("deliveryCharges");
        deliverytype = intent.getStringExtra("deliveryType");
        paymentType = intent.getStringExtra("paymentType");
        timeStamp = intent.getStringExtra("timeStamp");
        totalSavings = intent.getStringExtra("totalSavings");
        name = intent.getStringExtra("name");
        mobile = intent.getStringExtra("mobile");
        address = intent.getStringExtra("address");
        pin = intent.getStringExtra("pin");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        email = intent.getStringExtra("email");

        DeliveryInfo deliveryInfo = new DeliveryInfo(name, address, mobile, pin, date, time);
        orderInfo = new OrderInfo(null, null, userId, paymentType, deliverytype, timeStamp,
                totalCost, deliveryCharges, totalSavings, cartTotal, deliveryInfo, null,email);
        if (AddressController.getInstance().addressLists.size() > 0) {
            for (int i = 0; i < AddressController.getInstance().addressLists.size(); i++) {
                if (AddressController.getInstance().addressLists.get(i).getFavourite()) {
                    double dlat = AddressController.getInstance().addressLists.get(i).getLatitude();
                    double dlang = AddressController.getInstance().addressLists.get(i).getLongitude();
                    lati = (float) dlat;
                    longi = (float) dlang;
                    address1 = AddressController.getInstance().addressLists.get(i).getAddressId();
                    landmark = AddressController.getInstance().addressLists.get(i).getLandmark();
                    city = AddressController.getInstance().addressLists.get(i).getCity();
                    state = AddressController.getInstance().addressLists.get(i).getState();
                    pincode = AddressController.getInstance().addressLists.get(i).getPinCode();
                    name1 = AddressController.getInstance().addressLists.get(i).getName();
                    phonenumber = AddressController.getInstance().addressLists.get(i).getMobile();
                }
            }
        }
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        toolbarImage = findViewById(R.id.backIcon);
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RelativeLayout relative_back = findViewById(R.id.relative_back);
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbarText = findViewById(R.id.toolProductNameStart);
        toolbarText.setText("Select payment mode");
        toolbarText.setVisibility(View.VISIBLE);
        payonlinetext = findViewById(R.id.payonline);
        codtext = findViewById(R.id.cod);
        text_info_text = findViewById(R.id.text_info);
        text_subInfo1_text = findViewById(R.id.text_subInfo1);
        cod = findViewById(R.id.rl_Cod);
        payonline = findViewById(R.id.rl_payOnline);
        payonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.haveNetworkConnection(PaymentModeActivity.this)) {
                    proceedTomakePayment();
                } else {
                    Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertCod();
            }
        });
    }

    public void AlertCod() {
        Button ok;
        final Dialog dialog = new Dialog(PaymentModeActivity.this);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.payment_alert);
        TextView texthead = dialog.findViewById(R.id.text_head);
        TextView codnotavailable = dialog.findViewById(R.id.textcancel);
        ok = dialog.findViewById(R.id.ok);
        texthead.setText("Not Applicable");
        codnotavailable.setText("COD not Available");
        ok.setText("OK");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    public void proceedTomakePayment() {
        Progress.show(PaymentModeActivity.this);
        JsonObject proceedObj;
        JSONObject paymentJsonObject = new JSONObject();
        try {
            paymentJsonObject.put("userId", userId);
            paymentJsonObject.put("totalCost", totalCost); // TODO playstore
            //paymentJsonObject.put("totalCost", "1"); // TODO local
            paymentJsonObject.put("paymentType", "Online");

            JsonParser jsonParser = new JsonParser();
            proceedObj = (JsonObject) jsonParser.parse(paymentJsonObject.toString());
            Log.e("proceedObjBody", " " + proceedObj);
            serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
            Call<ResponseBody> proceddCall = serverApiInterface.ordersPostingToServer(proceedObj);
            proceddCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String statuscode = null, message = null;
                    JSONArray respnseJsonArray;
                    JSONObject responseJsonObj;
                    String orderId = null, checksumhash = null;
                    if (response.body() != null) {
                        String bodyString = null;
                        try {
                            bodyString = new String(response.body().bytes());
                            Log.e("Response", " " + bodyString);
                            respnseJsonArray = new JSONArray(bodyString);
                            responseJsonObj = respnseJsonArray.getJSONObject(0);
                            statuscode = responseJsonObj.getString("response");
                            message = responseJsonObj.getString("message");
                            orderId = responseJsonObj.getString("orderId");
                            checksumhash = responseJsonObj.getString("checksumhash");
                            Log.e("responseObj", " " + statuscode + " " + message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (statuscode.equals("3")) {
                            Progress.dismiss(PaymentModeActivity.this);
                            Log.e("DeliveryType", " " + deliverytype);
                            if (Utils.haveNetworkConnection(PaymentModeActivity.this)) {
                                startTransaction(orderId, checksumhash, totalCost, userId, deliverytype);
                            } else {
                                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Progress.dismiss(PaymentModeActivity.this);
                    Log.e("failure", " " + t.getMessage());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startTransaction(String orderId, String checksumhash, String totalCost, String userId, String deliverytype) {
        PaytmPGService Service = PaytmPGService.getProductionService();
        Log.e("paytmamount", " " + totalCost + " " + userId.substring(1).trim());
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", "VMartP95907532925453");
        paramMap.put("ORDER_ID", orderId);
        paramMap.put("CUST_ID", userId.substring(1).trim());
        paramMap.put("INDUSTRY_TYPE_ID", "Retail109");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("TXN_AMOUNT", totalCost/*"1"*/); //TODO Playstore update = totalCost  and   local update = "1"
        paramMap.put("WEBSITE", "VMartP");
        paramMap.put("CHECKSUMHASH", checksumhash);
        paramMap.put("CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + orderId);

        final PaytmOrder Order = new PaytmOrder(paramMap);
        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void onBackPressedCancelTransaction() {
                        Log.e("onbackpressesTranaction", "true");
                        Toast.makeText(getApplicationContext(), "Transaction Cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        if (Utils.haveNetworkConnection(PaymentModeActivity.this)) {
                            Log.e("paymentResponse", "" + inResponse);
                            String txnResponseCode = inResponse.getString("RESPCODE");
                            String txnStatus = inResponse.getString("STATUS");
                            String txnId = inResponse.getString("TXNID");
                            String orderId = inResponse.getString("ORDERID");
                            Intent intent = new Intent(PaymentModeActivity.this, PaymentResponseActivity.class);
                            Log.e("txnDetails", " " + txnId + " " + orderId);
                            if (txnStatus.equals("TXN_SUCCESS")) {
                                Log.e("checkTxn", " " + txnId + " " + orderId);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("OrderInfoData", orderInfo);
                                intent.putExtras(bundle);
                                intent.putExtra("txnId", txnId);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("txnstauts", "sucess");
                                intent.putExtra("addresspos123", selectedpos);
                                finish();
                                startActivity(intent);
                            } else {
                                //onBackPressedCancelTransaction();
                                intent.putExtra("txnstauts", "failure");
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void networkNotAvailable() {
                        Log.e("paytmnetwork", "error");
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Log.e("clientAuthenticatio", " " + inErrorMessage);
                    }

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Log.e("errorMsg", " " + inErrorMessage);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                        Log.e("LoadingWebpage", " " + iniErrorCode + " " + inErrorMessage + " " + inFailingUrl);
                        Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Toast.makeText(getApplicationContext(), "Transaction Cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.e("ontranactionCancel", " " + inErrorMessage + " " + inResponse);
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

    ////////for unix time stamp
    public String getCurrentTime() {
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1000L);
        Log.e("attem", "" + attempt_time);
        return attempt_time;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
