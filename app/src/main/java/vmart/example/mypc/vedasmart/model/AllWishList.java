package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Abhilash on 6/19/2019.
 */

public class AllWishList {
    @SerializedName("response")
    @Expose
    private String response;

    public String getResponse() { return this.response; }

    public void setResponse(String response) { this.response = response; }

    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() { return this.message; }

    public void setMessage(String message) { this.message = message; }

    @SerializedName("wishList")
    @Expose
    private ArrayList<WishList> wishList;

    public ArrayList<WishList> getWishList() { return this.wishList; }

    public void setWishList(ArrayList<WishList> wishList) { this.wishList = wishList; }
}
