package com.gcy.util;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Mr.G on 2016/6/4.
 */
public class UnusualNumerical {



    public static List<Boolean> isNumericalNormal(List<String> mList,List<Float> standardList){
        List<Boolean> list = new ArrayList<>();
        boolean flag = true;


        if(mList==null||standardList==null){

            list.add(true);
            list.add(true);
            list.add(true);
            list.add(true);
            list.add(true);
            return list;}
        if(mList.size()!=5||standardList.size()!=5){
            list.add(true);
            list.add(true);
            list.add(true);
            list.add(true);
            list.add(true);
            return list;}

        if(!mList.get(0).equals("null"))
            if(mList.get(0).matches("\\d+\\.*\\d*"))
                if(Float.parseFloat(mList.get(0))> standardList.get(0)){
                flag = false;

                }
        list.add(flag);
        flag = true;
        if(!mList.get(1).equals("null"))
            if(mList.get(1).matches("\\d+\\.*\\d*"))
                if(Float.parseFloat(mList.get(1))>standardList.get(1)){
                    flag = false;

                }
        list.add(flag);
        flag = true;
        if(!mList.get(2).equals("null"))
            if(mList.get(2).matches("\\d+\\.*\\d*"))
                if(Float.parseFloat(mList.get(2))>standardList.get(2)){
                    flag = false;

                }
        list.add(flag);
        flag = true;
        if(!mList.get(3).equals("null"))
            if(mList.get(3).matches("\\d+\\.*\\d*"))
                if(Float.parseFloat(mList.get(3))>standardList.get(3)){
                    flag = false;

                }
        list.add(flag);
        flag = true;
        if(!mList.get(4).equals("null"))
            if(mList.get(4).matches("\\d+\\.*\\d*"))
                if(Float.parseFloat(mList.get(4))>standardList.get(4)){
                    flag = false;

                }
        list.add(flag);

        return list;


    }

}
