package cnr.rubicon.cml;
import java.util.Properties;

import ucd.rubicon.network.RubiconAddress;

/**
 * Unit Test
 * @author rubicon
 *
 */
public class Test {
	
	protected static Connectionless connection = null;
	protected static Network network = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
				
		network = Network.getNetwork();
		Properties properties = new Properties();
		properties.put(Network.NETWORK_SOURCE, args[0]);
		//network._init(11, null, properties);
		
		connection  = Connectionless.getConnectionless();
		
		connection.setOnReceiveConlessControlMessage(
				new ReceiveConlessControlListener() {

					@Override
					public void onReceiveConlessControlMessage(
							RubiconAddress sender, byte[] payload, short nbytes) {
						System.out.println("Test - received packet type "+payload[0]+ " from island #"+
							sender.getPeisId()+" device id "+sender.getDeviceId());
						
					}
					
				}
		);
		
		
	}

}
