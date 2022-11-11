package vmart.example.mypc.vedasmart.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vmart.example.mypc.vedasmart.activities.AddressActivity;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.model.AddressList;
import vmart.example.mypc.vedasmart.model.AllAddressList;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AddressController {
    public static AddressController addressObj;
    public ServerApiInterface serverApiInterface;
    public ArrayList<AddressList> addressLists;
    Context context;
    public AddressList selectedAddressList = null;

    public static AddressController getInstance() {
        if (addressObj == null) {
            addressObj = new AddressController();
        }
        return addressObj;
    }

    public void fillContext(Context context) {
        this.context = context;
        addressLists = new ArrayList<>();
    }
    ////Adding a new Address
    public void addnewAddress(final Context context, final String pinCode, final String address, final String name, final String state, final String mobile, final String city,
                              final String area, final String landmark, final String userId, final boolean favourite, final double latitude, final double longitude,final String time,String merchant) {
        Log.e("addaddress", " " + pinCode + " " + address + " " + name + " " + state + " " + mobile + " " + city + " " + area + " " + landmark + " " + userId + " " + favourite + " "+time);
        JsonObject addAddressObj = new JsonObject();
        JSONObject addObj = new JSONObject();
        try {
            addObj.put("pinCode", pinCode);
            addObj.put("address", address);
            addObj.put("name", name);
            addObj.put("state", state);
            addObj.put("mobile", mobile);
            addObj.put("city", city);
            addObj.put("area", area);
            addObj.put("landmark", landmark);
            addObj.put("userId", userId);
            addObj.put("favourite", favourite);
            addObj.put("latitude", latitude);
            addObj.put("longitude", longitude);
            addObj.put("timeStamp",time);
            addObj.put("merchantId",merchant);
            JsonParser jsonParser = new JsonParser();
            addAddressObj = (JsonObject) jsonParser.parse(addObj.toString());
            Log.e("addnewAddressObj", " " + addAddressObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> addAddresscall = serverApiInterface.addNewAddress(addAddressObj);
        addAddresscall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("addnewAddressResponse", " " + response.isSuccessful() + " " + response.body());
                JSONArray addaddreessJsonArry = null;
                JSONObject addAddressJsobObj = null;
                String message, statuscode, addressID;

                if (response.body() != null) {
                    String addnewAddressresponse = null;
                    try {
                        addnewAddressresponse = new String(response.body().bytes());
                        addaddreessJsonArry = new JSONArray(addnewAddressresponse);
                        addAddressJsobObj = addaddreessJsonArry.getJSONObject(0);
                        Log.e("address", " " + addAddressJsobObj);
                        statuscode = addAddressJsobObj.getString("response");
                        message = addAddressJsobObj.getString("message");
                        addressID = addAddressJsobObj.getString("addressId");
                        if (statuscode.equals("3")) {

                            if (favourite) {
                                for (int i = 0; i < addressLists.size(); i++) {
                                    AddressList addressList1 = addressLists.get(i);
                                    addressList1.setFavourite(false);
                                    addressLists.set(i, addressList1);
                                }
                            }
                            AddressList addressListObj = new AddressList();
                            addressListObj.setAddressId(addressID);
                            addressListObj.setUserId(userId);
                            addressListObj.setName(name);
                            addressListObj.setPinCode(pinCode);
                            addressListObj.setMobile(mobile);
                            addressListObj.setLandmark(landmark);
                            addressListObj.setFavourite(favourite);
                            addressListObj.setCity(city);
                            addressListObj.setArea(area);
                            addressListObj.setAddress(address);
                            addressListObj.setState(state);
                            addressListObj.setLatitude(latitude);
                            addressListObj.setLongitude(longitude);
                            addressListObj.setTimeStamp(time);
                            addressLists.add(addressListObj);
                            Collections.sort(addressLists, new Comparator<AddressList>() {
                                @Override
                                public int compare(AddressList o1, AddressList o2) {
                                    return o2.getTimeStamp().compareTo(o1.getTimeStamp());
                                }
                            });
                            Progress.dismiss((Activity) context);
                            Intent intent = new Intent(context, AddressActivity.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        } else {
                            Progress.dismiss((Activity) context);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //////////Fetching the AddressList from server
    public void FetchAllAddress(final Context context, String mobile,String merchant) {
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Observable<Response<ResponseBody>> observable = serverApiInterface.fetchAddress(mobile,merchant).
                subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(Response<ResponseBody> response) {
                JSONArray addressJsonArray = null;
                JSONObject addressJsonObj = null;
                JSONArray addressListJsonArray = null;
                JSONObject addressObj = null;
                String statuscode, message;
                addressLists = new ArrayList<>();
                AllAddressList allAddressList = new AllAddressList();
                if (response.body() != null) {
                    String bodyString = null;
                    try {
                        bodyString = new String(response.body().bytes());
                        addressJsonArray = new JSONArray(bodyString);
                        addressJsonObj = addressJsonArray.getJSONObject(0);
                        statuscode = addressJsonObj.getString("response");
                        message = addressJsonObj.getString("message");
                        Log.e("fetchAddressResponse", " " + statuscode + " " + message);
                        if (statuscode.equals("3")) {
                            allAddressList.setResponse(statuscode);
                            allAddressList.setMessage(message);
                            addressListJsonArray = addressJsonObj.getJSONArray("addressList");
                            Log.e("addressJsonLenght", " " + addressListJsonArray.length());

                            for (int i = 0; i < addressListJsonArray.length(); i++) {
                                addressObj = addressListJsonArray.getJSONObject(i);
                                AddressList address = new AddressList();
                                address.setAddressId(addressObj.getString("addressId"));
                                address.setPinCode(addressObj.getString("pinCode"));
                                address.setFavourite(addressObj.getBoolean("favourite"));
                                address.setUserId(addressObj.getString("userId"));
                                address.setAddress(addressObj.getString("address"));
                                address.setName(addressObj.getString("name"));
                                address.setState(addressObj.getString("state"));
                                address.setMobile(addressObj.getString("mobile"));
                                address.setCity(addressObj.getString("city"));
                                address.setLandmark(addressObj.getString("landmark"));
                                address.setArea(addressObj.getString("area"));
                                address.setLatitude(addressObj.getDouble("latitude"));
                                address.setLongitude(addressObj.getDouble("longitude"));
                                address.setTimeStamp(addressObj.getString("timeStamp"));
                                addressLists.add(address);
                            }
                            Collections.sort(addressLists, new Comparator<AddressList>() {
                                @Override
                                public int compare(AddressList o1, AddressList o2) {
                                    return o2.getTimeStamp().compareTo(o1.getTimeStamp());
                                }
                            });
                            allAddressList.setAddressLists(addressLists);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("FetchCartData", " " + bodyString);
                }
            }

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() { }
        });
    }
    ////////////Edit the Address
    public void editAddress(final Context context, final String addressId, final String pinCode, final String address, final String name, final String state, final String mobile, final String city, final String area, final String landmark, final String userId, final boolean favoriteCheckbox, final double latitude, final double longitude , final String time, String merchant) {
        Log.e("editAddressDetails", " " + addressId + "" + pinCode + "" + address + " " + name + " " + state + " " + mobile + " " + city + " " + area + " " + landmark + favoriteCheckbox);
        JsonObject editAddress = new JsonObject();
        JSONObject editAddressObj = new JSONObject();
        try {
            editAddressObj.put("addressId", addressId);
            editAddressObj.put("pinCode", pinCode);
            editAddressObj.put("address", address);
            editAddressObj.put("name", name);
            editAddressObj.put("state", state);
            editAddressObj.put("mobile", mobile);
            editAddressObj.put("city", city);
            editAddressObj.put("area", area);
            editAddressObj.put("landmark", landmark);
            editAddressObj.put("userId", userId);
            editAddressObj.put("favourite", favoriteCheckbox);
            editAddressObj.put("latitude", latitude);
            editAddressObj.put("longitude", longitude);
            editAddressObj.put("timeStamp",time);
            editAddressObj.put("merchantId",merchant);
            JsonParser jsonParser = new JsonParser();
            editAddress = (JsonObject) jsonParser.parse(editAddressObj.toString());
            Log.e("editAddress", " " + editAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> editAddressCall = serverApiInterface.editAddress(editAddress);
        editAddressCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("EditResponse", " " + response.isSuccessful());
                JSONArray editaddreessJsonArry = null;
                JSONObject editAddressJsobObj = null;
                String message, statuscode;

                if (response.body() != null) {
                    String editAddressresponse = null;
                    try {
                        editAddressresponse = new String(response.body().bytes());
                        editaddreessJsonArry = new JSONArray(editAddressresponse);
                        editAddressJsobObj = editaddreessJsonArry.getJSONObject(0);
                        statuscode = editAddressJsobObj.getString("response");
                        message = editAddressJsobObj.getString("message");
                        if (statuscode.equals("3")) {
                            AddressList editaddressListObj = new AddressList();
                            editaddressListObj.setAddressId(addressId);
                            editaddressListObj.setUserId(userId);
                            editaddressListObj.setName(name);
                            editaddressListObj.setPinCode(pinCode);
                            editaddressListObj.setMobile(mobile);
                            editaddressListObj.setLandmark(landmark);
                            editaddressListObj.setFavourite(favoriteCheckbox);
                            editaddressListObj.setCity(city);
                            editaddressListObj.setArea(area);
                            editaddressListObj.setAddress(address);
                            editaddressListObj.setState(state);
                            editaddressListObj.setLatitude(latitude);
                            editaddressListObj.setLongitude(longitude);
                            editaddressListObj.setTimeStamp(time);

                            if (favoriteCheckbox) {
                                for (int i = 0; i < addressLists.size(); i++) {
                                    AddressList addressList1 = addressLists.get(i);
                                    addressList1.setFavourite(false);
                                    addressLists.set(i, addressList1);
                                }
                            }
                            if (addressLists.contains(AddressController.getInstance().selectedAddressList)) {
                                int index = addressLists.indexOf(AddressController.getInstance().selectedAddressList);
                                addressLists.set(index, editaddressListObj);
                            }
                            Collections.sort(addressLists, new Comparator<AddressList>() {
                                @Override
                                public int compare(AddressList o1, AddressList o2) {
                                    return o2.getTimeStamp().compareTo(o1.getTimeStamp());
                                }
                            });
                            Intent intent = new Intent(context, AddressActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                            context.getApplicationContext().startActivity(intent);
                            Progress.dismiss((Activity) context);
                        } else {
                            Progress.dismiss((Activity) context);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
}