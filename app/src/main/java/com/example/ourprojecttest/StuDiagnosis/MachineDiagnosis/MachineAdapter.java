package com.example.ourprojecttest.StuDiagnosis.MachineDiagnosis;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourprojecttest.R;
import com.example.ourprojecttest.StuDrugStore.DrugStoreRecyclerAdapter;

import java.util.ArrayList;

public class MachineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    //脚布局当前的状态,默认为还有更多
    private int footer_state = 1;
    //普通布局的type
    private static final int TYPE_ITEM = 0;
    //脚布局
    private static final int TYPE_FOOTER = 1;
    //正在加载更多
    private static final int LOADING_MORE = 1;
    //没有更多
    private static final int NO_MORE = 2;
    public ArrayList<DiseaseBean> mList = new ArrayList<>();

    public void addList(ArrayList<DiseaseBean> list) {
        mList.addAll(list);
        Log.d("drugsize",mList.size()+"");
    }



    public MachineAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(ArrayList<DiseaseBean> List) {
        mList = List;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==TYPE_ITEM){
            //View view = View.inflate(mContext, R.layout.display_disease_item, null);
            View view = LayoutInflater.from(mContext).inflate(R.layout.display_disease_item,parent,false);
            final ViewHolder holder = new ViewHolder(view);
            return holder;
        }else if(viewType==TYPE_FOOTER){//脚布局
            //脚布局
            View view = View.inflate(mContext, R.layout.recycler_load_more_layout, null);
            DrugStoreRecyclerAdapter.FootViewHolder footViewHolder = new DrugStoreRecyclerAdapter.FootViewHolder(view);
            return footViewHolder;

        }
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView diseaseName;
        LinearLayout diseaseLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            diseaseName = itemView.findViewById(R.id.displayDiseaseName);
            diseaseLayout = itemView.findViewById(R.id.diseaseLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            DiseaseBean info = mList.get(position);
            ViewHolder viewHolder = ((ViewHolder) (holder));
            //显示药品名字
            viewHolder.diseaseName.setText("疾病名称:" + info.getDiseaseName());
            //设置布局的点击事件
            viewHolder.diseaseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DiseaseDetail.class);
                    intent.putExtra("recoveryRate", info.getRecoveryRate());
                    intent.putExtra("infectious", info.getInfectious());
                    intent.putExtra("treatmentDepartment", info.getTreatmentDepartment());
                    intent.putExtra("belongInsurance", info.getBelongInsurance());
                    intent.putExtra("population", info.getPopulation());
                    intent.putExtra("concurrentDisease", info.getConcurrentDisease());
                    intent.putExtra("symptom", info.getSymptom());
                    intent.putExtra("id", info.getId());
                    intent.putExtra("diseaseName", info.getDiseaseName());
                    intent.putExtra("diseaseAlias", info.getDiseaseAlias());
                    intent.putExtra("diseaseLocation", info.getDiseaseLocation());
                    intent.putExtra("introducton", info.getIntroduction());
                    intent.putExtra("treatmentDuration", info.getTreatmentDuration());
                    mContext.startActivity(intent);
                }
            });
        } else if (holder instanceof DrugStoreRecyclerAdapter.FootViewHolder) {
            DrugStoreRecyclerAdapter.FootViewHolder footViewHolder = (DrugStoreRecyclerAdapter.FootViewHolder) holder;
            if (position == 0) {//如果第一个就是脚布局,,那就让他隐藏
                footViewHolder.tv_state.setText("");
            }
            switch (footer_state) {//根据状态来让脚布局发生改变
                case LOADING_MORE:
                    footViewHolder.tv_state.setText("正在加载...");
                    break;
                case NO_MORE:
                    footViewHolder.tv_state.setText("已加载全部疾病信息！");
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

    /**
     * 改变脚布局的状态的方法,在activity根据请求数据的状态来改变这个状态
     *
     * @param state
     */
    public void changeState(int state) {
        this.footer_state = state;
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        Log.d("msginit", "drug:" + mList.size());
        return mList != null ? mList.size() + 1 : 0;
    }
}
