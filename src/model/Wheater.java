package model;

import java.util.HashMap;
import java.util.Map;

public enum Wheater {
	sunny(0), cloudy(1), rain(2);
	
	private int wheater;
	
	private static Map<Integer, Wheater> _map = new HashMap<>();
	
	static{
		for(Wheater w : Wheater.values())
			_map.put(w.wheater, w);
	}
	
	private Wheater(final int wheater) {this.wheater = wheater;}
	
	public static Wheater valueOf(int wheater){
		return _map.get(wheater);
	}
}
