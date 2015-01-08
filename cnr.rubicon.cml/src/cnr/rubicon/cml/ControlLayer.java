package cnr.rubicon.cml;
//
//  ControlLayer.java
//  RUBICON src
//
//  Created by Claudio Vairo on 20/05/13.
//
//

import java.util.Properties;

import cnr.rubicon.migFiles.SerialJoinedMsg;
import cnr.rubicon.migFiles.UpdatesMsg;

import ucd.rubicon.network.RubiconAddress;

public class ControlLayer {

	private Connectionless cless;
	
	
	public ControlLayer() {
		
		System.out.println("Test ControlLayer interface, initializing TinyOS Network...");
		Properties properties = new Properties();
		properties.put("source", "serial@/dev/ttyUSB0:telosb");
		try {
			Network.getNetwork()._init(12, null, properties);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cless = Connectionless.getConnectionless();
		
		cless.setOnReceiveConlessControlMessage(new ReceiveConlessControlListener() {
			@Override
			public void onReceiveConlessControlMessage(RubiconAddress sender, byte[] payload, short nbytes) {
				switch(payload[0]) {
				case Definitions.AM_SERIAL_JOINED_MSG:
					System.out.println("[CONTROL LAYER]: Received a serial joined msg");
					SerialJoinedMsg smsg = new SerialJoinedMsg(payload);
					System.out.println("------  Mote Descriptor received ------"); 
					System.out.println("mote_type: " + smsg.get_mote_type() + "\ntransducers: " + smsg.get_transducers() +
							"\nactuators: " + smsg.get_actuators());
					break;
				
				
				case Definitions.AM_UPDATES_MSG:					
					UpdatesMsg up = new UpdatesMsg(payload);
					int trans = up.get_output_size();
					System.out.println("[CONTROL LAYER]: Received an updates msg of size " + nbytes + " containing " + trans + " transducers readings:");
					int[] reads = up.get_updates();
					int j = 0;
					for(int i = 0; i < trans; i++, j+=2) {
						System.out.println("trans"+reads[j]+ " = " + reads[j+1]);
					}
					break;
				
				}				
			}
		});	
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ControlLayer cl = new ControlLayer();
	}

}
