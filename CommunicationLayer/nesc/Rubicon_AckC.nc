//
//  Rubicon_AckC.nc
//  RUBICON src
//
//  Created by Claudio Vairo on 22/03/13.
//  Copyright (c) 2013 ISTI-CNR. All rights reserved.
//


configuration Rubicon_AckC {
    //list of provided interfaces
	provides {
		interface Rubicon_Ack[uint8_t comp];
	}
}

implementation {
    //list of used components
	//components LedsC;
	components Rubicon_AckP;
	components TransportMD_P;
	
	//wiring
	Rubicon_Ack = Rubicon_AckP;
	//TransportMD_P.Leds->LedsC;
	Rubicon_AckP.TransportMD->TransportMD_P.TransportMD[RUBICON_ACK];
}
