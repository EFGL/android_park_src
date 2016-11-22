package com.gz.gzcar.server;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 神保佑无 BUG
 * <p/>
 * <p/>
 * 项目名称：MyViewGroup
 * 类描述：
 * 创建人：jindanke
 * 创建时间：16/11/22 下午3:26
 * 修改人：jindanke
 * 修改时间：16/11/22 下午3:26
 * 修改备注：
 */
public class FileUtils {
    public static boolean show=true;

    public  void witefile(String msg,String sendtime){
        //创建文件夹
        createDir("/sdcard/GZ_CAR");
        //创建文件
        createfile("/sdcard/GZ_CAR/"+sendtime+".txt");
        //开始写文件
        writemessage("/sdcard/GZ_CAR/"+sendtime+".txt",msg+"\n");
    }
    /**
     * 创建文件夹
     * @param destDirName
     * @return
     */
    private boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            showlog("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            showlog("创建目录" + destDirName + "成功！");
            return true;
        } else {
            showlog("创建目录" + destDirName + "失败！");
            return false;
        }
    }

    /**
     * 创建文件
     * @param filepath
     */
    private void createfile(String filepath){
        File file=new File(filepath);
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 追加写文件
     * @param file
     * @param msg
     */
    private void writemessage(String file,String msg){
        try {
            FileWriter fileWriter=new FileWriter(file,true);
            fileWriter.write(msg);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void showlog(String msg){
        if(show)
            Log.i("chenghao","输出内容："+msg);
    }
}
