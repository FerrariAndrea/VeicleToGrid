package persistence.sib;

import java.util.HashMap;

public class Ontology {

	
	//------------------------------------------------------------------
	public static String RESOURCE = "resource";
	public static String BLANKNODE = "";
	public static String LITERAL = "literal";
	public static String LITERAL_INTEGER = "^^xsd:integer";
	public static String LITERAL_FLOAT = "^^xsd:float";
	public static String LITERAL_DOUBLE = "^^xsd:double";
	public static String LITERAL_DATE = "^^xsd:datetimestamp";
	public static String LITERAL_BOOL = "^^xsd:boolean";
	public static String LITERAL_DECIMAL = "^^xsd:decimal";
	public static String LITERAL_TIME = "^^xsd:time";
	public static String LITERAL_STRING = "^^xsd:string";
	public static String LITERAL_NAME = "^^xsd:Name";
	
	public static String UNKNOW = "";
	public static String SEPARATOR_URI = "#";
	public static String SEPARATOR_PREFIX = ":";
	//--------------------------------------------------APPLICAZIONE
	public static String APP_NS = "app"+SEPARATOR_PREFIX;//"http://veicletogridsimulator/"+SEPARATOR_URI;
	//--------------------------------------------------ONTOLOGIA APLLICAZIONE
	public static String vtg_SimulationScreen ="vtg:SimulationScreen";
	public static String vtg_chargingSpeedStorageByEnel ="vtg:chargingSpeedStorageByEnel";
	public static String vtg_chargingVehiclesSpeed ="vtg:chargingVehiclesSpeed";
	public static String vtg_hasForecast ="vtg:hasForecast"; 
	public static String vtg_hasParking ="vtg:hasParking";
	public static String vtg_hasStore ="vtg:hasStore";
	public static String vtg_hasUser ="vtg:hasUser";
	public static String vtg_initialChargteStorage ="vtg:initialChargteStorage";
	public static String vtg_maxChargeStorageCapacity ="vtg:maxChargeStorageCapacity";
	public static String vtg_maxChargeVehicleStorage ="vtg:maxChargeVehicleStorage";	
	public static String vtg_maxDurationCarPark ="vtg:maxDurationCarPark";
	public static String vtg_minDurationCarPark ="vtg:minDurationCarPark";
	public static String vtg_maxVehicleCapacityOnArrive ="vtg:maxVehicleCapacityOnArrive";
	public static String vtg_minTimeToNowForReserving ="vtg:minTimeToNowForReserving";
	public static String vtg_numberParkingSpace ="vtg:numberParkingSpace";
	public static String vtg_timeStamp ="vtg:timeStamp";
	public static String vtg_hasWheater ="vtg:hasWheater";
	public static String vtg_inDate ="vtg:inDate";
	public static String vtg_wheaterAt ="vtg:wheaterAt";
	public static String vtg_wheaterIs ="vtg:wheaterIs";
	public static String vtg_hasReservations ="vtg:hasReservations";
	public static String vtg_isComposed ="vtg:isComposed";
	public static String vtg_hasId ="vtg:hasId";
	public static String vtg_isBusy ="vtg:isBusy";
	public static String vtg_parkingVeicleStorage ="vtg:parkingVeicleStorage";
	public static String vtg_startTimeReserving ="vtg:startTimeReserving";
	public static String vtg_endTimeReserving ="vtg:endTimeReserving";
	public static String vtg_isReferedTo ="vtg:isReferedTo";
	public static String vtg_owner ="vtg:owner";
	public static String vtg_actualCharge ="vtg:actualCharge";
	public static String vtg_hasEmail ="vtg:hasEmail";
	public static String vtg_Nickname ="vtg:hasNickname";
	public static String vtg_Password ="vtg:hasPassword";
	
	
	//--------------------------------------------------ALTRE ONTOLOGIE
	public static String rdf_type ="rdf:type";
	


	public static HashMap<String, String> _prefixsRef=null;
	public static HashMap<String, String> _objectTypeIfHasPredicate=null;
	
	private  static void init() {
		_prefixsRef= new HashMap<String, String> ();
		_objectTypeIfHasPredicate= new HashMap<String, String> ();
		//prefix
		_prefixsRef.put("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns/");
		_prefixsRef.put("vtg","http://www.semanticweb.org/tsg/ontologies/2019/itunibo/veicletogrid/");
		_prefixsRef.put("app","http://veicletogridsimulator/#");
		
		_objectTypeIfHasPredicate.put(vtg_Nickname,LITERAL_NAME);	
		_objectTypeIfHasPredicate.put(vtg_hasEmail,LITERAL_STRING);	
		_objectTypeIfHasPredicate.put(vtg_Password,LITERAL_STRING);		
		_objectTypeIfHasPredicate.put(rdf_type,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_SimulationScreen,LITERAL_FLOAT);
		_objectTypeIfHasPredicate.put(vtg_chargingSpeedStorageByEnel,LITERAL_FLOAT);
		_objectTypeIfHasPredicate.put(vtg_chargingVehiclesSpeed,LITERAL_INTEGER);
		_objectTypeIfHasPredicate.put(vtg_hasForecast,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_hasParking,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_hasStore,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_hasUser,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_initialChargteStorage,LITERAL_INTEGER);
		_objectTypeIfHasPredicate.put(vtg_maxChargeStorageCapacity,LITERAL_FLOAT);
		_objectTypeIfHasPredicate.put(vtg_maxChargeVehicleStorage,LITERAL_FLOAT);
		_objectTypeIfHasPredicate.put(vtg_maxDurationCarPark,LITERAL_INTEGER);
		_objectTypeIfHasPredicate.put(vtg_minDurationCarPark,LITERAL_INTEGER);
		_objectTypeIfHasPredicate.put(vtg_maxVehicleCapacityOnArrive,LITERAL_INTEGER);
		_objectTypeIfHasPredicate.put(vtg_minTimeToNowForReserving,LITERAL_INTEGER);
		_objectTypeIfHasPredicate.put(vtg_numberParkingSpace,LITERAL_INTEGER);
		_objectTypeIfHasPredicate.put(vtg_timeStamp,LITERAL_DATE);
		_objectTypeIfHasPredicate.put(vtg_hasWheater,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_inDate,LITERAL_DATE);
		_objectTypeIfHasPredicate.put(vtg_wheaterAt,LITERAL_TIME);
		_objectTypeIfHasPredicate.put(vtg_wheaterIs,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_hasReservations,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_isComposed,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_hasId,LITERAL_INTEGER);
		_objectTypeIfHasPredicate.put(vtg_isBusy,LITERAL_BOOL);
		_objectTypeIfHasPredicate.put(vtg_parkingVeicleStorage,LITERAL_DOUBLE);
		_objectTypeIfHasPredicate.put(vtg_startTimeReserving,LITERAL_DATE);
		_objectTypeIfHasPredicate.put(vtg_endTimeReserving,LITERAL_DATE);
		_objectTypeIfHasPredicate.put(vtg_isReferedTo,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_owner,RESOURCE);
		_objectTypeIfHasPredicate.put(vtg_actualCharge,LITERAL_DOUBLE);
		
	}
	
	public static String resolveObjectType(String prefix) {
		
		if(_objectTypeIfHasPredicate.containsKey(prefix)) {
			return _objectTypeIfHasPredicate.get(prefix);
		}
		
		return RESOURCE;
	
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


