package com.gcy.util;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;

/**
 * Created by Mr.G on 2016/6/14.
 */
public class MyApplication extends Application {
    private static Context context;
    public static MqttClient mMqttClient;
    public static ProgressDialog progressDialog;
    public static boolean isOnline = false;
    public static MqttCallback mbc;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        //super.onCreate();
    }
    public static Context getContext() {
        return context;
    }

}
