#include <Timer.h>
#include "../Rssi.h"
 
configuration RssiAnchorAppC 
{
}

implementation 
{
	components MainC;
	components LedsC;
	components RssiAnchorC as App;

	App.Boot -> MainC;
	App.Leds -> LedsC;

	/*-----------------------------------------------*/
	/* To implement algorithm with periodic send     */
	/*-----------------------------------------------*/

	components new TimerMilliC() as TimerAnchor;
	App.TimerAnchor -> TimerAnchor;

	/*-----------------------------------------------*/
	/* To send through the radio                     */
	/*-----------------------------------------------*/

	components new AMSenderC(AM_RSSI);
	App.AMSend -> AMSenderC;

	/*-----------------------------------------------*/
	/* To send create a radio packet                 */
	/*-----------------------------------------------*/

	App.Packet -> AMSenderC;
	// App.AMPacket -> AMSenderC;

	/*-----------------------------------------------*/
	/* To receive from the radio                     */
	/*-----------------------------------------------*/

	components new AMReceiverC(AM_RSSI);
	App.Receive -> AMReceiverC;

	/*-----------------------------------------------*/
	/* To start the radio                            */
	/*-----------------------------------------------*/

	components ActiveMessageC;
	App.AMControl -> ActiveMessageC;

	/*-----------------------------------------------*/
	/* To change dynamically the radio channel       */
	/*-----------------------------------------------*/

	components CC2420ControlC;
        App.CC2420Config -> CC2420ControlC; 


}
