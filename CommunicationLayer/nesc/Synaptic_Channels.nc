/*
 *  Synaptic_Channels.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 01/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Interface of the Communication Layer for the Synaptic Channel abstraction.
 */

//#include "synaptic_channels.h"
#include "definitions.h"

interface Synaptic_Channels {
	/**
	 * Initialize the Synaptic component.
	 *
	 */
	command void init();
	
	
	/**
	 * Setup of a Synaptic Channel between local node and a specified destination node. 
	 * If the Synaptic Channel already exist, the size of the channel is increased by size parameter. 
	 *
	 * @param dest (input) Mote address of destination end of the Synaptic Channel.
	 *
	 * @param size (input) Number of neuron readings whose values 
	 * have to be transmitted to dest (i.e. the channel size).
	 *
	 * @param params[] (input) Channel parameters like type, QoS.
	 *
	 * @return FAIL if creating the channel is impossible because, for
	 * example, there is no more available space to store a new
	 * channel.
	 * SUCCESS means a createSynChannelOutDone() event will be signaled once the Synaptic Channel has been opened.
	 */
	command error_t create_syn_channel_out(uint16_t dest, uint8_t size, int8_t params[DIM_SYNCHANNEL_PARAM]);
	
	
	/**
	 * Signal the outcome of the output Synaptic Channel creation request.
	 *
	 * @param channel_id (output) Channel ID of the opened Synaptic Channel.
     *
     * @param firstChIndex (output) The position of the first synaptic connection (chIndex) to be filled by the Learing Layer.
	 *
	 * @param success (output) Whether the Synaptic Channel creation was successful.
	 */
	event void createSynChannelOutDone(syn_ch_id_t channel_id, uint8_t firstChIndex, error_t success);
	
	
	/**
	 * Setup of a Synaptic Channel between a remote source node and the local node. 
	 *
	 * @param src (input) Mote address of the source end of the Synaptic Channel.
	 *
	 * @param size (input) Number of neuron readings (in src) whose values 
	 * have to be received (i.e. the channel size).
	 *
	 * @param params (input) Channel parameters like type, QoS.
	 *
	 * @return FAIL if creating the channel is impossible because, for
	 * example, there is no more available space to store a new
	 * channel.
	 * SUCCESS means a createSynChannelInDone() event will be signaled once the Synaptic Channel has been opened.
	 */
	command error_t create_syn_channel_in(uint16_t src, uint8_t size, int8_t params[DIM_SYNCHANNEL_PARAM]);
	
	
	/**
	 * Signal the outcome of the input Synaptic Channel creation request.
	 *
	 * @param channel_id (output) Channel ID of the opened Synaptic Channel.
     *
     * @param firstChIndex (output) The position of the first synaptic connection (chIndex) to be filled by the Learing Layer.
	 *
	 * @param success (output) Whether the Synaptic Channel creation was successful.
	 */
	event void createSynChannelInDone(syn_ch_id_t channel_id, uint8_t firstChIndex, error_t success);
	
	
	/**
	 * Closes the specified Synaptic Channel that resides in the node where the function is invoked. 
	 * If the channel is still active for transmission, it stops the synaptic communications first and then destroys the channel.  
	 *
	 * @param channel (input) The ID of the Synaptic Channel to be closed.
	 *
	 * @return FAIL if the operation can not be completed; SUCCESS otherwise.
	 *
	 */
	command error_t dispose_syn_channel(syn_ch_id_t channel_id);
	
	
	/**
	 * Starts transmitting the stream of neuron output vectors on the specified channel. 
	 * A channel that is activated with this primitive becomes an active synaptic channel.  
	 *
	 * @param channel_id (input) The ID of the Synaptic Channel whose stream of neuron output has to be started.
	 *
	 * @return FAIL if the operation can not be completed (for example the channel does not exists); SUCCESS otherwise.
	 *
	 */
	command error_t start_syn_channel(syn_ch_id_t channel_id);
	
	
	/**
	 * Stops transmitting the stream of neuron output vectors over the specified active synaptic channel.
	 *
	 * @param channel (input) The ID of the Synaptic Channel whose stream of neuron output has to be stopped.
	 *
	 * @return FAIL if the operation can not be completed (for example the channel is not active, or does not exists); 
	 * SUCCESS otherwise.
	 *
	 */
	command error_t stop_syn_channel(syn_ch_id_t channel_id);
	
	
	/**
	 * Starts the execution of all the synaptic channels opened (i.e. whose status is CREATED) on the node. 
	 *
	 * @return FAIL if the operation can not be completed (for example there is no CREATED synaptic channel); SUCCESS otherwise.
	 *
	 */
	command error_t start_all_syn_channels();
	
	
	/**
	 * Stops the execution of all the ACTIVE synaptic channels opened on the node. 
	 */
	command void stop_all_syn_channels();
	
	/**
	 * Fills the ChOut vector associated to the specified Synaptic Channel with the output data to be sent. 
	 *
	 * @param channel_id The ID of the Synaptic Channel that uniquely identifies 
	 * a ChOut vector structure in the output interface of the local mote.
	 *
	 * @param size Number of elements to be written in ChOut.
	 *
	 * @param output_data Pointer to the array of values to be written into the ChOut vector.
	 *
	 * @return SUCCESS if the write operation is completed successfully, FAIL otherwise.
	 *
	 */
	command error_t write_output(syn_ch_id_t channel_id, uint8_t size, syn_data_t* output_data);
	
	/**
	 * Write the specified value in the specified position of the ChOut vector associated to the 
	 * specified Synaptic Channel. 
	 *
	 * @param channel_id The ID of the Synaptic Channel that uniquely identifies 
	 * a ChOut vector structure in the output interface of the local mote.
	 *
	 * @param pos Position of the ChOut vector to be written.
	 *
	 * @param output_data value to be written in the specified position of the ChOut vector.
	 *
	 * @return SUCCESS if the write operation is completed successfully, FAIL otherwise.
	 *
	 */
	command error_t write_outputValue(syn_ch_id_t channel_id, uint8_t pos, syn_data_t output_data);
	
	
	/**
	 * Notifies the status update of an input synaptic connection.
	 *
	 * @param channel (output) The ID of the Synaptic Channel that uniquely identifies 
	 * a ChIn vector structure in the input interface of the local mote.
	 *
	 * @param index (output) The index of the ChIn element whose status update has to be notified.
	 *
	 * @param status (output) The status of the synaptic connection. 
	 * UPDATED means that the remote neuron reading has been successfully updated, MISS means a missing remote neuron reading.
	 */
	//event void syn_input_update(syn_ch_id_t channel_id, uint8_t index, synStatus status);
	
	
	/**
	 *	Signal the reception of synaptic data from a synaptic channel.
	 *
	 *	@param channel_id The ID of the Synaptic Channel from which the data has been received.
	 *
	 *  @param status Information about the received data (for example: new data, not_changed data, etc) 
	 */
	event void synData_received(syn_ch_id_t channel_id, uint8_t status);
	
	
	/**
	 * Read the data stored into the specified ChIn vector of the input interface of the ESN running on the local mote. 
	 *
	 * @param channel_id The ID of the Synaptic Channel whose ChIn vector has to be read.
	 *
	 * @return the pointer to the data field of the requested input Synaptic Channel
	 */
	command syn_data_t* read_input(syn_ch_id_t channel_id);
	//command int16_t* read_input(syn_ch_id_t channel_id);

	
	/**
	 * Read the data stored into the specified position of ChIn vector of the specified input Synaptic Channel. 
	 *
	 * @param channel_id The ID of the Synaptic Channel whose ChIn vector has to be read.
	 *
	 * @param pos Position of ChIn vecto where to read the data.
	 *
	 * @return the value of the specified position in the input Synaptic Channel data buffer
	 */
	command syn_data_t read_inputValue(syn_ch_id_t channel_id, uint8_t pos);
	
	/**
	 * Signals the tick of the current distributed RUBICON clock T.
	 *
	 * @param currentClock The current value of the RUBICON clock.
	 *
	 */
	//event void clock_tick(uint8_t currentClock);
	
	
	/**
	 * Set the RUBICON clock, that is the frequency at which the forward computation is executed, 
	 * i.e. the output synaptic data are transmitted
	 * to the input buffer of a ESN through the synaptic channel.
	 * THIS COMMAND MUST BE CALLED BEFORE STARTING THE NEURAL COMPUTATION.
	 * 
	 * @param clock The new RUBICON clock.
	 */
	
	command void setTimeStep(uint16_t clock);
}
