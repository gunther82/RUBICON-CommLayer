/*
 *  StreamsP.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 05/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Implementation of the Communication layer for the Streams abstraction.
 */

includes streams;

module StreamsP {
	//list of provided interfaces
	provides {
		interface Streams;
	}
	
	//list of used interfaces
	
}

implementation {
	uint8_t stream_id = 0;	
	
		
	/********************************************************************************/
	/*																				*/
	/*					implementation of the Streams interface						*/
	/*																				*/
	/********************************************************************************/
	
	command error_t Streams.open_r(uint16_t dest, uint8_t symbolic_name, uint32_t stream_rate, uint16_t qos, uint8_t buffer_size) {

		return SUCCESS;
	}
	
	
	
	command stream_type Streams.open_s(uint8_t sensor_tid, uint32_t stream_rate, uint8_t sampling_type) {
		stream_type st;
		st.stream_id = 0;
		st.status = 0;
		
		return st;
	}
	
	
	command stream_type Streams.open_l(uint8_t buffer_size) {
		stream_type st;
		st.stream_id = 0;
		st.status = 0;
		
		return st;
	}
	
	
	command error_t Streams.close(stream_type stream_desc) {
		
		return FAIL;
	}
	
	
	command error_t Streams.read(stream_type stream_desc, uint8_t nbytes) {
		
		return FAIL;
	}
	
	
	
	command int8_t Streams.write(stream_type stream_desc, int8_t* buf, uint8_t nbytes) {
		
		return 0; 
	}
}