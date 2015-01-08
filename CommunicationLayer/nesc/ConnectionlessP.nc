/*
 *  ConnectionlessP.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 09/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Implementation of the Communication layer for the Connectionless interface.
 */

//#include "network.h"
//#include "definitions.h"

module ConnectionlessP {
	//list of provided interfaces
	provides {
		interface Connectionless[uint8_t comp];
	}
	
	//list of used interfaces
    uses interface TransportMD;
	uses interface Rubicon_Ack;
	uses interface Leds;
	
}

implementation {

    uint8_t ack_compid;
    uint16_t ack_seqnum;
    r_addr_t ack_dest;
    
    //add the Application ID (comp_id) to the payload and forwards the new payload to the TransportMD
	command uint16_t Connectionless.send[uint8_t comp](r_addr_t dest, int8_t* payload, uint8_t nbytes, bool reliable, uint8_t comp_id) {
        uint16_t seq_num;
        //set the new size of the payload
        uint8_t len = nbytes + 1;
        //pointer to the struct allocated by the Application layer for [TRANS_ID, APP_ID, payload], that for the moment contains only the payload
        int8_t* ptr = payload;
        //increase the pointer to the second field of the struct that will contain the APP_ID.
        ptr++;
        //fill this field with the APP_ID
        *ptr = comp_id;
		
        //call the TransportMD.send with the new payload and the new size
        seq_num = call TransportMD.send(dest, payload, len, reliable, CONNLESS);

		return seq_num;
	}

    //task that prepare the ack message and call the Rubicon_Ack.send_ack
    task void sendAck() {
        app_msg_t ackmsg;
        //prepare the learning_notification_msg_t containing the sequence number and the code of error
        learning_notification_msg_t ackpayload;
        ackpayload.type = AM_LEARNING_NOTIFICATION_MSG;
        ackpayload.seq_num = ack_seqnum;
        ackpayload.ack = ACK_SUCCESS;
        
        //copy the learning_notification_msg_t in the payload of the ackmsg
        memcpy(&(ackmsg.payload), &ackpayload, sizeof(learning_notification_msg_t));
        //call the send_ack. the size is the size of the seqnum
        call Rubicon_Ack.send_ack(ack_dest, (int8_t*)&ackmsg, sizeof(learning_notification_msg_t), FALSE, ack_compid);
    }
    
    
    //retrieve the Application ID (comp_id) from the message received from the Transport and dispatches it to the proper Application component
    event void TransportMD.receive(r_addr_t sender, int8_t* payload, uint8_t nbytes, bool reliable, uint16_t seqnum) {
        //set the size of the payload except the comp_id
        uint8_t len = nbytes - 1;
        uint8_t comp_id;
        
        atomic {
            //extract the Application comp_id from the payload and increase the pointer so that it signals to the upper level only the remaining part of the message (payload)
            comp_id = (uint8_t)*payload;
            payload++;
            
            //check if an ack is requested for the received message
            if (reliable == TRUE) {
                //prepare the environment for sending the ack: comp_id, seq_num and destnation
                ack_compid = comp_id;
                ack_seqnum = seqnum;
                ack_dest = sender;
                //post the task that actually send the ack
                post sendAck();
            }
            
            //signal the reception to the proper Application component according to the comp_id
            signal Connectionless.receive[comp_id](sender, payload, len);
        }
    }
	
	event void Rubicon_Ack.receive_ack(r_addr_t sender, uint16_t seq_num) {
		
	}
	
	default event void Connectionless.receive[uint8_t comp](r_addr_t src, int8_t* buf, uint8_t nbytes) {
		
	}
}