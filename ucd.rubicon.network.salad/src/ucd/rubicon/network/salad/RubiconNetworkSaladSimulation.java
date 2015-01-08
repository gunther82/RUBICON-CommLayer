package ucd.rubicon.network.salad;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ucd.rubicon.network.DeviceUpdate;
import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessage;
import ucd.rubicon.network.RubiconMessageCtrlActuateCmd;
import ucd.rubicon.network.RubiconMessageUpdates;
import ucd.rubicon.network.RubiconMessageJoin;
import ucd.rubicon.network.RubiconNetwork;
import ucd.rubicon.network.SensorUpdate;

/**
 * Provides a {@Link RubiconNetwork} implementation to interface with TECNALIA's SALAD middleware
 * @author rubicon
 *
 */
public class RubiconNetworkSaladSimulation extends RubiconNetwork implements Runnable {
			
	public static final String ID_PREFIX = "device_";
	
	protected Thread  thread = null;
	
	protected int DELAY_UPDATES = 500;
		
	protected boolean bIsRunning = false;
	
	protected Map<Integer, DeviceDescription> mapDevices = new HashMap<Integer, DeviceDescription>();
	
	public void parseDevices() {	
		//System.out.println("Parsing devices");
		Enumeration e = getProperties().keys();
		String s;
		while (e.hasMoreElements()) {
			String key 	 = (String)e.nextElement();
			String value = getProperties().getProperty(key);
			//System.out.println("key("+key+")="+value);
			if (key.startsWith(ID_PREFIX)) {
				s = key.substring(ID_PREFIX.length());
				int rubiconDeviceId = Integer.parseInt(s);
				DeviceDescription deviceDescription = new DeviceDescription(rubiconDeviceId);
				//System.out.println("device id("+rubiconDeviceId+")");
				try {
					deviceDescription.parseFromString(value);
					mapDevices.put(rubiconDeviceId, deviceDescription);
					
					RubiconAddress sender = new RubiconAddress(getIslandID(), this.getNetworkName(), rubiconDeviceId);
					RubiconAddress proxy = new RubiconAddress(getIslandID(), 0);
					RubiconMessageJoin rubiconMessage = new RubiconMessageJoin(sender);
					Properties resources = new Properties();
					resources.put(deviceDescription.getSymbolicName(), deviceDescription.getRubiconType());
					rubiconMessage.setResources(resources);
					rubiconMessage.setName(deviceDescription.getSymbolicName());
					rubiconMessage.setAddr(rubiconDeviceId);
					rubiconMessage.setIsland_addr((short)getIslandID());
					
					sendToGateway(rubiconMessage, proxy);
					
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}				
			}
		}		
	}

	

	public void init() throws Exception {
		
		System.out.println("Initializing RubiconNetworkSimulation...");
		
		thread = new Thread(this);
		thread.start();		
	}
		

	protected synchronized void stop() {
		bIsRunning = false;		
	}
	
	protected synchronized boolean isRunning() {
		return bIsRunning;
	}
	
	/**
	 * This method is called by {@Link RubiconGateway} when a message is received by this network
	 */
	@Override
	public void handleMessageReceived(RubiconMessage message, RubiconAddress destination) {
		System.out.println("RubiconNetworkSimulation.handleMessageReceived");
		System.out.println(message.toString());
		if (destination.getDeviceId()<=0) {
			System.out.println("Invalid device id ("+destination.getDeviceId()+")! Request ignored.");
			return;
		}

		if (message instanceof RubiconMessageCtrlActuateCmd) {
			RubiconMessageCtrlActuateCmd actMsg = (RubiconMessageCtrlActuateCmd)message;
			DeviceDescription device = mapDevices.get(destination.getDeviceId());
			if (device == null) {
				System.out.println("Unrecognized device id ("+destination.getDeviceId()+")! Request ignored.");
				return;				
			}
			if (device.getActuatorSaladId()==null) {
				System.out.println("Device id ("+destination.getDeviceId()+") is not an actuator! Request ignored.");
				return;
			}

			// actuate
			device.setLastValue(actMsg.getValue());
		}
	}
	
	
	public void run() {		
	
		parseDevices();
						
		bIsRunning = true;
		while (isRunning()) {
			for (DeviceDescription deviceDescription: mapDevices.values()) {						
				DeviceUpdate update = new DeviceUpdate();
				double value = deviceDescription.getLastValue();
				String nameUpdate = deviceDescription.getSymbolicName();
				if (value == Math.floor(value)) {
					update.add(new SensorUpdate(nameUpdate, (int)value));
				} else {
					update.add(new SensorUpdate(nameUpdate, value));
				}
				RubiconAddress sender = new RubiconAddress(getIslandID(), this.getNetworkName(), deviceDescription.getRubiconDeviceId());
				RubiconAddress proxy = new RubiconAddress(getIslandID(), 0);
				RubiconMessageUpdates rubiconMessage = new RubiconMessageUpdates(sender, update);
				sendToGateway(rubiconMessage, proxy);		
			}
		
			try {
				Thread.sleep(DELAY_UPDATES);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public static void main(String[] args) {
		
		
	}
}
