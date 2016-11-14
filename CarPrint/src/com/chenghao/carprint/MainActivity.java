package com.chenghao.carprint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.service.GpPrintService;

/**
 * 停车场打印首页
 */
public class MainActivity extends Activity implements OnClickListener{
	private PrinterServiceConnection conn = null;

	private List<String> usblist=new ArrayList<String>();
	private StringBuffer buffer;
	/**
	 * 显示当前的usb设备
	 */
	private TextView tv_usb;
	/**
	 * 测试打印
	 */
	private Button but_print;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findid();
		connection();
		getUsbDeviceList();
		buffer=new StringBuffer();
		for(int c=0;c<usblist.size();c++){
			buffer.append(usblist.get(c));
		}
		tv_usb.setText("当前可连接的usb设备为："+buffer.toString());
	}

	private void findid() {
		tv_usb=(TextView) findViewById(R.id.tv_usb);	
		but_print=(Button) findViewById(R.id.but_print);
		but_print.setOnClickListener(this);
	}

	private void connection() {
		conn = new PrinterServiceConnection();
		Intent intent = new Intent(this, GpPrintService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
	}
	class PrinterServiceConnection implements ServiceConnection {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Appcontext.mGpService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Appcontext.mGpService = GpService.Stub.asInterface(service);
			try {
				Appcontext.mGpService.openPort(0, 2, buffer.toString(), 0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	public void getUsbDeviceList(){
		UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> devices = manager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = devices.values().iterator();
		int count = devices.size();
		showlog("count "+ count);
		if(count > 0)
		{
			while (deviceIterator.hasNext()) {
				UsbDevice device = deviceIterator.next();
				String devicename = device.getDeviceName();
				if(checkUsbDevicePidVid(device)){
					usblist.add(devicename);	
				}
			} 
		}
		else
		{
			String noDevices ="没有usb设备";
			usblist.add(noDevices);
		}
	}
	boolean checkUsbDevicePidVid(UsbDevice dev) {
		int pid = dev.getProductId();
		int vid = dev.getVendorId();
		boolean rel = false;
		if ((vid == 34918 && pid == 256) || (vid == 1137 && pid == 85)
				|| (vid == 6790 && pid == 30084)
				|| (vid == 26728 && pid == 256) || (vid == 26728 && pid == 512)
				|| (vid == 26728 && pid == 256) || (vid == 26728 && pid == 768)
				|| (vid == 26728 && pid == 1024)|| (vid == 26728 && pid == 1280)
				|| (vid == 26728 && pid == 1536)) {
			rel = true;
		}
		return rel;
	}
	public void showlog(String str){
		Log.i("chenghao", "内容："+str);
	}
	public void showtoast(String str){
		Toast.makeText(MainActivity.this, "内容："+str, Toast.LENGTH_LONG).show();

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.but_print:
			//测试打印
			EscCommand esc = new EscCommand();
			esc.addPrintAndFeedLines((byte) 3);
			esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
			esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);// 设置为倍高倍宽
			esc.addText("停车场打印测试\n"); // 打印文字
			esc.addPrintAndLineFeed();
			esc.addText("\n\n\n"); // 打印文字
			Vector<Byte> datas = esc.getCommand(); // 发送数据
			Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
			byte[] bytes = ArrayUtils.toPrimitive(Bytes);
			String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
			int rs;
			try {
				rs = Appcontext.mGpService.sendEscCommand(0, sss);
				GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
				if (r != GpCom.ERROR_CODE.SUCCESS) {
					Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}
