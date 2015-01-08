/*
 *  ComponentManagement.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 19/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Interface of the Component Management of the Communication layer.
 */

#include "network.h"

interface ComponentManagement {

	/**
	 * Start both radio and serial interface.
	 *
	 */
	command void initCommunications();


	/**
	 * Signal the completion of both radio and serial component activation.
	 *
	 * @param success Whether the radio and serial activation was successful.
	 */
	event void initCommunicationsDone(error_t success);
	
	
	/**
	 * Request the sink of the current island to be joined. As consequence the mote will receive the mote_addr and the island_addr
	 *
	 * @param description Pointer to the desription of the capabilities (type, transducers and actuators) installed on the mote.
	 */
	command void joinIsland(mote_desc_t* description);
	
	
	/**
	 * Signal to the SINK that a new mote joined the RUBICON system and passes the mote descriptor.
	 *
	 * @param island_addr Address of the joined island.
	 *
	 * @param mote_addr Identifier of the joined mote.
	 *
	 * @param description Pointer to the desription of the capabilities (type, transducers and actuators) installed on the mote.
	 */
	event void moteJoined(uint16_t island_addr, uint16_t mote_addr, mote_desc_t* description);
    
    
    /**
	 * Signal to the application level that the mote has joined the island.
	 *
	 * @param island_addr Address of the joined island.
	 *
	 * @param mote_addr Identifier of the joined mote.
     *
     * @param success whether the join is successfull or not
	 */
	event void joined(uint16_t island_addr, uint16_t mote_addr, error_t success);

}
