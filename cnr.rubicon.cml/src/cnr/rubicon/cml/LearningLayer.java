package cnr.rubicon.cml;
//
//  LearningLayer.java
//  RUBICON src
//
//  Created by Claudio Vairo on 20/05/13.
//
//

import ucd.rubicon.network.RubiconAddress;

public class LearningLayer {
    
    private Connectionless connless;
    //private SynChannel synch;
    
    public LearningLayer() {
        
        connless = new Connectionless();
        
        connless.setOnReceiveConlessLearningMessage(new ReceiveConlessLearningListener() {
			@Override
			public void onReceiveConlessLearningMessage(RubiconAddress sender, byte[] payload, short nbytes) {
				System.out.println("onReceiveConlessLearningMessage invoked");
                
                //hanlde the received message
			}
		});
        
//        synch.setOnReceiveSynData(new ReceiveSynDataListener() {
//			@Override
//			public void onReceiveSynData(long channel_id, short status) {
//				System.out.println("onReceiveSynData invoked");
//                
//                //hanlde the reception of synaptic data
//			}
//		});
    }
    
    
}