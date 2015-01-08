/*
 *  streams.h
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 01/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

#ifndef STREAMS_H
#define STREAMS_H


/* Stream descriptor type definition: table identifier + table entry index */
typedef struct stream_desc {
	uint8_t stream_id;
	int8_t status;
} stream_desc;

/* Write and read return values */
enum {
	STREAM_OPENED = 1,
	INVALID_DESCRIPTOR = -1,
	STREAM_NOT_OPEN = -2,
	SEND_FAIL = -3,
	ILLEGAL_OPERATION = -4,
	NO_SPACE_LEFT = -5,
	TEMPORARY_FAILURE = -6,
	WRITE_ERROR = -7
};

/* Sensor types */
enum {
	LIGHT = 1,
	TEMPERATURE = 2,
	VOICE = 3,
	MAGNETISMX = 4,
	MAGNETISMY = 5,
	ACCELERATIONX = 6,
	ACCELERATIONY = 7,
	HUMID = 8,
	RSSI = 9
};

/* Sampling type */
enum {
	PERIODIC = 1,
	ON_DEMAND = 2,
};

enum {
	SENSORLIGHT  = 0,
	SENSORTEMP   = 1,
	SENSORACCELX = 2,
	SENSORACCELY = 3,
	SENSORMAGX   = 4,
	SENSORMAGY   = 5,
	SENSORVOICE  = 6
	
};

enum {
    QUEUE_SIZE = 5
};

#endif