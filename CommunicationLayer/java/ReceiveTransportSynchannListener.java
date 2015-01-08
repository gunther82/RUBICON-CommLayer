//package CommunicationLayer;

//
//  ReceiveTransportSynchannListener.java
//  RUBICON src
//
//  Created by Claudio Vairo on 17/05/13.
//
//

import ucd.rubicon.network.RubiconAddress;

public interface ReceiveTransportSynchannListener {

    public abstract void onReceiveTransportSynchannMessage(RubiconAddress sender, byte[] payload, short nbytes, byte reliable, int seqnum);

}
