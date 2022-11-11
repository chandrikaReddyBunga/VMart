package vmart.example.mypc.vedasmart.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vmart.example.mypc.vedasmart.R;

public class FrequentQuestions extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    TextView textHeading;
    WebView webView;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequent_questions);
        init();
    }

    public void init(){
        toolbar = findViewById(R.id.frequentquestions_toolbar);
        textHeading = findViewById(R.id.toolProductNameStart);
        textHeading.setText("FAQ");
        textHeading.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
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
        webView = findViewById(R.id.webview_frequentQuestions);
        webView.loadUrl("file:///android_asset/frequentquestions.html");
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();

    }
}
