package model;

import java.util.HashMap;
import java.util.Map;

public enum ElectricitySupply {
	Fascia(0), cloudy(1), rain(2);
	
	private int wheater;
	
	private static Map<Integer, ElectricitySupply> _map = new HashMap<>();
	
	static{
		for(ElectricitySupply w : ElectricitySupply.values())
			_map.put(w.wheater, w);
	}
	
	private ElectricitySupply(final int wheater) {this.wheater = wheater;}
	
	public static ElectricitySupply valueOf(int wheater){
		return _map.get(wheater);
	}
}
