package vmart.example.mypc.vedasmart.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.model.ProductOverview;

public class SellerProductOverview extends AppCompatActivity {
    TableLayout tableLayout;
    ArrayList<ProductOverview> productOverviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_overview);
        productOverviews = new ArrayList<>();
        init();
    }

    public void init() {
        tableLayout = findViewById(R.id.table_weight_prices);
        ///adding the headers to table
        addHeader();
        setTableData();
    }
    ///////adding the headers to tablelayout
    public void addHeader() {
        TableRow tr = new TableRow(SellerProductOverview.this);
        tr.setLayoutParams(getLayoutParams());
        tr.addView(getTextView(0, "NetWeight", Color.BLACK, Typeface.NORMAL, getResources().getColor(R.color.white)));
        tr.addView(getTextView(0, "MRP", Color.BLACK, Typeface.NORMAL, getResources().getColor(R.color.white)));
        tr.addView(getTextView(0, "Selling Price", Color.BLACK, Typeface.NORMAL, getResources().getColor(R.color.white)));
        tableLayout.addView(tr, getTblLayoutParams());
    }
    ////////layout parameters for the table
    private TableRow.LayoutParams getLayoutParams() {
        TableRow.LayoutParams params = new TableRow.LayoutParams(300, 100);
        params.setMargins(2, 2, 2, 2);
        return params;
    }
    //////header Text for the table
    private TextView getTextView(int id, String title, int color, int typeface, int bgColor) {
        TextView tv = new TextView(getApplicationContext());
        tv.setId(id);
        tv.setText(title);
        tv.setTextColor(color);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setBackgroundColor(bgColor);
        tv.setLayoutParams(getLayoutParams());
        return tv;
    }
    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    }
    public void setTableData() {
        productOverviews.add(new ProductOverview("1 kg", "490", "350"));
        productOverviews.add(new ProductOverview("2 kg", "980", "700"));
        for (int i = 0; i < productOverviews.size(); i++) {
            TableRow tr = new TableRow(getApplicationContext());
            tr.setLayoutParams(getLayoutParams());
            tr.addView(getTextView(i + 1, productOverviews.get(i).getNetweight(), Color.BLACK, Typeface.NORMAL, getResources().getColor(R.color.white)));
            tr.addView(getTextView(i + productOverviews.size(), productOverviews.get(i).getMrp(), Color.BLACK, Typeface.NORMAL, getResources().getColor(R.color.white)));
            tr.addView(getTextView(i + productOverviews.size(), productOverviews.get(i).getSellingPrice(), Color.BLACK, Typeface.NORMAL, getResources().getColor(R.color.white)));
            tableLayout.addView(tr, getTblLayoutParams());
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
