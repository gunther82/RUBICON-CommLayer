//package CommunicationLayer;

import ucd.rubicon.network.RubiconAddress;

public class RubiconAck {
	private TransportMD tmd;
	
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
    private static RubiconAck rack;
    
    public static synchronized RubiconAck getRubiconAck() {
    	if(rack == null) {
    		rack = new RubiconAck();
    	}
    	return rack;
    }
    /*********** END CODE CLAUDIO: check if this solution is good ******************/
    
    
	public RubiconAck() {
		tmd = TransportMD.getTransportMD();
		
		tmd.setOnReceiveTransportRubiconAckListener(new ReceiveTransportRubiconAckListener() {
			@Override
			public void onReceiveTransportRubiconAckMessage(RubiconAddress sender, byte[] payload, short nbytes, byte reliable, int seqnum) {
				//System.out.println("onReceiveTransportRubiconAckMessage invoked");
                
				//set the size of the payload except the comp_id
				short len = (short)(nbytes - 1);
				//extract the Application comp_id from the first byte of the payload
				byte comp_id = payload[0];

				//o preparo qui il LearningNotificationMessage, o lo faccio preparare al momento della spedizione
				//nel primo caso il payload e' solo il seqnum (come attualmente in C), nel secondo e' direttamente il LearningNotificationMessage
				//copy the remain part of the payload (just the payload) in a new array to forward to the upper layer
				byte[] app_payload = new byte[len];
				for(int i = 0; i < len; i++) {
					app_payload[i] = payload[i+1];
				}
				
                //check to which Application-level component the message has to be forwarded
                switch(comp_id) {
                    case Definitions.CONTROL_LAYER:
                        if(receiveConlessControlListener!=null) {
                            //signal the reception of the message to the Control Layer component through the listener
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
	
	
	//send the ack for a message. the payload is an app_msg_t whose payload contains only the seq_num
	public void send_ack(RubiconAddress dest, byte[] payload, short nbytes, boolean reliable, short comp_id) {
		//set the new size of the payload
        short len = (short)(nbytes + 1);
        //fill the second field of the payload with the APP_ID. The Application already allocated the space for this field.
		payload[1] = (byte)comp_id;
		
        //call the TransportMD.send with the new payload and the new size
        tmd.send(dest, payload, len, false, Definitions.RUBICON_ACK);	
	}
}
