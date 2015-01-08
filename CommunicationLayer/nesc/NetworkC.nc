/*
 *  NetworkC.nc
 *  RUBICON src
 *
 *  Created by Claudio Vairo on 09/09/11.
 *  Copyright 2011 ISTI-CNR. All rights reserved.
 *
 */

/**
 * Configuration of the Network component of the Communication layer.
 */

//#include "network.h"

configuration NetworkC {
	//list of provided interfaces
	provides {
		interface Network[uint8_t comp];
	}
}

implementation {
	//list of used components
	components MainC;
	
	components NetworkP;
	components CommControlC;
	components LedsC;
	components HilTimerMilliC;
	components RandomC;
	components new TimerMilliC() as TimerJoinTimeout;
	
	//Serial
    components SerialActiveMessageC as Serial;
    components new SerialAMSenderC(AM_NET_MSG) as SerialNetworkSend;
	components new SerialAMReceiverC(AM_NET_MSG) as SerialNetworkReceive;
	components new SerialAMReceiverC(AM_CONFIG_MSG) as SerialConfigReceive;
	
	//Radio
	components ActiveMessageC as Radio;
	
	components new AMSenderC(AM_JOIN_MSG) as JoinSend;
	components new AMReceiverC(AM_JOIN_MSG) as JoinReceive;
	components new AMSenderC(AM_JOIN_REPLY_MSG) as JoinReplySend;
	components new AMReceiverC(AM_JOIN_REPLY_MSG) as JoinReplyReceive;
    components new AMSenderC(AM_NET_MSG) as NetworkSend;
    components new AMReceiverC(AM_NET_MSG) as NetworkReceive;
    components new AMSenderC(AM_ACK_JOIN_MSG) as AckJoinSend;    
    components new AMReceiverC(AM_ACK_JOIN_MSG) as AckJoinReceive;
    
	//RSSI
#if defined(PLATFORM_MICAZ) || defined(PLATFORM_TELOSB) || defined(PLATFORM_XM1000)
	components CC2420ActiveMessageC;
	NetworkP.CC2420Packet -> CC2420ActiveMessageC.CC2420Packet;
#elif  defined(PLATFORM_IRIS)
	components  RF230ActiveMessageC;
	NetworkP.PacketRSSI -> RF230ActiveMessageC.PacketRSSI;
#endif
	
	
	//wiring
	NetworkP.Boot -> MainC.Boot;
	
	Network = NetworkP.Network;
	NetworkP.CommControl->CommControlC;
	NetworkP.Leds->LedsC;
	NetworkP.LocalTime->HilTimerMilliC;
	NetworkP.Random->RandomC.Random;
	NetworkP.TimerJoinTimeout->TimerJoinTimeout;
	
    //Serial
	NetworkP.SerialPacket->Serial;
	NetworkP.SerialNetworkSend->SerialNetworkSend;
    NetworkP.SerialNetworkReceive->SerialNetworkReceive;
	NetworkP.SerialConfigReceive->SerialConfigReceive;
	
	//Radio
	NetworkP.RadioPacket->Radio;
	NetworkP.AMPacket->Radio;
	NetworkP.Ack->Radio.PacketAcknowledgements;
	
	NetworkP.JoinSend->JoinSend;
	NetworkP.JoinReceive->JoinReceive;
	NetworkP.JoinReplySend->JoinReplySend;
	NetworkP.JoinReplyReceive->JoinReplyReceive;
    NetworkP.NetworkSend->NetworkSend;
    NetworkP.NetworkReceive->NetworkReceive;
    NetworkP.AckJoinSend->AckJoinSend;
    NetworkP.AckJoinReceive->AckJoinReceive;
}
