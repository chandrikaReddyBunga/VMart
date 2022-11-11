package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.AddressController;
import vmart.example.mypc.vedasmart.controllers.CartController;
import vmart.example.mypc.vedasmart.controllers.OrdersController;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.helper.Utils;
import vmart.example.mypc.vedasmart.model.AddressList;
import vmart.example.mypc.vedasmart.model.CartList;
import vmart.example.mypc.vedasmart.model.DeliveryInfo;
import vmart.example.mypc.vedasmart.model.OrderInfo;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;

import static android.graphics.Color.parseColor;

public class PaymentResponseActivity extends AppCompatActivity {

    ImageView statusImage;
    Button continue_return;
    TextView transactionStatus, texttransactionid;
    TextView tranactionId;
    String orderId, transactionid, tranactionText;
    ServerApiInterface serverApiInterface;
    private OrderInfo orderInfo;
    String totalSavings, totalMrp, totalVmart, totalsavings = "0";
    ///passing data to server variables
    String address;
    String pincode, landmark, city, state, country, name, phonenumber;
    String randomvalue;
    float lati, longi;
    String dunzoTaskId = null;
    String delivertytype;
    private int selected;
    String merchant;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_response);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        orderInfo = (OrderInfo) bundle.getSerializable("OrderInfoData");
        orderId = intent.getStringExtra("orderId");
        transactionid = intent.getStringExtra("txnId");
        tranactionText = intent.getStringExtra("txnstauts");
        selected =  intent.getIntExtra("addresspos123", -1);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        merchant = sharedPreferences.getString("merchantId","");
        Log.e("resposneTxnDetails", " " + orderId + " " + transactionid + " " + tranactionText);

        getSaltString();
        if (AddressController.getInstance().addressLists.size() > 0) {
            for (int i = 0; i < AddressController.getInstance().addressLists.size(); i++) {
                if (AddressController.getInstance().addressLists.get(i).getFavourite()) {
                    double dlat = AddressController.getInstance().addressLists.get(i).getLatitude();
                    double dlang = AddressController.getInstance().addressLists.get(i).getLongitude();
                    lati = (float) dlat;
                    longi = (float) dlang;
                    address = AddressController.getInstance().addressLists.get(i).getAddressId();
                    landmark = AddressController.getInstance().addressLists.get(i).getLandmark();
                    city = AddressController.getInstance().addressLists.get(i).getCity();
                    state = AddressController.getInstance().addressLists.get(i).getState();
                    pincode = AddressController.getInstance().addressLists.get(i).getPinCode();
                    name = AddressController.getInstance().addressLists.get(i).getName();
                    phonenumber = AddressController.getInstance().addressLists.get(i).getMobile();
                    Log.e("setlat", "" + dlat);
                    Log.e("setlang", "" + dlang);
                    Log.e("latttttt", "" + address);
                    Log.e("land", "" + landmark);
                    Log.e("city", "" + city);
                    Log.e("st", "" + state);
                    Log.e("pin", "" + pincode);
                    Log.e("name", "" + name);
                    Log.e("phone", "" + phonenumber);
                }
            }
            if (selected != -1) {
                AddressList addressList= AddressController.getInstance().addressLists.get(selected);
                double dlat = addressList.getLatitude();
                double dlang = addressList.getLongitude();
                lati = (float) dlat;
                longi = (float) dlang;
                address = addressList.getAddressId();
                landmark = addressList.getLandmark();
                city = addressList.getCity();
                state = addressList.getState();
                pincode = addressList.getPinCode();
                name = addressList.getName();
                phonenumber = addressList.getMobile();
                Log.e("setlat", "" + dlat);
                Log.e("setlang", "" + dlang);
                Log.e("latttttt", "" + address);
                Log.e("land", "" + landmark);
                Log.e("city", "" + city);
                Log.e("st", "" + state);
                Log.e("pin", "" + pincode);
                Log.e("name", "" + name);
                Log.e("phone", "" + phonenumber);
            }
        }
        init();
        if (orderInfo != null) {
            delivertytype = orderInfo.getDeliveryType();
            Log.e("delivertytype", "" + delivertytype);
        }
        String ss = null;
        if (Utils.haveNetworkConnection(PaymentResponseActivity.this)) {
            if (delivertytype!=null && delivertytype.equalsIgnoreCase("Self Pick Up")) {
                orderConfirmationself(orderId);
            } else if (delivertytype!=null && delivertytype.equalsIgnoreCase("Home Delivery")) {
                homeProductsSendtoServer();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void init() {
        transactionStatus = findViewById(R.id.text_payment_status);
        texttransactionid = findViewById(R.id.text_transaction_id);
        tranactionId = findViewById(R.id.transactionID);
        statusImage = findViewById(R.id.image_payment_status);
        continue_return = findViewById(R.id.btn_continue_return);

        if (tranactionText != null) {
            if (tranactionText.equals("sucess")) {
                transactionStatus.setText("Your payment and order is successfull");
                statusImage.setBackgroundResource(R.drawable.icon_success);
                tranactionId.setText(transactionid);
                /*continue_return.setText("continue");
                continue_return.getBackground().setColorFilter(Color.parseColor("#18c252"), PorterDuff.Mode.MULTIPLY);
                continue_return.setTextColor(Color.parseColor("#FFFFFF"));*/
            } else {
                transactionStatus.setText("Transaction Cancelled");
                texttransactionid.setVisibility(View.GONE);
                statusImage.setBackgroundResource(R.drawable.failure_icon);
                tranactionId.setVisibility(View.GONE);
                continue_return.setText("Return");
                continue_return.setVisibility(View.VISIBLE);
                continue_return.setBackgroundColor(Color.parseColor("#18c252"));
                continue_return.setTextColor(parseColor("#FFFFFF"));
            }
        }

        continue_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "continue");
                if (tranactionText != null) {
                    if (tranactionText.equals("sucess")) {
                        //orderConfirmation(orderId);
                    } else {
                        finish();
                        startActivity(new Intent(PaymentResponseActivity.this, HomeActivity.class));
                    }
                }
                /*OrdersController.getInstance().fetchOrders(PaymentResponseActivity.this);
                CartController.getInstace().cartListArray.clear();
                startActivity(new Intent(PaymentResponseActivity.this, HomeActivity.class));*/
            }
        });
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        randomvalue = salt.toString();
        Log.e("saltStr", "" + randomvalue);
        return randomvalue;
    }

    public void homeProductsSendtoServer() {
        Log.e("sys", " Callllll");
        JSONObject main = new JSONObject();
        Progress.show(PaymentResponseActivity.this);
        try {
            main.put("request_id", randomvalue);
            JSONObject pickup_address = new JSONObject();
            JSONObject pickup_details = new JSONObject();
            pickup_details.put("lat", 13.107568);
            pickup_details.put("lng", 77.571198);
            pickup_details.put("address", pickup_address);
            main.put("pickup_details", pickup_details);

            pickup_address.put("apartment_address", "Uha Reddy #48-4-321");
            pickup_address.put("street_address_1", "Yelahanka,AmruthNagar,KodigeHalliGate BusStop,Bangalore");
            pickup_address.put("street_address_2", "Abcd");
            pickup_address.put("landmark", "Yelahanka");
            pickup_address.put("city", "Bangalore");
            pickup_address.put("state", "Karnataka");
            pickup_address.put("pincode", "560061");
            pickup_address.put("country", "India");
            Log.e("pickup", " " + pickup_address);
            ///////Dropdetailes
            JSONObject drop_details = new JSONObject();
            JSONObject drop_address = new JSONObject();
            drop_details.put("lat", lati);
            drop_details.put("lng", longi);
            drop_details.put("address", drop_address);
            main.put("drop_details", drop_details);
            drop_address.put("apartment_address", address);
            drop_address.put("street_address_1", "Abcd");
            drop_address.put("street_address _2", "Abcd");
            drop_address.put("landmark", landmark);
            drop_address.put("city", city);
            drop_address.put("state", state);
            drop_address.put("pincode", pincode);
            drop_address.put("country", "India");
            Log.e("drop_", " " + drop_address + "" + lati + " " + longi + " " + address + " " + landmark + " " + city + " " + state + " " + pincode);
            ///SenderDetailes
            JSONObject sender_details = new JSONObject();
            sender_details.put("name", "Vedas Labs");
            sender_details.put("phone_number", "9154734093");
            main.put("sender_details", sender_details);

            ////receivedetailes
            JSONObject receiver_details = new JSONObject();
            receiver_details.put("name", name);
            receiver_details.put("phone_number", phonenumber);
            main.put("receiver_details", receiver_details);
            ///array value
            String[] array = {"Household Items"};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                main.put("package_content", new JSONArray(array));
            }
            Log.e("receiver", " " + name + " " + phonenumber);
            main.put("package_approx_value", 50);
            main.put("special_instructions", "Fragile items.Handle with great care!!");
            main.put("test", true);
            Log.e("tag", main.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject sverifyObj = (JsonObject) jsonParser.parse(main.toString());
        Log.e("sverifyObj", "" + sverifyObj);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApiInterface.Dunzo_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApiInterface api = retrofit.create(ServerApiInterface.class);
        Call<ResponseBody> callable = api.taskcreating(sverifyObj);
        callable.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String statusCode = String.valueOf(response.code());
                String message = String.valueOf(response.message());

                Log.e("statusCodetest", "" + statusCode + message);
                Progress.dismiss(PaymentResponseActivity.this);
                if (!statusCode.equals(null)) {
                    if (statusCode.equals("201")) {
                        String bodyString1 = null;
                        try {
                                bodyString1 = new String(response.body().bytes());
                                Log.e("bodyString", "" + bodyString1);
                                JSONObject createtask = new JSONObject(bodyString1);
                                dunzoTaskId = String.valueOf(createtask.get("task_id"));
                                Log.e("taskid", "" + dunzoTaskId);
                            if (Utils.haveNetworkConnection(PaymentResponseActivity.this)) {
                                orderConfirmation(orderId, dunzoTaskId);
                            } else {
                                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Button ok;
                        TextView text,text_head;
                        final Dialog dialog = new Dialog(PaymentResponseActivity.this);
                        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.show();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.alter_response_payment);
                        text_head= dialog.findViewById(R.id.text_head);
                        text_head.setText("Alert");
                        text = dialog.findViewById(R.id.textcancel);
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String userMessage = jsonObject.getString("message");
                            String errorCode = jsonObject.getString("code");
                            Log.e("Error code 400",userMessage+errorCode);
                            text.setText("Your order can not be door deliver at this moment. Reason"
                                    + " " + userMessage + " " + "Go to MyOrders and proceed with refund.");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ok = dialog.findViewById(R.id.ok);
                        ok.setText("OK");
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.cancel();
                                dunzoTaskId = "Task not created";
                                Log.e("taskid", "" + dunzoTaskId);
                                if (Utils.haveNetworkConnection(PaymentResponseActivity.this)) {
                                    orderConfirm(orderId, dunzoTaskId);
                                } else {
                                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Progress.dismiss(PaymentResponseActivity.this);
            }
        });
    }

    private void orderConfirm(String orderId, String dunzoTaskId) {
        Progress.show(PaymentResponseActivity.this);
        Log.e("call", "orderMethod");
        Log.e("OrderId", " " + orderId + " " + true);
        final JSONObject jObject = new JSONObject();
        JSONObject deliveryInfoObj = new JSONObject();
        JSONArray itemsJsonArray = new JSONArray();
        JsonObject orderObj = new JsonObject();
        String refund = "No";
        if (dunzoTaskId.equals("Task not created")){
            refund = "Yes";
            try {
                DeliveryInfo deliveryInfo = orderInfo.getDeliveryInfo();
                ///deliveryInfo Obj
                deliveryInfoObj.put("name", deliveryInfo.getName());
                deliveryInfoObj.put("mobile", deliveryInfo.getMobileno());
                deliveryInfoObj.put("address", deliveryInfo.getAddress());
                deliveryInfoObj.put("pin", deliveryInfo.getPin());
                deliveryInfoObj.put("date", deliveryInfo.getDeliveryDate());
                deliveryInfoObj.put("time", deliveryInfo.getDeliveryTime());

                // for creating orderInfo Json Object
                for (int i = 0; i < CartController.getInstace().cartListArray.size(); i++) {
                    JSONObject itemsJSonObj = new JSONObject();
                    ArrayList<CartList> cartListArrayList = new ArrayList<>();
                    itemsJSonObj.put("productId", CartController.getInstace().cartListArray.get(i).getProductId());
                    itemsJSonObj.put("itemName", CartController.getInstace().cartListArray.get(i).getItemName());
                    itemsJSonObj.put("quantity", CartController.getInstace().cartListArray.get(i).getQuantity());
                    totalMrp = getTotalMrp(CartController.getInstace().cartListArray.get(i).getQuantity(),
                            CartController.getInstace().cartListArray.get(i).getMrpPrice());
                    totalVmart = getTotalVmart(CartController.getInstace().cartListArray.get(i).getQuantity(),
                            CartController.getInstace().cartListArray.get(i).getVmartPrice());
                    totalsavings = getTotalSavings(totalMrp, totalVmart);
                    Log.d("Total Savings", orderInfo.getTotalSavings());
                    itemsJSonObj.put("price", totalVmart);
                    itemsJSonObj.put("netWeight", CartController.getInstace().cartListArray.get(i).getNetWeight());
                    itemsJSonObj.put("imageUrl", CartController.getInstace().cartListArray.get(i).getUrl());
                    itemsJSonObj.put("youSave", orderInfo.getTotalSavings());
                    itemsJsonArray.put(itemsJSonObj);
                }

                jObject.put("orderid", orderId);
                jObject.put("orderStatus", true);
                jObject.put("userId", orderInfo.getUserId());
                jObject.put("totalCost", orderInfo.getTotalCost());
                jObject.put("cartTotal", orderInfo.getCartTotal());
                jObject.put("deliveryCharges", orderInfo.getDeliveryCharges());
                jObject.put("deliveryType", delivertytype);
                jObject.put("paymentType", orderInfo.getPaymentType());
                jObject.put("timeStamp", orderInfo.getTimeStamp());
                jObject.put("totalSavings", orderInfo.getTotalSavings());
                jObject.put("deliveryInfo", deliveryInfoObj);
                jObject.put("itemsInfo", itemsJsonArray);
                jObject.put("dunzoTaskId", dunzoTaskId);
                jObject.put("refund", refund);
                jObject.put("merchantId",merchant);
                JsonParser jsonParser = new JsonParser();
                orderObj = (JsonObject) jsonParser.parse(jObject.toString());
                Log.e("orderconfirmationdunzo", " " + orderObj);

                serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
                Call<ResponseBody> ordercall = serverApiInterface.orderconfirm(orderObj);
                ordercall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.body() != null) {
                            String statuscode;
                            String message;
                            String resposnearray;
                            JSONArray jsonArray;
                            JSONObject jsonObject;
                            try {
                                resposnearray = new String(response.body().bytes());
                                jsonArray = new JSONArray(resposnearray);
                                jsonObject = jsonArray.getJSONObject(0);
                                statuscode = jsonObject.getString("response");
                                message = jsonObject.getString("message");
                                Log.e("orderConfirm", " " + statuscode + " " + message);
                                if (statuscode.equals("3")) {
                                    Progress.dismiss(PaymentResponseActivity.this);
                                    OrdersController.getInstance().fetchOrders(PaymentResponseActivity.this);
                                    CartController.getInstace().cartListArray.clear();
                                    startActivity(new Intent(PaymentResponseActivity.this, HomeActivity.class));
                                } else {
                                    Progress.dismiss(PaymentResponseActivity.this);
                                    Toast.makeText(PaymentResponseActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("Failure", t.getMessage());
                        t.printStackTrace();
                        Toast.makeText(PaymentResponseActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void orderConfirmationself(String orderId) {
        Progress.show(PaymentResponseActivity.this);
        Log.e("call", "orderMethod");
        Log.e("OrderId", " " + orderId + " " + true);
        final JSONObject jObject = new JSONObject();
        JSONObject deliveryInfoObj = new JSONObject();
        JSONArray itemsJsonArray = new JSONArray();
        JsonObject orderObj = new JsonObject();
        try {
            DeliveryInfo deliveryInfo = orderInfo.getDeliveryInfo();
            ///deliveryInfo Obj
            deliveryInfoObj.put("name", deliveryInfo.getName());
            deliveryInfoObj.put("mobile", deliveryInfo.getMobileno());
            deliveryInfoObj.put("address", deliveryInfo.getAddress());
            deliveryInfoObj.put("pin", deliveryInfo.getPin());
            deliveryInfoObj.put("date", deliveryInfo.getDeliveryDate());
            deliveryInfoObj.put("time", deliveryInfo.getDeliveryTime());

            // for creating orderInfo Json Object
            for (int i = 0; i < CartController.getInstace().cartListArray.size(); i++) {
                JSONObject itemsJSonObj = new JSONObject();
                ArrayList<CartList> cartListArrayList = new ArrayList<>();
                itemsJSonObj.put("productId", CartController.getInstace().cartListArray.get(i).getProductId());
                itemsJSonObj.put("itemName", CartController.getInstace().cartListArray.get(i).getItemName());
                itemsJSonObj.put("quantity", CartController.getInstace().cartListArray.get(i).getQuantity());
                totalMrp = getTotalMrp(CartController.getInstace().cartListArray.get(i).getQuantity(),
                        CartController.getInstace().cartListArray.get(i).getMrpPrice());
                totalVmart = getTotalVmart(CartController.getInstace().cartListArray.get(i).getQuantity(),
                        CartController.getInstace().cartListArray.get(i).getVmartPrice());
                totalsavings = getTotalSavings(totalMrp, totalVmart);
                Log.d("Total Savings", orderInfo.getTotalSavings());
                itemsJSonObj.put("price", totalVmart);
                itemsJSonObj.put("netWeight", CartController.getInstace().cartListArray.get(i).getNetWeight());
                itemsJSonObj.put("imageUrl", CartController.getInstace().cartListArray.get(i).getUrl());
                itemsJSonObj.put("youSave", orderInfo.getTotalSavings());
                // itemsJSonObj.put("mrp",CartController.getInstace().cartListArray.get(i).getMrpPrice());
                itemsJsonArray.put(itemsJSonObj);
            }

            jObject.put("orderid", orderId);
            jObject.put("orderStatus", true);
            jObject.put("userId", orderInfo.getUserId());
            jObject.put("totalCost", orderInfo.getTotalCost());
            jObject.put("cartTotal", orderInfo.getCartTotal());
            jObject.put("deliveryCharges", orderInfo.getDeliveryCharges());
            jObject.put("deliveryType", delivertytype);

            jObject.put("paymentType", orderInfo.getPaymentType());
            jObject.put("timeStamp", orderInfo.getTimeStamp());
            jObject.put("totalSavings", orderInfo.getTotalSavings());
            jObject.put("deliveryInfo", deliveryInfoObj);
            jObject.put("itemsInfo", itemsJsonArray);
            jObject.put("dunzoTaskId", "");
            if (orderInfo.getEmail().equals("")){
                jObject.put("email"," ");
            }else{
                jObject.put("email",orderInfo.getEmail());
            }
            jObject.put("merchantId",merchant);
            Log.e("emailid"," "+delivertytype+orderInfo.getEmail()+orderInfo.getPaymentType()+" "+merchant);

            JsonParser jsonParser = new JsonParser();
            orderObj = (JsonObject) jsonParser.parse(jObject.toString());
            Log.e("orderdeliverytype", " " + delivertytype);
            Log.e("orderconfirmation", " " + orderObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> ordercall = serverApiInterface.orderconfirm(orderObj);
        ordercall.enqueue(new Callback<ResponseBody>() {
            @Override

            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    String statuscode;
                    String message;
                    String resposnearray;
                    JSONArray jsonArray;
                    JSONObject jsonObject;
                    try {
                        resposnearray = new String(response.body().bytes());
                        jsonArray = new JSONArray(resposnearray);
                        jsonObject = jsonArray.getJSONObject(0);
                        statuscode = jsonObject.getString("response");
                        message = jsonObject.getString("message");
                        Log.e("orderConfirm", " " + statuscode + " " + message);
                        if (statuscode.equals("3")) {
                            Progress.dismiss(PaymentResponseActivity.this);
                            OrdersController.getInstance().fetchOrders(PaymentResponseActivity.this);
                            CartController.getInstace().cartListArray.clear();
                            startActivity(new Intent(PaymentResponseActivity.this, HomeActivity.class));
                            Toast.makeText(PaymentResponseActivity.this,  "Your payment and order is successful!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Progress.dismiss(PaymentResponseActivity.this);
                            Toast.makeText(PaymentResponseActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Failure", t.getMessage());
                t.printStackTrace();
                //  Toast.makeText(PaymentResponseActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void orderConfirmation(String orderId, String dunzo) {
        Progress.show(PaymentResponseActivity.this);
        Log.e("call", "orderMethod");
        Log.e("OrderId", " " + orderId + " " + true);
        final JSONObject jObject = new JSONObject();
        JSONObject deliveryInfoObj = new JSONObject();
        JSONArray itemsJsonArray = new JSONArray();
        JsonObject orderObj = new JsonObject();
        try {
            DeliveryInfo deliveryInfo = orderInfo.getDeliveryInfo();
            ///deliveryInfo Obj
            deliveryInfoObj.put("name", deliveryInfo.getName());
            deliveryInfoObj.put("mobile", deliveryInfo.getMobileno());
            deliveryInfoObj.put("address", deliveryInfo.getAddress());
            deliveryInfoObj.put("pin", deliveryInfo.getPin());
            deliveryInfoObj.put("date", deliveryInfo.getDeliveryDate());
            deliveryInfoObj.put("time", deliveryInfo.getDeliveryTime());

            // for creating orderInfo Json Object
            for (int i = 0; i < CartController.getInstace().cartListArray.size(); i++) {
                JSONObject itemsJSonObj = new JSONObject();
                ArrayList<CartList> cartListArrayList = new ArrayList<>();
                itemsJSonObj.put("productId", CartController.getInstace().cartListArray.get(i).getProductId());
                itemsJSonObj.put("itemName", CartController.getInstace().cartListArray.get(i).getItemName());
                itemsJSonObj.put("quantity", CartController.getInstace().cartListArray.get(i).getQuantity());
                totalMrp = getTotalMrp(CartController.getInstace().cartListArray.get(i).getQuantity(),
                        CartController.getInstace().cartListArray.get(i).getMrpPrice());
                totalVmart = getTotalVmart(CartController.getInstace().cartListArray.get(i).getQuantity(),
                        CartController.getInstace().cartListArray.get(i).getVmartPrice());
                totalsavings = getTotalSavings(totalMrp, totalVmart);
                Log.d("Total Savings", orderInfo.getTotalSavings());
                itemsJSonObj.put("price", totalVmart);
                itemsJSonObj.put("netWeight", CartController.getInstace().cartListArray.get(i).getNetWeight());
                itemsJSonObj.put("imageUrl", CartController.getInstace().cartListArray.get(i).getUrl());
                itemsJSonObj.put("youSave", orderInfo.getTotalSavings());
                itemsJsonArray.put(itemsJSonObj);
            }
            jObject.put("orderid", orderId);
            jObject.put("orderStatus", true);
            jObject.put("userId", orderInfo.getUserId());
            jObject.put("totalCost", orderInfo.getTotalCost());
            jObject.put("cartTotal", orderInfo.getCartTotal());
            jObject.put("deliveryCharges", orderInfo.getDeliveryCharges());
            jObject.put("deliveryType", delivertytype);
            jObject.put("paymentType", orderInfo.getPaymentType());
            jObject.put("timeStamp", orderInfo.getTimeStamp());
            jObject.put("totalSavings", orderInfo.getTotalSavings());
            jObject.put("deliveryInfo", deliveryInfoObj);
            jObject.put("itemsInfo", itemsJsonArray);
            jObject.put("dunzoTaskId", dunzo);
            if (orderInfo.getEmail().equals("")){
                jObject.put("email"," ");
            }else{
                jObject.put("email",orderInfo.getEmail());
            }
            jObject.put("merchantId",merchant);
            Log.e("emailid"," "+orderInfo.getEmail()+" "+merchant);
            JsonParser jsonParser = new JsonParser();
            orderObj = (JsonObject) jsonParser.parse(jObject.toString());
            Log.e("orderconfirmationdunzo", " " + orderObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> ordercall = serverApiInterface.orderconfirm(orderObj);
        ordercall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    String statuscode;
                    String message;
                    String resposnearray;
                    JSONArray jsonArray;
                    JSONObject jsonObject;
                    try {
                        resposnearray = new String(response.body().bytes());
                        jsonArray = new JSONArray(resposnearray);
                        jsonObject = jsonArray.getJSONObject(0);
                        statuscode = jsonObject.getString("response");
                        message = jsonObject.getString("message");
                        Log.e("orderConfirm", " " + statuscode + " " + message);
                        if (statuscode.equals("3")) {
                            Progress.dismiss(PaymentResponseActivity.this);
                            OrdersController.getInstance().fetchOrders(PaymentResponseActivity.this);
                            CartController.getInstace().cartListArray.clear();
                            startActivity(new Intent(PaymentResponseActivity.this, HomeActivity.class));
                            Toast.makeText(PaymentResponseActivity.this,  "Your payment and order is successful!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Progress.dismiss(PaymentResponseActivity.this);
                            Toast.makeText(PaymentResponseActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Failure", t.getMessage());
                t.printStackTrace();
                Toast.makeText(PaymentResponseActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
    }
}
