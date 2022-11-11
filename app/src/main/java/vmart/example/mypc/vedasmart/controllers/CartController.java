package vmart.example.mypc.vedasmart.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vmart.example.mypc.vedasmart.activities.LocationTracker;
import vmart.example.mypc.vedasmart.fragments.SideMenuFragment;
import vmart.example.mypc.vedasmart.helper.LocationDetailsHelper;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.interfaces.AddorRemoveCallbacks;
import vmart.example.mypc.vedasmart.model.AllCartList;
import vmart.example.mypc.vedasmart.model.CartList;
import vmart.example.mypc.vedasmart.model.Product;
import vmart.example.mypc.vedasmart.model.ProductInfo;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import static vmart.example.mypc.vedasmart.helper.LocationDetailsHelper.pinCodeData;

public class CartController {
    public static CartController addtoCartObj;
    public ArrayList<AllCartList> allCartLists;
    public ArrayList<CartList> cartListArray;
    private AddorRemoveCallbacks listener;
    Context context;
    ServerApiInterface serverApiInterface;
    static String[] tempArray;

    public static String[] add(String[] originalArray, String newItem) {
        int currentSize = originalArray.length;
        int newSize = currentSize + 1;
        tempArray = new String[ newSize ];
        for (int i=0; i < currentSize; i++)
        {
            tempArray[i] = originalArray [i];
        }
        tempArray[newSize- 1] = newItem;
        Log.e("temp",""+tempArray);
        return tempArray;
    }

    private String[] pincodesArray = {"516434","518145","560063", "560034", "560007", "560092", "560024", "562106", "560045", "560003", "560050", "562107", "560064", "560047", "560026", "560086", "560002", "560070", "560073", "562149", "560053", "560085", "560043", "560017", "560001", "560009", "560025", "560083", "560076", "560004", "560079", "560103", "560046", "562157", "560010", "560049", "560056", "560068", "560093", "560018", "560040", "560097", "560061", "562130", "560067", "560036", "560029", "560062", "560037", "560071", "562125", "560016", "560100", "560005", "560065", "560019", "560021", "560022", "560013", "560087", "560008", "560051", "560102", "560104", "560048", "560030", "560094", "560066", "560038", "560078", "560006", "560014", "560015", "560041", "560069", "560011", "560020", "560084", "560096", "560098", "560095", "560077", "560074", "560054", "560023", "560033", "560055", "560099", "560072", "560039", "560075", "560032", "560058", "560059", "560080", "560027", "560012", "560042", "560028", "560052", "560091"};

    public static CartController getInstace() {
        if (addtoCartObj == null) {
            addtoCartObj = new CartController();
        }
        return addtoCartObj;
    }

    public void fillcontext(Context context) {
        this.context = context;
        allCartLists = new ArrayList<>();
        cartListArray = new ArrayList<>();
    }

    public void setListener(AddorRemoveCallbacks listener) {
        this.listener = listener;
    }
    ////////send selected products to server and add to cart////////
    public void AddToCart(final Context mContext, final String productId, final String itemName, final String mrpPrice,
                          final String vmartPrice, final String qunatity, final String mobileNumber, final String netWeight,
                          final String timeStamp, final String cartID, final int cartCount, final int cartAmount,
                          final String url, final int clickedPosition,String merchant) {
        Log.d("Fused", "cartPin " + pinCodeData);
        if (!getPinStatus(mContext)) return;
        if (pinCodeData == null) {
            if (!getPinStatus(mContext)) return;
            return;
        }else if (!pinCodeData.isEmpty() && !checkIsPincodeMatched(pinCodeData)) {
            //pincodesArray = add(pincodesArray, pinCodeData);
            displayInvalidPincodeAlert(mContext);
            return;
        }
        Log.e("addToCartDetails", "" + productId + " " + itemName + " " + mrpPrice + " " + vmartPrice +
                " " + qunatity + " " + mobileNumber + " " + netWeight + " " + timeStamp+" "+merchant);
        JsonObject cartObj = new JsonObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId", productId);
            jsonObject.put("itemName", itemName);
            jsonObject.put("mrpPrice", mrpPrice);
            jsonObject.put("url",url);
            jsonObject.put("vmartPrice", vmartPrice);
            jsonObject.put("quantity", qunatity);
            jsonObject.put("mobileNumber", mobileNumber);
            jsonObject.put("netWeight", netWeight);
            jsonObject.put("timeStamp", timeStamp);
            jsonObject.put("cartId", cartID);
            jsonObject.put("merchantId",merchant);
            JsonParser jsonParser = new JsonParser();
            cartObj = (JsonObject) jsonParser.parse(jsonObject.toString());
            //Add to cart parameter
            Log.e("addcartJson:", " " + cartObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> addtocartCall = serverApiInterface.addToCart(cartObj);
        addtocartCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONArray resposneArray;
                JSONObject responseObj;
                String statusCode, message;
                Log.e("AddTocartSatus", " " + response.isSuccessful());
                if (response.body() != null) {
                    String addToCartResponse = null;
                    try {
                        addToCartResponse = new String(response.body().bytes());
                        Log.e("AddcartResponse", " " + addToCartResponse);
                        resposneArray = new JSONArray(addToCartResponse);
                        responseObj = resposneArray.getJSONObject(0);
                        statusCode = responseObj.getString("response");
                        message = responseObj.getString("message");
                        if (statusCode.equals("3")) {
                            CartList cartListObj = new CartList();
                            cartListObj.setCartId(cartID);
                            cartListObj.setProductId(productId);
                            cartListObj.setMobileNumber(mobileNumber);
                            cartListObj.setItemName(itemName);
                            cartListObj.setMrpPrice(mrpPrice);
                            cartListObj.setVmartPrice(vmartPrice);
                            cartListObj.setQuantity(qunatity);
                            cartListObj.setNetWeight(netWeight);
                            cartListObj.setTimeStamp(timeStamp);
                            cartListObj.setUrl(url);
                            Log.d("position", "" + clickedPosition);
                            cartListObj.setClickedPosition(clickedPosition);
                            cartListArray.add(cartListObj);
                            if (listener != null) {
                                listener.onAddProduct(String.valueOf(cartCount), String.valueOf(cartAmount));
                            }
                            EventBus.getDefault().post(new SideMenuFragment.MessageEvent("refreshCartData"));
                        } else {
                            Progress.dismiss((Activity) context);
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
                Progress.dismiss((Activity) context);
            }
        });
    }

    public boolean getPinStatus(Context mContext) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationTracker.getInstance().fillContext(mContext);
            LocationTracker.getInstance().startLocation();
            if (!LocationDetailsHelper.getInstance(mContext).isLocationEnabled()) {
                LocationDetailsHelper.getInstance(mContext).showAlert(mContext);
                return false;
            } else {
                LocationDetailsHelper.getInstance(mContext).getFusedLocationDetails();
                return true;
            }
        } else {
            LocationDetailsHelper.getInstance(mContext).requestLocationPermissions(mContext);
            return false;
        }
    }

    /////////////////////////////////// Update the cart//////////////////////////////
    public void updateCart(final Context mContext, final String mrpPrice, final String vmartPrice, final String quantity, final String netWeight, final String mobileNumber, final String productId, final String cartID, final String productName, final String timeStamp, final int cartCount, final int cartAmount, final String url,String merchant) {
        if (!getPinStatus(mContext)) return;
        if (pinCodeData == null) {
            if (!getPinStatus(mContext)) return;
            return;
        } else if (!pinCodeData.isEmpty() && !checkIsPincodeMatched(pinCodeData)) {//560061
            displayInvalidPincodeAlert(mContext);
            return;
        }
        Log.e("updateCartDetails", " " + mrpPrice + " " + vmartPrice + " " + quantity + " " + netWeight + " " + mobileNumber + " " + productId);
        JsonObject updateCart = new JsonObject();
        JSONObject updatecartObj = new JSONObject();
        try {
            updatecartObj.put("mrpPrice", mrpPrice);
            updatecartObj.put("vmartPrice", vmartPrice);
            updatecartObj.put("quantity", quantity);
            updatecartObj.put("netWeight", netWeight);
            updatecartObj.put("mobileNumber", mobileNumber);
            updatecartObj.put("productId", productId);
            updatecartObj.put("merchantId",merchant);
            JsonParser jsonParser = new JsonParser();
            updateCart = (JsonObject) jsonParser.parse(updatecartObj.toString());
            //Add to cart parameter
            Log.e("addcartJson:", " " + updateCart);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> updatecartCall = serverApiInterface.updateCart(updateCart);
        updatecartCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("updateTocartSatus", " " + response.isSuccessful());
                String statuscode, message;
                JSONArray updatecartArray;
                JSONObject updateCartJsonObj;
                if (response.body() != null) {
                    String updateCartResponse = null;
                    try {
                        updateCartResponse = new String(response.body().bytes());
                        Log.e("updatecartResponse", " " + updateCartResponse);
                        updatecartArray = new JSONArray(updateCartResponse);
                        updateCartJsonObj = updatecartArray.getJSONObject(0);
                        statuscode = updateCartJsonObj.getString("response");
                        message = updateCartJsonObj.getString("message");
                        if (statuscode.equals("3")) {
                            CartList cartList = new CartList();
                            cartList.setCartId(cartID);
                            cartList.setProductId(productId);
                            cartList.setMobileNumber(mobileNumber);
                            cartList.setItemName(productName);
                            cartList.setNetWeight(netWeight);
                            cartList.setMrpPrice(mrpPrice);
                            cartList.setVmartPrice(vmartPrice);
                            cartList.setQuantity(quantity);
                            cartList.setMobileNumber(mobileNumber);
                            cartList.setTimeStamp(timeStamp);
                            cartList.setUrl(url);
                            for (int i = 0; i < cartListArray.size(); i++) {
                                if (cartListArray.get(i).getProductId().equals(productId) && cartListArray.get(i).getNetWeight().equals(netWeight)) {
                                    CartList cartListObj = cartListArray.get(i);
                                    int index = cartListArray.indexOf(cartListObj);
                                    cartListArray.set(index, cartList);
                                }
                            }
                            Log.e("afterUpdateCartSize", " " + cartListArray.size());
                            for (int i = 0; i < cartListArray.size(); i++) {
                                Log.e("uuuuuuuuuu", " " + cartListArray.get(i).getProductId() + " " + cartListArray.get(i).getNetWeight() + " " + cartListArray.get(i).getQuantity());
                            }
                            if (listener != null) {
                                listener.onAddProduct(String.valueOf(cartCount), String.valueOf(cartAmount));
                            }
                            EventBus.getDefault().post(new SideMenuFragment.MessageEvent("refreshCartData"));
                        } else if (statuscode.equals("0")) {
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
                Progress.dismiss((Activity) context);
            }
        });
    }
    //////////////////////deleteCar//////////////
    public void deleteCart(final Context mContext, final String mrpPrice, final String vmartPrice, final String quantity,
                           final String netWeight, final String mobileNumber, final String productId, final String cartID,
                           final String productName, final String timeStamp, final int cartCount, final int cartAmount,
                           final String url, final int childeSelectePos, final Product productObj,String merchant) {
        if (!getPinStatus(mContext)) return;
        if (pinCodeData == null) {
            if (!getPinStatus(mContext)) return;
            return;
        } else if (!pinCodeData.isEmpty() && !checkIsPincodeMatched(pinCodeData)) {
            displayInvalidPincodeAlert(mContext);
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
                String statuscode, message;
                JSONArray deletecartArray;
                JSONObject deleteCartJsonObj;
                if (response.body() != null) {
                    String deleteCartResponse = null;
                    try {
                        deleteCartResponse = new String(response.body().bytes());
                        deletecartArray = new JSONArray(deleteCartResponse);
                        deleteCartJsonObj = deletecartArray.getJSONObject(0);
                        statuscode = deleteCartJsonObj.getString("response");
                        message = deleteCartJsonObj.getString("message");
                        if (statuscode.equals("3")) {
                            ProductInfo productInfo = new ProductInfo();
                            productInfo.setSelectedQnt(Integer.valueOf(quantity));//1
                            productInfo.setMrp(mrpPrice);
                            productInfo.setVmartPrice(vmartPrice);
                            productInfo.setQuantity(netWeight);
                            productInfo.setUrl(url);
                            if (HomeProductsDataController.getInstance().homeProductsArraylist.contains(productObj)) {
                                int index = HomeProductsDataController.getInstance().homeProductsArraylist.indexOf(productObj);
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
                                                productObj.setPinfo(addTocartProductArrayList);
                                            }
                                        }
                                    }
                                }
                                HomeProductsDataController.getInstance().homeProductsArraylist.set(index, productObj);
                            }

                            if (SubProductsDataController.getInstance().productArrayList.contains(productObj)) {
                                int index = SubProductsDataController.getInstance().productArrayList.indexOf(productObj);
                                int chideIndex;
                                for (int i = 0; i < SubProductsDataController.getInstance().productArrayList.size(); i++) {
                                    String pid = SubProductsDataController.getInstance().productArrayList.get(i).getProductId();
                                    ArrayList<ProductInfo> addTocartProductArrayList = SubProductsDataController.getInstance().productArrayList.get(i).getPinfo();
                                    for (int j = 0; j < addTocartProductArrayList.size(); j++) {
                                        if (pid.equals(productId) && netWeight.equals(addTocartProductArrayList.get(j).getQuantity())) {
                                            ProductInfo producinfoObj = addTocartProductArrayList.get(j);
                                            if (addTocartProductArrayList.contains(producinfoObj)) {
                                                chideIndex = addTocartProductArrayList.indexOf(producinfoObj);
                                                addTocartProductArrayList.set(chideIndex, productInfo);
                                                productObj.setPinfo(addTocartProductArrayList);
                                            }
                                        }
                                    }
                                }
                                SubProductsDataController.getInstance().productArrayList.set(index, productObj);
                            }
                            Log.e("beforedelete", " " + cartListArray.size());
                            for (int i = 0; i < cartListArray.size(); i++) {
                                if (cartListArray.get(i).getProductId().equals(productId) && cartListArray.get(i).getNetWeight().equals(netWeight)) {
                                    CartList cartListObj = cartListArray.get(i);
                                    if (cartListArray.contains(cartListObj)) {
                                        int index = cartListArray.indexOf(cartListObj);
                                        cartListArray.remove(cartListObj);
                                        Log.e("afterDeleteCartSize", " " + cartListArray.size());
                                    }
                                }
                            }

                            if (listener != null) {
                                listener.onAddProduct(String.valueOf(cartCount), String.valueOf(cartAmount));
                            }
                            EventBus.getDefault().post(new SideMenuFragment.MessageEvent("refreshCartData"));
                        } else if (statuscode.equals("0")) {
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
                Progress.dismiss((Activity) context);
            }
        });
    }
    ///////////////Fetching the cart Data//////////////////
    public void FetchCartData(final Context context, String mobilenumber,String merchant) {
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        io.reactivex.Observable<Response<ResponseBody>> observable = serverApiInterface.fetchCartList(mobilenumber,merchant)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("subscribe", " " + "fetchCart");
            }

            @Override
            public void onNext(Response<ResponseBody> response) {
                allCartLists.clear();
                cartListArray.clear();
                JSONArray cartdataArray = null;
                JSONObject cartJsonObj = null;
                JSONArray cartList = null;
                AllCartList allCartList = new AllCartList();
                String statuscode, message;
                Log.e("responseBody", " " + response.body());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("FetchCartData", " " + bodyString);
                        cartdataArray = new JSONArray(bodyString);
                        cartJsonObj = cartdataArray.getJSONObject(0);
                        statuscode = cartJsonObj.getString("response");
                        message = cartJsonObj.getString("message");
                        Log.e("cartJsonData", " " + cartJsonObj.length() + " " + cartJsonObj.toString());
                        Log.e("fetchcartrespose", " " + statuscode + " " + message);
                        allCartList.setResponse(statuscode);
                        allCartList.setMessage(message);
                        if (statuscode.equals("3")) {
                            cartList = cartJsonObj.getJSONArray("cartList");
                            Log.e("cartlistjsonArray", " " + cartList.length() + " " + cartList);
                            for (int i = 0; i < cartList.length(); i++) {
                                JSONObject cartObj = cartList.getJSONObject(i);
                                CartList cartlistbj = new CartList();
                                cartlistbj.setCartId(cartObj.getString("cartId"));
                                cartlistbj.setProductId(cartObj.getString("productId"));
                                cartlistbj.setMobileNumber(cartObj.getString("mobileNumber"));
                                cartlistbj.setItemName(cartObj.getString("itemName"));
                                cartlistbj.setMrpPrice(cartObj.getString("mrpPrice"));
                                cartlistbj.setVmartPrice(cartObj.getString("vmartPrice"));
                                cartlistbj.setQuantity(cartObj.getString("quantity"));
                                cartlistbj.setNetWeight(cartObj.getString("netWeight"));
                                cartlistbj.setTimeStamp(cartObj.getString("timeStamp"));
                                cartlistbj.setUrl(cartObj.getString("url"));
                                cartListArray.add(cartlistbj);
                            }
                            allCartList.setCartList(cartListArray);
                            for (int i = 0; i < cartListArray.size(); i++) {
                                Log.e("cartname", " " + cartListArray.get(i).getItemName() + cartListArray.get(i).getNetWeight() + " " + cartListArray.get(i).getProductId());
                            }
                            EventBus.getDefault().post(new SideMenuFragment.MessageEvent("refreshCartData"));
                        } else {
                            //  EventBus.getDefault().post(new AddressActivity.MessageEvent("error"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() { }
        });
    }
    /////display  if no user exist
    public void displayInvalidPincodeAlert(Context mContext) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        // set dialog message
        alertDialogBuilder
                .setTitle("Information")
                .setMessage("Currently no services provided in your location")
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                        EventBus.getDefault().post(new SideMenuFragment.MessageEvent("error"));
                    }
                });
        alertDialogBuilder.show();
    }

    private boolean checkIsPincodeMatched(String currentPincode) {
        for (int i = 0; i < pincodesArray.length; i++) {
            if (pincodesArray[i].equals(currentPincode)) {
                Log.e("pinpresentinarray", " " + pincodesArray[i]);
                return true;
            }
        }
        pincodesArray = add(pincodesArray, currentPincode);
        return true;
    }
    public boolean isConn(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }
}