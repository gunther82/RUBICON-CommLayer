/*
 *  Connectionless.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 09/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Interface of the Communication layer for the Connectionless interface.
 */

interface Connectionless {
    
    /**
	 * Forwards the given message to the Transport layer. It returns the sequence number generated by the Network layer.
	 *
	 * @param dest (input) The RUBICON address of the destionation of the message. 
     * If the devid field is null, it addresses a PC or a Robot.
	 *
	 * @param payload (input) Pointer to a region where the data to be sent are stored.
	 *
	 * @param nbytes (input) Number of bytes of the payload.
	 *
	 * @param reliable (input) TRUE if an ack is requested for the current message.
     *
     * @param comp_id (input) The identifier of the Application-level component sending the message.
     * It is used at destination side to dispatch the message to the right Application-level component.
	 *
	 * @return the sequence number generated for that message.
	 */
    command uint16_t send(r_addr_t dest, int8_t* payload, uint8_t nbytes, bool reliable, uint8_t comp_id);
    
    
    /**
	 * Notifies the reception of the given message from the specified sender to the Application Message Dispatcher component. 
	 *
	 * @param sender (output) The RUBICON address of the source of the message.
	 *
	 * @param payload (output) Pointer to the received message.
	 *
	 * @param nbytes (output) Number of bytes of the received payload.
	 */
    event void receive(r_addr_t sender, int8_t* payload, uint8_t nbytes);
}
