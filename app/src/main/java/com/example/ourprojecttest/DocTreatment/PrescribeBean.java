package com.example.ourprojecttest.DocTreatment;

import android.graphics.Bitmap;

public class PrescribeBean {
    private String id;
    private String drugName;
    private String drugPrice;
    private Bitmap drugPicture;
    private int drugAmount;

    public int getDrugAmount() {
        return drugAmount;
    }

    public void setDrugAmount(int drugAmount) {
        this.drugAmount = drugAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugPrice() {
        return drugPrice;
    }

    public void setDrugPrice(String drugPrice) {
        this.drugPrice = drugPrice;
    }

    public Bitmap getDrugPicture() {
        return drugPicture;
    }

    public void setDrugPicture(Bitmap drugPicture) {
        this.drugPicture = drugPicture;
    }
}
