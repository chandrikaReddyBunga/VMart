package vmart.example.mypc.vedasmart.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vmart.example.mypc.vedasmart.helper.InputFilterMinMax;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.CartController;
import vmart.example.mypc.vedasmart.controllers.HomeProductsDataController;
import vmart.example.mypc.vedasmart.fragments.SideMenuFragment;
import vmart.example.mypc.vedasmart.helper.LocationDetailsHelper;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.interfaces.AddorRemoveCallbacks;
import vmart.example.mypc.vedasmart.model.CartList;
import vmart.example.mypc.vedasmart.model.Product;
import vmart.example.mypc.vedasmart.model.ProductInfo;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;
import static vmart.example.mypc.vedasmart.controllers.CartController.add;
import static vmart.example.mypc.vedasmart.helper.LocationDetailsHelper.pinCodeData;

public class CartActivity extends AppCompatActivity implements AddorRemoveCallbacks {

    Toolbar toolbar;
    TextView toolProductName;
    RecyclerView cartRecyclerview;
    CartAdapter cartAdapter;
    RelativeLayout emptycartLayout, relativecartItems;
    Button removeAll, continueShopping, procedtocontine;
    ArrayList<CartList> cartLists;
    ImageView toolbarImage;
    SharedPreferences sharedPreferences;
    String mobileNumber;
    ServerApiInterface serverApiInterface;
    ScrollView cartDataScroll;
    int cartCount = 0, cartAmount = 0;
    String totalMrp, totalVmart, totalSavings = "0";
    TextView cartItems, cartSavings, carttotal;
    TextView Count, Total;
    RelativeLayout options_menu_layout;
    String cartID;
    ArrayList<Product> productArrayList;
    String pinCode;
    String[] pincodesArray = {"516434","518145","560063", "560034", "560007", "560092", "560024", "562106", "560045", "560003", "560050", "562107", "560064", "560047", "560026", "560086", "560002", "560070", "560073", "562149", "560053", "560085", "560043", "560017", "560001", "560009", "560025", "560083", "560076", "560004", "560079", "560103", "560046", "562157", "560010", "560049", "560056", "560068", "560093", "560018", "560040", "560097", "560061", "562130", "560067", "560036", "560029", "560062", "560037", "560071", "562125", "560016", "560100", "560005", "560065", "560019", "560021", "560022", "560013", "560087", "560008", "560051", "560102", "560104", "560048", "560030", "560094", "560066", "560038", "560078", "560006", "560014", "560015", "560041", "560069", "560011", "560020", "560084", "560096", "560098", "560095", "560077", "560074", "560054", "560023", "560033", "560055", "560099", "560072", "560039", "560075", "560032", "560058", "560059", "560080", "560027", "560012", "560042", "560028", "560052", "560091"};
    int previousSeectedPosition = -1;
    String merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartlist);
        CartController.getInstace().setListener(this);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        cartID = sharedPreferences.getString(LoginActivity.cartid, " ");
        mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId","");
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            productArrayList = (ArrayList<Product>) args.getSerializable("productArraylist");
            if (productArrayList != null) {
                Log.e("objectSize", " " + productArrayList.size());
            }
        }
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.cart_toolbar);
        toolProductName = findViewById(R.id.toolProductName);
        toolProductName.setText("View Cart");
        setSupportActionBar(toolbar);
        Count = findViewById(R.id.count);
        Total = findViewById(R.id.totalAmount);
        options_menu_layout = findViewById(R.id.options_menu_layout);
        options_menu_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartActivity.this, MyAccountActivity.class));
            }
        });

        toolbarImage = findViewById(R.id.backIcon);
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));

            }
        });
        RelativeLayout relative_back = findViewById(R.id.relative_back);
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });
        removeAll = findViewById(R.id.removeAllBtn);
        removeAll.setOnClickListener(removeClickEvent);
        continueShopping = findViewById(R.id.continueShoppingBtn);
        continueShopping.setOnClickListener(ClickEventOnContinueShopping);
        procedtocontine = findViewById(R.id.checkout);
        procedtocontine.setOnClickListener(ClickOnProceed);
        TextView text_cart_empty = findViewById(R.id.text_cart_empty);
        text_cart_empty.setText("your Shopping cart is empty! Start Shopping now");
        procedtocontine.setText("PROCEED TO CHECKOUT");
        removeAll.setText("Remove All");
        continueShopping.setText("Continue Shopping");
        cartRecyclerview = findViewById(R.id.cart_recyclerview);
        cartRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(CartActivity.this, cartLists);

        cartRecyclerview.setAdapter(cartAdapter);
        emptycartLayout = findViewById(R.id.relative_cart_empty);
        cartDataScroll = findViewById(R.id.scroll);
        cartItems = findViewById(R.id.cart_cout);
        cartSavings = findViewById(R.id.text_total_savings);
        carttotal = findViewById(R.id.text_total_price);
        relativecartItems = findViewById(R.id.relative_cart_items);
        cartLists = CartController.getInstace().cartListArray;
    }

    View.OnClickListener ClickOnProceed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isConn()) {
                /*if (!getPinStatus(CartActivity.this)) return;

                if (pinCodeData == null) {
                    if (!getPinStatus(CartActivity.this)) return;
                    return;
                } else if (!pinCodeData.isEmpty() && !checkIsPincodeMatched(pinCodeData)) {
                    CartController.getInstace().displayInvalidPincodeAlert(CartActivity.this);
                    return;
                }*/
                Intent intent = new Intent(CartActivity.this, DeliveryTypeActivity.class);
                intent.putExtra("cartamount", String.valueOf(cartAmount));
                intent.putExtra("cartsavings", totalSavings);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    View.OnClickListener removeClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isConn()) {
                if (!getPinStatus(CartActivity.this)) return;
                if (pinCodeData == null) {
                    if (!getPinStatus(CartActivity.this)) return;
                    return;
                } else if (!pinCodeData.isEmpty() && !checkIsPincodeMatched(pinCodeData) ) {
                    CartController.getInstace().displayInvalidPincodeAlert(CartActivity.this);
                    return;
                }
                displayAlterOnToClerCart();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private boolean checkIsPincodeMatched(String currentPincode) {
        for (int i = 0; i < pincodesArray.length; i++) {
            if (pincodesArray[i].equals(currentPincode)) {
                return true;
            }
        }
        pincodesArray = add(pincodesArray, currentPincode);
        return true;
    }

    View.OnClickListener ClickEventOnContinueShopping = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            startActivity(new Intent(CartActivity.this, HomeActivity.class));
        }
    };

    public boolean getPinStatus(Context mContext) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (!LocationDetailsHelper.getInstance(mContext).isLocationEnabled()) {
                LocationDetailsHelper.getInstance(mContext).showAlert(mContext);
                cartAdapter.notifyDataSetChanged();
                return false;
            } else {
                pinCode = LocationDetailsHelper.getInstance(mContext).getFusedLocationDetails();
                return true;
            }
        } else {
            LocationDetailsHelper.getInstance(mContext).requestLocationPermissions(mContext);
            return false;
        }
    }

    @Override
    public void onAddProduct(String cartCount, String cartAmount) {
        Log.e("listner", " " + cartCount + " " + cartAmount);
        totalSavings = "0";
        Count.setText(String.valueOf(cartCount));
        Total.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
        cartItems.setText(cartCount + " " + getString(R.string.cart_items));
        carttotal.setText("Total Price" + " " + "\u20B9" + cartAmount + ".00");

        for (int i = 0; i < cartLists.size(); i++) {
            totalMrp = getTotalMrp(cartLists.get(i).getQuantity(), cartLists.get(i).getMrpPrice());
            totalVmart = getTotalVmart(cartLists.get(i).getQuantity(), cartLists.get(i).getVmartPrice());
            totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));
            cartSavings.setText("Total Savings" + " " + "\u20B9" + totalSavings + ".00");
        }
        Progress.dismiss(CartActivity.this);
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        if (productArrayList != null) {
            Log.e("call", "cartonresume" + " " + productArrayList.size());
        }
        cartCount = 0;
        cartAmount = 0;
        totalSavings = "0";
        if (cartLists != null && cartLists.size() > 0) {
            cartDataScroll.setVisibility(View.VISIBLE);
            relativecartItems.setVisibility(View.VISIBLE);
            procedtocontine.setVisibility(View.VISIBLE);
            emptycartLayout.setVisibility(View.GONE);
            for (int i = 0; i < cartLists.size(); i++) {
                cartCount = cartCount + Integer.valueOf(cartLists.get(i).getQuantity());
                cartAmount = cartAmount + Integer.valueOf(cartLists.get(i).getQuantity()) * Integer.valueOf(cartLists.get(i).getVmartPrice());

                totalMrp = getTotalMrp(cartLists.get(i).getQuantity(), cartLists.get(i).getMrpPrice());
                totalVmart = getTotalVmart(cartLists.get(i).getQuantity(), cartLists.get(i).getVmartPrice());
                totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));

                cartItems.setText(cartCount + " " + "Items in cart");
                carttotal.setText("Total Price" + " " + "\u20B9" + " " + cartAmount + ".00");
                cartSavings.setText("Total Savings" + " " + "\u20B9" + " " + totalSavings + ".00");

                Count.setVisibility(View.VISIBLE);
                Count.setText(String.valueOf(cartCount));
                Total.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
            }
            cartAdapter = new CartAdapter(CartActivity.this, cartLists);
            cartRecyclerview.setAdapter(cartAdapter);
        } else {
            emptycartLayout.setVisibility(View.VISIBLE);
            cartDataScroll.setVisibility(View.GONE);
            procedtocontine.setVisibility(View.GONE);
            relativecartItems.setVisibility(View.GONE);
            Count.setVisibility(View.GONE);
            Total.setText("");
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SideMenuFragment.MessageEvent event) {
        String resultData = event.message.trim();
        if (resultData.equals("refreshCartData")) {
            cartCount = 0;
            cartAmount = 0;
            totalSavings = "0";
            if (cartLists != null && cartLists.size() > 0) {
                cartDataScroll.setVisibility(View.VISIBLE);
                relativecartItems.setVisibility(View.VISIBLE);
                procedtocontine.setVisibility(View.VISIBLE);
                emptycartLayout.setVisibility(View.GONE);
                for (int i = 0; i < cartLists.size(); i++) {
                    cartCount = cartCount + Integer.valueOf(cartLists.get(i).getQuantity());
                    cartAmount = cartAmount + Integer.valueOf(cartLists.get(i).getQuantity()) * Integer.valueOf(cartLists.get(i).getVmartPrice());
                    totalMrp = getTotalMrp(cartLists.get(i).getQuantity(), cartLists.get(i).getMrpPrice());
                    totalVmart = getTotalVmart(cartLists.get(i).getQuantity(), cartLists.get(i).getVmartPrice());
                    totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));
                    cartItems.setText(cartCount + " " + "Items in cart");
                    carttotal.setText( "Total Price" + " " + "\u20B9" + cartAmount);
                    cartSavings.setText( "Total Savings" + " " + "\u20B9" + totalSavings);
                    Count.setVisibility(View.VISIBLE);
                    Count.setText(String.valueOf(cartCount));
                    Total.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
                }
            } else {
                emptycartLayout.setVisibility(View.VISIBLE);
                cartDataScroll.setVisibility(View.GONE);
                procedtocontine.setVisibility(View.GONE);
                relativecartItems.setVisibility(View.GONE);
                Count.setVisibility(View.GONE);
                Total.setText("");
            }
            cartAdapter = new CartAdapter(CartActivity.this, cartLists);
            cartRecyclerview.setAdapter(cartAdapter);
            cartAdapter.notifyDataSetChanged();
            Progress.dismiss(CartActivity.this);
        } else if (resultData.equals("error")) {
            cartAdapter = new CartAdapter(CartActivity.this, cartLists);
            cartRecyclerview.setAdapter(cartAdapter);
            cartAdapter.notifyDataSetChanged();
            Progress.dismiss(CartActivity.this);
        }
    }

    class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

        ArrayList<CartList> cartLists = new ArrayList<>();
        //  AddorRemoveCallbacks listener;
        Context context;
        int lastItemPosition = -1;

        public CartAdapter(Context context, ArrayList<CartList> allCartLists) {
            this.context = context;
            this.cartLists = allCartLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_cart, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
            final CartList cartListObj = cartLists.get(position);
            viewHolder.CatName.setText(cartLists.get(position).getItemName());
            lastItemPosition = position;
            if (position > lastItemPosition) {
                // Scrolled Down
                closeKeyboard();
            } else {
                // Scrolled Up
                closeKeyboard();
            }

            viewHolder.CatQuntity.setText(cartLists.get(position).getNetWeight());
            totalMrp = getTotalMrp(cartLists.get(position).getQuantity(), cartLists.get(position).getMrpPrice());
            totalVmart = getTotalVmart(cartLists.get(position).getQuantity(), cartLists.get(position).getVmartPrice());
            totalSavings = getTotalSavings(totalMrp, totalVmart);
            viewHolder.CatSaving.setText("You Save" + " " + "\u20B9" + " " + totalSavings + ".00");
            viewHolder.CatmartCost.setText("You Pay" + " " + context.getResources().getString(R.string.rupee) + " " + totalVmart + ".00");
            viewHolder.CatCost.setVisibility(View.GONE);
            viewHolder.CatCost.setText( "on MRP" + "Rs." + totalMrp);
            viewHolder.count.setText(cartLists.get(position).getQuantity());
            Glide.with(getApplicationContext()).load(cartLists.get(position).getUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loadingpic1))
                    .into(viewHolder.catImage);
            viewHolder.count.setFilters(new InputFilter[]{new InputFilterMinMax("1", "15", getApplicationContext())});
            viewHolder.count.setFocusable(false);

            viewHolder.count.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isConn()) {
                        viewHolder.count.setFocusableInTouchMode(true);
                        viewHolder.count.post(new Runnable() {
                            @Override
                            public void run() {
                                viewHolder.count.requestFocus();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });

            viewHolder.count.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (previousSeectedPosition != viewHolder.getLayoutPosition()) {
                            if (previousSeectedPosition >= 0) {
                                notifyItemChanged(previousSeectedPosition);
                            }
                            previousSeectedPosition = viewHolder.getAdapterPosition();
                        }
                    } else {
                        viewHolder.count.setFocusableInTouchMode(false);
                        closeKeyboard();
                    }
                }
            });

            viewHolder.count.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getCurrentFocus() == viewHolder.count) {
                        //restrict 0 as in first lettter in keypad
                        if (s.toString().startsWith("0")) {
                            viewHolder.count.setText(" ");
                            Toast.makeText(getApplicationContext(), "Maximum 15 units are allowed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void afterTextChanged(Editable s) { }
            });
            viewHolder.count.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (isConn()) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            final String quantity = viewHolder.count.getText().toString();
                            if (!quantity.isEmpty()) {
                                cartCount = Integer.valueOf(quantity);
                            } else {
                                cartCount = 0;
                            }
                            cartAmount = cartCount * Integer.valueOf(cartLists.get(viewHolder.getAdapterPosition()).getVmartPrice());
                            if (quantity.isEmpty() | quantity.equals("0")) {
                                closeKeyboard();
                                viewHolder.count.setText(quantity);
                                notifyDataSetChanged();
                            /*CartList cartListObj = cartLists.get(viewHolder.getAdapterPosition());
                            if (cartLists.contains(cartListObj)) {
                                int index = cartLists.indexOf(cartListObj);
                                deleteSingleCartItem(cartLists.get(viewHolder.getAdapterPosition()).getProductId(), mobileNumber,
                                        cartLists.get(viewHolder.getAdapterPosition()).getNetWeight(), index,
                                        cartListObj.getMrpPrice(), cartListObj.getVmartPrice(), cartListObj.getUrl());
                                notifyDataSetChanged();
                            }*/
                             if (cartLists.size() == 0) {
                                    emptycartLayout.setVisibility(View.VISIBLE);
                                    cartDataScroll.setVisibility(View.GONE);
                                    procedtocontine.setVisibility(View.GONE);
                                    relativecartItems.setVisibility(View.GONE);
                                    Count.setVisibility(View.GONE);
                                    Total.setText("");
                                }
                            } else {
                                closeKeyboard();
                                Progress.show(CartActivity.this);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CartController.getInstace().updateCart(CartActivity.this,
                                                cartLists.get(viewHolder.getAdapterPosition()).getMrpPrice(),
                                                cartLists.get(viewHolder.getAdapterPosition()).getVmartPrice(),
                                                quantity, cartLists.get(viewHolder.getAdapterPosition()).getNetWeight(),
                                                mobileNumber, cartLists.get(viewHolder.getAdapterPosition()).getProductId(),
                                                cartID, cartLists.get(viewHolder.getAdapterPosition()).getItemName(),
                                                getCurrentTime(), cartCount, cartAmount,
                                                cartLists.get(viewHolder.getAdapterPosition()).getUrl(),merchant);
                                        cartAdapter.notifyDataSetChanged();
                                    }
                                }, 1000);
                            }
                            return true;
                        }
                    } else {
                        final String quantity = viewHolder.count.getText().toString();
                        closeKeyboard();
                        viewHolder.count.setText(quantity);
                        notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
            viewHolder.addCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                    Log.e("addCountClickPos", " " + viewHolder.getAdapterPosition() + cartListObj.getQuantity() + cartLists.get(viewHolder.getAdapterPosition()).getQuantity());
                        if (!viewHolder.minusCount.isEnabled()) {
                            viewHolder.minusCount.setEnabled(true);
                        }
                    int addCount = Integer.valueOf(cartLists.get(viewHolder.getAdapterPosition()).getQuantity());
                    addCount = addCount + 1;
                    if (addCount > 15) {
                        Toast.makeText(CartActivity.this, "Maximum 15 units are allowed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cartCount++;
                    cartAmount = cartCount * Integer.valueOf(cartLists.get(viewHolder.getAdapterPosition()).getVmartPrice());
                    ///updating the cart
                        Progress.show(CartActivity.this);
                        final int finalAddCount = addCount;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CartController.getInstace().updateCart(CartActivity.this,
                                        cartLists.get(viewHolder.getAdapterPosition()).getMrpPrice(),
                                        cartLists.get(viewHolder.getAdapterPosition()).getVmartPrice(),
                                        String.valueOf(finalAddCount),
                                        cartLists.get(viewHolder.getAdapterPosition()).getNetWeight(),
                                        mobileNumber, cartLists.get(viewHolder.getAdapterPosition()).getProductId(),
                                        cartID, cartLists.get(viewHolder.getAdapterPosition()).getItemName(), getCurrentTime(),
                                        cartCount, cartAmount, cartLists.get(viewHolder.getAdapterPosition()).getUrl(),merchant);
                                cartAdapter.notifyDataSetChanged();
                            }
                        }, 1000);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            viewHolder.minusCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                    int minusCount = Integer.valueOf(cartLists.get(viewHolder.getAdapterPosition()).getQuantity());
                    minusCount = --minusCount;
                        if (minusCount == 0) {
                            viewHolder.minusCount.setEnabled(false);
                            cartCount--;
                            cartAmount = cartAmount - Integer.valueOf(cartLists.get(viewHolder.getAdapterPosition()).getVmartPrice());
                            final CartList cartListObj = cartLists.get(viewHolder.getAdapterPosition());
                            if (cartLists.contains(cartListObj)) {
                                final int index = cartLists.indexOf(cartListObj);
                                Progress.show(CartActivity.this);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteSingleCartItem(cartLists.get(viewHolder.getAdapterPosition()).getProductId(), mobileNumber,
                                                cartLists.get(viewHolder.getAdapterPosition()).getNetWeight(), index,
                                                cartListObj.getMrpPrice(), cartListObj.getVmartPrice(), cartListObj.getUrl(),merchant);
                                        cartAdapter.notifyDataSetChanged();
                                    }
                                }, 1000);
                            }
                            if (cartLists.size() == 0) {
                                emptycartLayout.setVisibility(View.VISIBLE);
                                cartDataScroll.setVisibility(View.GONE);
                                procedtocontine.setVisibility(View.GONE);
                                relativecartItems.setVisibility(View.GONE);
                                Count.setVisibility(View.GONE);
                                Total.setText("");
                            }
                        } else {
                            cartCount--;
                            cartAmount = cartAmount - Integer.valueOf(cartLists.get(viewHolder.getAdapterPosition()).getVmartPrice());
                            ///updating the cart for minus count
                            Progress.show(CartActivity.this);
                            final int finalMinusCount = minusCount;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CartController.getInstace().updateCart(CartActivity.this,
                                            cartLists.get(viewHolder.getAdapterPosition()).getMrpPrice(),
                                            cartLists.get(viewHolder.getAdapterPosition()).getVmartPrice(),
                                            String.valueOf(finalMinusCount),
                                            cartLists.get(viewHolder.getAdapterPosition()).getNetWeight(),
                                            mobileNumber, cartLists.get(viewHolder.getAdapterPosition()).getProductId(),
                                            cartID, cartLists.get(viewHolder.getAdapterPosition()).getItemName(),
                                            getCurrentTime(), cartCount, cartAmount,
                                            cartLists.get(viewHolder.getAdapterPosition()).getUrl(),merchant);
                                    cartAdapter.notifyDataSetChanged();
                                }
                            }, 1000);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        final CartList cartListObj = cartLists.get(viewHolder.getAdapterPosition());
                        if (cartLists.contains(cartListObj)) {
                            final int index = cartLists.indexOf(cartListObj);
                            Progress.show(CartActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    deleteSingleCartItem(cartLists.get(viewHolder.getAdapterPosition()).getProductId(), mobileNumber, cartLists.get(viewHolder.getAdapterPosition()).getNetWeight(), index, cartListObj.getMrpPrice(), cartListObj.getVmartPrice(), cartListObj.getUrl(),merchant);
                                    Progress.dismiss(CartActivity.this);
                                    cartAdapter.notifyDataSetChanged();
                                }
                            }, 1000);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CartList cartListObj = cartLists.get(viewHolder.getAdapterPosition());
                    for (int i = 0; i < HomeProductsDataController.getInstance().homeProductsArraylist.size(); i++) {
                        if (cartListObj.getProductId().equals(HomeProductsDataController.getInstance().homeProductsArraylist.get(i).getProductId())) {
                            Product productObj = HomeProductsDataController.getInstance().homeProductsArraylist.get(i);
                            Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("selectedProdouct", "caProducts");
                            intent.putExtra("cartSelectedPos", viewHolder.getAdapterPosition());
                            Bundle b = new Bundle();
                            b.putSerializable("cart", productObj);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    }
                }
            });
            viewHolder.remove.setText("Remove");
        }
        @Override
        public int getItemCount() {
            if (cartLists != null) {
                return cartLists.size();
            } else {
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView CatName, CatCost, CatmartCost, CatSaving, CatQuntity, remove;
            Button qunatityBtn;
            TextView addTocart;
            TextView addCount, minusCount;
            EditText count;
            ImageView catImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                CatName = itemView.findViewById(R.id.cat_title);
                CatCost = itemView.findViewById(R.id.text_mrp);
                CatmartCost = itemView.findViewById(R.id.text_vmart);
                CatQuntity = itemView.findViewById(R.id.cat_quantity);
                CatSaving = itemView.findViewById(R.id.text_save);
                qunatityBtn = itemView.findViewById(R.id.quantity_btn);
                addTocart = itemView.findViewById(R.id.btn_addTocart);
                addCount = itemView.findViewById(R.id.text_plus);
                count = itemView.findViewById(R.id.text_count_);
                minusCount = itemView.findViewById(R.id.text_minus);
                catImage = itemView.findViewById(R.id.cat_img);
                remove = itemView.findViewById(R.id.remove);
            }
        }
    }
    /////display alert on click of remove button
    public void displayAlterOnToClerCart() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
        // set dialog message
        alertDialogBuilder
                .setTitle("Are you sure?")
                .setMessage("Do you want remove all products from cart?")
                //.setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //dialog.cancel();
                        deleteAllCartData(mobileNumber,merchant);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        // show it
        alertDialog.show();
    }

    ////////Delete all the cartData
    public void deleteAllCartData(String mobileNumber,String merchant) {
        Progress.show(CartActivity.this);
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> call = serverApiInterface.deleteAllItemsInCart(mobileNumber,merchant);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String body, statuscode, message;
                JSONArray resposneArray;
                JSONObject jsonObject;
                if (response.body() != null) {
                    try {
                        body = new String(response.body().bytes());
                        resposneArray = new JSONArray(body);
                        jsonObject = resposneArray.getJSONObject(0);
                        statuscode = jsonObject.getString("response");
                        message = jsonObject.getString("message");
                        if (statuscode.equals("3")) {
                            cartLists.clear();
                            cartAdapter.notifyDataSetChanged();
                            emptycartLayout.setVisibility(View.VISIBLE);
                            cartDataScroll.setVisibility(View.GONE);
                            procedtocontine.setVisibility(View.GONE);
                            relativecartItems.setVisibility(View.GONE);
                            Count.setVisibility(View.GONE);
                            Total.setText("");
                            Progress.dismiss(CartActivity.this);
                        } else if (statuscode.equals("0")) {
                            Progress.dismiss(CartActivity.this);
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
                Progress.dismiss(CartActivity.this);
            }
        });
    }

    ////////delete single item in cart
    public void deleteSingleCartItem(final String productId, String mobileNumber, final String netWeight, final int index, final String mrpPrice, final String vmartPrice, final String url,String merchant) {
        if (!getPinStatus(CartActivity.this)) return;
        if (pinCodeData == null) {
            if (!getPinStatus(CartActivity.this)) return;
            return;
        } else if (!pinCodeData.isEmpty() && !checkIsPincodeMatched(pinCodeData)) {
            CartController.getInstace().displayInvalidPincodeAlert(CartActivity.this);
            return;
        }
        JsonObject deleteCart = new JsonObject();
        JSONObject deletecartObj = new JSONObject();
        try {
            deletecartObj.put("netWeight", netWeight);
            deletecartObj.put("mobileNumber", mobileNumber);
            deletecartObj.put("productId", productId);
            deletecartObj.put("merchantId",merchant);
            JsonParser jsonParser = new JsonParser();
            deleteCart = (JsonObject) jsonParser.parse(deletecartObj.toString());
            //Add to cart parameter
            Log.e("addcartJson:", " " + deleteCart);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> deletecall = serverApiInterface.deleteCart(deleteCart);
        deletecall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    String deleteCartResponse = null;
                    String statuscode, message;
                    JSONArray deletecartArray;
                    JSONObject deleteCartJsonObj;
                    try {
                        deleteCartResponse = new String(response.body().bytes());
                        deletecartArray = new JSONArray(deleteCartResponse);
                        deleteCartJsonObj = deletecartArray.getJSONObject(0);
                        statuscode = deleteCartJsonObj.getString("response");
                        message = deleteCartJsonObj.getString("message");
                        if (statuscode.equals("3")) {
                            ProductInfo productInfo = new ProductInfo();
                            productInfo.setSelectedQnt(Integer.valueOf(0));//1
                            productInfo.setMrp(mrpPrice);
                            productInfo.setVmartPrice(vmartPrice);
                            productInfo.setQuantity(netWeight);
                            productInfo.setUrl(url);
                            Product product = null;
                            for (int i = 0; i < HomeProductsDataController.getInstance().homeProductsArraylist.size(); i++) {
                                if (HomeProductsDataController.getInstance().homeProductsArraylist.get(i).getProductId().equals(productId)) {
                                    product = HomeProductsDataController.getInstance().homeProductsArraylist.get(i);
                                }
                            }
                            if (HomeProductsDataController.getInstance().homeProductsArraylist.contains(product)) {
                                int index = HomeProductsDataController.getInstance().homeProductsArraylist.indexOf(product);
                                int chideIndex;
                                for (int i = 0; i < HomeProductsDataController.getInstance().homeProductsArraylist.size(); i++) {
                                    String pid = HomeProductsDataController.getInstance().homeProductsArraylist.get(i).getProductId();
                                    ArrayList<ProductInfo> addTocartProductArrayList = HomeProductsDataController.getInstance().homeProductsArraylist.get(i).getPinfo();
                                    for (int j = 0; j < addTocartProductArrayList.size(); j++) {
                                        if (pid.equals(productId) && netWeight.equals(addTocartProductArrayList.get(j).getQuantity())) {
                                            ProductInfo producinfoObj = addTocartProductArrayList.get(j);
                                            if (addTocartProductArrayList.contains(producinfoObj)) {
                                                chideIndex = addTocartProductArrayList.indexOf(producinfoObj);
                                                addTocartProductArrayList.set(chideIndex, productInfo);
                                                product.setPinfo(addTocartProductArrayList);
                                            }
                                        }
                                    }
                                }
                                HomeProductsDataController.getInstance().homeProductsArraylist.set(index, product);
                            }
                            cartLists.remove(index);
                            cartAdapter.notifyDataSetChanged();
                            cartCount = 0;
                            cartAmount = 0;
                            if (cartLists != null && cartLists.size() > 0) {
                                for (int i = 0; i < cartLists.size(); i++) {
                                    cartCount = cartCount + Integer.valueOf(cartLists.get(i).getQuantity());
                                    cartAmount = cartAmount + Integer.valueOf(cartLists.get(i).getQuantity()) * Integer.valueOf(cartLists.get(i).getVmartPrice());
                                    totalMrp = getTotalMrp(cartLists.get(i).getQuantity(), cartLists.get(i).getMrpPrice());
                                    totalVmart = getTotalVmart(cartLists.get(i).getQuantity(), cartLists.get(i).getVmartPrice());
                                    totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));
                                    cartItems.setText(cartCount + " " + "Items in cart");
                                    carttotal.setText("Total Price"+ " " + "\u20B9" + cartAmount);
                                    cartSavings.setText( "Total Savings" + " " + "\u20B9" + totalSavings);
                                    Count.setVisibility(View.VISIBLE);
                                    Count.setText(String.valueOf(cartCount));
                                    Total.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
                                }

                            } else {
                                emptycartLayout.setVisibility(View.VISIBLE);
                                cartDataScroll.setVisibility(View.GONE);
                                procedtocontine.setVisibility(View.GONE);
                                relativecartItems.setVisibility(View.GONE);
                                Count.setVisibility(View.GONE);
                                Total.setText("");
                            }
                            Progress.dismiss(CartActivity.this);
                        } else if (statuscode.equals("0")) {
                            Progress.dismiss(CartActivity.this);
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
                Progress.dismiss(CartActivity.this);
            }
        });
    }
    ///getting the total Mrp for adding
    public String getTotalMrp(String count, String mrp) {
        int totalmrp = Integer.valueOf(count) * Integer.valueOf(mrp);
        return String.valueOf(totalmrp);
    }
    ///getting the totalVmart for adding
    public String getTotalVmart(String count, String vmart) {
        int totalVmart = Integer.valueOf(count) * Integer.valueOf(vmart);
        return String.valueOf(totalVmart);
    }
    public String getTotalSavings(String totalMrp, String totalVmart) {
        String totalSavings = String.valueOf(Integer.valueOf(totalMrp) - Integer.valueOf(totalVmart));
        return totalSavings;
    }
    public String getCurrentTime() {
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1000L);
        return attempt_time;
    }
    // closing the keyboard for editext(Search functionality)
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}