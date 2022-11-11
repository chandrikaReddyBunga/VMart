package vmart.example.mypc.vedasmart.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Product implements Serializable {
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("itemName")
    @Expose
    private String itemName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("pinfo")
    @Expose
    private ArrayList<ProductInfo> pinfo;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<ProductInfo> getPinfo() {
        return pinfo;
    }

    public void setPinfo(ArrayList<ProductInfo> pinfo) {
        this.pinfo = pinfo;
    }

    private int selectedVarientPos;

    public int getSelectedVarientPos() {
        return selectedVarientPos;
    }

    public void setSelectedVarientPos(int selectedVarientPos) {
        this.selectedVarientPos = selectedVarientPos;
    }

    public static final Comparator<Product> BYPRICE_LOWTOHIGH = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            return Integer.valueOf(o1.getPinfo().get(0).getVmartPrice()).compareTo(Integer.valueOf(o2.getPinfo().get(0).getVmartPrice()));
        }
    };

    public static final Comparator<Product> BYPRICE_HIGHTOLOW = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            return Integer.valueOf(o2.getPinfo().get(0).getVmartPrice()).compareTo(Integer.valueOf(o1.getPinfo().get(0).getVmartPrice()));
        }
    };
}
