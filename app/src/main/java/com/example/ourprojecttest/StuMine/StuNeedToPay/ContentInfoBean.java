package com.example.ourprojecttest.StuMine.StuNeedToPay;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ContentInfoBean {
    String drugName;
    Drawable drugPicture;
    String drugAmount;
    String drugUnite;

    public Drawable getDrugPicture() {
        return drugPicture;
    }

    public void setDrugPicture(Drawable drugPicture) {
        this.drugPicture = drugPicture;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
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
