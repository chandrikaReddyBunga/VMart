package vmart.example.mypc.vedasmart.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.CartController;
import vmart.example.mypc.vedasmart.controllers.HomeProductsDataController;
import vmart.example.mypc.vedasmart.controllers.WishlistController;
import vmart.example.mypc.vedasmart.fragments.SideMenuFragment;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.interfaces.AddorRemoveCallbacks;
import vmart.example.mypc.vedasmart.model.CartList;
import vmart.example.mypc.vedasmart.model.Product;
import vmart.example.mypc.vedasmart.model.ProductInfo;
import vmart.example.mypc.vedasmart.model.WishList;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;

public class WishListActivity extends AppCompatActivity implements AddorRemoveCallbacks {
    Toolbar toolbar;
    TextView toolProductName,text_wish_empty;
    RecyclerView wishRecyclerview;
    WishListActivity.WishAdapter wishAdapter;
    RelativeLayout emptycartLayout;
    Button removeAll;
    ArrayList<WishList> wishLists;
    ArrayList<CartList> cartLists;
    ImageView toolbarImage;
    SharedPreferences sharedPreferences;
    String mobileNumber, merchant;
    ServerApiInterface serverApiInterface;
    ScrollView wishDataScroll;
    int cartCount = 0, cartAmount = 0;
    String totalMrp, totalVmart, totalSavings;
    TextView itemsCount, itemsAmount;
    RelativeLayout options_menu_layout;
    String cartID;
    ArrayList<Product> productArrayList;
    private ImageView cartImage;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        WishlistController.getInstace().setListener(this);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        cartID = sharedPreferences.getString(LoginActivity.cartid, " ");
        mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId", "");
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            productArrayList = (ArrayList<Product>) args.getSerializable("productArraylist");
            if (productArrayList != null) {
            }
        }
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.wishlist_toolbar);
        toolProductName = findViewById(R.id.wishlist_title);
        toolProductName.setText("WISHLIST");
        setSupportActionBar(toolbar);
        options_menu_layout = findViewById(R.id.options_menu_layout);
        options_menu_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WishListActivity.this, MyAccountActivity.class));
            }
        });
        toolbarImage = findViewById(R.id.backIcon);
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        RelativeLayout relative_back = findViewById(R.id.relative_back);
        relative_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });
        cartImage = findViewById(R.id.icon_badge);
        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WishListActivity.this, CartActivity.class));
            }
        });
        removeAll = findViewById(R.id.removeAllBtn);
        removeAll.setOnClickListener(removeClickEvent);
        wishRecyclerview = findViewById(R.id.wishlist_recyclerview);
        wishRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        wishAdapter = new WishAdapter(WishListActivity.this, wishLists, productArrayList);
        wishRecyclerview.setAdapter(wishAdapter);
        emptycartLayout = findViewById(R.id.relative_wish_empty);
        wishDataScroll = findViewById(R.id.scroll);
        itemsCount = findViewById(R.id.count);
        itemsAmount = findViewById(R.id.totalAmount);
        text_wish_empty = findViewById(R.id.text_wish_empty);
        wishLists = WishlistController.getInstace().wishListArray;
        cartLists = CartController.getInstace().cartListArray;
        wishAdapter.notifyDataSetChanged();
    }

    View.OnClickListener removeClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isConn()) {
                displayAlterOnToClerWish();
            } else {
                Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onAddProduct(String cartCount, String cartAmount) {
        itemsCount.setText(cartCount);
        itemsAmount.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
        for (int i = 0; i < cartLists.size(); i++) {
            totalMrp = getTotalMrp(cartLists.get(i).getQuantity(), cartLists.get(i).getMrpPrice());
            totalVmart = getTotalVmart(cartLists.get(i).getQuantity(), cartLists.get(i).getVmartPrice());
            totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));
        }
        if (Integer.valueOf(cartCount) == 0 && Integer.valueOf(cartAmount) == 0) {
            itemsCount.setVisibility(View.GONE);
            itemsAmount.setText("");
        }
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        cartCount = 0;
        cartAmount = 0;
        totalSavings = "0";
        if (wishLists != null && wishLists.size() > 0) {
            WishlistController.getInstace().FetchWishData(WishListActivity.this, mobileNumber, merchant);
            wishDataScroll.setVisibility(View.VISIBLE);
            emptycartLayout.setVisibility(View.GONE);
            wishAdapter = new WishAdapter(WishListActivity.this, wishLists, productArrayList);
            wishRecyclerview.setAdapter(wishAdapter);
        } else {
            emptycartLayout.setVisibility(View.VISIBLE);
            wishDataScroll.setVisibility(View.GONE);
        }
        if (cartLists != null && cartLists.size() > 0) {
            for (int i = 0; i < cartLists.size(); i++) {
                cartCount = cartCount + Integer.valueOf(cartLists.get(i).getQuantity());
                cartAmount = cartAmount + Integer.valueOf(cartLists.get(i).getQuantity()) * Integer.valueOf(cartLists.get(i).getVmartPrice());
                totalMrp = getTotalMrp(cartLists.get(i).getQuantity(), cartLists.get(i).getMrpPrice());
                totalVmart = getTotalVmart(cartLists.get(i).getQuantity(), cartLists.get(i).getVmartPrice());
                totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));
                itemsCount.setVisibility(View.VISIBLE);
                itemsCount.setText(String.valueOf(cartCount));
                itemsAmount.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
            }
        } else {
            itemsCount.setVisibility(View.GONE);
            itemsAmount.setText("");
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
        cartCount = 0;
        cartAmount = 0;
        totalSavings = "0";
        if (resultData.equals("refreshWishData")) {
            wishAdapter = new WishAdapter(WishListActivity.this, wishLists, productArrayList);
            wishRecyclerview.setAdapter(wishAdapter);
            wishAdapter.notifyDataSetChanged();
            Progress.dismiss(WishListActivity.this);
        }
        if (resultData.equals("refreshCartData")) {
            if (wishLists != null && wishLists.size() > 0) {
                wishDataScroll.setVisibility(View.VISIBLE);
                emptycartLayout.setVisibility(View.GONE);
            } else {
                emptycartLayout.setVisibility(View.VISIBLE);
                wishDataScroll.setVisibility(View.GONE);
            }
            wishAdapter = new WishAdapter(WishListActivity.this, wishLists, productArrayList);
            wishRecyclerview.setAdapter(wishAdapter);
            wishAdapter.notifyDataSetChanged();
        } else if (resultData.equals("error")) {
            wishAdapter = new WishAdapter(WishListActivity.this, wishLists, productArrayList);
            wishRecyclerview.setAdapter(wishAdapter);
            wishAdapter.notifyDataSetChanged();
        }
        if (cartLists != null && cartLists.size() > 0) {
            for (int i = 0; i < cartLists.size(); i++) {
                cartCount = cartCount + Integer.valueOf(cartLists.get(i).getQuantity());
                cartAmount = cartAmount + Integer.valueOf(cartLists.get(i).getQuantity()) * Integer.valueOf(cartLists.get(i).getVmartPrice());
                totalMrp = getTotalMrp(cartLists.get(i).getQuantity(), cartLists.get(i).getMrpPrice());
                totalVmart = getTotalVmart(cartLists.get(i).getQuantity(), cartLists.get(i).getVmartPrice());
                totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));
                itemsCount.setVisibility(View.VISIBLE);
                itemsCount.setText(String.valueOf(cartCount));
                itemsAmount.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
            }
        } else {
            itemsCount.setVisibility(View.GONE);
            itemsAmount.setText("");
        }

    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    private class WishAdapter extends RecyclerView.Adapter<WishAdapter.ViewHolder> {
        ArrayList<WishList> wishLists;
        ArrayList<CartList> cartList;
        ArrayList<Product> productArrayList;
        Context context;
        ArrayList<ProductInfo> list = new ArrayList<>();

        public WishAdapter(Context context, ArrayList<WishList> allWishLists, ArrayList<Product> productArrayList) {
            this.context = context;
            this.wishLists = allWishLists;
            this.cartList = CartController.getInstace().cartListArray;
            this.productArrayList = productArrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_whislistcart, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
            viewHolder.CatName.setText(wishLists.get(position).getItemName());
            if (wishLists != null) {
                for (int i = 0; i < HomeProductsDataController.getInstance().homeProductsArraylist.size(); i++) {
                    if (wishLists.get(position).getProductId().equals(HomeProductsDataController.getInstance().homeProductsArraylist.get(i).getProductId())) {
                        list = HomeProductsDataController.getInstance().homeProductsArraylist.get(i).getPinfo();
                        String s = "1kg";
                        String quqntity = s.substring(0, s.length() - 2);
                        totalMrp = getTotalMrp(quqntity, list.get(0).getMrp());
                        totalVmart = getTotalVmart(quqntity, list.get(0).getVmartPrice());
                        totalSavings = getTotalSavings(totalMrp, totalVmart);
                        viewHolder.CatSaving.setText("You Save" + "  " + "\u20B9" + " " + totalSavings + ".00");
                        viewHolder.CatCost.setText("on MRP" +"Rs." + totalMrp);
                        viewHolder.CatmartCost.setText("You Pay" + " " + context.getResources().getString(R.string.rupee) + " " + totalVmart + ".00");
                        Glide.with(getApplicationContext()).load(list.get(0).getUrl())
                                .apply(new RequestOptions().placeholder(R.drawable.loadingpic1))
                                .into(viewHolder.catImage);
                    }
                }
            }
            viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        WishList wishListObj = wishLists.get(viewHolder.getAdapterPosition());
                        if (wishLists.contains(wishListObj)) {
                            int index = wishLists.indexOf(wishListObj);
                            deleteSingleWishItem(wishLists.get(viewHolder.getAdapterPosition()).getProductId(), mobileNumber,
                                    wishLists.get(viewHolder.getAdapterPosition()).getNetWeight(), index, wishListObj.getMrpPrice(),
                                    wishListObj.getVmartPrice(), wishListObj.getUrl(), merchant);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WishList wishListObj = wishLists.get(viewHolder.getAdapterPosition());
                    for (int i = 0; i < HomeProductsDataController.getInstance().homeProductsArraylist.size(); i++) {
                        if (wishListObj.getProductId().equals(HomeProductsDataController.getInstance().homeProductsArraylist.get(i).getProductId())) {
                            Product productObj = HomeProductsDataController.getInstance().homeProductsArraylist.get(i);
                            Intent intent = new Intent(WishListActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("selectedProdouct", "caProducts");
                            intent.putExtra("cartSelectedPos", viewHolder.getAdapterPosition());
                            Bundle b = new Bundle();
                            b.putSerializable("cart", productObj);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                        wishAdapter.notifyDataSetChanged();
                    }
                }
            });
            viewHolder.remove.setText("Remove");
        }
        @Override
        public int getItemCount() {
            if (wishLists != null) {
                return wishLists.size();
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
    public void displayAlterOnToClerWish() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                WishListActivity.this);
        // set dialog message
        alertDialogBuilder
                .setTitle("Are you sure?")
                .setMessage("Do you want remove all products from wishlist?")
                //.setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        if (isConn()) {
                            deleteAllWishData(mobileNumber, merchant);
                        } else {
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    ////////Delete all the wishData
    public void deleteAllWishData(String mobileNumber, String merchant) {
        Progress.show(WishListActivity.this);
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> call = serverApiInterface.deleteAllItemsInWish(mobileNumber, merchant); // This one only changed in server api
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
                            wishLists.clear();
                            wishAdapter.notifyDataSetChanged();
                            emptycartLayout.setVisibility(View.VISIBLE);
                            wishDataScroll.setVisibility(View.GONE);
                            itemsCount.setVisibility(View.VISIBLE);
                            itemsCount.setText(String.valueOf(cartCount));
                            itemsAmount.setText(getResources().getString(R.string.rupee)+ " " + cartAmount + ".00");
                            Progress.dismiss(WishListActivity.this);
                        } else if (statuscode.equals("0")) {
                            Progress.dismiss(WishListActivity.this);
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
                Progress.dismiss(WishListActivity.this);
            }
        });
    }
    ////////delete single item in cart
    public void deleteSingleWishItem(final String productId, String mobileNumber, final String netWeight, final int index, final String mrpPrice, final String vmartPrice, final String url, String merchant) {
        Progress.show(WishListActivity.this);
        JsonObject deleteWish = new JsonObject();
        JSONObject deletewishObj = new JSONObject();
        try {
            deletewishObj.put("productId", productId);
            deletewishObj.put("userid", mobileNumber);
            deletewishObj.put("merchantId", merchant);
            JsonParser jsonParser = new JsonParser();
            deleteWish = (JsonObject) jsonParser.parse(deletewishObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> deletecall = serverApiInterface.deleteWish(deleteWish); // This one only changed in server api
        deletecall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    String deleteWishResponse = null;
                    String statuscode, message;
                    JSONArray deletewishArray;
                    JSONObject deleteWishJsonObj;
                    try {
                        deleteWishResponse = new String(response.body().bytes());
                        deletewishArray = new JSONArray(deleteWishResponse);
                        deleteWishJsonObj = deletewishArray.getJSONObject(0);
                        statuscode = deleteWishJsonObj.getString("response");
                        message = deleteWishJsonObj.getString("message");
                        if (statuscode.equals("3")) {
                            ProductInfo productInfo = new ProductInfo();
                            productInfo.setSelectedQnt(Integer.valueOf(0));
                            productInfo.setMrp(mrpPrice);
                            productInfo.setVmartPrice(vmartPrice);
                            productInfo.setQuantity(netWeight);
                            productInfo.setUrl(url);
                            wishLists.remove(index);
                            wishAdapter.notifyDataSetChanged();
                            if (WishlistController.getInstace().wishListArray.size() < 1) {
                                emptycartLayout.setVisibility(View.VISIBLE);
                                wishDataScroll.setVisibility(View.GONE);
                                itemsCount.setVisibility(View.VISIBLE);
                                itemsCount.setText(String.valueOf(cartCount));
                                itemsAmount.setText(getResources().getString(R.string.rupee)+ " " + cartAmount + ".00");
                                Progress.dismiss(WishListActivity.this);
                            } else {
                                wishDataScroll.setVisibility(View.VISIBLE);
                                itemsCount.setVisibility(View.VISIBLE);
                                itemsCount.setText(String.valueOf(cartCount));
                                itemsAmount.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
                                Progress.dismiss(WishListActivity.this);
                            }
                        } else if (statuscode.equals("0")) {
                            Progress.dismiss(WishListActivity.this);
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
                Progress.dismiss(WishListActivity.this);
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

    public String getCurrentTime() { return String.valueOf(System.currentTimeMillis() / 1000L); }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}