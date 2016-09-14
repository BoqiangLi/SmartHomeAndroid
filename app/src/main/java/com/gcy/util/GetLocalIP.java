package com.gcy.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Mr.G on 2016/6/1.
 */
public class GetLocalIP {

    /**
     *
     * 个别华为手机获取ip地址会为非局域网ip地址
     *
     * ***/
    public static String getLocalIpAddress()
    {
        StringBuffer sb = new StringBuffer();
        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    sb.append("_"+inetAddress.getHostAddress());
                }
            }
        }
        catch (SocketException ex) {
            return null;
        }
        String []str = sb.toString().split("_");

        return str[str.length-1];
    }
}
