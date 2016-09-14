package com.gcy.thread;

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
public class HttpThread implements Runnable {
    private String values;

    public HttpThread(String values){
        this.values = values;

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


                BufferedWriter bw  = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                bw.write(values);
                bw.flush();
                bw.close();
                if (conn.getResponseCode()==200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String str1;
                    StringBuffer message = new StringBuffer();
                    while ((str1 = reader.readLine()) != null) {
                        message.append(str1);
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
            }



    }
}
