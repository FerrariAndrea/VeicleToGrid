package persistence.sib;

import java.util.HashMap;

public class Ontology {

	
	//------------------------------------------------------------------
	public static String RESOURCE = "resource";
	public static String BLANKNODE = "blanknode";
	public static String LITERAL = "literal";
	public static String UNKNOW = "";
	public static String SEPARATOR_URI = "#";
	public static String SEPARATOR_PREFIX = ":";
	//--------------------------------------------------APPLICAZIONE
	public static String APP_NS = "http://veicletogridsimulator/"+SEPARATOR_URI;
	//--------------------------------------------------ONTOLOGIA APLLICAZIONE
	public static String vtg_SimulationScreen ="vtg:SimulationScreen";
	public static String vtg_chargingSpeedStorageByEnel ="vtg:chargingSpeedStorageByEnel";
	public static String vtg_chargingVehiclesSpeed ="vtg:chargingVehiclesSpeed";
	public static String vtg_hasForecast ="vtg:hasForecast"; 
	public static String vtg_hasParking ="vtg:hasParking";
	public static String vtg_hasStore ="vtg:hasStore";//---------da fare
	public static String vtg_hasUser ="vtg:hasUser";//---------da fare
	public static String vtg_initialChargteStorage ="vtg:initialChargteStorage";
	public static String vtg_maxChargeStorageCapacity ="vtg:maxChargeStorageCapacity";
	public static String vtg_maxChargeVehicleStorage ="vtg:maxChargeVehicleStorage";	
	public static String maxDurationCarPark ="vtg:maxDurationCarPark";
	public static String vtg_minDurationCarPark ="vtg:minDurationCarPark";
	public static String vtg_maxVehicleCapacityOnArrive ="vtg:maxVehicleCapacityOnArrive";
	public static String vtg_minTimeToNowForReserving ="vtg:minTimeToNowForReserving";
	public static String vtg_numberParkingSpace ="vtg:numberParkingSpace";
	public static String vtg_timeStamp ="vtg:timeStamp";
	public static String vtg_hasWheater ="vtg:hasWheater";
	public static String vtg_inDate ="vtg:inDate";
	public static String vtg_wheaterAt ="vtg:wheaterAt";
	public static String vtg_wheaterIs ="vtg:wheaterIs";
	public static String vtg_hasReservations ="vtg:hasReservations";//---------da fare
	public static String vtg_isComposed ="vtg:isComposed";//---------da fare
	
	
	//--------------------------------------------------ALTRE ONTOLOGIE
	public static String rdf_type ="rdf:type";
	


	private static HashMap<String, String> _prefixsRef=null;
	private static HashMap<String, String> _prefixsType=null;
	
	private  static void init() {
		_prefixsRef= new HashMap<String, String> ();
		_prefixsType= new HashMap<String, String> ();
		//prefix
		_prefixsRef.put("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns/");
		_prefixsRef.put("vtg","http://www.semanticweb.org/tsg/ontologies/2019/itunibo/veicletogrid/");
		
		/*
		_prefixsType.put(rdf_type,RESOURCE);
		_prefixsType.put(vtg_SimulationScreen,RESOURCE);
		_prefixsType.put(vtg_chargingSpeedStorageByEnel,RESOURCE);
		_prefixsType.put(vtg_chargingVehiclesSpeed,RESOURCE);
		*/
		
		
	}
	
	public static String resolveType(String prefix,String postfix) {
		
		return UNKNOW;
		/*
		if(_prefixsRef==null) {
			init();	
		}
		if( _prefixsType.containsKey(prefix)) {
			return _prefixsType.get(prefix+":"+postfix);
			
		}else {
			return BLANKNODE;
		}
		*/
	}
	public static String resolvePrefix(String prefix) {
		if(_prefixsRef==null) {
			init();	
		}
		return _prefixsRef.get(prefix);
	}
	
	public static boolean isValidPrefix(String prefix) {
		if(_prefixsRef==null) {
			init();	
		}
		return _prefixsRef.containsKey(prefix);
	}
}


