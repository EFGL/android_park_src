package com.gz.gzcar;

import android.app.Application;
import android.widget.Toast;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;

/**
 * Created by Endeavor on 2016/8/18.
 */
public class MyApplication extends Application {

    public static String Baseurl="http://221.204.11.69:3002/api/v1/";
    public static String mDBName = "tenement_passing_manager.db";
    public static DbManager.DaoConfig daoConfig;
    @Override
    public void onCreate() {
        super.onCreate();
        org.xutils.x.Ext.init(this);
//        x.Ext.init(this);
//        File file=new File(Environment.getExternalStorageDirectory().getPath());
        daoConfig = new DbManager.DaoConfig()
                .setDbName(mDBName)
//                .setDbDir(file)
                .setDbVersion(1)
                .setAllowTransaction(true)
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                        Toast.makeText(getApplicationContext(), table.getName() + "创建了...", Toast.LENGTH_SHORT).show();

                    }
                })
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {

                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();

                        Toast.makeText(getApplicationContext(), "数据库打开了...", Toast.LENGTH_SHORT).show();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        //
                        Toast.makeText(getApplicationContext(), "数据库升级了...", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
