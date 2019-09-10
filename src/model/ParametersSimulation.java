package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.ParameterInformation.ParameterInformationListValue;
import model.ParameterInformation.ParameterInformationSingleValue;

public class ParametersSimulation {

	private static ParametersSimulation _instance = null;
	private Map<String, ParameterInformation<?>> parameters;
	
	private ParametersSimulation() {
		parameters = new HashMap<>();
		ArrayList<String> values = new ArrayList<>();
		values.add("Cheap");
		values.add("Expensive");
		ParameterInformationListValue<String> p = new ParameterInformationListValue<>("Policy of storage", String.class,values, false);
		parameters.put("policy", p);
		values.clear();
		values.add("File.ttl");
		values.add("SIB");
		values.add("Both");
		values.add("-");
		p = new ParameterInformationListValue<>("Where do you want to save output?", String.class,values, false); 
		parameters.put("Location output", p);
		ParameterInformationSingleValue<Integer> pI = new ParameterInformationSingleValue<>("Min duration for car parking (in minutes)" ,Integer.class, 60, false);
		parameters.put("minDurationCarPark", pI);
		pI = new ParameterInformationSingleValue<>("Max duration for car parking (in minutes)" ,Integer.class, 480, false);
		parameters.put("maxDurationCarPark", pI);
		pI = new ParameterInformationSingleValue<>("Velocity to charge the storage by Enel (KW/h)" ,Integer.class, 40, false);
		parameters.put("chargingSpeedStorageByEnel", pI);
		pI = new ParameterInformationSingleValue<>("Velocity to charge the vehicles (KW/h)" ,Integer.class, 20, false);
		parameters.put("chargingVehiclesSpeed", pI);
		pI = new ParameterInformationSingleValue<>("Max charge of vehicle (KWh)" ,Integer.class, 80, true);
		parameters.put("maxChargeVehicleStorage", pI);
		pI = new ParameterInformationSingleValue<>("Max charge of vehicle when arrive to parking (KWh)" ,Integer.class, 60, false);
		parameters.put("maximumVehicleCapacityWhenArriveToParkingSpace", pI);
		pI = new ParameterInformationSingleValue<>("Power genererated by panels photovoltaic when is sunny(KW)" ,Integer.class, 40, false);
		parameters.put("generatedPowerByPanelsPhotovoltaic-Sunny", pI);
		pI = new ParameterInformationSingleValue<>("Power genererated by panels photovoltaic when is cloudy(KW)" ,Integer.class, 5, false);
		parameters.put("generatedPowerByPanelsPhotovoltaic-Cloudy", pI);
		pI = new ParameterInformationSingleValue<>("Power genererated by panels photovoltaic when is rainy(KW)" ,Integer.class, 1, false);
		parameters.put("generatedPowerByPanelsPhotovoltaic-Rain", pI);
		pI = new ParameterInformationSingleValue<>("Minutes that must pass before the start of a reservation(minutes)" ,Integer.class, 30, false);
		parameters.put("minTimeToNowForReserving", pI);
		pI = new ParameterInformationSingleValue<>("Initial charge of storage (KWh)", Integer.class, 600, true);
		parameters.put("InitialChargeStorage", pI);
		pI = new ParameterInformationSingleValue<>("Max charge of storage (KWh) (default: 60 * Number of Parking)", Integer.class, 600, true);
		parameters.put("maxChargeStorageCapacity", pI);
		pI = new ParameterInformationSingleValue<>("Number of parking space", Integer.class, 10, true);
		parameters.put("numberNormalParkingSpace", pI);
	}
	
	public static ParametersSimulation GetInstance(){
		if(_instance == null) _instance = new ParametersSimulation();
		return _instance;
	}
	
	public Set<String> getAllParameterName(){
		return parameters.keySet();
	}
	
	public ParameterInformation<?> getInformationOfParameter(String name){
		return parameters.get(name);
	}
	
	public void resetAllParameters() {
		for(String s : parameters.keySet())
			parameters.get(s).resetValue();
	}
}
