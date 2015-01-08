//package CommunicationLayer;

//
//  Connectionless.java
//  RUBICON src
//
//  Created by Claudio Vairo on 10/04/13.
//  Copyright (c) 2013 ISTI-CNR. All rights reserved.
//

import ucd.rubicon.network.RubiconAddress;

public class Connectionless {

	private TransportMD tmd;
    private RubiconAck rack;
	
    //notification to Control layer
    private ReceiveConlessControlListener receiveConlessControlListener;
    //notification to Learning layer
    private ReceiveConlessLearningListener receiveConlessLearningListener;
    
    
    //set the Listener for the event reception of a Control layer message
    public void setOnReceiveConlessControlMessage(ReceiveConlessControlListener listener) {
    	receiveConlessControlListener = listener;
    }
    
    //set the Listener for the event reception of a Learning message
    public void setOnReceiveConlessLearningMessage(ReceiveConlessLearningListener listener) {
    	receiveConlessLearningListener = listener;
    }
    
    
    /*********** START CODE CLAUDIO: check if this solution is good ******************/
    private static Connectionless connless;
    
    public static synchronized Connectionless getConnectionless() {
    	if(connless == null) {
    		connless = new Connectionless();
    	}
    	return connless;
    }
    /*********** END CODE CLAUDIO: check if this solution is good ******************/
    
    /*********** START CODE CLAUDIO: test with Davide TO BE REMOVED ******************/
    public Network getNetwork() {
    	return tmd.getNetwork();
    }
    /*********** END CODE CLAUDIO: test with Davide TO BE REMOVED ******************/
    
    
    //task that prepare the ack message and call the Rubicon_Ack.send_ack
    private void sendAck(short ack_compid, int ack_seqnum, RubiconAddress ack_dest) {
        AppMsg ackmsg = new AppMsg();
        //prepare the learning_notification_msg_t containing the sequence number and the code of error
        LearningNotificationMsg ackpayload = new LearningNotificationMsg();
        ackpayload.set_type(Definitions.AM_LEARNING_NOTIFICATION_MSG);
        ackpayload.set_seq_num(ack_seqnum);
        ackpayload.set_ack(Definitions.ACK_SUCCESS);

        //copy the learning_notification_msg_t in the payload of the ackmsg
        ackmsg.set_payload(ackpayload.dataGet());
        //call the send_ack. the size is the size of the seqnum
        rack.send_ack(ack_dest, ackmsg.dataGet(), (short)ackpayload.dataLength(), false, ack_compid);
    }
    
    
	public Connectionless() {
		tmd = TransportMD.getTransportMD();
        rack = RubiconAck.getRubiconAck();
        
        tmd.setOnReceiveTransportConnlessListener(new ReceiveTransportConnlessListener() {
			@Override
			public void onReceiveTransportConnlessMessage(RubiconAddress sender, byte[] payload, short nbytes, byte reliable, int seqnum) {
				//System.out.println("onReceiveTransportConnlessMessage invoked");
                
                //set the size of the payload except the comp_id
                short len = (short)(nbytes - 1);
                //extract the Application comp_id from the first byte of the payload
                byte comp_id = payload[0];

                //copy the remain part of the payload (just the payload) in a new array to forward to the upper layer
                byte[] app_payload = new byte[len];
                for(int i = 0; i < len; i++) {
                    app_payload[i] = payload[i+1];
                }
                
                if(reliable == 1) {
                    //send back the ack
                	sendAck((short)comp_id, seqnum, sender);
                }
                
                //check to which Application-level component the message has to be forwarded
                switch(comp_id) {
                    case Definitions.CONTROL_LAYER:
                        if(receiveConlessControlListener!=null) {
                            //signal the reception of the message to the Control Layer component through the listener
                            System.out.println("Received a msg, forwarding to CONTROL LAYER");
                            receiveConlessControlListener.onReceiveConlessControlMessage(sender, app_payload, len);
                        }
                        break;
                        
                    case Definitions.LEARNING:
                        if(receiveConlessLearningListener!=null) {
                            //signal the reception of the message to the Leraning Layer component through the listener
                            receiveConlessLearningListener.onReceiveConlessLearningMessage(sender, app_payload, len);
                        }
                        break;
                }
                
			}
		});
        
	}
	
	//add the Application ID (comp_id) to the payload and forwards the new payload to the Network
    public synchronized int send(RubiconAddress dest, byte[] payload, short nbytes, boolean reliable, short comp_id) {
		int seq_num;
        //set the new size of the payload
        short len = (short)(nbytes + 1);
        //fill the second field of the payload with the APP_ID. The Application already allocated the space for this field.
		payload[1] = (byte)comp_id;
		
        //call the TransportMD.send with the new payload and the new size
        seq_num = tmd.send(dest, payload, len, reliable, Definitions.CONNLESS);
		
		return seq_num;

	}
}
