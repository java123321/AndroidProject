package com.example.ourprojecttest;

import java.io.Serializable;

public class Tubiao implements Serializable {
    String Name;
    int ImageId;
    String xinxi;
    public Tubiao(String name,String a,int Imageid){
        Name=name;
        ImageId=Imageid;
        xinxi=a;
    }
    public String getName(){
        return  Name;
    }
    public int getImageId(){
        return ImageId;
    }
    public String getXinxi(){return  xinxi;}

}
