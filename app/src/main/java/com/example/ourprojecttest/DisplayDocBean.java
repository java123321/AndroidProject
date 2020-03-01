package com.example.ourprojecttest;

import android.graphics.Bitmap;

public class DisplayDocBean {

    Bitmap icon;
    String name;
    String brief;
    String sex;
    Bitmap license;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Bitmap getLicense() {
        return license;
    }

    public void setLicense(Bitmap license) {
        this.license = license;
    }

    public Bitmap getIcon() {
        return icon;
    }
    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
