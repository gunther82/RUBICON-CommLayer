//package CommunicationLayer;

import ucd.rubicon.network.RubiconAddress;

public interface ReceiveTransportRubiconAckListener {
    
    public abstract void onReceiveTransportRubiconAckMessage(RubiconAddress sender, byte[] payload, short nbytes, byte reliable, int seqnum);

}