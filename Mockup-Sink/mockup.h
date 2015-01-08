/*
 *  mockup.h
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 27/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

#ifndef MOCKUP_H
#define MOCKUP_H

#include "../CommunicationLayer/nesc/definitions.h"
#include "../CommunicationLayer/nesc/synaptic_channels.h"

#define MOCKUP_LEDS 1
#define NETWORK_LEDS 1
#define SEND_LED0 1
//#define LOGGER_LEDS 1

//COMPONENT MACRO
#define LEARNING_COMP 1
//#define CONTROL_COMP 1
#define AUTO_MAURO 11

//JOIN DEMO MACRO
#define MULTIPLE_JOIN 1
#define NO_RESET_SINK 1

//TO UPDATE: how to define the ISLAND_ADDRESS?? maybe a compilation parameter?
//#define SYNAPTIC 1
#define ISLAND_ADDR 11
const uint8_t mote_type = TELOSB_DEMO;
#ifdef SENSORBOARD_STANDARD
//const uint16_t transducers = 0xFFFF & (LIGHT+TEMP+HUMID+RSSI1+RSSI2);
const uint16_t transducers = 0xFFFF & (LIGHT+TEMP+HUMID+RSSI1+RSSI2+RSSI3+RSSI4+RSSI5+RSSI6+RSSI7+RSSI8+RSSI9+RSSI10);
#endif
#ifdef SENSORBOARD_EM1000
const uint16_t transducers = 0xFFFF & (ACCEL_X+ACCEL_Y+LIGHT+TEMP+HUMID+RSSI1+RSSI2+RSSI3+RSSI4+RSSI5);
#endif
#ifdef SENSORBOARD_SE1000
const uint16_t transducers = 0xFFFF & (PIR+MAGNETIC+MIC);
#endif
const uint8_t actuators = 0xFF & LED;

#define MAX_DESCRIPTION_SIZE 20// maximum size of the description of a mote
#define LEARN_MODULE_MSG_HEADER 11
#define TOSH_DATA_LENGTH 114
#define CONNLESS_PAYLOAD 100
#define CONNLESS_HEADER 8

///* Message Types */
//enum AM_TYPES {
//	AM_SERIALTEMP = 1,
//	AM_SERIALHUMID,
//	AM_SERIALLIGHT,
//	AM_SERIALRSSI,
//	AM_SERIALDATA,
//	AM_TEMP_MSG,
//	AM_HUMID_MSG,
//	AM_LIGHT_MSG,
//	AM_RSSI_MSG,
//	AM_DATA_MSG, //10
//	AM_COMMAND_MSG,
//	AM_SERIAL_DATA_MSG,
//	AM_SYN_DATA_MSG,
//	AM_JOIN_MSG,
//	AM_JOIN_REPLY_MSG,
//	AM_SERIAL_JOINED_MSG,
//} AM_TYPES;

enum WAIT_MODALITY {
	RANDOM = 1,
	FIXED,
	PERIODICALLY,
} WAIT_MODALITY;

/* Messages */
typedef nx_struct command_msg {
	nx_uint8_t type;
	nx_uint16_t counter;
	nx_uint16_t delay;
} command_msg;

typedef nx_struct data_msg {
	//nx_uint16_t id;
	nx_uint16_t counter;
	nx_uint16_t temp;
	nx_uint16_t humid;
	nx_uint16_t light;
	nx_uint16_t rssi;
} data_msg;

typedef nx_struct serial_data_msg {
	nx_uint8_t src_island_addr;
	nx_uint16_t src_mote_addr;
	nx_uint16_t counter;
	nx_uint16_t temp;
	nx_uint16_t humid;
	nx_uint16_t light;
	nx_uint16_t rssi;
} serial_data_msg;

//typedef nx_struct serial_joined_msg {
//	nx_uint8_t island_addr; //TO UPDATE: 2 bytes
//	nx_uint16_t mote_addr;
//	nx_uint16_t mote_type;  //TO UPDATE: only 1 byte
//	nx_uint16_t transducers;
//	nx_uint8_t actuators;
//} serial_joined_msg_t;

typedef nx_struct demo_data_msg {
	nx_uint8_t msg_type;
	nx_uint16_t light;
	nx_uint16_t temp;
	nx_uint16_t accx;
	nx_uint16_t accy;
	nx_uint16_t pir;
	nx_uint16_t mic;
	nx_uint16_t humid;
	nx_uint16_t magnetic;
	nx_uint16_t my_rssi;
} demo_data_msg_t;


/* Messages coming from the Learning Network Manager to the sink through the serial interface*/
typedef nx_struct netid_cmd_msg {
	nx_uint8_t type;
	nx_uint16_t netId; //Learning Module ID
	nx_uint16_t seq_num;
} netid_cmd_msg_t;

//set of messages to be sent to all the sensors in the network -> all the islands
typedef nx_struct broadcast_cmd_msg {
	nx_uint8_t type;
	nx_uint16_t clock;
	nx_uint16_t seq_num; //TO UPDATE: to check if it is possible to ack broadcast messages, in this case seq_num is used, otherwise it can be removed
} broadcast_cmd_msg_t;


//set of messages to create the synaptic connections -> that results in adding these connection in a synaptic channels
typedef nx_struct syn_create_cmd_msg {
	nx_uint8_t type;
	nx_uint16_t src_island_addr;
	nx_uint16_t src_mote_addr;
	nx_uint16_t dest_island_addr; //to check if it has to be here or the mote will add it later
	nx_uint16_t dest_mote_addr;
	nx_uint16_t src_netId;
	nx_uint16_t dest_netId;
    nx_uint8_t  src_neurons_size; //number of elements of the following src_neurons array
	nx_uint16_t src_neurons[MAX_SYN_CH_BUFFER_SIZE]; //20 bytes
    nx_uint8_t dest_neurons_size; //number of elements of the following dest_neurons array
	nx_uint16_t dest_neurons[MAX_SYN_CH_BUFFER_SIZE]; //20 bytes
	nx_int8_t params;
	nx_uint16_t seq_num;
} syn_create_cmd_msg_t;


//message to upload a portion of a weight matrix (Win, Wa or Wb) to a device (mote, pc, etc...)
typedef nx_struct learning_module_cmd_msg {
	nx_uint8_t type;
	nx_uint16_t netId; //Learning Module ID
	nx_uint16_t seq_num;
    nx_uint8_t rows; //number of TOTAL rows of the matrix Win, Wa, Wb
    nx_uint8_t cols; //number of columns of the original matrix (Win, Wa, Wb), comprising the Bias, not with the representation on 4 bits
	nx_uint8_t matrix_size; //actual size of the portion of the matrix contained in the message
	nx_uint8_t weights[MATRIX_PAYLOAD]; //92 bytes //maximum matrix of this message is 450 bytes
} learning_module_cmd_msg_t;


//message to upload a portion of a weight matrix (Win, Wa or Wb) to a device (mote, pc, etc...)
typedef nx_struct learning_module_float_cmd_msg {
	nx_uint8_t type;
	nx_uint16_t netId; //Learning Module ID
	nx_uint16_t seq_num;
    nx_uint8_t rows; //number of TOTAL rows of the matrix
    nx_uint8_t cols; //number of TOTAL columns of the matrix, comprising the Bias
	nx_uint8_t matrix_size; //actual size (number of elements) of the portion of the matrix contained in the message
    nx_float weights[FLOAT_ELEMENTS]; //23 elements = 92bytes //maximum matrix of this message is 255 float = 1020 bytes
} learning_module_float_cmd_msg_t;


////packetized weight matrix
//typedef nx_struct packetized_matrix_msg {
//	nx_uint16_t seq_num;
//	nx_uint8_t pkt_num;
//	nx_uint8_t weights[TOSH_DATA_LENGTH-3];
//} packetized_matrix_msg_t;


/* Messages coming from the Learning Network to the Learning Manager to notify the result of a command*/
typedef nx_struct learning_notification_msg {
	nx_uint8_t type;
    nx_uint16_t seq_num;
	nx_uint8_t ack;
} learning_notification_msg_t;


//Messages from the Learning Network downloading the learning module (ie. the weight matrix) from a device
typedef nx_struct learning_module_down_msg {
    nx_uint8_t type;
	nx_uint16_t netId; //Learning Module ID source of the matrix
    nx_uint8_t matrix_size; //actual size (number of elements) of the portion of the matrix contained in the message
	nx_float weights[FLOAT_ELEMENTS]; //23 elements = 92bytes //maximum matrix of this message is 255 float = 1020 bytes
} learning_module_down_msg_t;


//Set of messages delivering the output predicted by the Learning Network
typedef nx_struct learning_output_msg {
    nx_uint8_t type;
    nx_uint8_t output_size; //actual size (number of elements) of the output
	nx_float output[MAX_OUT_NUMBER]; //4*24 = 96 bytes //maximum size = 4*50 bytes
} learning_output_msg_t;


//message containing periodic updates from transducers for control layer
typedef nx_struct updates_msg {
    nx_uint8_t type;
    nx_uint8_t output_size; //number of transducers read, that is a couple <transducer_id, value>, so the size of the array is 2*output_size
	nx_uint16_t updates[NUM_UPDATES]; //2*49 = 98 bytes
} updates_msg_t;


//command message to request periodic readings from the requested transducers of a mote
typedef nx_struct read_updates_msg {
    nx_uint8_t type;
    nx_uint16_t sampling_rate;
	nx_uint16_t transducers; //bitmask of trasducers to be read
} read_updates_msg_t;


//Structure representing the Connectionless message 
typedef nx_struct connless_msg {
    nx_uint8_t msg_type; //identifier of the kind of message -> used to parse the payload
    nx_uint8_t island_addr; //dest island
    nx_uint16_t mote_addr; //dest mote
    nx_bool reliable;
    nx_uint16_t seq_num;
    nx_uint8_t nbytes; //number of bytes of the payload
    nx_int8_t payload[CONNLESS_PAYLOAD];
} connless_msg_t;

#endif 
