package com.gcy.mqttUtil;

import android.widget.Toast;

import com.gcy.util.GetLocalIP;
import com.gcy.util.MyApplication;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import confige.Config;

public class Phone {

	private MqttClient client;

	private MqttMessage message;

	private String light1 = "";
	private String light2 = "";
	private String light3 = "";
	private String light4 = "";


	/***
	 *
	 * 获取灯的状态信息
	 * ***/
	private LightInfoChangedCallBack lightInfoChangedCallBack = null;
	public void setLightInfoChangedCallBack(LightInfoChangedCallBack lightInfoChangedCallBack){
		this.lightInfoChangedCallBack = lightInfoChangedCallBack;
	}

	public boolean isConnect(){
		if(client==null)
			return false;
		return client.isConnected();
	}


	/**返回灯信息**/

	public String getLightInfo(int id){

		sendGetLightInfoConmand();
		String light = "null";
		switch(id){
			case 1:
				light = light1;
				break;
			case 2:
				light = light2;
				break;
			case 3:
				light = light3;
				break;
			case 4:
				light = light4;
				break;


		}
		return light;
	}

	public void sendGetLightInfoConmand(){
		MyApplication.isOnline = false;
		this.message = new MqttMessage();
		this.message.setQos(2);
		this.message.setRetained(true);
		this.message.setPayload("give_light_info".getBytes());

		pulish(GetLightInfo, this.message);
	}

	//发布主题
	private MqttTopic Stm32Light0Pub;
	private MqttTopic Stm32Light1Pub;
	private MqttTopic Stm32Light2Pub;
	private MqttTopic Stm32Light3Pub;
	private MqttTopic Stm32FanPub;
	private MqttTopic Stm32WaterPub;
	private MqttTopic PhoneIpPub;
	private MqttTopic RaspberrypiCameraPub;
	private MqttTopic PhoneIpAnswerPub;
	private MqttTopic VideoStopPub;
	private MqttTopic GetLightInfo;
	private MqttTopic GetStmStatue;
	private MqttTopic RaspberrypiOpenDoor;

	//订阅主题
	private int[] Qos = {2,2,2,2,2,2,2,2,2,2,2,2,2};
	private String[] TopicSub = {CommonInfo.Stm32Light0SubString,CommonInfo.Stm32Light1SubString,CommonInfo.Stm32Light2SubString,CommonInfo.Stm32Light3SubString,CommonInfo.Stm32PmStaSubString,CommonInfo.Stm32WaterStaSubString,CommonInfo.Stm32TempStaSubString,CommonInfo.Stm32HumiStaSubString,CommonInfo.Stm32GasStaSubString,CommonInfo.RaspberrypiIpSubString,CommonInfo.RaspberrypiIpAnswerSubString,CommonInfo.VideoStopAnswerSubString,CommonInfo.ApplicansStatues};

	public static Phone mPhone = null;
	public static Phone getInstance(){
		if(mPhone == null)
			mPhone = new Phone();
		return mPhone;
	}

	public Phone(){
		try {
			client = new MqttClient(CommonInfo.HOST, Config.clientId,new MemoryPersistence());


		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private static MqttCallback mqttCallBack;
	public void setMqttCallBack(MqttCallback mqttCallBack){
		this.mqttCallBack = null;
		this.mqttCallBack = mqttCallBack;
	}

	//链接函数
	public void Connect(){

		try {
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(true);
//			options.setKeepAliveInterval(10);
//			options.setConnectionTimeout(20);
//			options.setUserName(CommonInfo.username);
//			options.setPassword(CommonInfo.password.toCharArray());
			client.setCallback(mqttCallBack);
			client.connect(options);
			if(onlineCheckCallBack!=null)
				onlineCheckCallBack.online();
			MyApplication.mMqttClient = null;
			MyApplication.mMqttClient = client;


			//发布主题
			GetLightInfo = client.getTopic(CommonInfo.GetLightInfoMation);
			Stm32Light0Pub = client.getTopic(CommonInfo.Stm32Light0PubString);
			Stm32Light1Pub = client.getTopic(CommonInfo.Stm32Light1PubString);
			Stm32Light2Pub = client.getTopic(CommonInfo.Stm32Light2PubString);
			Stm32Light3Pub = client.getTopic(CommonInfo.Stm32Light3PubString);
			Stm32FanPub = client.getTopic(CommonInfo.Stm32FanPubString);
			Stm32WaterPub = client.getTopic(CommonInfo.Stm32WaterPubString);
			PhoneIpPub = client.getTopic(CommonInfo.PhoneIpPubString);
			RaspberrypiCameraPub = client.getTopic(CommonInfo.RaspberrypiCameraPubString);
			PhoneIpAnswerPub = client.getTopic(CommonInfo.PhoneIpAnswerPubString);
			VideoStopPub = client.getTopic(CommonInfo.VideoStopPubString);
			GetStmStatue = client.getTopic(CommonInfo.GetStmStatue);
			RaspberrypiOpenDoor = client.getTopic(CommonInfo.OpenTheDoor);
			//订阅主题
			client.subscribe(TopicSub, Qos);

		} catch (Exception e) {
			if(onlineCheckCallBack!=null)
				onlineCheckCallBack.offline();
			e.printStackTrace();
		}
	}

	/**
	 * 功能函数
	 */

	public void disConnect(){
		try {
			client.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}


	/**
	 *
	 * 发送中断视频连接请求
	 * 不用更改
	 * 直接调用
	 */
	public void ControlVideoStop(){

		this.message = new MqttMessage();
		this.message.setQos(2);
		this.message.setRetained(true);
		this.message.setPayload("stop".getBytes());

		pulish(VideoStopPub, this.message);
	}


	/**
	 *
	 * 树莓派接受开门信息
	 *
	 * */
	public void OpenTheDoor(){
		this.message = new MqttMessage();
		this.message.setQos(2);
		this.message.setRetained(true);
		this.message.setPayload("openthedoor".getBytes());

		pulish(RaspberrypiOpenDoor, this.message);

	}
	/**
	 * 控制灯
	 * @param action String类型  0关 1开
	 * @param id 控制灯的编号 0,1,2,3
	 */
	public void ControlLamp(String action,int id){
		this.message = new MqttMessage();
		this.message.setQos(1);
		this.message.setRetained(true);
		this.message.setPayload(action.getBytes());

		switch(id){
			case 0: pulish(Stm32Light0Pub, this.message); break;
			case 1: pulish(Stm32Light1Pub, this.message); break;
			case 2: pulish(Stm32Light2Pub, this.message); break;
			case 3: pulish(Stm32Light3Pub, this.message); break;
		}

	}



	/**
	 * 控制风扇
	 * @param action String类型 0关闭/值 开启
	 */
	public void ControlFan(String action){

		this.message = new MqttMessage();
		this.message.setQos(2);
		this.message.setRetained(true);
		this.message.setPayload(action.getBytes());
		if(pulish(Stm32FanPub, this.message)){
			Toast.makeText(MyApplication.getContext(), "风扇: "+action+" 设置成功！", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 控制水温
	 * @param action String类型  0 关闭/值 开启
	 * @param
	 */
	public void ControlWater(String action){
		this.message = new MqttMessage();
		this.message.setQos(2);
		this.message.setRetained(true);
		this.message.setPayload(action.getBytes());

		if(pulish(Stm32WaterPub, message)){
			Toast.makeText(MyApplication.getContext(), "水温: "+action+" 设置成功！", Toast.LENGTH_SHORT).show();

		}
	}

	/**
	 * 控制摄像头
	 * @param action String类型 0上 1下 2左 3右
	 * @param
	 */
	public void ControlCamera(String action){
		this.message = new MqttMessage();
		this.message.setQos(2);
		this.message.setRetained(true);

		if(action.equals("0")){
			this.message.setPayload("UP".getBytes());
		}
		else{
			if(action.equals("1")){
				this.message.setPayload("DOWN".getBytes());
			}
			else{
				if(action.equals("2")){
					this.message.setPayload("LEFT".getBytes());
				}
				else{
					if(action.equals("3")){
						this.message.setPayload("RIGHT".getBytes());
					}
				}
			}
		}
		pulish(RaspberrypiCameraPub, this.message);
	}

	/**
	 * 发送手机IP
	 * @param ip String类型 手机IP
	 * @param
	 */
	public void ControlPhoneIP(String ip){
		this.message = new MqttMessage();
		this.message.setQos(2);
		this.message.setRetained(true);
		this.message.setPayload(ip.getBytes());
		if(!pulish(PhoneIpPub, this.message)){
			Toast.makeText(MyApplication.getContext(), "连接服务器失败！", Toast.LENGTH_SHORT).show();

		}
	}

	/**
	 * 手机IP应答
	 * @param
	 */
	public void AnswerPhoneIP(){

		//TODO 手机IP地址
		String ip = GetLocalIP.getLocalIpAddress();
		this.message = new MqttMessage();
		this.message.setQos(2);
		this.message.setRetained(true);
		this.message.setPayload(ip.getBytes());

		pulish(PhoneIpAnswerPub, this.message);
	}



	/**
	 * 状态信息函数    收到状态信息时调用该函数
	 * @param value 值 ( null离线 )
	 */
	public void DeviceStatu(String device,String value){

		switch(device){

			//危险气体
			case CommonInfo.gas:
				if(value.equals("null"))
				{
					System.out.println("设备离线");
				}

				break;

			//湿度
			case CommonInfo.humi:
				if(value.equals("null"))
				{
					System.out.println("设备离线");
				}
				break;

			//温度
			case CommonInfo.temp:
				if(value.equals("null"))
				{
					System.out.println("设备离线");
				}
				break;

			//水温
			case CommonInfo.water:
				if(value.equals("null"))
				{
					System.out.println("设备离线");
				}
				break;

			//灯
			case CommonInfo.light0:
				light1 = value;
				lightInfoChangedCallBack.lightInfoChanged(1,value);
				onlineCheckCallBack.online();
				if(value.equals("null"))
				{
					System.out.println("设备离线");
				}
				break;

			case CommonInfo.light1:
				light2 = value;
				lightInfoChangedCallBack.lightInfoChanged(2,value);
				if(value.equals("null"))
				{
					System.out.println("设备离线");
				}
				break;

			case CommonInfo.light2:
				light3 = value;
				lightInfoChangedCallBack.lightInfoChanged(3,value);
				if(value.equals("null"))
				{
					System.out.println("设备离线");
				}
				break;

			case CommonInfo.light3:
				light4 = value;
				lightInfoChangedCallBack.lightInfoChanged(4,value);
				if(value.equals("null"))
				{
					System.out.println("设备离线");
				}
				break;
		}

	}

	/**
	 * 发送控制信息给STM32
	 * 控制成功 调用该函数  只要收到就可显示控制成功
	 */
	public void ControlSucess(){

		//System.out.println("控制成功");

	}

	/**
	 * 建立视频连接
	 */
	public void ConnectionVideo(){

	}

	/**
	 * 中断视频连接
	 */
	public void VideoStop(){

	}

	/**
	 * 重连
	 * */

	public void ReConnect(){

	}

	/**
	 *
	 * 获取stm设备状态
	 *
	 * */
	public void setGetStmStatue(){
		this.message = new MqttMessage();
		this.message.setQos(2);
		this.message.setRetained(true);
		this.message.setPayload("Are_You_Online".getBytes());

		pulish(GetStmStatue, this.message);
	}
	//推送消息
	public boolean pulish(MqttTopic t,MqttMessage m){
		try {
			MqttDeliveryToken token = t.publish(m);
			token.waitForCompletion();
			if(onlineCheckCallBack!=null)
				onlineCheckCallBack.online();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if(onlineCheckCallBack!=null)
				onlineCheckCallBack.offline();
			return false;
		}
	}

	private OnlineCheckCallBack onlineCheckCallBack;
	public void setOnlineCheckCallBack(OnlineCheckCallBack onlineCheckCallBack){
		this.onlineCheckCallBack = onlineCheckCallBack;
	}

	/*****测试部分代码*****/
	private MqttTopic test;



	public void pubishWater(String str){

		this.message = new MqttMessage();
		this.message.setQos(1);
		this.message.setRetained(true);
		this.message.setPayload(str.getBytes());

		pulish(test, this.message);
		//public String Stm32WaterStaSubString = "/stm32/water/sta";
	}
}
