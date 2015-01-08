package ucd.rubicon.proxy;

import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ucd.rubicon.cognitive.CognitiveInterface;
import ucd.rubicon.cognitive.MockCognitiveLayer;
import ucd.rubicon.gateway.RubiconGateway;
import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessage;
import ucd.rubicon.network.RubiconMessageCtrlActuateCmd;
import ucd.rubicon.network.RubiconMessageCtrlPeriodicCmd;
import ucd.rubicon.network.RubiconMessageUpdates;
import ucd.rubicon.network.RubiconMessageJoin;
import ucd.rubicon.network.RubiconMessageListener;
import ucd.rubicon.network.SensorUpdate;

import com.sun.jna.Pointer;

import core.CallbackObject;
import core.PeisJavaMT;
import core.PeisTuple;
import core.WrongPeisKernelVersionException;
import core.PeisJavaInterface.PeisSubscriberHandle;
import core.PeisJavaInterface.PeisTupleCallback;
import core.PeisJavaMT.ENCODING;

public class RubiconProxy implements RubiconMessageListener, Runnable {

	protected static String PROPERTIES_FILE = "proxy.properties";
	
	protected static String PROPERTY_CMD_REFRESH_PERIOD = "proxy.cmd_refresh_period";
	protected static String PROPERTY_KEEPALIVE_WAITING_PERIOD = "proxy.keepalive_waiting_period";
	protected static String PROXY = "proxy";
	protected static String CONTROLLER_PREFIX = "controller_";
	
	protected static String DOT = ".";
	protected static String STAR = "*";
	protected static String UNDERSCORE = "_";
	
	protected int CMD_REFRESH_PERIOD = 5; // [seconds]
	protected int KEEPALIVE_WAITING_PERIOD = 10; // [seconds]
			
	protected Properties properties;
	protected DeviceRepository deviceRepository;
	protected Thread thread;
	protected RubiconGateway gateway;
	
	protected List<Controller> listControllers = new LinkedList<Controller>();
	
	public void parseControllers() {
		
		System.out.println("Parsing controllers' definitions");
		Enumeration e = getProperties().keys();
		while (e.hasMoreElements()) {
			String key 	 = (String)e.nextElement();
			String value = getProperties().getProperty(key);
			if (key.startsWith(CONTROLLER_PREFIX)) {
				String controllerName = key.substring(CONTROLLER_PREFIX.length());
				Controller controller = new Controller(controllerName);
  
				try {
					controller.parseFromString(value);
					listControllers.add(controller);
					controller.start();
					
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}				
			}
		}		
		
		System.out.println("Proxy controllers:");
		System.out.println(e);
	}

	public Properties getProperties() {
		return properties;
	}
	
	public RubiconProxy() {
		
		properties = new Properties(); 
		try {
		    properties.load(RubiconGateway.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
		} 
		catch (Exception ex) {
			System.out.println("RUBICON Proxy: Missing property file ("+PROPERTIES_FILE+") for RUBICON Gateway, it should be on the classpath.");
			System.exit(-1);
		}
		
		parseControllers();

		gateway = new RubiconGateway();		
		deviceRepository = new DeviceRepository();
		
		thread = new Thread(this);
		gateway.registerListener(this); // Mauro, before or after???
		thread.start();
	
	}
	
	public synchronized void addDevice(RubiconMessageJoin messageJoin) {
		deviceRepository.addDevice(messageJoin);
		
		System.out.println("RubiconProxy: addDevice");
		System.out.println("  Actuators:");

		Properties resources = messageJoin.getResources();
		for (Object kr : resources.keySet()) {
			String resourceName = (String)kr;
			String resourceType = resources.getProperty(resourceName);
		
			String key = PROXY+DOT+messageJoin.getSender().getNetworkName()+DOT+messageJoin.getSender().getDeviceId()+DOT+resourceName;
			//String key = messageJoin.getSender().getNetworkName()+DOT+messageJoin.getSender().getDeviceId()+DOT+resourceName;
			PeisJavaMT.peisjava_setStringTuple(key, "");			

			if (resourceType.contains("actuator")) {
				String commandKey = key+DOT+"command";
				System.out.println("Proxy registering callback to "+ this.gateway.getIslandID()+":"+commandKey);
				PeisJavaMT.peisjava_setStringTuple(commandKey,"");			
				
				//TODO store the callback somewhere so that you can delete it if the device goes
				PeisTupleCallback callback = PeisJavaMT.peisjava_registerTupleCallback(
					this.gateway.getIslandID(), 
					commandKey,				  
					new CallbackObject() {
						@Override
						public void callback(PeisTuple tuple) {
							System.out.println("In callback");
							try {
								String keys[] = tuple.getKey().split("\\.");
								String networkName = keys[1];
								String deviceSensorFullName = keys[2];
								int upos = deviceSensorFullName.indexOf(UNDERSCORE);
								String deviceId = deviceSensorFullName.substring(0, upos);
								System.out.println("DevicesensorFullName("+deviceSensorFullName+") Network("+networkName+") deviceId("+deviceId+")");
								RubiconAddress destination = new RubiconAddress(getGateway().getIslandID(), networkName, Integer.parseInt(deviceId));	
								RubiconAddress proxy = new RubiconAddress(getGateway().getIslandID(), 0);	
								RubiconMessageCtrlActuateCmd cmd = new RubiconMessageCtrlActuateCmd(proxy, (short)0, Integer.parseInt(tuple.getStringData()));
								getGateway().sendToNetwork(cmd, destination);
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
					}
				);
			};
			
			PeisJavaMT.peisjava_setStringTuple(key+DOT+"type", resourceType);
		}
	}
	
	public synchronized void removeDevice(RubiconAddress address) {
		
		NodeInformation node = deviceRepository.getNodeInformation(address);
				
		System.out.println("RubiconProxy: removeDevice");
		System.out.println("  Actuators:");
		PeisJavaMT.peisjava_deleteTuple(this.gateway.getIslandID(), address.getNetworkName()+DOT+address.getDeviceId()+DOT+STAR);
		
		deviceRepository.removeDevice(address);
	}
	
	
	public RubiconGateway getGateway() {
		return gateway;
	}

	// TODO
	// This is temporary to make the Learning Layer's process these inputs in reti Java
	public void forwardPassthroughEventsToLearningLayer(String key, int ids[]) {	
		String dataForLearningLayer = "";
		for (int i=0; i<ids.length; i++) {
			int id = ids[i];
			RubiconAddress a = new RubiconAddress(gateway.getIslandID(), id);
			NodeInformation info = deviceRepository.getNodeInformation(a);
			//System.out.println("Info["+id+"] id="+info.nodeSpec.addr+" info="+info);
			SensorUpdate data = info.getLastValue();
			//System.out.println("data = "+data);
			dataForLearningLayer+=data.getSensorValue();
			if (i<ids.length-1) dataForLearningLayer+=",";
		}
		//System.out.println("Setting 994:"+key+" to "+dataForLearningLayer);					
		PeisJavaMT.peisjava_setRemoteStringTuple(994, key, dataForLearningLayer);		
	}
	
	
	@Override
	public synchronized void handleMessageReceived(RubiconMessage message,
			RubiconAddress destination) {
		
		//System.out.println("RUBICON Proxy: Received message "+message);
		
		if (message instanceof RubiconMessageJoin ) {
			System.out.println("RUBICON Proxy: Received message join, adding it to the repository");
			addDevice((RubiconMessageJoin)message);		
			
			//send periodic command
			//sendPeriodicCommand(message.getSender()); 
		} else if (message instanceof RubiconMessageUpdates ) {
			//System.out.println("RUBICON Proxy: Received CtrlUpdate message");
			//message.
			//System.out.println("Proxy: Update message from device id "+message.getSender().getDeviceId());
			NodeInformation node = deviceRepository.updateNode((RubiconMessageUpdates)message);
			//System.out.println("Proxy: Updated node "+ node);
			RubiconMessageUpdates messageUpdate = (RubiconMessageUpdates)message;
			List<SensorUpdate> sensorUpdates = messageUpdate.getUpdate().getSensorUpdates();
			for (SensorUpdate sensorUpdate:sensorUpdates) {
				String key = PROXY+DOT+message.getSender().getNetworkName()+DOT+message.getSender().getDeviceId()+DOT+sensorUpdate.getUpdateName();  
				//String key = message.getSender().getNetworkName()+DOT+message.getSender().getDeviceId()+DOT+sensorUpdate.getUpdateName();
				PeisJavaMT.peisjava_setStringTuple(key, sensorUpdate.getSensorValue().toString());
				
				//TODO temporary translation of states for the Control Layer
				if (key.endsWith("TV_STATE")) {
					String value = sensorUpdate.getSensorValue().toString();
					String translation = "";
					int v = Integer.parseInt(value);
					if (v>0) {
						translation = "ON";
					} else {
						translation = "OFF";
					}
					PeisJavaMT.peisjava_setStringTuple(key+".state", translation);	
				}

			}
			
			// TODO
			// This is temporary to make the Learning Layer's process these inputs in reti Java
			int deviceId = messageUpdate.getSender().getDeviceId();
			/*
			if (deviceId == 68) { // perche' cosi' so che i valori che spedisco sono freschi				
				int inputTask1[] = {1,2,3,4,6,7,8};
				forwardPassthroughEventsToLearningLayer("LearningModulePC-2:-1", inputTask1);
				forwardPassthroughEventsToLearningLayer("LearningModulePC-2:-4", inputTask1);
				
				int inputTask3[] = {1,2,8,28};
				forwardPassthroughEventsToLearningLayer("LearningModulePC-2:-3", inputTask3);

				int inputTask5[] = {1,2,3,8,15};
				forwardPassthroughEventsToLearningLayer("LearningModulePC-2:-5", inputTask5);

			}
			*/
			
		}
				
	}

	public static void main(String[] args) {		
				
		try {
			System.out.println("RUBICON Proxy: Initializing PeisJavaMT with "+args+"...");
			//String[] ar  = {"--peis-id", "993"};
			//PeisJavaMT.peisjava_initialize(ar);
			PeisJavaMT.peisjava_initialize(args);
			
			System.out.println("PeisID is "+ PeisJavaMT.peisjava_peisid());
			
			RubiconProxy proxy = new RubiconProxy();			
				
			//CognitiveInterface cognitiveInterface = new CognitiveInterface();
			
			//MockCognitiveLayer mock = new MockCognitiveLayer();
			//mock.test(1);
			
			//String keys[] = {"LLeventID","LLeventIDValue"};
			//new PeisLog(994, keys);
			
		
		} catch (WrongPeisKernelVersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.exit(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}		
	}

	void sendPeriodicCommand(RubiconAddress dest) {
		short sensorsBitmask = (short)3;
		short period = 1000;
		RubiconAddress sender = new RubiconAddress(PeisJavaMT.peisjava_peisid(), 0xFFFD);
		RubiconAddress address = dest;
		// TODO, for which sensor? ALL for now
		RubiconMessageCtrlPeriodicCmd cmd = new RubiconMessageCtrlPeriodicCmd(sender, null, period);
		System.out.println("RUBICON Proxy: Sending periodic command to pid: " + address.getPeisId() + ", devid: " + address.getDeviceId());
		try {
			gateway.sendToNetwork(cmd,  address);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void run() {
		while (PeisJavaMT.peisjava_isRunning()) {
			
			Date now = new Date();
			Collection<NodeInformation> nodes = deviceRepository.getNodes();
			for (NodeInformation node: nodes) {
				if (now.getTime() < node.tmLastSeen.getTime() + KEEPALIVE_WAITING_PERIOD*1000) {
															
				} else {
					// TODO-PUT IT BACK AND TEST (ALSO FOR SALAD)
					//removeDevice(node.nodeSpec.getSender());
					/*
					deviceRepository.removeDevice(node.nodeSpec.getSender());
					PeisJavaMT.peisjava_deleteTuple(this.gateway.getIslandID(), KEY_MOTES+DOT+node.nodeSpec.getSender().getDeviceId()+STAR);
					*/
					//PeisJavaMT.peisjava_setStringTuple(KEY_MOTES+DOT+messageJoin.getSender().getDeviceId()+DOT+KEY_ACTUATOR+DOT+t.getName(), "");
				}
			}
			
			//TODO: Mauro, broadcast this message to all, to tell them to send data
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PeisJavaMT.peisjava_setStringTuple(PROXY, "alive");
		}
		System.out.println("RubiconProxy stopped");
	}
	
}
