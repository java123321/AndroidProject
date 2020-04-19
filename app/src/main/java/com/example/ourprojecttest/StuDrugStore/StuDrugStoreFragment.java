package com.example.ourprojecttest.StuDrugStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.String;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ourprojecttest.StuMine.StuInfomation.StuInformation;
import com.example.ourprojecttest.StuMine.StuMineFragment;
import com.example.ourprojecttest.StuMine.Tubiao;
import com.example.ourprojecttest.Utils.CommonMethod;
import com.example.ourprojecttest.Utils.ImmersiveStatusbar;
import com.example.ourprojecttest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StuDrugStoreFragment extends Fragment {
    //正在加载更多
    static final int LOADING_MORE = 1;
    //没有更多
    static final int NO_MORE = 2;
    private String ipAddress;
    public boolean flag = false;
    private final String loadNum = "16";
    private LoadThread load;
    private CommonMethod method = new CommonMethod();
    private Context context;
    private int totalNum;//该变量用于记录每次访问数据库时数据库总共返回了多少条数据
    private int currentNum;//该变量用于记录在下载图片的线程中进行数目的统计，用于标志位的判断
    private int total = 0;
    private int last = 0;
    private  Activity a;
    private SwipeRefreshLayout refreshLayout;
    public Button addNewDrug;
    private TextView lastColorName;
    private Button sousuo;
    private EditText inputInspect;
    private String selectedMenu = "全部";
    private Drawable searchEditDraw, searchEditDraw1;
    private TextView quanbu;
    private TextView nanke;
    private TextView fuke;
    private TextView huxike;
    private TextView xiaohuake;
    private TextView neifenmike;
    private TextView xinxueguanke;
    private TextView miniaoke;
    private TextView xueyeke;
    private TextView fengshigu;
    private TextView erbihouke;
    private TextView yanke;
    private TextView kouqiangke;
    private TextView pifuke;
    private TextView shenjingke;
    private TextView ganranke;
    private TextView baojianshipin;
    private TextView yiliaoqixie;
    private TextView qita;
    private RecyclerView mRecycler;
    private DrugStoreRecyclerAdapter mAdapter;
    private GridLayoutManager gridLayoutManager;
    private int lastVisibleItem;
    private boolean isLoading = false;//用来控制进入getdata()的次数
    private boolean clear = true;
    private LinearLayout empty;
    private DecimalFormat df = new DecimalFormat("##0.00");
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            List<DrugInformation> list = (List<DrugInformation>) msg.obj;


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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stu_frag_yaodian_fore, container, false);//首先填充整个药店碎片的前段布局1
        context = getContext();
        ipAddress = getResources().getString(R.string.ipAdrress);
        a = getActivity();
        ImmersiveStatusbar.getInstance().Immersive(a.getWindow(), a.getActionBar());//状态栏透明
        initView(view);
        Log.d("msg", "获取图片");
        getData("1", loadNum, "全部", inputInspect.getText().toString().trim());
        return view;
    }


    //初始化RecyclerView布局
    private void initView(View view) {
        empty = view.findViewById(R.id.empty);
        empty.setVisibility(View.GONE);
        refreshLayout = view.findViewById(R.id.dropDownToRefresh);
        mRecycler = view.findViewById(R.id.stu_yaodian_recycler);//获取RecyclerView的滚动布局2
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecycler.setLayoutManager(gridLayoutManager);
        //为RecyclerView添加适配器
        mAdapter = new DrugStoreRecyclerAdapter(getContext(), this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setVisibility(View.VISIBLE);
        inputInspect = view.findViewById(R.id.stu_yaodian_search_box);
        sousuo = view.findViewById(R.id.stu_yaodian_sousuo);
        searchEditDraw = context.getResources().getDrawable(R.drawable.sousou);
        searchEditDraw.setBounds(0, 0, 55, 55);
        inputInspect.setCompoundDrawables(searchEditDraw, null, null, null);

        //实现下拉刷新的更新事件

        searchEditDraw = context.getResources().getDrawable(R.drawable.sousou);
        searchEditDraw1 = context.getResources().getDrawable(R.drawable.chahao);
        searchEditDraw.setBounds(0, 0, 60, 60);
        searchEditDraw1.setBounds(0, 0, 70, 70);
        inputInspect.setCompoundDrawables(searchEditDraw, null, null, null);
        //实现下拉刷新的更新事件

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData("1", loadNum, selectedMenu,inputInspect.getText().toString().trim());
            }
        });
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_bottom));
        refreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.color_progressbar));
        //注册搜索的点击事件
        sousuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData("1", loadNum, selectedMenu, inputInspect.getText().toString().trim());
                Log.d("yaodian", "choiced:" + selectedMenu + "    input:" + inputInspect.getText().toString().trim());
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
                        getData(String.valueOf(Integer.valueOf(last) + 1), loadNum, selectedMenu, inputInspect.getText().toString().trim());
                    } else {
                        mAdapter.changeState(NO_MORE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //拿到最后一个出现的item的位置
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                Log.d("123321", "last item is :" + lastVisibleItem);
            }
        });
        //实现输入框文本清空功能
        inputInspect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (inputInspect.getText().toString().length() == 0) {
                    inputInspect.setCompoundDrawables(searchEditDraw, null, null, null);
                    inputInspect.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {
                            //获取点击焦点
                            return false;
                        }
                    });

                } else {
                    inputInspect.setCompoundDrawables(searchEditDraw, null, searchEditDraw1, null);
                    inputInspect.setOnTouchListener(new View.OnTouchListener() {
                        Drawable drawable = inputInspect.getCompoundDrawables()[2];

                        @Override
                        public boolean onTouch(View view, MotionEvent event) {
                            //获取点击焦点
                            if (event.getX() > inputInspect.getWidth() - inputInspect.getPaddingRight() - drawable.getIntrinsicWidth()) {
                                //其他活动无响应
                                if (event.getAction() != MotionEvent.ACTION_UP)
                                    return false;
                                //清空用户名
                                inputInspect.setText("");
                            }
                            return false;
                        }
                    });
                }

            }
        });

    }


    private String getDrugInfoByPost(String start,String count,String type,String name){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("start=").append(start)
                .append("&count=").append(count)
                .append("&type=").append(type)
                .append("&name=").append(name);;
        byte[] data = stringBuilder.toString().getBytes();
        try {
            URL url = new URL(ipAddress + "IM/GetDrugInformation");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);//设置连接超时时间
            urlConnection.setDoInput(true);//设置输入流采用字节流
            urlConnection.setDoOutput(true);//设置输出采用字节流
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);//使用post方式不能使用缓存
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//设置meta参数
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            urlConnection.setRequestProperty("Charset", "utf-8");
            //获得输出流，向服务器写入数据
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(data);
            int response = urlConnection.getResponseCode();//获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] result = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(result)) != -1) {
                    byteArrayOutputStream.write(result, 0, len);
                }
                String resultData = new String(byteArrayOutputStream.toByteArray()).trim();
                return resultData;
            } else {
             return "获取药品失败";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "获取药品失败";
    }
    class LoadThread extends Thread {
        private String start;
        private String count;
        private String type;
        private String name;
        private volatile boolean flag = false;

        @Override
        public void run() {
//            String url;
//            StringBuilder stringBuilder = new StringBuilder();
//            //添加基础字符串
//            stringBuilder.append(ipAddress + "IM/GetDrugInformation?start=" + start + "&count=" + count);
//            //添加类别变量
//            if (!type.equals("全部")) {
//                stringBuilder.append("&type=" + type);
//            }
//            if (!name.equals("")) {
//                stringBuilder.append("&name=" + name);
//            }
//            url = stringBuilder.toString();
//            Log.d("yaodian", url);

//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
            try {
//                Response response = client.newCall(request).execute();
////                String responseData = response.body().string();
//                Log.d("drugstore", "response:" + responseData);
                //-------------------------解析-↓---------------------------//
                try {
                    //获取后面的药品数组
                    JSONArray jsonArray = new JSONArray(getDrugInfoByPost(start,count,type,name));
                    //totalNum用于记录当前的一次获取数据中一共有多少条数据
                    totalNum = jsonArray.length() - 1;
                    currentNum = 0;//每次访问数据库的时候将已加载的数目重置为0
                    Log.d("yaodian", "the total num is :" + String.valueOf(totalNum));
                    List<DrugInformation> list = new ArrayList<>();
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
                        //如果为当前的第一条数据的话，则读取数据数量的信息
                        jsonObject = jsonArray.getJSONObject(i);
                        try {
                            Log.d("yaodianName", jsonObject.getString("Drug_Name"));
                            final DrugInformation drug_information = new DrugInformation(); //创建药品对象
                            drug_information.setDrug_Describe(jsonObject.getString("Drug_Describe"));
                            drug_information.setDrug_Amount(jsonObject.getString("Drug_Amount"));
                            drug_information.setDrug_Name(jsonObject.getString("Drug_Name"));
                            drug_information.setDrug_Price(df.format(Double.valueOf(jsonObject.getString("Drug_Price"))));
                            drug_information.setDrug_Type(jsonObject.getString("Drug_Type"));
                            drug_information.setDrug_OTC(jsonObject.getString("Drug_OTC"));
                            drug_information.setId(jsonObject.getString("Drug_Id"));
                            final String imageUrl = jsonObject.getString("Drug_Index");
                            drug_information.setDrug_Picture(Drawable.createFromStream(new URL(ipAddress + imageUrl).openStream(), "image.jpg"));
                            if (flag) {
                                Log.d("drugstore", "中断--------------------------");
                                return;
                            }
                            list.add(drug_information);//将设置好的对象添加到数组里
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


    //该方法用于从数据库获取数据,参数分别为要搜索结果的开始，结束，药品类型(-1代表全部)，药品名字
    private void getData(final String start, final String count, final String type, final String name) {
        Log.d("getdrugstore","start:"+start+"count:"+count+"type:"+type+"name:"+name);
        //加载的过程中显示recycleview
        //mAdapter.mList.clear();
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
        load.type = type;
        load.name = name;
        load.start();
    }

    private void initTextView(View view) {

        //获取控件实例
        addNewDrug = view.findViewById(R.id.doc_yaodian_add_yaopin);
        //如果是医生登录则显示添加药品的按钮
        if ((method.getFileData("Type", getContext()).equals("Doc"))) {
            addNewDrug.setVisibility(View.VISIBLE);
            addNewDrug.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), UpDrugMsgActivity.class);
                    intent.putExtra("adjust", "0");
                    startActivity(intent);
                }
            });
        } else {
            addNewDrug.setVisibility(View.INVISIBLE);
        }
        quanbu = view.findViewById(R.id.quanbu);
        nanke = view.findViewById(R.id.nanke);
        fuke = view.findViewById(R.id.fuke);
        huxike = view.findViewById(R.id.huxike);
        xiaohuake = view.findViewById(R.id.xiaohuake);
        neifenmike = view.findViewById(R.id.neifenmike);
        xinxueguanke = view.findViewById(R.id.xinxueguanke);
        miniaoke = view.findViewById(R.id.miniaoke);
        xueyeke = view.findViewById(R.id.xueyeke);
        fengshigu = view.findViewById(R.id.fengshigu);
        erbihouke = view.findViewById(R.id.erbihouke);
        yanke = view.findViewById(R.id.yanke);
        kouqiangke = view.findViewById(R.id.kouqinagke);
        pifuke = view.findViewById(R.id.pifuke);
        shenjingke = view.findViewById(R.id.shenjingke);
        ganranke = view.findViewById(R.id.ganranke);
        baojianshipin = view.findViewById(R.id.baojianshipin);
        yiliaoqixie = view.findViewById(R.id.yiliaoqixie);
        qita = view.findViewById(R.id.qita);
        lastColorName = quanbu;
        lastColorName.setTextColor(Color.parseColor("#FD0896F5"));
        quanbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "全部";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                quanbu.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = quanbu;
                Log.d("yaodian", "test");
                getData("1", loadNum, "全部", inputInspect.getText().toString().trim());
            }
        });
        nanke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "男科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                nanke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = nanke;
                getData("1", loadNum, "男科", inputInspect.getText().toString().trim());
            }
        });
        fuke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "妇科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                fuke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = fuke;
                getData("1", loadNum, "妇科", inputInspect.getText().toString().trim());
            }
        });

        huxike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "呼吸科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                huxike.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = huxike;
                getData("1", loadNum, "呼吸科", inputInspect.getText().toString().trim());
            }
        });

        xiaohuake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "消化科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                xiaohuake.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = xiaohuake;
                getData("1", loadNum, "消化科", inputInspect.getText().toString().trim());
            }
        });
        neifenmike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "内分泌科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                neifenmike.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = neifenmike;
                getData("1", loadNum, "内分泌科", inputInspect.getText().toString().trim());
            }
        });
        xinxueguanke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "心血管科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                xinxueguanke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = xinxueguanke;
                getData("1", loadNum, "心血管科", inputInspect.getText().toString().trim());
            }
        });
        miniaoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "泌尿科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                miniaoke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = miniaoke;
                getData("1", loadNum, "泌尿科", inputInspect.getText().toString().trim());
            }
        });
        xueyeke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "血液科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                xueyeke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = xueyeke;
                getData("1", loadNum, "血液科", inputInspect.getText().toString().trim());
            }
        });
        fengshigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "风湿骨科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                fengshigu.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = fengshigu;
                getData("1", loadNum, "风湿骨科", inputInspect.getText().toString().trim());
            }
        });
        erbihouke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "耳鼻喉科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                erbihouke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = erbihouke;
                getData("1", loadNum, "耳鼻喉科", inputInspect.getText().toString().trim());
            }
        });
        yanke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "眼科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                yanke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = yanke;
                getData("1", loadNum, "眼科", inputInspect.getText().toString().trim());
            }
        });
        kouqiangke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "口腔科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                kouqiangke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = kouqiangke;
                getData("1", loadNum, "口腔科", inputInspect.getText().toString().trim());
            }
        });
        pifuke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "皮肤科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                pifuke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = pifuke;
                getData("1", loadNum, "皮肤科", inputInspect.getText().toString().trim());
            }
        });
        shenjingke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "神经科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                shenjingke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = shenjingke;
                getData("1", loadNum, "神经科",inputInspect.getText().toString().trim());
            }
        });
        ganranke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "感染科";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                ganranke.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = ganranke;
                getData("1", loadNum, "感染科", inputInspect.getText().toString().trim());
            }
        });
        baojianshipin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "保健食品";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                baojianshipin.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = baojianshipin;
                getData("1", loadNum, "保健食品", inputInspect.getText().toString().trim());
            }
        });
        yiliaoqixie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "医疗器械";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                yiliaoqixie.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = yiliaoqixie;
                getData("1", loadNum, "医疗器械", inputInspect.getText().toString().trim());
            }
        });
        qita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMenu = "其他";
                lastColorName.setTextColor(Color.parseColor("#000000"));
                qita.setTextColor(Color.parseColor("#FD0896F5"));
                lastColorName = qita;
                getData("1", loadNum, "其他", inputInspect.getText().toString().trim());
            }
        });
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}






