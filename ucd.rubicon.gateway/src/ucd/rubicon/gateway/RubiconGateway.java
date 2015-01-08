package ucd.rubicon.gateway;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessage;
import ucd.rubicon.network.RubiconMessageListener;
import ucd.rubicon.network.RubiconMessageSender;
import ucd.rubicon.network.RubiconNetwork;
import core.CallbackObject;
import core.PeisJavaInterface.PeisSubscriberHandle;
import core.PeisJavaInterface.PeisTupleCallback;
import core.PeisJavaMT;
import core.PeisJavaMT.ENCODING;
import core.PeisTuple;
import core.WrongPeisKernelVersionException;


/**
 * Implements the RUBICON gateway enabling the interface between multiple network layers and the transmission of rubicon messages ({@Link RubiconMessage} messages} between remote islands.
 * Each gateway is identified by its unique peis-id. This must be set programmatically by using the --peid-id <int> argument
 * before Peis is initialized (e.g. by the client program using the gateway).
 * Each gateway is designed to  administer a number of local network layers (but for the moment we only handle one
 * TinyOS Network layer).
 * When a payload is sent to a remote device with device id <DEVICE-ID> on a remote island with peis-id <REMOTE-ID>, it is simply added 
 * to the remote tuple with key <REMOTE-ID>:RUBICON_GATEWAY.<DEVICE-ID> 
 * Each gateway subscribes a callback to these tuples in order to be notified of incoming data.
 * The gateway manages a list of {@Link RubiconNetwork} that can register themselves with the gateway to be notified upon the reception of each message
 * 
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 *
 * @see RubiconNetwork
 */
public class RubiconGateway implements RubiconMessageSender {
		

	protected static String PROPERTIES_FILE = "gateway.properties";
	protected static String NETWORK_CLASS = "network.class";
	
	protected int MAX_ISLAND_ID = 65535;
	protected Properties properties = null;

	/**
	 * The map of networks connected to this gateway
	 */	
	//protected List<RubiconNetwork> networks = (List<RubiconNetwork>) Collections.synchronizedList(new LinkedList<RubiconNetwork>());
	protected Map<String, RubiconNetwork> mapNetworks = (Map<String, RubiconNetwork>)Collections.synchronizedMap(new HashMap<String, RubiconNetwork>());
	
	/**
	 * The list of local listeners interested in receiving messages addressed to
	 * this island
	 */
	protected List<RubiconMessageListener> listeners = (List<RubiconMessageListener>) Collections.synchronizedList(new LinkedList<RubiconMessageListener>());
		

	/**
	 * Prefix of the key for the tuples used to publish the data
	 */
	static final String GATEWAY_KEY = "RUBICON_GATEWAY";
	
	protected PeisTupleCallback callback = null;	
	Map<Integer, PeisSubscriberHandle> mapSubscriptions = Collections.synchronizedMap(new HashMap<Integer, PeisSubscriberHandle>());

	private int islandID;

	/** 
	 * Check if already subscribed to the remote tuple used to forward data to a remote island
	 * and it subscribes if it was not already subscribed 
	 * @param remotePeisId the peis id for the bridge used in the remote island
	 */
	public void checkSubscription(int remotePeisId) {		
		PeisSubscriberHandle hnd = mapSubscriptions.get(remotePeisId);
		if (hnd==null) {
			PeisTuple abstractBridgeTuple = new PeisTuple();
			abstractBridgeTuple.setOwner(remotePeisId);
			PeisJavaMT.peisjava_initAbstractTuple(abstractBridgeTuple);
			PeisJavaMT.peisjava_setTupleName(abstractBridgeTuple, GATEWAY_KEY+".*");			
			hnd = PeisJavaMT.peisjava_subscribe(abstractBridgeTuple);
			mapSubscriptions.put(remotePeisId, hnd);
		}
	}

	
	/** 
	 * Creates the RubiconGateway
	 * Peis must be already initialized with a --peis-id less than 256 in order
	 * to be compatible with the RUBICON Communication Layer. 
	 */
	public RubiconGateway() {
		this.islandID = PeisJavaMT.peisjava_peisid();
				
		if (getIslandID()<1 || getIslandID() > this.MAX_ISLAND_ID) {
			System.out.println("RUBICON Gateway: The PEIS ID is assumed as RUBICON island ID and should be between [1 and "+ this.MAX_ISLAND_ID+"]");
			System.exit(-1);
		}
	
		properties = new Properties(); 
    	try {
    		properties.load(RubiconGateway.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } 
    	catch (Exception ex) {
    		System.out.println("RUBICON Gateway: Missing property file ("+PROPERTIES_FILE+") for RUBICON Gateway, it should be on the classpath.");
    		//System.exit(-1);
    	}
    	
    	// Install Networks (optional, if you use the gateway just to access
    	// the Rubicon network, as in the LearningLayer, you do not need
    	// to install a RubiconNetwork object
    	System.out.println("RubiconGateway, properties: "+properties);
    	Enumeration ep = properties.keys();
    	while (ep.hasMoreElements()) {
    		String key = (String)ep.nextElement();
    		System.out.println("key is ("+key+")");
    		String value = properties.getProperty(key);
    		if (key.startsWith(NETWORK_CLASS)) {
    			int i=key.indexOf(NETWORK_CLASS);
    			String networkName = key.substring(NETWORK_CLASS.length());
    			String networkClassName = value;
    			
    			System.out.println(".....................................");
    			System.out.println("RubiconGateway: Instantiating "+ networkClassName);
    			RubiconNetwork network = null;
    			try {
    				Class<?> clazz;
    				clazz = Class.forName(networkClassName);
    				System.out.println("clazz = "+clazz);		
    				network = (RubiconNetwork) (clazz.newInstance());
    				System.out.println("network = " + network);		
    				System.out.println("calling network._init(...)");
    				network._init(this.getIslandID(), this);
    				System.out.println("calling installNetwork...");
    				this.installNetwork(network);
    			}
    			catch (ClassNotFoundException e) {
    				System.out.println("RUBICON Gateway: Missing network class ("+networkClassName+") from classpath!");
    				e.printStackTrace();
    			} catch (InstantiationException e) {
    					e.printStackTrace();
    			} catch (IllegalAccessException e) {
    					e.printStackTrace();
    			} catch (Throwable e) {
    					e.printStackTrace();
    			}
    		
    			if (network == null) {
    				System.exit(-1);
    			}
    		}
    	}
    		
		System.out.println("RUBICON Gateway: initialized with island ID "+getIslandID());		
		
		System.out.println("RUBICON Gateway: Registering callback for "+getIslandID()+":"+GATEWAY_KEY+".*");
		
		callback = PeisJavaMT.peisjava_registerTupleCallback(
				getIslandID(), 
				GATEWAY_KEY+".*",				  
				new CallbackObject() {
					@Override
					public void callback(PeisTuple tuple) {
						RubiconMessage msgReceived = null;									    	  
						RubiconAddress destination = null;
						
						try {							
							String keys[] = tuple.getKey().split("\\.");							
							msgReceived = (RubiconMessage)getObjectFromByteArray(tuple.getByteData());									    	  
							destination = new RubiconAddress(getIslandID(), keys[1], Integer.parseInt(keys[2]));							
						}
						catch (Throwable t) {
							System.out.println("RUBICON Gateway: I cannot process incoming peis message, error in payload and/or address?");
							//t.printStackTrace();
						}
						
						// notify all the local networks
						// TODO: (probably needs to check if the message must reach a local network or if it
						// is just addressed to a local, but high-level listener (e.g. Learning Layer)
						sendToNetwork(msgReceived, destination);
															
					    notifyLocalListeners(msgReceived, destination);
				    }				    		  				      
				} //End CallbackObject definition
		);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutdown();
			}
		});
	}
	
	public void notifyLocalListeners(RubiconMessage msgReceived, RubiconAddress destination) {
		// notify all the local listeners
		for (RubiconMessageListener listener : listeners) {
			try {
				listener.handleMessageReceived(msgReceived, destination);
			}
			catch (Throwable t) {
				System.out.println("RUBICON Gateway: I cannot route incoming peis message to local listener "+listener+ ", wrong message class/payload?");
				t.printStackTrace();
			}
		}
	}

    public int getIslandID() {
		return islandID;
	}

	public Properties getProperties() {
		return properties;
	}
		
	
	protected byte[] getByteArrayFromObject(Object obj) {		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] bytes = null;
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(obj);
			bytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

	protected Object getObjectFromByteArray(byte [] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		Object obj = null;
		try {
			in = new ObjectInputStream(bis);
			obj = in.readObject(); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		return obj;
	}
	
	/* 
	 * Forwards a given {@Link RubiconMessage} to the destination (another {@Link RubiconGateway})		
	 * @param message the message to be sent
	 * @param destination the {@Link RubiconAddress} of where the message must be delivered
	 * @throws Exception if the destination was unreachable or if the transmission failed 
	 */			
	public void send(RubiconMessage message, RubiconAddress destination) throws Exception {
		//checkSubscription(remotePeisId); //TODO test if needed when running two bridges on different machines
		//TODO: Mauro, temporary, this is the special address of the proxy
		System.out.println("RubiconGateway::send, destination="+ destination.getPeisId());
		//TODO this 11 seems generated directly by the nesc code, for now forward these messages
		//to the proxy
		if (destination.getPeisId()==11) {
			System.out.println("RubiconGateway: Received msg");
			notifyLocalListeners(message, destination);
		}
		else if (destination.getPeisId()==this.getIslandID()) {
			System.out.println("RubiconGateway: notityLocalListeners");
			notifyLocalListeners(message, destination);
		} else {
			byte payload[] = this.getByteArrayFromObject(message);
			PeisJavaMT.peisjava_setRemoteTuple(destination.getPeisId(), GATEWAY_KEY+"."+destination.getDeviceId(), payload, "application/rubicon", ENCODING.BINARY);
		}
	}
		
	public void sendToNetwork(RubiconMessage message, RubiconAddress destination) {
		RubiconNetwork network = mapNetworks.get(destination.getNetworkName());

		if (network == null) {
			System.out.println("RubiconGateway, received message from Peis for unspecified network");
		} else {
			try {
	            network.handleMessageReceived(message, destination);
	    	}
			catch (Throwable t) {
				System.out.println("RUBICON Gateway: I cannot route incoming peis message to network "+network+ ", wrong message class/payload?");
				//t.printStackTrace();
			}					            
	    }
	}
	
	/**
	 * Shutdown this gateway 
	 */
	public void shutdown() {
		System.out.println("RUBICON Gateway: Shutting down...");
		for (PeisSubscriberHandle handle:mapSubscriptions.values()) {
	    	PeisJavaMT.peisjava_unsubscribe(handle);
	    }
		PeisJavaMT.peisjava_unregisterTupleCallback(callback);
	}

	
    public void installNetwork(RubiconNetwork network) {
        mapNetworks.put(network.getNetworkName(), network);
    }
 
    public void removeNetwork(RubiconNetwork network) {
        mapNetworks.remove(network.getNetworkName());
    }
 
    public void registerListener(RubiconMessageListener listener) {
    	listeners.add(listener);
    }
 
    public void removeListener(RubiconMessageListener listener) {
        listeners.remove(listener);
    }
    
    public static void main(String[] args) {		
				
		try {
			System.out.println("RUBICON Gateway: Initializing PeisJavaMT...");
			PeisJavaMT.peisjava_initialize(args);
			
			System.out.println("RUBICON Gateway: PeisID is "+ PeisJavaMT.peisjava_peisid());
			
			RubiconGateway gateway = new RubiconGateway();
								
		} catch (WrongPeisKernelVersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}		
	}
	
	
		
}
