/*
 *  Network.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 09/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Interface of the Network component of the Communication layer.
 */

interface Network {
	

    /**
	 * Sends the given message to the specified destination. It generates and returns a sequence number.
	 *
	 * @param dest (input) The RUBICON address of the destionation of the message. If the devid field is null, 
     * it addresses a PC or a Robot.
	 *
	 * @param payload (input) Pointer to a region where the data to be sent are stored.
	 *
	 * @param nbytes (input) Number of bytes of the payload to be sent.
	 *
	 * @param reliable (input) TRUE if an ack is requested for the current message.
	 *
	 * @return the sequence number generated for that message. The value 0xFFFF is an error status and it means that the outgoing buffer is full.
	 */
    command uint16_t send(r_addr_t dest, int8_t* payload, uint8_t nbytes, bool reliable);
    
    
    /**
	 * Notifies the reception of the given message from the specified sender. 
	 *
	 * @param sender (output) The RUBICON address of the source of the message.
	 *
	 * @param payload (output) Pointer to the received message.
	 *
	 * @param nbytes (output) Number of bytes received.
     *
     * @param reliable (output) TRUE if an ack is requested for the received message.
     *
     * @param seqnum (output) the sequence number of the received message.
	 */
    event void receive(r_addr_t sender, int8_t* payload, uint8_t nbytes, bool reliable, uint16_t seqnum);
    
    
	/**
	 * Initiate the protocol to join the island.
	 *
	 * @param desc (input) Pointer to the descriptor of the mote joining the island. 
	 * It is only stored in the Network layer at this stage, it will be sent to the SINK when sending the Join_Ack.
	 *
	 * @return the sequence number generated for the join request. The value 0xFFFF means that the mote is already joined.
	 */
    command uint16_t join_island(mote_desc_t* desc);
    
    
    /**
	 * Notifies the completion of the joining protocol and, if it was successfull, gives the assigned RUBICON address. 
	 *
	 * @param addr (output) The assigned RUBICON address.
	 *
	 * @param success (output) Whether the joining was successfull.
	 */
    event void joined(r_addr_t addr, error_t success);
	
}
