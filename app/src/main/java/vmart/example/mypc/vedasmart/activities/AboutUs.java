package vmart.example.mypc.vedasmart.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import vmart.example.mypc.vedasmart.R;

public class AboutUs extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    ImageView toolImage;
    TextView Name;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.aboutUsToolbar);
        toolImage = findViewById(R.id.backIcon);
        toolImage.setOnClickListener(new View.OnClickListener() {
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
        Name = findViewById(R.id.toolProductNameStart);
        Name.setText("About Us");
        Name.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        webView = findViewById(R.id.webview_AboutUs);
        webView.loadUrl("file:///android_asset/About us.htm");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

}