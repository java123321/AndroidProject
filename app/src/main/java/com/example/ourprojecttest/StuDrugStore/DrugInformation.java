package com.example.ourprojecttest.StuDrugStore;

import android.graphics.drawable.Drawable;
public class DrugInformation {
    private String id;
    private String Drug_Describe;
    private String Drug_Name;
    private String Drug_Price;
    private String Drug_Amount;
    private Drawable Drug_Picture;
    private String  Drug_Type;
    private String Drug_OTC;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setDrug_OTC(String drug_OTC) {
        Drug_OTC = drug_OTC;
    }
    public String getDrug_OTC() {
        return Drug_OTC;
    }
    public void setDrug_Type(String drug_Type) {
        Drug_Type = drug_Type;
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
