package vmart.example.mypc.vedasmart.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;;
import vmart.example.mypc.vedasmart.R;

public class PrivacyPolicy extends AppCompatActivity {
    Toolbar toolbar;
    Button search;
    ImageView toolbarImage;
    TextView Name;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        init();
    }
    public void init() {
        toolbar = findViewById(R.id.privacyToolbar);
        toolbarImage = findViewById(R.id.backIcon);
        toolbarImage.setOnClickListener(new View.OnClickListener() {
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
        Name.setText("Privacy Policy");
        Name.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        webView = findViewById(R.id.webview_frequentQuestions);
        webView.loadUrl("file:///android_asset/Privacy Policy.htm");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
