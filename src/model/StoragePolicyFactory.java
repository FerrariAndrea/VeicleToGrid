package model;

import java.time.LocalTime;

public class StoragePolicyFactory {
	public static StoragePolicyFactory _instance = new StoragePolicyFactory();
	
	public static IStoragePolicy CreateCheapStoragePolicy() {
		return _instance.new CheapStoragePolicy();
	}
	
	public static IStoragePolicy CreateExpensiveStoragePolicy() {
		return _instance.new ExpensiveStoragePolicy();
	}
	
	private abstract class StoragePolicy implements IStoragePolicy{
		protected State _state = null;
		
		protected double updateStorageDueToPhotovoltaicPanels(double actualStorage){
			
			if(!isNight()){
				int generatedPower = ConstantProject.generatedPowerByPanelsPhotovoltaic.get(WheaterForecast.GetInstance().getWheater(Document.GetInstance().getTime().toLocalTime()));
				double newChargeStorage = actualStorage + (generatedPower/60.0);		//KW to Wmin
				if(newChargeStorage > ConstantProject.maxChargeStorageCapacity) 
					return ConstantProject.maxChargeStorageCapacity;
				return newChargeStorage;
			}
			return actualStorage;
		}
		
		protected boolean isNight(){
			if(Document.GetInstance().getTime().toLocalTime().isAfter(LocalTime.of(19, 59)) || Document.GetInstance().getTime().toLocalTime().isBefore(LocalTime.of(6, 0))) return true;
			return false;
		}
		
		protected double updateStorageDueToEnel(double actualStorage){
			double newChargeStorage = actualStorage + (ConstantProject.chargingSpeedStorageByEnel/60.0);		//KW to Wmin
			if(newChargeStorage > ConstantProject.maxChargeStorageCapacity) return ConstantProject.maxChargeStorageCapacity;
			return newChargeStorage;
		}
		
		protected double manageVehicle(double storage, double chargeThreshold){
			//trovo quanta carica dello storage mi serve per ricaricare i veicoli che hanno una carica inferiore alla soglia chargeThreshold
			double requestCharge = 0;
			for(ParkingSpace p : Parking.GetInstance().getParkingSpace()){
				if(!p.isFree() && p.getActualVehicleStorage() < (chargeThreshold * ConstantProject.maxChargeVehicleStorage)){
					if((p.getActualVehicleStorage() + (ConstantProject.chargingVehiclesSpeed / 60.0)) > ConstantProject.maxChargeVehicleStorage)
						requestCharge += ConstantProject.maxChargeVehicleStorage - p.getActualVehicleStorage();
					else requestCharge += ConstantProject.chargingVehiclesSpeed / 60.0; //KWh to Wmin
				}
			}
			
			//provo ad aggiornare la carica  dello Storage
			double newStorage = storage - requestCharge;
			if(newStorage > 0){
				
				//esito positivo aggiorno quindi la carica dei veicoli trovati precedentemente
				for(ParkingSpace p : Parking.GetInstance().getParkingSpace()){
					if(!p.isFree() && p.getActualVehicleStorage() < (chargeThreshold * ConstantProject.maxChargeVehicleStorage)){
						p.updateVehicleStorage();
					}
				}
				return newStorage;
			}
			return storage;
		}
		
		@Override
		public double modifyChargeStorage() {
			
			//ricarica storage dovuta ai pannelli fotovoltaici
			double newStorage = updateStorageDueToPhotovoltaicPanels(Storage.GetInstance().getActualCharge());
			
			_state = _state.changeState(newStorage);
			
			return _state.operation(newStorage);
		}
		
		protected abstract class State{
			public abstract State changeState(double storage);
			public abstract double operation(double storage);
		}
	}
	
	private class CheapStoragePolicy extends StoragePolicy{
		
		public CheapStoragePolicy(){
			double perc = Storage.GetInstance().getActualCharge() / ConstantProject.maxChargeStorageCapacity;
			if(perc >= 0.3) _state = new FullVehicleCharge();
			if(perc >= 0.1 && perc < 0.3) _state = new HalfVehicleCharge();
			if(perc < 0.1) _state = new Discharge();
		}

		@Override
		public String getName() {
			return "cheap";
		}
		
		private class Discharge extends State{
			public double operation(double storage){
				//ricarica tramite Enel
				return updateStorageDueToEnel(storage);
			}

			@Override
			public State changeState(double storage) {
				double perc = storage / ConstantProject.maxChargeStorageCapacity;
				if(perc >= 0.15) return new HalfVehicleCharge();
				return this;
			}
		}
		
		private class HalfVehicleCharge extends State{
			public double operation(double storage){
				//ricarica tramite Enel
				double newStorage = updateStorageDueToEnel(storage);
				//gestisco i veicoli
				return manageVehicle(newStorage, 0.5);
			}

			@Override
			public State changeState(double storage) {
				double perc = storage / ConstantProject.maxChargeStorageCapacity;
				if(perc >= 0.4) return new FullVehicleCharge();
				if(perc < 0.1 ) return new Discharge();
				return this;
			}
		}
		
		private class FullVehicleCharge extends State{
			public double operation(double storage){
				//gestisco i veicoli
				return manageVehicle(storage, 1);
			}

			@Override
			public State changeState(double storage) {
				double perc = storage / ConstantProject.maxChargeStorageCapacity;
				if(perc < 0.3) return new HalfVehicleCharge();
				return this;
			}
		}
	}
	
	private class ExpensiveStoragePolicy extends StoragePolicy{
	
		public ExpensiveStoragePolicy(){
			double perc = Storage.GetInstance().getActualCharge() / ConstantProject.maxChargeStorageCapacity;
			if(perc < 0.1) _state = new Discharge();
			else _state = new FullVehicleCharge();
		}
		
		@Override
		public String getName() {
			return "expensive";
		}
		
		private class Discharge extends State{
			public double operation(double storage){
				//ricarica tramite Enel
				return updateStorageDueToEnel(storage);
			}

			@Override
			public State changeState(double storage) {
				double perc = storage / ConstantProject.maxChargeStorageCapacity;
				if(perc >= 0.15) return new FullVehicleCharge();
				return this;
			}
		}
		
		private class FullVehicleCharge extends State{
			public double operation(double storage){
				//ricarica tramite Enel
				double newStorage = updateStorageDueToEnel(storage);
				
				//gestisco i veicoli
				return manageVehicle(newStorage, 1);
			}

			@Override
			public State changeState(double storage) {
				double perc = storage / ConstantProject.maxChargeStorageCapacity;
				if(perc < 0.1) return new Discharge();
				return this;
			}
		}
	}
}
