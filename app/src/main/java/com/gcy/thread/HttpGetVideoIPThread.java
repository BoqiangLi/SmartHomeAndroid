package com.gcy.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.example.changvvb.gstreamer3.Gstreamer_test2;
import com.gcy.beans.IntentKeyString;
import com.gcy.beans.TemporaryData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import confige.Config;

/**
 * Created by Mr.G on 2016/5/26.
 *
 * 向服务器发送消息
 *
 */
public class HttpGetVideoIPThread implements Runnable {
    private String message="null";
    private ProgressDialog progressDialog;
    private Context activity;
    private boolean activityOrService;     //Service启动activity需要加标志位   如果是服务 该标志位为true
    private TemporaryData td;

    public HttpGetVideoIPThread(Context activity,ProgressDialog progressDialog,boolean activityOrService,TemporaryData td){
        this.activity = activity;
        this.activityOrService = activityOrService;
        this.progressDialog = progressDialog;
        this.td = td ;
    }


    @Override
    public void run() {
        boolean flag = true;
        int count = 0;
        while(flag)
            try {




                URL httpUrl = new URL(Config.URL);
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.connect();


                BufferedWriter bw  = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                bw.write("SEND_IP_TO_ME");
                bw.flush();
                bw.close();
                if (conn.getResponseCode()==200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String str1;

                    while ((str1 = reader.readLine()) != null) {
                        message=str1;
                    }
                    td.setIP(message);

                    reader.close();
                }

                    conn.disconnect();
                flag = false;
                if(!message.equals("NO_DEVICE")){
                if(message.equals("0.0.0.0")){
                    flag = true;
                    try {
                        count++;
                        if(count>3) {
                            if(progressDialog!=null)
                                progressDialog.dismiss();
                            flag = false;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else {
                    if(progressDialog!=null)
                    progressDialog.dismiss();
                    if(message.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")){

                        Intent intent = new Intent(activity, Gstreamer_test2.class);
                        if(activityOrService)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(IntentKeyString.REMOTE_VIDEO_IP,message);
                        activity.startActivity(intent);
                    }else{

                    }
                }}else{

                    flag = false;
                    if(progressDialog!=null)
                        progressDialog.dismiss();

                }






            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }



    }
}
