//
//  Rubicon_Ack.nc
//  RUBICON src
//
//  Created by Claudio Vairo on 22/03/13.
//  Copyright (c) 2013 ISTI-CNR. All rights reserved.
//


/**
 * Interface of the Communication layer for the Transport Message Dispatcher component.
 */

interface Rubicon_Ack {
    
    /**
	 * Sends the ack for a received message. The payload has to be an app_msg_t contaning, in its payload,
	 * only the seq_num of the message to be acknowledged, therefore the expected nbytes is 2.
	 * This component intefaces with the TransportMD, as all the other Transport component. So it adds
     * the received comp_id in the app_msg_t and when it invokes the Transport.send it passes RUBICON_ACK as TRANS_ID
	 *
	 * @param dest (input) The RUBICON address of the destionation of the ack. 
     * If the devid field is null, it addresses a PC or a Robot.
	 *
	 * @param payload (input) The app_msg_t containing only the header and the seq_num.
	 *
	 * @param nbytes (input) Number of bytes of the payload (that in this case is only the size of seq_num).
	 *
	 * @param reliable (input) Always FALSE, it is left in order to be compliant with the TransportMD interface.
     * 
     * @param comp_id (input) The APP_ID for which the message to be acknowledged is destinated.
	 *
	 */
    command void send_ack(r_addr_t dest, int8_t* payload, uint8_t nbytes, bool reliable, uint8_t comp_id);
	
	
    /**
	 * Notifies the reception of the acknowledgment of the message identified by the given sequence number. 
	 *
	 * @param sender (output) The RUBICON address of the source of the ack.
	 *
	 * @param seq_num (output) Sequence number of the acknowledged message.
	 */
    event void receive_ack(r_addr_t sender, uint16_t seq_num);
	
}
