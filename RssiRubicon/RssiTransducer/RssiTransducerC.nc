#include <Timer.h>
#include "../Rssi.h"

configuration RssiTransducerC 
{

	provides interface RssiTransducer[uint8_t comp];

}

implementation 
{
	components MainC;
	components LedsC;

	components RssiTransducerP as App;
	RssiTransducer = App;

  
	
	components ActiveMessageC;
	components new AMReceiverC(AM_RSSI);
	App.Boot -> MainC.Boot;
	App.Leds -> LedsC;
	App.AMControl -> ActiveMessageC;
	App.Receive -> AMReceiverC;

	components CC2420ActiveMessageC;
	App.CC2420Packet -> CC2420ActiveMessageC.CC2420Packet;

	/*-----------------------------------------------*/
    /* To respond in limited interval                */
    /*-----------------------------------------------*/

	components new TimerMilliC() as TimerCollector;
	App.TimerCollector -> TimerCollector;

    /*-----------------------------------------------*/
    /* To change dynamically the radio channel       */
    /*-----------------------------------------------*/

	components CC2420ControlC;
	App.CC2420Config -> CC2420ControlC; 

	/*-----------------------------------------------*/
    /* To send the start packet to anchors           */
    /*-----------------------------------------------*/

	components new AMSenderC(AM_RSSI);
	App.Packet -> AMSenderC;
	App.AMSend -> AMSenderC;


}
