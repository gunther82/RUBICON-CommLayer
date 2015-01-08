/*
 *  Streams.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 01/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Interface of the Communication layer for the Streams abstraction.
 */

includes streams;

interface Streams {
	
	/**
	 * Start the Stream Component.
	 */
	command void init();
	
	/**
	 * Request that a remote stream is opened.
	 *
	 * @param island_addr (input) The address of the island destionation of the message.
	 *
	 * @param mote_addr (input) Destination of stream (if open_r is invoked by destination,
	 * dest is the node itself).
	 *
	 * @param symbolic_name (input) Code univocally identifying the stream to be opened.
	 *
	 * @param stream_rate (input) Rate at which source (destination) of the stream
	 * is going to write (read) data into (from) the stream. It is expressed in milliseconds.
	 *
	 * @param qos (input) Quality of service of the stream.
	 *
	 * @return FAIL if opening the stream is impossible because, for
	 * example, Stream System layer data structures have no space to store a new
	 * stream or the stream is already opened.
	 * SUCCESS means a openRemoteDone() event will be signaled once the stream has been opened.
	 */
	
	command error_t open_remote(uint8_t island_addr, uint16_t mote_addr, uint8_t symbolic_name, uint32_t stream_rate, uint16_t qos);
	
	
	
	/**
	 * Signal that the remote stream has been opened.
	 *
	 * @param desc (output) Descriptor of the opened stream.
	 *
	 * @param symbolic_name (output) Code univocally identifying the opened stream.
	 *
	 * @param success (output) Whether the open_r was successful.
	 */
	event void openRemoteDone(stream_desc desc, uint8_t symbolic_name, error_t success);
	
	
	
	/**
	 * Open a sensor stream.
	 *
	 * @param sensor_tid (input) Identifier of the transducer whose sensor stream has to be opened.
	 *
	 * @param stream_rate (input) Rate at which sensor must sample environment. It is expressed in milliseconds.
	 *
	 * @param sampling_type (input) If sampling is periodic or on demand.
	 *
	 * @return stream descriptor if opening has been completed successfully;
	 * -1 otherwise (for example no available space to store a new stream or the stream is already opened)
	 */
	command stream_desc open_sensor(uint8_t sensor_tid, uint32_t stream_rate, uint8_t sampling_type);
	
	
	
	/**
	 * Open a local stream.
	 *
	 * @return stream descriptor if opening has been completed successfully;
	 * -1 otherwise (for example no available space to store a new stream or the stream is already opened)
	 */
	command stream_desc open_local();
	
	
	
	/**
	 * Close the selected stream.
	 *
	 * @param desc Descriptor of the stream to be closed.
	 *
	 * @return FAIL if the operation can not be completed; SUCCESS otherwise.
	 *
	 */
	command error_t close(stream_desc desc);
	
	
	
	/**
	 * Request that nbytes bytes are read from the selected stream.
	 *
	 * @param desc Descriptor of the stream to be read.
	 *
	 * @param nbytes Number of bytes to be read.
	 *
	 * @return FAIL if the stream is not open or the read can not be performed
	 * (for example, attempt to read from a remote stream opened in write mode).
	 *
	 */
	command error_t read(stream_desc desc, uint8_t nbytes);
	
	
	
	/**
	 * Signal that the data is ready.
	 *
	 * @param desc Descriptor of the stream.
	 *
	 * @param buf Pointer to a region where read data was put; meaningful only
	 * if success = SUCCESS.
	 *
	 * @param nbytes Number of actually read bytes; meaningful only if success
	 * = SUCCESS.
	 *
	 * @param success Whether the read was successful.
	 */
	event void readDone(stream_desc desc, int8_t* buf, uint8_t nbytes, error_t success);
	
	
	
	/**
	 * Write the input buffer into the selected stream.
	 *
	 * @param desc Descriptor of the stream to be written.
	 *
	 * @param buf Pointer to the buffer to be written.
	 *
	 * @param nbytes Number of bytes to be written in the stream.
	 *
	 * @return the number of actually written bytes; -1 if the write fails.
	 *
	 */
	command int8_t write(stream_desc desc, int8_t* buf, uint8_t nbytes);
	
}
