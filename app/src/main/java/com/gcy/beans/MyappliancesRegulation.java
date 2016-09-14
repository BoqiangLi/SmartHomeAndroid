package com.gcy.beans;

/**
 * Created by Mr.G on 2016/5/14.
 *
 * 可控设备体
 */
public class MyappliancesRegulation {

    private String appliancesName;    //设备名称

    private String applicansValue;       //设备值

    private String regulation;          //是否可控

    private String status;              //状态

    private boolean isNormal = true;

    public boolean isNormal() {
        return isNormal;
    }

    public void setNormal(boolean normal) {
        isNormal = normal;
    }

    private String unit;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRegulation() {
        return regulation;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public String getApplicansValue() {
        return applicansValue;
    }

    public void setApplicansValue(String applicansValue) {
        this.applicansValue = applicansValue;
    }

    public String getAppliancesName() {
        return appliancesName;
    }

    public void setAppliancesName(String appliancesName) {
        this.appliancesName = appliancesName;

    }
}
