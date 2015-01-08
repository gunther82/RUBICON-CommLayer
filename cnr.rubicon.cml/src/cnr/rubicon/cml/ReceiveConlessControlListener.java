package cnr.rubicon.cml;

//
//  ReceiveConlessControlListener.java
//  RUBICON src
//
//  Created by Claudio Vairo on 20/05/13.
//
//

import ucd.rubicon.network.RubiconAddress;

public interface ReceiveConlessControlListener {

    public abstract void onReceiveConlessControlMessage(RubiconAddress sender, byte[] payload, short nbytes);

}
