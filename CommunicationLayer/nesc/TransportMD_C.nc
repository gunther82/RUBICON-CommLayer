//
//  TransportMD_C.nc
//  RUBICON src
//
//  Created by Claudio Vairo on 08/02/13.
//  Copyright (c) 2013 ISTI-CNR. All rights reserved.
//


configuration TransportMD_C {
    //list of provided interfaces
	provides {
		interface TransportMD[uint8_t comp];
	}
}

implementation {
    //list of used components
	components LedsC;
	components TransportMD_P;
	components NetworkC;
	
	//wiring
	TransportMD = TransportMD_P;
	TransportMD_P.Leds->LedsC;
	TransportMD_P.Network->NetworkC.Network[TRANSPORT_MD];
}
