//package CommunicationLayer;

//
//  ReceiveTransportConnlessListener.java
//  RUBICON src
//
//  Created by Claudio Vairo on 17/05/13.
//  Copyright (c) 2013 ISTI-CNR. All rights reserved.
//

import ucd.rubicon.network.RubiconAddress;

public interface ReceiveTransportConnlessListener {
    
    public abstract void onReceiveTransportConnlessMessage(RubiconAddress sender, byte[] payload, short nbytes, byte reliable, int seqnum);

}