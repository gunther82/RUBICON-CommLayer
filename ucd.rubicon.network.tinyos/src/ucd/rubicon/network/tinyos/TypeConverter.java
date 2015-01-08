package ucd.rubicon.network.tinyos;

import java.util.Properties;

import cnr.rubicon.cml.Definitions;

public class TypeConverter {

	public static Properties getResources(int transducersMask, short actuatorsMask) {
		
		Properties resources = new Properties();
			    
		// SENSORS
		if ((transducersMask & Definitions.LIGHT) == Definitions.LIGHT) {
			resources.put("light", "sensor.light");
		}

		if ((transducersMask & Definitions.TEMP) == Definitions.TEMP) {
			resources.put("temperature", "sensor.temperature");
		}

		if ((transducersMask & Definitions.ACCEL_X) == Definitions.ACCEL_X) {
			resources.put("accel_x_avg", "sensor.accellerometer.average");
			resources.put("accel_x_var", "sensor.accellerometer.variance");
		}
		
		if ((transducersMask & Definitions.ACCEL_Y) == Definitions.ACCEL_Y) {
			resources.put("accel_y_avg", "sensor.accellerometer.average");
			resources.put("accel_y_var", "sensor.accellerometer.variance");
		}

		if ((transducersMask & Definitions.PIR) == Definitions.PIR) {
			resources.put("pir", "sensor.pir");
		}
		
		if ((transducersMask & Definitions.MIC) == Definitions.MIC) {
			resources.put("mic", "sensor.microphone");
		}
	
		if ((transducersMask & Definitions.HUMID) == Definitions.HUMID) {
			resources.put("humidity", "sensor.humidity");
		}

		if ((transducersMask & Definitions.MAGNETIC) == Definitions.MAGNETIC) {
			resources.put("magnetic", "sensor.magnetic");
		}

		// TODO: check with Claudio why these values overlap, for the moment I use only RSS1
		if ((transducersMask & Definitions.RSSI1) == Definitions.RSSI1) {
			resources.put("rssi1", "sensor.rssi");
			resources.put("rssi2", "sensor.rssi");
			resources.put("rssi3", "sensor.rssi");
			resources.put("rssi4", "sensor.rssi");
			resources.put("rssi5", "sensor.rssi");
		}

		// ACTUATORS
		if ((actuatorsMask & Definitions.LED) > 0) {
			resources.put("led", "actuator.led");
		}

		if ((actuatorsMask & Definitions.OPEN) > 0) {
			resources.put("relay", "actuator.relay");
		}
	
		return resources;
	}
	
}
