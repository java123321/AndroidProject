package com.example.ourprojecttest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

public class Stu_Yaodian_Drug_Detail_Information extends AppCompatActivity {
    CommonMethod method=new CommonMethod();
private ImageView picture;
private TextView name;
private TextView price;
private TextView rest;
private TextView description;
private Button buy;
private Button addToCart;
private String Flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu__yaodian__drug__detail__information);
        initView();
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        //设置加入购物车的点击事件
        addToCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!Flag.equals("true")){
                    Toast.makeText(Stu_Yaodian_Drug_Detail_Information.this,"此药品为处方药，不可私自加入购物车!",Toast.LENGTH_SHORT).show();
                }
                else{
                    //将药品信息放入到对象中
                    Intent intent=getIntent();
                    ShoppingCartList shoppingCartList=new ShoppingCartList();
                    shoppingCartList.setId(intent.getStringExtra("id"));
                    Log.d("aaid",intent.getStringExtra("id"));
                    shoppingCartList.setDrugName(intent.getStringExtra("name"));
                    shoppingCartList.setDrugPrice(intent.getStringExtra("price"));
                    shoppingCartList.setTotalPrice(Double.valueOf(intent.getStringExtra("price")));
                    shoppingCartList.setDrugPicture(bitmap2Bytes(drawableToBitamp(picture.getDrawable())));
                    shoppingCartList.setChecked("false");
                    //在往本地存储购物车数据时先从中取出
                    ArrayList<ShoppingCartList> cartLists=readListFromSdCard("ShoppingCartList");
                    //如果没有数组就说明这是用户第一次加入购物车，新建一个即可
                    if(cartLists==null){
                        cartLists=new ArrayList<>();
                        cartLists.add(shoppingCartList);
                    }
                    else{
                        //将当前药品加入数组
                        cartLists.add(shoppingCartList);
                    }
                    //再将数组保存到本地
                    method.writeListIntoSDcard("ShoppingCartList",cartLists);

                    //当用户添加到购物车成功时给出提示
                    Toast.makeText(Stu_Yaodian_Drug_Detail_Information.this,"添加成功，在购物车等亲！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //设置购买的点击事件
        buy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("bu110","0"+Flag);
                //如果是处方药则弹出提示不让购买
                if(!Flag.equals("true")){
                    Toast.makeText(Stu_Yaodian_Drug_Detail_Information.this,"此药品为处方药，不可私自购买!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent=new Intent(Stu_Yaodian_Drug_Detail_Information.this, StuBuyDrug.class);
                    intent.putExtra("description",description.getText().toString());
                    intent.putExtra("name",name.getText().toString());
                    intent.putExtra("price",getIntent().getStringExtra("price"));
                    intent.putExtra("picture",getIntent().getByteArrayExtra("picture"));
                    Log.d("detail",name.getText().toString());
                    Log.d("detail",description.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
    /**
     * 读取sd卡对象
     *
     * @param fileName 文件名
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ShoppingCartList> readListFromSdCard(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  //检测sd卡是否存在
            ArrayList<ShoppingCartList> list;
            File sdCardDir = Environment.getExternalStorageDirectory();
            File sdFile = new File(sdCardDir, fileName);
            try {
                FileInputStream fis = new FileInputStream(sdFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (ArrayList<ShoppingCartList>) ois.readObject();
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
    private void initView(){
        //实例化控件
        addToCart=findViewById(R.id.stu_yaodian_add_shopping_car);
        picture=findViewById(R.id.stu_yaodian_detail_pic);
        name=findViewById(R.id.stu_yaodian_detail_drug_name);
        price=findViewById(R.id.stu_yaodian_detail_drug_price);
        rest=findViewById(R.id.stu_yaodian_detail_drug_kucun);
        description=findViewById(R.id.stu_yaodian_detail_description);
        buy=findViewById(R.id.stu_yaodian_buy);



        //设置图片源头
        Intent intent=getIntent();
        name.setText(intent.getStringExtra("name"));
        price.setText("￥ "+intent.getStringExtra("price"));
        description.setText(intent.getStringExtra("description"));
        rest.setText("库存量 "+intent.getStringExtra("rest"));
        Flag=intent.getStringExtra("Flag");


        byte[] appIcon=getIntent().getByteArrayExtra("picture");
        picture.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
    }

    /**
     * bitmap转化成byte数组
     * @param bm 需要转换的Bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    /**
     * drawable转化成bitmap的方法
     * @param drawable 需要转换的Drawable
     */
    public static Bitmap drawableToBitamp(Drawable drawable) {
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
}
