package org.libsdl.app;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;


public class PlayerUtil 
{
    private static final String TAG = "PlayerUtil";

    // Keep track of the paused state
    public static boolean mIsPaused, mIsSurfaceReady, mHasFocus;
    public static boolean mExitCalledFromJava;

    /** If shared libraries (e.g. SDL or the native application) could not be loaded. */
    public static boolean mBrokenLibraries;

    public static int mIntWidth, mIntHeight;
    
    // Main components
    protected static PlayerUtil mSingleton;
    protected static PlayerSurface mSurface;
    protected static View mTextEdit;
    protected static ViewGroup mLayout;
    protected static String[] args;
    
    private static Context context;
    private static Player mTestPlayer;
    private static Handler mHandler;
    public static boolean initOK = false;

    // This is what SDL runs in. It invokes SDL_main(), eventually
    public static Thread mSDLThread;
    
    // Audio
    protected static AudioTrack mAudioTrack;

    /**
     * This method is called by SDL before loading the native shared libraries.
     * It can be overridden to provide names of shared libraries to be loaded.
     * The default implementation returns the defaults. It never returns null.
     * An array returned by a new implementation must at least contain "SDL2".
     * Also keep in mind that the order the libraries are loaded may matter.
     * @return names of shared libraries to be loaded (e.g. "SDL2", "main").
     */
    protected String[] getLibraries() {
        return new String[] {
            "SDL2",
            "main"
        };
    }

    public String formatTime(int millis) {
		String time;
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;
		if (hour != 0)
			time = String.format("%02d:%02d:%02d", hour, minute, second);
		else
			time = String.format("%02d:%02d", minute, second);
		return time;
	}
    
    
    // Load the .so
    public void loadLibraries() {
       for (String lib : getLibraries()) {
    	   System.out.println("--------------------------lib="+lib);
          System.loadLibrary(lib);
       }
    }
    
    /**
     * This method is called by SDL using JNI.
     * This method is called by SDL before starting the native application thread.
     * It can be overridden to provide the arguments after the application name.
     * The default implementation returns an empty array. It never returns null.
     * @return arguments for the native application.
     */
    protected String[] getArguments() {
        return args;
    }
    
    public static void initialize() {
        // The static nature of the singleton and Android quirkyness force us to initialize everything here
        // Otherwise, when exiting the app and returning to it, these variables *keep* their pre exit values
        mSingleton = null;
        mSurface = null;
        mTextEdit = null;
        mLayout = null;
//        mJoystickHandler = null;
        mSDLThread = null;
        mAudioTrack = null;
        mExitCalledFromJava = false;
        mBrokenLibraries = false;
        mIsPaused = false;
        mIsSurfaceReady = false;
        mHasFocus = true;
    }

    public PlayerUtil(Context context,Handler _mHandler)
    {
    	PlayerUtil.context = context;
    	mHandler = _mHandler;
    	loadLibraries();
    	PlayerUtil.initialize();
    	mSingleton = this;
    	mSurface = new PlayerSurface(context,_mHandler);
    	SurfaceHolder holder = mSurface.getHolder();
    }
    
    public PlayerSurface getSDLSurface()
	{
		return mSurface;
	}
    

    /** Called by onPause or surfaceDestroyed. Even if surfaceDestroyed
     *  is the first to be called, mIsSurfaceReady should still be set
     *  to 'true' during the call to onPause (in a usual scenario).
     */
    public static void handlePause() {
    	Log.d(TAG, "handlePause() if = "+(!PlayerUtil.mIsPaused && PlayerUtil.mIsSurfaceReady));
    //	Log.d(TAG, "!PlayerActivity.mIsPaused = "+!PlayerUtil.mIsPaused);
    //	Log.d(TAG, "PlayerActivity.mIsSurfaceReady = "+PlayerUtil.mIsSurfaceReady);
        if (!PlayerUtil.mIsPaused && PlayerUtil.mIsSurfaceReady) {
            PlayerUtil.mIsPaused = true;
            PlayerUtil.nativePause();
            mSurface.enableSensor(Sensor.TYPE_ACCELEROMETER, false);
        }
    }

    /** Called by onResume or surfaceCreated. An actual resume should be done only when the surface is ready.
     * Note: Some Android variants may send multiple surfaceChanged events, so we don't need to resume
     * every time we get one of those events, only if it comes after surfaceDestroyed
     */
    public static void handleResume() {
    	Log.d(TAG, "handleResume() if = "+(PlayerUtil.mIsPaused && PlayerUtil.mIsSurfaceReady && PlayerUtil.mHasFocus));
//    	Log.d(TAG, "PlayerActivity.mIsPaused = "+PlayerActivity.mIsPaused);
//    	Log.d(TAG, "PlayerActivity.mIsSurfaceReady = "+PlayerActivity.mIsSurfaceReady);
//    	Log.d(TAG, "PlayerActivity.mHasFocus = "+PlayerActivity.mHasFocus);
        if ( PlayerUtil.mIsPaused && PlayerUtil.mIsSurfaceReady && PlayerUtil.mHasFocus) {
            PlayerUtil.mIsPaused = false;
            PlayerUtil.nativeResume();
            mSurface.handleResume();
        }
    }
        
    /* The native thread has finished */
    public static void handleNativeExit() {
        PlayerUtil.mSDLThread = null;
    }


    // Messages from the SDLMain thread
    static final int COMMAND_CHANGE_TITLE = 1;
    static final int COMMAND_UNUSED = 2;
    static final int COMMAND_TEXTEDIT_HIDE = 3;
    static final int COMMAND_SET_KEEP_SCREEN_ON = 5;

    protected static final int COMMAND_USER = 0x8000;

    /**
     * This method is called by SDL if SDL did not handle a message itself.
     * This happens if a received message contains an unsupported command.
     * Method can be overwritten to handle Messages in a different class.
     * @param command the command of the message.
     * @param param the parameter of the message. May be null.
     * @return if the message was handled in overridden method.
     */
    protected boolean onUnhandledMessage(int command, Object param) {
        return false;
    }
    
    public int getDuration(){
//    	System.out.println("wl PlayerActivity.PlayerGetDuration() = "+PlayerActivity.PlayerGetDuration());
		return PlayerUtil.PlayerGetDuration();
	}
	
	public int getCurrentPosition(){
		return PlayerUtil.PlayergetCurrentPosition();
	}
	
	public int getVideoStartTime(){
		return PlayerUtil.PlayergetVideoStartTime();
	}

    public void start(){
		if(isPlaying() == false)
			PlayerUtil.PlayerPause();
	}
	
	public void stop(){
		if(isPlaying() == true)
			PlayerUtil.PlayerPause();
		
	}
	
	public boolean isPlaying(){
		return PlayerUtil.PlayerIsPlay()==1?true:false;
	}
    
	public void seekTo(int msec){
		PlayerSeekTo(msec);
	}
    
    public boolean CheckIfPlayStart(){
        return PlayerUtil.isPlayStart()==1?true:false;
    }
    
    public boolean CheckIfPlayFinish(){
        return PlayerUtil.isPlayFinish()==1?true:false;
    }
    
    public boolean CheckIfInitStart(){
        return PlayerUtil.isInitStart()==1?true:false;
    }
    
    public int getBuffer(){
        return PlayerUtil.PlayerGetBuffer();
    }
    
    public int getLoadingProgress(){
        return PlayerUtil.PlayerGetLoadingProgress();
    }
	
	/**
     * A Handler class for Messages from native SDL applications.
     * It uses current Activities as target (e.g. for the title).
     * static to prevent implicit references to enclosing object.
     */
    protected static class SDLCommandHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Context context = PlayerUtil.context;
        	
            if (context == null) {
                Log.e(TAG, "error handling message, getContext() returned null");
                return;
            }
            switch (msg.arg1) {
            case COMMAND_CHANGE_TITLE:
                if (context instanceof Activity) {
                    ((Activity) context).setTitle((String)msg.obj);
                } else {
                    Log.e(TAG, "error handling message, getContext() returned no Activity");
                }
                break;
            case COMMAND_TEXTEDIT_HIDE:
                if (mTextEdit != null) {
                    mTextEdit.setVisibility(View.GONE);

                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mTextEdit.getWindowToken(), 0);
                }
                break;
            case COMMAND_SET_KEEP_SCREEN_ON:
            {
                Window window = ((Activity) context).getWindow();
                if (window != null) {
                    if ((msg.obj instanceof Integer) && (((Integer) msg.obj).intValue() != 0)) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                }
                break;
            }
            default:
//                if ((context instanceof PlayerActivity) && !((PlayerActivity) context).onUnhandledMessage(msg.arg1, msg.obj)) {
//                    Log.e(TAG, "error handling message, command is " + msg.arg1);
//                }
            }
        }
    }
    
	// Handler for the messages
    Handler commandHandler = new SDLCommandHandler();
	
	// Send a message from the SDLMain thread
    boolean sendCommand(int command, Object data) {
        Message msg = commandHandler.obtainMessage();
        msg.arg1 = command;
        msg.obj = data;
        return commandHandler.sendMessage(msg);
    }
    
    // C functions we call
	public static native String stringFromJNI();
	public static native int ffInit();
//    public static native int nativeInit(String args2, int[] car);
	public static native int nativeInit(String[] args2);
    public static native void nativeLowMemory();
    public static native int StartPlayer(String url);
    public static native void nativeQuit();
    public static native void nativePause();
    public static native void nativeResume();
    public static native void onNativeResize(int x, int y, int format);
    public static native int onNativePadDown(int device_id, int keycode);
    public static native int onNativePadUp(int device_id, int keycode);
    public static native void onNativeJoy(int device_id, int axis,
                                          float value);
    public static native void onNativeHat(int device_id, int hat_id,
                                          int x, int y);
    public static native void onNativeKeyDown(int keycode);
    public static native void onNativeKeyUp(int keycode);
    public static native void onNativeKeyboardFocusLost();
    public static native void onNativeTouch(int touchDevId, int pointerFingerId,
                                            int action, float x, 
                                            float y, float p);
    public static native void onNativeAccel(float x, float y, float z);
    public static native void onNativeSurfaceChanged();
    public static native void onNativeSurfaceDestroyed();
    public static native void nativeFlipBuffers();
    public static native int nativeAddJoystick(int device_id, String name, 
                                               int is_accelerometer, int nbuttons, 
                                               int naxes, int nhats, int nballs);
    public static native int nativeRemoveJoystick(int device_id);
    public static native String nativeGetHint(String name);
    //add by wl
    public static native int PlayerGetDuration();
    public static native int PlayergetCurrentPosition();
    public static native int PlayergetVideoStartTime();
    public static native int PlayerSeekTo(int msec);   
    public static native int PlayerPause();
    public static native int PlayerIsPlay();
    public static native int VirtualBack();
    
    public static native int isPlayStart();
    public static native int isPlayFinish();
    public static native int isInitStart();
    public static native int PlayerGetBuffer();
    public static native int PlayerGetLoadingProgress();
    public static native int SetBuffer(int bufSize);
    public static native int SetMinFrames(int bufSize);
    public static native int SetStartLoadingFrames(int bufSize);
    public static native int SetFinishLoadingFrames(int bufSize);
    public static native int SetRatio(int num, int den);


    /**
     * This method is called by SDL using JNI.
     */
    public static void flipBuffers() {
        PlayerUtil.nativeFlipBuffers();
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static boolean setActivityTitle(String title) {
        // Called from SDLMain() thread and can't directly affect the view
        return mSingleton.sendCommand(COMMAND_CHANGE_TITLE, title);
//    	return false;
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static Surface getNativeSurface() {
        return PlayerUtil.mSurface.getNativeSurface();
    }

    public static void startPlay() {
		Log.d(TAG, "====================================startPlay");
	}
    
  public static void loadingStart() {
		Log.d(TAG, "====================================loadingStart");
		if((PlayerUtil.PlayerIsPlay()==1?true:false) == true)
			PlayerUtil.PlayerPause();
		if(mHandler!=null){
    		mHandler.sendEmptyMessage(Player.MSG_LOAD_UNFINISHED);
    	}
	}
    
	public static void loadingFinish() {
		Log.d(TAG, "====================================loadingFinish");
		if((PlayerUtil.PlayerIsPlay()==1?true:false) == false)
			PlayerUtil.PlayerPause();
		if(mHandler!=null){
    		mHandler.sendEmptyMessage(Player.MSG_LOAD_FINISHED);
    	}
		Log.d(TAG, "buffer="+PlayerUtil.PlayerGetBuffer());
	}
    public static void stopPlay() {
		Log.d(TAG, "====================================stopPlay");
//		PlayerUtil.VirtualBack();
	}
    
    public final int MSG_FINISH = 7;
    public static void urlError(int errorCode){
    	Log.e(TAG, "====================================urlError = "+ errorCode);
    	PlayerUtil.VirtualBack();
    	if(mHandler!=null && errorCode==403){
    		mHandler.sendEmptyMessage(Player.MSG_URL_ERROR);
    	}
    }  
    
    public static void sendPlayerStatus(int messageType, long timestamp, long message1, long message2) {
//		Log.d(TAG, "====================================sendPlayerStatus = "
//				+ messageType + "+" + timestamp + "+" + message1 + "+" + message2);
		
		Message msg = new Message();  
        msg.arg1 = messageType;
        Bundle bundle = new Bundle(); 
        bundle.putLong("timestamp", timestamp);
        bundle.putLong("message1", message1);
        bundle.putLong("message2", message2);
        msg.setData(bundle);
        msg.what = Player.MSG_PLAYER_STATUS;
        mHandler.sendMessage(msg);
		//PlayerUtil.VirtualBack();
	}
    
    // Audio

    /**
     * This method is called by SDL using JNI.
     */
    public static int audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
        int channelConfig = isStereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int audioFormat = is16Bit ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
        int frameSize = (isStereo ? 2 : 1) * (is16Bit ? 2 : 1);
        
        Log.v(TAG, "SDL audio: wanted " + (isStereo ? "stereo" : "mono") + " " + (is16Bit ? "16-bit" : "8-bit") + " " + (sampleRate / 1000f) + "kHz, " + desiredFrames + " frames buffer");
        
        // Let the user pick a larger buffer if they really want -- but ye
        // gods they probably shouldn't, the minimums are horrifyingly high
        // latency already
        desiredFrames = Math.max(desiredFrames, (AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat) + frameSize - 1) / frameSize);
        
        if (mAudioTrack == null) {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                    channelConfig, audioFormat, desiredFrames * frameSize, AudioTrack.MODE_STREAM);
            //MODE_STATIC
            // Instantiating AudioTrack can "succeed" without an exception and the track may still be invalid
            // Ref: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/AudioTrack.java
            // Ref: http://developer.android.com/reference/android/media/AudioTrack.html#getState()
            
            if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
                Log.e(TAG, "Failed during initialization of Audio Track");
                mAudioTrack = null;
                return -1;
            }
            
            mAudioTrack.play();
        }
       
        Log.v(TAG, "SDL audio: got " + ((mAudioTrack.getChannelCount() >= 2) ? "stereo" : "mono") + " " + ((mAudioTrack.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) ? "16-bit" : "8-bit") + " " + (mAudioTrack.getSampleRate() / 1000f) + "kHz, " + desiredFrames + " frames buffer");
        
        return 0;
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void audioWriteShortBuffer(short[] buffer) {
        for (int i = 0; i < buffer.length; ) {
            int result = 0;
			try {
				result = mAudioTrack.write(buffer, i, buffer.length - i);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
				
			}
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    // Nom nom
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(short)");
                return;
            }
        }
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void audioWriteByteBuffer(byte[] buffer) {
        for (int i = 0; i < buffer.length; ) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    // Nom nom
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(byte)");
                return;
            }
        }
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void audioQuit() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack = null;
        }
    }

    // Input

    /**
     * This method is called by SDL using JNI.
     * @return an array which may be empty but is never null.
     */
    public static int[] inputGetInputDeviceIds(int sources) {
        int[] ids = InputDevice.getDeviceIds();
        int[] filtered = new int[ids.length];
        int used = 0;
        for (int i = 0; i < ids.length; ++i) {
            InputDevice device = InputDevice.getDevice(ids[i]);
            if ((device != null) && ((device.getSources() & sources) != 0)) {
                filtered[used++] = device.getId();
            }
        }
        return Arrays.copyOf(filtered, used);
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void pollInputDevices() {
        if (PlayerUtil.mSDLThread != null) {
//            mJoystickHandler.pollInputDevices();
        }
    }

    // APK extension files support

    /** com.android.vending.expansion.zipfile.ZipResourceFile object or null. */
    private Object expansionFile;

    /** com.android.vending.expansion.zipfile.ZipResourceFile's getInputStream() or null. */
    private Method expansionFileMethod;

    /**
     * This method is called by SDL using JNI.
     */
    public InputStream openAPKExtensionInputStream(String fileName) throws IOException {
        // Get a ZipResourceFile representing a merger of both the main and patch files
        if (expansionFile == null) {
            Integer mainVersion = Integer.valueOf(nativeGetHint("SDL_ANDROID_APK_EXPANSION_MAIN_FILE_VERSION"));
            Integer patchVersion = Integer.valueOf(nativeGetHint("SDL_ANDROID_APK_EXPANSION_PATCH_FILE_VERSION"));

            try {
                // To avoid direct dependency on Google APK extension library that is
                // not a part of Android SDK we access it using reflection
                expansionFile = Class.forName("com.android.vending.expansion.zipfile.APKExpansionSupport")
                    .getMethod("getAPKExpansionZipFile", Context.class, int.class, int.class)
                    .invoke(null, this, mainVersion, patchVersion);

                expansionFileMethod = expansionFile.getClass()
                    .getMethod("getInputStream", String.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                expansionFile = null;
                expansionFileMethod = null;
            }
        }

        // Get an input stream for a known file inside the expansion file ZIPs
        InputStream fileStream;
        try {
            fileStream = (InputStream)expansionFileMethod.invoke(expansionFile, fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
            fileStream = null;
        }

        if (fileStream == null) {
            throw new IOException();
        }

        return fileStream;
    }

    // Messagebox

    /** Result of current messagebox. Also used for blocking the calling thread. */
    protected final int[] messageboxSelection = new int[1];

    /** Id of current dialog. */
    protected int dialogs = 0;

    /**
     * This method is called by SDL using JNI.
     * Shows the messagebox from UI thread and block calling thread.
     * buttonFlags, buttonIds and buttonTexts must have same length.
     * @param buttonFlags array containing flags for every button.
     * @param buttonIds array containing id for every button.
     * @param buttonTexts array containing text for every button.
     * @param colors null for default or array of length 5 containing colors.
     * @return button id or -1.
     */
    public int messageboxShowMessageBox(
            final int flags,
            final String title,
            final String message,
            final int[] buttonFlags,
            final int[] buttonIds,
            final String[] buttonTexts,
            final int[] colors) {

        messageboxSelection[0] = -1;

        // sanity checks

        if ((buttonFlags.length != buttonIds.length) && (buttonIds.length != buttonTexts.length)) {
            return -1; // implementation broken
        }

        // collect arguments for Dialog

        final Bundle args = new Bundle();
        args.putInt("flags", flags);
        args.putString("title", title);
        args.putString("message", message);
        args.putIntArray("buttonFlags", buttonFlags);
        args.putIntArray("buttonIds", buttonIds);
        args.putStringArray("buttonTexts", buttonTexts);
        args.putIntArray("colors", colors);


        // block the calling thread

        synchronized (messageboxSelection) {
            try {
                messageboxSelection.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                return -1;
            }
        }

        // return selected value

        return messageboxSelection[0];
    }

    public void onPause() {
    	Log.e(TAG, "PlayerActivity.mBrokenLibraries = "+PlayerUtil.mBrokenLibraries);
		if (PlayerUtil.mBrokenLibraries) {
			return;
		}

		PlayerUtil.handlePause();
	}
    
    public void onResume() {
    	if (PlayerUtil.mBrokenLibraries) {
			return;
		}

		PlayerUtil.handleResume();
	}
    
    public void onDestroy() {
    	if (PlayerUtil.mBrokenLibraries) {
			// Reset everything in case the user re opens the app
			PlayerUtil.initialize();
			return;
		}

		// Send a quit message to the application
		PlayerUtil.mExitCalledFromJava = true;
		PlayerUtil.nativeQuit();

		// Now wait for the SDL thread to quit
		if (PlayerUtil.mSDLThread != null) {
			try {
				PlayerUtil.mSDLThread.join();
			} catch (Exception e) {
				Log.v(TAG, "Problem stopping thread: " + e);
			}
			PlayerUtil.mSDLThread = null;

			Log.v(TAG, "Finished waiting for SDL thread");
		}

		// Reset everything in case the user re opens the app
		PlayerUtil.initialize();
	}
}

/**
    Simple nativeInit() runnable
*/
class SDLMain implements Runnable {
	
	private String mUrl;
	private String width;
	private String height;
	private String liveVideo;
	private String lastPlayPos;
	
	public SDLMain(String _url,String _width,String _height,String _liveVideo, String _lastPlayPos)
	{
		mUrl = _url;
		width = _width;
		height = _height;
		liveVideo = _liveVideo;
		lastPlayPos = _lastPlayPos;
	}
	
    @Override
    public void run() {
    	String[] args = new String[5];
    	args[0] = mUrl;
    	args[1] = width;
    	args[2] = height;
    	args[3] = liveVideo;
    	args[4] = lastPlayPos;
    	
    	PlayerUtil.SetBuffer(150*1024);
    	PlayerUtil.SetMinFrames(50);//org=5
    	PlayerUtil.SetStartLoadingFrames(2);//if that size is 0, pause will not happen when buffer is low 
    	PlayerUtil.SetFinishLoadingFrames(10);//if that size is bigger than that MinFrames was set the resume will not happen
    	Log.e("wl", "wl mUrl = "+args[3]);
    	PlayerUtil.nativeInit(args);
    }
}




/* This is a fake invisible editor view that receives the input and defines the
 * pan&scan region
 */
class DummyEdit extends View implements View.OnKeyListener {
    InputConnection ic;

    public DummyEdit(Context context) {
        super(context);
        setFocusableInTouchMode(true);
        setFocusable(true);
        setOnKeyListener(this);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        // This handles the hardware keyboard input
        if (event.isPrintingKey()) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                ic.commitText(String.valueOf((char) event.getUnicodeChar()), 1);
            }
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            PlayerUtil.onNativeKeyDown(keyCode);
            return true;
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            PlayerUtil.onNativeKeyUp(keyCode);
            return true;
        }

        return false;
    }
        
    //
    @Override
    public boolean onKeyPreIme (int keyCode, KeyEvent event) {
        // As seen on StackOverflow: http://stackoverflow.com/questions/7634346/keyboard-hide-event
        // FIXME: Discussion at http://bugzilla.libsdl.org/show_bug.cgi?id=1639
        // FIXME: This is not a 100% effective solution to the problem of detecting if the keyboard is showing or not
        // FIXME: A more effective solution would be to change our Layout from AbsoluteLayout to Relative or Linear
        // FIXME: And determine the keyboard presence doing this: http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
        // FIXME: An even more effective way would be if Android provided this out of the box, but where would the fun be in that :)
        if (event.getAction()==KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            if (PlayerUtil.mTextEdit != null && PlayerUtil.mTextEdit.getVisibility() == View.VISIBLE) {
                PlayerUtil.onNativeKeyboardFocusLost();
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        ic = new SDLInputConnection(this, true);

        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
                | 33554432 /* API 11: EditorInfo.IME_FLAG_NO_FULLSCREEN */;

        return ic;
    }
}

class SDLInputConnection extends BaseInputConnection {

    public SDLInputConnection(View targetView, boolean fullEditor) {
        super(targetView, fullEditor);

    }

    @Override
    public boolean sendKeyEvent(KeyEvent event) {

        /*
         * This handles the keycodes from soft keyboard (and IME-translated
         * input from hardkeyboard)
         */
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.isPrintingKey()) {
                commitText(String.valueOf((char) event.getUnicodeChar()), 1);
            }
            PlayerUtil.onNativeKeyDown(keyCode);
            return true;
        } else if (event.getAction() == KeyEvent.ACTION_UP) {

            PlayerUtil.onNativeKeyUp(keyCode);
            return true;
        }
        return super.sendKeyEvent(event);
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {

        nativeCommitText(text.toString(), newCursorPosition);

        return super.commitText(text, newCursorPosition);
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {

        nativeSetComposingText(text.toString(), newCursorPosition);

        return super.setComposingText(text, newCursorPosition);
    }

    public native void nativeCommitText(String text, int newCursorPosition);

    public native void nativeSetComposingText(String text, int newCursorPosition);

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {       
        // Workaround to capture backspace key. Ref: http://stackoverflow.com/questions/14560344/android-backspace-in-webview-baseinputconnection
        if (beforeLength == 1 && afterLength == 0) {
            // backspace
            return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
        }

        return super.deleteSurroundingText(beforeLength, afterLength);
    }
}
