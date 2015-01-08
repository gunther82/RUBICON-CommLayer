//
//  TransportMD_P.nc
//  RUBICON src
//
//  Created by Claudio Vairo on 08/02/13.
//  Copyright (c) 2013 ISTI-CNR. All rights reserved.
//


//#include "definitions.h"

module TransportMD_P {
    //list of provided interfaces
	provides {
		interface TransportMD[uint8_t comp];
	}
	
	//list of used interfaces
	uses interface Network;
	uses interface Leds;
}

implementation {
    
    //add the Transport ID (comp_id) to the payload and forwards the new payload to the Network
    command uint16_t TransportMD.send[uint8_t comp](r_addr_t dest, int8_t* payload, uint8_t nbytes, bool reliable, uint8_t comp_id) {
        uint16_t seq;
        //set the new size of the payload
        uint8_t len = nbytes + 1;
        //pointer to the struct containing TRANS_ID, APP_ID, payload.
        int8_t* ptr = payload;
        //fill the first field of the message with the Transport Component ID. The Application already allocated the space for this field.
        *ptr = comp_id;

        //call the Network.send with the new payload and the new size
        seq = call Network.send(dest, payload, len, reliable);
        
        return seq;
    }
    
    
    //extract the Transport ID (comp_id) field from the message received from the Network and forwards it to the proper Transport component
    event void Network.receive(r_addr_t sender, int8_t* payload, uint8_t nbytes, bool reliable, uint16_t seqnum) {
        //set the size of the payload except the comp_id
        uint8_t len = nbytes - 1;
        uint8_t comp_id;
		
        atomic {
            //extract the Transport comp_id from the payload and increase the pointer so that it signals to the upper level only the remaining part of the message (APP_ID, payload)
            comp_id = (uint8_t)*payload;
            payload++;
            //signal the reception to the proper Transport component according to the comp_id
            signal TransportMD.receive[comp_id](sender, payload, len, reliable, seqnum);
        }
    }
	
	event void Network.joined(r_addr_t addr, error_t success) {
		
	}
    
    default event void TransportMD.receive[uint8_t comp](r_addr_t sender, int8_t* payload, uint8_t nbytes, bool reliable, uint16_t seqnum) {
    
    }
}
