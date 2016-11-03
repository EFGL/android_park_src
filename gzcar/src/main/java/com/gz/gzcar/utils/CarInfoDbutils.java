package com.gz.gzcar.utils;

import com.gz.gzcar.Database.CarInfoTable;
import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.MyApplication;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 车辆信息DBustils
 */
public class CarInfoDbutils {


	public static DbManager db = x.getDb(MyApplication.daoConfig);
	/**
	 *导入
	 * @param list
	 */
	public static int save_carinfo(ArrayList<CarInfoTable> list) {
		int reuslt = 0;
		for (CarInfoTable item : list) {
			try {
				db.save(item);
				reuslt++;
			} catch (DbException e) {
				e.printStackTrace();
				continue;
			}
		}
		return reuslt;
	}
	/**
	 * 添加
	 * @param carInfoTable
	 */
	public static void save_carinfo(CarInfoTable carInfoTable){
		try {
			db.save(carInfoTable);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除
	 * @param carInfoTable
	 */
	public static void del_user(CarInfoTable carInfoTable){
		try {
			db.delete(carInfoTable);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 修改 根据车号来修改
	 * @param carnum 车号
	 * @param carInfoTable 
	 */
	public static void update_user(String carnum,CarInfoTable carInfoTable){
		try {
			db.update(UserTable.class, WhereBuilder.b("car_no","=",carnum)
					,new KeyValue("car_type",carInfoTable.getCar_type())
			,new KeyValue("person_address", carInfoTable.getPerson_address())
			,new KeyValue("person_tel", carInfoTable.getPerson_tel())
			,new KeyValue("person_name", carInfoTable.getPerson_name())
			,new KeyValue("start_date", carInfoTable.getStart_date())
			,new KeyValue("stop_date", carInfoTable.getStop_date()));
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询全部
	 */
	public static List<CarInfoTable> findall(){
		try {
			List<CarInfoTable> list=db.selector(CarInfoTable.class).findAll();
			if(list.size()!=0){
				return list;
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据车号，查询单个信息
	 * @param carnum
	 * @return
	 */
	public static CarInfoTable finditem(String carnum){
		try {
			CarInfoTable table=db.selector(CarInfoTable.class).where("car_no", "=", carnum).findFirst();
			if(table!=null){
				return table;
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;

	}
}
