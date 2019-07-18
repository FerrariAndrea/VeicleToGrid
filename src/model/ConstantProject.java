package model;

import java.util.HashMap;
import java.util.Map;

public class ConstantProject {
	//constant for parking
	public static final int numerNormalParkingSpace = 10;
	public static final int minTimeToNowForReserving = 30;		//minutes
	public static final int minDurationCarPark = 60;			//minutes
	public static final int maxDurationCarPark = 480;			//minutes
	
	//constant for storage
	public static final float maxChargeStorageCapacity = Parking.GetInstance().getParkingSpace().size() * 60;	//n*60 KW/h
	public static final float InitialChargeStorage = Parking.GetInstance().getParkingSpace().size() * 60;	//n*60 KW/h
	public static final float chargingSpeedStorageByEnel = 3;	//n*3 KW
	
	//constant for vehicle
	public static final int maximumVehicleCapacityWhenArriveToParkingSpace = 60;		//60 KW/h
	public static final int chargingVehiclesSpeed = 20;			//3 KW 
	public static final int maxChargeVehicleStorage = 80;		//80 KW/h
	
	//constant for photovolaic Panels
	public static final Map<Wheater, Integer> generatedPowerByPanelsPhotovoltaic = new HashMap<>();
	static{
		generatedPowerByPanelsPhotovoltaic.put(Wheater.sunny,  5);	//n*5 KW
		generatedPowerByPanelsPhotovoltaic.put(Wheater.cloudy,  1);	//n*1 KW
		generatedPowerByPanelsPhotovoltaic.put(Wheater.rain, 0);		//0 KW
	}
}
