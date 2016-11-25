package com.gz.gzcar.server;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 神保佑无 BUG
 * 创建人：chenghao
 * 修改人：chenghao
 * 修改备注：
 */
public class FileUtils {
    public static boolean show=false;

    public  void witefile(String msg,String sendtime){
        //创建文件夹
        createDir("/sdcard/GZ_CAR");
        //创建文件
        createfile("/sdcard/GZ_CAR/"+sendtime+".txt");
        //开始写文件
        writemessage("/sdcard/GZ_CAR/"+sendtime+".txt",msg);
    }
    /**
     * 创建文件夹
     * @param destDirName
     * @return
     */
    private boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
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

    /**
     * 删除文件
     * @param path
     */
    public void delmyfile(String path,Long filesize){
        File myf=new File(path);
        if (myf.exists()) {//判断文件是否存在
            if (myf.isFile()) {
                //判断是否是文件
            } else if (myf.isDirectory()) {//否则如果它是一个目录
                File[] files = myf.listFiles();//声明目录下所有的文件 files[];
                //判断这个files 有几个目录.目前只删除1-2个文件夹
                for (int c=0;c<files.length;c++){
                    if(ifdelmyfile(filesize)){
                        deleteFile(files[c]);
                    }else {
                        showlog("容量已经满足要求");
                        return;
                    }
                }
            }
        }
    }


    //递归删除文件夹
    private void deleteFile(File file) {
        if (file.exists()) {//判断文件是否存在
            if (file.isFile()) {//判断是否是文件
                file.delete();//删除文件
            } else if (file.isDirectory()) {//否则如果它是一个目录
                File[] files = file.listFiles();//声明目录下所有的文件 files[];
                for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件
                    this.deleteFile(files[i]);//把每个文件用这个方法进行迭代
                }
                file.delete();//删除文件夹
                showlog("删除完成");
            }
        } else {
            showlog("所删除的文件不存在");
        }
    }

    /**
     * sd卡剩余
     * @return
     */
    private long getSDFreeSize(){
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize)/1024 /1024; //单位MB
    }

    /**
     * sd卡总
     * @return
     */
    private  long getSDAllSize(){
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //获取所有数据块数
        long allBlocks = sf.getBlockCount();
        //返回SD卡大小
        //return allBlocks * blockSize; //单位Byte
        //return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize)/1024/1024; //单位MB
    }


    /**
     * 判断是否要删除sd卡里面的一些数据
     * @param sdsize
     * @return
     */
    public Boolean ifdelmyfile(Long sdsize){
        Long mufreesize=getSDFreeSize();
        if(mufreesize>=sdsize){
            return false;
        }
        return  true;
    }
}
