package vmart.example.mypc.vedasmart.model;

public class ProductOverview
{
   private String netweight;

   private String mrp;

   private  String sellingPrice;

    public ProductOverview(String netweight, String mrp, String sellingPrice) {
        this.netweight = netweight;
        this.mrp = mrp;
        this.sellingPrice = sellingPrice;
    }

    public String getNetweight() {
        return netweight;
    }

    public void setNetweight(String netweight) {
        this.netweight = netweight;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
