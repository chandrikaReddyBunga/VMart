package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.afollestad.sectionedrecyclerview.SectionedViewHolder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.CartController;
import vmart.example.mypc.vedasmart.controllers.PickupController;
import vmart.example.mypc.vedasmart.model.PickupLists;

public class SelfPickUpActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    ImageView toolbarImage;
    TextView textHeading, spinnerText, spinnerText1;
    TextView PickUpTime;
    ImageView image;
    RelativeLayout relativeSummary;
    LinearLayout relativeCart;
    ArrayList<String> ouputdate = new ArrayList<String>();
    ArrayList<String> ouputday = new ArrayList<String>();
    TextView selectPickPointLocation;
    String pickuploaction,pickuploaction1;
    AlertDialog.Builder dialog;
    public static String Day1, Day2, Day3, Day4, Day5, Day6, Day7;
    public static String Date1, Date2, Date3, Date4, Date5, Date6, Date7;
    Button viewOrderSummary;
    TextView deliveryCharges, cartValue, amountPayable, youSavings, taxamount;
    int cartCount = 0, cartAmount = 0;
    String totalMrp, totalVmart, totalSavings = "0";
    String selfpick;
    TextView selfPickup;
    TextView changeText;
    TextView editChange;
    RelativeLayout backIconLayout;
    ArrayList<String> time = new ArrayList<>();
    int compare1, compare2, compare3, compare4;
    int slotsBlocked = 0;
    public static String ti;
    Date ctdate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_pick_up);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init() {
        time.add("12:00 PM - 03:00 PM");
        time.add("03:00 PM - 06:00 PM");
        time.add("06:00 PM - 09:00 PM");
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
        backIconLayout = findViewById(R.id.relative_back);
        backIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DeliveryTypeActivity.class));
                ti = null;
            }
        });
        selfPickup = findViewById(R.id.textSelfPickup);
        selfpick = selfPickup.getText().toString();
        textHeading = findViewById(R.id.Name);
        textHeading.setText("Select a Time Slot");
        textHeading.setVisibility(View.VISIBLE);
        image = findViewById(R.id.textMoreLessimg);
        spinnerText = findViewById(R.id.spinnerText);
        spinnerText1 = findViewById(R.id.spinnerText1);
        PickUpTime = findViewById(R.id.pickUptimeBox);
        deliveryCharges = findViewById(R.id.deliveryCharges);
        cartValue = findViewById(R.id.cartValue);
        amountPayable = findViewById(R.id.amountPay);
        youSavings = findViewById(R.id.Savings);
        taxamount = findViewById(R.id.TaxAmount);
        changeText = findViewById(R.id.texttimeChange);
        editChange = findViewById(R.id.textChange);
        editChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PickupLocations.class));
            }
        });
        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogue();
            }
        });
        deliveryCharges.setText("No Delivery Charges");
        PickUpTime.setText(ti);
        if (CartController.getInstace().cartListArray != null && CartController.getInstace().cartListArray.size() > 0) {
            for (int i = 0; i < CartController.getInstace().cartListArray.size(); i++) {
                cartCount = cartCount + Integer.valueOf(CartController.getInstace().cartListArray.get(i).getQuantity());
                cartAmount = cartAmount + Integer.valueOf(CartController.getInstace().cartListArray.get(i).getQuantity()) * Integer.valueOf(CartController.getInstace().cartListArray.get(i).getVmartPrice());
                totalMrp = getTotalMrp(CartController.getInstace().cartListArray.get(i).getQuantity(), CartController.getInstace().cartListArray.get(i).getMrpPrice());
                totalVmart = getTotalVmart(CartController.getInstace().cartListArray.get(i).getQuantity(), CartController.getInstace().cartListArray.get(i).getVmartPrice());
                totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));
            }
        }
        cartValue.setText(getResources().getString(R.string.rupee) + cartAmount + ".00");
        amountPayable.setText(getResources().getString(R.string.rupee) + cartAmount + ".00");
        youSavings.setText(getResources().getString(R.string.rupee) + totalSavings + ".00");
        viewOrderSummary = findViewById(R.id.btn_ViewOrder);
        viewOrderSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeslot = PickUpTime.getText().toString().trim();
                if (timeslot.length() > 0) {
                    if (PickUpTime.getText().toString().length() == 0) {
                        displayAlterOnNoTimeSlot();
                    } else {
                        String estimate = "0";
                        Intent intent = new Intent(SelfPickUpActivity.this, DeliveryOrderSummary.class);
                        intent.putExtra("selectedDeliveryType", selfpick);
                        intent.putExtra("pickuplocation", selectPickPointLocation.getText().toString());
                        intent.putExtra("pickuptime", PickUpTime.getText().toString());
                        intent.putExtra("pickuplandmark",pickuploaction1);
                        intent.putExtra("estimatedprice", estimate);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Please Select a Time Slot", Toast.LENGTH_SHORT).show();
                }
            }
        });
        PickUpTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogue();
            }
        });
        datesCheck();
        daysCheck();
        selectPickPointLocation = findViewById(R.id.pickUpLocationBox);
        selectPickPointLocation.setSelected(true);
        if (isConn()) {
            if (PickupController.getInstance().pickuplistsArrayList.size() > 0) {
                int favouritePosition = 0;
                for (int i = 0; i < PickupController.getInstance().pickuplistsArrayList.size(); i++) {
                    PickupLists objPickUp = PickupController.getInstance().pickuplistsArrayList.get(i);
                    if (objPickUp.getFavourite()) {
                        favouritePosition = i;
                    }
                }
                PickupLists objFavouritePickUp = PickupController.getInstance().pickuplistsArrayList.get(favouritePosition);
                pickuploaction = objFavouritePickUp.getAddress() + " " + objFavouritePickUp.getPincode();
                pickuploaction1 =objFavouritePickUp.getLandmark()+ ", " +objFavouritePickUp.getCity() + ", "+ objFavouritePickUp.getPincode() ;
                selectPickPointLocation.setText(pickuploaction);
            }
        } else {
            Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        selectPickPointLocation.setOnClickListener(ClickOnPickLoaction);
        getCurrentTime();
    }

    public void getCurrentTime() {
        Log.e("method", "CALL");
        String slota = "12:00 PM";
        String slotb = "15:00 PM";
        String slotc = "18:00 PM";
        String slotd = "21:00 PM";
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        ctdate = Calendar.getInstance().getTime();
        String ct = sdf1.format(ctdate);
        try {
            ctdate = sdf1.parse(ct);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            Date date1 = sdf1.parse(slota);
            Date date2 = sdf1.parse(slotb);
            Date date3 = sdf1.parse(slotc);
            Date date4 = sdf1.parse(slotd);
            compare1 = ctdate.compareTo(date1);
            compare2 = ctdate.compareTo(date2);
            compare3 = ctdate.compareTo(date3);
            compare4 = ctdate.compareTo(date4);
            Log.e("Compare", "Compare1 value is:" + compare1);
            Log.e("Compare", "Compare2 value is:" + compare2);
            Log.e("Compare", "Compare3 value is:" + compare3);
            Log.e("Compare", "Compare4 value is:" + compare4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    ///////click on selectPickup point
    View.OnClickListener ClickOnPickLoaction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isConn()) {
                Intent in = new Intent(getApplicationContext(), PickupLocations.class);
                in.putExtra("timeslot", PickUpTime.getText().toString().trim());
                startActivity(in);
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void daysCheck() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        for (int i = 0; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, i);
            String day = sdf.format(calendar.getTime());
            ouputdate.add(day);
        }
    }

    public void datesCheck() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE",new Locale("en", "US"));
        for (int i = 0; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, i);
            String day = sdf.format(calendar.getTime());
            ouputday.add(day);
        }
    }

    public void AlertDialogue() {
        if (isConn()) {
            dialog = new AlertDialog.Builder(SelfPickUpActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_timeslot, null);
            // Set the custom layout as alert dialog view
            dialog.setView(dialogView);
            DateAdaper dateAdaper;
            RecyclerView recyclerView;
            TextView textTitle = dialogView.findViewById(R.id.textTitle);
            TextView textTimeSlotText = dialogView.findViewById(R.id.textTimeSlotText);
            textTitle.setText("Please Select a Time Slot");
            textTimeSlotText.setText("Request you to be on time to collect your order, Missing your pickup time, might lead to order cancellation.");
            recyclerView = dialogView.findViewById(R.id.timeSlotRecyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Create the alert dialog
            final AlertDialog builder = dialog.create();
            dateAdaper = new DateAdaper(builder);
            recyclerView.setAdapter(dateAdaper);
            builder.show();
        }
    }
    /////display alert if no time slot is seleted
    public void displayAlterOnNoTimeSlot() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SelfPickUpActivity.this);
        // set dialog message
        alertDialogBuilder
                .setMessage("Please Select a Time Slot")
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
        alertDialog.setCanceledOnTouchOutside(false);
        // show it
        alertDialog.show();
    }
    //calling this method to check the internet connection
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
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
        public void onBindHeaderViewHolder(final SelfPickUpActivity.DateAdaper.MainVH holder, int section, boolean expanded) {
            switch (section) {
                case 0:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day1 = holder.day.getText().toString();
                    Date1 = holder.Date.getText().toString();
                    break;
                case 1:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day2 = holder.day.getText().toString();
                    Date2 = holder.Date.getText().toString();
                    break;
                case 2:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day3 = holder.day.getText().toString();
                    Date3 = holder.Date.getText().toString();
                    break;
                case 3:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day4 = holder.day.getText().toString();
                    Date4 = holder.Date.getText().toString();
                    break;
                case 4:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day5 = holder.day.getText().toString();
                    Date5 = holder.Date.getText().toString();
                    break;
                case 5:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day6 = holder.day.getText().toString();
                    Date6 = holder.Date.getText().toString();
                    break;
                case 6:
                    holder.Date.setText(ouputdate.get(section));
                    holder.day.setText(ouputday.get(section));
                    Day7 = holder.day.getText().toString();
                    Date7 = holder.Date.getText().toString();
                    break;
            }
        }

        @Override
        public void onBindFooterViewHolder(SelfPickUpActivity.DateAdaper.MainVH holder, int section) { }

        @Override
        public void onBindViewHolder(final SelfPickUpActivity.DateAdaper.MainVH holder, final int section, final int relativePosition, final int absolutePosition) {
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
                    holder.timing1.setText(time.get(0));
                    holder.timing2.setText(time.get(1));
                    holder.timing3.setText(time.get(2));
                    break;
                case 1:
                    holder.timing1.setText(time.get(0));
                    holder.timing2.setText(time.get(1));
                    holder.timing3.setText(time.get(2));
                    break;
                case 2:
                    holder.timing1.setText(time.get(0));
                    holder.timing2.setText(time.get(1));
                    holder.timing3.setText(time.get(2));
                    break;
                case 3:
                    holder.timing1.setText(time.get(0));
                    holder.timing2.setText(time.get(1));
                    holder.timing3.setText(time.get(2));
                    break;
                case 4:
                    holder.timing1.setText(time.get(0));
                    holder.timing2.setText(time.get(1));
                    holder.timing3.setText(time.get(2));
                    break;
                case 5:
                    holder.timing1.setText(time.get(0));
                    holder.timing2.setText(time.get(1));
                    holder.timing3.setText(time.get(2));
                    break;
                case 6:
                    holder.timing1.setText(time.get(0));
                    holder.timing2.setText(time.get(1));
                    holder.timing3.setText(time.get(2));
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
                                dialog.dismiss();
                                PickUpTime.setText(one + "," + Day1 + "," + Date1);
                            } else if (slotsBlocked >= 0) {
                                dialog.dismiss();
                                PickUpTime.setText("");
                                Toast.makeText(SelfPickUpActivity.this,"Unavailable", Toast.LENGTH_SHORT).show();
                            } else {
                            }
                            break;

                        case 1:
                            String two = holder.timing1.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(two + "," + Day2 + "," + Date2);
                            break;
                        case 2:
                            String three = holder.timing1.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(three + "," + Day3 + "," + Date3);
                            break;
                        case 3:
                            String four = holder.timing1.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(four + "," + Day4 + "," + Date4);
                            break;
                        case 4:
                            String five = holder.timing1.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(five + "," + Day5 + "," + Date5);
                            break;
                        case 5:
                            String six = holder.timing1.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(six + "," + Day6 + "," + Date6);
                            break;
                        case 6:
                            String seven = holder.timing1.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(seven + "," + Day7 + "," + Date7);
                    }
                    ti = PickUpTime.getText().toString();
                }

            });
            holder.rTime2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (section) {
                        case 0:
                            if (slotsBlocked == 0) {
                                String one = holder.timing2.getText().toString();
                                dialog.dismiss();
                                PickUpTime.setText(one + "," + Day1 + "," + Date1);
                            } else if (slotsBlocked == 1) {
                                String one = holder.timing2.getText().toString();
                                dialog.dismiss();
                                PickUpTime.setText(one + "," + Day1 + "," + Date1);
                            } else if (slotsBlocked >= 1 ) {
                                dialog.dismiss();
                                PickUpTime.setText("");
                                Toast.makeText(SelfPickUpActivity.this, "Unavailable", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            String two = holder.timing2.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(two + "," + Day2 + "," + Date2);
                            break;
                        case 2:
                            String three = holder.timing2.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(three + "," + Day3 + "," + Date3);
                            break;
                        case 3:
                            String four = holder.timing2.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(four + "," + Day4 + "," + Date4);
                            break;
                        case 4:
                            String five = holder.timing2.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(five + "," + Day5 + "," + Date5);
                            break;
                        case 5:
                            String six = holder.timing2.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(six + "," + Day6 + "," + Date6);
                            break;
                        case 6:
                            String seven = holder.timing2.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(seven + "," + Day7 + "," + Date7);
                    }
                    ti = PickUpTime.getText().toString();
                }
            });
            holder.rTime3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (section) {
                        case 0:
                            if (slotsBlocked == 0) {
                                String one = holder.timing3.getText().toString();
                                dialog.dismiss();
                                PickUpTime.setText(one + "," + Day1 + "," + Date1);
                            } else if (slotsBlocked == 1) {
                                String one = holder.timing3.getText().toString();
                                dialog.dismiss();
                                PickUpTime.setText(one + "," + Day1 + "," + Date1);
                            } else if (slotsBlocked == 2) {
                                String one = holder.timing3.getText().toString();
                                dialog.dismiss();
                                PickUpTime.setText(one + "," + Day1 + "," + Date1);
                            } else if (slotsBlocked == 3) {
                                dialog.dismiss();
                                PickUpTime.setText("");
                                Toast.makeText(SelfPickUpActivity.this,"Unavailable", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            String two = holder.timing3.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(two + "," + Day2 + "," + Date2);
                            break;
                        case 2:
                            String three = holder.timing3.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(three + "," + Day3 + "," + Date3);
                            break;
                        case 3:
                            String four = holder.timing3.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(four + "," + Day4 + "," + Date4);
                            break;
                        case 4:
                            String five = holder.timing3.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(five + "," + Day5 + "," + Date5);
                            break;
                        case 5:
                            String six = holder.timing3.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(six + "," + Day6 + "," + Date6);
                            break;
                        case 6:
                            String seven = holder.timing3.getText().toString();
                            dialog.dismiss();
                            PickUpTime.setText(seven + "," + Day7 + "," + Date7);
                    }
                    ti = PickUpTime.getText().toString();
                }

            });
            holder.unavailable1.setText("Unavailable");
            holder.unavailable2.setText("Unavailable");
            holder.unavailable3.setText("Unavailable");
        }

        @Override
        public int getItemViewType(int section, int relativePosition, int absolutePosition) {
            return super.getItemViewType(section, relativePosition, absolutePosition);
        }

        @Override
        public SelfPickUpActivity.DateAdaper.MainVH onCreateViewHolder(ViewGroup parent, int viewType) {
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
            return new DateAdaper.MainVH(v, this);
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
                }
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), DeliveryTypeActivity.class));
        ti = null;
    }
}