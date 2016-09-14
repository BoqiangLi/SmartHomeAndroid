package com.gcy.util;

/**
 * Created by Mr.G on 2016/5/15.
 */
public class DataOperation {
    public static String[] getApplicanData(String data){
        return data.split("_");
    }
    public static String[] getDetailApplicanData(String data){
        return data.split(" ");
    }
}
