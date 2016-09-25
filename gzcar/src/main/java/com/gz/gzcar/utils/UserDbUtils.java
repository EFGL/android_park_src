package com.gz.gzcar.utils;

import com.gz.gzcar.Database.UserTable;
import com.gz.gzcar.MyApplication;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * 
 *UserTable 工具类
 */
public class UserDbUtils {

	public static DbManager db =x.getDb(MyApplication.daoConfig);
	/**
	 * 添加
	 * @param userTable
	 */
	public static void save_user(UserTable userTable){
		try {
			db.save(userTable);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除
	 * @param userTable
	 */
	public static void del_user(UserTable userTable){
		try {
			db.delete(userTable);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 登陆
	 * @param username 账号
	 * @param password 密码
	 * @return
	 */
	public static UserTable login(String username,String password){
		try {
			UserTable table=db.selector(UserTable.class).where("userName", "=", username).and("password", "=", password).findFirst();
			if(table!=null){
				return table;
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 修改密码
	 * @param username 用户名
	 * @param password 需要修改的密码
	 */
	public static void update_user(String username,String password){
		try {
			db.update(UserTable.class,WhereBuilder.b("userName","=",username),new KeyValue("password",password));
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询全部
	 */
	public static List<UserTable> findall(){
		try {
			List<UserTable> list=db.selector(UserTable.class).findAll();
			if(list.size()!=0){
				return list;
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}
}
