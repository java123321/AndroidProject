package com.example.ourprojecttest.StuDiagnosis;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by XQS on 2017/10/3 0003.
 */

public class Msg implements Serializable {

    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    private String content;
    private int type;
    public Msg(String content, int type)
    {
        this.content=content;
        this.type=type;
    }
    public String getContent()
    {
        return content;
    }
    public int getType()
    {
        return type;
    }
}
