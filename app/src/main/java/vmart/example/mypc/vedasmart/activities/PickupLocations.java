package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.PickupController;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.model.PickupLists;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PickupLocations extends AppCompatActivity {
    Toolbar toolbar;
    TextView toolbarText;
    ImageView backIcon;
    RecyclerView pickupRecyclerview;
    PickupLocationAdapter pickupLocationAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_locations);
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.toolbarcart);
        setSupportActionBar(toolbar);
        toolbarText = findViewById(R.id.toolProductNameStart);
        toolbarText.setVisibility(View.VISIBLE);
        toolbarText.setText("Pickup point list");
        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
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
        pickupRecyclerview = findViewById(R.id.recyclerview_pickup);
        pickupRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        pickupLocationAdapter = new PickupLocationAdapter(PickupLocations.this);
        pickupRecyclerview.setAdapter(pickupLocationAdapter);
        pickupLocationAdapter.notifyDataSetChanged();
    }

    public class PickupLocationAdapter extends RecyclerView.Adapter<PickupLocationAdapter.ViewHolder> {
        private int selectedPosition = -1;
        Context context;

        public PickupLocationAdapter(Context context) {
            this.context = context;
            this.selectedPosition = selectedPosition;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pickup_locations_list, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            final PickupLists objPickUpAddress = PickupController.getInstance().pickuplistsArrayList.get(position);
            holder.address.setText(objPickUpAddress.getAddress());
            holder.landmark.setText(objPickUpAddress.getLandmark());
            holder.area.setText(objPickUpAddress.getArea());
            holder.city.setText(objPickUpAddress.getCity());
            holder.pincode.setText(objPickUpAddress.getPincode());

            if (objPickUpAddress.getFavourite()) {
                holder.favourite.setBackgroundResource(R.drawable.ic_like_on);
            } else {
                holder.favourite.setBackgroundResource(R.drawable.ic_like_off);
            }

            holder.viewmap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String originLocation = LocationTracker.getInstance().currentLocation.getLatitude() + "," + LocationTracker.getInstance().currentLocation.getLongitude();
                    String destinationLocation = PickupController.getInstance().pickuplistsArrayList.get(holder.getAdapterPosition()).getLatitude() + "," + PickupController.getInstance().pickuplistsArrayList.get(holder.getAdapterPosition()).getLangitude();
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + originLocation + "&daddr=" + destinationLocation + "&mode=d"));
                    startActivity(intent);
                }
            });

            holder.favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendFavourites(holder.getAdapterPosition());
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(View view) {
                    Intent catactivity = new Intent(context, SelfPickUpActivity.class);
                    catactivity.putExtra("locationselectedPos", holder.getAdapterPosition());
                    Log.e("timeu"," "+getIntent().getExtras().getString("timeslot"));
                    catactivity.putExtra("timeu", Objects.requireNonNull(getIntent().getExtras()).getString("timeslot"));
                    catactivity.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(catactivity);
                }
            });
           holder. viewmap.setText("View Map");
        }

        @Override
        public int getItemCount() {
            if (PickupController.pickupObj.pickuplistsArrayList.size() > 0) {
                return PickupController.getInstance().pickuplistsArrayList.size();
            } else {
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView address, landmark, area, city, pincode, viewmap;
            public CheckBox favourite;

            public ViewHolder(View itemView) {
                super(itemView);
                address = itemView.findViewById(R.id.pick_up_address);
                landmark = itemView.findViewById(R.id.pick_up_landmark);
                area = itemView.findViewById(R.id.pickup_area);
                city = itemView.findViewById(R.id.pickup_city);
                pincode = itemView.findViewById(R.id.pickup_pincode);
                viewmap = itemView.findViewById(R.id.view_map);
                favourite = itemView.findViewById(R.id.pickup_favourite);
            }
        }
    }

    public void SendFavourites(final int position) {
        final PickupLists selectedPickUp = PickupController.getInstance().pickuplistsArrayList.get(position);
        String addressId = selectedPickUp.getAddressId();
        final Boolean isFavourite = selectedPickUp.getFavourite();
        Progress.show(PickupLocations.this);
        JsonObject favObj = new JsonObject();
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        String mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        String merchant = sharedPreferences.getString("merchantId","");
        Log.e("mob", "" + mobileNumber);
        JSONObject obj = new JSONObject();
        try {
            obj.put("addressId", addressId);
            obj.put("favourite", !isFavourite);
            obj.put("userId", mobileNumber);
            obj.put("merchantId",merchant);
            JsonParser jsonParser = new JsonParser();
            favObj = (JsonObject) jsonParser.parse(obj.toString());
            Log.e("useridd", "" + obj.put("userId", mobileNumber));
            //print parameter
            Log.e("favData", " " + favObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ServerApiInterface.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        ServerApiInterface api = retrofit.create(ServerApiInterface.class);
        Call<ResponseBody> callable = api.sendFavourite(favObj);
        callable.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Progress.dismiss(PickupLocations.this);
                String statuscode, message;
                JSONArray favJsonArray;
                JSONObject favJsonObj;
                if (response.body() != null) {
                    String bodyString = null;
                    try {
                        bodyString = new String(response.body().bytes());
                        Log.e("otpResponse", " " + bodyString);
                        favJsonArray = new JSONArray(bodyString);
                        favJsonObj = favJsonArray.getJSONObject(0);
                        statuscode = favJsonObj.getString("response");
                        message = favJsonObj.getString("message");
                        if (statuscode.equals("3")) {
                            if (!isFavourite) {
                                clearAllFavourites();
                                selectedPickUp.setFavourite(true);
                                PickupController.getInstance().pickuplistsArrayList.set(position, selectedPickUp);
                                pickupLocationAdapter.notifyDataSetChanged();
                            } else {
                                selectedPickUp.setFavourite(false);
                                PickupController.getInstance().pickuplistsArrayList.set(position, selectedPickUp);
                                pickupLocationAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                Progress.dismiss(PickupLocations.this);
            }
        });
    }

    public void clearAllFavourites() {
        for (int i = 0; i < PickupController.getInstance().pickuplistsArrayList.size(); i++) {
            PickupLists objPickUp = PickupController.getInstance().pickuplistsArrayList.get(i);
            objPickUp.setFavourite(false);
            PickupController.getInstance().pickuplistsArrayList.set(i, objPickUp);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}