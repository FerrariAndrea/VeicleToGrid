package model;

import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

public class MySemaphore extends Semaphore {
	private static final long serialVersionUID = 1L;
	private LocalDateTime _time;
	
	public MySemaphore(int permits, LocalDateTime time){
		super(permits);
		this._time = time;
	}
	
	public boolean isReady(){
		LocalDateTime now = Document.GetInstance().getTime();
		if(now.equals(_time)) return true;
		return false;
	}
}
