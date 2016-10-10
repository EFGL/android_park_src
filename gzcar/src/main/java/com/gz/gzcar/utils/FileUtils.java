package com.gz.gzcar.utils;

/**
 * Created by Administrator on 2016/10/9.
 */

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
    private String SDPATH;
    public String getSDPATH() {
        return SDPATH;
    }
    public FileUtils() {
        //得到当前外部存储设备的目录
        // /SDCARD
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }
   
    public File creatSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    
    public File creatSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdirs();
        return dir;
    }

    
    public boolean isFileExist(String fileName){
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

   
    public File write2SDFromBytes(String path,String fileName,byte[] bytes){
        File file = null;
        OutputStream output = null;
        try{
            creatSDDir(path);
            file = creatSDFile(path + fileName);
            output = new FileOutputStream(file);
            output.write( bytes);
            output.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                output.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return file;
    }
    public String savePicture(byte[] bytes)
    {
        Date date= new Date();
        String dateStr=new SimpleDateFormat("yyyyMMdd").format(date);
        String fileName=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(date)+".bmp";
        String path="capture/"+dateStr+"/";
        try {
            FileUtils fileUtils = new FileUtils();
            if(fileUtils.isFileExist(path+fileName)) {
                return null;
            } else {
                File resultFile = fileUtils.write2SDFromBytes(path,fileName,bytes);
                if (resultFile == null)
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
        return  path+fileName;
    }

}