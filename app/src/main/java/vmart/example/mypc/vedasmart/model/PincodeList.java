package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PincodeList {
    @SerializedName("addressId")
    @Expose
    private String addressId;

    @SerializedName("area")
    @Expose
    private String area;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("pincode")
    @Expose
    private String pincode;

    @SerializedName("langitude")
    @Expose
    private String langitude;

    @SerializedName("latitude")
    @Expose
    private String latitude;




    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLangitude() {
        return langitude;
    }

    public void setLangitude(String langitude) {
        this.langitude = langitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

}
