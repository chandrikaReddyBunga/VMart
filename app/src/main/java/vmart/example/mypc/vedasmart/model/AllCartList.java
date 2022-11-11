package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllCartList
{
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

    @SerializedName("cartList")
    @Expose
    private ArrayList<CartList> cartList;

    public ArrayList<CartList> getCartList() { return this.cartList; }

    public void setCartList(ArrayList<CartList> cartList) { this.cartList = cartList; }
}
