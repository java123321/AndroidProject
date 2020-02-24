package com.example.ourprojecttest;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.io.Serializable;

public class Drug_Information{
    String id;
    String Drug_Describe;
    String Drug_Name;
    String Drug_Price;
    String Drug_Amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    Drawable Drug_Picture;
    String  Drug_Type;
    String Drug_OTC;

    public void setDrug_OTC(String drug_OTC) {
        Drug_OTC = drug_OTC;
    }
    public String getDrug_OTC() {
        return Drug_OTC;
    }

    public void setDrug_Type(String drug_Type) {
        Drug_Type = drug_Type;
    }

    public String getDrug_Type() {
        return Drug_Type;
    }

    public void setDrug_Picture(Drawable drug_Picture) {
        Drug_Picture = drug_Picture;
    }

    public Drawable getDrug_Picture() {
        return Drug_Picture;
    }

    public void setDrug_Price(String drug_Price) {
        Drug_Price = drug_Price;
    }

    public String getDrug_Price() {
        return Drug_Price;
    }


    public void setDrug_Name(String drug_Name) {
        Drug_Name = drug_Name;
    }

    public String getDrug_Name() {
        return Drug_Name;
    }


    public void setDrug_Describe(String drug_Describe) {
        Drug_Describe = drug_Describe;
    }

    public String getDrug_Describe() {
        return Drug_Describe;
    }


    public void setDrug_Amount(String drug_Amount) {
        Drug_Amount = drug_Amount;
    }

    public String getDrug_Amount() {
        return Drug_Amount;
    }

}
