package vmart.example.mypc.vedasmart.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
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
import vmart.example.mypc.vedasmart.activities.OrderInformation;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.model.DeliveryInfo;
import vmart.example.mypc.vedasmart.model.ItemsInfo;
import vmart.example.mypc.vedasmart.model.OrderInfo;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class OrdersController {
    public static OrdersController orderobj;
    public ServerApiInterface serverApiInterface;
    public ArrayList<OrderInfo> orderInfoArrayList;
    Context context;

    public static OrdersController getInstance() {
        if (orderobj == null) {
            orderobj = new OrdersController();
            orderobj.orderInfoArrayList = new ArrayList<>();
        }
        return orderobj;
    }
    public void fetchOrders(final Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("vmart", MODE_PRIVATE);
        String mobile = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        String merchant = sharedPreferences.getString("merchantId", "");
        Log.e("ordmob", "" + mobile+" "+merchant);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ServerApiInterface.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        ServerApiInterface api = retrofit.create(ServerApiInterface.class);
        Call<ResponseBody> callable = api.fetchorders(mobile,merchant);
        callable.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String statuscode, message;
                JSONArray JsonArray, orderArray, deliveryArray;
                JSONObject JsonObj;
                if (response.body() != null) {
                    String bodyString = null;
                    try {
                        bodyString = new String(response.body().bytes());
                        Log.e("fetOrderRes", " " + bodyString);
                        JsonArray = new JSONArray(bodyString);
                        JsonObj = JsonArray.getJSONObject(0);
                        statuscode = JsonObj.getString("response");
                        message = JsonObj.getString("message");
                        Log.e("orderidstatuscode", "" + statuscode);
                        if (statuscode.equals("3")) {
                            orderArray = JsonObj.getJSONArray("orderInfo");
                            Log.e("orderid", "" + orderArray);
                            orderInfoArrayList = new ArrayList<>();
                            Log.e("orderObj", " " + orderInfoArrayList);
                            for (int i = 0; i < orderArray.length(); i++) {
                                JSONObject orderObj, deliveryObj;
                                orderObj = orderArray.getJSONObject(i);
                                Log.e("orderObj", " " + orderObj);
                                OrderInfo orderInfo = new OrderInfo();
                                DeliveryInfo deliveryInfo = new DeliveryInfo();
                                orderInfo.setUserId(orderObj.getString("userId"));
                                orderInfo.setOrderId(orderObj.getString("orderId"));
                                orderInfo.setReason(orderObj.getString("reason_for_canceled_order"));
                                orderInfo.setPaymentType(orderObj.getString("paymentType"));
                                orderInfo.setDeliveryType(orderObj.getString("deliveryType"));
                                orderInfo.setTimeStamp(orderObj.getString("timeStamp"));
                                orderInfo.setTotalCost(orderObj.getString("totalCost"));
                                orderInfo.setCartTotal(orderObj.getString("cartTotal"));
                                orderInfo.setDeliveryCharges(orderObj.getString("deliveryCharges"));
                                orderInfo.setTotalSavings(orderObj.getString("totalSavings"));
                                orderInfo.setTimeStamp(orderObj.getString("timeStamp"));
                                orderInfo.setDeliveryInfo(deliveryInfo);
                                orderInfo.setOrderStatus(orderObj.getString("orderStatus"));
                                orderInfo.setDunzoTaskId(orderObj.getString("dunzoTaskId"));
                                orderInfo.setRefund(orderObj.getString("refund"));
                                Log.e("objDunzoTask", "" + orderInfo.getDunzoTaskId());
                                JSONObject obj = orderObj.getJSONObject("deliveryInfo");
                                Log.e("obj", "" + obj);
                                deliveryInfo.setAddress(obj.getString("address"));
                                deliveryInfo.setName(obj.getString("name"));
                                deliveryInfo.setMobileno(obj.getString("mobileno"));
                                deliveryInfo.setDeliveryTime(obj.getString("deliveryTime"));
                                deliveryInfo.setDeliveryDate(obj.getString("deliveryDate"));
                                deliveryInfo.setPin(obj.getString("pin"));
                                JSONArray itemArray = orderObj.getJSONArray("itemsInfo");
                                ArrayList<ItemsInfo> itemsInfoArrayList = new ArrayList<>();
                                for (int i1 = 0; i1 < itemArray.length(); i1++) {
                                    ItemsInfo itemsInfo = new ItemsInfo();
                                    JSONObject itemObj;
                                    itemObj = itemArray.getJSONObject(i1);
                                    itemsInfo.setImageUrl(itemObj.getString("imageUrl"));
                                    itemsInfo.setNetWeight(itemObj.getString("netWeight"));
                                    itemsInfo.setPrice(itemObj.getString("price"));
                                    itemsInfo.setQuantity(itemObj.getString("quantity"));
                                    itemsInfo.setProductId(itemObj.getString("productId"));
                                    itemsInfo.setItemName(itemObj.getString("itemName"));
                                    itemsInfo.setYouSave(itemObj.getString("youSave"));
                                    itemsInfoArrayList.add(itemsInfo);
                                }
                                orderInfo.setItemsInfo(itemsInfoArrayList);
                                orderInfoArrayList.add(orderInfo);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });
    }
    ////////////////////cancel the order////////////////////////////
    public void cancelOrder(final String orderID, final String reason, final Context context,String mobile,String merchant) {
        Progress.show((Activity) context);
        final JSONObject jObject = new JSONObject();
        JsonObject orderObj = new JsonObject();
        try {
            jObject.put("orderid",orderID);
            jObject.put( "reason",reason);
            jObject.put("userId",mobile);
            jObject.put("merchantId",merchant);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonParser jsonParser = new JsonParser();
        orderObj = (JsonObject) jsonParser.parse(jObject.toString());
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> cancelOrder = serverApiInterface.cancelOrderReason(orderObj);
        cancelOrder.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String statuscode, message;
                JSONArray cancelOrderresponse;
                JSONObject cancelOrderObj;
                String cancelResponse = null;
                if (response.body() != null) {
                    try {
                        cancelResponse = new String(response.body().bytes());
                        cancelOrderresponse = new JSONArray(cancelResponse);
                        cancelOrderObj = cancelOrderresponse.getJSONObject(0);
                        statuscode = cancelOrderObj.getString("response");
                        message = cancelOrderObj.getString("message");
                        if (statuscode.equals("3")) {
                            for (int i = 0; i < orderInfoArrayList.size(); i++) {
                                if (orderInfoArrayList.get(i).getOrderId().equals(orderID)) {
                                    OrderInfo orderInfoObj = orderInfoArrayList.get(i);
                                    if (orderInfoArrayList.contains(orderInfoObj)) {
                                        int index = orderInfoArrayList.indexOf(orderInfoObj);
                                        orderInfoObj.setOrderStatus("Canceled");
                                        orderInfoArrayList.set(index, orderInfoObj);
                                        Log.e("bhargaviiii"," "+index+" "+orderInfoObj+" "+orderInfoObj.getDeliveryType()+" "
                                                +orderInfoObj.getTask_id());
                                        String type=orderInfoObj.getDeliveryType();
                                        if(type.equalsIgnoreCase("Home Delivery")){
                                            Log.e("bhargaviiii"," "+orderInfoObj.getTask_id());
                                            if (orderInfoObj.getTask_id() != ("Task not created")){
                                                    cancelOrderStatusApi(orderInfoObj.getTask_id(),context);
                                            }else{
                                                Intent intent = new Intent(context, OrderInformation.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                                                context.getApplicationContext().startActivity(intent);
                                                Progress.dismiss((Activity) context);
                                            }
                                        }else if(type.equalsIgnoreCase("SELF PICK UP")){
                                            Intent intent = new Intent(context, OrderInformation.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                                            context.getApplicationContext().startActivity(intent);
                                            Progress.dismiss((Activity) context);
                                        }
                                    }
                                }
                            }
                            /*Intent intent = new Intent(context, OrderInformation.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                            context.getApplicationContext().startActivity(intent);
                            Progress.dismiss((Activity) context);*/
                        } else if (statuscode.equals("0")) {
                            Progress.dismiss((Activity) context);
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
                Progress.dismiss((Activity) context);
            }
        });
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    public void cancelOrderStatusApi(String taskid, final Context context) {
        JSONObject cancelorde = new JSONObject();
        try {
            cancelorde.put("cancellation_reason", "Changed my mind");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject cancelordeObj = (JsonObject) jsonParser.parse(cancelorde.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApiInterface.Dunzo_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApiInterface api = retrofit.create(ServerApiInterface.class);
        Call<ResponseBody> callable = api.cancelOrdersstatus(taskid, cancelordeObj);
        callable.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String statusCode = String.valueOf(response.code());
                Log.e("ordercancelCode", "" + statusCode);
                if (statusCode != null) {
                    if (statusCode.equals("204")) {
                        Log.e("ordercancel", "" + statusCode);
                        Intent intent = new Intent(context, OrderInformation.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                        context.getApplicationContext().startActivity(intent);
                        Progress.dismiss((Activity) context);
                    }else {
                        Progress.dismiss((Activity) context);
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String userMessage = jsonObject.getString("message");
                            String errorCode = jsonObject.getString("code");
                            Log.e("Error code 400",userMessage+errorCode);
                            Toast.makeText(context, userMessage, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Progress.dismiss((Activity) context);
            }
        });
    }
}