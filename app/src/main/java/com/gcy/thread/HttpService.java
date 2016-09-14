package com.gcy.thread;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;

import com.example.changvvb.gstreamer3.Gstreamer_test2;
import com.gcy.beans.IntentKeyString;
import com.gcy.beans.TemporaryData;
import com.gcy.mqttUtil.CommonInfo;
import com.gcy.mqttUtil.Phone;
import com.gcy.util.MyApplication;
import com.gcy.util.PreferenceUtil;
import com.gcy.view.SweetAlertDialog;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import confige.Config;

/**
 * Created by Mr.G on 2016/5/22.
 */
public class HttpService extends Service {

    private boolean servicealive = true;                    //服务销毁标志位   结束本服务的所有线程
    private boolean isAliving = true;                       //获取环境参数的标志位  其为false时app停止向服务器接收环境参数
    private boolean isReadyToVideo = true;                  //设置免打扰的标志位   其为true时  树莓派端点击门铃 app不做响应
    private TemporaryData temporaryData;                    //临时全局变量  储存一些临时需要的数据  实现类为单例模式

    private StringBuffer sb;                                //缓存从服务器接受的数据
    private String temp = "null";                                    //  拆分缓存中的温度数据
    private String humi = "null";                                    //  拆分缓存中的湿度数据
    private String water = "null";
    private String pm = "null";
    private String dang = "null";
    private String lamp1;
    private String lamp2;
    private String lamp3;
    private String lamp4;

    private float tempStandard;                             //      五个环
    private float humiStandard;                             //            境参数的
    private float waterStandard;                            //                     标准值
    private float pmStandard;                               //
    private float dangStandard;                             //
    private boolean tempNormal=true;                        //温度判断标志位
    private boolean humiNormal=true;                        //湿度判断标志位
    private boolean waterNormal=true;                       //水温判断标志位
    private boolean pmNormal=true;                          //pm2.5参数判断标志位
    private boolean dangNormal=true;                        //危险气体判断标志位
    private boolean serviceNormal = true;                   //判断服务器发来的数据是否有误
    private int monitoringFrequency;                        //环境参数监测频率



    private Thread EnvironmentThread;                       //从服务器获取环境数据的线程
    private Thread IPThread;                                //向服务器发送ip的线程

    private List<String> mList;                             //装载环境参数信息
    private List<String> mLampList;                         //装载灯信息状态
    private MDataBinder mdb = new MDataBinder();
    private boolean isAlerting = false;                     //消息框是否已弹出
    private boolean isVideo = false;                        //视频是否已打开
    private String MSG = "";                                //消息框弹出的内容





    @Override
    public IBinder onBind(Intent intent) {
        return mdb;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        servicealive = false;
        if(EnvironmentThread!=null){
            EnvironmentThread.interrupt();
            EnvironmentThread = null;
            }
        if(IPThread!=null){
            IPThread.interrupt();
            IPThread = null;
        }

        EnvironmentThread = new Thread(mHttpRunnable);
        //IPThread = new Thread(mIPRunnable);

        servicealive = true;

        EnvironmentThread.start();

        //IPThread.start();

        servicealive = true;



        /*********
         *
         * 读取用户设置的环境参数标准值
         *
         * *******/
        tempStandard = PreferenceUtil.load(getApplicationContext(),"tempStandard",Config.TEMPERATURE_STANDARD);
        humiStandard = PreferenceUtil.load(getApplicationContext(),"humiStandard",Config.HUMIDITY_STANDARD);
        waterStandard = PreferenceUtil.load(getApplicationContext(),"waterStandard",Config.WATER_STANDARD);
        pmStandard = PreferenceUtil.load(getApplicationContext(),"pmStandard",Config.PM2_5_STANDARD);
        dangStandard = PreferenceUtil.load(getApplicationContext(),"dangStandard",Config.DANGEROUS_GAS_STANDARD);
        monitoringFrequency = PreferenceUtil.load(getApplicationContext(),"monitoringFrequency",Config.MONITORING_FREQUENCY);
        temporaryData = TemporaryData.getInstance();

        isAliving = true;
        isReadyToVideo = PreferenceUtil.load(getApplicationContext(),"ReadyToVideo",true);
        isAliving = PreferenceUtil.load(getApplicationContext(),"Monitoring",true);
        sb = new StringBuffer();
        mList = new ArrayList<String>();
        mLampList = new ArrayList<String>();




        return super.onStartCommand(intent, flags, startId);
    }


    private Phone p;
    private String remoteIp;
    //环境监测线程
    Runnable mHttpRunnable = new Runnable() {
        @Override
        public void run() {

            if(MyApplication.mMqttClient!=null)
                try {
                    MyApplication.mMqttClient.disconnect();
                    //MyApplication.mMqttClient.close();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            p = Phone.getInstance();
            p.setMqttCallBack(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Log.e("HttpSerivce","重新连接...");
                    p.Connect();

//                    Message msg = new Message();
//                    MSG = "请检查网络";
//                    msg.what = OpenDialog;
//                    mHandler.sendMessage(msg);
                }

                @Override
                public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
                    Message msg = new Message();
                    //if(isAliving)
                        switch(arg0){

                            //手机发送IP，树莓派应答后，建立视频连接
                            case CommonInfo.RaspberrypiIpAnswerSubString:
                                //System.out.println("接受到树莓派应答IP为："+new String(arg1.getPayload()));
                                //建立视频连接
                                msg.what = OpenVideoDirector;
                                remoteIp = new String(arg1.getPayload());
                                break;

                            //树莓派发送IP后，手机发送IP应答，建立视频连接
                            case CommonInfo.RaspberrypiIpSubString:
                                //System.out.println("接收到树莓派的IP为："+new String(arg1.getPayload()));
                                //发送手机IP
                                p.AnswerPhoneIP();
                                //建立视频链接
                                msg.what = OpenVideoByDialog;
                                remoteIp = new String(arg1.getPayload());
    //                            p.ConnectionVideo();
                                break;

                            case CommonInfo.Stm32GasStaSubString:
                                //System.out.println("接收到危险气体为："+new String(arg1.getPayload()));
                                //p.DeviceStatu(CommonInfo.gas, new String(arg1.getPayload()));
                                dang  = "".equals(new String(arg1.getPayload()))?"null":new String(arg1.getPayload());
                                msg.what = UpDataSuccess;
                                break;

                            case CommonInfo.Stm32HumiStaSubString:
                                //System.out.println("接收到HUMI为"+new String(arg1.getPayload()));
                                //p.DeviceStatu(CommonInfo.humi, new String(arg1.getPayload()));
                                humi  = "".equals(new String(arg1.getPayload()))?"null":new String(arg1.getPayload());
                                msg.what = UpDataSuccess;
                                break;

                            case CommonInfo.Stm32PmStaSubString:
    //                            System.out.println("接收到PM为："+new String(arg1.getPayload()));
    //                            p.DeviceStatu(CommonInfo.pm, new String(arg1.getPayload()));
                                pm  = "".equals(new String(arg1.getPayload()))?"null":new String(arg1.getPayload());
                                msg.what = UpDataSuccess;
                                break;

                            case CommonInfo.Stm32WaterStaSubString:
                                water = "".equals(new String(arg1.getPayload()))?"null":new String(arg1.getPayload());
                                msg.what = UpDataSuccess;
                                break;

                            case CommonInfo.Stm32TempStaSubString:
    //                            System.out.println("接收到Temp为："+new String(arg1.getPayload()));
    //                            p.DeviceStatu(CommonInfo.temp, new String(arg1.getPayload()));
                                temp = "".equals(new String(arg1.getPayload()))?"null":new String(arg1.getPayload());
                                msg.what = UpDataSuccess;
                                break;

                            case CommonInfo.Stm32AnswerSubString:
                                MSG = "控制信息已发送！";
                                msg.what = ToastSubInt;
                                break;

                            case CommonInfo.VideoStopAnswerSubString:
                                //中断视频连接
                                MSG = "视频链接已断开！";
                                msg.what = VideoStop;
                                break;

                            //灯
                            case CommonInfo.Stm32Light0SubString:
                                p.DeviceStatu(CommonInfo.light0, new String(arg1.getPayload()));
                                MyApplication.isOnline = true;
                                break;

                            case CommonInfo.Stm32Light1SubString:
                                p.DeviceStatu(CommonInfo.light1, new String(arg1.getPayload()));
                                MyApplication.isOnline = true;
                                break;
                            case CommonInfo.Stm32Light2SubString:
                                p.DeviceStatu(CommonInfo.light2, new String(arg1.getPayload()));
                                MyApplication.isOnline = true;
                                break;
                            case CommonInfo.Stm32Light3SubString:
                                p.DeviceStatu(CommonInfo.light3, new String(arg1.getPayload()));
                                MyApplication.isOnline = true;
                                break;

                            //设备在线
                            case CommonInfo.ApplicansStatues:

                                break;
                        }
                    mHandler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
            p.Connect();

        }
    };
            //检查从服务器接受的参数是否异常  以及是否有错误数据

    private boolean CheckNormal = true;
    private boolean isCheckNormal() {
        boolean isUnusual = false;
        boolean maches = true;    //   辅助检查接受参数是否为数字或设备离线   null为离线
        MSG="";

        if(mList.size()!=5){
            MSG= "参数个数异常！->";
            return true;}

        if(!temp.matches("\\d+\\.*\\d*")&&!temp.equals("null"))
            maches = false;
        if(!humi.matches("\\d+\\.*\\d*")&&!humi.equals("null"))
            maches = false;
        if(!water.matches("\\d+\\.*\\d*")&&!water.equals("null"))
            maches = false;
        if(!pm.matches("\\d+\\.*\\d*")&&!pm.equals("null"))
            maches = false;
        if(!dang.matches("\\d+\\.*\\d*")&&!dang.equals("null"))
            maches = false;
        if(!maches){
            if(serviceNormal){
                MSG="接受参数非数字，请检查服务器！";
                serviceNormal=false;
                return true;
            }
        }else{
            serviceNormal = true;
        }
        if(!temp.equals("null")&&temp.matches("\\d+\\.*\\d*"))
            if(Float.parseFloat(temp)>tempStandard){
                if(tempNormal){
                    isUnusual = true;
                    MSG = MSG+"温度异常！\n";
                    tempNormal=false;
            }
        }else{
                tempNormal = true;
            }


        if(!humi.equals("null")&&humi.matches("\\d+\\.*\\d*"))
            if(Float.parseFloat(humi)>humiStandard){
                if(humiNormal){
                    isUnusual = true;
                    MSG = MSG+"湿度异常！\n";
                    humiNormal=false;
            }
        }else{
                humiNormal = true;
            }

        if (!water.equals("null")&&water.matches("\\d+\\.*\\d*"))
            if (Float.parseFloat(water) > waterStandard) {
                if(waterNormal) {
                    isUnusual = true;
                    MSG = MSG + "水温异常！\n";
                    waterNormal = false;
                }
        }else{
                waterNormal = true;
            }

        if(!pm.equals("null")&&pm.matches("\\d+\\.*\\d*"))
            if(Float.parseFloat(pm)>pmStandard){
                if(pmNormal){
                    isUnusual = true;
                    pmNormal = false;
                    MSG = MSG+"PM2.5异常！\n";}
        }else{
                pmNormal = true;
            }

        if(!dang.equals("null")&&dang.matches("\\d+\\.*\\d*"))
            if(Float.parseFloat(dang)>dangStandard){
                if(dangNormal){
                    isUnusual = true;
                    MSG = MSG+"危险气体异常！\n";
                    dangNormal = false;
                }
            }else{
                dangNormal = true;
            }

        if(isUnusual){
            return true;
        }

        return false;
    }
    private final int UpDataSuccess = 0;
    private final int ToastSubInt = 9;
    private final int OpenVideoDirector = 1;
    private final int OpenVideoByDialog = 2;
    private final int EnvironmentUnnormal = 3;
    private final int VideoStop = 4;
    private final int OpenDialog = 5;
    private boolean stopOnce = true;

    Handler mHandler = new Handler()                //服务中开启dialog  提示进行视频 或 弹出环境异常警告
    {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if(msg.what==EnvironmentUnnormal)
            {
                if(!isAlerting) {
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                        vibrator.vibrate(1000);

                        isAlerting=true;

                        final SweetAlertDialog waring = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.WARNING_TYPE);
                        waring.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        waring.setTitleText("环境参数异常!")
                                .setContentText(MSG)
                                .setConfirmText("确定")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        isAlerting = false;
                                        waring.dismiss();
                                    }
                                })
                                .show();
                }

            }
            if(msg.what==OpenVideoByDialog)
                if(isReadyToVideo) {
                    {
                        isReadyToVideo = false;
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        long[] pattern = {400, 500, 400, 500};
                        vibrator.vibrate(pattern, -1);
                        final SweetAlertDialog waring = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.SUCCESS_TYPE);
                        waring.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        waring.setTitleText("有人敲门!")
                                .setContentText(MSG)
                                .setConfirmText("打开视频")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        isVideo = false;
                                        Intent intent = new Intent(getApplicationContext(), Gstreamer_test2.class);
                                        intent.putExtra(IntentKeyString.REMOTE_VIDEO_IP, remoteIp);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        isReadyToVideo = true;
                                        waring.dismiss();

                                    }
                                })
                                .showCancelButton(true)
                                .setCancelText("开门")
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        isVideo = false;
                                        p.OpenTheDoor();
                                        isReadyToVideo = true;
                                        waring.dismiss();

                                    }
                                })
                                .show();
                    }
                }
            if(msg.what==OpenVideoDirector){
                if(MyApplication.progressDialog!=null)
                    MyApplication.progressDialog.dismiss();
                MyApplication.progressDialog=null;

                    Intent intent = new Intent(getApplicationContext(),Gstreamer_test2.class);
                    intent.putExtra(IntentKeyString.REMOTE_VIDEO_IP,remoteIp);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            if(msg.what==VideoStop){
                if(stopOnce)
                if(Config.mActivity!=null){
                    stopOnce = false;
                    if(Config.mActivity!=null)
                        Config.mActivity.finish();
                    if(Config.mSoundActivity!=null)
                        Config.mSoundActivity.finish();
                    final SweetAlertDialog waring = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.SUCCESS_TYPE);
                    waring.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    waring.setTitleText("通知")
                            .setContentText("视频通信结束...")
                            .setConfirmText("确定")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    stopOnce = true;
                                    waring.dismiss();
                                }
                            })
                            .showCancelButton(false)
                            .show();
                }

            }
            if(msg.what==ToastSubInt){
                //Toast.makeText(HttpService.this, MSG, Toast.LENGTH_SHORT).show();


            }
            if(msg.what==UpDataSuccess){
                if(isAliving){
                    mList.clear();
                    mList.add(temp);
                    mList.add(humi);
                    mList.add(water);
                    mList.add(pm);
                    mList.add(dang);


                    if(isCheckNormal()){
                        if(CheckNormal){
                            if(!isAlerting){
                                CheckNormal = false;
                                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(1000);

                                final SweetAlertDialog waring = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.WARNING_TYPE);
                                waring.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                waring.setTitleText("环境参数异常!")
                                        .setContentText(MSG)
                                        .setConfirmText("确定")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                waring.dismiss();
                                            }
                                        })
                                        .show();}}
                    }else {
                        CheckNormal = true;
                    }
                        Intent intent = new Intent("com.smartHome.demo.GET_DATA");
                        sendBroadcast(intent);}
            }
            if(msg.what==OpenDialog){
                final SweetAlertDialog waring = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.WARNING_TYPE);
                waring.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                waring.setTitleText("服务器连接失败!")
                        .setContentText(MSG)
                        .setConfirmText("确定")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                waring.dismiss();
                            }
                        })
                        .show();
            }

            super.handleMessage(msg);
        }
    };
            //与该服务绑定的activity将获取该类的实例  以便进行activity与service通讯
    public class MDataBinder extends Binder{

            //返回环境参数数据至活动界面
        public List<String> getEnvironmentApplicansData(){
            return mList;
        }



        public void setStandardConfig(List<Float> mList){
            if(mList.size()!=5)
                return;
            tempStandard=mList.get(0);
            humiStandard=mList.get(1);
            waterStandard=mList.get(2);
            pmStandard=mList.get(3);
            dangStandard=mList.get(4);
            /********
             *
             * 获取用户输入的各个参数值标准并保存
             *
             *
             * *******/
            PreferenceUtil.save(getApplicationContext(),"tempStandard",tempStandard);
            PreferenceUtil.save(getApplicationContext(),"humiStandard",humiStandard);
            PreferenceUtil.save(getApplicationContext(),"waterStandard",waterStandard);
            PreferenceUtil.save(getApplicationContext(),"pmStandard",pmStandard);
            PreferenceUtil.save(getApplicationContext(),"dangStandard",dangStandard);


        }


                /******设置监测频率******/
        public void setMonitoringFrequency(int value){
            monitoringFrequency = value;
            PreferenceUtil.save(getApplicationContext(),"dangStandard",monitoringFrequency);

        }
                /******是否开始监测环境******/
        public void startMonitoring(boolean command){
            isAliving = command;
            PreferenceUtil.save(getApplicationContext(),"Monitoring",isAliving);

        }
                /*******视频通话是否正在进行中*******/
        public void setReadyToVideo(boolean flag){
            isReadyToVideo = flag;
            PreferenceUtil.save(getApplicationContext(),"ReadyToVideo",isReadyToVideo);
        }
    }

    @Override
    public void onDestroy() {
        servicealive = false;
        isAliving = false;
        isReadyToVideo = false;
        p.disConnect();
        EnvironmentThread.interrupt();
        EnvironmentThread = null;
        super.onDestroy();
    }




}
