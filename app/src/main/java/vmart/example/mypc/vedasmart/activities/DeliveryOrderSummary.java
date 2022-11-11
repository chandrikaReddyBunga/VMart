package vmart.example.mypc.vedasmart.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.afollestad.sectionedrecyclerview.SectionedViewHolder;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.adapter.DeliveryOrderAdapter;
import vmart.example.mypc.vedasmart.controllers.AddressController;
import vmart.example.mypc.vedasmart.controllers.CartController;
import vmart.example.mypc.vedasmart.controllers.PickupController;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;
import static vmart.example.mypc.vedasmart.activities.DeliveryTypeActivity.isFromSelfpick;

public class DeliveryOrderSummary extends AppCompatActivity {

    NestedScrollView scrollView;
    Toolbar toolbar;
    TextView toolbarTitle, selectedDeliveryType, textDeliveryDate, textDeliveryTime;
    String selectedType, location, time, price,land;
    DeliveryOrderAdapter deliveryOrderAdapter;
    RecyclerView orderSummaryRecyclerview;
    int selectedPos;
    Button proceedTopayment;
    int cartCount = 0, cartAmount = 0;
    String totalMrp, totalVmart, totalSavings = "0";
    TextView cartPrice, deliveryCharges, amountpayable, totalMrpsavings;
    TextView name, address, landmark, deliveryDate, deliveryTime, textName, textlandMark,selfname,textselfname;
    TextView noofItemsincart, totalQuantity;
    int tquantity = 0;
    SharedPreferences sharedPreferences;
    String mobileNumber,merchant;
    String pinnumber;
    // Dialog dialog;
    AlertDialog.Builder dialog;
    ServerApiInterface serverApiInterface;
    RelativeLayout backbutton;
    String SelfType;
    String deliveryMobileNumber;
    public static String Day1, Day2, Day3, Day4, Day5, Day6, Day7;
    public static String Date1, Date2, Date3, Date4, Date5, Date6, Date7;
    ArrayList<String> ouputdate = new ArrayList<>();
    ArrayList<String> ouputday = new ArrayList<>();
    TextView change;
    ArrayList<String> timeslot = new ArrayList<>();
    int valid = 0;
    int compare1, compare2, compare3, compare4;
    int slotsBlocked = 0;
    String[] dateTimeArray;
    private String editfield;
    private TextView textAddress;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_order_summary);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId","");
        Intent intent = getIntent();
        SelfType = intent.getStringExtra("selectedDeliveryType");
        location = intent.getStringExtra("pickuplocation");
        time = intent.getStringExtra("pickuptime");
        land = intent.getStringExtra("pickuplandmark");
        price = intent.getStringExtra("estimatedprice");
        selectedPos = intent.getIntExtra("addressPos", -1);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void init() {
        timeslot.add("12:00 PM - 03:00 PM");
        timeslot.add("03:00 PM - 06:00 PM");
        timeslot.add("06:00 PM - 09:00 PM");
        scrollView = findViewById(R.id.ScrollView);
        scrollView.scrollTo(0, 0);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        backbutton = findViewById(R.id.relative_back);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbarTitle = findViewById(R.id.Name);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText("Order Summary");
        textName = findViewById(R.id.textName);
        textlandMark = findViewById(R.id.textLandmark);
        selectedDeliveryType = findViewById(R.id.selectedDeliveryType);
        if (SelfType.equalsIgnoreCase("Self Pick Up")) {
            selectedDeliveryType.setText(SelfType);
        } else if (SelfType.equalsIgnoreCase("Home Delivery")) {
            selectedDeliveryType.setText(SelfType);
        }

        orderSummaryRecyclerview = findViewById(R.id.productsRecycler);
        orderSummaryRecyclerview.setLayoutManager(new LinearLayoutManager(DeliveryOrderSummary.this));
        orderSummaryRecyclerview.setNestedScrollingEnabled(false);

        deliveryOrderAdapter = new DeliveryOrderAdapter(DeliveryOrderSummary.this, CartController.getInstace().cartListArray);
        orderSummaryRecyclerview.setAdapter(deliveryOrderAdapter);

        proceedTopayment = findViewById(R.id.ProceedPaymentBtn);
        proceedTopayment.setOnClickListener(proceedButtonClick);

        cartPrice = findViewById(R.id.cartPrice);
        deliveryCharges = findViewById(R.id.deliveryChargesPrice);
        amountpayable = findViewById(R.id.payablePrice);
        totalMrpsavings = findViewById(R.id.YourSavingsPrice);

        name = findViewById(R.id.deliveryAddName);
        selfname = findViewById(R.id.textselfName);
        textselfname = findViewById(R.id.deliveryselfAddName);
        textAddress= findViewById(R.id.textAddress);
        address = findViewById(R.id.Address);
        landmark = findViewById(R.id.Landmark);
        deliveryTime = findViewById(R.id.DeliveryTime);
        deliveryDate = findViewById(R.id.Deliverydate);
        textDeliveryDate = findViewById(R.id.textDeliveryDate);
        textDeliveryTime = findViewById(R.id.textDeliveryTime);
        noofItemsincart = findViewById(R.id.numberOfItemsinCart);
        noofItemsincart.setText(String.valueOf(CartController.getInstace().cartListArray.size()));
        totalQuantity = findViewById(R.id.Quantity);
        change = findViewById(R.id.changeButton);

        if (isFromSelfpick == true) {
            change.setVisibility(View.VISIBLE);
        } else {
            change.setVisibility(View.GONE);
        }
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogue();
            }
        });
        datesCheck();
        daysCheck();

        if (SelfType == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else if (SelfType.equalsIgnoreCase("Self Pick Up")) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) address.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.textselfName);
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) textAddress.getLayoutParams();
            params1.addRule(RelativeLayout.BELOW, R.id.textselfName);
            address.setText(location);
            name.setVisibility(View.GONE);
            selfname.setVisibility(View.VISIBLE);
            textselfname.setVisibility(View.VISIBLE);
            textselfname.setText(land);
            landmark.setVisibility(View.GONE);
            textName.setVisibility(View.GONE);
            textlandMark.setVisibility(View.GONE);
            dateTimeArray = time.trim().split("\\s*,\\s*");
            deliveryTime.setText(dateTimeArray[0]);
            deliveryDate.setText(dateTimeArray[1] + " " + dateTimeArray[2]);
            pinnumber = PickupController.getInstance().pickuplistsArrayList.get(0).getPincode();
            deliveryMobileNumber = "";
            deliveryCharges.setText("INR 0.00");
            Log.e("number", "" + pinnumber);
        } else if (SelfType.equals("HOME DELIVERY")) {
            Log.e("homedelivery", "HOME DELIVERY");
            selfname.setVisibility(View.GONE);
            textselfname.setVisibility(View.GONE);
            if (AddressController.getInstance().addressLists.size() > 0) {
                for (int i = 0; i < AddressController.getInstance().addressLists.size(); i++) {
                    if (AddressController.getInstance().addressLists.get(i).getFavourite()) {
                        name.setText(AddressController.getInstance().addressLists.get(i).getName());
                        Log.e("name", " " + AddressController.getInstance().addressLists.get(i).getName() + " " + AddressController.getInstance().addressLists.get(i).getAddress()+" "+ AddressController.getInstance().addressLists.get(i).getArea());
                        address.setText(AddressController.getInstance().addressLists.get(i).getAddress() +" "
                                + AddressController.getInstance().addressLists.get(i).getArea()+ " "
                                + AddressController.getInstance().addressLists.get(i).getCity() + " "
                                + AddressController.getInstance().addressLists.get(i).getState() + " "
                                + AddressController.getInstance().addressLists.get(i).getPinCode());
                        landmark.setText(AddressController.getInstance().addressLists.get(i).getLandmark());
                        pinnumber = AddressController.getInstance().addressLists.get(i).getPinCode();
                        deliveryMobileNumber = AddressController.getInstance().addressLists.get(i).getMobile();
                        textDeliveryDate.setVisibility(View.GONE);
                        textDeliveryTime.setVisibility(View.GONE);
                    }
                }
                if (selectedPos != -1) {
                    Log.e("setSelectedAddress", "true");
                    name.setVisibility(View.VISIBLE);
                    name.setText(AddressController.getInstance().addressLists.get(selectedPos).getName());
                    Log.e("deliveryAddress", " " + AddressController.getInstance().addressLists.get(selectedPos).getAddress());
                    address.setText(AddressController.getInstance().addressLists.get(selectedPos).getAddress() +" "+ AddressController.getInstance().addressLists.get(selectedPos).getArea()
                            + " " + AddressController.getInstance().addressLists.get(selectedPos).getCity() + AddressController.getInstance().addressLists.get(selectedPos).getState() + AddressController.getInstance().addressLists.get(selectedPos).getPinCode());
                    landmark.setText(AddressController.getInstance().addressLists.get(selectedPos).getLandmark());
                    pinnumber = AddressController.getInstance().addressLists.get(selectedPos).getPinCode();
                    deliveryMobileNumber = AddressController.getInstance().addressLists.get(selectedPos).getMobile();
                    textDeliveryDate.setVisibility(View.GONE);
                    textDeliveryTime.setVisibility(View.GONE);
                }
                deliveryCharges.setText(getResources().getString(R.string.rupee) + " " + price);
            }
        }

        for (int i = 0; i < CartController.getInstace().cartListArray.size(); i++) {

            cartCount = cartCount + Integer.valueOf(CartController.getInstace().cartListArray.get(i).getQuantity());
            cartAmount = cartAmount + Integer.valueOf(CartController.getInstace().cartListArray.get(i).getQuantity()) * Integer.valueOf(CartController.getInstace().cartListArray.get(i).getVmartPrice());
            tquantity = tquantity + Integer.valueOf(CartController.getInstace().cartListArray.get(i).getQuantity());
            totalMrp = getTotalMrp(CartController.getInstace().cartListArray.get(i).getQuantity(), CartController.getInstace().cartListArray.get(i).getMrpPrice());
            totalVmart = getTotalVmart(CartController.getInstace().cartListArray.get(i).getQuantity(), CartController.getInstace().cartListArray.get(i).getVmartPrice());
            totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));
        }
        int amount = Integer.parseInt(price);

        totalQuantity.setText(String.valueOf(tquantity));

        double tax = Integer.valueOf(cartAmount) * 0.03;
        double i2 = Double.parseDouble(String.valueOf(tax + cartAmount));
        double i3 = Double.parseDouble(String.valueOf(tax + cartAmount + amount));

        cartPrice.setText(getResources().getString(R.string.rupee) + new DecimalFormat("##.00").format(i2));

        deliveryCharges.setText(getResources().getString(R.string.rupee) + " " + price);
        amountpayable.setText(getResources().getString(R.string.rupee) + " " + new DecimalFormat("##.00").format(i3));

        totalMrpsavings.setText(getResources().getString(R.string.rupee) + " " + totalSavings);
        if (SelfType != null) {
            if (SelfType.equals("Self Pick Up")) {
                deliveryCharges.setText("INR 0.00");
                amountpayable.setText(getResources().getString(R.string.rupee) + " " + new DecimalFormat("##.00").format(i2));
            }
        }
        getCurrentTimeSlots();
    }

    public void getCurrentTimeSlots() {
        Log.e("method", "CALL");

        String slota = "12:00 PM";
        String slotb = "15:00 PM";
        String slotc = "18:00 PM";
        String slotd = "21:00 PM";
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        Date c = Calendar.getInstance().getTime();
        String currentTime = sdf1.format(c);
        Log.e("currentTime", "string format:" + currentTime);
        try {
            Date date1 = sdf1.parse(slota);
            String slot1Time = sdf1.format(date1);
            Log.e("slot1Time", "string format:" + slot1Time);
            Date date2 = sdf1.parse(slotb);
            String slot2Time = sdf1.format(date2);
            Log.e("slot2Time", "string format" + slot2Time);
            Date date3 = sdf1.parse(slotc);
            String slot3Time = sdf1.format(date3);
            Log.e("slot3Time", "string format" + slot3Time);
            Date date4 = sdf1.parse(slotd);
            String slot4Time = sdf1.format(date4);
            Log.e("slot4Time", "string format" + slot4Time);
            compare1 = currentTime.compareTo(slot1Time);
            compare2 = currentTime.compareTo(slot2Time);
            compare3 = currentTime.compareTo(slot3Time);
            compare4 = currentTime.compareTo(slot4Time);
            Log.e("Compare", "Compare1 value is:" + compare1);
            Log.e("Compare", "Compare2 value is:" + compare2);
            Log.e("Compare", "Compare3 value is:" + compare3);
            Log.e("Compare", "Compare4 value is:" + compare4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener proceedButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isConn()) {
                EmailAlert();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void EmailAlert() {
        final Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.alert_email_proceed);
        d.setCanceledOnTouchOutside(false);
        d.show();

        Button button = d.findViewById(R.id.btn_no);
        final EditText edit = d.findViewById(R.id.edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setText("");
                editfield = "";
                d.dismiss();
                Intent intent = new Intent(DeliveryOrderSummary.this, PaymentModeActivity.class);
                intent.putExtra("userId", mobileNumber);
                intent.putExtra("cartTotal", cartPrice.getText().toString().substring(1).trim());
                intent.putExtra("totalCost", amountpayable.getText().toString().substring(1).trim());
                if (deliveryCharges.getText().toString().trim().equals("INR 0.00")) {
                    intent.putExtra("deliveryCharges", deliveryCharges.getText().toString().trim());
                } else {
                    intent.putExtra("deliveryCharges", deliveryCharges.getText().toString().substring(1).trim());
                }
                intent.putExtra("deliveryType", selectedDeliveryType.getText().toString().trim());
                intent.putExtra("paymentType", "Online");
                intent.putExtra("timeStamp", getCurrentTime());
                intent.putExtra("totalSavings", totalMrpsavings.getText().toString().substring(1).trim());
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("mobile", deliveryMobileNumber);
                intent.putExtra("address", address.getText());
                intent.putExtra("pin", pinnumber);
                intent.putExtra("addresspos11", selectedPos);
                intent.putExtra("date", deliveryDate.getText().toString().trim());
                intent.putExtra("time", deliveryTime.getText().toString().trim());
                intent.putExtra("pin", pinnumber);
                intent.putExtra("email", editfield);
                Log.e("sellllll", " " + selectedPos + " " + deliveryTime.getText().toString().trim() + " " + editfield);
                startActivity(intent);
                Log.e("setdeliver", "" + selectedDeliveryType.getText().toString().trim() + " " + editfield);
            }
        });
        final Button done = d.findViewById(R.id.btn_yes);
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit.getText().toString().trim().matches(emailPattern)) {
                    editfield = edit.getText().toString().trim();
                    d.dismiss();
                    Intent intent = new Intent(DeliveryOrderSummary.this, PaymentModeActivity.class);
                    intent.putExtra("userId", mobileNumber);
                    intent.putExtra("cartTotal", cartPrice.getText().toString().substring(1).trim());
                    intent.putExtra("totalCost", amountpayable.getText().toString().substring(1).trim());
                    if (deliveryCharges.getText().toString().trim().equals("INR 0.00")) {
                        intent.putExtra("deliveryCharges", deliveryCharges.getText().toString().trim());
                    } else {
                        intent.putExtra("deliveryCharges", deliveryCharges.getText().toString().substring(1).trim());
                    }
                    intent.putExtra("deliveryType", selectedDeliveryType.getText().toString().trim());
                    intent.putExtra("paymentType", "Online");
                    intent.putExtra("timeStamp", getCurrentTime());
                    intent.putExtra("totalSavings", totalMrpsavings.getText().toString().substring(1).trim());
                    intent.putExtra("name", name.getText().toString());
                    intent.putExtra("mobile", deliveryMobileNumber);
                    intent.putExtra("address", address.getText());
                    intent.putExtra("pin", pinnumber);
                    intent.putExtra("addresspos11", selectedPos);
                    intent.putExtra("date", deliveryDate.getText().toString().trim());
                    intent.putExtra("time", deliveryTime.getText().toString().trim());
                    intent.putExtra("pin", pinnumber);
                    intent.putExtra("email", editfield);
                    Log.e("sellllll", " " + selectedPos + " " + deliveryTime.getText().toString().trim() + " " + editfield);
                    startActivity(intent);
                    Log.e("setdeliver", "" + selectedDeliveryType.getText().toString().trim() + " " + editfield);
                } else if (edit.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email ID is empty", Toast.LENGTH_SHORT).show();
                } else {
                    edit.setError("Enter valid email-id");
                }
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

    public void daysCheck() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        for (int i = 0; i < 8; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i);
            String day = sdf.format(calendar.getTime());
            ouputdate.add(day);
            Log.e("Days", "" + day);
        }
    }

    public void datesCheck() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE",new Locale("en", "US"));
        for (int i = 0; i < 8; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i);
            String day = sdf.format(calendar.getTime());
            ouputday.add(day);
            Log.e("Days", "" + day);
        }
    }

    public void AlertDialogue() {
        if (isConn()) {
            dialog = new AlertDialog.Builder(DeliveryOrderSummary.this);

            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_timeslot, null);

            // Set the custom layout as alert dialog view
            dialog.setView(dialogView);
            DateAdaper dateAdaper;
            RecyclerView recyclerView;
            TextView textTitle = dialogView.findViewById(R.id.textTitle);
            TextView textTimeSlotText = dialogView.findViewById(R.id.textTimeSlotText);

            textTitle.setText("Please Select a Time Slot");
            textTimeSlotText.setText( "Request you to be on time to collect your order, Missing your pickup time, might lead to order cancellation.");

            recyclerView = dialogView.findViewById(R.id.timeSlotRecyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Create the alert dialog
            final AlertDialog builder = dialog.create();
            dateAdaper = new DateAdaper(builder);
            recyclerView.setAdapter(dateAdaper);
            builder.show();

        }
    }

    public class DateAdaper extends SectionedRecyclerViewAdapter<DateAdaper.MainVH> {
        AlertDialog dialog;

        public DateAdaper(AlertDialog builder) {
            this.dialog = builder;
        }

        @Override
        public int getSectionCount() {
            return 7;
        }

        @Override
        public int getItemCount(int section) {
            return 1;
        }

        @Override
        public void onBindHeaderViewHolder(final MainVH holder, int section, boolean expanded) {
            switch (section) {
                case 0:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day1 = holder.day.getText().toString();
                    Date1 = holder.Date.getText().toString();
                    Log.e("Day1", "" + Day1);
                    break;
                case 1:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day2 = holder.day.getText().toString();
                    Date2 = holder.Date.getText().toString();
                    Log.e("Day2", "" + Day2);
                    break;
                case 2:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day3 = holder.day.getText().toString();
                    Date3 = holder.Date.getText().toString();
                    Log.e("Day3", "" + Day3);
                    break;
                case 3:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day4 = holder.day.getText().toString();
                    Date4 = holder.Date.getText().toString();
                    Log.e("Day4", "" + Day4);
                    break;
                case 4:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day5 = holder.day.getText().toString();
                    Date5 = holder.Date.getText().toString();
                    Log.e("Day5", "" + Day5);
                    break;
                case 5:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day6 = holder.day.getText().toString();
                    Date6 = holder.Date.getText().toString();
                    Log.e("Day6", "" + Day6);
                    break;
                case 6:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day7 = holder.day.getText().toString();
                    Date7 = holder.Date.getText().toString();
                    Log.e("Day7", "" + Day7);
                    break;
            }
        }

        @Override
        public void onBindFooterViewHolder(MainVH holder, int section) { }

        @Override
        public void onBindViewHolder(final MainVH holder, final int section, final int relativePosition, final int absolutePosition) {
            holder.unavailable1.setText("Unavailable");
            holder.unavailable2.setText("Unavailable");
            holder.unavailable3.setText("Unavailable");
            holder.unavailable1.setVisibility(View.INVISIBLE);
            holder.unavailable2.setVisibility(View.INVISIBLE);
            holder.unavailable3.setVisibility(View.INVISIBLE);
            switch (section) {
                case 0:
                    if (compare1 < 0) {
                        slotsBlocked = 0;
                    } else {
                        if (compare1 > 0) {
                            slotsBlocked = 1;
                            if (compare2 > 0) {
                                slotsBlocked = 2;
                                if (compare3 > 0) {
                                    slotsBlocked = 3;
                                }
                            }
                        }
                    }
                    if (slotsBlocked == 0) {
                        // all available
                    } else if (slotsBlocked == 1) {
                        //first slot not available
                        //remaining slots available
                        holder.unavailable1.setVisibility(View.VISIBLE);
                        holder.unavailable2.setVisibility(View.INVISIBLE);
                        holder.unavailable3.setVisibility(View.INVISIBLE);
                    } else if (slotsBlocked == 2) {
                        //first and seconf slot not available
                        //remaining slots available
                        holder.unavailable1.setVisibility(View.VISIBLE);
                        holder.unavailable2.setVisibility(View.VISIBLE);
                        holder.unavailable3.setVisibility(View.INVISIBLE);
                    } else if (slotsBlocked == 3) {
                        //all slots unavailable
                        holder.unavailable1.setVisibility(View.VISIBLE);
                        holder.unavailable2.setVisibility(View.VISIBLE);
                        holder.unavailable3.setVisibility(View.VISIBLE);
                    } else {
                        //all slots unavailable
                        holder.unavailable1.setVisibility(View.VISIBLE);
                        holder.unavailable2.setVisibility(View.VISIBLE);
                        holder.unavailable3.setVisibility(View.VISIBLE);
                    }
                    holder.timing1.setText(timeslot.get(0));
                    holder.timing2.setText(timeslot.get(1));
                    holder.timing3.setText(timeslot.get(2));
                    break;
                case 1:
                    holder.timing1.setText(timeslot.get(0));
                    holder.timing2.setText(timeslot.get(1));
                    holder.timing3.setText(timeslot.get(2));
                    break;
                case 2:
                    holder.timing1.setText(timeslot.get(0));
                    holder.timing2.setText(timeslot.get(1));
                    holder.timing3.setText(timeslot.get(2));
                    break;
                case 3:
                    holder.timing1.setText(timeslot.get(0));
                    holder.timing2.setText(timeslot.get(1));
                    holder.timing3.setText(timeslot.get(2));
                    break;
                case 4:
                    holder.timing1.setText(timeslot.get(0));
                    holder.timing2.setText(timeslot.get(1));
                    holder.timing3.setText(timeslot.get(2));
                    break;
                case 5:
                    holder.timing1.setText(timeslot.get(0));
                    holder.timing2.setText(timeslot.get(1));
                    holder.timing3.setText(timeslot.get(2));
                    break;
                case 6:
                    holder.timing1.setText(timeslot.get(0));
                    holder.timing2.setText(timeslot.get(1));
                    holder.timing3.setText(timeslot.get(2));
                    break;
            }

            holder.rTime1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Crashlytics.getInstance().crash(); // Force a crash
                    switch (section) {
                        case 0:
                            if (slotsBlocked == 0) {
                                String one = holder.timing1.getText().toString();
                                Log.e("timing1", "" + one);
                                dialog.dismiss();
                                deliveryTime.setText(one);
                                deliveryDate.setText(Day1 + " " + Date1);
                            } else if (/*slotsBlocked == 1*/slotsBlocked >= 0) {
                                Log.e("timing1", "calllll");
                                dialog.dismiss();
                                deliveryTime.setText(dateTimeArray[0]);
                                Toast.makeText(DeliveryOrderSummary.this, "Unavailable", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            String two = holder.timing1.getText().toString();
                            Log.e("tim1", "" + two);
                            dialog.dismiss();
                            deliveryTime.setText(two);
                            deliveryDate.setText(Day2 + " " + Date2);
                            break;
                        case 2:
                            String three = holder.timing1.getText().toString();
                            Log.e("tim1", "" + three);
                            dialog.dismiss();
                            deliveryTime.setText(three);
                            deliveryDate.setText(Day3 + " " + Date3);
                            break;
                        case 3:
                            String four = holder.timing1.getText().toString();
                            Log.e("tim1", "" + four);
                            dialog.dismiss();
                            deliveryTime.setText(four);
                            deliveryDate.setText(Day4 + " " + Date4);
                            break;
                        case 4:
                            String five = holder.timing1.getText().toString();
                            Log.e("tim1", "" + five);
                            dialog.dismiss();
                            deliveryTime.setText(five);
                            deliveryDate.setText(Day5 + " " + Date5);
                            break;
                        case 5:
                            String six = holder.timing1.getText().toString();
                            Log.e("tim1", "" + six);
                            dialog.dismiss();
                            deliveryTime.setText(six);
                            deliveryDate.setText(Day6 + " " + Date6);
                            break;
                        case 6:
                            String seven = holder.timing1.getText().toString();
                            Log.e("tim1", "" + seven);
                            dialog.dismiss();
                            deliveryTime.setText(seven);
                            deliveryDate.setText(Day7 + " " + Date7);
                    }
                }

            });

            holder.rTime2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (section) {
                        case 0:
                            if (slotsBlocked == 0) {
                                String one = holder.timing2.getText().toString();
                                Log.e("timing1", "" + one);
                                dialog.dismiss();
                                deliveryTime.setText(one);
                                deliveryDate.setText(Day1 + " " + Date1);
                            } else if (slotsBlocked == 1) {
                                String one = holder.timing2.getText().toString();
                                Log.e("timing1", "" + one);
                                dialog.dismiss();
                                deliveryTime.setText(one);
                                deliveryDate.setText(Day1 + " " + Date1);
                            } else if (/*slotsBlocked == 2*/slotsBlocked >= 1) {
                                Log.e("timing1", "calllll");
                                dialog.dismiss();
                                deliveryTime.setText(dateTimeArray[0]);
                                Toast.makeText(DeliveryOrderSummary.this, "Unavailable", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            String two = holder.timing2.getText().toString();
                            Log.e("tim1", "" + two);
                            dialog.dismiss();
                            deliveryTime.setText(two);
                            deliveryDate.setText(Day2 + " " + Date2);
                            break;
                        case 2:
                            String three = holder.timing2.getText().toString();
                            Log.e("tim1", "" + three);
                            dialog.dismiss();
                            deliveryTime.setText(three);
                            deliveryDate.setText(Day3 + " " + Date3);
                            break;
                        case 3:
                            String four = holder.timing2.getText().toString();
                            Log.e("tim1", "" + four);
                            dialog.dismiss();
                            deliveryTime.setText(four);
                            deliveryDate.setText(Day4 + " " + Date4);
                            break;
                        case 4:
                            String five = holder.timing2.getText().toString();
                            Log.e("tim1", "" + five);
                            dialog.dismiss();
                            deliveryTime.setText(five);
                            deliveryDate.setText(Day5 + " " + Date5);
                            break;
                        case 5:
                            String six = holder.timing2.getText().toString();
                            Log.e("tim1", "" + six);
                            dialog.dismiss();
                            deliveryTime.setText(six);
                            deliveryDate.setText(Day6 + " " + Date6);
                            break;
                        case 6:
                            String seven = holder.timing2.getText().toString();
                            Log.e("tim1", "" + seven);
                            dialog.dismiss();
                            deliveryTime.setText(seven);
                            deliveryDate.setText(Day7 + " " + Date7);
                    }
                }
            });
            holder.rTime3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (section) {
                        case 0:
                            if (slotsBlocked == 0) {
                                String one = holder.timing3.getText().toString();
                                Log.e("timing1", "" + one);
                                dialog.dismiss();
                                deliveryTime.setText(one);
                                deliveryDate.setText(Day1 + " " + Date1);
                            } else if (slotsBlocked == 1) {
                                String one = holder.timing3.getText().toString();
                                Log.e("timing1", "" + one);
                                dialog.dismiss();
                                deliveryTime.setText(one);
                                deliveryDate.setText(Day1 + " " + Date1);
                            } else if (slotsBlocked == 2) {
                                String one = holder.timing3.getText().toString();
                                Log.e("timing1", "" + one);
                                dialog.dismiss();
                                deliveryTime.setText(one);
                                deliveryDate.setText(Day1 + " " + Date1);
                            } else if (slotsBlocked == 3) {
                                Log.e("timing1", "calllll");
                                dialog.dismiss();
                                deliveryTime.setText(dateTimeArray[0]);
                                Toast.makeText(DeliveryOrderSummary.this, "Unavailable", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            String two = holder.timing3.getText().toString();
                            Log.e("tim1", "" + two);
                            dialog.dismiss();
                            deliveryTime.setText(two);
                            deliveryDate.setText(Day2 + " " + Date2);
                            break;
                        case 2:
                            String three = holder.timing3.getText().toString();
                            Log.e("tim1", "" + three);
                            dialog.dismiss();
                            deliveryTime.setText(three);
                            deliveryDate.setText(Day3 + " " + Date3);
                            break;
                        case 3:
                            String four = holder.timing3.getText().toString();
                            Log.e("tim1", "" + four);
                            dialog.dismiss();
                            deliveryTime.setText(four);
                            deliveryDate.setText(Day4 + " " + Date4);
                            break;
                        case 4:
                            String five = holder.timing3.getText().toString();
                            Log.e("tim1", "" + five);
                            dialog.dismiss();
                            deliveryTime.setText(five);
                            deliveryDate.setText(Day5 + " " + Date5);
                            break;
                        case 5:
                            String six = holder.timing3.getText().toString();
                            Log.e("tim1", "" + six);
                            dialog.dismiss();
                            deliveryTime.setText(six);
                            deliveryDate.setText(Day6 + " " + Date6);
                            break;
                        case 6:
                            String seven = holder.timing3.getText().toString();
                            Log.e("tim1", "" + seven);
                            dialog.dismiss();
                            deliveryTime.setText(seven);
                            deliveryDate.setText(Day7 + " " + Date7);
                    }
                }

            });
        }

        @Override
        public int getItemViewType(int section, int relativePosition, int absolutePosition) { return super.getItemViewType(section, relativePosition, absolutePosition); }

        @Override
        public MainVH onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout = 0;
            switch (viewType) {
                case VIEW_TYPE_HEADER:
                    layout = R.layout.list_activity_dayinfo;
                    break;
                case VIEW_TYPE_ITEM:
                    layout = R.layout.timings_text;
                    break;
            }
            View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new MainVH(v, this);
        }

        public class MainVH extends SectionedViewHolder implements View.OnClickListener {
            final DateAdaper adapter;
            public View view;
            TextView day, Date;
            TextView timing1, timing2, timing3;
            RelativeLayout rTime1, rTime2, rTime3;
            TextView unavailable1, unavailable2, unavailable3;

            MainVH(View itemView, DateAdaper adapter) {
                super(itemView);
                day = itemView.findViewById(R.id.textDay);
                Date = itemView.findViewById(R.id.date);
                timing1 = itemView.findViewById(R.id.timingText1);
                timing2 = itemView.findViewById(R.id.timingText2);
                timing3 = itemView.findViewById(R.id.timingText3);
                rTime1 = itemView.findViewById(R.id.relativeTime1);
                rTime2 = itemView.findViewById(R.id.relativeTime2);
                rTime3 = itemView.findViewById(R.id.relativeTime3);
                unavailable1 = itemView.findViewById(R.id.unavailable1);
                unavailable2 = itemView.findViewById(R.id.unavailable2);
                unavailable3 = itemView.findViewById(R.id.unavailable3);
                this.adapter = adapter;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (isFooter()) {
                    Log.e("footer", "call");
                }
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
        finish();
    }
}