package vmart.example.mypc.vedasmart.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vmart.example.mypc.vedasmart.model.PickupLists;
import vmart.example.mypc.vedasmart.model.PincodeList;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import vmart.example.mypc.vedasmart.sessions.SessionManager;
import static android.content.Context.MODE_PRIVATE;

public class PickupController {
    public static PickupController pickupObj;
    ServerApiInterface serverApiInterface;
    public ArrayList<PickupLists> pickuplistsArrayList;
    public ArrayList<PincodeList> pincodelistArrayList;
    Context context;

    public static PickupController getInstance() {
        if (pickupObj == null) {
            pickupObj = new PickupController();
            pickupObj.pickuplistsArrayList = new ArrayList<>();
            pickupObj.pincodelistArrayList = new ArrayList<>();
        }
        return pickupObj;
    }
    public void fillContext(Context context) {
        this.context = context;
    }

    public void GetPickupLocations(final Context context) {
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Observable<Response<ResponseBody>> observable = serverApiInterface.getVmartPickUpLocations().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(Response<ResponseBody> response) {
                JSONArray pickupJsonarray = null;
                JSONObject pickupJsonObj = null;
                JSONArray pickuplistArray = null;
                JSONObject pickupObj = null;
                String statuscode, message;
                if (response.body() != null) {
                    String bodyString = null;
                    try {
                        bodyString = new String(response.body().bytes());
                        pickupJsonarray = new JSONArray(bodyString);
                        pickupJsonObj = pickupJsonarray.getJSONObject(0);
                        statuscode = pickupJsonObj.getString("response");
                        message = pickupJsonObj.getString("message");
                        if (statuscode.equals("3")) {
                            pickuplistArray = pickupJsonObj.getJSONArray("addressList");
                            Log.e("picklistArrayListsize", " " + pickuplistArray);
                            pickuplistsArrayList = new ArrayList<>();
                            for (int i = 0; i < pickuplistArray.length(); i++) {
                                pickupObj = pickuplistArray.getJSONObject(i);
                                Log.e("pickupObj", " " + pickupObj);
                                PickupLists pickup = new PickupLists();
                                pickup.setAddressId(pickupObj.getString("addressId"));
                                pickup.setAddress(pickupObj.getString("address"));
                                pickup.setLandmark(pickupObj.getString("landmark"));
                                pickup.setArea(pickupObj.getString("area"));
                                pickup.setCity(pickupObj.getString("city"));
                                pickup.setPincode(pickupObj.getString("pincode"));
                                pickup.setLatitude(pickupObj.getString("latitude"));
                                pickup.setLangitude(pickupObj.getString("langitude"));
                                pickup.setFavourite(false);
                                pickuplistsArrayList.add(pickup);
                            }
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
            public void onComplete() { fetchFavourites(context); }
        });
    }

    public void getpincodes(final Context context) {
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Observable<Response<ResponseBody>> observable = serverApiInterface.getVmartaddresspincodes().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(Response<ResponseBody> response) {
                JSONArray pincodeJsonarray = null;
                JSONObject pincodeJsonObj = null;
                JSONArray pincodelistArray = null;
                JSONObject pinObj = null;
                String statuscode1, message1;
                if (response.body() != null) {
                    String bodyString = null;
                    try {
                        bodyString = new String(response.body().bytes());
                        pincodeJsonarray = new JSONArray(bodyString);
                        pincodeJsonObj = pincodeJsonarray.getJSONObject(0);
                        statuscode1 = pincodeJsonObj.getString("response");
                        message1 = pincodeJsonObj.getString("message");
                        if (statuscode1.equals("3")) {
                            pincodelistArray = pincodeJsonObj.getJSONArray("deliveryPinsList");
                            Log.e("pincodeArrayListsize", " " + pincodelistArray);
                            pincodelistArrayList = new ArrayList<>();
                            for (int i = 0; i < pincodelistArray.length(); i++) {
                                pinObj = pincodelistArray.getJSONObject(i);
                                Log.e("pickupObj", " " + pinObj);
                                PincodeList pincode = new PincodeList();
                                pincode.setAddressId(pinObj.getString("addressId"));
                                pincode.setArea(pinObj.getString("area"));
                                pincode.setCity(pinObj.getString("city"));
                                pincode.setPincode(pinObj.getString("pincode"));
                                pincode.setLatitude(pinObj.getString("latitude"));
                                pincode.setLangitude(pinObj.getString("langitude"));
                                pincodelistArrayList.add(pincode);
                                Log.e("pickupLocationSize", " " + pincodelistArrayList.size());
                            }
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
            public void onComplete() { fetchFavourites(context); }
        });
    }

    public void fetchFavourites(final Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("vmart", MODE_PRIVATE);
        String mobileNumber = sharedPreferences.getString(SessionManager.KEY_MOBILE, "");
        String merchant = sharedPreferences.getString("merchantId", "");
        Log.e("mob", "" + mobileNumber);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ServerApiInterface.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        ServerApiInterface api = retrofit.create(ServerApiInterface.class);
        Call<ResponseBody> callable = api.getFavourite(mobileNumber,merchant);
        callable.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String statuscode, message;
                JSONArray contactJsonArray;
                JSONObject contactJsonObj, favouriteObj;
                if (response.body() != null) {
                    String bodyString = null;
                    try {
                        bodyString = new String(response.body().bytes());
                        Log.e("fetResponse", " " + bodyString);
                        contactJsonArray = new JSONArray(bodyString);
                        contactJsonObj = contactJsonArray.getJSONObject(0);
                        statuscode = contactJsonObj.getString("response");
                        message = contactJsonObj.getString("message");
                        if (statuscode.equals("3")) {
                            favouriteObj = contactJsonObj.getJSONObject("favouriteList");
                            Log.e("favaddress", "" + favouriteObj);
                            String addressid = favouriteObj.getString("addressId");
                            Log.e("addId", "" + addressid);
                            Log.e("siz", "" + pickuplistsArrayList.size());
                            for (int i = 0; i < pickuplistsArrayList.size(); i++) {
                                PickupLists objPickup = pickuplistsArrayList.get(i);
                                if (objPickup.getAddressId().equals(addressid)) {
                                    objPickup.setFavourite(true);
                                    pickuplistsArrayList.set(i, objPickup);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });
    }
}