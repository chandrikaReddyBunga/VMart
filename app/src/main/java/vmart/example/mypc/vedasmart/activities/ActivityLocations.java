package vmart.example.mypc.vedasmart.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.AddressController;
import vmart.example.mypc.vedasmart.controllers.PickupController;
import vmart.example.mypc.vedasmart.model.PincodeList;

public class ActivityLocations extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Marker options;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private double longitude;
    private double latitude;
    ArrayList<LatLng> markerPoints = new ArrayList<>();
    TextView toolbarText;
    String selectedlatitude, selectedlongitude;
    LatLng myPosition;
    RelativeLayout relative_back, currentlocation;
    String currentlat, currentlang;
    String na, ad, lan, pi, are, ci, sta, mo, fav;
    public List<PincodeList> pincodeArray;
    double d1, d2;
    Geocoder geocoder;
    List<Address> addresses;
    String postalCode;
    String[] pincodesArray = {"516434","518145","515001","560063", "560034", "560007", "560092", "560024", "562106", "560045", "560003", "560050", "562107", "560064", "560047", "560026", "560086", "560002", "560070", "560073", "562149", "560053", "560085", "560043", "560017", "560001", "560009", "560025", "560083", "560076", "560004", "560079", "560103", "560046", "562157", "560010", "560049", "560056", "560068", "560093", "560018", "560040", "560097", "560061", "562130", "560067", "560036", "560029", "560062", "560037", "560071", "562125", "560016", "560100", "560005", "560065", "560019", "560021", "560022", "560013", "560087", "560008", "560051", "560102", "560104", "560048", "560030", "560094", "560066", "560038", "560078", "560006", "560014", "560015", "560041", "560069", "560011", "560020", "560084", "560096", "560098", "560095", "560077", "560074", "560054", "560023", "560033", "560055", "560099", "560072", "560039", "560075", "560032", "560058", "560059", "560080", "560027", "560012", "560042", "560028", "560052", "560091"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Intent i = this.getIntent();
        if (AddressController.getInstance().selectedAddressList != null) {
            pi = AddressController.getInstance().selectedAddressList.getPinCode();
        } else {
            na = i.getStringExtra("name");
            ad = i.getStringExtra("address");
            lan = i.getStringExtra("landmark");
            pi = i.getStringExtra("pincode");
            are = i.getStringExtra("area");
            ci = i.getStringExtra("city");
            sta = i.getStringExtra("state");
            mo = i.getStringExtra("mobilenumber");
        }
        init();
        gettinglatlang();
        TextView current = findViewById(R.id.current);
        current.setText("Current Location");
    }

    public void init() {
        pincodeArray = PickupController.getInstance().pincodelistArrayList;
        /////////toolbar
        relative_back = findViewById(R.id.relative_back);
        toolbarText = findViewById(R.id.toolProductName);
        toolbarText.setText("Delivery Location");
        geocoder = new Geocoder(this, Locale.getDefault());
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("postal", "" + postalCode);
                Log.e("pincodeArray", "" + Arrays.asList(pincodesArray));
                dismissalert();
            }
        });
        currentlocation = findViewById(R.id.currentlocation);
        currentlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }

    public void gettinglatlang() {
        final Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(pi, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Use the address as needed
                String message = String.format("Latitude: %f, Longitude: %f",
                        address.getLatitude(), address.getLongitude());
                Log.e("addressgetLatitude", "" + address.getLatitude());
                Log.e("addressgetLongitude", "" + address.getLongitude());
                latitude = address.getLatitude();
                longitude = address.getLongitude();
            } else {
                // Display appropriate message when Geocoder services are not available
                Toast.makeText(this, "Unable to geocode zipcode", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // handle exception
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.e("gmap", " " + latitude + ", " + longitude);

        LatLng sydney = new LatLng(latitude, longitude);
        final LatLng latLng = sydney;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

        Log.e("sydney", " " + sydney);
        if (markerPoints.size() > 0) {
            markerPoints.clear();
            mMap.clear();
        }
        // Adding new item to the ArrayList
        markerPoints.add(latLng);

        // Creating MarkerOptions
        options = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(String.valueOf(latLng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        Log.e("latLng", " " + latLng.latitude + "," + latLng.longitude);
        selectedlatitude = String.valueOf(latLng.latitude);
        selectedlongitude = String.valueOf(latLng.longitude);

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            postalCode = addresses.get(0).getPostalCode();
            Log.e("postalcode", "" + postalCode);

        } catch (IOException e) {
            e.printStackTrace();
        }
        options.showInfoWindow();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                Log.e("myselectd loc", " " + d1+", "+d2+" ,"+latLng.latitude+","+latLng.longitude);
                //locationlatilongi(latLng.latitude , latLng.longitude);
                if (Arrays.asList(pincodesArray).contains(postalCode)) {
                    locationlatilongi(latLng.latitude, latLng.longitude);
                } else {
                    Toast.makeText(getApplicationContext(), "Services are not available in this location", Toast.LENGTH_SHORT).show();
                    mMap.clear();
                }
                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (markerPoints.size() > 0) {
                    markerPoints.clear();
                    mMap.clear();
                }
                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                options = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(String.valueOf(latLng))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                Log.e("latLng", " " + latLng.latitude + "," + latLng.longitude);
                selectedlatitude = String.valueOf(latLng.latitude);
                selectedlongitude = String.valueOf(latLng.longitude);

                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    postalCode = addresses.get(0).getPostalCode();
                    Log.e("postalcode", "" + postalCode);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Arrays.asList(pincodesArray).contains(postalCode)) {
                    locationlatilongi(latLng.latitude, latLng.longitude);
                } else {
                    Toast.makeText(getApplicationContext(), "Services are not available in this location", Toast.LENGTH_SHORT).show();
                    mMap.clear();

                }
                options.showInfoWindow();
            }
        });
    }


    public void dismissalert() {
        Log.e("passwordalert", "call");
        final Dialog dismiss = new Dialog(this);
        dismiss.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dismiss.setContentView(R.layout.validationalert);
        dismiss.show();

        TextView textView1 = dismiss.findViewById(R.id.textView1);
        TextView alert = dismiss.findViewById(R.id.text_info);

        alert.setText("Alert");
        textView1.setText("Location may be empty or no service available at selected location. Continue ?");
        Button done = dismiss.findViewById(R.id.btn_yes);
        done.setText("Yes");

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("call", "done");
                dismiss.dismiss();
                finish();
            }
        });
        Button button = dismiss.findViewById(R.id.btn_no);
        button.setText("No");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss.dismiss();
                // finish();
            }
        });
    }

    public void locationlatilongi(final double latitude, final double longitude) {
        Log.e("passwordalert", "call");
        final Dialog otpAlert = new Dialog(this);
        otpAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpAlert.setContentView(R.layout.locationalert);
        otpAlert.setCanceledOnTouchOutside(false);
        otpAlert.show();
        Button done = otpAlert.findViewById(R.id.btn_yes);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("call", "done");
                String latiii = String.valueOf(latitude);
                String longeee = String.valueOf(longitude);
                Intent sendingIntent = new Intent(getApplicationContext(), AddNewAddressActivity.class);
                if (AddressController.getInstance().selectedAddressList != null) {
                    sendingIntent.putExtra("doubleValue_e1", latiii);
                    sendingIntent.putExtra("doubleValue_e2", longeee);
                } else {
                    sendingIntent.putExtra("doubleValue_e1", latiii);
                    sendingIntent.putExtra("doubleValue_e2", longeee);
                    sendingIntent.putExtra("name1", na);
                    sendingIntent.putExtra("address1", ad);
                    sendingIntent.putExtra("land1", lan);
                    sendingIntent.putExtra("search", pi);
                    sendingIntent.putExtra("Area", are);
                    sendingIntent.putExtra("city", ci);
                    sendingIntent.putExtra("state", sta);
                    sendingIntent.putExtra("mobile1", mo);
                    sendingIntent.putExtra("favorite1",getIntent().getExtras().getBoolean("favorite"));
                    Log.e("latiii", "done" + latiii);
                    Log.e("longeee", "done" + longeee);
                }
                sendingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(sendingIntent);
                otpAlert.dismiss();

            }
        });
        Button button = otpAlert.findViewById(R.id.btn_no);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                otpAlert.dismiss();
                // finish();
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //  getCurrentLocation();
    }

    //Getting current location
    private void getCurrentLocation() {
        mMap.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PackageManager.PERMISSION_GRANTED);
            return;
        }
         mMap.setMyLocationEnabled(true);

        FusedLocationProviderClient location = LocationServices.getFusedLocationProviderClient(this);
        location.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            //Getting longitude and latitude
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            //moving the map to location
                            onMapReady(mMap);

                            myPosition = new LatLng(latitude, longitude);
                            d1 = latitude;
                            d2 = longitude;
                            // create marker
                            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello Maps ");
                            // adding marker
                            mMap.addMarker(marker);
                            try {
                                addresses = geocoder.getFromLocation(d1, d2, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                postalCode = addresses.get(0).getPostalCode();
                                Log.e("currentpostalcode", "" + postalCode);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (Arrays.asList(pincodesArray).contains(postalCode)) {
                                locationlatilongi(d1, d2);
                            } else {
                                mMap.clear();
                                markerPoints.clear();
                                Toast.makeText(getApplicationContext(), "Services are not available in this location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
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
