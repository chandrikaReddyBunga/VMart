package vmart.example.mypc.vedasmart.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Categories implements Serializable,Parcelable
{
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("subcategories")
    @Expose
    private ArrayList<Subcategories> subcategories = null;
    public final static Parcelable.Creator<Categories> CREATOR = new Parcelable.Creator<Categories>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Categories createFromParcel(Parcel in) {
            return new Categories(in);
        }

        public Categories[] newArray(int size) {
            return (new Categories[size]);
        }

    }
            ;
    private final static long serialVersionUID = -8478442598707120404L;

    protected Categories(Parcel in) {
        this.categoryName = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.subcategories, (Subcategories.class.getClassLoader()));
    }

    public Categories() {
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<Subcategories> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(ArrayList<Subcategories> subcategories) {
        this.subcategories = subcategories;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(categoryName);
        dest.writeList(subcategories);
    }

    public int describeContents() {
        return 0;
    }

}


