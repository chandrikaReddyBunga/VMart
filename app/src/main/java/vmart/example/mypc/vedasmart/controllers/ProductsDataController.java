package vmart.example.mypc.vedasmart.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import retrofit2.Response;
import vmart.example.mypc.vedasmart.adapter.CategoriesAdapter;
import vmart.example.mypc.vedasmart.helper.Progress;
import vmart.example.mypc.vedasmart.model.Categories;
import vmart.example.mypc.vedasmart.model.Products;
import vmart.example.mypc.vedasmart.model.Subcategories;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;

public class ProductsDataController {
    public static ProductsDataController productsObj;
    public ArrayList<Products> productsArrayList;
    ServerApiInterface serverApiInterface;
    Products products;
    Context context;

    public static ProductsDataController getInstance() {
        if (productsObj == null) {
            productsObj = new ProductsDataController();
            productsObj.productsArrayList = new ArrayList<>();
            productsObj.products = new Products();
        }
        return productsObj;
    }

    public void fillcontext(Context context) { this.context = context; }

    public void fetchProducts(final Context context, final RecyclerView recyclerView) {
        final ArrayList<Categories> categoriesArrayList = new ArrayList<>();
        serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Observable<Response<ResponseBody>> observable = serverApiInterface.fetchProductsData()
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onSubscribe(Disposable d) { Progress.show((Activity) context); }

            @Override
            public void onNext(Response<ResponseBody> response) {
                productsArrayList = new ArrayList<>();
                String bodyString = null;
                JSONArray jsonArray = null;
                JSONObject categoriesObj = null;
                if (response.body() != null) {
                    try {
                        bodyString = new String(response.body().bytes());
                        jsonArray = new JSONArray(bodyString);
                        categoriesObj = jsonArray.getJSONObject(0);
                        Products products = new Products();
                        products.setResponse(categoriesObj.getString("response"));
                        products.setMessage(categoriesObj.getString("message"));
                        JSONArray categoriesArray = categoriesObj.getJSONArray("categories");
                        Log.e("catjsonarray", " " + categoriesArray.getJSONObject(0));
                        for (int j = 0; j < categoriesArray.length(); j++) {
                            ArrayList<Subcategories> subcategoriesArrayList = new ArrayList<>();
                            JSONObject catObj = categoriesArray.getJSONObject(j);
                            Categories categories = new Categories();
                            String name = String.valueOf(catObj.get("categoryName"));
                            categories.setCategoryName("Grocery");
                            JSONArray subCategoriesarray = catObj.getJSONArray("subcategories");
                            for (int k = 0; k < subCategoriesarray.length(); k++) {
                                JSONObject subObj = subCategoriesarray.getJSONObject(k);
                                Subcategories subcategories = new Subcategories();
                                subcategories.setSubCategoryName("Grains");
                                subcategories.setSubCategoryId(subObj.getString("subCategoryId"));
                                subcategoriesArrayList.add(subcategories);
                                categories.setSubcategories(subcategoriesArrayList);
                            }
                            categoriesArrayList.add(categories);
                            products.setCategories(categoriesArrayList);
                            Log.e("crashhh", " " + productsArrayList.size());
                            productsArrayList.add(products);
                        }
                        for (int allcat = 0; allcat < productsArrayList.size(); allcat++) {
                            Log.e("names", " " + productsArrayList.get(allcat) + productsArrayList.size());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onError(Throwable e) {
                Progress.dismiss((Activity) context);
            }

            @Override
            public void onComplete() {
                CategoriesAdapter categoriesAdapter;
                categoriesAdapter = new CategoriesAdapter(productsArrayList, context);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(categoriesAdapter);
                Progress.dismiss((Activity) context);
            }
        });
    }
}