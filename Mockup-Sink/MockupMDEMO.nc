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
	uses interface SplitControl as SerialControl;
	uses interface Packet as SerialPacket;
	
	uses interface AMSend as SerialTempSend;
	uses interface AMSend as SerialHumidSend;
	uses interface AMSend as SerialLightSend;
	uses interface AMSend as SerialRSSISend;
	uses interface AMSend as SerialDataSend;
	uses interface AMSend as SerialJoinedSend;

	uses interface Receive as SerialCommandReceive;
	
	//Radio
	uses interface SplitControl as RadioControl;
	
//	#ifdef __CC2420_H__
	#if defined(PLATFORM_MICAZ) || defined(PLATFORM_TELOSB) || defined(PLATFORM_XM1000)
		uses interface CC2420Packet;
	#elif defined(PLATFORM_IRIS)
		uses interface PacketField<uint8_t> as PacketRSSI;
	#endif
}

implementation {
	
	bool serialState;
	bool serialBusy;
	
	bool radioState;
	bool radioBusy;
	
	//outgoing messages
	message_t serialPkt;
	
	//incoming messages data structures
	message_t radioBuf[RADIO_BUFF_SIZE];
	message_t *radioBufPos[RADIO_BUFF_SIZE];
	uint8_t nextfree;
	uint8_t current;
	
	uint16_t rssi;
	
	uint16_t counter;
	
	uint16_t delay;
	
	command_msg commandMsg;
	data_msg dataMsg;
	
	bool firstSet = FALSE;
	
	event void Boot.booted() {
		uint8_t i;
		
		//call SerialControl.start();
		//call RadioControl.start();
		call ComponentManagement.initCommunications();
		
		//buffer variables
		for(i = 0; i < RADIO_BUFF_SIZE; i++) {
			radioBufPos[i] = &radioBuf[i];
		}
		nextfree = 0;
		current = 0;
		
		counter = 0;
		
		
		
#if defined(PLATFORM_TELOSB)
		//call Leds.led0On();
#elif defined(PLATFORM_IRIS) || defined(PLATFORM_MICAZ)
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
	
	
	//updates the buffer for the incoming messages
	message_t* updateBuffer(message_t *msg) {
		message_t *ret;
		atomic {
			ret = radioBufPos[nextfree];
			radioBufPos[nextfree] = msg;
			nextfree = (nextfree + 1) % RADIO_BUFF_SIZE;
		}
		return ret;
	}
	
	
	//reception of a command message over the serial interface
	event message_t* SerialCommandReceive.receive(message_t* msg, void* payload, uint8_t len) {
		command_msg *comm = (command_msg*)payload;
		
		call Leds.led1Toggle();
		
		if (comm->type != READ_LOG) {//READ_DATA, READ_DATA_RANDOM_WAIT or READ_PERIODICALLY
			commandMsg.type = comm->type;
			commandMsg.counter = counter++;
			commandMsg.delay = comm->delay; //in case of READ_PERIODICALLY the delay is the sampling rate
			
			call Connectionless.send(LOCAL_ISLAND, AM_BROADCAST_ADDR, (int8_t*)&commandMsg, sizeof(command_msg), FALSE, AM_COMMAND_MSG);
		}
		
		if(comm->type == DEMO_SYN) {
			call Learning.start();
		}
		
		return msg;
	}	
	
	//reception of a radio message from the Connectionless interface, tipically for receiving data from motes
	event void Connectionless.receive(uint8_t src_island_addr, uint16_t src_mote_addr, int8_t* buf, uint8_t nbytes, uint8_t am_type) {
		uint8_t *pointer;
		serial_data_msg *serialDataMsg;
		
		switch (am_type) {
			case AM_DATA_MSG:				
				//SINK: get the rssi from the received msg and forwards it on the serial
				if (TOS_NODE_ID == SINK) {
					//prepare the serial_data_msg: set src_island and src_mote and copy the received data
					serialDataMsg = (serial_data_msg*)(call SerialPacket.getPayload(&serialPkt, sizeof(serial_data_msg)));
					pointer = (uint8_t*)serialDataMsg;
					serialDataMsg->src_island_addr = src_island_addr;
					serialDataMsg->src_mote_addr = src_mote_addr;
					memcpy((pointer+DIM_ADDRESS), buf, nbytes);
					//serialDataMsg->rssi = 11;
					
					if((serialState) && (!serialBusy)) {
						if(call SerialDataSend.send(AM_BROADCAST_ADDR, &serialPkt, sizeof(serial_data_msg)) == SUCCESS) {
							serialBusy = TRUE;
						}
					}
				}
				
				break;

		}
	}

	
	event void Learning.demo_data(uint16_t demo_data) {
		serial_data_msg *serialDataMsg;
		serialDataMsg = (serial_data_msg*)(call SerialPacket.getPayload(&serialPkt, sizeof(serial_data_msg)));

		serialDataMsg->src_island_addr = LOCAL_ISLAND;
		serialDataMsg->src_mote_addr = 1;
		serialDataMsg->counter = counter++;
		serialDataMsg->light = demo_data;
		serialDataMsg->temp = 0;
		serialDataMsg->humid = 0;
		serialDataMsg->rssi = 0;
		
		if((serialState) && (!serialBusy)) {
			if(call SerialDataSend.send(AM_BROADCAST_ADDR, &serialPkt, sizeof(serial_data_msg)) == SUCCESS) {
				serialBusy = TRUE;
			}
		}
	}
	
	
	event void Connectionless.ack(error_t success) {
		
	}
	
	
	event void SerialControl.stopDone(error_t error){
		serialState = FALSE;
	}
	
	event void SerialControl.startDone(error_t error){
		if(error == SUCCESS) {		
			serialState = TRUE;
		}
		else {
			call SerialControl.start();
			serialState = FALSE;
		}
	}
	
	
	event void RadioControl.stopDone(error_t error){
		if(error == SUCCESS) {
			radioState = FALSE;
			//call Leds.led1Off();
		}
	}
	
	event void RadioControl.startDone(error_t error){
		if(error == SUCCESS) {		
			//call Leds.led1On();
			radioState = TRUE;
		}
		else {
			radioState = FALSE;
			call RadioControl.start();
		}
	}
	
	
	
	event void SerialTempSend.sendDone(message_t *msg, error_t error){
			serialBusy = FALSE;
	}
	
	event void SerialHumidSend.sendDone(message_t *msg, error_t error){
			serialBusy = FALSE;
	}
	
	event void SerialLightSend.sendDone(message_t *msg, error_t error){
			serialBusy = FALSE;
	}
	
	event void SerialRSSISend.sendDone(message_t *msg, error_t error){
			serialBusy = FALSE;
	}
	event void SerialDataSend.sendDone(message_t *msg, error_t error){
		serialBusy = FALSE;
		call Leds.led2Toggle();
	}
	event void SerialJoinedSend.sendDone(message_t *msg, error_t error){
		serialBusy = FALSE;
		if (!firstSet) {
			firstSet = TRUE;
			call Leds.led2Toggle();
		}
	}
	
	event void ComponentManagement.initCommunicationsDone(error_t success) {

	}
	
	event void ComponentManagement.moteJoined(uint8_t island_addr, uint16_t mote_addr, mote_desc_t* description) {
		serial_joined_msg_t *joinedMsg;
		
		if (TOS_NODE_ID == SINK) {
			//prepare the serial_joined_msg_t: set src_island and src_mote and copy the received data
			joinedMsg = (serial_joined_msg_t*)(call SerialPacket.getPayload(&serialPkt, sizeof(serial_joined_msg_t)));
			joinedMsg->island_addr = island_addr;
			joinedMsg->mote_addr = mote_addr;
			joinedMsg->mote_type = description->mote_type; 
			joinedMsg->transducers = description->transducers;
			joinedMsg->actuators = description->actuators;
			
			if((serialState) && (!serialBusy)) {
				if(call SerialJoinedSend.send(AM_BROADCAST_ADDR, &serialPkt, sizeof(serial_joined_msg_t)) == SUCCESS) {
					serialBusy = TRUE;
				}
			}
			
		}
		
	}
	
#ifdef SS
	event error_t SSToApp.openRemoteDone(stream_type stream_desc, uint8_t symbolic_name, error_t success) {
		return FAIL;
	}
	
	event error_t SSToApp.readDone(stream_type stream_desc, int8_t* buf, uint8_t nbytes, error_t success) {
		return FAIL;
	}
#endif
}