/*
 *  ComponentManagementP.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 09/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Implementation of the Component Management of the Communication layer.
 */

module ComponentManagementP {
	//list of provided interfaces
	provides {
		interface ComponentManagement;
	}
	
	//list of used interfaces
	uses interface CommControl;
	uses interface Network;
	uses interface Leds;
}

implementation {
	//app_msg_t join_msg;
	
	command void ComponentManagement.initCommunications() {
		call CommControl.init();
	}
	

    //request to join an island an passes to the network the mote descriptor received by the application
	command void ComponentManagement.joinIsland(mote_desc_t* description) {
        uint16_t seq;

		seq = call Network.join_island(description);
		if (seq == 0xFFFF) {
			//TO UPDATE:
			//notifies the application that the mote is already joined
            signal ComponentManagement.joined(0, 0, FAIL);
		}
	}
	
	
	//only the SINK should receive this event
    event void Network.receive(r_addr_t sender, int8_t* payload, uint8_t nbytes, bool reliable, uint16_t seqnum) {
        //SINK: sender is the r_addr of the joined mote and payload is the mote_desc
        if (TOS_NODE_ID == SINK) {
			signal ComponentManagement.moteJoined(sender.pid, sender.devid, (mote_desc_t*)payload);
        }
    }
	
	
	//MOTE: notification of completed join protocol
	event void Network.joined(r_addr_t addr, error_t success) {
        if(TOS_NODE_ID != SINK) {
			if (success == SUCCESS) {
				signal ComponentManagement.joined(addr.pid, addr.devid, SUCCESS);
			}
        }
	}
	
	
	event void CommControl.initDone(error_t success) {
		signal ComponentManagement.initCommunicationsDone(success);
	}
	
	event void CommControl.radioStarted(error_t success) {
		
	}
	
	event void CommControl.radioStopped(error_t success) {
		
	}
	
	event void CommControl.serialStarted(error_t success) {
		
	}
	
	event void CommControl.serialStopped(error_t success) {
		
	}
}