package com.example.ourprojecttest;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.example.ourprojecttest.DocTreatment.PrescribeBean;
import com.example.ourprojecttest.StuMine.ShoppingCart.ShoppingCartBean;
import com.example.ourprojecttest.StuMine.StuNeedToPay.OrderListBean;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CommonMethod {


    /**
     *
     * @param mContext  上下文对象
     * @param serviceName  包名加类名
     * @return
     */

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }



    /**
     * 该方法用于上传文件
     * @param file 要上传的文件对象
     * @param url  需要访问的url地址
     *
     */
    public void uploadMultiFile(File file,String url) {
        try {
            String imageType = "multipart/form-data";
            //File file = new File(imgUrl);//imgUrl为图片位置
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fileName", "head_image.jpg", fileBody)
                    .addFormDataPart("imagetype", imageType)
                    .build();
            Request request = new Request.Builder().url(url).post(requestBody).build();
            final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
            OkHttpClient okHttpClient = httpBuilder
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                                                      @Override
                                                      public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                          Log.d("result", "falure");
                                                      }

                                                      @Override
                                                      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                          String htmlStr = response.body().string();
                                                          Log.i("result", htmlStr);
                                                      }
                                                  }

            );
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 该方法用来计算购物车里所有选中选项的总价格
     *
     * @param lists 数组
     * @param length     数组的长度
     * @return true 保存成功
     */
    public Double calculatePrice(ArrayList<ShoppingCartBean> lists, int length) {
        Double price = 0.0;
        for (int i = 0; i < length; i++) {
            ShoppingCartBean list = lists.get(i);
            if (list.getChecked().equals("true")) {
                Log.d("method",i+"");
                price += list.getTotalPrice();
            }
            else{
                Log.d("methodnull",i+"");
            }
        }
        return price;
    }

    /**
     *该方法用于将对象(非数组)保存到本地
     *
     * @param name 保存的文件名
     * @param obj 要保存的对象
     *
     * @return true 保存成功
     */
    public boolean saveObj2SDCard(String name,Object obj) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录
            File sdFile = new File(sdCardDir, name);
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                fos = new FileOutputStream(sdFile);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(obj); //写入
                oos.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) fos.close();
                    if (oos != null) oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     *该方法用于将对象(非数组)取出到本地
     *
     * @param  name 保存的文件名
     * @return 保存的对象
     */
    public Object readObjFromSDCard(String name) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Object obj;
            File sdCardDir = Environment.getExternalStorageDirectory(); //获取SDCard目录
            File sdFile = new File(sdCardDir, name);
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                fis = new FileInputStream(sdFile); //获得输入流
                ois = new ObjectInputStream(fis);
                obj = ois.readObject();
                return obj;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (ois != null) ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 读取sd卡购物车对象数组
     *
     * @param fileName 文件名
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ShoppingCartBean> readListFromSdCard(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  //检测sd卡是否存在
            ArrayList<ShoppingCartBean> list;
            File sdCardDir = Environment.getExternalStorageDirectory();
            File sdFile = new File(sdCardDir, fileName);
            try {
                FileInputStream fis = new FileInputStream(sdFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (ArrayList<ShoppingCartBean>) ois.readObject();
                fis.close();
                ois.close();
                return list;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
                return null;
            } catch (OptionalDataException e) {
                e.printStackTrace();
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 读取消息记录对象数组
     *
     * @param fileName 文件名
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<MessageBean> readMessageRecordListFromSdCard(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  //检测sd卡是否存在
            ArrayList<MessageBean> list;
            File sdCardDir = Environment.getExternalStorageDirectory();
            File sdFile = new File(sdCardDir, fileName);
            try {
                FileInputStream fis = new FileInputStream(sdFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (ArrayList<MessageBean>) ois.readObject();
                fis.close();
                ois.close();
                return list;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
                return null;
            } catch (OptionalDataException e) {
                e.printStackTrace();
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * 该方法用于将购物车对象数组集合写入sd卡
     *
     * @param fileName 文件名
     * @param list     集合
     * @return true 保存成功
     */
    public boolean writeListIntoSDcard(String fileName, ArrayList<ShoppingCartBean> list) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();//获取sd卡目录
            File sdFile = new File(sdCardDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(sdFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(list);//写入
                fos.close();
                oos.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * 该方法用于将消息记录集合写入sd卡
     *
     * @param fileName 文件名
     * @param list     集合
     * @return true 保存成功
     */
    public boolean writeMessageRecordListIntoSDcard(String fileName, ArrayList<MessageBean> list) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();//获取sd卡目录
            File sdFile = new File(sdCardDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(sdFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(list);//写入
                fos.close();
                oos.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * 读取消息Msg数组
     *
     * @param fileName 文件名
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Msg> readMessageContentFromSdCard(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  //检测sd卡是否存在
            ArrayList<Msg> list;
            File sdCardDir = Environment.getExternalStorageDirectory();
            File sdFile = new File(sdCardDir, fileName);
            try {
                FileInputStream fis = new FileInputStream(sdFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (ArrayList<Msg>) ois.readObject();
                fis.close();
                ois.close();
                return list;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
                return null;
            } catch (OptionalDataException e) {
                e.printStackTrace();
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * 该方法用于将将消息内容msg数据保存到本地
     *
     * @param fileName 文件名
     * @param list     集合
     * @return true 保存成功
     */
    public boolean writeMessageContentIntoSDcard(String fileName, ArrayList<Msg> list) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();//获取sd卡目录
            File sdFile = new File(sdCardDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(sdFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(list);//写入
                fos.close();
                oos.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * 保存数据到本地
     *
     * @param name 要保存数据的文件名
     *  @param data 要保存的数据内容，类型为字符串
     * @param context 上下文
     *
     */
    public void saveFileData(String name, String data,Context context) {
        BufferedWriter writer = null;
        try {
            FileOutputStream out = context.openFileOutput(name, Context.MODE_PRIVATE);//保存的文件名为“data”
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);//文件中保存此字符串
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * bitmap转化成byte数组
     * @param bm 需要转换的Bitmap
     * @return
     */
    public byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    /**
     * drawable转化成bitmap的方法
     * @param drawable 需要转换的Drawable
     */
    public  Bitmap drawableToBitamp(Drawable drawable) {
        Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        //System.out.println("Drawable转Bitmap");
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

  /**
     * 读取本地存储中的数据
     *
     * @param str 需要转换的汉字
     * @return 十六进制
     */
      public String conversion(String str) {
      char[] chars = "0123456789ABCDEF".toCharArray();
      StringBuilder sb = new StringBuilder("");
      byte[] bs = str.getBytes();
      int bit;
      for (int i = 0; i < bs.length; i++) {
          bit = (bs[i] & 0x0f0) >> 4;
          sb.append(chars[bit]);
          bit = bs[i] & 0x0f;
          sb.append(chars[bit]);
          // sb.append(' ');
      }
      return sb.toString().trim();
  }


    /**
     * 读取本地存储中的数据
     *
     * @param name 要取出的文件名
     * @param context 上下文
     * @return 本地数据
     */
    public String getFileData(String name, Context context) {
        BufferedReader reader = null;
        StringBuilder result=null;
        try {                                                    //openFileInput方法需要上下文，若不在活动中需要引入context
            FileInputStream fileInputStream = context.openFileInput(name);
            reader = new BufferedReader(new InputStreamReader(fileInputStream));

            String line = "";
            result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d("Test", "result data is " + result);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }
    public String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) { //进制转换
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

        //该方法用来截取Str字符串中strStart和strEnd之间的部分
    public static String subString(String str, String strStart, String strEnd) {

        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);

        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strStart + ", 无法截取目标字符串";
        }
        if (strEndIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strEnd + ", 无法截取目标字符串";
        }
        /* 开始截取 */
        String result = str.substring(strStartIndex, strEndIndex).substring(strStart.length());
        return result;
    }
}
