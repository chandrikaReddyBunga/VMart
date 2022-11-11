package vmart.example.mypc.vedasmart.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import vmart.example.mypc.vedasmart.R;

public class PickUpPointList extends AppCompatActivity {
    Toolbar toolbar;
    TextView toolTextName;
    ImageView back;
    Button pickupBtn,searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_point_list);
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.frequentquestions_toolbar);
        setSupportActionBar(toolbar);
        toolTextName = findViewById(R.id.toolProductNameStart);
        toolTextName.setText( "pick up point");
        toolTextName.setVisibility(View.VISIBLE);
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
        pickupBtn = findViewById(R.id.pickUpBtn);
        pickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),PickUpPointList_One.class));
            }
        });
        searchBtn=findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PickUpPointList_One.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
