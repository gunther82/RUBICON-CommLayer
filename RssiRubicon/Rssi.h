#ifndef RSSI_H
#define RSSI_H

typedef nx_struct RssiMsg 
{
	nx_uint8_t idmitt;
	//nx_uint8_t idtransducer;
} RssiMsg;
 
#define	RSSI_CHANNEL 11
#define AM_RSSI 6
#define	NUM_ANCHORS 5

#define ALGORITHM_REQUEST 0
#define	ALGORITHM_CYCLE 1
#define ALGORITHM_ONLYONEREQUEST 2
#define ALGORITHM_INDEPENDENTCYCLE 3


#define ALGORITHM 0
	
#define TIMER_ANCHOR NUM_ANCHORS*20

#define REQUESTER 1 

#define TIMER_TRANSDUCER 35+(10*NUM_ANCHORS)


#endif

