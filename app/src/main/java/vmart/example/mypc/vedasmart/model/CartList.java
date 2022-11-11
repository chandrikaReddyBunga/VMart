package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartList implements Serializable {
    @SerializedName("cartId")
    @Expose
    private String cartId;

    public String getCartId() {
        return this.cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
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

    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    @SerializedName("netWeight")
    @Expose
    private String netWeight;

    public String getNetWeight() {
        return this.netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
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

    @SerializedName("url")
    @Expose
    private String url;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private int clickedPosition;

    public int getClickedPosition() {
        return clickedPosition;
    }

    public void setClickedPosition(int clickedPosition) {
        this.clickedPosition = clickedPosition;
    }

   /*@SerializedName("subId")
    @Expose
    private String subId;

    public String getSubId() { return this.subId; }

    public void setSubId(String subId) { this.subId = subId; }*/
}
