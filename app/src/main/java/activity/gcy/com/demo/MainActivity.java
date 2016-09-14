package activity.gcy.com.demo;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.changvvb.gstreamer3.Gstreamer_test2;
import com.gcy.adapter.MyappliancesAdapter;
import com.gcy.adapter.MyappliancesEnvironmentAdapter;
import com.gcy.beans.IntentKeyString;
import com.gcy.beans.Myappliances;
import com.gcy.beans.TemporaryData;
import com.gcy.mqttUtil.OnlineCheckCallBack;
import com.gcy.mqttUtil.Phone;
import com.gcy.thread.HttpService;
import com.gcy.util.GetLocalIP;
import com.gcy.util.MyApplication;
import com.gcy.util.PreferenceUtil;
import com.gcy.util.UnusualNumerical;
import com.gcy.view.CrossView;
import com.gcy.view.SlidingMenu;
import com.gcy.view.SweetAlertDialog;
import com.gcy.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import confige.Config;


public class MainActivity extends Activity {
    private TabHost tabHost;
    private TitleBar titleBar;
    private SlidingMenu sliding_menu;
    private Context context;
    private LinearLayout main_tap_myappliances;
    private LinearLayout main_tap_environment;
    private LinearLayout main_tap_chat;
    private ImageView view1;
    private ImageView view2;
    private ImageView view3;
    private TextView stop_monitoring_text;
    private Intent serviceIntent;
    private List<Float> mStandardList;
    private List<String> mLampList;
    private String TEMP;
    private float tempStandard;
    private float humiStandard;
    private float waterStandard;
    private float pm2_5Standard;
    private float dangStandard;

    private TextView innerTest;

    //tab1
    private TemporaryData temporaryData;
    private ListView myApplianceListView;
    private CrossView myappliances_add;
    private MyappliancesAdapter adapter;
    private List<Myappliances> myappliancesesList;
    private List<Myappliances> myappliancesesListClone;
    private boolean isDeivceOnline = true;


    /********    tab2     ******/
    private ListView main_tap_environment_listiew;
    private MyappliancesEnvironmentAdapter environment_adapter;
    private List<String> mDataList;
    private TextView onlineOrNot;
    private HttpService.MDataBinder mBinder;


    //tab3
    private Button openVideo;
    private LinearLayout noDisturb;
    private TextView noDisturbStatus;
    private CrossView crossView;


    //left menu
    private RelativeLayout config_frequency;
    private RelativeLayout applican_standard;
    private RelativeLayout stop_monitoring;
    private RelativeLayout about_us;

    private mReceive mr;
    private IntentFilter intentFilter;
    private String remoteIp = "";

    private Thread mMqttThread;


    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (HttpService.MDataBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_layout);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){        //全屏显示，通知栏背景设置
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        context = this;
        p = Phone.getInstance();




        temporaryData = TemporaryData.getInstance();        //保存临时数据
        mDataList = new ArrayList<>();          //保存环境监测数据的list


        //环境标准值
        tempStandard = PreferenceUtil.load(context,"tempStandard",Config.TEMPERATURE_STANDARD);
        humiStandard = PreferenceUtil.load(context,"humiStandard",Config.HUMIDITY_STANDARD);
        waterStandard = PreferenceUtil.load(context,"waterStandard",Config.WATER_STANDARD);
        pm2_5Standard = PreferenceUtil.load(context,"pm2_5Standard",Config.PM2_5_STANDARD);
        dangStandard = PreferenceUtil.load(context,"dangStandard",Config.DANGEROUS_GAS_STANDARD);

        mStandardList = new ArrayList<>();          //环境参数标准值

        mStandardList.add(tempStandard);
        mStandardList.add(humiStandard);
        mStandardList.add(waterStandard);
        mStandardList.add(pm2_5Standard);
        mStandardList.add(dangStandard);

        Intent bindIntent = new Intent(this,HttpService.class);//绑定服务
        bindService(bindIntent,conn,BIND_AUTO_CREATE);



        intentFilter = new IntentFilter();  //绑定接收广播
        intentFilter.addAction("com.smartHome.demo.GET_DATA");
        mr = new mReceive();

        registerReceiver(mr,intentFilter);

        initViewGroup();
        mLampList = new ArrayList<String>();
        serviceIntent = new Intent(this,HttpService.class);
        startService(serviceIntent);


        //设备
        myappliancesesList = new ArrayList<Myappliances>();

        myappliancesesListClone = new ArrayList<>();
        for (int i=0;i<Config.getApplicancesList().size();i++) {
            myappliancesesList.add(Config.getApplicancesList().get(i));
            myappliancesesListClone.add(Config.getApplicancesList().get(i));

        }


        Myappliances mpLamp = new Myappliances();//灯设备
        mpLamp.setAppliancesName("灯");
        mpLamp.setApplicansValue("null");
        mpLamp.setRegulation("是");
        mpLamp.setStatus("正常");
        myappliancesesListClone.add(mpLamp);

        Myappliances mpFan = new Myappliances();  //风扇设备
        mpFan.setAppliancesName("风扇");
        mpFan.setApplicansValue("null");
        mpFan.setRegulation("是");
        mpFan.setStatus("正常");
        myappliancesesListClone.add(mpFan);



        adapter = new MyappliancesAdapter(context,R.layout.activity_myappliance_item,myappliancesesListClone);
        environment_adapter = new MyappliancesEnvironmentAdapter(context,R.layout.activity_myappliance_environment_item,myappliancesesList);
        myApplianceListView.setAdapter(adapter);



        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("我的设备",getResources().getDrawable(R.mipmap.ic_launcher)).setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("环境卫生",getResources().getDrawable(R.mipmap.ic_launcher)).setContent(R.id.tab2));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("视频对讲",getResources().getDrawable(R.mipmap.ic_launcher)).setContent(R.id.tab3));



        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

            }
        });

        //   通过判断radioButton的点击 确定当前为那个tab
        tabHost.setCurrentTab(1);
        setCurrentTab(1);


        main_tap_myappliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabHost.setCurrentTab(0);
                setCurrentTab(0);

            }
        });
        main_tap_environment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabHost.setCurrentTab(1);
                setCurrentTab(1);

            }
        });
        main_tap_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabHost.setCurrentTab(2);
                setCurrentTab(2);

            }
        });


        /***************
         *
         *
         * 设备是否在线接口
         *
         * *************/
        p.setOnlineCheckCallBack(new OnlineCheckCallBack() {
            @Override
            public void online() {
                Message msg = new Message();
                msg.what = 7;
                handler.sendMessage(msg);
                isDeivceOnline = true;
            }

            @Override
            public void offline() {
                isDeivceOnline =false;
                Message msg = new Message();
                msg.what = 6;
                handler.sendMessage(msg);
            }
        });


        ///***********************************tab事件

        myappliances_add.setVisibility(View.GONE);



        myApplianceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!isDeivceOnline){
                    Toast.makeText(MainActivity.this, "设备离线，暂无法控制设备", Toast.LENGTH_SHORT).show();
                    return;
                }

                //水温
                if(position==2) {

                    Intent intent = new Intent(MainActivity.this, ApplicansWaterActivity.class);
                    startActivity(intent);
                }


                //灯
                if(position==5){
                    Intent intent = new Intent(MainActivity.this,ApplicansLampActivity.class);
                    startActivity(intent);


                }

                //风扇
                if(position==6){
                    Intent intent = new Intent(MainActivity.this,ApplicansFanActivity.class);
                    startActivity(intent);



                }



            }
        });

        titleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                if(titleBar.getActionView().equals("back"))
                    sliding_menu.openMenu();
                else
                    sliding_menu.closeMenu();
            }

            @Override
            public void onRightButtonClick(View v) {

            }
        });




        /**
         *
         * 设备是否在线
         *
         * */
        new Thread(isDeviceOnlineThread).start();

        //  监测频率设置
        config_frequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //p.pubishWater("25");
                Toast.makeText(MainActivity.this, "该功能暂时无实际效果", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this,ConfigActivity.class);
//                intent.putExtra(IntentKeyString.CONFIG_INTENT_KEY,IntentKeyString.CONFIG_INTENT_KEY_CONFIG_FREQUENCY);
//                startActivityForResult(intent,1);
            }
        });

        //环境参数标准值设置

        applican_standard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ConfigActivity.class);
                intent.putExtra(IntentKeyString.CONFIG_INTENT_KEY,IntentKeyString.CONFIG_INTENT_KEY_APPLICANS_STANDARD);
                intent.putExtra("tempStandard",tempStandard);
                intent.putExtra("humiStandard",humiStandard);
                intent.putExtra("waterStandard",waterStandard);
                intent.putExtra("pm2_5Standard",pm2_5Standard);
                intent.putExtra("dangStandard",dangStandard);
                startActivityForResult(intent,2);
            }
        });




        //停止监测

        stop_monitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stop_monitoring_text.getText().toString().equals("停止监测")){

                    mBinder.startMonitoring(false);
                    stop_monitoring_text.setText("开始监测");
                    PreferenceUtil.save(MainActivity.this,"MONITORING",false);}
                else{
                    mBinder.startMonitoring(true);
                    stop_monitoring_text.setText("停止监测");
                    PreferenceUtil.save(MainActivity.this,"MONITORING",true);
                }
            }
        });

        //关于我们
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ConfigActivity.class);
                intent.putExtra(IntentKeyString.CONFIG_INTENT_KEY,IntentKeyString.CONFIG_INTENT_KEY_ABOUT_US);
                startActivity(intent);
            }
        });


        main_tap_environment_listiew.setAdapter(environment_adapter);



        main_tap_environment_listiew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


            //打开视频！
        openVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.ControlPhoneIP(GetLocalIP.getLocalIpAddress());
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                MyApplication.progressDialog = progressDialog;
                progressDialog.setTitle("请稍后...");
                progressDialog.setMessage("正在连接...");
                progressDialog.setCancelable(true);
                progressDialog.show();





            }
        });


        //设置消息免打扰
        noDisturb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noDisturbStatus.getText().toString().equals("关闭")){
                    crossView.toggle(500);
                    mBinder.setReadyToVideo(false);
                    noDisturbStatus.setText("开启");
                    PreferenceUtil.save(MainActivity.this,"DISTURB",true);
                }else{
                    crossView.toggle(500);
                    mBinder.setReadyToVideo(true);
                    noDisturbStatus.setText("关闭");
                    PreferenceUtil.save(MainActivity.this,"DISTURB",false);
                }

            }
        });

        main_tap_environment_listiew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        //p.sendGetLightInfoConmand();
        Message msg = new Message();
        msg.what = 5;
        handler.sendMessage(msg);
    }


    private void initViewGroup() {

        titleBar = (TitleBar) findViewById(R.id.title_bar);
        sliding_menu =(SlidingMenu) findViewById(R.id.sliding_menu);
        titleBar.initTitleBarInfo("",-1,-1,"","");
        titleBar.setLeftContainerClickAble(true);
        main_tap_myappliances = (LinearLayout)findViewById(R.id.main_tap_myappliances);
        main_tap_environment = (LinearLayout)findViewById(R.id.main_tap_environment);
        main_tap_chat = (LinearLayout)findViewById(R.id.main_tap_chat);
        view2 =(ImageView) findViewById(R.id.main_tap_environment_image);
        view3 =(ImageView) findViewById(R.id.main_tap_chat_image);
        view1 =(ImageView) findViewById(R.id.main_tap_myappliances_image);
        onlineOrNot = (TextView)findViewById(R.id.online_or_not);
        myappliances_add = (CrossView)findViewById(R.id.layout_myappliances_button_add);
        myApplianceListView = (ListView)findViewById(R.id.layout_myappliances_lv);

        main_tap_environment_listiew = (ListView) findViewById(R.id.main_tap_environment_listiew);


        config_frequency = (RelativeLayout) findViewById(R.id.config_frequency);
        applican_standard = (RelativeLayout) findViewById(R.id.applican_standard);
        stop_monitoring = (RelativeLayout) findViewById(R.id.stop_monitoring);
        about_us = (RelativeLayout) findViewById(R.id.about_us);

        openVideo = (Button)findViewById(R.id.layout_main_open_video);
        noDisturb = (LinearLayout)findViewById(R.id.layout_main_no_video);
        noDisturbStatus = (TextView)findViewById(R.id.layout_main_no_video_text) ;
        crossView = (CrossView) findViewById(R.id.layout_main_no_video_crossview);
        innerTest = (TextView) findViewById(R.id.inner_test);

        stop_monitoring_text = (TextView) findViewById(R.id.stop_monitoring_text);
        innerTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlineOrNot.setVisibility(View.GONE);
            }
        });
        innerTest.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onlineOrNot.setVisibility(View.VISIBLE);
                return false;
            }
        });


    }

    public void setCurrentTab(int id){
        view2.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.widget_bar_explore_nor));
        view3.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.widget_bar_me_nor));
        view1.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.widget_bar_news_nor));
        switch (id){
            case 0:
                view1.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.widget_bar_news_over));
                break;
            case 1:
                view2.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.widget_bar_explore_over));
                break;
            case 2:
                view3.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.widget_bar_me_over));
                break;

        }
    }

    private Phone p;
    private String tempData="",humiData="",waterData="",pmData="",dangData="";


        Runnable isDeviceOnlineThread = new Runnable() {  //检查设备是否在线
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(isDeviceOnlineThread,7000);    //每十秒发送一次信息以确认设备是否在线
                p.sendGetLightInfoConmand();
            }
        };
//    Runnable mMqttRunnable = new Runnable() {
//        @Override
//        public void run() {
//            p = Phone.getInstance();
//            p.setMqttCallBack(new MqttCallback() {
//                @Override
//                public void connectionLost(Throwable throwable) {
//
//                }
//
//                @Override
//                public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
//                    Message msg = new Message();
//
//                    switch(arg0){
//
//                        //手机发送IP，树莓派应答后，建立视频连接
//                        case CommonInfo.RaspberrypiIpAnswerSubString:
//                            System.out.println("接受到树莓派应答IP为："+new String(arg1.getPayload()));
//                            //建立视频连接
//                            //p.ConnectionVideo();
//                            msg.what = OpenVideoDirector;
//                            remoteIp = new String(arg1.getPayload());
//                            break;
//
//                        //树莓派发送IP后，手机发送IP应答，建立视频连接
//                        case CommonInfo.RaspberrypiIpSubString:
//                            System.out.println("接收到树莓派的IP为："+new String(arg1.getPayload()));
//                            //发送手机IP
//                            p.AnswerPhoneIP();
//                            //建立视频链接
//                            msg.what = OpenVideoByDialog;
//                            remoteIp = new String(arg1.getPayload());
////                            p.ConnectionVideo();
//                            break;
//
//                        case CommonInfo.Stm32GasStaSubString:
//                            //System.out.println("接收到危险气体为："+new String(arg1.getPayload()));
//                            //p.DeviceStatu(CommonInfo.gas, new String(arg1.getPayload()));
//                            dangData = new String(arg1.getPayload());
//                            msg.what = UpDataSuccess;
//                            break;
//
//                        case CommonInfo.Stm32HumiStaSubString:
//                            //System.out.println("接收到HUMI为"+new String(arg1.getPayload()));
//                            //p.DeviceStatu(CommonInfo.humi, new String(arg1.getPayload()));
//                            humiData = new String(arg1.getPayload());
//                            msg.what = UpDataSuccess;
//                            break;
//
//                        case CommonInfo.Stm32PmStaSubString:
////                            System.out.println("接收到PM为："+new String(arg1.getPayload()));
////                            p.DeviceStatu(CommonInfo.pm, new String(arg1.getPayload()));
//                            pmData = new String(arg1.getPayload());
//                            msg.what = UpDataSuccess;
//                            break;
//
//                        case CommonInfo.Stm32WaterStaSubString:
//
////                            System.out.println("接收到水温为："+new String(arg1.getPayload()));
////                            p.DeviceStatu(CommonInfo.water, new String(arg1.getPayload()));
//                            Log.e("接收到水温为",new String(arg1.getPayload()));
//                            waterData = new String(arg1.getPayload());
//                            MSG = "水温是"+waterData;
//                            msg.what = ToastSubInt;
//                            break;
//
//                        case CommonInfo.Stm32TempStaSubString:
////                            System.out.println("接收到Temp为："+new String(arg1.getPayload()));
////                            p.DeviceStatu(CommonInfo.temp, new String(arg1.getPayload()));
//                            tempData = new String(arg1.getPayload());
//                            msg.what = UpDataSuccess;
//                            break;
//
//                        case CommonInfo.Stm32AnswerSubString:
//                            System.out.println("STM32控制信息发送成功");
//                            MSG = "控制信息已发送！";
//                            msg.what = ToastSubInt;
//                            break;
//
//                        case CommonInfo.VideoStopAnswerSubString:
//                            System.out.println("视频停止应答");
//                            //中断视频连接
//                            p.VideoStop();
//                            MSG = "视频链接已断开！";
//                            msg.what = ToastSubInt;
//                            break;
//
//                        //灯
//                        case CommonInfo.Stm32Light0SubString:
//                            p.DeviceStatu(CommonInfo.light0, new String(arg1.getPayload()));
//                            break;
//
//                        case CommonInfo.Stm32Light1SubString:
//                            p.DeviceStatu(CommonInfo.light1, new String(arg1.getPayload()));
//                            break;
//                        case CommonInfo.Stm32Light2SubString:
//                            p.DeviceStatu(CommonInfo.light2, new String(arg1.getPayload()));
//                            break;
//                        case CommonInfo.Stm32Light3SubString:
//                            p.DeviceStatu(CommonInfo.light3, new String(arg1.getPayload()));
//                            break;
//                    }
//                    handler.sendMessage(msg);
//                }
//
//                @Override
//                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//
//                }
//            });
//            p.Connect();
//        }
//    };

    private String MSG = "";

    //Toast
    private final int ToastSubInt = 9;

    //手机发送IP，树莓派应答后，建立视频连接
    private final int OpenVideoDirector = 0;

    //树莓派发送IP后，手机发送IP应答，建立视频连接
    private final int OpenVideoByDialog = 1;

    /*****危险气体*****/
    private final int UpDataSuccess = 2;





    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case ToastSubInt:
                    Toast.makeText(MainActivity.this, MSG, Toast.LENGTH_SHORT).show();
                    break;
                case OpenVideoDirector:
                    Intent intent = new Intent(MainActivity.this, Gstreamer_test2.class);
                    intent.putExtra(IntentKeyString.REMOTE_VIDEO_IP,remoteIp);
                    startActivity(intent);

                    break;
                case OpenVideoByDialog:
                    Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    long [] pattern = {400,500,400,500};
                    vibrator.vibrate(pattern,-1);
                    final SweetAlertDialog waring = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    waring.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    waring.setTitleText("有人敲门!")
                            .setContentText(MSG)
                            .setConfirmText("确定")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Intent intent = new Intent(getApplicationContext(),Gstreamer_test2.class);
                                    intent.putExtra(IntentKeyString.REMOTE_VIDEO_IP,remoteIp);
                                    startActivity(intent);
                                    waring.dismiss();
                                }
                            })
                            .showCancelButton(true)
                            .setCancelText("取消")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {


                                    waring.dismiss();
                                }
                            })
                            .show();


                    break;
                case UpDataSuccess:
                    mDataList.clear();
                    mDataList.add(tempData);
                    mDataList.add(humiData);
                    mDataList.add(waterData);
                    mDataList.add(pmData);
                    mDataList.add(dangData);
                    Log.e("UpData Success:"," _ "+humiData+" _ "+waterData+" _ "+pmData+" _ "+dangData);

                    for(int i=0;i<Config.getApplicancesList().size();i++) {
                        myappliancesesList.get(i).setApplicansValue(mDataList.get(i));
                    }
                    environment_adapter.notifyDataSetChanged();
                    break;
                case 4:
                    crossView.toggle(500);
                    break;
                case 5:
                    if(PreferenceUtil.load(MainActivity.this,"DISTURB",false)){
                        noDisturbStatus.setText("开启");
                        crossView.toggle(500);
                    }else{
                        noDisturbStatus.setText("关闭");
                    }
                    if(PreferenceUtil.load(MainActivity.this,"MONITORING",true)){
                        stop_monitoring_text.setText("停止监测");
                    }else{

                        stop_monitoring_text.setText("开始监测");
                    }
                    break;
                case 6:
                    onlineOrNot.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!p.isConnect())
                                p.Connect();
                        }
                    }).start();
                    Log.e("MainActivity","服务器重新连接！");
                    //p.sendGetLightInfoConmand();
                    break;
                case 7:
                    onlineOrNot.setVisibility(View.GONE);
                    break;

            }
        }
    };


    @Override
    protected void onDestroy() {
        unbindService(conn);
        unregisterReceiver(mr);
        super.onDestroy();
    }

    class mReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("onReceive: ","-->接收到广播" );

            mDataList.clear();

            List<String> mServiceDatalist = mBinder.getEnvironmentApplicansData();
            if(mServiceDatalist.size()!=5)
                return;
            for(int i=0;i<mServiceDatalist.size();i++){
                mDataList.add(mServiceDatalist.get(i));
            }


            handler.post(new Runnable() {
                @Override
                public void run() {

                    List<Boolean> isNormal= UnusualNumerical.isNumericalNormal(mDataList,mStandardList);

                    for(int i=0;i<Config.getApplicancesList().size();i++){
                        myappliancesesList.get(i).setApplicansValue(mDataList.get(i));
                        if(!isNormal.get(i))
                            myappliancesesList.get(i).setNormal(false);
                        else
                            myappliancesesList.get(i).setNormal(true);
                    }

                    TEMP = mDataList.get(0);
                    environment_adapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==1){
            int frequency;
            frequency=data.getIntExtra("MONITORING_FREQUENCY",Config.MONITORING_FREQUENCY);
            mBinder.setMonitoringFrequency(frequency);
            PreferenceUtil.save(context,"MONITORING_FREQUENCY",frequency);

        }
        if (resultCode==2){

            tempStandard = data.getFloatExtra("tempStandard",Config.TEMPERATURE_STANDARD);
            humiStandard = data.getFloatExtra("humiStandard",Config.HUMIDITY_STANDARD);
            waterStandard = data.getFloatExtra("waterStandard",Config.WATER_STANDARD);
            pm2_5Standard = data.getFloatExtra("pm2_5Standard",Config.PM2_5_STANDARD);
            dangStandard = data.getFloatExtra("dangStandard",Config.DANGEROUS_GAS_STANDARD);

            mStandardList.clear();

            mStandardList.add(tempStandard);
            mStandardList.add(humiStandard);
            mStandardList.add(waterStandard);
            mStandardList.add(pm2_5Standard);
            mStandardList.add(dangStandard);

            PreferenceUtil.save(context,"tempStandard",tempStandard);
            PreferenceUtil.save(context,"humiStandard",humiStandard);
            PreferenceUtil.save(context,"waterStandard",waterStandard);
            PreferenceUtil.save(context,"pm2_5Standard",pm2_5Standard);
            PreferenceUtil.save(context,"dangStandard",dangStandard);

            mBinder.setStandardConfig(mStandardList);



        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
