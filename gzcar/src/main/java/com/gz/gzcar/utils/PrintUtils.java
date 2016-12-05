package com.gz.gzcar.utils;

import android.content.Context;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;

import org.apache.commons.lang.ArrayUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * 打印机工具类
 * 程浩
 */
public class PrintUtils {
    public static  Boolean isshowlog=false;

    /**
     * 打印停车费用
     * @param context
     * @param mGpService
     * @param mmsg
     * @param moperatename
     * @param mcompany
     */
    public static void printParkfee(Context context, GpService mGpService, String mmsg, String moperatename, String mcompany){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String msg=mmsg;
        String operatename=moperatename;
        String company=mcompany;

        showlog("msg="+msg);
        showlog("operatename="+operatename);
        showlog("operatename="+operatename);


        MyFreeInfoBean table= JSON.parseObject(msg, MyFreeInfoBean.class);

        EscCommand esc = new EscCommand();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("停车费\n"); // 打印文字
        esc.addPrintAndLineFeed();

        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        esc.addText("车号："+table.getCarNumber()+"\n\n"); // 打印文字
        esc.addText("停车时长："+table.getParkTime()+"\n\n"); // 打印文字
        esc.addText("收费金额：￥"+table.getMoney()+"\n\n"); // 打印文字

        esc.addSelectPrintModes(EscCommand.FONT.FONTB, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        esc.addText("类型："+table.getType()+"\n\n"); // 打印文字
        esc.addText("入场时间："+table.getInTime()+"\n\n"); // 打印文字
        esc.addText("出厂时间："+table.getOutTime()+"\n\n"); // 打印文字
        esc.addText("收费员："+operatename+"\n\n"); // 打印文字

        esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);// 设置打印右对齐
        esc.addText(company+"\n\n"); // 打印文字
        esc.addText(dateFormat.format(new Date())+"\n"); // 打印文字
        esc.addText("\n\n\n\n\n\n\n\n");
        Vector<Byte> datas = esc.getCommand(); // 发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(0, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(context, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印当班信息
     * @param context
     * @param mGpService
     * @param mmsg
     */
    public static void printonduty(Context context, GpService mGpService, String mmsg){
        String msg=mmsg;

        DayMessageBean table=JSON.parseObject(msg, DayMessageBean.class);
        EscCommand esc = new EscCommand();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("收费统计单\n"); // 打印文字
        esc.addPrintAndLineFeed();

        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        esc.addText("操作员："+table.getOperator()+"\n\n"); // 操作员
        esc.addText("起始时间："+table.getStarttime()+"\n\n"); // 起始时间
        esc.addText("结束时间："+table.getEndtime()+"\n\n"); // 结束时间
        esc.addText("停车总时间："+table.getStoptime()+"\n\n"); // 停车总时间
        esc.addText("车辆总数："+table.getCarallnum()+"\n\n"); // 车辆总数
        esc.addText("应收合计￥："+table.getReceivable()+"\n\n"); // 应收合计
        esc.addText("实收合计￥："+table.getRealPrice()+"\n\n"); // 实收合计


        esc.addSelectPrintModes(EscCommand.FONT.FONTB, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        esc.addText("\n\n\n\n\n\n\n\n");

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs =mGpService.sendEscCommand(0, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(context, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打印测试
     * @param context
     * @param mGpService
     */
    public static void printtest(Context context, GpService mGpService){
        showlog("dayin ceshi");
        //测试打印
        EscCommand esc = new EscCommand();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("\n"); // 打印文字
        esc.addPrintAndLineFeed();
        Vector<Byte> datas = esc.getCommand(); // 发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(0, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                showlog("error:"+GpCom.getErrorText(r));
//                Toast.makeText(context, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void showlog(String msg){
        if(isshowlog)
        Log.i("Print",msg);
    }


}
