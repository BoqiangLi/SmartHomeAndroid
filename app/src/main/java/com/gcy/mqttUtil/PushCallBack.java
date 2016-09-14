package com.gcy.mqttUtil;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PushCallBack implements MqttCallback {

	Phone phone;
	//传入上层对象
	public PushCallBack(Phone phone){
		this.phone = phone;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println("链接断开");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		System.out.println("deliveryComplete----------"+arg0.isComplete());
	}

	/**
	 * 接收推送消息
	 */
	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
//		System.out.println("接收消息主题："+arg0);
//		System.out.println("接受消息Qos"+arg1.getQos());
//		System.out.println("接受消息内容"+new String(arg1.getPayload()));

		switch(arg0){

			//手机发送IP，树莓派应答后，建立视频连接
			case CommonInfo.RaspberrypiIpAnswerSubString:
				System.out.println("接受到树莓派应答IP为："+new String(arg1.getPayload()));
				//建立视频连接
				phone.ConnectionVideo();
				break;

			//树莓派发送IP后，手机发送IP应答，建立视频连接
			case CommonInfo.RaspberrypiIpSubString:
				System.out.println("接收到树莓派的IP为："+new String(arg1.getPayload()));
				//发送手机IP
				phone.AnswerPhoneIP();
				//建立视频链接
				phone.ConnectionVideo();
				break;

			case CommonInfo.Stm32GasStaSubString:
				System.out.println("接收到危险气体为："+new String(arg1.getPayload()));
				phone.DeviceStatu(CommonInfo.gas, new String(arg1.getPayload()));
				break;

			case CommonInfo.Stm32HumiStaSubString:
				System.out.println("接收到HUMI为"+new String(arg1.getPayload()));
				phone.DeviceStatu(CommonInfo.humi, new String(arg1.getPayload()));
				break;

			case CommonInfo.Stm32PmStaSubString:
				System.out.println("接收到PM为："+new String(arg1.getPayload()));
				phone.DeviceStatu(CommonInfo.pm, new String(arg1.getPayload()));
				break;

			case CommonInfo.Stm32WaterStaSubString:

				System.out.println("接收到水温为："+new String(arg1.getPayload()));
				phone.DeviceStatu(CommonInfo.water, new String(arg1.getPayload()));
				break;

			case CommonInfo.Stm32TempStaSubString:
				System.out.println("接收到Temp为："+new String(arg1.getPayload()));
				phone.DeviceStatu(CommonInfo.temp, new String(arg1.getPayload()));
				break;

			case CommonInfo.Stm32AnswerSubString:
				System.out.println("STM32控制信息发送成功");
				phone.ControlSucess();
				break;

			case CommonInfo.VideoStopAnswerSubString:
				System.out.println("视频停止应答");
				//中断视频连接
				phone.VideoStop();
				break;

			//灯
			case CommonInfo.Stm32Light0SubString:
				phone.DeviceStatu(CommonInfo.light0, new String(arg1.getPayload()));
				break;

			case CommonInfo.Stm32Light1SubString:
				phone.DeviceStatu(CommonInfo.light1, new String(arg1.getPayload()));
				break;
			case CommonInfo.Stm32Light2SubString:
				phone.DeviceStatu(CommonInfo.light2, new String(arg1.getPayload()));
				break;
			case CommonInfo.Stm32Light3SubString:
				phone.DeviceStatu(CommonInfo.light3, new String(arg1.getPayload()));
				break;
		}
	}


}
