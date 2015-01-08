/*
 *  NetworkP.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 09/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Implementation of the Network component of the Communication layer.
 */

#include "network.h"
#include "definitions.h"

module NetworkP {
	//list of provided interfaces
	provides {
		interface Network[uint8_t comp];
	}
	
	//list of used interfaces
	uses interface Boot;
	
	uses interface CommControl;
	uses interface Leds;
	uses interface LocalTime<TMilli>;
	uses interface Random;
	uses interface Timer<TMilli> as TimerJoinTimeout;
	
    //Serial
	uses interface Packet as SerialPacket;
    uses interface AMSend as SerialNetworkSend;
    uses interface Receive as SerialNetworkReceive;
	uses interface Receive as SerialConfigReceive;
	
	//Radio
	uses interface Packet as RadioPacket;
	uses interface AMPacket;
	uses interface PacketAcknowledgements as Ack;
	
	uses interface AMSend as JoinSend;
	uses interface AMSend as JoinReplySend;
    uses interface AMSend as NetworkSend;
    uses interface AMSend as AckJoinSend;
	
	uses interface Receive as JoinReceive;
	uses interface Receive as JoinReplyReceive;
    uses interface Receive as NetworkReceive;
    uses interface Receive as AckJoinReceive;
    
#if defined(PLATFORM_MICAZ) || defined(PLATFORM_TELOSB) || defined(PLATFORM_XM1000)
	uses interface CC2420Packet;
#elif defined(PLATFORM_IRIS)
	uses interface PacketField<uint8_t> as PacketRSSI;
#endif
}

implementation {
    
    //new code
    //outgoing radio buffer variables
    message_t outMsg[RADIO_BUFF_SIZE];
    //int8_t *ptrOutMsg[RADIO_BUFF_SIZE];
    uint8_t sizeOutMsg[RADIO_BUFF_SIZE];
    uint16_t destinations[RADIO_BUFF_SIZE];
    uint8_t nextOut;
    uint8_t curOut;
    bool outFull;
    bool sending;
    
	//variable used in the join protocol. 
	//in the MOTE: it is sent twice: the first time it contains a join_request_msg_t containing the mote_desc_t and the seq_num, 
	//the second time is an ack_join_msg_t
	//in the SINK: it contains the ack_join_msg_t
	message_t join_msg;
	
	
	//outgoing serial buffer variables
    message_t outSerialMsg[SERIAL_BUFF_SIZE];
    uint8_t nextSerialOut;
    uint8_t curSerialOut;
    bool outSerialFull;
    bool serialSending;
	
    
    //ingoing serial messages data structures
	message_t serialBuf[SERIAL_BUFF_SIZE];
	message_t *serialBufPos[SERIAL_BUFF_SIZE];
	uint8_t serialSize[SERIAL_BUFF_SIZE];
	uint8_t nextserial;
	uint8_t currentserial;
    
    //ingoing radio messages data structures
	message_t radioBuf[RADIO_BUFF_SIZE];
	message_t *radioBufPos[RADIO_BUFF_SIZE];
	uint8_t radioSize[RADIO_BUFF_SIZE];
	uint8_t nextfree;
	uint8_t current;
    
    
    task void sendTask();
	task void sendSerialTask();
	
    
	bool radioBusy;
	bool serialBusy;
	
	//system info variables
	uint16_t moteID;
	uint16_t islandID;
	uint32_t curTime;
	
	
    //new join variables
	//MOTE: descriptor of the mote joining the island
    mote_desc_t mote_desc;
	//bool whether joined or not
	bool joined;
	//ID of the joining mote
	uint16_t srcJoinID;
    //list of joined motes. 0 means empty position, otherwise it contains the ID of the joined mote
    uint16_t mote_list[MAX_NUM_MOTES];
    uint8_t motelistpos;
	//join sequence numbers of SINK and MOTE
	uint16_t sinkseqnum;
	uint16_t moteseqnum;
	//ack message to be analyzed
	ack_join_msg_t recackmsg;
	//boolean for keeping track of the joining protocol for retransmission in case of timeout
	bool joinRequest_received;
	//counter for join attempts
	uint8_t join_attempts;
	
	
    void signalError() {
        call Leds.led0On();
        call Leds.led1On();
        call Leds.led2On();
        call Leds.led0Off();
        call Leds.led1Off();
        call Leds.led2Off();
        call Leds.led0On();
        call Leds.led1On();
        call Leds.led2On();
        call Leds.led0Off();
        call Leds.led1Off();
        call Leds.led2Off();
        call Leds.led0On();
        call Leds.led1On();
        call Leds.led2On();
    }
    
    
	event void Boot.booted() {
		uint8_t i;
		
		radioBusy = FALSE;
		serialBusy = FALSE;
		
		//radio buffer variables
		for(i = 0; i < RADIO_BUFF_SIZE; i++) {
			radioBufPos[i] = &radioBuf[i];
            //ptrOutMsg[i] = (int8_t*) (call RadioPacket.getPayload(&outMsg[i], sizeof(net_msg_t)));
		}
        
        //serial buffer variables
        for (i = 0; i < SERIAL_BUFF_SIZE; i++) {
            serialBufPos[i] = &serialBuf[i];
        }
        
        //list of motes
        for (i = 0; i < MAX_NUM_MOTES; i++) {
            mote_list[i] = 0;
        }
        motelistpos = 0;
		joined = FALSE;
		joinRequest_received = FALSE;
		join_attempts = 0;
        
        //incoming radio messages pointers
		nextfree = 0;
		current = 0;
        
        //incoming radio messages pointers        
        nextserial = 0;
        currentserial = 0;
        
        //outgoing messages variables
        nextOut = 0;
        curOut = 0;
        sending = FALSE;
        outFull = FALSE;
        
		nextSerialOut = 0;
		curSerialOut = 0;
		serialSending = FALSE;
		outSerialFull = FALSE;
		
		//TO UPDATE: decide how to set those values!
		moteID = TOS_NODE_ID;
		if (TOS_NODE_ID == SINK) {
            islandID = ISLAND_ADDR;//call AMPacket.localGroup(); //default = 11;
			joined = TRUE;
        }
		curTime = call LocalTime.get();
	}
	
	
#if defined(PLATFORM_MICAZ) || defined(PLATFORM_TELOSB) ||defined(PLATFORM_XM1000)
	uint16_t getRssi(message_t *msg){
		return (uint16_t) call CC2420Packet.getRssi(msg)+50;
	}
#elif defined(PLATFORM_IRIS)
	uint16_t getRssi(message_t *msg){
		if(call PacketRSSI.isSet(msg))
			return (uint16_t) call PacketRSSI.get(msg);
		else
			return 0xFFFF;
	}
#else
	uint16_t getRssi(message_t *msg){
		return 11;
	}
#endif
	
	uint8_t* addRssi() {
		uint8_t rssi = getRssi(radioBufPos[current]);
		//create a new pointer that will contain the received data plus two bytes for the RSSI
		uint8_t *newMsg = (uint8_t*) (call RadioPacket.getPayload(radioBufPos[current], (radioSize[current]+2)));
		newMsg = newMsg+radioSize[current];
		
		*newMsg = (uint8_t)(rssi >> 8);
		newMsg++;
		*newMsg = (uint8_t)(rssi & 0xFF);
		
		//move the pointer to the part of payload just after the island address and mote address. this is the pointer to be returned
		newMsg = newMsg-(radioSize[current]+1)+DIM_ADDRESS;
		return newMsg;
	}
	
    
    
    /******************************* OUTGOING PART **********************************/
	
    //return the next sequence number
    uint16_t getSeqNum() {
		return call Random.rand16();
    }
    
    //TO UPDATE: can be removed and addr is a global variable??
    r_addr_t getLocalAddress() {
        r_addr_t addr;
        
        addr.pid = islandID;
        addr.devid = moteID;
        
        return addr;
    }
	
    
    //TO UPDATE: check the BROADCAST
    //Send the given message to the specified destination
    //Actually this command simply copy the message in the out buffer, and post the task for sending the message
    command uint16_t Network.send[uint8_t comp](r_addr_t dest, int8_t* payload, uint8_t nbytes, bool reliable) {
        uint16_t seq= getSeqNum();
        uint8_t len = nbytes + NETWORK_HEADER;
        net_msg_t* ptr;
        
		
        //check if the message is from the Synaptic Channel component
        if(dest.pid == LOCAL_ISLAND) {
            //replace the macro LOCAL_ISLAND with the actual local island address
            dest.pid = islandID;
        }
        
        
        //special case for ack generated by the SINK for the PC connected (for example for remote syn connections)
        if((dest.pid == islandID) && (dest.devid == NULL_MOTE) && (TOS_NODE_ID == SINK)) {
            //forward the message over the serial rather than over the air, check if the serial buffer is full
            atomic {
                //if not full, fill the next free position and increase the nextSerialOut
                if (!outSerialFull) {
                    ptr = (net_msg_t*)(call SerialPacket.getPayload(&outSerialMsg[nextSerialOut], len));
                    
                    //fill the network header
                    ptr->dest = dest;
                    ptr->src = getLocalAddress();
                    ptr->reliable = reliable;
                    ptr->seq_num = seq;
                    ptr->nbytes = nbytes;
                    //copy the payload in the outgoing buffer
                    memcpy(ptr->payload, payload, nbytes);
                    
                    //increase nextSerialOut index
                    nextSerialOut = (nextSerialOut + 1)%SERIAL_BUFF_SIZE;
                    
                    //check if the buffer is full
                    if(nextSerialOut == curSerialOut) {
                        outSerialFull = TRUE;
                    }
                }
                
                //if we are not already sending messages from outSerialMsg, start sending
                if(!serialSending) {
                    serialSending = TRUE;
                    post sendSerialTask();
                }
            }
        }
        
        //send over the air
        else {
            //check if the out buffer is full
            atomic {
                //if not full, fill the next free position and increase the nextOut
                if (!outFull) {
                    ptr = (net_msg_t*)(call RadioPacket.getPayload(&outMsg[nextOut], len));
                    
                    //fill the network header
                    ptr->dest = dest;
                    ptr->src = getLocalAddress();
                    ptr->reliable = reliable;
                    ptr->seq_num = seq;
                    ptr->nbytes = nbytes;
                    //copy the payload in the outgoing buffer
                    memcpy(ptr->payload, payload, nbytes);
                    
                    //set the size for this message: payload+NETWORK_HEADER
                    sizeOutMsg[nextOut] = len;
                    
                    //set the destination for this message
                    //the message is for a mote in a local island, or it is in broadcast
                    if((dest.pid == islandID) && (dest.devid != NULL_MOTE)) {
                        destinations[nextOut] = dest.devid;
                    }
                    //message has to reach the sink
                    else {
                        destinations[nextOut] = SINK;
                    }
                    
                    //increase nextOut index
                    nextOut = (nextOut + 1)%RADIO_BUFF_SIZE;
                    
                    //check if the buffer is full
                    if(nextOut == curOut) {
                        outFull = TRUE;
                    }
                    
                }
                //if the buffer is full return error status to upper layer
                else seq = 0xFFFF;
                
                //if we are not already sending messages from outBuf, start sending
                if(!sending) {
                    sending = TRUE;
                    post sendTask();
                }
            }
        }
        
        return seq;
    }
    
    
    //the task that ACTUALLY send the next message in the out buffer
    task void sendTask() {
        atomic {
            //check if we finished sending outgoing messages, 
            //it means that curOut has reached nextOut and that the buffer is not full
            if ((nextOut == curOut) && !outFull) {
                sending = FALSE;
                return;
            }
            
			//check if the radio is busy, in this case stop sending. this task will be posted in the sendDone of the current transmitting interface
			if (!radioBusy) {
				//TO UPDATE: ack management
				//else we have to send the next msg in the buffer
				if(call NetworkSend.send(destinations[curOut], &outMsg[curOut], sizeOutMsg[curOut]) == SUCCESS) {
                    radioBusy = TRUE;
                }
			}
			else {
				sending = FALSE;
			}
        }
    }
    
    
    //updates the out buffer and post again the sending task
    event void NetworkSend.sendDone(message_t *msg, error_t error){
        radioBusy = FALSE;
        
		if(error == SUCCESS) {
#ifdef SEND_LED0
			call Leds.led0Toggle();
#endif
			atomic {
				//increase index of to_send messages
				curOut = (curOut +1) % RADIO_BUFF_SIZE;
				//check if the buffer was full and in case set outFull to FALSE
				if (outFull) {
					outFull = FALSE;
				}
			}
		}
        else {
            call Leds.led2On();
        }
        //post again the sendTask for either sending the next message (if any) or termination
		post sendTask();
	}
    
    
    /******************************* INCOMING PART **********************************/
    
	    
    //updates the radio buffer for the incoming messages
	message_t* updateRadioBuffer(message_t *msg, uint8_t len) {
		atomic {
            message_t *ret;
            //set the pointer where the next message will be received
			ret = radioBufPos[nextfree];
            //set the pointer to the current msg, so that the next time the 
			radioBufPos[nextfree] = msg;
			radioSize[nextfree] = len;
			nextfree = (nextfree + 1) % RADIO_BUFF_SIZE;
            return ret;
		}
	}
    
    
    //updates the serial for the incoming messages
	message_t* updateSerialBuffer(message_t *msg, uint8_t len) {
		atomic {
            message_t *ret;
            //set the pointer where the next message will be received
			ret = serialBufPos[nextserial];
            //set the pointer to the current msg, so that the next time the 
			serialBufPos[nextserial] = msg;
			serialSize[nextserial] = len;
			nextserial = (nextserial + 1) % SERIAL_BUFF_SIZE;
            return ret;
		}
	}
    
    
    //return the pointer to the next network received radio msg and increments the pointer to the current msg
	uint8_t* getNextRadioMsg() {
		uint8_t *rec = (uint8_t*) (call RadioPacket.getPayload(radioBufPos[current], radioSize[current]));
		current = (current + 1) % RADIO_BUFF_SIZE;
		
		return rec;
	}
    
    
    //return the pointer to the next received serial msg and increments the pointer to the current msg
	uint8_t* getNextSerialMsg() {
		uint8_t *rec = (uint8_t*) (call SerialPacket.getPayload(serialBufPos[currentserial], serialSize[currentserial]));
		currentserial = (currentserial + 1) % SERIAL_BUFF_SIZE;
		
		return rec;
	}
    
    
    //handle the reception of a radio Network message
    task void handleNetworkMsg() {
        //get the msg from the incoming message buffer
        net_msg_t *rec_msg = (net_msg_t*)getNextRadioMsg();
        net_msg_t *serial_msg;
        uint8_t len = rec_msg->nbytes + NETWORK_HEADER;
        bool forward = FALSE;
        
        //MOTE receiving a radio message should simply check if the message belogns to the same island, otherwise it ignores it
        if (TOS_NODE_ID != SINK) {
            //if it is of another island ignores it
            if (rec_msg->dest.pid != islandID) {
                return;
            }
            //otherwise the message is for the current mote, because there is no MULTI-HOP and if it received the message the TOS_ADDR must have been the TOS_NODE_ID or BROADCAST, so handle it
            else {
                //TO UPDATE: add ack management
                //signal the payload (except the network_header) of the received message to the TMD
                signal Network.receive[TRANSPORT_MD](rec_msg->src, (int8_t*)rec_msg->payload, rec_msg->nbytes, rec_msg->reliable, rec_msg->seq_num);
            }
            
        }
        
        //SINK receiving a radio message first check the island address
        else {
            //if the message is for the local island, then it check the devid
            if (rec_msg->dest.pid == islandID) {
                //if the devid is != NULL_MOTE it means that the message is for the SINK, i.e. the TOS_ADDR is SINK or BROADCAST (it's not possible that it is for another motes, because in this case the message would have been seen directly to him, if in the same island, or it would have been received from the serial, if it comes from a different island), so handle it.
                if (rec_msg->dest.devid != NULL_MOTE) {
                    //TO UPDATE: add ack management
                    //signal the payload (except the network_header) of the received message to the TMD
                    signal Network.receive[TRANSPORT_MD](rec_msg->src, (int8_t*)rec_msg->payload, rec_msg->nbytes, rec_msg->reliable, rec_msg->seq_num);
                }
                //if the devid is NULL it means that the message is for the LOCAL Base Station, so forwards the msg over the serial
                else {
                    //forward over serial
                    forward = TRUE;
                }
            }
            
            //if the message is for another island, then check the src island address for avoiding possible intereference between island
            else {
                //if the src island address is not from the local island ignores it 
                if (rec_msg->src.pid != islandID) {
                    return;
                }
                //if the src is from the local island, forwards the message over the serial
                forward = TRUE;
            }
        }
        
		//forward the message over the serial
        if (forward) {
			//prepare the msg to be forwarded to the sink over the serial
			//check if the out serial buffer is full
			atomic {
				//if not full, fill the next free position and increase the nextSerialOut
				if (!outSerialFull) {                
                    serial_msg = (net_msg_t*)(call SerialPacket.getPayload(&outSerialMsg[nextSerialOut], sizeof(net_msg_t)));
					
					//copy the msg into the outSerialMsg buffer
					memcpy(serial_msg, rec_msg, len);
					
                    //set the size of the payload: it MUST be the size of app_msg_t otherwise MIG won't work
                    serial_msg->nbytes = sizeof(app_msg_t);
					
					//increase nextSerialOut index
					nextSerialOut = (nextSerialOut + 1)%SERIAL_BUFF_SIZE;
					
					//check if the buffer is full
					if(nextSerialOut == curSerialOut) {
						outSerialFull = TRUE;
					}
				}
				
				//if we are not already sending messages from outSerialMsg, start sending
				if(!serialSending) {
					serialSending = TRUE;
					post sendSerialTask();
				}
			}       
        }
    }
	
	
	//the task that ACTUALLY send the next message in the out serial buffer
	task void sendSerialTask() {
		atomic {
            //check if we finished sending outgoing messages, 
            //it means that curSerialOut has reached nextSerialOut and that the buffer is not full
            if ((nextSerialOut == curSerialOut) && !outSerialFull) {
                serialSending = FALSE;
                return;
            }
            
            //TO UPDATE: ack management
            //else we have to send the next msg in the buffer
            //the size pf the serial message is ALWAYS the size of net_msg_t otherwise MIG won't work
            if(call SerialNetworkSend.send(AM_BROADCAST_ADDR, &outSerialMsg[curSerialOut], sizeof(net_msg_t)) == SUCCESS) {
                serialBusy = TRUE;
            }
        }
	}

	
	//updates the out serial buffer and post again the sending serial task	
    event void SerialNetworkSend.sendDone(message_t *msg, error_t error){
        serialBusy = FALSE;
        
		if(error == SUCCESS) {
#ifdef  NETWORK_LEDS
			//call Leds.led2Toggle();
#endif
            
			atomic {
				//increase index of to_send serial messages
				curSerialOut = (curSerialOut +1) % SERIAL_BUFF_SIZE;
				//check if the serial buffer was full and in case set outSerialFull to FALSE
				if (outSerialFull) {
					outSerialFull = FALSE;
				}
			}
		}		
		
		//post again the sendSerialTask for either sending the next message (if any) or termination
		post sendSerialTask();
	}
    
    
    //SINK: handle the reception of a serial Network message
    task void handleSerialNetworkMsg() {
        //get the msg from the incoming message buffer
        net_msg_t *rec_msg = (net_msg_t*)getNextSerialMsg();
        //pitgoing msg pointer
        net_msg_t *ptr; 
        uint8_t len = rec_msg->nbytes + NETWORK_HEADER;
		
        //check if the message has reached the destination mote or if it is in broadcast
        if((rec_msg->dest.devid == TOS_NODE_ID) || (rec_msg->dest.devid == AM_BROADCAST_ADDR)) {
            //TO UPDATE: add ack management
            
            //signal the payload (except the network_header) of the received message to the TMD
            signal Network.receive[TRANSPORT_MD](rec_msg->src, (int8_t*)rec_msg->payload, rec_msg->nbytes, rec_msg->reliable, rec_msg->seq_num);
        }
        
        if((rec_msg->dest.devid != TOS_NODE_ID) || (rec_msg->dest.devid == AM_BROADCAST_ADDR)) {
            
            //forward the msg to the destination mote, by putting it in the out radio buffer and posting the sending task
            atomic {
                //check if the out buffer is full
                //if not full, put the msg in the next free position of the out radio buffer and increase the nextOut
                if (!outFull) {                
                    ptr = (net_msg_t*)(call RadioPacket.getPayload(&outMsg[nextOut], len));
                    memcpy(ptr, rec_msg, len);
                    
                    //set the size for this message
                    sizeOutMsg[nextOut] = len;
                    
                    //set the destination for this message
                    destinations[nextOut] = rec_msg->dest.devid;
                    
                    //increase nextOut index
                    nextOut = (nextOut + 1)%RADIO_BUFF_SIZE;
                    
                    //check if the buffer is full
                    if(nextOut == curOut) {
                        outFull = TRUE;
                    }
                }
                
                //if we are not already sending messages from outBuf, start sending
                if(!sending) {
                    sending = TRUE;
                    post sendTask();
                }
            }
        }
    } 
    
    //TO UPDATE: check the BROADCAST
    //reception of a radio Network message
    event message_t* NetworkReceive.receive(message_t* msg, void* payload, uint8_t len) {
        //put the received message in the incoming message buffer
		message_t *ret = updateRadioBuffer(msg, len);
        
        post handleNetworkMsg();
		
		return ret;
	}
    
    
    //reception of a message from the Gateway
    event message_t* SerialNetworkReceive.receive(message_t* msg, void* payload, uint8_t len) {
        //put the received message in the incoming message buffer
		message_t *ret = updateSerialBuffer(msg, len);
		
        post handleSerialNetworkMsg();
		
		return ret;
	}
    
	//reception of a configuration msg from the base station
	event message_t* SerialConfigReceive.receive(message_t* msg, void* payload, uint8_t len) {
		config_msg_t* confmsg = (config_msg_t*)payload;
		
		//check which type of configuration msg is
		switch (confmsg->type) {
			//msg to set the islandID
			case SET_ISLAND_ID:
				islandID = confmsg->value;
				break;
		}
		
		return msg;
	}
	
    /******************************************************************************/
    /*************************** START JOIN PROTOCOL ******************************/
    /******************************************************************************/
    
    
    //MOTE: command to join the island
	command uint16_t Network.join_island[uint8_t comp](mote_desc_t* desc) {
		uint16_t seq = getSeqNum();
		join_request_msg_t* msg = (join_request_msg_t*)(call RadioPacket.getPayload(&join_msg, sizeof(join_request_msg_t)));
		
		//save the sequence number
		if(!joined) {
			moteseqnum = seq;
		}
		else {
			return 0xFFFF;
		}
		
		//store the descriptor of the mote, that will be sent again as ack to SINK when sending the join_ack
		memcpy(&mote_desc, desc, sizeof(mote_desc_t));
		//prepare the join_msg
		msg->seq_num = seq;
		//copy the mote descriptor in the join_msg
		memcpy(&(msg->mote_desc), desc, sizeof(mote_desc_t));
		
		//set the timer
		call TimerJoinTimeout.startOneShot(JOIN_TIMEOUT);
		
		//send the request
		if(call JoinSend.send(SINK, &join_msg, sizeof(join_request_msg_t)) == SUCCESS) {
			radioBusy = TRUE;
		}
        return seq;
	}
    
    
    //MOTE: sendDone of the join request
	event void JoinSend.sendDone(message_t *msg, error_t error){
        radioBusy = FALSE;
        
		if(error == SUCCESS) {
#ifdef SEND_LED0
			call Leds.led0Toggle();
#endif
		}
		else {
			if(call JoinSend.send(SINK, &join_msg, sizeof(join_request_msg_t)) == SUCCESS) {
				radioBusy = TRUE;
			}
		}
	}
    
	
	//SINK handling the reception of the join request from a mote
	event message_t* JoinReceive.receive(message_t* msg, void* payload, uint8_t len) {
		join_reply_msg_t* joinMsg;
		join_request_msg_t* rec_msg = (join_request_msg_t*)payload;
		
		//only the sink should handle this message
		if (TOS_NODE_ID == SINK) {
			atomic {
				//save the seq_num
				sinkseqnum = rec_msg->seq_num;
				//get the id of the mote that wants to join
				srcJoinID = call AMPacket.source(msg);
	
				//if we want that already joined moted does not join the island we should undefine the macro NO_RESET_SINK
#ifndef NO_RESET_SINK
				//check if the mote is not already joined, in this case we ignore the message
				for (i = 0; i < MAX_NUM_MOTES; i++) {
					if (mote_list[i] == srcJoinID) {
						return msg;
					}
				}
#endif
				
				//prepare the reply msg containing the island address, the compile time ID of the joined mote, the time and the seq_num
				joinMsg = (join_reply_msg_t*) (call RadioPacket.getPayload(&join_msg, sizeof(join_reply_msg_t)));
				joinMsg->island_addr = islandID;
				joinMsg->mote_addr = srcJoinID;
				joinMsg->curTime = curTime;
				joinMsg->seq_num = sinkseqnum;
				
				if(call JoinReplySend.send(srcJoinID, &join_msg, sizeof(join_reply_msg_t)) == SUCCESS) {
					radioBusy = TRUE;
				}
			}
		}
		return msg;
		
	}
	
    
    //SINK: sendDone of the reply to the join request received from a MOTE
	event void JoinReplySend.sendDone(message_t *msg, error_t error){
        radioBusy = FALSE;
        
		if(error == SUCCESS) {
#ifdef SEND_LED0
			call Leds.led0Toggle();
#endif
			//start again the sendingTask because it may be interrupted by this send
			atomic {
				if (!sending) {
					sending = TRUE;
					post sendTask();
				}
			}
		}
		else {
			if(call JoinReplySend.send(srcJoinID, &join_msg, sizeof(join_reply_msg_t)) == SUCCESS) {
				radioBusy = TRUE;
			}
		}
	}
    
	
	//MOTE handling the join reply from the SINK
	event message_t* JoinReplyReceive.receive(message_t* msg, void* payload, uint8_t len) {
		join_reply_msg_t* rec_msg = (join_reply_msg_t*)payload;
		ack_join_msg_t *ackmsg = (ack_join_msg_t*)(call RadioPacket.getPayload(&join_msg, sizeof(ack_join_msg_t)));
		r_addr_t dest;
		
		atomic {
			//check if not already joined and if the seq_num matches the previously saved one
			if ((rec_msg->seq_num == moteseqnum) && (!joined)) {
				//save information from the msg: moteID, islandID and current time
				islandID = rec_msg->island_addr;
				moteID = rec_msg->mote_addr;
				curTime = rec_msg->curTime;
				
				//prepare ack
				dest.pid = islandID;
				dest.devid = SINK;
				ackmsg->dest = dest;
				ackmsg->src = getLocalAddress();
				ackmsg->seq_num = moteseqnum;
				memcpy(&(ackmsg->mote_desc), &mote_desc, sizeof(mote_desc_t));
			}
			
			joinRequest_received = TRUE;
			
			//send ack to SINK
			//TO UPDATE: the tinyos ack is not enough, the SINK should send a network-layer ack
			call Ack.requestAck(&join_msg);
			if(call AckJoinSend.send(SINK, &join_msg, sizeof(ack_join_msg_t)) == SUCCESS) {
				radioBusy = TRUE;
			}
		}
		return msg;
	}
	
    
    //MOTE signalling the complete join to the application
    event void AckJoinSend.sendDone(message_t *msg, error_t error){
        radioBusy = FALSE;
        
		if(error == SUCCESS) {
#ifdef SEND_LED0
			call Leds.led0Toggle();
#endif
			
			//TO UPDATE: the tinyso ack is not enough in this case, we should put the following code in a proper receive event
			//ack received for the JoinReplyMsg
			if(!call Ack.wasAcked(&join_msg)) {
                //retransmit ack msg
				if(call AckJoinSend.send(SINK, &join_msg, sizeof(ack_join_msg_t)) == SUCCESS) {
                    radioBusy = TRUE;
                }
#ifdef NETWORK_LEDS
                signalError();
#endif
			}
			else {
				//stop the timer
				call TimerJoinTimeout.stop();
				join_attempts = 0;
				
				//set joined TRUE
				joined = TRUE;
				
				//signal to the application the joining by passing the received r_addr
				signal Network.joined[COMP_MNGMNT](getLocalAddress(), SUCCESS);
			}
		}
		else {
            //retransmit ack msg
			if(call AckJoinSend.send(SINK, &join_msg, sizeof(ack_join_msg_t)) == SUCCESS) {
				radioBusy = TRUE;
			}
		}
	}
    
	
	//SINK forwarding the the mote_desc of the joined mote to the serial
	task void forwardJoinSerial() {
		net_msg_t *serialjoin;
        app_msg_t appjoinmsg;
		//serial_joined_msg_t sjoinmsg;
		r_addr_t dest;
		r_addr_t src;
		
        serial_joined_msg_t* sjoinmsg = (serial_joined_msg_t*)appjoinmsg.payload;
        //prepare the serial join msg
        sjoinmsg->msg_type = AM_SERIAL_JOINED_MSG;
		sjoinmsg->island_addr = recackmsg.src.pid;
		sjoinmsg->mote_addr = recackmsg.src.devid;
		sjoinmsg->mote_type = recackmsg.mote_desc.mote_type;
		sjoinmsg->transducers = recackmsg.mote_desc.transducers;
		sjoinmsg->actuators = recackmsg.mote_desc.actuators;
        
        
//		//prepare the serial join msg
//		sjoinmsg.msg_type = AM_SERIAL_JOINED_MSG;
//		sjoinmsg.island_addr = recackmsg.src.pid;
//		sjoinmsg.mote_addr = recackmsg.src.devid;
//		sjoinmsg.mote_type = recackmsg.mote_desc.mote_type;
//		sjoinmsg.transducers = recackmsg.mote_desc.transducers;
//		sjoinmsg.actuators = recackmsg.mote_desc.actuators;
		
        //prepare the app_msg_t for the join
        appjoinmsg.trans_id = CONNLESS;
        appjoinmsg.app_id = CONTROL_LAYER;
        
		atomic {
			//if not full, fill the next free position and increase the nextSerialOut
			if (!outSerialFull) {                
				serialjoin = (net_msg_t*)(call SerialPacket.getPayload(&outSerialMsg[nextSerialOut], sizeof(net_msg_t)));
				
				//fill the network header
				//dest.pid = PROXY_PEIS_ID;
                dest.pid = islandID;
				dest.devid = NULL_MOTE;
				src.pid = islandID;
				src.devid = recackmsg.src.devid;
                serialjoin->dest = dest;
                serialjoin->src = src;
                serialjoin->reliable = NOT_ACK;                
                serialjoin->seq_num = 0;
                serialjoin->nbytes = sizeof(app_msg_t);
				//copy the serial joined msg into the payload of the network msg in outSerialMsg buffer
				//memcpy(serialjoin->payload, &sjoinmsg, sizeof(serial_joined_msg_t));
                memcpy(serialjoin->payload, &appjoinmsg, sizeof(app_msg_t));
				
//				//set the size for this message: received payload+NETWORK_HEADER
//				sizeSerialOutMsg[nextSerialOut] = sizeof(net_msg_t);
				
				//increase nextSerialOut index
				nextSerialOut = (nextSerialOut + 1)%SERIAL_BUFF_SIZE;
				
				//check if the buffer is full
				if(nextSerialOut == curSerialOut) {
					outSerialFull = TRUE;
				}
			}
			
			//if we are not already sending messages from outSerialMsg, start sending
			if(!serialSending) {
				serialSending = TRUE;
				post sendSerialTask();
			}
		}       
		
        //signal to the application the joining of the new mote by passing the corresponding mote descriptor
		signal Network.receive[COMP_MNGMNT](recackmsg.src, (int8_t*)&mote_desc, sizeof(mote_desc_t), NOT_ACK, 0);
	}
	
	
    //SINK receiving the ack to the join reply
    event message_t* AckJoinReceive.receive(message_t* msg, void* payload, uint8_t len) {
        ack_join_msg_t* ackmsg = (ack_join_msg_t*)payload;
		
        //check islandID and seq_num, if it is not for this island or the seq_num doen't match, ignore it
        if ((ackmsg->dest.pid != islandID) || (ackmsg->seq_num != sinkseqnum)) {
            return msg;
        }
		
		atomic {
			//copy the mote descriptor in a local global variable from the recevied ack msg
			memcpy(&mote_desc, &(ackmsg->mote_desc), sizeof(mote_desc_t));		
			
			//TO UPDATE: circular list??
			//update mote_list
            mote_list[motelistpos] = ackmsg->src.devid;
            motelistpos = (motelistpos + 1)%MAX_NUM_MOTES;
			
			
			//TO UPDATE: implement the reply to the ack at network-level, the current tinyos ack is not enough
			
			
			//copy the received msg in the global ack msg
			memcpy(&recackmsg, payload, sizeof(ack_join_msg_t));
			//post the task forwarding the mote_desc to the serial
			post forwardJoinSerial();
		}
		return msg;
	}
	
	//MOTE: timeout for starting again the join protocol
	event void TimerJoinTimeout.fired() {
		//check if we reached the maximum number of join attempts
		if (join_attempts < JOIN_ATTEMPTS) {
			//if we didn't reach the limit increase the counter and start again the timer
			join_attempts++;
			call TimerJoinTimeout.startOneShot(JOIN_TIMEOUT);
		}
		
		
		//check if the join_request has been already received at sink mote
		//in case it is not, then we send again the join_request_msg_t
		if (!joinRequest_received) {
			if(call JoinSend.send(SINK, &join_msg, sizeof(join_request_msg_t)) == SUCCESS) {
				radioBusy = TRUE;
			}
		}
		//else, we just have to send the ack_join_msg_t
		else {
			call Ack.requestAck(&join_msg);
			if(call AckJoinSend.send(SINK, &join_msg, sizeof(ack_join_msg_t)) == SUCCESS) {
				radioBusy = TRUE;
			}
		}
	}
    
    /******************************************************************************/
    /***************************** END JOIN PROTOCOL ******************************/
    /******************************************************************************/
    
	
	
	event void CommControl.initDone(error_t success) {
		
	}
	
	event void CommControl.radioStarted(error_t success) {
		
	}
	
	event void CommControl.radioStopped(error_t success) {
		
	}
	
	event void CommControl.serialStarted(error_t success) {
		
	}
	
	event void CommControl.serialStopped(error_t success) {
		
	}
    
    default event void Network.receive[uint8_t comp](r_addr_t sender, int8_t* payload, uint8_t nbytes, bool reliable, uint16_t seq_num) {
    
    }
	
	default event void Network.joined[uint8_t comp](r_addr_t addr, error_t success) {
	
	}
}