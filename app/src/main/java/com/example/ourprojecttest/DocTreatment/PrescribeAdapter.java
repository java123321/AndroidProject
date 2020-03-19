package com.example.ourprojecttest.DocTreatment;

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
import com.example.ourprojecttest.StuMine.ShoppingCart.ShoppingCartBean;

import java.util.ArrayList;

public class PrescribeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    CommonMethod method = new CommonMethod();
    Intent intent = new Intent("com.example.ourprojecttest.Perscribe");
    private Context mContext;
    private ArrayList<PrescribeBean> mList = new ArrayList<>();
    private int length;
    private int rankBig = 0;

    public PrescribeAdapter(Context context) {
        mContext = context;
    }

    //设置list
    public void setList(ArrayList<PrescribeBean> list) {
        mList = list;
        length = mList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.prescribe_recyler_item, null);
        final ViewHolder holder = new ViewHolder(view);
        holder.rank = rankBig++;
        Log.d("cribe", "oncreate");
        //设置药品的减号事件
        holder.jianhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(holder.drugNumber.getText().toString().trim());
                if (num > 1) {//最少要购买一件
                    holder.drugNumber.setText(String.valueOf(--num));
                    mList.get(holder.rank).setDrugAmount(num);
                    intent.putExtra("sub", holder.singlePrice);

                    mContext.sendBroadcast(intent);
                }
            }
        });
        //设置药品的加号事件
        holder.jiahao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(holder.drugNumber.getText().toString().trim());
                holder.drugNumber.setText(String.valueOf(++num));
                mList.get(holder.rank).setDrugAmount(num);
                intent.putExtra("add", holder.singlePrice);
                mContext.sendBroadcast(intent);

            }
        });
        return holder;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView drugPrice;
        String singlePrice;
        TextView drugName;
        ImageView jianhao;
        ImageView jiahao;
        TextView drugNumber;
        int rank;

        public ViewHolder(View itemView) {
            super(itemView);
            drugNumber = itemView.findViewById(R.id.stu_shopping_cart_item_num);
            jianhao = itemView.findViewById(R.id.stu_shopping_cart_item_jianhao);
            jiahao = itemView.findViewById(R.id.stu_shopping_cart_item_jiahao);
            imageView = itemView.findViewById(R.id.stu_shopping_cart_item_picture);
            drugName = itemView.findViewById(R.id.stu_shopping_cart_item_name);
            drugPrice = itemView.findViewById(R.id.stu_shopping_cart_item_price);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("cribe", "onbind");
        PrescribeBean drug = mList.get(position);
        ((ViewHolder) holder).drugName.setText(drug.getDrugName());
        ((ViewHolder) holder).drugPrice.setText(drug.getDrugPrice());
        ((ViewHolder) holder).singlePrice = drug.getDrugPrice();
        Log.d("cribe", "price" + drug.getDrugPrice());
        //设置图片
        ((ViewHolder) holder).imageView.setImageBitmap(drug.getDrugPicture());
    }

    @Override
    public int getItemCount() {
        Log.d("cribe", "listSize:" + mList.size());
        return mList.size();
    }
}
