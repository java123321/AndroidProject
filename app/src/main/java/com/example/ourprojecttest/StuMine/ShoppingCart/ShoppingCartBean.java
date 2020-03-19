package com.example.ourprojecttest.StuMine.ShoppingCart;

import java.io.Serializable;

public class ShoppingCartBean implements Serializable {
    // Drawable drugPicture;
    private String drugName;
    private String drugPrice;
    private byte[] drugPicture;
    private String checked;
    private double totalPrice;
    private String id;
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


    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getChecked() {
        return checked;
    }

    public void setDrugPicture(byte[] drugPicture) {
        this.drugPicture = drugPicture;
    }

    public byte[] getDrugPicture() {
        return drugPicture;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugPrice(String drugPrice) {
        this.drugPrice = drugPrice;
    }

    public String getDrugPrice() {
        return drugPrice;
    }

//
//    public void setDrugPicture(Drawable drugPicture) {
//        this.drugPicture = drugPicture;
//    }
//
//    public Drawable getDrugPicture() {
//        return drugPicture;
//    }
}
