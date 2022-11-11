package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.AddressController;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.model.AddressList;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static vmart.example.mypc.vedasmart.activities.MyAccountActivity.isfroAddress;

public class AddressActivity extends AppCompatActivity {

    RecyclerView addressRecyclerview;
    Button addnewAddress;
    Toolbar addressToolbar;
    RelativeLayout toolbarImage;
    TextView toolbarText;
    AddressAdapter addressAdapter;
    int selectedPosition = -1;
    SharedPreferences sharedPreferences;
    String mobile;
    String name, address, pincode, mobilenumber, addressId;
    boolean checkboxstatus = false;
    public ServerApiInterface serverApiInterface;
    String merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        mobile = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId","");
        AddressController.getInstance().selectedAddressList = null;
        Log.e("userID", " " + mobile);
        init();
        addnewAddress.setText("Add new Address");
        AddressController.getInstance().FetchAllAddress(AddressActivity.this, mobile,merchant);
    }


    public void init() {
        /////////toolbar
        addressToolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(addressToolbar);
        toolbarImage = findViewById(R.id.relative_back);
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isfroAddress == true) {
                    startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), HomeDeliveryActivity.class));
                }
            }
        });
        toolbarText = findViewById(R.id.toolProductNameStart);
        toolbarText.setText("Address Book");
        toolbarText.setVisibility(View.VISIBLE);
        /////Recyclerview
        addressRecyclerview = findViewById(R.id.address_recylerview);
        addressRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressAdapter(AddressActivity.this);
        addressRecyclerview.setAdapter(addressAdapter);
        /////Addnew Address button click
        addnewAddress = findViewById(R.id.btn_addnew_address);
        addnewAddress.setOnClickListener(clickAddnewAddress);
    }


    ////click on AddnewAddress
    View.OnClickListener clickAddnewAddress = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isConn()) {
                AddressController.getInstance().selectedAddressList = null;
                startActivity(new Intent(getApplicationContext(), AddNewAddressActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    };


    class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

        Context context;

        public AddressAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.address_list, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.name.setText(AddressController.getInstance().addressLists.get(position).getName());
            holder.address.setText(AddressController.getInstance().addressLists.get(position).getAddress() + "," + AddressController.getInstance().addressLists.get(position).getArea());
            holder.landmark.setText("LANDMARK: " + AddressController.getInstance().addressLists.get(position).getLandmark());
            holder.state.setText(AddressController.getInstance().addressLists.get(position).getCity());
            holder.pincode.setText(AddressController.getInstance().addressLists.get(position).getPinCode());
            Log.e("like", "" + AddressController.getInstance().addressLists.get(position).getFavourite());
            if (AddressController.getInstance().addressLists.get(position).getFavourite()) {
                holder.btnlike.setBackgroundResource(R.drawable.ic_like_on);
            } else {
                holder.btnlike.setBackgroundResource(R.drawable.ic_like_off);
            }

            if (selectedPosition == position) {
                Log.e("if", "" + position);
            } else {
                Log.e("else", "" + position);
            }
            holder.relativelike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click", " like");
                    if (isConn()) {
                        Progress.show(AddressActivity.this);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                favourite(context, position, AddressController.getInstance().addressLists);
                            }
                        }, 1000);
                    } else {
                        if (AddressController.getInstance().addressLists.get(position).getFavourite()) {
                            holder.btnlike.setBackgroundResource(R.drawable.ic_like_on);
                            holder.btnlike.setEnabled(false);

                        } else {
                            holder.btnlike.setBackgroundResource(R.drawable.ic_like_off);
                            holder.btnlike.setEnabled(true);
                        }
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.Relativedelet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click", " delete");
                    if (isConn()) {
                        final AddressList addressList = AddressController.getInstance().addressLists.get(holder.getAdapterPosition());
                        if (AddressController.getInstance().addressLists.contains(addressList)) {
                            Progress.show(AddressActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int index = AddressController.getInstance().addressLists.indexOf(addressList);
                                    deleteAddress(context, AddressController.getInstance().addressLists.get(holder.getAdapterPosition()).getAddressId(), index,merchant);
                                }
                            }, 1000);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.relativeedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click", " edit");
                    if (isConn()) {
                        AddressController.getInstance().selectedAddressList = AddressController.getInstance().addressLists.get(holder.getAdapterPosition());
                        Intent intent = new Intent(context, AddNewAddressActivity.class);
                        intent.putExtra("editAddress", holder.getAdapterPosition());
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            /*holder.btnlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click", " like");

                    favourite(context, position, addressLists);

                }
            });*/

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click", " delete");
                    if (isConn()) {
                        final AddressList addressList = AddressController.getInstance().addressLists.get(holder.getAdapterPosition());
                        if (AddressController.getInstance().addressLists.contains(addressList)) {
                            Progress.show(AddressActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int index = AddressController.getInstance().addressLists.indexOf(addressList);
                                    deleteAddress(context, AddressController.getInstance().addressLists.get(holder.getAdapterPosition()).getAddressId(), index,merchant);
                                    Progress.dismiss(AddressActivity.this);
                                }
                            }, 1000);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click", " edit");

                    if (isConn()) {
                        AddressController.getInstance().selectedAddressList = AddressController.getInstance().addressLists.get(holder.getAdapterPosition());
                        Intent intent = new Intent(context, AddNewAddressActivity.class);
                        intent.putExtra("editAddress", holder.getAdapterPosition());
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e("addressSelectedPos", "" + holder.getAdapterPosition());
                    if (isConn()) {
                        if (isfroAddress == false) {
                            Intent intent = new Intent(context, HomeDeliveryActivity.class);
                            intent.putExtra("Address", holder.getAdapterPosition());
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            //startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (AddressController.getInstance().addressLists.size() > 0) {
                return AddressController.getInstance().addressLists.size();
            } else {
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, address, state, landmark, pincode;
            ImageButton btnEdit, btnDelete, btnlike;
            RelativeLayout Relativedelet, relativeedit, relativelike;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                address = itemView.findViewById(R.id.address);
                state = itemView.findViewById(R.id.city_state);
                pincode = itemView.findViewById(R.id.pincode);
                landmark = itemView.findViewById(R.id.landmark);
                btnEdit = itemView.findViewById(R.id.btn_edit);
                btnDelete = itemView.findViewById(R.id.btn_delete);
                btnlike = itemView.findViewById(R.id.btn_like_unlike);

                Relativedelet = itemView.findViewById(R.id.Relativedelet);
                relativeedit = itemView.findViewById(R.id.relativeedit);
                relativelike = itemView.findViewById(R.id.relativelike);

            }
        }

        ///////delete Address
        public void deleteAddress(final Context context, String addressID, final int adapterPosition,String merchant) {
            ServerApiInterface serverApiInterface;
            Log.e("deleteID", " " + addressID);
            JsonObject deleteObj = new JsonObject();
            JSONObject deleteJsonObj = new JSONObject();
            try {
                deleteJsonObj.put("addressId", addressID);
                deleteJsonObj.put("merchantId",merchant);
                JsonParser jsonParser = new JsonParser();
                deleteObj = (JsonObject) jsonParser.parse(deleteJsonObj.toString());
                Log.e("deleteObj", " " + deleteObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
            Call<ResponseBody> deleteCall = serverApiInterface.deleteAdress(deleteObj);
            deleteCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    JSONArray deleteaddreessJsonArry = null;
                    JSONObject deleteAddressJsobObj = null;
                    String message, statuscode;

                    Log.e("deleteResponse", " " + response.isSuccessful());
                    if (response.body() != null) {

                        String deleteAddressresponse = null;
                        try {
                            deleteAddressresponse = new String(response.body().bytes());
                            deleteaddreessJsonArry = new JSONArray(deleteAddressresponse);
                            deleteAddressJsobObj = deleteaddreessJsonArry.getJSONObject(0);
                            statuscode = deleteAddressJsobObj.getString("response");
                            message = deleteAddressJsobObj.getString("message");
                            Log.e("response", " " + response);
                            if (statuscode.equals("3")) {
                                AddressController.getInstance().addressLists.remove(adapterPosition);
                                addressAdapter.notifyDataSetChanged();
                                Progress.dismiss(AddressActivity.this);
                            } else {
                                Progress.dismiss(AddressActivity.this);
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
                    Progress.dismiss(AddressActivity.this);
                }
            });
        }
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    public void favourite(final Context context, final int position, ArrayList<AddressList> addressLists) {
        final AddressList add = addressLists.get(position);

        String userid = add.getUserId();
        String addressid = add.getAddressId();
        final boolean favourite = add.getFavourite();
        String address = add.getAddress();
        String name = add.getName();
        String state = add.getState();
        String mobile = add.getMobile();
        String city = add.getCity();
        String area = add.getArea();
        String landmark = add.getLandmark();
        String pin = add.getPinCode();
        double latti = add.getLatitude();
        double longit = add.getLongitude();
        String time = add.getTimeStamp();
        Log.e("editAddressDetails", " " + addressId);
        JsonObject editAddress = new JsonObject();
        JSONObject editAddressObj = new JSONObject();
        try {

            editAddressObj.put("pinCode", pin);
            editAddressObj.put("addressId", addressid);
            editAddressObj.put("address", address);
            editAddressObj.put("name", name);
            editAddressObj.put("state", state);
            editAddressObj.put("mobile", mobile);
            editAddressObj.put("city", city);
            editAddressObj.put("area", area);
            editAddressObj.put("landmark", landmark);
            editAddressObj.put("userId", userid);
            editAddressObj.put("favourite", !favourite);
            editAddressObj.put("latitude", latti);
            editAddressObj.put("longitude", longit);
            editAddressObj.put("timeStamp", time);
            editAddressObj.put("merchantId",merchant);
            JsonParser jsonParser = new JsonParser();
            editAddress = (JsonObject) jsonParser.parse(editAddressObj.toString());
            Log.e("editAddress", " " + editAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> editAddressCall = serverApiInterface.editAddress(editAddress);
        editAddressCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("EditResponse", " " + response.isSuccessful());
                JSONArray editaddreessJsonArry = null;
                JSONObject editAddressJsobObj = null;
                String message, statuscode;

                if (response.body() != null) {
                    String editAddressresponse = null;
                    try {
                        editAddressresponse = new String(response.body().bytes());
                        editaddreessJsonArry = new JSONArray(editAddressresponse);
                        editAddressJsobObj = editaddreessJsonArry.getJSONObject(0);
                        statuscode = editAddressJsobObj.getString("response");
                        message = editAddressJsobObj.getString("message");
                        if (statuscode.equals("3")) {
                            if (!favourite) {
                                clearAllFavourites();
                                add.setFavourite(true);
                                AddressController.getInstance().addressLists.set(position, add);
                                addressAdapter.notifyDataSetChanged();
                            } else {
                                add.setFavourite(false);
                                AddressController.getInstance().addressLists.set(position, add);
                                addressAdapter.notifyDataSetChanged();
                            }
                            Collections.sort(AddressController.getInstance().addressLists, new Comparator<AddressList>() {
                                @Override
                                public int compare(AddressList o1, AddressList o2) {
                                    return o2.getTimeStamp().compareTo(o1.getTimeStamp());
                                }
                            });
                            Progress.dismiss(AddressActivity.this);
                        } else {
                            Progress.dismiss(AddressActivity.this);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                Progress.dismiss(AddressActivity.this);
            }
        });
    }

    public void clearAllFavourites() {
        for (int i = 0; i < AddressController.getInstance().addressLists.size(); i++) {
            AddressList addressList1 = AddressController.getInstance().addressLists.get(i);
            addressList1.setFavourite(false);
            AddressController.getInstance().addressLists.set(i, addressList1);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        }else {
            finish();
        }
        if (isfroAddress == true) {
            startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), HomeDeliveryActivity.class));
        }
    }
}
