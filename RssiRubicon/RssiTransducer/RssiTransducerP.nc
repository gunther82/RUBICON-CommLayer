/*-----------------------------------------------*/
/* Include                                       */ 
/*-----------------------------------------------*/

#include <Timer.h>
#include "../Rssi.h"

/*-----------------------------------------------*/
/* Debugging macro, define DEBUG to use printf   */
/*-----------------------------------------------*/

//#define DEBUG

#ifdef DEBUG
	#include "printf.h"
	#define dbgprintf printf
#else
	#define dbgprintf(fmt,args...)
#endif

/*-----------------------------------------------*/
/* Local definition                              */
/*-----------------------------------------------*/

#define MAX_RSSI 42

module RssiTransducerP 
{
	provides interface RssiTransducer[uint8_t comp];
	uses interface Boot;
	uses interface Leds;
	uses interface Timer<TMilli> as TimerCollector;
	uses interface SplitControl as AMControl;
	uses interface Receive;
	uses interface CC2420Packet;
	uses interface CC2420Config;
	uses interface AMSend;
	uses interface Packet;
}

implementation 
{

	uint8_t defaultRadioChannel;
	RssiMsg* rssimsg;
	message_t pkt;
	bool received[NUM_ANCHORS];
	uint16_t RSSI[NUM_ANCHORS];
	bool running;
	

	/*-----------------------------------------------*/
	/* Useful timing when debugging                  */
	/*-----------------------------------------------*/
	
	#ifdef DEBUG

	uint16_t lastTime = 0;

	uint16_t interval()
        {
                uint32_t nowTime = call TimerCollector.getNow();
                uint16_t i = nowTime - lastTime;
                lastTime = nowTime;
                return i;
        }

	#endif

	/*-----------------------------------------------*/
	/* Converts RSSI data from int8_t to uint16_t    */
	/*-----------------------------------------------*/

	uint16_t convertForSynaptic(int8_t x)
	{	
		//if (x > MAX_RSSI) x = MAX_RSSI;
//		if ( x >= 0 ) return MAX_RSSI - ((uint16_t) x);
//		else return MAX_RSSI + (-1)*((uint16_t) x);
		
		return (uint16_t)x+50;
		
		/*-----------------------------------------------*/
		/* Code to use during tuning                     */
		/*-----------------------------------------------*/
		
		//if ( x >= 0 ) return ((uint16_t) x);
		//else return (-1)*((uint16_t) x);
	}

	command void RssiTransducer.read[uint8_t comp]()
	{
		/*-----------------------------------------------*/
		/* Start a timer to ensure that the data is sent */
		/* even if incomplete				 			 */
		/*-----------------------------------------------*/
		//check if there is another read already running
		if(running == FALSE) {
			running = TRUE;
			call TimerCollector.startOneShot(TIMER_TRANSDUCER);

			dbgprintf("* [%d]\t Caller has executed read\n",interval());

		/*-----------------------------------------------*/
		/* Switch to the RSSI_CHANNEL to communicate     */
		/* with anchors                                  */		
		/*-----------------------------------------------*/

			call CC2420Config.setChannel(RSSI_CHANNEL);
			call CC2420Config.sync();
		}
	}

	int8_t getRssi(message_t *msg)
        {
                return call CC2420Packet.getRssi(msg);
        }

	bool allReceived()
	{
		uint16_t i = 0;
		uint16_t c = 1;
		while ( i < NUM_ANCHORS && (c = received[i]) ) i++;
		return c;
	}

	void setZero(bool array[])
	{
		uint16_t i;
		for (i = 0; i < NUM_ANCHORS; i++) array[i]=0;
	}

	event void Boot.booted() 
	{
		/*-----------------------------------------------*/
		/* Start the radio				                 */
		/*-----------------------------------------------*/

		call AMControl.start();
		running = FALSE;
	}

	event void AMControl.startDone(error_t err) 
	{
		uint16_t i;

		if (err == SUCCESS) 
		{
			/*-----------------------------------------------*/
			/* Initializes arrays which need to              */
			/*-----------------------------------------------*/

			for (i = 0; i < NUM_ANCHORS; i++)
			{
				received[i]=0;
				RSSI[i]=0;
			}

			/*-----------------------------------------------*/
			/* Store the default radio channel               */
			/*-----------------------------------------------*/
	
			defaultRadioChannel = call CC2420Config.getChannel();

			/*-----------------------------------------------*/
			/* Build the packet that will be send to anchors */
			/* to start the protocol                         */
			/*-----------------------------------------------*/

			rssimsg = (RssiMsg*)(call Packet.getPayload(&pkt, sizeof (RssiMsg)));
        	rssimsg->idmitt = 0;
        	//rssimsg->idtransducer = TOS_NODE_ID;
		}
		else 
		{
			/*-----------------------------------------------*/
			/* Try again to connect                          */
			/*-----------------------------------------------*/

			call AMControl.start();
    	}
  	}

	event void AMControl.stopDone(error_t err) 
	{
  	}

	event void TimerCollector.fired()
	{
		/*-----------------------------------------------*/
		/* If the timer is fired, I must return data to  */
		/* the caller, even if incomplete. The signal    */
		/* will be called in syncDone                    */
		/*-----------------------------------------------*/

		dbgprintf("* [%d]\t Timer is fired\n",interval());		

		call CC2420Config.setChannel(defaultRadioChannel);
		call CC2420Config.sync();
	}

	event message_t* Receive.receive(message_t* msg, void* payload, uint8_t len) 
	{

		// TODO: controlla AM con: command am_group_t group(message_t *amsg)

		//call Leds.led1Toggle();
		//call Leds.led2Toggle();
		
		if (len == sizeof(RssiMsg)) 
		{
			/*-----------------------------------------------*/
			/* Obtains the information from the package      */
			/*-----------------------------------------------*/

			rssimsg = (RssiMsg*) payload;
			
			if (rssimsg->idmitt > 0 && rssimsg->idmitt <= NUM_ANCHORS/* && rssimsg->idtransducer == TOS_NODE_ID*/)
			{
				RSSI[rssimsg->idmitt-1] = convertForSynaptic(getRssi(msg));
				received[rssimsg->idmitt-1] = 1;
				dbgprintf("* [%d]\t Received a beacon from anchor %d and the RSSI is %d\n",interval(),rssimsg->idmitt,RSSI[rssimsg->idmitt-1]);		
			}
			
			if ( allReceived() )
			{
				dbgprintf("* [%d]\t Received all beacons\n",interval());		

				/*-----------------------------------------------*/
				/* Stop the timer                                */
				/*-----------------------------------------------*/

				call TimerCollector.stop();

				/*-----------------------------------------------*/
				/* Restore default channel and then when         */
				/* syncDone is signaled data will be returned to */
				/* caller                                        */
				/*-----------------------------------------------*/

				call CC2420Config.setChannel(defaultRadioChannel);
				call CC2420Config.sync();				
			}
			

		}
		return msg;
	}


	event void AMSend.sendDone(message_t* msg, error_t error)
    {
    }

	event void CC2420Config.syncDone(error_t error) 
	{
	
		uint8_t channel = call CC2420Config.getChannel();		

		if ( channel == RSSI_CHANNEL )
		{
			dbgprintf("* [%d]\t Radio channel is changed, now it is %d\n",interval(),channel);

			/*-----------------------------------------------*/
			/* Send a packet to anchors                      */
			/*-----------------------------------------------*/
			
			if (ALGORITHM == ALGORITHM_REQUEST)
			{
	        	call AMSend.send(AM_BROADCAST_ADDR, &pkt, sizeof(RssiMsg));
				dbgprintf("* [%d]\t Send a package to the anchors\n",interval());
			}
			
			if (ALGORITHM == ALGORITHM_ONLYONEREQUEST)
			{
				if (TOS_NODE_ID == REQUESTER)
				{
	        		call AMSend.send(AM_BROADCAST_ADDR, &pkt, sizeof(RssiMsg));
					dbgprintf("* [%d]\t Send a package to the anchors\n",interval());
				}
			}

		}
		if ( channel == defaultRadioChannel )
		{
			error_t result;

			dbgprintf("* [%d]\t Radio channel is changed, now it is %d\n",interval(),channel);

			/*-----------------------------------------------*/
			/* Set error	                                 */
			/*-----------------------------------------------*/


			if ( allReceived() ) 
				result = SUCCESS;
			else
				result = EOFF;

			/*-----------------------------------------------*/
			/* Reset information about received beacons      */
			/*-----------------------------------------------*/

			setZero(received);

			/*-----------------------------------------------*/
			/* Signal readDone                               */
			/*-----------------------------------------------*/

			dbgprintf("* [%d]\t Sending signal readDone to caller\n",interval());

			signal RssiTransducer.readDone[LEARNING](result, NUM_ANCHORS, RSSI);
			signal RssiTransducer.readDone[CONTROL_LAYER](result, NUM_ANCHORS, RSSI);
		
			//reset the running variable for accepting other requests
			running = FALSE;
		}

	}
	
	default event void RssiTransducer.readDone[uint8_t comp](error_t result, uint16_t length, uint16_t data[]) {
	
	}

 }
