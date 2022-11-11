package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.AddressController;
import vmart.example.mypc.vedasmart.controllers.PickupController;
import vmart.example.mypc.vedasmart.model.PincodeList;

public class DeliveryNewActivity extends AppCompatActivity {
    PincodeAdapter pincodeAdapter;
    RecyclerView recyclerView;
    public int selectedPosition = -1;
    public List<PincodeList> fetchPincodeArray = new ArrayList<PincodeList>();
    EditText deliveryPincodeEditText;
    public List<PincodeList> filterArray = new ArrayList<>();
    Boolean yourBool;
    Toolbar toolbar;
    TextView toolProductName;
    ImageView toolbarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_areas_list);
        yourBool = getIntent().getExtras().getBoolean("yourBoolName");
        fetchPincodeArray = PickupController.getInstance().pincodelistArrayList;
        filterArray = fetchPincodeArray;
        createToolbar();
        loadRecyclerView();
        hideSoftKeyboard(deliveryPincodeEditText);
        loadSearchtext();
    }

    private void loadRecyclerView() {
        deliveryPincodeEditText = findViewById(R.id.deliveryPincodeEditText);
        pincodeAdapter = new PincodeAdapter(this);
        recyclerView = findViewById(R.id.deliveryAreasRecyclerview);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(pincodeAdapter);
        recyclerView.setHasFixedSize(true);
    }

    public void createToolbar() {
        toolbar = findViewById(R.id.wishlist_toolbar);
        toolProductName = findViewById(R.id.wishlist_title);
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchPincodeArray = PickupController.getInstance().pincodelistArrayList;
        pincodeAdapter.notifyDataSetChanged();
    }

    private void loadSearchtext() {
        deliveryPincodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                selectedPosition = -1;
                updateSearchResults(s.toString().toLowerCase());
                String outputedText = s.toString();
                String edit = deliveryPincodeEditText.getText().toString().trim();
                if (edit.length() == 6 || edit.length() == 3) {
                    if (filterArray.size() == 0) {
                        getlocation(edit);
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "Our services is not available in your pincode currently", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateSearchResults(String text) {
        ArrayList<PincodeList> sortedArray = null;
        sortedArray = new ArrayList<>();
        for (PincodeList object : PickupController.getInstance().pincodelistArrayList) {
            if (object.getPincode().toLowerCase().startsWith(text) || object.getArea().toLowerCase().startsWith(text)) {
                sortedArray.add(object);
            }
        }
        filterArray = sortedArray;
        loadRecyclerView();
        pincodeAdapter.notifyDataSetChanged();
    }

    public class PincodeAdapter extends RecyclerView.Adapter<PincodeAdapter.MyViewHolder> {
        Context context;

        public PincodeAdapter(Context context) {
            this.context = context;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView deliveryAreaTextView;

            public MyViewHolder(View view) {
                super(view);
                deliveryAreaTextView = itemView.findViewById(R.id.deliveryAreaTextView);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_delivery_locations_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final PincodeList deliveryArea = filterArray.get(position);
            holder.deliveryAreaTextView.setText(deliveryArea.getArea() + "," + deliveryArea.getPincode());
            if (selectedPosition == position) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        deliveryPincodeEditText.setText(filterArray.get(holder.getAdapterPosition()).getArea() + "," + deliveryArea.getPincode());
                        if (yourBool) {
                            Intent i = new Intent(DeliveryNewActivity.this, AddNewAddressActivity.class);
                            if (AddressController.getInstance().selectedAddressList != null) {
                                AddressController.getInstance().selectedAddressList.setPinCode(deliveryArea.getPincode());
                                AddressController.getInstance().selectedAddressList.setArea(deliveryArea.getArea());
                            } else {
                                i.putExtra("search", deliveryArea.getPincode());
                                i.putExtra("Area", deliveryArea.getArea());
                                i.putExtra("city", "Karnataka");
                                i.putExtra("state", "Bangalore");
                                i.putExtra("name1", getIntent().getExtras().getString("name"));
                                i.putExtra("address1", getIntent().getExtras().getString("address"));
                                i.putExtra("land1", getIntent().getExtras().getString("landmark"));
                                i.putExtra("favorite1", getIntent().getExtras().getBoolean("favorite"));
                                i.putExtra("mobile1", getIntent().getExtras().getString("mobilenumber"));
                            }
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                            Log.e("else", "call :: " + yourBool);
                        }
                    }
                });
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedPosition != position) {
                        selectedPosition = position;
                        notifyDataSetChanged();
                    } else {
                        selectedPosition = -1;
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (filterArray.size() > 0) {
                return filterArray.size();
            } else {
                return 0;
            }
        }
    }

    public void hideSoftKeyboard(EditText edtView) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtView.getWindowToken(), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void getlocation(String code) {
        final Geocoder geocoder = new Geocoder(getApplicationContext());
        final String zip = code;
        if (TextUtils.isEmpty(zip)) {
            filterArray.addAll(fetchPincodeArray);
        } else {
            for (int i = 0; i < fetchPincodeArray.size(); i++) {
                if (fetchPincodeArray.get(i).getPincode().contains(zip) || fetchPincodeArray.get(i).getArea().contains(zip)) {
                    // Adding Matched items
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(zip, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            // Use the address as needed
                            String message = String.format("Latitude: %f, Longitude: %f",
                                    address.getLatitude(), address.getLongitude());
                            double lng = address.getLongitude();
                            double lat = address.getLatitude();
                            Location pinLoc = new Location("");
                            pinLoc.setLatitude(lat);
                            pinLoc.setLongitude(lng);
                            getNearestLocation(pinLoc);
                        } else {
                            // Display appropriate message when Geocoder services are not available
                            Toast.makeText(getApplicationContext(), "Our services is not available in your pincode currently", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        // handle exception
                    }
                } else {
                    // Display appropriate message when Geocoder services are not available
                    Toast.makeText(getApplicationContext(), "Our services is not available in your pincode currently", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void getNearestLocation(Location location) {
        String lat = null;
        String lang = null;
        for (int i = 0; i < fetchPincodeArray.size(); i++) {
            Location each = new Location("");
            lat = fetchPincodeArray.get(i).getLatitude();
            lang = fetchPincodeArray.get(i).getLangitude();
            each.setLatitude(Double.parseDouble(lat));
            each.setLongitude(Double.parseDouble(lang));
            double distance = location.distanceTo(each);///meters
            double distance1 = distance / 1000;/////////in Kilometers
            if (distance1 < 15) {
                filterArray.add(fetchPincodeArray.get(i));
                pincodeAdapter.notifyDataSetChanged();
                loadRecyclerView();
            }
        }
        pincodeAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        }else {
            finish();
        }
    }
}


