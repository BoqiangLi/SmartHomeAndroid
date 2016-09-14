package com.gcy.beans;

import android.app.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.G on 2016/6/5.
 */
public class TemporaryData {
    private static TemporaryData temporaryData;


    public static String IP_CLIENT;


    private TemporaryData(){
        temporaryData = this;
    }


    public static TemporaryData getInstance(){
        if(temporaryData==null)
            new TemporaryData();
        return temporaryData;
    }

    public boolean isVideoNow  = false;

    public List<String> mLampList;

    public List<String> getmLampList(){
        return mLampList;
    }

    public void setmLampList(List <String> lampList){
        if(mLampList==null)
            mLampList = new ArrayList<>();
        mLampList.clear();
        if(lampList.size()!=4)
            return;
        for(int i=0;i<lampList.size();i++){
            this.mLampList.add(lampList.get(i));

        }
    }

    public boolean loginSuccess;
    public void setLoginSuccess(boolean flag){
        loginSuccess = flag;
    }

    public boolean getLoginSuccess(){

        return loginSuccess;
    }

    public String IP = "";

    private boolean ip_status = false;

    public boolean isIp_status() {
        return ip_status;
    }

    private ProgressDialog progressDialog;

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public void setIp_status(boolean ip_status) {
        this.ip_status = ip_status;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setFlagIsVideo(boolean flag){
        isVideoNow = flag;

    }
    public boolean getFlagIs(){

        return isVideoNow;
    }
}
