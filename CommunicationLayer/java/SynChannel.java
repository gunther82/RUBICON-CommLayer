//package CommunicationLayer;

//
//  SynChannel.java
//  RUBICON src
//
//  Created by Claudio Vairo on 17/05/13.
//
//

import ucd.rubicon.network.RubiconAddress;

public class SynChannel {

    private TransportMD tmd;
    
    public SynChannel() {
        
        tmd = TransportMD.getTransportMD();
        
        
        tmd.setOnReceiveTransportSynchannListener(new ReceiveTransportSynchannListener() {
			@Override
			public void onReceiveTransportSynchannMessage(RubiconAddress sender, byte[] payload, short nbytes, byte reliable, int seqnum) {
				System.out.println("onReceiveTransportSynchannMessage invoked");
                

                //signal the reception to the proper Transport component according to the comp_id
                //signal TransportMD.receive[comp_id](sender, trans_payload, len);
			}
		});
        
    }

}
