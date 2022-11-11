package vmart.example.mypc.vedasmart.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import vmart.example.mypc.vedasmart.helper.Constants;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.OrdersController;
import vmart.example.mypc.vedasmart.model.ItemsInfo;
import vmart.example.mypc.vedasmart.model.OrderInfo;
import static vmart.example.mypc.vedasmart.activities.MyAccountActivity.isFrommyaccount;

public class OrderInformation extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    TextView toolHeading;
    OrderAdapter orderAdapter;
    RecyclerView orderRecyclerView;
    ArrayList<OrderInfo> allOrderlist;
    ArrayList<ItemsInfo> allItemlist;
    TextView noordersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);
        OrdersController.getInstance().fetchOrders(OrderInformation.this);
        allOrderlist = new ArrayList<>();
        allItemlist = new ArrayList<>();
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.ordersInfoToolbar);
        setSupportActionBar(toolbar);
        toolHeading = findViewById(R.id.toolProductNameStart);
        toolHeading.setText("List of Orders");
        toolHeading.setVisibility(View.VISIBLE);
        RelativeLayout relative_back = findViewById(R.id.relative_back);
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFrommyaccount == true) {
                    startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }
        });
        noordersText = findViewById(R.id.text_no_orders);
        allOrderlist = OrdersController.getInstance().orderInfoArrayList;
        orderRecyclerView = findViewById(R.id.recyclerList);
        if (allOrderlist != null && allOrderlist.size() > 0) {
            orderRecyclerView.setVisibility(View.VISIBLE);
            noordersText.setVisibility(View.GONE);
        } else {
            noordersText.setVisibility(View.VISIBLE);
        }
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(OrderInformation.this, allOrderlist);
        orderRecyclerView.setAdapter(orderAdapter);
        orderAdapter.notifyDataSetChanged();
    }

    public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
        Context context;

        public OrderAdapter(OrderInformation orderInformation, ArrayList<OrderInfo> allOrderlist) {

        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.order_info_list, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final OrderInfo orderInfo = OrdersController.getInstance().orderInfoArrayList.get(position);
            holder.OrderNumber.setText(orderInfo.getOrderId());
            holder.OrderTotal.setText(orderInfo.getTotalCost());
            String paym = orderInfo.getPaymentType();
            if (paym.equals(Constants.payment.onl.toString())) {
                holder.paymentType.setText("Online");
            }
            String delivertytype = orderInfo.getDeliveryType();
            if (delivertytype.equalsIgnoreCase("Self Pick Up")) {
                holder.deliveryType.setText( "Self Pick Up");
            } else if (delivertytype.equalsIgnoreCase("Home Delivery")) {
                holder.deliveryType.setText("Home Delivery");
            }
            holder.Time.setText(orderInfo.getDeliveryInfo().getDeliveryTime());
            holder.Date.setText(orderInfo.getDeliveryInfo().getDeliveryDate());
            holder.linearOrderList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String delivertytype = orderInfo.getDeliveryType();
                    if (delivertytype.equalsIgnoreCase("Home Delivery")) {
                        String dunzotaskid = orderInfo.getDunzoTaskId();
                        if (dunzotaskid.equals("Task not created")){
                            Intent intent = new Intent(getApplicationContext(), OrderDetails.class);
                            intent.putExtra("selectPos", holder.getAdapterPosition());
                            intent.putExtra("ordnumber", (orderInfo.getOrderId()));
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), OrderDetails.class);
                            intent.putExtra("selectPos", holder.getAdapterPosition());
                            intent.putExtra("ordnumber", (orderInfo.getOrderId()));
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), OrderDetails.class);
                        intent.putExtra("selectPos", holder.getAdapterPosition());
                        intent.putExtra("ordnumber", (orderInfo.getOrderId()));
                        if (delivertytype.equals(Constants.ordertype.self.toString())) {
                            holder.deliveryType.setText("Self Pick Up");
                        } else if (delivertytype.equals(Constants.ordertype.home.toString())) {
                            holder.deliveryType.setText("Home Delivery");
                        }
                        startActivity(intent);
                    }
                }
            });

            holder.downloadInvoiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent intent = new Intent(OrderInformation.this, InvoiceDisplayActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("purchasedOrder", orderInfo);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(OrderInformation.this, "Sorry !! Device Doesn't support for invoice download", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if(allOrderlist.size()>0){
                return allOrderlist.size();
            }else{
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView OrderNumber, paymentProcess, textTime, OrderTotal, paymentType, deliveryType, Date, Time, downloadInvoiceButton, textOrderTotal, textDeliveryType, textOrderNumber;
            LinearLayout linearOrderList;

            public ViewHolder(View itemView) {
                super(itemView);
                OrderNumber = itemView.findViewById(R.id.OrderNumber);
                paymentProcess = itemView.findViewById(R.id.paymentProcess);
                OrderTotal = itemView.findViewById(R.id.OrderTotal);
                paymentType = itemView.findViewById(R.id.paymentType);
                deliveryType = itemView.findViewById(R.id.deliveryType);
                Date = itemView.findViewById(R.id.Date);
                Time = itemView.findViewById(R.id.Time);
                downloadInvoiceButton = itemView.findViewById(R.id.downloadInvoiceButton);
                linearOrderList = itemView.findViewById(R.id.linearOrderList);
                textOrderTotal = itemView.findViewById(R.id.textOrderTotal);
                textDeliveryType = itemView.findViewById(R.id.textDeliveryType);
                textTime = itemView.findViewById(R.id.textTime);
                textOrderNumber = itemView.findViewById(R.id.textOrderNumber);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFrommyaccount == true) {
            startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }
}