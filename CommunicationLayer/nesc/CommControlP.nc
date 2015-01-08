/*
 *  CommControlP.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 09/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Implementation of the Control component of the Communication layer.
 */

module CommControlP {
	//list of provided interfaces
	provides {
		interface CommControl;
	}
	
	//list of used interfaces
	uses interface Leds;
	uses interface SplitControl as RadioControl;
	uses interface SplitControl as SerialControl;
	
}

implementation {
	
	bool radioState = FALSE;
	bool serialState = FALSE;
	bool initCalled = FALSE;
	
	command void CommControl.init() {
		call RadioControl.start();
		call SerialControl.start();
		initCalled = TRUE;
	}
	
	command void CommControl.startRadio() {
		call RadioControl.start();
	}
	
	
	command void CommControl.stopRadio() {
		call RadioControl.stop();
	}

	
	command void CommControl.startSerial() {
		call SerialControl.start();
	}
	
	
	command void CommControl.stopSerial() {
		call SerialControl.stop();
	}
	
	command bool CommControl.getRadioState() {
		return radioState;
	}
	
	command bool CommControl.getSerialState() {
		return serialState;
	}
	
	event void RadioControl.startDone(error_t error){
		if(error == SUCCESS) {
			radioState = TRUE;
		}
		else {
			radioState = FALSE;
		}
		if(initCalled == FALSE)
			signal CommControl.radioStarted(error);
		else {
			if (serialState == TRUE) {
				signal CommControl.initDone(SUCCESS);
			}
		}

	}
	
	event void RadioControl.stopDone(error_t error){
		if(error == SUCCESS) {
			radioState = FALSE;
		}
		signal CommControl.radioStopped(error);
	}
	
	
	event void SerialControl.startDone(error_t error){
		if(error == SUCCESS) {
			serialState = TRUE;
		}
		else {
			serialState = FALSE;
		}
		if(initCalled == FALSE)
			signal CommControl.serialStarted(error);
		else {
			if (radioState == TRUE) {
				signal CommControl.initDone(SUCCESS);
			}
		}
	}
	
	event void SerialControl.stopDone(error_t error){
		if(error == SUCCESS) {
			serialState = FALSE;
		}
		signal CommControl.serialStopped(error);
	}
	
}