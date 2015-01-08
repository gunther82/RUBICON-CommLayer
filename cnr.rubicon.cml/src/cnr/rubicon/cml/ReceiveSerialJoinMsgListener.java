package cnr.rubicon.cml;

import cnr.rubicon.migFiles.SerialJoinedMsg;

public interface ReceiveSerialJoinMsgListener {
	public abstract void onReceiveSerialJoinMessage(SerialJoinedMsg smsg);
}
