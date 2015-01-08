/*
 *  ConnectionlessC.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 09/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Configuration of the Communication layer for the Connectionless interface.
 */

#include "network.h"
//#include "definitions.h"

configuration ConnectionlessC {
	//list of provided interfaces
	provides {
		interface Connectionless[uint8_t comp];
	}
}

implementation {
	//list of used components
	components LedsC;
	components ConnectionlessP;
    components TransportMD_C;
	components Rubicon_AckC;
	
	//wiring
	Connectionless = ConnectionlessP;
	ConnectionlessP.Leds->LedsC;
	ConnectionlessP.TransportMD->TransportMD_C.TransportMD[CONNLESS];
	ConnectionlessP.Rubicon_Ack->Rubicon_AckC.Rubicon_Ack[CONNLESS];
}