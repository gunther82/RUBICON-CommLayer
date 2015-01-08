/*
 *  MockupC.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 27/03/12.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Configuration file for the WSN-side version of the Mockup Communication Layer of RUBICON.
 */

//includes mockup;
#include "../Mockup-Sink/mockup.h"
#include "definitions.h"
#include "StorageVolumes.h"

configuration MockupC {
	
}

implementation {
	//list of used components
	components MainC;
	
	//Communiction Layer components
	components MockupM as app, LedsC;
	//components StreamsP;
	//components SynChannelsC;
#ifdef LEARNING_COMP
	components LearningC;
	app.Learning-> LearningC;
#endif
#ifdef CONTROL_COMP
    components ControlC;
	app.Control->ControlC;
#endif
	components ConnectionlessC;
	components ComponentManagementC;
	components RandomC;
	components new LogStorageC(VOLUME_LOGTEST, TRUE);
	
#ifdef SS
	components SSToAppC;
	app.SSToApp-> SSToAppC;
#endif	
	
	//Timers
	components new TimerMilliC() as TimerReply;
	components new TimerMilliC() as TimerPeriodicRead;
	components new TimerMilliC() as TimerDemo;
	
	//Serial
	components SerialActiveMessageC as Serial;
	components new SerialAMSenderC(AM_SERIAL_DATA_MSG) as SerialDataSend;
	components new SerialAMReceiverC(AM_COMMAND_MSG) as SerialCommandReceive;
	
	//Radio
	components ActiveMessageC as Radio;
	components new AMSenderC(AM_TEMP_MSG) as TempSend;
	components new AMSenderC(AM_HUMID_MSG) as HumidSend;
	components new AMSenderC(AM_LIGHT_MSG) as LightSend;
	components new AMSenderC(AM_RSSI_MSG) as RSSISend;
	components new AMSenderC(AM_DATA_MSG) as DataSend;
	
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
#if defined(PLATFORM_TELOSB) || defined(PLATFORM_XM1000)
	components new SensirionSht11C();
	app.Temp->SensirionSht11C.Temperature;
	app.Humidity->SensirionSht11C.Humidity;
	components new HamamatsuS1087ParC() as Light;
	app.Light -> Light;
	components new MTS_EM1000C() as Accel;
	app.AccelX->Accel.ADXL321_ACC_Axis_X;
	app.AccelY->Accel.ADXL321_ACC_Axis_Y;
	components new MTS_SE1000C() as SE1000;
	app.Pir->SE1000.PIR_LHI878;
	app.Mic->SE1000.Mic_SPOB_413S44;
	app.Magnetic->SE1000.Magnetic_AMS_10C;
#elif defined(PLATFORM_IRIS) || defined(PLATFORM_MICAZ)
	components new SensorMts300C() as Temp;
	app.Temp->Temp.Temp;
	components new SensorMts300C() as Light;
	app.Light -> Light.Light;
	components new DemoSensorC() as Humidity;
	app.Humidity->Humidity;
//#else
//	components new DemoSensorC() as Temp;
//	app.Temp->Temp;
//	components new DemoSensorC() as Light;
//	app.Light -> Light;
//	components new DemoSensorC() as Humidity;
//	app.Humidity->Humidity;
#endif
	
	
	//wiring of the components 
	app.Boot -> MainC.Boot;
	app.Leds->LedsC;
	
	//Communication Layer interfaces binding
	//app.Streams -> StreamsP.Streams;
	//app.Synaptic -> SynChannelsC;
	app.Connectionless -> ConnectionlessC.Connectionless[MOCKUP];
	app.ComponentManagement->ComponentManagementC;
	app.Random -> RandomC;
	app.LogRead -> LogStorageC;
	app.LogWrite -> LogStorageC;
	
	//Timers
	app.TimerReply->TimerReply;
	app.TimerPeriodicRead->TimerPeriodicRead;
	app.TimerDemo->TimerDemo;
	
	
	//Serial
	app.SerialControl->Serial;
	app.SerialPacket->Serial;

	app.SerialDataSend->SerialDataSend;
	app.SerialCommandReceive->SerialCommandReceive;
	
	//Radio
	app.RadioControl->Radio;
	app.RadioPacket->Radio;
	
	app.TempSend->TempSend;
	app.HumidSend->HumidSend;
	app.LightSend->LightSend;
	app.RSSISend->RSSISend;
	app.DataSend->DataSend;
}
