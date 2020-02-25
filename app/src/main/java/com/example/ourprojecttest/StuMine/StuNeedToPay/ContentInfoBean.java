package com.example.ourprojecttest.StuMine.StuNeedToPay;

import android.graphics.Bitmap;

public class ContentInfoBean {
    String drugName;
    Bitmap drugPicture;
    String drugAmount;
    String drugUnite;

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public Bitmap getDrugPicture() {
        return drugPicture;
    }

    public void setDrugPicture(Bitmap drugPicture) {
        this.drugPicture = drugPicture;
    }

    public String getDrugAmount() {
        return drugAmount;
    }

    public void setDrugAmount(String drugAmount) {
        this.drugAmount = drugAmount;
    }

    public String getDrugUnite() {
        return drugUnite;
    }

    public void setDrugUnite(String drugUnite) {
        this.drugUnite = drugUnite;
    }
}
