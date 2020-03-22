package com.example.ourprojecttest.Utils;

import java.io.Serializable;

public class PictureStore implements Serializable {
    private Boolean flag;
    private byte[] picture;

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public byte[] getPicture() {
        return picture;
    }
}
