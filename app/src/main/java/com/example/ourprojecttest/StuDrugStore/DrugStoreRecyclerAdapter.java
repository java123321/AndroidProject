package com.example.ourprojecttest.StuDrugStore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuMine.ShoppingCart.ShoppingCartBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DrugStoreRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Intent intentToPrescribe = new Intent("com.example.ourprojecttest.Perscribe");
    private CommonMethod method = new CommonMethod();
    private StuDrugStoreFragment fragment;
    private String type;
    private int num = 1;
    private Context mContext;
    public List<DrugInformation> mList = new ArrayList<>();
    //普通布局的type
    private static final int TYPE_ITEM = 0;
    //脚布局
    private static final int TYPE_FOOTER = 1;
    //  //上拉加载更多
//  static final int PULL_LOAD_MORE = 0;
    //正在加载更多
    private static final int LOADING_MORE = 1;
    //没有更多
    private static final int NO_MORE = 2;
    //脚布局当前的状态,默认为还有更多
    private int footer_state = 1;
    public void setList(List<DrugInformation> list) {
        mList = list;
    }
    public void addList(List<DrugInformation> list) {
        mList.addAll(list);
    }
    public DrugStoreRecyclerAdapter(Context context, StuDrugStoreFragment fragment) {
        mContext = context;
        this.type = method.getFileData("Type", mContext);
        this.fragment = fragment;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = View.inflate(mContext, R.layout.stu_yaodian_recyclerview_item, null);
            final MyViewHolder holder = new MyViewHolder(view);
            holder.itemViewFather.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = holder.getAdapterPosition();
                    //进行点击事件的页面跳转

                    //首先将点击位置的数据取出
                    DrugInformation drug_information = mList.get(position);
                    //如果当前是学生登录

                    if (type.equals("Stu")) {
                        Intent intent = new Intent(mContext, StuDrugDetail.class);
                        intent.putExtra("id", drug_information.getId());
                        intent.putExtra("picture", method.bitmap2Bytes(method.drawableToBitamp(drug_information.getDrug_Picture())));
                        intent.putExtra("name", drug_information.getDrug_Name());
                        intent.putExtra("rest", drug_information.getDrug_Amount());
                        intent.putExtra("description", drug_information.getDrug_Describe());
                        intent.putExtra("price", drug_information.getDrug_Price());
                        intent.putExtra("Flag", drug_information.getDrug_OTC());

                        Log.d("yaodian", "hahahha");
                        mContext.startActivity(intent);
                    } else {//如果是医生登录
                        Log.d("cribe", "flag1" + fragment.flag);
                        //如果是从医生开订单哪里过来的
                        if (fragment.flag) {
                            intentToPrescribe.putExtra("drugId", drug_information.getId());
                            intentToPrescribe.putExtra("drugName", drug_information.getDrug_Name());
                            intentToPrescribe.putExtra("drugPrice", drug_information.getDrug_Price());
                            intentToPrescribe.putExtra("drugPicture", method.bitmap2Bytes(method.drawableToBitamp(drug_information.getDrug_Picture())));
                            mContext.sendBroadcast(intentToPrescribe);
                            Log.d("cribe", "sent");
                        } else {
                            Intent intent = new Intent(mContext, UpDrugMsgActivity.class);
                            intent.putExtra("adjust", "1");
                            intent.putExtra("amount", drug_information.getDrug_Amount());
                            intent.putExtra("drugName", drug_information.getDrug_Name());
                            intent.putExtra("drugPrice", drug_information.getDrug_Price());
                            intent.putExtra("drugDescription", drug_information.getDrug_Describe());
                            intent.putExtra("drugPicture", method.bitmap2Bytes(method.drawableToBitamp(drug_information.getDrug_Picture())));
                            mContext.startActivity(intent);
                        }

                    }
                }
            });
            return holder;
        } else if (viewType == TYPE_FOOTER) {
            //脚布局
            View view = View.inflate(mContext, R.layout.recycler_load_more_layout, null);
            FootViewHolder footViewHolder = new FootViewHolder(view);
            return footViewHolder;
        }
        return null;
    }

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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("adpter", "onBindView is started :" + num++);
        if (holder instanceof MyViewHolder) {
            DrugInformation info = mList.get(position);
            ((MyViewHolder) holder).mTextView.setText(info.getDrug_Name());
            ((MyViewHolder) holder).imageView.setImageDrawable(info.getDrug_Picture());
            if (mList.get(position).getDrug_OTC().equals("true")) {
                ((MyViewHolder) holder).OTCFlag.setText("OTC   ");
                ((MyViewHolder) holder).OTCFlag.setTextColor(Color.GREEN);
            } else {
                ((MyViewHolder) holder).OTCFlag.setText("RX   ");
                ((MyViewHolder) holder).OTCFlag.setTextColor(Color.RED);
            }
        } else if (holder instanceof FootViewHolder) {

            FootViewHolder footViewHolder = (FootViewHolder) holder;
            if (position == 0) {//如果第一个就是脚布局,,那就让他隐藏
                footViewHolder.tv_state.setText("");
            }
            switch (footer_state) {//根据状态来让脚布局发生改变
//        case PULL_LOAD_MORE://上拉加载
//          footViewHolder.mProgressBar.setVisibility(View.GONE);
//          footViewHolder.tv_state.setText("上拉加载更多");
//          break;
                case LOADING_MORE:
                    footViewHolder.tv_state.setText("正在加载...");
                    break;
                case NO_MORE:
                    footViewHolder.tv_state.setText("已加载全部药品！");
                    break;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        //如果position加1正好等于所有item的总和,说明是最后一个item,将它设置为脚布局
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        Log.d("msginit", "drug:" + mList.size());
        return mList != null ? mList.size() + 1 : 0;
    }

    /**
     * 正常布局的ViewHolder
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView imageView;
        View itemViewFather;
        TextView OTCFlag;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemViewFather = itemView;
            mTextView = itemView.findViewById(R.id.stu_yaopin_item_name);
            imageView = itemView.findViewById(R.id.stu_yaopin_item_pic);
            OTCFlag = itemView.findViewById(R.id.stu_otc_flag);
        }
    }

    /**
     * 脚布局的ViewHolder
     */
    public static class FootViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_state;

        public FootViewHolder(View itemView) {
            super(itemView);
            tv_state = itemView.findViewById(R.id.foot_view_item_tv);
        }
    }

    /**
     * 改变脚布局的状态的方法,在activity根据请求数据的状态来改变这个状态
     *
     * @param state
     */
    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }


    //该方法用来为脚布局跨越多行
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (getItemViewType(position) == TYPE_FOOTER) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
    }

}
