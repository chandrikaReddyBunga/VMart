package vmart.example.mypc.vedasmart.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.adapter.AdminProductAdapter;

public class AdminProductsList extends AppCompatActivity {

    RecyclerView sellerProductRecyclerview;
    Toolbar toolbar;
    TextView toolbarText;
    RelativeLayout backBtnLayout;
    AdminProductAdapter sellerProductAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_products_list);
        init();
    }


    public void init()
    {
        toolbar = findViewById(R.id.sellerProducttoolbar);
        setSupportActionBar(toolbar);
        toolbarText = findViewById(R.id.Name);
        toolbarText.setVisibility(View.VISIBLE);
        toolbarText.setText("Product List");
        backBtnLayout = findViewById(R.id.relative_back);
        backBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        sellerProductRecyclerview = findViewById(R.id.seller_product_recyclerview);
        sellerProductRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        sellerProductAdapter = new AdminProductAdapter(AdminProductsList.this);
        sellerProductRecyclerview.setAdapter(sellerProductAdapter);
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();

    }
}
