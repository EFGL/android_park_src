package com.gz.gzcar.device;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
public  class  LedModule {
    //定义设备类型
    public static HashMap<String, Character> DeviceTypeMap = new HashMap<String, Character>() {
        {
            put("BX_5K1", (char) 0x51);
            put("BX_5K2", (char) 0x52);
            put("BX_5MK2", (char) 0x53);
            put("BX_7MK1", (char) 0x54);
        }
    };
    //定义设备类型
    public static HashMap<String, Character> DisplayModeMap = new HashMap<String, Character>() {
        {
            put("STATIC_MODE", (char) 0x01);
            put("FAST_MODE", (char) 0x02);
            put("LEFT_MODE", (char) 0x03);
            put("RIGHT_MODE", (char) 0x04);
            put("UP_MODE", (char) 0x05);
            put("DOWN_MODE", (char) 0x06);
        }
    };
    public static char[] PackHeader = {0xA5, 0xA5, 0xA5, 0xA5, 0xA5, 0xA5, 0xA5, 0xA5};
    public static char[] PHY0 = {
            0x01, 0x00,//dstAddr
            0x00, 0x80, //srcAddr
            0x00, 0x00, 0x00, 0x00, 0x00,//reserved
            0x00,    //display mode
            0x00,    //device type
            0x02,    //protocolVersion
            0x00, 0x00,//dataLen
    };
    public static char[] PHY1 = {0xA3, 0x06, 0x02, 0x00, 0x00, 0x00,
            0x01,  //areaNum
            0x00, 0x00 //areaDataLen
    };
    public static char[] areaData = {0x00,
            0x00, 0x00, //area X
            0x00, 0x00, //area Y
            0x08, 0x00, //area width
            0x10, 0x00, //area height
            0x00,      //DynamicAreaLoc
            0x00,      //Lines sizes
            0x00,      //run mode
            0x0A, 0x00, //timeout S
            0x00, 0x00, 0x00,    //reserved
            0x01,      //sigleLine
            0x01,      //newLine
            0x00,      //DisplayMode
            0x00,      //exitMode
            0x06,      //speed
            0x00,      //stayTime
            0x00, 0x00, 0x00, 0x00  //dataLen
    };
    public static char[] crcTabel = {
            0X0000, 0XC0C1, 0XC181, 0X0140, 0XC301, 0X03C0, 0X0280, 0XC241,
            0XC601, 0X06C0, 0X0780, 0XC741, 0X0500, 0XC5C1, 0XC481, 0X0440,
            0XCC01, 0X0CC0, 0X0D80, 0XCD41, 0X0F00, 0XCFC1, 0XCE81, 0X0E40,
            0X0A00, 0XCAC1, 0XCB81, 0X0B40, 0XC901, 0X09C0, 0X0880, 0XC841,
            0XD801, 0X18C0, 0X1980, 0XD941, 0X1B00, 0XDBC1, 0XDA81, 0X1A40,
            0X1E00, 0XDEC1, 0XDF81, 0X1F40, 0XDD01, 0X1DC0, 0X1C80, 0XDC41,
            0X1400, 0XD4C1, 0XD581, 0X1540, 0XD701, 0X17C0, 0X1680, 0XD641,
            0XD201, 0X12C0, 0X1380, 0XD341, 0X1100, 0XD1C1, 0XD081, 0X1040,
            0XF001, 0X30C0, 0X3180, 0XF141, 0X3300, 0XF3C1, 0XF281, 0X3240,
            0X3600, 0XF6C1, 0XF781, 0X3740, 0XF501, 0X35C0, 0X3480, 0XF441,
            0X3C00, 0XFCC1, 0XFD81, 0X3D40, 0XFF01, 0X3FC0, 0X3E80, 0XFE41,
            0XFA01, 0X3AC0, 0X3B80, 0XFB41, 0X3900, 0XF9C1, 0XF881, 0X3840,
            0X2800, 0XE8C1, 0XE981, 0X2940, 0XEB01, 0X2BC0, 0X2A80, 0XEA41,
            0XEE01, 0X2EC0, 0X2F80, 0XEF41, 0X2D00, 0XEDC1, 0XEC81, 0X2C40,
            0XE401, 0X24C0, 0X2580, 0XE541, 0X2700, 0XE7C1, 0XE681, 0X2640,
            0X2200, 0XE2C1, 0XE381, 0X2340, 0XE101, 0X21C0, 0X2080, 0XE041,
            0XA001, 0X60C0, 0X6180, 0XA141, 0X6300, 0XA3C1, 0XA281, 0X6240,
            0X6600, 0XA6C1, 0XA781, 0X6740, 0XA501, 0X65C0, 0X6480, 0XA441,
            0X6C00, 0XACC1, 0XAD81, 0X6D40, 0XAF01, 0X6FC0, 0X6E80, 0XAE41,
            0XAA01, 0X6AC0, 0X6B80, 0XAB41, 0X6900, 0XA9C1, 0XA881, 0X6840,
            0X7800, 0XB8C1, 0XB981, 0X7940, 0XBB01, 0X7BC0, 0X7A80, 0XBA41,
            0XBE01, 0X7EC0, 0X7F80, 0XBF41, 0X7D00, 0XBDC1, 0XBC81, 0X7C40,
            0XB401, 0X74C0, 0X7580, 0XB541, 0X7700, 0XB7C1, 0XB681, 0X7640,
            0X7200, 0XB2C1, 0XB381, 0X7340, 0XB101, 0X71C0, 0X7080, 0XB041,
            0X5000, 0X90C1, 0X9181, 0X5140, 0X9301, 0X53C0, 0X5280, 0X9241,
            0X9601, 0X56C0, 0X5780, 0X9741, 0X5500, 0X95C1, 0X9481, 0X5440,
            0X9C01, 0X5CC0, 0X5D80, 0X9D41, 0X5F00, 0X9FC1, 0X9E81, 0X5E40,
            0X5A00, 0X9AC1, 0X9B81, 0X5B40, 0X9901, 0X59C0, 0X5880, 0X9841,
            0X8801, 0X48C0, 0X4980, 0X8941, 0X4B00, 0X8BC1, 0X8A81, 0X4A40,
            0X4E00, 0X8EC1, 0X8F81, 0X4F40, 0X8D01, 0X4DC0, 0X4C80, 0X8C41,
            0X4400, 0X84C1, 0X8581, 0X4540, 0X8701, 0X47C0, 0X4680, 0X8641,
            0X8201, 0X42C0, 0X4380, 0X8341, 0X4100, 0X81C1, 0X8081, 0X4040
    };

    public static Character CalcCRC(ArrayList<Character> data, int size) {
        int i;
        char crc = 0;
        for (i = 0; i < size; i++) {
            crc = (char) ((crc >> 8) ^ crcTabel[(crc ^ data.get(i)) & 0XFF]);
        }
        return crc;
    }

    /*＊*****************************
    入口： line 行号
          DeviceType   卡类型
          info         显示的数据
          resBuf       返回的数据
    */
    public static byte[] formatData(char line, String info, String devType, String dispMode) {
        int len;
        byte[] formatBuffer = null;
        try {
            byte[] infoBuffer = info.getBytes("GBK");
            ArrayList<Character> buffer = new ArrayList<>();
            //设置PHY0
            PHY0[10] = DeviceTypeMap.get(devType);
            len = buffer.size() + 36;
            PHY0[12] = (char) (len % 256);
            PHY0[13] = (char) (len / 256);
            //设置PHY1
            len = infoBuffer.length + 27;
            PHY1[7] = (char) (len % 256);
            PHY1[8] = (char) (len / 256);
            //设置数据空间
            areaData[3] = (char) ((line - 1) * 16);  //area Y
            areaData[9] = (char) (line - 1);       //area Y
            //设置显示模式
            if (infoBuffer.length > 8) {
                if (info.indexOf("\\D") < 0) {
                    areaData[19] = DisplayModeMap.get(dispMode);
                    if (areaData[19] == DisplayModeMap.get("LEFT_MODE")) {
                        areaData[22] = 0x00;
                    } else {
                        areaData[22] = 0x03;
                    }
                } else {
                    areaData[19] = DisplayModeMap.get("STATIC_MODE");
                }
            } else {
                areaData[19] = DisplayModeMap.get("STATIC_MODE");
            }
            areaData[23] = (char) (infoBuffer.length % 256);
            areaData[24] = (char) (infoBuffer.length / 256);
            //加入PHY0
            for (Character item : PHY0) {
                buffer.add(item);
            }
            //加入PHY1
            for (Character item : PHY1) {
                buffer.add(item);
            }
            //加入数据头
            for (Character item : areaData) {
                buffer.add(item);
            }
            //加入数据
            for (byte item : infoBuffer) {
                buffer.add((char) item);
            }
            char crcCheck = CalcCRC(buffer, buffer.size());
            buffer.add((char) (crcCheck % 256));
            buffer.add((char) (crcCheck / 256));
            buffer.add((char) 0x5A);
            //生成返回数据
            formatBuffer = new byte[PackHeader.length + buffer.size()];
            int count = 0;
            for (Character item : PackHeader) {
                formatBuffer[count++] = (byte) (item & 0xFF);
            }
            for (Character item : buffer) {
                formatBuffer[count++] = (byte) (item & 0xFF);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return formatBuffer;
    }

    public static byte[] FormatClock() {
        int len;
        byte[] formatBuffer = null;
        ArrayList<Character> buffer = new ArrayList<>();
        //PHP0
        char[] PHY0 = {
                0x01, 0x00,//dstAddr
                0x00, 0x80, //srcAddr
                0x00, 0x00, 0x00, 0x00, 0x00,//reserved
                0x00,    //display mode
                0x51,    //device type
                0x02,    //protocolVersion
                0x0D, 0x00,//dataLen
        };
        char[] PHY1 = {0xA2, 0x03, 0x02, 0x00, 0x00,
                0x16, 0x20,  //年
                0x01,       //月
                0x01,       //日
                0x01,       //时
                0x11,       //分
                0x11,       //秒
                0x01       //星期
        };
        Calendar clock = Calendar.getInstance();
        int valueH = clock.get(Calendar.YEAR) / 100;
        int valueL = clock.get(Calendar.YEAR) % 100;
        PHY1[5] = (char) ((valueL / 10) * 16 + valueL % 10);
        PHY1[6] = (char) ((valueH / 10) * 16 + valueH % 10);
        int month = clock.get(Calendar.MONTH) + 1;
        PHY1[7] = (char) ((month / 10) * 16 + month % 10);
        int day = clock.get(Calendar.DATE);
        PHY1[8] = (char) ((day / 10 * 16) + day % 10);
        int hour = clock.get(Calendar.HOUR_OF_DAY);
        PHY1[9] = (char) ((hour / 10 * 16) + hour % 10);
        int minute = clock.get(Calendar.MINUTE);
        PHY1[10] = (char) ((minute / 10) * 16 + minute % 10);
        int second = clock.get(Calendar.SECOND);
        PHY1[11] = (char) ((second / 10) * 16 + second % 10);
        PHY1[12] = (char) clock.get(Calendar.DAY_OF_WEEK);
        //加入PHY0
        for (Character item : PHY0) {
            buffer.add(item);
        }
        //加入PHY1
        for (Character item : PHY1) {
            buffer.add(item);
        }
        char crcCheck = CalcCRC(buffer, buffer.size());
        buffer.add((char) (crcCheck % 256));
        buffer.add((char) (crcCheck / 256));
        buffer.add((char) 0x5A);
        //生成返回数据
        formatBuffer = new byte[PackHeader.length + buffer.size()];
        int count = 0;
        for (Character item : PackHeader) {
            formatBuffer[count++] = (byte) (item & 0xFF);
        }
        for (Character item : buffer) {
            formatBuffer[count++] = (byte) (item & 0xFF);
        }
        return formatBuffer;
    }

    public static void udpLedDispaly(String ip, int port, String info) {
        try {
            String sendStr = "!#001%1" + info + "$$";
            Log.i("log",sendStr);
            //首先创建一个DatagramSocket对象
            DatagramSocket socket = new DatagramSocket(port);
            //创建一个InetAddree
            InetAddress serverAddress = InetAddress.getByName(ip);
            byte data[] = sendStr.getBytes("GBK");  //把传输内容分解成字节
            //创建一个DatagramPacket对象，并指定要讲这个数据包发送到网络当中的哪个、
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress,5005);
            //调用socket对象的send方法，发送数据
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
