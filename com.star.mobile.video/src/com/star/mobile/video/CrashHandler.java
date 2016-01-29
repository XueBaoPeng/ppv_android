package com.star.mobile.video;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import com.star.mobile.video.activity.WelcomeActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.util.app.GA;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	
	public static final String TAG = "CrashHandler";
	
	//系统默认的UncaughtException处理类 
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	//CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	//程序的Context对象
	private Context mContext;
	//用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	//用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	StarApplication application;
	private Dialog mDialog;
	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}
	
	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context,StarApplication application) {
		mContext = context;
		this.application=application;
		//获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		}
//		else {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				Log.e(TAG, "error : ", e);
//			}
			
//		handleException(ex);
//		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		//使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				CommonUtil.getInstance().showPromptSystemDialog(mContext,
						null, mContext.getString(R.string.crash_message), mContext.getString(R.string.confirm), null, new PromptDialogClickListener() {
					
					@Override
					public void onConfirmClick() {
						 Intent intent = new Intent(mContext, WelcomeActivity.class);  
				         PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent,    
				                    Intent.FLAG_ACTIVITY_NEW_TASK);                                                 
				         AlarmManager mgr = (AlarmManager)application.getSystemService(Context.ALARM_SERVICE);    
				         mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,restartIntent); // 1秒钟后重启应用   
						//退出程序
						System.exit(1);
					}
					
					@Override
					public void onCancelClick() {
						
					}
				});
				//收集设备参数信息 
				collectDeviceInfo(mContext);
				//保存日志文件 
				saveCrashInfo2File(ex);
				Looper.loop();
			}
		}.start();
		
		return true;
	}
	
	/**
	 * 收集设备参数信息
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return	返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		GA.sendEvent("android", "crash", result, 1);
		
		Log.e("System error", result); 
//		sb.append(result);
//		try {
//			long timestamp = System.currentTimeMillis();
//			String time = formatter.format(new Date());
//			String fileName = "crash-" + time + "-" + timestamp + ".log";
//			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//				String path = Environment.getExternalStorageDirectory() + File.separator + "startimes/crash/";
//				File dir = new File(path);
//				if (!dir.exists()) {
//					dir.mkdirs();
//				}
//				FileOutputStream fos = new FileOutputStream(path + fileName);
//				fos.write(sb.toString().getBytes());
//				fos.close();
//			}
//			return fileName;
//		} catch (Exception e) {
//			Log.e(TAG, "an error occured while writing file...", e);
//		}
		return null;
	}
}
