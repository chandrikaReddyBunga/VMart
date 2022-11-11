package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllAddressList
{
    @SerializedName("message")
    @Expose
    String message;

    @SerializedName("response")
    @Expose
    String response;

    @SerializedName("addressList")
    @Expose
    ArrayList<AddressList> addressLists;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ArrayList<AddressList> getAddressLists() {
        return addressLists;
    }

    public void setAddressLists(ArrayList<AddressList> addressLists) {
        this.addressLists = addressLists;
    }
}
