package presentation;

public abstract class ObserverControl<T>{
	protected T control;
	
	protected ObserverControl(T control){
		this.control = control;
	}
	
	protected T getControl(){
		return control;
	}
}
