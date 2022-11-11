package vmart.example.mypc.vedasmart.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
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
import retrofit2.Response;
import vmart.example.mypc.vedasmart.activities.SubcategoriesActivity;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.model.Product;
import vmart.example.mypc.vedasmart.model.ProductInfo;
import vmart.example.mypc.vedasmart.model.SubItems;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SubProductsDataController {
    public static SubProductsDataController subProductsObj;
    public ArrayList<SubItems> subItemsArrayList;
    public ArrayList<Product> productArrayList;
    ServerApiInterface serverApiInterface;
    SubItems subItems;
    private Context context;

    public static SubProductsDataController getInstance() {
        if (subProductsObj == null) {
            subProductsObj = new SubProductsDataController();
            subProductsObj.subItemsArrayList = new ArrayList<>();
            subProductsObj.subItems = new SubItems();
            subProductsObj.productArrayList = new ArrayList<>();
        }
        return subProductsObj;
    }
    public void fillcontext(Context context) { this.context = context; }

    public void fetchSubItemsData(String id, final Context context, final String selectedCategory,String merchant) {
        Progress.show((Activity)context);
        productArrayList = new ArrayList<>();
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Observable<Response<ResponseBody>> observable = serverApiInterface.fetchSubItemData(id,merchant)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(Response<ResponseBody> response) {
                JSONArray subProducts = null;
                JSONObject subproductJsonObj;
                JSONArray productArray;
                JSONObject productJsonObj;
                Log.e("responseBody", " " + response);
                if (response.body() != null) {
                    try {
                        Progress.dismiss((Activity) context);
                        String bodyString = new String(response.body().bytes());
                        Log.e("subitems", " " + bodyString);
                        subProducts = new JSONArray(bodyString);
                        subproductJsonObj = subProducts.getJSONObject(0);
                        Log.e("subProdcutsjsonObj", " " + subproductJsonObj.length());
                        SubItems subItems = new SubItems();
                        subItems.setResponse(subproductJsonObj.getString("response"));
                        subItems.setMessage(subproductJsonObj.getString("message"));
                        productArray = subproductJsonObj.getJSONArray("products");
                        Log.e("ppppppppp", " " + productArray.length());
                        subItemsArrayList = new ArrayList<>();
                        subItemsArrayList.clear();
                        for (int i = 0; i < productArray.length(); i++) {
                            ArrayList<ProductInfo> productInfosarray = new ArrayList<>();
                            Product product = new Product();
                            productJsonObj = productArray.getJSONObject(i);
                            product.setProductId(productJsonObj.getString("productId"));
                            product.setDescription(productJsonObj.getString("description"));
                            product.setItemName(productJsonObj.getString("itemName"));
                            JSONArray pinfo = productJsonObj.getJSONArray("pinfo");
                            for (int j = 0; j < pinfo.length(); j++) {
                                JSONObject pinfoObj = pinfo.getJSONObject(j);
                                ProductInfo productInfo = new ProductInfo();
                                productInfo.setMrp(pinfoObj.getString("mrp"));
                                productInfo.setQuantity(pinfoObj.getString("quantity"));
                                productInfo.setVmartPrice(pinfoObj.getString("vmartPrice"));
                                productInfo.setUrl(pinfoObj.getString("url"));
                                productInfosarray.add(productInfo);
                            }
                            product.setPinfo(productInfosarray);
                            productArrayList.add(product);
                            subItems.setProducts(productArrayList);
                            subItemsArrayList.add(subItems);
                        }
                        if (productArrayList.size() > 0) {
                            for (int p = 0; p < productArrayList.size(); p++) {
                                Product objProduct = productArrayList.get(p);
                                objProduct.setSelectedVarientPos(0);
                                for (int i2 = 0; i2 < objProduct.getPinfo().size(); i2++) {
                                    ProductInfo objVariant = objProduct.getPinfo().get(i2);
                                    objVariant.setSelectedQnt(0);
                                    objProduct.getPinfo().set(i2, objVariant);
                                }
                                productArrayList.set(p, objProduct);
                            }
                        }
                        if (subItemsArrayList.size() > 0) {
                            for (int k = 0; k < subItemsArrayList.size(); k++) { }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onError(Throwable e) { Progress.dismiss((Activity) context); }

            @Override
            public void onComplete() {
                Progress.dismiss((Activity) context);
                Log.e("subitemsize"," "+subItemsArrayList.size());
                if(subItemsArrayList.size()==0) {
                    Toast.makeText(context,"No products avialable for this Category",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(context, SubcategoriesActivity.class);
                    intent.putExtra("grains", selectedCategory);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK );
                    context.getApplicationContext().startActivity(intent);
                }
            }
        });
    }
}