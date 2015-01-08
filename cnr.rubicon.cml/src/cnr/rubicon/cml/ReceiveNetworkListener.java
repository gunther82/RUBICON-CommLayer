package cnr.rubicon.cml;

//
//  ReceiveNetworkListener.java
//  RUBICON src
//
//  Created by Claudio Vairo on 10/04/13.
//  Copyright (c) 2013 ISTI-CNR. All rights reserved.
//

import ucd.rubicon.network.RubiconAddress;

public interface ReceiveNetworkListener {
	
	public abstract void onReceiveNetworkMessage(RubiconAddress sender, byte[] payload, short nbytes, byte reliable, int seqnum);
	
	//public abstract void onReceiveAck();
}
