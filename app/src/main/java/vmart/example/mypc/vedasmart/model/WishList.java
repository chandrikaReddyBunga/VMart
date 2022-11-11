package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Abhilash on 6/19/2019.
 */

public class WishList implements Serializable {

    @SerializedName("merchantId")
    @Expose
    private String merchantId;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    @SerializedName("userid")
    @Expose
    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @SerializedName("productId")
    @Expose
    private String productId;

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @SerializedName("itemName")
    @Expose
    private String itemName;

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @SerializedName("status")
    @Expose
    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @SerializedName("url")
    @Expose
    private String url;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @SerializedName("timeStamp")
    @Expose
    private String timeStamp;

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @SerializedName("netWeight")
    @Expose
    private String netWeight;

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getNetWeight() {
        return this.netWeight;
    }

    @SerializedName("mrpPrice")
    @Expose
    private String mrpPrice;

    public String getMrpPrice() {
        return this.mrpPrice;
    }

    public void setMrpPrice(String mrpPrice) {
        this.mrpPrice = mrpPrice;
    }

    @SerializedName("vmartPrice")
    @Expose
    private String vmartPrice;

    public String getVmartPrice() {
        return this.vmartPrice;
    }

    public void setVmartPrice(String vmartPrice) {
        this.vmartPrice = vmartPrice;
    }

    @SerializedName("quantity")
    @Expose
    private String quantity;

    public String getQuantity() {
        return this.quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    private int selectedQnt;

    public int getSelectedQnt() {
        return selectedQnt;
    }

    public void setSelectedQnt(int selectedQnt) {
        this.selectedQnt = selectedQnt;
    }

}

