package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Comparator;

public class ProductInfo implements Serializable {
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("vmartPrice")
    @Expose
    private String vmartPrice;
    @SerializedName("quantity")
    @Expose
    private String quantity;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getVmartPrice() {
        return vmartPrice;
    }

    public void setVmartPrice(String vmartPrice) {
        this.vmartPrice = vmartPrice;
    }

    public String getQuantity() {
        return quantity;
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

    public static final Comparator<ProductInfo> ByPrice_LowToHigh = new Comparator<ProductInfo>() {
        @Override
        public int compare(ProductInfo o1, ProductInfo o2) {
            return Integer.valueOf(o1.mrp).compareTo(Integer.valueOf(o2.mrp));
        }
    };


}
