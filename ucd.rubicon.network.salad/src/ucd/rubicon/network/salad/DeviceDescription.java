package ucd.rubicon.network.salad;

import java.util.Hashtable;
import java.util.StringTokenizer;

public class DeviceDescription {
	
	protected int	 	rubiconDeviceId = 0;
	protected String 	actuatorSaladId = null;
	protected String    rubiconType = null;
	protected String 	symbolicName = null;
	protected double 	lastValue = 0;
	
	public DeviceDescription(int rubiconDeviceId) {
		this.rubiconDeviceId = rubiconDeviceId;
	}

	public String getActuatorSaladId() {
		return actuatorSaladId;
	}

	public void setActuatorSaladId(String actuatorId) {
		this.actuatorSaladId = actuatorId;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public int getRubiconDeviceId() {
		return rubiconDeviceId;
	}

	public boolean canBeActuated() {
		return (actuatorSaladId!=null);
	}

	public String getRubiconType() {
		return rubiconType;
	}
	
	public void setRubiconType(String rubiconType) {
		this.rubiconType = rubiconType;
	}
	
	//<name>,<type>[,<actuationId>]
	public void parseFromString(String str) throws Exception {
		StringTokenizer st = new StringTokenizer(str, ",");
		if (st.countTokens()<1) {
			throw new Exception("Invalid device description, expected device_<Id>=<SymbolicName>,<type>[,<ActuatorHardwareId>]");
		}		
		setSymbolicName(st.nextToken());
		setRubiconType(st.nextToken());
		if (st.hasMoreElements()) {
			setActuatorSaladId(st.nextToken());			
		}
	}
	
	public double getLastValue() {
		return lastValue;
	}
	
	public void setLastValue(double lastValue) {
		this.lastValue = lastValue;
	}

}
