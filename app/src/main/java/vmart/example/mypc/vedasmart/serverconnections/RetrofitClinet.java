package vmart.example.mypc.vedasmart.serverconnections;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClinet {
    public static Retrofit getInstance() {
        return new Retrofit.Builder().baseUrl(ServerApiInterface.BASE_URL)
                   .addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                   .build();
    }
}