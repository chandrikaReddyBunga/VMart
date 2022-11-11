package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vmart.example.mypc.vedasmart.helper.Constants;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.OrdersController;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.model.ItemsInfo;
import vmart.example.mypc.vedasmart.model.OrderInfo;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;

public class OrderDetails extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    TextView toolTextName;
    TextView OrderNumber, purchaseDate, totalAmountPay, paymentMode, Day, Time, address, deliveryDetailsView, statusProcess;
    TextView cartTotal, Disc, DeliveryCharges, CartTotalPrice, youSavedAmount, totalAmountPaid;
    Button upDownArrow;
    ImageView back;
    RecyclerView orderList;
    RelativeLayout relativeOrderSummary;
    String ordernum, statustype;
    ArrayList<ItemsInfo> allItemslist = new ArrayList<>();
    Button cancel_order;
    ItemlistAdapter itemlistAdapter;
    static int orderPosition;
    OrderInfo orderInfo;
    private EditText text;
    private String res;
    private TextView statuss;
    private SharedPreferences sharedPreferences;
    String cartID,mobileNumber,merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        cartID = sharedPreferences.getString(LoginActivity.cartid, " ");
        mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId","");
        init();
    }

    @SuppressLint("SetTextI18n")
    public void init() {
        toolbar = findViewById(R.id.orderDetailsToolbar);
        setSupportActionBar(toolbar);
        toolTextName = findViewById(R.id.toolProductNameStart);
        toolTextName.setText("Order Details");
        toolTextName.setVisibility(View.VISIBLE);
        back = findViewById(R.id.backIcon);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), OrderInformation.class));
            }
        });
        RelativeLayout relative_back = findViewById(R.id.relative_back);
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), OrderInformation.class));
            }
        });
        statuss = findViewById(R.id.taskstatus);
        Intent intent = getIntent();
        orderPosition = intent.getIntExtra("selectPos", 0);
        ordernum = intent.getStringExtra("ordnumber");
        allItemslist = OrdersController.getInstance().orderInfoArrayList.get(orderPosition).getItemsInfo();
        orderInfo = OrdersController.getInstance().orderInfoArrayList.get(orderPosition);
        ArrayList<ItemsInfo> itemsInfo = orderInfo.getItemsInfo();
        orderList = findViewById(R.id.orderList);
        orderList.setLayoutManager(new LinearLayoutManager(this));
        itemlistAdapter = new ItemlistAdapter(OrderDetails.this, itemsInfo);
        orderList.setAdapter(itemlistAdapter);
        deliveryDetailsView = findViewById(R.id.deliveryDetails);
        OrderNumber = findViewById(R.id.OrderNumber);
        purchaseDate = findViewById(R.id.purchaseDate);
        totalAmountPay = findViewById(R.id.totalAmountPay);
        paymentMode = findViewById(R.id.paymentMode);
        Day = findViewById(R.id.Day);
        Time = findViewById(R.id.Time);
        address = findViewById(R.id.address);
        upDownArrow = findViewById(R.id.upDownArrow);
        cartTotal = findViewById(R.id.cartTotal);
        Disc = findViewById(R.id.Disc);
        DeliveryCharges = findViewById(R.id.DeliveryCharges);
        CartTotalPrice = findViewById(R.id.CartTotalPrice);
        statusProcess = findViewById(R.id.statusProcess);
        youSavedAmount = findViewById(R.id.youSavedAmount);
        totalAmountPaid = findViewById(R.id.totalAmountPaid);
        relativeOrderSummary = findViewById(R.id.relativeOrderSummary);
        relativeOrderSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderList.getVisibility() == View.VISIBLE) {
                    orderList.setVisibility(View.GONE);
                    upDownArrow.setBackgroundResource(R.drawable.ic_down_red);
                } else {
                    orderList.setVisibility(View.VISIBLE);
                    upDownArrow.setBackgroundResource(R.drawable.ic_up_red);
                }
            }
        });
        String paym = orderInfo.getPaymentType();
        long itemLong = Long.parseLong(orderInfo.getTimeStamp()) * 1000L;
        Date orderedDate = new Date(itemLong);
        String itemDateStr = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en", "US")).format(orderedDate);
        // for getting current date
        Date currentDate = Calendar.getInstance().getTime();
        // for getting difference b/w two dates in hours
        long difference = currentDate.getTime() - orderedDate.getTime();
        long diffInHours = difference / (60 * 60 * 1000);
        OrderNumber.setText(orderInfo.getOrderId());
        purchaseDate.setText(itemDateStr);
        totalAmountPay.setText(orderInfo.getTotalCost());
        if (paym.equals(Constants.payment.onl.toString())) {
            paymentMode.setText("Online");
            Log.e("ifordertype", "" + orderInfo.getPaymentType());
        }
        Day.setText(orderInfo.getDeliveryInfo().getDeliveryDate());
        Time.setText(orderInfo.getDeliveryInfo().getDeliveryTime());
        address.setText(orderInfo.getDeliveryInfo().getAddress());
        Disc.setText(getResources().getString(R.string.rupee) + " " + "0.0");
        DeliveryCharges.setText(getResources().getString(R.string.rupee) + " " + orderInfo.getDeliveryCharges());
        CartTotalPrice.setText(getResources().getString(R.string.rupee) + " " + orderInfo.getTotalCost());
        youSavedAmount.setText(getResources().getString(R.string.rupee) + " " + orderInfo.getTotalSavings());
        totalAmountPaid.setText(getResources().getString(R.string.rupee) + " " + orderInfo.getTotalCost());
        cancel_order = findViewById(R.id.cancel_order_btn);
        String refunds = orderInfo.getRefund();
        if (orderInfo.getDeliveryType().equalsIgnoreCase("Home Delivery")) {
            deliveryDetailsView.setText("Delivery Details");
            /*String s = orderInfo.getTotalCost();
            if (s.contains("INR")) {
                String[] splitStr = s.split("\\s+");
                String helpDesk = splitStr[1];
                double tat = Double.valueOf(helpDesk) - Double.valueOf(orderInfo.getDeliveryCharges());
                cartTotal.setText(getResources().getString(R.string.rupee) + " " + String.format("%.2f", tat));
            } else {
                double tat = Double.valueOf(orderInfo.getTotalCost()) - Double.valueOf(orderInfo.getDeliveryCharges());
                cartTotal.setText(getResources().getString(R.string.rupee) + " " + String.format("%.2f", tat));
            }*/
            cartTotal.setText(getResources().getString(R.string.rupee) + " " + orderInfo.getTotalCost());
            if (orderInfo.getDunzoTaskId().equals("Task not created") && orderInfo.getOrderStatus().equals("Confirmed") && refunds.equals("Yes")) {
                cancel_order.setVisibility(View.VISIBLE);
                statusProcess.setText(orderInfo.getOrderStatus());
                statuss.setVisibility(View.GONE);
                cancel_order.setText("Refund");
            } else if (orderInfo.getDunzoTaskId().equals("Task not created") && orderInfo.getOrderStatus().equals("Canceled") && refunds.equals("Yes")) {
                statusProcess.setText("Cancelled");
                cancel_order.setVisibility(View.GONE);
                statuss.setText( "Order has been cancelled.Reason:" + orderInfo.getReason());
            } else {
                if (isConn()) {
                    getOrderStatusApi(orderInfo.getDunzoTaskId(), orderInfo);
                }else {
                    statuss.setVisibility(View.GONE);
                    cancel_order.setText("CANCEL ORDER");
                    cancel_order.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            deliveryDetailsView.setText("PICK UP DETAILS");
            cartTotal.setText(getResources().getString(R.string.rupee) + " " + orderInfo.getTotalCost());
            if (orderInfo.getOrderStatus().equals("Canceled")) {
                cancel_order.setVisibility(View.GONE);
                statuss.setVisibility(View.VISIBLE);
                statuss.setText("Order has been cancelled.Reason:" + orderInfo.getReason());
                statusProcess.setText("Canceled");
            } else if (orderInfo.getOrderStatus().equals("Confirmed")) {
                statusProcess.setText("Confirmed");
                statuss.setVisibility(View.GONE);
                cancel_order.setVisibility(View.VISIBLE);
                cancel_order.setText("CANCEL ORDER");
            } else {
                if (diffInHours >= 12) {
                    if (cancel_order.getVisibility() != View.GONE) {
                        cancel_order.setVisibility(View.GONE);
                        statuss.setVisibility(View.GONE);
                        statusProcess.setText("Confirmed");
                    }
                } else {
                    statuss.setVisibility(View.GONE);
                    cancel_order.setText("CANCEL ORDER");
                    cancel_order.setVisibility(View.VISIBLE);
                }
            }
        }
        //////click on cancel order button
        cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buttonText = cancel_order.getText().toString();
                if (buttonText.equals("Refund")) {
                    if (isConn()){
                        OrdersController.getInstance().cancelOrder(orderInfo.getOrderId(), "Refund", OrderDetails.this,mobileNumber,merchant);
                    }else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    cancel_order.setVisibility(View.GONE);
                } else {
                    cancelOrderself(orderInfo.getOrderId());
                }
            }
        });
    }

    private void getOrderStatusApi(String dunzoTaskId, final OrderInfo orderInfo) {
            Progress.show(OrderDetails.this);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ServerApiInterface.Dunzo_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ServerApiInterface api = retrofit.create(ServerApiInterface.class);
            Call<ResponseBody> callable = api.getOrdersstatus(dunzoTaskId);
            callable.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Progress.dismiss(OrderDetails.this);
                    String statusCode = String.valueOf(response.code());
                    if (!statusCode.equals(null)) {
                        if (statusCode.equals("200")) {
                            String bodyString = null;
                            String status, taskid = null;
                                try {
                                    bodyString = new String(response.body().bytes());
                                    JSONObject reponse = new JSONObject(bodyString);
                                    status = String.valueOf(reponse.get("state"));
                                    taskid = String.valueOf(reponse.get("task_id"));
                                    orderInfo.setStatus(status);
                                    orderInfo.setTask_id(taskid);
                                    Log.e("bhargaviiii"," "+taskid);
                                    statustype = orderInfo.getStatus();
                                    if (orderInfo.getStatus().equalsIgnoreCase("delivered")) {
                                        statusProcess.setText("delivered");
                                        statuss.setVisibility(View.GONE);
                                        cancel_order.setVisibility(View.GONE);
                                    } else if (orderInfo.getStatus().equalsIgnoreCase("cancelled")) {
                                        statusProcess.setText("Cancelled");
                                        statuss.setText("Order has been cancelled.Reason:" + orderInfo.getReason());
                                        statuss.setVisibility(View.VISIBLE);
                                        cancel_order.setVisibility(View.GONE);
                                    } else {
                                        statusProcess.setText(statustype);
                                        statuss.setVisibility(View.GONE);
                                        cancel_order.setText("CANCEL ORDER");
                                        cancel_order.setVisibility(View.VISIBLE);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }else{
                            if (orderInfo.getOrderStatus().equals("Canceled")) {
                                cancel_order.setVisibility(View.GONE);
                                statuss.setVisibility(View.VISIBLE);
                                statuss.setText("Order has been cancelled.Reason:" + orderInfo.getReason());
                                statusProcess.setText("Canceled");
                            } else if (orderInfo.getOrderStatus().equals("Confirmed")) {
                                statusProcess.setText("Confirmed");
                                statuss.setVisibility(View.GONE);
                                cancel_order.setVisibility(View.VISIBLE);
                                cancel_order.setText("CANCEL ORDER");
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Progress.dismiss(OrderDetails.this);
                }
            });
    }

    public void cancelOrderself(final String orderID) {
        Button ok;
        TextView textt, cod;
        final Dialog dialog = new Dialog(OrderDetails.this);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.alert_cancel_order);
        textt = dialog.findViewById(R.id.text_head);
        cod = dialog.findViewById(R.id.textcancel);
        cod.setText("Please Enter the Reason for cancellation.");
        textt.setText("Alert");
        text = dialog.findViewById(R.id.edit);
        text.setHint("Reason");
        ok = dialog.findViewById(R.id.ok);
        ok.setText( "Proceed");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                res = text.getText().toString();
                if (res.length() != 0) {
                    if (isConn()) {
                        dialog.dismiss();
                        OrdersController.getInstance().cancelOrder(orderID, res, OrderDetails.this,mobileNumber,merchant);
                    }else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    text.setError("Please enter a reason");
                }
            }
        });
    }

    ///getting the total Mrp for adding
    public String getTotalMrp(String count, String mrp) {
        int totalmrp = Integer.valueOf(count) * Integer.valueOf(mrp);
        return String.valueOf(totalmrp);
    }

    public class ItemlistAdapter extends RecyclerView.Adapter<ItemlistAdapter.ViewHolder> {
        ArrayList<ItemsInfo> itemsInfo;

        public ItemlistAdapter(OrderDetails orderDetails, ArrayList<ItemsInfo> itemsInfo) {
            this.itemsInfo = itemsInfo;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ordersummaryitems, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ItemsInfo objItemsInfo = itemsInfo.get(position);
            Glide.with(getApplicationContext())
                    .load(objItemsInfo.getImageUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loadingpic1))
                    .into(holder.image);
            holder.itemQuantity.setText(objItemsInfo.getQuantity());
            holder.cat_netweight.setText(objItemsInfo.getNetWeight());
            holder.save.setVisibility(View.VISIBLE);
            holder.save.setText("You save " + getResources().getString(R.string.rupee) + " " + objItemsInfo.getYouSave()+".00");
            holder.Price.setText("Vedas Mart " + getResources().getString(R.string.rupee) + " " + objItemsInfo.getPrice()+".00");
            holder.itemName.setText(objItemsInfo.getItemName());
            holder.textcat_quantity.setText("QUANTITY");
        }

        @Override
        public int getItemCount() {
            return itemsInfo.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView itemName, itemQuantity, Price, save, textcat_quantity,cat_netweight;
            public ViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.item_Img);
                itemName = itemView.findViewById(R.id.item_Name);
                cat_netweight = itemView.findViewById(R.id.cat_netweight);
                itemQuantity = itemView.findViewById(R.id.item_quantity);
                Price = itemView.findViewById(R.id.mrp);
                save = itemView.findViewById(R.id.youSave);
                textcat_quantity = itemView.findViewById(R.id.textcat_quantity);
            }
        }
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
        startActivity(new Intent(getApplicationContext(), OrderInformation.class));

    }
}
