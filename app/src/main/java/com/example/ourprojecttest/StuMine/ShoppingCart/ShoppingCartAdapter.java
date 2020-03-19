package com.example.ourprojecttest.StuMine.ShoppingCart;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.CommonMethod;
import com.example.ourprojecttest.R;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ShoppingCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Intent intent=new Intent("com.example.ourprojecttest.UPDATE_DATA");
    private Context mContext;
    private ArrayList<ShoppingCartBean> mList;
    private int rankBig=0;
    private int length;
    DecimalFormat df = new DecimalFormat("##0.0");
    CommonMethod method=new CommonMethod();
    ShoppingCartActivity cartActivity=new ShoppingCartActivity();
    public ShoppingCartAdapter(Context context) {
        mContext = context;
    }

    public ArrayList<ShoppingCartBean> getList(){
        return mList;
    }
    //设置list
    public void setList(ArrayList<ShoppingCartBean> list){
        mList = list;
        length=mList.size();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.shopping_cart_recylerview_item, null);
        final ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView drugPrice;
        TextView drugName;
        ImageView drugChoiced;
        ImageView jianhao;
        ImageView jiahao;
        TextView drugNumber;

        public ViewHolder(View itemView){
            super(itemView);
            drugNumber=itemView.findViewById(R.id.stu_shopping_cart_item_num);
            jianhao=itemView.findViewById(R.id.stu_shopping_cart_item_jianhao);
            jiahao=itemView.findViewById(R.id.stu_shopping_cart_item_jiahao);
            imageView=itemView.findViewById(R.id.stu_shopping_cart_item_picture);
            drugName=itemView.findViewById(R.id.stu_shopping_cart_item_name);
            drugPrice=itemView.findViewById(R.id.stu_shopping_cart_item_price);
            drugChoiced=itemView.findViewById(R.id.stu_shopping_cart_item_choiced);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final ShoppingCartBean drug=mList.get(position);
        final ViewHolder viewHolder= ((ViewHolder) holder);
        viewHolder.drugName.setText(drug.getDrugName());
        viewHolder.drugPrice.setText(drug.getDrugPrice());
        if(drug.getChecked().equals("false")){
            viewHolder.drugChoiced.setImageResource(R.drawable.unchecked);
        }
        else{
            viewHolder.drugChoiced.setImageResource(R.drawable.checked);
        }
        //设置图片
        byte[] appIcon=drug.getDrugPicture();
        viewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
        //设置药品的数量
        viewHolder.drugNumber.setText(String.valueOf(drug.getDrugAmount()));

        //设置药品的减号事件
        viewHolder.jianhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num= drug.getDrugAmount();
                if(num>1){//最少要购买一件
                    viewHolder.drugNumber.setText(String.valueOf(--num));
                    drug.setDrugAmount(num);
                    drug.setTotalPrice(Double.valueOf(drug.getDrugPrice())*num);
                    intent.putExtra("value",String.valueOf(method.calculatePrice(mList,length)));
                    mContext.sendBroadcast(intent);
                }
            }
        });
        //设置药品的加号事件
        viewHolder.jiahao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num=drug.getDrugAmount();
                viewHolder.drugNumber.setText(String.valueOf(++num));
                drug.setDrugAmount(num);
                drug.setTotalPrice(Double.valueOf(drug.getDrugPrice())*num);
                intent.putExtra("value",String.valueOf(method.calculatePrice(mList,length)));
                mContext.sendBroadcast(intent);

//                Intent intent=new Intent(mContext,ShoppingCartActivity.class);
//                intent.putExtra("value",String.valueOf(method.calculatePrice(mList,length)));
            }
        });

        //绑定药品的选择点击事件
        viewHolder.drugChoiced.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (drug.getChecked().equals("false")){
//当image1的src为R.drawable.A时，设置image1的src为R.drawable.B
                    viewHolder.drugChoiced.setImageResource(R.drawable.checked);
                    drug.setChecked("true");
                    intent.putExtra("value",String.valueOf(method.calculatePrice(mList,length)));
                    mContext.sendBroadcast(intent);
                }else{
                    //否则设置image1的src为R.drawable.A
                    viewHolder.drugChoiced.setImageResource(R.drawable.unchecked);
                    drug.setChecked("false");
                    intent.putExtra("value",String.valueOf(method.calculatePrice(mList,length)));
                    mContext.sendBroadcast(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
