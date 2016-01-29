package com.star.mobile.video.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;

public class RingtoneUtil {
	private List<String> list_ringtone = new ArrayList<String>();
	private List<String> list_filepath = new ArrayList<String>();
	private MediaPlayer mMediaPlayer;
	private static RingtoneUtil util;
	private AudioManager mAudiManager;
	private Vibrator mVibrator;
	private Context context;
	
	private RingtoneUtil(Context context){
		this.context = context;
		mAudiManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	public static RingtoneUtil getInstance(Context context){
		if(util == null){
			util = new RingtoneUtil(context);
		}
		return util;
	}

	public List<String> getRingtoneList(){
		if(list_ringtone.size()==0)
			scanSystemRingtone();
		return list_ringtone;
	}
	
	public List<String> getRingtoneFilepathList(){
		if(list_filepath.size()==0)
			scanSystemRingtone();
		return list_filepath;
	}
	
	private void scanSystemRingtone(){
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE }, "is_ringtone != ?",
                new String[] { "0" }, "_id asc");
        if (cursor == null) {
            return;
        }
        int count = 0;
        while (cursor.moveToNext()) {
        	String path = cursor.getString(1);
        	String[] splits = path.split("/");
        	list_ringtone.add(splits[splits.length-1].replace(".ogg", ""));
        	list_filepath.add(path);
        	if(count++ == 5)
        		return;
        }
	}
	
	public void playRingtone(int pos){
		playRingtone(pos, false);
	}
	
	public void playRingtone(int pos, boolean setting){
		int mode = mAudiManager.getRingerMode();
		if(mode == AudioManager.RINGER_MODE_VIBRATE && !setting){
			long [] pattern = {100,400,100,400}; 
			mVibrator.vibrate(pattern,-1);
		}else if(mode == AudioManager.RINGER_MODE_NORMAL){
			String filepath = getRingtoneFilepathList().get(pos);
			Uri mediaUri = Uri.fromFile(new File(filepath));
			if(mMediaPlayer!=null && mMediaPlayer.isPlaying())
				mMediaPlayer.stop();
	        mMediaPlayer = MediaPlayer.create(context, mediaUri);        
	        mMediaPlayer.setLooping(false);  
	        mMediaPlayer.start();
		}
	}
	
	public void stopRingtone(){
		if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
			mMediaPlayer = null;
		}
	}
}
