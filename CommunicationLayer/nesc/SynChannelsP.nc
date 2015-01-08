/*
 *  SynChannelsP.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 05/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Implementation of the Communication layer for the Synaptic Channel abstraction.
 */

includes synaptic_channels;

module SynChannelsP {
	//list of provided interfaces
	provides {
		interface Synaptic_Channels;
	}
	
	//list of used interfaces
	uses interface Timer<TMilli> as TimerSendChOut;
    uses interface Timer<TMilli> as TimerPhaseShift;
    uses interface Leds;

    uses interface TransportMD;
}

implementation {
	
	//	syn_data_t chin[MAX_SYN_CHANNEL_NUMBER][MAX_SYN_CH_BUFFER_SIZE];
	//	syn_data_t chout[MAX_SYN_CHANNEL_NUMBER][MAX_SYN_CH_BUFFER_SIZE];
	//	
	//	//buffer for associating ch_ids to positions in the buffer of channels
	//	uint8_t chin_id[MAX_SYN_CHANNEL_NUMBER];
	//	uint8_t chout_id[MAX_SYN_CHANNEL_NUMBER];
	//
	//	//buffer for storing the size of synaptic channel
	//	uint8_t chin_size[MAX_SYN_CHANNEL_NUMBER];
	//	uint8_t chout_size[MAX_SYN_CHANNEL_NUMBER];
	
	//buffer of input and output synaptic channels
	syn_channel_t chin[MAX_SYN_CHANNEL_NUMBER];
	syn_channel_t chout[MAX_SYN_CHANNEL_NUMBER];
	
	//pointers to the next free positions of the buffers
	uint8_t next_chin_free = 0;
	uint8_t next_chout_free = 0;
	
	uint16_t clock;
	syn_data_t pluto;
	syn_data_t* pippo;
	
	/********************************************************************************/
	/*																				*/
	/*				implementation of the Synaptic_Channels interface				*/
	/*																				*/
	/********************************************************************************/	
	
	
	//search for the position in the synaptic channel buffer corresponding to the given channel_id
	//if whichBuffer == SYN_OUT -> search into chOut
	//if whichBuffer == IN -> search into chIn
	int8_t findChannelPos(syn_ch_id_t channel_id, uint8_t whichBuffer) {
		uint8_t i;
		int8_t pos = -1;
		
		if (whichBuffer == SYN_OUT) {
			for (i=0; i<MAX_SYN_CHANNEL_NUMBER; i++) {
				if ((chout[i].channel_id == channel_id) && (chout[i].status != AVAILABLE)) {
					pos = i;
					break;
				}
			}
		}
		else {//whichBuffer == IN
			for (i=0; i<MAX_SYN_CHANNEL_NUMBER; i++) {
				if ((chin[i].channel_id == channel_id) && (chin[i].status != AVAILABLE)) {
					pos = i;
					break;
				}
			}
		}
		return pos;
	}
	
	
	//TO UPDATE: the clock should be updated at joining time
	command void Synaptic_Channels.init() {
		uint8_t i;
		clock = DEFAULT_CLOCK;
		
		
		for (i = 0; i<MAX_SYN_CHANNEL_NUMBER; i++) {
			chin[i].status = AVAILABLE;
			chout[i].status = AVAILABLE;
		}
	}
	
	command error_t Synaptic_Channels.create_syn_channel_out(uint16_t dest, uint8_t size, int8_t params[DIM_SYNCHANNEL_PARAM]) {
		//here we SHOULD check if there is still a free position in the buffer -> smart management of the buffer (maybe XOR and AND)
		//channel_id = SRC-DEST that is also SYN_OUT-IN.
		//the channel_id is the concatenation of TOS_NODE_ID (the source of the syn channel) and dest.
		syn_ch_id_t chid = (syn_ch_id_t)TOS_NODE_ID << ((sizeof(syn_ch_id_t)/2)*8);
		int8_t pos;
        uint8_t firstChIndex; //this is the position of the first chIndex to be filled by the LN
        chid = chid + dest;
        pos = findChannelPos(chid, SYN_OUT);
        
        //look if this is an already existing synaptic channel
        //if not, create it
        if (pos == -1) {
            chout[next_chout_free].channel_id = chid;
            chout[next_chout_free].status = CREATED;
            chout[next_chout_free].size = size;
            //we assume that all the params are specified.
            //TO UPDATE: possibly add management of params
            //memcpy(chout[next_chout_free].params, &params, DIM_SYNCHANNEL_PARAM);
            
            //TO UPDATE: find the next free position, in a SMART way possibly
            next_chout_free++;
            firstChIndex = 0;
        }
        //existing synaptic channel, just increase its size
        else {
            firstChIndex = chout[pos].size; // return the position of the new first chIndex, that the old size
            chout[pos].size += size;            
        }
		
		signal Synaptic_Channels.createSynChannelOutDone(chid, firstChIndex, SUCCESS);
		
		return FAIL;
		//GALLICCHIO
		//TO UPDATE: it is important that we can know the position of the created syn_channel in the buffer next_chout_free
	}
	
	//TO UPDATE: if the specified channel already exists increment its size of the new size
	command error_t Synaptic_Channels.create_syn_channel_in(uint16_t src, uint8_t size, int8_t params[DIM_SYNCHANNEL_PARAM]) {
		//here we SHOULD check if there is still a free position in the buffer -> smart management of the buffer (maybe XOR and AND)
		//channel_id = SRC-DEST.
		//the channel_id is the concatenation of src and TOS_NODE_ID.
		syn_ch_id_t chid = (syn_ch_id_t)src << ((sizeof(syn_ch_id_t)/2)*8);
		int8_t pos;
        uint8_t firstChIndex; //this is the position of the first chIndex to be filled by the LN
        chid = chid + TOS_NODE_ID;
        pos = findChannelPos(chid, IN);
        
        //look if this is an already existing synaptic channel
        //if not, create it
        if (pos == -1) {
            chin[next_chin_free].channel_id = chid;
            chin[next_chin_free].status = CREATED;
            chin[next_chin_free].size = size;
            //we assume that all the params are specified.
            //TO UPDATE: possibly add management of params
            //memcpy(chin[next_chin_free].params, &params, DIM_SYNCHANNEL_PARAM);
            
            //TO UPDATE: find the next free position, in a SMART way possibly
            next_chin_free++;
            firstChIndex = 0;
        }
        //existing synaptic channel, just increase its size
        else {
            firstChIndex = chin[pos].size; // return the position of the new first chIndex, that the old size
            chin[pos].size += size;
        }
		
		signal Synaptic_Channels.createSynChannelInDone(chid, firstChIndex, SUCCESS);
		
		return FAIL;
	}
	
	
	command error_t Synaptic_Channels.dispose_syn_channel(syn_ch_id_t channel_id) {
		
		return FAIL;
	}
	
	//start the specified syn channel, either in or out
	command error_t Synaptic_Channels.start_syn_channel(syn_ch_id_t channel_id) {
		int8_t posIn;
		int8_t posOut;
		
		//search both in output channels and input channels
		posOut = findChannelPos(channel_id, SYN_OUT);
		posIn = findChannelPos(channel_id, IN);
		
		//if the channel to be started is an out channel we have to start the timer to send the ChOut buffer
		if (posOut >= 0) {
			chout[posOut].status = ACTIVE;
            if (TOS_NODE_ID == SINK) {
                call TimerSendChOut.startPeriodic(clock);
            }
            else {
                call TimerPhaseShift.startOneShot((TOS_NODE_ID-1)*PHASE_SHIFT);
            }
			return SUCCESS;
			
		}
		//if the channel to be started is an input channel we set the status to ACTIVE ---> is this needed???
		else if (posIn >= 0){
			//TO UPDATE: check if we can remove the start for the input syn channels 
			chin[posIn].status = ACTIVE;
			return SUCCESS;
		}
		//we didn't find a match, this should never happen!!!
		return FAIL;
	}
	
	
	//stop the specified syn channel, either in or out
	command error_t Synaptic_Channels.stop_syn_channel(syn_ch_id_t channel_id) {
		int8_t posIn;
		int8_t posOut;
		
		//search both in output channels and input channels
		posOut = findChannelPos(channel_id, SYN_OUT);
		posIn = findChannelPos(channel_id, IN);
		
		//if the channel to be stopped is an out channel we have to stop the timer to send the ChOut buffer
		if (posOut >= 0) {
			chout[posOut].status = CREATED;
			call TimerSendChOut.stop();
			return SUCCESS;
			
		}
		//if the channel to be stopped is an input channel we set the status to CREATED ---> is this needed???
		else if (posIn >= 0){
			//TO UPDATE: check if we can remove the start for the input syn channels 
			chin[posIn].status = CREATED;
			return SUCCESS;
		}
		//we didn't find a match, this should never happen!!!
		return FAIL;
	}
	
	
	//start all CREATED synaptic channels
	command error_t Synaptic_Channels.start_all_syn_channels() {
		uint8_t i;
		bool found = FALSE;
        
		for (i=0; i<MAX_SYN_CHANNEL_NUMBER; i++) {
			if (chout[i].status == CREATED) {
				chout[i].status = ACTIVE;
				found = TRUE;
			}
			if (chin[i].status == CREATED) {
				chin[i].status = ACTIVE;
				found = TRUE;
			}
		}
		
		if (found) {
			if (!call TimerSendChOut.isRunning()) {
                if (TOS_NODE_ID == SINK) {
                    call TimerSendChOut.startPeriodic(clock);
                }
                else {
                    call TimerPhaseShift.startOneShot((TOS_NODE_ID-1)*PHASE_SHIFT);
                }
			}
			return SUCCESS;
		}
		
		return FAIL;
	}
	
    
    event void TimerPhaseShift.fired() {
        call TimerSendChOut.startPeriodic(clock);
    }
    
	
	//stops all ACTIVE synaptic channels
	command void Synaptic_Channels.stop_all_syn_channels() {
		uint8_t i;
		
		for (i=0; i<MAX_SYN_CHANNEL_NUMBER; i++) {
			if (chout[i].status == ACTIVE) {
				chout[i].status = CREATED;
			}
			if (chin[i].status == ACTIVE) {
				chin[i].status = CREATED;
			}
		}
		
		call TimerSendChOut.stop();
	}
	
	
	command error_t Synaptic_Channels.write_output(syn_ch_id_t channel_id, uint8_t size, syn_data_t* val) {
		int8_t posOut;

		posOut = findChannelPos(channel_id, SYN_OUT);
		//if pos < 0 we didn't find a match. this should never happen!!!
		if(posOut >= 0) {
			//here we could use the size attribute of the struct chout, since we are expected to receive always
			//a data of that size. TO UPDATE: check if this size here can be removed and manage possible errors -> turn on led
			//TO UPDATE: check if the channel is ??? CREATED???
			memcpy(chout[posOut].data, val, size*sizeof(syn_data_t));
			return SUCCESS;
		}
		return FAIL;
	}
	
	
	command error_t Synaptic_Channels.write_outputValue(syn_ch_id_t channel_id, uint8_t pos, syn_data_t output_data){
		int8_t posOut;
		
		posOut = findChannelPos(channel_id, SYN_OUT);
		//if pos < 0 we didn't find a match. this should never happen!!!
		if(posOut >= 0) {
			//here we could use the size attribute of the struct chout, since we are expected to receive always
			//a data of that size. TO UPDATE: check if this size here can be removed and manage possible errors -> turn on led
			//TO UPDATE: check if the channel is ??? CREATED???
			chout[posOut].data[pos] = output_data;
			return SUCCESS;
		}
		return FAIL;		
	}
	
	command syn_data_t* Synaptic_Channels.read_input(syn_ch_id_t channel_id) {
		int8_t posIn;
		//pluto = 10;
		//pippo = &pluto;
		
		
		posIn = findChannelPos(channel_id, IN);
		//if pos < 0 we didn't find a match. this should never happen!!!
		if(posIn >= 0) {
			//we don't copy the message another time, but we return the pointer to data field of the requested input syn channel
			//TO UPDATE: check if the channel is ??? CREATED???
			return chin[posIn].data;
		}
		//return pippo;
		return NULL;
	}

	command syn_data_t Synaptic_Channels.read_inputValue(syn_ch_id_t channel_id, uint8_t pos) {
		syn_data_t val;
		int8_t posIn;
		
		val = 0;
		//GALLICCHIO 
		//12-07-2012
		//Here consider also the case of the local synaptic connections
		//Possibly TO UPDATE
		//if (channel_id == 0){
		//	//in this case the input synaptic connection is a local input synaptic connection
		//	//TO CONTINUE
		//}
		//else{	
			
			posIn = findChannelPos(channel_id, IN);
			//if pos < 0 we didn't find a match. this should never happen!!!
			if(posIn >= 0) {
				//we don't copy the message another time, but we return the pointer to data field of the requested input syn channel
				//TO UPDATE: check if the channel is ??? CREATED???
				val = chin[posIn].data[pos];
                //chin[posIn].data[pos] = -1000;
			}
		//}
		return val;
	}
	
	//TO UPDATE: implement and de-comment the command below
//	//GALLICCHIO - updated: added a new version in which the position within the 
//	//data buffer is explicitely specified
//	// TO UPDATE - please non-Dr Vairo, check this
//	command syn_data_t Synaptic_Channels.read_input_ind(syn_ch_id_t channel_id, uint8_t chIndex) {
//		//TO UPDATE: the data type for chIndex
//		int8_t posIn;
//		//pluto = 10;
//		//pippo = &pluto;
//		
//		
//		posIn = findChannelPos(channel_id, IN);
//		//if pos < 0 we didn't find a match. this should never happen!!!
//		if(posIn >= 0) {
//			//we don't copy the message another time, but we return the pointer to data field of the requested input syn channel
//			//TO UPDATE: check if the channel is ??? CREATED???
//			//here it returns the content of index chIndex within the data buffer, so:
//			return chin[posIn].data[chIndex];
//		}
//		//return pippo;
//		return NULL;
//	}
	
	//GALLICCHIO
	//TO UPDATE:
	//MAYBE THIS IS NOT IMPORTANT SO CAN REMOVE ANYTHING OF THIS
	//possibly functions are needed to access the specific positions within the .data field of 
	//a chout/chin buffer
	//
	//I would say: - for the output syn channel we need to write in the exact position
	//             - for the input syn channel we need to read from exact position
	//Mr. Vairo have a look at this, please:
	/*
	syn_data_t get_out_syn_data(syn_channel_id, position)
	- returns the content of chout buffer for 
	*/
	
	
		
	command void Synaptic_Channels.setTimeStep(uint16_t synclock) {
		clock = synclock;
	}
	
    
    
    //when the timer fires we need to transmit the output of all the ACTIVE synaptic channels
	event void TimerSendChOut.fired() {
		int8_t posIn;
		uint8_t status;
		uint8_t i;
		uint16_t dest_devid;
		r_addr_t dest;
		syn_ch_id_t mom = 1;
		
		app_msg_t out_msg;
        
		//TO CHECK!!!!!
		//syn_ch_id_t chid=1;
		
		for (i=0; i<MAX_SYN_CHANNEL_NUMBER; i++) {
			if (chout[i].status == ACTIVE) {
				//the mote destination address is codified into the channel_id, if we change how we codify the channel_id
				//we MUST change also this function to get the destination address
				//we need to get the last sizeof(syn_ch_id_t)/2 bits that encode the destination:
				//shift left 1 of 8*(sizeof(syn_ch_id_t)/2) and subtract 1 to get 111111111
				dest_devid = (uint16_t)(chout[i].channel_id & ((mom << (sizeof(syn_ch_id_t)*4))-1));
				
				//check if it is a local syn channel
				if (dest_devid == TOS_NODE_ID) {
                    
					posIn = findChannelPos(chout[i].channel_id, IN);
					if(posIn >= 0) {
						//UPDATED 12-07-2012
						//PLEASE VAIRO CHECK THIS
						//In this case, if the synaptic channel is local (i.e. the ids of the src and destination nodes
						//are the same, then read the values from the right sensor's transducers
						//in chin[posIn].params[0] it should be specified the type of the transducer to read
						
						//LET HANDLE THIS HERE, PLEASE
						//TO UPDATE - ...ASAP

						//here we could use the size attribute of the struct chout, since we are expected to receive always
						//a data of that size. TO UPDATE: check if this size here can be removed and manage possible errors -> turn on led
						//TO UPDATE2: here we should check if the input channel where we are putting the data is ACTIVE or not.
						//TO UPDATE: check if the channel is ??? CREATED???
						memcpy(chin[posIn].data, chout[i].data, chout[i].size*sizeof(syn_data_t));
						//TO UPDATE: check the kind of data received. default->NEW_DATA
						status = NEW_DATA;
						signal Synaptic_Channels.synData_received(chin[posIn].channel_id, status);
					}
					else {//this should never happen
						status = NO_SYN_CHANNEL_ID;
						signal Synaptic_Channels.synData_received(chin[posIn].channel_id, status);
					}
				}
				else { //we have to send the buffer to the remote end of the syn channel
					//TO UPDATE: put some delay to avoid collisions
					
					//prepare the dest
					dest.pid = LOCAL_ISLAND;
					dest.devid = dest_devid;
					
					//prepare the app_msg_t
					out_msg.app_id = ESN_DATA;
					//copy the outpunt in the app_msg_t payload field
					memcpy(&(out_msg.payload), (int8_t*)(chout[i].data), chout[i].size*sizeof(syn_data_t));
					
					
					//call the TransportMD.send with the prepared app_msg_t and the size containng also the APP_ID
					call TransportMD.send(dest, (int8_t*)&out_msg, (chout[i].size*sizeof(syn_data_t) + 1), NOT_ACK, SYN_CHANNEL);
				}
			}
		}
	}
    
	//reception of a AM_SYN_DATA_MSG msg    
    event void TransportMD.receive(r_addr_t sender, int8_t* payload, uint8_t nbytes, bool reliable, uint16_t seqnum) {
        uint8_t len;
        int8_t posIn;
		uint8_t status;
        syn_ch_id_t chid = (syn_ch_id_t)(sender.devid) << (sizeof(syn_ch_id_t)*4);
		chid = chid + TOS_NODE_ID;
        
		if(TOS_NODE_ID == 1) {
			call Leds.led1Toggle();
		}
		
        //increment the payload pointer in order to skip the byte reserved to the Application comp_id that in this case is not used
        payload++;
        //set len to the actual size of the payload
        len = nbytes - 1;
        
        //handle the received syn data
        posIn = findChannelPos(chid, IN);
        if(posIn >= 0) {
            //here we could use the size attribute of the struct chout, since we are expected to receive always
            //a data of that size. TO UPDATE: check if this size here can be removed and manage possible errors -> turn on led
            //TO UPDATE2: here we should check if the input channel where we are putting the data is ACTIVE or not.
            //TO UPDATE: check if the channel is ??? CREATED???
            memcpy(chin[posIn].data, (syn_data_t*)payload, len);
         
			if (TOS_NODE_ID == 1) {
				if (chin[0].data[0] > 70) {
					call Leds.led2Toggle();
				}
			}
			
            //TO UPDATE: check the kind of data received. default->NEW_DATA
            status = NEW_DATA;
        }
        else {//this should never happen
            status = NO_SYN_CHANNEL_ID;
        }
        signal Synaptic_Channels.synData_received(chid, status);
    }
}
