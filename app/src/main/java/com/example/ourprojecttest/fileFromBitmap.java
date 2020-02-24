package com.example.ourprojecttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class fileFromBitmap  {

    File file;
    Context context;
    Bitmap bitmap;

    public fileFromBitmap(Bitmap bitmap, Context context) {
        this.bitmap = bitmap;
        this.context= context;
    }


public File df() {

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    file = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
    try {
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(bytes.toByteArray());
        fo.flush();
        fo.close();
    } catch (IOException e) {
        e.printStackTrace();
    }

    return file;
}





}