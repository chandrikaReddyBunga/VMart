package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.AddressController;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.model.AllAddressList;
import vmart.example.mypc.vedasmart.sessions.SessionManager;

public class AddNewAddressActivity extends AppCompatActivity {

    EditText editName, editAddress, editLandmark, editPincode, editArea, editCity, editState,editmobile;
    Button proceedbtn, btn_googlemaps;
    ImageView backBtn;
    String name, address, landmark, pincode, area, city, state, mobilenumber, Area, names, addressess, landmarks, mobilenumbers;
    CheckBox favoriteCheckbox;
    boolean checkboxstatus = false;
    SharedPreferences sharedPreferences;
    String userId;
    int selectePos;
    Toolbar newAddressToolbar;
    ImageView toolbarImage;
    TextView toolbarText;
    ArrayList<AllAddressList> allAddressLists;
    String addressId;
    String getlatitude, getlongitude;
    double getlat, getlang;
    TextView text_hint;
    public static boolean isFromAddaddress = false;
    private String timestamp;
    String merchant;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        allAddressLists = new ArrayList<>();
        Intent intent = getIntent();
        pincode = intent.getStringExtra("search");
        Area = intent.getStringExtra("Area");
        city = intent.getStringExtra("city");
        state = intent.getStringExtra("state");
        names = intent.getStringExtra("name1");
        addressess = intent.getStringExtra("address1");
        landmarks = intent.getStringExtra("land1");
        mobilenumbers = intent.getStringExtra("mobile1");
        checkboxstatus = intent.getBooleanExtra("favorite1", checkboxstatus);

        Intent receiveIntent = this.getIntent();
        getlatitude = receiveIntent.getStringExtra("doubleValue_e1");
        getlongitude = receiveIntent.getStringExtra("doubleValue_e2");
        pincode = receiveIntent.getStringExtra("search");
        Area = receiveIntent.getStringExtra("Area");
        city = receiveIntent.getStringExtra("city");
        state = receiveIntent.getStringExtra("state");
        names = receiveIntent.getStringExtra("name1");
        addressess = receiveIntent.getStringExtra("address1");
        landmarks = receiveIntent.getStringExtra("land1");
        mobilenumbers = receiveIntent.getStringExtra("mobile1");
        checkboxstatus = receiveIntent.getBooleanExtra("favorite1", checkboxstatus);
        ///getting the userLogin mobileNumber
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        userId = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId","");
        init();
    }

    public void init() {
        text_hint = findViewById(R.id.textmobilenumber);
        text_hint.setText(userId.substring(3));
        Log.e("substring", " " + userId.substring(3));
        editmobile = findViewById(R.id.edit_mobileNo);
        if(mobilenumbers != null){
            editmobile.setText(mobilenumbers);
        }else{
            editmobile.setText(userId);
        }
        editName = findViewById(R.id.edit_name);
        editAddress = findViewById(R.id.edit_address);
        editLandmark = findViewById(R.id.edit_landmark);
        editArea = findViewById(R.id.edit_area);
        editArea.setEnabled(false);
        editPincode = findViewById(R.id.edit_pincode);
        editCity = findViewById(R.id.edit_city);
        editCity.setEnabled(false);
        editState = findViewById(R.id.edit_state);
        editState.setEnabled(false);
        proceedbtn = findViewById(R.id.btn_proceed);
        btn_googlemaps = findViewById(R.id.btn_googlemaps);

        btn_googlemaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConn()) {
                    pincode = editPincode.getText().toString();
                    if (pincode.length() == 0 || pincode.length() < 6 || pincode.length() > 6) {
                        editPincode.setError("Enter a 6 digit pincode");
                    } else {
                        Intent i = new Intent(getApplicationContext(), ActivityLocations.class);
                        if (AddressController.getInstance().selectedAddressList == null) {
                            //toolbarText.setText("ADD NEW ADDRESS ");
                            name = editName.getText().toString();
                            address = editAddress.getText().toString();
                            landmark = editLandmark.getText().toString();
                            pincode = editPincode.getText().toString();
                            area = editArea.getText().toString();
                            city = editCity.getText().toString();
                            state = editState.getText().toString();
                            //mobilenumber = text_hint.getText().toString();
                            mobilenumber = editmobile.getText().toString();
                            i.putExtra("name", name);
                            i.putExtra("address", address);
                            i.putExtra("landmark", landmark);
                            i.putExtra("pincode", pincode);
                            i.putExtra("area", area);
                            i.putExtra("city", city);
                            i.putExtra("state", state);
                            i.putExtra("mobilenumber", mobilenumber);
                            i.putExtra("favorite", checkboxstatus);
                        } else {
                            //  toolbarText.setText("EDIT ADDRESS");
                            AddressController.getInstance().selectedAddressList.setName(editName.getText().toString());
                            AddressController.getInstance().selectedAddressList.setAddress(editAddress.getText().toString());
                            AddressController.getInstance().selectedAddressList.setLandmark(editLandmark.getText().toString());
                            //AddressController.getInstance().selectedAddressList.setMobile(text_hint.getText().toString());
                            AddressController.getInstance().selectedAddressList.setMobile(editmobile.getText().toString());
                            AddressController.getInstance().selectedAddressList.setState(editState.getText().toString());
                            AddressController.getInstance().selectedAddressList.setCity(editCity.getText().toString());
                            AddressController.getInstance().selectedAddressList.setFavourite(checkboxstatus);
                            AddressController.getInstance().selectedAddressList.setPinCode(editPincode.getText().toString());
                            AddressController.getInstance().selectedAddressList.setArea(editArea.getText().toString());
                        }
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        favoriteCheckbox = findViewById(R.id.checkbox_favorite_address);
        /////////toolbar
        newAddressToolbar = findViewById(R.id.toolbarcart);
        setSupportActionBar(newAddressToolbar);
        toolbarImage = findViewById(R.id.backIcon);
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            }
        });
        RelativeLayout relative_back = findViewById(R.id.relative_back);
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            }
        });

        toolbarText = findViewById(R.id.toolProductNameStart);

        if (getlatitude == null) {
            proceedbtn.setVisibility(View.GONE);
            btn_googlemaps.setVisibility(View.VISIBLE);
            if (getlongitude == null) {
                proceedbtn.setVisibility(View.GONE);
                btn_googlemaps.setVisibility(View.VISIBLE);
            } else {
                proceedbtn.setVisibility(View.VISIBLE);
                btn_googlemaps.setVisibility(View.GONE);
            }
        } else {
            proceedbtn.setVisibility(View.VISIBLE);
            btn_googlemaps.setVisibility(View.GONE);
        }
        if (AddressController.getInstance().selectedAddressList == null) {
            toolbarText.setText("Add new Address");
            toolbarText.setVisibility(View.VISIBLE);
            editName.setText("");
            editAddress.setText("");
            editLandmark.setText("");
            editArea.setText(Area);
            editCity.setText(city);
            editState.setText(state);
            editPincode.setText(pincode);
            if (checkboxstatus == true) {
                favoriteCheckbox.setChecked(true);
            } else {
                favoriteCheckbox.setChecked(false);
            }
            editPincode.setFocusable(false);
            editPincode.setPressed(false);
            editPincode.setCursorVisible(false);
            if (isConn()) {
                editPincode.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(AddNewAddressActivity.this, DeliveryNewActivity.class);
                        it.putExtra("yourBoolName", true);
                        name = editName.getText().toString();
                        address = editAddress.getText().toString();
                        landmark = editLandmark.getText().toString();
                        pincode = editPincode.getText().toString();
                        mobilenumber = editmobile.getText().toString();
                        it.putExtra("name", name);
                        it.putExtra("address", address);
                        it.putExtra("landmark", landmark);
                        it.putExtra("favorite", checkboxstatus);
                        it.putExtra("mobilenumber", mobilenumber);
                        startActivity(it);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
            editName.setText(names);
            editAddress.setText(addressess);
            editLandmark.setText(landmarks);

        } else {
            //  toolbarText.setText("EDIT ADDRESS");
            toolbarText.setText("Edit Address");
            editPincode.setText(AddressController.getInstance().selectedAddressList.getPinCode());
            editPincode.setFocusable(false);
            editPincode.setPressed(false);
            editPincode.setInputType(InputType.TYPE_NULL);
            editPincode.setCursorVisible(false);
            toolbarText.setVisibility(View.VISIBLE);
            addressId = AddressController.getInstance().selectedAddressList.getAddressId();
            editName.setText(AddressController.getInstance().selectedAddressList.getName());
            editAddress.setText(AddressController.getInstance().selectedAddressList.getAddress());
            landmark = AddressController.getInstance().selectedAddressList.getLandmark();
            if (landmark == null) {
                editLandmark.setText("");
            } else {
                editLandmark.setText(AddressController.getInstance().selectedAddressList.getLandmark());
            }
            editArea.setText(AddressController.getInstance().selectedAddressList.getArea());
            Log.e("city", " " + AddressController.getInstance().selectedAddressList.getCity());
            editCity.setText(AddressController.getInstance().selectedAddressList.getCity());
            editState.setText(AddressController.getInstance().selectedAddressList.getState());
            //text_hint.setText(AddressController.getInstance().selectedAddressList.getMobile());
            editmobile.setText(AddressController.getInstance().selectedAddressList.getMobile());
            checkboxstatus = AddressController.getInstance().selectedAddressList.getFavourite();
            Log.e("checkstatus", "" + checkboxstatus);
            if (checkboxstatus == true) {
                favoriteCheckbox.setChecked(true);
            } else {
                favoriteCheckbox.setChecked(false);
            }
            if (isConn()) {
                editPincode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(AddNewAddressActivity.this, DeliveryNewActivity.class);
                        it.putExtra("yourBoolName", true);
                        AddressController.getInstance().selectedAddressList.setName(editName.getText().toString());
                        AddressController.getInstance().selectedAddressList.setAddress(editAddress.getText().toString());
                        AddressController.getInstance().selectedAddressList.setLandmark(editLandmark.getText().toString());
                        //AddressController.getInstance().selectedAddressList.setMobile(text_hint.getText().toString());
                        AddressController.getInstance().selectedAddressList.setMobile(editmobile.getText().toString());
                        AddressController.getInstance().selectedAddressList.setState(editState.getText().toString());
                        AddressController.getInstance().selectedAddressList.setCity(editCity.getText().toString());
                        AddressController.getInstance().selectedAddressList.setFavourite(checkboxstatus);
                        startActivity(it);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        proceedbtn.setOnClickListener(clickOnProceed);

        favoriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e("checkboxstatus", "" + b);
                if (isConn()) {
                    checkboxstatus = b;
                } else {
                    if (b) {
                        favoriteCheckbox.setChecked(false);
                    } else {
                        favoriteCheckbox.setChecked(true);
                    }
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //////clickOn proceed
    View.OnClickListener clickOnProceed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isConn()) {
                validateEditTextFields();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    /////////validations
    public void validateEditTextFields() {
        name = editName.getText().toString();
        address = editAddress.getText().toString();
        landmark = editLandmark.getText().toString();
        pincode = editPincode.getText().toString();
        area = editArea.getText().toString();
        city = editCity.getText().toString();
        state = editState.getText().toString();
        //mobilenumber = editmobile.getText().toString();
        if (editmobile.getText().toString().startsWith("+91")){
            mobilenumber = editmobile.getText().toString();
            Log.e("call", "mobile1"+mobilenumber);
        }else{
            mobilenumber = "+91"+editmobile.getText().toString();
            Log.e("call", "mobile2"+mobilenumber);
        }
        //mobilenumber = userId;

        Log.e("call", "validations");
        if (name.length() == 0) {
            editName.setError("Enter Full Name");
        } else if (address.length() == 0) {
            editAddress.setError("Enter complete Address");
        } else if (pincode.length() == 0 || pincode.length() < 6 || pincode.length() > 6) {
            editPincode.setError("Enter a 6 digit pincode");
        } else if (area.length() == 0) {
            editArea.setError("Mention the area");
        } else if (city.length() == 0) {
            editCity.setError("city");
        } else if (state.length() == 0) {
            editState.setError("state");
        }else if (mobilenumber.length() == 0 || mobilenumber.length() < 13 || mobilenumber.length() > 13) {
            editmobile.setError("Enter valid Mobile Number");
        } else {
            Log.e("call", "addAddress");
            getlat = Double.parseDouble(getlatitude);
            getlang = Double.parseDouble(getlongitude);
            if (isConn()) {
                if (AddressController.getInstance().selectedAddressList == null) {
                    Progress.show(AddNewAddressActivity.this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timestamp = getCurrentTime();
                            Log.e("timestttt", " " +pincode+address+name+state+mobilenumber+city+area+landmark+userId+checkboxstatus+getlat+getlang+timestamp+merchant);
                            AddressController.getInstance().addnewAddress(AddNewAddressActivity.this, pincode, address, name, state, mobilenumber, city, area, landmark, userId, checkboxstatus, getlat, getlang, timestamp,merchant);
                        }
                    }, 1000);
                } else {
                    Progress.show(AddNewAddressActivity.this);
                    timestamp = AddressController.getInstance().selectedAddressList.getTimeStamp();
                    Log.e("timestttt123", " " + timestamp);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AddressController.getInstance().editAddress(AddNewAddressActivity.this, addressId, pincode, address, name, state, mobilenumber, city, area, landmark, userId, checkboxstatus, getlat, getlang, timestamp,merchant);
                        }
                    }, 1000);
                }
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ////////for unix time stamp
    public String getCurrentTime() {
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1000L);
        Log.e("attem", "" + attempt_time);
        return attempt_time;
    }

    /////display alert for invalid pin number
    public void displayAlterOnInvalidPin() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AddNewAddressActivity.this);

        // set dialog message
        alertDialogBuilder
                .setMessage("As of now delivery is available in only Karnataka region.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        Log.e("click", "invalid pin");
                        dialog.dismiss();

                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
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


