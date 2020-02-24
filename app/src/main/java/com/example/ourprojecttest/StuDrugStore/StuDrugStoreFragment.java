package com.example.ourprojecttest.StuDrugStore;

import java.lang.String;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.ourprojecttest.CommonMethod;
import com.example.ourprojecttest.Drug_Information;
import com.example.ourprojecttest.ImmersiveStatusbar;
import com.example.ourprojecttest.R;
import com.example.ourprojecttest.ShoppingCartActivity;
import com.example.ourprojecttest.StuId;
import com.example.ourprojecttest.UpDrugMsgActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StuDrugStoreFragment extends Fragment {

    private final String loadNum="16";
    LoadThread load;
    CommonMethod method=new CommonMethod();
    Context context;
    int totalNum;//该变量用于记录每次访问数据库时数据库总共返回了多少条数据
    int currentNum;//该变量用于记录在下载图片的线程中进行数目的统计，用于标志位的判断
    int total=0;
    int last=0;
    SwipeRefreshLayout refreshLayout;
    Button addNewDrug;
    Button myOrder;
    TextView lastColorName;
    Button sousuo;
    EditText inputInspect;
    String selectedMenu="-1";
    TextView quanbu;
    TextView nanke;
    TextView fuke;
    TextView huxike;
    TextView xiaohuake;
    TextView neifenmike;
    TextView xinxueguanke;
    TextView miniaoke;
    TextView xueyeke;
    TextView fengshigu;
    TextView erbihouke;
    TextView yanke;
    TextView kouqiangke;
    TextView pifuke;
    TextView shenjingke;
    TextView ganranke;
    TextView baojianshipin;
    TextView yiliaoqixie;
    TextView qita;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecycler;
    private DrugStoreRecyclerAdapter mAdapter;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    int lastVisibleItem;
    boolean isLoading = false;//用来控制进入getdata()的次数
    boolean clear=true;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            List<Drug_Information> list=(List<Drug_Information>)msg.obj;
            //如果清空list
            if(clear){
                mAdapter.setList(list);
            }
            else{
                mAdapter.addList(list);
            }
            mAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
            isLoading=false;
            //将清空恢复
            clear=true;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stu_frag_yaodian_fore,container,false);//首先填充整个药店碎片的前段布局1
        context=getContext();
        Activity a=getActivity();
        ImmersiveStatusbar.getInstance().Immersive(a.getWindow(),a.getActionBar());//状态栏透明
        initView(view);
        initTextView(view);
        Log.d("msg","获取图片");
        getData("1",loadNum,"-1","");
        return view;
    }

    //初始化RecyclerView布局
    private void initView(View view) {
        refreshLayout=view.findViewById(R.id.dropDownToRefresh);
        mRecycler = view.findViewById(R.id.stu_yaodian_recycler);//获取RecyclerView的滚动布局2
        gridLayoutManager=new GridLayoutManager(getActivity(),2);
        mRecycler.setLayoutManager(gridLayoutManager);
        //为RecyclerView添加适配器
        mAdapter = new DrugStoreRecyclerAdapter(getContext());
        mRecycler.setAdapter(mAdapter);
        mRecycler.setVisibility(View.VISIBLE);
        inputInspect=view.findViewById(R.id.stu_yaodian_search_box);
        sousuo=view.findViewById(R.id.stu_yaodian_sousuo);
        myOrder = view.findViewById(R.id.my_order);
        Drawable searchEditDraw = context.getResources().getDrawable(R.drawable.sousou);
        searchEditDraw.setBounds(0, 0, 55, 55);
        sousuo.setCompoundDrawables(searchEditDraw, null, null, null);

        //实现下拉刷新的更新事件
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData("1",loadNum,selectedMenu,method.conversion(inputInspect.getText().toString().trim()));
            }
        });
        //注册搜索的点击事件
        sousuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getData("1",loadNum,selectedMenu,method.conversion(inputInspect.getText().toString().trim()));
                Log.d("yaodian","choiced:"+selectedMenu+"    input:"+inputInspect.getText().toString().trim());
            }
        });

        myOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),ShoppingCartActivity.class);
                startActivity(intent);
            }
        });


        //给recyclerView添加滑动监听
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
        /*
        到达底部了,如果不加!isLoading的话到达底部如果还一滑动的话就会一直进入这个方法
        就一直去做请求网络的操作,这样的用户体验肯定不好.添加一个判断,每次滑倒底只进行一次网络请求去请求数据
        当请求完成后,在把isLoading赋值为false,下次滑倒底又能进入这个方法了
         */

                //代表当前滚动状态为停止，否则会导致重绘异常     当前滑动到最后一个项目                     并且不在加载，以防止重复加载
                Log.d("123321","getItemCount:"+mAdapter.getItemCount()+"");
                Log.d("123321","isloading:"+isLoading);
                Log.d("123321",recyclerView.SCROLL_STATE_IDLE+"");
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem+1== mAdapter.getItemCount() && !isLoading) {
                    //到达底部之后如果footView的状态不是正在加载的状态,就将 他切换成正在加载的状态

                    Log.d("123321","the last is :"+last+"total is :"+total);
                    //如果当前的数据数小于总共的数据数的话则加载
                    if(last<total){
                        isLoading = true;
                        mAdapter.changeState(1);
                        //如果是加载更多获取的数据则不清空list
                        clear=false;
                        getData(String.valueOf(Integer.valueOf(last)+1),loadNum,selectedMenu,method.conversion(inputInspect.getText().toString().trim()));
                    }
                    else{
                        mAdapter.changeState(2);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //拿到最后一个出现的item的位置
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                Log.d("123321","last item is :"+lastVisibleItem);
            }
        });
    }

    class LoadThread extends Thread{
        private String start;
        private String count;
        private String type;
        private String name;
        private volatile boolean flag=false;

        @Override
        public void run() {
            String url;
            StringBuilder stringBuilder=new StringBuilder();
            //添加基础字符串
            stringBuilder.append(getResources().getString(R.string.ipAdrress)+"IM/GetDrugInformation?start="+start+"&count="+count);
            //添加类别变量
            if(!type.equals("-1")){
                stringBuilder.append("&type="+type);
            }
            if(!name.equals("")){
                stringBuilder.append("&name="+name);
            }
            url=stringBuilder.toString();
            Log.d("yaodian",url);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                //-------------------------解析-↓---------------------------//
                try {
                    //获取后面的药品数组
                    JSONArray jsonArray = new JSONArray(responseData);
                    //totalNum用于记录当前的一次获取数据中一共有多少条数据
                    totalNum = jsonArray.length() - 1;
                    currentNum = 0;//每次访问数据库的时候将已加载的数目重置为0
                    Log.d("yaodian", "the total num is :" + String.valueOf(totalNum));
                    List<Drug_Information> list=new ArrayList<>();
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    /*-------读取json数组中的第一个信息↓-----*/
                        String description = jsonObject.getString("Msg");
                        StringBuilder sb = new StringBuilder();
                        //开始读取该检索总共有多少条数据
                        for (int j = 5; ; j++) {
                            char ch = description.charAt(j);
                            //如果是数字，则截取下来
                            if ((int) ch >= 48 && (int) ch <= 57) {
                                sb.append(ch);
                            } else {
                                break;
                            }
                        }
                        Log.d("yaodianChar", sb.toString());
                        //用total变量记录当前检索结果的总共数据个数
                        total= Integer.valueOf(sb.toString().trim());
                        //last变量用于记录当前已加载的个数
                        last+=jsonArray.length()-1;
                        Log.d("storea","total is "+total+"last is :"+last);
                    /*-------读取json数组中的第一个信息↑-----*/

                    for (int i = 1; i < jsonArray.length(); i++) {
                        Log.d("drugstore",i+"");
                        //如果为当前的第一条数据的话，则读取数据数量的信息
                        jsonObject=jsonArray.getJSONObject(i);
                            try {
                                Log.d("yaodianName", jsonObject.getString("Drug_Name"));
                                final Drug_Information drug_information = new Drug_Information(); //创建药品对象
                                drug_information.setDrug_Describe(jsonObject.getString("Drug_Describe"));
                                drug_information.setDrug_Amount(jsonObject.getString("Drug_Amount"));
                                drug_information.setDrug_Name(jsonObject.getString("Drug_Name"));
                                drug_information.setDrug_Price(jsonObject.getString("Drug_Price"));
                                drug_information.setDrug_Type(jsonObject.getString("Drug_Type"));
                                drug_information.setDrug_OTC(jsonObject.getString("Drug_OTC"));
                                drug_information.setId(jsonObject.getString("Drug_Id"));
                                final String imageUrl = jsonObject.getString("Drug_Index");
                                drug_information.setDrug_Picture( Drawable.createFromStream(new URL(imageUrl).openStream(), "image.jpg"));
                                if(flag){
                                    Log.d("drugstore","中断--------------------------");
                                    return;
                                }
                                list.add(drug_information);//将设置好的对象添加到数组里
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                    }
                    Message msg=Message.obtain();
                    msg.obj=list;
                    handler.sendMessage(msg);
                }catch(JSONException e){
                    e.printStackTrace();
                }
                //-------------------------解析-↑---------------------------//
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //该方法用于从数据库获取数据,参数分别为要搜索结果的开始，结束，药品类型(-1代表全部)，药品名字
    private void getData(final String start, final String count, final String type, final String name){
        if(clear){
            last=0;
        }
        refreshLayout.setRefreshing(true);
        //设置加载图片的显示
               if(load!=null){
                   boolean f=load.isAlive();
                   Log.d("drugstore","------------------------------"+f);
                   if(f){
                    load.flag=true;
                   }
               }
               Log.d("drugstore","中断后-------------------------------------------------");
        load= new LoadThread();
        load.start=start;
        load.count=count;
        load.type=type;
        load.name=name;
        load.start();
    }
    private void initTextView(View view){
        if(StuId.stuId =="")
        {
            myOrder.setVisibility(View.GONE);
        }
        //获取控件实例
        addNewDrug=view.findViewById(R.id.doc_yaodian_add_yaopin);
        //如果是医生登录则显示添加药品的按钮
        if(method.getFileData("Type",getContext()).equals("Doc")&&StuId.stuId == ""){
            addNewDrug.setVisibility(View.VISIBLE);
            addNewDrug.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (StuId.stuId == ""){
                        Intent intent=new Intent(getContext(), UpDrugMsgActivity.class);
                        intent.putExtra("adjust","0");
                        startActivity(intent);
                    }else {
                        Intent intent=new Intent(getContext(), ShoppingCartActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        else{
            addNewDrug.setVisibility(View.INVISIBLE);
        }
        quanbu=view.findViewById(R.id.quanbu);
         nanke=view.findViewById(R.id.nanke);
        fuke=view.findViewById(R.id.fuke);
         huxike=view.findViewById(R.id.huxike);
        xiaohuake=view.findViewById(R.id.xiaohuake);
        neifenmike=view.findViewById(R.id.neifenmike);
       xinxueguanke=view.findViewById(R.id.xinxueguanke);
         miniaoke=view.findViewById(R.id.miniaoke);
         xueyeke=view.findViewById(R.id.xueyeke);
         fengshigu=view.findViewById(R.id.fengshigu);
        erbihouke=view.findViewById(R.id.erbihouke);
        yanke=view.findViewById(R.id.yanke);
        kouqiangke=view.findViewById(R.id.kouqinagke);
        pifuke=view.findViewById(R.id.pifuke);
        shenjingke=view.findViewById(R.id.shenjingke);
        ganranke=view.findViewById(R.id.ganranke);
      baojianshipin=view.findViewById(R.id.baojianshipin);
         yiliaoqixie=view.findViewById(R.id.yiliaoqixie);
        qita=view.findViewById(R.id.qita);
        lastColorName=quanbu;
        lastColorName.setBackgroundColor(Color.parseColor("#00BFFF"));
        quanbu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="-1";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                quanbu.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=quanbu;
                Log.d("yaodian","test");
                getData("1",loadNum,"-1","");
            }
        });
        nanke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="1";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                nanke.setBackgroundColor(Color.parseColor("#FD0896F5"));
                lastColorName=nanke;
                getData("1",loadNum,"1","");
            }
        });
        fuke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="2";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                fuke.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=fuke;
                getData("1",loadNum,"2","");
            }
        });

        huxike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="3";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                huxike.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=huxike;
                getData("1",loadNum,"3","");
            }
        });

        xiaohuake.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="4";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                xiaohuake.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=xiaohuake;
                getData("1",loadNum,"4","");
            }
        });
        neifenmike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="5";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                neifenmike.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=neifenmike;
                getData("1",loadNum,"5","");
            }
        });
        xinxueguanke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="6";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                xinxueguanke.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=xinxueguanke;
                getData("1",loadNum,"6","");
            }
        });
        miniaoke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="7";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                miniaoke.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=miniaoke;
                getData("1",loadNum,"7","");
            }
        });
        xueyeke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="8";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                xueyeke.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=xueyeke;
                getData("1",loadNum,"8","");
            }
        });
        fengshigu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="9";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                fengshigu.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=fengshigu;
                getData("1",loadNum,"9","");
            }
        });
        erbihouke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="10";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                erbihouke.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=erbihouke;
                getData("1",loadNum,"10","");
            }
        });
        yanke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="11";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                yanke.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=yanke;
                getData("1",loadNum,"11","");
            }
        });
        kouqiangke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="12";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                kouqiangke.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=kouqiangke;
                getData("1",loadNum,"12","");
            }
        });
        pifuke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="13";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                pifuke.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=pifuke;
                getData("1",loadNum,"13","");
            }
        });
        shenjingke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="14";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                shenjingke.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=shenjingke;
                getData("1",loadNum,"14","");
            }
        });
        ganranke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="15";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ganranke.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=ganranke;
                getData("1",loadNum,"15","");
            }
        });
        baojianshipin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="16";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                baojianshipin.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=baojianshipin;
                getData("1",loadNum,"16","");
            }
        });
        yiliaoqixie.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="17";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                yiliaoqixie.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=yiliaoqixie;
                getData("1",loadNum,"17","");
            }
        });
        qita.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectedMenu="100";
                lastColorName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                qita.setBackgroundColor(Color.parseColor("#00BFFF"));
                lastColorName=qita;
                getData("1",loadNum,"100","");
            }
        });
    }
}






