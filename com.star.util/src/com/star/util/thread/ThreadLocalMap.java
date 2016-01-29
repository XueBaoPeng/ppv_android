package com.star.util.thread;

import java.util.HashMap;
import java.util.Map;

import com.star.util.BuildConfig;

import android.util.Log;


/**
 * 
 * ThreadLocal Context
 * 
 * @author yaohw
 * 
 */
public class ThreadLocalMap{
	private final static String TAG = "ThreadLocalMap";
	protected final static ThreadLocal<Map<Object,Object>> threadContext = new MapThreadLocal();
	
	private ThreadLocalMap(){};
	
	public static void put(Object key,Object value){
		getContextMap().put(key,value);
	}
	
	public static Object remove(Object key){
		return getContextMap().remove(key);
	}
	
	public static Object get(Object key){
		return getContextMap().get(key);
	}
	
	public static boolean containsKey(Object key){
		return getContextMap().containsKey(key);
	}
	
	private static class MapThreadLocal extends ThreadLocal<Map<Object,Object>> {
        protected Map<Object,Object> initialValue() {
        	return new HashMap<Object,Object>() {
				
        		private static final long serialVersionUID = 3637958959138295593L;
				
				public Object put(Object key, Object value) {
                    if (BuildConfig.DEBUG) {
                        if (containsKey(key)) {
                        	Log.d(TAG,"Overwritten attribute to thread context: " + key
                                + " = " + value);
                        } else {
                        	Log.d(TAG,"Added attribute to thread context: " + key + " = "
                                + value);
                        }
                    }

                    return super.put(key, value);
                }
            };
        }
    }
	
	/**
     *
     * @return thread context 
     */
    protected static Map<Object,Object> getContextMap() {
        return (Map<Object,Object>) threadContext.get();
    }
	
    
    /**
     */
    
    public static void reset(){
    	getContextMap().clear();
    }
}
