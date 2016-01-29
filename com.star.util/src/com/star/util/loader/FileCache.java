package com.star.util.loader;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.util.Log;
  
public class FileCache {
	private String TAG = this.getClass().getName();
    private File cacheDir;
  
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"starTempImages");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            if(!cacheDir.mkdirs()){
            	Log.e(TAG, "make dir error.");
            }
    }
  
    public File getFile(String url){
        String filename=String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        	try {
				f.createNewFile();
			} catch (IOException e) {
				Log.e("FileCache", "File create error.",e);
			}
        return f;
  
    }
  
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }
  
}