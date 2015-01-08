/*
 *  MockupM.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 27/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Implementation file for the Mockup Communication Layer of RUBICON.
 */

//includes mockup;
//#include "mockup.h"
//#include "definitions.h"

module MockupM {
	
	//list of used interfaces
	uses interface Boot;
	uses interface Leds;
	
	//Communication Layer interfaces
	//uses interface Streams;
	//uses interface Synaptic_Channels as Synaptic;
	uses interface Connectionless;
	uses interface ComponentManagement;
	uses interface Learning;
	
	
#ifdef SS
	uses interface SSToApp;
#endif
	
	//Timers
		
	//Serial
	uses interface Packet as SerialPacket;

	uses interface Receive as LearnModuleCmdReceive;
    uses interface Receive as LearnModuleFloatCmdReceive;
	
	//Radio
	uses interface Packet as RadioPacket;
	
//	#ifdef __CC2420_H__
	#if defined(PLATFORM_MICAZ) || defined(PLATFORM_TELOSB) || defined(PLATFORM_XM1000)
		uses interface CC2420Packet;
	#elif defined(PLATFORM_IRIS)
		uses interface PacketField<uint8_t> as PacketRSSI;
	#endif
}

implementation {
	uint16_t rssi;
	
	uint16_t islandID;
    uint16_t moteID;
	
	event void Boot.booted() {
		
		call ComponentManagement.initCommunications();
		
		islandID = ISLAND_ADDR;
        moteID = SINK;
#ifdef MOCKUP_LEDS
		//call Leds.led2On();
#endif
	}
	
	
//#ifdef __CC2420_H__
#if defined(PLATFORM_MICAZ) || defined(PLATFORM_TELOSB) || defined(PLATFORM_XM1000)
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
		return 0;
	}
#endif
	
	
	/*************** START LEARNING SERIAL COMMANDS ********************/
	
	
//	task void packetize_matrix() {
//		uint8_t tot_pkts;
//		uint8_t cur_pkt = 0;
//		uint16_t seq_num = learnmodcmdmsg.seq_num;
//		uint8_t dest_isl;
//		uint16_t dest_mote;
//		packetized_matrix_msg_t *outmsg = (packetized_matrix_msg_t*)(call RadioPacket.getPayload(&radioPkt, sizeof(packetized_matrix_msg_t)));
//		
//		//how many packets are needed to send the matrix
//		tot_pkts = sizeof(learning_module_cmd_msg_t) / TOSH_DATA_LENGTH;
//		
//		//check if the message is in the local island
//		if (learnmodcmdmsg.dest_island_addr == island) {
//			dest_isl = island;
//			dest_mote = learnmodcmdmsg.dest_mote_addr;
//
//		}
//		else {
//			//send the message to a remote island
//		}
//		
//		//TO UPDATE: smart management of ack and seq number
//		wait_ack = cur_pkt;
//		
//		//prepare the pkt
//		outmsg->seq_num = seq_num;
//		outmsg->pkt_num = cur_pkt;
//		memcpy(outmsg->weights, &learnmodcmdmsg.weights, (TOSH_DATA_LENGTH-3));
//		
//		call Connectionless.send(dest_isl, dest_mote, (int8_t*)outmsg, sizeof(packetized_matrix_msg_t), REQ_ACK, learnmodcmdmsg->seq_num, AM_PACKETIZED_MATRIX_MSG);
//		
//	}
	
	event message_t* LearnModuleCmdReceive.receive(message_t* msg, void* payload, uint8_t len) {
//		learning_module_cmd_msg_t *learnmodcmdmsg = (learning_module_cmd_msg_t*)payload;
//        r_addr_t dest;
//        
//		//check if the message is in the local island
//		if (learnmodcmdmsg->dest_island_addr == islandID) {
//			
//			//TO UPDATE: smart management of ack and seq number
//			wait_ack = learnmodcmdmsg->seq_num;
//            
//            //prepare the destination
//            dest.pid = ISLAND_ADDR;
//            dest.devid = learnmodcmdmsg->dest_mote_addr;
//            
//            //TO UPDATE: check the seq num and the AM_TYPE  -> learnmodcmdmsg->seq_num, AM_LEARNING_MODULE_CMD_MSG
//			if(call Connectionless.send(dest, (int8_t*)learnmodcmdmsg, learnmodcmdmsg->matrix_size+LEARN_MODULE_MSG_HEADER, REQ_ACK, LEARNING) == SUCCESS) { 
//            }
//		}
//		else {
//			//send the message to a remote island
//		}
	
		return msg;
	}
    
    event message_t* LearnModuleFloatCmdReceive.receive(message_t* msg, void* payload, uint8_t len) {
//		learning_module_float_cmd_msg_t *learnmodfloatcmdmsg = (learning_module_float_cmd_msg_t*)payload;
//        r_addr_t dest;
//        
//		//check if the message is in the local island
//		if (learnmodfloatcmdmsg->dest_island_addr == islandID) {
//			
//			//TO UPDATE: smart management of ack and seq number
//			wait_ack = learnmodfloatcmdmsg->seq_num;
//			
//            //prepare the destination
//            dest.pid = ISLAND_ADDR;
//            dest.devid = learnmodfloatcmdmsg->dest_mote_addr;
//            
//            //TO UPDATE: check the seq num and the AM_TYPE  -> learnmodfloatcmdmsg->seq_num, AM_LEARNING_MODULE_FLOAT_CMD_MSG
//			call Connectionless.send(dest, (int8_t*)learnmodfloatcmdmsg, (learnmodfloatcmdmsg->matrix_size*4)+LEARN_MODULE_MSG_HEADER, REQ_ACK, LEARNING);
//		}
//		else {
//			//send the message to a remote island
//		}
//        
		return msg;
	}
    
    
    
	/*************** END LEARNING SERIAL COMMANDS ********************/	
	
    event void Connectionless.receive(r_addr_t sender, int8_t* payload, uint8_t nbytes) {
        
    }
    
	event void ComponentManagement.initCommunicationsDone(error_t success) {

	}
	
	event void ComponentManagement.moteJoined(uint16_t island_addr, uint16_t mote_addr, mote_desc_t* description) {
//		serial_joined_msg_t *joinedMsg;
//		
//		if (TOS_NODE_ID == SINK) {
//			//prepare the serial_joined_msg_t: set src_island and src_mote and copy the received data
//			joinedMsg = (serial_joined_msg_t*)(call SerialPacket.getPayload(&serialPkt, sizeof(serial_joined_msg_t)));
//			joinedMsg->island_addr = island_addr;
//			joinedMsg->mote_addr = mote_addr;
//			joinedMsg->mote_type = description->mote_type; 
//			joinedMsg->transducers = description->transducers;
//			joinedMsg->actuators = description->actuators;
//			
//			if((serialState) && (!serialBusy)) {
//				if(call SerialJoinedSend.send(AM_BROADCAST_ADDR, &serialPkt, sizeof(serial_joined_msg_t)) == SUCCESS) {
//					serialBusy = TRUE;
//				}
//			}
//			
//		}
		
	}
    
    event void ComponentManagement.joined(uint16_t island_addr, uint16_t mote_addr, error_t success) {
        
    }
	
    event void Learning.demo_data(uint16_t demo_data) {}
    
#ifdef SS
	event error_t SSToApp.openRemoteDone(stream_type stream_desc, uint8_t symbolic_name, error_t success) {
		return FAIL;
	}
	
	event error_t SSToApp.readDone(stream_type stream_desc, int8_t* buf, uint8_t nbytes, error_t success) {
		return FAIL;
	}
#endif
}
