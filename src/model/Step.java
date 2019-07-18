package model;

import java.util.concurrent.Semaphore;

public class Step implements Runnable {
	private boolean _pause = false;
	private Semaphore _sem = new Semaphore(0);
	
	@Override
	public void run() {
		try {
			while(!Document.GetInstance().isExit()){
				//controllo se l'applicazione deve essere messa in pausa
				if(_pause){ 
					_sem.acquire(); 
					_pause = false;
				}
				
				//rimozione vecchie prenotazioni
				Parking.GetInstance().removeOldRserving();
				
				//aggiornamento stato parcheggi a causa delle prenotazioni
				Parking.GetInstance().updateParkingSpaceDueToReserving();
				
				//gestione eventi
				ContainerEvent.GetInstance().managementEvent();
				
				//gestione semafori
				ContainerEvent.GetInstance().attivateSemaphore();
				
				//aggiornamento cariche in base alla politica
				Storage.GetInstance().chargeUpdate();
				
				//incremento del tempo
				Document.GetInstance().updateTime();
				
				Thread.sleep(Document.GetInstance().getSleepTimeStep());
			}
			
			//terminazione applicazione, gestione semafori per terminare gli altri Thread generatori di eventi casuali
			ContainerEvent.GetInstance().attivateSemaphore();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Semaphore getSemaphore(){
		return _sem;
	}
	
	public boolean isInPause(){
		return _pause;
	}
	
	public void requestPause(){
		_pause = true;
	}
}
