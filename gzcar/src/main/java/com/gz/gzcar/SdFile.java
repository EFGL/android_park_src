package com.gz.gzcar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * 只找一级目录
 *@ClassName: SdFile 
 * @author A18ccms a18ccms_gmail_com
 * @date 2016-11-28 下午3:44:31 
 */
public class SdFile {
	private  List<String> myfilelist=new ArrayList<String>();
	public List<String>  getallfilemessage(String path,String type){
		File file=new File(path);
		File[] files = file.listFiles();
		if(files!=null){
			for(File myfile:files){
				if(!myfile.isDirectory()){
					String filename=myfile.getName();
					if(filename.indexOf(type)!=-1){
						myfilelist.add(myfile.getAbsolutePath()+"");
					}
				}
			}
			return myfilelist;
		}
		return null;
	}
}
