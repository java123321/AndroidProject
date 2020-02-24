package com.example.ourprojecttest;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ShoppingCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Intent intent=new Intent("com.example.ourprojecttest.UPDATE_DATA");
    private Context mContext;
    private ArrayList<ShoppingCartList> mList;
    private int rankBig=0;
    private int length;
    DecimalFormat df = new DecimalFormat("##0.0");
    CommonMethod method=new CommonMethod();
    ShoppingCartActivity cartActivity=new ShoppingCartActivity();
    public ShoppingCartAdapter(Context context) {
        mContext = context;

    }

    public ArrayList<ShoppingCartList> getList(){
        return mList;
    }
    //设置list
    public void setList(ArrayList<ShoppingCartList> list){
        mList = list;
        length=mList.size();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.shopping_cart_recylerview_item, null);
        final ViewHolder holder=new ViewHolder(view);
        holder.rank=rankBig++;
        Log.d("cart",holder.rank+"");
        //绑定药品的选择点击事件
        holder.drugChoiced.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (holder.drugChoiced.getDrawable().getCurrent().getConstantState().equals(mContext.getResources().getDrawable(R.drawable.unchecked).getConstantState())){
//当image1的src为R.drawable.A时，设置image1的src为R.drawable.B
                    holder.drugChoiced.setImageResource(R.drawable.checked);
                    mList.get(holder.rank).setChecked("true");
                    intent.putExtra("value",String.valueOf(method.calculatePrice(mList,length)));
                    mContext.sendBroadcast(intent);
                }else{
                    //否则设置image1的src为R.drawable.A
                    holder.drugChoiced.setImageResource(R.drawable.unchecked);
                    mList.get(holder.rank).setChecked("false");
                    intent.putExtra("value",String.valueOf(method.calculatePrice(mList,length)));
                    mContext.sendBroadcast(intent);
                }
            }
        });

        //设置药品的减号事件
        holder.jianhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num= Integer.valueOf(holder.drugNumber.getText().toString().trim());
           if(num>1){//最少要购买一件
               holder.drugNumber.setText(String.valueOf(--num));
                 mList.get(holder.rank).setTotalPrice(Double.valueOf(holder.drugPrice.getText().toString().trim())*num);
               intent.putExtra("value",String.valueOf(method.calculatePrice(mList,length)));
               mContext.sendBroadcast(intent);
           }
            }
        });
        //设置药品的加号事件
        holder.jiahao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num=Integer.valueOf(holder.drugNumber.getText().toString().trim());
                holder.drugNumber.setText(String.valueOf(++num));
                mList.get(holder.rank).setTotalPrice(Double.valueOf(holder.drugPrice.getText().toString().trim())*num);
                intent.putExtra("value",String.valueOf(method.calculatePrice(mList,length)));
                mContext.sendBroadcast(intent);

//                Intent intent=new Intent(mContext,ShoppingCartActivity.class);
//                intent.putExtra("value",String.valueOf(method.calculatePrice(mList,length)));
            }
        });
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
        int rank;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ShoppingCartList drug=mList.get(position);
        ((ViewHolder) holder).drugName.setText(drug.getDrugName());
        ((ViewHolder) holder).drugPrice.setText(drug.getDrugPrice());
        if(drug.getChecked().equals("false")){
            ((ViewHolder) holder).drugChoiced.setImageResource(R.drawable.unchecked);
        }
        else{
            ((ViewHolder) holder).drugChoiced.setImageResource(R.drawable.checked);
        }
        //设置图片
        byte[] appIcon=drug.getDrugPicture();
        ((ViewHolder) holder).imageView.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
