/**
 * 
 */
package ucd.rubicon.proxy;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessageUpdates;
import ucd.rubicon.network.RubiconMessageJoin;
import ucd.rubicon.network.SensorUpdate;
import ucd.rubicon.proxy.DeviceRepository.DeviceState;


/**
 * Specification of the devices sensing and actuator capabilities, as well as their basic network information, including
 * island and node address.
 * 
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 * @version %I%, %G%
 * 
 * @see DeviceRepository
 */
public class NodeInformation {
	
	protected Date tmLastSeen;
	protected DeviceState state;	
	protected List<SensorUpdate> values = new LinkedList<SensorUpdate>();
	protected RubiconMessageJoin nodeSpec; 		

	public NodeInformation(RubiconMessageJoin joinMessage) {
		nodeSpec = joinMessage;
		tmLastSeen = new Date();
	}

	public synchronized void update(RubiconMessageUpdates msgUpdate) {
		//System.out.println("NodeInformation: update(): updating node information...");
		tmLastSeen = new Date();
		//TODO
		values.clear();
		values.addAll(msgUpdate.getUpdate().getSensorUpdates());
		//System.out.println("List size is "+values.size() +" value is "+msgUpdate.getUpdate().getSensorUpdates().get(0));
	}

	protected synchronized SensorUpdate  getLastValue() {
		if (values.size()==0) return null;
		return values.get(0);
	}
}
