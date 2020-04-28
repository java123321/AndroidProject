package com.example.ourprojecttest.StuDiagnosis.MachineDiagnosis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;

import com.example.ourprojecttest.R;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendDrug extends AppCompatActivity {
    private final String loadNum = "35";//该数字代表每次从数据库申请获取多少条数据
    private String machineIpAddress;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout empty;
    private int last = 0;
    private boolean clear = true;
    private int total = 0;//改变量用于统计每次检索检索条件数据库里共有多少条数据
    //正在加载更多
    private static final int LOADING_MORE = 1;
    //没有更多
    static final int NO_MORE = 2;
    private LoadThread load;
    private RecyclerView mRecycler;
    private LinearLayoutManager layoutManager;
    private RecommondDrugAdapter mAdapter;
    private int totalNum;//该变量用于记录每次访问数据库时数据库总共返回了多少条数据
    private boolean isLoading = false;//用来控制进入getdata()的次数
    private int lastVisibleItem;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            ArrayList<RecommondDrugBean> list = (ArrayList<RecommondDrugBean>) msg.obj;

            if (clear) {//如果通过更改检索条件检索的结果，则清空原有数组

                if (list.size() == 0) {//如果没有药品，则显示空界面
                    empty.setVisibility(View.VISIBLE);
                    mRecycler.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    mRecycler.setVisibility(View.VISIBLE);
                    mAdapter.setList(list);
                    if (last < total) {
                        mAdapter.changeState(LOADING_MORE);
                    } else {
                        mAdapter.changeState(NO_MORE);
                    }
                }
            } else {//如果是通话下滑加载的数据，调用add方法
                mAdapter.addList(list);
                Log.d("drugsize","addsize:"+list.size());
            }
            mAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
            isLoading = false;
            //将清空恢复
            clear = true;
        }
    };
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_drug);
        initView();
    }

    private void initView(){
        String drugName=getIntent().getStringExtra("diseaseName");
        machineIpAddress=getResources().getString(R.string.machineIpAddress);
        ImmersiveStatusbar.getInstance().Immersive(getWindow(), getActionBar());//状态栏透明
        refreshLayout = findViewById(R.id.dropDownToRefresh);
        //实现下拉刷新的更新事件
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData("1", loadNum,drugName);
            }
        });
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_bottom));
        refreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.color_progressbar));
        empty = findViewById(R.id.empty);
        empty.setVisibility(View.GONE);
        mRecycler=findViewById(R.id.machinRecycer);
        layoutManager = new LinearLayoutManager(RecommendDrug.this);
        mRecycler.setLayoutManager(layoutManager);
        //设置适配器
        mAdapter=new RecommondDrugAdapter(RecommendDrug.this);
        mRecycler.setAdapter(mAdapter);
        getData("1",loadNum,drugName);
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
                Log.d("123321", "getItemCount:" + mAdapter.getItemCount() + "");
                Log.d("123321", "isloading:" + isLoading);
                Log.d("123321", recyclerView.SCROLL_STATE_IDLE + "");
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount() && !isLoading) {
                    //到达底部之后如果footView的状态不是正在加载的状态,就将 他切换成正在加载的状态

                    Log.d("123321", "the last is :" + last + "total is :" + total);
                    //如果当前的数据数小于总共的数据数的话则加载
                    if (last < total) {
                        isLoading = true;
                        // mAdapter.changeState(LOADING_MORE);
                        //如果是加载更多获取的数据则不清空list
                        clear = false;
                        getData(String.valueOf(Integer.valueOf(last) + 1), loadNum,drugName);
                    } else {
                        mAdapter.changeState(NO_MORE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //拿到最后一个出现的item的位置
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                Log.d("123321", "last item is :" + lastVisibleItem);
            }
        });
    }
    private String str2HexStr(String str) {
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
    private void getData(final String start, final String count, String name){   name="肺炎";
//
        name=str2HexStr(name);//首先将中文转化为字符串
        mAdapter.changeState(LOADING_MORE);
        empty.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
        if (clear) {
            last = 0;
            mAdapter.mList.clear();
        }
        refreshLayout.setRefreshing(true);
        //设置加载图片的显示
        if (load != null) {
            boolean f = load.isAlive();
            Log.d("drugstore", "------------------------------" + f);
            if (f) {
                load.flag = true;
            }
        }
        Log.d("drugstore", "中断后-------------------------------------------------");
        load = new LoadThread();
        load.start = start;
        load.count = count;
        load.name = name;
        load.start();
    }

    class LoadThread extends Thread {
        private String start;
        private String count;
        private String name;
        private volatile boolean flag = false;

        @Override
        public void run() {
            try {
                try {
                    StringBuilder stringBuilder=new StringBuilder();
                    stringBuilder.append(machineIpAddress)
                            .append("IM/GetDrugForDisease?start=")
                            .append(start)
                            .append("&count=")
                            .append(count)
                            .append("&disease=")
                            .append(name);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(stringBuilder.toString())
                            .build();
                    Log.d("machine.url",stringBuilder.toString());
                    String responseData=null;
                    try {
                        Response response = client.newCall(request).execute();
                        responseData = response.body().string();
                        Log.d("recommenddrug.response",responseData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //获取后面的药品数组
                    JSONArray jsonArray = new JSONArray(responseData);
                    //totalNum用于记录当前的一次获取数据中一共有多少条数据
                    totalNum = jsonArray.length() - 1;
//                    currentNum = 0;//每次访问数据库的时候将已加载的数目重置为0
                    Log.d("yaodian", "the total num is :" + String.valueOf(totalNum));
                    ArrayList<RecommondDrugBean> list = new ArrayList<>();
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
                    total = Integer.valueOf(sb.toString().trim());
                    //last变量用于记录当前已加载的个数
                    last += jsonArray.length() - 1;
                    Log.d("storea", "total is " + total + "last is :" + last);
                    /*-------读取json数组中的第一个信息↑-----*/
                    for (int i = 1; i < jsonArray.length(); i++) {
                        Log.d("drugstore", i + "");
                        jsonObject = jsonArray.getJSONObject(i);
                        try {

                            final RecommondDrugBean drugBean = new RecommondDrugBean(); //创建药品对象
                            drugBean.setGoodsName(jsonObject.getString("goods_name"));
                            drugBean.setPyCode(jsonObject.getString("py_code"));
                            drugBean.setGuiGe(jsonObject.getString("spec"));
                            drugBean.setUnit(jsonObject.getString("unit"));
                            drugBean.setApprovalNumber(jsonObject.getString("approval_number"));
                            drugBean.setManufacture(jsonObject.getString("manufacturer"));
                            drugBean.setBarCode(jsonObject.getString("bar_code"));
                            drugBean.setCureDisease(jsonObject.getString("zhuzhi"));
                            drugBean.setExplainBook(jsonObject.getString("explain_book"));
                            drugBean.setAdditionalExplain(jsonObject.getString("replenish"));
                            drugBean.setOtc(jsonObject.getString("is_otc"));
                            drugBean.setUpTime(jsonObject.getString("c_time"));

                            if (flag) {
                                Log.d("drugstore", "中断--------------------------");
                                return;
                            }
                            list.add(drugBean);//将设置好的对象添加到数组里
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    Message msg = Message.obtain();
                    msg.obj = list;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //-------------------------解析-↑---------------------------//
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
