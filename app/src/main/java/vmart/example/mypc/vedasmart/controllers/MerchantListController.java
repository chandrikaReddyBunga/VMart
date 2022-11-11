package vmart.example.mypc.vedasmart.controllers;

import android.content.Context;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vmart.example.mypc.vedasmart.fragments.SideMenuFragment;
import vmart.example.mypc.vedasmart.model.AllMerchantList;
import vmart.example.mypc.vedasmart.model.MerchantList;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;

public class MerchantListController {
    public static MerchantListController merchantobj;
    public ServerApiInterface serverApiInterface;
    public ArrayList<MerchantList> merchantLists;
    Context context;

    public static MerchantListController getInstance() {
        if (merchantobj == null) {
            merchantobj = new MerchantListController();
        }
        return merchantobj;
    }

    public void fillContext(Context context) {
        this.context = context;
        this.merchantLists = new ArrayList<>();
    }
    //////////Fetching the MerchantList from server
    public void FetchAllMerchant(final Context context) {
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Call<ResponseBody> call = serverApiInterface.fetchMerchant();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                AllMerchantList allMerchantList = new AllMerchantList();
                String statuscode, message;
                JSONArray merchantJsonArray;
                JSONObject merchantJsonObj;
                if (response.body() != null) {
                    String bodyString = null;
                    try {
                        bodyString = new String(response.body().bytes());
                        merchantJsonArray = new JSONArray(bodyString);
                        merchantJsonObj = merchantJsonArray.getJSONObject(0);
                        statuscode = merchantJsonObj.getString("response");
                        message = merchantJsonObj.getString("message");
                        if (statuscode.equals("3")) {
                            JSONArray merchantListJsonArray = merchantJsonObj.optJSONArray("merchantInfo");
                            for (int i = 0; i < merchantListJsonArray.length(); i++) {
                                JSONObject merchantObj = merchantListJsonArray.getJSONObject(i);
                                MerchantList merchant = new MerchantList();
                                merchant.setMerchantId(merchantObj.getString("merchantId"));
                                merchant.setName(merchantObj.getString("name"));
                                merchant.setMobile(merchantObj.getString("mobile"));
                                merchant.setEmail(merchantObj.getString("email"));
                                merchant.setAddress(merchantObj.getString("address"));
                                merchant.setTimeStamp(merchantObj.getString("timeStamp"));
                                merchant.setVerification_status(merchantObj.getString("verification_status"));
                                merchantLists.add(merchant);
                            }
                            allMerchantList.setMerchantLists(merchantLists);
                            EventBus.getDefault().post(new SideMenuFragment.MessageEvent("fetchMerchantlist"));
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
                Log.e("merchantFailure", " " + t.getMessage());
            }
        });
    }
}