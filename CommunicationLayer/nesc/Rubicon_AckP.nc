//
//  Rubicon_AckP.nc
//  RUBICON src
//
//  Created by Claudio Vairo on 22/03/13.
//  Copyright (c) 2013 ISTI-CNR. All rights reserved.
//


module Rubicon_AckP {
    //list of provided interfaces
	provides {
		interface Rubicon_Ack[uint8_t comp];
	}
	
	//list of used interfaces
	uses interface TransportMD;
}

implementation {
	
	//send the ack for a message. the payload is an app_msg_t whose payload contains only the seq_num
	command void Rubicon_Ack.send_ack[uint8_t comp](r_addr_t dest, int8_t* payload, uint8_t nbytes, bool reliable, uint8_t comp_id) {
        //set the new size of the payload
        uint8_t len = nbytes + 1;
        //pointer to the struct allocated by the Application layer for [TRANS_ID, APP_ID, payload], that for the moment contains only the payload
        int8_t* ptr = payload;
        //increase the pointer to the second field of the struct that will contain the APP_ID.
        ptr++;
        //fill this field with the APP_ID
        *ptr = comp_id;
		
        //call the TransportMD.send with the new payload and the new size
		call TransportMD.send(dest, payload, len, FALSE, RUBICON_ACK);
	}
	
	event void TransportMD.receive(r_addr_t sender, int8_t* payload, uint8_t nbytes, bool reliable, uint16_t seqnum) {
        uint8_t comp_id;
		uint16_t seq_num;
        
        atomic {
            //extract the Application comp_id from the payload and increase the pointer so that it points to the payload, that is the seq_num
            comp_id = (uint8_t)*payload;
            payload++;
			//copy the seq_num
			memcpy(&seq_num, payload, sizeof(seq_num));
            //signal the reception of the ack to the proper Application component according to the comp_id
			signal Rubicon_Ack.receive_ack[comp_id](sender, seq_num);
        }
	}
	
	
	default event void Rubicon_Ack.receive_ack[uint8_t comp](r_addr_t sender, uint16_t seq_num) {
	
	}
}
