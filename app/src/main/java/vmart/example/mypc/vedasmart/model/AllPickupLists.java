package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllPickupLists
{
    @SerializedName("addressId")
    @Expose
    String addressId;

    @SerializedName("message")
    @Expose
    String message;

    @SerializedName("response")
    @Expose
    String response;

    @SerializedName("addressList")
    @Expose
    ArrayList<PickupLists> pickuplistsArrayList;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

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

    public ArrayList<PickupLists> getPickuplistsArrayList() {
        return pickuplistsArrayList;
    }

    public void setPickuplistsArrayList(ArrayList<PickupLists> pickuplistsArrayList) {
        this.pickuplistsArrayList = pickuplistsArrayList;
    }
}
