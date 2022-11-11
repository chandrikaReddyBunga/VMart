package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeliveryInfo implements Serializable {
    @SerializedName("name")
    String name;
    @SerializedName("address")
    String address;
    @SerializedName("mobileno")
    String mobileno;
    @SerializedName("pin")
    String pin;
    @SerializedName("deliveryDate")
    String deliveryDate;
    @SerializedName("deliveryTime")
    String deliveryTime;

    public DeliveryInfo() {
    }

    public DeliveryInfo(String name, String address, String mobileno, String pin, String deliveryDate, String deliveryTime) {
        this.name = name;
        this.address = address;
        this.mobileno = mobileno;
        this.pin = pin;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
