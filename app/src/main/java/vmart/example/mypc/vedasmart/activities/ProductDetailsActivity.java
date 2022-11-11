package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vmart.example.mypc.vedasmart.helper.InputFilterMinMax;
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

public class ProductDetailsActivity extends AppCompatActivity implements AddorRemoveCallbacks {
    ImageView productImg;
    TextView ItemName, mrp, save, toolText, description;
    Button qty;
    android.support.v7.widget.Toolbar toolbar;
    ImageView toolbarImage, search, cart, menu;
    int pos, capos;
    ArrayList<ProductInfo> pinfo;
    Product product;
    int itemCount;
    int cartCount = 0, cartAmount = 0;
    String totalMrp, totalVmart, totalSavings = "0";
    RecyclerView quantitiesRecyclerview;
    ProductsDescriptionAdapter productsDescriptionAdapter;
    ArrayList<CartList> cartListArrayList;
    RelativeLayout relativeback;
    TextView Count, Total;
    String selectedType;
    LinearLayout cartLinear;
    TextView netweight, vmartPrice;
    TextView addtocart, addcount, count, minuscount;
    TextView textDescription;
    View viewBelowDescription;
    RelativeLayout optionmenulayout;
    SharedPreferences sharedPreferences;
    String mobileNumber, cartID,merchant;
    ImageView cartImage;
    ServerApiInterface serverApiInterface;
    ArrayList<Product> productArrayList;
    int childSelectedPos;
    ImageButton shareBtn;
    ToggleButton wish;
    ArrayList<WishList> wishList;
    private static final int REQUEST_WRITE_PERMISSION = 56;
    RelativeLayout relativeImage;

    @SuppressLint("NewApi")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        cartID = sharedPreferences.getString(LoginActivity.cartid, " ");
        merchant = sharedPreferences.getString("merchantId","");
        cartListArrayList = new ArrayList<>();
        Intent intent = getIntent();
        selectedType = intent.getStringExtra("selectedProdouct");
        if (selectedType != null) {
            if (selectedType.equals("homeProducts")) {
                pos = intent.getIntExtra("homeSelectedPos", -1);
                childSelectedPos = intent.getIntExtra("childSelectedPos", -1);
                Bundle getBundle = intent.getExtras();
                assert getBundle != null;
                productArrayList = (ArrayList<Product>) getBundle.getSerializable("product");
                assert productArrayList != null;
                product = productArrayList.get(pos);
                pinfo = product.getPinfo();
            } else if (selectedType.equals("caProducts")) {
                Bundle getBundle = intent.getExtras();
                capos = intent.getIntExtra("cartSelectedPos", -1);
                assert getBundle != null;
                product = (Product) getBundle.getSerializable("cart");
                assert product != null;
                pinfo = product.getPinfo();
            }
        }
        wishList = WishlistController.getInstace().wishListArray;
        init();
        requestPermissions();
    }

    public void init() {
        toolbar = findViewById(R.id.product_des_toolbar);
        cartImage = findViewById(R.id.icon_badge);
        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("productArraylist", productArrayList);
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
            }
        });
        relativeback = findViewById(R.id.relative_back);
        toolText = findViewById(R.id.subProduct_titla);
        Count = findViewById(R.id.count);
        Total = findViewById(R.id.totalAmount);
        netweight = findViewById(R.id.text_netweight);
        vmartPrice = findViewById(R.id.text_vmartCost);
        addtocart = findViewById(R.id.btn_addTocart);
        addtocart.setText("AddToCart");
        addcount = findViewById(R.id.text_plus);
        minuscount = findViewById(R.id.text_minus);
        count = findViewById(R.id.text_count);
        textDescription = findViewById(R.id.textDescription);
        viewBelowDescription = findViewById(R.id.view5);
        optionmenulayout = findViewById(R.id.options_menu_layout);
        shareBtn = findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(ShareBtnClick);
        relativeImage = findViewById(R.id.relativeImage);
        wish = findViewById(R.id.wishlist);

        WishlistController.getInstace().FetchWishData(ProductDetailsActivity.this, mobileNumber,merchant);
        if (wishList != null) {
            for (int i = 0; i < wishList.size(); i++) {
                String itemname = wishList.get(i).getItemName();
                if (itemname.equals(product.getItemName())) {
                    wish.setChecked(true);
                } else {
                }
            }
        }
        wish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, final  boolean isChecked) {
                if (isConn()) {
                    if (isChecked) {
                        if (mobileNumber.equals("")) {
                            if (isChecked) {
                                wish.setChecked(false);
                            } else {
                                wish.setChecked(true);
                            }
                            displayAltertLogin();
                        } else {
                            pinfo = product.getPinfo();
                            Progress.show(ProductDetailsActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    WishlistController.getInstace().AddToWish(ProductDetailsActivity.this, mobileNumber, product.getProductId(),
                                            product.getItemName(), isChecked, pinfo.get(0).getUrl(), getCurrentTime(),
                                            pinfo.get(0).getMrp(), pinfo.get(0).getVmartPrice(), 0, pinfo.get(0).getQuantity(),merchant);
                                }
                            }, 1000);
                        }
                    } else {
                        if (wishList != null) {
                            Progress.show(ProductDetailsActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    WishlistController.getInstace().deleteWish(ProductDetailsActivity.this, mobileNumber, product.getProductId(), product,merchant);
                                }
                            }, 1000);
                        }
                    }
                } else {
                    if (isChecked) {
                        wish.setChecked(false);
                    } else {
                        wish.setChecked(true);
                    }
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        relativeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        optionmenulayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductDetailsActivity.this, MyAccountActivity.class));
            }
        });
        productImg = findViewById(R.id.productImage);
        ItemName = findViewById(R.id.productname);
        mrp = findViewById(R.id.mrpPrice);
        save = findViewById(R.id.Save);
        qty = findViewById(R.id.quantity_btn);
        description = findViewById(R.id.description);
        quantitiesRecyclerview = findViewById(R.id.quantities_display_view);
        cartListArrayList = CartController.getInstace().cartListArray;
        cartLinear = findViewById(R.id.linear_cart);
        quantitiesRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    View.OnClickListener ShareBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*String Applink = "App Link : https://play.google.com/store/apps/details?id=vmart.example.mypc.vmart&hl=en";
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    Drawable drawable = productImg.getDrawable();
                    Bitmap bmp = null;
                    if (drawable instanceof BitmapDrawable) {
                        bmp = ((BitmapDrawable) productImg.getDrawable()).getBitmap();
                    }
                    // Store image to default external storage directory
                    File file = null;
                    try {
                        file = new File(Environment.getExternalStorageDirectory() + "/V-Mart/Images", "share_image.png");
                        file.getParentFile().mkdirs();
                        FileOutputStream out = new FileOutputStream(file);
                        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                        file.setReadable(true, false);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file.exists()) {
                        Intent install = new Intent(Intent.ACTION_SEND);
                        if (file.exists()) {
                            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            install.putExtra(Intent.EXTRA_SUBJECT, "VMart application");
                            install.setType("text/plain");
                            Uri apkURI = FileProvider.getUriForFile(ProductDetailsActivity.this,
                                    "com.vmart.fileprovider", file);
                            install.setType("image/png");
                            install.putExtra(Intent.EXTRA_STREAM, apkURI);
                            String shareMessage = "Hello! Check this Product " + product.getItemName();
                            shareMessage = shareMessage + "\n\n " + Applink;
                            install.putExtra(Intent.EXTRA_TEXT, "" + shareMessage);
                            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            // Launch sharing dialog for image
                            startActivity(Intent.createChooser(install, "share_satus"));
                        }
                    }

                } else {
                    Bitmap bitmap = getBitmapFromView(relativeImage);
                    try {
                        File file = new File(getExternalCacheDir(), "logicchip.png");
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        file.setReadable(true, false);
                        final Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "VMart application");
                        intent.setType("image/*");
                        String shareMessage = "Hello! Check this Product " + product.getItemName();
                        shareMessage = shareMessage + "\n\n " + Applink;
                        intent.putExtra(Intent.EXTRA_TEXT, "" + shareMessage);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(intent, "share_satus"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else{
                Toast.makeText(ProductDetailsActivity.this,"No Product is available",Toast.LENGTH_SHORT).show();
            }*/
            //     setupFacebookShareIntent();

            String Applink = "App Link : https://play.google.com/store/apps/details?id=vmart.example.mypc.vmart&hl=en";
            if (product != null) {
                List<Intent> targetShareIntents = new ArrayList<Intent>();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                List<ResolveInfo> resInfos = getPackageManager().queryIntentActivities(shareIntent, 0);
                if (!resInfos.isEmpty()) {
                    System.out.println("Have package");
                    for (ResolveInfo resInfo : resInfos) {
                        String packageName = resInfo.activityInfo.packageName;
                        Log.i("Package Name", packageName);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            Drawable drawable = productImg.getDrawable();
                            Bitmap bmp = null;
                            if (drawable instanceof BitmapDrawable) {
                                bmp = ((BitmapDrawable) productImg.getDrawable()).getBitmap();
                            }
                            // Store image to default external storage directory
                            File file = null;
                            try {
                                file = new File(Environment.getExternalStorageDirectory() + "/V-Mart/Images", "share_image.png");
                                file.getParentFile().mkdirs();
                                FileOutputStream out = new FileOutputStream(file);
                                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                                file.setReadable(true, false);
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (file.exists()) {
                                Intent install = new Intent(Intent.ACTION_SEND);
                                //       install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                install.putExtra(Intent.EXTRA_SUBJECT, "Vedas Mart application");
                                install.setType("text/plain");
                                if (TextUtils.equals(packageName, "com.facebook.katana")) {
                                    install.putExtra(Intent.EXTRA_TEXT, "" + Applink);
                                } else if (TextUtils.equals(packageName, "com.skype.raider")) {
                                    String shareMessage = "Hello! Check this Product " + product.getItemName();
                                    shareMessage = shareMessage + "\n\n " + Applink;
                                    install.putExtra(Intent.EXTRA_TEXT, "" + shareMessage);
                                } else {
                                    Uri apkURI = FileProvider.getUriForFile(ProductDetailsActivity.this,
                                            "com.vmart.fileprovider", file);
                                    install.setType("image/png");
                                    install.putExtra(Intent.EXTRA_STREAM, apkURI);
                                    String shareMessage = "Hello! Check this Product " + product.getItemName();
                                    shareMessage = shareMessage + "\n\n " + Applink;
                                    install.putExtra(Intent.EXTRA_TEXT, "" + shareMessage);
                                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                }
                                /*install.setPackage(packageName);*/
                                install.setClassName(
                                        resInfo.activityInfo.packageName,
                                        resInfo.activityInfo.name);
                                // Launch sharing dialog for image
                                targetShareIntents.add(install);
                            }

                        } else {
                            Bitmap bitmap = getBitmapFromView(relativeImage);
                            try {
                                File file = new File(getExternalCacheDir(), "logicchip.png");
                                FileOutputStream fOut = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                fOut.flush();
                                fOut.close();
                                file.setReadable(true, false);
                                final Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Vedas Mart application");
                                if (TextUtils.equals(packageName, "com.facebook.katana")) {
                                    intent.putExtra(Intent.EXTRA_TEXT, "" + Applink);
                                } else if (TextUtils.equals(packageName, "com.skype.raider")) {
                                    String shareMessage = "Hello! Check this Product " + product.getItemName();
                                    shareMessage = shareMessage + "\n\n " + Applink;
                                    intent.putExtra(Intent.EXTRA_TEXT, "" + shareMessage);
                                } else {
                                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                                    intent.setType("image/png");
                                    String shareMessage = "Hello! Check this Product " + product.getItemName();
                                    shareMessage = shareMessage + "\n\n " + Applink;
                                    intent.putExtra(Intent.EXTRA_TEXT, "" + shareMessage);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                }
                                intent.setClassName(
                                        resInfo.activityInfo.packageName,
                                        resInfo.activityInfo.name);
                                targetShareIntents.add(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (!targetShareIntents.isEmpty()) {
                        System.out.println("Have Intent");
                        Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(targetShareIntents.size() - 1), "Choose app to share");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[targetShareIntents.size()]));
                        startActivity(chooserIntent);
                    } else {
                        System.out.println("Do not Have Intent");
                    }
                }
            } else {
                Toast.makeText(ProductDetailsActivity.this, "No Product is available", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {

        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    ///getting the total Mrp for adding
    public String getTotalMrp(String count, String mrp) {
        Log.e("mrp", " " + mrp);
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

    ////////for unix time stamp
    public String getCurrentTime() {
        return String.valueOf(System.currentTimeMillis() / 1L);
    }

    @Override
    public void onAddProduct(String cartCount, String cartAmount) {
        Count.setVisibility(View.VISIBLE);
        Count.setText(String.valueOf(cartCount));
        Total.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
        if (Integer.valueOf(cartCount) == 0) {
            Count.setVisibility(View.GONE);
            Total.setText("");
        }
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        CartController.getInstace().setListener(ProductDetailsActivity.this);
        if (CartController.getInstace().cartListArray.size() == 0) {
            cartCount = 0;
            cartAmount = 0;
            Count.setVisibility(View.GONE);
            Total.setText("");
            if (product != null) {
                productsDescriptionAdapter = new ProductsDescriptionAdapter(pinfo, ProductDetailsActivity.this, product, CartController.getInstace().cartListArray, productArrayList);
                quantitiesRecyclerview.setAdapter(productsDescriptionAdapter);
            }
        }
        if (product != null) {
            for (int i = 0; i < HomeProductsDataController.getInstance().homeProductsArraylist.size(); i++) {
                if (HomeProductsDataController.getInstance().homeProductsArraylist.get(i).getProductId().equals(product.getProductId())) {
                    product = HomeProductsDataController.getInstance().homeProductsArraylist.get(i);
                    pinfo = product.getPinfo();
                }
            }
            productsDescriptionAdapter = new ProductsDescriptionAdapter(pinfo, ProductDetailsActivity.this, product, CartController.getInstace().cartListArray, productArrayList);
            quantitiesRecyclerview.setAdapter(productsDescriptionAdapter);
            toolText.setText(product.getItemName());
            ItemName.setText(product.getItemName());
            description.setText(product.getDescription());
            Glide.with(getApplicationContext()).load(pinfo.get(0).getUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loadingpic11))
                    .into(productImg);
            itemCount = pinfo.get(0).getSelectedQnt();
            qty = findViewById(R.id.quantity_btn);
            qty.setText(pinfo.get(0).getQuantity());
            if (itemCount > 0) {
                totalMrp = getTotalMrp(String.valueOf(itemCount), pinfo.get(0).getMrp());
                totalVmart = getTotalVmart(String.valueOf(itemCount), pinfo.get(0).getVmartPrice());
                totalSavings = getTotalSavings(totalMrp, totalVmart);
                mrp.setText("Rs." + totalVmart + ".00");
                save.setText("Rs."+ totalSavings + ".00");
            } else {
                mrp.setText("Rs." + pinfo.get(0).getVmartPrice() + ".00");
                totalSavings = getTotalSavings(pinfo.get(0).getMrp(), pinfo.get(0).getVmartPrice());
                save.setText("Rs." + totalSavings + ".00");
            }
        }
        if (cartListArrayList != null && cartListArrayList.size() > 0) {
            cartCount = 0;
            cartAmount = 0;
            for (int i = 0; i < cartListArrayList.size(); i++) {
                cartCount = cartCount + Integer.valueOf(cartListArrayList.get(i).getQuantity());
                cartAmount = cartAmount + Integer.valueOf(cartListArrayList.get(i).getQuantity()) * Integer.valueOf(cartListArrayList.get(i).getVmartPrice());
                totalMrp = getTotalMrp(cartListArrayList.get(i).getQuantity(), cartListArrayList.get(i).getMrpPrice());
                totalVmart = getTotalVmart(cartListArrayList.get(i).getQuantity(), cartListArrayList.get(i).getVmartPrice());
                totalSavings = String.valueOf(Integer.valueOf(totalSavings) + Integer.valueOf(getTotalSavings(totalMrp, totalVmart)));
                Count.setVisibility(View.VISIBLE);
                Count.setText(String.valueOf(cartCount));
                Total.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
            }
        } else {
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
        if (resultData.equals("refreshWishData")) {
            Progress.dismiss(ProductDetailsActivity.this);
        }
        if (resultData.equals("refreshCartData")) {
            if (CartController.getInstace().cartListArray != null && CartController.getInstace().cartListArray.size() > 0) {
                cartCount = 0;
                cartAmount = 0;
                for (int i = 0; i < CartController.getInstace().cartListArray.size(); i++) {
                    ArrayList<CartList> cartListArrayList = CartController.getInstace().cartListArray;
                    cartCount = cartCount + Integer.valueOf(cartListArrayList.get(i).getQuantity());
                    cartAmount = cartAmount + Integer.valueOf(cartListArrayList.get(i).getQuantity()) * Integer.valueOf(cartListArrayList.get(i).getVmartPrice());
                    if (cartCount > 0 && cartAmount > 0) {
                        Count.setVisibility(View.VISIBLE);
                        Count.setText(String.valueOf(cartCount));
                        Total.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
                    } else if (cartCount == 0 && cartAmount == 0) {
                        count.setVisibility(View.GONE);
                    }
                }
            } else if (CartController.getInstace().cartListArray.size() == 0) {
                count.setVisibility(View.GONE);
                Total.setText("");
                cartCount = 0;
                cartAmount = 0;
            }
            Progress.dismiss(ProductDetailsActivity.this);
            if (product != null) {
                for (int i = 0; i < HomeProductsDataController.getInstance().homeProductsArraylist.size(); i++) {
                    if (product.getProductId().equals(HomeProductsDataController.getInstance().homeProductsArraylist.get(i).getProductId())) {
                        product = HomeProductsDataController.getInstance().homeProductsArraylist.get(i);
                        productsDescriptionAdapter = new ProductsDescriptionAdapter(HomeProductsDataController.getInstance().homeProductsArraylist.get(i).getPinfo(),
                                ProductDetailsActivity.this, product, CartController.getInstace().cartListArray, productArrayList);
                        quantitiesRecyclerview.setAdapter(productsDescriptionAdapter);
                        productsDescriptionAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (resultData.equals("error")) {
            Progress.dismiss(ProductDetailsActivity.this);
        }
    }

    class ProductsDescriptionAdapter extends RecyclerView.Adapter<ProductsDescriptionAdapter.ViewHolder> {

        ArrayList<ProductInfo> productInfos;
        ArrayList<Product> productArrayList;
        Context context;
        Product productObj;
        ArrayList<CartList> cartListArrayList;

        public ProductsDescriptionAdapter(ArrayList<ProductInfo> productInfos, Context context, Product product, ArrayList<CartList> cartListArray, ArrayList<Product> productArrayList) {
            this.productInfos = productInfos;
            this.context = context;
            this.productObj = product;
            cartListArrayList = cartListArray;
            this.productArrayList = productArrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.products_description_lits, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.qunaity.setText(productInfos.get(position).getQuantity());
            if (cartListArrayList.size() > 0 && cartListArrayList != null) {
                for (int i = 0; i < cartListArrayList.size(); i++) {
                    if (cartListArrayList.get(i).getProductId().equals(productObj.getProductId())) {
                        for (int j = 0; j < pinfo.size(); j++) {
                            if (cartListArrayList.get(i).getNetWeight().equals(pinfo.get(j).getQuantity())) {
                                ProductInfo productInfoObj = productInfos.get(j);
                                productInfoObj.setSelectedQnt(Integer.valueOf(cartListArrayList.get(i).getQuantity()));
                                productInfoObj.setQuantity(cartListArrayList.get(i).getNetWeight());
                                productInfoObj.setVmartPrice(cartListArrayList.get(i).getVmartPrice());
                                productInfoObj.setMrp(cartListArrayList.get(i).getMrpPrice());
                                productInfoObj.setUrl(cartListArrayList.get(i).getUrl());
                                pinfo.set(j, productInfoObj);
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < pinfo.size(); i++) {
                    ProductInfo productInfoObj = productInfos.get(i);
                    if (pinfo.get(i).getQuantity().equals(productInfoObj.getQuantity())) {
                        productInfoObj.setSelectedQnt(0);
                        pinfo.set(i, productInfoObj);
                    }
                }
            }
            if (pinfo.get(position).getSelectedQnt() > 0) {
                totalSavings = "0";
                holder.addtocart.setVisibility(View.GONE);
                holder.countEditText.setVisibility(View.VISIBLE);
                holder.addcount.setVisibility(View.VISIBLE);
                holder.minuscount.setVisibility(View.VISIBLE);
                holder.countEditText.setText(String.valueOf(pinfo.get(position).getSelectedQnt()));
                totalMrp = getTotalMrp(String.valueOf(pinfo.get(position).getSelectedQnt()), pinfo.get(position).getMrp());
                totalVmart = getTotalVmart(String.valueOf(pinfo.get(position).getSelectedQnt()), pinfo.get(position).getVmartPrice());
                totalSavings = getTotalSavings(totalMrp, totalVmart);
                holder.vamrtPrice.setText("Vedas Mart" +"Rs." + pinfo.get(position).getVmartPrice() + ".00");
                mrp.setText("Rs." + totalVmart + ".00");
                save.setText("Rs." + totalSavings + ".00");
            } else {
                holder.addtocart.setVisibility(View.VISIBLE);
                holder.countEditText.setVisibility(View.GONE);
                holder.addcount.setVisibility(View.GONE);
                holder.minuscount.setVisibility(View.GONE);
                holder.vamrtPrice.setText("Vedas Mart" +"Rs."+ pinfo.get(position).getVmartPrice() + ".00");
                totalSavings = getTotalSavings(pinfo.get(position).getMrp(), pinfo.get(position).getVmartPrice());
                mrp.setText("Rs."+ pinfo.get(position).getVmartPrice() + ".00");
                save.setText("Rs." + totalSavings + ".00");
            }
            holder.addtocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        if (mobileNumber.equals("")) {
                            displayAltertLogin();
                        } else {
                            if (!holder.addcount.isEnabled()) {
                                holder.addcount.setEnabled(true);
                            }
                            if (!holder.minuscount.isEnabled()) {
                                holder.minuscount.setEnabled(true);
                            }
                            final int addcount = Integer.valueOf(productInfos.get(holder.getAdapterPosition()).getSelectedQnt()) + 1;
                            cartCount++;
                            cartAmount = cartAmount + Integer.valueOf(productInfos.get(holder.getAdapterPosition()).getVmartPrice());
                            Progress.show(ProductDetailsActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CartController.getInstace().AddToCart(ProductDetailsActivity.this, productObj.getProductId(),
                                            productObj.getItemName(), pinfo.get(position).getMrp(), pinfo.get(position).getVmartPrice(),
                                            String.valueOf(addcount), mobileNumber, pinfo.get(position).getQuantity(), getCurrentTime(),
                                            cartID, cartCount, cartAmount, pinfo.get(position).getUrl(), 0,merchant);
                                    productsDescriptionAdapter.notifyDataSetChanged();
                                }
                            }, 1000);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.countEditText.setFilters(new InputFilter[]{new InputFilterMinMax("1", "15", getApplicationContext())});
            holder.countEditText.setFocusable(false);
            holder.countEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isConn()) {
                        holder.countEditText.setFocusableInTouchMode(true);
                        holder.countEditText.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.countEditText.requestFocus();
                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
            holder.countEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getCurrentFocus() == holder.countEditText) {
                        //restrict 0 as in first lettter in keypad
                        if (s.toString().startsWith("0")) {
                            holder.countEditText.setText(" ");
                            Toast.makeText(getApplicationContext(), "Maximum 15 units are allowed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            holder.countEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (isConn()) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            final String quantity = holder.countEditText.getText().toString();
                            if (!quantity.isEmpty()) {
                                cartCount = Integer.valueOf(quantity);
                            } else {
                                cartCount = 0;
                            }
                            cartAmount = cartCount * Integer.valueOf(productInfos.get(holder.getAdapterPosition()).getVmartPrice());
                            if (quantity.isEmpty() | quantity.equals("0")) {
                                closeKeyboard();
                                holder.countEditText.setText(quantity);
                                notifyDataSetChanged();
                            /*deleteSingleCartItem(productObj.getProductId(), mobileNumber,
                                    productInfos.get(holder.getAdapterPosition()).getQuantity(),
                                    productInfos.get(holder.getAdapterPosition()).getMrp(),
                                    productInfos.get(holder.getAdapterPosition()).getVmartPrice(),
                                    productInfos.get(holder.getAdapterPosition()).getUrl(), cartCount, cartAmount);
                            notifyDataSetChanged();*/

                            } else {
                                closeKeyboard();
                                Progress.show(ProductDetailsActivity.this);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CartController.getInstace().updateCart(ProductDetailsActivity.this,
                                                productInfos.get(holder.getAdapterPosition()).getMrp(),
                                                productInfos.get(holder.getAdapterPosition()).getVmartPrice(),
                                                quantity, productInfos.get(holder.getAdapterPosition()).getQuantity(),
                                                mobileNumber, productObj.getProductId(), cartID, productObj.getItemName(), getCurrentTime(),
                                                cartCount, cartAmount, productInfos.get(holder.getAdapterPosition()).getUrl(),merchant);
                                        productsDescriptionAdapter.notifyDataSetChanged();
                                    }
                                }, 1000);
                            }
                            return true;
                        }
                    }else {
                        final String quantity = holder.countEditText.getText().toString();
                        closeKeyboard();
                        holder.countEditText.setText(quantity);
                        notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });

            holder.addcount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        if (!holder.minuscount.isEnabled()) {
                            holder.minuscount.setEnabled(true);
                        }
                        int addCount = productInfos.get(holder.getAdapterPosition()).getSelectedQnt();
                        addCount = addCount + 1;
                        if (addCount > 15) {
                            Toast.makeText(ProductDetailsActivity.this, "Maximum 15 units are allowed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        cartCount++;
                        cartAmount = cartAmount + Integer.valueOf(productInfos.get(holder.getAdapterPosition()).getVmartPrice());
                        Progress.show(ProductDetailsActivity.this);
                        final int finalAddCount = addCount;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CartController.getInstace().updateCart(ProductDetailsActivity.this,
                                        pinfo.get(holder.getAdapterPosition()).getMrp(),
                                        pinfo.get(holder.getAdapterPosition()).getVmartPrice(),
                                        String.valueOf(finalAddCount), pinfo.get(holder.getAdapterPosition()).getQuantity(),
                                        mobileNumber, productObj.getProductId(), cartID, productObj.getItemName(), getCurrentTime(),
                                        cartCount, cartAmount, pinfo.get(holder.getAdapterPosition()).getUrl(),merchant);
                                productsDescriptionAdapter.notifyDataSetChanged();
                            }
                        }, 2000);
                    }else {
                        Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.minuscount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        int minusCount = productInfos.get(holder.getAdapterPosition()).getSelectedQnt();
                        minusCount = minusCount > 0 ? --minusCount : 0;
                        cartCount--;
                        cartAmount = cartAmount - Integer.valueOf(productInfos.get(holder.getAdapterPosition()).getVmartPrice());
                        if (minusCount == 0) {
                            Progress.show(ProductDetailsActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    deleteSingleCartItem(productObj.getProductId(), mobileNumber,
                                            productInfos.get(holder.getAdapterPosition()).getQuantity(),
                                            productInfos.get(holder.getAdapterPosition()).getMrp(),
                                            productInfos.get(holder.getAdapterPosition()).getVmartPrice(),
                                            productInfos.get(holder.getAdapterPosition()).getUrl(), cartCount, cartAmount,merchant);
                                    productsDescriptionAdapter.notifyDataSetChanged();
                                }
                            }, 2000);
                        } else {
                            final int finalMinusCount = minusCount;
                            Progress.show(ProductDetailsActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CartController.getInstace().updateCart(ProductDetailsActivity.this,
                                            productInfos.get(holder.getAdapterPosition()).getMrp(),
                                            productInfos.get(holder.getAdapterPosition()).getVmartPrice(),
                                            String.valueOf(finalMinusCount), productInfos.get(holder.getAdapterPosition()).getQuantity(),
                                            mobileNumber, productObj.getProductId(), cartID, productObj.getItemName(), getCurrentTime(),
                                            cartCount, cartAmount, productInfos.get(holder.getAdapterPosition()).getUrl(),merchant);
                                    productsDescriptionAdapter.notifyDataSetChanged();
                                }
                            }, 2000);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qty.setText(productInfos.get(holder.getAdapterPosition()).getQuantity());
                    if (productInfos.get(holder.getAdapterPosition()).getSelectedQnt() > 0) {
                        totalMrp = getTotalMrp(String.valueOf(productInfos.get(holder.getAdapterPosition()).getSelectedQnt()), productInfos.get(holder.getAdapterPosition()).getMrp());
                        totalVmart = getTotalVmart(String.valueOf(productInfos.get(holder.getAdapterPosition()).getSelectedQnt()), productInfos.get(holder.getAdapterPosition()).getVmartPrice());
                        totalSavings = getTotalSavings(totalMrp, totalVmart);
                        mrp.setText("Rs." + totalVmart + ".00");
                        save.setText("Rs."+ totalSavings + ".00");
                    } else {
                        mrp.setText("Rs."+ productInfos.get(holder.getAdapterPosition()).getVmartPrice() + ".00");
                        totalSavings = getTotalSavings(pinfo.get(holder.getAdapterPosition()).getMrp(), productInfos.get(holder.getAdapterPosition()).getVmartPrice());
                        save.setText("Rs." + totalSavings + ".00");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (productInfos != null) {
                return productInfos.size();
            } else {
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView qunaity, vamrtPrice;
            EditText countEditText;
            TextView addcount, minuscount, addtocart;

            public ViewHolder(View itemView) {
                super(itemView);
                qunaity = itemView.findViewById(R.id.text_netweight);
                vamrtPrice = itemView.findViewById(R.id.text_vmartCost);
                addtocart = itemView.findViewById(R.id.btn_addTocart);
                addcount = itemView.findViewById(R.id.text_plus);
                countEditText = itemView.findViewById(R.id.productDesc_EditText_count);
                minuscount = itemView.findViewById(R.id.text_minus);
            }
        }
    }

    public void deleteSingleCartItem(final String productId, String mobileNumber, final String netWeight, final String mrpPrice,
                                     final String vmartPrice, final String url, final int cartCount, final int cartAmount,String merchant) {
        Progress.show(ProductDetailsActivity.this);
        JsonObject deleteCart = new JsonObject();
        JSONObject deletecartObj = new JSONObject();
        try {
            deletecartObj.put("netWeight", netWeight);
            deletecartObj.put("mobileNumber", mobileNumber);
            deletecartObj.put("productId", productId);
            deletecartObj.put("merchantId",merchant);
            JsonParser jsonParser = new JsonParser();
            deleteCart = (JsonObject) jsonParser.parse(deletecartObj.toString());
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
                            for (int i = 0; i < CartController.getInstace().cartListArray.size(); i++) {
                                if (CartController.getInstace().cartListArray.get(i).getProductId().equals(productId) && CartController.getInstace().cartListArray.get(i).getNetWeight().equals(netWeight)) {
                                    CartList cartListObj = CartController.getInstace().cartListArray.get(i);
                                    if (CartController.getInstace().cartListArray.contains(cartListObj)) {
                                        int index = CartController.getInstace().cartListArray.indexOf(cartListObj);
                                        CartController.getInstace().cartListArray.remove(cartListObj);
                                    }
                                }
                            }
                            Count.setText(String.valueOf(cartCount));
                            Total.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
                            if (Integer.valueOf(cartCount) == 0) {
                                Count.setVisibility(View.GONE);
                                Total.setText("");
                            }
                            EventBus.getDefault().post(new SideMenuFragment.MessageEvent("refreshCartData"));
                        } else if (statuscode.equals("0")) {
                            Progress.dismiss(ProductDetailsActivity.this);
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
                Progress.dismiss(ProductDetailsActivity.this);
                Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /////display  if no user exist
    public void displayAltertLogin() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ProductDetailsActivity.this);
        // set dialog message
        alertDialogBuilder
                .setMessage("Please Sign In")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                        startActivity(new Intent(ProductDetailsActivity.this,LoginActivity.class));
                        finishAffinity();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        // show it
        alertDialog.show();
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
        finish();
    }
}