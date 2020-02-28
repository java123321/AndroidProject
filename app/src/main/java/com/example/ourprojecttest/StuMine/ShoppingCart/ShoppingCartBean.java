package com.example.ourprojecttest.StuMine.ShoppingCart;
import java.io.Serializable;

public class ShoppingCartBean implements Serializable{
   // Drawable drugPicture;
    String drugName;
    String drugPrice;
    byte[] drugPicture;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String checked;
    double totalPrice;
    String id;

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
