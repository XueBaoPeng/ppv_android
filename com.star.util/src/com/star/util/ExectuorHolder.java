package com.star.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExectuorHolder {
	private static ExecutorService pool;
	private RunnableChan runnableChan;
	private Future<?> future;
	
	public ExectuorHolder() {
		if(pool==null){
			pool = Executors.newFixedThreadPool(30);
		}
	}
	
	static class RunnableChan implements Runnable{
		private	List<Runnable> runnables = new LinkedList<Runnable>();
		private boolean isAlive = true;
		
		public boolean push(Runnable runnable){
			synchronized(this){
				if(isAlive){
					runnables.add(runnable);
					return true;
				}
				return false;				
			}
		}
		
		private Runnable getNextRunnable(){
			synchronized(this){
				if(isAlive && runnables.size() != 0){
					Runnable runnable = runnables.get(0);
					runnables.remove(0);
					return runnable;
				}
			}		
			return null;
		}

		@Override
		public void run() {
			Runnable runnable = null;
			while(true){
				runnable = getNextRunnable();
				
				if(runnable != null){
					runnable.run();
				}else{
					synchronized(this){
						if(runnables.size() == 0){
							isAlive = false;
							return ;
						}
					}
				}
			}
		}
		
		public void reset(){
			synchronized(this){
				runnables.clear();
				isAlive = false;
			}
		}
	}
	
	public ExectuorHolder addTask(Runnable runnable){
		synchronized(this){
			if(runnableChan == null || runnableChan.push(runnable) == false){
				runnableChan = new RunnableChan();
				runnableChan.push(runnable);
				future = pool.submit(runnableChan);
			}
		}
		return this;
	}
	
	public void cancle(){
		synchronized(this){
			if(future != null && !future.isDone()){
				future.cancel(true);
				runnableChan.reset();			
			}
			future = null;
			runnableChan = null;			
		}
	}
}
