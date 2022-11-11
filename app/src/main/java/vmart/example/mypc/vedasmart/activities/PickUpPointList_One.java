package vmart.example.mypc.vedasmart.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import vmart.example.mypc.vedasmart.R;

public class PickUpPointList_One extends AppCompatActivity
{
    Toolbar toolbar;
    TextView toolTextName;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickuppoint_list_one);
        init();
    }
    private void init() {
        toolbar = findViewById(R.id.frequentquestions_toolbar);
        setSupportActionBar(toolbar);
        toolTextName = findViewById(R.id.toolProductNameStart);
        toolTextName.setText("pick up point list");
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
