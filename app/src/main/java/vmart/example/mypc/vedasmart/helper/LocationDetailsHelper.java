package vmart.example.mypc.vedasmart.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationDetailsHelper extends AppCompatActivity {

    private static LocationDetailsHelper instance;
    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int LOCATION_PERMISSION_CODE = 101;
    private Double latitude, longitude;
    private LocationManager locationManager;
    public static String pinCodeData;

    public LocationDetailsHelper(Context mContext) {
        this.context = mContext;
        // FusedLocationProvider Client object for getting the location access
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public static synchronized LocationDetailsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LocationDetailsHelper(context);
        }
        return instance;
    }

    public void requestLocationPermissions(final Context context) {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                Manifest.permission.ACCESS_FINE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

            new AlertDialog.Builder(context)
                    .setTitle("Permission Info")
                    .setMessage("Location Permissions are needed to Get Your Current Location PinCode")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();

        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    public boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public boolean checkForLocationSettingsEnabled() {
        if (!isLocationEnabled()) {
            showAlert(context);
        }
        return isLocationEnabled();
    }


    public String getFusedLocationDetails() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            List<Address> addressList;
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            try {
                                addressList = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addressList != null) {
                                    String postalCode = addressList.get(0).getPostalCode();
                                    if (!postalCode.isEmpty()) {
                                        pinCodeData = postalCode;
                                    } else {
                                        Toast.makeText(context, "Unable to Access your Location", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        return pinCodeData;
    }

    public String getHomeFusedLocationDetails() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            List<Address> addressList;
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            try {
                                addressList = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addressList != null) {
                                    String postalCode = addressList.get(0).getPostalCode();
                                    if (!postalCode.isEmpty()) {
                                        pinCodeData = postalCode;
                                        Toast.makeText(context, pinCodeData, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Unable to Access your Location", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        return pinCodeData;
    }

    public void showAlert(final Context methodContext) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(methodContext);
        dialog.setTitle("Locations are disabled in the phone")
                .setMessage("Would you like to turn on the locations now?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = dialog.create();
        // show it
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Progress.dismiss((Activity) methodContext);
    }
}
