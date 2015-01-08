package ucd.rubicon.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Mauro, this is 
 * @author rubicon
 *
 */
public class DeviceUpdate {
	protected List<SensorUpdate> listUpdates = new LinkedList<SensorUpdate>();
	
	public void add(SensorUpdate update) {
		listUpdates.add(update);
	}
	
	public List<SensorUpdate> getSensorUpdates() {
		return listUpdates;
	}
}
