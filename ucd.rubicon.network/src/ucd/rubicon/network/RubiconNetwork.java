package ucd.rubicon.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Abstract class to be specialized by specific networks to operate with the RUBICON Gateway
 *  
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 * 
 */
public abstract class RubiconNetwork implements RubiconMessageListener {
	
	protected static String PROPERTY_NETWORK_NAME = "name";
	protected int islandID = -1; 
	protected RubiconMessageSender gateway;
	protected Properties properties;
	protected Map<Class, RubiconMessageConverter> mapConverterToRubiconFormat = new HashMap<Class, RubiconMessageConverter>();
	protected Map<Class, RubiconMessageConverter> mapConverterToNetworkFormat = new HashMap<Class, RubiconMessageConverter>();
	protected String networkName = "network";
	
	// TODO, the network should have a name (could be a property)
	public RubiconNetwork() {
		System.out.println("Constructor RubiconNetwork");
	}

	/**
	 * Initialize the RubiconNetwork 
	 * @param islandID the island ID 
	 * @param gateway the reference to the RUBICON Gateway
	 * @param properties properties to be used to configure the RubiconNetwork
	 * @throws Exception 
	 */
	public void _init(int islandID, RubiconMessageSender gateway, Properties properties) throws Exception {
		
		System.out.println("In RubiconNetwork:_init island is "+islandID);
		this.islandID = islandID;
		this.gateway = gateway;
	
		if (properties != null) {
			this.properties = properties;
		} else {
			this.properties = new Properties();
			String propertyFileName = this.getClass().getSimpleName()+".properties";
			System.out.println("Loading "+propertyFileName);
			this.properties.load(this.getClass().getClassLoader().getResourceAsStream(propertyFileName));
			if (this.properties.get(PROPERTY_NETWORK_NAME)!=null) {
				networkName = (String)this.properties.get(PROPERTY_NETWORK_NAME);
			}
		}
		
		System.out.println("In RubiconNetwork:_init calling Network.init()");
		init();
	}
	
	public void _init(int islandID, RubiconMessageSender gateway) throws Exception {
		_init(islandID, gateway, null);		
	}
	
	/** 
	 * Must be sub-classed by specific RubiconNetwork implementations to perform additional
	 * initialization steps
	 */
	public abstract void init() throws Exception;
	
	/**
	 * 
	 * @return the properties defined for this network
	 */
	public Properties getProperties() {
		return properties;
	}
	
	/**
	 * 
	 * @return the reference to the RubiconGateway
	 */
	protected final RubiconMessageSender getGateway() {
		return gateway;
	}

	public void translateAndSendToGateway(Object msg, RubiconAddress sender, RubiconAddress where) {
		sendToGateway(this.getRubiconMessage(msg, sender, where), where);
	}

	public void sendToGateway(RubiconMessage msg, RubiconAddress where) {
		
		try {
			this.getGateway().send(msg, where);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return the ID of the island where this network is installed
	 */
	public final int getIslandID() {
		return islandID;
	}
	
	/**
	 * Sends a message via the {@Link RubiconGateway}.
	 * It must overridden by specific network implementations, e.g. to translate between
	 * Network-specific message representations and {@Link RubiconMessage} 
	 * @param message		network-specific object to be sent via the {@Link RubiconGateway}
	 * @param destination   RubiconAddres of the end-point destination for this message
	 * TODO: this must be abstract
	 * @throws Exception 
	 */		
	public final void installMessageConverter (RubiconMessageConverter converter) throws Exception {
		System.out.println("RubicoNetwork::instalMessageConverter");
		System.out.println("  Rubicon Format Class is "+converter.getClassRubiconFormat());
		System.out.println("  Network Format Class is "+converter.getClassNetworkFormat());
		if (converter.getClassRubiconFormat()==null) {
			throw new Exception("Message converter "+converter +" must return rubicon format class!");
		}
		if (converter.getClassNetworkFormat()==null) {
			throw new Exception("Message converter "+converter +" must return network format class!");
		}		
		mapConverterToNetworkFormat.put((Class<?>)converter.getClassRubiconFormat(), converter);
		mapConverterToRubiconFormat.put((Class<?>)converter.getClassNetworkFormat(), converter);
	}
		
	public final RubiconMessageConverter getConverterToRubiconFormat(Object networkMessage) {
		System.out.println("RubiconMessageConverter getConverterToRubiconFormat "+networkMessage.getClass());
		System.out.println("RubiconMessageConverter map is "+mapConverterToRubiconFormat);
		return mapConverterToRubiconFormat.get(networkMessage.getClass());
	}

	public final RubiconMessageConverter getConverterToNetworkFormat(RubiconMessage rubiconMessage) {
		return mapConverterToNetworkFormat.get(rubiconMessage.getClass());
	}
	
	public RubiconMessage getRubiconMessage(Object networkMessage, RubiconAddress sender, RubiconAddress destination) {
		System.out.println("RubiconNetwork::getRubiconMessage, getting converter for "+networkMessage);
		RubiconMessageConverter converter = getConverterToRubiconFormat(networkMessage);
		System.out.println("  Message onverter is "+converter);
		if (converter != null) {
			try {
				return converter.getRubiconFormat(networkMessage, sender, destination);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public Object getNetworkMessage(RubiconMessage rubiconMessage, RubiconAddress sender, RubiconAddress destination) {
		RubiconMessageConverter converter = getConverterToNetworkFormat(rubiconMessage);
		if (converter != null) {
			try {
				return converter.getNetworkFormat(rubiconMessage, sender, destination);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getNetworkName() {
		return networkName;
	}
}
