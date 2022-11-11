package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemsInfo implements Serializable {
    @SerializedName("productId")
    private String productId;
    @SerializedName("itemName")
    private String itemName;
    @SerializedName("quantity")
    private String quantity;
    @SerializedName("price")
    private String price;
    @SerializedName("netWeight")
    private String netWeight;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("youSave")
    private String youSave;
/*
    @SerializedName("mrp")
    private String mrp;*/

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getYouSave() {
        return youSave;
    }

    public void setYouSave(String youSave) {
        this.youSave = youSave;
    }

  /*  public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }*/
}
