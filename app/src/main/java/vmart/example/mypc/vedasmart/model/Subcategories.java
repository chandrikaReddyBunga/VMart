package vmart.example.mypc.vedasmart.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Subcategories implements Serializable, Parcelable
{
    @SerializedName("subCategoryName")
    @Expose
    private String subCategoryName;
    @SerializedName("subCategoryId")
    @Expose
    private String subCategoryId;
    public final static Parcelable.Creator<Subcategories> CREATOR = new Creator<Subcategories>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Subcategories createFromParcel(Parcel in) {
            return new Subcategories(in);
        }

        public Subcategories[] newArray(int size) {
            return (new Subcategories[size]);
        }

    }
            ;
    private final static long serialVersionUID = -5655361136190031747L;

    protected Subcategories(Parcel in) {
        this.subCategoryName = ((String) in.readValue((String.class.getClassLoader())));
        this.subCategoryId = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Subcategories() {
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(subCategoryName);
        dest.writeValue(subCategoryId);
    }

    public int describeContents() {
        return 0;
    }



}