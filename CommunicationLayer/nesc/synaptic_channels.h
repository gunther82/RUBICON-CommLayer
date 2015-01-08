/*
 *  synaptic_channels.h
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 04/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

#ifndef SYNAPTIC_CHANNELS_H
#define SYNAPTIC_CHANNELS_H

//availability of the syn channel
//#define FREE 1
//#define BUSY 0

#define SYN_OUT 0
#define IN 1

//information about the data received on a syn channel
#define NEW_DATA 0
#define NO_SYN_CHANNEL_ID 1

//status of syn channel
#define AVAILABLE 0
#define CREATED 1
#define ACTIVE 2

#define DEFAULT_CLOCK 500

typedef float syn_data_t;
typedef uint32_t syn_ch_id_t;

/* Synaptic Channel descriptor type definition: channel identifier */
typedef struct syn_channel {
	syn_ch_id_t channel_id; //try to find a way to reduce it to at least 16 bits
	uint8_t status; // possible status of the synaptic channel (active, etc)
	uint8_t size; //the actual size of the synaptic channel, i.e. the number of synaptic connections
	int8_t params[DIM_SYNCHANNEL_PARAM];
	syn_data_t data[MAX_SYN_CH_BUFFER_SIZE]; //MAX_SYN_CH_BUFFER_SIZE is the maximum number of synaptic connections for a synaptic channel
} syn_channel_t;

// GALLICCH.
// This is to UPDATE
typedef uint8_t transducerID_t;

#endif
