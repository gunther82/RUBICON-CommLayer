/*
 *  CommControl.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 16/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Interface of the Control component of the Communication layer.
 */

interface CommControl {

	/**
	 * Start both radio and serial interface.
	 */
	command void init();
	
	
	/**
	 * Signal the completion of both radio and serial component activation.
	 *
	 * @param success Whether the radio and serial activation was successful.
	 */
	event void initDone(error_t success);
	
	
	/**
	 * Returns the status of the radio interface.
	 *
	 * @return TRUE if the radio interface is on, FALSE otherwise.
	 */
	command bool getRadioState();
	
	/**
	 * Returns the status of the serial interface.
	 *
	 * @return TRUE if the serial interface is on, FALSE otherwise.
	 */
	command bool getSerialState();
	
	
	/**
	 * Start the radio interface component.
	 */
	command void startRadio();


	/**
	 * Signal the completion of the radio component activation.
	 *
	 * @param success Whether the radio activation was successful.
	 */
	event void radioStarted(error_t success);


	/**
	 * Stop the radio interface component.
	 */
	command void stopRadio();


	/**
	 * Signal the completion of the radio component deactivation.
	 *
	 * @param success Whether the radio deactivation was successful.
	 */
	event void radioStopped(error_t success);


	/**
	 * Start the serial interface component.
	 */
	command void startSerial();


	/**
	 * Signal the completion of the serial component activation.
	 *
	 * @param success Whether the serial activation was successful.
	 */
	event void serialStarted(error_t success);


	/**
	 * Stop the serial interface component.
	 */
	command void stopSerial();


	/**
	 * Signal the completion of the serial component deactivation.
	 *
	 * @param success Whether the serial deactivation was successful.
	 */
	event void serialStopped(error_t success);
	
}
