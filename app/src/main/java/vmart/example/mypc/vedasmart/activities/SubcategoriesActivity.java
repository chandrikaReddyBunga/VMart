package vmart.example.mypc.vedasmart.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.Collections;
import vmart.example.mypc.vedasmart.helper.InputFilterMinMax;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.CartController;
import vmart.example.mypc.vedasmart.controllers.SubProductsDataController;
import vmart.example.mypc.vedasmart.fragments.SideMenuFragment;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.interfaces.AddorRemoveCallbacks;
import vmart.example.mypc.vedasmart.model.CartList;
import vmart.example.mypc.vedasmart.model.Product;
import vmart.example.mypc.vedasmart.model.ProductInfo;
import vmart.example.mypc.vedasmart.model.SubItems;
import vmart.example.mypc.vedasmart.sessions.SessionManager;

public class SubcategoriesActivity extends AppCompatActivity implements AddorRemoveCallbacks {
    public int selectedPosition = 0;
    Toolbar toolbar;
    ImageView cartIcon;
    RecyclerView recyclerView;
    ArrayList<SubItems> subItemsArrayList;
    ArrayList<Product> productArrayList;
    SubAdapter subAdapter;
    String selectedGrains;
    TextView toolbarText;
    public int cartCount = 0, cartAmount = 0;
    TextView itemsCount, itemsAmount, edit_search, text_search;
    String totalMrp, totalVmart, totalSavings;
    String timestamp;
    String productID, productName;
    String mobileNumber, cartID,merchant;
    SharedPreferences sharedPreferences;
    RelativeLayout relativeBack;
    ArrayList<Product> ReleventArraylist, tempRelevantArraylist;
    LinearLayout sortLinerLayout, filterLinearLayout;
    RelativeLayout optionmenulayout;
    int previousSeectedPosition = -1;
    public static int scrollpos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategories);
        CartController.getInstace().setListener(this);
        /////getting the cartID,mobilenumber and merchantid that are store in sharedpreference
        sharedPreferences = getSharedPreferences("vmart", MODE_PRIVATE);
        cartID = sharedPreferences.getString(LoginActivity.cartid, " ");
        mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        merchant = sharedPreferences.getString("merchantId","");
        Intent intent = getIntent();
        selectedGrains = intent.getStringExtra("grains");
        subItemsArrayList = new ArrayList<>();
        subItemsArrayList = SubProductsDataController.getInstance().subItemsArrayList;
        if (subItemsArrayList.size() > 0) {
            for (int i = 0; i < subItemsArrayList.size(); i++) {
                productArrayList = subItemsArrayList.get(i).getProducts();
            }
            ReleventArraylist = new ArrayList<>(productArrayList);
        } else if (subItemsArrayList == null) {
            Toast.makeText(getApplicationContext(), "No items available", Toast.LENGTH_SHORT).show();
        }
        init();
        edit_search.setText("Filter");
        text_search.setText("Sort");
    }

    public void init() {
        toolbar = findViewById(R.id.subcategory_toolbar);
        toolbarText = findViewById(R.id.subProduct_titla);
        cartIcon = findViewById(R.id.icon_badge);
        itemsCount = findViewById(R.id.count);
        itemsAmount = findViewById(R.id.totalAmount);
        sortLinerLayout = findViewById(R.id.linear_sort);
        filterLinearLayout = findViewById(R.id.linearfilter);
        filterLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SubcategoriesActivity.this, "No operations as of now", Toast.LENGTH_SHORT).show();
            }
        });
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubcategoriesActivity.this, CartActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("productArraylist", productArrayList);
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
            }
        });
        optionmenulayout = findViewById(R.id.options_menu_layout);
        optionmenulayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SubcategoriesActivity.this, MyAccountActivity.class));
            }
        });
        relativeBack = findViewById(R.id.relative_back);
        relativeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (selectedGrains != null) {
            toolbarText.setText(selectedGrains);
        }
        edit_search = findViewById(R.id.edit_search);
        edit_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SubcategoriesActivity.this,  "No operations as of now", Toast.LENGTH_SHORT).show();
            }
        });
        text_search = findViewById(R.id.text_search);
        recyclerView = findViewById(R.id.subCategories_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subAdapter = new SubAdapter(SubcategoriesActivity.this, ReleventArraylist, CartController.getInstace().cartListArray);
        recyclerView.setAdapter(subAdapter);
        sortLinerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortDialogPrices();
            }
        });
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
                    itemsCount.setText(String.valueOf(cartCount));
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
        subAdapter = new SubAdapter(SubcategoriesActivity.this, ReleventArraylist, CartController.getInstace().cartListArray);
        recyclerView.setAdapter(subAdapter);
        if (scrollpos > 0) {
            recyclerView.scrollToPosition(scrollpos);
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
        if (resultData.equals("refreshLanguages")) {
            Log.e("LanguageupdateInfo", "Called");
        }
        if (resultData.equals("refreshCartData")) {
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
                        itemsCount.setText(String.valueOf(cartCount));
                    } else if (cartCount == 0 && cartAmount == 0) {
                        itemsCount.setVisibility(View.GONE);
                    }
                }
                subAdapter = new SubAdapter(SubcategoriesActivity.this, ReleventArraylist, CartController.getInstace().cartListArray);
                recyclerView.setAdapter(subAdapter);
                recyclerView.scrollToPosition(selectedPosition);
                subAdapter.notifyDataSetChanged();
                Progress.dismiss(SubcategoriesActivity.this);
            } else if (CartController.getInstace().cartListArray.size() == 0) {
                itemsCount.setVisibility(View.GONE);
                itemsAmount.setText("");
                cartCount = 0;
                cartAmount = 0;
                subAdapter = new SubAdapter(SubcategoriesActivity.this, ReleventArraylist, CartController.getInstace().cartListArray);
                recyclerView.setAdapter(subAdapter);
                recyclerView.scrollToPosition(selectedPosition);
                subAdapter.notifyDataSetChanged();
            }
            Progress.dismiss(SubcategoriesActivity.this);
        } else if (resultData.equals("error")) {
            Progress.dismiss(SubcategoriesActivity.this);
        }
    }

    public void SortDialogPrices() {
        RelativeLayout Low, High, Relevance;
        final Dialog dialog = new Dialog(SubcategoriesActivity.this);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_sort);
        Low = dialog.findViewById(R.id.rl_lowtoHigh);
        High = dialog.findViewById(R.id.rl_hightoLow);
        Relevance = dialog.findViewById(R.id.rl_Relevance);
        TextView sortby = dialog.findViewById(R.id.sortby);
        TextView relevance = dialog.findViewById(R.id.relevance);
        TextView lowtohigh = dialog.findViewById(R.id.lowtohigh);
        TextView hightolow = dialog.findViewById(R.id.hightolow);
        sortby.setText("SORT BY");
        relevance.setText("Relevance");
        lowtohigh.setText( "Price" + ": " + "Low to High");
        hightolow.setText("Price" + ": " +  "High to Low");

        Relevance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                ReleventArraylist = null;
                ReleventArraylist = new ArrayList<>(productArrayList);
                subAdapter = new SubAdapter(SubcategoriesActivity.this, ReleventArraylist, CartController.getInstace().cartListArray);
                subAdapter.setListener(SubcategoriesActivity.this);
                recyclerView.setAdapter(subAdapter);
            }
        });

        Low.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                dialog.cancel();
                comparingarray();
                subAdapter = new SubAdapter(SubcategoriesActivity.this, tempRelevantArraylist, CartController.getInstace().cartListArray);
                subAdapter.setListener(SubcategoriesActivity.this);
                recyclerView.setAdapter(subAdapter);
                recyclerView.scrollToPosition(0);
                subAdapter.notifyDataSetChanged();
            }
        });

        High.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                dialog.cancel();
                comparingdummyarray();
                subAdapter = new SubAdapter(SubcategoriesActivity.this, tempRelevantArraylist, CartController.getInstace().cartListArray);
                subAdapter.setListener(SubcategoriesActivity.this);
                recyclerView.setAdapter(subAdapter);
                recyclerView.scrollToPosition(0);
                subAdapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("NewApi")
    private ArrayList<Product> comparingdummyarray() {
        tempRelevantArraylist = null;
        tempRelevantArraylist = new ArrayList<>();
        ReleventArraylist = null;
        ReleventArraylist = new ArrayList<>(productArrayList);
        Collections.sort(ReleventArraylist, Product.BYPRICE_LOWTOHIGH);
        Collections.reverse(ReleventArraylist);
        tempRelevantArraylist = ReleventArraylist;
        return tempRelevantArraylist;
    }

    @SuppressLint("NewApi")
    private ArrayList<Product> comparingarray() {
        tempRelevantArraylist = null;
        tempRelevantArraylist = new ArrayList<>();
        ReleventArraylist = null;
        ReleventArraylist = new ArrayList<>(productArrayList);
        Collections.sort(ReleventArraylist, Product.BYPRICE_LOWTOHIGH);
        tempRelevantArraylist = ReleventArraylist;
        return tempRelevantArraylist;
    }

    class SubAdapter extends RecyclerView.Adapter<SubAdapter.ViewHolder> {
        Context context;
        ArrayList<Product> productArrayList;
        ArrayList<ProductInfo> productInfoArrayList = new ArrayList<>();
        ArrayList<CartList> cartListArray;
        int itemPrice, itemvamrPrice;
        String savings;
        private int mExpandedPosition = -1;
        QuantityAdapter quantityAdapter;
        int childeSelectePos = 0;
        ArrayList<ProductInfo> addTocartProductArrayList;
        ArrayList<ProductInfo> childProductArrayList;
        int getSelectedPos;
        private AddorRemoveCallbacks listener;
        ArrayList<Product> filteredProductList;
        int lastItemPosition = -1;

        public SubAdapter(Context context, ArrayList<Product> productArrayList, ArrayList<CartList> cartListArray) {
            this.context = context;
            this.productArrayList = productArrayList;
            this.cartListArray = cartListArray;
            this.filteredProductList = new ArrayList<>();
            this.filteredProductList.addAll(this.productArrayList);
        }

        public void setListener(AddorRemoveCallbacks listener) {
            this.listener = listener;
        }
        @NonNull
        @Override
        public SubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.items_home, viewGroup, false);
            return new SubcategoriesActivity.SubAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SubAdapter.ViewHolder holder, final int position) {
            lastItemPosition = position;
            if (position > lastItemPosition) {
                // Scrolled Down
                closeKeyboard();
            } else {
                // Scrolled Up
                closeKeyboard();
            }
            holder.CatName.setText(productArrayList.get(position).getItemName());
            holder.addTocart.setText("AddToCart");
            holder.CatSaving.setText("Save");
            productInfoArrayList = filteredProductList.get(position).getPinfo();
            for (int i = 0; i < productInfoArrayList.size(); i++) {
            }
            holder.quantityRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            quantityAdapter = new QuantityAdapter(productInfoArrayList, productArrayList.get(position).getSelectedVarientPos());
            holder.quantityRecyclerview.setAdapter(quantityAdapter);
            holder.quantityRecyclerview.setNestedScrollingEnabled(false);
            holder.quantityRecyclerview.scrollToPosition(holder.getAdapterPosition());
            if (productInfoArrayList != null) {
                if (cartListArray != null && cartListArray.size() > 0) {
                    for (int i = 0; i < cartListArray.size(); i++) {
                        if (cartListArray.get(i).getProductId().equals(productArrayList.get(position).getProductId())) {
                            for (int j = 0; j < productInfoArrayList.size(); j++) {
                                String varitants = productInfoArrayList.get(j).getQuantity();
                                if (varitants.equals(cartListArray.get(i).getNetWeight())) {
                                    ProductInfo productInfo = new ProductInfo();
                                    productInfo.setMrp(cartListArray.get(i).getMrpPrice());
                                    productInfo.setVmartPrice(cartListArray.get(i).getVmartPrice());
                                    productInfo.setSelectedQnt(Integer.valueOf(cartListArray.get(i).getQuantity()));
                                    productInfo.setQuantity(cartListArray.get(i).getNetWeight());
                                    productInfo.setUrl(cartListArray.get(i).getUrl());
                                    productInfoArrayList.set(j, productInfo);
                                    Product productObj = productArrayList.get(position);
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
                    holder.addTocart.setVisibility(View.GONE);
                    holder.addCount.setVisibility(View.VISIBLE);
                    holder.count.setVisibility(View.VISIBLE);
                    holder.minusCount.setVisibility(View.VISIBLE);
                    holder.CatCost.setVisibility(View.INVISIBLE);
                    holder.CatQuntity.setText(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getQuantity());
                    totalMrp = getTotalMrp(String.valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getSelectedQnt()), productInfoArrayList.get(productArrayList.get(position).getSelectedVarientPos()).getMrp().replace(".", ""));
                    holder.CatCost.setText("on MRP" + " " + totalMrp);
                    totalVmart = getTotalVmart(String.valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getSelectedQnt()), productInfoArrayList.get(productArrayList.get(position).getSelectedVarientPos()).getVmartPrice());
                    holder.CatmartCost.setText("Vedas Mart" + " " + "Rs." + " " + totalVmart + ".00");
                    totalSavings = getTotalSavings(totalMrp, totalVmart);
                    holder.CatSaving.setText("Save" + " " + "Rs." + " " + totalSavings + ".00");
                    holder.qunatityBtn.setText(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getQuantity());
                    holder.count.setText(String.valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getSelectedQnt()));
                    Glide.with(getApplicationContext()).load(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getUrl())
                            .apply(new RequestOptions().placeholder(R.drawable.loadingpic1))
                            .into(holder.catImage);
                } else {
                    holder.addTocart.setVisibility(View.VISIBLE);
                    holder.addCount.setVisibility(View.GONE);
                    holder.count.setVisibility(View.GONE);
                    holder.minusCount.setVisibility(View.GONE);
                    holder.CatCost.setVisibility(View.VISIBLE);
                    holder.CatQuntity.setText(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getQuantity());
                    holder.CatCost.setText("on MRP" + " " +  "Rs." + productInfoArrayList.get(productArrayList.get(position).getSelectedVarientPos()).getMrp() + ".00");
                    holder.CatmartCost.setText("Vedas Mart" + " " + "Rs." + " " + productInfoArrayList.get(productArrayList.get(position).getSelectedVarientPos()).getVmartPrice() + ".00");
                    itemPrice = Integer.valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getMrp().replace(".", ""));
                    itemvamrPrice = Integer.valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getVmartPrice().replace(".", ""));
                    savings = String.valueOf(itemPrice - itemvamrPrice);
                    holder.CatSaving.setText("Save" + " " + "Rs." + savings + ".00");
                    holder.qunatityBtn.setText(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getQuantity());
                    holder.count.setText(String.valueOf(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getSelectedQnt()));
                    Glide.with(getApplicationContext()).load(productInfoArrayList.get(filteredProductList.get(position).getSelectedVarientPos()).getUrl())
                            .apply(new RequestOptions().placeholder(R.drawable.loadingpic1))
                            .into(holder.catImage);
                }
            }
            final boolean isExpanded = position == mExpandedPosition;
            holder.qunatityBtn.setActivated(isExpanded);
            holder.quantityRecyclerview.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.qunatityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSelectedPos = holder.getAdapterPosition();
                    mExpandedPosition = isExpanded ? -1 : position;
                    notifyDataSetChanged();
                }
            });
            quantityAdapter.setClickListener(new SubcategoriesActivity.ItemClickListener() {
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
            holder.addTocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        if (mobileNumber.equals("")) {
                            displayAltertLogin();
                        } else {
                            childeSelectePos = productArrayList.get(holder.getAdapterPosition()).getSelectedVarientPos();
                            timestamp = getCurrentTime();
                            productID = productArrayList.get(holder.getAdapterPosition()).getProductId();
                            productName = productArrayList.get(holder.getAdapterPosition()).getItemName();
                            if (!holder.minusCount.isEnabled()) {
                                holder.minusCount.setEnabled(true);
                            }
                            addTocartProductArrayList = productArrayList.get(holder.getAdapterPosition()).getPinfo();
                            final int addcount = addTocartProductArrayList.get(childeSelectePos).getSelectedQnt() + 1;
                            cartCount++;
                            cartAmount = cartAmount + Integer.valueOf(addTocartProductArrayList.get(childeSelectePos).getVmartPrice());
                            selectedPosition = holder.getAdapterPosition();
                            Progress.show(SubcategoriesActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CartController.getInstace().AddToCart(SubcategoriesActivity.this, productID, productName,
                                            addTocartProductArrayList.get(childeSelectePos).getMrp(), addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                            String.valueOf(addcount), mobileNumber, addTocartProductArrayList.get(childeSelectePos).getQuantity(), timestamp,
                                            cartID, cartCount, cartAmount, addTocartProductArrayList.get(childeSelectePos).getUrl(), position,merchant);
                                    subAdapter.notifyDataSetChanged();
                                }
                            }, 1000);
                            recyclerView.scrollToPosition(selectedPosition);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.count.setFilters(new InputFilter[]{new InputFilterMinMax("1", "15", getApplicationContext())});
            holder.count.setFocusable(false);
            holder.count.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isConn()) {
                        holder.count.setFocusableInTouchMode(true);
                        holder.count.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.count.requestFocus();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });

            holder.count.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (previousSeectedPosition != holder.getLayoutPosition()) {
                            if (previousSeectedPosition >= 0) {
                                notifyItemChanged(previousSeectedPosition);
                            }
                            previousSeectedPosition = holder.getAdapterPosition();
                        }
                    } else {
                        holder.count.setFocusableInTouchMode(false);
                        closeKeyboard();
                    }
                }
            });

            holder.count.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getCurrentFocus() == holder.count) {
                        //restrict 0 as in first lettter in keypad
                        if (s.toString().startsWith("0")) {
                            holder.count.setText(" ");
                            Toast.makeText(getApplicationContext(), "Maximum 15 units are allowed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            holder.count.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    final String quantity = holder.count.getText().toString();
                    if (isConn()) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            Product productObj = filteredProductList.get(holder.getAdapterPosition());
                            childeSelectePos = filteredProductList.get(holder.getAdapterPosition()).getSelectedVarientPos();
                            productID = filteredProductList.get(holder.getAdapterPosition()).getProductId();
                            productName = filteredProductList.get(holder.getAdapterPosition()).getItemName();
                            timestamp = getCurrentTime();
                            addTocartProductArrayList = filteredProductList.get(holder.getAdapterPosition()).getPinfo();
                            if (!quantity.isEmpty()) {
                                cartCount = Integer.valueOf(quantity);
                            } else {
                                cartCount = 0;
                            }
                            cartAmount = cartCount * Integer.valueOf(addTocartProductArrayList.get(childeSelectePos).getVmartPrice());
                            if (quantity.isEmpty() | quantity.equals("0")) {
                                closeKeyboard();
                                holder.count.setText(quantity);
                                notifyDataSetChanged();
                           /* CartController.getInstace().deleteCart(SubcategoriesActivity.this,
                                    addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                    addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                    !quantity.isEmpty() ? quantity : "0", addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                    mobileNumber, productID, cartID, productName, timestamp, cartCount, cartAmount,
                                    addTocartProductArrayList.get(childeSelectePos).getUrl(), childeSelectePos,
                                    productArrayList.get(holder.getAdapterPosition()));*/
                                recyclerView.scrollToPosition(holder.getAdapterPosition());
                            } else {
                                closeKeyboard();
                                Progress.show(SubcategoriesActivity.this);
                                selectedPosition = holder.getAdapterPosition();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CartController.getInstace().updateCart(SubcategoriesActivity.this,
                                                addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                                addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                                quantity, addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                                mobileNumber, productID, cartID, productName, timestamp, cartCount, cartAmount,
                                                addTocartProductArrayList.get(childeSelectePos).getUrl(),merchant);
                                        subAdapter.notifyDataSetChanged();
                                    }
                                }, 1000);
                                recyclerView.scrollToPosition(selectedPosition);
                            }
                            return true;
                        }
                    } else {
                        closeKeyboard();
                        holder.count.setText(quantity);
                        notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
            ///click event on plus button
            holder.addCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        Product productObj = filteredProductList.get(holder.getAdapterPosition());
                        childeSelectePos = filteredProductList.get(holder.getAdapterPosition()).getSelectedVarientPos();
                        productID = filteredProductList.get(holder.getAdapterPosition()).getProductId();
                        productName = filteredProductList.get(holder.getAdapterPosition()).getItemName();
                        timestamp = getCurrentTime();
                        addTocartProductArrayList = filteredProductList.get(holder.getAdapterPosition()).getPinfo();
                        if (!holder.minusCount.isEnabled()) {
                            holder.minusCount.setEnabled(true);
                        }
                        if (!holder.addCount.isEnabled()) {
                            holder.addCount.setEnabled(true);
                        }
                        int addcount = addTocartProductArrayList.get(childeSelectePos).getSelectedQnt();
                        addcount = addcount + 1;
                        if (addcount > 15) {
                            Toast.makeText(SubcategoriesActivity.this, "Maximum 15 units are allowed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        cartCount++;
                        cartAmount = cartCount * Integer.valueOf(addTocartProductArrayList.get(childeSelectePos).getVmartPrice());
                        Progress.show(SubcategoriesActivity.this);
                        final int finalAddcount = addcount;
                        selectedPosition = holder.getAdapterPosition();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CartController.getInstace().updateCart(SubcategoriesActivity.this,
                                        addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                        addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                        String.valueOf(finalAddcount), addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                        mobileNumber, productID, cartID, productName, timestamp, cartCount, cartAmount,
                                        addTocartProductArrayList.get(childeSelectePos).getUrl(),merchant);
                                subAdapter.notifyDataSetChanged();
                            }
                        }, 1000);
                        recyclerView.scrollToPosition(selectedPosition);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.minusCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConn()) {
                        holder.CatCost.setVisibility(View.INVISIBLE);
                        addTocartProductArrayList = filteredProductList.get(holder.getAdapterPosition()).getPinfo();
                        childeSelectePos = filteredProductList.get(holder.getAdapterPosition()).getSelectedVarientPos();
                        Product productObj = filteredProductList.get(holder.getAdapterPosition());
                        timestamp = getCurrentTime();
                        productID = filteredProductList.get(holder.getAdapterPosition()).getProductId();
                        productName = filteredProductList.get(holder.getAdapterPosition()).getItemName();
                        int minuscount = addTocartProductArrayList.get(childeSelectePos).getSelectedQnt();
                        minuscount = minuscount > 0 ? --minuscount : 0;
                        cartCount--;
                        cartAmount = cartAmount - Integer.valueOf(addTocartProductArrayList.get(childeSelectePos).getVmartPrice());

                        if (minuscount == 0) {
                            final int finalMinuscount1 = minuscount;
                            selectedPosition = holder.getAdapterPosition();
                            Progress.show(SubcategoriesActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CartController.getInstace().deleteCart(SubcategoriesActivity.this,
                                            addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                            addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                            String.valueOf(finalMinuscount1), addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                            mobileNumber, productID, cartID, productName, timestamp, cartCount, cartAmount,
                                            addTocartProductArrayList.get(childeSelectePos).getUrl(), childeSelectePos,
                                            productArrayList.get(holder.getAdapterPosition()),merchant);
                                    subAdapter.notifyDataSetChanged();
                                }
                            }, 1000);
                            recyclerView.scrollToPosition(selectedPosition);
                        } else {
                            final int finalMinuscount = minuscount;
                            selectedPosition = holder.getAdapterPosition();
                            Progress.show(SubcategoriesActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CartController.getInstace().updateCart(SubcategoriesActivity.this,
                                            addTocartProductArrayList.get(childeSelectePos).getMrp(),
                                            addTocartProductArrayList.get(childeSelectePos).getVmartPrice(),
                                            String.valueOf(finalMinuscount), addTocartProductArrayList.get(childeSelectePos).getQuantity(),
                                            mobileNumber, productID, cartID, productName, timestamp, cartCount, cartAmount,
                                            addTocartProductArrayList.get(childeSelectePos).getUrl(),merchant);
                                    subAdapter.notifyDataSetChanged();
                                }
                            }, 1000);
                            recyclerView.scrollToPosition(selectedPosition);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product productObj = filteredProductList.get(holder.getAdapterPosition());
                    scrollpos = holder.getAdapterPosition();
                    Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                    intent.putExtra("selectedProdouct", "homeProducts");
                    intent.putExtra("homeSelectedPos", holder.getAdapterPosition());
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

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView CatName, CatCost, CatmartCost, CatSaving, CatQuntity;
            Button qunatityBtn;
            TextView addTocart;
            TextView addCount, minusCount;
            ImageView catImage;
            EditText count;
            RecyclerView quantityRecyclerview;

            public ViewHolder(View itemView) {
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
                quantityRecyclerview = itemView.findViewById(R.id.quantity_recyclerview);
            }
        }
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    class QuantityAdapter extends RecyclerView.Adapter<SubcategoriesActivity.QuantityAdapter.ViewHolder> {
        ArrayList<ProductInfo> productInfoArrayList;
        int quantitySelectePos;
        public SubcategoriesActivity.ItemClickListener clickListener;
        int selectedVaraintPos;

        public QuantityAdapter(ArrayList<ProductInfo> productInfoArrayList, int selectedVarientPos) {
            this.productInfoArrayList = productInfoArrayList;
            this.selectedVaraintPos = selectedVarientPos;
        }

        @NonNull
        @Override
        public SubcategoriesActivity.QuantityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.items_quantites, parent, false);
            return new SubcategoriesActivity.QuantityAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SubcategoriesActivity.QuantityAdapter.ViewHolder holder, final int position) {
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

        public void setClickListener(SubcategoriesActivity.ItemClickListener itemClickListener) {
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

    ////////for unix time stamp
    public String getCurrentTime() {
        long unixTime;
        unixTime = System.currentTimeMillis() / 1L;
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1L);
        return attempt_time;
    }

    /////display  if no user exist
    public void displayAltertLogin() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SubcategoriesActivity.this);
        // set dialog message
        alertDialogBuilder
                .setMessage("Please Sign In")
                .setCancelable(false)
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                        startActivity(new Intent(SubcategoriesActivity.this, LoginActivity.class));
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