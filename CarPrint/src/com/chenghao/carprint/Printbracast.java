package com.chenghao.carprint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Base64;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gprinter.command.EscCommand;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.command.GpCom;
/**
 * 打印广播
 */
public class Printbracast extends BroadcastReceiver{

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Toast.makeText(arg0, "打印机收到打印消息", Toast.LENGTH_LONG).show();
		/**
		 * 	intent.putExtra("msg", jsonStrng);
			intent.putExtra("operatename", "张三");
			intent.putExtra("company", "石景山首钢小区停车场");
		 * 
		 * msg的格式
		 * 
		 * {"carNumber":"京Q66478","inTime":"2016-10-1 19:00","money":999.9,"outTime":"2016-10-9 8:00","parkTime":"20小时58分钟","type":"探亲车"}
		 */

		String msg=arg1.getStringExtra("msg");
		String operatename=arg1.getStringExtra("operatename");
		String company=arg1.getStringExtra("company");

		FreeInfoTable table=JSON.parseObject(msg, FreeInfoTable.class);

		EscCommand esc = new EscCommand();
		esc.addPrintAndFeedLines((byte) 3);
		esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
		esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);// 设置为倍高倍宽
		esc.addText("停车费\n"); // 打印文字
		esc.addPrintAndLineFeed();

		esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);// 设置为倍高倍宽
		esc.addSelectJustification(JUSTIFICATION.LEFT);// 设置打印左对齐
		esc.addText("车号："+table.getCarNumber()+"\n\n"); // 打印文字
		esc.addText("停车时长："+table.getParkTime()+"\n\n"); // 打印文字
		esc.addText("收费金额：￥"+table.getMoney()+"\n\n"); // 打印文字

		esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);// 取消倍高倍宽
		esc.addSelectJustification(JUSTIFICATION.LEFT);// 设置打印左对齐
		esc.addText("类型："+table.getType()+"\n\n"); // 打印文字
		esc.addText("入场时间："+table.getInTime()+"\n\n"); // 打印文字
		esc.addText("出厂时间："+table.getOutTime()+"\n\n"); // 打印文字
		esc.addText("收费员："+operatename+"\n\n"); // 打印文字

		esc.addSelectJustification(JUSTIFICATION.RIGHT);// 设置打印右对齐
		esc.addText(company+"\n"); // 打印文字
		esc.addText(dateFormat.format(new Date())+"\n"); // 打印文字
		esc.addText("\n\n\n\n\n\n\n\n");
		Vector<Byte> datas = esc.getCommand(); // 发送数据
		Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
		byte[] bytes = ArrayUtils.toPrimitive(Bytes);
		String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
		int rs;
		try {
			rs = Appcontext.mGpService.sendEscCommand(0, sss);
			GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
			if (r != GpCom.ERROR_CODE.SUCCESS) {
				Toast.makeText(arg0, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
