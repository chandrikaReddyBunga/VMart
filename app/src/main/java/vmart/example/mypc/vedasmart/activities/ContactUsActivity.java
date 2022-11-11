package vmart.example.mypc.vedasmart.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;

public class ContactUsActivity extends AppCompatActivity {
    EditText editName, editEmail, editMessage;
    Button submit;
    ImageView back;
    Toolbar toolbar;
    TextView toolTextName;
    Button callUsButton;
    public static String name, email, message;
    private final int CALL_REQUEST = 100;
    private SharedPreferences sharedPreferences;
    private String mobileNumber,merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId","");
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.titletoolbar);
        setSupportActionBar(toolbar);
        back = findViewById(R.id.backIcon);
        toolTextName = findViewById(R.id.toolProductNameStart);
        toolTextName.setText("Contact us");
        toolTextName.setVisibility(View.VISIBLE);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editMessage = findViewById(R.id.editMessage);
        submit = findViewById(R.id.submit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout relative_back = findViewById(R.id.relative_back);
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        callUsButton = findViewById(R.id.callUsButton);
        callUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+919154734093"));
                startActivity(callIntent);
            }
        });

        if (editName.length() > 0) {
        } else if (editEmail.length() > 0) {

        } else if (editMessage.length() > 0) {

        } else {
            submit.setBackgroundResource(R.color.colorGreen);
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
    }

    public void validation() {
        name = editName.getText().toString();
        email = editEmail.getText().toString().trim();
        message = editMessage.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (name.isEmpty() || name.length() < 4) {
            editName.setError("Enter Your Name");
        } else if (!email.matches(emailPattern)) {
            editEmail.setError("Enter Your Email");
        } else if (message.isEmpty() || message.length() < 5) {
            editMessage.setError( "Enter Your Message");
        } else {
            if (isConn()){
                if (mobileNumber.equals("")) {
                    displayAltertLogin();
                } else {
                    feedBack();
                }
            }else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /////display  if no user exist
    public void displayAltertLogin() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ContactUsActivity.this);
        // set dialog message
        alertDialogBuilder
                .setMessage("Please Sign In")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                        startActivity(new Intent(ContactUsActivity.this, LoginActivity.class));
                        finishAffinity();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        // show it
        alertDialog.show();
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    // Data Sending to Server for Feedback
    public void feedBack() {
        Progress.show(ContactUsActivity.this);
        name = editName.getText().toString();
        email = editEmail.getText().toString();
        message = editMessage.getText().toString();
        Log.e("feedGetDetails", "" + name + email + message);
        JsonObject cntObj = new JsonObject();

        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("emailAddress", email);
            obj.put("Message", message);
            obj.put("merchantId",merchant);
            obj.put("userId",mobileNumber);
            JsonParser jsonParser = new JsonParser();
            cntObj = (JsonObject) jsonParser.parse(obj.toString());
            //print parameter
            Log.e("feedMessage", " " + cntObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ServerApiInterface.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        ServerApiInterface api = retrofit.create(ServerApiInterface.class);
        Call<ResponseBody> callable = api.msgFeedback(cntObj);
        callable.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String statuscode, message;
                JSONArray contactJsonArray;
                JSONObject contactJsonObj;
                if (response.body() != null) {
                    String bodyString = null;
                    try {
                        bodyString = new String(response.body().bytes());
                        Log.e("otpResponse", " " + bodyString);
                        contactJsonArray = new JSONArray(bodyString);
                        contactJsonObj = contactJsonArray.getJSONObject(0);
                        statuscode = contactJsonObj.getString("response");
                        message = contactJsonObj.getString("message");
                        if (statuscode.equals("3")) {
                            Toast.makeText(getApplicationContext(), "Mail Sent Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), " " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Progress.dismiss(ContactUsActivity.this);

            }
        });
    }
    // Phone Back Button click
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();

    }
}


