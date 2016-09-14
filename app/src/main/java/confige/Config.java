package confige;

import android.app.Activity;

import com.gcy.beans.Myappliances;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.G on 2016/5/15.
 */
public class Config {
    public static String URL = "http://121.42.62.140:80/server/phone.do";


    public static String clientId = "phone";
    public static Activity mActivity;
    public static Activity mSoundActivity;
    public static float TEMPERATURE_STANDARD = 50.0f;         //温度超标界限
    public static float HUMIDITY_STANDARD = 50.0f;           //湿度超标界限
    public static float WATER_STANDARD = 50.0f;             //水温超标界限
    public static float PM2_5_STANDARD = 50.0f;            //PM2.5 安全值界限
    public static float DANGEROUS_GAS_STANDARD = 50.0f;    //危险气体安全值界限
    public static int MONITORING_FREQUENCY = 5000;//环境监测频率  5秒/次



    //当前设备
    public static List<Myappliances> getApplicancesList(){
        List<Myappliances> mList= new ArrayList<Myappliances>();
        Myappliances temp = new Myappliances();
        temp.setAppliancesName("温度");
        temp.setApplicansValue("离线");
        temp.setUnit("℃");
        temp.setRegulation("否");
        temp.setStatus("正常");

        Myappliances humi = new Myappliances();
        humi.setAppliancesName("湿度");
        humi.setApplicansValue("离线");
        humi.setUnit("%");
        humi.setRegulation("否");
        humi.setStatus("正常");

        Myappliances water = new Myappliances();
        water.setAppliancesName("水温");
        water.setApplicansValue("离线");
        water.setUnit("℃");
        water.setRegulation("是");
        water.setStatus("正常");

        Myappliances pm = new Myappliances();
        pm.setAppliancesName("PM2.5");
        pm.setApplicansValue("离线");
        pm.setUnit("μg/m3");
        pm.setRegulation("否");
        pm.setStatus("正常");

        Myappliances dang = new Myappliances();
        dang.setAppliancesName("危险气体");
        dang.setApplicansValue("离线");
        dang.setUnit("ppm");
        dang.setRegulation("否");
        dang.setStatus("正常");

        mList.add(temp);
        mList.add(humi);
        mList.add(water);
        mList.add(pm);
        mList.add(dang);



        return mList;
    }







}
