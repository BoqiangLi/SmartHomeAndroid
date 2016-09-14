package com.gcy.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gcy.util.MyApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import activity.gcy.com.demo.MainActivity;
import confige.Config;

/**
 * Created by Mr.G on 2016/5/26.
 *
 * 向服务器发送消息
 *
 */
public class HttpGetLoginThread implements Runnable {
    private String message="";
    private String values;
    private ProgressDialog progressDialog;
    private Context context;


    public HttpGetLoginThread(ProgressDialog progressDialog, String values, Context context){
        this.progressDialog = progressDialog;
        this.values = values;
        this.context = context;

    }

    @Override
    public void run() {

        try {


            URL httpUrl = new URL(Config.URL);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();


            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

            bw.write(values);
            bw.flush();
            bw.close();
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str1;

                while ((str1 = reader.readLine()) != null) {
                    message = str1;
                }


                reader.close();
            }

            conn.disconnect();




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (message.equals("success")) {
                progressDialog.dismiss();
                Intent intent = new Intent(context,MainActivity.class);
                context.startActivity(intent);


            }else{
                Toast.makeText(MyApplication.getContext(), "登陆失败检查用户名和密码！", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();


            }
        }


    }
}
