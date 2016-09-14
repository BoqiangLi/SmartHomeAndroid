package com.gcy.mqttUtil;

public interface CommonInfo {

	/**
	 * 配置
	 */
	public String HOST = "tcp://121.42.62.140:1883";
	public String deviceid = "phone";
	public String username = "admin";
	public String password = "password";

	/**
	 * 设备
	 */
	//危险气体
	public String gas = "GAS";
	//湿度
	public String humi = "HUMI";
	//温度
	public String temp = "TEMP";
	//PM2.5
	public String pm = "PM2.5";
	//水温
	public String water = "WATER";
	//灯
	public String light0 = "LIGHT0";
	public String light1 = "LIGHT1";
	public String light2 = "LIGHT2";
	public String light3 = "LIGHT3";

	/**
	 * 发布
	 */
	//灯
	public String Stm32Light0PubString = "/stm32/light/0";
	public String Stm32Light1PubString = "/stm32/light/1";
	public String Stm32Light2PubString = "/stm32/light/2";
	public String Stm32Light3PubString = "/stm32/light/3";
	//风扇
	public String Stm32FanPubString = "/stm32/fan/0";
	//水温
	public String Stm32WaterPubString = "/stm32/water/0";
	//手机IP
	public String PhoneIpPubString = "/phone/ip";
	//摄像头
	public String RaspberrypiCameraPubString = "/raspberrypi/camera";
	//手机IP应答
	public String PhoneIpAnswerPubString = "/phone/ip/answer";
	//视频停止
	public String VideoStopPubString = "/raspberrypi/video/stop";
	//获取灯信息
	public String GetLightInfoMation = "/stm32/light_sta/";
	//主动获取设备信息
	public String GetStmStatue = "/stm32/sta/isonline";
	//开门
	public String OpenTheDoor = "/raspberrypi/door/open";

	/**
	 * 订阅
	 */
	//灯
	public String Stm32Light0SubString = "/stm32/light/0/sta";
	public String Stm32Light1SubString = "/stm32/light/1/sta";
	public String Stm32Light2SubString = "/stm32/light/2/sta";
	public String Stm32Light3SubString = "/stm32/light/3/sta";
	//PM2.5
	public String Stm32PmStaSubString = "/stm32/pm/sta";
	//水温
	public String Stm32WaterStaSubString = "/stm32/water/sta";
	//温度
	public String Stm32TempStaSubString = "/stm32/temp/sta";
	//湿度
	public String Stm32HumiStaSubString = "/stm32/humi/sta";
	//可燃气体
	public String Stm32GasStaSubString = "/stm32/gas/sta";
	//树莓派IP
	public String RaspberrypiIpSubString = "/raspberrypi/ip";
	//树莓派IP应答
	public String RaspberrypiIpAnswerSubString = "/raspberrypi/ip/answer";
	//STM32应答
	public String Stm32AnswerSubString = "/stm32/answer";
	//视频停止 应答
	public String VideoStopAnswerSubString = "/raspberrypi/video/stop/answer";
	//设备状态
	public String ApplicansStatues = "/stm32/sta/online";
}
