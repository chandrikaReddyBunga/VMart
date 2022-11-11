package vmart.example.mypc.vedasmart.controllers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import vmart.example.mypc.vedasmart.fragments.SideMenuFragment;
import vmart.example.mypc.vedasmart.model.Product;
import vmart.example.mypc.vedasmart.model.SubItems;
import vmart.example.mypc.vedasmart.serverconnections.RetrofitClinet;
import vmart.example.mypc.vedasmart.serverconnections.ServerApiInterface;

public class HomeProductsDataController {
    Context context;
    public static HomeProductsDataController homeObj;
    public ArrayList<Product> homeProductsArraylist;
    String merchant;

    public static HomeProductsDataController getInstance() {
        if (homeObj == null) {
            homeObj = new HomeProductsDataController();
            homeObj.homeProductsArraylist = new ArrayList<>();
        }
        return homeObj;
    }

    public void fillcontext(Context context,String merch) {
        this.context = context;
        this.merchant = merch;
    }

    //Fetching the All Home Products
    public void FetchHomeProducts(final Context context,String merchant) {
        ServerApiInterface serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Observable<ArrayList<SubItems>> observable = serverApiInterface.fetchHomeProducts(merchant)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<ArrayList<SubItems>>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(ArrayList<SubItems> subItems) {
                if (subItems != null) {
                    if (subItems.get(0).getResponse().equals("3")) {
                        if (subItems.get(0).getProducts() != null) {
                            homeProductsArraylist = subItems.get(0).getProducts();
                            Log.e("homepro", "" + homeProductsArraylist.size());
                            Log.e("sixe", "" + subItems.get(0).getProducts().size());
                            for (int i = 0; i < homeProductsArraylist.size(); i++) {
                                String pid = subItems.get(0).getProducts().get(i).getProductId();
                                String itemame = subItems.get(0).getProducts().get(i).getItemName();
                                String Description = subItems.get(0).getProducts().get(i).getDescription();
                                Log.e("pid", "" + pid);
                                for (int i2 = 0; i2 < subItems.get(0).getProducts().get(i).getPinfo().size(); i2++) {
                                    String vprice = subItems.get(0).getProducts().get(i).getPinfo().get(i2).getVmartPrice();
                                    String url = subItems.get(0).getProducts().get(i).getPinfo().get(i2).getUrl();
                                    String mrp = subItems.get(0).getProducts().get(i).getPinfo().get(i2).getMrp();
                                    String qty = subItems.get(0).getProducts().get(i).getPinfo().get(i2).getQuantity();
                                    Log.e("vmrt", "" + vprice);
                                }
                            }
                            EventBus.getDefault().post(new SideMenuFragment.MessageEvent("fetchHomeProducts"));
                        }
                    }
                }
            }
            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() { }
        });
    }
    public void FetchHomeagainProducts(final Context context,String merchant) {
        ServerApiInterface serverApiInterface = RetrofitClinet.getInstance().create(ServerApiInterface.class);
        Observable<ArrayList<SubItems>> observable = serverApiInterface.fetchHomeProducts(merchant)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<ArrayList<SubItems>>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(ArrayList<SubItems> subItems) {
                if (subItems != null) {
                    Log.e("body", "" + subItems.get(0).getResponse());
                    if (subItems.get(0).getResponse().equals("3")) {
                        if (subItems.get(0).getProducts() != null) {
                            homeProductsArraylist = subItems.get(0).getProducts();
                            Log.e("homepro", "" + homeProductsArraylist.size());
                            Log.e("sixe", "" + subItems.get(0).getProducts().size());
                            for (int i = 0; i < homeProductsArraylist.size(); i++) {
                                String pid = subItems.get(0).getProducts().get(i).getProductId();
                                String itemame = subItems.get(0).getProducts().get(i).getItemName();
                                String Description = subItems.get(0).getProducts().get(i).getDescription();
                                Log.e("pid", "" + pid);
                                for (int i2 = 0; i2 < subItems.get(0).getProducts().get(i).getPinfo().size(); i2++) {
                                    String vprice = subItems.get(0).getProducts().get(i).getPinfo().get(i2).getVmartPrice();
                                    String url = subItems.get(0).getProducts().get(i).getPinfo().get(i2).getUrl();
                                    String mrp = subItems.get(0).getProducts().get(i).getPinfo().get(i2).getMrp();
                                    String qty = subItems.get(0).getProducts().get(i).getPinfo().get(i2).getQuantity();
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() { }
        });
    }
    public boolean isConn(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            return connectivity.getActiveNetworkInfo().isConnected();
        }
        return false;
    }
}