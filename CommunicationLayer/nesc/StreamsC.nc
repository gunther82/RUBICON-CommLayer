/*
 *  StreamsC.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 05/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Configuration of the Communication layer for the Streams abstraction.
 */

configuration StreamsC {
	//list of provided interfaces
	provides {
		interface Streams;
	}
}

implementation {
	//list of used components
	components StreamsP;
	
	//wiring
	Streams = StreamsP;
}