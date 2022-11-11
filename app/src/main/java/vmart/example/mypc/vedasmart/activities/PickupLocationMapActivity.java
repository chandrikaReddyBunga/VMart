package vmart.example.mypc.vedasmart.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import vmart.example.mypc.vedasmart.R;

public class PickupLocationMapActivity extends FragmentActivity implements OnMapReadyCallback {
    Toolbar maptoolabr;
    TextView toolbarText;
    String latitude,longitude;
    LatLng latLng;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_location_map);
        //getting latitude and longitude from pickuploaction Adapter
        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        Log.e("selectedlatitude&long"," "+latitude+" "+longitude);
        init();
    }
    public void init()
    {
        maptoolabr = findViewById(R.id.map_toolbar);
        toolbarText = findViewById(R.id.toolProductNameStart);
        toolbarText.setText("Display Current Loaction");
        toolbarText.setVisibility(View.VISIBLE);
        back = findViewById(R.id.backIcon);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RelativeLayout relative_back=findViewById(R.id.relative_back);
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Getting Reference to SupportMapFragment of activity_map.xml
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // Getting reference to google map
        fm.getMapAsync( this);
        latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //work with different map types
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions()
                .position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        float zoomLevel = (float) 18.0;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
