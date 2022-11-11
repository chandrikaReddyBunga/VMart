package vmart.example.mypc.vedasmart.controllers;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
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
import vmart.example.mypc.vedasmart.fragments.SideMenuFragment;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.interfaces.AddorRemoveCallbacks;
import vmart.example.mypc.vedasmart.model.AllWishList;
import vmart.example.mypc.vedasmart.model.Product;
import vmart.example.mypc.vedasmart.model.ProductInfo;
import vmart.example.mypc.vedasmart.model.WishList;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;

public class WishlistController {
    public static WishlistController addtoWishlistObj;
    public ArrayList<AllWishList> allWishList;
    public ArrayList<WishList> wishListArray = new ArrayList<>();
    public WishList wishListObj;
    private AddorRemoveCallbacks listener;
    Context context;
    ServerApiInterface serverApiInterface;
    public ArrayList<WishList> wishlistsArrayList;

    public static WishlistController getInstace() {
        if (addtoWishlistObj == null) {
            addtoWishlistObj = new WishlistController();
            addtoWishlistObj.wishListObj = new WishList();
            addtoWishlistObj.wishlistsArrayList = new ArrayList<>();
        }
        return addtoWishlistObj;
    }

    public void fillcontext(Context context) {
        this.context = context;
        allWishList = new ArrayList<>();
    }

    public void setListener(AddorRemoveCallbacks listener) {
        this.listener = listener;
    }
    ////////send selected products to server and add to wish////////
    public void AddToWish(final Context mContext, final String mobileNumber, final String productId, final String itemName,
                          final boolean isChecked, final String url, final String timeStamp,
                          final String mrpPrice, final String vmartPrice, final int qunatity, final String netWeight,String merchant) {
        Log.e("addToWishDetails", "" + mobileNumber + " " + productId + " " + itemName + " " + isChecked + " " + url + " " + timeStamp+" "+merchant);
        JsonObject addWishObj = new JsonObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", mobileNumber);
            jsonObject.put("productId", productId);
            jsonObject.put("itemName", itemName);
            jsonObject.put("status", isChecked);
            jsonObject.put("url", url);
            jsonObject.put("timeStamp", timeStamp);
            jsonObject.put("merchantId",merchant);
            JsonParser jsonParser = new JsonParser();
            addWishObj = (JsonObject) jsonParser.parse(jsonObject.toString());
            //Add to cart parameter
            Log.e("addwishJson:", " " + addWishObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> addtowishCall = serverApiInterface.addToWish(addWishObj); //This one only changed for sever api
        addtowishCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONArray resposneArray;
                JSONObject responseObj;
                String statusCode, message;
                Log.e("AddTowishSatus", " " + response.isSuccessful());
                if (response.body() != null) {
                    String addToWishResponse = null;
                    try {
                        addToWishResponse = new String(response.body().bytes());
                        Log.e("AddwishResponse", " " + addToWishResponse);
                        resposneArray = new JSONArray(addToWishResponse);
                        responseObj = resposneArray.getJSONObject(0);
                        statusCode = responseObj.getString("response");
                        message = responseObj.getString("message");
                        if (statusCode.equals("3")) {
                            Progress.dismiss((Activity) context);
                            wishListObj.setUserid(mobileNumber);
                            wishListObj.setProductId(productId);
                            wishListObj.setItemName(itemName);
                            wishListObj.setStatus(isChecked);
                            wishListObj.setUrl(url);
                            wishListObj.setTimeStamp(timeStamp);
                            wishListObj.setMrpPrice(mrpPrice);
                            wishListObj.setQuantity(String.valueOf(qunatity));
                            wishListObj.setNetWeight(netWeight);
                            wishListObj.setVmartPrice(vmartPrice);
                            wishListArray.add(wishListObj);
                            wishlistsArrayList.add(wishListObj);
                            EventBus.getDefault().post(new SideMenuFragment.MessageEvent("refreshWishData"));
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
            public void onFailure(Call<ResponseBody> call, Throwable t) { Progress.dismiss((Activity) context); }
        });
    }

    public boolean isConn(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }
    //////////////////////deleteWish//////////////
    public void deleteWish(final Context mContext, final String mobilenumber, final String productId,final Product product,String merchant) {
        JsonObject deleteWish = new JsonObject();
        JSONObject deletewishObj = new JSONObject();
        try {
            deletewishObj.put("userid", mobilenumber);
            deletewishObj.put("productId", productId);
            deletewishObj.put("merchantId",merchant);
            JsonParser jsonParser = new JsonParser();
            deleteWish = (JsonObject) jsonParser.parse(deletewishObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> deletecall = serverApiInterface.deleteWish(deleteWish);
        deletecall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String statuscode, message;
                JSONArray deletewishArray;
                JSONObject deleteWishJsonObj;
                if (response.body() != null) {
                    String deleteCartResponse = null;
                    try {
                        deleteCartResponse = new String(response.body().bytes());
                        deletewishArray = new JSONArray(deleteCartResponse);
                        deleteWishJsonObj = deletewishArray.getJSONObject(0);
                        statuscode = deleteWishJsonObj.getString("response");
                        message = deleteWishJsonObj.getString("message");
                        if (statuscode.equals("3")) {
                            ProductInfo productInfo = new ProductInfo();
                            for (int i = 0; i < wishListArray.size(); i++) {
                                if (wishListArray.get(i).getProductId().equals(productId)) {
                                    WishList wishListObj = wishListArray.get(i);
                                    if (wishListArray.contains(wishListObj)) {
                                        int index = wishListArray.indexOf(wishListObj);
                                        wishListArray.remove(wishListObj);
                                    }
                                }
                            }
                            EventBus.getDefault().post(new SideMenuFragment.MessageEvent("refreshWishData"));
                        } else if (statuscode.equals("0")) { }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { Progress.dismiss((Activity) context); }
        });
    }
    ///////////////Fetching the cart Data//////////////////
    public void FetchWishData(final Context context, String mobilenumber,String merchant) {
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class); // This one only server api changes
        io.reactivex.Observable<Response<ResponseBody>> observable = serverApiInterface.fetchWishList(mobilenumber,merchant) // This one also
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(Response<ResponseBody> response) {
                allWishList.clear();
                wishListArray.clear();
                JSONArray wishdataArray = null;
                JSONObject wishJsonObj = null;
                JSONArray wishList = null;
                AllWishList allWishList = new AllWishList();
                String statuscode, message;
                Log.e("responseBody", " " + response.body());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("FetchWishData", " " + bodyString);
                        wishdataArray = new JSONArray(bodyString);
                        wishJsonObj = wishdataArray.getJSONObject(0);
                        statuscode = wishJsonObj.getString("response");
                        message = wishJsonObj.getString("message");
                        allWishList.setResponse(statuscode);
                        allWishList.setMessage(message);
                        if (statuscode.equals("3")) {
                            wishList = wishJsonObj.getJSONArray("wishlistList");
                            for (int i = 0; i < wishList.length(); i++) {
                                JSONObject wishObj = wishList.getJSONObject(i);
                                WishList wishlistbj = new WishList();
                                wishlistbj.setMerchantId(wishObj.getString("merchantId"));
                                wishlistbj.setUserid(wishObj.getString("userid"));
                                wishlistbj.setItemName(wishObj.getString("itemName"));
                                wishlistbj.setTimeStamp(wishObj.getString("timeStamp"));
                                wishlistbj.setUrl(wishObj.getString("url"));
                                wishlistbj.setStatus(wishObj.getBoolean("status"));
                                wishlistbj.setProductId(wishObj.getString("productId"));
                                wishListArray.add(wishlistbj);
                                wishlistsArrayList.add(wishlistbj);
                            }
                            allWishList.setWishList(wishListArray);
                            EventBus.getDefault().post(new SideMenuFragment.MessageEvent("refreshWishData"));
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
}