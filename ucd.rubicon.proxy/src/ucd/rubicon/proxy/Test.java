package ucd.rubicon.proxy;

import java.util.List;
import java.util.Properties;

import ucd.rubicon.gateway.RubiconGateway;
import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessage;
import ucd.rubicon.network.RubiconMessageListener;
import ucd.rubicon.network.RubiconNetwork;
import core.PeisJavaMT;
import core.WrongPeisKernelVersionException;

/**
 * Test the {@Link RubiconProxy} by installing a dummy network {@Link DummyNetwork}
 * which send a periodic message to a remote island
 * and introduces two fake devices with different sensors and actuators
 * 
 * Test by running two shells:
 * 
 * shell1>~/workspace/ucd.rubicon.proxy/testRubiconProxy5.sh
 * shell2>~/workspace/ucd.rubicon.proxy/testRubiconProxy6.sh
 *
 * The first shell starts the proxy with peis-id 5 and sends data to the island with peis-id 6
 * The second shell starts the proxy with peis-id 6 and sends data to the island with peis-id 5
 * 
 * When both gateways are running, they receive each other data
 *  
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 *
 * @see RubiconProxy
 */
public class Test {
	
	public static void main(String[] args) {		
		// the first argument should be the peis-id of the bridge on the remote island
		final int remotePeisId = Integer.parseInt(args[0]); 
				
		try {
			System.out.println("Test RUBICON Proxy: Initializing PeisJavaMT...");
			PeisJavaMT.peisjava_initialize(args);
			
			System.out.println("PeisID is "+ PeisJavaMT.peisjava_peisid());
			
			// Create the Gateway
			RubiconGateway gateway = new RubiconGateway();

			// Create and install the proxy
			RubiconProxy proxy = new RubiconProxy();
			gateway.registerListener(proxy);

			
			// install a Dummy Network (normally the class of the network
			// is specified in the gateway.properties file, in the network.class field
			// but here we are installing a dummy network just for testing
			DummyNetwork dummyNetwork = new DummyNetwork();
			Properties properties = new Properties();
			dummyNetwork._init(gateway.getIslandID(), gateway, properties);
			
			// sets the peis id of the remote island where to send dummy messages
			// this is only for test purpose
			dummyNetwork.setRemoteIslandPeisID(remotePeisId);
			
			// install the network with the gateway
			gateway.installNetwork(dummyNetwork);
					
		} catch (WrongPeisKernelVersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}		
	}
}