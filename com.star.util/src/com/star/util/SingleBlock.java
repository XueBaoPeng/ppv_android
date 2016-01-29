package com.star.util;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;


public class SingleBlock {
	static final String TAG = SingleBlock.class.getName();
	private final Lock lock = new ReentrantLock();
	private final Condition waitingConditon = lock.newCondition();
	private final Condition signalConditon = lock.newCondition();
	
	private boolean signal = false;
	
	public void waiting() {
		lock.lock();
		try{
			while(signal == false){
				waitingConditon.await();
			}
			signal = false;
			signalConditon.signal();
		}
		catch (InterruptedException e) {
			Log.e(TAG, "waiting",e);
		}
		finally{
			lock.unlock();
		}
	}
	
	public void signal() {
		lock.lock();
		try{
			while(signal == true){
				signalConditon.await();
			}
			signal = true;
			waitingConditon.signal();
		}
		catch (InterruptedException e) {
			Log.e(TAG, "signal",e);
		}
		finally{
			lock.unlock();
		}
	}

}
