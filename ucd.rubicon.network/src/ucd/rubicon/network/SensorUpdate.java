package ucd.rubicon.network;

public class SensorUpdate {
	protected String updateName;
	protected String sensorType;
	protected Object value;
	
	public SensorUpdate(String sensorType, String updateName, Object value) {
		super();
		this.updateName = updateName;
		this.sensorType = sensorType;
		this.value = value;		
	}
	
	public SensorUpdate(String sensorType, Object value) {
		this(sensorType, sensorType, value);
	}
	
	public String getUpdateName() {
		return updateName;
	}
	
	public String getSensorType() {
		return sensorType;
	}
	
	public Object getSensorValue() {
		return value;
	}
	
}
