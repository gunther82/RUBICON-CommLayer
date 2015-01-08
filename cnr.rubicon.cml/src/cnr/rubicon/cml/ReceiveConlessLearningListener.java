package cnr.rubicon.cml;

//
//  ReceiveConlessLearningListener.java
//  RUBICON src
//
//  Created by Claudio Vairo on 20/05/13.
//
//

import ucd.rubicon.network.RubiconAddress;

public interface ReceiveConlessLearningListener {

    public abstract void onReceiveConlessLearningMessage(RubiconAddress sender, byte[] payload, short nbytes);

}
