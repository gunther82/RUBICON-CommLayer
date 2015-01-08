/*
 *  CommControlC.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 16/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Configuration of the Control component of the Communication layer.
 */

configuration CommControlC {
	//list of provided interfaces
	provides {
		interface CommControl;
	}
}

implementation {
	//list of used components
	components CommControlP;
	components LedsC;
	
	components SerialActiveMessageC as Serial;
	components ActiveMessageC as Radio;
	
	//wiring
	CommControl = CommControlP;
	CommControlP.Leds->LedsC;
	
	CommControlP.SerialControl->Serial;
	CommControlP.RadioControl->Radio;
}