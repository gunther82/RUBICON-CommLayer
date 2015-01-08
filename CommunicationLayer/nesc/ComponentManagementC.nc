/*
 *  ComponentManagementC.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 09/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Configuration of the Component Management of the Communication layer.
 */

#include "network.h"

configuration ComponentManagementC {
	//list of provided interfaces
	provides {
		interface ComponentManagement;
	}
}

implementation {
	//list of used components
	components ComponentManagementP;
	components CommControlC;
	components NetworkC;
	components LedsC;
	
	//wiring
	ComponentManagement = ComponentManagementP;
	ComponentManagementP.CommControl->CommControlC;
	ComponentManagementP.Network->NetworkC.Network[COMP_MNGMNT];
	ComponentManagementP.Leds->LedsC.Leds;
}