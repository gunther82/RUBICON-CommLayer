/*
 *  SynChannelsC.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 05/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

//#include "synaptic_channels.h"

/**
 * Configuration of the Communication layer for the Synaptic Channel abstraction.
 */

configuration SynChannelsC {
	//list of provided interfaces
	provides {
		interface Synaptic_Channels;
	}
}

implementation {
	//list of used components
	components SynChannelsP;
	components new TimerMilliC() as TimerSendChOut;
    components new TimerMilliC() as TimerPhaseShift;
    components LedsC;
	
    components TransportMD_C;
	
	//wiring
	Synaptic_Channels = SynChannelsP;
	SynChannelsP.TimerSendChOut->TimerSendChOut;
    SynChannelsP.TimerPhaseShift->TimerPhaseShift;
    SynChannelsP.TransportMD->TransportMD_C.TransportMD[SYN_CHANNEL];
    SynChannelsP.Leds->LedsC;
}