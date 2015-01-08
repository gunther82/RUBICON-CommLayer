/*
 *  MockupC.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 27/07/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Configuration file for the Mockup Communication Layer of RUBICON.
 */

//includes mockup;
#include "definitions.h"
#include "mockup.h"
#include "network.h"


configuration MockupC {
	
}

implementation {
	//list of used components
	components MainC;
	
	//Communiction Layer components
	components MockupM as app, LedsC;
	//components StreamsP;
	//components SynChannelsC;
	components ConnectionlessC;
	components ComponentManagementC;
	components LearningC;
	
	
#ifdef SS
	components SSToAppC;
	app.SSToApp-> SSToAppC;
#endif	
	
	//Timers
	
	//Serial
	components SerialActiveMessageC as Serial;

	components new SerialAMReceiverC(AM_LEARNING_MODULE_CMD_MSG) as LearnModuleCmdReceive;
    components new SerialAMReceiverC(AM_LEARNING_MODULE_FLOAT_CMD_MSG) as LearnModuleFloatCmdReceive;
	
	
	//Radio
	components ActiveMessageC as Radio;
	
	//RSSI
//#ifdef __CC2420_H__
#if defined(PLATFORM_MICAZ) || defined(PLATFORM_TELOSB) || defined(PLATFORM_XM1000)
	components CC2420ActiveMessageC;
	app.CC2420Packet -> CC2420ActiveMessageC.CC2420Packet;
#elif  defined(PLATFORM_IRIS)
	components  RF230ActiveMessageC;
	app.PacketRSSI -> RF230ActiveMessageC.PacketRSSI;
#endif
	
	//Read components
//#if defined(PLATFORM_TELOSB)
//	components new SensirionSht11C() as Temp;
//	app.Temp->Temp.Temperature;
//	components new SensirionSht11C() as Humidity; 
//	app.Humidity->Humidity.Humidity;
//	components new HamamatsuS1087ParC() as Light;
//	app.Light -> Light;
//#elif defined(PLATFORM_IRIS) || defined(PLATFORM_MICAZ)
//	components new SensorMts300C() as Temp;
//	app.Temp->Temp.Temp;
//	components new SensorMts300C() as Light;
//	app.Light -> Light.Light;
//	components new DemoSensorC() as Humidity;
//	app.Humidity->Humidity;
//#else
//	components new DemoSensorC() as Temp;
//	app.Temp->Temp;
//	components new DemoSensorC() as Light;
//	app.Light -> Light;
//	components new DemoSensorC() as Humidity;
//	app.Humidity->Humidity;
//#endif
	
	
	//wiring of the components 
	app.Boot -> MainC.Boot;
	app.Leds->LedsC;
	
	//Communication Layer interfaces binding
	//app.Streams -> StreamsP.Streams;
	//app.Synaptic -> SynChannelsC;
	app.Connectionless -> ConnectionlessC.Connectionless[MOCKUP];
	app.ComponentManagement->ComponentManagementC;
	app.Learning-> LearningC;
	
	//Timers

	//Serial
	app.SerialPacket->Serial;
	
	app.LearnModuleCmdReceive->LearnModuleCmdReceive;
    app.LearnModuleFloatCmdReceive->LearnModuleFloatCmdReceive;
	
	//Radio
	app.RadioPacket->Radio;
}
