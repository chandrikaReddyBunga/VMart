package vmart.example.mypc.vedasmart.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.adapter.SubCategoriesAdapter;
import vmart.example.mypc.vedasmart.controllers.ProductsDataController;
import vmart.example.mypc.vedasmart.controllers.SubProductsDataController;
import vmart.example.mypc.vedasmart.model.Categories;
import vmart.example.mypc.vedasmart.model.Products;
import vmart.example.mypc.vedasmart.model.Subcategories;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;

public class CategoriesActivity extends AppCompatActivity {

    Toolbar categoriesToolbar;
    TextView toolbarText;
    RecyclerView recyclerView;
    ArrayList<Products> productsArrayList;
    private int selectedPos;
    ArrayList<Subcategories> subcategories;
    ArrayList<Categories> categories;
    SubCategoriesAdapter subCategoriesAdapter;
    ServerApiInterface serverApiInterface;
    ImageView backicon;
    private SharedPreferences sharedPreferences;
    String merchant;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        productsArrayList = new ArrayList<>();
        subcategories = new ArrayList<>();
        Intent intent = getIntent();
        selectedPos = intent.getIntExtra("selectedPos", 0);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        merchant = sharedPreferences.getString("merchantId","");
        productsArrayList = ProductsDataController.getInstance().productsArrayList;
        categories = productsArrayList.get(selectedPos).getCategories();
        subcategories.clear();
        subcategories = categories.get(selectedPos).getSubcategories();
        if (subcategories != null) {
            for (int i = 0; i < subcategories.size(); i++) {
            }
        }
        init();
    }

    public void init() {
        ////Toolbar
        categoriesToolbar = findViewById(R.id.cat_toolbar);
        setSupportActionBar(categoriesToolbar);
        backicon = findViewById(R.id.back_arrow);
        backicon.setOnClickListener(new View.OnClickListener() {
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.ic_more);
        categoriesToolbar.setOverflowIcon(drawable);
        toolbarText = findViewById(R.id.tool_category_name);
        toolbarText.setText(categories.get(selectedPos).getCategoryName());

        ///Recyclerview
        recyclerView = findViewById(R.id.categories_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (subcategories != null) {
            subCategoriesAdapter = new SubCategoriesAdapter(CategoriesActivity.this, subcategories);
            recyclerView.setAdapter(subCategoriesAdapter);
        } else {
           // Toast.makeText(getApplicationContext(), "No items are avialable for this product", Toast.LENGTH_SHORT).show();
        }
    }
    ///////Fetching the subitems from server
    public void fetchSubItems(int position, String selectedCategory) {
        String subCategoryId = subcategories.get(position).getSubCategoryId();
        SubProductsDataController.getInstance().fetchSubItemsData(subCategoryId, CategoriesActivity.this, selectedCategory,merchant);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}