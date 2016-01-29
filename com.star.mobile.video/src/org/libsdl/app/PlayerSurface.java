package org.libsdl.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.star.mobile.video.util.LinkUtil;

/**
 * SDLSurface. This is what we draw on, so we need to know when it's created in
 * order to do anything useful.
 * 
 * Because of this, that's where we set up the SDL thread
 */

public class PlayerSurface extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener,View.OnKeyListener
{

	// Sensors
	String TAG="PlayerSurface";
	protected static SensorManager mSensorManager;
	protected static Display mDisplay;

	private OnInfoListener mOnInfoListener;
	
	// Keep track of the surface size to normalize touch events
	protected static float mWidth, mHeight;
//	public static int mIntWidth, mIntHeight;
	public static int sdlFormat;
	private static Uri url;
	private static int liveVideo;
	private static int lastPlayPos;

	private static AudioManager mAudioManager;
	public static Thread sdlThread;
	
	private Handler mHandler;
	
	public final int MSG_FINISH = 7;
	
	// Startup
	public PlayerSurface(Context context,Handler _mHandler)
	{
		super(context);
		getHolder().addCallback(this);
		this.mHandler = _mHandler;

		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		setOnKeyListener(this);

		mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		// Some arbitrary defaults to avoid a potential division by zero
		mWidth = 1.0f;
		mHeight = 1.0f;
	}

	public void handleResume()
	{
		Log.d(TAG, "wl handleResume");
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		setOnKeyListener(this);
		enableSensor(Sensor.TYPE_ACCELEROMETER, true);
	}

	public Surface getNativeSurface()
	{
		return getHolder().getSurface();
	}

	// Called when we have a valid drawing surface
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.v(TAG, "wl surfaceCreated");
		holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
	}

	// Called when we lose the surface
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.v(TAG, "surfaceDestroyed()");
		// Call this *before* setting mIsSurfaceReady to 'false'
		PlayerUtil.handlePause();
		PlayerUtil.mIsSurfaceReady = false;
		PlayerUtil.onNativeSurfaceDestroyed();
	}

	// Called when the surface is resized
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		Log.v(TAG, "surfaceChanged()");

	    sdlFormat = 0x15151002; // SDL_PIXELFORMAT_RGB565 by default
		switch (format)
		{
		case PixelFormat.A_8:
			Log.v(TAG, "pixel format A_8");
			break;
		case PixelFormat.LA_88:
			Log.v(TAG, "pixel format LA_88");
			break;
		case PixelFormat.L_8:
			Log.v(TAG, "pixel format L_8");
			break;
		case PixelFormat.RGBA_4444:
			Log.v(TAG, "pixel format RGBA_4444");
			sdlFormat = 0x15421002; // SDL_PIXELFORMAT_RGBA4444
			break;
		case PixelFormat.RGBA_5551:
			Log.v(TAG, "pixel format RGBA_5551");
			sdlFormat = 0x15441002; // SDL_PIXELFORMAT_RGBA5551
			break;
		case PixelFormat.RGBA_8888:
			Log.v(TAG, "pixel format RGBA_8888");
			sdlFormat = 0x16462004; // SDL_PIXELFORMAT_RGBA8888
			break;
		case PixelFormat.RGBX_8888:
			Log.v(TAG, "pixel format RGBX_8888");
			sdlFormat = 0x16261804; // SDL_PIXELFORMAT_RGBX8888
			break;
		case PixelFormat.RGB_332:
			Log.v(TAG, "pixel format RGB_332");
			sdlFormat = 0x14110801; // SDL_PIXELFORMAT_RGB332
			break;
		case PixelFormat.RGB_565:
			Log.v(TAG, "pixel format RGB_565");
			sdlFormat = 0x15151002; // SDL_PIXELFORMAT_RGB565
			break;
		case PixelFormat.RGB_888:
			Log.v(TAG, "pixel format RGB_888");
			// Not sure this is right, maybe SDL_PIXELFORMAT_RGB24 instead?
			sdlFormat = 0x16161804; // SDL_PIXELFORMAT_RGB888
			break;
		default:
			Log.v(TAG, "pixel format unknown " + format);
			break;
		}

		mWidth = width;
		mHeight = height;
		PlayerUtil.mIntWidth = width;
		PlayerUtil.mIntHeight = height;
		
		PlayerUtil.onNativeResize(width, height, sdlFormat);
		Log.v("SDL", "wl surfaceChanged Window size: " + width + "x" + height);

		// Set mIsSurfaceReady to 'true' *before* making a call to handleResume
		PlayerUtil.mIsSurfaceReady = true;
		PlayerUtil.onNativeSurfaceChanged();
		Log.v("SDL", "wl 111111");
		
		if (PlayerSurface.url != null)
		{
			if (PlayerUtil.mSDLThread == null)
			{
				// This is the entry point to the C app.
				// Start up the C app thread and enable sensor input for the first
				// time
				sdlThread = new Thread(new SDLMain(PlayerSurface.url.toString(),Integer.toString(width),Integer.toString(height),Integer.toString(PlayerSurface.liveVideo),Integer.toString(lastPlayPos)), "SDLThread");
				enableSensor(Sensor.TYPE_ACCELEROMETER, true);
				sdlThread.start();

				// Set up a listener thread to catch when the native thread ends
				PlayerUtil.mSDLThread = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							sdlThread.join();
							Log.d(TAG, "sdlThread.join()");
						} catch (Exception e)
						{
						} finally
						{
							// Native thread has finished
							if (!PlayerUtil.mExitCalledFromJava)
							{
								Message msg = new Message();
								msg.what = MSG_FINISH;
								PlayerSurface.this.mHandler.sendMessage(msg);
							}
						}
					}
				}, "SDLThreadListener");
				PlayerUtil.mSDLThread.start();
			}
		}
	}
	
	public void setVideoURI(Uri uri)
	{		
		Log.v(TAG, "wl setVideoURI()");
		//		PlayerActivity.mIsPaused = true;
		if(LinkUtil.isValidURL(uri.toString())) { 
			PlayerSurface.url = uri;
			PlayerSurface.liveVideo = 0;
		}else{
			Log.e(TAG, "Invalid video url " + uri.toString());
			surfaceDestroyed(null);
			return;

		}

	}
	
	public void SetLastPlayPos(int _lastPlayPos) {
		this.lastPlayPos = _lastPlayPos*1000;
	}
	
	public void setLiveVideoUrl(Uri uri)
	{
//		PlayerActivity.mIsPaused = true;
		PlayerSurface.url = uri;
		PlayerSurface.liveVideo = 1;
		Log.d(TAG, "wl SDLSurface.liveVideo = "+PlayerSurface.liveVideo);
		Log.v(TAG, "wl setLiveVideoUrl()");
	}

	// unused
	@Override
	public void onDraw(Canvas canvas)
	{
	}


	// Key events
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event)
	{
		// Dispatch the different events depending on where they come from
		// Some SOURCE_DPAD or SOURCE_GAMEPAD are also SOURCE_KEYBOARD
		// So, we try to process them as DPAD or GAMEPAD events first, if that
		// fails we try them as KEYBOARD

//		if ((event.getSource() & InputDevice.SOURCE_KEYBOARD) != 0)
//		{
//			if (event.getAction() == KeyEvent.ACTION_DOWN)
//			{
//				// Log.v(TAG, "key down: " + keyCode);
//				PlayerActivity.onNativeKeyDown(keyCode);
//				return true;
//			} else if (event.getAction() == KeyEvent.ACTION_UP)
//			{
//				// Log.v(TAG, "key up: " + keyCode);
//				PlayerActivity.onNativeKeyUp(keyCode);
//				return true;
//			}
//		}
		
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			 VolumeUp();
			 return true;
			 //return false;
		 }
		 else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			 VolumeDown();
			 return true;
		 }
		return false;
	}

	public void VolumeUp(){
		 mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,   AudioManager.FX_FOCUS_NAVIGATION_UP);
	}

	public void VolumeDown(){
		 //mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
		 mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,   AudioManager.FX_FOCUS_NAVIGATION_UP);
	}
	
	public void setOnStartListner(OnInfoListener l) {
		mOnInfoListener = l;
	}
	
	// Sensor events
	public void enableSensor(int sensortype, boolean enabled)
	{
		// TODO: This uses getDefaultSensor - what if we have >1 accels?
		/*
		 * if (enabled) { mSensorManager.registerListener(this,
		 * mSensorManager.getDefaultSensor(sensortype),
		 * SensorManager.SENSOR_DELAY_GAME, null); } else {
		 * mSensorManager.unregisterListener(this,
		 * mSensorManager.getDefaultSensor(sensortype)); }
		 */
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// TODO
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			float x, y;
			switch (mDisplay.getRotation())
			{
			case Surface.ROTATION_90:
				x = -event.values[1];
				y = event.values[0];
				break;
			case Surface.ROTATION_270:
				x = event.values[1];
				y = -event.values[0];
				break;
			case Surface.ROTATION_180:
				x = -event.values[1];
				y = -event.values[0];
				break;
			default:
				x = event.values[0];
				y = event.values[1];
				break;
			}
			PlayerUtil.onNativeAccel(-x / SensorManager.GRAVITY_EARTH, y / SensorManager.GRAVITY_EARTH, event.values[2] / SensorManager.GRAVITY_EARTH - 1);
		}
	}

}
