/*
 *  network.h
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 19/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

#include "definitions.h"

#ifndef NETWORK_H
#define NETWORK_H

#define JOIN_TIMEOUT 500
#define JOIN_ATTEMPTS 5

////size of incoming and outgoing radio messages buffer
//#define RADIO_BUFF_SIZE 4
////size of incoming serial messages buffer
//#define SERIAL_BUFF_SIZE 2


//network generic message, this is the message that the Network component sends
typedef nx_struct net_msg {
    r_addr_t dest;
    r_addr_t src;
    nx_bool reliable;
    nx_uint16_t seq_num;
    nx_uint8_t nbytes; //actual number of bytes of the payload
    //NETWORK_PAYLOAD is 114 (maximum tinyos payload) - 16 of network header
    //- 1 byte for transportMD - 1 byte for application msg_type - 1 byte for appMD
    nx_int8_t payload[NETWORK_PAYLOAD];
} net_msg_t;

//serial message coming from the base station to configure one of the network parameter, such as the islandID
typedef nx_struct config_msg {
	nx_uint8_t type;
	nx_uint16_t value; //according to type field, the value can assume different meanings, like the islandID
} config_msg_t;

/******************************************************************************/
/************************** START JOIN MESSAGES *******************************/
/******************************************************************************/
typedef nx_struct mote_desc {
	nx_uint8_t mote_type;
	nx_uint16_t transducers;
	nx_uint8_t actuators;
} mote_desc_t;

typedef nx_struct join_request_msg {
	nx_uint16_t seq_num;
	mote_desc_t mote_desc;
} join_request_msg_t;


typedef nx_struct join_reply_msg {
	nx_uint16_t island_addr;
    nx_uint16_t mote_addr;
	nx_uint32_t curTime;
	nx_uint16_t seq_num;	
} join_reply_msg_t;


typedef nx_struct ack_join_msg {
    r_addr_t dest;
	r_addr_t src;
    nx_uint16_t seq_num;
	mote_desc_t mote_desc;
} ack_join_msg_t;


typedef nx_struct serial_joined_msg {
	nx_int8_t msg_type;
	nx_uint16_t island_addr;
	nx_uint16_t mote_addr;
	nx_uint8_t mote_type;
	nx_uint16_t transducers;
	nx_uint8_t actuators;
} serial_joined_msg_t;
/******************************************************************************/
/************************** END JOIN MESSAGES *******************************/
/******************************************************************************/




//old messages: to be removed
/* Messages */
typedef nx_struct net_command_msg {
	nx_uint8_t src_island_addr;
	nx_uint16_t src_mote_addr;
	nx_uint8_t type;
} net_command_msg;

typedef nx_struct net_data_msg {
	nx_uint8_t src_island_addr;
	nx_uint16_t src_mote_addr;
	nx_uint16_t id;
	nx_uint16_t counter;
	nx_uint16_t temp;
	nx_uint16_t humid;
	nx_uint16_t light;
	nx_uint16_t rssi;
} net_data_msg;

typedef nx_struct demo_msg {
	nx_uint8_t msg_type;
	nx_uint16_t sensorsBitmask;
	nx_uint16_t period;
} demo_msg_t;


typedef nx_struct complete_msg {
    r_addr_t dest;
    r_addr_t src;
    nx_bool reliable;
    nx_uint16_t seq_num;
    nx_uint8_t nbytes; //actual number of bytes of the payload
    nx_uint8_t trans_id;
    nx_uint8_t app_id;
	//NETWORK_PAYLOAD is 114 (maximum tinyos payload) - 16 of network header
    //- 1 byte for transportMD - 1 byte for application msg_type - 1 byte for appMD
    demo_msg_t payload; 
} complete_msg_t;


////Structure representing the Connectionless message 
//typedef nx_struct connless_msg {
//    nx_bool reliable;
//    nx_uint16_t seq_num;
//    nx_uint8_t nbytes; //number of bytes of the payload
//    nx_int8_t payload[CONNLESS_PAYLOAD];
//} connless_msg_t;


#endif 
