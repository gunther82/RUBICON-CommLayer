//package CommunicationLayer;

import ucd.rubicon.network.RubiconAddress;

public interface ReceiveSerialJoinMsgListener {
	public abstract void onReceiveSerialJoinMessage(SerialJoinedMsg smsg);
}
