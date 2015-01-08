/**
 * 
 */
package ucd.rubicon.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import ucd.rubicon.network.RubiconMessageUpdates;
import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessageJoin;


/**
 * Repository of devices connected to the RUBICON Proxy through the joining 
 * Holds node specification information
 * detailing sensing and actuator capabilities of the devices. Thread-safe device addition
 * and removal is regulated through <code>synchronized</code> methods.
 * 
 * 
 * @author Davide Bacciu - UNIPI (<a href="mailto:bacciu@di.unipi.it">Contact</a>)
 * @version %I%, %G%
 * 
 * 
 **/
public class DeviceRepository {
	
	//The private map of devices
	private Map<RubiconAddress, NodeInformation> repository = new  ConcurrentHashMap<RubiconAddress, NodeInformation>();
	
	public DeviceRepository() {
	}
		
	/**
	 * Service class describing the current state of the device 
	 *
	 * @see generics.DeviceRepository
	 */
	public static enum DeviceState{
		/**
		 * Denotes a device with a pending connection request  
		 */
		CONNECTING,
		/**
		 * Denotes a device with a pending disconnection request  
		 */
		DISCONNECTING,
		/**
		 * Denotes a connected device  
		 */
		CONNECTED,
	}	
	
	/**
	 * Thread-safe addition of a device to the repository. The device is assigned the ID
	 * passed as argument and is associated to the specification information.
	 * The state of the device is set to {@link DeviceState#CONNECTED} 
	 *  
	 * @param networkAddress Rubicon Address for the device
	 * @param spec Specification of the device capabilities
	 * 
	 * @see DeviceRepository.DeviceState
	 * @see NodeInformation
	 */
	public synchronized void addDevice(RubiconMessageJoin messageJoin) {
		NodeInformation d = new NodeInformation(messageJoin);		
		d.state = DeviceState.CONNECTED;
		repository.put(messageJoin.getSender(), d);
	}
	
	/**
	 * Thread-safe removal of a device from the repository. 
	 *  
	 */
	public synchronized void removeDevice(RubiconAddress address){
		repository.remove(address);
	}
	
	/**
	 * Sets the device state to one of the {@link DeviceState}. 
	 * Thread-safe implementation.
	 *  
	 * @param nodeID Identifier of the device
	 * @param state New device state
	 * 
	 * @see DeviceRepository.DeviceState
	 */
	public synchronized void setState(RubiconAddress address, DeviceState state) {
		NodeInformation nodeInfo = repository.get(address);
		if (nodeInfo != null) {
			nodeInfo.state = state;
		}		
	}
	
		
	/**
	 * Retrieves the current runtime information for a device in the repository.
	 * 
	 * @param address Rubicon address of the device
	 * @return the current NodeInformation for the device
	 */	
	public synchronized NodeInformation getNodeInformation(RubiconAddress address) {
		return repository.get(address);
	}
	
	/**
	 * Retrieves the current runtime information for a device in the repository.
	 * 
	 * @param address Rubicon address of the device
	 * @return the current RuntimeNodeInformation for the device
	 */	
	public synchronized NodeInformation updateNode(RubiconMessageUpdates msgUpdate) {
		NodeInformation node = repository.get(msgUpdate.getSender());	
		if (node == null) {
			System.out.println("DeviceRepository: updateNode(): received msg from a not joined node. THIS SHOULD NOT HAPPEN!");
			// TODO: Log and ask Claudio, will nodes send only one join message?
			// what if I miss it and start receive sensory updates?
			// for the moment do nothing and returns
			// this node will be picked up when the node sends its next join message
			return null;
		}
		node.update(msgUpdate);
		return node;
	}
	
	
	public void print() {
		System.out.println("REPOSITORY");
		System.out.println("-----------------------------------");
		for (RubiconAddress address:repository.keySet()) {
			NodeInformation node = repository.get(address);
			Properties resources = node.nodeSpec.getResources();
			System.out.println(" |_node "+node.nodeSpec.getIsland_addr()+"."+node.nodeSpec.getAddr());
			for (Object key : resources.keySet()) {
				String resourceName = (String)key;
				String resourceType = (String)resources.get(key);
				System.out.println("   |__"+resourceName+" :"+resourceType);
			}			
		}
		
	}

	public synchronized Collection<NodeInformation> getNodes() {
		return repository.values();
	}
	
}
