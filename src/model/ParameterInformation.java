package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ParameterInformation<T> {
		protected String _graficInterfaceName;
		protected Class<T> _typeOf;
		protected boolean _isModified = false;
		protected boolean _isInitial;
		
		protected ParameterInformation(String graficInterfaceName, Class<T> typeOf, boolean isInitial){
			this._graficInterfaceName = graficInterfaceName;
			this._typeOf = typeOf;
			this._isInitial = isInitial;
		}
		
		public String get_graficInterfaceName() {
			return _graficInterfaceName;
		}
		
		public Class<T> getType(){
			return _typeOf;
		}
		
		public boolean isModified() {
			return _isModified;
		}
		
		public boolean isInitial() {
			return _isInitial;
		}
		
		public abstract void resetValue();
		public abstract void setValueOfParameter(Object value);
		public abstract T getValue();
		
	
	public static class ParameterInformationListValue<T> extends ParameterInformation<T>{
		private Map<T, Boolean> _values = new HashMap<>();
		
		public ParameterInformationListValue(String graficInterfaceName, Class<T> type, List<T> values, boolean isInitial){
			super(graficInterfaceName, type, isInitial);
			for(T v : values)
				_values.put(v, false);
		}
		
		@Override
		public void setValueOfParameter(Object value){
			if(!isInitial() || (isInitial() && !isModified())) {
				if(!value.getClass().equals(_typeOf)) return;
				_isModified = true;
				for(T v : _values.keySet())
					if(_values.get(v)) { _values.replace(v, false); break;}
				_values.replace(_typeOf.cast(value), true);
			}
		}
		
		@Override
		public T getValue(){
			for(T value : _values.keySet())
				if(_values.get(value)) return value;
			return null;
		}
		
		public Set<T> getAllValueOfParameter(){
			return _values.keySet();
		}

		@Override
		public void resetValue() {
			if(!isInitial()) {
				for(T v : _values.keySet())
					_values.replace(v, false);
				_isModified = false;
			}
		}
	}
	
	public static class ParameterInformationSingleValue<T> extends ParameterInformation<T>{
		private T _initialValue;
		private T _value;
		
		public ParameterInformationSingleValue(String graficInterfaceName,Class<T> type, T value, boolean isInitial) {
			super(graficInterfaceName, type, isInitial);
			_value = value;
			_initialValue = value;
		}
		
		@Override
		public void setValueOfParameter(Object value) {
			if(!isInitial() || (isInitial() && !isModified())) {
				if(!value.getClass().equals(_typeOf) || (value.getClass().equals(_typeOf) && value.equals(_initialValue))) return;
				_isModified = true;
				_value = _typeOf.cast(value);
			}
		}
		
		@Override
		public T getValue() {
			return _value;
		}

		@Override
		public void resetValue() {
			if(!isInitial()) {
				_isModified = false;
				_value = _initialValue;
			}
		}
	}
}
