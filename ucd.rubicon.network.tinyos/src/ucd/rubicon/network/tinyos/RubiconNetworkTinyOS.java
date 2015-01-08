package ucd.rubicon.network.tinyos;


import net.tinyos.message.Message;
import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessage;
import ucd.rubicon.network.RubiconNetwork;
import cnr.rubicon.cml.Connectionless;
import cnr.rubicon.cml.Definitions;
import cnr.rubicon.cml.Network;
import cnr.rubicon.cml.ReceiveConlessControlListener;
import cnr.rubicon.migFiles.NetMsg;
import cnr.rubicon.migFiles.SerialJoinedMsg;
import cnr.rubicon.migFiles.UpdatesMsg;

public class RubiconNetworkTinyOS extends RubiconNetwork {

	protected static Connectionless connection = null;	

	public RubiconNetworkTinyOS() {
		System.out.println("Constructor of RubiconNetworkTinyOS");
	}
	
	@Override
	public void handleMessageReceived(RubiconMessage rubiconMessage, RubiconAddress destination) {
		NetMsg netMsg;

		if (rubiconMessage.getSender().getPeisId() != this.getIslandID()) {
			netMsg = (NetMsg)rubiconMessage.getUserObject();
		} else {		
			netMsg = (NetMsg)getNetworkMessage(rubiconMessage, rubiconMessage.getSender(), destination);			
		}
		
		Network.getNetwork().GWReceive(netMsg, netMsg.get_payload().length);
	}
	
	@Override
	public void init() throws Exception {
		installMessageConverter(new RubiconMessageJoinConverter());
		//installMessageConverter(new RubiconMessageCtrlActuateCmdConverter());
		//installMessageConverter(new RubiconMessageCtrlPeriodicCmdConverter());
		installMessageConverter(new RubiconMessageUpdatesConverter());
				
		Network.getNetwork()._init(this.getIslandID(), this.getGateway(), this.getProperties());
		
		connection  = Connectionless.getConnectionless();

		connection.setOnReceiveConlessControlMessage(
			new ReceiveConlessControlListener() {

				@Override
				public void onReceiveConlessControlMessage(RubiconAddress sender, byte[] payload, short nbytes) {
					Message msg = null;
					System.out.println("RubiconNetworkTinyOS::onReceiveConlessControlMessage type "+payload[0]);
					switch(payload[0]) {
						case Definitions.AM_SERIAL_JOINED_MSG:
							msg = new SerialJoinedMsg(payload);
							break;
						case Definitions.AM_UPDATES_MSG:
							msg = new UpdatesMsg(payload);
							break;
					}
					
					if (msg != null) {
						System.out.println("RubiconNetworkTinyOS::translateAndSendToGateway");
						translateAndSendToGateway(msg, sender, new RubiconAddress(getIslandID(),-1));
					}
					
				}						
			}
		);

	}

}

