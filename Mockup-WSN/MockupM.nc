/*
 *  MockupM.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 12/03/12.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Implementation file for the WSN-side version of the Mockup Communication Layer of RUBICON.
 */

//includes mockup;
//#include "mockup.h"
//#include "definitions.h"

module MockupM {
	
	//list of used interfaces
	uses interface Boot;
	uses interface Leds;
	
	//Communication Layer interfaces
	//uses interface Streams;
	//uses interface Synaptic_Channels as Synaptic;
#ifdef LEARNING_COMP
	uses interface Learning;
#endif
#ifdef CONTROL_COMP
    uses interface Control;
#endif
	uses interface Connectionless;
	uses interface ComponentManagement;
	uses interface Random;
	uses interface LogRead;
	uses interface LogWrite;
	
#ifdef SS
	uses interface SSToApp;
#endif
	
	//Timers
	uses interface Timer<TMilli> as TimerReply;
	uses interface Timer<TMilli> as TimerPeriodicRead;
	uses interface Timer<TMilli> as TimerDemo;
	
	//Serial
	uses interface SplitControl as SerialControl;
	uses interface Packet as SerialPacket;
	
	uses interface AMSend as SerialDataSend;
	uses interface Receive as SerialCommandReceive;
	
	//Radio
	uses interface SplitControl as RadioControl;
	uses interface Packet as RadioPacket;
	
	uses interface AMSend as TempSend;
	uses interface AMSend as HumidSend;
	uses interface AMSend as LightSend;
	uses interface AMSend as RSSISend;
	uses interface AMSend as DataSend;
	
	//Read interfaces
	uses interface Read<uint16_t> as Temp;
	uses interface Read<uint16_t> as Humidity;
	uses interface Read<uint16_t> as Light;
    uses interface Read<uint16_t> as AccelX;
	uses interface Read<uint16_t> as AccelY;
	uses interface Read<uint16_t> as Pir;
	uses interface Read<uint16_t> as Mic;
	uses interface Read<uint16_t> as Magnetic;
	
//	#ifdef __CC2420_H__
	#if defined(PLATFORM_MICAZ) || defined(PLATFORM_TELOSB) || defined(PLATFORM_XM1000)
		uses interface CC2420Packet;
	#elif defined(PLATFORM_IRIS)
		uses interface PacketField<uint8_t> as PacketRSSI;
	#endif
}

implementation {
	typedef nx_struct logentry_t {
        nx_uint16_t counter;
        nx_uint16_t temp;
        nx_uint16_t humid;
        nx_uint16_t light;
    } logentry_t;
    
    
	bool serialState;
	bool serialBusy;
	
	bool radioState;
	bool radioBusy;
	
	//outgoing messages
	message_t serialPkt;
	
//	//ingoing messages data structures
//	message_t radioBuf[RADIO_BUFF_SIZE];
//	message_t *radioBufPos[RADIO_BUFF_SIZE];
//	uint8_t nextfree;
//	uint8_t current;
	
	//received messages pointers
	netid_cmd_msg_t *netidcmd;
	
	uint16_t temp;	
	uint16_t humid;
	uint16_t light;
	uint16_t rssi;
    uint16_t accelx;
    uint16_t accely;
    uint16_t pir;
    uint16_t mic;
    uint16_t magnetic;
	
	uint16_t recCounter;
	
	uint8_t destIsland;
	uint16_t destMote;
	
	uint8_t modality;
	uint16_t delay;
	uint16_t periodicCounter;
	
    uint16_t bitmask_updates;
	
	//DEMO VARIABLES
	uint16_t demoSensorBitmask;
	r_addr_t src;
	
	//logging variables
	bool m_busy;
	logentry_t m_entry;
	
	data_msg dataMsg;
	
	//mote description
	mote_desc_t moteDesc;
	
    uint16_t islandID;
    uint16_t moteID;
    
    task void sendUpdates();
    
	event void Boot.booted() {
//		uint8_t i;
		
		//call SerialControl.start();
		//call RadioControl.start();
		call ComponentManagement.initCommunications();
		
//		//buffer variables
//		for(i = 0; i < RADIO_BUFF_SIZE; i++) {
//			radioBufPos[i] = &radioBuf[i];
//		}
//		nextfree = 0;
//		current = 0;
		
		m_busy = FALSE;
        serialBusy = FALSE;
		
		//initialization of the description of the mote according to what is configured in the .h file
		moteDesc.mote_type = mote_type;
		moteDesc.transducers = transducers;
		moteDesc.actuators = actuators;
		
		//call LogWrite.erase();
		
		//call LogRead.seek(SEEK_BEGINNING-20);
		
		//if (call LogRead.currentOffset() == SEEK_BEGINNING) {
#ifdef SENSORBOARD_EM1000
        //call Leds.led1On();
#endif
//		}
	}
	
	
//#ifdef __CC2420_H__
#if defined(PLATFORM_MICAZ) || defined(PLATFORM_TELOSB) || defined(PLATFORM_XM1000)
	uint16_t getRssi(message_t *msg){
		return (uint16_t) call CC2420Packet.getRssi(msg)+50;
	}
#elif defined(PLATFORM_IRIS)
	uint16_t getRssi(message_t *msg){
		if(call PacketRSSI.isSet(msg))
			return (uint16_t) call PacketRSSI.get(msg);
		else
			return 0xFFFF;
	}
#else
	uint16_t getRssi(message_t *msg){
		return 0;
	}
#endif
	
	
//	//updates the buffer for the incoming messages
//	message_t* updateBuffer(message_t *msg) {
//		message_t *ret;
//		atomic {
//			ret = radioBufPos[nextfree];
//			radioBufPos[nextfree] = msg;
//			nextfree = (nextfree + 1) % RADIO_BUFF_SIZE;
//		}
//		return ret;
//	}
	
	//read the next entry in the flash
	task void readLog() {
		if (call LogRead.read(&m_entry, sizeof(logentry_t)) != SUCCESS) {
			// Handle error.
		}
#ifdef LOGGER_LEDS
        call Leds.led2Toggle();
#endif
	}

	//sends over the serial the last read entry from the flash and read the next one, if any
	event void LogRead.readDone(void* buf, storage_len_t len, error_t error) {
		serial_data_msg *serialDataMsg;
		
		if((len == sizeof(logentry_t)) && (buf == &m_entry) ) {
			//call Send.send(&m_entry.msg, m_entry.len);
			serialDataMsg = (serial_data_msg*)(call SerialPacket.getPayload(&serialPkt, sizeof(serial_data_msg)));
			
			serialDataMsg->src_mote_addr = TOS_NODE_ID;
			serialDataMsg->counter = m_entry.counter;
			serialDataMsg->temp = m_entry.temp;
			serialDataMsg->humid = m_entry.humid;
			serialDataMsg->light = m_entry.light;
			
			if((serialState) && (!serialBusy)) {
				if(call SerialDataSend.send(AM_BROADCAST_ADDR, &serialPkt, sizeof(serial_data_msg)) == SUCCESS) {
					serialBusy = TRUE;
				}
			}
#ifdef LOGGER_LEDS
            call Leds.led1On();
#endif
		}
		else {
			if (call LogWrite.erase() != SUCCESS) {
				// Handle error.
			}
#ifdef LOGGER_LEDS
            call Leds.led0On();
#endif
		}
	}
	
	
	//task for the READ_DATA command -> it performs a single read, for periodic reads see TimerPeriodicRead.fired()
	task void readData() {
		uint16_t randDelay;
		
		call Temp.read();
		call Humidity.read();
		call Light.read();
		
		if (modality == FIXED) {
			call TimerReply.startOneShot(delay*(TOS_NODE_ID));
		}
		else if (modality == RANDOM) {
			randDelay = (call Random.rand16() % delay);
			call TimerReply.startOneShot(randDelay);
		}
	}
	
	//to avoid collisions the motes wait for a timer before sending a reply to a read request
	event void TimerReply.fired() {
        r_addr_t dest;
        
		if (!m_busy) {
			m_busy = TRUE;
			m_entry.counter = recCounter;
			m_entry.temp = temp;
			m_entry.humid = humid;
			m_entry.light = light;
			if (call LogWrite.append(&m_entry, sizeof(logentry_t)) != SUCCESS) {
				m_busy = FALSE;
			}
		}
		
		dataMsg.counter = recCounter;
		dataMsg.temp = temp;
		dataMsg.humid = humid;
		dataMsg.light = light;
        
        //prepare the destination
        dest.pid = destIsland;
        dest.devid = destMote;
        
        //TO UPDATE: check the seq num and the AM_TYPE  -> 0, AM_DATA_MSG
		//we send the dataMsg minus the last two bytes of the rssi field that is not sent in this case
		call Connectionless.send(dest, (int8_t*)&dataMsg, (sizeof(data_msg) - 2), FALSE, MOCKUP);
	}
	
	//timer for READ_PERIODICALLY command, we send the last readings and we call the next ones
	event void TimerPeriodicRead.fired() {
        /************* OLD CODE *****************/
//        r_addr_t dest;
//        
//		dataMsg.counter = periodicCounter;
//		dataMsg.temp = temp;
//		dataMsg.humid = humid;
//		dataMsg.light = light;
//        
//        //prepare the destination
//        dest.pid = ISLAND_ADDR;
//        dest.devid = SINK;
//        
//        //TO UPDATE: check the seq num and the AM_TYPE  -> 0, AM_DATA_MSG
//		//we send the dataMsg minus the last two bytes of the rssi field that is not sent in this case
//		call Connectionless.send(dest, (int8_t*)&dataMsg, (sizeof(data_msg) - 2), FALSE, MOCKUP);
//		
//		call Temp.read();
//		call Humidity.read();
//		call Light.read();
//		periodicCounter++;
        /************* OLD CODE *****************/
        
        post sendUpdates();
	}
	
    
	task void handleNetIdCmd() {
		
	}
	
	//the only case where a WSN mote can receive a message from the serial is for downloading the log from the flash
	event message_t* SerialCommandReceive.receive(message_t* msg, void* payload, uint8_t len) {
		command_msg *comm = (command_msg*)payload;
		
		if (comm->type == READ_LOG) {
#ifdef LOGGER_LEDS
            call Leds.led1Toggle();
#endif
			post readLog();
		}
		
		return msg;
	}	
	
//	//reception of a message from the Connectionless interface
//	event void Connectionless.receive(uint8_t src_island_addr, uint16_t src_mote_addr, int8_t* buf, uint8_t nbytes, uint8_t am_type) {
//		command_msg *recCommand;
//		destIsland = src_island_addr;
//		destMote = src_mote_addr;
//		
//		switch (am_type) {
//			case AM_COMMAND_MSG://sensor receiving a command
//				recCommand = (command_msg*)buf;
//				recCounter = recCommand->counter;
//				delay = recCommand->delay; //in case of READ_PERIODICALLY delay is the sampling rate
//				switch (recCommand->type) {
//					case READ_DATA:
//						modality = FIXED;
//						post readData();
//						break;
//					case READ_DATA_RANDOM_WAIT:
//						modality = RANDOM;
//						post readData();
//						break;
//					case READ_PERIODICALLY:
//						modality = PERIODICALLY;
//						periodicCounter = recCommand->counter;
//						call Temp.read();
//						call Humidity.read();
//						call Light.read();
//						call TimerPeriodicRead.startPeriodic(recCommand->delay); //in case of READ_PERIODICALLY delay is the sampling rate
//						break;
//					case DEMO_SYN:
//						call Learning.start();
//						break;
//				}
//				break;
//				
//			case AM_NETID_CMD_MSG:
//				netidcmd = (netid_cmd_msg_t*)buf;
//				post handleNetIdCmd();
//				break;
//
//		}
//	}
    
	task void sendDemo() {
		//instantiate the app_msg_t
		app_msg_t amsg;
		//instantiate and fill the application msg to be sent
		demo_data_msg_t data;
		data.msg_type = AM_DEMO_DATA_MSG;
		data.light = light;
		data.temp = temp;
		//copy the application msg into the payload filed of the app_msg_t
		memcpy(&(amsg.payload), &data, sizeof(demo_data_msg_t));
		
#ifdef MOCKUP_LEDS
        //call Leds.led2Toggle();
#endif
		
		call Connectionless.send(src, (int8_t*)&amsg, sizeof(demo_data_msg_t), NOT_ACK, MOCKUP);
	}
	
	void analyzeBitmask(uint16_t bitmask) {
		if (bitmask & LIGHT) {
			call Light.read();
		}
		if (bitmask & TEMP) {
			call Temp.read();
		}
		if (bitmask & HUMID) {
			call Humidity.read();
		}
        if (bitmask & ACCEL_X) {
			call AccelX.read();
		}
        if (bitmask & ACCEL_Y) {
			call AccelY.read();
		}
        if (bitmask & PIR) {
			call Pir.read();
		}
        if (bitmask & MIC) {
			call Mic.read();
		}
        if (bitmask & MAGNETIC) {
			call Magnetic.read();
		}
    }
    
    
	
	event void TimerDemo.fired() {
		analyzeBitmask(demoSensorBitmask);
		post sendDemo();
	}
    
    
    
    
    
    
    
    task void sendUpdates() {
        app_msg_t app_updates;
        //updates_msg_t updates;
        uint8_t len;
        r_addr_t dest;
        updates_msg_t *updates = (updates_msg_t*)(app_updates.payload);
        
        updates->type = AM_UPDATES_MSG;
        updates->output_size = 3; //this is the number of the couples <sensor,value> contained in the msg
        updates->updates[0] = LIGHT;
        updates->updates[1] = light;
        updates->updates[2] = TEMP;
        updates->updates[3] = temp;
        updates->updates[4] = HUMID;
        updates->updates[5] = humid;
        
        
//        updates.type = AM_UPDATES_MSG;
//        updates.output_size = 3; //this is the number of the couples <sensor,value> contained in the msg
//        updates.updates[0] = LIGHT;
//        updates.updates[1] = light;
//        updates.updates[2] = TEMP;
//        updates.updates[3] = temp;
//        updates.updates[4] = HUMID;
//        updates.updates[5] = humid;
        
        dest.pid = LOCAL_ISLAND;
        dest.devid = NULL_MOTE;
        
        len = 2 + (4*updates->output_size); //2 bytes for type and output_size fields + 2 bytes for each couple <sensor,value>

//        len = 2 + (4*updates.output_size); //2 bytes for type and output_size fields + 2 bytes for each couple <sensor,value>
//        memcpy(&(app_updates.payload), &updates, len);
        
        call Leds.led2Toggle();
        call Connectionless.send(dest, (int8_t*)&app_updates, len, NOT_ACK, CONTROL_LAYER);
        
        //recall reads of the transducers
        analyzeBitmask(bitmask_updates);
    }
    
	
    
    //reception of a message from the Connectionless interface
	event void Connectionless.receive(r_addr_t sender, int8_t* payload, uint8_t nbytes) {
		uint8_t msg_type = *payload;
		demo_msg_t* demo_msg;
		
		switch (msg_type) {
			case 1: //DEMO_MSG
				demo_msg = (demo_msg_t*)payload;
				
				call TimerDemo.startPeriodic(demo_msg->period);
				demoSensorBitmask = demo_msg->sensorsBitmask;
				src = sender;
				break;
				
            case AM_READ_UPDATES_MSG:
                //store the bitmask received in a global variable
                bitmask_updates = 0xFFFF & (LIGHT+TEMP+HUMID);
                //call reads for the requested transducers
                analyzeBitmask(bitmask_updates);
                break;
			default:
				break;
		}
		
		
//		command_msg *recCommand;
//        uint8_t msg_type = *payload;
//		destIsland = sender.pid;
//		destMote = sender.devid;
//        
//        
//		switch (msg_type) {
//			case AM_COMMAND_MSG://sensor receiving a command
//				recCommand = (command_msg*)payload;
//				recCounter = recCommand->counter;
//				delay = recCommand->delay; //in case of READ_PERIODICALLY delay is the sampling rate
//				switch (recCommand->type) {
//					case READ_DATA:
//						modality = FIXED;
//						post readData();
//						break;
//					case READ_DATA_RANDOM_WAIT:
//						modality = RANDOM;
//						post readData();
//						break;
//					case READ_PERIODICALLY:
//						modality = PERIODICALLY;
//						periodicCounter = recCommand->counter;
//						call Temp.read();
//						call Humidity.read();
//						call Light.read();
//						call TimerPeriodicRead.startPeriodic(recCommand->delay); //in case of READ_PERIODICALLY delay is the sampling rate
//						break;
//					case DEMO_SYN:
//						call Learning.start();
//						break;
//				}
//				break;
//				
//			case AM_NETID_CMD_MSG:
//				netidcmd = (netid_cmd_msg_t*)payload;
//				post handleNetIdCmd();
//				break;
//                
//		}
	}
    
	
	//for TELOSB: raw * 0.01 - 40 -> Celsius
	event void Temp.readDone(error_t result, uint16_t data) 
	{
		if (result == SUCCESS){
			temp = data;
		}
	}
	
	event void Humidity.readDone(error_t result, uint16_t data) 
	{
		if (result == SUCCESS){
			humid = data;
		}
	}
	
	event void Light.readDone(error_t result, uint16_t data) 
	{
		if (result == SUCCESS){
			light = data;
		}
	}
	
    event void AccelX.readDone(error_t result, uint16_t data)
	{
		if (result == SUCCESS){
			accelx = data;
		}
	}
    
    event void AccelY.readDone(error_t result, uint16_t data)
	{
		if (result == SUCCESS){
			accely = data;
		}
	}
    
    event void Pir.readDone(error_t result, uint16_t data)
	{
		if (result == SUCCESS){
			pir = data;
		}
	}
    
    event void Mic.readDone(error_t result, uint16_t data)
	{
		if (result == SUCCESS){
			mic = data;
		}
	}
    
    event void Magnetic.readDone(error_t result, uint16_t data)
	{
		if (result == SUCCESS){
			magnetic = data;
		}
	}
    
	event void SerialControl.stopDone(error_t error){
		serialState = FALSE;
	}
	
	event void SerialControl.startDone(error_t error){
		if(error == SUCCESS) {		
			serialState = TRUE;
		}
		else {
			call SerialControl.start();
			serialState = FALSE;
		}
	}
	
	
	event void RadioControl.stopDone(error_t error){
		if(error == SUCCESS) {
			radioState = FALSE;
		}
	}
	
	event void RadioControl.startDone(error_t error){
		if(error == SUCCESS) {
			//rssimsg = (rssi_msg*) (call RadioPacket.getPayload(&radioPkt, sizeof(rssi_msg)));
			radioState = TRUE;
		}
		else {
			radioState = FALSE;
			call RadioControl.start();
		}
	}
	
	//to download the flash content to base station
	event void SerialDataSend.sendDone(message_t *msg, error_t error){
		serialBusy = FALSE;
		
		if ((error == SUCCESS) && (msg == &serialPkt)) {
			call SerialPacket.clear(&serialPkt);
			if (call LogRead.read(&m_entry, sizeof(logentry_t)) != SUCCESS) {
				// Handle error.
			}
		}
	}
	
	event void TempSend.sendDone(message_t *msg, error_t error){
			radioBusy = FALSE;
	}
	event void HumidSend.sendDone(message_t *msg, error_t error){
			radioBusy = FALSE;
	}
	event void LightSend.sendDone(message_t *msg, error_t error){
			radioBusy = FALSE;
	}
	event void RSSISend.sendDone(message_t *msg, error_t error){
			radioBusy = FALSE;
	}
	
	event void DataSend.sendDone(message_t *msg, error_t error){
			radioBusy = FALSE;
	}
	
	
	event void ComponentManagement.initCommunicationsDone(error_t success) {
		//TO UPDATE: this shoul be in a cycle until we are joined
		call ComponentManagement.joinIsland(&moteDesc);
	}
    
    void signalError() {
        call Leds.led0On();
        call Leds.led1On();
        call Leds.led2On();
    }
	
    //mote receiving this event: island_addr and mote_addr are the assigned addresses
    event void ComponentManagement.joined(uint16_t island_addr, uint16_t mote_addr, error_t success) {
        if (success == SUCCESS) {
#ifdef MOCKUP_LEDS
            call Leds.led1On();
#endif
            islandID = island_addr;
            moteID = mote_addr;
      
            
#if defined(AUTO_MAURO) && defined(CONTROL_COMP)			
            call Control.init();
            
//            call TimerPeriodicRead.startPeriodic(500);
//            
//            //TO UPODATE: remove this code when implemented the reception of the command AM_READ_UPDATES_MSG
//            //store the bitmask received in a global variable
//            bitmask_updates = 0xFFFF & (LIGHT+TEMP+HUMID);
//            //call reads for the requested transducers
//            analyzeBitmask(bitmask_updates);
#endif
#ifdef LEARNING_COMP
			call Learning.start();
#endif
        }
        else {
            signalError();
        }
    }
    
    event void ComponentManagement.moteJoined(uint16_t island_addr, uint16_t mote_addr, mote_desc_t* description) {
        
	}
    
	event void LogWrite.appendDone(void* buf, storage_len_t len, bool recordsLost, error_t error){
		m_busy = FALSE;
	}
	event void LogWrite.eraseDone(error_t error) {
		
	}
	event void LogWrite.syncDone(error_t error) {
		
	}
	event void LogRead.seekDone(error_t error) {
		
	}
	
	
#ifdef SS
	event error_t SSToApp.openRemoteDone(stream_type stream_desc, uint8_t symbolic_name, error_t success) {
		return FAIL;
	}
	
	event error_t SSToApp.readDone(stream_type stream_desc, int8_t* buf, uint8_t nbytes, error_t success) {
		return FAIL;
	}
#endif
#ifdef LEARNING_COMP
	event void Learning.demo_data(uint16_t demo_data) {}
#endif
}