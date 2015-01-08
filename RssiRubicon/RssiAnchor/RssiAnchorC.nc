#include <Timer.h>
#include "../Rssi.h"

module RssiAnchorC 
{
	uses interface Boot;
	uses interface Leds;
	uses interface Packet;
//	uses interface AMPacket;
	uses interface AMSend;
	uses interface SplitControl as AMControl;
	uses interface Receive;
	uses interface CC2420Config;
	uses interface Timer<TMilli> as TimerAnchor;

}

implementation 
{
	
	message_t pkt;
	RssiMsg* btrpkt;
	RssiMsg* rssimsg;

	event void Boot.booted() 
	{
		call AMControl.start();
		
		if (ALGORITHM == ALGORITHM_CYCLE)
		{
			call TimerAnchor.startPeriodic(TIMER_ANCHOR);		
		}
		
		if (ALGORITHM == ALGORITHM_INDEPENDENTCYCLE)
		{
			call TimerAnchor.startOneShot(TIMER_ANCHOR);
		}
		
		
	}

	event void AMControl.startDone(error_t err) 
	{
		if (err == SUCCESS) 
		{
            call Leds.led2On();
			/*-----------------------------------------------*/
			/* When the radio is started, change the channel */
			/* where anchors send beacons                    */
			/*-----------------------------------------------*/

			call CC2420Config.setChannel(RSSI_CHANNEL);
			call CC2420Config.sync();

			/*-----------------------------------------------*/
			/* Build the beacon that will be send            */
			/*-----------------------------------------------*/

			rssimsg = (RssiMsg*)(call Packet.getPayload(&pkt, sizeof (RssiMsg)));
            rssimsg->idmitt = TOS_NODE_ID;
		}
		else 
		{
			call AMControl.start();
    	}
  	}

	event void AMControl.stopDone(error_t err) 
	{
  	}

	event void TimerAnchor.fired() {

		//call Leds.led2Toggle();
		
		if (ALGORITHM == ALGORITHM_CYCLE)
		{
			call AMSend.send(AM_BROADCAST_ADDR, &pkt, sizeof(RssiMsg));
		}
		
		if (ALGORITHM == ALGORITHM_INDEPENDENTCYCLE)
		{
			call AMSend.send(AM_BROADCAST_ADDR, &pkt, sizeof(RssiMsg));
			call TimerAnchor.startOneShot(TIMER_ANCHOR);		
		}
		
	}

	event message_t* Receive.receive(message_t* msg, void* payload, uint8_t len)
	{
		
		
		
		if (ALGORITHM == ALGORITHM_REQUEST) 
		{ 
			
			if ( len == sizeof(RssiMsg)) 
			{
				btrpkt = (RssiMsg*) payload;

				if ( btrpkt->idmitt == TOS_NODE_ID - 1 )
				{
					//call Leds.led1Toggle();
					//rssimsg->idtransducer = btrpkt->idtransducer;
					call AMSend.send(AM_BROADCAST_ADDR, &pkt, sizeof(RssiMsg));
				}

			}
		}
		
		if (ALGORITHM == ALGORITHM_INDEPENDENTCYCLE) 
		{ 
			
			if ( len == sizeof(RssiMsg)) 
			{
				btrpkt = (RssiMsg*) payload;
				
				if ( (TOS_NODE_ID == 1 && btrpkt->idmitt == NUM_ANCHORS ) || btrpkt->idmitt == TOS_NODE_ID - 1) 
				{
					//call Leds.led1Toggle();
					call TimerAnchor.stop();
					call AMSend.send(AM_BROADCAST_ADDR, &pkt, sizeof(RssiMsg));
					call TimerAnchor.startOneShot(TIMER_ANCHOR);
				}

			}
		}
		
		return msg;
		
	}

	event void AMSend.sendDone(message_t* msg, error_t error) 
	{
        call Leds.led0Toggle();
	}
	
	event void CC2420Config.syncDone( error_t error ) 
    {
		//call Leds.led0On();		
    }


 }
