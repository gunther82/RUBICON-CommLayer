//package CommunicationLayer;

//
//  TransportMD.java
//  RUBICON src
//
//  Created by Claudio Vairo on 10/04/13.
//  Copyright (c) 2013 ISTI-CNR. All rights reserved.
//

import ucd.rubicon.network.RubiconAddress;
//import ucd.rubicon.network.RubiconMessage;
//import ucd.rubicon.network.RubiconMessageJoin;
//import ucd.rubicon.network.RubiconMessageSender;
//import ucd.rubicon.network.RubiconNetwork;

public class TransportMD {
	
	private Network network;
	
    //notification to Connectionless
    private ReceiveTransportConnlessListener receiveTransportConnlessListener;
    //notification to Synaptic Channel
    private ReceiveTransportSynchannListener receiveTransportSynchannListener;
    //notification to Rubicon Ack
    private ReceiveTransportRubiconAckListener receiveTransportRubiconAckListener;
	
	//set the Listener for the event reception of a Connectionless message
    public void setOnReceiveTransportConnlessListener(ReceiveTransportConnlessListener listener) {
    	receiveTransportConnlessListener = listener;
    }
    
    //set the Listener for the event reception of a Synaptic Channel message
    public void setOnReceiveTransportSynchannListener(ReceiveTransportSynchannListener listener) {
    	receiveTransportSynchannListener = listener;
    }
    
    //set the Listener for the event reception of a Rubicon Ack message
    public void setOnReceiveTransportRubiconAckListener(ReceiveTransportRubiconAckListener listener) {
    	receiveTransportRubiconAckListener = listener;
    }
    
    
    /*********** START CODE CLAUDIO: check if this solution is good ******************/
    private static TransportMD tmd;
    
    public static synchronized TransportMD getTransportMD() {
    	if(tmd == null) {
    		tmd = new TransportMD();
    	}
    	return tmd;
    }
    /*********** END CODE CLAUDIO: check if this solution is good ******************/
    
    
    /*********** START CODE CLAUDIO: test with Davide TO BE REMOVED ******************/
    public Network getNetwork() {
    	return network;
    }
    /*********** END CODE CLAUDIO: test with Davide TO BE REMOVED ******************/
    
    
	public TransportMD() {
		//network = new Network();
		network = Network.getNetwork();
		//network.init();
		
		network.setOnReceiveNetworkListener(new ReceiveNetworkListener() {
			@Override
			public void onReceiveNetworkMessage(RubiconAddress sender, byte[] payload, short nbytes, byte reliable, int seqnum) {
				//System.out.println("onReceiveNetworkMessage invoked");				
                
                //set the size of the payload except the comp_id
                short len = (short)(nbytes - 1);
                //extract the Transport comp_id from the first byte of the payload
                byte comp_id = payload[0];
                
                //copy the remain part of the payload (APP_ID, payload) in a new array to forward to the upper layer
                byte[] trans_payload = new byte[len];
                for(int i = 0; i < len; i++) {
                    trans_payload[i] = payload[i+1];
                }
                //check to which Transport-level component the message has to be forwarded
                switch(comp_id) {
                    case Definitions.CONNLESS:
                        if(receiveTransportConnlessListener!=null) {
                            //signal the reception of the message to the Connectionless component through the listener
                            receiveTransportConnlessListener.onReceiveTransportConnlessMessage(sender, trans_payload, len, reliable, seqnum);
                        }
                        break;
                        
                    case Definitions.SYN_CHANNEL:
                        if(receiveTransportSynchannListener!=null) {
                            //signal the reception of the message to the Synaptic Channel component through the listener
                            receiveTransportSynchannListener.onReceiveTransportSynchannMessage(sender, trans_payload, len, reliable, seqnum);
                        }
                        break;
                    case Definitions.RUBICON_ACK:
                    	if(receiveTransportRubiconAckListener!=null) {
                            //signal the reception of the message to the Rubicon Ack component through the listener
                    		receiveTransportRubiconAckListener.onReceiveTransportRubiconAckMessage(sender, trans_payload, len, reliable, seqnum);
                        }
                    	break;
                } 
			}
		});
	}

	//add the Transport ID (comp_id) to the payload and forwards the new payload to the Network
	public synchronized int send(RubiconAddress dest, byte[] payload, short nbytes, boolean reliable, short comp_id) {
		int seq;
		//set the new size of the payload
		short len = (short)(nbytes + 1);
		//fill the first field of the message with the Transport Component ID. The Application already allocated the space for this field.
		payload[0] = (byte)comp_id;
		
		//call the Network.send with the new payload and the new size
		seq = network.send(dest, payload, len, reliable);
		
		return seq;
	}
	
	
	

}
