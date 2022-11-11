package vmart.example.mypc.vedasmart.activities;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.ArrayList;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import vmart.example.mypc.vedasmart.DIALOGS.InternetDialog;
import vmart.example.mypc.vedasmart.helper.InputFilterMinMax;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.RECEIVER.InternetConnection;
import vmart.example.mypc.vedasmart.controllers.AddressController;
import vmart.example.mypc.vedasmart.controllers.CartController;
import vmart.example.mypc.vedasmart.controllers.HomeProductsDataController;
import vmart.example.mypc.vedasmart.controllers.OrdersController;
import vmart.example.mypc.vedasmart.controllers.PickupController;
import vmart.example.mypc.vedasmart.controllers.ProductsDataController;
import vmart.example.mypc.vedasmart.controllers.WishlistController;
import vmart.example.mypc.vedasmart.fragments.SideMenuFragment;
import vmart.example.mypc.vedasmart.helper.LocationDetailsHelper;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.helper.Utils;
import vmart.example.mypc.vedasmart.interfaces.AddorRemoveCallbacks;
import vmart.example.mypc.vedasmart.model.CartList;
import vmart.example.mypc.vedasmart.model.Product;
import vmart.example.mypc.vedasmart.model.ProductInfo;
import vmart.example.mypc.vedasmart.model.SubItems;
import vmart.example.mypc.vedasmart.sessions.SessionManager;
import static java.lang.String.valueOf;
import static vmart.example.mypc.vedasmart.activities.MyAccountActivity.isFrommyaccount;

public class HomeActivity extends AppCompatActivity implements
        SideMenuFragment.FragmentDrawerListener, AddorRemoveCallbacks, InternetConnection.InternetConnectionListener {
    public int selectedPosition = 0;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private DrawerLayout drawer;
    SideMenuFragment drawerFragment;
    SideMenuFragment sideMenuFragment;
    Toolbar toolbar;
    ImageView toolbarImage;
    RecyclerView recyclerView;
    RecyclerView homeRecyclerview;
    HomeAdapter homeAdapter;
    ArrayList<SubItems> subItemsArrayList;
    ArrayList<Product> productArrayList = new ArrayList<>();
    SessionManager sessionManager;
    String selectedcategory;
    SharedPreferences sharedPreferences;
    String mobileNumber, cartID;
    public int cartCount = 0, cartAmount = 0;
    RelativeLayout privacyPolicy, TermsAndConditions, Refund, ContactUs, AboutUs;
    String timestamp;
    String totalMrp, totalVmart, totalSavings;
    String productID, productName;
    RelativeLayout loaction;
    FrameLayout itemcart;
    TextView itemsCount, itemsAmount, text_search;
    ImageView moreImg;
    RelativeLayout TrackOrder, frequentQuestions, pickUpList;
    Button customerService;
    SearchView searchProduct;
    RelativeLayout optionsMenuLayout;
    int SPLASH_TIME_OUT = 1500;
    Observable<Integer> FetchAlldataFromServer;
    MaterialDialog progressdialog;
    int previousSeectedPosition = -1;
    public static int scrollpos = 0;
    static final private int DELAY_TIME = 30 * 1000;
    private Parcelable recylerViewState;
    String merchant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get User Location
        LocationTracker.getInstance().fillContext(getApplicationContext());
        LocationTracker.getInstance().startLocation();
        /////getting the cartID and mobilenumber that are store in sharedpreference
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        cartID = sharedPreferences.getString(LoginActivity.cartid, " ");
        mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId","");
        Log.e("cartId", " " + cartID + " " + mobileNumber+" "+merchant);
        HomeProductsDataController.getInstance().homeProductsArraylist.clear();
        progressdialog = new MaterialDialog.Builder(HomeActivity.this)
                .content("Loading....")
                .canceledOnTouchOutside(false)
                .progress(true, 0).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // close this activity
                Log.e("handler", "first");
                FetchAlldataFromServer = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        integerSubscriber.onNext(fetchallData());
                        integerSubscriber.onComplete();
                    }
                });
                FetchAlldataFromServer.subscribe();
                progressdialog.dismiss();
            }
        }, SPLASH_TIME_OUT);

        HomeProductsDataController.getInstance().fillcontext(HomeActivity.this,merchant);
        CartController.getInstace().fillcontext(HomeActivity.this);
        AddressController.getInstance().fillContext(HomeActivity.this);
        WishlistController.getInstace().fillcontext(HomeActivity.this);
        ProductsDataController.getInstance().fillcontext(HomeActivity.this);

        CartController.getInstace().setListener(this);
        subItemsArrayList = new ArrayList<>();
        ///getting the selected Category from CategoriesActivity(which are selected in sidemenu)
        Intent intent = getIntent();
        selectedcategory = intent.getStringExtra("grains");
        ////HomeProducts Recyclerview
        homeRecyclerview = findViewById(R.id.home_recyclerview);
        homeRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        ////Instance for sessionManager for logout
        sessionManager = new SessionManager(getApplicationContext());
        ///////to load all the products in Home
        if (Utils.haveNetworkConnection(getApplicationContext())) {
            // FetchHomeProducts();
        } else {
            Log.e("checkInternet", " " + "not avialable");
            Snackbar.make(findViewById(R.id.drawerlayoutabout), "No Internet", Snackbar.LENGTH_INDEFINITE).show();
        }
        /////Intializing all the UI elements
        init();
        initializeDrawer();
        // for getting the current location pincode
        getCurrentLocationPinCode();
    }

    /////// Rxjava////////////////
    Subscriber integerSubscriber = new Subscriber() {
        @Override
        public void onComplete() {
            System.out.println("Complete!formme");
        }

        @Override
        public void onSubscribe(Subscription s) { System.out.println("Subscription" + s.toString()); }

        @Override
        public void onNext(Object o) {
            System.out.println("onNext:Fromme" + o.toString());
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("Throwable" + e.getMessage());
        }
    };

    public String fetchallData() {
        HomeProductsDataController.getInstance().FetchHomeProducts(HomeActivity.this,merchant);
        CartController.getInstace().FetchCartData(HomeActivity.this, mobileNumber,merchant);
        PickupController.getInstance().GetPickupLocations(HomeActivity.this);
        PickupController.getInstance().getpincodes(HomeActivity.this);
        OrdersController.getInstance().fetchOrders(HomeActivity.this);
        AddressController.getInstance().FetchAllAddress(HomeActivity.this, mobileNumber,merchant);
        WishlistController.getInstace().FetchWishData(HomeActivity.this, mobileNumber,merchant);
        ProductsDataController.getInstance().fetchProducts(HomeActivity.this, recyclerView);
        return "complete";
    }

    public void init() {
        ////Toolbar Intializtion
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarImage = findViewById(R.id.image_sidemenu);
        loaction = findViewById(R.id.relative_location);
        itemcart = findViewById(R.id.item_counter);
        moreImg = findViewById(R.id.optionsmenu);
        optionsMenuLayout = findViewById(R.id.options_menu_layout);
        itemsCount = findViewById(R.id.count);
        itemsAmount = findViewById(R.id.totalAmount);

        /////click on cartIcon
        itemcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("productArraylist", productArrayList);
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
            }
        });

        optionsMenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MyAccountActivity.class));
            }
        });
        text_search = findViewById(R.id.text_search);
        searchProduct = findViewById(R.id.productSearch);
        searchProduct.onActionViewExpanded(); //new Added line
        searchProduct.setIconifiedByDefault(false);
        searchProduct.setQueryHint(Html.fromHtml("<font size = 1sp>" + "Search a product" + "</font>"));
        if (!searchProduct.isFocused()) {
            searchProduct.clearFocus();
        }
        searchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchProduct.setFocusable(true);
                searchProduct.setIconified(false);
            }
        });
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchProduct.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchProduct.setMaxWidth(Integer.MAX_VALUE);

        searchProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("search", "text");
                if (homeAdapter != null && newText != null) {
                    homeAdapter.filter(newText);
                    text_search.setText("Clear");
                }
                return true;
            }
        });

        customerService = findViewById(R.id.btnCall);
        customerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+919154734093"));
                startActivity(callIntent);
                drawer.closeDrawers();
            }
        });

        pickUpList = findViewById(R.id.relativePickUp);
        pickUpList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PickUpPointList.class);
                startActivity(intent);
                drawer.closeDrawers();
            }
        });
        frequentQuestions = findViewById(R.id.relativeFrequent);
        frequentQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FrequentQuestions.class);
                startActivity(intent);
                drawer.closeDrawers();
            }
        });

        TrackOrder = findViewById(R.id.relativetrackOrder);
        TrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobileNumber.equals("")) {
                    displayAltertLogin();
                } else {
                    isFrommyaccount = false;
                    Intent intent = new Intent(getApplicationContext(), OrderInformation.class);
                    startActivity(intent);
                }
                drawer.closeDrawers();
            }
        });

       /* sellerRegistration = findViewById(R.id.relativesellProdcuts);
        sellerRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, SellerRegistrationActivity.class);
                startActivity(intent);

            }
        });*/

        Refund = findViewById(R.id.relativeRefund);
        Refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReturnAndRefundPolicy.class);
                startActivity(intent);
                drawer.closeDrawers();
            }
        });
        TermsAndConditions = findViewById(R.id.relativeTerms);
        TermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TermsAndConditions.class);
                startActivity(intent);
                drawer.closeDrawers();
            }
        });
        privacyPolicy = findViewById(R.id.relativePrivacy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PrivacyPolicy.class);
                startActivity(intent);
                drawer.closeDrawers();
            }
        });

        ContactUs = findViewById(R.id.relative2);
        ContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
                startActivity(intent);
                drawer.closeDrawers();
            }
        });

        AboutUs = findViewById(R.id.relativeAboutUs);
        AboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AboutUs.class));
                drawer.closeDrawers();
            }
        });

        recyclerView = findViewById(R.id.rvNavigation);
        ProductsDataController.getInstance().fetchProducts(HomeActivity.this, recyclerView);

        drawer = findViewById(R.id.drawerlayoutabout);
        sideMenuFragment = (SideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.fragment1);
        sideMenuFragment.setup(R.id.fragment1, drawer, toolbar);
        sideMenuFragment.setFragmentDrawerListner(this);
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        text_search.setText("Search");
        text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Collapse the action view
                searchProduct.onActionViewCollapsed();
                searchProduct.setQuery("", false);
                searchProduct.setFocusable(false);
                searchProduct.onActionViewExpanded(); //new Added line
                searchProduct.setIconifiedByDefault(false);
                searchProduct.setQueryHint(Html.fromHtml("<font size = 1sp>" + "Search a product" + "</font>"));
                if (!searchProduct.isFocused()) {
                    searchProduct.clearFocus();
                }
                searchProduct.setQueryHint(Html.fromHtml("<font size = 1sp>" + "Search a product" + "</font>"));
                text_search.setText("Search");
            }
        });
        homeAdapter = new HomeAdapter(HomeActivity.this, HomeProductsDataController.getInstance().homeProductsArraylist,
                CartController.getInstace().cartListArray);
        homeRecyclerview.setAdapter(homeAdapter);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.e("handler", "second");
                recylerViewState = homeRecyclerview.getLayoutManager().onSaveInstanceState();
                merchant = sharedPreferences.getString("merchantId","");
                HomeProductsDataController.getInstance().FetchHomeagainProducts(HomeActivity.this,merchant);
                homeRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                homeAdapter = new HomeAdapter(HomeActivity.this, HomeProductsDataController.getInstance().homeProductsArraylist,
                        CartController.getInstace().cartListArray);
                homeRecyclerview.setAdapter(homeAdapter);
                homeAdapter.notifyDataSetChanged();
                homeRecyclerview.getLayoutManager().onRestoreInstanceState(recylerViewState);
                /* post new handler to re-trigger in 30 seconds */
                // wrap this in IF statement to make a way of stopping the looping.
                handler.postDelayed(this, DELAY_TIME);
            }
        };
        handler.postDelayed(runnable, DELAY_TIME);
    }
    //initializing the navigation drawer
    public void initializeDrawer() {
        drawer = findViewById(R.id.drawerlayoutabout);
        drawerFragment = (SideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.fragment1);
        drawerFragment.setup(R.id.fragment1, drawer, toolbar);
        drawerFragment.setDrawerListener(this);
    }

    @Override
    public void onAddProduct(String cartCount, String cartAmount) {
        itemsCount.setVisibility(View.VISIBLE);
        itemsCount.setText(cartCount);
        itemsAmount.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
        if (Integer.valueOf(cartCount) == 0 && Integer.valueOf(cartAmount) == 0) {
            itemsCount.setVisibility(View.GONE);
            itemsAmount.setText("");
        }
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        if (CartController.getInstace().cartListArray != null && CartController.getInstace().cartListArray.size() > 0) {
            cartCount = 0;
            cartAmount = 0;
            for (int i = 0; i < CartController.getInstace().cartListArray.size(); i++) {
                ArrayList<CartList> cartLists = CartController.getInstace().cartListArray;
                cartCount = cartCount + Integer.valueOf(cartLists.get(i).getQuantity());
                cartAmount = cartAmount + Integer.valueOf(cartLists.get(i).getQuantity()) * Integer.valueOf(cartLists.get(i).getVmartPrice());
                if (cartCount > 0 && cartAmount > 0) {
                    itemsCount.setVisibility(View.VISIBLE);
                    itemsAmount.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
                    itemsCount.setText(valueOf(cartCount));
                } else if (cartCount == 0 && cartAmount == 0) {
                    itemsCount.setVisibility(View.GONE);
                }
            }
        } else if (CartController.getInstace().cartListArray.size() == 0) {
            itemsCount.setVisibility(View.GONE);
            itemsAmount.setText("");
            cartCount = 0;
            cartAmount = 0;
        }
        homeAdapter = new HomeAdapter(HomeActivity.this, HomeProductsDataController.getInstance().homeProductsArraylist,
                CartController.getInstace().cartListArray);
        homeRecyclerview.setAdapter(homeAdapter);
        if (scrollpos > 0) {
            homeRecyclerview.scrollToPosition(scrollpos);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
        recylerViewState = homeRecyclerview.getLayoutManager().onSaveInstanceState();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SideMenuFragment.MessageEvent event) {
        String resultData = event.message.trim();
        if (resultData.equals("fetchHomeProducts")) {
            homeAdapter = new HomeAdapter(HomeActivity.this, HomeProductsDataController.getInstance().homeProductsArraylist,
                    CartController.getInstace().cartListArray);
            homeRecyclerview.setAdapter(homeAdapter);
            Progress.dismiss(HomeActivity.this);
        }
        if (resultData.equals("refreshCartData")) {
            if (CartController.getInstace().cartListArray != null && CartController.getInstace().cartListArray.size() > 0) {
                cartCount = 0;
                cartAmount = 0;
                ArrayList<CartList> cartLists = new ArrayList<>();
                for (int i = 0; i < CartController.getInstace().cartListArray.size(); i++) {
                    cartLists = CartController.getInstace().cartListArray;
                    cartCount = cartCount + Integer.valueOf(cartLists.get(i).getQuantity());
                    cartAmount = cartAmount + Integer.valueOf(cartLists.get(i).getQuantity()) * Integer.valueOf(cartLists.get(i).getVmartPrice());
                    if (cartCount > 0 && cartAmount > 0) {
                        itemsCount.setVisibility(View.VISIBLE);
                        itemsAmount.setText(getResources().getString(R.string.rupee) + " " + cartAmount + ".00");
                        itemsCount.setText(valueOf(cartCount));
                    } else if (cartCount == 0 && cartAmount == 0) {
                        itemsCount.setVisibility(View.GONE);
                    }
                }
                homeAdapter = new HomeAdapter(HomeActivity.this, HomeProductsDataController.getInstance().homeProductsArraylist,
                        CartController.getInstace().cartListArray);
                homeRecyclerview.setAdapter(homeAdapter);
                homeRecyclerview.scrollToPosition(selectedPosition);
                homeAdapter.notifyDataSetChanged();
            } else if (CartController.getInstace().cartListArray.size() == 0) {
                itemsCount.setVisibility(View.GONE);
                itemsAmount.setText("");
                cartCount = 0;
                cartAmount = 0;
                homeAdapter = new HomeAdapter(HomeActivity.this, HomeProductsDataController.getInstance().homeProductsArraylist,
                        CartController.getInstace().cartListArray);
                homeRecyclerview.setAdapter(homeAdapter);
                homeRecyclerview.scrollToPosition(selectedPosition);
                homeAdapter.notifyDataSetChanged();
            }
            Progress.dismiss(HomeActivity.this);
        } else if (resultData.equals("error")) {
            homeAdapter = new HomeAdapter(HomeActivity.this, HomeProductsDataController.getInstance().homeProductsArraylist,
                    CartController.getInstace().cartListArray);
            homeRecyclerview.setAdapter(homeAdapter);
            homeRecyclerview.scrollToPosition(selectedPosition);
            homeAdapter.notifyDataSetChanged();
            Progress.dismiss(HomeActivity.this);
        }
    }
    //////////////////Adapter class for HomeActivity/////////////////////
    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
        ArrayList<Product> productArrayList;
        ArrayList<ProductInfo> productInfoArrayList = new ArrayList<>();
        ArrayList<ProductInfo> qunatityProductArrayList;
        ArrayList<ProductInfo> addTocartProductArrayList;
        ArrayList<ProductInfo> childProductArrayList;
        int itemPrice, itemvamrPrice;
        String savings;
        QuantityAdapter quantityAdapter;
        private int mExpandedPosition = -1;
        int getSelectedPos;
        int childeSelectePos = 0;
        ArrayList<CartList> cartList;
        Context context;
        ArrayList<Product> filteredProductList;

        public HomeAdapter(Context context, ArrayList<Product> productArrayList, ArrayList<CartList> allCartdata) {
            this.productArrayList = productArrayList;
            this.cartList = allCartdata;
            this.context = context;
            this.filteredProductList = new ArrayList<>();
            // we copy the original list to the filter list and use it for setting row values
            this.filteredProductList.addAll(this.productArrayList);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.items_home, viewGroup, false);
            return new ViewHolder(itemView);
        }

        int lastItemPosition = -1;

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
            lastItemPosition = position;
            if (position > lastItemPosition) {
                // Scrolled Down
                closeKeyboard(viewHolder.itemView);
            } else {
                // Scrolled Up
                closeKeyboard(viewHolder.itemView);
            }
            viewHolder.addTocart.setText("AddToCart");
            viewHolder.CatSaving.setText("Save");
            productInfoArrayList = filteredProductList.get(position).getPinfo();
            for (int i = 0; i < productInfoArrayList.size(); i++) {
                Log.e("quantity", " " + productInfoArrayList.get(i).getSelectedQnt());
                viewHolder.CatName.setText(filteredProductList.get(position).getItemName());
            }

            viewHolder.quantityRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            quantityAdapter = new QuantityAdapter(productInfoArrayList, productArrayList.get(position).getSelectedVarientPos());
            viewHolder.quantityRecyclerview.setAdapter(quantityAdapter);
            viewHolder.quantityRecyclerview.setNestedScrollingEnabled(false);
            viewHolder.quantityRecyclerview.scrollToPosition(viewHolder.getAdapterPosition());

            if (productInfoArrayList != null) {
                if (cartList != null && cartList.size() > 0) {
                    for (int i = 0; i < cartList.size(); i++) {
                        if (cartList.get(i).getProductId().equals(filteredProductList.get(position).getProductId())) {
                            for (int j = 0; j < productInfoArrayList.size(); j++) {
                                String varitants = productInfoArrayList.get(j).getQuantity();
                                if (varitants.equals(cartList.get(i).getNetWeight())) {
                                    ProductInfo productInfo = new ProductInfo();
                                    productInfo.setMrp(cartList.get(i).getMrpPrice());
                                    productInfo.setVmartPrice(cartList.get(i).getVmartPrice());
                                    productInfo.setSelectedQnt(Integer.valueOf(cartList.get(i).getQuantity()));
                                    productInfo.setQuantity(cartList.get(i).getNetWeight());
                                    productInfo.setUrl(cartList.get(i).getUrl());
                                    productInfoArrayList.set(j, productInfo);
                                    Product productObj = filteredProductList.get(position);
                                    productObj.setPinfo(productInfoArrayList);
                                }
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < productInfoArrayList.size(); i++) {
                        ProductInfo productInfoObj = productInfoArrayList.get(i);
                        productInfoObj.setSelectedQnt(0);
                    }
                    cartCount = 0;
                    cartAmount = 0;
                }
                if (productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getSelectedQnt() > 0) {
                    viewHolder.addTocart.setVisibility(View.GONE);
                    viewHolder.addCount.setVisibility(View.VISIBLE);
                    viewHolder.count.setVisibility(View.VISIBLE);
                    viewHolder.minusCount.setVisibility(View.VISIBLE);
                    viewHolder.CatCost.setVisibility(View.INVISIBLE);
                    viewHolder.CatQuntity.setText(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getQuantity());
                    totalMrp = getTotalMrp(valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getSelectedQnt()), productInfoArrayList.get(productArrayList.get(position).getSelectedVarientPos()).getMrp());
                    viewHolder.CatCost.setText("on MRP" + " " + "Rs." + " " + totalMrp + ".00");
                    totalVmart = getTotalVmart(valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getSelectedQnt()), productInfoArrayList.get(productArrayList.get(position).getSelectedVarientPos()).getVmartPrice());
                    viewHolder.CatmartCost.setText("Vedas Mart" + " " + "Rs." + " " + totalVmart + ".00");
                    totalSavings = getTotalSavings(totalMrp, totalVmart);
                    viewHolder.CatSaving.setText("Save" + " " + "Rs." + " " + totalSavings + ".00");
                    viewHolder.qunatityBtn.setText(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getQuantity());
                    viewHolder.count.setText(valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getSelectedQnt()));
                    Glide.with(getApplicationContext())
                            .load(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getUrl())
                            .apply(new RequestOptions().placeholder(R.drawable.loadingpic1))
                            .into(viewHolder.catImage);
                } else {
                    viewHolder.addTocart.setVisibility(View.VISIBLE);
                    viewHolder.addCount.setVisibility(View.GONE);
                    viewHolder.count.setVisibility(View.GONE);
                    viewHolder.minusCount.setVisibility(View.GONE);
                    viewHolder.CatCost.setVisibility(View.VISIBLE);
                    viewHolder.CatQuntity.setText(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getQuantity());
                    viewHolder.CatCost.setText("MRP" + " " + "Rs." + " " + productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getMrp() + ".00");
                    viewHolder.CatmartCost.setText("Vedas Mart" + " " + "Rs." + " " + productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getVmartPrice() + ".00");
                    itemPrice = Integer.valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getMrp().replace(".", ""));
                    itemvamrPrice = Integer.valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getVmartPrice().replace(".", ""));
                    savings = valueOf(itemPrice - itemvamrPrice);
                    viewHolder.CatSaving.setText("Save"+ " " + "Rs." + savings + ".00");
                    viewHolder.qunatityBtn.setText(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getQuantity());
                    viewHolder.count.setText(valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getSelectedQnt()));
                    Glide.with(getApplicationContext())
                            .load(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getUrl())
                            .apply(new RequestOptions().placeholder(R.drawable.loadingpic1))
                            .into(viewHolder.catImage);
                }
            }

            final boolean isExpanded = position == mExpandedPosition;
            viewHolder.qunatityBtn.setActivated(isExpanded);

            viewHolder.quantityRecyclerview.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            viewHolder.qunatityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSelectedPos = viewHolder.getAdapterPosition();
                    mExpandedPosition = isExpanded ? -1 : position;
                    notifyDataSetChanged();
                }
            });

            quantityAdapter.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(int position) {
                    childeSelectePos = position;
                    childProductArrayList = new ArrayList<>();
                    childProductArrayList = filteredProductList.get(getSelectedPos).getPinfo();
                    Product productObj = filteredProductList.get(getSelectedPos);
                    productObj.setSelectedVarientPos(childeSelectePos);
                    notifyDataSetChanged();
                }
            });

            viewHolder.addTocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        if (mobileNumber.equals("")) {
                            displayAltertLogin();
                        } else {
                            childeSelectePos = filteredProductList.get(viewHolder.getAdapterPosition()).getSelectedVarientPos();
                            timestamp = getCurrentTime();
                            productID = filteredProductList.get(viewHolder.getAdapterPosition()).getProductId();
                            productName = filteredProductList.get(viewHolder.getAdapterPosition()).getItemName();

                            if (!viewHolder.minusCount.isEnabled()) {
                                viewHolder.minusCount.setEnabled(true);
                            }

                            addTocartProductArrayList = filteredProductList.get(viewHolder.getAdapterPosition()).getPinfo();
                            final int addcount = addTocartProductArrayList.get(childeSelectePos).getSelectedQnt() + 1;
                            cartCount++;
                            cartAmount = cartAmount + Integer.valueOf(addTocartProductArrayList.get(childeSelectePos).getVmartPrice());
                            selectedPosition = viewHolder.getAdapterPosition();
                            Progress.show(HomeActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CartController.getInstace().AddToCart(HomeActivity.this, productID, productName,
                                            addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                            addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                            valueOf(addcount), mobileNumber,
                                            addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                            timestamp, cartID, cartCount, cartAmount, addTocartProductArrayList.get(childeSelectePos).getUrl(),
                                            position,merchant);
                                    homeAdapter.notifyDataSetChanged();
                                }
                            }, 1000);
                            homeRecyclerview.scrollToPosition(selectedPosition);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
            viewHolder.count.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

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
                public void afterTextChanged(Editable s) {

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
                        closeKeyboard(viewHolder.itemView);
                    }
                }
            });
            viewHolder.count.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    final String quantity = viewHolder.count.getText().toString();
                    if (isConn()) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            Product productObj = filteredProductList.get(viewHolder.getAdapterPosition());
                            childeSelectePos = filteredProductList.get(viewHolder.getAdapterPosition()).getSelectedVarientPos();
                            productID = filteredProductList.get(viewHolder.getAdapterPosition()).getProductId();
                            productName = filteredProductList.get(viewHolder.getAdapterPosition()).getItemName();
                            timestamp = getCurrentTime();
                            addTocartProductArrayList = filteredProductList.get(viewHolder.getAdapterPosition()).getPinfo();
                            if (!quantity.isEmpty()) {
                                cartCount = Integer.valueOf(quantity);
                            } else {
                                cartCount = 0;
                            }
                            cartAmount = cartCount * Integer.valueOf(addTocartProductArrayList.get(childeSelectePos).getVmartPrice());

                            if (quantity.isEmpty() | quantity.equals("0")) {
                                //closeKeyboard(viewHolder.itemView);
                                viewHolder.count.setText(quantity);
                                notifyDataSetChanged();
                            /*cartCount = 0;
                            cartAmount = cartAmount - Integer.valueOf(addTocartProductArrayList.get(childeSelectePos).getVmartPrice());
                            Progress.show(HomeActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CartController.getInstace().deleteCart(HomeActivity.this,
                                            addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                            addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                            !quantity.isEmpty() ? quantity : "0",
                                            addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                            mobileNumber, productID, cartID, productName, timestamp, cartCount, cartAmount,
                                            addTocartProductArrayList.get(childeSelectePos).getUrl(),
                                            childeSelectePos, productArrayList.get(viewHolder.getAdapterPosition()));
                                    Progress.dismiss(HomeActivity.this);
                                }
                            }, 1000);*/
                            } else {
                                closeKeyboard(viewHolder.itemView);
                                selectedPosition = viewHolder.getAdapterPosition();
                                Progress.show(HomeActivity.this);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CartController.getInstace().updateCart(HomeActivity.this,
                                                addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                                addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                                quantity, addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                                mobileNumber, productID, cartID, productName, timestamp, cartCount, cartAmount,
                                                addTocartProductArrayList.get(childeSelectePos).getUrl(),merchant);
                                        homeAdapter.notifyDataSetChanged();
                                    }
                                }, 1000);
                                homeRecyclerview.scrollToPosition(selectedPosition);
                            }
                            return true;
                        }
                    } else {
                        closeKeyboard(viewHolder.itemView);
                        viewHolder.count.setText(quantity);
                        notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });

            ///click event on plus button
            viewHolder.addCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        Product productObj = filteredProductList.get(viewHolder.getAdapterPosition());
                        childeSelectePos = filteredProductList.get(viewHolder.getAdapterPosition()).getSelectedVarientPos();
                        productID = filteredProductList.get(viewHolder.getAdapterPosition()).getProductId();
                        productName = filteredProductList.get(viewHolder.getAdapterPosition()).getItemName();
                        timestamp = getCurrentTime();
                        addTocartProductArrayList = filteredProductList.get(viewHolder.getAdapterPosition()).getPinfo();

                        if (!viewHolder.minusCount.isEnabled()) {
                            viewHolder.minusCount.setEnabled(true);
                        }

                        if (!viewHolder.addCount.isEnabled()) {
                            viewHolder.addCount.setEnabled(true);
                        }

                        int addcount = addTocartProductArrayList.get(childeSelectePos).getSelectedQnt();

                        addcount = addcount + 1;

                        if (addcount > 15) {
                            Toast.makeText(HomeActivity.this, "Maximum 15 units are allowed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        cartCount++;
                        cartAmount = cartCount * Integer.valueOf(addTocartProductArrayList.get(childeSelectePos).getVmartPrice());
                        selectedPosition = viewHolder.getAdapterPosition();
                        Progress.show(HomeActivity.this);
                        final int finalAddcount = addcount;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CartController.getInstace().updateCart(HomeActivity.this,
                                        addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                        addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                        valueOf(finalAddcount), addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                        mobileNumber, productID, cartID, productName, timestamp, cartCount, cartAmount,
                                        addTocartProductArrayList.get(childeSelectePos).getUrl(),merchant);
                                homeAdapter.notifyDataSetChanged();
                            }
                        }, 1000);
                        homeRecyclerview.scrollToPosition(selectedPosition);
                    } else {
                        Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            viewHolder.minusCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        viewHolder.CatCost.setVisibility(View.INVISIBLE);
                        addTocartProductArrayList = filteredProductList.get(viewHolder.getAdapterPosition()).getPinfo();
                        childeSelectePos = filteredProductList.get(viewHolder.getAdapterPosition()).getSelectedVarientPos();
                        Product productObj = filteredProductList.get(viewHolder.getAdapterPosition());
                        timestamp = getCurrentTime();
                        productID = filteredProductList.get(viewHolder.getAdapterPosition()).getProductId();
                        productName = filteredProductList.get(viewHolder.getAdapterPosition()).getItemName();
                        int minuscount = addTocartProductArrayList.get(childeSelectePos).getSelectedQnt();
                        minuscount = minuscount > 0 ? --minuscount : 0;
                        cartCount--;
                        cartAmount = cartAmount - Integer.valueOf(addTocartProductArrayList.get(childeSelectePos).getVmartPrice());

                        if (minuscount == 0) {
                            final int finalMinuscount = minuscount;
                            selectedPosition = viewHolder.getAdapterPosition();
                            Progress.show(HomeActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CartController.getInstace().deleteCart(HomeActivity.this,
                                            addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                            addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                            valueOf(finalMinuscount),
                                            addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                            mobileNumber, productID, cartID, productName, timestamp, cartCount, cartAmount,
                                            addTocartProductArrayList.get(childeSelectePos).getUrl(),
                                            childeSelectePos, productArrayList.get(viewHolder.getAdapterPosition()),merchant);
                                }
                            }, 1000);
                            homeRecyclerview.scrollToPosition(selectedPosition);
                        } else {
                            final int finalMinuscount1 = minuscount;
                            selectedPosition = viewHolder.getAdapterPosition();
                            Progress.show(HomeActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CartController.getInstace().updateCart(HomeActivity.this,
                                            addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                            addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                            valueOf(finalMinuscount1),
                                            addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                            mobileNumber, productID, cartID, productName, timestamp, cartCount, cartAmount,
                                            addTocartProductArrayList.get(childeSelectePos).getUrl(),merchant);
                                }
                            }, 1000);
                            homeRecyclerview.scrollToPosition(selectedPosition);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product productObj = filteredProductList.get(viewHolder.getAdapterPosition());
                    if (filteredProductList.contains(productObj)) {
                    }
                    scrollpos = viewHolder.getAdapterPosition();
                    Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                    intent.putExtra("selectedProdouct", "homeProducts");
                    intent.putExtra("homeSelectedPos", viewHolder.getAdapterPosition());
                    intent.putExtra("childSelectedPos", childeSelectePos);
                    Bundle b = new Bundle();
                    b.putSerializable("product", filteredProductList);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (filteredProductList.size() > 0) {
                return filteredProductList.size();
            } else {
                return 0;
            }
        }

        public void filter(final String text) {
            // Searching could be complex..so we will dispatch it to a different thread.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Clear the filter list
                    filteredProductList.clear();
                    if (TextUtils.isEmpty(text)) {
                        filteredProductList.addAll(productArrayList);
                    } else {
                        for (Product product : productArrayList) {
                            if (product.getItemName().toLowerCase().contains(text.toLowerCase())) {
                                // Adding Matched items
                                filteredProductList.add(product);
                            }
                        }
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView CatName, CatCost, CatmartCost, CatSaving, CatQuntity;
            Button qunatityBtn;
            TextView addTocart;
            EditText count;
            TextView addCount, minusCount;
            ImageView catImage;
            RecyclerView quantityRecyclerview;
            LinearLayout qunatityBtnLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                CatName = itemView.findViewById(R.id.cat_title);
                CatCost = itemView.findViewById(R.id.textmrp);
                CatmartCost = itemView.findViewById(R.id.text_vmart);
                CatQuntity = itemView.findViewById(R.id.cat_quantity);
                CatSaving = itemView.findViewById(R.id.text_save);
                qunatityBtn = itemView.findViewById(R.id.quantity_btn);
                addTocart = itemView.findViewById(R.id.btn_addTocart);
                addCount = itemView.findViewById(R.id.text_plus);
                count = itemView.findViewById(R.id.editText_count);
                minusCount = itemView.findViewById(R.id.text_minus);
                catImage = itemView.findViewById(R.id.cat_img);
                qunatityBtnLayout = itemView.findViewById(R.id.quantity_btn_layout);
                quantityRecyclerview = itemView.findViewById(R.id.quantity_recyclerview);
            }
        }
    }

    class QuantityAdapter extends RecyclerView.Adapter<QuantityAdapter.ViewHolder> {

        ArrayList<ProductInfo> productInfoArrayList;
        int quantitySelectePos;
        public ItemClickListener clickListener;
        int selectedVaraintPos;

        public QuantityAdapter(ArrayList<ProductInfo> productInfoArrayList, int selectedVarientPos) {
            this.productInfoArrayList = productInfoArrayList;
            this.selectedVaraintPos = selectedVarientPos;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.items_quantites, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.quantitybtn.setText(productInfoArrayList.get(position).getQuantity());
            if (selectedVaraintPos == position) {
                holder.quantitybtn.getBackground().setColorFilter(Color.parseColor("#18c252"), PorterDuff.Mode.MULTIPLY);
                holder.quantitybtn.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                holder.quantitybtn.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.MULTIPLY);
                holder.quantitybtn.setTextColor(Color.parseColor("#000000"));
            }

            holder.quantitybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedVaraintPos = position;
                    quantitySelectePos = holder.getAdapterPosition();
                    if (clickListener != null) {
                        clickListener.onClick(quantitySelectePos);
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return productInfoArrayList.size();
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            Button quantitybtn;

            public ViewHolder(View itemView) {
                super(itemView);
                quantitybtn = itemView.findViewById(R.id.quantity_displaybtn);
            }
        }
    }

    public interface ItemClickListener {
        void onClick(int position);
    }

    ////////for unix time stamp
    public String getCurrentTime() {
        long unixTime;
        unixTime = System.currentTimeMillis() / 1L;
        String attempt_time = valueOf(System.currentTimeMillis() / 1000L);
        Log.e("attem", "" + attempt_time);
        return attempt_time;
    }

    ///getting the total Mrp for adding
    public String getTotalMrp(String count, String mrp) {
        int totalmrp = Integer.valueOf(count) * Integer.valueOf(mrp);
        return valueOf(totalmrp);
    }

    ///getting the totalVmart for adding
    public String getTotalVmart(String count, String vmart) {
        int totalVmart = Integer.valueOf(count) * Integer.valueOf(vmart);
        return valueOf(totalVmart);
    }

    public String getTotalSavings(String totalMrp, String totalVmart) {
        String totalSavings = valueOf(Integer.valueOf(totalMrp) - Integer.valueOf(totalVmart));
        return totalSavings;
    }

    /////display  if no user exist
    public void displayAltertLogin() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        // set dialog message
        alertDialogBuilder
                .setMessage("Please Sign In")
                //.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finishAffinity();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    // closing the keyboard for editext(Search functionality)
    private void closeKeyboard(View view) {
        Log.e("closeKeyboard", "call");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getCurrentLocationPinCode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!LocationDetailsHelper.getInstance(this).isLocationEnabled()) {
                LocationDetailsHelper.getInstance(this).showAlert(this);
                LocationDetailsHelper.getInstance(this).getFusedLocationDetails();
            } else {
                LocationDetailsHelper.getInstance(this).getFusedLocationDetails();
            }
        } else {
            LocationDetailsHelper.getInstance(this).requestLocationPermissions(HomeActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocationPinCode();
                } else {
                    //  Toast.makeText(this, "Permission DENIED \nUnable to Access Your Pincode", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {    //when click on phone backbutton
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) { // import method from InternetConnection for internet checking, this method call automatically
        if (!isConnected) {
            InternetDialog.interNet(HomeActivity.this);
        }
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }
}